package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQUsdrateService;
import com.btc100.btc_q.service.SpotMemberTradeDetailService;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 币币交易 API（交易记录）
 * 
 * @author Administrator
 * 
 */
public class SpotTimerTaskTradesJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private SpotMemberTradeDetailService memberTradeDetailService;
	@Autowired
	private SpotTradeLogService spotTradeLogService;
	@Autowired
	private BQUsdrateService bqUsdrateService;
	
	private static String[] marketArray = null;

	public void run() {
		
		if (marketArray == null) {
			String contracts = UrlConfig.getValue("spotTrade.markets");
			if (!StringUtil.isEmpty(contracts)) {
				marketArray = contracts.split(",");
			}
		}
		
		if (marketArray == null || marketArray.length == 0) return;
		
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}
		
		for (String market : marketArray) {
			BQSpotMarketCoinPair coinPair = null;
			for (BQSpotMarketCoinPair marketCoinPair : coinPairs) {
				if (marketCoinPair.getCoin_pair_name().equals(market)) {
					coinPair = marketCoinPair;
					break;
				}
			}
			
			if (coinPair == null) continue;
			
			String bqCode = coinPair.getCoin_pair_name(); // 获取合约号
			spyMemcachedClient.set(BTCQMemecacheKeys.TRADE_CODE + bqCode, BTCQMemecacheKeys.TIMES.TRADE_CODE, memberTradeDetailService.getAllJsonTopN(bqCode, Contents.TRADE_JSON).toString());
			
			tickersData(coinPair);
			
			Integer digits = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.DIGITS);
			if (digits == null) {
				digits = Utils.getDigitsFromMinFloat(coinPair.getMin_float_price());
				spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
			}
			
			int amountPrecision = coinPair.getDecimals();
			JSONObject json = new JSONObject();
			json.put("symbol", bqCode);
			json.put("pricePrecision", digits);
			json.put("amountPrecision", amountPrecision);
			json.put("market", coinPair.getMarket_coin_name().toLowerCase());
			json.put("coin", coinPair.getCoin_name().toLowerCase());
			json.put("showName", coinPair.getCoin_name() + "/" + coinPair.getMarket_coin_name());
			
			JSONArray array = new JSONArray();
			String symbols = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_SYMBOLS_DATA);
			if (symbols == null) {
				array.put(json);
			} else {
				try {
					array = new JSONArray(symbols);
					boolean have = false;
					for (int i = 0; i < array.length(); i++) {
						org.json.JSONObject o = array.getJSONObject(i);
						if (o.getString("symbol").equals(json.getString("symbol"))) {
							have = true;
							o.put("symbol", bqCode);
							o.put("pricePrecision", digits);
							o.put("amountPrecision", amountPrecision);
							o.put("market", coinPair.getMarket_coin_name().toLowerCase());
							o.put("coin", coinPair.getCoin_name().toLowerCase());
							o.put("showName", coinPair.getCoin_name() + "/" + coinPair.getMarket_coin_name());
							break;
						}
					}
					
					if (!have) {
						array.put(json);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_SYMBOLS_DATA, BTCQMemecacheKeys.TIMES.ONE_HOUR, array.toString());
		}
	}
	
	private void tickersData(BQSpotMarketCoinPair pair) {
		Map<String, Object> map = new HashMap<String, Object>();
		String pairName = pair.getCoin_pair_name();
		map.put("pairId", pair.getId());
		map.put("pairName", pairName);
		map.put("marketName", pair.getMarket_coin_name());
		map.put("coinName", pair.getCoin_name());
		
		Integer digits = spyMemcachedClient.get(pairName + BTCQMemecacheKeys.DIGITS);
		if (digits == null) {
			digits = Utils.getDigitsFromMinFloat(pair.getMin_float_price());
			spyMemcachedClient.set(pairName + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}
		
		JSONObject bqLastTicker = null;
		String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + pairName);
		if (!StringUtil.isEmpty(ticker)) {
			bqLastTicker = JSONObject.parseObject(ticker);
		}
		
		double high = 0.0;
		double low = 0.0;
		double vol = 0.0;
		double lastPrice = 0.0;
		double firstPrice = 0.0;
		
		BigDecimal last = spyMemcachedClient.get(pairName + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (last == null) {
			last = pair.getLatest_price();
		}
		
		if (last == null) last = BigDecimal.ZERO;
		lastPrice = last.doubleValue();
		
		if (bqLastTicker == null) {
			long time = TimeUtil.getYesterdayTime();
			BigDecimal highPrice = spotTradeLogService.findHighPrice(pairName, time).setScale(digits, BigDecimal.ROUND_DOWN);
			high = highPrice.doubleValue();
			
			BigDecimal lowPrice = spotTradeLogService.findLowPrice(pairName, time).setScale(digits, BigDecimal.ROUND_DOWN);
			low = lowPrice.doubleValue();
			
			BigDecimal volume = spotTradeLogService.findBTCVol(pairName, time);
			volume = volume == null ? new BigDecimal(0) : volume;
			vol = volume.doubleValue();
		} else {
			high = bqLastTicker.getDouble("high");
			low = bqLastTicker.getDouble("low");
			vol = bqLastTicker.getDouble("vol");
			lastPrice = bqLastTicker.getDouble("last");
		}
		
		BigDecimal first = spyMemcachedClient.get(pairName + BTCQMemecacheKeys.FIRST_PRICE);
		if (first == null) {
			first = spotTradeLogService.findFirstPrice(pairName, TimeUtil.getTimesmorning());
			spyMemcachedClient.set(pairName + BTCQMemecacheKeys.FIRST_PRICE, BTCQMemecacheKeys.TIMES.BPI, first);
		}
		
		if (first != null) {
			firstPrice = first.doubleValue();
		}
		
		BigDecimal usdRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
		if (StringUtil.isEmpty(usdRate)) {
			usdRate = bqUsdrateService.findUsdRateOnuse(1);
			spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, usdRate);
		}
		
		BigDecimal lastCnyPrice = new BigDecimal(lastPrice).multiply(usdRate).setScale(digits, BigDecimal.ROUND_HALF_UP);
		
		map.put("firstPrice", firstPrice);
		map.put("lastPrice", lastPrice);
		map.put("lastCnyPrice", lastCnyPrice.doubleValue());
		map.put("highPrice", high);
		map.put("lowPrice", low);
		
		// 涨跌幅
		if (lastPrice <= 0.0) {
			map.put("fudu", "0%");
		} else {
			DecimalFormat df = new DecimalFormat("0.00");
			double rate = 0.0;
			if (firstPrice > 0.0) {
				rate = (lastPrice - firstPrice) / firstPrice;
			}
			
			String div = df.format(rate * 100);
			map.put("fudu", div.toString() + "%");
		}
		
		// 成交量
		map.put("vol", vol);
		
		List<Map<String, Object>> lisMap = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_BASE); 
		if (lisMap == null) {
			lisMap = new ArrayList<Map<String, Object>>();
		}
		
		for (Map<String, Object> item : lisMap) {
			if (item.get("pairName").equals(map.get("pairName"))) {
				lisMap.remove(item);
				break;
			}
		}
		lisMap.add(map);
		
		spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_BASE, BTCQMemecacheKeys.TIMES.ONE_HOUR, lisMap);
	}
}
