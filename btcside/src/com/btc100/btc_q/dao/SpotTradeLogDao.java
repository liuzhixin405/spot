package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.SpotTradeLog;
import com.btc100.btc_q.vo.PagerModel;

public interface SpotTradeLogDao<T> extends BaseDaoI<T> {

	SpotTradeLog insertLog(SpotTradeLog tradeLog);
	
	void createTradeLogBak(String code);
	
	void insertTradeLogBak(String code, long endTime);
	
	void deleteTradeLog(String code, long endTime);

	public BigDecimal findHighPrice(String code, long yesterdayTime);

	public BigDecimal findLowPrice(String code, long yesterdayTime);

	public BigDecimal findLastPrice(String code);
	
	public BigDecimal findFirstPrice(String code, long time);
	
	public BigDecimal findLastPrice(String code, long time);

	public BigDecimal findVol(String code, long yesterdayTime);
	
	BigDecimal findTradeBTC(String code, long yesterdayTime);

	public List<SpotTradeLog> getAllByUserIdTopN(String bqCode, long userId, int n);

	List<SpotTradeLog> findByCode(String code);

	List<SpotTradeLog> getAllByUserId(String transactionCode, long fUserId);

	PagerModel getPageByUserId(String code, long fUserId, int direction, long startTime, long endTime);
	
	PagerModel getPageByUserId(String code, long fUserId, int direction);
	
	public long maxTime(String code, String type);
	
	public long minTime(String code, String type);

	public BigDecimal getInTimeHighPrice(String code, long startTime, long endTime);

	public BigDecimal getInTimeLowPrice(String code, long startTime, long endTime);

	public BigDecimal getInTimeLastPrice(String code, long startTime, long endTime);
	
	public BigDecimal getInTimeFirstPrice(String code, long startTime, long endTime);
	
	public BigDecimal getInTimeVol(String code, long startTime, long endTime);
	
	public BigDecimal sumBuyIn(String code, long uid, int currencyId);
	public BigDecimal sumSellOut(String code, long uid, int currencyId);
	public BigDecimal sumBuySpend(String code, long uid, int currencyId);
	public BigDecimal sumSellObtain(String code, long uid, int currencyId);
	
	public BigDecimal sumBakBuyIn(String code, long uid, int currencyId);
	public BigDecimal sumBakSellOut(String code, long uid, int currencyId);
	public BigDecimal sumBakBuySpend(String code, long uid, int currencyId);
	public BigDecimal sumBakSellObtain(String code, long uid, int currencyId);
	
	BigDecimal getFeeInTimeNoScalping(String code, int direction, long startTime, long endTime, long scalUserId);
	
	BigDecimal getBfxFeeNoScalping(String code, int direction, long startTime, long endTime, long scalUserId);
	
	long getTradeUserCount(String code, long startTime, long endTime, long scalUserId);
	
	List<Integer> getTradeUserIds(String code, long startTime, long endTime, long scalUserId);

}
