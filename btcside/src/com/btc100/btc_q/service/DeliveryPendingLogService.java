package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.DeliveryPendingLog;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryPendingLogService {

	public void savePendingLog(DeliveryPendingLog DeliveryPendingLog);
	
	public void updateActionByPendingNo(String bqCode, long pendingNo, long userId, int action);
	
	public List<DeliveryPendingLog> findByPendingNo(String code, String pendingNo, long userId);

	void createDeliveryPendingLogBak(String code);
	
	void insertDeliveryPendingLogBak(String code, long endTime);
	
	
	///************************************ for read only **************************************/
	
	List<DeliveryPendingLog> readListByUserId(String code, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	PagerModel readPagerMode(String code, long userId, int tradeType, long startTime, long endTime);
	
	public List<DeliveryPendingLog> readByPendingNo(String code, String pendingNo, long userId);
	
}
