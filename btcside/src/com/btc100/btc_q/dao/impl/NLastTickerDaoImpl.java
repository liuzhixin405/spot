package com.btc100.btc_q.dao.impl;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.NLastTickerDao;
import com.btc100.btc_q.entity.NLastTicker;

@Repository("nLastTickerDao")
public class NLastTickerDaoImpl extends BaseDaoImpl<NLastTicker> implements NLastTickerDao<NLastTicker> {
	
	@Override
	public int insert(String partition, NLastTicker ticker) {

		Session session = getSession();
		String sql = "insert into " + partition + "_last_ticker "
				+ "(f_ticker_code,f_ticker_high,f_ticker_low,f_ticker_buy,f_ticker_sell,f_ticker_last,f_ticker_vol,f_ticker_trades) "
				+ "values (?,?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, ticker.getF_ticker_code());
		query.setBigDecimal(1, ticker.getF_ticker_high());
		query.setBigDecimal(2, ticker.getF_ticker_low());
		query.setBigDecimal(3, ticker.getF_ticker_buy());
		query.setBigDecimal(4, ticker.getF_ticker_sell());
		query.setBigDecimal(5, ticker.getF_ticker_last());
		query.setBigDecimal(6, ticker.getF_ticker_vol());
		query.setBigDecimal(7, ticker.getF_ticker_trades());
		
		return query.executeUpdate();
	}
	
	@Override
	public int update(String partition, NLastTicker ticker) {

		Session session = getSession();
		String sql = "update " + partition + "_last_ticker set "
				+ "f_ticker_code=?,f_ticker_high=?,f_ticker_low=?,f_ticker_buy=?,f_ticker_sell=?,f_ticker_last=?,f_ticker_vol=?,f_ticker_trades=?"
				+ " where f_ticker_id=?";

		SQLQuery query = session.createSQLQuery(sql);

		query.setString(0, ticker.getF_ticker_code());
		query.setBigDecimal(1, ticker.getF_ticker_high());
		query.setBigDecimal(2, ticker.getF_ticker_low());
		query.setBigDecimal(3, ticker.getF_ticker_buy());
		query.setBigDecimal(4, ticker.getF_ticker_sell());
		query.setBigDecimal(5, ticker.getF_ticker_last());
		query.setBigDecimal(6, ticker.getF_ticker_vol());
		query.setBigDecimal(7, ticker.getF_ticker_trades());
		query.setInteger(8, ticker.getF_ticker_id());
		
		return query.executeUpdate();
	}
}
