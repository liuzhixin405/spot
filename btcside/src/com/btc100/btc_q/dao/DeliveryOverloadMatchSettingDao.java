package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.DeliveryOverloadMatchSetting;

public interface DeliveryOverloadMatchSettingDao<T> extends BaseDaoI<T> {
	
	DeliveryOverloadMatchSetting getSettingByCode(String code);
}
