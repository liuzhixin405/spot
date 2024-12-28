package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQAccountContractDao;
import com.btc100.btc_q.dao.BQMemberTransactionDao;
import com.btc100.btc_q.dao.BQMemberTransactionOverloadLogDao;
import com.btc100.btc_q.dao.BQTransactionFeeRateDao;
import com.btc100.btc_q.dao.PendingDelegationCacheDao;
import com.btc100.btc_q.dao.PendingLogDao;
import com.btc100.btc_q.dao.TradeLogDao;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQMemberTransactionOverloadLog;
import com.btc100.btc_q.entity.BQTransactionFeeRate;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.entity.PendingLog;
import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.OverFlowService;

@Service("overFlowService")
public class OverFlowServiceImpl implements OverFlowService {
	
	private static final Logger logger = Logger.getLogger(OverFlowServiceImpl.class);
	
	@Autowired
	private BQMemberTransactionDao<BQMemberTransaction> bqMemberTransactionDao;
	@Autowired
	private PendingDelegationCacheDao<PendingDelegationCache> pendingDelegationCacheDao;
	@Autowired
	private PendingLogDao<PendingLog> pendingLogDao;
	@Autowired
	private BQMemberTransactionOverloadLogDao<BQMemberTransactionOverloadLog> bqMemberTransactionOverloadLogDao;
	@Autowired
	private TradeLogDao<TradeLog> tradeLogDao;
	@Autowired
	private BQAccountContractDao<BQAccountContract> bqAccountContractDao;
	@Autowired
	private BQTransactionFeeRateDao<BQTransactionFeeRate> bqTransactionFeeRateDao;
	
	@Override
	public void overFlowDelegate(int diection, BigDecimal qpj, int digits, BQMemberTransaction trans, BQMemberLevel userLevel, BigDecimal coefficient) throws ServiceException {
		BigDecimal changeRemain = BigDecimal.ZERO;
		if (trans.getF_lockmoney().compareTo(BigDecimal.ZERO) > 0) {
			
			BQAccountContract account = bqAccountContractDao.findByUserIDCode(trans.getF_user_id(), trans.getTransaction_code());
			if (account.getF_btc().compareTo(trans.getF_lockmoney()) >= 0) {
				changeRemain = trans.getF_lockmoney();
			} else {
				changeRemain = account.getF_btc();
			}
			
			if (changeRemain.compareTo(BigDecimal.ZERO) != 0) {
				if (bqAccountContractDao.updateAmount(trans.getF_user_id(), trans.getTransaction_code(), BigDecimal.ZERO, changeRemain.negate(), BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
					throw new ServiceException("");
				}
			}
		}
		
		// 上面有进行强平操作，用户持单可能有变，需重新获取
		BQMemberTransaction newMemberTran = bqMemberTransactionDao.getMemberTransactionById(trans.getMt_id());
		BigDecimal tradeCount = newMemberTran.getHold_count().add(newMemberTran.getLock_count());
		if (newMemberTran == null || tradeCount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RuntimeException("获取用户持仓为空或持仓为0");	
		}
		
		// 删除该持仓
		bqMemberTransactionDao.delete(newMemberTran);
		
		BigDecimal feeRate = userLevel.getEat_commission_rate();
		BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateDao.getByCodeAndLevel(trans.getTransaction_code(), userLevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		BigDecimal haveCapital = newMemberTran.getDeposit().add(newMemberTran.getDeposit2()).add(newMemberTran.getF_fromremain())
				.add(newMemberTran.getF_frozenremain()).add(newMemberTran.getF_min_frozenremain()).add(changeRemain);
		BigDecimal bcj = BTCQMath.BCJ(diection, newMemberTran.getAverage_price(), haveCapital,
				feeRate, newMemberTran.getHold_count().add(newMemberTran.getLock_count()), coefficient);	// 爆仓价
		
		int needPendDirection = 0;	// 交易方向
		int needTradeTypeID = 0;	// 交易类型
		
		// 未被强平掉的仓位按爆仓价进行委托
		if (diection == Contents.DIRECTION_BUY) {	// 挂卖单
			bcj = bcj.setScale(digits, RoundingMode.UP);
			needPendDirection = Contents.DIRECTION_SELL;
			needTradeTypeID = Contents.CZLX_DDBC;
		} else if (diection == Contents.DIRECTION_SELL) {	// 挂买单
			bcj = bcj.setScale(digits, RoundingMode.DOWN);
			needPendDirection = Contents.DIRECTION_BUY;
			needTradeTypeID = Contents.CZLX_KTBC;
		}

		if (bcj.compareTo(BigDecimal.ZERO) <= 0) {
			logger.error("爆仓为0, 不予爆仓, 用户ID--" + trans.getF_user_id());
			throw new RuntimeException("爆仓为0, 不予爆仓, 用户ID--" + trans.getF_user_id());
		}
		
		// 爆仓价挂单
		BigDecimal income = bcj.subtract(newMemberTran.getAverage_price()).multiply(tradeCount).multiply(coefficient);
		if (diection == Contents.DIRECTION_SELL) {
			income = income.negate();
		}

		BigDecimal fee = haveCapital.add(income);
		if (fee.compareTo(BigDecimal.ZERO) < 0) fee = BigDecimal.ZERO;
		
		// 扣除资金费(算在盈亏里面) - (已有资金费记录)
//		income= income.add(newMemberTran.getDeposit2());
		
		BigDecimal deposit = BTCQMath.getDeposit(tradeCount, bcj, newMemberTran.getTrade_multiple(), coefficient);
		PendingDelegationCache cache = new PendingDelegationCache(newMemberTran.getTransaction_id(), newMemberTran.getTransaction_code(), needPendDirection, tradeCount, bcj, 
				BigDecimal.ZERO, tradeCount,newMemberTran.getTrade_multiple().intValue(), deposit, 
				feeRate, Contents.PRIORITY_BC, newMemberTran.getF_user_id(), BigDecimal.ZERO, newMemberTran.getAverage_price());
		PendingDelegationCache resultCache = pendingDelegationCacheDao.insertCache(cache);	// 挂单
		Long pendingID = 0l;
		if (resultCache != null) {
			pendingID = resultCache.getDelegation_id();
			if (pendingID == 0) {
				logger.error("爆仓挂单未获取到用户的挂单ID, 用户ID--" + newMemberTran.getF_user_id());
			}
		}

		// 挂单日志
		PendingLog pendingLog = new PendingLog();
		pendingLog.setAction(Contents.PENDING_LOG_ACTION_QP);	// 强平
		pendingLog.setDateline(pendingLogDao.getUnix_TIMESTAMP());
		pendingLog.setDeal_count(BigDecimal.ZERO);
		pendingLog.setDelegation_count(cache.getDelegation_count());
		pendingLog.setDelegation_id(pendingID);
		pendingLog.setDelegation_price(bcj);
		pendingLog.setDelegation_time(pendingLogDao.getUnix_TIMESTAMP());
		pendingLog.setDeposit(cache.getDeposit());
		pendingLog.setF_user_id(newMemberTran.getF_user_id());
		pendingLog.setMultiple(cache.getMultiple());
		pendingLog.setTrade_type_id(needTradeTypeID);
		pendingLog.setTransaction_code(newMemberTran.getTransaction_code());
		pendingLog.setTransaction_id(cache.getTransaction_id());
		pendingLog.setUndeal_count(cache.getUndeal_count());
		pendingLog.setPending_no(cache.getPending_no());
		pendingLog.setAvg_price(bcj);
		pendingLog.setDevice(Contents.DEVICE_SERVER_QP);
		pendingLogDao.insertLog(pendingLog);

		// 记录爆仓日志
		BQMemberTransactionOverloadLog olog = new BQMemberTransactionOverloadLog();
		olog.setDateline(bqMemberTransactionOverloadLogDao.getUnix_TIMESTAMP());
		olog.setDelegation_id(pendingID);
		olog.setDirection(needPendDirection);
		olog.setF_user_id(newMemberTran.getF_user_id());
		olog.setTrade_count(cache.getDelegation_count());
		olog.setTrade_deposit(newMemberTran.getDeposit());
		olog.setTrade_overload_price(bcj);
		olog.setTrade_force_price(qpj);
		olog.setTrade_price(newMemberTran.getAverage_price());
		olog.setTrade_fee(fee);
		olog.setTransaction_code(newMemberTran.getTransaction_code());
		olog.setTransaction_id(cache.getTransaction_id());
		olog.setBalance(BigDecimal.ZERO);
		olog.setPending_no(cache.getPending_no());
		olog.setF_fromremain(newMemberTran.getF_fromremain().add(newMemberTran.getF_frozenremain()).add(newMemberTran.getF_min_frozenremain()));
		bqMemberTransactionOverloadLogDao.save(olog);
		
		// 插入交易记录
		TradeLog tradeLog = new TradeLog(newMemberTran.getTransaction_id(), newMemberTran.getTransaction_code(), needTradeTypeID, needPendDirection, newMemberTran.getAverage_price(), bcj, cache.getDelegation_count(), 
				newMemberTran.getTrade_multiple().intValue(), newMemberTran.getDeposit(), income, fee, newMemberTran.getF_user_id(), cache.getPending_no());
		tradeLogDao.insertLog(tradeLog);
		
		// 记录爆仓数量-系统用户持单
		transInsertOrUpdate(newMemberTran.getTrade_type_id(), newMemberTran.getTransaction_code(), newMemberTran.getTransaction_id(), cache.getDelegation_count());
	}
	

	private void transInsertOrUpdate(int typeid, String scode, long icode, BigDecimal amount) {
		long uid = Contents.BQMEMBER_ADMIN;
		String selectHQL = "FROM BQMemberTransaction b WHERE f_user_id=:uid AND trade_type_id=:type AND transaction_code=:tranid";
		Map<String, Object> selectMap = new HashMap<String, Object>();
		selectMap.put("uid", uid);
		selectMap.put("type", typeid);
		selectMap.put("tranid", scode);
		List<BQMemberTransaction> trans = bqMemberTransactionDao.find(selectHQL, selectMap);

		if (trans != null && trans.size() > 0) {
			String hql = "UPDATE BQMemberTransaction b SET b.hold_count=b.hold_count+:amount WHERE mt_id=:id";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("amount", amount);
			map.put("id", trans.get(0).getMt_id());

			bqMemberTransactionDao.updateCondition(hql, map);
		} else {
			BQMemberTransaction o = new BQMemberTransaction();

			o.setAverage_price(BigDecimal.ZERO);
			o.setCommission(BigDecimal.ZERO);
			o.setDeposit(BigDecimal.ZERO);
			o.setDeposit2(BigDecimal.ZERO);
			o.setF_user_id(uid);
			o.setHold_count(amount);
			o.setLock_count(BigDecimal.ZERO);
			o.setTrade_type_id(typeid);
			o.setTrade_multiple(BigDecimal.ZERO);
			o.setTransaction_code(scode);
			o.setTransaction_id(icode);
			o.setF_type_forjs(typeid == Contents.DIRECTION_BUY ? 1 : -1);

			bqMemberTransactionDao.save(o);
		}
	}

}
