package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONArray;

import com.btc100.btc_q.entity.DeliveryMemberTradeDetail;
import com.btc100.btc_q.vo.CountVo;
import com.btc100.btc_q.vo.PagerModel;


public interface DeliveryMemberTradeDetailService {

	/**
	 * 按人查询交易详情
	 * @param transactionCode
	 * @param buyid
	 * @param sellid
	 * @return
	 */
	PagerModel findModel(String transactionCode, int buyid, int sellid, long startTime,
			long endTime, String quantitativeId, String tag);
	
	List<DeliveryMemberTradeDetail> findByTime(String transactionCode, int buyid, int sellid, long startTime,
			long endTime, String quantitativeId, String tag);
	
	/**
	 * 按人查询总交易量
	 * @param transactionCode
	 * @param buyid
	 * @param sellid
	 * @return
	 */
	double findTotal(String transactionCode, int buyid, int sellid, long startTime,
			long endTime, String quantitativeId, String tag);

	public JSONArray getAllJsonTopN(String bqCode, int n);

	/**
	 * 查询统计（成交量，类型，均价）
	 * @param bqCode 合约号
	 * @param type 类型（1买2卖）
	 * @param starttime 起始时间
	 * @param endtime 截止时间
	 * @return
	 */
	public List<CountVo> finCountByTime(String bqCode, int type, long startTime, long endTime);
	
	/**
	 * 查询净交易
	 * @param bqCode 合约号
	 * @param dirType 方向（1买2卖）
	 * @return
	 */
	public PagerModel findDetailsByPara(String bqCode, int dirType);
	
	
	public CountVo findScalpingByTime(String bqCode, long startTime, long endTime);
	
	public void save(DeliveryMemberTradeDetail tradeDetail);

	public void deleteVTradeScalpingDetail(String vCode, long scalpingUid, long beforeTime);
	
	PagerModel findQuantitativeModel(String code,long startTime, long endTime, long quantitativeId);
	
	BigDecimal getQuantitativeFee(String code,long startTime, long endTime, long quantitativeId);
	
	/**
	 * 统计与量化用户的成交额，剔除自成交
	 * @param code
	 * @param startTime
	 * @param endTime
	 * @param quantUid
	 * @param initiatively 不为空时： 统计主动与量化用户成交，为空时：统计所有与量化用户成交
	 * @return
	 */
	BigDecimal getTradeAmountWithQuant(String code, long startTime, long endTime, long quantUid, String initiatively);
	
	void createDeliveryMemberTradeDetailBak(String code);
	
	void insertDeliveryMemberTradeDetailBak(String code, long endTime);
	
	List<Object[]> findNextFromId(String code, long id, int limit);
	
	
	/************************************ for read only **************************************/
	
	List<DeliveryMemberTradeDetail> readByTime(String code, int buyid, int sellid, String quantitativeId, long startTime,
			long endTime, long from, String direct, int limit);
	
	List<DeliveryMemberTradeDetail> readFromBakByTime(String code, int buyid, int sellid, String quantitativeId, long startTime,
			long endTime, long from, String direct, int limit);
	
	public JSONArray readAllJsonTopN(String bqCode, int n);
}
