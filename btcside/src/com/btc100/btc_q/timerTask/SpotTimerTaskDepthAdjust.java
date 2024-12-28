package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQSpotArbitrageSetting;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQSpotArbitrageSettingService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.SpotPendingDelegationCacheService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

public class SpotTimerTaskDepthAdjust {
	
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private SpotPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private SpotPendingDelegationService pendingDelegationService;
	@Autowired
	private BQSpotArbitrageSettingService bqArbitrageSettingService;
	
	Map<String, Map<BigDecimal, BigDecimal>> toRemoveMaps = new HashMap<String, Map<BigDecimal, BigDecimal>>();
	Map<String, Map<BigDecimal, BigDecimal>> maps = new HashMap<String, Map<BigDecimal, BigDecimal>>();
	
	public static String[] marketArray = null;
	
	private static final int DEPTH_LEVEL = 40;
	
	public void run() throws JSONException {
		
		if (marketArray == null) {
			String contracts = UrlConfig.getValue("spotTrade.markets");
			marketArray = contracts.split(",");
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
			
			if (coinPair.getSuspend()) {
				continue;
			}
			
			String code = coinPair.getCoin_pair_name();
			String coinName = coinPair.getCoin_name();
			BQSpotArbitrageSetting setting = bqArbitrageSettingService.getArbitrageSettingByCode(code);
			if (setting == null) {
				setting = new BQSpotArbitrageSetting();
				setting.setRunning(false);
				setting.setScalping(false);
				setting.setHedging(false);
				setting.setRisk_manage(false);
				setting.setDepth_uid(0L);
			}
				
			spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			
			if (toRemoveMaps.containsKey(code + "_buy")) {
				Map<BigDecimal, BigDecimal> toRemoveMap = toRemoveMaps.get(code + "_buy");
				if (toRemoveMap.size() > 0) {
					removePending(setting.getDepth_uid(), toRemoveMap, Contents.DIRECTION_BUY, coinPair);
				}
			}
			
			if (toRemoveMaps.containsKey(code + "_sell")) {
				Map<BigDecimal, BigDecimal> toRemoveMap = toRemoveMaps.get(code + "_sell");
				if (toRemoveMap.size() > 0) {
					removePending(setting.getDepth_uid(), toRemoveMap, Contents.DIRECTION_SELL, coinPair);
				}
			}
			
			if (!setting.getRunning()) continue;
			
			if (setting.getDepth_self()) {
				continue;
			} 
			
			if (!maps.containsKey(code + "_buy") || !maps.containsKey(code + "_sell")) {
				if (!removePendingAll(setting.getDepth_uid(), coinPair)) continue;
				
				maps.put(code + "_buy", new HashMap<BigDecimal, BigDecimal>());
				maps.put(code + "_sell", new HashMap<BigDecimal, BigDecimal>());
			}

			Integer digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.DIGITS);
			if (digits == null) {
				digits = Utils.getDigitsFromMinFloat(coinPair.getMin_float_price());
				spyMemcachedClient.set(coinPair.getCoin_pair_name() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
			}
			
			JSONObject tick = null;
			Long time = System.currentTimeMillis();
			String symbol = "symbol=" + coinName.toLowerCase() + "usdt&type=step0";
			if (setting.getDepth_url().contains("binance")) {
				symbol = "symbol=" + coinName.toUpperCase() + "USDT&limit=100";
			} else if (setting.getDepth_url().contains("okex")) {
				symbol = "size=100";
			} 
			
			//String strResult = HttpToServer.sendGet(setting.getDepth_url(), symbol);
			String strResult = HttpToServer.sendGet("https://localhost:7282/SpotApi","");
			if (StringUtil.isEmpty(strResult)) {
					if (StringUtil.isEmpty(setting.getSpare_depth_url())) {
						continue;
					}
			
				if (setting.getCurr_use_state() == 0) {
					Long lastTime = spyMemcachedClient.get(BTCQMemecacheKeys.HUOBI_DEPTH_UPDATE + code);
					if (lastTime == null || lastTime == 0) {
						spyMemcachedClient.set(BTCQMemecacheKeys.HUOBI_DEPTH_UPDATE + code, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, time);
					} else {
						if (time > lastTime + 60 * 1000) {
							setting.setCurr_use_state(1);
							bqArbitrageSettingService.update(setting);
							spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
						}
					}
					
					continue;
				} else {
					
				}
			} else {
				try {
					JSONObject obj = new JSONObject(strResult);
					if (setting.getDepth_url().contains("binance") || setting.getDepth_url().contains("okex")) {
						tick = obj;
					} else {
						String status = obj.getString("status");
						if (!"ok".equals(status)) {
							continue;
						}
						
						tick = (JSONObject) obj.get("tick");
					}
				} catch (JSONException e) {
					continue;
				}
				
				if (setting.getCurr_use_state() != 0) {
					setting.setCurr_use_state(0);
					bqArbitrageSettingService.update(setting);
					spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
				}
			}
			
			if (tick == null) continue;
			
			JSONArray buys = tick.getJSONArray("bids");
			JSONArray sells = tick.getJSONArray("asks");

			int amountDigits = coinPair.getDecimals();
			Map<BigDecimal, BigDecimal> newBuys = new HashMap<BigDecimal, BigDecimal>();
			Map<BigDecimal, BigDecimal> needAppendBuys = new HashMap<BigDecimal, BigDecimal>();
			
			BigDecimal buy1 = BigDecimal.ZERO;
			Map<BigDecimal, BigDecimal> depthBuys = maps.get(code + "_buy");
			int step = 1;
//			if (trans.getTransaction_type_code().equals("ft")) {
//				step = 2;
//			}
			
			int order = 1;
			if (step ==  1 && buys.length() > 0 
					&& buys.getJSONArray(0).getDouble(0) < buys.getJSONArray(buys.length() - 1).getDouble(0)) {
				order = -1;
			}
			
			for (int i = 0; i < buys.length(); i = i + step) {
				double price = 0;
				double amount = 0;
				if (step == 1) {
					JSONArray buy = buys.getJSONArray(i);
					if (order != 1) {
						buy = buys.getJSONArray(buys.length() - i - 1);
					}
					
					price = buy.getDouble(0);
					amount = buy.getDouble(1);
				} else {
					price = buys.getDouble(i);
					amount = buys.getDouble(i + 1);
				}
				
				BigDecimal tAmount = new BigDecimal(amount).setScale(amountDigits, BigDecimal.ROUND_DOWN);
				if (tAmount.compareTo(setting.getDepth_amount_min()) < 0 || tAmount.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}
				
				BigDecimal tPrice = new BigDecimal(price).multiply(BigDecimal.ONE.subtract(setting.getDepth_buy_range().multiply(new BigDecimal(0.01)))).setScale(digits, BigDecimal.ROUND_DOWN);
				if (newBuys.containsKey(tPrice)) {
					BigDecimal at = newBuys.get(tPrice);
					tAmount = tAmount.add(at); 
				} else {
					if (newBuys.size() >= DEPTH_LEVEL) {
						break;
					}
				}
				
				if (setting.getDepth_amount_max().compareTo(BigDecimal.ZERO) > 0 && tAmount.compareTo(setting.getDepth_amount_max()) > 0 ) {
					continue;
				}
				
				newBuys.put(tPrice, tAmount);
				
				if (buy1.compareTo(tPrice) < 0) {
					buy1 = tPrice;
				}
			}
			
			for (BigDecimal price : newBuys.keySet()) {
				BigDecimal tAmount = newBuys.get(price);
				if (depthBuys.containsKey(price)) {
					if (depthBuys.get(price).compareTo(tAmount) != 0) {
						needAppendBuys.put(price, tAmount);
					} else {
						depthBuys.remove(price);
					}
				} else {
					needAppendBuys.put(price, tAmount);
				}
			}
			
			maps.put(code + "_buy", newBuys);
			
			removePending(setting.getDepth_uid(), depthBuys, Contents.DIRECTION_BUY, coinPair);
			if (depthBuys.size() > 0) {
				if (toRemoveMaps.containsKey(code + "_buy")) {
					Map<BigDecimal, BigDecimal> toRemoveMap = toRemoveMaps.get(code + "_buy");
					toRemoveMap.putAll(depthBuys);
				} else {
					toRemoveMaps.put(code + "_buy", depthBuys);
				}
			}
			addPending(setting.getDepth_uid(), needAppendBuys, Contents.DIRECTION_BUY, coinPair);
		
			BigDecimal sell1 = BigDecimal.ZERO;
			Map<BigDecimal, BigDecimal> newSells = new HashMap<BigDecimal, BigDecimal>();
			Map<BigDecimal, BigDecimal> needAppendSells = new HashMap<BigDecimal, BigDecimal>();
			Map<BigDecimal, BigDecimal> depthSells = maps.get(code + "_sell");
			order = 1;
			if (step ==  1 && sells.length() > 0 
					&& sells.getJSONArray(0).getDouble(0) > sells.getJSONArray(sells.length() - 1).getDouble(0)) {
				order = -1;
			}
			for (int i = 0; i < sells.length(); i = i + step) {
				double price = 0;
				double amount = 0;
				if (step == 1) {
					JSONArray sell = sells.getJSONArray(i);
					if (order != 1) {
						sell = sells.getJSONArray(sells.length() - i - 1);
					}
					
					price = sell.getDouble(0);
					amount = sell.getDouble(1);
				} else {
					price = sells.getDouble(i);
					amount = sells.getDouble(i + 1);
				}
				
				BigDecimal tAmount = new BigDecimal(amount).setScale(amountDigits, BigDecimal.ROUND_DOWN);
				if (tAmount.compareTo(setting.getDepth_amount_min()) < 0 || tAmount.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}
				
				BigDecimal tPrice = new BigDecimal(price).multiply(BigDecimal.ONE.add(setting.getDepth_sell_range().multiply(new BigDecimal(0.01)))).setScale(digits, BigDecimal.ROUND_UP);
				if (tPrice.compareTo(buy1) <= 0) {
					continue;
				}
				
				if (newSells.containsKey(tPrice)) {
					BigDecimal at = newSells.get(tPrice);
					tAmount = tAmount.add(at); 
				} else {
					if (newSells.size() >= DEPTH_LEVEL) {
						break;
					}
				}
				
				if (setting.getDepth_amount_max().compareTo(BigDecimal.ZERO) > 0 && tAmount.compareTo(setting.getDepth_amount_max()) > 0 ) {
					continue;
				}
				
				newSells.put(tPrice, tAmount);
				
				if (sell1.compareTo(BigDecimal.ZERO) == 0 || sell1.compareTo(tPrice) > 0) {
					sell1 = tPrice;
				}
			}
			
			for (BigDecimal price : newSells.keySet()) {
				BigDecimal tAmount = newSells.get(price);
				if (depthSells.containsKey(price)) {
					if (depthSells.get(price).compareTo(tAmount) != 0) {
						needAppendSells.put(price, tAmount);
					} else {
						depthSells.remove(price);
					}
				} else {
					needAppendSells.put(price, tAmount);
				}
			}
			
			maps.put(code + "_sell", newSells);
			removePending(setting.getDepth_uid(), depthSells, Contents.DIRECTION_SELL, coinPair);
			if (depthSells.size() > 0) {
				if (toRemoveMaps.containsKey(code + "_sell")) {
					Map<BigDecimal, BigDecimal> toRemoveMap = toRemoveMaps.get(code + "_sell");
					toRemoveMap.putAll(depthSells);
				} else {
					toRemoveMaps.put(code + "_sell", depthSells);
				}
			}
			addPending(setting.getDepth_uid(), needAppendSells, Contents.DIRECTION_SELL, coinPair);
			
			// 移除大于买一及小于卖一
			removePending(setting.getDepth_uid(), coinPair, buy1, sell1);
			
			spyMemcachedClient.set(BTCQMemecacheKeys.HUOBI_DEPTH_UPDATE + code, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, time);
		}
	}
	
	private boolean removePendingAll(long userId, BQSpotMarketCoinPair coinPair) {
		// 注：只有开仓单，不用锁持单
		boolean bRet = true;
		String code = coinPair.getCoin_pair_name();
		List<SpotPendingDelegationCache> plistCache = pendingDelegationCacheService.getAllByUserId(0, code, userId);
		for (SpotPendingDelegationCache pendingDelegationCache : plistCache) {
			String lock = pendingDelegationCacheService.lock(pendingDelegationCache.getCoin_pair_name(), pendingDelegationCache.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, false, coinPair);
				} catch (Exception e) {
					bRet = false;
					e.printStackTrace();
				} finally {
					if (pendingDelegationCache != null) {
						pendingDelegationCacheService.unLock(pendingDelegationCache.getCoin_pair_name(), pendingDelegationCache.getDelegation_id(), lock);
					}
				}
			}
		}
		
		List<SpotPendingDelegation> plist = pendingDelegationService.getAllByUserId(0, code, userId);
		for (SpotPendingDelegation pendingDelegation : plist) {
			String lock = pendingDelegationService.lock(pendingDelegation.getCoin_pair_name(), pendingDelegation.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationService.cancelPending_one(pendingDelegation, false, coinPair);
				} catch (Exception e) {
					bRet = false;
					e.printStackTrace();
				} finally {
					if (pendingDelegation != null) {
						pendingDelegationService.unLock(pendingDelegation.getCoin_pair_name(), pendingDelegation.getDelegation_id(), lock);
					}
				}
			}
		}
		
		return bRet;
	}
	
	private void addPending(long userId, Map<BigDecimal, BigDecimal> orders, int direction, BQSpotMarketCoinPair bqTransaction) {
		
		int remainCoinId = 0;
		
		if (direction == Contents.DIRECTION_BUY) {
			remainCoinId = bqTransaction.getMarket_coin_id();
		} else {
			remainCoinId = bqTransaction.getCoin_id();
		}
		
		BQAccount bqAccount = bqAccountService.getAccount(userId, remainCoinId);
		if (bqAccount == null) return;
		
		for (BigDecimal price : orders.keySet()) {
			BigDecimal amount = orders.get(price);
			BigDecimal frozen = amount;
			if (direction == Contents.DIRECTION_BUY) {
				frozen = amount.multiply(price);
			}
			SpotPendingDelegationCache cahce = new SpotPendingDelegationCache(bqTransaction.getId(), bqTransaction.getCoin_pair_name(),
					direction, amount, price, BigDecimal.ZERO, amount,
					BigDecimal.ZERO, userId, BigDecimal.ZERO);
	
			try {
				pendingDelegationCacheService.insertCacheAndChangeAccount(cahce, frozen, bqAccount, remainCoinId, false, Contents.DEVICE_SERVER_DEPTH, "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void removePending(long userId, Map<BigDecimal, BigDecimal> orders, int direction, BQSpotMarketCoinPair bqTransaction) {
		List<BigDecimal> removed = new ArrayList<BigDecimal>();
		for (BigDecimal price : orders.keySet()) {
			boolean bRet = true;
			List<SpotPendingDelegationCache> plistCache = pendingDelegationCacheService.findPendingAtPriceByUser(userId, bqTransaction.getCoin_pair_name(), direction, price);
			for (SpotPendingDelegationCache pending : plistCache) {
				String lock = pendingDelegationCacheService.lock(pending.getCoin_pair_name(), pending.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						pendingDelegationCacheService.cancelPendingCache_one(pending, false, bqTransaction);
					} catch (Exception e) {
						bRet = false;
						e.printStackTrace();
					} finally {
						if (pending != null) {
							pendingDelegationCacheService.unLock(pending.getCoin_pair_name(), pending.getDelegation_id(), lock);
						}
					}
				}
			}
			
			List<SpotPendingDelegation> plistpd = pendingDelegationService.findPendingAtPriceByUser(userId, bqTransaction.getCoin_pair_name(), direction, price);
			for (SpotPendingDelegation pending : plistpd) {
				String lock = pendingDelegationService.lock(pending.getCoin_pair_name(), pending.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						pendingDelegationService.cancelPending_one(pending, false, bqTransaction);
					} catch (Exception e) {
						bRet = false;
						e.printStackTrace();
					} finally {
						if (pending != null) {
							pendingDelegationService.unLock(pending.getCoin_pair_name(), pending.getDelegation_id(), lock);
						}
					}
				}
			}
			
			if (bRet) {
				removed.add(price);
			}
		}
		
		for (BigDecimal price : removed) {
			orders.remove(price);
		}
	}
	
	private void removePending(long uid, BQSpotMarketCoinPair coinPair, BigDecimal buy1, BigDecimal sell1) {
		String code = coinPair.getCoin_pair_name();
		List<SpotPendingDelegation> buyPendings = pendingDelegationService.findPendingBetweenPriceByUserId(uid, code, Contents.DIRECTION_BUY, buy1, new BigDecimal(10000000));
		for (SpotPendingDelegation pending : buyPendings) {
			String lock = pendingDelegationService.lock(pending.getCoin_pair_name(), pending.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationService.cancelPending_one(pending, false, coinPair);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pending != null) {
						pendingDelegationService.unLock(pending.getCoin_pair_name(), pending.getDelegation_id(), lock);
					}
				}
			}
		}
		
		List<SpotPendingDelegation> sellPendings = pendingDelegationService.findPendingBetweenPriceByUserId(uid, code, Contents.DIRECTION_SELL, BigDecimal.ZERO, sell1);
		for (SpotPendingDelegation pending : sellPendings) {
			String lock = pendingDelegationService.lock(pending.getCoin_pair_name(), pending.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationService.cancelPending_one(pending, false, coinPair);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pending != null) {
						pendingDelegationService.unLock(pending.getCoin_pair_name(), pending.getDelegation_id(), lock);
					}
				}
			}
		}
	}
}
