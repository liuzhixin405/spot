package com.btc100.btc_q.timerTask;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQSpotHedgingRecord;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.service.BQSpotHedgingRecordService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;

public class SpotTimerTaskHedgingCheck {
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private BQSpotHedgingRecordService bqHedgingRecordService;
	
	private static final String LAST_CHECK_TIME = "last_hedging_check_time_";
	
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
		
		Long currTime = System.currentTimeMillis() / 1000;
		for (String market : marketArray) {
			BQSpotMarketCoinPair coinPair = null;
			for (BQSpotMarketCoinPair marketCoinPair : coinPairs) {
				if (marketCoinPair.getCoin_pair_name().equals(market)) {
					coinPair = marketCoinPair;
					break;
				}
			}
			
			if (coinPair == null) continue;
			
			String code = coinPair.getCoin_pair_name();
			Long lastTime = spyMemcachedClient.get(LAST_CHECK_TIME + code);
			if (lastTime != null && lastTime.longValue() != 0) {
				List<BQSpotHedgingRecord> records = new ArrayList<BQSpotHedgingRecord>();
				List<BQSpotHedgingRecord> rds = bqHedgingRecordService.findRecordsByCodeAndStateBetweenTime(code, 2, lastTime, currTime);
				records.addAll(rds);
				
				String message = "";
				for (int i = 0; i < records.size(); i++) {
					if (i > 10) break;
					
					BQSpotHedgingRecord record = records.get(i);
					message += record.getTransaction_code().toUpperCase() + "币币交易" + (record.getTrade_direction() == 1 ? "买入 " : "卖出 ") + record.getTrade_amount().doubleValue()
							+ " 个 " + record.getCoin_name() + "，价格 " + record.getTrade_price().doubleValue() + record.getMarket_coin_name() + "，站外对冲失败，请尽快核实<br>"; 
				}
				
				if (!StringUtil.isEmpty(message)) {
					final String content = message;
					Runnable runnable = new Runnable() {
			            public void run() {
		            	   	try {
		            	   		String email = UrlConfig.getValue("spot.hedgingFailureNotify.email");
			            		SendMail themail = new SendMail();
		            			themail.send(email, content);
			            	} catch (Exception e) {
			            	}
		            	}
			        };
			        Thread thread = new Thread(runnable);
			        thread.start();
				}
			}
		
			spyMemcachedClient.set(LAST_CHECK_TIME + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, currTime);
		}
	}
}
