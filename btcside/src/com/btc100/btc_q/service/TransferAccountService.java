package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.exception.ServiceException;

public interface TransferAccountService {

	/**
	 * 转账
	 * @param uid 用户ID
	 * @param bqCode 合约号
	 * @param amount 数量（正值代表从钱包往总账户转，负值反之）
	 * @throws ServiceException
	 */
	public String updateAccountForZC(long uid, String bqCode, BigDecimal amount) throws ServiceException;
	
	public String updateAccountToOTC(BQCurrencyType currency, long uid, BigDecimal amount) throws ServiceException;
	
	public String updateAccountFromOTC(BQCurrencyType currency, long uid, BigDecimal amount) throws ServiceException;

}
