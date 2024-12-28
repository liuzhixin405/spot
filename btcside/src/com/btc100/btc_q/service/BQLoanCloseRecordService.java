package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQLoanCloseRecord;
import com.btc100.btc_q.vo.PagerModel;

public interface BQLoanCloseRecordService {
	
	void save(BQLoanCloseRecord setting);
	
	
	/************************************ for read only **************************************/
	
	
	PagerModel readAllByUserId(long userId, long borrowId);
}
