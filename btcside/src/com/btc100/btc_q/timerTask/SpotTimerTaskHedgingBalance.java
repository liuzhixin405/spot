package com.btc100.btc_q.timerTask;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQSpotMarket;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQSpotMarketService;
import com.btc100.btc_q.util.HuobiApiUtil;
import com.btc100.btc_q.util.OKexApiUtil;

public class SpotTimerTaskHedgingBalance {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private BQSpotMarketService bqSpotMarketService;
	
	Logger logger = Logger.getLogger(TimerTaskHedging.class);
	private static HuobiApiUtil apiUtil = null; 
	private static OKexApiUtil okecUtil = null; 
	
	public void run() {
		
		if (apiUtil == null) {
			try {
				apiUtil = new HuobiApiUtil(UrlConfig.getValue("SpotHuobiSecretKey"), UrlConfig.getValue("SpotHuobiAccessKey"));
				apiUtil.getAccounts();
			} catch (UnsupportedEncodingException | JSONException e) {
				e.printStackTrace();
			}
		}
		
		Map<String, BigDecimal> map = null;
		try {
			map = apiUtil.getBalance();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (okecUtil == null) {
			okecUtil = new OKexApiUtil(UrlConfig.getValue("SpotOkexSecretKey"), UrlConfig.getValue("SpotOkexAccessKey"), UrlConfig.getValue("SpotOkexPwd"));
		}
		
		Map<String, BigDecimal> okMap = null;
		if (okecUtil != null) {
			okMap = okecUtil.getAccounts();
		}
		
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}
		
		List<BQSpotMarket> markets = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_MARKET);
		if (markets == null) {
			markets = bqSpotMarketService.findAllUsing();
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_MARKET,BTCQMemecacheKeys.TIMES.ONE_MIN, markets);
		}
		
		if (map != null) {
			for (BQSpotMarket market : markets) {
				String marketName = market.getMarket_name().toLowerCase();
				if (map.containsKey(marketName)) {
					spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_HEDGING_BALANCE + marketName, BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get(marketName));
				}
			}
			
			for(BQSpotMarketCoinPair pair : coinPairs) {
				String coinName = pair.getCoin_name().toLowerCase();
				if (map.containsKey(coinName)) {
					spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_HEDGING_BALANCE + coinName, BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get(coinName));
				}
			}
		}
		
		if (okMap != null) {
			String okb = "okb";
			if (okMap.containsKey("okb")) {
				spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_HEDGING_BALANCE + okb, BTCQMemecacheKeys.TIMES.TEN_SECOND, okMap.get(okb));
			}
			
			if (okMap.containsKey("usdt")) {
				spyMemcachedClient.set("okex_" + BTCQMemecacheKeys.SPOT_HEDGING_BALANCE + "usdt", BTCQMemecacheKeys.TIMES.TEN_SECOND, okMap.get("usdt"));
			}
		}
		
	}
}
