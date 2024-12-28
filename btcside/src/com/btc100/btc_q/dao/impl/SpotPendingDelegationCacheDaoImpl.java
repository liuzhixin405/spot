package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.SpotPendingDelegationCacheDao;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;

@Repository("spotPendingDelegationCacheDao")
public class SpotPendingDelegationCacheDaoImpl<T> extends BaseDaoImpl<T>
		implements SpotPendingDelegationCacheDao<T> {
	
	@Override
	public boolean insertCache(final List<SpotPendingDelegationCache> caches, final String coinPairName)
			throws HibernateException {
		if (caches.size() == 0) return false;
		
		final List<Long> rets = new ArrayList<>();
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				String sql = "insert into " + coinPairName + "_pending_delegation_cache"
						+ " (delegation_time, coin_pair_id, coin_pair_name, direction, delegation_count,"
						+ "delegation_price,deal_count,undeal_count,f_user_id,pending_no,commission_rate,f_frozenremain) "
						+ "values ";

				long unix_time = getUnix_TIMESTAMP();
				String values = "";
				for (SpotPendingDelegationCache cache : caches) {
					if (!StringUtils.isEmpty(values)) {
						values += ",";
					}
					
					String value = "(" + unix_time + "," + cache.getCoin_pair_id() + ",'" + cache.getCoin_pair_name() + "'," + cache.getDirection() + "," 
							+ cache.getDelegation_count() + "," + cache.getDelegation_price() + "," + cache.getDeal_count() + "," + cache.getUndeal_count() + ","
							+ cache.getF_user_id() + "," + cache.getPending_no() + "," + cache.getCommission_rate() + "," + cache.getF_frozenremain() + ")";
					values += value;
				}
				
				sql += values;
				PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				if (results.next()) {
					rets.add(results.getLong(1));
				}
			}

		});

		return rets.size() > 0;
	}
	
	@Override
	public SpotPendingDelegationCache insertCache(final SpotPendingDelegationCache cache)
			throws HibernateException {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				String sql = "insert into " + cache.getCoin_pair_name() + "_pending_delegation_cache"
						+ " (delegation_time,coin_pair_id,coin_pair_name,direction,delegation_count,delegation_price,"
						+ "deal_count,undeal_count,f_user_id,pending_no,commission_rate,f_frozenremain) "
						+ "values (?,?,?,?,?,?,?,?,?,?,?,?)";

				PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				long unix_time = getUnix_TIMESTAMP();
				psmt.setLong(1, unix_time);
				psmt.setLong(2, cache.getCoin_pair_id());
				psmt.setString(3, cache.getCoin_pair_name());
				psmt.setInt(4, cache.getDirection());
				psmt.setBigDecimal(5, cache.getDelegation_count());
				psmt.setBigDecimal(6, cache.getDelegation_price());
				psmt.setBigDecimal(7, cache.getDeal_count());
				psmt.setBigDecimal(8, cache.getUndeal_count());
				psmt.setLong(9, cache.getF_user_id());
				psmt.setLong(10, cache.getPending_no());
				psmt.setBigDecimal(11, cache.getCommission_rate());
				psmt.setBigDecimal(12, cache.getF_frozenremain());
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				long num = -1;
				if (results.next()) {
					num = results.getLong(1);
				}

				cache.setDelegation_id(num);
				cache.setDelegation_time(unix_time);
			}

		});

		return cache;
	}

	@Override
	public int deletePending(String coinPairName, Long id) throws HibernateException {
		Session session = getSession();
		String sql = " delete from " + coinPairName + "_pending_delegation_cache where delegation_id = ? ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);

		return query.executeUpdate();

	}

	@Override
	public SpotPendingDelegationCache getBuy1(String coinPairName) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation_cache where direction=" + Contents.DIRECTION_BUY
				+ " AND flag<>2 order by delegation_price desc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(SpotPendingDelegationCache.class);

		return (SpotPendingDelegationCache) query.uniqueResult();
	}

	@Override
	public SpotPendingDelegationCache getBuy1(String coinPairName, long fUserId) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation_cache where direction=" + Contents.DIRECTION_BUY
				+ " and f_user_id != ? order by delegation_price desc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, fUserId);
		query.addEntity(SpotPendingDelegationCache.class);

		return (SpotPendingDelegationCache) query.uniqueResult();
	}

	@Override
	public SpotPendingDelegationCache getSell1(String coinPairName) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation_cache where direction=" + Contents.DIRECTION_SELL
				+ " AND flag<>2 order by delegation_price asc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(SpotPendingDelegationCache.class);

		return (SpotPendingDelegationCache) query.uniqueResult();
	}

	@Override
	public SpotPendingDelegationCache getSell1(String coinPairName, long fUserId) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation_cache where direction=" + Contents.DIRECTION_SELL
				+ " and f_user_id != ? order by delegation_price asc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, fUserId);
		query.addEntity(SpotPendingDelegationCache.class);

		return (SpotPendingDelegationCache) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegationCache> queryByUserId(int direction, String coinPairName, long fUserId) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation_cache where f_user_id =:fUserId";
		if (direction > 0) {
			sql = sql + " and direction=:direction ";
		}
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("fUserId", fUserId);
		if (direction > 0) {
			query.setParameter("direction", direction);
		}
		query.addEntity(SpotPendingDelegationCache.class);
		
		return query.list();
	}

//	@Override
//	public boolean lock(String coinPairName, long id) {
//		Session session = getSession();
//		String sql = " update  " + coinPairName + "_pending_delegation_cache set flag = 1 where delegation_id = ? and flag = 0";
//
//		SQLQuery query = session.createSQLQuery(sql);
//		query.setLong(0, id);
//		return query.executeUpdate() > 0 ;
//	}
//
//	@Override
//	public boolean unLock(String coinPairName, long id) {
//		Session session = getSession();
//		String sql = " update  " + coinPairName + "_pending_delegation_cache set flag = 0 where delegation_id = ? and flag != 0";
//
//		SQLQuery query = session.createSQLQuery(sql);
//		query.setLong(0, id);
//		return query.executeUpdate() > 0 ;
//	}

	@Override
	public int updateNumByID(long id, String coinPairName, BigDecimal dealNum, BigDecimal undealNum) {
		Session session = getSession();
		String sql = " update  " + coinPairName + "_pending_delegation_cache "
				+ " set deal_count =deal_count+? ,undeal_count=undeal_count+? where delegation_id = ? ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal(0, dealNum);
		query.setBigDecimal(1, undealNum);
		query.setLong(2, id);

		return query.executeUpdate();
	}

	@Override
	public SpotPendingDelegationCache findPendingById(long orderId, String coinPairName) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation_cache where delegation_id= :orderId ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("orderId", orderId);
		query.addEntity(SpotPendingDelegationCache.class);
		return (SpotPendingDelegationCache) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegationCache> findPendingAtPriceByUser(long uid, String coinPairName, int direction,
			BigDecimal price) {
		Session session = getSession();
		
		String sql = "select * from " + coinPairName + "_pending_delegation_cache where direction = :direction "
				+ " and f_user_id = :uid and delegation_price = :price";

		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("direction", direction);
		query.setParameter("uid", uid);
		query.setParameter("price", price);
		query.addEntity(SpotPendingDelegationCache.class);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegationCache> findPendingByMemberTransInfo(String coinPairName, long uId, int direction) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + coinPairName + "_pending_delegation_cache "
				+ " where f_user_id=" + uId + " and direction=" + direction);

		SQLQuery query = session.createSQLQuery(sql.toString());
		query.addEntity(SpotPendingDelegationCache.class);
		return query.list();
	}

	@Override
	public BigDecimal findSumBuyFrozen(String coinPairName, long userId) {
		String sql="SELECT ifnull(SUM(t.undeal_count * t.delegation_price), 0) from "
				+ coinPairName + "_pending_delegation_cache t where t.f_user_id = :userId and direction = 1";
				
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public BigDecimal findSumSellFrozen(String coinPairName, long userId) {
		String sql="SELECT ifnull(SUM(t.undeal_count), 0) from "
				+ coinPairName + "_pending_delegation_cache t where t.f_user_id = :userId and direction = 2";
				
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public SpotPendingDelegationCache getPendingByPendingNo(String pendingNo, String coinPairName, long userId) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation_cache where pending_no= :pendingNo and f_user_id= :userId order by delegation_id desc limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("pendingNo", pendingNo);
		query.setParameter("userId", userId);
		query.addEntity(SpotPendingDelegationCache.class);
		return (SpotPendingDelegationCache) query.uniqueResult();
	}

}
