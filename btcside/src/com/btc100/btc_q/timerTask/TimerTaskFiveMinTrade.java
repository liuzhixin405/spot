package com.btc100.btc_q.timerTask;

import java.util.List;

import org.json.JSONException;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.VTransaction;

/**
 * 5分钟内交易（市场行情）
 * 
 * @author Administrator
 * 
 */
public class TimerTaskFiveMinTrade extends BaseTask {

	public void run() {
		
		long currentTimeMillis = System.currentTimeMillis() / 1000;
		// 获取当前时间所在的5分钟区间内的起始时间点（0:00:00、0:05:00、0:10:00、0:15:00、0:20:00...）
		long typeTime = 300L;
		long fiveMinAgoMillis = currentTimeMillis / typeTime * typeTime;
		String timeTypeStr = "_five_minutes";
		String _v_timeTypeStr = "_v_five_minutes";
		String timeTypeStrAbbr = "_one_minute";
		
		Long lastTime = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr);
		if (lastTime == null) {
			lastTime = 0L;
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, fiveMinAgoMillis);
		
		// 找出在用的合约
		List<BQTransaction> transactionList = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doKLine(transactionList, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, fiveMinAgoMillis, BTCQMemecacheKeys.TICKER_ONEMINUTE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的交易对
		List<BQSpotMarketCoinPair> coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doSpotKLine(coinPairs, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, fiveMinAgoMillis, BTCQMemecacheKeys.TICKER_ONEMINUTE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的合约
		List<VTransaction> vTransactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doSimuKLine(vTransactionList, _v_timeTypeStr, "_v_one_minute", typeTime, lastTime, fiveMinAgoMillis, BTCQMemecacheKeys.V_TICKER_ONEMINUTE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的合约 doDeliveryKLine
		List<DeliveryTransaction> dTransactionList = dTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doDeliveryKLine(dTransactionList, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, fiveMinAgoMillis, BTCQMemecacheKeys.D_TICKER_ONEMINUTE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
