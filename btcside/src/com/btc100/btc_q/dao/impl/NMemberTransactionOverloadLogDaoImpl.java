package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NMemberTransactionOverloadLogDao;
import com.btc100.btc_q.entity.NMemberTransactionOverloadLog;

@Repository("nMemberTransactionOverloadLogDao")
public class NMemberTransactionOverloadLogDaoImpl extends BaseDaoImpl<NMemberTransactionOverloadLog> implements NMemberTransactionOverloadLogDao<NMemberTransactionOverloadLog> {

	@Override
	public NMemberTransactionOverloadLog getByPendingNo(String partition, String code, long uid, long pendingNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);
		map.put("uid", uid);
		map.put("pendingNo", pendingNo);
		
		return (NMemberTransactionOverloadLog) getBySql("select * from " + partition + "_member_transaction_overload_log where transaction_code = :code and f_user_id = :uid and pending_no = :pendingNo", map);
	}

	@Override
	public void addBalance(String partition, String code, long uid, long pendingNo, BigDecimal change) {
		Session session = getSession();
		String sql = "update " + partition + "_member_transaction_overload_log set balance = balance + :change where transaction_code = :code and f_user_id = :uid and pending_no = :pendingNo";
		SQLQuery query1 = session.createSQLQuery(sql);
		query1.setBigDecimal("change", change);
		query1.setString("code", code);
		query1.setLong("uid", uid);
		query1.setLong("pendingNo", pendingNo);
		query1.executeUpdate();
	}

	@Override
	public int insert(String partition, NMemberTransactionOverloadLog log) {
		Session session = getSession();
		String sql = "insert into " + partition + "_member_transaction_overload_log "
				+ "(f_user_id,transaction_id,transaction_code,trade_price,trade_count,trade_deposit,trade_overload_price,trade_force_price,"
				+ "trade_fee,delegation_id,dateline,direction,balance,pending_no,f_fromremain) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);

		query.setLong(0, log.getF_user_id());
		query.setLong(1, log.getTransaction_id());
		query.setString(2, log.getTransaction_code());
		query.setBigDecimal(3, log.getTrade_price());
		query.setBigDecimal(4, log.getTrade_count());
		query.setBigDecimal(5, log.getTrade_deposit());
		query.setBigDecimal(6, log.getTrade_overload_price());
		query.setBigDecimal(7, log.getTrade_force_price());
		query.setBigDecimal(8, log.getTrade_fee());
		query.setLong(9, log.getDelegation_id());
		query.setLong(10, log.getDateline());
		query.setInteger(11, log.getDirection());
		query.setBigDecimal(12, log.getBalance());
		query.setLong(13, log.getPending_no());
		query.setBigDecimal(14, log.getF_fromremain());
		
		return query.executeUpdate();
	} 
	
}
