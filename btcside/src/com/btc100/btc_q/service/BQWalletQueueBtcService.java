package com.btc100.btc_q.service;

import java.util.List;
import java.util.Map;

import com.btc100.btc_q.entity.BQWalletQueueBtc;
import com.btc100.btc_q.vo.PagerModel;

public interface BQWalletQueueBtcService {

	BQWalletQueueBtc get(int currencyId);
	
	BQWalletQueueBtc getErc20();
	
	BQWalletQueueBtc getTrc20();
	
	boolean lock(int walletId);

	int deleteById(int walletId);

	PagerModel findAll(int currencyId);
	
	Map<String,Object> saveList(int currencyId, List<BQWalletQueueBtc> list);
}
