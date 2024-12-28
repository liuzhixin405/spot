package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.NPendingLog;
import com.btc100.btc_q.vo.PagerModel;

public interface NPendingLogService {

	public void savePendingLog(String partition, NPendingLog pendingLog);
	
	public void updateActionByPendingNo(String partition, String bqCode, long pendingNo, long userId, int action);
	
	public List<NPendingLog> findByPendingNo(String partition, String code, String pendingNo, long userId);

	void createPendingLogBak(String partition, String code);
	
	void insertPendingLogBak(String partition, String code, long endTime);
	
	void deleteByUidBeforeTime(String partition, String code, long userId, long time, int action);
	
	
	///************************************ for read only **************************************/
	
	List<NPendingLog> readListByUserId(String partition, String code, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	PagerModel readPagerMode(String partition, String code, long userId, int tradeType, long startTime, long endTime);
	
	public List<NPendingLog> readByPendingNo(String partition, String code, String pendingNo, long userId);
	
	public List<NPendingLog> readPendingByUserIdAndSymbol(String partition, String code, long userId, String orderIds);
}
