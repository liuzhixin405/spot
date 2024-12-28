package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.vo.PagerModel;

public interface TradeLogService {

	
	public BigDecimal findHighPrice(String code, long yesterdayTime);

	public BigDecimal findLowPrice(String code, long yesterdayTime);

	public BigDecimal findFirstPrice(String code, long time);
	
	public BigDecimal findPriceAtTime(String code, long time);
	
	public BigDecimal findPriceLastClose(String code, long time);
	
	public BigDecimal getInTimeHighPrice(String code, long startTime, long endTime);

	public BigDecimal getInTimeLowPrice(String code, long startTime, long endTime);

	public BigDecimal getInTimeLastPrice(String code, long startTime, long endTime);
	
	public BigDecimal getInTimeFirstPrice(String code, long startTime, long endTime);
	
	public BigDecimal getInTimeVol(String code, long startTime, long endTime);

	/**
	 * 24小时内的成交量
	 * @param bqCode
	 * @param yesterdayTime
	 * @return
	 */
	public BigDecimal findBTCVol(String bqCode, long yesterdayTime);
	
	BigDecimal findTradeBTC(String code, long yesterdayTime);

	public PagerModel findByUserIdAndTransaction(String bqCode, long fUserId, int czlxDdbc, long startTime, long endTime);
	
//	public BigDecimal getTradeAmount(String tranCode, String string, long yesterdayTime);

	public BigDecimal getLastDayAgoByCode(String bqCode);
	
	public BigDecimal getLastHourAgoByCode(String bqCode);

	public void saveTradeLog(TradeLog tradeLog);
	
	public void deleteVTradeScalpingLog(String vCode, long scalpingUid, long beforeTime);

	public BigDecimal findFeeByTimeCode(String bqCode, long userID, long starttime, long endtime);

	public List<TradeLog> findUserTurnoverBetweenTime(String bqCode, long beginTime, long endTime, long depthUid, long quantUid);
	
	public List<TradeLog> findByUserAndTime(String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	BigDecimal getInTimeNoScalpingVol(String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getInTimeNoScalpingFee(String code, long startTime, long endTime, long scalUserId);
	
	long getTradeUserCount(String code, long startTime, long endTime, long scalUserId);
	
	List<Integer> getTradeUserIds(String code, long startTime, long endTime, long scalUserId);
	
	BigDecimal getVolByTimeAndTradeType(String code, long startTime, long endTime, long scalUserId, long userId,
			int tradeType);
	
	BigDecimal getFeeByTimeAndUser(String code, long startTime, long endTime, long scalUserId, long userId);
	
	BigDecimal getVol(String code, long startTime, long endTime, long userId, int tradeType);

	BigDecimal getTradeAmount(String code, long startTime, long endTime, long userId, int tradeType);
	
	BigDecimal getIncome(String code, long startTime, long endTime, long userId, int tradeType);
	
	List<Object[]> findIncome(String code, long startTime, long endTime, long except);
	
	Long getLastTradeId(String code, long userId);
	
	List<TradeLog> findGtFrom(String bqCode, long userId, long from, int limit);
	
	Long getTime();
	
	void createTradeLogBak(String code);
	
	void insertTradeLogBak(String code, long endTime);

	public List<TradeLog> findUidByCodeAndTimeGroupUid(String bqCode, long startTime, long endTime);
	
	///************************************ for read only **************************************/
	
	public BigDecimal readFirstPrice(String code, long time);
	
	public List<TradeLog> readAllByUserIdTopN(String bqCode, long userId, int n);
	
	public PagerModel readByUserIdAndTransaction(String bqCode, long fUserId, int czlxDdbc, long startTime, long endTime);
	
	public BigDecimal readLastDayAgoByCode(String bqCode);
	
	public BigDecimal readLastHourAgoByCode(String bqCode);
	
	public List<TradeLog> readByUserAndTime(String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit);
	
	public List<TradeLog> readFromBakByUserAndTime(String bqCode, long userId, long from, String direct, long startTime, long endTime, int limit);
}
