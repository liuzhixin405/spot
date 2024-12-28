package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.BQOverloadMatchSetting;

public interface BQOverloadMatchSettingDao<T> extends BaseDaoI<T> {
	
	BQOverloadMatchSetting getSettingByCode(String code);
}
