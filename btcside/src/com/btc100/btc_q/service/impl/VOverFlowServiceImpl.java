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
import com.btc100.btc_q.dao.VAccountContractDao;
import com.btc100.btc_q.dao.VMemberTransactionDao;
import com.btc100.btc_q.dao.VMemberTransactionOverloadLogDao;
import com.btc100.btc_q.dao.VPendingDelegationCacheDao;
import com.btc100.btc_q.dao.VPendingLogDao;
import com.btc100.btc_q.dao.VTradeLogDao;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.entity.VMemberTransactionOverloadLog;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.entity.VPendingLog;
import com.btc100.btc_q.entity.VTradeLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.VOverFlowService;

@Service("vOverFlowService")
public class VOverFlowServiceImpl implements VOverFlowService {
	
	private static final Logger logger = Logger.getLogger(VOverFlowServiceImpl.class);
	
	@Autowired
	private VMemberTransactionDao<VMemberTransaction> vMemberTransactionDao;
	@Autowired
	private VPendingDelegationCacheDao<VPendingDelegationCache> vPendingDelegationCacheDao;
	@Autowired
	private VPendingLogDao<VPendingLog> vPendingLogDao;
	@Autowired
	private VMemberTransactionOverloadLogDao<VMemberTransactionOverloadLog> vMemberTransactionOverloadLogDao;
	@Autowired
	private VTradeLogDao<VTradeLog> vTradeLogDao;
	@Autowired
	private VAccountContractDao<VAccountContract> vAccountContractDao;
	
	@Override
	public void overFlowDelegate(int diection, BigDecimal qpj, int digits, VMemberTransaction trans, BQMemberLevel userLevel, BigDecimal coefficient) throws ServiceException {
		
		BigDecimal changeRemain = BigDecimal.ZERO;
		if (trans.getF_lockmoney().compareTo(BigDecimal.ZERO) > 0) {
			
			VAccountContract account = vAccountContractDao.findByUserIDCode(trans.getF_user_id(), trans.getTransaction_code());
			if (account.getF_btc().compareTo(trans.getF_lockmoney()) >= 0) {
				changeRemain = trans.getF_lockmoney();
			} else {
				changeRemain = account.getF_btc();
			}
				
			if (changeRemain.compareTo(BigDecimal.ZERO) != 0) {
				if (vAccountContractDao.updateAmount(trans.getF_user_id(), trans.getTransaction_code(), BigDecimal.ZERO, changeRemain.negate(), BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
					throw new ServiceException("");
				}
			}
		}
		
		// 上面有进行强平操作，用户持单可能有变，需重新获取
		VMemberTransaction newMemberTran = vMemberTransactionDao.getMemberTransactionById(trans.getMt_id());
		if (newMemberTran == null || newMemberTran.getHold_count().compareTo(BigDecimal.ZERO) <= 0) {
			throw new RuntimeException("获取用户持仓为空或持仓为0");	
		}
		
		// 删除该持仓
		vMemberTransactionDao.delete(newMemberTran);
		
		BigDecimal haveCapital = newMemberTran.getDeposit().add(newMemberTran.getDeposit2()).add(newMemberTran.getF_fromremain())
				.add(newMemberTran.getF_frozenremain()).add(newMemberTran.getF_min_frozenremain()).add(changeRemain);
		BigDecimal bcj = BTCQMath.BCJ(diection, newMemberTran.getAverage_price(), haveCapital,
				userLevel.getEat_commission_rate(), newMemberTran.getHold_count(), coefficient);	// 爆仓价
		
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
		BigDecimal income = bcj.subtract(newMemberTran.getAverage_price()).multiply(newMemberTran.getHold_count());
		if (diection == Contents.DIRECTION_SELL) {
			income = income.negate();
		}
		
		BigDecimal fee = haveCapital.add(income);
		if (fee.compareTo(BigDecimal.ZERO) < 0) fee = BigDecimal.ZERO;
		
		// 扣除资金费(算在盈亏里面) - 已有资金费记录
//		income= income.add(newMemberTran.getDeposit2());
		
		BigDecimal deposit = BTCQMath.getDeposit(newMemberTran.getHold_count(), bcj, newMemberTran.getTrade_multiple(), BigDecimal.ONE);
		VPendingDelegationCache cache = new VPendingDelegationCache(newMemberTran.getTransaction_id(), newMemberTran.getTransaction_code(), needPendDirection, newMemberTran.getHold_count(), bcj, 
				BigDecimal.ZERO, newMemberTran.getHold_count(),newMemberTran.getTrade_multiple().intValue(), deposit, 
				userLevel.getEat_commission_rate(), Contents.PRIORITY_BC, newMemberTran.getF_user_id(), BigDecimal.ZERO, newMemberTran.getAverage_price());
		VPendingDelegationCache resultCache = vPendingDelegationCacheDao.insertCache(cache);	// 挂单
		Long pendingID = 0l;
		if (resultCache != null) {
			pendingID = resultCache.getDelegation_id();
			if (pendingID == 0) {
				logger.error("爆仓挂单未获取到用户的挂单ID, 用户ID--" + newMemberTran.getF_user_id());
			}
		}

		// 挂单日志
		VPendingLog pendingLog = new VPendingLog();
		pendingLog.setAction(Contents.PENDING_LOG_ACTION_QP);	// 强平
		pendingLog.setDateline(vPendingLogDao.getUnix_TIMESTAMP());
		pendingLog.setDeal_count(BigDecimal.ZERO);
		pendingLog.setDelegation_count(cache.getDelegation_count());
		pendingLog.setDelegation_id(pendingID);
		pendingLog.setDelegation_price(bcj);
		pendingLog.setDelegation_time(vPendingLogDao.getUnix_TIMESTAMP());
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
		vPendingLogDao.insertLog(pendingLog);

		// 记录爆仓日志
		VMemberTransactionOverloadLog olog = new VMemberTransactionOverloadLog();
		olog.setDateline(vMemberTransactionOverloadLogDao.getUnix_TIMESTAMP());
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
		vMemberTransactionOverloadLogDao.save(olog);
		
		// 插入交易记录
		VTradeLog tradeLog = new VTradeLog(newMemberTran.getTransaction_id(), newMemberTran.getTransaction_code(), needTradeTypeID, needPendDirection, newMemberTran.getAverage_price(), bcj, cache.getDelegation_count(), 
				newMemberTran.getTrade_multiple().intValue(), newMemberTran.getDeposit(), income, fee, newMemberTran.getF_user_id(), cache.getPending_no());
		vTradeLogDao.insertLog(tradeLog);
		
		// 记录爆仓数量-系统用户持单
		transInsertOrUpdate(newMemberTran.getTrade_type_id(), newMemberTran.getTransaction_code(), newMemberTran.getTransaction_id(), cache.getDelegation_count());
	}
	

	private void transInsertOrUpdate(int typeid, String scode, long icode, BigDecimal amount) {
		long uid = Contents.BQMEMBER_ADMIN;
		String selectHQL = "FROM VMemberTransaction b WHERE f_user_id=:uid AND trade_type_id=:type AND transaction_code=:tranid";
		Map<String, Object> selectMap = new HashMap<String, Object>();
		selectMap.put("uid", uid);
		selectMap.put("type", typeid);
		selectMap.put("tranid", scode);
		List<VMemberTransaction> trans = vMemberTransactionDao.find(selectHQL, selectMap);

		if (trans != null && trans.size() > 0) {
			String hql = "UPDATE VMemberTransaction b SET b.hold_count=b.hold_count+:amount WHERE mt_id=:id";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("amount", amount);
			map.put("id", trans.get(0).getMt_id());

			vMemberTransactionDao.updateCondition(hql, map);
		} else {
			VMemberTransaction o = new VMemberTransaction();

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

			vMemberTransactionDao.save(o);
		}
	}

}
