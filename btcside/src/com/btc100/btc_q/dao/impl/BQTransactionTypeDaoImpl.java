package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.BQTransactionTypeDao;
import com.btc100.btc_q.entity.BQTransactionType;

@Repository("bqTransactionTypeDao")
public class BQTransactionTypeDaoImpl extends BaseDaoImpl<BQTransactionType> implements BQTransactionTypeDao<BQTransactionType> {

	@Override
	public BQTransactionType getTransTypeByCode(String code){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return get(" from BQTransactionType t where t.type_code = :code ", params);
	}
	
	@Override
	public BQTransactionType getTransTypeById(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return get(" from BQTransactionType t where t.type_id = :id ", params);
	}

}
