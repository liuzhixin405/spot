package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.VMemberTransactionDao;
import com.btc100.btc_q.dao.VStopLossDao;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.entity.VStopLoss;
import com.btc100.btc_q.service.VStopLossService;
import com.btc100.btc_q.util.StringUtil;

@Service("vStopLossService")
public class VStopLossServiceImpl implements VStopLossService {
	@Autowired
	private VStopLossDao<VStopLoss> vStopLossDao;
	@Autowired
	private VMemberTransactionDao<VMemberTransaction> vMemberTransactionDao;
	@SuppressWarnings("unchecked")
	public List<VStopLoss> readStopLossList(long uid, Long transaction_member_id) {
		List<VStopLoss> list = vStopLossDao.createQuery("from VStopLoss t where t.transaction_member_id = :transaction_member_id AND f_user_id=:uid order by t.stop_loss_type_id asc ").setParameter("transaction_member_id", transaction_member_id).setParameter("uid", uid).list();
		return list;
	}

	public void updateStopLosses(Long transaction_member_id, String hasWin, String stopWinPrice, String pendingWinPrice,
			String hasLoss, String stopLossPrice, String pendingLossPrice, int orderType, String amount) {
		VMemberTransaction bqmt = vMemberTransactionDao.get(VMemberTransaction.class, transaction_member_id);
		vStopLossDao.deleteByTransactionMemberId(transaction_member_id);
		long linux_time = vStopLossDao.getUnix_TIMESTAMP();
		if (!StringUtil.isEmpty(hasWin) && "1".equalsIgnoreCase(hasWin)) {
			VStopLoss stopWin = new VStopLoss();
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
			vStopLossDao.save(stopWin);
		}
		if (!StringUtil.isEmpty(hasLoss) && "1".equalsIgnoreCase(hasLoss)) {
			VStopLoss stopLoss = new VStopLoss();
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
			vStopLossDao.save(stopLoss);
		}
	}

	@SuppressWarnings("unchecked")
	public List<VStopLoss> getStopLossByPrice(BigDecimal lastPrice, int trade_type_id, String transaction_code) {
		List<VStopLoss> list = null;
		if(Contents.TRADE_TYPE_BUY == trade_type_id){//多仓止损
			list = vStopLossDao.createQuery("from VStopLoss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price >= :lastPrice  order by t.id asc ")
			.setParameter("stop_loss_type_id", Contents.STOP_LOSS)
			.setParameter("trade_type_id", trade_type_id)
			.setParameter("transaction_code", transaction_code)
			.setParameter("lastPrice", lastPrice)
			.list();
		}else{//空仓止损
			list = vStopLossDao.createQuery("from VStopLoss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price <= :lastPrice  order by t.id asc ")
			.setParameter("stop_loss_type_id", Contents.STOP_LOSS)
			.setParameter("trade_type_id", trade_type_id)
			.setParameter("transaction_code", transaction_code)
			.setParameter("lastPrice", lastPrice)
			.list();
		}	

		return list;
	}

	@SuppressWarnings("unchecked")
	public List<VStopLoss> getStopWinByPrice(BigDecimal lastPrice, int trade_type_id, String transaction_code) {
		List<VStopLoss> list = null;
		if(Contents.TRADE_TYPE_BUY == trade_type_id){//多仓止赢
			list = vStopLossDao.createQuery("from VStopLoss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price <= :lastPrice  order by t.id asc ")
			.setParameter("stop_loss_type_id", Contents.STOP_WIN)
			.setParameter("trade_type_id", trade_type_id)
			.setParameter("transaction_code", transaction_code)
			.setParameter("lastPrice", lastPrice)
			.list();
		}else{//空仓止赢
			list = vStopLossDao.createQuery("from VStopLoss t where t.stop_loss_type_id =:stop_loss_type_id and t.trade_type_id = :trade_type_id and t.transaction_code=:transaction_code and stop_loss_price >= :lastPrice  order by t.id asc ")
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
		List<VStopLoss> list = vStopLossDao.createQuery("from VStopLoss t where t.transaction_member_id = :transaction_member_id ").setParameter("transaction_member_id", mtId).setFirstResult(0).setMaxResults(1).list();
		return list.size() > 0 ? 1 : 0;
	}

	public void deleteAllByBqCode(String bqCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bqCode", bqCode);
		vStopLossDao.deleteCondition("delete from VStopLoss where transaction_code = :bqCode", params);
	}

	@Override
	public void deleteByMid(long mId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mtId", mId);
		vStopLossDao.deleteCondition("delete from VStopLoss where transaction_member_id = :mtId", params);
		
	}

}
