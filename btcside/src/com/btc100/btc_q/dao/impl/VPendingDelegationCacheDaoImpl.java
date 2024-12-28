package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.VPendingDelegationCacheDao;
import com.btc100.btc_q.entity.VPendingDelegationCache;

@Repository("vPendingDelegationCacheDao")
public class VPendingDelegationCacheDaoImpl<T> extends BaseDaoImpl<T> implements
		VPendingDelegationCacheDao<T> {

	public int deletePending(String code, Long id) throws HibernateException {
		Session session = getSession();

		String sql = " delete from " + code + "_v_pending_delegation_cache where delegation_id = ? ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);

		return query.executeUpdate();

	}

	public int deletePending(String code, int priority, int direction) throws HibernateException {
		Session session = getSession();

		String sql = " delete from " + code + "_v_pending_delegation_cache WHERE direction=? AND priority=?";

		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, direction);
		query.setInteger(1, priority);

		return query.executeUpdate();
	}

	public List<com.btc100.btc_q.entity.VPendingDelegationCache> query(String code, Map<String, Object> params) throws HibernateException {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + code + "_v_pending_delegation_cache where 1=1 ");
		if (params.containsKey("direction")) {
			sql.append(" and direction = :direction ");

		}
		SQLQuery query = session.createSQLQuery(sql.toString());

		if (params.containsKey("direction")) {
			sql.append(" and direction = :direction ");

			query.setParameter("direction", params.get("direction"));
		}

		query.addEntity(com.btc100.btc_q.entity.VPendingDelegationCache.class);
		return query.list();
	}

	public com.btc100.btc_q.entity.VPendingDelegationCache getBuy1(String code) {
		Session session = getSession();
		String sql = "select * from " + code + "_v_pending_delegation_cache where direction=" + Contents.DIRECTION_BUY
				+ " AND flag<>2 order by delegation_price desc,f_enforce_price desc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(com.btc100.btc_q.entity.VPendingDelegationCache.class);

		return (com.btc100.btc_q.entity.VPendingDelegationCache) query.uniqueResult();
	}

	public com.btc100.btc_q.entity.VPendingDelegationCache getSell1(String code) {
		Session session = getSession();
		String sql = "select * from " + code + "_v_pending_delegation_cache where direction=" + Contents.DIRECTION_SELL
				+ " AND flag<>2 order by delegation_price asc,f_enforce_price desc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(com.btc100.btc_q.entity.VPendingDelegationCache.class);

		return (com.btc100.btc_q.entity.VPendingDelegationCache) query.uniqueResult();
	}

	public com.btc100.btc_q.entity.VPendingDelegationCache getBuy1(String code, long fUserId) {
		Session session = getSession();
		String sql = "select * from " + code + "_v_pending_delegation_cache where direction=" + Contents.DIRECTION_BUY
				+ " and f_user_id != ? order by delegation_price desc,f_enforce_price desc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, fUserId);
		query.addEntity(com.btc100.btc_q.entity.VPendingDelegationCache.class);

		return (com.btc100.btc_q.entity.VPendingDelegationCache) query.uniqueResult();
	}

	public com.btc100.btc_q.entity.VPendingDelegationCache getSell1(String code, long fUserId) {
		Session session = getSession();
		String sql = "select * from " + code + "_v_pending_delegation_cache where direction=" + Contents.DIRECTION_SELL
				+ " and f_user_id != ? order by delegation_price asc,f_enforce_price desc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, fUserId);
		query.addEntity(com.btc100.btc_q.entity.VPendingDelegationCache.class);

		return (com.btc100.btc_q.entity.VPendingDelegationCache) query.uniqueResult();
	}

	public void updateCacheDealNum(com.btc100.btc_q.entity.VPendingDelegationCache sellcache1) {
		Session session = getSession();

		String sql = " update  " + sellcache1.getTransaction_code() + "_v_pending_delegation_cache set deal_count =? ,undeal_count=? where delegation_id = ? ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal(0, sellcache1.getDeal_count());
		query.setBigDecimal(1, sellcache1.getUndeal_count());
		query.setLong(2, sellcache1.getDelegation_id());

		query.executeUpdate();

	}

	public void updateCacheNumByID(long id, String scode, BigDecimal dealNum, BigDecimal undealNum, BigDecimal fromremain, BigDecimal frozenremain) {
		Session session = getSession();
		String jsdespoit = "deposit=undeal_count*delegation_price/multiple";

		String sql = " update  " + scode
				+ "_v_pending_delegation_cache set deal_count =deal_count+? ,undeal_count=undeal_count+? , f_fromremain=f_fromremain+?, f_frozenremain=f_frozenremain+?, " + jsdespoit
				+ " where delegation_id = ? ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal(0, dealNum);
		query.setBigDecimal(1, undealNum);
		query.setBigDecimal(2, fromremain);
		query.setBigDecimal(3, frozenremain);
		query.setLong(4, id);

		query.executeUpdate();
	}

	public List<com.btc100.btc_q.entity.VPendingDelegationCache> queryByUserId(int direction, String code, long fUserId) {
		Session session = getSession();
		String sql = "select * from " + code + "_v_pending_delegation_cache where f_user_id =:fUserId";
		if (direction > 0) {
			sql = sql + " and direction=:direction ";
		}
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("fUserId", fUserId);
		if (direction > 0) {
			query.setParameter("direction", direction);
		}
		query.addEntity(com.btc100.btc_q.entity.VPendingDelegationCache.class);
		return query.list();

	}
	
	@Override
	public boolean insertCache(final List<VPendingDelegationCache> caches, final String code) throws HibernateException {
		if (caches.size() == 0) return false;
		
		final List<Long> rets = new ArrayList<>();
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				String sql = "insert into " + code + "_v_pending_delegation_cache"
						+ " (delegation_time, transaction_id, transaction_code, direction, delegation_count,"
						+ "delegation_price,deal_count,undeal_count,multiple,deposit,priority,f_user_id,f_fromremain,f_enforce_price,pending_no,commission_rate,f_frozenremain) "
						+ "values ";

				long unix_time = getUnix_TIMESTAMP();
				String values = "";
				for (VPendingDelegationCache cache : caches) {
					if (!StringUtils.isEmpty(values)) {
						values += ",";
					}
					
					String value = "(" + unix_time + "," + cache.getTransaction_id() + ",'" + cache.getTransaction_code() + "'," + cache.getDirection() + "," 
							+ cache.getDelegation_count() + "," + cache.getDelegation_price() + "," + cache.getDeal_count() + "," + cache.getUndeal_count() + ","
							+ cache.getMultiple() + "," + cache.getDeposit() + "," + cache.getPriority() + "," + cache.getF_user_id() + "," + cache.getF_fromremain() 
							+ "," + cache.getF_enforce_price() + "," + cache.getPending_no() + "," + cache.getCommission_rate() + "," + cache.getF_frozenremain() + ")";
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

	public com.btc100.btc_q.entity.VPendingDelegationCache insertCache(final com.btc100.btc_q.entity.VPendingDelegationCache cache) throws HibernateException {
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				String sql = "insert into " + cache.getTransaction_code() + "_v_pending_delegation_cache"
						+ " (delegation_time, transaction_id, transaction_code, direction, delegation_count,"
						+ "delegation_price,deal_count,undeal_count,multiple,deposit,priority,f_user_id,f_fromremain,f_enforce_price,pending_no,commission_rate,f_frozenremain) "
						+ "values (?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?)";

				PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				long unix_time = getUnix_TIMESTAMP();
				psmt.setLong(1, unix_time);
				psmt.setLong(2, cache.getTransaction_id());
				psmt.setString(3, cache.getTransaction_code());
				psmt.setInt(4, cache.getDirection());
				psmt.setBigDecimal(5, cache.getDelegation_count());
				psmt.setBigDecimal(6, cache.getDelegation_price());
				psmt.setBigDecimal(7, cache.getDeal_count());
				psmt.setBigDecimal(8, cache.getUndeal_count());
				psmt.setInt(9, cache.getMultiple());
				psmt.setBigDecimal(10, cache.getDeposit());
				psmt.setInt(11, cache.getPriority());
				psmt.setLong(12, cache.getF_user_id());
				psmt.setBigDecimal(13, cache.getF_fromremain());
				psmt.setBigDecimal(14, cache.getF_enforce_price());
				psmt.setLong(15, cache.getPending_no());
				psmt.setBigDecimal(16, cache.getCommission_rate());
				psmt.setBigDecimal(17, cache.getF_frozenremain());
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

	public List<com.btc100.btc_q.entity.VPendingDelegationCache> getBuyList(String code0) {
		Session session = getSession();
		String sql = "select * from " + code0 + "_v_pending_delegation_cache where direction=" + Contents.DIRECTION_BUY
				+ "  order by delegation_price desc,f_enforce_price desc,delegation_time asc limit 0,50";

		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(com.btc100.btc_q.entity.VPendingDelegationCache.class);

		return query.list();
	}

	public List<com.btc100.btc_q.entity.VPendingDelegationCache> getSellList(String code0) {
		Session session = getSession();
		String sql = "select * from " + code0 + "_v_pending_delegation_cache where direction=" + Contents.DIRECTION_SELL
				+ "  order by delegation_price asc,f_enforce_price desc,delegation_time asc limit 0,50";

		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(com.btc100.btc_q.entity.VPendingDelegationCache.class);

		return query.list();
	}

	public int changePendingFlagForDelPC(int direction, String code, Long fUserId) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("update " + code + "_v_pending_delegation_cache set flag=2 where f_user_id=" + fUserId + " and direction="
				+ direction + " AND priority=2");

		SQLQuery query = session.createSQLQuery(sql.toString());
		return query.executeUpdate();
	}

	public List<com.btc100.btc_q.entity.VPendingDelegationCache> queryPCDelegations(int direction, String code, Long fUserId, int multiple) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + code + "_v_pending_delegation_cache where f_user_id=" + fUserId + " and direction=" + direction
				+ " AND priority=2 and multiple=" + multiple);

		SQLQuery query = session.createSQLQuery(sql.toString());
		query.addEntity(com.btc100.btc_q.entity.VPendingDelegationCache.class);
		return query.list();
	}

	public int updateNumByID(long id, String scode, BigDecimal dealNum, BigDecimal undealNum, BigDecimal fromremain) {
		Session session = getSession();
		String sql = " update  " + scode
				+ "_v_pending_delegation_cache set deal_count =deal_count+? ,undeal_count=undeal_count+? , f_fromremain=f_fromremain+? where delegation_id = ? ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal(0, dealNum);
		query.setBigDecimal(1, undealNum);
		query.setBigDecimal(2, fromremain);
		query.setLong(3, id);

		return query.executeUpdate();
	}

	public void delete() {
	}

	/*
	@Override
	public int changeToTrading(String code, Long id) {
		Session session = getSession();
		String sql = " update  " + code + "_v_pending_delegation_cache set flag = 1  where delegation_id = ? and flag=0";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);
		return query.executeUpdate();
	}

	@Override
	public int changeToCanceling(String code, Long id) {
		Session session = getSession();
		String sql = " update  " + code + "_v_pending_delegation_cache set flag = 2  where delegation_id = ? and flag=0";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);
		return query.executeUpdate();
	}

	@Override
	public int resetToNormal(String code, Long id) {
		Session session = getSession();
		String sql = " update  " + code + "_v_pending_delegation_cache set flag = 0  where delegation_id = ? and flag!=0";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);
		return query.executeUpdate();
	}
	 */
	
	@Override
	public VPendingDelegationCache findPendingById(long orderId, String vCode, long userId) {
		Session session = getSession();
		String sql = "select * from " + vCode + "_v_pending_delegation_cache where delegation_id= :orderId and f_user_id= :userId";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("orderId", orderId);
		query.setParameter("userId", userId);
		query.addEntity(VPendingDelegationCache.class);
		return (VPendingDelegationCache) query.uniqueResult();
	}

	@Override
	public VPendingDelegationCache findPendingById(long orderId, String vCode) {
		Session session = getSession();
		String sql = "select * from " + vCode + "_v_pending_delegation_cache where delegation_id= :orderId ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("orderId", orderId);
		query.addEntity(VPendingDelegationCache.class);
		return (VPendingDelegationCache) query.uniqueResult();
	}
	
	@Override
	public VPendingDelegationCache getPendingByPendingNo(String pendingNo, String vCode, long userId) {
		Session session = getSession();
		String sql = "select * from " + vCode + "_v_pending_delegation_cache where pending_no= :pendingNo and f_user_id= :userId order by delegation_id desc limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("pendingNo", pendingNo);
		query.setParameter("userId", userId);
		query.addEntity(VPendingDelegationCache.class);
		return (VPendingDelegationCache) query.uniqueResult();
	}

	@Override
	public List<VPendingDelegationCache> findPendingBetweenPrice(String code, int direction, BigDecimal minPrice, BigDecimal maxPrice) {
		Session session = getSession();
		
		String sql = "select * from " + code + "_v_pending_delegation_cache where direction = :direction "
				+ " and delegation_price >= :minPrice and delegation_price <= :maxPrice order by delegation_price" 
				+ (direction == Contents.DIRECTION_BUY ? " desc" : "");

		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("direction", direction);
		query.setParameter("minPrice", minPrice);
		query.setParameter("maxPrice", maxPrice);
		query.addEntity(VPendingDelegationCache.class);

		return query.list();
	}
	
	@Override
	public List<VPendingDelegationCache> findPendingAtPriceByUser(long uid, String code, int direction,
			BigDecimal price) {
		Session session = getSession();
		
		String sql = "select * from " + code + "_v_pending_delegation_cache where direction = :direction "
				+ " and f_user_id = :uid and delegation_price = :price";

		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("direction", direction);
		query.setParameter("uid", uid);
		query.setParameter("price", price);
		query.addEntity(VPendingDelegationCache.class);

		return query.list();
	}

	@Override
	public List<VPendingDelegationCache> findPendingByMemberTransInfo(String code, long userId, int priority,
			int direction, int multiple) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + code + "_v_pending_delegation_cache where f_user_id=" + userId + " and direction=" + direction
				+ " AND priority=" + priority + " and multiple=" + multiple + " order by delegation_price");
		
		if (direction == Contents.DIRECTION_BUY ) {
			sql.append(" desc");
		}

		SQLQuery query = session.createSQLQuery(sql.toString());
		query.addEntity(com.btc100.btc_q.entity.VPendingDelegationCache.class);
		return query.list();
	}

//	@Override
//	public boolean lock(String code, long id) {
//		Session session = getSession();
//		String sql = " update  " + code + "_v_pending_delegation_cache set flag = 1  where delegation_id = ? and flag = 0";
//
//		SQLQuery query = session.createSQLQuery(sql);
//		query.setLong(0, id);
//		return query.executeUpdate() > 0 ;
//	}
//
//	@Override
//	public boolean unLock(String code, long id) {
//		Session session = getSession();
//		String sql = " update  " + code + "_v_pending_delegation_cache set flag = 0  where delegation_id = ? and flag != 0";
//
//		SQLQuery query = session.createSQLQuery(sql);
//		query.setLong(0, id);
//		return query.executeUpdate() > 0 ;
//	}

	@Override
	public Object[] findSumDepositAndFeeByUser(String code, long userId) {
		String sql="SELECT ifnull(SUM(t.undeal_count * t.delegation_price / t.multiple), 0) AS amount, ifnull(SUM(t.delegation_price * t.undeal_count * commission_rate), 0) AS fee from " 
				+ code + "_v_pending_delegation_cache t where t.f_user_id = :userId and t.priority = 1";
				
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		
		return (Object[]) query.uniqueResult();
	}
}
