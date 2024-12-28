package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQInviteClickCountDao;


@Repository("bqInviteClickDao")
public class BQInviteClickCountDaoImpl<BQInviteClickCount> extends BaseDaoImpl<BQInviteClickCount> implements BQInviteClickCountDao<BQInviteClickCount>{

	public List<BQInviteClickCount> findByTimeAndUid(long uid,int time){
		String hql="From BQInviteClickCount WHERE f_user_id=:uid AND day=:day";
		Map<String,Object> map =new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("day", time);
		
		return this.find(hql, map);		
	}

	public int addclick(long uid,int time){
		String hql="UPDATE BQInviteClickCount SET click_day_count=click_day_count+1,click_total_count=click_total_count+1 WHERE f_user_id=:uid AND day=:day";
		Map<String,Object> map =new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("day", time);
		
		return this.updateCondition(hql, map);		
	}
	
	
	
}
