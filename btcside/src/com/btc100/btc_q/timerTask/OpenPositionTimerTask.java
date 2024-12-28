package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;

/**
 * 交易服务
 * 
 * @author Administrator
 * 
 */
@Component
public class OpenPositionTimerTask  extends OpenPositionTask {

	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private BQNotradetimeService bqNotradetimeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	public static String[] contractArray = null;

	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.contracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		List<BQTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, lstTrads);
		}
		
		for (String contract : contractArray) {
			BQTransaction bqTransaction = null;
			for (BQTransaction transaction : lstTrads) {
				if (transaction.getTransaction_code().equals(contract)) {
					bqTransaction = transaction;
					break;
				}
			}
			
			if (bqTransaction == null) continue;
			
			if (bqTransaction.getSuspend()) {
				continue;
			}
			
			if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
				if (!bqNotradetimeService.isTradeTime(bqTransaction.getTransaction_id())) {
					continue;
				}
			}

			String code = bqTransaction.getTransaction_code();
			
			PendingDelegationCache buycache1 = pendingDelegationCacheService.getBuy1(code);
			PendingDelegation sell1 = pendingDelegationService.getSell1(code);
			
			boolean ret = dealBuyCacheAndSell(code, buycache1, sell1, bqTransaction.getTrade_coefficient(), false);
			if (buycache1 != null && sell1 != null && ret) {
				
				BigDecimal last_price = spyMemcachedClient.get(code + BTCQMemecacheKeys.BTCLATESTPRICE);
				BigDecimal lastPrice = sell1.getDelegation_price();
				if (last_price == null || last_price.compareTo(lastPrice) != 0) {
					spyMemcachedClient.set(code + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
					last_price = lastPrice;
				}
			}

			/*******************************************************************************************************/

			PendingDelegationCache sellcache1 = pendingDelegationCacheService.getSell1(code);
			PendingDelegation buy1 = pendingDelegationService.getBuy1(code);
			
			ret = dealBuyAndSellCache(code, buy1, sellcache1, bqTransaction.getTrade_coefficient(), false);
			if (buy1 != null && sellcache1 != null && ret) {
				
				BigDecimal last_price = spyMemcachedClient.get(code + BTCQMemecacheKeys.BTCLATESTPRICE);
				BigDecimal lastPrice = buy1.getDelegation_price();
				if (last_price == null || last_price.compareTo(lastPrice) != 0) {
					spyMemcachedClient.set(code + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
					last_price = lastPrice;
				}
			}
		}
	}
}
