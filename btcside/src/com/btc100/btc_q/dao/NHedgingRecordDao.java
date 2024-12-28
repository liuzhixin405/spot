package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.NHedgingRecord;

public interface NHedgingRecordDao<T> extends BaseDaoI<T> {
	
	public int insert(String partition, NHedgingRecord record);
	
}
