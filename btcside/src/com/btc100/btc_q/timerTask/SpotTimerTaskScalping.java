package com.btc100.btc_q.timerTask;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQSpotArbitrageSetting;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQSpotQuantSetting;
import com.btc100.btc_q.entity.SpotMemberTradeDetail;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotTradeLog;
import com.btc100.btc_q.service.BQSpotArbitrageSettingService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQSpotQuantSettingService;
import com.btc100.btc_q.service.SpotMemberTradeDetailService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.service.SpotPendingLogService;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpotTimerTaskScalping {
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private BQSpotArbitrageSettingService bqSpotArbitrageSettingService;
	@Autowired
	private SpotPendingDelegationService pendingDelegationService;
	@Autowired
	private SpotTradeLogService tradeLogService;
	@Autowired
	private SpotMemberTradeDetailService memberTradeDetailService;
	@Autowired
	private SpotPendingLogService spotPendingLogService;
	@Autowired
	private BQSpotQuantSettingService bqQuantSettingService;
	
	private static Map<String, Integer> countDown = new HashMap<String, Integer>();
	private static Map<String, Long> transIdMap = new HashMap<String, Long>();
	
	private static int timeCount = 0;
	
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
		
		if (countDown.size() == 0) {
			for (String market : marketArray) {
				BQSpotMarketCoinPair coinPair = null;
				for (BQSpotMarketCoinPair marketCoinPair : coinPairs) {
					if (marketCoinPair.getCoin_pair_name().equals(market)) {
						coinPair = marketCoinPair;
						break;
					}
				}
				
				if (coinPair == null) continue;
				
				if (!countDown.containsKey(coinPair.getCoin_pair_name())) {
					countDown.put(coinPair.getCoin_pair_name(), 0);
					transIdMap.put(coinPair.getCoin_pair_name(), coinPair.getId());
				}
			}
		}
		
		for (String code : countDown.keySet()) {
			BQSpotMarketCoinPair coinPair = null;
			for (BQSpotMarketCoinPair marketCoinPair : coinPairs) {
				if (marketCoinPair.getCoin_pair_name().equals(code)) {
					coinPair = marketCoinPair;
					break;
				}
			}
			
			if (coinPair == null) continue;
			
			if (coinPair.getSuspend()) {
				continue;
			}
			
			int value = countDown.get(code);
			if (value == 0) {
				int sleepTime = 1000;
				do {
					BQSpotArbitrageSetting setting = null;
					try {
						setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + code);
					} catch (Exception e) {}
					
					if (setting == null) {
						setting = bqSpotArbitrageSettingService.getArbitrageSettingByCode(code);
						if (setting == null) {
							setting = new BQSpotArbitrageSetting();
							setting.setRunning(false);
							setting.setScalping(false);
							setting.setHedging(false);
							setting.setRisk_manage(false);
							setting.setDepth_uid(0L);
						}
						
						spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
					}
					
					if (setting == null || !setting.getScalping()) break;
					
					SpotPendingDelegation sell1 = pendingDelegationService.getSell1(code);
					SpotPendingDelegation buy1 = pendingDelegationService.getBuy1(code);
					
					if (sell1 == null || buy1 == null) break;
					
					if (setting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0
							&& sell1.getDelegation_price().subtract(buy1.getDelegation_price()).compareTo(setting.getScalping_depth_range().multiply(buy1.getDelegation_price())) > 0) {
						break;
					}
					
					Integer digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.DIGITS);
					if (digits == null) {
						digits = Utils.getDigitsFromMinFloat(coinPair.getMin_float_price());
						spyMemcachedClient.set(coinPair.getCoin_pair_name() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
					}
					
					BigDecimal minChange = new BigDecimal(1.0 / Math.pow(10, digits));
					minChange = minChange.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
					if (sell1.getDelegation_price().subtract(buy1.getDelegation_price()).abs().compareTo(minChange) <= 0) {
						break;
					}
					
					boolean isSell = true;
					if (Math.random() < 0.5) {
						isSell = false;
					}
					
					int min = setting.getScalping_frequency_min();
					int max = setting.getScalping_frequency_max();
					int num = min + (int)(Math.random() * (max - min));
					if (num == 0) {
						sleepTime = (60 + 30) * 1000;
					} else {
						sleepTime = (int)(60000.0 / num);
					}
					
					int addi = (int)(Math.random() * 20) - 10;
					sleepTime += (addi * 1000);
					if (sleepTime < 1000) {
						sleepTime = 1000;
					}


					int amountDigits = coinPair.getDecimals();
					BigDecimal random = new BigDecimal(Math.random());
					BigDecimal amount = setting.getScalping_min_each().add(random.multiply(setting.getScalping_max_each().subtract(setting.getScalping_min_each())));
					amount = amount.setScale(amountDigits, RoundingMode.DOWN);

					BigDecimal high = sell1.getDelegation_price().subtract(minChange);
					BigDecimal low = buy1.getDelegation_price().add(minChange);
					
					if (high.compareTo(low) < 0 || high.subtract(low).compareTo(low.multiply(new BigDecimal(0.05))) > 0) {
						continue;
					}
					
					BigDecimal price = low.add(new BigDecimal(Math.random()).multiply(high.subtract(low)));
					price = price.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
					
					if (sell1.getDelegation_price().compareTo(price) == 0) {
						price = sell1.getDelegation_price().subtract(minChange);
					}
					
					if (buy1.getDelegation_price().compareTo(price) == 0) {
						price = buy1.getDelegation_price().add(minChange);
					}
					
					price = price.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
					if (sell1.getDelegation_price().compareTo(price) == 0 
							|| buy1.getDelegation_price().compareTo(price) == 0) {
						continue;
					}
					
					SpotTradeLog sellSpotTradeLog = new SpotTradeLog(coinPair.getId(), code, 2, price, amount, BigDecimal.ZERO, 
							Contents.BQMEMBER_ADMIN, coinPair.getCoin_id(), coinPair.getMarket_coin_id(), price.multiply(amount), 0);
					tradeLogService.saveSpotTradeLog(sellSpotTradeLog);
					
					SpotTradeLog buySpotTradeLog = new SpotTradeLog(coinPair.getId(), code, 1, price, amount, BigDecimal.ZERO, 
							Contents.BQMEMBER_ADMIN, coinPair.getCoin_id(), coinPair.getMarket_coin_id(), price.multiply(amount), 0);
					tradeLogService.saveSpotTradeLog(buySpotTradeLog);
					
					SpotMemberTradeDetail tradeDetail = new SpotMemberTradeDetail(buySpotTradeLog.getTrade_id(), Contents.BQMEMBER_ADMIN, "", Contents.BQMEMBER_ADMIN, "", 
							sellSpotTradeLog.getTrade_id(), 1, isSell ? 2 : 1, price, amount, coinPair.getId(), code);
					memberTradeDetailService.save(tradeDetail);
				
					spyMemcachedClient.set(code + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, price);
					
				} while (false);
				
				value = sleepTime / 1000 - 1;
			} else {
				value -= 1;
			}
			
			if (value < 0) value = 0;
			countDown.put(code, value);
		}
		
		if (timeCount % 1800 == 0) {
			timeCount = 0;
			for (String code : countDown.keySet()) {
				BQSpotMarketCoinPair coinPair = null;
				for (BQSpotMarketCoinPair marketCoinPair : coinPairs) {
					if (marketCoinPair.getCoin_pair_name().equals(code)) {
						coinPair = marketCoinPair;
						break;
					}
				}
				if (coinPair == null) continue;
				
				long yesterday = System.currentTimeMillis() / 1000 - 3600*24;
				tradeLogService.deleteTradeScalpingLog(code, Contents.BQMEMBER_ADMIN, yesterday);
				memberTradeDetailService.deleteTradeScalpingDetail(code, Contents.BQMEMBER_ADMIN, yesterday);
				
				BQSpotQuantSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_QUANT_SETTING);
				if (setting == null) {
					setting = bqQuantSettingService.get();
					if (setting != null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_QUANT_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
					}
				}
				
				if (setting != null) {
					String[] quantUsers = setting.getUids().split(",");
					for (String quantUserId : quantUsers) {
						long userId = Convert.toLong(quantUserId, 0);
						if (userId > 0) {
							spotPendingLogService.deleteByUidBeforeTime(code, userId, yesterday, 4);
						}
					}
				}
			}
		}
		
		timeCount++;
	}
}
