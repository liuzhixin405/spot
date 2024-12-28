package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQUserRiskControl;
import com.btc100.btc_q.vo.PagerModel;

public interface BQUserRiskControlService {
	
	PagerModel find(long uid);
	
	void save(BQUserRiskControl userRiskControl);
	
	void update(BQUserRiskControl userRiskControl);
	
	void delete(long uid);
	
	List<BQUserRiskControl> findAll();
	
	
	/************************************ for read only **************************************/
	
	BQUserRiskControl readOne(long uid);
	
}

