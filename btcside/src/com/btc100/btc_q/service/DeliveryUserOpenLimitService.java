package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.DeliveryUserOpenLimit;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryUserOpenLimitService {
	
	DeliveryUserOpenLimit get(String code, long uid);
	
	void save(DeliveryUserOpenLimit obj);
	
	void update(DeliveryUserOpenLimit obj);
	
	void delete(DeliveryUserOpenLimit obj);
	
	
	/************************************ for read only **************************************/
	
	PagerModel read(String code, long uid);
	
	DeliveryUserOpenLimit readById(long id);
	
	DeliveryUserOpenLimit readByCodeAndUid(String code, long uid);
	
	List<DeliveryUserOpenLimit> readAll();
}

