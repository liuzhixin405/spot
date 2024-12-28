package com.btc100.btc_q.dao;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.VFundingFeeRecord;
import com.btc100.btc_q.vo.PagerModel;

public interface VFundingFeeRecordDao<T> extends BaseDaoI<T> {

	public VFundingFeeRecord insertRecord(VFundingFeeRecord record);
	
	public BigDecimal sumFundingFee(String symbol, long userId);
	
	PagerModel findBySymbol(String symbol, long uid);
	
}
