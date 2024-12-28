package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.SpotTradeLogDao;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Repository("spotTradeLogDao")
public class SpotTradeLogDaoImpl<SpotTradeLog> extends BaseDaoImpl<SpotTradeLog> implements SpotTradeLogDao<SpotTradeLog> {

	public com.btc100.btc_q.entity.SpotTradeLog insertLog(final com.btc100.btc_q.entity.SpotTradeLog tradeLog) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				PreparedStatement psmt = con
						.prepareStatement(
								"insert into "
										+ tradeLog.getCoin_pair_name()
										+ "_trade_log (coin_pair_id,coin_pair_name,trade_trans_direction,trade_average_price,trade_count,trade_fee,trade_bfx_fee,f_user_id,coin_id,market_coin_id,trade_amount,trade_time,pending_no)"
										+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
				psmt.setLong(1, tradeLog.getCoin_pair_id());
				psmt.setString(2, tradeLog.getCoin_pair_name());
				psmt.setInt(3, tradeLog.getTrade_trans_dirction());
				psmt.setBigDecimal(4, tradeLog.getTrade_average_price());
				psmt.setBigDecimal(5, tradeLog.getTrade_count());
				psmt.setBigDecimal(6, tradeLog.getTrade_fee());
				psmt.setBigDecimal(7, tradeLog.getTrade_bfx_fee());
				psmt.setLong(8, tradeLog.getF_user_id());
				psmt.setInt(9, tradeLog.getCoin_id());
				psmt.setInt(10, tradeLog.getMarket_coin_id());
				psmt.setBigDecimal(11, tradeLog.getTrade_amount());
				psmt.setLong(12, getUnix_TIMESTAMP());
				psmt.setLong(13, tradeLog.getPending_no());
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				long num = -1;
				if (results.next()) {
					num = results.getLong(1);
				}

				tradeLog.setTrade_id(num);

			}

		});

		return tradeLog;
	}

	@Override
	public void createTradeLogBak(final String code) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				final String create_table_sql = "create table if not exists " + code + "_trade_log_bak like " + code + "_trade_log;";
				
				Statement ps = con.createStatement();
				ps.execute(create_table_sql);
			}
			
		});
	}

	@Override
	public void insertTradeLogBak(final String code, final long endTime) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				String insert_sql = "insert into " + code + "_trade_log_bak select * from " + code + "_trade_log"
						+ " where trade_time < " + endTime;
				System.out.println(insert_sql);
				Statement ps = con.createStatement();
				ps.execute(insert_sql);
			}

		});
	}
	
	@Override
	public void deleteTradeLog(String code, long endTime) {
		Session session = getSession();
		String sql = "DELETE FROM " + code + "_trade_log WHERE trade_time < ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, endTime);
		query.executeUpdate();
	}
	
	@Autowired
	SpyMemcachedClient spyMemcachedClient;

	// max(trade_average_price) AS trade_max_price,min(trade_average_price) AS
	// trade_min_price FROM `{btc1407}_trade_log` where trade_time>1407340801;
	public BigDecimal findHighPrice(String code, long yesterdayTime) {
		Session session = getSession();
		String sql = "select ifnull(max(trade_price), 0) AS high from " + code + "_member_trade_detail where trade_time>" + yesterdayTime + " limit 1 ";
		SQLQuery query = session.createSQLQuery(sql);
		BigDecimal high = (BigDecimal) query.uniqueResult();
		spyMemcachedClient.set(BTCQMemecacheKeys.BTCHIGHSTPRICE + code, BTCQMemecacheKeys.TIMES.DEPTH_CODE, high);

		return high;
	}

	public BigDecimal findLowPrice(String code, long yesterdayTime) {
		Session session = getSession();
		String sql = "select ifnull(min(trade_price), 0) AS low from " + code + "_member_trade_detail where trade_time>" + yesterdayTime + " limit 1 ";
		SQLQuery query = session.createSQLQuery(sql);
		BigDecimal low = (BigDecimal) query.uniqueResult();
		spyMemcachedClient.set(BTCQMemecacheKeys.BTCLOWSTPRICE + code, BTCQMemecacheKeys.TIMES.DEPTH_CODE, low);
		return low;
	}

	public BigDecimal findFirstPrice(String code, long time) {
		Session session = getSession();
		String sql = "select ifnull(trade_average_price, 0) AS first from " + code + "_trade_log where trade_time>"
				+ time + " limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal findLastPrice(String code, long time) {
		Session session = getSession();
		String sql = "select ifnull(trade_average_price, 0) AS last from " + code + "_trade_log where trade_time>"
				+ time + " order by trade_time DESC limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	// select trade_average_price from btc1407_trade_log order by trade_time DESC
	// limit 1
	public BigDecimal findLastPrice(String code) {
		Session session = getSession();
		String sql = "select ifnull(trade_average_price, 0) AS last from " + code + "_trade_log order by trade_time DESC limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}
	
	// SELECT SUM(trade_count) FROM `{btc1407}_trade_log` WHERE
	// trade_time>1407340801;
	public BigDecimal findVol(String code, long yesterdayTime) {
		Session session = getSession();
		String sql = "select ifnull(SUM(trade_count), 0) AS vol from " + code + "_trade_log where trade_time>" + yesterdayTime + " limit 1 ";
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult();
	}

	public BigDecimal findTradeBTC(String code, long yesterdayTime) {
		Session session = getSession();
		String sql = "select ifnull(SUM(trade_count*trade_average_price), 0) AS vol from " + code
				+ "_trade_log where trade_time>" + yesterdayTime + " limit 1 ";
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult();
	}

	public List<com.btc100.btc_q.entity.SpotTradeLog> getAllByUserIdTopN(String code, long userId, int n) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + code + "_trade_log WHERE f_user_id= :userId ORDER BY trade_time DESC LIMIT " + n);
		SQLQuery query = session.createSQLQuery(sql.toString());

		query.setParameter("userId", userId);
		query.addEntity(com.btc100.btc_q.entity.SpotTradeLog.class);

		return query.list();
	}

	public List<com.btc100.btc_q.entity.SpotTradeLog> findByCode(String code) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + code + "_trade_log ORDER BY trade_time DESC LIMIT " + 60);
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.addEntity(com.btc100.btc_q.entity.SpotTradeLog.class);
		return query.list();
	}

	public List<com.btc100.btc_q.entity.SpotTradeLog> getAllByUserId(String transactionCode, long fUserId) {
		Session session = getSession();
		StringBuffer sql = null;
		SQLQuery query = null;
		sql = new StringBuffer("select * from " + transactionCode + "_trade_log WHERE f_user_id= :userId ORDER BY trade_time DESC  ");
		query = session.createSQLQuery(sql.toString());
		query.setParameter("userId", fUserId);
		query.addEntity(com.btc100.btc_q.entity.SpotTradeLog.class);

		return query.list();
	}

	public PagerModel getPageByUserId(String code, long fUserId, int direction, long startTime, long endTime) {
		Session session = getSession();

		String sql = null;
		String countSql = null;
		SQLQuery query = null;
		SQLQuery countQuery = null;
		if (direction == 0) {
			sql = "select * from " + code + "_trade_log WHERE f_user_id= :userId";
			countSql = "select count(1) from " + code + "_trade_log WHERE f_user_id= :userId";

			if (startTime > 0) {
				sql += " and trade_time >= :startTime";
				countSql += " and trade_time >= :startTime";
			}
			
			if (endTime > 0) {
				sql += " and trade_time < :endTime";
				countSql += " and trade_time < :endTime";
			}
			
			sql += " ORDER BY trade_time DESC LIMIT " + SystemContext.getOffset() + "," + SystemContext.getPageSize();
			
			query = session.createSQLQuery(sql);
			query.setParameter("userId", fUserId);
			
			countQuery = session.createSQLQuery(countSql);
			countQuery.setParameter("userId", fUserId);
			
			if (startTime > 0) {
				query.setParameter("startTime", startTime);
				countQuery.setParameter("startTime", startTime);
			}
			
			if (endTime > 0) {
				query.setParameter("endTime", endTime);
				countQuery.setParameter("endTime", endTime);
			}
			
		} else {
			sql = "select * from " + code + "_trade_log WHERE f_user_id= :userId";
			countSql = "select count(1) from " + code + "_trade_log WHERE f_user_id= :userId and trade_trans_dirction= :direction";
			
			if (startTime > 0) {
				sql += " and trade_time >= :startTime";
				countSql += " and trade_time >= :startTime";
			}
			
			if (endTime > 0) {
				sql += " and trade_time < :endTime";
				countSql += " and trade_time < :endTime";
			}
			
			sql += " and trade_trans_dirction= :direction ORDER BY trade_time DESC LIMIT " + SystemContext.getOffset() + "," + SystemContext.getPageSize();
					
			query = session.createSQLQuery(sql);
			query.setParameter("userId", fUserId);
			query.setParameter("direction", direction);
			
			countQuery = session.createSQLQuery(countSql);
			countQuery.setParameter("userId", fUserId);
			countQuery.setParameter("direction", direction);
			
			if (startTime > 0) {
				query.setParameter("startTime", startTime);
				countQuery.setParameter("startTime", startTime);
			}
			
			if (endTime > 0) {
				query.setParameter("endTime", endTime);
				countQuery.setParameter("endTime", endTime);
			}
		}

		query.addEntity(com.btc100.btc_q.entity.SpotTradeLog.class);
		
		PagerModel pm = new PagerModel();
		pm.setDatas(query.list());
		pm.setTotal(ConvertUtil.toInt(countQuery.uniqueResult(), 0));
		return pm;
	}
	
	public PagerModel getPageByUserId(String code, long fUserId, int direction) {
		Session session = getSession();

		StringBuffer sql = null;
		StringBuffer countSql = null;
		SQLQuery query = null;
		SQLQuery countQuery = null;
		if (direction == 0) {
			sql = new StringBuffer("select * from " + code + "_trade_log WHERE f_user_id= :userId ORDER BY trade_time DESC LIMIT " + SystemContext.getOffset() + "," + SystemContext.getPageSize());
			query = session.createSQLQuery(sql.toString());
			query.setParameter("userId", fUserId);
			
			countSql = new StringBuffer("select count(1) from " + code + "_trade_log WHERE f_user_id= :userId");
			countQuery = session.createSQLQuery(countSql.toString());
			countQuery.setParameter("userId", fUserId);
			
		} else {
			sql = new StringBuffer("select * from " + code
					+ "_trade_log WHERE f_user_id= :userId and trade_trans_dirction= :direction ORDER BY trade_time DESC LIMIT " + SystemContext.getOffset() + "," + SystemContext.getPageSize());
			query = session.createSQLQuery(sql.toString());
			query.setParameter("userId", fUserId);
			query.setParameter("direction", direction);
			
			countSql = new StringBuffer("select count(1) from " + code + "_trade_log WHERE f_user_id= :userId and trade_trans_dirction= :direction");
			countQuery = session.createSQLQuery(countSql.toString());
			countQuery.setParameter("userId", fUserId);
			countQuery.setParameter("direction", direction);
		}

		query.addEntity(com.btc100.btc_q.entity.SpotTradeLog.class);
		
		PagerModel pm = new PagerModel();
		pm.setDatas(query.list());
		pm.setTotal(ConvertUtil.toInt(countQuery.uniqueResult(), 0));
		return pm;
	}

	@Override
	public long maxTime(String code, String type) {
		long maxTime = 0L;
		Session session = getSession();
		String sql = null;
		if ("oneMin".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MAX(trade_time) DIV 60 * 60)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("threeMin".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MAX(trade_time) DIV 180 * 180)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("fiveMin".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MAX(trade_time) DIV 300 * 300)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("fifteenMin".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MAX(trade_time) DIV 900 * 900)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("halfHour".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MAX(trade_time) DIV 1800 * 1800)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("oneHour".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MAX(trade_time) DIV 3600 * 3600)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("twoHours".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MAX(trade_time) DIV 7200 * 7200)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("fourHours".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MAX(trade_time) DIV 14400 * 14400)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("sixHours".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MAX(trade_time) DIV 21600 * 21600 - 7200)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("twelveHours".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MAX(trade_time) DIV 43200 * 43200 - 28800)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("oneDay".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MAX(trade_time) DIV 86400 * 86400 - 28800)) AS trade_time FROM "
					 + code + "_trade_log";
		}
		SQLQuery query = session.createSQLQuery(sql);
		BigInteger time = (BigInteger) query.uniqueResult();
		if (null == time) {
			maxTime = TimeUtil.getTimesmorning();
		} else {
			maxTime =  time.longValue();
		}
		return maxTime;
	}

	@Override
	public long minTime(String code, String type) {
		long minTime = 0L;
		Session session = getSession();
		String sql = null;
		if ("oneMin".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MIN(trade_time) DIV 60 * 60)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("threeMin".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MIN(trade_time) DIV 180 * 180)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("fiveMin".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MIN(trade_time) DIV 300 * 300)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("fifteenMin".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MIN(trade_time) DIV 900 * 900)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("halfHour".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MIN(trade_time) DIV 1800 * 1800)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("oneHour".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MIN(trade_time) DIV 3600 * 3600)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("twoHours".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MIN(trade_time) DIV 7200 * 7200)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("fourHours".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MIN(trade_time) DIV 14400 * 14400)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("sixHours".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MIN(trade_time) DIV 21600 * 21600 - 7200)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("twelveHours".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MIN(trade_time) DIV 43200 * 43200 - 28800)) AS trade_time FROM "
					 + code + "_trade_log";
		} else if ("oneDay".equals(type)) {
			sql = "SELECT UNIX_TIMESTAMP(FROM_UNIXTIME(MIN(trade_time) DIV 86400 * 86400 - 28800)) AS trade_time FROM "
					 + code + "_trade_log";
		}
		SQLQuery query = session.createSQLQuery(sql);
		BigInteger time = (BigInteger) query.uniqueResult();
		if (null == time) {
			minTime = TimeUtil.getTimesmorning();
		} else {
			minTime =  time.longValue();
		}
		return minTime;
	}

	@Override
	public BigDecimal getInTimeHighPrice(String code, long startTime, long endTime) {
		Session session = getSession();
		String sql = "SELECT IFNULL(max(trade_average_price), 0) AS high FROM " + code
				+ "_trade_log where trade_time >= " + startTime + " and trade_time < " + endTime;
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getInTimeLowPrice(String code, long startTime, long endTime) {
		Session session = getSession();
		String sql = "SELECT IFNULL(min(trade_average_price), 0) AS low FROM " + code
				+ "_trade_log where trade_time >= " + startTime + " and trade_time < " + endTime;
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getInTimeLastPrice(String code, long startTime, long endTime) {
		Session session = getSession();
		String sql = "SELECT IFNULL(trade_average_price, 0) AS last FROM " + code
				+ "_trade_log where trade_time >= " + startTime + " and trade_time < " + endTime
				+ " order by trade_time DESC limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getInTimeFirstPrice(String code, long startTime, long endTime) {
		Session session = getSession();
		String sql = "SELECT IFNULL(trade_average_price, 0) AS first FROM " + code
				+ "_trade_log where trade_time >= " + startTime + " and trade_time < " + endTime
				+ " limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getInTimeVol(String code, long startTime, long endTime) {
		Session session = getSession();
		String sql = "SELECT IFNULL(SUM(trade_count)/2, 0) AS vol FROM " + code
				+ "_trade_log where trade_time >= " + startTime + " and trade_time < " + endTime;
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal sumBuyIn(String code, long uid, int currencyId) {
		String sql = "select IFNULL(sum(b.trade_count - b.trade_fee),0) from " + code + "_trade_log" + " b where 1=1 and f_user_id = :uid and coin_id = :currencyId and trade_trans_dirction = 1";	
		
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("uid", uid);
		query.setInteger("currencyId", currencyId);
		return (BigDecimal) query.uniqueResult();
	}
	
	@Override
	public BigDecimal sumBakBuyIn(String code, long uid, int currencyId) {
		Session session = getSession();
		
		String sql0 = "select count(*) from information_schema.TABLES t where t.TABLE_NAME = '" +  code + "_trade_log_bak'";
		SQLQuery query0 = session.createSQLQuery(sql0);
		int tablse = ((BigInteger) query0.uniqueResult()).intValue();
		if (tablse == 0) {
			return BigDecimal.ZERO;
		}
		
		String sql = "select IFNULL(sum(b.trade_count - b.trade_fee),0) from " + code + "_trade_log_bak" + " b where 1=1 and f_user_id = :uid and coin_id = :currencyId and trade_trans_dirction = 1";	
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("uid", uid);
		query.setInteger("currencyId", currencyId);
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal sumSellOut(String code, long uid, int currencyId) {
		String sql = "select IFNULL(sum(b.trade_count),0) from " + code + "_trade_log" + " b where 1=1 and f_user_id = :uid and coin_id = :currencyId and trade_trans_dirction = 2";	
		
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("uid", uid);
		query.setInteger("currencyId", currencyId);
		return (BigDecimal) query.uniqueResult();
	}
	
	@Override
	public BigDecimal sumBakSellOut(String code, long uid, int currencyId) {
		Session session = getSession();
		
		String sql0 = "select count(*) from information_schema.TABLES t where t.TABLE_NAME = '" +  code + "_trade_log_bak'";
		SQLQuery query0 = session.createSQLQuery(sql0);
		int tablse = ((BigInteger) query0.uniqueResult()).intValue();
		if (tablse == 0) {
			return BigDecimal.ZERO;
		}
		
		String sql = "select IFNULL(sum(b.trade_count),0) from " + code + "_trade_log_bak" + " b where 1=1 and f_user_id = :uid and coin_id = :currencyId and trade_trans_dirction = 2";	
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("uid", uid);
		query.setInteger("currencyId", currencyId);
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal sumBuySpend(String code, long uid, int currencyId) {
		String sql = "select IFNULL(sum(b.trade_amount),0) from " + code + "_trade_log" + " b where 1=1 and f_user_id = :uid and market_coin_id = :currencyId and trade_trans_dirction = 1";	
		
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("uid", uid);
		query.setInteger("currencyId", currencyId);
		return (BigDecimal) query.uniqueResult();
	}
	
	@Override
	public BigDecimal sumBakBuySpend(String code, long uid, int currencyId) {
		Session session = getSession();
		
		String sql0 = "select count(*) from information_schema.TABLES t where t.TABLE_NAME = '" +  code + "_trade_log_bak'";
		SQLQuery query0 = session.createSQLQuery(sql0);
		int tablse = ((BigInteger) query0.uniqueResult()).intValue();
		if (tablse == 0) {
			return BigDecimal.ZERO;
		}
		
		String sql = "select IFNULL(sum(b.trade_amount),0) from " + code + "_trade_log_bak" + " b where 1=1 and f_user_id = :uid and market_coin_id = :currencyId and trade_trans_dirction = 1";	
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("uid", uid);
		query.setInteger("currencyId", currencyId);
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal sumSellObtain(String code, long uid, int currencyId) {
		String sql = "select IFNULL(sum(b.trade_amount - b.trade_fee),0) from " + code + "_trade_log" + " b where 1=1 and f_user_id = :uid and market_coin_id = :currencyId and trade_trans_dirction = 2";	
		
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("uid", uid);
		query.setInteger("currencyId", currencyId);
		return (BigDecimal) query.uniqueResult();
	}
	
	@Override
	public BigDecimal sumBakSellObtain(String code, long uid, int currencyId) {
		Session session = getSession();
		
		String sql0 = "select count(*) from information_schema.TABLES t where t.TABLE_NAME = '" +  code + "_trade_log_bak'";
		SQLQuery query0 = session.createSQLQuery(sql0);
		int tablse = ((BigInteger) query0.uniqueResult()).intValue();
		if (tablse == 0) {
			return BigDecimal.ZERO;
		}
		
		String sql = "select IFNULL(sum(b.trade_amount - b.trade_fee),0) from " + code + "_trade_log_bak" + " b where 1=1 and f_user_id = :uid and market_coin_id = :currencyId and trade_trans_dirction = 2";	
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong("uid", uid);
		query.setInteger("currencyId", currencyId);
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getFeeInTimeNoScalping(String code, int direction, long startTime, long endTime,
			long scalUserId) {
		Session session = getSession();
		String sql = "SELECT IFNULL(SUM(trade_fee), 0) AS vol FROM " + code
				+ "_trade_log where f_user_id > 1 and trade_fee > 0 and f_user_id <> " + scalUserId 
				+ " and trade_time >= " + startTime + " and trade_time < " + endTime
				+ " and trade_trans_dirction = " + direction;
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getBfxFeeNoScalping(String code, int direction, long startTime, long endTime,
			long scalUserId) {
		Session session = getSession();
		String sql = "SELECT IFNULL(SUM(trade_bfx_fee), 0) AS vol FROM " + code
				+ "_trade_log where f_user_id > 1 and trade_bfx_fee > 0 and f_user_id <> " + scalUserId 
				+ " and trade_time >= " + startTime + " and trade_time < " + endTime
				+ " and trade_trans_dirction = " + direction;
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public long getTradeUserCount(String code, long startTime, long endTime, long scalUserId) {
		Session session = getSession();
		String sql = "SELECT COUNT(1) FROM (SELECT DISTINCT f_user_id FROM " + code
				+ "_trade_log where f_user_id > 1 and f_user_id <> " + scalUserId 
				+ " and trade_time >= " + startTime + " and trade_time < " + endTime + ") T";
		SQLQuery query = session.createSQLQuery(sql);
		BigInteger total = (BigInteger) query.uniqueResult();
		return total.longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<java.lang.Integer> getTradeUserIds(String code, long startTime, long endTime, long scalUserId) {
		Session session = getSession();
		String sql = "SELECT DISTINCT f_user_id FROM " + code
				+ "_trade_log where f_user_id > 1 and f_user_id <> " + scalUserId 
				+ " and trade_time >= " + startTime + " and trade_time < " + endTime;
		SQLQuery query = session.createSQLQuery(sql);
		return query.list();
	}
	
}
