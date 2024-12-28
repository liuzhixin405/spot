package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.NStopLossLogDao;
import com.btc100.btc_q.entity.NStopLoss;

@Repository("nStopLossLogDao")
public class NStopLossLogDaoImpl extends BaseDaoImpl<NStopLoss> implements NStopLossLogDao<NStopLoss> {

	public void insertLog(String partition, com.btc100.btc_q.entity.NStopLossLog log) {
		Session session = getSession();
		String sql = "insert into " + partition + "_" + log.getTransaction_code() + "_stop_loss_log"
				+ " (transaction_id,transaction_code,trade_type_id,f_user_id,dateline,action,"
				+ "remark,stop_loss_type_id,transaction_member_id,stop_loss_price,pending_trans_price) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, log.getTransaction_id());
		query.setString(1, log.getTransaction_code());
		query.setInteger(2, log.getTrade_type_id());
		query.setLong(3, log.getF_user_id());
		query.setLong(4, getUnix_TIMESTAMP());
		query.setInteger(5, log.getAction());
		query.setString(6, log.getRemark());
		query.setInteger(7, log.getStop_loss_type_id());
		query.setLong(8, log.getTransaction_member_id());
		query.setBigDecimal(9, log.getStop_loss_price());
		query.setBigDecimal(10, log.getPending_trans_price());
		query.executeUpdate();
	}

}
