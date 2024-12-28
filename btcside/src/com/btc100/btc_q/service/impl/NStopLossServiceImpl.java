package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.NMemberTransactionDao;
import com.btc100.btc_q.dao.NPlanPendingDelegationDao;
import com.btc100.btc_q.dao.NStopLossDao;
import com.btc100.btc_q.dao.NStopLossLogDao;
import com.btc100.btc_q.dao.NTransactionDao;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NPlanPendingDelegation;
import com.btc100.btc_q.entity.NStopLoss;
import com.btc100.btc_q.entity.NStopLossLog;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.service.NStopLossService;
import com.btc100.btc_q.util.StringUtil;

@Service("nStopLossService")
public class NStopLossServiceImpl implements NStopLossService {
	
	@Autowired
	private NStopLossDao<NStopLoss> bqStopLossDao;
	@Autowired
	private NStopLossLogDao<NStopLossLog> bqStopLossLogDao;
	@Autowired
	private NMemberTransactionDao<NMemberTransaction> bqMemberTransactionDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NPlanPendingDelegationDao<NPlanPendingDelegation> planPendingDelegationDao;
	@Autowired
	private NTransactionDao<NTransaction> transactionDao;
	
	public List<NStopLoss> getStopLossByMid(String partition, long tmp_mId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("transaction_member_id", tmp_mId);
		return bqStopLossDao.findBySql("select * from " + partition + "_stop_loss t where t.transaction_member_id = :transaction_member_id order by t.stop_loss_type_id asc ", params);
	}
	
	public List<NStopLoss> readStopLossList(String partition, long uid, Long transaction_member_id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("transaction_member_id", transaction_member_id);
		params.put("uid", uid);
		return bqStopLossDao.findBySql("select * from " + partition + "_stop_loss t where t.transaction_member_id = :transaction_member_id AND f_user_id=:uid order by t.stop_loss_type_id asc ", params);
	}

	public void updateStopLosses(String partition, Long transaction_member_id, String hasWin, String stopWinPrice, String pendingWinPrice,
			String hasLoss, String stopLossPrice, String pendingLossPrice, int orderType, String amount) {
		
		NMemberTransaction bqmt = bqMemberTransactionDao.getMemberTransactionById(partition, transaction_member_id);
		if (bqmt == null) return;

		BigDecimal lastPrice = spyMemcachedClient.get(partition + "_" + bqmt.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = readTransByCode(partition, bqmt.getTransaction_code()).getLatest_transaction_price();
		}
		deleteByMid(partition, bqmt.getMt_id(), Contents.STOP_LOSS_LOG_ACTION_CD, "用户更新止盈止损-删除原有止盈止损单");

		long linux_time = bqStopLossDao.getUnix_TIMESTAMP();
		if (!StringUtil.isEmpty(hasWin) && "1".equalsIgnoreCase(hasWin)) {

			NPlanPendingDelegation stopWin = new NPlanPendingDelegation();
			stopWin.setF_code(bqmt.getTransaction_code());
			stopWin.setF_user_id(bqmt.getF_user_id());
			stopWin.setOrder_type(orderType);
			stopWin.setTri_price(new BigDecimal(stopWinPrice));
			stopWin.setDelegation_price(StringUtil.isEmpty(pendingWinPrice) ? BigDecimal.ZERO : new BigDecimal(pendingWinPrice));
			stopWin.setDelegation_count(new BigDecimal(amount));
			stopWin.setDelegation_time(linux_time);
			stopWin.setMultiple(bqmt.getTrade_multiple().intValue());
			stopWin.setDirection(bqmt.getTrade_type_id() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY);
			stopWin.setType(lastPrice.compareTo(new BigDecimal(stopWinPrice)) > 0 ? Contents.PLAN_PENDING_TYPE_GT : Contents.PLAN_PENDING_TYPE_LT);
			stopWin.setStatus(Contents.PLAN_PENDING_STATUS_INEFFECTIVE);
			stopWin.setNote("止盈计划委托挂单创建-未生效");
			stopWin.setTrade_type(1);
			stopWin.setTransaction_member_id(bqmt.getMt_id());
			stopWin.setStop_loss_type_id(1);
			NPlanPendingDelegation bqt = planPendingDelegationDao.getByStatusDirectionAndPositionId(partition, stopWin.getF_code(), stopWin.getF_user_id(),
					Contents.PLAN_PENDING_STATUS_INEFFECTIVE, stopWin.getTransaction_member_id(), stopWin.getStop_loss_type_id());
			if (null != bqt && bqt.getId() > 0) {
				planPendingDelegationDao.updateStopWinAndLoss(partition, bqt.getId(), stopWin);
			} else {
				planPendingDelegationDao.insert(partition, stopWin);
			}

//			NStopLoss stopWin = new NStopLoss();
//			stopWin.setTransaction_id(bqmt.getTransaction_id());
//			stopWin.setTransaction_code(bqmt.getTransaction_code());
//			stopWin.setTransaction_member_id(bqmt.getMt_id());
//			stopWin.setOrder_type(orderType);
//			stopWin.setStop_loss_type_id(Contents.STOP_WIN);
//			stopWin.setStop_loss_price(new BigDecimal(stopWinPrice));
//			stopWin.setPending_amount(new BigDecimal(amount));
//			stopWin.setPending_trans_price(StringUtil.isEmpty(pendingWinPrice) ? BigDecimal.ZERO : new BigDecimal(pendingWinPrice));
//			stopWin.setDateline(linux_time);
//			stopWin.setF_user_id(bqmt.getF_user_id());
//			stopWin.setTrade_type_id(bqmt.getTrade_type_id());
//			bqStopLossDao.insert(partition, stopWin);
//
//			NStopLossLog log = new NStopLossLog(stopWin, Contents.STOP_LOSS_LOG_ACTION_CS, "用户更新/新加止盈止损-新加止盈止损单");
//			bqStopLossLogDao.insertLog(partition, log);
		}
		
		if (!StringUtil.isEmpty(hasLoss) && "1".equalsIgnoreCase(hasLoss)) {

			NPlanPendingDelegation stopLoss = new NPlanPendingDelegation();
			stopLoss.setF_code(bqmt.getTransaction_code());
			stopLoss.setF_user_id(bqmt.getF_user_id());
			stopLoss.setOrder_type(orderType);
			stopLoss.setTri_price(new BigDecimal(stopLossPrice));
			stopLoss.setDelegation_price(StringUtil.isEmpty(pendingLossPrice) ? BigDecimal.ZERO : new BigDecimal(pendingLossPrice));
			stopLoss.setDelegation_count(new BigDecimal(amount));
			stopLoss.setDelegation_time(linux_time);
			stopLoss.setMultiple(bqmt.getTrade_multiple().intValue());
			stopLoss.setDirection(bqmt.getTrade_type_id() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY);
			stopLoss.setType(lastPrice.compareTo(new BigDecimal(stopLossPrice)) > 0 ? Contents.PLAN_PENDING_TYPE_GT : Contents.PLAN_PENDING_TYPE_LT);
			stopLoss.setStatus(Contents.PLAN_PENDING_STATUS_INEFFECTIVE);
			stopLoss.setNote("止损计划委托挂单创建-未生效");
			stopLoss.setTrade_type(1);
			stopLoss.setTransaction_member_id(bqmt.getMt_id());
			stopLoss.setStop_loss_type_id(2);
			NPlanPendingDelegation bqt = planPendingDelegationDao.getByStatusDirectionAndPositionId(partition, stopLoss.getF_code(), stopLoss.getF_user_id(),
					Contents.PLAN_PENDING_STATUS_INEFFECTIVE, stopLoss.getTransaction_member_id(), stopLoss.getStop_loss_type_id());
			if (null != bqt && bqt.getId() > 0) {
				planPendingDelegationDao.updateStopWinAndLoss(partition, bqt.getId(), stopLoss);
			} else {
				planPendingDelegationDao.insert(partition, stopLoss);
			}

//			NStopLoss stopLoss = new NStopLoss();
//			stopLoss.setTransaction_id(bqmt.getTransaction_id());
//			stopLoss.setTransaction_code(bqmt.getTransaction_code());
//			stopLoss.setTransaction_member_id(bqmt.getMt_id());
//			stopLoss.setOrder_type(orderType);
//			stopLoss.setStop_loss_type_id(Contents.STOP_LOSS);
//			stopLoss.setStop_loss_price(new BigDecimal(stopLossPrice));
//			stopLoss.setPending_amount(new BigDecimal(amount));
//			stopLoss.setPending_trans_price(StringUtil.isEmpty(pendingLossPrice) ? BigDecimal.ZERO : new BigDecimal(pendingLossPrice));
//			stopLoss.setDateline(linux_time);
//			stopLoss.setF_user_id(bqmt.getF_user_id());
//			stopLoss.setTrade_type_id(bqmt.getTrade_type_id());
//			bqStopLossDao.insert(partition, stopLoss);
//
//			NStopLossLog log = new NStopLossLog(stopLoss, Contents.STOP_LOSS_LOG_ACTION_CS, "用户更新/新加止盈止损-新加止盈止损单");
//			bqStopLossLogDao.insertLog(partition, log);
		}
		
	}

	public NTransaction readTransByCode(String partition, String code) {
		NTransaction transaction = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTION_KEY + partition + "_" + code);
		if (transaction == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("code", code);
			transaction = transactionDao.getBySql("select * from " + partition + "_transaction t where t.transaction_code = :code ", params);
			if (transaction != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTION_KEY + partition + "_" + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, transaction);
			}
		}

		return transaction;
	}

	public List<NStopLoss> getStopLossByPrice(String partition, BigDecimal lastPrice, int trade_type_id, String transaction_code) {
		List<NStopLoss> list = null;
		if(Contents.TRADE_TYPE_BUY == trade_type_id){//多仓止损
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("stop_loss_type_id", Contents.STOP_LOSS);
			params.put("trade_type_id", trade_type_id);
			params.put("transaction_code", transaction_code);
			params.put("lastPrice", lastPrice);
			
			
			list = bqStopLossDao.findBySql("select * from " + partition + "_stop_loss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price >= :lastPrice  order by t.id asc ", params);
		}else{//空仓止损
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("stop_loss_type_id", Contents.STOP_LOSS);
			params.put("trade_type_id", trade_type_id);
			params.put("transaction_code", transaction_code);
			params.put("lastPrice", lastPrice);
			
			list = bqStopLossDao.findBySql("select * from " + partition + "_stop_loss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price <= :lastPrice  order by t.id asc ", params);
		}	

		return list;
	}

	public List<NStopLoss> getStopWinByPrice(String partition, BigDecimal lastPrice, int trade_type_id, String transaction_code) {
		List<NStopLoss> list = null;
		if(Contents.TRADE_TYPE_BUY == trade_type_id){//多仓止赢
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("stop_loss_type_id", Contents.STOP_WIN);
			params.put("trade_type_id", trade_type_id);
			params.put("transaction_code", transaction_code);
			params.put("lastPrice", lastPrice);
			
			list = bqStopLossDao.findBySql("select * from " + partition + "_stop_loss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price <= :lastPrice  order by t.id asc ", params);
		}else{//空仓止赢
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("stop_loss_type_id", Contents.STOP_WIN);
			params.put("trade_type_id", trade_type_id);
			params.put("transaction_code", transaction_code);
			params.put("lastPrice", lastPrice);
			
			list = bqStopLossDao.findBySql("select * from " + partition + "_stop_loss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price >= :lastPrice  order by t.id asc ", params);
		}
		return list;
	}

	public int readHasStopLoss(String partition, long mtId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("transaction_member_id", mtId);
		NStopLoss stopLoss  = bqStopLossDao.getBySql("select * from " + partition + "_stop_loss t where t.transaction_member_id = :transaction_member_id ", params);
		return (stopLoss != null) ? 1 : 0;
	}

	@Override
	public void deleteByMid(String partition, long mId, int action, String remark) {
		
		List<NStopLoss> stopLossList = getStopLossByMid(partition, mId);
		for (NStopLoss stopLoss : stopLossList) {
			NStopLossLog log = new NStopLossLog(stopLoss, action, remark);
			bqStopLossLogDao.insertLog(partition, log);
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mtId", mId);
		bqStopLossDao.deleteConditionBySql("delete from " + partition + "_stop_loss where transaction_member_id = :mtId", params);
		
	}

}
