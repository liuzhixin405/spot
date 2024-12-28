package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.DeliveryLastTicker;
import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.service.DeliveryLastTickerService;
import com.btc100.btc_q.service.DeliveryNotradetimeService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 合约 API（市场行情）
 * 
 * @author Administrator
 * 
 */
public class DeliveryTimerTaskTickerJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private DeliveryTradeLogService tradeLogService;
	@Autowired
	private DeliveryPendingDelegationService pendingDelegationService;
	@Autowired
	private DeliveryLastTickerService bqLastTickerService;
	@Autowired
	private DeliveryTransactionTypeService transTypeService;
	@Autowired
	private DeliveryNotradetimeService bqNoTradeService;
	
	public static String[] contractArray = null;

	public void run() {

		if (contractArray == null) {
			String contracts = UrlConfig.getValue("deliveryTrade.contracts");
			contractArray = contracts.split(",");
		}

		if (contractArray == null || contractArray.length == 0)
			return;
		
		JSONArray array = new JSONArray();

		// 获取当前时间的24小时时间（时间戳）
		long yesterdayTime = TimeUtil.getYesterdayTime();
		
		// 找出在用的合约
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
			
			String bqCode = transaction.getTransaction_code(); // 获取合约号
			if (transaction.getTransaction_kind() == 1 && !bqNoTradeService.isTradeTime(transaction.getTransaction_id())){
				String old = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_BTC + "_" + bqCode);
				if (!StringUtil.isEmpty(old)) {
					spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_BTC + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_BTC, old);
					continue;
				}
			}
			
			BigDecimal high = BigDecimal.ZERO;
			BigDecimal low = BigDecimal.ZERO;
			BigDecimal buy = BigDecimal.ZERO;
			BigDecimal sell = BigDecimal.ZERO;
			BigDecimal last = BigDecimal.ZERO;
			BigDecimal vol = BigDecimal.ZERO;
			
			Integer digits = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.D_DIGITS);
			if (digits == null) {
				DeliveryTransactionType DeliveryTransactionType = transTypeService.getTransactionType(transaction.getTransaction_type());
				digits = Utils.getDigitsFromMinFloat(DeliveryTransactionType.getMin_float_price());
				spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.D_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
			}

			// 1.先从缓存中查找 最新成交价
			// 从数据库中算出最高价 btc1407_trade_log
			high = tradeLogService.findHighPrice(bqCode, yesterdayTime);
			if (high == null) high = BigDecimal.ZERO;

			// 从数据库中算出最低价 btc1407_trade_log
			low = tradeLogService.findLowPrice(bqCode, yesterdayTime);
			if (low == null) low = BigDecimal.ZERO;
			
			String strLast = bqCode + BTCQMemecacheKeys.D_BTCLATESTPRICE;
			last = spyMemcachedClient.get(strLast);
			if (last == null) {
				// 从数据库中算出最新成交价 
				last = transactionService.findTransByCode(bqCode).getLatest_transaction_price();
			}
			
			if (last == null) last = BigDecimal.ZERO;

			DeliveryPendingDelegation pendingBuy = pendingDelegationService.getBuy1(bqCode);
			DeliveryPendingDelegation pendingSell = pendingDelegationService.getSell1(bqCode);

			if (pendingBuy == null || pendingSell == null) {
				buy = new BigDecimal(0);
				sell = new BigDecimal(0);
			} else {
				// 从数据库中算出买一价
				buy = pendingBuy.getDelegation_price();
				// 从数据库中算出卖一价
				sell = pendingSell.getDelegation_price();
			}

			// 从数据库中算出成交量
			vol = tradeLogService.findBTCVol(bqCode, yesterdayTime);
			vol = vol == null ? new BigDecimal(0) : vol;

			BigDecimal trades = tradeLogService.findTradeBTC(bqCode, yesterdayTime);
			trades = (trades == null ? BigDecimal.ZERO : trades);
			
			high = high.setScale(digits, BigDecimal.ROUND_DOWN);
			low = low.setScale(digits, BigDecimal.ROUND_DOWN);
			buy = buy.setScale(digits, BigDecimal.ROUND_DOWN);
			sell = sell.setScale(digits, BigDecimal.ROUND_DOWN);
			last = last.setScale(digits, BigDecimal.ROUND_DOWN);
			vol = vol.setScale(digits, BigDecimal.ROUND_DOWN);
			trades = trades.setScale(digits, BigDecimal.ROUND_DOWN);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("bqCode", bqCode);
			map.put("high", high);
			map.put("low", low);
			map.put("buy", buy);
			map.put("sell", sell);
			map.put("last", last);
			map.put("vol", vol);
			map.put("trades", trades);
			array.put(map);
			map.clear();

			JSONObject obj1 = new JSONObject();
			try {
				obj1.put("bqCode", bqCode);
				obj1.put("high", high);
				obj1.put("low", low);
				obj1.put("buy", buy);
				obj1.put("sell", sell);
				obj1.put("last", last);
				obj1.put("vol", vol);
				obj1.put("trades", trades);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			DeliveryTransaction bqt = transactionService.findTransToUpdateByCode(bqCode);
			if (high.compareTo(bqt.getTransaction_highest()) != 0 || low.compareTo(bqt.getTransaction_lowest()) != 0) {
				transactionService.updateTrans(bqt.getTransaction_id(), high, low);
			}
			
			bqLastTickerService.insertOrUploadLastTicker(new DeliveryLastTicker(0, bqCode, high, low, buy, sell, last, vol, trades));
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_BTC + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_BTC, obj1.toString());
		}

	}
}
