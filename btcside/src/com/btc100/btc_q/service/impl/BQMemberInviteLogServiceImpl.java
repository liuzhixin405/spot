package com.btc100.btc_q.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.BQMemberDao;
import com.btc100.btc_q.dao.BQMemberInviteLogDao;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberInviteLog;
import com.btc100.btc_q.service.BQMemberInviteLogService;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("bqMemberInviteLogService")
public class BQMemberInviteLogServiceImpl implements BQMemberInviteLogService {

	@Autowired
	private BQMemberInviteLogDao<BQMemberInviteLog> bqMemberInviteLogDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberDao<BQMember> bqMemberDao;

	public void save(BQMemberInviteLog bqil) {
		bqMemberInviteLogDao.save(bqil);
	}

	@Override
	public void update(BQMemberInviteLog log) {
		String hql = "update BQMemberInviteLog set invite_uid = :inviteId where f_user_id = :userId";
		Map<String, Object> params = new HashMap<>();
		params.put("inviteId", log.getInvite_uid());
		params.put("userId", log.getF_user_id());
		bqMemberInviteLogDao.updateCondition(hql, params);
	}

	public long getTime() {
		return bqMemberInviteLogDao.getUnix_TIMESTAMP();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PagerModel readPagerByUserId(long id) {
		String sql = "SELECT m.f_user_id, m.f_user_email, l.level_name, m.f_user_credits FROM bq_member_level l LEFT JOIN bq_member m ON l.level_id = m.f_user_level_id "
				+ "WHERE m.f_user_id IN ( SELECT f_user_id FROM bq_member_invite_log a WHERE a.invite_uid = :uid )";
		StringBuilder listsql = new StringBuilder(sql);
		listsql.append(" LIMIT ").append(SystemContext.getOffset()).append(",").append(SystemContext.getPageSize());
		String countSql = "SELECT count(*) FROM bq_member_invite_log a WHERE a.invite_uid = :uid";
		
		Session session = bqMemberInviteLogDao.getSession();
		SQLQuery query = session.createSQLQuery(listsql.toString());
		query.setParameter("uid", id);
		List<Object[]> list = query.list();
		
		session = bqMemberInviteLogDao.getSession();
		SQLQuery countQuery = session.createSQLQuery(countSql.toString());
		countQuery.setParameter("uid", id);
		BigInteger total = (BigInteger) countQuery.uniqueResult();
		PagerModel pm = new PagerModel();
		pm.setDatas(list);
		pm.setTotal(total.intValue());
		
		return pm;
	}

	@Override
	public long findInviterByUserId(long invited) {
		Session session = bqMemberInviteLogDao.getSession();
		String sql = "select * from bq_member_invite_log where f_user_id=?";
		SQLQuery query = session.createSQLQuery(sql);
		query.setLong(0, invited);
		query.addEntity(BQMemberInviteLog.class);
		
		BQMemberInviteLog invitLog = (BQMemberInviteLog) query.uniqueResult();
		if (invitLog != null) return invitLog.getInvite_uid();
		
		return 0L;
	}

	@Override
	public BQMemberInviteLog readByUserId(long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		return bqMemberInviteLogDao.get("From BQMemberInviteLog where f_user_id = :userId", params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> readByUserAndTime(long uid, long from, String direct, long startTime, long endTime,
			int limit) {
		StringBuffer inviteSql = new StringBuffer(" select t.f_user_id from (SELECT * FROM bq_member_invite_log WHERE invite_uid = :uid ");
		if (startTime > 0) {
			inviteSql.append(" and dateline >= :startTime");
		}
		
		if (endTime > 0) {
			inviteSql.append(" and dateline <= :endTime");
		}
		
		if (from >= 0 && "prev".equals(direct)) {
			inviteSql.append(" and f_user_id " + ">=" + " :from");
		} else if (from >= 0 && "next".equals(direct)) {
			inviteSql.append(" and f_user_id " + "<=" + " :from");
		}
		
		inviteSql.append(" ORDER BY f_user_id DESC ");
		
		if (limit > 0) {
			inviteSql.append(" limit " + limit);	
		}
		inviteSql.append(") as t");
		
		String sql = " SELECT m.f_user_id, m.f_user_email, l.level_name, m.f_user_credits FROM bq_member_level l LEFT JOIN bq_member m ON l.level_id = m.f_user_level_id "
				+ "WHERE m.f_user_id IN (" + inviteSql.toString() + ")";
		
		Session session = bqMemberInviteLogDao.getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", uid);
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
		List<Object[]> list = query.list();
		return list;
	}

	@Override
	public long readCountByUserId(long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String hql = "select count(*) From BQMemberInviteLog where invite_uid = :userId";
		return bqMemberInviteLogDao.count(hql, params);
	}

	@Override
	public Long readAllCount(long userId) {
		Long count = 0L;
		try {
			Long cacheCount = spyMemcachedClient.get(BTCQMemecacheKeys.INVITE_COUNT + userId);
			if (cacheCount == null) {
				count = readCountByUserId(userId);
				spyMemcachedClient.set(BTCQMemecacheKeys.INVITE_COUNT, BTCQMemecacheKeys.TIMES.ONE_HOUR, count);
			} else {
				count = cacheCount;
			}
			
			if (count > 0L) {
				List<Object[]> list = readByUserAndTime(userId, 0, "", 0, 0, 0);
				if (list.size() > 0) {
					for (Object[] obj : list) {
						long uId = (Integer) obj[0];
						count += readAllCount(uId);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return count;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> readAllByUserId(long userId) {
		StringBuffer sql = new StringBuffer("SELECT c.f_user_id, c.f_user_email, d.level_name FROM bq_member_invite_log b ");
		sql.append("LEFT JOIN bq_member c ON b.f_user_id = c.f_user_id ");
		sql.append("LEFT JOIN bq_member_level d ON c.f_user_level_id = d.level_id ");
		sql.append("WHERE b.invite_uid = :uid");
		
		Session session = bqMemberInviteLogDao.getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", userId);
		List<Object[]> list = query.list();
		
		return list;
	}
	
	@Override
	public BQMemberInviteLog readAgent(long userId) {
		BQMemberInviteLog log = readByUserId(userId);
		if (log != null) {
			BQMember inviter = bqMemberDao.get(BQMember.class, log.getInvite_uid());
			if (inviter != null) {
				if (inviter.getF_user_identity() == 1) {
					return log;
				} else {
					BQMemberInviteLog inviteLog = readAgent(inviter.getF_user_id());
					if (inviteLog != null) {
						return inviteLog;
					}
				}
			}
		}
		return null;
	}

	@Override
	public List<Integer> readAllInviteesNotAgent(long userId, int oms) {
		List<Integer> list = new ArrayList<Integer>();
		List<Integer> firstList = readFirstRankNotAgent(userId, oms);
		if (firstList.size() > 0) {
			list.addAll(firstList);
			for (Integer uid : firstList) {
				list.addAll(readAllInviteesNotAgent(uid.longValue(), oms));
			}
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> readFirstRankNotAgent(long userId, int oms) {
		StringBuffer sql = new StringBuffer("SELECT a.f_user_id FROM bq_member a ");
		sql.append("LEFT JOIN bq_member_invite_log b ON a.f_user_id = b.f_user_id ");
		sql.append("WHERE a.f_user_identity = 0 and a.f_user_oms = :oms and b.invite_uid = :uid");
		
		Session session = bqMemberInviteLogDao.getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", userId);
		query.setParameter("oms", oms);
		List<Integer> list = query.list();
		
		return list;
	}
}
