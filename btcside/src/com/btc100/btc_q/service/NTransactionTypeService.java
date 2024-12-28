package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.vo.PagerModel;

public interface NTransactionTypeService {

	NTransactionType getTransTypeByCode(String partition, String code);
	
	NTransactionType getTransTypeById(String partition, int id);
	
	NTransactionType getTransTypeByName(String partition, String name);
	
	List<NTransactionType> findAllUsing(String partition);
	
	PagerModel findAll_pm(String partition);
	
	void updateTransactionType(String partition, NTransactionType transType);
	
	NTransactionType getTransactionType(String partition, int typeId);
	
	
	/************************************ for read only **************************************/
	
	NTransactionType readTransTypeByCode(String partition, String code);
	
	List<NTransactionType> readAllUsing(String partition);
	
	NTransactionType readTransactionType(String partition, int typeId);
	
}
