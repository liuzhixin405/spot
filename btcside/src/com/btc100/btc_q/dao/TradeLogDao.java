package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.vo.PagerModel;

public interface TradeLogDao<T> extends BaseDaoI<T> {

	TradeLog insertLog(TradeLog tradeLog);
	
	Boolean zTradeLogIsExist(String code, long startTime);
	
	void createTradeLogBak(String code);
	
	void insertTradeLogBak(String code, long endTime);
	
	void deleteTradeLog(String code, long endTime);
	
	Object[] getFeeFromZTradeLogByUser(String code, long userId, long startTime);

	public BigDecimal findHighPrice(String code, long yesterdayTime);

	public BigDecimal findLowPrice(String code, long yesterdayTime);

	public BigDecimal findLastPrice(String code);
	
	public BigDecimal findFirstPrice(String code, long time);
	
	public BigDecimal findLastPrice(String code, long time);
	
	public BigDecimal findPriceAtTime(String code, long time);
	
	public BigDecimal findPriceLastClose(String code, long time);

	public BigDecimal findVol(String code, long yesterdayTime);
	
	BigDecimal findTradeBTC(String code, long yesterdayTime);

	public List<TradeLog> getAllByUserIdTopN(String bqCode, long userId, int n);

	List<TradeLog> findByCode(String code);

	List<TradeLog> getAllByUserId(String transactionCode, long fUserId, int czlx);

	PagerModel getPageByUserId(String code, long fUserId, int czlx, long startTime, long endTime);
	
	long maxTime(String code, String type);
	
	long minTime(String code, String type);

	BigDecimal getInTimeHighPrice(String code, long startTime, long endTime);

	BigDecimal getInTimeLowPrice(String code, long startTime, long endTime);

	BigDecimal getInTimeLastPrice(String code, long startTime, long endTime);
	
	BigDecimal getInTimeFirstPrice(String code, long startTime, long endTime);
	
	BigDecimal getInTimeVol(String code, long startTime, long endTime);

	Object[] getAllIncomeAndFeeByUser(String bqCode, long userId, long startTime, long endTime);
	
	Object[] getAllBakIncomeAndFeeByUser(String bqCode, long userId, long startTime, long endTime);
	
	BigDecimal getInTimeNoScalpingVol(String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getInTimeNoScalpingDeposit(String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getInTimeNoScalpingFee(String code, long startTime, long endTime, long scalUserId);
	
	long getTradeUserCount(String code, long startTime, long endTime, long scalUserId);
	
	List<Integer> getTradeUserIds(String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getVolByTimeAndTradeType(String code, long startTime, long endTime, long scalUserId, long userId, int tradeType);
	
	BigDecimal getFeeByTimeAndUser(String code, long startTime, long endTime, long scalUserId, long userId);
	
	BigDecimal getVol(String code, long startTime, long endTime, long userId, int tradeType);
	
	BigDecimal getTradeAmount(String code, long startTime, long endTime, long userId, int tradeType);
	
	BigDecimal getIncome(String code, long startTime, long endTime, long userId, int tradeType);
	
	List<Object[]> findIncome(String code, long startTime, long endTime, long except);
}
