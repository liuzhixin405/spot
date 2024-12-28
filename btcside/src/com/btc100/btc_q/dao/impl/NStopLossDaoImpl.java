package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NStopLossDao;
import com.btc100.btc_q.entity.NStopLoss;

@Repository("nStopLossDao")
public class NStopLossDaoImpl extends BaseDaoImpl<NStopLoss> implements NStopLossDao<NStopLoss> {

	public void deleteByTransactionMemberId(String partition, Long transactionMemberId) {

		Session session = getSession();
          String sql = "delete from " + partition + "_stop_loss where transaction_member_id = ? ";

          SQLQuery query = session.createSQLQuery(sql);
          query.setLong(0, transactionMemberId);
          query.executeUpdate();	
	}
	
	
	@Override
	public int insert(String partition, NStopLoss form) {
		Session session = getSession();
		String sql = "insert into " + partition + "_stop_loss "
				+ "(transaction_id,transaction_code,trade_type_id,stop_loss_type_id,order_type,f_user_id,transaction_member_id,stop_loss_price,"
				+ "pending_amount,pending_trans_price,dateline) values (?,?,?,?,?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);

		query.setLong(0, form.getTransaction_id());
		query.setString(1, form.getTransaction_code());
		query.setInteger(2, form.getTrade_type_id());
		query.setInteger(3, form.getStop_loss_type_id());
		query.setInteger(4, form.getOrder_type());
		query.setLong(5, form.getF_user_id());
		query.setLong(6, form.getTransaction_member_id());
		query.setBigDecimal(7, form.getStop_loss_price());
		query.setBigDecimal(8, form.getPending_amount());
		query.setBigDecimal(9, form.getPending_trans_price());
		query.setLong(10, form.getDateline());
		
		return query.executeUpdate();
	}
}
