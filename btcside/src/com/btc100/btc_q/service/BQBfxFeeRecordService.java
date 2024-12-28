package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.vo.PagerModel;

public interface BQBfxFeeRecordService {
	
	BigDecimal sumBfxFee(String partition, long uid, String code, long startTime, long endTime);
	
	
	/************************************ for read only **************************************/

	PagerModel readRecords(long uid, long startTime, long endTime);
	
	BigDecimal readSumBfxFee(String partition, long uid, String code, long startTime, long endTime);
}

