package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.TextUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.NMemberTransactionOverloadLogDao;
import com.btc100.btc_q.dao.NTradeLogDao;
import com.btc100.btc_q.entity.NMemberTransactionOverloadLog;
import com.btc100.btc_q.entity.NTradeLog;
import com.btc100.btc_q.service.NTradeLogService;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("nTradeLogService")
public class NTradeLogServiceImpl implements NTradeLogService {

	@Autowired
	private NTradeLogDao<NTradeLog> tradeLogDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NMemberTransactionOverloadLogDao<NMemberTransactionOverloadLog> overloadLogDao;
	
	public BigDecimal findHighPrice(String partition, String code, long yesterdayTime) {
		return tradeLogDao.findHighPrice(partition, code, yesterdayTime);
	}

	public BigDecimal findLowPrice(String partition, String code, long yesterdayTime) {
		return tradeLogDao.findLowPrice(partition, code, yesterdayTime);
	}
	
	public BigDecimal findFirstPrice(String partition, String code, long time) {
		return tradeLogDao.findFirstPrice(partition, code, time);
	}
	
	public BigDecimal readFirstPrice(String partition, String code, long time) {
		return tradeLogDao.findFirstPrice(partition, code, time);
	}

	@Override
	public BigDecimal findPriceAtTime(String partition, String code, long time) {
		return tradeLogDao.findPriceAtTime(partition, code, time);
	}
	
	@Override
	public BigDecimal findPriceLastClose(String partition, String code, long time) {
		return tradeLogDao.findPriceLastClose(partition, code, time);
	}

	public BigDecimal findBTCVol(String partition, String code, long yesterdayTime) {
		if (code == null) {
			return BigDecimal.ZERO;
		}
		BigDecimal vols = spyMemcachedClient.get(partition + "_" + code + BTCQMemecacheKeys.TICKER_TRADEVOLBTC);
		if (vols == null) {
			vols = tradeLogDao.findVol(partition, code, yesterdayTime);
			spyMemcachedClient.set(partition + "_" + code + BTCQMemecacheKeys.TICKER_TRADEVOLBTC, BTCQMemecacheKeys.TIMES.TICKER_VOL, vols);
		}

		return vols;
	}

	public BigDecimal findTradeBTC(String partition, String code, long yesterdayTime) {
		if (code == null) {
			return BigDecimal.ZERO;
		}
		BigDecimal vols = spyMemcachedClient.get(partition + "_" + code + BTCQMemecacheKeys.TICKER_TRADEBTC);
		if (vols == null) {
			vols = tradeLogDao.findTradeBTC(partition, code, yesterdayTime);
			spyMemcachedClient.set(partition + "_" + code + BTCQMemecacheKeys.TICKER_TRADEBTC, BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, vols);
		}

		return vols;
	}

	public List<NTradeLog> readAllByUserIdTopN(String partition, String bqCode, long userId, int n) {
		return tradeLogDao.getAllByUserIdTopN(partition, bqCode, userId, n);
	}

	public PagerModel findByUserIdAndTransaction(String partition, String bqCode, long fUserId, int czlx, long startTime, long endTime) {
		return tradeLogDao.getPageByUserId(partition, bqCode, fUserId, czlx, startTime, endTime);
	}
	
	public PagerModel readByUserIdAndTransaction(String partition, String bqCode, long fUserId, int czlx, long startTime, long endTime) {
		PagerModel pm = tradeLogDao.getPageByUserId(partition, bqCode, fUserId, czlx, startTime, endTime);
		
		@SuppressWarnings("unchecked")
		List<NTradeLog> logs = (List<NTradeLog>) pm.getDatas();
		for (NTradeLog log : logs) {
			if (log.getTrade_type_id() > 4) {
				NMemberTransactionOverloadLog overLog = overloadLogDao.getByPendingNo(partition, log.getTransaction_code(), log.getF_user_id(), log.getPending_no());
				if (overLog != null && overLog.getTrade_force_price().compareTo(BigDecimal.ZERO) > 0) {
					log.setForce_price(overLog.getTrade_force_price());
				}
			}
		}
		
		return pm;
	}

	public void saveTradeLog(String partition, NTradeLog tradeLog) {
		tradeLogDao.insertLog(partition, tradeLog);
	}
	
	@Override
	public void deleteVTradeScalpingLog(String partition, String vCode, long scalpingUid, long beforeTime) {
		Session session = tradeLogDao.getSession();
		String sql = "delete from " + partition + "_" + vCode + "_trade_log where f_user_id = :uid and trade_time < :time";
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", scalpingUid);
		query.setParameter("time", beforeTime);
		query.executeUpdate();
	}

//	public BigDecimal getTradeAmount(String partition, String tranCode, String str, long yesterdayTime) {
//		Session session = tradeLogDao.getSession();
//		StringBuilder sbBuilder = new StringBuilder();
//		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(trade_count), 0) from " + partition + "_" + tranCode + "_trade_log ");
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
	
	private BigDecimal getLastTimesAgoByCode(String partition, String bqCode, long seconds, boolean isHour) {
		Session session = tradeLogDao.getSession();
		String sql = "SELECT trade_average_price from " + partition + "_" + bqCode + "_trade_log where trade_time < " + seconds
				+ " order by trade_time desc limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		Object result = query.uniqueResult();

		BigDecimal price = null;
		if (result == null) {
			String sql2 = "SELECT * from " + partition + "_" + bqCode + "_trade_log limit 1";
			SQLQuery query2 = session.createSQLQuery(sql2);
			query2.addEntity(NTradeLog.class);
			NTradeLog result2 = (NTradeLog) query2.uniqueResult();

			if (result2 != null) {
				price = result2.getTrade_average_price();
				if (isHour) {	// 一小时，更新开盘价
					BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + bqCode);
					if (openPrice == null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + bqCode, BTCQMemecacheKeys.TIMES.ONE_HOUR, price);
					}
				} else {	// 一天获取第一个小时的收盘价为天的开盘价
					long time = result2.getTrade_time() / 3600 * 3600;
					BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + bqCode);
					if (openPrice != null) {
						long nowTime = tradeLogDao.getUnix_TIMESTAMP() / 3600 * 3600;
						if (time != nowTime) {
							spyMemcachedClient.delete(BTCQMemecacheKeys.OPEN_PRICE + partition + "_" + bqCode);
						}
					}
					time += 3600;
					String sql3 = "SELECT trade_average_price from " + partition + "_" + bqCode + "_trade_log where trade_time < " + time
							+ " order by trade_time desc limit 1";
					SQLQuery query3 = session.createSQLQuery(sql3);
					Object result3 = query3.uniqueResult();
					if (result3 != null) {
						price = new BigDecimal(result3.toString());
					}
				}
			}
		} else {
			price = new BigDecimal(result.toString());
		}
		return price;
	}

	public BigDecimal getLastDayAgoByCode(String partition, String bqCode) {
		long nowTime = tradeLogDao.getUnix_TIMESTAMP();
		long morning = TimeUtil.getTimesmorning(nowTime * 1000);
		return getLastTimesAgoByCode(partition, bqCode, morning, false);
	}
	
	public BigDecimal readLastDayAgoByCode(String partition, String bqCode) {
		long nowTime = tradeLogDao.getUnix_TIMESTAMP();
		long morning = TimeUtil.getTimesmorning(nowTime * 1000);
		return getLastTimesAgoByCode(partition, bqCode, morning, false);
	}
	
	public BigDecimal readLastHourAgoByCode(String partition, String bqCode) {
		long nowTime = tradeLogDao.getUnix_TIMESTAMP() / 3600;
		return getLastTimesAgoByCode(partition, bqCode, nowTime * 3600, true);
	}
	
	@Override
	public BigDecimal getLastHourAgoByCode(String partition, String bqCode) {
		long nowTime = tradeLogDao.getUnix_TIMESTAMP() / 3600;
		return getLastTimesAgoByCode(partition, bqCode, nowTime * 3600, true);
	}

	public BigDecimal findFeeByTimeCode(String partition, String bqCode, long userID, long starttime, long endtime) {
		Session session = tradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(trade_fee), 0) from " + partition + "_" + bqCode + "_trade_log where 1 = 1");
		if (userID > 0) {
			sql.append(" AND f_user_id = :userID ");
		}
		sql.append(" AND trade_time >= :startTime AND trade_time < :endTime ");
		
		SQLQuery query = session.createSQLQuery(sql.toString());
		if (userID > 0) {
			query.setParameter("userID", userID);
		}
		
		query.setParameter("startTime", starttime);
		query.setParameter("endTime", endtime);
		
		return (BigDecimal) query.uniqueResult();
	}

	public BigDecimal findAvagPrice(String partition, String bqCode, long time, int num) {

		if (TextUtils.isEmpty(bqCode)) {
			return BigDecimal.ZERO;
		}

		BigDecimal prices = spyMemcachedClient.get(partition + "_" + bqCode + BTCQMemecacheKeys.TICKER_YESTERDAYAVGPRICE);

		if (prices == null) {
			Session session = tradeLogDao.getSession();
			StringBuilder sql = new StringBuilder("select ifnull(SUM(trade_count * trade_average_price), 0) / ifnull(SUM(trade_count), 1) as avg from "
					+ partition + "_" + bqCode + "_trade_log ");
			if (num > 0) {
				sql.append("where trade_time BETWEEN " + time + " AND " + (time + num));
			} else {
				sql.append("where trade_time BETWEEN " + (time + num) + " AND " + time);
			}
			SQLQuery query = session.createSQLQuery(sql.toString());
			prices = (BigDecimal) query.uniqueResult();
			spyMemcachedClient.set(partition + "_" + bqCode + BTCQMemecacheKeys.TICKER_YESTERDAYAVGPRICE, BTCQMemecacheKeys.TIMES.TICKER_YESTERDAYAVGPRICE, prices);
		}
		return prices;
	}

	@Override
	public BigDecimal getInTimeHighPrice(String partition, String code, long startTime, long endTime) {
		return tradeLogDao.getInTimeHighPrice(partition, code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeLowPrice(String partition, String code, long startTime, long endTime) {
		return tradeLogDao.getInTimeLowPrice(partition, code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeLastPrice(String partition, String code, long startTime, long endTime) {
		return tradeLogDao.getInTimeLastPrice(partition, code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeFirstPrice(String partition, String code, long startTime, long endTime) {
		return tradeLogDao.getInTimeFirstPrice(partition, code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeVol(String partition, String code, long startTime, long endTime) {
		return tradeLogDao.getInTimeVol(partition, code, startTime, endTime);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NTradeLog> findUserTurnoverBetweenTime(String partition, String bqCode, long beginTime, long endTime, long depthUid, long quantUid) {
		Session session = tradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("select f_user_id as userId, sum(trade_average_price * trade_count) as tradeCount from " + partition + "_" + bqCode 
				+ "_trade_log where (trade_type_id = ? or trade_type_id = ?) and f_user_id > 1 and f_user_id != ? and f_user_id != ? and trade_time >= ? and trade_time < ? group by f_user_id");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setInteger(0, Contents.CZLX_DDKC);
		query.setInteger(1, Contents.CZLX_KTKC);
		query.setLong(2, depthUid);
		query.setLong(3, quantUid);
		query.setLong(4, beginTime);
		query.setLong(5, endTime);
		
		List<NTradeLog> retList = new ArrayList<NTradeLog>();
		List<Object[]> list = query.list();
		for (Object[] obj : list) {
			int id = (int) obj[0];
			BigDecimal sum = (BigDecimal) obj[1];
			retList.add(new NTradeLog(id, sum));
		}
		
		return retList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NTradeLog> findByUserAndTime(String partition, String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit) {
		Session session = tradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + bqCode + "_trade_log where f_user_id = :userId");
		
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
		
		query.addEntity(com.btc100.btc_q.entity.NTradeLog.class);
		return query.list();
	}

	@Override
	public BigDecimal getInTimeNoScalpingVol(String partition, String code, long startTime, long endTime, long scalUserId) {
		return tradeLogDao.getInTimeNoScalpingVol(partition, code, startTime, endTime, scalUserId);
	}

	@Override
	public BigDecimal getInTimeNoScalpingFee(String partition, String code, long startTime, long endTime, long scalUserId) {
		return tradeLogDao.getInTimeNoScalpingFee(partition, code, startTime, endTime, scalUserId);
	}

	@Override
	public long getTradeUserCount(String partition, String code, long startTime, long endTime, long scalUserId) {
		return tradeLogDao.getTradeUserCount(partition, code, startTime, endTime, scalUserId);
	}

	@Override
	public List<Integer> getTradeUserIds(String partition, String code, long startTime, long endTime, long scalUserId) {
		return tradeLogDao.getTradeUserIds(partition, code, startTime, endTime, scalUserId);
	}

	@Override
	public BigDecimal getVolByTimeAndTradeType(String partition, String code, long startTime, long endTime, long scalUserId, long userId,
			int tradeType) {
		return tradeLogDao.getVolByTimeAndTradeType(partition, code, startTime, endTime, scalUserId, userId, tradeType);
	}

	@Override
	public BigDecimal getFeeByTimeAndUser(String partition, String code, long startTime, long endTime, long scalUserId, long userId) {
		return tradeLogDao.getFeeByTimeAndUser(partition, code, startTime, endTime, scalUserId, userId);
	}

	@Override
	public BigDecimal getVol(String partition, String code, long startTime, long endTime, long userId, int tradeType) {
		return tradeLogDao.getVol(partition, code, startTime, endTime, userId, tradeType);
	}

	@Override
	public BigDecimal getTradeAmount(String partition, String code, long startTime, long endTime, long userId, int tradeType) {
		return tradeLogDao.getTradeAmount(partition, code, startTime, endTime, userId, tradeType);
	}

	@Override
	public BigDecimal getIncome(String partition, String code, long startTime, long endTime, long userId, int tradeType) {
		return tradeLogDao.getIncome(partition, code, startTime, endTime, userId, tradeType);
	}
	
	@Override
	public List<Object[]> findIncome(String partition, String code, long startTime, long endTime, long except) {
		return tradeLogDao.findIncome(partition, code, startTime, endTime, except);
	}

	@Override
	public Long getLastTradeId(String partition, String code, long userId) {
		Session session = tradeLogDao.getSession();
		String sql = "SELECT max(trade_id) FROM " + partition + "_" + code + "_trade_log where f_user_id  = :userId";
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong("userId", userId);
		Integer id = (Integer) query.uniqueResult();
		return id == null ? 0 : id.longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NTradeLog> findGtFrom(String partition, String bqCode, long userId, long from, int limit) {
		Session session = tradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + bqCode + "_trade_log where f_user_id = :userId");
		
		if (from >= 0) {
			sql.append(" and trade_id > :from");
		}
		
		sql.append(" ORDER BY trade_time DESC");
		
		if (limit > 0) {
			sql.append(" limit " + limit);
		}
		
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong("userId", userId);
		
		if (from >= 0) {
			query.setLong("from", from);
		}
		
		query.addEntity(com.btc100.btc_q.entity.NTradeLog.class);
		return query.list();
	}

	@Override
	public Long getTime() {
		return tradeLogDao.getUnix_TIMESTAMP();
	}

	@Override
	public void createTradeLogBak(String partition, String code) {
		tradeLogDao.createNTradeLogBak(partition, code);
	}

	@Override
	public void insertTradeLogBak(String partition, String code, long endTime) {
		tradeLogDao.insertNTradeLogBak(partition, code, endTime);
		tradeLogDao.deleteNTradeLog(partition, code, endTime);
	}

	@Override
	public List<NTradeLog> findUidByCodeAndTimeGroupUid(String partition, String bqCode, long startTime, long endTime) {
		Session session = tradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("select f_user_id, IFNULL(sum(trade_fee),0) as fee from " + partition + "_" + bqCode + "_trade_log" +
				" where f_user_id > 1 and trade_time >= ? and trade_time < ? group by f_user_id");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong(0, startTime);
		query.setLong(1, endTime);
		List<Object[]> list = query.list();
		List<NTradeLog> nTradeLogs = new ArrayList<>();
		for (Object[] obj : list) {
			int id = (int) obj[0];
			BigDecimal sum = (BigDecimal) obj[1];
			nTradeLogs.add(new NTradeLog(id, sum));
		}
		return nTradeLogs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NTradeLog> readByUserAndTime(String partition, String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit) {
		Session session = tradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + bqCode + "_trade_log where f_user_id = :userId");
		
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
		
		query.addEntity(com.btc100.btc_q.entity.NTradeLog.class);
		List<NTradeLog> logs = (List<NTradeLog>) query.list();
		for (NTradeLog log : logs) {
			if (log.getTrade_type_id() > 4) {
				NMemberTransactionOverloadLog overLog = overloadLogDao.getByPendingNo(partition, log.getTransaction_code(), log.getF_user_id(), log.getPending_no());
				if (overLog != null && overLog.getTrade_force_price().compareTo(BigDecimal.ZERO) > 0) {
					log.setForce_price(overLog.getTrade_force_price());
				}
			}
		}
		
		return logs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NTradeLog> readFromBakByUserAndTime(String partition, String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit) {
		Session session = tradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + bqCode + "_trade_log_bak where f_user_id = :userId");
		
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
		
		query.addEntity(com.btc100.btc_q.entity.NTradeLog.class);
		List<NTradeLog> logs = (List<NTradeLog>) query.list();
		for (NTradeLog log : logs) {
			if (log.getTrade_type_id() > 4) {
				NMemberTransactionOverloadLog overLog = overloadLogDao.getByPendingNo(partition, log.getTransaction_code(), log.getF_user_id(), log.getPending_no());
				if (overLog != null && overLog.getTrade_force_price().compareTo(BigDecimal.ZERO) > 0) {
					log.setForce_price(overLog.getTrade_force_price());
				}
			}
		}
		
		return logs;
	}

}
