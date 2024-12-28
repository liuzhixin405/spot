package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.VStopLossDao;

@Repository("vStopLossDao")
public class VStopLossDaoImpl<T> extends BaseDaoImpl<T> implements VStopLossDao<T> {

	public void deleteByTransactionMemberId(Long transactionMemberId) {

		Session session = getSession();
          String sql = "delete from v_stop_loss where transaction_member_id = ? ";

          SQLQuery query = session.createSQLQuery(sql);
          query.setLong(0, transactionMemberId);
          query.executeUpdate();
	
		
	} }
