package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.SpotPendingLog;
import com.btc100.btc_q.vo.PagerModel;

public interface SpotPendingLogService {

	void deleteByUidBeforeTime(String code, long userId, long time, int action);

	void createPendingLogBak(String code);
	
	void insertPendingLogBak(String code, long endTime);
	
	
	
	///************************************ for read only **************************************/
	
	List<SpotPendingLog> readListByUserId(String code, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	PagerModel readPagerMode(String code, long userId, int tradeType, long startTime, long endTime);
	
	List<SpotPendingLog> readByPendingNo(String code, String pendingNo, long userId);
}
