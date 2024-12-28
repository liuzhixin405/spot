package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQUserAppKey;
import com.btc100.btc_q.vo.PagerModel;

public interface BQUserAppKeyService {
	
	void save(BQUserAppKey userMonitor);
	
	void update(BQUserAppKey userMonitor);
	
	void delete(long uid);
	
	List<BQUserAppKey> findAll();
	
	BQUserAppKey getByAppKey(String appKey);
	
	
	/************************************ for read only **************************************/
	
	PagerModel readPagerModel(long uid);
	
	PagerModel readByState(int state);
	
	BQUserAppKey readOne(long uid);

	BQUserAppKey readByAppKey(String appKey);
	
	List<Object[]> readAllSubAppkey(long userId);
}

