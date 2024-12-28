package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQUserOpenLimit;
import com.btc100.btc_q.vo.PagerModel;

public interface BQUserOpenLimitService {
	
	BQUserOpenLimit get(String code, long uid);
	
	void save(BQUserOpenLimit obj);
	
	void update(BQUserOpenLimit obj);
	
	void delete(BQUserOpenLimit obj);
	
	
	/************************************ for read only **************************************/
	
	PagerModel read(String code, long uid);
	
	BQUserOpenLimit readById(long id);
	
	BQUserOpenLimit readByCodeAndUid(String code, long uid);
	
	List<BQUserOpenLimit> readAll();
}

