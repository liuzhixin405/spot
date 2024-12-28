package com.btc100.btc_q.timerTask;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQHedgingRecord;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.service.BQHedgingRecordService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;

public class TimerTaskHedgingCheck {
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private BQHedgingRecordService bqHedgingRecordService;
	
	private static final String LAST_CHECK_TIME = "last_hedging_check_time_";
	
	private static String[] contractArray = null;
	
	public void run() {
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.contracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		List<BQTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if(list == null) {
			list = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list);
		}
		
		Long currTime = System.currentTimeMillis() / 1000;
		for (String contract : contractArray) {
			BQTransaction bqt = null;
			for (BQTransaction bqTransaction : list) {
				if (bqTransaction.getTransaction_code().equals(contract)) {
					bqt = bqTransaction;
					break;
				}
			}
			
			if (bqt == null) {
				continue;
			}
		
			Long lastTime = spyMemcachedClient.get(LAST_CHECK_TIME + bqt.getTransaction_code());
			if (lastTime != null && lastTime.longValue() != 0) {
				List<BQHedgingRecord> records = new ArrayList<BQHedgingRecord>();
				List<BQHedgingRecord> rds = bqHedgingRecordService.findRecordsByCodeAndStateBetweenTime(bqt.getTransaction_code(), 2, lastTime, currTime);
				records.addAll(rds);
				
				String message = "";
				for (int i = 0; i < records.size(); i++) {
					if (i > 10) break;
					
					BQHedgingRecord record = records.get(i);
					message += record.getTransaction_code().toUpperCase() + "合约" + (record.getTrade_direction() == 1 ? "买入 " : "卖出 ") + record.getTrade_amount().doubleValue()
							+ " 个，价格 " + record.getTrade_price().doubleValue() + " USDT，站外对冲失败，请尽快核实<br>"; 
				}
				
				if (!StringUtil.isEmpty(message)) {
					final String content = message;
					Runnable runnable = new Runnable() {
			            public void run() {
		            	   	try {
			            		String email = "409072486@qq.com";
			            		SendMail themail = new SendMail();
		            			themail.send(email, content);
			            	} catch (Exception e) {
			            	}
		            	}
			        };
			        Thread thread = new Thread(runnable);
			        thread.start();
				}
			}
		
			spyMemcachedClient.set(LAST_CHECK_TIME + bqt.getTransaction_code(), BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, currTime);
		}
	}
}
