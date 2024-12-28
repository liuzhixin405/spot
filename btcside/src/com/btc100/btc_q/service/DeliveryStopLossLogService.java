package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.DeliveryStopLossLog;

public interface DeliveryStopLossLogService {
	
	public void save(DeliveryStopLossLog log);
	
	public void saveList(List<DeliveryStopLossLog> data);
	
}
