package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQArbitrageSetting;

public interface BQArbitrageSettingDao<T> extends BaseDaoI<T> {
	
	BQArbitrageSetting getArbitrageSettingByCode(String code);
}
