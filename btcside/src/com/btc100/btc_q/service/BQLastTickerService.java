package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQLastTicker;

public interface BQLastTickerService {
	
	public int insertBQLastTicker(BQLastTicker entity);
	
	public int insertOrUploadBQLastTicker(BQLastTicker entity);
	
	public int deleteBQLastTicker(int f_ticker_id);
	
	
	/************************************ for read only **************************************/
	
	public BQLastTicker readByCode(String code);
}
