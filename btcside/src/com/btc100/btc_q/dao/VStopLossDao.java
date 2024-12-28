package com.btc100.btc_q.dao;

public interface VStopLossDao<T> extends BaseDaoI<T> {

	void deleteByTransactionMemberId(Long transactionMemberId); }
