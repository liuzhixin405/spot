package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NFundingFeeSetting;

public interface NFundingFeeSettingDao<T> extends BaseDaoI<T> {
	
	NFundingFeeSetting getFundingFeeSettingByCode(String partition, String code);
	
	void createfundingFeeTables(String partition, String symbol);
	
	int insert(String partition, NFundingFeeSetting setting);
	
	int update(String partition, NFundingFeeSetting setting);
}
