package com.btc100.btc_q.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.btc100.btc_q.dao.DeliveryTransactionTypeDao;
import com.btc100.btc_q.entity.DeliveryTransactionType;

@Repository("deliveryTransactionTypeDao")
public class DeliveryTransactionTypeDaoImpl<T> extends BaseDaoImpl<T> implements DeliveryTransactionTypeDao<T> {

	@Override
	public DeliveryTransactionType getTransTypeByCode(String code){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return (DeliveryTransactionType) get(" from DeliveryTransactionType t where t.type_code = :code ", params);
	}
	
	@Override
	public DeliveryTransactionType getTransTypeById(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		return (DeliveryTransactionType) get(" from DeliveryTransactionType t where t.type_id = :id ", params);
	}

}
