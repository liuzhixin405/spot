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
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.SpotPendingDelegationDao;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.entity.ViewSpotPendingManager;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.MarketPendingVO;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;
import com.btc100.btc_q.vo.Top5VO;

@Repository("spotPendingDelegationDao")
public class SpotPendingDelegationDaoImpl<T> extends BaseDaoImpl<T> implements SpotPendingDelegationDao<T> {

	@Override
	public int deletePending(String coinPairName, Long id) throws HibernateException {
		Session session = getSession();
		String sql = " delete from " + coinPairName + "_pending_delegation where delegation_id = ? ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);

		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegation> query(String coinPairName, Map<String, Object> params)
			throws HibernateException {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + coinPairName + "_pending_delegation where 1=1 ");
		if (params.containsKey("direction")) {
			sql.append(" and direction = :direction ");

		}
		SQLQuery query = session.createSQLQuery(sql.toString());

		if (params.containsKey("direction")) {
			sql.append(" and direction = :direction ");

			query.setParameter("direction", params.get("direction"));
		}

		query.addEntity(com.btc100.btc_q.entity.SpotPendingDelegation.class);
		return query.list();
	}

	@Override
	public SpotPendingDelegation getBuy1(String coinPairName) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation where direction=" + Contents.DIRECTION_BUY
				+ " order by delegation_price desc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(SpotPendingDelegation.class);

		return (SpotPendingDelegation) query.uniqueResult();
	}

	@Override
	public SpotPendingDelegation getBuy1(String coinPairName, long fUserId) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation where direction=" + Contents.DIRECTION_BUY
				+ " and f_user_id != ? order by delegation_price desc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, fUserId);
		query.addEntity(SpotPendingDelegation.class);

		return (SpotPendingDelegation) query.uniqueResult();
	}

	@Override
	public SpotPendingDelegation getSell1(String coinPairName) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation where direction=" + Contents.DIRECTION_SELL
				+ " order by delegation_price asc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(SpotPendingDelegation.class);

		return (SpotPendingDelegation) query.uniqueResult();
	}

	@Override
	public SpotPendingDelegation getSell1(String coinPairName, long fUserId) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation where direction=" + Contents.DIRECTION_SELL
				+ " and f_user_id != ? order by delegation_price asc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, fUserId);
		query.addEntity(SpotPendingDelegation.class);

		return (SpotPendingDelegation) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Top5VO> getBuyTop5(String coinPairName) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("SELECT delegation_price as price ,sum(undeal_count) AS countNum from " + coinPairName
				+ "_pending_delegation where direction=:direction and flag < 2 GROUP BY delegation_price ORDER BY delegation_price DESC LIMIT 20 ");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(Top5VO.class));

		query.setParameter("direction", Contents.DIRECTION_BUY);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Top5VO> getSellTop5(String coinPairName) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("SELECT delegation_price as price ,sum(undeal_count) AS countNum from " + coinPairName
				+ "_pending_delegation where  direction=:direction and flag < 2 GROUP BY delegation_price ORDER BY delegation_price ASC LIMIT 20 ");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(Top5VO.class));

		query.setParameter("direction", Contents.DIRECTION_SELL);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegation> queryByUserId(int direction, String coinPairName, long fUserId) {
		Session session = getSession();

		String sql = "select * from " + coinPairName + "_pending_delegation where f_user_id =:fUserId";
		if (direction > 0) {
			sql = sql + " and direction=:direction ";
		}

		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("fUserId", fUserId);

		if (direction > 0) {
			query.setParameter("direction", direction);
		}

		query.addEntity(SpotPendingDelegation.class);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegation> queryByTypeUId(String coinPairName, String direction, long uId, String orderby) {
		Session session = getSession();
		String order = "";
		if (StringUtil.isEmpty(orderby) || "all".equals(orderby)) {
			order = " order by delegation_time desc";
		} else {
			order = " order by delegation_price " + orderby + ", delegation_time desc";
		}

		String fx = "";
		if (!StringUtil.isEmpty(direction) && !"all".equals(direction)) {
			fx = " and direction=" + direction;
		}

		StringBuffer sql = new StringBuffer("select * from " + coinPairName + "_pending_delegation where f_user_id =:fUserId " + fx + order);

		session.clear();
		session.flush();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("fUserId", uId);
		query.addEntity(SpotPendingDelegation.class);
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MarketPendingVO> getBuyList(String coinPairName) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select delegation_price as price,sum(undeal_count) as count from " + coinPairName
				+ "_pending_delegation where  direction=:direction and flag < 2 group by delegation_price ORDER BY delegation_price DESC limit 100");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(MarketPendingVO.class));
		query.setParameter("direction", Contents.DIRECTION_BUY);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MarketPendingVO> getSellList(String coinPairName) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select delegation_price as price,sum(undeal_count) as count from " + coinPairName
				+ "_pending_delegation where direction=:direction and flag < 2 group by delegation_price ORDER BY delegation_price ASC limit 100");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(MarketPendingVO.class));
		query.setParameter("direction", Contents.DIRECTION_SELL);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getBuyTopN(String coinPairName, int n) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select delegation_price,sum(undeal_count) AS delegation_count from " + coinPairName
				+ "_pending_delegation WHERE direction=:direction and flag < 2 GROUP BY delegation_price ORDER BY delegation_price DESC LIMIT " + n);
		SQLQuery query = session.createSQLQuery(sql.toString());

		query.setParameter("direction", Contents.DIRECTION_BUY);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getSellTopN(String coinPairName, int n) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("SELECT delegation_price,sum(undeal_count) AS delegation_count from " + coinPairName
				+ "_pending_delegation where direction=:direction and flag < 2 GROUP BY delegation_price ORDER BY delegation_price ASC LIMIT " + n);
		SQLQuery query = session.createSQLQuery(sql.toString());

		query.setParameter("direction", Contents.DIRECTION_SELL);

		return query.list();
	}

	@Override
	public int updateNumByID(long id, String coinPairName, BigDecimal dealNum, BigDecimal undealNum) {
		Session session = getSession();
		String sql = " update  " + coinPairName 
				+ "_pending_delegation set deal_count =deal_count+? ,undeal_count=undeal_count+? "
				+ "where delegation_id = ? and deal_count+?>=0 and undeal_count+?>=0";;
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal(0, dealNum);
		query.setBigDecimal(1, undealNum);
		query.setLong(2, id);
		query.setBigDecimal(3, dealNum);
		query.setBigDecimal(4, undealNum);
		
		return query.executeUpdate();
	}

	@Override
	public void moveFromCache(SpotPendingDelegationCache cache) {
		Session session = getSession();
		String sql = "insert into " + cache.getCoin_pair_name() + "_pending_delegation"
				+ " (delegation_time, coin_pair_id, coin_pair_name, direction, delegation_count,"
				+ "delegation_price,deal_count,undeal_count,f_user_id,pending_no,commission_rate,f_frozenremain) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, cache.getDelegation_time());
		query.setLong(1, cache.getCoin_pair_id());
		query.setString(2, cache.getCoin_pair_name());
		query.setInteger(3, cache.getDirection());
		query.setBigDecimal(4, cache.getDelegation_count());
		query.setBigDecimal(5, cache.getDelegation_price());
		query.setBigDecimal(6, cache.getDeal_count());
		query.setBigDecimal(7, cache.getUndeal_count());
		query.setLong(8, cache.getF_user_id());
		query.setLong(9, cache.getPending_no());
		query.setBigDecimal(10, cache.getCommission_rate());
		query.setBigDecimal(11, cache.getF_frozenremain());
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PagerModel findPagerModel(int direction, Long uid, String uemail, String coinPairName) {
		PagerModel pm = new PagerModel();
		String swhere = "";
		if (uid > 0) {
			swhere = " AND a.f_user_id=" + uid;
		}
		if (uemail != null && !uemail.isEmpty()) {
			swhere = swhere + " AND b.f_real_email='" + uemail + "'";
		}

		Session session = getSession();
		String sqlquery = "select delegation_id,delegation_time,coin_pair_name,direction,delegation_count,delegation_price,deal_count,undeal_count,b.f_user_id,b.f_user_email from "
				+ coinPairName
				+ "_pending_delegation a,bq_member b where a.f_user_id=b.f_user_id AND direction="
				+ direction
				+ swhere
				+ " ORDER BY delegation_price "
				+ (direction == Contents.DIRECTION_BUY ? "DESC" : "ASC")
				+ ",delegation_id DESC LIMIT :start,:page ";
		SQLQuery query = session.createSQLQuery(sqlquery);
		query.setParameter("start", SystemContext.getOffset());
		query.setParameter("page", SystemContext.getPageSize());
		query.addEntity(ViewSpotPendingManager.class);
		List<ViewSpotPendingManager> list = query.list();
		pm.setDatas(list);

		if (list != null && list.size() > 0) {
			if (uid > 0) {
				swhere = " AND f_user_id=" + uid;
			} else if (uemail != null && !uemail.isEmpty()) {
				uid = list.get(0).getF_user_id();
				swhere = " AND f_user_id=" + uid;
			}

			String sqlcount = "select count(1) from " + coinPairName + "_pending_delegation where direction=" + direction + swhere;
			SQLQuery querycount = session.createSQLQuery(sqlcount);
			pm.setTotal(ConvertUtil.toInt(querycount.uniqueResult().toString(), 0));
		}

		return pm;
	}

//	@Override
//	public boolean lock(String coinPairName, long id) {
//		Session session = getSession();
//		String sql = "update  " + coinPairName + "_pending_delegation set flag = 1  where delegation_id = ? and flag = 0";
//
//		SQLQuery query = session.createSQLQuery(sql);
//		query.setLong(0, id);
//		return query.executeUpdate() > 0;
//	}
//
//	@Override
//	public boolean unLock(String coinPairName, long id) {
//		Session session = getSession();
//		String sql = "update  " + coinPairName + "_pending_delegation set flag = 0  where delegation_id = ? and flag != 0";
//
//		SQLQuery query = session.createSQLQuery(sql);
//		query.setLong(0, id);
//		return query.executeUpdate() > 0;
//	}

	@Override
	public SpotPendingDelegation findPendingById(long orderId, String coinPairName) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation where delegation_id= :orderId ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("orderId", orderId);
		query.addEntity(SpotPendingDelegation.class);
		return (SpotPendingDelegation) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegation> findPendingBetweenPrice(String coinPairName, int direction, BigDecimal minPrice,
			BigDecimal maxPrice) {
		Session session = getSession();
		
		String sql = "select * from " + coinPairName + "_pending_delegation where direction = :direction "
				+ " and delegation_price >= :minPrice and delegation_price <= :maxPrice";

		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("direction", direction);
		query.setParameter("minPrice", minPrice);
		query.setParameter("maxPrice", maxPrice);
		query.addEntity(SpotPendingDelegation.class);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegation> findPendingBetweenPriceByUserId(long uid, String coinPairName, int direction,
			BigDecimal minPrice, BigDecimal maxPrice) {
		Session session = getSession();
		
		String sql = "select * from " + coinPairName + "_pending_delegation where direction = :direction and f_user_id = :uid"
				+ " and delegation_price > :minPrice and delegation_price < :maxPrice";

		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("direction", direction);
		query.setParameter("uid", uid);
		query.setParameter("minPrice", minPrice);
		query.setParameter("maxPrice", maxPrice);
		query.addEntity(SpotPendingDelegation.class);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegation> findPendingAtPriceByUser(long uid, String coinPairName, int direction,
			BigDecimal price) {
		Session session = getSession();
		
		String sql = "select * from " + coinPairName + "_pending_delegation where direction = :direction "
				+ " and f_user_id = :uid and delegation_price = :price";

		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("direction", direction);
		query.setParameter("uid", uid);
		query.setParameter("price", price);
		query.addEntity(SpotPendingDelegation.class);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpotPendingDelegation> findPendingByMemberTransInfo(String coinPairName, long uId, int direction) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + coinPairName + "_pending_delegation where f_user_id=" 
						+ uId + " and direction=" + direction);

		SQLQuery query = session.createSQLQuery(sql.toString());
		query.addEntity(SpotPendingDelegation.class);
		return query.list();
	}

	@Override
	public BigDecimal getDelegation(Long id, String coinPairName, int i) {
		Session session = getSession();
		String sql = "select ifnull(sum(undeal_count), 0) as undeal_count from " + coinPairName
				+ "_pending_delegation where f_user_id=? and coin_pair_name=? and direction=? ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);
		query.setString(1, coinPairName);
		query.setInteger(2, i);
		BigDecimal bg = (BigDecimal) query.uniqueResult();
		if (bg == null) {
			bg = new BigDecimal(0);
		}
		return bg;
	}

	@Override
	public BigDecimal findSumBuyFrozen(String coinPairName, long userId) {
		String sql="SELECT ifnull(SUM(t.undeal_count * t.delegation_price), 0) from "
				+ coinPairName + "_pending_delegation t where t.f_user_id = :userId and direction = 1";
				
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		
		return (BigDecimal) query.uniqueResult();
	}
	
	@Override
	public BigDecimal findSumSellFrozen(String coinPairName, long userId) {
		String sql="SELECT ifnull(SUM(t.undeal_count), 0) from "
				+ coinPairName + "_pending_delegation t where t.f_user_id = :userId and direction = 2";
				
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		
		return (BigDecimal) query.uniqueResult();
	}

	@Override
	public SpotPendingDelegation getPendingByPendingNo(String pendingNo, String coinPairName, long userId) {
		Session session = getSession();
		String sql = "select * from " + coinPairName + "_pending_delegation where pending_no= :pendingNo and f_user_id= :userId order by delegation_id desc limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("pendingNo", pendingNo);
		query.setParameter("userId", userId);
		query.addEntity(SpotPendingDelegation.class);
		return (SpotPendingDelegation) query.uniqueResult();
	}

	@Override
	public boolean insertCache(final List<SpotPendingDelegation> pendings, final String coinPairName) throws HibernateException {
		if (pendings.size() == 0) return false;
		
		final List<Long> rets = new ArrayList<>();
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				String sql = "insert into " + coinPairName + "_pending_delegation"
						+ " (delegation_time, coin_pair_id, coin_pair_name, direction, delegation_count,"
						+ "delegation_price,deal_count,undeal_count,f_user_id,pending_no,commission_rate,f_frozenremain) "
						+ "values ";

				long unix_time = getUnix_TIMESTAMP();
				String values = "";
				for (SpotPendingDelegation cache : pendings) {
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
}
