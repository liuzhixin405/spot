package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQLastTicker;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.service.BQLastTickerService;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.TradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 合约 API（市场行情）
 * 
 * @author Administrator
 * 
 */
public class TimerTaskTickerJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private TradeLogService tradeLogService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private BQLastTickerService bqLastTickerService;
	@Autowired
	private BQTransactionTypeService transTypeService;
	@Autowired
	private BQNotradetimeService bqNoTradeService;
	
	private static String[] contractArray = null;
	
	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.contracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;

		// 获取当前时间的24小时时间（时间戳）
		long yesterdayTime = TimeUtil.getYesterdayTime();
		
		// 找出在用的合约
		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
		}
		
		// 遍历在用合约，找出合约号
		for (String contract : contractArray) {
			BQTransaction bq = null;
			for (BQTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equals(contract)) {
					bq = bqTransaction;
					break;
				}
			}
			
			if (bq == null) {
				continue;
			}
			
			String bqCode = bq.getTransaction_code(); // 获取合约号
			if (bq.getTransaction_kind() == 1 && !bqNoTradeService.isTradeTime(bq.getTransaction_id())){
				String old = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + bqCode);
				if (!StringUtil.isEmpty(old)) {
					spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_BTC + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_BTC, old);
					continue;
				}
			}
			
			BigDecimal high = BigDecimal.ZERO;
			BigDecimal low = BigDecimal.ZERO;
			BigDecimal buy = BigDecimal.ZERO;
			BigDecimal sell = BigDecimal.ZERO;
			BigDecimal last = BigDecimal.ZERO;
			BigDecimal vol = BigDecimal.ZERO;
			
			Integer digits = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.DIGITS);
			if (digits == null) {
				BQTransactionType bqTransactionType = transTypeService.getTransactionType(bq.getTransaction_type());
				digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
				spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
			}

			// 1.先从缓存中查找 最新成交价
			// 从数据库中算出最高价 btc1407_trade_log
			high = tradeLogService.findHighPrice(bqCode, yesterdayTime);
			if (high == null) high = BigDecimal.ZERO;

			// 从数据库中算出最低价 btc1407_trade_log
			low = tradeLogService.findLowPrice(bqCode, yesterdayTime);
			if (low == null) low = BigDecimal.ZERO;

			PendingDelegation pendingBuy = pendingDelegationService.getBuy1(bqCode);
			PendingDelegation pendingSell = pendingDelegationService.getSell1(bqCode);

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
			vol = vol.setScale(digits, BigDecimal.ROUND_DOWN);
			trades = trades.setScale(digits, BigDecimal.ROUND_DOWN);
			
			String strLast = bqCode + BTCQMemecacheKeys.BTCLATESTPRICE;
			last = spyMemcachedClient.get(strLast);
			if (last == null) {
				// 从数据库中算出最新成交价 
				last = transactionService.findTransByCode(bqCode).getLatest_transaction_price();
			}
			
			if (last == null) last = BigDecimal.ZERO;
			last = last.setScale(digits, BigDecimal.ROUND_DOWN);
			if (last != null && last.doubleValue() > 0) {
				if (high.doubleValue() < last.doubleValue() || high.doubleValue() == 0) {
					high = last;
				}
				
				if (low.doubleValue() > last.doubleValue() || low.doubleValue() == 0) {
					low = last;
				}
			}

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
			
			BQTransaction bqt = transactionService.findTransToUpdateByCode(bqCode);
			if (high.compareTo(bqt.getTransaction_highest()) != 0 || low.compareTo(bqt.getTransaction_lowest()) != 0) {
				transactionService.updateTrans(bqt.getTransaction_id(), high, low);
			}
			
			bqLastTickerService.insertOrUploadBQLastTicker(new BQLastTicker(0, bqCode, high, low, buy, sell, last, vol, trades));
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_BTC + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_BTC, obj1.toString());
		}

	}
}
