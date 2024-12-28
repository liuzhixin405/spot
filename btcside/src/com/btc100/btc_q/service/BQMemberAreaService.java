package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberArea;

public interface BQMemberAreaService {

	void save(BQMemberArea bqA);

	BQMemberArea findByUserId(long fUserId);
	
	/************************************ for read only **************************************/
	
	BQMemberArea readByUserId(long fUserId);

}
