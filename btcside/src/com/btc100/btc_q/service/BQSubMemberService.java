package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQSubMember;
import com.btc100.btc_q.vo.PagerModel;

public interface BQSubMemberService {

	void save(BQSubMember member);
	
	void update(BQSubMember member);

	long getTime();
	
	
	/************************************ for read only **************************************/

	PagerModel readByUserId(long motherUserId, String email, String note);
	
	BQSubMember readByUserId(long motherUserId, long subUserId);
	
	List<Object[]> readAllSubUser(long motherUserId);
	
	PagerModel readSubUserAndAppkey(long motherUserId);
	
	List<Object[]> readAllSubUserAndAppkey(long motherUserId);
}
