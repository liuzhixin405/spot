package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.vo.TickerVo;

public interface BQPriceIndexCacheService {

	public BQPriceIndexCache getBtcIndexPrice(String typeCode);

	public void updateBtcCache(TickerVo vo, BigDecimal price);

	
	/************************************ for read only **************************************/
	
	public BQPriceIndexCache readByCode(String typeCode);
}
