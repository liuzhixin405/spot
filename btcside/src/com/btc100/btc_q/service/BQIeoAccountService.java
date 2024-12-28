package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQIeoAccount;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

public interface BQIeoAccountService {
	
	void recharge(long uid, int currencyId, int operation, BigDecimal amount, String note) throws ServiceException;
	
	void changeRecord(long id, BigDecimal amount, String note) throws ServiceException;
	
	int updateAccount(long uid, int currencyId, BigDecimal initial, BigDecimal locked, BigDecimal unlocked) ;
	
	PagerModel findByUserId(long uid, String order);
	
	BQIeoAccount sumAll();
	
	List<BQIeoAccount> findAll();
	
	BQIeoAccount getAccountByUserId(long uid);
	
	List<BQIeoAccount> findAboveZero();
}
