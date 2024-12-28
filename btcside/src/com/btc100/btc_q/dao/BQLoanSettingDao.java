package com.btc100.btc_q.dao;

public interface BQLoanSettingDao<T> extends BaseDaoI<T> {
	
	T getLoanSettingByCode(String borrow, String pledge);
	
}
