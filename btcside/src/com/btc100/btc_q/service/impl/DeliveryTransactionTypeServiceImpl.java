package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.DeliveryTransactionTypeDao;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.vo.PagerModel;

@Service("deliveryTransactionTypeService")
public class DeliveryTransactionTypeServiceImpl implements DeliveryTransactionTypeService {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@Autowired
	private DeliveryTransactionTypeDao<DeliveryTransactionType> deliveryTransactionTypeDao;
	
	@Override
	public DeliveryTransactionType getTransTypeByCode(String code){
		return deliveryTransactionTypeDao.getTransTypeByCode(code);
	}
	
	@Override
	public DeliveryTransactionType readTransTypeByCode(String code){
		return deliveryTransactionTypeDao.getTransTypeByCode(code);
	}
	
	@Override
	public DeliveryTransactionType getTransTypeById(int id) {
		return deliveryTransactionTypeDao.getTransTypeById(id);
	}

	@Override
	public List<DeliveryTransactionType> findAllUsing() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isUsing", true);
		return deliveryTransactionTypeDao.find(" from DeliveryTransactionType t where t.is_using = :isUsing ", params);
	}

	@Override
	public List<DeliveryTransactionType> readAllUsing() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isUsing", true);
		return deliveryTransactionTypeDao.find(" from DeliveryTransactionType t where t.is_using = :isUsing ", params);
	}
	
	@Override
	public PagerModel findAll_pm() {
		return deliveryTransactionTypeDao.searchHql(" from DeliveryTransactionType");
	}

	@Override
	public void updateTransactionType(DeliveryTransactionType transType) {
		if (transType == null 
				||  transType.getType_name() == null || transType.getType_name().isEmpty()
				||  transType.getType_code() == null || transType.getType_code().isEmpty()) {
			return;
		}
		
		if (transType.getType_id() != null && transType.getType_id() > 0) {
			deliveryTransactionTypeDao.update(transType);
		} else {
			transType.setType_id(null);
			deliveryTransactionTypeDao.save(transType);
		}
		
	}

	@Override
	public DeliveryTransactionType getTransTypeByName(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		return deliveryTransactionTypeDao.get(" from DeliveryTransactionType t where t.type_name = :name ", params);
	}

	@Override
	public DeliveryTransactionType getTransactionType(int typeId) {
		List<DeliveryTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANS_TYPES);
		if (types == null) {
			types = findAllUsing();
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		}
		
		if (types == null) return null;
		
		for (DeliveryTransactionType type : types) {
			if (type.getType_id() == typeId) {
				return type;
			}
		}
		
		return deliveryTransactionTypeDao.getTransTypeById(typeId);
	}
	
	@Override
	public DeliveryTransactionType readTransactionType(int typeId) {
		List<DeliveryTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANS_TYPES);
		if (types == null) {
			types = readAllUsing();
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		}
		
		if (types == null) return null;
		
		for (DeliveryTransactionType type : types) {
			if (type.getType_id() == typeId) {
				return type;
			}
		}
		
		return deliveryTransactionTypeDao.getTransTypeById(typeId);
	}

}
