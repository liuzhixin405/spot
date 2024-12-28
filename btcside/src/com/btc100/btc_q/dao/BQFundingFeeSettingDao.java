package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQFundingFeeSetting;

public interface BQFundingFeeSettingDao<T> extends BaseDaoI<T> {
	
	BQFundingFeeSetting getFundingFeeSettingByCode(String code);
	
	void createfundingFeeTables(String symbol);
}
