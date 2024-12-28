package com.btc100.btc_q.dao;

import java.util.List;
import java.util.Map;

public interface BQWalletQueueBtcDao<T> extends BaseDaoI<T> {

	public Map<String, Object> insertDatas(List<com.btc100.btc_q.entity.BQWalletQueueBtc> list);
	
	public long getCount(int currencyId);
	
	public long getErc20Count();
	
	public long getTrc20Count();
}
