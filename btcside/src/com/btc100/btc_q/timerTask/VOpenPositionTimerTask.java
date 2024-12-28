package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VPendingDelegation;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VPendingDelegationCacheService;
import com.btc100.btc_q.service.VPendingDelegationService;

/**
 * 交易服务
 * 
 * @author Administrator
 * 
 */
public class VOpenPositionTimerTask extends VOpenPositionTask {
	
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	private VPendingDelegationCacheService vPendingDelegationCacheService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	public void run() {
		
		List<VTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, lstTrads);
		}
		
		for (VTransaction bqTransaction : lstTrads) {
			if (bqTransaction.getSuspend()) {
				continue;
			}

			String code = bqTransaction.getTransaction_code();
			
			VPendingDelegationCache buycache1 = vPendingDelegationCacheService.getBuy1(code);
			VPendingDelegation sell1 = vPendingDelegationService.getSell1(code);
			
			boolean ret = dealBuyCacheAndSell(code, bqTransaction.getCurrency_type_id(), buycache1, sell1, false);
			if (buycache1 != null && sell1 != null && ret) {
				// 更新最新成交价
				BigDecimal lastPrice = sell1.getDelegation_price();
				VTransaction bqt_temp = vTransactionService.findTransToUpdateByCode(code);
				BigDecimal highPrice = bqt_temp.getTransaction_highest();
				bqt_temp.setLatest_transaction_price(lastPrice);
				if (lastPrice.compareTo(highPrice) > 0) {
					bqt_temp.setTransaction_highest(lastPrice);
				}
				BigDecimal lowPrice = bqt_temp.getTransaction_lowest();
				if (lastPrice.compareTo(lowPrice) < 0 || lowPrice.signum() <= 0) {
					bqt_temp.setTransaction_lowest(lastPrice);
				}
				vTransactionService.update(bqt_temp);
				spyMemcachedClient.set(code + BTCQMemecacheKeys.V_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
			}

			/*******************************************************************************************************/

			VPendingDelegationCache sellcache1 = vPendingDelegationCacheService.getSell1(code);
			VPendingDelegation buy1 = vPendingDelegationService.getBuy1(code);
			
			ret = dealBuyAndSellCache(code, bqTransaction.getCurrency_type_id(), buy1, sellcache1, false);
			if (buy1 != null && sellcache1 != null && ret) {
				// 更新最新成交价
				BigDecimal lastPrice = buy1.getDelegation_price();
				VTransaction bqt_temp = vTransactionService.findTransToUpdateByCode(code);
				BigDecimal highPrice = bqt_temp.getTransaction_highest();
				bqt_temp.setLatest_transaction_price(lastPrice);
				if (lastPrice.compareTo(highPrice) > 0) {
					bqt_temp.setTransaction_highest(lastPrice);
				}
				BigDecimal lowPrice = bqt_temp.getTransaction_lowest();
				if (lastPrice.compareTo(lowPrice) < 0 || lowPrice.signum() <= 0) {
					bqt_temp.setTransaction_lowest(lastPrice);
				}
				vTransactionService.update(bqt_temp);
				spyMemcachedClient.set(code + BTCQMemecacheKeys.V_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
			}
		}
	}
}
