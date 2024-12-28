package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.VLastTicker;

public interface VLastTickerService {
	
	public VLastTicker findByCode(String code);
	
	public int insertLastTicker(VLastTicker entity);
	
	public int insertOrUploadLastTicker(VLastTicker entity);
	
	public int deleteLastTicker(int f_ticker_id);
	
	
	
	///************************************ for read only **************************************/
	
	public VLastTicker readByCode(String code);
}
