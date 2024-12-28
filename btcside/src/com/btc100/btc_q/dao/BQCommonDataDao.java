package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

public interface BQCommonDataDao<T> extends BaseDaoI<T> { 
	public List<T> findByCode(String scode);
	
	public int UpdateData(int id,BigDecimal data);
}
