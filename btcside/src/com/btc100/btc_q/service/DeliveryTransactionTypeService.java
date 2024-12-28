package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryTransactionTypeService {

	DeliveryTransactionType getTransTypeByCode(String code);
	
	DeliveryTransactionType getTransTypeById(int id);
	
	DeliveryTransactionType getTransTypeByName(String name);
	
	List<DeliveryTransactionType> findAllUsing();
	
	PagerModel findAll_pm();
	
	void updateTransactionType(DeliveryTransactionType transType);
	
	DeliveryTransactionType getTransactionType(int typeId);
	
	
	/************************************ for read only **************************************/
	
	DeliveryTransactionType readTransTypeByCode(String code);
	
	List<DeliveryTransactionType> readAllUsing();
	
	DeliveryTransactionType readTransactionType(int typeId);
	
}
