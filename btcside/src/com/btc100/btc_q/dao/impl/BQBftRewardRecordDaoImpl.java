package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQBftRewardRecordDao;

@Repository("bqBftRewardRecordDao")
public class BQBftRewardRecordDaoImpl<T> extends BaseDaoImpl<T> implements BQBftRewardRecordDao<T> {

	@Override
	public BigDecimal sumAmountBetweenTime(long uid, int currencyId, long startTime, long endTime) {
		String sql = "select IFNULL(sum(b.amount),0) from bq_bft_reward_record b where 1=1";
		sql += " and currency_type_id = :currencyId";
		
		if (uid > 0) {
			sql += " and f_user_id = :uid";
		}
		
		if (startTime > 0) {
			sql += " and record_time >= :startTime";
		}
		
		if (startTime > 0) {
			sql += " and record_time < :endTime";
		}
		
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger("currencyId", currencyId);
		if (uid > 0) {
			query.setLong("uid", uid);
		}
		
		if (startTime > 0) {
			query.setLong("startTime", startTime);
		}
		
		if (startTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		return (BigDecimal) query.uniqueResult();
	}
	
	
}
