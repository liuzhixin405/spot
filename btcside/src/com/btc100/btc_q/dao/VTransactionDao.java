package com.btc100.btc_q.dao;

import com.btc100.btc_q.entity.VTransaction;

public interface VTransactionDao<T> extends BaseDaoI<T> {

	void createTransactionRelated(String code);

	VTransaction findTranByCode(String code);
}
