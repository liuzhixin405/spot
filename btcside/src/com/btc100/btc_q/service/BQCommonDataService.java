package com.btc100.btc_q.service;

import java.math.BigDecimal;

public interface BQCommonDataService {

	public BigDecimal readShareLoss(String transactionCode);

	/**
	 * 更改分摊金额
	 * @param data
	 */
	public void updateCommonData(String bqCode, BigDecimal data);

}
