package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.VFundingFeeSetting;

public interface VFundingFeeSettingService {

	VFundingFeeSetting getFundingFeeSettingByCode(String code);

	void save(VFundingFeeSetting setting);
	
	void update(VFundingFeeSetting setting);
	
	
	/************************************ for read only **************************************/
	
	List<VFundingFeeSetting> readAll();
}

