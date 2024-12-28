package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQUserMonitor;
import com.btc100.btc_q.vo.PagerModel;

public interface BQUserMonitorService {
	
	PagerModel find(long uid);
	
	BQUserMonitor get(long uid);
	
	void save(BQUserMonitor userMonitor);
	
	void update(BQUserMonitor userMonitor);
	
	void delete(long uid);
	
	List<BQUserMonitor> findAll();
	
}

