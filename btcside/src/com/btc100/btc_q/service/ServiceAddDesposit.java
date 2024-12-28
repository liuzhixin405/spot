package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.exception.ServiceException;

public interface ServiceAddDesposit {
	/**
	 * 追加保证金
	 * @param Uid
	 * @param deposit
	 * @throws ServiceException 
	 */
	void AddDesposit(Long uid, BigDecimal deposit, Long hostid, int transactionID, String transactionCode, int direction) throws ServiceException;
}
