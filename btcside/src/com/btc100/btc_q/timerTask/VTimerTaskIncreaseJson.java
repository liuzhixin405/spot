package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.util.TimeUtil;

/**
 * 合约 API（市场增仓）
 * 
 * @author Administrator
 * 
 */
public class VTimerTaskIncreaseJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VTradeLogService vTradeLogService;
	@Autowired
	private VTransactionService vTransactionService;
	
	public void run() {
		
		List<VTransaction> vTransactionList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (vTransactionList == null) {
			vTransactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, vTransactionList);
		}
		
		for (VTransaction bq : vTransactionList) {
			BigDecimal first = vTradeLogService.findFirstPrice(bq.getTransaction_code(), TimeUtil.getTimesmorning());
			if (first == null) first = BigDecimal.ZERO;
			spyMemcachedClient.set(bq.getTransaction_code() + BTCQMemecacheKeys.V_FIRST_PRICE, BTCQMemecacheKeys.TIMES.ONE_HOUR, first);
		}
	}
}
