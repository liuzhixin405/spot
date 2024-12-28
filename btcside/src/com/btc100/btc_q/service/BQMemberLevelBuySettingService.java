package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQMemberLevelBuySetting;

public interface BQMemberLevelBuySettingService {
	
	public void save(BQMemberLevelBuySetting ba);
	
	public void delete(long id);

	public void update(BQMemberLevelBuySetting setting);
	
	public BQMemberLevelBuySetting readById();
	
	public BQMemberLevelBuySetting get();
	
}
