package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.http.util.TextUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.VMemberTransactionOverloadLogDao;
import com.btc100.btc_q.dao.VTradeLogDao;
import com.btc100.btc_q.entity.VMemberTransactionOverloadLog;
import com.btc100.btc_q.entity.VTradeLog;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.vo.PagerModel;

@Service("vTradeLogService")
public class VTradeLogServiceImpl implements VTradeLogService {

	@Autowired
	private VTradeLogDao<VTradeLog> vTradeLogDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	VMemberTransactionOverloadLogDao<VMemberTransactionOverloadLog> overloadLogDao;

	public BigDecimal findHighPrice(String code, long yesterdayTime) {
		return vTradeLogDao.findHighPrice(code, yesterdayTime);
	}

	public BigDecimal findLowPrice(String code, long yesterdayTime) {
		return vTradeLogDao.findLowPrice(code, yesterdayTime);
	}
	
	public BigDecimal findFirstPrice(String code, long time) {
		return vTradeLogDao.findFirstPrice(code, time);
	}
	
	public BigDecimal readFirstPrice(String code, long time) {
		return vTradeLogDao.findFirstPrice(code, time);
	}
	
	@Override
	public BigDecimal findPriceAtTime(String code, long time) {
		return vTradeLogDao.findPriceAtTime(code, time);
	}

	public BigDecimal findBTCVol(String code, long yesterdayTime) {
		if (code == null) {
			return BigDecimal.ZERO;
		}
		BigDecimal vols = spyMemcachedClient.get(code + BTCQMemecacheKeys.V_TICKER_TRADEVOLBTC);
		if (vols == null) {
			vols = vTradeLogDao.findVol(code, yesterdayTime);
			spyMemcachedClient.set(code + BTCQMemecacheKeys.V_TICKER_TRADEVOLBTC, BTCQMemecacheKeys.TIMES.TICKER_VOL, vols);
		}

		return vols;
	}

	public BigDecimal findTradeBTC(String code, long yesterdayTime) {
		if (code == null) {
			return BigDecimal.ZERO;
		}
		BigDecimal vols = spyMemcachedClient.get(code + BTCQMemecacheKeys.V_TICKER_TRADEBTC);
		if (vols == null) {
			vols = vTradeLogDao.findTradeBTC(code, yesterdayTime);
			spyMemcachedClient.set(code + BTCQMemecacheKeys.V_TICKER_TRADEBTC, BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, vols);
		}

		return vols;
	}

	public PagerModel findByUserIdAndTransaction(String bqCode, long fUserId, int czlx) {
		return vTradeLogDao.getPageByUserId(bqCode, fUserId, czlx);
	}

	public void saveVTradeLog(VTradeLog tradeLog) {
		// vTradeLogDao.save(tradeLog);
		vTradeLogDao.insertLog(tradeLog);
	}
	
	@Override
	public void deleteVTradeScalpingLog(String vCode, long scalpingUid, long beforeTime) {
		Session session = vTradeLogDao.getSession();
		String sql = "delete from " + vCode + "_v_trade_log where f_user_id = :uid and trade_time < :time";
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", scalpingUid);
		query.setParameter("time", beforeTime);
		query.executeUpdate();
	}

	public List<VTradeLog> findByCode(String code) {
		return vTradeLogDao.findByCode(code);
	}

//	public BigDecimal getTradeAmount(String tranCode, String str, long yesterdayTime) {
//		Session session = vTradeLogDao.getSession();
//		StringBuilder sbBuilder = new StringBuilder();
//		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(trade_count), 0) from " + tranCode + "_v_trade_log ");
//		if (str.equals("+")) {
//			sbBuilder.append("where trade_type_id in(1,3)");
//		}
//		if (str.equals("-")) {
//			sbBuilder.append("where trade_type_id in(2,4)");
//		}
//		if (str.equals("<>")) {
//			sbBuilder.append("where trade_type_id in(5,6)");
//		}
//		sbBuilder.append(" and trade_time>" + yesterdayTime);
//		SQLQuery query = session.createSQLQuery(sql.toString() + sbBuilder.toString());
//
//		return (BigDecimal) query.uniqueResult();
//	}

	// SELECT ifnull(SUM(trade_count * trade_average_price),
	// 0)/ifnull(SUM(trade_count), 0) as w FROM btc1503_trade_log WHERE
	// trade_time BETWEEN 1417418349 and 1417418774;
	public BigDecimal findAvagPrice(String vCode, long time, int num) {

		if (TextUtils.isEmpty(vCode)) {
			return BigDecimal.ZERO;
		}

		BigDecimal prices = spyMemcachedClient.get(vCode + BTCQMemecacheKeys.V_TICKER_YESTERDAYAVGPRICE);

		if (prices == null) {
			Session session = vTradeLogDao.getSession();
			StringBuilder sql = new StringBuilder("select ifnull(SUM(trade_count * trade_average_price), 0) / ifnull(SUM(trade_count), 1) as avg from "
					+ vCode + "_v_trade_log ");
			if (num > 0) {
				sql.append("where trade_time BETWEEN " + time + " AND " + (time + num));
			} else {
				sql.append("where trade_time BETWEEN " + (time + num) + " AND " + time);
			}
			SQLQuery query = session.createSQLQuery(sql.toString());
			prices = (BigDecimal) query.uniqueResult();
			spyMemcachedClient.set(vCode + BTCQMemecacheKeys.V_TICKER_YESTERDAYAVGPRICE, BTCQMemecacheKeys.TIMES.TICKER_YESTERDAYAVGPRICE, prices);
		}
		return prices;
	}

	@Override
	public BigDecimal getInTimeHighPrice(String code, long startTime, long endTime) {
		return vTradeLogDao.getInTimeHighPrice(code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeLowPrice(String code, long startTime, long endTime) {
		return vTradeLogDao.getInTimeLowPrice(code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeLastPrice(String code, long startTime, long endTime) {
		return vTradeLogDao.getInTimeLastPrice(code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeFirstPrice(String code, long startTime, long endTime) {
		return vTradeLogDao.getInTimeFirstPrice(code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeVol(String code, long startTime, long endTime) {
		return vTradeLogDao.getInTimeVol(code, startTime, endTime);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VTradeLog> readByUserAndTime(String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit) {
		Session session = vTradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + bqCode + "_v_trade_log where f_user_id = :userId");
		
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
		
		query.addEntity(com.btc100.btc_q.entity.VTradeLog.class);
		List<VTradeLog> logs = (List<VTradeLog>) query.list();
		for (VTradeLog log : logs) {
			if (log.getTrade_type_id() > 4) {
				VMemberTransactionOverloadLog overLog = overloadLogDao.getByPendingNo(log.getTransaction_code(), log.getF_user_id(), log.getPending_no());
				if (overLog != null && overLog.getTrade_force_price().compareTo(BigDecimal.ZERO) > 0) {
					log.setForce_price(overLog.getTrade_force_price());
				}
			}
		}
		
		return logs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<VTradeLog> readFromBakByUserAndTime(String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit) {
		Session session = vTradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + bqCode + "_v_trade_log_bak where f_user_id = :userId");
		
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
		
		query.addEntity(com.btc100.btc_q.entity.VTradeLog.class);
		List<VTradeLog> logs = (List<VTradeLog>) query.list();
		for (VTradeLog log : logs) {
			if (log.getTrade_type_id() > 4) {
				VMemberTransactionOverloadLog overLog = overloadLogDao.getByPendingNo(log.getTransaction_code(), log.getF_user_id(), log.getPending_no());
				if (overLog != null && overLog.getTrade_force_price().compareTo(BigDecimal.ZERO) > 0) {
					log.setForce_price(overLog.getTrade_force_price());
				}
			}
		}
		
		return logs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VTradeLog> readByCodeAndUserAndTime(String vCode, long userId, long startTime, long endTime, int limit) {
		Session session = vTradeLogDao.getSession();
		String sql = "select * from " + vCode + "_v_trade_log where f_user_id = :userId";
		
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
		
		query.addEntity(com.btc100.btc_q.entity.VTradeLog.class);
		List<VTradeLog> logs = (List<VTradeLog>) query.list();
		for (VTradeLog log : logs) {
			if (log.getTrade_type_id() > 4) {
				VMemberTransactionOverloadLog overLog = overloadLogDao.getByPendingNo(log.getTransaction_code(), log.getF_user_id(), log.getPending_no());
				if (overLog != null && overLog.getTrade_force_price().compareTo(BigDecimal.ZERO) > 0) {
					log.setForce_price(overLog.getTrade_force_price());
				}
			}
		}
		
		return logs;
	}
	
	@Override
	public BigDecimal getInTimeNoScalpingVol(String code, long startTime, long endTime, long scalUserId) {
		return vTradeLogDao.getInTimeNoScalpingVol(code, startTime, endTime, scalUserId);
	}

	@Override
	public BigDecimal getInTimeNoScalpingFee(String code, long startTime, long endTime, long scalUserId) {
		return vTradeLogDao.getInTimeNoScalpingFee(code, startTime, endTime, scalUserId);
	}

	@Override
	public long getTradeUserCount(String code, long startTime, long endTime, long scalUserId) {
		return vTradeLogDao.getTradeUserCount(code, startTime, endTime, scalUserId);
	}

	@Override
	public List<Integer> getTradeUserIds(String code, long startTime, long endTime, long scalUserId) {
		return vTradeLogDao.getTradeUserIds(code, startTime, endTime, scalUserId);
	}

	@Override
	public List<Object[]> getIncomeAndDepoistByUser(String bqCode, long except, long beginTime, long endTime) {
		return vTradeLogDao.getIncomeAndDepoistByUser(bqCode, except, beginTime, endTime);
	}
	
	@Override
	public List<Object[]> findIncome(String code, long startTime, long endTime, long except) {
		return vTradeLogDao.findIncome(code, startTime, endTime, except);
	}

	@Override
	public void createTradeLogBak(String code) {
		vTradeLogDao.createTradeLogBak(code);
	}

	@Override
	public void insertTradeLogBak(String code, long endTime) {
		vTradeLogDao.insertTradeLogBak(code, endTime);
		vTradeLogDao.deleteTradeLog(code, endTime);
	}

	public PagerModel readByUserIdAndTransaction(String bqCode, long fUserId, int czlx, long startTime, long endTime) {
		PagerModel pm = vTradeLogDao.getPageByUserId(bqCode, fUserId, czlx, startTime, endTime);
		
		@SuppressWarnings("unchecked")
		List<VTradeLog> logs = (List<VTradeLog>) pm.getDatas();
		for (VTradeLog log : logs) {
			if (log.getTrade_type_id() > 4) {
				VMemberTransactionOverloadLog overLog = overloadLogDao.getByPendingNo(log.getTransaction_code(), log.getF_user_id(), log.getPending_no());
				if (overLog != null && overLog.getTrade_force_price().compareTo(BigDecimal.ZERO) > 0) {
					log.setForce_price(overLog.getTrade_force_price());
				}
			}
		}
		
		return pm;
	}

}
