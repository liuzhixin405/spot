package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.VArbitrageSetting;

public interface VArbitrageSettingDao<T> extends BaseDaoI<T> {
	
	VArbitrageSetting getArbitrageSettingByCode(String code);
}
