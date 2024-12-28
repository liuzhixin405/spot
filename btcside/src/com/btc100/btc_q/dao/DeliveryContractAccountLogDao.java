package com.btc100.btc_q.dao;

import java.util.List;

import com.btc100.btc_q.entity.DeliveryContractAccountLog;

public interface DeliveryContractAccountLogDao<T> extends BaseDaoI<T> {

	DeliveryContractAccountLog insertLog(final DeliveryContractAccountLog contractLog);

	List<Object[]> findSumZRZCRecord(String bqCode, long userId);

	void createAccountLogBak(String code);

	void insertAccountLogBak(String code, long endTime);

	void deleteAccountLog(String code, long endTime);
}
