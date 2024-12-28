package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.VTransactionTypeDao;
import com.btc100.btc_q.entity.VTransactionType;

@Repository("vTransactionTypeDao")
public class VTransactionTypeDaoImpl extends BaseDaoImpl<VTransactionType> implements VTransactionTypeDao<VTransactionType> {

	@Override
	public VTransactionType getTransTypeByCode(String code){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return get(" from VTransactionType t where t.type_code = :code ", params);
	}
	
	@Override
	public VTransactionType getTransTypeById(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return get(" from VTransactionType t where t.type_id = :id ", params);
	}

}
