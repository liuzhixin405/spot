package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.VFundingFeeSetting;

public interface VFundingFeeSettingDao<T> extends BaseDaoI<T> {
	
	VFundingFeeSetting getFundingFeeSettingByCode(String code);
	
	void createfundingFeeTables(String symbol);
}
