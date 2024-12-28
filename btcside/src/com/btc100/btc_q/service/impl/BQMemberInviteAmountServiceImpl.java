package com.btc100.btc_q.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.BQMemberInviteAmountDao;
import com.btc100.btc_q.entity.BQMemberInviteAmount;
import com.btc100.btc_q.service.BQMemberInviteAmountService;
import com.btc100.btc_q.util.DateUtil;

@Service("bqMemberInviteAmountService")
public class BQMemberInviteAmountServiceImpl implements BQMemberInviteAmountService {

	@Autowired
	private BQMemberInviteAmountDao<BQMemberInviteAmount> bqMemberInviteAmountDao;

	public BQMemberInviteAmount readTodayAmount(long userId, String time) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		
		if (time == null || time.equals("")) {
			Date date = new Date(bqMemberInviteAmountDao.getUnix_TIMESTAMP() * 1000l);
			time = DateUtil.dateToStr(date, "yyyyMMdd");
		}
		
		params.put("day", time);
		return bqMemberInviteAmountDao.get("From BQMemberInviteAmount where f_user_id = :userId and f_date = :day", params);
	}

	public void updateClickByUserId(long userId) {
		// 判断有没有
		Date date = new Date(bqMemberInviteAmountDao.getUnix_TIMESTAMP() * 1000l);
		String time = DateUtil.dateToStr(date, "yyyyMMdd");
		
		BQMemberInviteAmount amount = readTodayAmount(userId, time);
		if (amount == null) {
			// 插入一条数据
			amount = new BQMemberInviteAmount();
			amount.setF_user_id(userId);
			amount.setF_day_click(1);
			amount.setF_day_success(0);
			amount.setF_date(time);
			bqMemberInviteAmountDao.save(amount);
		} else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", userId);
			params.put("day", time);
			bqMemberInviteAmountDao.updateCondition("Update BQMemberInviteAmount set f_day_click = f_day_click + 1 where f_date = :day and f_user_id = :userId ", params);
		}
	}

	public void updateSuccByUserId(long userId) {
		// 判断有没有
		Date date = new Date(bqMemberInviteAmountDao.getUnix_TIMESTAMP() * 1000l);
		String time = DateUtil.dateToStr(date, "yyyyMMdd");
		
		BQMemberInviteAmount amount = readTodayAmount(userId, time);
		if (amount == null) {
			// 插入一条数据
			amount = new BQMemberInviteAmount();
			amount.setF_user_id(userId);
			amount.setF_day_click(1);
			amount.setF_day_success(1);
			amount.setF_date(time);
			bqMemberInviteAmountDao.save(amount);
		} else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", userId);
			params.put("day", time);
			bqMemberInviteAmountDao.updateCondition("Update BQMemberInviteAmount set f_day_success = f_day_success + 1 where f_date = :day and f_user_id = :userId ", params);
		}
	}

	@Override
	public long readSumClick(long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String hql = "SELECT SUM(f_day_click) From BQMemberInviteAmount where f_user_id = :userId";
		Query query = bqMemberInviteAmountDao.queryparams(hql, params);
		Long sumClick = null;
		try {
			sumClick = (long) query.uniqueResult();
		} catch (Exception e) {
			sumClick = 0L;
		}
		return sumClick;
	}

	@Override
	public long readSumSuccess(long userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		String hql = "SELECT SUM(f_day_success) From BQMemberInviteAmount where f_user_id = :userId";
		Query query = bqMemberInviteAmountDao.queryparams(hql, params);
		Long sumSuccess = null;
		try {
			sumSuccess = (long) query.uniqueResult();
		} catch (Exception e) {
			sumSuccess = 0L;
		}
		return sumSuccess;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> readRanking(int limit) {
		StringBuffer sql = new StringBuffer(
		//		"SELECT b.f_user_email AS email, SUM(a.f_day_success) AS invite FROM bq_member_invite_amount a LEFT JOIN bq_member b ON a.f_user_id = b.f_user_id and b.f_user_email_status=1   GROUP BY a.f_user_id ORDER BY SUM(a.f_day_success) DESC");
				"select email,invite from (\r\n" + 
				"SELECT b.f_user_email AS email, SUM(a.f_day_success) AS invite FROM bq_member_invite_amount a LEFT JOIN bq_member b ON a.f_user_id = b.f_user_id and b.f_user_email_status=1   GROUP BY a.f_user_id ORDER BY SUM(a.f_day_success) DESC) res where res.email is not null");
		if (limit > 0)
			sql.append(" limit " + limit);
		Session session = bqMemberInviteAmountDao.getSession();
		SQLQuery query = session.createSQLQuery(sql.toString());
		List<Object[]> list = query.list();
		return list;
	}
	
}
