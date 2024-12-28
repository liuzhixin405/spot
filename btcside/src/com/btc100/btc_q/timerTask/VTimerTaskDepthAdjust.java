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
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VArbitrageSetting;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.service.VAccountContractService;
import com.btc100.btc_q.service.VArbitrageSettingService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.service.VPendingDelegationCacheService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

public class VTimerTaskDepthAdjust {
	
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VTransactionTypeService vTransTypeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private VPendingDelegationCacheService vPendingDelegationCacheService;
	@Autowired
	private VAccountContractService vAccountContractService;
	@Autowired
	private VArbitrageSettingService vArbitrageSettingService;
	
	public void run() throws JSONException {
		
		List<VTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if(list == null){
			list = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list);
		}
		
		for(VTransaction trans :list) {
			if (trans.getSuspend()) continue;
			
			BigDecimal indexPrice = spyMemcachedClient.get(trans.getTransaction_type_code());
			if (indexPrice == null || indexPrice.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			
			VArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.V_ARBITRAGE_SETTING + trans.getTransaction_code());
			if (setting == null) {
				setting = vArbitrageSettingService.getArbitrageSettingByCode(trans.getTransaction_code());
				if (setting == null) {
					setting = new VArbitrageSetting();
					setting.setRunning(false);
					setting.setScalping(false);
					setting.setHedging(false);
					setting.setRisk_manage(false);
					setting.setDepth_uid(0L);
					setting.setAuto_trade(false);
				}
				
				spyMemcachedClient.set(BTCQMemecacheKeys.V_ARBITRAGE_SETTING + trans.getTransaction_code(), BTCQMemecacheKeys.TIMES.ONE_SECOND, setting);
			}
			
			if (!setting.getRunning() || setting.getDepth_size() <= 0) continue;

			// 移除cache
			removeAllPendingCache(setting.getDepth_uid(), trans.getTransaction_code(), trans.getCurrency_type_id());
			
			Integer digits = setting.getPrice_digits();
			if (digits < 0) {
				digits = spyMemcachedClient.get(trans.getTransaction_type_code() + BTCQMemecacheKeys.V_DIGITS);
				if (digits == null) {
					List<VTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANS_TYPES);
					if (transTypes == null) {
						transTypes = vTransTypeService.findAllUsing();
						spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
					}
					
					VTransactionType transType = null;
					for (VTransactionType type : transTypes) {
						if (type.getType_code().equals(trans.getTransaction_type_code())) {
							transType = type;
						}
					}
					
					if (transType == null) continue;
					
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(transType.getType_code() + BTCQMemecacheKeys.V_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
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
	
	private void addPending(long userId, Map<BigDecimal, BigDecimal> orders, int direction, VTransaction bqTransaction) {
		BQMemberLevel userlevel = bqMemberLevelService.findByUserId(userId);
		VAccountContract bqContract = vAccountContractService.findByUidCode(userId, bqTransaction.getTransaction_code());
		if (bqContract == null) return;
		
		for (BigDecimal price : orders.keySet()) {
			BigDecimal amount = orders.get(price);
			BigDecimal deposit = BTCQMath.getDeposit(amount, price, 20, BigDecimal.ONE);
			BigDecimal fee = BTCQMath.getFee(amount, price, userlevel.getEat_commission_rate(), BigDecimal.ONE);
			VPendingDelegationCache cahce = new VPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					direction, amount, price, BigDecimal.ZERO, amount, 20, deposit,
					userlevel.getEat_commission_rate(), Contents.PRIORITY_KC, userId, BigDecimal.ZERO, BigDecimal.ZERO);
	
			try {
				vPendingDelegationCacheService.insertCacheAndChangeAccount(cahce, bqTransaction.getCurrency_type_id(), fee, bqContract, false, Contents.DEVICE_SERVER_SLW, "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		BigDecimal changeAmounts = BigDecimal.ZERO;
		List<VPendingDelegationCache> caches = new ArrayList<>();
		for (BigDecimal price : orders.keySet()) {
			BigDecimal amount = orders.get(price);
			BigDecimal deposit = BTCQMath.getDeposit(amount, price, 20, BigDecimal.ONE);
			BigDecimal fee = BigDecimal.ZERO;
			VPendingDelegationCache cahce = new VPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					direction, amount, price, BigDecimal.ZERO, amount, 20, deposit,
					BigDecimal.ZERO, Contents.PRIORITY_KC, userId, BigDecimal.ZERO, BigDecimal.ZERO);
	
			caches.add(cahce);
			
			BigDecimal changeAmount = deposit.add(fee);
			changeAmounts = changeAmounts.add(changeAmount);
		}
		
		try {
			vPendingDelegationCacheService.insertCacheForDepth(caches, userId, changeAmounts, bqTransaction.getTransaction_code());
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	private boolean removeAllPendingCache(long userId, String code, int currencyId) {
		// 注：只有开仓单，不用锁持单
		boolean bRet = true;
		List<VPendingDelegationCache> plistCache = vPendingDelegationCacheService.getAllByUserId(0, code, userId);
		for (VPendingDelegationCache pendingDelegationCache : plistCache) {
			String lock = vPendingDelegationCacheService.lock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					vPendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, currencyId, false);
				} catch (Exception e) {
					bRet = false;
					e.printStackTrace();
				} finally {
					if (pendingDelegationCache != null) {
						vPendingDelegationCacheService.unLock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id(), lock);
					}
				}
			}
		}
		
		return bRet;
	}
}
