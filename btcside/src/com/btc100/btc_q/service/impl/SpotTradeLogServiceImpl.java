package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import org.apache.http.util.TextUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.SpotTradeLogDao;
import com.btc100.btc_q.entity.SpotTradeLog;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.vo.PagerModel;

@Service("spotSpotTradeLogService")
public class SpotTradeLogServiceImpl implements SpotTradeLogService {

	@Autowired
	private SpotTradeLogDao<SpotTradeLog> spotTradeLogDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	public BigDecimal findHighPrice(String code, long yesterdayTime) {
		return spotTradeLogDao.findHighPrice(code, yesterdayTime);
	}

	public BigDecimal findLowPrice(String code, long yesterdayTime) {
		return spotTradeLogDao.findLowPrice(code, yesterdayTime);
	}
	
	public BigDecimal findFirstPrice(String code, long time) {
		return spotTradeLogDao.findFirstPrice(code, time);
	}

	public BigDecimal findBTCVol(String code, long yesterdayTime) {
		if (code == null) {
			return BigDecimal.ZERO;
		}
		BigDecimal vols = spyMemcachedClient.get(code + BTCQMemecacheKeys.TICKER_TRADEVOLBTC);
		if (vols == null) {
			vols = spotTradeLogDao.findVol(code, yesterdayTime);
			spyMemcachedClient.set(code + BTCQMemecacheKeys.TICKER_TRADEVOLBTC, BTCQMemecacheKeys.TIMES.TICKER_VOL, vols);
		}

		return vols;
	}

	public BigDecimal findTradeBTC(String code, long yesterdayTime) {
		if (code == null) {
			return BigDecimal.ZERO;
		}
		BigDecimal vols = spyMemcachedClient.get(code + BTCQMemecacheKeys.TICKER_TRADEBTC);
		if (vols == null) {
			vols = spotTradeLogDao.findTradeBTC(code, yesterdayTime);
			spyMemcachedClient.set(code + BTCQMemecacheKeys.TICKER_TRADEBTC, BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, vols);
		}

		return vols;
	}

	public PagerModel findByUserIdAndTransaction(String bqCode, long fUserId, int direction, long startTime, long endTime) {
		return spotTradeLogDao.getPageByUserId(bqCode, fUserId, direction, startTime, endTime);
	}
	
	public PagerModel findByUserIdAndTransaction(String bqCode, long fUserId, int direction) {
		return spotTradeLogDao.getPageByUserId(bqCode, fUserId, direction);
	}

	public void saveSpotTradeLog(SpotTradeLog tradeLog) {
		spotTradeLogDao.insertLog(tradeLog);
	}

	public BigDecimal findFeeByTimeCode(String bqCode, long userID, int direction, long starttime, long endtime) throws ParseException {
		Session session = spotTradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(trade_fee), 0) from " + bqCode + "_trade_log where 1=1");
		sql.append(" AND trade_trans_dirction = :direction");
		if (userID > 0) {
			sql.append(" AND f_user_id = :userID ");
		}
		sql.append(" AND trade_time >= :startTime AND trade_time < :endTime ");
		
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("direction", direction);
		if (userID > 0) {
			query.setParameter("userID", userID);
		}
		query.setParameter("startTime", starttime);
		query.setParameter("endTime", endtime);
		
		return (BigDecimal) query.uniqueResult();
	}
	
	public BigDecimal readFeeByTimeCode(String bqCode, long userID, int direction, long starttime, long endtime) throws ParseException {
		Session session = spotTradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(trade_fee), 0) from " + bqCode + "_trade_log where 1=1");
		sql.append(" AND trade_trans_dirction = :direction");
		if (userID > 0) {
			sql.append(" AND f_user_id = :userID ");
		}
		sql.append(" AND trade_time >= :startTime AND trade_time < :endTime ");
		
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("direction", direction);
		if (userID > 0) {
			query.setParameter("userID", userID);
		}
		query.setParameter("startTime", starttime);
		query.setParameter("endTime", endtime);
		
		return (BigDecimal) query.uniqueResult();
	}

	public BigDecimal findAvagPrice(String bqCode, long time, int num) {

		if (TextUtils.isEmpty(bqCode)) {
			return BigDecimal.ZERO;
		}

		BigDecimal prices = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.TICKER_YESTERDAYAVGPRICE);

		if (prices == null) {
			Session session = spotTradeLogDao.getSession();
			StringBuilder sql = new StringBuilder("select ifnull(SUM(trade_count * trade_average_price), 0) / ifnull(SUM(trade_count), 1) as avg from "
					+ bqCode + "_trade_log ");
			if (num > 0) {
				sql.append("where trade_time BETWEEN " + time + " AND " + (time + num));
			} else {
				sql.append("where trade_time BETWEEN " + (time + num) + " AND " + time);
			}
			SQLQuery query = session.createSQLQuery(sql.toString());
			prices = (BigDecimal) query.uniqueResult();
			spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.TICKER_YESTERDAYAVGPRICE, BTCQMemecacheKeys.TIMES.TICKER_YESTERDAYAVGPRICE, prices);
		}
		return prices;
	}

	@Override
	public BigDecimal getInTimeHighPrice(String code, long startTime, long endTime) {
		return spotTradeLogDao.getInTimeHighPrice(code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeLowPrice(String code, long startTime, long endTime) {
		return spotTradeLogDao.getInTimeLowPrice(code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeLastPrice(String code, long startTime, long endTime) {
		return spotTradeLogDao.getInTimeLastPrice(code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeFirstPrice(String code, long startTime, long endTime) {
		return spotTradeLogDao.getInTimeFirstPrice(code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeVol(String code, long startTime, long endTime) {
		return spotTradeLogDao.getInTimeVol(code, startTime, endTime);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotTradeLog> readByCodeAndUserAndTime(String bqCode, long userId, long startTime, long endTime, int limit) {
		Session session = spotTradeLogDao.getSession();
		String sql = "select * from " + bqCode + "_trade_log where f_user_id = :userId";
		
		if (startTime > 0) {
			sql += " and trade_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and trade_time < :endTime";
		}
		
		sql += " ORDER BY trade_time DESC LIMIT " + limit;
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("userId", userId);
		
		if (startTime > 0) {
			query.setLong("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		query.addEntity(com.btc100.btc_q.entity.SpotTradeLog.class);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotTradeLog> readByUserAndTime(String bqCode, long userId, long from, String direct, long startTime,
			long endTime, int limit) {
		Session session = spotTradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + bqCode + "_trade_log where f_user_id = :userId");
		
		if (startTime > 0) {
			sql.append(" and trade_time >= :startTime");
		}
		
		if (endTime > 0) {
			sql.append(" and trade_time <= :endTime");
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			sql.append(" and trade_id " + ">=" + " :from");
		} else if (from >= 0 && "next".equals(direct)) {
			sql.append(" and trade_id " + "<" + " :from");
		}
		
		sql.append(" ORDER BY trade_time DESC");
		
		if (limit > 0) {
			sql.append(" limit " + limit);	
		}
		
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong("userId", userId);
		
		if (startTime > 0) {
			query.setLong("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			query.setLong("from", from);
		} else if (from >= 0 && "next".equals(direct)) {
			query.setLong("from", from);
		}
		
		query.addEntity(com.btc100.btc_q.entity.SpotTradeLog.class);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SpotTradeLog> readFromBakByUserAndTime(String bqCode, long userId, long from, String direct, long startTime,
			long endTime, int limit) {
		Session session = spotTradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + bqCode + "_trade_log_bak where f_user_id = :userId");
		
		if (startTime > 0) {
			sql.append(" and trade_time >= :startTime");
		}
		
		if (endTime > 0) {
			sql.append(" and trade_time <= :endTime");
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			sql.append(" and trade_id " + ">=" + " :from");
		} else if (from >= 0 && "next".equals(direct)) {
			sql.append(" and trade_id " + "<" + " :from");
		}
		
		sql.append(" ORDER BY trade_time DESC");
		
		if (limit > 0) {
			sql.append(" limit " + limit);	
		}
		
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong("userId", userId);
		
		if (startTime > 0) {
			query.setLong("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setLong("endTime", endTime);
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			query.setLong("from", from);
		} else if (from >= 0 && "next".equals(direct)) {
			query.setLong("from", from);
		}
		
		query.addEntity(com.btc100.btc_q.entity.SpotTradeLog.class);
		return query.list();
	}

	@Override
	public void deleteTradeScalpingLog(String code, long scalpingUid, long beforeTime) {
		Session session = spotTradeLogDao.getSession();
		String sql = "delete from " + code + "_trade_log where f_user_id = :uid and trade_time < :time";
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", scalpingUid);
		query.setParameter("time", beforeTime);
		query.executeUpdate();
	}

	@Override
	public BigDecimal getFeeInTimeNoScalping(String code, int direction, long startTime, long endTime,
			long scalUserId) {
		return spotTradeLogDao.getFeeInTimeNoScalping(code, direction, startTime, endTime, scalUserId);
	}
	
	@Override
	public BigDecimal getBfxFeeNoScalping(String code, int direction, long startTime, long endTime,
			long scalUserId) {
		return spotTradeLogDao.getBfxFeeNoScalping(code, direction, startTime, endTime, scalUserId);
	}

	@Override
	public long getTradeUserCount(String code, long startTime, long endTime, long scalUserId) {
		return spotTradeLogDao.getTradeUserCount(code, startTime, endTime, scalUserId);
	}

	@Override
	public List<Integer> getTradeUserIds(String code, long startTime, long endTime, long scalUserId) {
		return spotTradeLogDao.getTradeUserIds(code, startTime, endTime, scalUserId);
	}

	@Override
	public void createTradeLogBak(String code) {
		spotTradeLogDao.createTradeLogBak(code);
	}

	@Override
	public void insertTradeLogBak(String code, long endTime) {
		spotTradeLogDao.insertTradeLogBak(code, endTime);
		spotTradeLogDao.deleteTradeLog(code, endTime);
	}

}
