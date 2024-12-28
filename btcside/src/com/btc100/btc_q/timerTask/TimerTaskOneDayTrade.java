package com.btc100.btc_q.timerTask;

import java.util.List;

import org.json.JSONException;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.util.TimeUtil;

/**
 * 一天内交易（市场行情）
 * 
 * @author Administrator
 * 
 */
public class TimerTaskOneDayTrade extends BaseTask {

	public void run() {
		
		// 获取今天0点（时间戳）
		long todayZeroTime = TimeUtil.getTimesmorning();
		long typeTime = 86400L;
		String timeTypeStr = "_one_day";
		String _v_timeTypeStr = "_v_one_day";
		String timeTypeStrAbbr = "_twelve_hours";
		
		Long lastTime = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr);
		if (lastTime == null) {
			lastTime = 0L;
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.LAST_TICKER_TIME + timeTypeStr, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, todayZeroTime);
		
		// 找出在用的合约
		List<BQTransaction> transactionList = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doKLine(transactionList, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, todayZeroTime, BTCQMemecacheKeys.TICKER_TWELVEHOURS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的交易对
		List<BQSpotMarketCoinPair> coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doSpotKLine(coinPairs, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, todayZeroTime, BTCQMemecacheKeys.TICKER_TWELVEHOURS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的合约
		List<VTransaction> vTransactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doSimuKLine(vTransactionList, _v_timeTypeStr, "_v_twelve_hours", typeTime, lastTime, todayZeroTime, BTCQMemecacheKeys.V_TICKER_TWELVEHOURS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 找出在用的合约
		List<DeliveryTransaction> dTransactionList = dTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		try {
			doDeliveryKLine(dTransactionList, timeTypeStr, timeTypeStrAbbr, typeTime, lastTime, todayZeroTime, BTCQMemecacheKeys.D_TICKER_TWELVEHOURS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
