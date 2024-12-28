package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.PendingLog;
import com.btc100.btc_q.vo.PagerModel;

public interface PendingLogService {

	public void savePendingLog(PendingLog pendingLog);
	
	public void updateActionByPendingNo(String bqCode, long pendingNo, long userId, int action);
	
	public List<PendingLog> findByPendingNo(String code, String pendingNo, long userId);

	void createPendingLogBak(String code);
	
	void insertPendingLogBak(String code, long endTime);
	
	
	///************************************ for read only **************************************/
	
	List<PendingLog> readListByUserId(String code, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	PagerModel readPagerMode(String code, long userId, int tradeType, long startTime, long endTime);
	
	public List<PendingLog> readByPendingNo(String code, String pendingNo, long userId);
	
}
