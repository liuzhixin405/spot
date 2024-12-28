package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQBftRewardSetting;

public interface BQBftRewardSettingService {

	void save(BQBftRewardSetting setting);
	
	void update(BQBftRewardSetting setting);
	
	boolean bfxRewarded(long id);
	
	boolean oldRegisterRewarded(long id);
	
	BQBftRewardSetting get();
	
	
	/************************************ for read only **************************************/
	
	BQBftRewardSetting readOne();
}

