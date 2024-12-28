package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.vo.PagerModel;

public interface BQTransactionTypeService {

	BQTransactionType getTransTypeByCode(String code);
	
	BQTransactionType getTransTypeById(int id);
	
	BQTransactionType getTransTypeByName(String name);
	
	List<BQTransactionType> findAllUsing();
	
	PagerModel findAll_pm();
	
	void updateTransactionType(BQTransactionType transType);
	
	BQTransactionType getTransactionType(int typeId);
	
	
	/************************************ for read only **************************************/
	
	BQTransactionType readTransTypeByCode(String code);
	
	List<BQTransactionType> readAllUsing();
	
	BQTransactionType readTransactionType(int typeId);
	
}
