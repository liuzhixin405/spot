package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.VOverloadMatchSetting;

public interface VOverloadMatchSettingDao<T> extends BaseDaoI<T> {
	
	VOverloadMatchSetting getSettingByCode(String code);
}
