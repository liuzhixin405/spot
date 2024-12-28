package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.NLastTicker;

public interface NLastTickerService {
	
	public NLastTicker findByCode(String partition, String code);
	
	public void insertLastTicker(String partition, NLastTicker entity);
	
	public int insertOrUploadLastTicker(String partition, NLastTicker entity);
	
	public int deleteLastTicker(String partition, int f_ticker_id);
	
	
	
	///************************************ for read only **************************************/
	
	public NLastTicker readByCode(String partition, String code);
}
