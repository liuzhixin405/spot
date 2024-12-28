package com.btc100.btc_q.dao;

import java.util.List;

import com.btc100.btc_q.entity.BQCurrencyType;

public interface BQCurrencyTypeDao<T> extends BaseDaoI<T> {

	BQCurrencyType getByTypeName(String name);

	BQCurrencyType getByTypeId(Integer id);

	List<BQCurrencyType> findAll();
	
	List<BQCurrencyType> findAll(boolean withUnuse);
	
}
