package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NNotradetime;

public interface NNotradetimeDao<T> extends BaseDaoI<T> {
	
	public int insert(String partition, NNotradetime time);
	
}
