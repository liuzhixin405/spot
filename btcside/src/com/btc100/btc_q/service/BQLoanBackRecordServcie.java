package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQLoanBackRecord;
import com.btc100.btc_q.vo.PagerModel;

public interface BQLoanBackRecordServcie {

	void save(BQLoanBackRecord setting);
	
	
	
	/************************************ for read only **************************************/
	
	
	PagerModel readAllByUserId(long userId, long borrowId);
	
}
