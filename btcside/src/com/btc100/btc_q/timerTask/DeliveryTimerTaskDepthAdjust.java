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
import com.btc100.btc_q.entity.DeliveryArbitrageSetting;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.DeliveryArbitrageSettingService;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

public class DeliveryTimerTaskDepthAdjust {
	
	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryTransactionTypeService transTypeService;
	@Autowired
	private DeliveryPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private DeliveryArbitrageSettingService arbitrageSettingService;
	
	public static String[] contractArray = null;
	public static Map<String, Integer> speedMap = new HashMap<>();

	public void run() throws JSONException {

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
		
		for (String contract : contractArray) {
			String[] codeDelivery = contract.split("_");
			if (codeDelivery.length == 0) {
				continue;
			}
			String typeCode = codeDelivery[0];
			String deliveryType = codeDelivery[1];
			
			DeliveryTransaction trans = null;
			for (DeliveryTransaction bqTransaction : lstTrads) {
				if (bqTransaction.getTransaction_type_code().equals(typeCode) && bqTransaction.getDelivery_type().equals(deliveryType)) {
					trans = bqTransaction;
					break;
				}
			}

			if (trans == null) {
				continue;
			}
			
			if (trans.getSuspend() || trans.getTransaction_state() != 1) {
				continue;
			}
			
			BigDecimal indexPrice = spyMemcachedClient.get(trans.getTransaction_type_code());
			if (indexPrice == null || indexPrice.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			
			DeliveryArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.D_ARBITRAGE_SETTING + trans.getTransaction_code());
			if (setting == null) {
				setting = arbitrageSettingService.getArbitrageSettingByCode(trans.getTransaction_code());
				if (setting == null) {
					setting = new DeliveryArbitrageSetting();
					setting.setRunning(false);
					setting.setScalping(false);
					setting.setHedging(false);
					setting.setRisk_manage(false);
					setting.setAuto_trade(false);
					setting.setDepth_uid(0L);
				}
				
				spyMemcachedClient.set(BTCQMemecacheKeys.D_ARBITRAGE_SETTING + trans.getTransaction_code(), BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
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
					
					List<DeliveryTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANS_TYPES);
					if (transTypes == null) {
						transTypes = transTypeService.findAllUsing();
						spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
					}
					
					DeliveryTransactionType transType = null;
					for (DeliveryTransactionType type : transTypes) {
						if (type.getType_code().equals(trans.getTransaction_type_code())) {
							transType = type;
						}
					}
					
					if (transType == null) continue;
					
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(trans.getTransaction_code() + BTCQMemecacheKeys.D_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
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
	
	private void addPending(long userId, Map<BigDecimal, BigDecimal> orders, int direction, DeliveryTransaction transaction) {
		BigDecimal changeAmounts = BigDecimal.ZERO;
		List<DeliveryPendingDelegationCache> caches = new ArrayList<>();
		for (BigDecimal price : orders.keySet()) {
			BigDecimal amount = orders.get(price);
			BigDecimal deposit = BTCQMath.getDeposit(amount, price, 20, transaction.getTrade_coefficient());
			BigDecimal fee = BigDecimal.ZERO;
			DeliveryPendingDelegationCache cahce = new DeliveryPendingDelegationCache(transaction.getTransaction_id(), transaction.getTransaction_code(),
					direction, amount, price, BigDecimal.ZERO, amount, 20, deposit,
					BigDecimal.ZERO, Contents.PRIORITY_KC, userId, BigDecimal.ZERO, BigDecimal.ZERO);
	
			caches.add(cahce);
			
			BigDecimal changeAmount = deposit.add(fee);
			changeAmounts = changeAmounts.add(changeAmount);
		}
		
		try {
			pendingDelegationCacheService.insertCacheForDepth(caches, userId, changeAmounts, transaction.getTransaction_code());
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean removeAllPendingCache(long userId, String code, BigDecimal coefficient) {
		// 注：只有开仓单，不用锁持单
		boolean bRet = true;
		List<DeliveryPendingDelegationCache> plistCache = pendingDelegationCacheService.getAllByUserId(0, code, userId);
		for (DeliveryPendingDelegationCache cache : plistCache) {
			String lock = pendingDelegationCacheService.lock(cache.getTransaction_code(), cache.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationCacheService.cancelPendingCache_one(cache, coefficient, false);
				} catch (Exception e) {
					bRet = false;
					e.printStackTrace();
				} finally {
					if (cache != null) {
						pendingDelegationCacheService.unLock(cache.getTransaction_code(), cache.getDelegation_id(), lock);
					}
				}
			}
		}
		return bRet;
	}
}
