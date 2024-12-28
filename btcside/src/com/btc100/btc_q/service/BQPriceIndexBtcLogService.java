package com.btc100.btc_q.service;

import java.math.BigDecimal;

public interface BQPriceIndexBtcLogService {

	public void saveBtcBpi(int typeId, BigDecimal price, long dateline);

	//public BpiVO getBpiVO();

}
