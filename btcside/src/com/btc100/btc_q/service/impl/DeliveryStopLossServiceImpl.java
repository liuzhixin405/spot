package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.DeliveryMemberTransactionDao;
import com.btc100.btc_q.dao.DeliveryStopLossDao;
import com.btc100.btc_q.dao.DeliveryStopLossLogDao;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryStopLoss;
import com.btc100.btc_q.entity.DeliveryStopLossLog;
import com.btc100.btc_q.service.DeliveryStopLossService;
import com.btc100.btc_q.util.StringUtil;

@Service("deliveryStopLossService")
public class DeliveryStopLossServiceImpl implements DeliveryStopLossService {
	@Autowired
	private DeliveryStopLossDao<DeliveryStopLoss> deliveryStopLossDao;
	@Autowired
	private DeliveryStopLossLogDao<DeliveryStopLossLog> deliveryStopLossLogDao;
	@Autowired
	private DeliveryMemberTransactionDao<DeliveryMemberTransaction> bqMemberTransactionDao;
	
	@SuppressWarnings("unchecked")
	public List<DeliveryStopLoss> getStopLossByMid(long tmp_mId) {
		List<DeliveryStopLoss> list = deliveryStopLossDao.createQuery("from DeliveryStopLoss t where t.transaction_member_id = :transaction_member_id order by t.stop_loss_type_id asc ").setParameter("transaction_member_id", tmp_mId).list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<DeliveryStopLoss> readStopLossList(long uid, Long transaction_member_id) {
		List<DeliveryStopLoss> list = deliveryStopLossDao.createQuery("from DeliveryStopLoss t where t.transaction_member_id = :transaction_member_id AND f_user_id=:uid order by t.stop_loss_type_id asc ").setParameter("transaction_member_id", transaction_member_id).setParameter("uid", uid).list();
		return list;
	}

	public void updateStopLosses(Long transaction_member_id, String hasWin, String stopWinPrice, String pendingWinPrice,
			String hasLoss, String stopLossPrice, String pendingLossPrice, int orderType, String amount) {
		DeliveryMemberTransaction bqmt = bqMemberTransactionDao.get(DeliveryMemberTransaction.class, transaction_member_id);
		if (bqmt == null) return;
		
		deleteByMid(bqmt.getMt_id(), Contents.STOP_LOSS_LOG_ACTION_CD, "用户更新止盈止损-删除原有止盈止损单");

		long linux_time = deliveryStopLossDao.getUnix_TIMESTAMP();
		if (!StringUtil.isEmpty(hasWin) && "1".equalsIgnoreCase(hasWin)) {
			DeliveryStopLoss stopWin = new DeliveryStopLoss();
			stopWin.setTransaction_id(bqmt.getTransaction_id());
			stopWin.setTransaction_code(bqmt.getTransaction_code());
			stopWin.setTransaction_member_id(bqmt.getMt_id());
			stopWin.setOrder_type(orderType);
			stopWin.setStop_loss_type_id(Contents.STOP_WIN);
			stopWin.setStop_loss_price(new BigDecimal(stopWinPrice));
			stopWin.setPending_amount(new BigDecimal(amount));
			stopWin.setPending_trans_price(StringUtil.isEmpty(pendingWinPrice) ? BigDecimal.ZERO : new BigDecimal(pendingWinPrice));
			stopWin.setDateline(linux_time);
			stopWin.setF_user_id(bqmt.getF_user_id());
			stopWin.setTrade_type_id(bqmt.getTrade_type_id());
			deliveryStopLossDao.save(stopWin);
			
			DeliveryStopLossLog log = new DeliveryStopLossLog(stopWin, Contents.STOP_LOSS_LOG_ACTION_CS, "用户更新/新加止盈止损-新加止盈止损单");
			deliveryStopLossLogDao.insertLog(log);
		}
		
		if (!StringUtil.isEmpty(hasLoss) && "1".equalsIgnoreCase(hasLoss)) {
			DeliveryStopLoss stopLoss = new DeliveryStopLoss();
			stopLoss.setTransaction_id(bqmt.getTransaction_id());
			stopLoss.setTransaction_code(bqmt.getTransaction_code());
			stopLoss.setTransaction_member_id(bqmt.getMt_id());
			stopLoss.setOrder_type(orderType);
			stopLoss.setStop_loss_type_id(Contents.STOP_LOSS);
			stopLoss.setStop_loss_price(new BigDecimal(stopLossPrice));
			stopLoss.setPending_amount(new BigDecimal(amount));
			stopLoss.setPending_trans_price(StringUtil.isEmpty(pendingLossPrice) ? BigDecimal.ZERO : new BigDecimal(pendingLossPrice));
			stopLoss.setDateline(linux_time);
			stopLoss.setF_user_id(bqmt.getF_user_id());
			stopLoss.setTrade_type_id(bqmt.getTrade_type_id());
			deliveryStopLossDao.save(stopLoss);
			
			DeliveryStopLossLog log = new DeliveryStopLossLog(stopLoss, Contents.STOP_LOSS_LOG_ACTION_CS, "用户更新/新加止盈止损-新加止盈止损单");
			deliveryStopLossLogDao.insertLog(log);
		}
		
	}

	@SuppressWarnings("unchecked")
	public List<DeliveryStopLoss> getStopLossByPrice(BigDecimal lastPrice, int trade_type_id, String transaction_code) {
		List<DeliveryStopLoss> list = null;
		if(Contents.TRADE_TYPE_BUY == trade_type_id){//多仓止损
			list = deliveryStopLossDao.createQuery("from DeliveryStopLoss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price >= :lastPrice  order by t.id asc ")
			.setParameter("stop_loss_type_id", Contents.STOP_LOSS)
			.setParameter("trade_type_id", trade_type_id)
			.setParameter("transaction_code", transaction_code)
			.setParameter("lastPrice", lastPrice)
			.list();
		}else{//空仓止损
			list = deliveryStopLossDao.createQuery("from DeliveryStopLoss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price <= :lastPrice  order by t.id asc ")
			.setParameter("stop_loss_type_id", Contents.STOP_LOSS)
			.setParameter("trade_type_id", trade_type_id)
			.setParameter("transaction_code", transaction_code)
			.setParameter("lastPrice", lastPrice)
			.list();
		}	

		return list;
	}

	@SuppressWarnings("unchecked")
	public List<DeliveryStopLoss> getStopWinByPrice(BigDecimal lastPrice, int trade_type_id, String transaction_code) {
		List<DeliveryStopLoss> list = null;
		if(Contents.TRADE_TYPE_BUY == trade_type_id){//多仓止赢
			list = deliveryStopLossDao.createQuery("from DeliveryStopLoss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price <= :lastPrice  order by t.id asc ")
			.setParameter("stop_loss_type_id", Contents.STOP_WIN)
			.setParameter("trade_type_id", trade_type_id)
			.setParameter("transaction_code", transaction_code)
			.setParameter("lastPrice", lastPrice)
			.list();
		}else{//空仓止赢
			list = deliveryStopLossDao.createQuery("from DeliveryStopLoss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price >= :lastPrice  order by t.id asc ")
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
		List<DeliveryStopLoss> list = deliveryStopLossDao.createQuery("from DeliveryStopLoss t where t.transaction_member_id = :transaction_member_id ").setParameter("transaction_member_id", mtId).setFirstResult(0).setMaxResults(1).list();
		return list.size() > 0 ? 1 : 0;
	}

	@Override
	public void deleteByMid(long mId, int action, String remark) {
		
		List<DeliveryStopLoss> stopLossList = getStopLossByMid(mId);
		for (DeliveryStopLoss stopLoss : stopLossList) {
			DeliveryStopLossLog log = new DeliveryStopLossLog(stopLoss, action, remark);
			deliveryStopLossLogDao.insertLog(log);
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mtId", mId);
		deliveryStopLossDao.deleteCondition("delete from DeliveryStopLoss where transaction_member_id = :mtId", params);
		
	}

}
