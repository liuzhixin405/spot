package com.btc100.btc_q.service.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQSubMemberDao;
import com.btc100.btc_q.entity.BQSubMember;
import com.btc100.btc_q.service.BQSubMemberService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.SystemContext;

@Service("bqSubMemberService")
public class BQSubMemberServiceImpl implements BQSubMemberService {

	@Autowired
	private BQSubMemberDao<BQSubMember> bqSubMemberDao;

	public void save(BQSubMember member) {
		bqSubMemberDao.save(member);
	}

	@Override
	public void update(BQSubMember member) {
		bqSubMemberDao.update(member);
	}

	public long getTime() {
		return bqSubMemberDao.getUnix_TIMESTAMP();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PagerModel readByUserId(long motherUserId, String email, String note) {
		StringBuffer sql = new StringBuffer("SELECT c.f_user_id,c.f_user_email,c.f_user_state,c.sub_user_note,d.f_user_safetype FROM bq_sub_member b");
		sql.append(" LEFT JOIN bq_member c ON b.sub_user_id = c.f_user_id");
		sql.append(" LEFT JOIN bq_member_safe_settings d ON b.sub_user_id = d.f_user_id");
		sql.append(" WHERE b.mother_user_id = :uid AND c.sub_user_delete <> :isDelete");
		if (!StringUtil.isEmpty(email)) {
			sql.append(" AND c.f_user_email = :email");
		}
		if (!StringUtil.isEmpty(note)) {
			sql.append(" AND c.sub_user_note = :note");
		}
		sql.append(" ORDER BY b.id DESC");
		sql.append(" LIMIT ").append(SystemContext.getOffset()).append(", ").append(SystemContext.getPageSize());
		
		Session session = bqSubMemberDao.getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", motherUserId);
		query.setParameter("isDelete", 1);
		if (!StringUtil.isEmpty(email)) {
			query.setParameter("email", email);
		}
		if (!StringUtil.isEmpty(note)) {
			query.setParameter("note", note);
		}
		List<Object[]> list = query.list();
		
		StringBuffer countSql = new StringBuffer("SELECT count(*) FROM bq_sub_member b");
		countSql.append(" LEFT JOIN bq_member c ON b.sub_user_id = c.f_user_id");
		countSql.append(" WHERE b.mother_user_id = :uid AND c.sub_user_delete <> :isDelete");
		if (!StringUtil.isEmpty(email)) {
			countSql.append(" AND c.f_user_email = :email");
		}
		if (!StringUtil.isEmpty(note)) {
			countSql.append(" AND c.sub_user_note = :note");
		}
		session = bqSubMemberDao.getSession();
		SQLQuery countQuery = session.createSQLQuery(countSql.toString());
		countQuery.setParameter("uid", motherUserId);
		countQuery.setParameter("isDelete", 1);
		if (!StringUtil.isEmpty(email)) {
			countQuery.setParameter("email", email);
		}
		if (!StringUtil.isEmpty(note)) {
			countQuery.setParameter("note", note);
		}
		BigInteger total = (BigInteger) countQuery.uniqueResult();
		PagerModel pm = new PagerModel();
		pm.setDatas(list);
		pm.setTotal(total.intValue());
		
		return pm;
	}

	@Override
	public BQSubMember readByUserId(long motherUserId, long subUserId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("motherUserId", motherUserId);
		params.put("subUserId", subUserId);
		return bqSubMemberDao.get("FROM BQSubMember WHERE mother_user_id = :motherUserId AND sub_user_id = :subUserId", params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> readAllSubUser(long motherUserId) {
		StringBuffer sql = new StringBuffer("SELECT c.f_user_id, c.f_user_email FROM bq_sub_member b");
		sql.append(" LEFT JOIN bq_member c ON b.sub_user_id = c.f_user_id");
		sql.append(" WHERE b.mother_user_id = :uid AND c.sub_user_delete <> :isDelete");
		
		Session session = bqSubMemberDao.getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", motherUserId);
		query.setParameter("isDelete", 1);
		List<Object[]> list = query.list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PagerModel readSubUserAndAppkey(long motherUserId) {
		bqSubMemberDao.getSession().flush();
		bqSubMemberDao.getSession().clear();
		
		StringBuffer sql = new StringBuffer("SELECT c.f_user_id, c.f_user_email, d.app_key, d.allow_ip FROM bq_sub_member b");
		sql.append(" LEFT JOIN bq_member c ON b.sub_user_id = c.f_user_id");
		sql.append(" LEFT JOIN bq_sub_member_info d ON b.sub_user_id = d.f_user_id");
		sql.append(" WHERE b.mother_user_id = :uid AND c.sub_user_delete <> :isDelete");
		sql.append(" ORDER BY b.id DESC");
		sql.append(" LIMIT ").append(SystemContext.getOffset()).append(", ").append(SystemContext.getPageSize());

		Session session = bqSubMemberDao.getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", motherUserId);
		query.setParameter("isDelete", 1);
		List<Object[]> list = query.list();
		
		StringBuffer countSql = new StringBuffer("SELECT count(*) FROM bq_sub_member b");
		countSql.append(" LEFT JOIN bq_member c ON b.sub_user_id = c.f_user_id");
		countSql.append(" LEFT JOIN bq_sub_member_info d ON b.sub_user_id = d.f_user_id");
		countSql.append(" WHERE b.mother_user_id = :uid AND c.sub_user_delete <> :isDelete");
		
		session = bqSubMemberDao.getSession();
		SQLQuery countQuery = session.createSQLQuery(countSql.toString());
		countQuery.setParameter("uid", motherUserId);
		countQuery.setParameter("isDelete", 1);
		BigInteger total = (BigInteger) countQuery.uniqueResult();
		PagerModel pm = new PagerModel();
		pm.setDatas(list);
		pm.setTotal(total.intValue());
		
		return pm;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> readAllSubUserAndAppkey(long motherUserId) {
		bqSubMemberDao.getSession().flush();
		bqSubMemberDao.getSession().clear();
		
		StringBuffer sql = new StringBuffer("SELECT c.f_user_id, c.f_user_email, d.unencrypted_password, d.app_key, d.app_secret, d.allow_ip FROM bq_sub_member b");
		sql.append(" LEFT JOIN bq_member c ON b.sub_user_id = c.f_user_id");
		sql.append(" LEFT JOIN bq_sub_member_info d ON b.sub_user_id = d.f_user_id");
		sql.append(" WHERE b.mother_user_id = :uid AND c.sub_user_delete <> :isDelete");
		sql.append(" ORDER BY b.id DESC");

		Session session = bqSubMemberDao.getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		query.setParameter("uid", motherUserId);
		query.setParameter("isDelete", 1);
		List<Object[]> list = query.list();
		
		return list;
	}

}
