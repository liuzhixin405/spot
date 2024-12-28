package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQCommonFailedlogin;

public interface BQCommonFailedloginService {
	
	void save(BQCommonFailedlogin bqFl);

	void updateLoginTime(BQCommonFailedlogin bqFl);

	void clearLoginTime(String email);

	void clearGoogleLoginTime(String email);
	
	
	/************************************ for read only **************************************/
	
	BQCommonFailedlogin readByName(String email);
}
