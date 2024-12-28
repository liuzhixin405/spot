package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQUserTradeWhite;
import com.btc100.btc_q.vo.PagerModel;

public interface BQUserTradeWhiteService {
	
	PagerModel find(long uid);
	
	BQUserTradeWhite get(long uid);
	
	void save(BQUserTradeWhite userRiskControl);
	
	void update(BQUserTradeWhite userRiskControl);
	
	void delete(long uid);
	
	List<BQUserTradeWhite> findAll();
	
	
	/************************************ for read only **************************************/
	
	List<BQUserTradeWhite> readAll();
}

