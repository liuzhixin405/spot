package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.vo.PagerModel;

import java.util.List;

public interface BQCurrencyTypeService {

	BQCurrencyType getByTypeName(String name);

	void save(BQCurrencyType type);
	
	void update(BQCurrencyType type);
	
	List<BQCurrencyType> findAll();
	
	BQCurrencyType getByTypeId(int id);

	int updateRecharge(int typeId, boolean isRecharge, boolean state);
	
	
	/************************************ for read only **************************************/
	
	BQCurrencyType readByTypeName(String name);
	
	BQCurrencyType readByTypeId(int id); 
	
	List<BQCurrencyType> readAll();
	
	List<BQCurrencyType> readAll(boolean withUnuse);
	
	PagerModel readAllTypes();
}
