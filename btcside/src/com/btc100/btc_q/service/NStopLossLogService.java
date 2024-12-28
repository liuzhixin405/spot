package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.NStopLossLog;

public interface NStopLossLogService {
	
	public void saveNStopLossLog(String partition, NStopLossLog bqStopLossLog);
	
	public void saveNStopLossLogList(String partition, List<NStopLossLog> data);
	
}
