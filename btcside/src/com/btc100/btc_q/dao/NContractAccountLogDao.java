package com.btc100.btc_q.dao;

import java.util.List;

import com.btc100.btc_q.entity.NContractAccountLog;

public interface NContractAccountLogDao<T> extends BaseDaoI<T> { 
	
	public NContractAccountLog insertLog(final String partition, final NContractAccountLog contractLog);
	
	List<Object[]> findSumZRZCRecord(String partition, String vCode, long userId);
	
	void createAccountLogBak(String partition, String code);
	
	void insertAccountLogBak(String partition, String code, long endTime);
	
	void deleteAccountLog(String partition, String code, long endTime);
}
