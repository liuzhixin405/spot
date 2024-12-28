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
import com.btc100.btc_q.dao.BQMemberTransactionDao;
import com.btc100.btc_q.dao.BQStopLossDao;
import com.btc100.btc_q.dao.BQStopLossLogDao;
import com.btc100.btc_q.dao.BQTransactionDao;
import com.btc100.btc_q.dao.PlanPendingDelegationDao;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQStopLoss;
import com.btc100.btc_q.entity.BQStopLossLog;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.PlanPendingDelegation;
import com.btc100.btc_q.service.BQStopLossService;
import com.btc100.btc_q.util.StringUtil;

@Service("bqStopLossService")
public class BQStopLossServiceImpl implements BQStopLossService {
	@Autowired
	private BQStopLossDao<BQStopLoss> bqStopLossDao;
	@Autowired
	private BQStopLossLogDao<BQStopLossLog> bqStopLossLogDao;
	@Autowired
	private BQMemberTransactionDao<BQMemberTransaction> bqMemberTransactionDao;
	@Autowired
	private PlanPendingDelegationDao<PlanPendingDelegation> planPendingDelegationDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionDao<BQTransaction> bqTransactionDao;
	
	@SuppressWarnings("unchecked")
	public List<BQStopLoss> getStopLossByMid(long tmp_mId) {
		List<BQStopLoss> list = bqStopLossDao.createQuery("from BQStopLoss t where t.transaction_member_id = :transaction_member_id order by t.stop_loss_type_id asc ").setParameter("transaction_member_id", tmp_mId).list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<BQStopLoss> readStopLossList(long uid, Long transaction_member_id) {
		List<BQStopLoss> list = bqStopLossDao.createQuery("from BQStopLoss t where t.transaction_member_id = :transaction_member_id AND f_user_id=:uid order by t.stop_loss_type_id asc ").setParameter("transaction_member_id", transaction_member_id).setParameter("uid", uid).list();
		return list;
	}

	public void updateStopLosses(Long transaction_member_id, String hasWin, String stopWinPrice, String pendingWinPrice,
			String hasLoss, String stopLossPrice, String pendingLossPrice, int orderType, String amount) {
		BQMemberTransaction bqmt = bqMemberTransactionDao.get(BQMemberTransaction.class, transaction_member_id);
		if (bqmt == null) return;

		BigDecimal lastPrice = spyMemcachedClient.get(bqmt.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			lastPrice = readTransByCode(bqmt.getTransaction_code()).getLatest_transaction_price();
		}
		deleteByMid(bqmt.getMt_id(), Contents.STOP_LOSS_LOG_ACTION_CD, "用户更新止盈止损-删除原有止盈止损单");

		long linux_time = bqStopLossDao.getUnix_TIMESTAMP();
		if (!StringUtil.isEmpty(hasWin) && "1".equalsIgnoreCase(hasWin)) {

			PlanPendingDelegation stopWin = new PlanPendingDelegation();
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
			PlanPendingDelegation bqt = planPendingDelegationDao.getByStatusDirectionAndPositionId(stopWin.getF_code(), stopWin.getF_user_id(),
					Contents.PLAN_PENDING_STATUS_INEFFECTIVE, stopWin.getTransaction_member_id(), stopWin.getStop_loss_type_id());
			if (null != bqt && bqt.getId() > 0) {
				planPendingDelegationDao.updateStopWinAndLoss(bqt.getId(), stopWin);
			} else {
				planPendingDelegationDao.insert(stopWin);
			}

//			BQStopLoss stopWin = new BQStopLoss();
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
//			bqStopLossDao.save(stopWin);
			
//			BQStopLossLog log = new BQStopLossLog(stopWin, Contents.STOP_LOSS_LOG_ACTION_CS, "用户更新/新加止盈止损-新加止盈止损单");
//			bqStopLossLogDao.insertLog(log);
		}
		
		if (!StringUtil.isEmpty(hasLoss) && "1".equalsIgnoreCase(hasLoss)) {

			PlanPendingDelegation stopLoss = new PlanPendingDelegation();
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
			PlanPendingDelegation bqt = planPendingDelegationDao.getByStatusDirectionAndPositionId(stopLoss.getF_code(), stopLoss.getF_user_id(),
					Contents.PLAN_PENDING_STATUS_INEFFECTIVE, stopLoss.getTransaction_member_id(), stopLoss.getStop_loss_type_id());
			if (null != bqt && bqt.getId() > 0) {
				planPendingDelegationDao.updateStopWinAndLoss(bqt.getId(), stopLoss);
			} else {
				planPendingDelegationDao.insert(stopLoss);
			}

//			BQStopLoss stopLoss = new BQStopLoss();
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
//			bqStopLossDao.save(stopLoss);
//
//			BQStopLossLog log = new BQStopLossLog(stopLoss, Contents.STOP_LOSS_LOG_ACTION_CS, "用户更新/新加止盈止损-新加止盈止损单");
//			bqStopLossLogDao.insertLog(log);
		}
		
	}

	public BQTransaction readTransByCode(String code) {
		BQTransaction transaction = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTION_KEY + code);
		if (transaction == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("code", code);
			transaction = bqTransactionDao.get(" from BQTransaction t where t.transaction_code = :code ", params);
			if (transaction != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTION_KEY + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, transaction);
			}
		}

		return transaction;
	}

	@SuppressWarnings("unchecked")
	public List<BQStopLoss> getStopLossByPrice(BigDecimal lastPrice, int trade_type_id, String transaction_code) {
		List<BQStopLoss> list = null;
		if(Contents.TRADE_TYPE_BUY == trade_type_id){//多仓止损
			list = bqStopLossDao.createQuery("from BQStopLoss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price >= :lastPrice  order by t.id asc ")
			.setParameter("stop_loss_type_id", Contents.STOP_LOSS)
			.setParameter("trade_type_id", trade_type_id)
			.setParameter("transaction_code", transaction_code)
			.setParameter("lastPrice", lastPrice)
			.list();
		}else{//空仓止损
			list = bqStopLossDao.createQuery("from BQStopLoss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price <= :lastPrice  order by t.id asc ")
			.setParameter("stop_loss_type_id", Contents.STOP_LOSS)
			.setParameter("trade_type_id", trade_type_id)
			.setParameter("transaction_code", transaction_code)
			.setParameter("lastPrice", lastPrice)
			.list();
		}	

		return list;
	}

	@SuppressWarnings("unchecked")
	public List<BQStopLoss> getStopWinByPrice(BigDecimal lastPrice, int trade_type_id, String transaction_code) {
		List<BQStopLoss> list = null;
		if(Contents.TRADE_TYPE_BUY == trade_type_id){//多仓止赢
			list = bqStopLossDao.createQuery("from BQStopLoss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price <= :lastPrice  order by t.id asc ")
			.setParameter("stop_loss_type_id", Contents.STOP_WIN)
			.setParameter("trade_type_id", trade_type_id)
			.setParameter("transaction_code", transaction_code)
			.setParameter("lastPrice", lastPrice)
			.list();
		}else{//空仓止赢
			list = bqStopLossDao.createQuery("from BQStopLoss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price >= :lastPrice  order by t.id asc ")
			.setParameter("stop_loss_type_id", Contents.STOP_WIN)
			.setParameter("trade_type_id", trade_type_id)
			.setParameter("transaction_code", transaction_code)
			.setParameter("lastPrice", lastPrice)
			.list();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public int readHasStopLoss(long mtId) {
		List<BQStopLoss> list = bqStopLossDao.createQuery("from BQStopLoss t where t.transaction_member_id = :transaction_member_id ").setParameter("transaction_member_id", mtId).setFirstResult(0).setMaxResults(1).list();
		return list.size() > 0 ? 1 : 0;
	}

	@Override
	public void deleteByMid(long mId, int action, String remark) {
		
		List<BQStopLoss> stopLossList = getStopLossByMid(mId);
		for (BQStopLoss stopLoss : stopLossList) {
			BQStopLossLog log = new BQStopLossLog(stopLoss, action, remark);
			bqStopLossLogDao.insertLog(log);
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mtId", mId);
		bqStopLossDao.deleteCondition("delete from BQStopLoss where transaction_member_id = :mtId", params);
		
	}

}
