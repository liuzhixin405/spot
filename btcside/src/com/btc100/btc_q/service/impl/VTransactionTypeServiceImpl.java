package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.VTransactionTypeDao;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.vo.PagerModel;

@Service("vTransactionTypeService")
public class VTransactionTypeServiceImpl implements VTransactionTypeService {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@Autowired
	private VTransactionTypeDao<VTransactionType> vTransactionTypeDao;
	
	@Override
	public VTransactionType getTransTypeByCode(String code){
		return vTransactionTypeDao.getTransTypeByCode(code);
	}
	
	@Override
	public VTransactionType getTransTypeById(int id) {
		return vTransactionTypeDao.getTransTypeById(id);
	}

	@Override
	public List<VTransactionType> findAllUsing() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isUsing", true);
		return vTransactionTypeDao.find(" from VTransactionType t where t.is_using = :isUsing ", params);
	}
	
	@Override
	public List<VTransactionType> readAllUsing() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isUsing", true);
		return vTransactionTypeDao.find(" from VTransactionType t where t.is_using = :isUsing ", params);
	}

	@Override
	public PagerModel findAll_pm() {
		return vTransactionTypeDao.searchHql(" from VTransactionType");
	}

	@Override
	public void updateTransactionType(VTransactionType transType) {
		if (transType == null 
				||  transType.getType_name() == null || transType.getType_name().isEmpty()
				||  transType.getType_code() == null || transType.getType_code().isEmpty()) {
			return;
		}
		
		if (transType.getType_id() != null && transType.getType_id() > 0) {
			vTransactionTypeDao.update(transType);
		} else {
			transType.setType_id(null);
			vTransactionTypeDao.save(transType);
		}
		
	}

	@Override
	public VTransactionType getTransTypeByName(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		return vTransactionTypeDao.get(" from VTransactionType t where t.type_name = :name ", params);
	}

	@Override
	public VTransactionType getTransactionType(int typeId) {
		List<VTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANS_TYPES);
		if (types == null) {
			types = findAllUsing();
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANS_TYPES, BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		}
		
		if (types == null) return null;
		
		for (VTransactionType type : types) {
			if (type.getType_id() == typeId) {
				return type;
			}
		}
		
		return vTransactionTypeDao.getTransTypeById(typeId);
	}
	
	@Override
	public VTransactionType readTransactionType(int typeId) {
		List<VTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANS_TYPES);
		if (types == null) {
			types = readAllUsing();
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANS_TYPES, BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		}
		
		if (types == null) return null;
		
		for (VTransactionType type : types) {
			if (type.getType_id() == typeId) {
				return type;
			}
		}
		
		return vTransactionTypeDao.getTransTypeById(typeId);
	}

}
