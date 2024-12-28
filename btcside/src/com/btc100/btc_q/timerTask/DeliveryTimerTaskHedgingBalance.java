package com.btc100.btc_q.timerTask;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.util.FCoinApiUtil;
import com.btc100.btc_q.util.HuobiApiUtil;

public class DeliveryTimerTaskHedgingBalance {
	
//	TODO UrlConfig 配置文件需要增加配置
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryTransactionService bqTransactionService;
	
	Logger logger = Logger.getLogger(TimerTaskHedging.class);
	private static HuobiApiUtil apiUtil = null; 
	private static FCoinApiUtil fCoinUtil = null;
	private static HuobiApiUtil elfUtil = null; 
	
	public void run() {
		
		if (apiUtil == null) {
			try {
				apiUtil = new HuobiApiUtil(UrlConfig.getValue("DeliveryHuobiSecretKey"), UrlConfig.getValue("DeliveryHuobiAccessKey"));
				apiUtil.getAccounts();
			} catch (UnsupportedEncodingException | JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (elfUtil == null) {
			try {
				elfUtil = new HuobiApiUtil(UrlConfig.getValue("DeliveryELFSecretKey"), UrlConfig.getValue("DeliveryELFAccessKey"));
				elfUtil.getAccounts();
			} catch (UnsupportedEncodingException | JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (fCoinUtil == null) {
			fCoinUtil = new FCoinApiUtil(UrlConfig.getValue("DeliveryFCoinSecretKey"), UrlConfig.getValue("DeliveryFCoinAccessKey"));
		}
		
		Map<String, BigDecimal> map = null;
		try {
			map = apiUtil.getBalance();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<DeliveryTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if(list == null) {
			list = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list);
		}
		
		if (map != null) {
			if (map.containsKey("usdt")) {
				spyMemcachedClient.set(BTCQMemecacheKeys.D_HEDGING_BALANCE + "usdt", BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get("usdt"));
			}
			
			for(DeliveryTransaction trans :list) {
				String code = trans.getTransaction_type_code();
				if (!"ft".equals(code) && !"elf".equals(code)) {
					if (map.containsKey(code)) {
						spyMemcachedClient.set(BTCQMemecacheKeys.D_HEDGING_BALANCE + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get(code));
					}
				}
			}
		}
		
		map = null;
		try {
			map = elfUtil.getBalance();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (map != null) {
			if (map.containsKey("usdt")) {
				spyMemcachedClient.set("ELF_" + BTCQMemecacheKeys.D_HEDGING_BALANCE + "usdt", BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get("usdt"));
			}
			
			if (map.containsKey("elf")) {
				spyMemcachedClient.set(BTCQMemecacheKeys.D_HEDGING_BALANCE + "elf", BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get("elf"));
			}
		}
		
		map = null;
		try {
			map = fCoinUtil.getBalance();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (map != null) {
			if (map.containsKey("ft")) {
				spyMemcachedClient.set(BTCQMemecacheKeys.D_HEDGING_BALANCE + "ft", BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get("ft"));
			} 
			
			if (map.containsKey("usdt")) {
				spyMemcachedClient.set("Fcoin_" + BTCQMemecacheKeys.D_HEDGING_BALANCE + "usdt", BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get("usdt"));
			}
		}
	}
}
