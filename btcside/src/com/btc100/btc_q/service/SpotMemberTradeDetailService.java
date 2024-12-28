package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONArray;

import com.btc100.btc_q.entity.SpotMemberTradeDetail;
import com.btc100.btc_q.vo.PagerModel;


public interface SpotMemberTradeDetailService {

	/**
	 * 交易详情分页
	 * @param transactionCode 合约号
	 * @return
	 */
	public PagerModel findModel(String coinPairnName);
	/**
	 * 按人查询交易详情
	 * @param transactionCode
	 * @param buyid
	 * @param sellid
	 * @return
	 */
	PagerModel findModel(String coinPairnName, Integer buyid, Integer sellid);
	
	/**
	 * 按人查询总交易量
	 * @param transactionCode
	 * @param buyid
	 * @param sellid
	 * @return
	 */
	double findTotal(String coinPairnName, Integer buyid, Integer sellid);
	
	public JSONArray getAllJsonTopN(String bqCode, int n);
	
	public Integer getLatesetTradeTime(String transactionCode, long beforeTime);

	public void save(SpotMemberTradeDetail tradeDetail);
	
	public void deleteTradeScalpingDetail(String vCode, long scalpingUid, long beforeTime);
	
	public BigDecimal findAvgPriceBetweenTime(String bqCode, long startTime, long endTime);
	
	public BigDecimal getInTimeNoScalpingAmount(String code, long startTime, long endTime, long scalUserId);

	void createMemberTradeDetailBak(String code);
	
	void insertMemberTradeDetailBak(String code, long endTime);
	
	List<Object[]> findNextFromId(String code, long id, int limit);
	
	
	
	///************************************ for read only **************************************/
	
	public JSONArray readAllJsonTopN(String bqCode, int n);
	
	public BigDecimal readLastPriceBeforeTime(String bqCode, long endTime);
}
