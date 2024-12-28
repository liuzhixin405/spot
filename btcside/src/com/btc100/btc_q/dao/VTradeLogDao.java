package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.VTradeLog;
import com.btc100.btc_q.vo.PagerModel;

public interface VTradeLogDao<T> extends BaseDaoI<T> {

	VTradeLog insertLog(VTradeLog tradeLog);
	
	void createTradeLogBak(String code);
	
	void insertTradeLogBak(String code, long endTime);
	
	void deleteTradeLog(String code, long endTime);

	public BigDecimal findHighPrice(String code, long yesterdayTime);

	public BigDecimal findLowPrice(String code, long yesterdayTime);

	public BigDecimal findLastPrice(String code);
	
	public BigDecimal findFirstPrice(String code, long time);
	
	public BigDecimal findPriceAtTime(String code, long time);
	
	public BigDecimal findLastPrice(String code, long time);

	public BigDecimal findVol(String code, long yesterdayTime);
	
	BigDecimal findTradeBTC(String code, long yesterdayTime);

	public List<VTradeLog> getAllByUserIdTopN(String vCode, long userId, int n);

	List<VTradeLog> findByCode(String code);

	List<VTradeLog> getAllByUserId(String transactionCode, long fUserId, int czlx);

	PagerModel getPageByUserId(String bqCode, long fUserId, int czlx, long startTime, long endTime);
	
	PagerModel getPageByUserId(String code, long fUserId, int czlx);
	
	long maxTime(String code, String type);
	
	long minTime(String code, String type);

	BigDecimal getInTimeHighPrice(String code, long startTime, long endTime);

	BigDecimal getInTimeLowPrice(String code, long startTime, long endTime);

	BigDecimal getInTimeLastPrice(String code, long startTime, long endTime);
	
	BigDecimal getInTimeFirstPrice(String code, long startTime, long endTime);
	
	BigDecimal getInTimeVol(String code, long startTime, long endTime);

	Object[] getAllIncomeAndFeeByUser(String vCode, long userId);
	
	Object[] getAllBakIncomeAndFeeByUser(String vCode, long userId);
	
	BigDecimal getInTimeNoScalpingVol(String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getInTimeNoScalpingDeposit(String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getInTimeNoScalpingFee(String code, long startTime, long endTime, long scalUserId);
	
	long getTradeUserCount(String code, long startTime, long endTime, long scalUserId);
	
	List<Integer> getTradeUserIds(String code, long startTime, long endTime, long scalUserId);
	
	List<Object[]> getIncomeAndDepoistByUser(String bqCode, long except, long beginTime, long endTime);
	
	List<Object[]> findIncome(String code, long startTime, long endTime, long except);

}
