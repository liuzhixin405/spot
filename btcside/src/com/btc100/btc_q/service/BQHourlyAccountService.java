package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQHourlyAccount;

public interface BQHourlyAccountService {

	void save(BQHourlyAccount account);

	BigDecimal getMinAmountByTime(long startTime, long endTime, long uId, int currencyId);
	
	List<Integer> findUserIdOverZero(long startTime, long endTime, int currencyId);

	void createHourlyAccountBak();

	void insertHourlyAccountBak(long endTime);
}
