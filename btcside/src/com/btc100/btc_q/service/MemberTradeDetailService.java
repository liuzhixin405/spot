package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import org.json.JSONArray;

import com.btc100.btc_q.entity.MemberTradeDetail;
import com.btc100.btc_q.vo.CountVo;
import com.btc100.btc_q.vo.PagerModel;


public interface MemberTradeDetailService {

	/**
	 * 按人查询交易详情
	 * @param transactionCode
	 * @param buyid
	 * @param sellid
	 * @return
	 */
	PagerModel findModel(String transactionCode, int buyid, int sellid, long startTime,
			long endTime, String quantitativeId, String tag);
	
	List<MemberTradeDetail> findByTime(String transactionCode, int buyid, int sellid, long startTime,
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
	
	public void save(MemberTradeDetail tradeDetail);

	public void deleteVTradeScalpingDetail(String vCode, long scalpingUid, long beforeTime);
	
	PagerModel findQuantitativeModel(String code,long startTime, long endTime, long quantitativeId);
	
	BigDecimal getQuantitativeFee(String code,long startTime, long endTime, long quantitativeId);
	
	/**
	 * 统计与量化用户的成交额，剔除自成交
	 * @param code
	 * @param startTime
	 * @param endTime
	 * @param quantUid
	 * @param type type>0时， type=1统计主动与量化用户成交（量化为maker）；type=2,量化为taker。type=0时，统计所有与量化用户成交
	 * @return
	 */
	BigDecimal getTradeAmountWithQuant(String code, long startTime, long endTime, long quantUid, int type);
	
	/**
	 * 统计与量化用户的成交额，剔除自成交
	 * @param code
	 * @param startTime
	 * @param endTime
	 * @param quantUid
	 * @param type type>0时， type=1统计主动与量化用户成交（量化为maker）；type=2,量化为taker。type=0时，统计所有与量化用户成交
	 * @return
	 */
	PagerModel findTradeWithQuant(String code, long startTime, long endTime, long quantUid, int type);
	
	void createMemberTradeDetailBak(String code);
	
	void insertMemberTradeDetailBak(String code, long endTime);
	
	List<Object[]> findNextFromId(String code, long id, int limit);
	
	
	/************************************ for read only **************************************/
	
	List<MemberTradeDetail> readByTime(String code, int buyid, int sellid, String quantitativeId, long startTime,
			long endTime, long from, String direct, int limit);
	
	List<MemberTradeDetail> readFromBakByTime(String code, int buyid, int sellid, String quantitativeId, long startTime,
			long endTime, long from, String direct, int limit);
	
	List<MemberTradeDetail> readByMakerTaker(String code, long quantUid, int type, long startTime, long endTime,
			long from, String direct, int limit, boolean isBak);

	public JSONArray readAllJsonTopN(String bqCode, int n);
}
