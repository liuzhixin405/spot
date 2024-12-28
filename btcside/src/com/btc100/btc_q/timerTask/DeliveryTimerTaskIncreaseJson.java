package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.DeliveryStockSetting;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.DeliveryNotradetimeService;
import com.btc100.btc_q.service.DeliveryStockSettingService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.util.TimeUtil;

/**
 * 合约 API（市场增仓）
 * 
 * @author Administrator
 * 
 */
public class DeliveryTimerTaskIncreaseJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private DeliveryTradeLogService tradeLogService;
	@Autowired
	private DeliveryNotradetimeService bqNoTradeService;
	@Autowired
	private DeliveryStockSettingService deliveryStockSettingService;
	
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
		
		long currentTimeMillis = (System.currentTimeMillis() + 10) / 1000;
		// 获取当前时间所在的1分钟区间内的起始时间点（0:01:00、0:02:00、0:03:00、0:04:00、0:05:00...）
		long typeTime = 60L;
		long oneMinAgoMillis = currentTimeMillis / typeTime * typeTime;
		
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
			
			String code = transaction.getTransaction_code();
			BigDecimal closingPrice = spyMemcachedClient.get(BTCQMemecacheKeys.D_COLSING_PRICE + code);
			if (closingPrice == null && transaction.getTransaction_kind() == 1) {
				DeliveryStockSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.D_STOCK_SETTING + "_" + transaction.getTransaction_id());
				if (setting == null) {
					setting = deliveryStockSettingService.getByTransId(transaction.getTransaction_id());
				}
				if (setting != null && setting.getSat_state() != null) {
					// 周六有交易：欧美股以及A50
					if (setting.getSat_state()) {
						if (!bqNoTradeService.isTradeTime(transaction.getTransaction_id())) {
							BigDecimal last = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_BTCLATESTPRICE);
							if (last == null) {
								// 从数据库中算出最新成交价 
								last = transaction.getLatest_transaction_price();
							}
							closingPrice = last;
							spyMemcachedClient.set(BTCQMemecacheKeys.D_COLSING_PRICE + code, BTCQMemecacheKeys.TIMES.ONE_DAY, closingPrice);
						}
					}
				}
			}
			
			BigDecimal first = BigDecimal.ZERO;
			if (transaction.getTransaction_kind() == 1 && !bqNoTradeService.isTradeTime(transaction.getTransaction_id())){
				BigDecimal oldPrice = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.D_FIRST_PRICE);
				if (oldPrice != null) {
					first = oldPrice;
				}
				spyMemcachedClient.set(transaction.getTransaction_code() + BTCQMemecacheKeys.D_FIRST_PRICE, BTCQMemecacheKeys.TIMES.TICKER_BTC, first);
			} else {
				// 股指(欧美及A50)将昨日收盘价放入first price
				if (closingPrice != null && transaction.getTransaction_kind() == 1) {
					first = closingPrice;
				} else {
					if (transaction.getTransaction_kind() == 1) {
						first = tradeLogService.findPriceLastClose(transaction.getTransaction_code(), TimeUtil.getTimesmorning());
						if (first == null) {
							first = tradeLogService.findFirstPrice(transaction.getTransaction_code(), TimeUtil.getTimesmorning());
						}
					} else {
						first = tradeLogService.findFirstPrice(transaction.getTransaction_code(), TimeUtil.getTimesmorning());
					}
				}
				if (first == null) first = BigDecimal.ZERO;
				spyMemcachedClient.set(transaction.getTransaction_code() + BTCQMemecacheKeys.D_FIRST_PRICE, BTCQMemecacheKeys.TIMES.TICKER_BTC, first);
			}
			
			Long lastTime = spyMemcachedClient.get("d_trade_amount_" + code);
			BigDecimal amount = BigDecimal.ZERO;
			long startTime = lastTime == null ? 0 : lastTime;
			amount = tradeLogService.getTradeAmount(code, startTime, oneMinAgoMillis, 0, 0);
			
			spyMemcachedClient.set("d_trade_amount_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, oneMinAgoMillis);
			
			BigDecimal allAmount = spyMemcachedClient.get(BTCQMemecacheKeys.D_ALL_TRADE_AMOUNT);
			if (allAmount == null) {
				allAmount = BigDecimal.ZERO;
			}
			
			allAmount = allAmount.add(amount).setScale(0, BigDecimal.ROUND_HALF_UP);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_ALL_TRADE_AMOUNT, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, allAmount);
		}
	}
}
