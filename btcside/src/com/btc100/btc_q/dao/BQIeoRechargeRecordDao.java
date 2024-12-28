package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQIeoRechargeRecordDao<T> extends BaseDaoI<T> { 
	
	int updateRecord(long id, BigDecimal locked, BigDecimal unlocked);
}
