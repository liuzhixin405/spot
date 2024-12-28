package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.PendingDelegationDao;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.entity.ViewPendingManager;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.MarketPendingVO;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Repository("pendingDelegationDao")
public class PendingDelegationDaoImpl<T> extends BaseDaoImpl<T> implements PendingDelegationDao<T> {

	public int deletePending(String code, Long id) throws HibernateException {
		Session session = getSession();

		String sql = " delete from " + code + "_pending_delegation where delegation_id = ? ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);

		return query.executeUpdate();
	}

	public int deletePending(String code, int priority, int direction) throws HibernateException {
		Session session = getSession();

		String sql = " delete from " + code + "_pending_delegation WHERE direction=? AND priority=?";

		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, direction);
		query.setInteger(1, priority);

		return query.executeUpdate();
	}

	public void delete() {

	}

	public List<com.btc100.btc_q.entity.PendingDelegation> query(String code, Map<String, Object> params) throws HibernateException {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + code + "_pending_delegation where 1=1 ");
		if (params.containsKey("direction")) {
			sql.append(" and direction = :direction ");

		}
		SQLQuery query = session.createSQLQuery(sql.toString());

		if (params.containsKey("direction")) {
			sql.append(" and direction = :direction ");

			query.setParameter("direction", params.get("direction"));
		}

		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);
		return query.list();
	}

	public com.btc100.btc_q.entity.PendingDelegation getBuy1(String code) {
		Session session = getSession();
		String sql = "select * from " + code + "_pending_delegation where direction=" + Contents.DIRECTION_BUY
				+ "  AND flag<>2 order by delegation_price desc,f_enforce_price desc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);

		return (com.btc100.btc_q.entity.PendingDelegation) query.uniqueResult();
	}

	public com.btc100.btc_q.entity.PendingDelegation getSell1(String code) {
		Session session = getSession();
		String sql = "select * from " + code + "_pending_delegation where direction=" + Contents.DIRECTION_SELL
				+ "  AND flag<>2 order by delegation_price asc,f_enforce_price desc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);

		return (com.btc100.btc_q.entity.PendingDelegation) query.uniqueResult();
	}
	
	public List<com.btc100.btc_q.entity.PendingDelegation> getBuyTop(String code, BigDecimal minPrice) {
		Session session = getSession();
		String sql = "select * from " + code + "_pending_delegation where direction=" + Contents.DIRECTION_BUY + " AND delegation_price >= :price"
				+ " AND flag<>2 order by delegation_price desc,f_enforce_price desc,delegation_time asc ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("price", minPrice);
		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);

		return query.list();
	}

	public List<com.btc100.btc_q.entity.PendingDelegation> getSellTop(String code, BigDecimal maxPrice) {
		Session session = getSession();
		String sql = "select * from " + code + "_pending_delegation where direction=" + Contents.DIRECTION_SELL + " AND delegation_price <= :price"
				+ " AND flag<>2 order by delegation_price asc,f_enforce_price desc,delegation_time asc ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal("price", maxPrice);
		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);

		return query.list();
	}

	public com.btc100.btc_q.entity.PendingDelegation getBuy1(String code, long fUserId) {
		Session session = getSession();
		String sql = "select * from " + code + "_pending_delegation where direction=" + Contents.DIRECTION_BUY
				+ " and f_user_id != ? order by delegation_price desc,f_enforce_price desc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, fUserId);
		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);

		return (com.btc100.btc_q.entity.PendingDelegation) query.uniqueResult();
	}

	public com.btc100.btc_q.entity.PendingDelegation getSell1(String code, long fUserId) {
		Session session = getSession();
		String sql = "select * from " + code + "_pending_delegation where direction=" + Contents.DIRECTION_SELL
				+ " and f_user_id != ? order by delegation_price asc,f_enforce_price desc,delegation_time asc limit 0,1 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, fUserId);
		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);

		return (com.btc100.btc_q.entity.PendingDelegation) query.uniqueResult();
	}

	public List<com.btc100.btc_q.vo.Top5VO> getBuyTop5(String transactionCode) {
		Session session = getSession();
		// StringBuffer sql = new
		// StringBuffer("select * from "+transactionCode+"_pending_delegation where  direction=:direction order by delegation_price desc,delegation_time asc limit 0,5 ");
		StringBuffer sql = new StringBuffer("SELECT delegation_price as price ,sum(undeal_count) AS countNum, min(priority) AS priority from " + transactionCode
				+ "_pending_delegation where direction=:direction and flag < 2 GROUP BY delegation_price ORDER BY delegation_price DESC LIMIT 20 ");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(com.btc100.btc_q.vo.Top5VO.class));

		query.setParameter("direction", Contents.DIRECTION_BUY);

		return query.list();
	}

	public List<Object[]> getBuyTopN(String bqCode, int n) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select delegation_price,sum(undeal_count) AS delegation_count, min(priority) AS priority from " + bqCode
				+ "_pending_delegation WHERE direction=:direction and flag < 2 GROUP BY delegation_price ORDER BY delegation_price DESC LIMIT " + n);
		SQLQuery query = session.createSQLQuery(sql.toString());

		query.setParameter("direction", Contents.DIRECTION_BUY);

		return query.list();
	}

	public List<com.btc100.btc_q.vo.Top5VO> getSellTop5(String transactionCode) {
		Session session = getSession();
		// StringBuffer sql = new
		// StringBuffer("select * from "+transactionCode+"_pending_delegation where  direction=:direction order by delegation_price asc,delegation_time asc limit 0,5 ");
		StringBuffer sql = new StringBuffer("SELECT delegation_price as price ,sum(undeal_count) AS countNum, min(priority) AS priority from " + transactionCode
				+ "_pending_delegation where  direction=:direction and flag < 2 GROUP BY delegation_price ORDER BY delegation_price ASC LIMIT 20 ");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(com.btc100.btc_q.vo.Top5VO.class));

		query.setParameter("direction", Contents.DIRECTION_SELL);
		return query.list();
	}

	public List<Object[]> getSellTopN(String bqCode, int n) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("SELECT delegation_price,sum(undeal_count) AS delegation_count, min(priority) AS priority from " + bqCode
				+ "_pending_delegation where  direction=:direction and flag < 2 GROUP BY delegation_price ORDER BY delegation_price ASC LIMIT " + n);
		SQLQuery query = session.createSQLQuery(sql.toString());

		query.setParameter("direction", Contents.DIRECTION_SELL);

		return query.list();
	}

	public List<com.btc100.btc_q.entity.PendingDelegation> queryByUserId(String code, long fUserId) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + code
				+ "_pending_delegation where f_user_id =:fUserId and priority!=3 order by delegation_time desc ");

		SQLQuery query = session.createSQLQuery(sql.toString());

		query.setParameter("fUserId", fUserId);

		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);
		return query.list();
	}

	public List<com.btc100.btc_q.entity.PendingDelegation> queryByTypeUId(String code, String direction, String priority, long fUserId, String orderby) {
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

		String dz = "";
		if (!StringUtil.isEmpty(priority) && !"all".equals(priority)) {
			dz = " and priority=" + priority;
		} else {
			dz = " and priority!=3";
		}

		StringBuffer sql = new StringBuffer("select * from " + code + "_pending_delegation where f_user_id =:fUserId " + dz + fx + order);

		SQLQuery query = session.createSQLQuery(sql.toString());

		query.setParameter("fUserId", fUserId);

		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);
		return query.list();
	}

	public List<com.btc100.btc_q.entity.PendingDelegation> queryByUserId(int direction, String code, long fUserId) {
		Session session = getSession();

		// StringBuffer sql = null;
		// select * from btc1408_pending_delegation where f_user_id =108 and
		// ((direction=1 AND priority=1)or (direction=2 AND priority=2) or
		// (direction=2 AND priority=3));
		// select * from btc1408_pending_delegation where f_user_id =120 and
		// ((direction=2 AND priority=1)or (direction=1 AND priority=2) or
		// (direction=1 AND priority=3));
		// if(direction == Contents.DIRECTION_BUY){
		// sql = new
		// StringBuffer("select * from "+code+"_pending_delegation where f_user_id =:fUserId and ((direction=1 AND priority=1)or (direction=2 AND priority=2) or (direction=2 AND priority=3)) ");
		// }else{
		// sql = new
		// StringBuffer("select * from "+code+"_pending_delegation where f_user_id =:fUserId and ((direction=2 AND priority=1)or (direction=1 AND priority=2) or (direction=1 AND priority=3)) ");
		// }

		String sql = "select * from " + code + "_pending_delegation where f_user_id =:fUserId";
		if (direction > 0) {
			sql = sql + " and direction=:direction ";
		}

		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("fUserId", fUserId);

		if (direction > 0) {
			query.setParameter("direction", direction);
		}

		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);
		return query.list();
	}

	public List<MarketPendingVO> getBuyList(String code0) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select delegation_price as price,sum(undeal_count) as count from " + code0
				+ "_pending_delegation where  direction=:direction and flag < 2 group by delegation_price  ORDER BY delegation_price DESC limit 100");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(MarketPendingVO.class));
		query.setParameter("direction", Contents.DIRECTION_BUY);
		return query.list();
	}

	public List<MarketPendingVO> getSellList(String code0) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select delegation_price as price,sum(undeal_count) as count from " + code0
				+ "_pending_delegation where direction=:direction and flag < 2 group by delegation_price ORDER BY delegation_price ASC limit 100");
		Query query = session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(MarketPendingVO.class));
		query.setParameter("direction", Contents.DIRECTION_SELL);
		return query.list();
	}

	public com.btc100.btc_q.entity.PendingDelegation queryByUserIdOrderId(String code, long userId, long orderId) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + code + "_pending_delegation where f_user_id = :fUserId and delegation_id= :delegationId ");

		SQLQuery query = session.createSQLQuery(sql.toString());

		query.setParameter("fUserId", userId);
		query.setParameter("delegationId", orderId);

		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);
		return (com.btc100.btc_q.entity.PendingDelegation) query.uniqueResult();
	}

	public void updateDealNum(com.btc100.btc_q.entity.PendingDelegation sell1) {
		Session session = getSession();

		String sql = " update  " + sell1.getTransaction_code() + "_pending_delegation set deal_count =? ,undeal_count=? where delegation_id = ? ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal(0, sell1.getDeal_count());
		query.setBigDecimal(1, sell1.getUndeal_count());
		query.setLong(2, sell1.getDelegation_id());

		query.executeUpdate();
	}

	public int updateNumByID(long id, String scode, BigDecimal dealNum, BigDecimal undealNum, BigDecimal fromremain, BigDecimal frozenremain, BigDecimal coefficient) {
		Session session = getSession();
		String sql = " update  " + scode;
		sql = sql
				+ "_pending_delegation set deal_count =deal_count+? ,undeal_count=undeal_count+? , f_fromremain=f_fromremain+?, f_frozenremain=f_frozenremain+?, deposit=undeal_count*delegation_price*?/multiple "
				+ "where delegation_id = ? and deal_count+?>=0 and undeal_count+?>=0";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal(0, dealNum);
		query.setBigDecimal(1, undealNum);
		query.setBigDecimal(2, fromremain);
		query.setBigDecimal(3, frozenremain);
		query.setBigDecimal(4, coefficient);
		query.setLong(5, id);
		query.setBigDecimal(6, dealNum);
		query.setBigDecimal(7, undealNum);
		
		return query.executeUpdate();
	}

	public int updateNumByID(long id, String scode, BigDecimal dealNum, BigDecimal deposit) {
		Session session = getSession();
		String sql = " update  " + scode
				+ "_pending_delegation set deal_count =deal_count+? ,undeal_count=undeal_count-? , deposit=deposit+? where delegation_id = ? ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setBigDecimal(0, dealNum);
		query.setBigDecimal(1, dealNum);
		query.setBigDecimal(2, deposit);
		query.setLong(3, id);

		return query.executeUpdate();
	}

	public void moveFromCache(PendingDelegationCache buycache1) {
		Session session = getSession();
		String sql = "insert into " + buycache1.getTransaction_code() + "_pending_delegation"
				+ " (delegation_time, transaction_id, transaction_code, direction, delegation_count,"
				+ "delegation_price,deal_count,undeal_count,multiple,deposit,priority,f_user_id,f_fromremain,f_enforce_price,pending_no,commission_rate,f_frozenremain) "
				+ "values (?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?)";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, buycache1.getDelegation_time());
		query.setLong(1, buycache1.getTransaction_id());
		query.setString(2, buycache1.getTransaction_code());
		query.setInteger(3, buycache1.getDirection());
		query.setBigDecimal(4, buycache1.getDelegation_count());
		query.setBigDecimal(5, buycache1.getDelegation_price());
		query.setBigDecimal(6, buycache1.getDeal_count());
		query.setBigDecimal(7, buycache1.getUndeal_count());
		query.setInteger(8, buycache1.getMultiple());
		query.setBigDecimal(9, buycache1.getDeposit());
		query.setInteger(10, buycache1.getPriority());
		query.setLong(11, buycache1.getF_user_id());
		query.setBigDecimal(12, buycache1.getF_fromremain());
		query.setBigDecimal(13, buycache1.getF_enforce_price());
		query.setLong(14, buycache1.getPending_no());
		query.setBigDecimal(15, buycache1.getCommission_rate());
		query.setBigDecimal(16, buycache1.getF_frozenremain());
		query.executeUpdate();
	}

	public PagerModel getPagerModel(int iType, Long uid, String uemail, String code) {
		PagerModel pm = new PagerModel();

		String swhere = "";
		if (uid > 0) {
			swhere = " AND a.f_user_id=" + uid;
		}
		if (uemail != null && !uemail.isEmpty()) {
			swhere = swhere + " AND b.f_user_email='" + uemail + "'";
		}

		Session session = getSession();
		String sqlquery = "select delegation_id,delegation_time,transaction_code,direction,delegation_count,delegation_price,deal_count,undeal_count,multiple,deposit,priority,b.f_user_id,b.f_user_email from "
				+ code
				+ "_pending_delegation a,bq_member b where a.f_user_id=b.f_user_id AND direction="
				+ iType
				+ swhere
				+ " ORDER BY delegation_price "
				+ (iType == Contents.DIRECTION_BUY ? "DESC" : "ASC")
				+ ",delegation_id DESC LIMIT "
				+ SystemContext.getOffset()
				+ ","
				+ SystemContext.getPageSize();
		SQLQuery query = session.createSQLQuery(sqlquery);
		query.addEntity(ViewPendingManager.class);
		List<ViewPendingManager> list = query.list();
		pm.setDatas(list);

		if (list != null && list.size() > 0) {
			if (uid > 0) {
				swhere = " AND f_user_id=" + uid;
			} else if (uemail != null && !uemail.isEmpty()) {
				uid = list.get(0).getF_user_id();
				swhere = " AND f_user_id=" + uid;
			}

			// String sqlsum = "select sum(undeal_count) count from " + code +
			// "_pending_delegation where direction=" + iType + swhere;
			// SQLQuery querysum = session.createSQLQuery(sqlsum);
			// pm.setFlag(querysum.uniqueResult().toString());

			String sqlcount = "select count(1) from " + code + "_pending_delegation where direction=" + iType + swhere;
			SQLQuery querycount = session.createSQLQuery(sqlcount);
			pm.setTotal(ConvertUtil.toInt(querycount.uniqueResult().toString(), 0));
		}

		return pm;
	}

	public BigDecimal getGain(String bqCode) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("SELECT IFNULL(sum((undeal_count/multiple)-f_fromremain),0) AS delegation_count from " + bqCode
				+ "_pending_delegation where priority=1 AND (undeal_count/multiple)-f_fromremain>0");
		SQLQuery query = session.createSQLQuery(sql.toString());
		Object object = query.uniqueResult();
		if (!StringUtil.isEmpty(object)) {
			return new BigDecimal(object.toString());
		}

		return BigDecimal.ZERO;
	}

	public BigDecimal getLoss(BigDecimal lastPrice, String bqCode, BigDecimal coefficient) {// 当前价.....
		BigDecimal total = BigDecimal.ZERO;

		Session session = getSession();
		StringBuffer sql_sell = new StringBuffer("SELECT IFNULL(sum(((" + lastPrice + "-delegation_price)*undeal_count)/" + coefficient
				+ "),0) AS delegation_count from " + bqCode + "_pending_delegation where priority=3 AND direction=2");
		SQLQuery query_sell = session.createSQLQuery(sql_sell.toString());
		Object object_sell = query_sell.uniqueResult();
		if (!StringUtil.isEmpty(object_sell)) {
			total = new BigDecimal(object_sell.toString());
		}

		StringBuffer sql = new StringBuffer("SELECT IFNULL(sum(((delegation_price-" + lastPrice + ")*undeal_count)/" + coefficient
				+ "),0) AS delegation_count from " + bqCode + "_pending_delegation where priority=3 AND direction=1");
		SQLQuery query = session.createSQLQuery(sql.toString());
		Object object = query.uniqueResult();
		if (!StringUtil.isEmpty(object)) {
			total = total.add(new BigDecimal(object.toString()));
		}

		return total;
	}

	public List<com.btc100.btc_q.entity.PendingDelegation> queryPCDelegations(int direction, String code, Long fUserId, int multiple) {
		Session session = getSession();

		StringBuffer sql = new StringBuffer("select * from " + code + "_pending_delegation where f_user_id =" + fUserId + " and direction=" + direction
				+ " AND priority=2 and multiple=" + multiple);

		SQLQuery query = session.createSQLQuery(sql.toString());

		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);
		return query.list();
	}

	@Override
	public PendingDelegation findPendingById(long iorderId, String bqCode, long userId) {
		Session session = getSession();
		String sql = "select * from " + bqCode + "_pending_delegation where delegation_id= :orderId and f_user_id= :userId";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("orderId", iorderId);
		query.setParameter("userId", userId);
		query.addEntity(PendingDelegation.class);
		return (PendingDelegation) query.uniqueResult();
	}

	@Override
	public PendingDelegation findPendingById(long iorderId, String bqCode) {
		Session session = getSession();
		String sql = "select * from " + bqCode + "_pending_delegation where delegation_id= :orderId ";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("orderId", iorderId);
		query.addEntity(PendingDelegation.class);
		return (PendingDelegation) query.uniqueResult();
	}
	
	@Override
	public PendingDelegation getPendingByPendingNo(String pendingNo, String bqCode, long userId) {
		Session session = getSession();
		String sql = "select * from " + bqCode + "_pending_delegation where pending_no= :pendingNo and f_user_id= :userId order by delegation_id desc limit 1";
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("pendingNo", pendingNo);
		query.setParameter("userId", userId);
		query.addEntity(PendingDelegation.class);
		return (PendingDelegation) query.uniqueResult();
	}
	
	@Override
	public List<PendingDelegation> findPendingBetweenPrice(String code, int direction, BigDecimal minPrice, BigDecimal maxPrice) {
		Session session = getSession();
		
		String sql = "select * from " + code + "_pending_delegation where direction = :direction "
				+ " and delegation_price >= :minPrice and delegation_price <= :maxPrice";

		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("direction", direction);
		query.setParameter("minPrice", minPrice);
		query.setParameter("maxPrice", maxPrice);
		query.addEntity(PendingDelegation.class);

		return query.list();
	}
	
	@Override
	public List<PendingDelegation> findPendingBetweenPriceByUserId(long uid, String code, int direction, BigDecimal minPrice, BigDecimal maxPrice) {
		Session session = getSession();
		
		String sql = "select * from " + code + "_pending_delegation where direction = :direction and f_user_id = :uid"
				+ " and delegation_price > :minPrice and delegation_price < :maxPrice";

		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("direction", direction);
		query.setParameter("uid", uid);
		query.setParameter("minPrice", minPrice);
		query.setParameter("maxPrice", maxPrice);
		query.addEntity(PendingDelegation.class);

		return query.list();
	}
	
	public List<PendingDelegation> findPendingAtPriceByUser(long uid, String code, int direction, BigDecimal price) {
		Session session = getSession();
		
		String sql = "select * from " + code + "_pending_delegation where direction = :direction "
				+ " and f_user_id = :uid and delegation_price = :price";

		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("direction", direction);
		query.setParameter("uid", uid);
		query.setParameter("price", price);
		query.addEntity(PendingDelegation.class);

		return query.list();
	}

	@Override
	public List<PendingDelegation> findBCPendingOverOneMinute(String code, int direction) {
		Long thisTime = getUnix_TIMESTAMP();
		Session session = getSession();
		String sql = "select * from " + code + "_pending_delegation where direction = :direction "
				+ " and priority = :priority and delegation_time+60 <= :time";

		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("direction", direction);
		query.setParameter("priority", Contents.PRIORITY_BC);
		query.setParameter("time", thisTime);
		query.addEntity(PendingDelegation.class);

		return query.list();
	}

	@Override
	public List<PendingDelegation> findPendingByMemberTransInfo(String code, long userId, int priority,
			int direction, int multiple) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + code + "_pending_delegation where f_user_id=" + userId + " and direction=" + direction
				+ " AND priority=" + priority + " and multiple=" + multiple + " order by delegation_price");
		
		if (direction == Contents.DIRECTION_BUY ) {
			sql.append(" desc");
		}

		SQLQuery query = session.createSQLQuery(sql.toString());
		query.addEntity(com.btc100.btc_q.entity.PendingDelegation.class);
		return query.list();
	}

//	@Override
//	public boolean lock(String code, long id) {
//		Session session = getSession();
//		String sql = "update  " + code + "_pending_delegation set flag = 1  where delegation_id = ? and flag = 0";
//
//		SQLQuery query = session.createSQLQuery(sql);
//		query.setLong(0, id);
//		return query.executeUpdate() > 0;
//	}
//
//	@Override
//	public boolean unLock(String code, long id) {
//		Session session = getSession();
//		String sql = "update  " + code + "_pending_delegation set flag = 0  where delegation_id = ? and flag != 0";
//
//		SQLQuery query = session.createSQLQuery(sql);
//		query.setLong(0, id);
//		return query.executeUpdate() > 0;
//	}

	@Override
	public Object[] findSumDepositAndFeeByUser(String code, long userId, BigDecimal coefficient) {
		String sql="SELECT ifnull(SUM(t.undeal_count * :coefficient * t.delegation_price / t.multiple ), 0) AS amount, ifnull(SUM(t.delegation_price * :coefficient * t.undeal_count * commission_rate), 0) AS fee from " 
				+ code + "_pending_delegation t where t.f_user_id = :userId and t.priority = 1";
				
		Session session = getSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("coefficient", coefficient);
		
		return (Object[]) query.uniqueResult();
	}

}
