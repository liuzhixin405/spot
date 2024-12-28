package com.btc100.btc_q.timerTask;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.NContractPartitionService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.util.FCoinApiUtil;
import com.btc100.btc_q.util.HuobiApiUtil;

public class TimerTaskHedgingBalance {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private NContractPartitionService nContractPartitionService;
	@Autowired
	private NTransactionService nTransactionService;
	
	Logger logger = Logger.getLogger(TimerTaskHedging.class);
	private static HuobiApiUtil apiUtil = null; 
	private static FCoinApiUtil fCoinUtil = null;
	private static HuobiApiUtil elfUtil = null; 
	
	public void run() {
		
		if (apiUtil == null) {
			try {
				apiUtil = new HuobiApiUtil(UrlConfig.getValue("HuobiSecretKey"), UrlConfig.getValue("HuobiAccessKey"));
				apiUtil.getAccounts();
			} catch (UnsupportedEncodingException | JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (elfUtil == null) {
			try {
				elfUtil = new HuobiApiUtil(UrlConfig.getValue("ELFSecretKey"), UrlConfig.getValue("ELFAccessKey"));
				elfUtil.getAccounts();
			} catch (UnsupportedEncodingException | JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (fCoinUtil == null) {
			fCoinUtil = new FCoinApiUtil(UrlConfig.getValue("FCoinSecretKey"), UrlConfig.getValue("FCoinAccessKey"));
		}
		
		Map<String, BigDecimal> map = null;
		try {
			map = apiUtil.getBalance();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		List<BQTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if(list == null) {
			list = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list);
		}
		
		List<String> codes = new ArrayList<String>();
		if (map != null) {
			if (map.containsKey("usdt")) {
				spyMemcachedClient.set(BTCQMemecacheKeys.HEDGING_BALANCE + "usdt", BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get("usdt"));
			}
			
			for(BQTransaction trans :list) {
				String code = trans.getTransaction_code();
				codes.add(code);
				
				if (!"ft".equals(code) && !"elf".equals(code)) {
					if (map.containsKey(code)) {
						spyMemcachedClient.set(BTCQMemecacheKeys.HEDGING_BALANCE + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get(code));
					}
				}
			}
		}
		
		if (map != null) {
			List<NContractPartition> partitions = nContractPartitionService.findAll();
			for (NContractPartition partition : partitions) {
				List<NTransaction> list1 = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + partition);
				if(list1 == null) {
					list1 = nTransactionService.findTransByState_new(partition.getPart(), Contents.TRANSACTION_STATE_ZC);
					spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + partition, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list1);
				}
				
				if (map.containsKey(partition)) {
					spyMemcachedClient.set(BTCQMemecacheKeys.HEDGING_BALANCE + partition, BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get(partition));
				}
				
				for(NTransaction trans : list1) {
					String code = trans.getTransaction_code();
					if (codes.contains(code)) continue;
					
					codes.add(code);
					if (!"ft".equals(code) && !"elf".equals(code)) {
						if (map.containsKey(code)) {
							spyMemcachedClient.set(BTCQMemecacheKeys.HEDGING_BALANCE + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get(code));
						}
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
				spyMemcachedClient.set("ELF_" + BTCQMemecacheKeys.HEDGING_BALANCE + "usdt", BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get("usdt"));
			}
			
			if (map.containsKey("elf")) {
				spyMemcachedClient.set(BTCQMemecacheKeys.HEDGING_BALANCE + "elf", BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get("elf"));
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
				spyMemcachedClient.set(BTCQMemecacheKeys.HEDGING_BALANCE + "ft", BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get("ft"));
			} 
			
			if (map.containsKey("usdt")) {
				spyMemcachedClient.set("Fcoin_" + BTCQMemecacheKeys.HEDGING_BALANCE + "usdt", BTCQMemecacheKeys.TIMES.TEN_SECOND, map.get("usdt"));
			}
		}
	}
}
