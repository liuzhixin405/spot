package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.DeliveryNotradetimeService;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryTransactionService;

/**
 * 交易服务
 * 
 * @author Administrator
 * 
 */
@Component
public class DeliveryOpenPositionTimerTask extends DeliveryOpenPositionTask {

	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private DeliveryPendingDelegationService dPendingDelegationService;
	@Autowired
	private DeliveryPendingDelegationCacheService dPendingDelegationCacheService;
	@Autowired
	private DeliveryNotradetimeService dNotradetimeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	public static String[] contractArray = null;

	public void run() {

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
		
		long currTime = transactionService.time();
		for (String contract : contractArray) {
			String[] codeDelivery = contract.split("_");
			if (codeDelivery.length == 0) {
				continue;
			}
			String typeCode = codeDelivery[0];
			String deliveryType = codeDelivery[1];
			
			DeliveryTransaction transaction = null;
			for (DeliveryTransaction bqTransaction : lstTrads) {
				if (bqTransaction.getTransaction_type_code().equals(typeCode) && bqTransaction.getDelivery_type().equals(deliveryType)) {
					transaction = bqTransaction;
					break;
				}
			}

			if (transaction == null) {
				continue;
			}
			
			if (transaction.getTransaction_state() != 1) {
				continue;
			}
			
			if (transaction.getTransaction_end_time() < currTime) {
				transaction.setSuspend(true);
				transaction.setTransaction_state(Contents.TRANSACTION_STATE_QSZ);
				transactionService.update(transaction);
				continue;
			}
			
			if (transaction.getSuspend()) {
				continue;
			}
			
			if (transaction.getTransaction_kind() != null && transaction.getTransaction_kind() == 1) {
				if (!dNotradetimeService.isTradeTime(transaction.getTransaction_id())) {
					continue;
				}
			}

			String code = transaction.getTransaction_code();
			
			DeliveryPendingDelegationCache buycache1 = dPendingDelegationCacheService.getBuy1(code);
			DeliveryPendingDelegation sell1 = dPendingDelegationService.getSell1(code);
			
			boolean ret = dealBuyCacheAndSell(code, buycache1, sell1, transaction.getTrade_coefficient(), false);
			if (buycache1 != null && sell1 != null && ret) {
				
				BigDecimal last_price = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_BTCLATESTPRICE);
				BigDecimal lastPrice = sell1.getDelegation_price();
				if (last_price == null || last_price.compareTo(lastPrice) != 0) {
					spyMemcachedClient.set(code + BTCQMemecacheKeys.D_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
					last_price = lastPrice;
				}
			}

			/*******************************************************************************************************/

			DeliveryPendingDelegationCache sellcache1 = dPendingDelegationCacheService.getSell1(code);
			DeliveryPendingDelegation buy1 = dPendingDelegationService.getBuy1(code);
			
			ret = dealBuyAndSellCache(code, buy1, sellcache1, transaction.getTrade_coefficient(), false);
			if (buy1 != null && sellcache1 != null && ret) {
				
				BigDecimal last_price = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_BTCLATESTPRICE);
				BigDecimal lastPrice = buy1.getDelegation_price();
				if (last_price == null || last_price.compareTo(lastPrice) != 0) {
					spyMemcachedClient.set(code + BTCQMemecacheKeys.D_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
					last_price = lastPrice;
				}
			}
		}
	}
}
