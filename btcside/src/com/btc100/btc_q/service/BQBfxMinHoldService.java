package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQBfxMinHold;

public interface BQBfxMinHoldService {

	void save(BQBfxMinHold setting);
	
	void update(BQBfxMinHold setting);
	
	BQBfxMinHold getBfxMinHoldByUserAndTime(long userId, long time);
	
	
	/************************************ for read only **************************************/
	
	BQBfxMinHold readBfxMinHoldByUserAndTime(long userId, long time);
}

