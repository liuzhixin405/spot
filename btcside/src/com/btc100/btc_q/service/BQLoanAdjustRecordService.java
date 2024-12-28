package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQLoanAdjustRecord;
import com.btc100.btc_q.vo.PagerModel;

public interface BQLoanAdjustRecordService {
	
	void save(BQLoanAdjustRecord setting);
	
	
	/************************************ for read only **************************************/
	
	
	PagerModel readAllByUserId(long userId);
}
