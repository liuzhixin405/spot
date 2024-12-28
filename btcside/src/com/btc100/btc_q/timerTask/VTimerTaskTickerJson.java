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
import com.btc100.btc_q.entity.VLastTicker;
import com.btc100.btc_q.entity.VPendingDelegation;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.service.VLastTickerService;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 合约 API（市场行情）
 * 
 * @author Administrator
 * 
 */
public class VTimerTaskTickerJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VTransactionTypeService vTransTypeService;
	@Autowired
	private VTradeLogService vTradeLogService;
	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	private VLastTickerService vLastTickerService;
	
	public void run() {
		
		JSONArray array = new JSONArray();

		// 获取当前时间的24小时时间（时间戳）
		long yesterdayTime = TimeUtil.getYesterdayTime();
		
		List<VTransaction> vTransactionList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (vTransactionList == null) {
			vTransactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, vTransactionList);
		}

		for (VTransaction vTransaction : vTransactionList) {
			BigDecimal high = BigDecimal.ZERO;
			BigDecimal low = BigDecimal.ZERO;
			BigDecimal buy = BigDecimal.ZERO;
			BigDecimal sell = BigDecimal.ZERO;
			BigDecimal last = BigDecimal.ZERO;
			BigDecimal vol = BigDecimal.ZERO;
			
			String bqCode = vTransaction.getTransaction_code(); // 获取合约号
			Integer digits = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.V_DIGITS);
			if (digits == null) {
				VTransactionType vTransactionType = vTransTypeService.getTransactionType(vTransaction.getTransaction_type());
				digits = Utils.getDigitsFromMinFloat(vTransactionType.getMin_float_price());
				spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.V_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
			}

			// 1.先从缓存中查找 最新成交价
			// 从数据库中算出最高价 btc1407_trade_log
			high = vTradeLogService.findHighPrice(bqCode, yesterdayTime);
			if (high == null) high = BigDecimal.ZERO;

			// 从数据库中算出最低价 btc1407_trade_log
			low = vTradeLogService.findLowPrice(bqCode, yesterdayTime);
			if (low == null) low = BigDecimal.ZERO;
			
			String strLast = bqCode + BTCQMemecacheKeys.V_BTCLATESTPRICE;
			last = spyMemcachedClient.get(strLast);
			if (last == null) {
				// 从数据库中算出最新成交价 
				last = vTransactionService.findTransByCode(bqCode).getLatest_transaction_price();
			}
			
			if (last == null) last = BigDecimal.ZERO;

			VPendingDelegation pendingBuy = vPendingDelegationService.getBuy1(bqCode);
			VPendingDelegation pendingSell = vPendingDelegationService.getSell1(bqCode);

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
			vol = vTradeLogService.findBTCVol(bqCode, yesterdayTime);
			vol = vol == null ? new BigDecimal(0) : vol;

			BigDecimal trades = vTradeLogService.findTradeBTC(bqCode, yesterdayTime);
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
			
			vTransactionService.updateTrans(vTransaction.getTransaction_id(), high, low);
			vLastTickerService.insertOrUploadLastTicker(new VLastTicker(0, bqCode, high, low, buy, sell, last, vol, trades));
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_BTC + "_" + bqCode, BTCQMemecacheKeys.TIMES.TICKER_BTC, obj1.toString());
		}
	}

}
