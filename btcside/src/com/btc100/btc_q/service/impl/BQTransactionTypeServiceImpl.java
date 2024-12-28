package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.BQTransactionTypeDao;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqTransactionTypeService")
public class BQTransactionTypeServiceImpl implements BQTransactionTypeService {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@Autowired
	private BQTransactionTypeDao<BQTransactionType> bqTransactionTypeDao;
	
	@Override
	public BQTransactionType getTransTypeByCode(String code){
		return bqTransactionTypeDao.getTransTypeByCode(code);
	}
	
	@Override
	public BQTransactionType readTransTypeByCode(String code){
		return bqTransactionTypeDao.getTransTypeByCode(code);
	}
	
	@Override
	public BQTransactionType getTransTypeById(int id) {
		return bqTransactionTypeDao.getTransTypeById(id);
	}

	@Override
	public List<BQTransactionType> findAllUsing() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isUsing", true);
		return bqTransactionTypeDao.find(" from BQTransactionType t where t.is_using = :isUsing ", params);
	}

	@Override
	public List<BQTransactionType> readAllUsing() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isUsing", true);
		return bqTransactionTypeDao.find(" from BQTransactionType t where t.is_using = :isUsing ", params);
	}
	
	@Override
	public PagerModel findAll_pm() {
		return bqTransactionTypeDao.searchHql(" from BQTransactionType");
	}

	@Override
	public void updateTransactionType(BQTransactionType transType) {
		if (transType == null 
				||  transType.getType_name() == null || transType.getType_name().isEmpty()
				||  transType.getType_code() == null || transType.getType_code().isEmpty()) {
			return;
		}
		
		if (transType.getType_id() != null && transType.getType_id() > 0) {
			bqTransactionTypeDao.update(transType);
		} else {
			transType.setType_id(null);
			bqTransactionTypeDao.save(transType);
		}
		
	}

	@Override
	public BQTransactionType getTransTypeByName(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		return bqTransactionTypeDao.get(" from BQTransactionType t where t.type_name = :name ", params);
	}

	@Override
	public BQTransactionType getTransactionType(int typeId) {
		List<BQTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES);
		if (types == null) {
			types = findAllUsing();
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		}
		
		if (types == null) return null;
		
		for (BQTransactionType type : types) {
			if (type.getType_id() == typeId) {
				return type;
			}
		}
		
		return bqTransactionTypeDao.getTransTypeById(typeId);
	}
	
	@Override
	public BQTransactionType readTransactionType(int typeId) {
		List<BQTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES);
		if (types == null) {
			types = readAllUsing();
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		}
		
		if (types == null) return null;
		
		for (BQTransactionType type : types) {
			if (type.getType_id() == typeId) {
				return type;
			}
		}
		
		return bqTransactionTypeDao.getTransTypeById(typeId);
	}

}
