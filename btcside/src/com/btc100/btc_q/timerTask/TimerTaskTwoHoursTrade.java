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
 * 2小时内交易（市场行情）
 * 
 * @author Administrator
 * 
 */
public class TimerTaskTwoHoursTrade extends BaseTask {

	public void run() {
		
		long currentTimeMillis = System.currentTimeMillis() / 1000;
		// 获取当前时间所在的2小时区间内的起始时间点（0:00:00、2:00:00、4:00:00...）
		long typeTime = 7200L;
		long twoHoursAgoMillis = currentTimeMillis / typeTime * typeTime;
		String timeTypeStr = "_two_hours";
		String _v_timeTypeStr = "_v_two_hours";
		String timeTypeStrAbbr = "_one_hour";
		
		Long lastTime = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr);
		if (lastTime == null) {
			lastTime = 0L;
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, twoHoursAgoMillis);
		
		// 找出在用的合约
		List<BQTransaction> transactionList = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doKLine(transactionList, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, twoHoursAgoMillis, BTCQMemecacheKeys.TICKER_ONEHOUR);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的交易对
		List<BQSpotMarketCoinPair> coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doSpotKLine(coinPairs, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, twoHoursAgoMillis, BTCQMemecacheKeys.TICKER_ONEHOUR);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的合约
		List<VTransaction> vTransactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doSimuKLine(vTransactionList, _v_timeTypeStr, "_v_one_hour", typeTime, lastTime, twoHoursAgoMillis, BTCQMemecacheKeys.V_TICKER_ONEHOUR);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的合约
		List<DeliveryTransaction> dTransactionList = dTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doDeliveryKLine(dTransactionList, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, twoHoursAgoMillis, BTCQMemecacheKeys.D_TICKER_ONEHOUR);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
