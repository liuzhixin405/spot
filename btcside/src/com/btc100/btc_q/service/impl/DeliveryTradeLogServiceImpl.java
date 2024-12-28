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
import com.btc100.btc_q.dao.DeliveryMemberTransactionOverloadLogDao;
import com.btc100.btc_q.dao.DeliveryTradeLogDao;
import com.btc100.btc_q.entity.DeliveryMemberTransactionOverloadLog;
import com.btc100.btc_q.entity.DeliveryTradeLog;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("deliveryTradeLogService")
public class DeliveryTradeLogServiceImpl implements DeliveryTradeLogService {

	@Autowired
	private DeliveryTradeLogDao<DeliveryTradeLog> deliveryTradeLogDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryMemberTransactionOverloadLogDao<DeliveryMemberTransactionOverloadLog> overloadLogDao;
	
	public BigDecimal findHighPrice(String code, long yesterdayTime) {
		return deliveryTradeLogDao.findHighPrice(code, yesterdayTime);
	}

	public BigDecimal findLowPrice(String code, long yesterdayTime) {
		return deliveryTradeLogDao.findLowPrice(code, yesterdayTime);
	}
	
	public BigDecimal findFirstPrice(String code, long time) {
		return deliveryTradeLogDao.findFirstPrice(code, time);
	}
	
	public BigDecimal readFirstPrice(String code, long time) {
		return deliveryTradeLogDao.findFirstPrice(code, time);
	}

	@Override
	public BigDecimal findPriceAtTime(String code, long time) {
		return deliveryTradeLogDao.findPriceAtTime(code, time);
	}
	
	@Override
	public BigDecimal findPriceLastClose(String code, long time) {
		return deliveryTradeLogDao.findPriceLastClose(code, time);
	}

	public BigDecimal findBTCVol(String code, long yesterdayTime) {
		if (code == null) {
			return BigDecimal.ZERO;
		}
		BigDecimal vols = spyMemcachedClient.get(code + BTCQMemecacheKeys.TICKER_TRADEVOLBTC);
		if (vols == null) {
			vols = deliveryTradeLogDao.findVol(code, yesterdayTime);
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
			vols = deliveryTradeLogDao.findTradeBTC(code, yesterdayTime);
			spyMemcachedClient.set(code + BTCQMemecacheKeys.TICKER_TRADEBTC, BTCQMemecacheKeys.TIMES.TICKER_TRADEBTC, vols);
		}

		return vols;
	}

	public List<DeliveryTradeLog> readAllByUserIdTopN(String bqCode, long userId, int n) {
		return deliveryTradeLogDao.getAllByUserIdTopN(bqCode, userId, n);
	}

	public PagerModel findByUserIdAndTransaction(String bqCode, long fUserId, int czlx, long startTime, long endTime) {
		return deliveryTradeLogDao.getPageByUserId(bqCode, fUserId, czlx, startTime, endTime);
	}
	
	public PagerModel readByUserIdAndTransaction(String bqCode, long fUserId, int czlx, long startTime, long endTime) {
		PagerModel pm = deliveryTradeLogDao.getPageByUserId(bqCode, fUserId, czlx, startTime, endTime);
		
		@SuppressWarnings("unchecked")
		List<DeliveryTradeLog> logs = (List<DeliveryTradeLog>) pm.getDatas();
		for (DeliveryTradeLog log : logs) {
			if (log.getTrade_type_id() > 4) {
				DeliveryMemberTransactionOverloadLog overLog = overloadLogDao.getByPendingNo(log.getTransaction_code(), log.getF_user_id(), log.getPending_no());
				if (overLog != null && overLog.getTrade_force_price().compareTo(BigDecimal.ZERO) > 0) {
					log.setForce_price(overLog.getTrade_force_price());
				}
			}
		}
		
		return pm;
	}

	public void saveTradeLog(DeliveryTradeLog DeliveryTradeLog) {
		deliveryTradeLogDao.insertLog(DeliveryTradeLog);
	}
	
	@Override
	public void deleteVTradeScalpingLog(String vCode, long scalpingUid, long beforeTime) {
		Session session = deliveryTradeLogDao.getSession();
		String sql = "delete from " + vCode + "_trade_log where f_user_id = :uid and trade_time < :time";
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", scalpingUid);
		query.setParameter("time", beforeTime);
		query.executeUpdate();
	}

//	public BigDecimal getTradeAmount(String tranCode, String str, long yesterdayTime) {
//		Session session = deliveryTradeLogDao.getSession();
//		StringBuilder sbBuilder = new StringBuilder();
//		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(trade_count), 0) from " + tranCode + "_trade_log ");
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
	
	private BigDecimal getLastTimesAgoByCode(String bqCode, long seconds, boolean isHour) {
		Session session = deliveryTradeLogDao.getSession();
		String sql = "SELECT trade_average_price from " + bqCode + "_trade_log where trade_time < " + seconds
				+ " order by trade_time desc limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		Object result = query.uniqueResult();

		BigDecimal price = null;
		if (result == null) {
			String sql2 = "SELECT * from " + bqCode + "_trade_log limit 1";
			SQLQuery query2 = session.createSQLQuery(sql2);
			query2.addEntity(DeliveryTradeLog.class);
			DeliveryTradeLog result2 = (DeliveryTradeLog) query2.uniqueResult();

			if (result2 != null) {
				price = result2.getTrade_average_price();
				if (isHour) {	// 一小时，更新开盘价
					BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + bqCode);
					if (openPrice == null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.OPEN_PRICE + bqCode, BTCQMemecacheKeys.TIMES.ONE_HOUR, price);
					}
				} else {	// 一天获取第一个小时的收盘价为天的开盘价
					long time = result2.getTrade_time() / 3600 * 3600;
					BigDecimal openPrice = spyMemcachedClient.get(BTCQMemecacheKeys.OPEN_PRICE + bqCode);
					if (openPrice != null) {
						long nowTime = deliveryTradeLogDao.getUnix_TIMESTAMP() / 3600 * 3600;
						if (time != nowTime) {
							spyMemcachedClient.delete(BTCQMemecacheKeys.OPEN_PRICE + bqCode);
						}
					}
					time += 3600;
					String sql3 = "SELECT trade_average_price from " + bqCode + "_trade_log where trade_time < " + time
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

	public BigDecimal getLastDayAgoByCode(String bqCode) {
		long nowTime = deliveryTradeLogDao.getUnix_TIMESTAMP();
		long morning = TimeUtil.getTimesmorning(nowTime * 1000);
		return getLastTimesAgoByCode(bqCode, morning, false);
	}
	
	public BigDecimal readLastDayAgoByCode(String bqCode) {
		long nowTime = deliveryTradeLogDao.getUnix_TIMESTAMP();
		long morning = TimeUtil.getTimesmorning(nowTime * 1000);
		return getLastTimesAgoByCode(bqCode, morning, false);
	}
	
	public BigDecimal readLastHourAgoByCode(String bqCode) {
		long nowTime = deliveryTradeLogDao.getUnix_TIMESTAMP() / 3600;
		return getLastTimesAgoByCode(bqCode, nowTime * 3600, true);
	}
	
	@Override
	public BigDecimal getLastHourAgoByCode(String bqCode) {
		long nowTime = deliveryTradeLogDao.getUnix_TIMESTAMP() / 3600;
		return getLastTimesAgoByCode(bqCode, nowTime * 3600, true);
	}

	public BigDecimal findFeeByTimeCode(String bqCode, long userID, long starttime, long endtime) {
		Session session = deliveryTradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("SELECT ifnull(SUM(trade_fee), 0) from " + bqCode + "_trade_log where 1 = 1");
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

	public BigDecimal findAvagPrice(String bqCode, long time, int num) {

		if (TextUtils.isEmpty(bqCode)) {
			return BigDecimal.ZERO;
		}

		BigDecimal prices = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.TICKER_YESTERDAYAVGPRICE);

		if (prices == null) {
			Session session = deliveryTradeLogDao.getSession();
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
		return deliveryTradeLogDao.getInTimeHighPrice(code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeLowPrice(String code, long startTime, long endTime) {
		return deliveryTradeLogDao.getInTimeLowPrice(code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeLastPrice(String code, long startTime, long endTime) {
		return deliveryTradeLogDao.getInTimeLastPrice(code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeFirstPrice(String code, long startTime, long endTime) {
		return deliveryTradeLogDao.getInTimeFirstPrice(code, startTime, endTime);
	}

	@Override
	public BigDecimal getInTimeVol(String code, long startTime, long endTime) {
		return deliveryTradeLogDao.getInTimeVol(code, startTime, endTime);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryTradeLog> findUserTurnoverBetweenTime(String bqCode, long beginTime, long endTime, long depthUid, long quantUid) {
		Session session = deliveryTradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("select f_user_id as userId, sum(trade_average_price * trade_count) as tradeCount from " + bqCode 
				+ "_trade_log where (trade_type_id = ? or trade_type_id = ?) and f_user_id > 1 and f_user_id != ? and f_user_id != ? and trade_time >= ? and trade_time < ? group by f_user_id");
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setInteger(0, Contents.CZLX_DDKC);
		query.setInteger(1, Contents.CZLX_KTKC);
		query.setLong(2, depthUid);
		query.setLong(3, quantUid);
		query.setLong(4, beginTime);
		query.setLong(5, endTime);
		
		List<DeliveryTradeLog> retList = new ArrayList<DeliveryTradeLog>();
		List<Object[]> list = query.list();
		for (Object[] obj : list) {
			int id = (int) obj[0];
			BigDecimal sum = (BigDecimal) obj[1];
			retList.add(new DeliveryTradeLog(id, sum));
		}
		
		return retList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryTradeLog> findByUserAndTime(String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit) {
		Session session = deliveryTradeLogDao.getSession();
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
		
		query.addEntity(com.btc100.btc_q.entity.DeliveryTradeLog.class);
		return query.list();
	}

	@Override
	public BigDecimal getInTimeNoScalpingVol(String code, long startTime, long endTime, long scalUserId) {
		return deliveryTradeLogDao.getInTimeNoScalpingVol(code, startTime, endTime, scalUserId);
	}

	@Override
	public BigDecimal getInTimeNoScalpingFee(String code, long startTime, long endTime, long scalUserId) {
		return deliveryTradeLogDao.getInTimeNoScalpingFee(code, startTime, endTime, scalUserId);
	}

	@Override
	public long getTradeUserCount(String code, long startTime, long endTime, long scalUserId) {
		return deliveryTradeLogDao.getTradeUserCount(code, startTime, endTime, scalUserId);
	}

	@Override
	public List<Integer> getTradeUserIds(String code, long startTime, long endTime, long scalUserId) {
		return deliveryTradeLogDao.getTradeUserIds(code, startTime, endTime, scalUserId);
	}

	@Override
	public BigDecimal getVolByTimeAndTradeType(String code, long startTime, long endTime, long scalUserId, long userId,
			int tradeType) {
		return deliveryTradeLogDao.getVolByTimeAndTradeType(code, startTime, endTime, scalUserId, userId, tradeType);
	}

	@Override
	public BigDecimal getFeeByTimeAndUser(String code, long startTime, long endTime, long scalUserId, long userId) {
		return deliveryTradeLogDao.getFeeByTimeAndUser(code, startTime, endTime, scalUserId, userId);
	}

	@Override
	public BigDecimal getVol(String code, long startTime, long endTime, long userId, int tradeType) {
		return deliveryTradeLogDao.getVol(code, startTime, endTime, userId, tradeType);
	}

	@Override
	public BigDecimal getTradeAmount(String code, long startTime, long endTime, long userId, int tradeType) {
		return deliveryTradeLogDao.getTradeAmount(code, startTime, endTime, userId, tradeType);
	}

	@Override
	public BigDecimal getIncome(String code, long startTime, long endTime, long userId, int tradeType) {
		return deliveryTradeLogDao.getIncome(code, startTime, endTime, userId, tradeType);
	}
	
	@Override
	public List<Object[]> findIncome(String code, long startTime, long endTime, long except) {
		return deliveryTradeLogDao.findIncome(code, startTime, endTime, except);
	}

	@Override
	public Long getLastTradeId(String code, long userId) {
		Session session = deliveryTradeLogDao.getSession();
		String sql = "SELECT max(trade_id) FROM " + code + "_trade_log where f_user_id  = :userId";
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setLong("userId", userId);
		Integer id = (Integer) query.uniqueResult();
		return id == null ? 0 : id.longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryTradeLog> findGtFrom(String bqCode, long userId, long from, int limit) {
		Session session = deliveryTradeLogDao.getSession();
		StringBuffer sql = new StringBuffer("select * from " + bqCode + "_trade_log where f_user_id = :userId");
		
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
		
		query.addEntity(com.btc100.btc_q.entity.DeliveryTradeLog.class);
		return query.list();
	}

	@Override
	public Long getTime() {
		return deliveryTradeLogDao.getUnix_TIMESTAMP();
	}

	@Override
	public void createDeliveryTradeLogBak(String code) {
		deliveryTradeLogDao.createDeliveryTradeLogBak(code);
	}

	@Override
	public void insertDeliveryTradeLogBak(String code, long endTime) {
		deliveryTradeLogDao.insertDeliveryTradeLogBak(code, endTime);
		deliveryTradeLogDao.deleteDeliveryTradeLog(code, endTime);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryTradeLog> readByUserAndTime(String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit) {
		Session session = deliveryTradeLogDao.getSession();
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
		
		query.addEntity(com.btc100.btc_q.entity.DeliveryTradeLog.class);
		List<DeliveryTradeLog> logs = (List<DeliveryTradeLog>) query.list();
		for (DeliveryTradeLog log : logs) {
			if (log.getTrade_type_id() > 4) {
				DeliveryMemberTransactionOverloadLog overLog = overloadLogDao.getByPendingNo(log.getTransaction_code(), log.getF_user_id(), log.getPending_no());
				if (overLog != null && overLog.getTrade_force_price().compareTo(BigDecimal.ZERO) > 0) {
					log.setForce_price(overLog.getTrade_force_price());
				}
			}
		}
		
		return logs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryTradeLog> readFromBakByUserAndTime(String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit) {
		Session session = deliveryTradeLogDao.getSession();
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
		
		query.addEntity(com.btc100.btc_q.entity.DeliveryTradeLog.class);
		return query.list();
	}

}
