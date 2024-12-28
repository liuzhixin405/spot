package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.NLastTicker;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.service.NLastTickerService;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.service.NTradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 合约 API（市场行情）
 * 
 * @author Administrator
 * 
 */
public class NTimerTaskTickerJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NTransactionService transactionService;
	@Autowired
	private NTradeLogService tradeLogService;
	@Autowired
	private NPendingDelegationService pendingDelegationService;
	@Autowired
	private NLastTickerService bqLastTickerService;
	@Autowired
	private NTransactionTypeService transTypeService;
	@Autowired
	private NNotradetimeService bqNoTradeService;
	
	private static String[] contractArray = null;
	
	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.ncontracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		JSONArray array = new JSONArray();

		// 获取当前时间的24小时时间（时间戳）
		long yesterdayTime = TimeUtil.getYesterdayTime();
		
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
			
			String bqCode = bq.getTransaction_code(); // 获取合约号
			if (bq.getTransaction_kind() == 1 && !bqNoTradeService.isTradeTime(partition, bq.getTransaction_id())){
				String old = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + partition + "_" + bqCode);
				if (!StringUtil.isEmpty(old)) {
					spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_BTC + "_" + partition + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_BTC, old);
					continue;
				}
			}
			
			BigDecimal high = BigDecimal.ZERO;
			BigDecimal low = BigDecimal.ZERO;
			BigDecimal buy = BigDecimal.ZERO;
			BigDecimal sell = BigDecimal.ZERO;
			BigDecimal last = BigDecimal.ZERO;
			BigDecimal vol = BigDecimal.ZERO;
			
			Integer digits = spyMemcachedClient.get(partition + "_" + bqCode + BTCQMemecacheKeys.DIGITS);
			if (digits == null) {
				NTransactionType bqTransactionType = transTypeService.getTransactionType(partition, bq.getTransaction_type());
				digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
				spyMemcachedClient.set(partition + "_" + bqCode + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
			}

			// 1.先从缓存中查找 最新成交价
			// 从数据库中算出最高价 btc1407_trade_log
			high = tradeLogService.findHighPrice(partition, bqCode, yesterdayTime);
			if (high == null) high = BigDecimal.ZERO;

			// 从数据库中算出最低价 btc1407_trade_log
			low = tradeLogService.findLowPrice(partition, bqCode, yesterdayTime);
			if (low == null) low = BigDecimal.ZERO;
			
			String strLast = partition + "_" + bqCode + BTCQMemecacheKeys.BTCLATESTPRICE;
			last = spyMemcachedClient.get(strLast);
			if (last == null) {
				// 从数据库中算出最新成交价 
				last = transactionService.findTransByCode(partition, bqCode).getLatest_transaction_price();
			}
			
			if (last == null) last = BigDecimal.ZERO;

			NPendingDelegation pendingBuy = pendingDelegationService.getBuy1(partition, bqCode);
			NPendingDelegation pendingSell = pendingDelegationService.getSell1(partition, bqCode);

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
			vol = tradeLogService.findBTCVol(partition, bqCode, yesterdayTime);
			vol = vol == null ? new BigDecimal(0) : vol;

			BigDecimal trades = tradeLogService.findTradeBTC(partition, bqCode, yesterdayTime);
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
			
			NTransaction bqt = transactionService.findTransToUpdateByCode(partition, bqCode);
			if (high.compareTo(bqt.getTransaction_highest()) != 0 || low.compareTo(bqt.getTransaction_lowest()) != 0) {
				transactionService.updateTrans(partition, bqt.getTransaction_id(), high, low);
			}
			
			bqLastTickerService.insertOrUploadLastTicker(partition, new NLastTicker(0, bqCode, high, low, buy, sell, last, vol, trades));
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_BTC + "_" + partition + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_BTC, obj1.toString());
		}

	}
}
