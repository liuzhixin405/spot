package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberPayword;

public interface BQMemberPaywordService {

	void save(BQMemberPayword bqmP);

	int UpdateSZStateById(long id);
	
	void resetFailedTimes(long fUserId);
	
	void addFailedTimes(long fUserId);
	
	
	/************************************ for read only **************************************/
	
	BQMemberPayword readById(long fUserId);
}
