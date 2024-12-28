package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.vo.PagerModel;

public interface VTransactionTypeService {

	VTransactionType getTransTypeByCode(String code);
	
	VTransactionType getTransTypeById(int id);
	
	VTransactionType getTransTypeByName(String name);
	
	List<VTransactionType> findAllUsing();
	
	PagerModel findAll_pm();
	
	void updateTransactionType(VTransactionType transType);
	
	VTransactionType getTransactionType(int typeId);
	
	
	///************************************ for read only **************************************/
	
	List<VTransactionType> readAllUsing();
	
	VTransactionType readTransactionType(int typeId);
	
}
