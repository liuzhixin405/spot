package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQHourlyAccountDao;
import com.btc100.btc_q.entity.BQHourlyAccount;
import com.btc100.btc_q.service.BQHourlyAccountService;

@Service("bqHourlyAccountService")
public class BQHourlyAccountServiceImpl implements BQHourlyAccountService {

	@Autowired
	private BQHourlyAccountDao<BQHourlyAccount> bqHourlyAccountDao;

	@Override
	public void save(BQHourlyAccount account) {
		bqHourlyAccountDao.save(account);
	}
	
	@Override
	public BigDecimal getMinAmountByTime(long startTime, long endTime, long uId, int currencyId) {
		String sql = "select min(currency_amount) from bq_hourly_account "
				+ "where hour_time >= ? and hour_time < ? and user_id = ? and currency_id = ?";
		Session session = bqHourlyAccountDao.getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, startTime);
		query.setLong(1, endTime);
		query.setLong(2, uId);
		query.setInteger(3, currencyId);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> findUserIdOverZero(long startTime, long endTime, int currencyId) {
		Session session = bqHourlyAccountDao.getSession();
		String sql = "SELECT user_id from bq_hourly_account where currency_id = ? "
				+ " and hour_time >= ? and hour_time < ? and currency_amount > 0 "
				+ " and user_id <> 1260 and user_id <> 8670 and user_id <> 8774 group by user_id";
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, currencyId);
		query.setLong(1, startTime);
		query.setLong(2, endTime);
		return query.list();
	}

	@Override
	public void createHourlyAccountBak() {
		bqHourlyAccountDao.createHourlyAccountBak();
	}

	@Override
	public void insertHourlyAccountBak(long endTime) {
		bqHourlyAccountDao.insertHourlyAccountBak(endTime);
		bqHourlyAccountDao.deleteHourlyAccount(endTime);
	}

}
