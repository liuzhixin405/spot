package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NOverloadMatchSetting;

public interface NOverloadMatchSettingDao<T> extends BaseDaoI<T> {
	
	NOverloadMatchSetting getSettingByCode(String partition, String code);
	
	public int insert(String partition, NOverloadMatchSetting ticker);
	
	public int update(String partition, NOverloadMatchSetting ticker);
}
