package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.vo.PagerModel;

public interface BQSpotMarketCoinPairService {

	void createTable(String code);
	
	public List<BQSpotMarketCoinPair> findByState_new(int state);
	
	public Boolean addMarketCoinPair(BQSpotMarketCoinPair coinPair);
	
	public void update(BQSpotMarketCoinPair marketCoinPair);

	long time();
	
	public BQSpotMarketCoinPair getByName(String coinPairName);

	public BQSpotMarketCoinPair getById(long coinPairId);
	
	public int updateHighAndLow(long coinPairId, BigDecimal highest, BigDecimal lowest);
	
	public int updateHighAndLowAndLast(long coinPairId, BigDecimal highest, BigDecimal lowest, BigDecimal last);
	
	
	/************************************ for read only **************************************/
	
	public List<BQSpotMarketCoinPair> readByState_new(int state);
	
	public PagerModel readByState_pm(String coinPairName, int state);
	
	public BQSpotMarketCoinPair readByName(String coinPairName);

}
