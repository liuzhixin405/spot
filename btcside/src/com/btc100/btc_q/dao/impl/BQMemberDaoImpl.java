package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQMemberDao;
import com.btc100.btc_q.entity.BQMember;

@Repository("bqMemberDao")
public class BQMemberDaoImpl<BQMember> extends BaseDaoImpl<BQMember> implements BQMemberDao<BQMember> {
	
	public List<BQMember> findsByEmail(String email) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", "%" + email + "%");

		String hql = "FROM BQMember b WHERE f_user_email like :email";
		return find(hql, map);
	}

	public BQMember findByEmail(String email) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", email);

		String hql = "FROM BQMember b WHERE f_user_email = :email";
		List<BQMember> list = find(hql, map);
		if (list == null || list.isEmpty()) {
			return null;
		} else
			return list.get(0);
	}
	
	public void addCredits(long credits, long vipId, long uid) {

		if (credits > 0) {
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("uid", uid);
	
			String hql = "FROM BQMember b WHERE f_user_id = :uid";
			List<BQMember> list = find(hql, map1);
			if (list.size() > 0) {
				com.btc100.btc_q.entity.BQMember member = (com.btc100.btc_q.entity.BQMember) list.get(0);
				if (vipId < member.getF_user_level_id()) {
					vipId = member.getF_user_level_id();
				}
			}
		}
		
		String hql = "UPDATE BQMember b SET b.f_user_credits = b.f_user_credits + :credit, b.f_user_level_id = :vipId WHERE b.f_user_id = :id";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("credit", credits);
		map.put("vipId", vipId);
		map.put("id", uid);
		
		updateCondition(hql, map);
	}

}
