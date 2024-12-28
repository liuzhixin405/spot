package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQIeoUnlockSetting;
import com.btc100.btc_q.vo.PagerModel;

public interface BQIeoUnlockSettingService {
	
	public void save(BQIeoUnlockSetting ba);
	
	public void delete(long id);

	public void update(BQIeoUnlockSetting setting);
	
	public BQIeoUnlockSetting readById(long id);
	
	public BQIeoUnlockSetting getByTime(long time);

	PagerModel readAll();
	
}
