package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.NNotradetimeDao;
import com.btc100.btc_q.entity.NNotradetime;

@Repository("nNotradetimeDao")
public class NNotradetimeDaoImpl extends BaseDaoImpl<NNotradetime> implements NNotradetimeDao<NNotradetime> {

	@Override
	public int insert(String partition, NNotradetime time) {
		Session session = getSession();
		String sql = "insert into " + partition + "_notradetime "
				+ "(h_time,h_type,transaction_id) values (?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, time.getH_time());
		query.setInteger(1, time.getH_type());
		query.setLong(2, time.getTransaction_id());
		
		return query.executeUpdate();
	}
	
}
