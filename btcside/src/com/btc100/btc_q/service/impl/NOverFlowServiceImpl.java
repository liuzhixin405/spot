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
import com.btc100.btc_q.dao.NAccountContractDao;
import com.btc100.btc_q.dao.NMemberTransactionDao;
import com.btc100.btc_q.dao.NMemberTransactionOverloadLogDao;
import com.btc100.btc_q.dao.NTransactionFeeRateDao;
import com.btc100.btc_q.dao.NPendingDelegationCacheDao;
import com.btc100.btc_q.dao.NPendingLogDao;
import com.btc100.btc_q.dao.NTradeLogDao;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NMemberTransactionOverloadLog;
import com.btc100.btc_q.entity.NTransactionFeeRate;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.entity.NPendingLog;
import com.btc100.btc_q.entity.NTradeLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.NOverFlowService;

@Service("nOverFlowService")
public class NOverFlowServiceImpl implements NOverFlowService {
	
	private static final Logger logger = Logger.getLogger(NOverFlowServiceImpl.class);
	
	@Autowired
	private NMemberTransactionDao<NMemberTransaction> bqMemberTransactionDao;
	@Autowired
	private NPendingDelegationCacheDao<NPendingDelegationCache> pendingDelegationCacheDao;
	@Autowired
	private NPendingLogDao<NPendingLog> pendingLogDao;
	@Autowired
	private NMemberTransactionOverloadLogDao<NMemberTransactionOverloadLog> bqMemberTransactionOverloadLogDao;
	@Autowired
	private NTradeLogDao<NTradeLog> tradeLogDao;
	@Autowired
	private NAccountContractDao<NAccountContract> bqAccountContractDao;
	@Autowired
	private NTransactionFeeRateDao<NTransactionFeeRate> bqTransactionFeeRateDao;
	
	@Override
	public void overFlowDelegate(String partition, int diection, BigDecimal qpj, int digits, NMemberTransaction trans, BQMemberLevel userLevel, BigDecimal coefficient) throws ServiceException {
		BigDecimal changeRemain = BigDecimal.ZERO;
		if (trans.getF_lockmoney().compareTo(BigDecimal.ZERO) > 0) {
			
			NAccountContract account = bqAccountContractDao.findByUserIDCode(partition, trans.getF_user_id(), trans.getTransaction_code());
			if (account.getF_btc().compareTo(trans.getF_lockmoney()) >= 0) {
				changeRemain = trans.getF_lockmoney();
			} else {
				changeRemain = account.getF_btc();
			}
			
			if (changeRemain.compareTo(BigDecimal.ZERO) != 0) {
				if (bqAccountContractDao.updateAmount(partition, trans.getF_user_id(), trans.getTransaction_code(), BigDecimal.ZERO, changeRemain.negate(), BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
					throw new ServiceException("");
				}
			}
		}
		
		// 上面有进行强平操作，用户持单可能有变，需重新获取
		NMemberTransaction newMemberTran = bqMemberTransactionDao.getMemberTransactionById(partition, trans.getMt_id());
		BigDecimal tradeCount = newMemberTran.getHold_count().add(newMemberTran.getLock_count());
		if (newMemberTran == null || tradeCount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RuntimeException("获取用户持仓为空或持仓为0");	
		}
		
		// 删除该持仓
		bqMemberTransactionDao.delete(partition, newMemberTran);
		
		BigDecimal feeRate = userLevel.getEat_commission_rate();
		NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateDao.getByCodeAndLevel(partition, trans.getTransaction_code(), userLevel.getLevel_id());
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
		
		// 扣除资金费(算在盈亏里面) - 已有资金费记录
//		income= income.add(newMemberTran.getDeposit2());
		
		BigDecimal deposit = BTCQMath.getDeposit(tradeCount, bcj, newMemberTran.getTrade_multiple(), coefficient);
		NPendingDelegationCache cache = new NPendingDelegationCache(newMemberTran.getTransaction_id(), newMemberTran.getTransaction_code(), needPendDirection, tradeCount, bcj, 
				BigDecimal.ZERO, tradeCount,newMemberTran.getTrade_multiple().intValue(), deposit, 
				feeRate, Contents.PRIORITY_BC, newMemberTran.getF_user_id(), BigDecimal.ZERO, newMemberTran.getAverage_price());
		NPendingDelegationCache resultCache = pendingDelegationCacheDao.insertCache(partition, cache);	// 挂单
		Long pendingID = 0l;
		if (resultCache != null) {
			pendingID = resultCache.getDelegation_id();
			if (pendingID == 0) {
				logger.error("爆仓挂单未获取到用户的挂单ID, 用户ID--" + newMemberTran.getF_user_id());
			}
		}

		// 挂单日志
		NPendingLog pendingLog = new NPendingLog();
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
		pendingLogDao.insertLog(partition, pendingLog);

		// 记录爆仓日志
		NMemberTransactionOverloadLog olog = new NMemberTransactionOverloadLog();
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
		bqMemberTransactionOverloadLogDao.insert(partition, olog);
		
		// 插入交易记录
		NTradeLog tradeLog = new NTradeLog(newMemberTran.getTransaction_id(), newMemberTran.getTransaction_code(), needTradeTypeID, needPendDirection, newMemberTran.getAverage_price(), bcj, cache.getDelegation_count(), 
				newMemberTran.getTrade_multiple().intValue(), newMemberTran.getDeposit(), income, fee, newMemberTran.getF_user_id(), cache.getPending_no());
		tradeLogDao.insertLog(partition, tradeLog);
		
		// 记录爆仓数量-系统用户持单
		transInsertOrUpdate(partition, newMemberTran.getTrade_type_id(), newMemberTran.getTransaction_code(), newMemberTran.getTransaction_id(), cache.getDelegation_count());
	}
	

	private void transInsertOrUpdate(String partition, int typeid, String scode, long icode, BigDecimal amount) {
		long uid = Contents.BQMEMBER_ADMIN;
		String selectHQL = "select * FROM " + partition + "_member_transaction b WHERE f_user_id=:uid AND trade_type_id=:type AND transaction_code=:tranid";
		Map<String, Object> selectMap = new HashMap<String, Object>();
		selectMap.put("uid", uid);
		selectMap.put("type", typeid);
		selectMap.put("tranid", scode);
		List<NMemberTransaction> trans = bqMemberTransactionDao.findBySql(selectHQL, selectMap);

		if (trans != null && trans.size() > 0) {
			String hql = "UPDATE " + partition + "_member_transaction b SET b.hold_count=b.hold_count+:amount WHERE mt_id=:id";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("amount", amount);
			map.put("id", trans.get(0).getMt_id());

			bqMemberTransactionDao.updateConditionBySql(hql, map);
		} else {
			NMemberTransaction o = new NMemberTransaction();

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

			bqMemberTransactionDao.insert(partition, o);
		}
	}

}
