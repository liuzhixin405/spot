package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.service.NPendingDelegationCacheService;
import com.btc100.btc_q.service.NPendingDelegationService;

/**
 * 交易服务
 * 
 * @author Administrator
 * 
 */
public class NOpenPositionTimerTask  extends NOpenPositionTask {

	@Autowired
	private NTransactionService transactionService;
	@Autowired
	private NPendingDelegationService pendingDelegationService;
	@Autowired
	private NPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private NNotradetimeService bqNotradetimeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	public static String[] contractArray = null;

	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.ncontracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		for (String pContract : contractArray) {
			String[] strs = pContract.split("_");
			if (strs.length != 2) continue;
			String partition = strs[0];
			String contract = strs[1];
			
			if (StringUtil.isEmpty(partition) || StringUtil.isEmpty(contract)) {
				continue;
			}

			NTransaction bqTransaction = transactionService.findTransByCode(partition, contract);
			if (bqTransaction == null) continue;
			
			if (bqTransaction.getSuspend()) {
				continue;
			}
			
			if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
				if (!bqNotradetimeService.isTradeTime(partition, bqTransaction.getTransaction_id())) {
					continue;
				}
			}

			String code = bqTransaction.getTransaction_code();
			
			NPendingDelegationCache buycache1 = pendingDelegationCacheService.getBuy1(partition, code);
			NPendingDelegation sell1 = pendingDelegationService.getSell1(partition, code);
			
			boolean ret = dealBuyCacheAndSell(partition, bqTransaction.getCurrency_type_id(), code, buycache1, sell1, bqTransaction.getTrade_coefficient(), false);
			if (buycache1 != null && sell1 != null && ret) {
				
				BigDecimal last_price = spyMemcachedClient.get(partition + "_" + code + BTCQMemecacheKeys.BTCLATESTPRICE);
				BigDecimal lastPrice = sell1.getDelegation_price();
				if (last_price == null || last_price.compareTo(lastPrice) != 0) {
					spyMemcachedClient.set(partition + "_" + code + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
					last_price = lastPrice;
				}
			}

			/*******************************************************************************************************/

			NPendingDelegationCache sellcache1 = pendingDelegationCacheService.getSell1(partition, code);
			NPendingDelegation buy1 = pendingDelegationService.getBuy1(partition, code);
			
			ret = dealBuyAndSellCache(partition, bqTransaction.getCurrency_type_id(), code, buy1, sellcache1, bqTransaction.getTrade_coefficient(), false);
			if (buy1 != null && sellcache1 != null && ret) {
				
				BigDecimal last_price = spyMemcachedClient.get(partition + "_" + code + BTCQMemecacheKeys.BTCLATESTPRICE);
				BigDecimal lastPrice = buy1.getDelegation_price();
				if (last_price == null || last_price.compareTo(lastPrice) != 0) {
					spyMemcachedClient.set(partition + "_" + code + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
					last_price = lastPrice;
				}
			}
		}
	}
}
