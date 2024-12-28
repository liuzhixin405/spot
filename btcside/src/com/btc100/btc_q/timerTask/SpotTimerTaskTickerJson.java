package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btc100.btc_q.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQLastTicker;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.service.BQLastTickerService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 币币交易 API（市场行情）
 * 
 * @author Administrator
 * 
 */
public class SpotTimerTaskTickerJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQLastTickerService bqLastTickerService;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private SpotTradeLogService spotTradeLogService;
	@Autowired
	private SpotPendingDelegationService spotPendingDelegationService;
	
	private static String[] marketArray = null;
	
	public void run() {
		if (marketArray == null) {
			String contracts = UrlConfig.getValue("spotTrade.markets");
			if (!StringUtil.isEmpty(contracts)) {
				marketArray = contracts.split(",");
			}
		}
		
		if (marketArray == null && marketArray.length == 0) return;
		
		JSONArray array = new JSONArray();
		
		// 获取当前时间的24小时时间（时间戳）
		long yesterdayTime = TimeUtil.getYesterdayTime();
		
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
			
			BigDecimal high = BigDecimal.ZERO;
			BigDecimal low = BigDecimal.ZERO;
			BigDecimal buy = BigDecimal.ZERO;
			BigDecimal sell = BigDecimal.ZERO;
			BigDecimal last = BigDecimal.ZERO;
			BigDecimal vol = BigDecimal.ZERO;
			
			String bqCode = coinPair.getCoin_pair_name(); // 获取合约号
			Integer digits = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.DIGITS);
			if (digits == null) {
				digits = Utils.getDigitsFromMinFloat(coinPair.getMin_float_price());
				spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
			}
			
			// 1.先从缓存中查找 最新成交价
			// 从数据库中算出最高价 btc1407_trade_log
			high = spotTradeLogService.findHighPrice(bqCode, yesterdayTime);
			if (high == null) high = BigDecimal.ZERO;
			
			// 从数据库中算出最低价 btc1407_trade_log
			low = spotTradeLogService.findLowPrice(bqCode, yesterdayTime);
			if (low == null) low = BigDecimal.ZERO;
			
			String strLast = bqCode + BTCQMemecacheKeys.BTCLATESTPRICE;
			last = spyMemcachedClient.get(strLast);
			if (last == null) {
				// 从数据库中算出最新成交价 
				last = bqSpotMarketCoinPairService.getByName(bqCode).getLatest_price();
			}
			
			if (last == null) last = BigDecimal.ZERO;
			
			SpotPendingDelegation pendingBuy = spotPendingDelegationService.getBuy1(bqCode);
			SpotPendingDelegation pendingSell = spotPendingDelegationService.getSell1(bqCode);
			
			if (pendingBuy == null || pendingSell == null) {
				buy = new BigDecimal(0);
				sell = new BigDecimal(0);
			} else {
				// 从数据库中算出买一价
				buy = pendingBuy.getDelegation_price();
				// 从数据库中算出卖一价
				sell = pendingSell.getDelegation_price();
			}
			
			// 从数据库中算出成交量
			vol = spotTradeLogService.findBTCVol(bqCode, yesterdayTime);
			vol = vol == null ? new BigDecimal(0) : vol;
			
			BigDecimal trades = spotTradeLogService.findTradeBTC(bqCode, yesterdayTime);
			trades = (trades == null ? BigDecimal.ZERO : trades);
			
			high = high.setScale(digits, BigDecimal.ROUND_DOWN);
			low = low.setScale(digits, BigDecimal.ROUND_DOWN);
			buy = buy.setScale(digits, BigDecimal.ROUND_DOWN);
			sell = sell.setScale(digits, BigDecimal.ROUND_DOWN);
			last = last.setScale(digits, BigDecimal.ROUND_DOWN);
			vol = vol.setScale(digits, BigDecimal.ROUND_DOWN);
			trades = trades.setScale(digits, BigDecimal.ROUND_DOWN);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("bqCode", bqCode);
			map.put("high", high);
			map.put("low", low);
			map.put("buy", buy);
			map.put("sell", sell);
			map.put("last", last);
			map.put("vol", vol);
			map.put("trades", trades);
			array.put(map);
			map.clear();
			
			JSONObject obj1 = new JSONObject();
			try {
				obj1.put("bqCode", bqCode);
				obj1.put("high", high);
				obj1.put("low", low);
				obj1.put("buy", buy);
				obj1.put("sell", sell);
				obj1.put("last", last);
				obj1.put("vol", vol);
				obj1.put("trades", trades);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			bqSpotMarketCoinPairService.updateHighAndLow(coinPair.getId(), high, low);
			bqLastTickerService.insertOrUploadBQLastTicker(new BQLastTicker(0, bqCode, high, low, buy, sell, last, vol, trades));
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_BTC + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_BTC, obj1.toString());
		}

	}
	
}
