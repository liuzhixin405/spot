package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQMemberInviteAmountDao;

@Repository("bqMemberInviteAmountDao")
public class BQMemberInviteAmountDaoImpl<T> extends BaseDaoImpl<T> implements BQMemberInviteAmountDao<T> {

	public List<T> findByUidAndDay(long uid, String day) {
		String hql = "FROM BQMemberInviteAmount WHERE f_user_id=:uid AND f_date=:date";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("date", day);

		return this.find(hql, map);
	}
	
	public int addClick(long uid, String day) {
		String hql = "UPDATE BQMemberInviteAmount SET f_day_click=f_day_click+1 WHERE f_user_id=:uid AND f_date=:date";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("date", day);

		return this.updateCondition(hql, map);
	}

	public int addSuccess(long uid, String day) {
		String hql = "UPDATE BQMemberInviteAmount SET f_day_success=f_day_success+1 WHERE f_user_id=:uid AND f_date=:date";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("date", day);

		return this.updateCondition(hql, map);
	}

}
