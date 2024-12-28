package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONArray;

import com.btc100.btc_q.entity.NMemberTradeDetail;
import com.btc100.btc_q.vo.CountVo;
import com.btc100.btc_q.vo.PagerModel;


public interface NMemberTradeDetailService {

	/**
	 * 按人查询交易详情
	 * @param transactionCode
	 * @param buyid
	 * @param sellid
	 * @return
	 */
	PagerModel findModel(String partition, String transactionCode, int buyid, int sellid, long startTime,
			long endTime, String quantitativeId, String tag);
	
	List<NMemberTradeDetail> findByTime(String partition, String transactionCode, int buyid, int sellid, long startTime,
			long endTime, String quantitativeId, String tag);
	
	/**
	 * 按人查询总交易量
	 * @param transactionCode
	 * @param buyid
	 * @param sellid
	 * @return
	 */
	double findTotal(String partition, String transactionCode, int buyid, int sellid, long startTime,
			long endTime, String quantitativeId, String tag);

	public JSONArray getAllJsonTopN(String partition, String bqCode, int n);

	/**
	 * 查询统计（成交量，类型，均价）
	 * @param bqCode 合约号
	 * @param type 类型（1买2卖）
	 * @param starttime 起始时间
	 * @param endtime 截止时间
	 * @return
	 */
	public List<CountVo> finCountByTime(String partition, String bqCode, int type, long startTime, long endTime);
	
	/**
	 * 查询净交易
	 * @param bqCode 合约号
	 * @param dirType 方向（1买2卖）
	 * @return
	 */
	public PagerModel findDetailsByPara(String partition, String bqCode, int dirType);
	
	
	public CountVo findScalpingByTime(String partition, String bqCode, long startTime, long endTime);
	
	public void save(String partition, NMemberTradeDetail tradeDetail);

	public void deleteVTradeScalpingDetail(String partition, String vCode, long scalpingUid, long beforeTime);
	
	PagerModel findQuantitativeModel(String partition, String code,long startTime, long endTime, long quantitativeId);
	
	BigDecimal getQuantitativeFee(String partition, String code,long startTime, long endTime, long quantitativeId);
	
	/**
	 * 统计与量化用户的成交额，剔除自成交
	 * @param code
	 * @param startTime
	 * @param endTime
	 * @param quantUid
	 * @param type type>0时， type=1统计主动与量化用户成交（量化为maker）；type=2,量化为taker。type=0时，统计所有与量化用户成交
	 * @return
	 */
	BigDecimal getTradeAmountWithQuant(String partition, String code, long startTime, long endTime, long quantUid, int type);
	
	/**
	 * 统计与量化用户的成交额，剔除自成交
	 * @param code
	 * @param startTime
	 * @param endTime
	 * @param quantUid
	 * @param type type>0时， type=1统计主动与量化用户成交（量化为maker）；type=2,量化为taker。type=0时，统计所有与量化用户成交
	 * @return
	 */
	PagerModel findTradeWithQuant(String partition, String code, long startTime, long endTime, long quantUid, int type);
	
	void createNMemberTradeDetailBak(String partition, String code);
	
	void insertNMemberTradeDetailBak(String partition, String code, long endTime);
	
	List<Object[]> findNextFromId(String partition, String code, long id, int limit);
	
	
	/************************************ for read only **************************************/
	
	List<NMemberTradeDetail> readByTime(String partition, String code, int buyid, int sellid, String quantitativeId, long startTime,
			long endTime, long from, String direct, int limit);
	
	List<NMemberTradeDetail> readFromBakByTime(String partition, String code, int buyid, int sellid, String quantitativeId, long startTime,
			long endTime, long from, String direct, int limit);
	
	List<NMemberTradeDetail> readByMakerTaker(String partition, String code, long quantUid, int type, long startTime, long endTime,
			long from, String direct, int limit, boolean isBak);

	public JSONArray readAllJsonTopN(String partition, String bqCode, int n);
}
