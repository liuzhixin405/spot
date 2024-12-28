package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQArbitrageSetting;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQArbitrageSettingService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

public class TimerTaskDepthAdjust {
	
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionTypeService transTypeService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private BQArbitrageSettingService bqArbitrageSettingService;
	
	public static String[] contractArray = null;
	public static Map<String, Integer> speedMap = new HashMap<>();
	
	
	public void run() throws JSONException {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.contracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		List<BQTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if(list == null){
			list = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list);
		}
		
		for (String contract : contractArray) {
			BQTransaction trans = null;
			for (BQTransaction bqTransaction : list) {
				if (bqTransaction.getTransaction_code().equals(contract)) {
					trans = bqTransaction;
					break;
				}
			}
			
			if (trans == null) {
				continue;
			}
			
			if (trans.getSuspend()) {
				continue;
			}
			
			String code = trans.getTransaction_code();
			BigDecimal indexPrice = spyMemcachedClient.get(code);
			if (indexPrice == null || indexPrice.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			
			BQArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + trans.getTransaction_code());
			if (setting == null) {
				setting = bqArbitrageSettingService.getArbitrageSettingByCode(trans.getTransaction_code());
				if (setting == null) {
					setting = new BQArbitrageSetting();
					setting.setRunning(false);
					setting.setScalping(false);
					setting.setHedging(false);
					setting.setRisk_manage(false);
					setting.setAuto_trade(false);
					setting.setDepth_uid(0L);
				}
				
				spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + trans.getTransaction_code(), BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
			
			if (!setting.getRunning() || setting.getDepth_size() <= 0) continue;
			
			Integer number = 0;
			if (speedMap.containsKey(contract)) {
				number = speedMap.get(contract);
			}
			if (number % setting.getSpeed_type() != 0) {
				number++;
				speedMap.put(contract, number);
				continue;
			}
			
			number = 1;
			speedMap.put(contract, number); 

			// 移除cache
			removeAllPendingCache(setting.getDepth_uid(), trans.getTransaction_code(), trans.getTrade_coefficient());
			
			Integer digits = setting.getPrice_digits();
			if (digits < 0) {
				digits = spyMemcachedClient.get(trans.getTransaction_type_code() + BTCQMemecacheKeys.DIGITS);
				if (digits == null) {
					List<BQTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES);
					if (transTypes == null) {
						transTypes = transTypeService.findAllUsing();
						spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
					}
					
					BQTransactionType transType = null;
					for (BQTransactionType type : transTypes) {
						if (type.getType_code().equals(trans.getTransaction_type_code())) {
							transType = type;
						}
					}
					
					if (transType == null) continue;
					
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(transType.getType_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
			}
			
			int amountDigits = setting.getAmount_digits();
			if (amountDigits < 0) {
				amountDigits = Utils.getAmountDigtis(trans.getTransaction_code());
			}

			if (setting.getDepth_index_range().compareTo(BigDecimal.ZERO) != 0) {
				indexPrice = indexPrice.multiply(setting.getDepth_index_range().add(BigDecimal.ONE));
			}
			
			Map<BigDecimal, BigDecimal> depthSells = new HashMap<>();
			Map<BigDecimal, BigDecimal> depthBuys = new HashMap<>();
			
			
			if (setting.getDepth_buy_range().compareTo(BigDecimal.ONE) > 0 || setting.getDepth_sell_range().compareTo(BigDecimal.ONE) > 0) continue;
			
			BigDecimal buy1 = indexPrice.multiply(BigDecimal.ONE.subtract(setting.getDepth_buy_range())).setScale(digits, BigDecimal.ROUND_DOWN);
			BigDecimal sell1 = indexPrice.multiply(BigDecimal.ONE.add(setting.getDepth_sell_range())).setScale(digits, BigDecimal.ROUND_UP);
			
			BigDecimal random = new BigDecimal(Math.random());
			BigDecimal buyAmount = setting.getDepth_amount_min().add(random.multiply(setting.getDepth_amount_max().subtract(setting.getDepth_amount_min()))).setScale(amountDigits, BigDecimal.ROUND_HALF_UP);
			
			random = new BigDecimal(Math.random());
			BigDecimal sellAmount = setting.getDepth_amount_min().add(random.multiply(setting.getDepth_amount_max().subtract(setting.getDepth_amount_min()))).setScale(amountDigits, BigDecimal.ROUND_HALF_UP);
			
			depthSells.put(sell1, sellAmount);
			depthBuys.put(buy1, buyAmount);
			for (int i = 1; i < setting.getDepth_size(); i++) {
				BigDecimal gap = setting.getDepth_price_gap().multiply(new BigDecimal(i));
				BigDecimal sell = sell1.add(gap).setScale(digits, BigDecimal.ROUND_UP);
				BigDecimal buy = buy1.subtract(gap).setScale(digits, BigDecimal.ROUND_DOWN);
				
				random = new BigDecimal(Math.random());
				sellAmount = sellAmount.add(random.multiply(setting.getDepth_amount_gap_max().subtract(setting.getDepth_amount_gap_min())).add(setting.getDepth_amount_gap_min())).setScale(amountDigits, BigDecimal.ROUND_HALF_UP);
				
				random = new BigDecimal(Math.random());
				buyAmount = buyAmount.add(random.multiply(setting.getDepth_amount_gap_max().subtract(setting.getDepth_amount_gap_min())).add(setting.getDepth_amount_gap_min())).setScale(amountDigits, BigDecimal.ROUND_HALF_UP);

				depthSells.put(sell, sellAmount);
				depthBuys.put(buy, buyAmount);
			}
			
			addPending(setting.getDepth_uid(), depthSells, Contents.DIRECTION_SELL, trans);
			addPending(setting.getDepth_uid(), depthBuys, Contents.DIRECTION_BUY, trans);
		}
	}
	
	private void addPending(long userId, Map<BigDecimal, BigDecimal> orders, int direction, BQTransaction bqTransaction) {
		BigDecimal changeAmounts = BigDecimal.ZERO;
		List<PendingDelegationCache> caches = new ArrayList<>();
		for (BigDecimal price : orders.keySet()) {
			BigDecimal amount = orders.get(price);
			BigDecimal deposit = BTCQMath.getDeposit(amount, price, 20, bqTransaction.getTrade_coefficient());
			BigDecimal fee = BigDecimal.ZERO;
			PendingDelegationCache cahce = new PendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					direction, amount, price, BigDecimal.ZERO, amount, 20, deposit,
					BigDecimal.ZERO, Contents.PRIORITY_KC, userId, BigDecimal.ZERO, BigDecimal.ZERO);
	
			caches.add(cahce);
			
			BigDecimal changeAmount = deposit.add(fee);
			changeAmounts = changeAmounts.add(changeAmount);
		}
		
		try {
			pendingDelegationCacheService.insertCacheForDepth(caches, userId, changeAmounts, bqTransaction.getTransaction_code());
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	private boolean removeAllPendingCache(long userId, String code, BigDecimal coefficient) {
		// 注：只有开仓单，不用锁持单
		boolean bRet = true;
		List<PendingDelegationCache> plistCache = pendingDelegationCacheService.getAllByUserId(0, code, userId);
		for (PendingDelegationCache pendingDelegationCache : plistCache) {
			String lock = pendingDelegationCacheService.lock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, coefficient, false);
				} catch (Exception e) {
					bRet = false;
					e.printStackTrace();
				} finally {
					if (pendingDelegationCache != null) {
						pendingDelegationCacheService.unLock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id(), lock);
					}
				}
			}
		}
		return bRet;
	}
	
}
