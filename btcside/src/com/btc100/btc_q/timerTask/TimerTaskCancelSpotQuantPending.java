package com.btc100.btc_q.timerTask;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.base.BaseTradeAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQSpotQuantSetting;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQSpotQuantSettingService;
import com.btc100.btc_q.service.SpotPendingDelegationCacheService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.util.StringUtil;

public class TimerTaskCancelSpotQuantPending extends BaseTradeAction {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotQuantSettingService bqQuantSettingService;
	@Autowired
	private SpotPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private SpotPendingDelegationService pendingDelegationService;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	
	private static String[] marketArray = null;
	
	Logger logger = Logger.getLogger(TimerTaskCancelSpotQuantPending.class);
	
	private void doCancel(long uid, BQSpotMarketCoinPair coinPair, long endTime, int start) {
		String contract = coinPair.getCoin_pair_name();
		List<SpotPendingDelegationCache> plistCache = pendingDelegationCacheService.findPendingByUserIdAndTime(contract, uid, 0, endTime, 0, false);
		for (SpotPendingDelegationCache pendingDelegationCache : plistCache) {
			String lock = pendingDelegationCacheService.lock(pendingDelegationCache.getCoin_pair_name(), pendingDelegationCache.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, false, coinPair);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pendingDelegationCache != null) {
						pendingDelegationCacheService.unLock(pendingDelegationCache.getCoin_pair_name(), pendingDelegationCache.getDelegation_id(), lock);
					}
				}
			}
		}
		
		List<SpotPendingDelegation> plist = pendingDelegationService.findPendingByUserIdAndTime(contract, uid, 0, 0, start, 300, true);
		for (SpotPendingDelegation pendingDelegation : plist) {
			String lock = pendingDelegationService.lock(pendingDelegation.getCoin_pair_name(), pendingDelegation.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationService.cancelPending_one(pendingDelegation, false, coinPair);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pendingDelegation != null) {
						pendingDelegationService.unLock(pendingDelegation.getCoin_pair_name(), pendingDelegation.getDelegation_id(), lock);
					}
				}
			}
		}
		
	}
	
	public void run() {
		if (marketArray == null) {
			String contracts = UrlConfig.getValue("spotTrade.markets");
			if (!StringUtil.isEmpty(contracts)) {
				marketArray = contracts.split(",");
			}
		}
		
		if (marketArray == null || marketArray.length == 0) return;
		
		BQSpotQuantSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_QUANT_SETTING);
		if (setting == null) {
			setting = bqQuantSettingService.get();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_QUANT_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
		}
		
		if (setting == null) {
			return;
		}
		
		if (setting.getIs_auto_cancel()) {
		
			String ids = setting.getUids();
			if (StringUtil.isEmpty(ids)) return;
			
			int timeout = setting.getCancel_timeout();
			if (timeout <= 0) return;
			
			long currTime = pendingDelegationCacheService.getUnix_TIMESTAMP();
			long endTime = currTime - timeout;
			
			String[] users = ids.split(",");
			
			List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
			if (coinPairs == null) {
				coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
				spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
			}
		
			int start = 40;
			if (setting.getKeep_nums() != null && setting.getKeep_nums() > 0) {
				start = setting.getKeep_nums();
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
				
				for (String userId : users) {
					Long uid = Convert.toLong(userId, 0);
					if (uid <= 0) continue;
					
					doCancel(uid, coinPair, endTime, start);
				}
			}
		}
		
	}

}
