package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.VTradeLog;
import com.btc100.btc_q.vo.PagerModel;

public interface VTradeLogService {

	public BigDecimal findHighPrice(String code, long yesterdayTime);

	public BigDecimal findLowPrice(String code, long yesterdayTime);

	public BigDecimal findFirstPrice(String code, long time);
	
	public BigDecimal findPriceAtTime(String code, long time);
	
	public BigDecimal getInTimeHighPrice(String code, long startTime, long endTime);

	public BigDecimal getInTimeLowPrice(String code, long startTime, long endTime);

	public BigDecimal getInTimeLastPrice(String code, long startTime, long endTime);
	
	public BigDecimal getInTimeFirstPrice(String code, long startTime, long endTime);
	
	public BigDecimal getInTimeVol(String code, long startTime, long endTime);

	/**
	 * 24小时内的成交量
	 * @param vCode
	 * @param yesterdayTime
	 * @return
	 */
	public BigDecimal findBTCVol(String vCode, long yesterdayTime);
	
	BigDecimal findTradeBTC(String code, long yesterdayTime);
	
	public PagerModel findByUserIdAndTransaction(String bqCode, long fUserId, int czlxDdbc);

//	public BigDecimal getTradeAmount(String tranCode, String string, long yesterdayTime);

	public void saveVTradeLog(VTradeLog tradeLog);
	
	public void deleteVTradeScalpingLog(String vCode, long scalpingUid, long beforeTime);
	
	BigDecimal getInTimeNoScalpingVol(String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getInTimeNoScalpingFee(String code, long startTime, long endTime, long scalUserId);
	
	long getTradeUserCount(String code, long startTime, long endTime, long scalUserId);
	
	List<Integer> getTradeUserIds(String code, long startTime, long endTime, long scalUserId);
	
	List<Object[]> getIncomeAndDepoistByUser(String bqCode, long except, long beginTime, long endTime);
	
	List<Object[]> findIncome(String code, long startTime, long endTime, long except);
	
	void createTradeLogBak(String code);
	
	void insertTradeLogBak(String code, long endTime);

	
	
	
	///************************************ for read only **************************************/
	
	public BigDecimal readFirstPrice(String code, long time);
	
	public PagerModel readByUserIdAndTransaction(String bqCode, long fUserId, int czlxDdbc, long startTime, long endTime);
	
	public List<VTradeLog> readByUserAndTime(String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	public List<VTradeLog> readFromBakByUserAndTime(String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	public List<VTradeLog> readByCodeAndUserAndTime(String vCode, long userId, long startTime, long endTime, int limit);
}
