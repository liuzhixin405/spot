package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.NFundingFeeSetting;

public interface NFundingFeeSettingService {

	NFundingFeeSetting getFundingFeeSettingByCode(String partition, String code);

	void save(String partition, NFundingFeeSetting setting);
	
	void update(String partition, NFundingFeeSetting setting);
	
	
	/************************************ for read only **************************************/
	
	List<NFundingFeeSetting> readAll(String partition);
}

