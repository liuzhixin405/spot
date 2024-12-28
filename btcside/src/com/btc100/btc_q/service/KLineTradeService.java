package com.btc100.btc_q.service;

import org.json.JSONArray;

import com.btc100.btc_q.entity.KLineTrade;

public interface KLineTradeService {

	public void insert(KLineTrade trade, String timeType);
	
	public KLineTrade updateFromOneMinute(KLineTrade oneMinuteTrade, String timeType, long time);

	public JSONArray findPreJson(String code, String timeType);
	
	public JSONArray findByTimeJson(String code, String timeType, long startTime, long endTime);
	
	public long getlastTime(String code, String timeType);
	
	public long getMinTime(String code, String timeType);
	
	public KLineTrade getAtTime(String code, String timeType, long time);


	/************************************ for read only **************************************/
	
	public JSONArray readPreJson(String code, String timeType);
}
