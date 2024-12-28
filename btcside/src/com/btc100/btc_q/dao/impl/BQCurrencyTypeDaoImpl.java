package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQCurrencyTypeDao;
import com.btc100.btc_q.entity.BQCurrencyType;

@Repository("bqCurrencyTypeDao")
public class BQCurrencyTypeDaoImpl<T> extends BaseDaoImpl<T> implements BQCurrencyTypeDao<T> {

	@Override
	public BQCurrencyType getByTypeName(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		return (BQCurrencyType) get("from BQCurrencyType where type_name = :name", params);
	}

	@Override
	public BQCurrencyType getByTypeId(Integer id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return (BQCurrencyType) get("from BQCurrencyType where type_id = :id", params);
	}

	@Override
	public List<BQCurrencyType> findAll() {
		return findAll(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BQCurrencyType> findAll(boolean withUnuse) {
		if (!withUnuse) {
			return (List<BQCurrencyType>) find("from BQCurrencyType where 1=1 and state = 1");
		} else {
			return (List<BQCurrencyType>) find("from BQCurrencyType where 1=1");
		}
	}

}
