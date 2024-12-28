package com.btc100.btc_q.timerTask;

import java.util.Calendar;
import java.util.List;

import org.json.JSONException;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.VTransaction;

/**
 * 6小时内交易（市场行情）
 * 
 * @author Administrator
 * 
 */
public class TimerTaskSixHoursTrade extends BaseTask {

	public void run() {
		
		// 获取当前时间所在的6小时区间内的起始时间点（0:00:00、6:00:00、12:00:00、18:00:00）
		long typeTime = 21600L;
		Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
		long sixHoursAgoMillis = ca.getTimeInMillis() / 1000;
		String timeTypeStr = "_six_hours";
		String _v_timeTypeStr = "_v_six_hours";
		String timeTypeStrAbbr = "_two_hours";
		
		Long lastTime = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr);
		if (lastTime == null) {
			lastTime = 0L;
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, sixHoursAgoMillis);
		
		// 找出在用的合约
		List<BQTransaction> transactionList = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doKLine(transactionList, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, sixHoursAgoMillis, BTCQMemecacheKeys.TICKER_TWOHOURS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的交易对
		List<BQSpotMarketCoinPair> coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doSpotKLine(coinPairs, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, sixHoursAgoMillis, BTCQMemecacheKeys.TICKER_TWOHOURS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的合约
		List<VTransaction> vTransactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doSimuKLine(vTransactionList, _v_timeTypeStr, "_v_two_hours", typeTime, lastTime, sixHoursAgoMillis, BTCQMemecacheKeys.V_TICKER_TWOHOURS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的合约
		List<DeliveryTransaction> dTransactionList = dTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doDeliveryKLine(dTransactionList, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, sixHoursAgoMillis, BTCQMemecacheKeys.D_TICKER_TWOHOURS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
