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
import com.btc100.btc_q.entity.KLineTrade;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.KLineTradeService;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.google.gson.Gson;

/**
 * 1分钟内交易（市场行情）
 * 
 * @author Administrator
 * 
 */
public class VTimerTaskOneMinTrade extends BaseTask {
	
	@Autowired
	private KLineTradeService kLineTradeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VTradeLogService vTradeLogService;
	
	public void run() {
		
		long currentTimeMillis = vTransactionService.time() - 1;
		// 获取当前时间所在的1分钟区间内的起始时间点（0:01:00、0:02:00、0:03:00、0:04:00、0:05:00...）
		long typeTime = 60L;
		long oneMinAgoMillis = currentTimeMillis / typeTime * typeTime;
		String timeTypeStr = "_one_minute";
		
		Long lastTime = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr);
		if (lastTime == null) {
			lastTime = 0L;
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, oneMinAgoMillis);
		
		// 找出在用的合约
		List<VTransaction> vTransactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doOneMinuteSimuKLine(vTransactionList, lastTime, oneMinAgoMillis);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void updateOneMinuteSimuKLine(String bqCode, long beginTime, long endTime, boolean needUpdate) throws JSONException {
		BigDecimal zero = new BigDecimal(0);
	
		BigDecimal high = vTradeLogService.getInTimeHighPrice(bqCode, beginTime, endTime);
		BigDecimal low = vTradeLogService.getInTimeLowPrice(bqCode, beginTime, endTime);
		BigDecimal first = vTradeLogService.getInTimeFirstPrice(bqCode, beginTime, endTime);
		BigDecimal last = vTradeLogService.getInTimeLastPrice(bqCode, beginTime, endTime);
		BigDecimal vol = vTradeLogService.getInTimeVol(bqCode, beginTime, endTime);
		KLineTrade trade = new KLineTrade(0, bqCode, high, low, first, last, vol, endTime);
		
		if (trade.getHigh().compareTo(zero) > 0 && trade.getLow().compareTo(zero) > 0
				&& trade.getFirst().compareTo(zero) > 0 && trade.getLast().compareTo(zero) > 0
				&& trade.getVol().compareTo(zero) > 0) {
			
//			kLineTradeService.insert(trade, "_v_one_minute");
			
			Map<String, Integer> timeMap = new HashMap<String, Integer>();
			timeMap.put("_v_one_minute", 60);
			timeMap.put("_v_three_minutes", 180);
			timeMap.put("_v_five_minutes", 300);
			timeMap.put("_v_fifteen_minutes", 900);
			timeMap.put("_v_half_hour", 1800);
			timeMap.put("_v_one_hour", 3600);
			timeMap.put("_v_two_hours", 7200);
			timeMap.put("_v_four_hours", 14400);
			timeMap.put("_v_six_hours", 21600);
			timeMap.put("_v_twelve_hours", 43200);
			timeMap.put("_v_one_day", 86400);
			timeMap.put("_v_one_week", 86400 * 7);
			timeMap.put("_v_one_month", 86400 * 30);
			
			Map<String, String> typeMap = new HashMap<String, String>();
			typeMap.put("_v_one_minute", BTCQMemecacheKeys.V_TICKER_ONEMINUTE);
			typeMap.put("_v_three_minutes", BTCQMemecacheKeys.V_TICKER_THREEMINUTES);
			typeMap.put("_v_five_minutes", BTCQMemecacheKeys.V_TICKER_FIVEMINUTES);
			typeMap.put("_v_fifteen_minutes", BTCQMemecacheKeys.V_TICKER_FIFTEENMINUTES);
			typeMap.put("_v_half_hour", BTCQMemecacheKeys.V_TICKER_HALFHOUR);
			typeMap.put("_v_one_hour", BTCQMemecacheKeys.V_TICKER_ONEHOUR);
			typeMap.put("_v_two_hours", BTCQMemecacheKeys.V_TICKER_TWOHOURS);
			typeMap.put("_v_four_hours", BTCQMemecacheKeys.V_TICKER_FOURHOURS);
			typeMap.put("_v_six_hours", BTCQMemecacheKeys.V_TICKER_SIXHOURS);
			typeMap.put("_v_twelve_hours", BTCQMemecacheKeys.V_TICKER_TWELVEHOURS);
			typeMap.put("_v_one_day", BTCQMemecacheKeys.V_TICKER_ONEDAY);
			typeMap.put("_v_one_week", BTCQMemecacheKeys.V_TICKER_ONEWEEK);
			typeMap.put("_v_one_month", BTCQMemecacheKeys.V_TICKER_ONEMONTH);
			
			long morning = TimeUtil.getTimesmorning(beginTime * 1000);
//			String oneMinute = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_ONEMINUTE + bqCode);
//			if (!StringUtil.isEmpty(oneMinute)) {
//				Gson gson = new Gson();
//				JSONArray datas = new JSONArray(oneMinute);
//				datas.put(new JSONObject(gson.toJson(trade)));
//				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEMINUTE  + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, datas.toString());
//			}
			
			for (Entry<String, Integer> entry : timeMap.entrySet()) {
				String key = entry.getKey();
				int interval = entry.getValue(); 
				if (interval == 0) continue;
				
				long time = (beginTime - morning) / interval * interval + interval + morning;
				if ("_v_one_week".equals(key)) {
					time = TimeUtil.getWeekend(beginTime) + 86400l;
				} else if ("_v_one_month".equals(key)) {
					time = TimeUtil.getMonthLastDay(beginTime) + 86400l;
				}
				
				if (needUpdate) {
					KLineTrade kline = kLineTradeService.updateFromOneMinute(trade, key, time);
				
					String type = typeMap.get(key);
					String arrayStr = spyMemcachedClient.get(type + bqCode);
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
							spyMemcachedClient.set(type + bqCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
						} else {
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
	
	private void doOneMinuteSimuKLine(List<VTransaction> transactionList, long startTime, long endTime) throws JSONException {
		for (VTransaction bq : transactionList) {
			long beginTime = startTime;
			String bqCode = bq.getTransaction_code(); // 获取合约号
//			vTransactionService.createTable(bqCode);// 无表则先创建表
			
			long weekLastTime = kLineTradeService.getlastTime(bqCode, "_v_one_week");
			if (weekLastTime <= 0) {
				doOtherKline(bqCode, "_v_one_week", "_v_one_day", BTCQMemecacheKeys.TICKER_ONEDAY);
			}
			long monthLastTime = kLineTradeService.getlastTime(bqCode, "_v_one_month");
			if (monthLastTime <= 0) {
				doOtherKline(bqCode, "_v_one_month", "_v_one_day", BTCQMemecacheKeys.TICKER_ONEDAY);
			}
			
			if (beginTime == 0) {
				beginTime = kLineTradeService.getlastTime(bqCode, "_v_one_minute");
			}
			
			if (beginTime == 0) {
				beginTime = endTime - 60;
			}
			
			while (beginTime < endTime) {
				updateOneMinuteSimuKLine(bqCode, beginTime, beginTime + 60, true);
				beginTime = beginTime + 60;
			}
			
			updateOneMinuteSimuKLine(bqCode, endTime, endTime + 60, false);
		}
	}

}
