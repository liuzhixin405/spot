package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.NStockSetting;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.NStockSettingService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NTradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;

/**
 * 合约 API（市场增仓）
 * 
 * @author Administrator
 * 
 */
public class NTimerTaskIncreaseJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NTransactionService transactionService;
	@Autowired
	private NTradeLogService tradeLogService;
	@Autowired
	private NNotradetimeService bqNoTradeService;
	@Autowired
	private NStockSettingService bqStockSettingService;
	
	private static String[] contractArray = null;

	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.ncontracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		long currentTimeMillis = (System.currentTimeMillis() + 10) / 1000;
		// 获取当前时间所在的1分钟区间内的起始时间点（0:01:00、0:02:00、0:03:00、0:04:00、0:05:00...）
		long typeTime = 60L;
		long oneMinAgoMillis = currentTimeMillis / typeTime * typeTime;
		
		// 遍历在用合约，找出合约号
		for (String pContract : contractArray) {
			String[] strs = pContract.split("_");
			if (strs.length != 2) continue;
			String partition = strs[0];
			String contract = strs[1];
			
			if (StringUtil.isEmpty(partition) || StringUtil.isEmpty(contract)) {
				continue;
			}
			
			NTransaction bq = transactionService.findTransByCode(partition, contract);
			if (bq == null) continue;
			
			BigDecimal closingPrice = spyMemcachedClient.get(BTCQMemecacheKeys.COLSING_PRICE + partition + "_" + contract);
			if (closingPrice == null && bq.getTransaction_kind() == 1) {
				NStockSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.STOCK_SETTING + "_" + partition + "_" + bq.getTransaction_id());
				if (setting == null) {
					setting = bqStockSettingService.getByTransId(partition, bq.getTransaction_id());
				}
				if (setting != null && setting.getSat_state() != null) {
					// 周六有交易：欧美股以及A50
					if (setting.getSat_state()) {
						if (!bqNoTradeService.isTradeTime(partition, bq.getTransaction_id())) {
							BigDecimal last = spyMemcachedClient.get(partition + "_" + contract + BTCQMemecacheKeys.BTCLATESTPRICE);
							if (last == null) {
								// 从数据库中算出最新成交价 
								last = bq.getLatest_transaction_price();
							}
							closingPrice = last;
							spyMemcachedClient.set(BTCQMemecacheKeys.COLSING_PRICE + partition + "_" + contract, BTCQMemecacheKeys.TIMES.ONE_DAY, closingPrice);
						}
					}
				}
			}
			
			BigDecimal first = BigDecimal.ZERO;
			if (bq.getTransaction_kind() == 1 && !bqNoTradeService.isTradeTime(partition, bq.getTransaction_id())){
				BigDecimal oldPrice = spyMemcachedClient.get(partition + "_" + bq.getTransaction_code() + BTCQMemecacheKeys.FIRST_PRICE);
				if (oldPrice != null) {
					first = oldPrice;
				}
				spyMemcachedClient.set(partition + "_" + bq.getTransaction_code() + BTCQMemecacheKeys.FIRST_PRICE, BTCQMemecacheKeys.TIMES.TICKER_BTC, first);
			} else {
				// 股指(欧美及A50)将昨日收盘价放入first price
				if (closingPrice != null && bq.getTransaction_kind() == 1) {
					first = closingPrice;
				} else {
					if (bq.getTransaction_kind() == 1) {
						first = tradeLogService.findPriceLastClose(partition, bq.getTransaction_code(), TimeUtil.getTimesmorning());
						if (first == null) {
							first = tradeLogService.findFirstPrice(partition, bq.getTransaction_code(), TimeUtil.getTimesmorning());
						}
					} else {
						first = tradeLogService.findFirstPrice(partition, bq.getTransaction_code(), TimeUtil.getTimesmorning());
					}
				}
				if (first == null) first = BigDecimal.ZERO;
				spyMemcachedClient.set(partition + "_" + bq.getTransaction_code() + BTCQMemecacheKeys.FIRST_PRICE, BTCQMemecacheKeys.TIMES.TICKER_BTC, first);
			}
			
			Long lastTime = spyMemcachedClient.get("trade_amount_" + partition + "_" + contract);
			BigDecimal amount = BigDecimal.ZERO;
			long startTime = lastTime == null ? 0 : lastTime;
			amount = tradeLogService.getTradeAmount(partition, contract, startTime, oneMinAgoMillis, 0, 0);
			
			spyMemcachedClient.set("trade_amount_" + partition + "_" + contract, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, oneMinAgoMillis);
			
			BigDecimal allAmount = spyMemcachedClient.get(BTCQMemecacheKeys.ALL_TRADE_AMOUNT);
			if (allAmount == null) {
				allAmount = BigDecimal.ZERO;
			}
			
			allAmount = allAmount.add(amount).setScale(0, BigDecimal.ROUND_HALF_UP);
			spyMemcachedClient.set(BTCQMemecacheKeys.ALL_TRADE_AMOUNT, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, allAmount);
		}
	}
}
