package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.NTradeLog;
import com.btc100.btc_q.vo.PagerModel;

public interface NTradeLogService {

	
	public BigDecimal findHighPrice(String partition, String code, long yesterdayTime);

	public BigDecimal findLowPrice(String partition, String code, long yesterdayTime);

	public BigDecimal findFirstPrice(String partition, String code, long time);
	
	public BigDecimal findPriceAtTime(String partition, String code, long time);
	
	public BigDecimal findPriceLastClose(String partition, String code, long time);
	
	public BigDecimal getInTimeHighPrice(String partition, String code, long startTime, long endTime);

	public BigDecimal getInTimeLowPrice(String partition, String code, long startTime, long endTime);

	public BigDecimal getInTimeLastPrice(String partition, String code, long startTime, long endTime);
	
	public BigDecimal getInTimeFirstPrice(String partition, String code, long startTime, long endTime);
	
	public BigDecimal getInTimeVol(String partition, String code, long startTime, long endTime);

	/**
	 * 24小时内的成交量
	 * @param bqCode
	 * @param yesterdayTime
	 * @return
	 */
	public BigDecimal findBTCVol(String partition, String bqCode, long yesterdayTime);
	
	BigDecimal findTradeBTC(String partition, String code, long yesterdayTime);

	public PagerModel findByUserIdAndTransaction(String partition, String bqCode, long fUserId, int czlxDdbc, long startTime, long endTime);
	
//	public BigDecimal getTradeAmount(String partition, String tranCode, String string, long yesterdayTime);

	public BigDecimal getLastDayAgoByCode(String partition, String bqCode);
	
	public BigDecimal getLastHourAgoByCode(String partition, String bqCode);

	public void saveTradeLog(String partition, NTradeLog tradeLog);
	
	public void deleteVTradeScalpingLog(String partition, String vCode, long scalpingUid, long beforeTime);

	public BigDecimal findFeeByTimeCode(String partition, String bqCode, long userID, long starttime, long endtime);

	public List<NTradeLog> findUserTurnoverBetweenTime(String partition, String bqCode, long beginTime, long endTime, long depthUid, long quantUid);
	
	public List<NTradeLog> findByUserAndTime(String partition, String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	BigDecimal getInTimeNoScalpingVol(String partition, String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getInTimeNoScalpingFee(String partition, String code, long startTime, long endTime, long scalUserId);
	
	long getTradeUserCount(String partition, String code, long startTime, long endTime, long scalUserId);
	
	List<Integer> getTradeUserIds(String partition, String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getVolByTimeAndTradeType(String partition, String code, long startTime, long endTime, long scalUserId, long userId,
			int tradeType);
	
	BigDecimal getFeeByTimeAndUser(String partition, String code, long startTime, long endTime, long scalUserId, long userId);
	
	BigDecimal getVol(String partition, String code, long startTime, long endTime, long userId, int tradeType);

	BigDecimal getTradeAmount(String partition, String code, long startTime, long endTime, long userId, int tradeType);
	
	BigDecimal getIncome(String partition, String code, long startTime, long endTime, long userId, int tradeType);
	
	List<Object[]> findIncome(String partition, String code, long startTime, long endTime, long except);
	
	Long getLastTradeId(String partition, String code, long userId);
	
	List<NTradeLog> findGtFrom(String partition, String bqCode, long userId, long from, int limit);
	
	Long getTime();
	
	void createTradeLogBak(String partition, String code);
	
	void insertTradeLogBak(String partition, String code, long endTime);
	
	public List<NTradeLog> findUidByCodeAndTimeGroupUid(String partition, String bqCode, long startTime, long endTime);
	
	///************************************ for read only **************************************/
	
	public BigDecimal readFirstPrice(String partition, String code, long time);
	
	public List<NTradeLog> readAllByUserIdTopN(String partition, String bqCode, long userId, int n);
	
	public PagerModel readByUserIdAndTransaction(String partition, String bqCode, long fUserId, int czlxDdbc, long startTime, long endTime);
	
	public BigDecimal readLastDayAgoByCode(String partition, String bqCode);
	
	public BigDecimal readLastHourAgoByCode(String partition, String bqCode);
	
	public List<NTradeLog> readByUserAndTime(String partition, String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	public List<NTradeLog> readFromBakByUserAndTime(String partition, String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit);
}
