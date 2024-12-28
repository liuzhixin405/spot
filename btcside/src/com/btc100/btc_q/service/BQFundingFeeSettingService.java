package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQFundingFeeSetting;

public interface BQFundingFeeSettingService {

	BQFundingFeeSetting getFundingFeeSettingByCode(String code);

	void save(BQFundingFeeSetting setting);
	
	void update(BQFundingFeeSetting setting);
	
	
	/************************************ for read only **************************************/
	
	List<BQFundingFeeSetting> readAll();
}

