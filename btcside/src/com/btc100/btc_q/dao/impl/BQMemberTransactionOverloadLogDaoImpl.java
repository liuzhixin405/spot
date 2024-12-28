package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQMemberTransactionOverloadLogDao;
import com.btc100.btc_q.entity.BQMemberTransactionOverloadLog;

@Repository("bqMemberTransactionOverloadLogDao")
public class BQMemberTransactionOverloadLogDaoImpl<T> extends BaseDaoImpl<T> implements BQMemberTransactionOverloadLogDao<T> {

	@Override
	public BQMemberTransactionOverloadLog getByPendingNo(String code, long uid, long pendingNo) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);
		map.put("uid", uid);
		map.put("pendingNo", pendingNo);
		
		return (BQMemberTransactionOverloadLog) get("from BQMemberTransactionOverloadLog where transaction_code = :code and f_user_id = :uid and pending_no = :pendingNo", map);
	}

	@Override
	public void addBalance(String code, long uid, long pendingNo, BigDecimal change) {
		Session session = getSession();
		String sql = "update bq_member_transaction_overload_log set balance = balance + :change where transaction_code = :code and f_user_id = :uid and pending_no = :pendingNo";
		SQLQuery query1 = session.createSQLQuery(sql);
		query1.setBigDecimal("change", change);
		query1.setString("code", code);
		query1.setLong("uid", uid);
		query1.setLong("pendingNo", pendingNo);
		query1.executeUpdate();
		
	} 
}
