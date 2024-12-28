package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.DeliveryAutoCreateSetting;

public interface DeliveryAutoCreateSettingDao<T> extends BaseDaoI<T> {

	DeliveryAutoCreateSetting getByType(String type);
}
