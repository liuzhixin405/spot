package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

public interface VAccountContractDao<T> extends BaseDaoI<T> {
	public int updateAmount(long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle);

	int updateAmountCanFS(long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle);

	public T findByUserIDCode(long userID, String code);
	
	public List<T> findByUserID(long userID);
}
