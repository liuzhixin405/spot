package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQStopLossLog;

public interface BQStopLossLogService {
	
	public void saveBQStopLossLog(BQStopLossLog bqStopLossLog);
	
	public void saveBQStopLossLogList(List<BQStopLossLog> data);
	
}
