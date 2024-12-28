package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQBcexMember;

public interface BQBcexMemberService {

	void save(BQBcexMember member);
	
	void update(BQBcexMember member);
	
	
	/************************************ for read only **************************************/
	
	BQBcexMember readByEmail(String uemail);

}
