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
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.KLineTrade;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.KLineTradeService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.google.gson.Gson;

/**
 * 1分钟内交易（市场行情）
 * 
 * @author Administrator
 * 
 */
public class DeliveryTimerTaskOneMinTrade extends BaseTask {
	
	@Autowired
	private KLineTradeService kLineTradeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private DeliveryTradeLogService tradeLogService;

	private static String[] contractArray = null;

	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("deliveryTrade.contracts");
			contractArray = contracts.split(",");
		}

		if (contractArray == null || contractArray.length == 0)
			return;
		
		List<DeliveryTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, lstTrads);
		}
		if (lstTrads == null || lstTrads.size() == 0) return;
		
		long currentTimeMillis = transactionService.time() - 1;
		// 获取当前时间所在的1分钟区间内的起始时间点（0:01:00、0:02:00、0:03:00、0:04:00、0:05:00...）
		long typeTime = 60L;
		long oneMinAgoMillis = currentTimeMillis / typeTime * typeTime;
		String timeTypeStr = "_one_minute";
		
		// 遍历在用合约，找出合约号
		for (String contract : contractArray) {
			String[] codeDelivery = contract.split("_");
			if (codeDelivery.length == 0) {
				continue;
			}
			String typeCode = codeDelivery[0];
			String deliveryType = codeDelivery[1];
			
			DeliveryTransaction transaction = null;
			for (DeliveryTransaction bqTransaction : lstTrads) {
				if (bqTransaction.getTransaction_type_code().equals(typeCode) && bqTransaction.getDelivery_type().equals(deliveryType)) {
					transaction = bqTransaction;
					break;
				}
			}

			if (transaction == null) {
				continue;
			}
			
			if (transaction.getTransaction_state() != 1) {
				continue;
			}
			
			Long lastTime = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_TICKER_TIME + timeTypeStr + "_" + transaction.getTransaction_code());
			if (lastTime == null) {
				lastTime = 0L;
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_TICKER_TIME + timeTypeStr + "_" + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, oneMinAgoMillis);
			
			try {
				doOneMinuteDeliveryKLine(transaction, lastTime, oneMinAgoMillis);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
//	K线，合约类型（btc，eth）+ 交割类型（日，季度）+ _one_minute_trade
//	日交割（btc_d_one_minute_trade） 周交割（btc_w_one_minute_trade）季度交割（btc_q_one_minute_trade）
	private void updateOneMinuteDeliveryKLine(String code, String prefixCode, long beginTime, long endTime, boolean needUpdate) throws JSONException {
		BigDecimal zero = new BigDecimal(0);
	
		BigDecimal high = tradeLogService.getInTimeHighPrice(code, beginTime, endTime);
		BigDecimal low = tradeLogService.getInTimeLowPrice(code, beginTime, endTime);
		BigDecimal first = tradeLogService.getInTimeFirstPrice(code, beginTime, endTime);
		BigDecimal last = tradeLogService.getInTimeLastPrice(code, beginTime, endTime);
		BigDecimal vol = tradeLogService.getInTimeVol(code, beginTime, endTime);
		KLineTrade trade = new KLineTrade(0, prefixCode, high, low, first, last, vol, endTime);
		
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
			typeMap.put("_one_minute", BTCQMemecacheKeys.D_TICKER_ONEMINUTE);
			typeMap.put("_three_minutes", BTCQMemecacheKeys.D_TICKER_THREEMINUTES);
			typeMap.put("_five_minutes", BTCQMemecacheKeys.D_TICKER_FIVEMINUTES);
			typeMap.put("_fifteen_minutes", BTCQMemecacheKeys.D_TICKER_FIFTEENMINUTES);
			typeMap.put("_half_hour", BTCQMemecacheKeys.D_TICKER_HALFHOUR);
			typeMap.put("_one_hour", BTCQMemecacheKeys.D_TICKER_ONEHOUR);
			typeMap.put("_two_hours", BTCQMemecacheKeys.D_TICKER_TWOHOURS);
			typeMap.put("_four_hours", BTCQMemecacheKeys.D_TICKER_FOURHOURS);
			typeMap.put("_six_hours", BTCQMemecacheKeys.D_TICKER_SIXHOURS);
			typeMap.put("_twelve_hours", BTCQMemecacheKeys.D_TICKER_TWELVEHOURS);
			typeMap.put("_one_day", BTCQMemecacheKeys.D_TICKER_ONEDAY);
			typeMap.put("_one_week", BTCQMemecacheKeys.D_TICKER_ONEWEEK);
			typeMap.put("_one_month", BTCQMemecacheKeys.D_TICKER_ONEMONTH);
			
			
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
					String arrayStr = spyMemcachedClient.get(type + "_" + prefixCode);
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
							spyMemcachedClient.set(type + "_" + prefixCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, array.toString());
						}
					}
				} else {
					String type = typeMap.get(key);
					Gson gson = new Gson();
					KLineTrade newDeliveryKLine = new KLineTrade(trade);
					newDeliveryKLine.setTime(time);
					spyMemcachedClient.set("last_" + type + "_" + prefixCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, gson.toJson(newDeliveryKLine));
				}
			}
		}
	}
	
	private void doOneMinuteDeliveryKLine(DeliveryTransaction transaction, long startTime, long endTime) throws JSONException {
		long beginTime = startTime;
		String typeCode = transaction.getTransaction_type_code(); // 获取type_code
		String code = transaction.getTransaction_code(); // 获取code
		// 日交割（btc_d_one_minute_trade） 周交割（btc_w_one_minute_trade）季度交割（btc_q_one_minute_trade）
		String prefixCode = typeCode + "_" + transaction.getDelivery_type();
//		transactionService.createTable(code, typeCode, transaction.getDelivery_type());// 无表则先创建表
		
		long weekLastTime = kLineTradeService.getlastTime(prefixCode, "_one_week");
		if (weekLastTime <= 0) {
			doOtherKline(prefixCode, "_one_week", "_one_day", BTCQMemecacheKeys.D_TICKER_ONEDAY);
		}
		long monthLastTime = kLineTradeService.getlastTime(prefixCode, "_one_month");
		if (monthLastTime <= 0) {
			doOtherKline(prefixCode, "_one_month", "_one_day", BTCQMemecacheKeys.D_TICKER_ONEDAY);
		}
		
		if (beginTime == 0) {
			beginTime = kLineTradeService.getlastTime(prefixCode, "_one_minute");
		}
		
		if (beginTime == 0) {
			beginTime = endTime - 60;
		}
		
		while (beginTime < endTime) {
			updateOneMinuteDeliveryKLine(code, prefixCode, beginTime, beginTime + 60, true);
			beginTime = beginTime + 60;
		}
		
		updateOneMinuteDeliveryKLine(code, prefixCode, endTime, endTime + 60, false);
	}

}
