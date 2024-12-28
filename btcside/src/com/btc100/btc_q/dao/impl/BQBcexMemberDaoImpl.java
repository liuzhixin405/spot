package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQBcexMemberDao;
import com.btc100.btc_q.entity.BQBcexMember;

@Repository("bqBcexMemberDao")
public class BQBcexMemberDaoImpl<T> extends BaseDaoImpl<T> implements BQBcexMemberDao<T> {

	public BQBcexMember getByEmail(String email) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("email", email);

		String hql = "FROM BQBcexMember b WHERE f_user_email = :email";
		return (BQBcexMember) get(hql, params);
				
	}
	
}
