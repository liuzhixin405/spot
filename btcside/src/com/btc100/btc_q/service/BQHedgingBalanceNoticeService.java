package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQHedgingBalanceNotice;
import com.btc100.btc_q.vo.PagerModel;

public interface BQHedgingBalanceNoticeService {
	
	BQHedgingBalanceNotice get(long id);
	
	void save(BQHedgingBalanceNotice notice);
	
	void update(BQHedgingBalanceNotice notice);
	
	void delete(long id);
	
	BQHedgingBalanceNotice getByTypeAndCoinName(int type, String coinName);
	
	
	/************************************ for read only **************************************/
	
	PagerModel readPager(int type, String coinName);
	
	BQHedgingBalanceNotice readByTypeAndCoinName(int type, String coinName);
}

