package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQArbitrageSetting;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.DeliveryArbitrageSetting;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.KLineTrade;
import com.btc100.btc_q.entity.NArbitrageSetting;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.VArbitrageSetting;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQArbitrageSettingService;
import com.btc100.btc_q.service.BQBfxFeeRecordService;
import com.btc100.btc_q.service.BQBfxFeeSettingService;
import com.btc100.btc_q.service.BQMemberInviteRebateLogService;
import com.btc100.btc_q.service.BQMemberLoginLogService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.DeliveryArbitrageSettingService;
import com.btc100.btc_q.service.DeliveryMemberInviteRebateLogService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.KLineTradeService;
import com.btc100.btc_q.service.NAccountContractService;
import com.btc100.btc_q.service.NArbitrageSettingService;
import com.btc100.btc_q.service.NMemberTransactionService;
import com.btc100.btc_q.service.NTradeLogService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.service.TradeLogService;
import com.btc100.btc_q.service.VAccountContractService;
import com.btc100.btc_q.service.VArbitrageSettingService;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.StatisticalFormContractVo;
import com.btc100.btc_q.vo.StatisticalFormVo;
import com.btc100.btc_q.vo.VStatisticalFormContractVo;

public class BaseTask {
	
	@Autowired
	public SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQNotradetimeService bqNotradetimeService;
	@Autowired
	public BQTransactionService bqTransactionService;
	@Autowired
	public TradeLogService tradeLogService;
	@Autowired
	public KLineTradeService kLineTradeService;
	@Autowired
	public SpotTradeLogService spotTradeLogService;
	@Autowired
	public VTradeLogService vTradeLogService;
	@Autowired
	public BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	public VTransactionService vTransactionService;
	@Autowired
	private BQArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private DeliveryArbitrageSettingService dArbitrageSettingService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private DeliveryMemberTransactionService dMemberTransactionService;
	@Autowired
	private BQMemberLoginLogService bqMemberLoginLogService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private DeliveryAccountContractService dAccountContractService;
	@Autowired
	private VArbitrageSettingService vArbitrageSettingService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	@Autowired
	private VAccountContractService vAccountContractService;
	@Autowired
	private BQMemberInviteRebateLogService bqMemberInviteRebateLogService;
	@Autowired
	private DeliveryMemberInviteRebateLogService dMemberInviteRebateLogService;
	@Autowired
	private BQBfxFeeRecordService bqBfxFeeRecordService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;
	@Autowired
	public DeliveryTransactionService dTransactionService;
	@Autowired
	public DeliveryTradeLogService dTradeLogService;
	@Autowired
	private NTransactionService nTransactionService;
	@Autowired
	private NArbitrageSettingService nArbitrageSettingService;
	@Autowired
	private NTradeLogService nTradeLogService;
	@Autowired
	private NMemberTransactionService nMemberTransactionService;
	@Autowired
	private NAccountContractService nAccountContractService;
	
	public BQTransaction findCurrTrans(String scode) {
		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}

		for (BQTransaction bqTransaction : transactionList) {
			if (bqTransaction.getTransaction_code().equalsIgnoreCase(scode)) {
				return bqTransaction;
			}
		}

		return transactionList.get(0);
	}
	
	public int szCodeCanTrade(String bqCode, long time) {
		Integer nt = spyMemcachedClient.get(BTCQMemecacheKeys.Trade_Normal + "_" + bqCode);
		if (nt != null) {
			return nt;
		}

		int normalTrade = 0;
		// String strtime = bqCode.substring(2, bqCode.length()); // MMdd
//		BQTransaction bqTransaction = findCurrTrans(bqCode);
		// 交割日0点
//		int endtime = TimeUtil.getTimesmorning((int) bqTransaction.getTransaction_end_time());
		// 今天0点
//		int nomalTime = TimeUtil.getTimesmorning();
		// DateUtil.DateToUnix(DateUtil.StrToDate(DateUtil.dateToStr(DateUtil.UnixToDate(time),11)
		// + " 00:00:00", "yyyy-MM-dd HH:mm:ss"));

		// time 周六周日
		try {
			if (TimeUtil.boleanSatudaySunday(time)) {
				return normalTrade;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return normalTrade;
		}

		BQTransaction currTrans = findCurrTrans(bqCode);
		if (currTrans.getTransaction_kind() != null && currTrans.getTransaction_kind() == 1) {
			if (bqNotradetimeService.isTradeTime(currTrans.getTransaction_id())) { // 今天可以参与交易
				normalTrade = 1;
			}
		}

		spyMemcachedClient.set(BTCQMemecacheKeys.Trade_Normal + "_" + bqCode, BTCQMemecacheKeys.TIMES.TRANSACTIONLISTSZ, normalTrade);
		return normalTrade;
	}
	
	private KLineTrade getTradeFromArray(JSONArray trades, String code, long startTime, long endTime) throws JSONException {
		BigDecimal zero = new BigDecimal(0);
		BigDecimal high = zero;
		BigDecimal low = zero;
		BigDecimal first = zero;
		BigDecimal last = zero;
		BigDecimal vol = zero;
		
		if (trades != null && trades.length() >= 0) {
			for (int i = trades.length() - 1; i >= 0; i--) {
				JSONObject trade = trades.getJSONObject(i);
				long tradeTime = trade.getLong("time");
				if (tradeTime == endTime || i == trades.length() - 1) {
					last = new BigDecimal(trade.getString("last"));
					high = new BigDecimal(trade.getString("high"));
					low = new BigDecimal(trade.getString("low"));
					vol = new BigDecimal(trade.getString("vol"));
					first = new BigDecimal(trade.getString("first"));
				} else if (tradeTime <= startTime) {
					break;
				} else if (last.compareTo(zero) > 0) {
					BigDecimal temphigh = new BigDecimal(trade.getString("high"));
					BigDecimal templow = new BigDecimal(trade.getString("low"));
					BigDecimal tempvol = new BigDecimal(trade.getString("vol"));
					if (temphigh.compareTo(high) > 0) {
						high = temphigh;
					}
					
					if (low.compareTo(templow) > 0) {
						low = templow;
					}
					
					vol = vol.add(tempvol);
					first = new BigDecimal(trade.getString("first"));
				}
			}
		}
		
		return new KLineTrade(0, code, high, low, first, last, vol, endTime);
	}
	
	private KLineTrade getTradeFromCache(String bqCode, long startTime, long endTime, String timeTypeStr, String memKey) throws JSONException {
		if (StringUtil.isEmpty(memKey)) {
			return new KLineTrade(0, "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, endTime);
		}
		
		String arrayStr = spyMemcachedClient.get(memKey);
		if (StringUtil.isEmpty(arrayStr)) {
			arrayStr = "[]";
		}
		JSONArray trades = new JSONArray(arrayStr);
		KLineTrade trade = getTradeFromArray(trades, bqCode, startTime, endTime);
		
		if (trade.getFirst().compareTo(BigDecimal.ZERO) == 0 || trade.getLast().compareTo(BigDecimal.ZERO) == 0) {
//			KLineTrade t = kLineTradeService.getAtTime(bqCode, timeTypeStr, endTime);
//			if (t != null) {
//			}
			trades = kLineTradeService.findByTimeJson(bqCode, timeTypeStr, startTime, endTime);
			trade = getTradeFromArray(trades, bqCode, startTime, endTime);
		}
		
		return trade;
	}
	
	public void insertToKLine(String bqCode, long minTime, long maxTime, long typeTime, String timeTypeStr, String baseTimeTypeStr, String memKey) throws JSONException {
		BigDecimal zero = new BigDecimal(0);
		
		while (minTime < maxTime) {
			long tempTime = minTime + typeTime;
			
			KLineTrade trade = getTradeFromCache(bqCode, minTime, tempTime, baseTimeTypeStr, memKey);
			if (trade.getFirst().compareTo(zero) == 0 || trade.getLast().compareTo(zero) == 0) {
				BigDecimal high = tradeLogService.getInTimeHighPrice(bqCode, minTime, tempTime);
				BigDecimal low = tradeLogService.getInTimeLowPrice(bqCode, minTime, tempTime);
				BigDecimal first = tradeLogService.getInTimeFirstPrice(bqCode, minTime, tempTime);
				BigDecimal last = tradeLogService.getInTimeLastPrice(bqCode, minTime, tempTime);
				BigDecimal vol = tradeLogService.getInTimeVol(bqCode, minTime, tempTime);
				trade = new KLineTrade(0, bqCode, high, low, first, last, vol, tempTime);
			}
			
			if (trade.getHigh().compareTo(zero) > 0 && trade.getLow().compareTo(zero) > 0
					&& trade.getFirst().compareTo(zero) > 0 && trade.getLast().compareTo(zero) > 0
					&& trade.getVol().compareTo(zero) > 0) {
				kLineTradeService.insert(trade, timeTypeStr);
			}
			
			minTime = tempTime;
		}
	}
	
	public void doOtherKline(String code, String timeTypeStr, String baseTimeTypeStr, String memKey) throws JSONException {
		long minTime = kLineTradeService.getMinTime(code, baseTimeTypeStr);
		long maxTime = kLineTradeService.getlastTime(code, baseTimeTypeStr);

		minTime = minTime - 86400l;
		if (timeTypeStr.endsWith("_one_week")) {
			minTime = TimeUtil.getMonday(minTime);
			insertOtherToKLine(code, minTime, maxTime, timeTypeStr, baseTimeTypeStr, memKey);
		} else if (timeTypeStr.endsWith("_one_month")) {
			minTime = TimeUtil.getMonthFirstDay(minTime);
			insertOtherToKLine(code, minTime, maxTime, timeTypeStr, baseTimeTypeStr, memKey);
		}
	}
	
	public void insertOtherToKLine(String bqCode, long minTime, long maxTime, String timeTypeStr, String baseTimeTypeStr, String memKey) throws JSONException {
		BigDecimal zero = new BigDecimal(0);
		
		while (minTime < maxTime) {
			// 周线:minTime必须是周一0:0:0;月线:minTime必须是月1号0:0:0;
			long tempTime = 0;
			if (timeTypeStr.endsWith("_one_week")) {
				tempTime = minTime + 7 * 86400l;
			} else if (timeTypeStr.endsWith("_one_month")) {
				tempTime = TimeUtil.getMonthLastDay(minTime) + 86400l; // 月末加1天
			}
			
			KLineTrade trade = getTradeFromCache(bqCode, minTime, tempTime, baseTimeTypeStr, memKey);
			if (trade.getHigh().compareTo(zero) > 0 && trade.getLow().compareTo(zero) > 0
					&& trade.getFirst().compareTo(zero) > 0 && trade.getLast().compareTo(zero) > 0
					&& trade.getVol().compareTo(zero) > 0) {
				kLineTradeService.insert(trade, timeTypeStr);
			}
			
			minTime = tempTime;
		}
	}
	
	public void insertToSpotKLine(String coinPairName, long minTime, long maxTime, long typeTime, String timeTypeStr, String baseTimeTypeStr, String memKey) throws JSONException {
		BigDecimal zero = new BigDecimal(0);
		while (minTime < maxTime) {
			long tempTime = minTime + typeTime;
			
			KLineTrade trade = getTradeFromCache(coinPairName, minTime, tempTime, baseTimeTypeStr, memKey);
			if (trade.getFirst().compareTo(zero) == 0 || trade.getLast().compareTo(zero) == 0) {
				BigDecimal high = spotTradeLogService.getInTimeHighPrice(coinPairName, minTime, tempTime);
				BigDecimal low = spotTradeLogService.getInTimeLowPrice(coinPairName, minTime, tempTime);
				BigDecimal first = spotTradeLogService.getInTimeFirstPrice(coinPairName, minTime, tempTime);
				BigDecimal last = spotTradeLogService.getInTimeLastPrice(coinPairName, minTime, tempTime);
				BigDecimal vol = spotTradeLogService.getInTimeVol(coinPairName, minTime, tempTime);
				trade = new KLineTrade(0, coinPairName, high, low, first, last, vol, tempTime);
			}
			
			if (trade.getHigh().compareTo(zero) > 0 && trade.getLow().compareTo(zero) > 0
					&& trade.getFirst().compareTo(zero) > 0 && trade.getLast().compareTo(zero) > 0
					&& trade.getVol().compareTo(zero) > 0) {
				kLineTradeService.insert(trade, timeTypeStr);
			}
			
			minTime = tempTime;
		}
	}
	
	public void insertToSimuKLine(String vCode, long minTime, long maxTime, long typeTime, String _v_timeTypeStr, String _v_baseTimeTypeStr, String memKey) throws JSONException {
		BigDecimal zero = new BigDecimal(0);
		while (minTime < maxTime) {
			long tempTime = minTime + typeTime;
			
			KLineTrade trade = getTradeFromCache(vCode, minTime, tempTime, _v_baseTimeTypeStr, memKey);
			if (trade.getFirst().compareTo(zero) == 0 || trade.getLast().compareTo(zero) == 0) {
				BigDecimal high = vTradeLogService.getInTimeHighPrice(vCode, minTime, tempTime);
				BigDecimal low = vTradeLogService.getInTimeLowPrice(vCode, minTime, tempTime);
				BigDecimal first = vTradeLogService.getInTimeFirstPrice(vCode, minTime, tempTime);
				BigDecimal last = vTradeLogService.getInTimeLastPrice(vCode, minTime, tempTime);
				BigDecimal vol = vTradeLogService.getInTimeVol(vCode, minTime, tempTime);
				trade = new KLineTrade(0, vCode, high, low, first, last, vol, tempTime);
			}
			
			if (trade.getHigh().compareTo(zero) > 0 && trade.getLow().compareTo(zero) > 0
					&& trade.getFirst().compareTo(zero) > 0 && trade.getLast().compareTo(zero) > 0
					&& trade.getVol().compareTo(zero) > 0) {
				kLineTradeService.insert(trade, _v_timeTypeStr);
			}
			
			minTime = tempTime;
		}
	}
	
	public void insertToDeliveryKLine(String prefixCode, long minTime, long maxTime, long typeTime, String _timeTypeStr, String _baseTimeTypeStr, String memKey) throws JSONException {
		BigDecimal zero = new BigDecimal(0);
		while (minTime < maxTime) {
			long tempTime = minTime + typeTime;
			
			KLineTrade trade = getTradeFromCache(prefixCode, minTime, tempTime, _baseTimeTypeStr, memKey);
			if (trade.getFirst().compareTo(zero) == 0 || trade.getLast().compareTo(zero) == 0) {
				BigDecimal high = dTradeLogService.getInTimeHighPrice(prefixCode, minTime, tempTime);
				BigDecimal low = dTradeLogService.getInTimeLowPrice(prefixCode, minTime, tempTime);
				BigDecimal first = dTradeLogService.getInTimeFirstPrice(prefixCode, minTime, tempTime);
				BigDecimal last = dTradeLogService.getInTimeLastPrice(prefixCode, minTime, tempTime);
				BigDecimal vol = dTradeLogService.getInTimeVol(prefixCode, minTime, tempTime);
				trade = new KLineTrade(0, prefixCode, high, low, first, last, vol, tempTime);
			}
			
			if (trade.getHigh().compareTo(zero) > 0 && trade.getLow().compareTo(zero) > 0
					&& trade.getFirst().compareTo(zero) > 0 && trade.getLast().compareTo(zero) > 0
					&& trade.getVol().compareTo(zero) > 0) {
				kLineTradeService.insert(trade, _timeTypeStr);
			}
			
			minTime = tempTime;
		}
	}
	
	public void putDataToMemcached(String bqCode, String timeTypeStr) {
		JSONArray array = new JSONArray();
		
		array = kLineTradeService.findPreJson(bqCode, timeTypeStr);// 查询2000条数据
		if ("_one_month".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEMONTH + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_one_week".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEWEEK + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_one_day".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEDAY + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_one_minute".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEMINUTE + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_three_minutes".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_THREEMINUTES + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_five_minutes".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_FIVEMINUTES + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_fifteen_minutes".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_FIFTEENMINUTES + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_half_hour".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_HALFHOUR + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_one_hour".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEHOUR + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_two_hours".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_TWOHOURS + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_four_hours".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_FOURHOURS + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_six_hours".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_SIXHOURS + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_twelve_hours".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_TWELVEHOURS + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		}
	}
	
	public void putSimuDataToMemcached(String vCode, String _v_timeTypeStr) {
		JSONArray array = new JSONArray();
		
		array = kLineTradeService.findPreJson(vCode, _v_timeTypeStr);// 查询2000条数据
		if ("_one_month".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEMONTH + "_" + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_one_week".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEWEEK + "_" + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_v_one_day".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEDAY + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_v_one_minute".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEMINUTE + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_v_three_minutes".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_THREEMINUTES + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_v_five_minutes".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_FIVEMINUTES + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_v_fifteen_minutes".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_FIFTEENMINUTES + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_v_half_hour".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_HALFHOUR + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_v_one_hour".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEHOUR + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_v_two_hours".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_TWOHOURS + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_v_four_hours".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_FOURHOURS + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_v_six_hours".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_SIXHOURS + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_v_twelve_hours".equals(_v_timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_TWELVEHOURS + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		}
	}
	
	public void putDeliveryDataToMemcached(String bqCode, String timeTypeStr) {
		JSONArray array = new JSONArray();
		
		array = kLineTradeService.findPreJson(bqCode, timeTypeStr);// 查询2000条数据
		if ("_one_month".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_ONEMONTH + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_one_week".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_ONEWEEK + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_one_day".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_ONEDAY + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_one_minute".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_ONEMINUTE + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_three_minutes".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_THREEMINUTES + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_five_minutes".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_FIVEMINUTES + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_fifteen_minutes".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_FIFTEENMINUTES + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_half_hour".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_HALFHOUR + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_one_hour".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_ONEHOUR + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_two_hours".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_TWOHOURS + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_four_hours".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_FOURHOURS + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_six_hours".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_SIXHOURS + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		} else if ("_twelve_hours".equals(timeTypeStr)) {
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_TWELVEHOURS + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
		}
	}
	
	public void doKLine(List<BQTransaction> transactionList, String timeTypeStr, String lastTimeTypeStr, long typeTime,
			long startTime, long endTime, String memKey) throws JSONException {

		for (BQTransaction bq : transactionList) {
			String bqCode = bq.getTransaction_code(); // 获取合约号
			
			if (startTime == 0) {
				startTime = kLineTradeService.getlastTime(bqCode, timeTypeStr);
			}
			
			if (startTime == 0) {
				startTime = endTime - typeTime;
			}
			if (!StringUtil.isEmpty(memKey)) {
				memKey = memKey + "_" + bqCode;
			}
			
			insertToKLine(bqCode, startTime, endTime, typeTime, timeTypeStr, lastTimeTypeStr, memKey);
			// 查询行情数据记录(json数组对象)放入缓存中
			putDataToMemcached(bqCode, timeTypeStr);
		}
	}
	
	public void doSpotKLine(List<BQSpotMarketCoinPair> coinPairs, String timeTypeStr, String lastTimeTypeStr,
			long typeTime, long startTime, long endTime, String memKey) throws JSONException {

		for (BQSpotMarketCoinPair coinPair : coinPairs) {
			String coinPairName = coinPair.getCoin_pair_name();
			
			if (startTime == 0) {
				startTime = kLineTradeService.getlastTime(coinPairName, timeTypeStr);
			}
			
			if (startTime == 0) {
				startTime = endTime - typeTime;
			}

			if (!StringUtil.isEmpty(memKey)) {
				memKey = memKey + "_" + coinPairName;
			}
			insertToSpotKLine(coinPairName, startTime, endTime, typeTime, timeTypeStr, lastTimeTypeStr, memKey);
			// 查询行情数据记录(json数组对象)放入缓存中
			putDataToMemcached(coinPairName, timeTypeStr);
		}
	}
	
	public void doSimuKLine(List<VTransaction> vTransactionList, String _v_timeTypeStr, String lastTimeTypeStr,
			long typeTime, long startTime, long endTime, String memKey) throws JSONException {

		for (VTransaction bq : vTransactionList) {
			String vCode = bq.getTransaction_code(); // 获取合约号
			
			if (startTime == 0) {
				startTime = kLineTradeService.getlastTime(vCode, _v_timeTypeStr);
			}
			
			if (startTime == 0) {
				startTime = endTime - typeTime;
			}

			if (!StringUtil.isEmpty(memKey)) {
				memKey = memKey + vCode;
			}
			insertToSimuKLine(vCode, startTime, endTime, typeTime, _v_timeTypeStr, lastTimeTypeStr, memKey);
			// 查询行情数据记录(json数组对象)放入缓存中
			putSimuDataToMemcached(vCode, _v_timeTypeStr);
		}
	}
	
	public void doDeliveryKLine(List<DeliveryTransaction> dTransactionList, String _timeTypeStr, String lastTimeTypeStr,
			long typeTime, long startTime, long endTime, String memKey) throws JSONException {
		
		for (DeliveryTransaction bq : dTransactionList) {
			String typeCode = bq.getTransaction_type_code(); // 获取合约类型号
			// 日交割（btc_d_one_minute_trade） 周交割（btc_w_one_minute_trade）季度交割（btc_q_one_minute_trade）
			String prefixCode = typeCode + "_" + bq.getDelivery_type();
			
			if (startTime == 0) {
				startTime = kLineTradeService.getlastTime(prefixCode, _timeTypeStr);
			}
			
			if (startTime == 0) {
				startTime = endTime - typeTime;
			}
			
			if (!StringUtil.isEmpty(memKey)) {
				memKey = memKey + prefixCode;
			}
			insertToDeliveryKLine(prefixCode, startTime, endTime, typeTime, _timeTypeStr, lastTimeTypeStr, memKey);
			// 查询行情数据记录(json数组对象)放入缓存中
			putDeliveryDataToMemcached(prefixCode, _timeTypeStr);
		}
	}
	
	
	public StatisticalFormVo getStatisticsReportForm(long startTime, long endTime) throws Exception {
		StatisticalFormVo form = new StatisticalFormVo();
		List<StatisticalFormContractVo> contractForms = new ArrayList<StatisticalFormContractVo>();
		List<VStatisticalFormContractVo> vContractForms = new ArrayList<VStatisticalFormContractVo>();
		
		form.setTodayRegister(bqMemberService.findCountByTime(startTime, endTime));
		form.setTotalRegister(bqMemberService.findCount());
		form.setLoginUserCount(bqMemberLoginLogService.getTotalLoginUserByTime(startTime, endTime));
		
		List<BQTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		List<VTransaction> vlstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (vlstTrads == null) {
			vlstTrads = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		BQBfxFeeSetting bfxFeeSetting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (bfxFeeSetting == null) {
			bfxFeeSetting = bqBfxFeeSettingService.get();
			if (bfxFeeSetting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, bfxFeeSetting);
			}
		}
		
		List<Integer> users = new ArrayList<Integer>();
		List<Integer> vusers = new ArrayList<Integer>();
		for (BQTransaction bqTransaction : lstTrads) {
			StatisticalFormContractVo contractForm = new StatisticalFormContractVo();
			
			String traCode = bqTransaction.getTransaction_code();
			BQArbitrageSetting setting = bqArbitrageSettingService.getArbitrageSettingByCode(traCode);
			if (setting == null) {
				setting = new BQArbitrageSetting();
				setting.setDepth_uid(0L);
			}
			
			contractForm.setCode(traCode);
			contractForm.setTradeUser(tradeLogService.getTradeUserCount(traCode, startTime, endTime, setting.getDepth_uid()));
			contractForm.setTradeAmount(
					tradeLogService.getInTimeNoScalpingVol(traCode, startTime, endTime, 0).doubleValue());
			contractForm.setFeeAmount(
					tradeLogService.getInTimeNoScalpingFee(traCode, startTime, endTime, setting.getDepth_uid()).doubleValue());
			
			contractForm.setDepositAmount(bqMemberTransactionService.findSumMinDepositByUser(traCode, 0, setting.getDepth_uid()).doubleValue());
			contractForm.setHoldAmount(bqMemberTransactionService.findSumHoldsByUser(traCode, 0, setting.getDepth_uid()).doubleValue());
			BigDecimal balance = bqAccountContractService.findSumBalanceByUser(traCode, 0, setting.getDepth_uid());
			contractForm.setContractBalance(balance.doubleValue());
			contractForm.setRebate(bqMemberInviteRebateLogService.getSumRebateByTimeAndUser("", traCode, startTime, endTime, 0, Contents.TRADE_CURRENCY_TYPE_ID).doubleValue());
			contractForm.setBfxFeeAmount(bqBfxFeeRecordService.sumBfxFee("", 0, traCode, startTime, endTime).doubleValue());
			
			if (bfxFeeSetting != null && bfxFeeSetting.getCoin_id() > 0) {
				contractForm.setBfxRebate(bqMemberInviteRebateLogService.getSumRebateByTimeAndUser("", traCode, startTime, endTime, 
						0, bfxFeeSetting.getCoin_id()).doubleValue());
			}
			
			contractForms.add(contractForm);
			
			List<Integer> ids = tradeLogService.getTradeUserIds(traCode, startTime, endTime, setting.getDepth_uid());
			for (Integer id : ids) {
				boolean have = false;
				for (Integer user : users) {
					if (id.longValue() == user.longValue()) {
						have = true;
						break;
					}
				}
				
				if (!have) {
					users.add(id);
				}
			}
			
			form.setTotalBfxFee(contractForm.getBfxFeeAmount() + form.getTotalBfxFee());
			form.setTotalFee(contractForm.getFeeAmount() + form.getTotalFee());
			form.setTotalRebate(contractForm.getRebate() + form.getTotalRebate());
			form.setTotalBfxRebate(contractForm.getBfxRebate() + form.getTotalBfxRebate());
		}
		
		for (VTransaction vTrans : vlstTrads) {
			VStatisticalFormContractVo vContractForm = new VStatisticalFormContractVo();
			String vTraCode = vTrans.getTransaction_code();
			VArbitrageSetting vArbitrageSetting = vArbitrageSettingService.getArbitrageSettingByCode(vTraCode);
			if (vArbitrageSetting == null) {
				vArbitrageSetting = new VArbitrageSetting();
				vArbitrageSetting.setDepth_uid(0L);
			}
			
			long vtradeUser = vTradeLogService.getTradeUserCount(vTraCode, startTime, endTime,
					vArbitrageSetting.getDepth_uid());
			double vtradeAmount = vTradeLogService.getInTimeNoScalpingVol(vTraCode, startTime, endTime, vArbitrageSetting.getDepth_uid())
					.doubleValue();
			double vfeeAmount = vTradeLogService.getInTimeNoScalpingFee(vTraCode, startTime, endTime, vArbitrageSetting.getDepth_uid()).doubleValue();
			double vdepositAmount = vMemberTransactionService
					.findSumMinDepositByUser(vTraCode, 0, vArbitrageSetting.getDepth_uid()).doubleValue();
			double vholdAmount = vMemberTransactionService
					.findSumHoldsByUser(vTraCode, 0, vArbitrageSetting.getDepth_uid()).doubleValue();
			double vcontractBalance = vAccountContractService
					.findSumBalanceByUser(vTraCode, 0, vArbitrageSetting.getDepth_uid()).doubleValue();
			
			vContractForm.setCode(vTraCode);
			vContractForm.setVtradeUser(vtradeUser);
			vContractForm.setVtradeAmount(vtradeAmount);
			vContractForm.setVfeeAmount(vfeeAmount);
			vContractForm.setVdepositAmount(vdepositAmount);
			vContractForm.setVholdAmount(vholdAmount);
			vContractForm.setVcontractBalance(vcontractBalance);
			
			vContractForms.add(vContractForm);
			
			List<Integer> ids = vTradeLogService.getTradeUserIds(vTraCode, startTime, endTime, vArbitrageSetting.getDepth_uid());
			for (Integer id : ids) {
				boolean have = false;
				for (Integer user : vusers) {
					if (id.longValue() == user.longValue()) {
						have = true;
						break;
					}
				}
				
				if (!have) {
					vusers.add(id);
				}
			}
			
			form.setVtotalFee(vContractForm.getVfeeAmount() + form.getVtotalFee());
		}
		
		form.setTotalTradeUser(users.size());
		form.setVtotalTradeUser(vusers.size());
		form.setConForms(contractForms);
		form.setVconForms(vContractForms);
		
		return form;
	}
	
	public StatisticalFormVo getNStatisticsReportForm(String partition, long startTime, long endTime) throws Exception {
		StatisticalFormVo form = new StatisticalFormVo();
		List<StatisticalFormContractVo> contractForms = new ArrayList<StatisticalFormContractVo>();
		
		List<NTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + partition);
		if (lstTrads == null) {
			lstTrads = nTransactionService.findTransByState_new(partition, Contents.TRANSACTION_STATE_ZC);
		}
		
		BQBfxFeeSetting bfxFeeSetting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (bfxFeeSetting == null) {
			bfxFeeSetting = bqBfxFeeSettingService.get();
			if (bfxFeeSetting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, bfxFeeSetting);
			}
		}
		
		List<Integer> users = new ArrayList<Integer>();
		for (NTransaction bqTransaction : lstTrads) {
			StatisticalFormContractVo contractForm = new StatisticalFormContractVo();
			
			String traCode = bqTransaction.getTransaction_code();
			NArbitrageSetting setting = nArbitrageSettingService.getArbitrageSettingByCode(partition, traCode);
			if (setting == null) {
				setting = new NArbitrageSetting();
				setting.setDepth_uid(0L);
			}
			
			contractForm.setCode(traCode);
			contractForm.setTradeUser(nTradeLogService.getTradeUserCount(partition, traCode, startTime, endTime, setting.getDepth_uid()));
			contractForm.setTradeAmount(
					nTradeLogService.getInTimeNoScalpingVol(partition, traCode, startTime, endTime, 0).doubleValue());
			contractForm.setFeeAmount(
					nTradeLogService.getInTimeNoScalpingFee(partition, traCode, startTime, endTime, setting.getDepth_uid()).doubleValue());
			
			contractForm.setDepositAmount(nMemberTransactionService.findSumMinDepositByUser(partition, traCode, 0, setting.getDepth_uid()).doubleValue());
			contractForm.setHoldAmount(nMemberTransactionService.findSumHoldsByUser(partition, traCode, 0, setting.getDepth_uid()).doubleValue());
			BigDecimal balance = nAccountContractService.findSumBalanceByUser(partition, traCode, 0, setting.getDepth_uid());
			contractForm.setContractBalance(balance.doubleValue());
			contractForm.setRebate(bqMemberInviteRebateLogService.getSumRebateByTimeAndUser(partition, traCode, startTime, endTime, 0, bqTransaction.getCurrency_type_id()).doubleValue());
			contractForm.setBfxFeeAmount(bqBfxFeeRecordService.sumBfxFee("", 0, traCode, startTime, endTime).doubleValue());
			
			if (bfxFeeSetting != null && bfxFeeSetting.getCoin_id() > 0) {
				contractForm.setBfxRebate(bqMemberInviteRebateLogService.getSumRebateByTimeAndUser("", traCode, startTime, endTime, 
						0, bfxFeeSetting.getCoin_id()).doubleValue());
			}
			
			contractForms.add(contractForm);
			
			List<Integer> ids = nTradeLogService.getTradeUserIds(partition, traCode, startTime, endTime, setting.getDepth_uid());
			for (Integer id : ids) {
				boolean have = false;
				for (Integer user : users) {
					if (id.longValue() == user.longValue()) {
						have = true;
						break;
					}
				}
				
				if (!have) {
					users.add(id);
				}
			}
			
			form.setTotalBfxFee(contractForm.getBfxFeeAmount() + form.getTotalBfxFee());
			form.setTotalFee(contractForm.getFeeAmount() + form.getTotalFee());
			form.setTotalRebate(contractForm.getRebate() + form.getTotalRebate());
			form.setTotalBfxRebate(contractForm.getBfxRebate() + form.getTotalBfxRebate());
		}
		
		form.setTotalTradeUser(users.size());
		form.setConForms(contractForms);
		
		return form;
	}
	
	public StatisticalFormVo getDeliveryForm(long startTime, long endTime) throws Exception {
		StatisticalFormVo form = new StatisticalFormVo();
		List<StatisticalFormContractVo> contractForms = new ArrayList<StatisticalFormContractVo>();
		
		List<DeliveryTransaction> vlstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (vlstTrads == null) {
			vlstTrads = dTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		BQBfxFeeSetting bfxFeeSetting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (bfxFeeSetting == null) {
			bfxFeeSetting = bqBfxFeeSettingService.get();
			if (bfxFeeSetting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, bfxFeeSetting);
			}
		}
		
		List<Integer> users = new ArrayList<Integer>();
		for (DeliveryTransaction bqTransaction : vlstTrads) {
			StatisticalFormContractVo contractForm = new StatisticalFormContractVo();
			
			String traCode = bqTransaction.getTransaction_code();
			DeliveryArbitrageSetting setting = dArbitrageSettingService.getArbitrageSettingByCode(traCode);
			if (setting == null) {
				setting = new DeliveryArbitrageSetting();
				setting.setDepth_uid(0L);
			}
			
			contractForm.setCode(traCode);
			contractForm.setTradeUser(dTradeLogService.getTradeUserCount(traCode, startTime, endTime, setting.getDepth_uid()));
			contractForm.setTradeAmount(
					dTradeLogService.getInTimeNoScalpingVol(traCode, startTime, endTime, 0).doubleValue());
			contractForm.setFeeAmount(
					dTradeLogService.getInTimeNoScalpingFee(traCode, startTime, endTime, setting.getDepth_uid()).doubleValue());
			
			contractForm.setDepositAmount(dMemberTransactionService.findSumMinDepositByUser(traCode, 0, setting.getDepth_uid()).doubleValue());
			contractForm.setHoldAmount(dMemberTransactionService.findSumHoldsByUser(traCode, 0, setting.getDepth_uid()).doubleValue());
			BigDecimal balance = dAccountContractService.findSumBalanceByUser(traCode, 0, setting.getDepth_uid());
			contractForm.setContractBalance(balance.doubleValue());
			contractForm.setRebate(dMemberInviteRebateLogService.getSumRebateByTimeAndUser(traCode, startTime, endTime, 0, Contents.TRADE_CURRENCY_TYPE_ID).doubleValue());
			contractForm.setBfxFeeAmount(bqBfxFeeRecordService.sumBfxFee("", 0, traCode, startTime, endTime).doubleValue());
			
			if (bfxFeeSetting != null && bfxFeeSetting.getCoin_id() > 0) {
				contractForm.setBfxRebate(dMemberInviteRebateLogService.getSumRebateByTimeAndUser(traCode, startTime, endTime, 
						0, bfxFeeSetting.getCoin_id()).doubleValue());
			}
			
			contractForms.add(contractForm);
			
			List<Integer> ids = dTradeLogService.getTradeUserIds(traCode, startTime, endTime, setting.getDepth_uid());
			for (Integer id : ids) {
				boolean have = false;
				for (Integer user : users) {
					if (id.longValue() == user.longValue()) {
						have = true;
						break;
					}
				}
				
				if (!have) {
					users.add(id);
				}
			}
			
			form.setTotalBfxFee(contractForm.getBfxFeeAmount() + form.getTotalBfxFee());
			form.setTotalFee(contractForm.getFeeAmount() + form.getTotalFee());
			form.setTotalRebate(contractForm.getRebate() + form.getTotalRebate());
			form.setTotalBfxRebate(contractForm.getBfxRebate() + form.getTotalBfxRebate());
		}
		
		form.setTotalTradeUser(users.size());
		form.setConForms(contractForms);
		
		return form;
	}
}
