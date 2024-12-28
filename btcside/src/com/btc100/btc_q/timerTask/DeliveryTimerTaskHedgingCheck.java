package com.btc100.btc_q.timerTask;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.DeliveryHedgingRecord;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.DeliveryHedgingRecordService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;

public class DeliveryTimerTaskHedgingCheck {
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryTransactionService bqTransactionService;
	@Autowired
	private DeliveryHedgingRecordService deliveryHedgingRecordService;
	
	private static final String LAST_CHECK_TIME = "d_last_hedging_check_time_";
	
	public static String[] contractArray = null;

	public void run() {

		if (contractArray == null) {
			String contracts = UrlConfig.getValue("deliveryTrade.contracts");
			contractArray = contracts.split(",");
		}

		if (contractArray == null || contractArray.length == 0)
			return;
		
		List<DeliveryTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, lstTrads);
		}
		if (lstTrads == null || lstTrads.size() == 0) return;
		
		Long currTime = System.currentTimeMillis() / 1000;
		for (String contract : contractArray) {
			String[] codeDelivery = contract.split("_");
			if (codeDelivery.length == 0) {
				continue;
			}
			String typeCode = codeDelivery[0];
			String deliveryType = codeDelivery[1];
			
			DeliveryTransaction transaction = null;
			for (DeliveryTransaction bqTransaction : lstTrads) {
				if (bqTransaction.getTransaction_type_code().equals(typeCode) && bqTransaction.getDelivery_type().equals(deliveryType)) {
					transaction = bqTransaction;
					break;
				}
			}

			if (transaction == null) {
				continue;
			}
			
			if (transaction.getTransaction_state() != 1) {
				continue;
			}
			Long lastTime = spyMemcachedClient.get(LAST_CHECK_TIME + transaction.getTransaction_code());
			if (lastTime != null && lastTime.longValue() != 0) {
				List<DeliveryHedgingRecord> records = new ArrayList<DeliveryHedgingRecord>();
				List<DeliveryHedgingRecord> rds = deliveryHedgingRecordService.findRecordsByCodeAndStateBetweenTime(transaction.getTransaction_code(), 2, lastTime, currTime);
				records.addAll(rds);
				
				String message = "";
				for (int i = 0; i < records.size(); i++) {
					if (i > 10) break;
					
					DeliveryHedgingRecord record = records.get(i);
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
		
			spyMemcachedClient.set(LAST_CHECK_TIME + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, currTime);
		}
	}
}
