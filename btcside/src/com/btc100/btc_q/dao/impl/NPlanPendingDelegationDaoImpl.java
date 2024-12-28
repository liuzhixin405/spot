package com.btc100.btc_q.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.NPlanPendingDelegationDao;
import com.btc100.btc_q.entity.NPlanPendingDelegation;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Repository("nPlanPendingDelegationDao")
public class NPlanPendingDelegationDaoImpl extends BaseDaoImpl<NPlanPendingDelegation> implements NPlanPendingDelegationDao<NPlanPendingDelegation> {

	@Override
	public NPlanPendingDelegation insert(final String partition, final NPlanPendingDelegation obj) throws HibernateException {

		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				String sql = "insert into " + partition + "_" + obj.getF_code() + "_plan_pending_delegation"
						+ " (f_code, f_user_id, tri_price, delegation_price, delegation_count,"
						+ " delegation_time, multiple, direction, type, status, note, order_type, trade_type, transaction_member_id, stop_loss_type_id) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

				PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				long unix_time = getUnix_TIMESTAMP();
				psmt.setString(1, obj.getF_code());
				psmt.setLong(2, obj.getF_user_id());
				psmt.setBigDecimal(3, obj.getTri_price());
				psmt.setBigDecimal(4, obj.getDelegation_price());
				psmt.setBigDecimal(5, obj.getDelegation_count());
				psmt.setLong(6, unix_time);
				psmt.setInt(7, obj.getMultiple());
				psmt.setInt(8, obj.getDirection());
				psmt.setInt(9, obj.getType());
				psmt.setInt(10, obj.getStatus());
				psmt.setString(11, obj.getNote());
				psmt.setInt(12, obj.getOrder_type());
				psmt.setInt(13, obj.getTrade_type());
				psmt.setLong(14, obj.getTransaction_member_id());
				psmt.setInt(15, obj.getStop_loss_type_id());
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				long num = -1;
				if (results.next()) {
					num = results.getLong(1);
				}

				obj.setId(num);
				obj.setDelegation_time(unix_time);
			}

		});

		return obj;
	}

	@Override
	public int deletePending(String partition, String code, Long id) throws HibernateException {
		Session session = getSession();

		String sql = " delete from " + partition + "_" + code + "_plan_pending_delegation where id = ? ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, id);

		return query.executeUpdate();
	}

	@Override
	public int deletePendingAmonthAgo(String partition, String code, Long uid) throws HibernateException {
		Session session = getSession();


		String sql = " delete from " + partition + "_" + code + "_plan_pending_delegation where f_user_id = ?  AND delegation_time < ? AND status <> 0 ";

		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, uid);
		query.setLong(1, System.currentTimeMillis()/1000 - 60*60*24*30);//30天前

		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NPlanPendingDelegation> findByTypeAndLastPrice(String partition, String code, int type, BigDecimal lastPrice, int limit) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer(
				"select * from " + partition + "_" + code + "_plan_pending_delegation where type = " + type + " and status = 0");
		if (type == Contents.PLAN_PENDING_TYPE_GT) {
			sql.append(" and tri_price >= " + lastPrice);
		} else {
			sql.append(" and tri_price <= " + lastPrice);
		}
		
		sql.append(" order by delegation_time asc limit 0," + limit);

		SQLQuery query = session.createSQLQuery(sql.toString());
		query.addEntity(NPlanPendingDelegation.class);
		return query.list();
	}

	@Override
	public int updateStatusAndNote(String partition, String code, Long id, int status, String note) {
		Session session = getSession();
		String sql = " update  " + partition + "_" + code + "_plan_pending_delegation " + " set status = ?, note = ? where id = ?";
		
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, status);
		query.setString(1, note);
		query.setLong(2, id);

		return query.executeUpdate();
	}

	@Override
	public int updateStatusAndNoteByMid(String partition, String code, Long uid, Long mid, int status, String note) {
		Session session = getSession();
		String sql = " update  " + partition + "_" + code + "_plan_pending_delegation " + " set status = ?, note = ? where transaction_member_id = ? and f_user_id = ? and stop_loss_type_id > 0";

		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, status);
		query.setString(1, note);
		query.setLong(2, mid);
		query.setLong(3, uid);

		return query.executeUpdate();
	}

	@Override
	public int updateStopWinAndLoss(String partition, long id, NPlanPendingDelegation nPendingDelegation) {
		Session session = getSession();
		String sql = " update " + partition + "_" + nPendingDelegation.getF_code() + "_plan_pending_delegation set order_type = ?, tri_price = ?, delegation_price = ?, delegation_count = ?, delegation_time = UNIX_TIMESTAMP(), multiple = ?, direction = ?, type = ?, trade_type = ?, status = ?, note = ? where " +
				" id = ?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, nPendingDelegation.getOrder_type());
		query.setBigDecimal(1, nPendingDelegation.getTri_price());
		query.setBigDecimal(2, nPendingDelegation.getDelegation_price());
		query.setBigDecimal(3, nPendingDelegation.getDelegation_count());
		query.setInteger(4, nPendingDelegation.getMultiple());
		query.setInteger(5, nPendingDelegation.getDirection());
		query.setInteger(6, nPendingDelegation.getType());
		query.setInteger(7, nPendingDelegation.getTrade_type());
		query.setInteger(8, nPendingDelegation.getStatus());
		query.setString(9, nPendingDelegation.getNote());
		query.setLong(10, id);
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NPlanPendingDelegation> findByUserId(String partition, String code, long fUserId, int direction, int status) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + code + "_plan_pending_delegation where 1 = 1");
		if (fUserId > 0) {
			sql.append(" and f_user_id = :fUserId");
		}
		if (direction > 0) {
			sql.append(" and direction = :direction");
		}
		if (status >= 0) {
			sql.append(" and status = :status");
		}
		
		sql.append(" order by case when status=0 then 0 else 1 end asc, delegation_time desc ");
		
		SQLQuery query = session.createSQLQuery(sql.toString());
		
		if (fUserId > 0) {
			query.setParameter("fUserId", fUserId);
		}
		if (direction > 0) {
			query.setParameter("direction", direction);
		}
		if (status >= 0) {
			query.setParameter("status", status);
		}
		query.addEntity(NPlanPendingDelegation.class);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NPlanPendingDelegation> findByUserIdDirection(String partition, int direction, String code, long fUserId) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer("select * from " + partition + "_" + code + "_plan_pending_delegation where f_user_id = "
				+ fUserId + " and status <> 1 and status <> 3");
		if (direction > 0) {
			sql.append(" and direction = " + direction );
		}
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.addEntity(NPlanPendingDelegation.class);
		return query.list();
	}

	@Override
	public NPlanPendingDelegation getById(String partition, long id, String code, long userId) {
		Session session = getSession();
		StringBuffer sql = new StringBuffer(
				"select * from " + partition + "_" + code + "_plan_pending_delegation where id = " + id);
		if (userId > 0) {
			sql.append(" and f_user_id = " + userId);
		}
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.addEntity(NPlanPendingDelegation.class);
		return (NPlanPendingDelegation) query.uniqueResult();
	}

	@Override
	public NPlanPendingDelegation getByStatusDirectionAndPositionId(String partition, String code, long fUserId, int status, long transactionMemberId, int stoplossId) {
		Session session = getSession();
		String sql = "select * from " + partition + "_" + code + "_plan_pending_delegation where 1=1 ";

		if (fUserId > 0) {
			sql += "and f_user_id = :userId ";
		}
		if (status >= 0) {
			sql += "and status = :status ";
		}
		if (transactionMemberId > 0) {
			sql += "and transaction_member_id = :transactionMemberId ";
		}
		if (stoplossId > 0) {
			sql += "and stop_loss_type_id = :stoplossId ";
		}
		sql += "limit 0, 1";

		SQLQuery query = session.createSQLQuery(sql);
		if (fUserId > 0) {
			query.setParameter("userId", fUserId);
		}
		if (status >= 0) {
			query.setParameter("status", status);
		}
		if (transactionMemberId > 0) {
			query.setParameter("transactionMemberId", transactionMemberId);
		}
		if (stoplossId > 0) {
			query.setParameter("stoplossId", stoplossId);
		}
		query.addEntity(NPlanPendingDelegation.class);
		return (NPlanPendingDelegation) query.uniqueResult();
	}

	@Override
	public PagerModel getPagerModel(String partition, String code, Long fUserId, int direction, int status, int type) {
		Session session = getSession();
		
		String sql = "select * from " + partition + "_" + code + "_plan_pending_delegation WHERE 1 = 1";
		String countSql = "select count(1) from " + partition + "_" + code + "_plan_pending_delegation WHERE 1 = 1";
		if (fUserId > 0) {
			sql += " and f_user_id = :fUserId";
			countSql += " and f_user_id = :fUserId";
		}
		
		if (direction > 0) {
			sql += " and direction = :direction";
			countSql += " and direction = :direction";
		}
		
		if (status >= 0) {
			sql += " and status = :status";
			countSql += " and status = :status";
		}
		
		if (type > 0) {
			sql += " and type = :type";
			countSql += " and type = :type";
		}
		
		sql += " ORDER BY delegation_time DESC LIMIT " + SystemContext.getOffset() + "," + SystemContext.getPageSize();
		
		SQLQuery query = session.createSQLQuery(sql);
		SQLQuery countQuery = session.createSQLQuery(countSql);
		
		if (fUserId > 0) {
			countQuery.setParameter("fUserId", fUserId);
			query.setParameter("fUserId", fUserId);
		}
		
		if (direction > 0) {
			query.setParameter("direction", direction);
			countQuery.setParameter("direction", direction);
		}
		
		if (status >= 0) {
			query.setParameter("status", status);
			countQuery.setParameter("status", status);
		}

		if (type > 0) {
			query.setParameter("type", type);
			countQuery.setParameter("type", type);
		}
		query.addEntity(NPlanPendingDelegation.class);
		
		PagerModel pm = new PagerModel();
		pm.setDatas(query.list());
		pm.setTotal(ConvertUtil.toInt(countQuery.uniqueResult(), 0));
		return pm;
	}

}
