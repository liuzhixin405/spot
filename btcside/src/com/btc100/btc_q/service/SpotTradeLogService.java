package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import com.btc100.btc_q.entity.SpotTradeLog;
import com.btc100.btc_q.vo.PagerModel;

public interface SpotTradeLogService {

	public BigDecimal findHighPrice(String code, long yesterdayTime);

	public BigDecimal findLowPrice(String code, long yesterdayTime);

	public BigDecimal findFirstPrice(String code, long time);
	
	public BigDecimal getInTimeHighPrice(String code, long startTime, long endTime);

	public BigDecimal getInTimeLowPrice(String code, long startTime, long endTime);

	public BigDecimal getInTimeLastPrice(String code, long startTime, long endTime);
	
	public BigDecimal getInTimeFirstPrice(String code, long startTime, long endTime);
	
	public BigDecimal getInTimeVol(String code, long startTime, long endTime);
	
	public void deleteTradeScalpingLog(String vCode, long scalpingUid, long beforeTime);

	/**
	 * 24小时内的成交量
	 * @param bqCode
	 * @param yesterdayTime
	 * @return
	 */
	public BigDecimal findBTCVol(String bqCode, long yesterdayTime);
	
	public BigDecimal findTradeBTC(String code, long yesterdayTime);

	public PagerModel findByUserIdAndTransaction(String bqCode, long fUserId, int direction, long startTime, long endTime);
	
	public PagerModel findByUserIdAndTransaction(String bqCode, long fUserId, int direction);

	public void saveSpotTradeLog(SpotTradeLog tradeLog);

	public BigDecimal findFeeByTimeCode(String bqCode, long userID, int direction, long starttime, long endtime) throws ParseException;
	
	BigDecimal getFeeInTimeNoScalping(String code, int direction, long startTime, long endTime, long scalUserId);
	
	BigDecimal getBfxFeeNoScalping(String code, int direction, long startTime, long endTime, long scalUserId);
	
	long getTradeUserCount(String code, long startTime, long endTime, long scalUserId);
	
	List<Integer> getTradeUserIds(String code, long startTime, long endTime, long scalUserId);
	
	void createTradeLogBak(String code);
	
	void insertTradeLogBak(String code, long endTime);

	
	
	///************************************ for read only **************************************/
	
	public BigDecimal readFeeByTimeCode(String bqCode, long userID, int direction, long starttime, long endtime) throws ParseException;
	
	public List<SpotTradeLog> readByCodeAndUserAndTime(String bqCode, long userId, long startTime, long endTime, int limit);
	
	List<SpotTradeLog> readByUserAndTime(String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	List<SpotTradeLog> readFromBakByUserAndTime(String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit);
}
