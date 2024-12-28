package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.VPendingLog;
import com.btc100.btc_q.vo.PagerModel;

public interface VPendingLogService {

	public void savePendingLog(VPendingLog pendingLog);
	
	void createPendingLogBak(String code);
	
	void insertPendingLogBak(String code, long endTime);
	
	void updateActionByPendingNo(String bqCode, long pendingNo, long userId, int action);
	
	
	
	///************************************ for read only **************************************/
	
	List<VPendingLog> readListByUserId(String code, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	PagerModel readPagerMode(String code, long userId, int tradeType, long startTime, long endTime);
}
