package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.DeliveryTradeLogDao;
import com.btc100.btc_q.entity.DeliveryTradeLog;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Repository("deliveryTradeLogDao")
public class DeliveryTradeLogDaoImpl<T> extends BaseDaoImpl<T> implements DeliveryTradeLogDao<T> {

	public DeliveryTradeLog insertLog(final DeliveryTradeLog tradeLog) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				PreparedStatement psmt = con
						.prepareStatement(
								"insert into "
										+ tradeLog.getTransaction_code()
										+ "_trade_log (transaction_id,transaction_code,trade_type_id,trade_trans_dirction,hold_price,trade_average_price,trade_count,trade_multiple,trade_deposit,trade_income,trade_fee,trade_bfx_fee,f_user_id,trade_time,pending_no)"
										+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
				psmt.setLong(1, tradeLog.getTransaction_id());
				psmt.setString(2, tradeLog.getTransaction_code());
				psmt.setInt(3, tradeLog.getTrade_type_id());
				psmt.setInt(4, tradeLog.getTrade_trans_dirction());
				psmt.setBigDecimal(5, tradeLog.getHold_price());
				psmt.setBigDecimal(6, tradeLog.getTrade_average_price());
				psmt.setBigDecimal(7, tradeLog.getTrade_count());
				psmt.setInt(8, tradeLog.getTrade_multiple());
				psmt.setBigDecimal(9, tradeLog.getTrade_deposit());
				psmt.setBigDecimal(10, tradeLog.getTrade_income());
				psmt.setBigDecimal(11, tradeLog.getTrade_fee());
				psmt.setBigDecimal(12, tradeLog.getTrade_bfx_fee());
				psmt.setLong(13, tradeLog.getF_user_id());
				psmt.setLong(14, getUnix_TIMESTAMP());
				psmt.setLong(15, tradeLog.getPending_no());
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
	public Boolean zDeliveryTradeLogIsExist(String code, long startTime) {
		Session session = getSession();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		String timeStr = format.format(startTime * 1000);
		String prefix = "z" + timeStr + "_" + code;
		String sql = "select count(*) from information_schema.TABLES t where t.TABLE_NAME = '" + prefix + "_trade_log'";
		SQLQuery query = session.createSQLQuery(sql);
		int tablse = ((BigInteger) query.uniqueResult()).intValue();
		if (tablse > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void createDeliveryTradeLogBak(final String code) {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				
				final String create_table_sql = "create table if not exists " + code + "_trade_log_bak like " + code + "_trade_log;";
				
				Statement ps = con.createStatement();
				ps.execute(create_table_sql);
			}
			
		});
	}

	@Override
	public void insertDeliveryTradeLogBak(final String code, final long endTime) {
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
	public void deleteDeliveryTradeLog(String code, long endTime) {
		Session session = getSession();
		String sql = "DELETE FROM " + code + "_trade_log WHERE trade_time < ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, endTime);
		query.executeUpdate();
	}
	
	@Override
	public Object[] getFeeFromZDeliveryTradeLogByUser(String code, long userId, long startTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		String timeStr = format.format(startTime * 1000);
		String prefix = "z" + timeStr + "_" + code;
		String sql="SELECT ifnull(SUM(t.trade_income), 0) AS amount, ifnull(SUM(t.trade_fee), 0) AS fee from " 
				+ prefix + "_trade_log t where t.f_user_id = :userId";
				
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		return (Object[]) query.uniqueResult();
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
	
	@Override
	public BigDecimal findPriceLastClose(String code, long time) {
		Session session = getSession();
		String sql = "select ifnull(trade_average_price, 0) AS last from " + code + "_trade_log where trade_time <"
				+ time + " order by trade_time DESC limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}
	
	@Override
	public BigDecimal findPriceAtTime(String code, long time) {
		Session session = getSession();
		String sql = "select ifnull(trade_average_price, 0) AS last from " + code + "_trade_log where trade_time <="
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

	public List<DeliveryTradeLog> getAllByUserIdTopN(String code, long userId, int n) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + code + "_trade_log WHERE f_user_id= :userId ORDER BY trade_time DESC LIMIT " + n);
		SQLQuery query = session.createSQLQuery(sql.toString());

		query.setParameter("userId", userId);
		query.addEntity(DeliveryTradeLog.class);

		return query.list();
	}

	public List<DeliveryTradeLog> findByCode(String code) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + code + "_trade_log ORDER BY trade_time DESC LIMIT " + 60);
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.addEntity(DeliveryTradeLog.class);
		return query.list();
	}

	public List<DeliveryTradeLog> getAllByUserId(String transactionCode, long fUserId, int czlx) {
		Session session = getSession();
		StringBuffer sql = null;
		SQLQuery query = null;
		if (czlx == 0) {
			sql = new StringBuffer("select * from " + transactionCode + "_trade_log WHERE f_user_id= :userId ORDER BY trade_time DESC  ");
			query = session.createSQLQuery(sql.toString());

			query.setParameter("userId", fUserId);
		} else {
			sql = new StringBuffer("select * from " + transactionCode
					+ "_trade_log WHERE f_user_id= :userId and trade_type_id= :tradeTypeId ORDER BY trade_time DESC  ");
			query = session.createSQLQuery(sql.toString());

			query.setParameter("userId", fUserId);
			query.setParameter("tradeTypeId", czlx);
		}

		query.addEntity(DeliveryTradeLog.class);

		return query.list();
	}

	public PagerModel getPageByUserId(String code, long fUserId, int czlx, long startTime, long endTime) {
		Session session = getSession();
		
		String sql = "select * from " + code + "_trade_log WHERE f_user_id= :userId";
		String countSql = "select count(1) from " + code + "_trade_log WHERE f_user_id= :userId";
		if (czlx != 0) {
			sql += " and trade_type_id= :tradeTypeId";
			countSql += " and trade_type_id= :tradeTypeId";
		}
		
		if (startTime > 0) {
			sql += " and trade_time >= :startTime";
			countSql += " and trade_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and trade_time < :endTime";
			countSql += " and trade_time < :endTime";
		}
		
		sql += " ORDER BY trade_time DESC LIMIT " + SystemContext.getOffset() + "," + SystemContext.getPageSize();
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", fUserId);
		
		SQLQuery countQuery = session.createSQLQuery(countSql);
		countQuery.setParameter("userId", fUserId);
		
		if (czlx > 0) {
			query.setParameter("tradeTypeId", czlx);
			countQuery.setParameter("tradeTypeId", czlx);
		}
		
		if (startTime > 0) {
			query.setParameter("startTime", startTime);
			countQuery.setParameter("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setParameter("endTime", endTime);
			countQuery.setParameter("endTime", endTime);
		}

		query.addEntity(DeliveryTradeLog.class);
		
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
				+ "_trade_log where trade_type_id < 5 and trade_time >= " + startTime + " and trade_time < " + endTime;
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getInTimeLowPrice(String code, long startTime, long endTime) {
		Session session = getSession();
		String sql = "SELECT IFNULL(min(trade_average_price), 0) AS low FROM " + code
				+ "_trade_log where trade_type_id < 5 and trade_time >= " + startTime + " and trade_time < " + endTime;
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getInTimeLastPrice(String code, long startTime, long endTime) {
		Session session = getSession();
		String sql = "SELECT IFNULL(trade_average_price, 0) AS last FROM " + code
				+ "_trade_log where trade_type_id < 5 and trade_time >= " + startTime + " and trade_time < " + endTime
				+ " order by trade_time DESC limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getInTimeFirstPrice(String code, long startTime, long endTime) {
		Session session = getSession();
		String sql = "SELECT IFNULL(trade_average_price, 0) AS first FROM " + code
				+ "_trade_log where trade_type_id < 5 and trade_time >= " + startTime + " and trade_time < " + endTime
				+ " limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getInTimeVol(String code, long startTime, long endTime) {
		Session session = getSession();
		String sql = "SELECT IFNULL(SUM(trade_count)/2, 0) AS vol FROM " + code
				+ "_trade_log where trade_type_id < 5 and trade_time >= " + startTime + " and trade_time < " + endTime;
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public Object[] getAllIncomeAndFeeByUser(String bqCode, long userId, long startTime, long endTime) {
		String sql="SELECT ifnull(SUM(t.trade_income), 0) AS amount, ifnull(SUM(t.trade_fee), 0) AS fee from " 
				+ bqCode + "_trade_log t where t.f_user_id = :userId";
		
		if (startTime > 0) {
			sql +=" and trade_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and trade_time < :endTime";
		}
		
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		
		if (startTime > 0) {
			query.setParameter("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setParameter("endTime", endTime);
		}
		
		query.setParameter("userId", userId);
		return (Object[]) query.uniqueResult();
	}
	
	@Override
	public Object[] getAllBakIncomeAndFeeByUser(String bqCode, long userId, long startTime, long endTime) {
		
		Session session = getSession();
		String sql0 = "select count(*) from information_schema.TABLES t where t.TABLE_NAME = '" +  bqCode + "_trade_log_bak'";
		SQLQuery query0 = session.createSQLQuery(sql0);
		int tablse = ((BigInteger) query0.uniqueResult()).intValue();
		if (tablse == 0) {
			Object[] objs = { BigDecimal.ZERO, BigDecimal.ZERO };
			return objs;
		}
		
		String sql="SELECT ifnull(SUM(t.trade_income), 0) AS amount, ifnull(SUM(t.trade_fee), 0) AS fee from " 
				+ bqCode + "_trade_log_bak t where t.f_user_id = :userId";
		
		if (startTime > 0) {
			sql +=" and trade_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and trade_time < :endTime";
		}
		
		SQLQuery query = session.createSQLQuery(sql);
		
		if (startTime > 0) {
			query.setParameter("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setParameter("endTime", endTime);
		}
		
		query.setParameter("userId", userId);
		return (Object[]) query.uniqueResult();
	}
	
	@Override
	public BigDecimal getInTimeNoScalpingVol(String code, long startTime, long endTime, long scalUserId) {
		Session session = getSession();
		String sql = "SELECT IFNULL(SUM(trade_count) / 2, 0) AS vol FROM " + code
				+ "_trade_log where f_user_id > 1 and f_user_id <> " + scalUserId 
				+ " and trade_time >= " + startTime + " and trade_time < " + endTime;
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getInTimeNoScalpingDeposit(String code, long startTime, long endTime, long scalUserId) {
		Session session = getSession();
		String sql = "SELECT IFNULL(SUM(trade_deposit), 0) AS vol FROM " + code
				+ "_trade_log where f_user_id > 1 and f_user_id <> " + scalUserId 
				+ " and  trade_time >= " + startTime + " and trade_time < " + endTime;
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getInTimeNoScalpingFee(String code, long startTime, long endTime, long scalUserId) {
		Session session = getSession();
		String sql = "SELECT IFNULL(SUM(trade_fee), 0) AS vol FROM " + code
				+ "_trade_log where f_user_id > 1 and trade_fee > 0 and f_user_id <> " + scalUserId 
				+ " and trade_time >= " + startTime + " and trade_time < " + endTime;
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

	@Override
	public List<Integer> getTradeUserIds(String code, long startTime, long endTime, long scalUserId) {
		Session session = getSession();
		String sql = "SELECT DISTINCT f_user_id FROM " + code
				+ "_trade_log where f_user_id > 1 and f_user_id <> " + scalUserId 
				+ " and trade_time >= " + startTime + " and trade_time < " + endTime;
		SQLQuery query = session.createSQLQuery(sql);
		return query.list();
	}

	@Override
	public BigDecimal getVolByTimeAndTradeType(String code, long startTime, long endTime, long scalUserId, long userId,
			int tradeType) {
		Session session = getSession();
		String sql = "SELECT IFNULL(SUM(trade_count), 0) AS vol FROM " + code + "_trade_log where"
				+ " f_user_id > 1 and f_user_id <> " + scalUserId + " and trade_time >= " + startTime
				+ " and trade_time < " + endTime + " and trade_type_id = " + tradeType;
		if (userId > 0) {
			sql = "SELECT IFNULL(SUM(trade_count), 0) AS vol FROM " + code + "_trade_log where trade_time >= "
					+ startTime + " and trade_time < " + endTime + " and trade_type_id = " + tradeType
					+ " and f_user_id = " + userId;
		}
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getFeeByTimeAndUser(String code, long startTime, long endTime, long scalUserId, long userId) {
		Session session = getSession();
		String sql = "SELECT IFNULL(SUM(trade_fee), 0) AS fee FROM " + code + "_trade_log where"
				+ " f_user_id > 1 and f_user_id <> " + scalUserId + " and trade_time >= " + startTime
				+ " and trade_time < " + endTime;
		if (userId > 0) {
			sql = "SELECT IFNULL(SUM(trade_fee), 0) AS fee FROM " + code + "_trade_log where trade_time >= " + startTime
					+ " and trade_time < " + endTime + " and f_user_id = " + userId;
		}
		SQLQuery query = session.createSQLQuery(sql);
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getVol(String code, long startTime, long endTime, long userId, int tradeType) {
		Session session = getSession();
		
		String sql = "SELECT IFNULL(SUM(trade_count), 0) AS vol FROM " + code + "_trade_log WHERE f_user_id= :userId";
		if (tradeType > 0) {
			sql += " and trade_type_id= :tradeTypeId";
		}
		
		if (startTime > 0) {
			sql += " and trade_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and trade_time < :endTime";
		}
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		if (tradeType > 0) {
			query.setParameter("tradeTypeId", tradeType);
		}
		
		if (startTime > 0) {
			query.setParameter("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setParameter("endTime", endTime);
		}
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}
	
	@Override
	public BigDecimal getTradeAmount(String code, long startTime, long endTime, long userId, int tradeType) {
		Session session = getSession();
		
		String sql = "SELECT IFNULL(SUM(trade_average_price * trade_count), 0) AS amount FROM " + code + "_trade_log WHERE 1 = 1";
		if (userId > 0) {
			sql += " and f_user_id= :userId";
		}
		if (tradeType > 0) {
			sql += " and trade_type_id= :tradeTypeId";
		}
		
		if (startTime > 0) {
			sql += " and trade_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and trade_time < :endTime";
		}
		SQLQuery query = session.createSQLQuery(sql);
		if (userId > 0) {
			query.setParameter("userId", userId);
		}
		if (tradeType > 0) {
			query.setParameter("tradeTypeId", tradeType);
		}
		
		if (startTime > 0) {
			query.setParameter("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setParameter("endTime", endTime);
		}
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal getIncome(String code, long startTime, long endTime, long userId, int tradeType) {
		Session session = getSession();
		
		String sql = "SELECT IFNULL(SUM(trade_income), 0) AS income FROM " + code + "_trade_log WHERE f_user_id= :userId";
		if (tradeType > 0) {
			sql += " and trade_type_id= :tradeTypeId";
		}
		
		if (startTime > 0) {
			sql += " and trade_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " and trade_time < :endTime";
		}
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		if (tradeType > 0) {
			query.setParameter("tradeTypeId", tradeType);
		}
		
		if (startTime > 0) {
			query.setParameter("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setParameter("endTime", endTime);
		}
		return (BigDecimal) query.uniqueResult() == null ? (new BigDecimal(0.00)) : (BigDecimal) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findIncome(String code, long startTime, long endTime, long except) {
		Session session = getSession();
		
		String sql = "SELECT f_user_id, IFNULL(SUM(trade_income), 0) AS income FROM " + code + "_trade_log WHERE trade_type_id <> 1 and trade_type_id <> 3 ";
		
		if (startTime > 0) {
			sql += " AND trade_time >= :startTime";
		}
		
		if (endTime > 0) {
			sql += " AND trade_time < :endTime";
		}
		
		if (except > 0) {
			sql += " AND f_user_id <> :except";
		}
		
		sql += " AND f_user_id > 1 AND f_user_id <> 8670 AND f_user_id <> 8774 AND f_user_id <> 1260";
		sql += " GROUP BY f_user_id ";
		
		SQLQuery query = session.createSQLQuery(sql);
		
		if (except > 0) {
			query.setParameter("except", except);
		}
		
		if (startTime > 0) {
			query.setParameter("startTime", startTime);
		}
		
		if (endTime > 0) {
			query.setParameter("endTime", endTime);
		}
		
		return query.list();
	}

}
