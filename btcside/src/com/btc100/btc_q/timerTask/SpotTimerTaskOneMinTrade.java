package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.KLineTrade;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.KLineTradeService;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.google.gson.Gson;

/**
 * 1分钟内交易（市场行情）
 * 
 * @author Administrator
 * 
 */
public class SpotTimerTaskOneMinTrade extends BaseTask {
	
	@Autowired
	private KLineTradeService kLineTradeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private SpotTradeLogService spotTradeLogService;
	
	private static String[] marketArray = null;
	
	public void run() {
		
		if (marketArray == null) {
			String contracts = UrlConfig.getValue("spotTrade.markets");
			if (!StringUtil.isEmpty(contracts)) {
				marketArray = contracts.split(",");
			}
		}
		
		if (marketArray == null && marketArray.length == 0) return;
		
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}
		
		long currentTimeMillis = bqSpotMarketCoinPairService.time() - 1;
		// 获取当前时间所在的1分钟区间内的起始时间点（0:01:00、0:02:00、0:03:00、0:04:00、0:05:00...）
		long typeTime = 60L;
		long oneMinAgoMillis = currentTimeMillis / typeTime * typeTime;
		String timeTypeStr = "_one_minute";
		
		for (String market : marketArray) {
			BQSpotMarketCoinPair coinPair = null;
			for (BQSpotMarketCoinPair marketCoinPair : coinPairs) {
				if (marketCoinPair.getCoin_pair_name().equals(market)) {
					coinPair = marketCoinPair;
					break;
				}
			}
			
			if (coinPair == null) continue;
			
			String bqCode = coinPair.getCoin_pair_name();
			Long lastTime = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr + "_" + bqCode);
			if (lastTime == null) {
				lastTime = 0L;
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, oneMinAgoMillis);
			
			try {
				doOneMinuteKLine(coinPair, lastTime, oneMinAgoMillis);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void updateOneMinuteKLine(String bqCode, long beginTime, long endTime, boolean needUpdate) throws JSONException {
		BigDecimal zero = new BigDecimal(0);
	
		BigDecimal high = spotTradeLogService.getInTimeHighPrice(bqCode, beginTime, endTime);
		BigDecimal low = spotTradeLogService.getInTimeLowPrice(bqCode, beginTime, endTime);
		BigDecimal first = spotTradeLogService.getInTimeFirstPrice(bqCode, beginTime, endTime);
		BigDecimal last = spotTradeLogService.getInTimeLastPrice(bqCode, beginTime, endTime);
		BigDecimal vol = spotTradeLogService.getInTimeVol(bqCode, beginTime, endTime);
		KLineTrade trade = new KLineTrade(0, bqCode, high, low, first, last, vol, endTime);
		
		if (trade.getHigh().compareTo(zero) > 0 && trade.getLow().compareTo(zero) > 0
				&& trade.getFirst().compareTo(zero) > 0 && trade.getLast().compareTo(zero) > 0
				&& trade.getVol().compareTo(zero) > 0) {
			
//			if (needUpdate) {
//				kLineTradeService.insert(trade, "_one_minute");
//			}
			
			Map<String, Integer> timeMap = new HashMap<String, Integer>();
			timeMap.put("_one_minute", 60);
			timeMap.put("_three_minutes", 180);
			timeMap.put("_five_minutes", 300);
			timeMap.put("_fifteen_minutes", 900);
			timeMap.put("_half_hour", 1800);
			timeMap.put("_one_hour", 3600);
			timeMap.put("_two_hours", 7200);
			timeMap.put("_four_hours", 14400);
			timeMap.put("_six_hours", 21600);
			timeMap.put("_twelve_hours", 43200);
			timeMap.put("_one_day", 86400);
			timeMap.put("_one_week", 86400 * 7);
			timeMap.put("_one_month", 86400 * 30);
			
			Map<String, String> typeMap = new HashMap<String, String>();
			typeMap.put("_one_minute", BTCQMemecacheKeys.TICKER_ONEMINUTE);
			typeMap.put("_three_minutes", BTCQMemecacheKeys.TICKER_THREEMINUTES);
			typeMap.put("_five_minutes", BTCQMemecacheKeys.TICKER_FIVEMINUTES);
			typeMap.put("_fifteen_minutes", BTCQMemecacheKeys.TICKER_FIFTEENMINUTES);
			typeMap.put("_half_hour", BTCQMemecacheKeys.TICKER_HALFHOUR);
			typeMap.put("_one_hour", BTCQMemecacheKeys.TICKER_ONEHOUR);
			typeMap.put("_two_hours", BTCQMemecacheKeys.TICKER_TWOHOURS);
			typeMap.put("_four_hours", BTCQMemecacheKeys.TICKER_FOURHOURS);
			typeMap.put("_six_hours", BTCQMemecacheKeys.TICKER_SIXHOURS);
			typeMap.put("_twelve_hours", BTCQMemecacheKeys.TICKER_TWELVEHOURS);
			typeMap.put("_one_day", BTCQMemecacheKeys.TICKER_ONEDAY);
			typeMap.put("_one_week", BTCQMemecacheKeys.TICKER_ONEWEEK);
			typeMap.put("_one_month", BTCQMemecacheKeys.TICKER_ONEMONTH);
			
			
			long morning = TimeUtil.getTimesmorning(beginTime * 1000);
//			String oneMinute = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEMINUTE + "_" + bqCode);
//			if (!StringUtil.isEmpty(oneMinute)) {
//				Gson gson = new Gson();
//				JSONArray datas = new JSONArray(oneMinute);
//				datas.put(new JSONObject(gson.toJson(trade)));
//				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEMINUTE + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, datas.toString());
//			}
			
			for (Entry<String, Integer> entry : timeMap.entrySet()) {
				String key = entry.getKey();
				int interval = entry.getValue(); 
				if (interval == 0) continue;
				
				long time = (beginTime - morning) / interval * interval + interval + morning;
				if ("_one_week".equals(key)) {
					time = TimeUtil.getWeekend(beginTime) + 86400l;
				} else if ("_one_month".equals(key)) {
					time = TimeUtil.getMonthLastDay(beginTime) + 86400l;
				}
				
				if (needUpdate) {
					KLineTrade kline = kLineTradeService.updateFromOneMinute(trade, key, time);
					String type = typeMap.get(key);
					String arrayStr = spyMemcachedClient.get(type + "_" + bqCode);
					if (!StringUtil.isEmpty(arrayStr)) {
						JSONArray array = new JSONArray(arrayStr);
						if (array != null && array.length() > 0) {
							JSONObject json = array.getJSONObject(array.length() - 1);
							if (json.getLong("time") == kline.getTime()) {
								json.put("high", kline.getHigh());
								json.put("low", kline.getLow());
								json.put("last", kline.getLast());
								json.put("first", kline.getFirst());
								json.put("vol", kline.getVol());
							} else {
								Gson gson = new Gson();
								array.put(new JSONObject(gson.toJson(kline)));
							}
							spyMemcachedClient.set(type + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
						}
					}
				} else {
					String type = typeMap.get(key);
					Gson gson = new Gson();
					KLineTrade newKline = new KLineTrade(trade);
					newKline.setTime(time);
					spyMemcachedClient.set("last_" + type + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, gson.toJson(newKline));
				}
			}
		}
	}
	
	private void doOneMinuteKLine(BQSpotMarketCoinPair coinPair, long startTime, long endTime) throws JSONException {
		long beginTime = startTime;
		String bqCode = coinPair.getCoin_pair_name(); // 获取合约号
//		bqSpotMarketCoinPairService.createTable(bqCode);// 无表则先创建表
		
		long weekLastTime = kLineTradeService.getlastTime(bqCode, "_one_week");
		if (weekLastTime <= 0) {
			doOtherKline(bqCode, "_one_week", "_one_day", BTCQMemecacheKeys.TICKER_ONEDAY);
		}
		long monthLastTime = kLineTradeService.getlastTime(bqCode, "_one_month");
		if (monthLastTime <= 0) {
			doOtherKline(bqCode, "_one_month", "_one_day", BTCQMemecacheKeys.TICKER_ONEDAY);
		}
		
		if (beginTime == 0) {
			beginTime = kLineTradeService.getlastTime(bqCode, "_one_minute");
		}
		
		if (beginTime == 0) {
			beginTime = endTime - 60;
		}
		
		while (beginTime < endTime) {
			updateOneMinuteKLine(bqCode, beginTime, beginTime + 60, true);
			beginTime = beginTime + 60;
		}
		
		updateOneMinuteKLine(bqCode, endTime, endTime + 60, false);
	}

}
