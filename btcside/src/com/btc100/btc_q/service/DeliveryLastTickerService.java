package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.DeliveryLastTicker;

public interface DeliveryLastTickerService {
	
	public int insertLastTicker(DeliveryLastTicker entity);
	
	public int insertOrUploadLastTicker(DeliveryLastTicker entity);
	
	public int deleteLastTicker(int f_ticker_id);
	
	
	/************************************ for read only **************************************/
	
	public DeliveryLastTicker readByCode(String code);
}
