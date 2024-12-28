package com.btc100.btc_q.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.NTransactionTypeDao;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.vo.PagerModel;

@Service("nTransactionTypeService")
public class NTransactionTypeServiceImpl implements NTransactionTypeService {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@Autowired
	private NTransactionTypeDao<NTransactionType> nTransactionTypeDao;
	
	@Override
	public NTransactionType getTransTypeByCode(String partition, String code){
		return nTransactionTypeDao.getTransTypeByCode(partition, code);
	}
	
	@Override
	public NTransactionType readTransTypeByCode(String partition, String code){
		return nTransactionTypeDao.getTransTypeByCode(partition, code);
	}
	
	@Override
	public NTransactionType getTransTypeById(String partition, int id) {
		return nTransactionTypeDao.getTransTypeById(partition, id);
	}

	@Override
	public List<NTransactionType> findAllUsing(String partition) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isUsing", true);
		return nTransactionTypeDao.findBySql("select * from " + partition + "_transaction_type t where t.is_using = :isUsing ", params);
	}

	@Override
	public List<NTransactionType> readAllUsing(String partition) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isUsing", true);
		return nTransactionTypeDao.findBySql("select * from " + partition + "_transaction_type t where t.is_using = :isUsing ", params);
	}
	
	@Override
	public PagerModel findAll_pm(String partition) {
		return nTransactionTypeDao.searchSql("select * from " + partition + "_transaction_type");
	}

	@Override
	public void updateTransactionType(String partition, NTransactionType transType) {
		if (transType == null 
				||  transType.getType_name() == null || transType.getType_name().isEmpty()
				||  transType.getType_code() == null || transType.getType_code().isEmpty()) {
			return;
		}
		
		if (transType.getType_id() != null && transType.getType_id() > 0) {
			nTransactionTypeDao.update(partition, transType);
		} else {
			transType.setType_id(null);
			nTransactionTypeDao.insert(partition, transType);
		}
		
	}

	@Override
	public NTransactionType getTransTypeByName(String partition, String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		return nTransactionTypeDao.getBySql("select * from " + partition + "_transaction_type t where t.type_name = :name ", params);
	}

	@Override
	public NTransactionType getTransactionType(String partition, int typeId) {
		List<NTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition);
		if (types == null) {
			types = findAllUsing(partition);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition, BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		}
		
		if (types == null) return null;
		
		for (NTransactionType type : types) {
			if (type.getType_id() == typeId) {
				return type;
			}
		}
		
		return nTransactionTypeDao.getTransTypeById(partition, typeId);
	}
	
	@Override
	public NTransactionType readTransactionType(String partition, int typeId) {
		List<NTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition);
		if (types == null) {
			types = readAllUsing(partition);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition, BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		}
		
		if (types == null) return null;
		
		for (NTransactionType type : types) {
			if (type.getType_id() == typeId) {
				return type;
			}
		}
		
		return nTransactionTypeDao.getTransTypeById(partition, typeId);
	}

}
