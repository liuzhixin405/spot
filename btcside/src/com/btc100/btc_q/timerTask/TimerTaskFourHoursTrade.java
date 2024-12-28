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
 * 4小时内交易（市场行情）
 * 
 * @author Administrator
 * 
 */
public class TimerTaskFourHoursTrade extends BaseTask {

	public void run() {
		
		long currentTimeMillis = System.currentTimeMillis() / 1000;
		// 获取当前时间所在的4小时区间内的起始时间点（0:00:00、4:00:00、8:00:00...）
		long typeTime = 14400L;
		long fourHoursAgoMillis = currentTimeMillis / typeTime * typeTime;
		String timeTypeStr = "_four_hours";
		String _v_timeTypeStr = "_v_four_hours";
		String timeTypeStrAbbr = "_two_hours";
		
		Long lastTime = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr);
		if (lastTime == null) {
			lastTime = 0L;
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, fourHoursAgoMillis);
		
		// 找出在用的合约
		List<BQTransaction> transactionList = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doKLine(transactionList, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, fourHoursAgoMillis, BTCQMemecacheKeys.TICKER_TWOHOURS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的交易对
		List<BQSpotMarketCoinPair> coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doSpotKLine(coinPairs, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, fourHoursAgoMillis, BTCQMemecacheKeys.TICKER_TWOHOURS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的合约
		List<VTransaction> vTransactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doSimuKLine(vTransactionList, _v_timeTypeStr, "_v_two_hours", typeTime, lastTime, fourHoursAgoMillis, BTCQMemecacheKeys.V_TICKER_TWOHOURS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的合约 doDeliveryKLine
		List<DeliveryTransaction> dTransactionList = dTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doDeliveryKLine(dTransactionList, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, fourHoursAgoMillis, BTCQMemecacheKeys.D_TICKER_TWOHOURS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
