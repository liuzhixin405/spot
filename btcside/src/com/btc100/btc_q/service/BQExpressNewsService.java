package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQExpressNews;
import com.btc100.btc_q.vo.PagerModel;

public interface BQExpressNewsService {

	int add(BQExpressNews obj);

	int update(BQExpressNews obj);

	int delById(long id);

	
	/************************************ for read only **************************************/
	
	BQExpressNews readById(long id);
	
	List<BQExpressNews> readLimit(int limit);
	
	PagerModel readModel();
	
	List<BQExpressNews> readByTime(long from, String direct, long startTime, long endTime, int limit);

}
