package com.btc100.btc_q.service;

import java.util.List;

import org.json.JSONArray;

import com.btc100.btc_q.entity.VMemberTradeDetail;
import com.btc100.btc_q.vo.CountVo;
import com.btc100.btc_q.vo.PagerModel;


public interface VMemberTradeDetailService {

	/**
	 * 按人查询交易详情
	 * @param transactionCode
	 * @param buyid
	 * @param sellid
	 * @return
	 */
	PagerModel findModel(String transactionCode, int buyid, int sellid);
	
	/**
	 * 按人查询总交易量
	 * @param transactionCode
	 * @param buyid
	 * @param sellid
	 * @return
	 */
	double findTotal(String transactionCode, int buyid, int sellid);
	
	public JSONArray getAllJsonTopN(String vCode, int n);

	public CountVo findScalpingByTime(String vCode, long startTime, long endTime);
	
	public void save(VMemberTradeDetail tradeDetail);
	
	public void deleteVTradeScalpingDetail(String vCode, long scalpingUid, long beforeTime);

	void createMemberTradeDetailBak(String code);
	
	void insertMemberTradeDetailBak(String code, long endTime);
	
	List<Object[]> findNextFromId(String code, long id, int limit);
	
	
	///************************************ for read only **************************************/
	
	public JSONArray readAllJsonTopN(String vCode, int n);
}
