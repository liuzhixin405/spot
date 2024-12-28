package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQSpotArbitrageSetting;

public interface BQSpotArbitrageSettingDao<T> extends BaseDaoI<T> {
	
	BQSpotArbitrageSetting getArbitrageSettingByCode(String code);
}
