package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberPartner;

public interface BQMemberPartnerService {

	public BQMemberPartner findUserApiById(long userid);

	public void save(BQMemberPartner bqp);
	
	
	/************************************ for read only **************************************/

	public BQMemberPartner readApiById(long fUserId);
	
}
