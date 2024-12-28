package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.NAccountContract;

public interface NAccountContractDao<T> extends BaseDaoI<T> {
	
	public int updateAmount(String partition, long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle);

	int updateAmountCanFS(String partition, long userID, String code, BigDecimal initBtc, BigDecimal btc, BigDecimal lockBtc, BigDecimal settle);

	public NAccountContract findByUserIDCode(String partition, long userID, String code);
	
	public List<NAccountContract> findByUserID(String partition, long userID);
	
	public int insert(String partition, NAccountContract account);
}
