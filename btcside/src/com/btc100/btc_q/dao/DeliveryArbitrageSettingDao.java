package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.DeliveryArbitrageSetting;

public interface DeliveryArbitrageSettingDao<T> extends BaseDaoI<T> {
	
	DeliveryArbitrageSetting getArbitrageSettingByCode(String code);
}
