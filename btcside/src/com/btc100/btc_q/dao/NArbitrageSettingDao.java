package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NArbitrageSetting;

public interface NArbitrageSettingDao<T> extends BaseDaoI<T> {
	
	NArbitrageSetting getArbitrageSettingByCode(String partition, String code);
}
