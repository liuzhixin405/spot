package com.btc100.btc_q.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.KLineTradeDao;
import com.btc100.btc_q.entity.KLineTrade;

@Repository("kLineTradeDao")
public class KLineTradeDaoImpl extends BaseDaoImpl<KLineTrade> implements KLineTradeDao {
	public KLineTrade insert(final KLineTrade trade, final String timeType) {
		getSession().doWork(new Work() {
			public void execute(Connection conn) throws SQLException {
				PreparedStatement psmt = conn.prepareStatement("insert into " + trade.getCode()
						+ timeType + "_trade (id,code,high,low,first,last,vol,time)" + "values (?,?,?,?,?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS);
				psmt.setLong(1, trade.getId());
				psmt.setString(2, trade.getCode());
				psmt.setBigDecimal(3, trade.getHigh());
				psmt.setBigDecimal(4, trade.getLow());
				psmt.setBigDecimal(5, trade.getFirst());
				psmt.setBigDecimal(6, trade.getLast());
				psmt.setBigDecimal(7, trade.getVol());
				psmt.setLong(8, trade.getTime());
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				long num = -1;
				if (results.next()) {
					num = results.getLong(1);
				}

				trade.setId(num);
			}
		});
		return trade;
	}

	@Override
	public KLineTrade getAtTime(String code, String timeType, long time) {
		String sql = "SELECT * FROM " + code + timeType + "_trade WHERE time = :time";
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql.toString()).addEntity(KLineTrade.class);
		query.setParameter("time", time);
		
		@SuppressWarnings("unchecked")
		List<KLineTrade> list = query.list();
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}

	@Override
	public void update(KLineTrade trade, String timeType) {
		String sql = "UPDATE " + trade.getCode() + timeType + "_trade set high = :high, low = :low, first= :first, last= :last, vol= :vol where id = :id";
		
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("high", trade.getHigh());
		query.setParameter("low", trade.getLow());
		query.setParameter("first", trade.getFirst());
		query.setParameter("last", trade.getLast());
		query.setParameter("vol", trade.getVol());
		query.setParameter("id", trade.getId());
		
		query.executeUpdate();
	}

}
