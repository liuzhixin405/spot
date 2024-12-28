package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.NTradeLog;
import com.btc100.btc_q.vo.PagerModel;

public interface NTradeLogDao<T> extends BaseDaoI<T> {

	NTradeLog insertLog(String partition, NTradeLog tradeLog);
	
	Boolean zNTradeLogIsExist(String partition, String code, long startTime);
	
	void createNTradeLogBak(String partition, String code);
	
	void insertNTradeLogBak(String partition, String code, long endTime);
	
	void deleteNTradeLog(String partition, String code, long endTime);
	
	Object[] getFeeFromZNTradeLogByUser(String partition, String code, long userId, long startTime);

	public BigDecimal findHighPrice(String partition, String code, long yesterdayTime);

	public BigDecimal findLowPrice(String partition, String code, long yesterdayTime);

	public BigDecimal findLastPrice(String partition, String code);
	
	public BigDecimal findFirstPrice(String partition, String code, long time);
	
	public BigDecimal findLastPrice(String partition, String code, long time);
	
	public BigDecimal findPriceAtTime(String partition, String code, long time);
	
	public BigDecimal findPriceLastClose(String partition, String code, long time);

	public BigDecimal findVol(String partition, String code, long yesterdayTime);
	
	BigDecimal findTradeBTC(String partition, String code, long yesterdayTime);

	public List<NTradeLog> getAllByUserIdTopN(String partition, String bqCode, long userId, int n);

	List<NTradeLog> findByCode(String partition, String code);

	List<NTradeLog> getAllByUserId(String partition, String transactionCode, long fUserId, int czlx);

	PagerModel getPageByUserId(String partition, String code, long fUserId, int czlx, long startTime, long endTime);
	
	long maxTime(String partition, String code, String type);
	
	long minTime(String partition, String code, String type);

	BigDecimal getInTimeHighPrice(String partition, String code, long startTime, long endTime);

	BigDecimal getInTimeLowPrice(String partition, String code, long startTime, long endTime);

	BigDecimal getInTimeLastPrice(String partition, String code, long startTime, long endTime);
	
	BigDecimal getInTimeFirstPrice(String partition, String code, long startTime, long endTime);
	
	BigDecimal getInTimeVol(String partition, String code, long startTime, long endTime);

	Object[] getAllIncomeAndFeeByUser(String partition, String bqCode, long userId, long startTime, long endTime);
	
	Object[] getAllBakIncomeAndFeeByUser(String partition, String bqCode, long userId, long startTime, long endTime);
	
	BigDecimal getInTimeNoScalpingVol(String partition, String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getInTimeNoScalpingDeposit(String partition, String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getInTimeNoScalpingFee(String partition, String code, long startTime, long endTime, long scalUserId);
	
	long getTradeUserCount(String partition, String code, long startTime, long endTime, long scalUserId);
	
	List<Integer> getTradeUserIds(String partition, String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getVolByTimeAndTradeType(String partition, String code, long startTime, long endTime, long scalUserId, long userId, int tradeType);
	
	BigDecimal getFeeByTimeAndUser(String partition, String code, long startTime, long endTime, long scalUserId, long userId);
	
	BigDecimal getVol(String partition, String code, long startTime, long endTime, long userId, int tradeType);
	
	BigDecimal getTradeAmount(String partition, String code, long startTime, long endTime, long userId, int tradeType);
	
	BigDecimal getIncome(String partition, String code, long startTime, long endTime, long userId, int tradeType);
	
	List<Object[]> findIncome(String partition, String code, long startTime, long endTime, long except);
}
