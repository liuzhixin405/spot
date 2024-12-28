package com.btc100.btc_q.timerTask;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.NHedgingRecord;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.service.NHedgingRecordService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;

public class NTimerTaskHedgingCheck {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NTransactionService bqTransactionService;
	@Autowired
	private NHedgingRecordService bqHedgingRecordService;
	
	private static final String LAST_CHECK_TIME = "last_hedging_check_time_";
	
	private static String[] contractArray = null;
	
	public void run() {
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.ncontracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		Long currTime = System.currentTimeMillis() / 1000;
		for (String pContract : contractArray) {
			String[] strs = pContract.split("_");
			if (strs.length != 2) continue;
			String partition = strs[0];
			String contract = strs[1];
			
			if (StringUtil.isEmpty(partition) || StringUtil.isEmpty(contract)) {
				continue;
			}
			
			NTransaction bqt = bqTransactionService.findTransByCode(partition, contract);
			if (bqt == null) continue;
		
			Long lastTime = spyMemcachedClient.get(LAST_CHECK_TIME + partition + "_" + bqt.getTransaction_code());
			if (lastTime != null && lastTime.longValue() != 0) {
				List<NHedgingRecord> records = new ArrayList<NHedgingRecord>();
				List<NHedgingRecord> rds = bqHedgingRecordService.findRecordsByCodeAndStateBetweenTime(partition, bqt.getTransaction_code(), 2, lastTime, currTime);
				records.addAll(rds);
				
				String message = "";
				for (int i = 0; i < records.size(); i++) {
					if (i > 10) break;
					
					NHedgingRecord record = records.get(i);
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
		
			spyMemcachedClient.set(LAST_CHECK_TIME + partition + "_" + bqt.getTransaction_code(), BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, currTime);
		}
	}
}
