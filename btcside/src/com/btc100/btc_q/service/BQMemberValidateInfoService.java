package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberValidateInfo;

public interface BQMemberValidateInfoService {

	void save(BQMemberValidateInfo entity); 
	
	void update(BQMemberValidateInfo entity);
	
	
	/************************************ for read only **************************************/
	
	BQMemberValidateInfo readByUserId(long userId); 
	
}
