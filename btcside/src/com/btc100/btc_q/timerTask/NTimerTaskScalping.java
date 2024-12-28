package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.NArbitrageSetting;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.NMemberTradeDetail;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NTradeLog;
import com.btc100.btc_q.service.NArbitrageSettingService;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.service.NMemberTradeDetailService;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.service.NTradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

public class NTimerTaskScalping {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NTransactionService bqTransactionService;
	@Autowired
	private NArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private NPendingDelegationService pendingDelegationService;
	@Autowired
	private NTradeLogService tradeLogService;
	@Autowired
	private NMemberTradeDetailService memberTradeDetailService;
	@Autowired
	private NTransactionTypeService transTypeService;
	@Autowired
	private NNotradetimeService bqNoTradeService;
	
	private static Map<String, Integer> countDown = new HashMap<String, Integer>();
	private static Map<String, Long> transIdMap = new HashMap<String, Long>();
	
	private static int timeCount = 0;
	
	private static String[] contractArray = null;
	
	Logger logger = Logger.getLogger(NTimerTaskScalping.class);
	
	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.ncontracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		if (countDown.size() == 0) {
			for (String pContract : contractArray) {
				String[] strs = pContract.split("_");
				if (strs.length != 2) continue;
				String partition = strs[0];
				String contract = strs[1];
				
				if (StringUtil.isEmpty(partition) || StringUtil.isEmpty(contract)) {
					continue;
				}
				
				NTransaction trans = bqTransactionService.findTransByCode(partition, contract);
				if (trans == null) continue;
				
				if (!countDown.containsKey(pContract)) {
					countDown.put(pContract, 0);
					transIdMap.put(pContract, trans.getTransaction_id());
				}
			}
		}
		
		for (String pContract : countDown.keySet()) {
			String[] strs = pContract.split("_");
			if (strs.length != 2) continue;
			String partition = strs[0];
			String contract = strs[1];
			
			if (StringUtil.isEmpty(partition) || StringUtil.isEmpty(contract)) {
				continue;
			}
			
			NTransaction bqTransaction = bqTransactionService.findTransByCode(partition, contract);
			if (bqTransaction == null) continue;
			
			if (bqTransaction.getSuspend()) {
				continue;
			}
			
			if (bqTransaction.getTransaction_kind() == 1 && !bqNoTradeService.isTradeTime(partition, bqTransaction.getTransaction_id())) {
				continue;
			}
			
			int value = countDown.get(pContract);
			if (value == 0) {
				long codeId = transIdMap.get(pContract);
				int sleepTime = 1000;
				do {
					NArbitrageSetting setting = null;
					try {
						setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + partition + "_" + contract);
					} catch (Exception e) {}
					
					if (setting == null) {
						setting = bqArbitrageSettingService.getArbitrageSettingByCode(partition, contract);
						if (setting == null) {
							setting = new NArbitrageSetting();
							setting.setRunning(false);
							setting.setScalping(false);
							setting.setHedging(false);
							setting.setRisk_manage(false);
							setting.setAuto_trade(false);
							setting.setDepth_uid(0L);
						}
						
						spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + partition + "_" + contract, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
					}
					
					if (setting == null || !setting.getScalping()) break;
					
					NPendingDelegation sell1 = pendingDelegationService.getSell1(partition, contract);
					NPendingDelegation buy1 = pendingDelegationService.getBuy1(partition, contract);
					
					if (sell1 == null || buy1 == null) break;
					
					if (setting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0
							&& sell1.getDelegation_price().subtract(buy1.getDelegation_price()).compareTo(setting.getScalping_depth_range().multiply(buy1.getDelegation_price())) > 0) {
						break;
					}
					
					Integer digits = spyMemcachedClient.get(partition + "_" + contract + BTCQMemecacheKeys.DIGITS);
					if (digits == null) {
						List<NTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition);
						if (transTypes == null) {
							transTypes = transTypeService.findAllUsing(partition);
							spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition,BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
						}
						
						NTransactionType transType = null;
						for (NTransactionType type : transTypes) {
							if (type.getType_code().equals(contract)) {
								transType = type;
							}
						}
						
						if (transType == null) continue;
						
						digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
						spyMemcachedClient.set(partition + "_" + transType.getType_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
					}
					
					BigDecimal minChange = new BigDecimal(1.0 / Math.pow(10, digits));
					minChange = minChange.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
					if (sell1.getDelegation_price().subtract(buy1.getDelegation_price()).abs().compareTo(minChange) <= 0) {
						break;
					}
					
					boolean isSell = true;
					if (Math.random() < 0.5) {
						isSell = false;
					}
					
					int min = setting.getScalping_frequency_min();
					int max = setting.getScalping_frequency_max();
					int num = min + (int)(Math.random() * (max - min));
					if (num == 0) {
						sleepTime = (60 + 30) * 1000;
					} else {
						sleepTime = (int)(60000.0 / num);
					}
					
					int addi = (int)(Math.random() * 20) - 10;
					sleepTime += (addi * 1000);
					if (sleepTime < 1000) {
						sleepTime = 1000;
					}
					
					int amountDigits = Utils.getAmountDigtis(contract);
					BigDecimal random = new BigDecimal(Math.random());
					BigDecimal amount = setting.getScalping_min_each().add(random.multiply(setting.getScalping_max_each().subtract(setting.getScalping_min_each())));
					amount = amount.setScale(amountDigits, BigDecimal.ROUND_DOWN);
					
					BigDecimal btc_index_price = spyMemcachedClient.get(contract);
					if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
						BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + bqTransaction.getTransaction_code());
						if (preClose != null)
							btc_index_price = preClose;
					}
					
					if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}
					
					BigDecimal range = new BigDecimal(0.05);
					if (setting.getScalping_index_range().compareTo(BigDecimal.ZERO) > 0) {
						range = setting.getScalping_index_range();
					}
					
					BigDecimal high = btc_index_price.multiply(BigDecimal.ONE.add(range));
					BigDecimal low = btc_index_price.multiply(BigDecimal.ONE.subtract(range));
					
					if (sell1.getDelegation_price().compareTo(high) <= 0) {
						high = sell1.getDelegation_price().subtract(minChange);
					}
					
					if (buy1.getDelegation_price().compareTo(low) >= 0) {
						low = buy1.getDelegation_price().add(minChange);
					}
					
					BigDecimal depthWidth = new BigDecimal(0.02);
					if (setting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0) {
						depthWidth = setting.getScalping_depth_range();
					}
					if (high.compareTo(low) < 0 || high.subtract(low).divide(low, 4, BigDecimal.ROUND_HALF_UP).compareTo(depthWidth) > 0) {
						continue;
					}
					
					BigDecimal price = low.add(new BigDecimal(Math.random()).multiply(high.subtract(low)));
					price = price.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
					
					if (sell1.getDelegation_price().compareTo(price) == 0) {
						price = sell1.getDelegation_price().subtract(minChange);
					}
					
					if (buy1.getDelegation_price().compareTo(price) == 0) {
						price = buy1.getDelegation_price().add(minChange);
					}
					
					price = price.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
					if (sell1.getDelegation_price().compareTo(price) == 0 
							|| buy1.getDelegation_price().compareTo(price) == 0) {
						continue;
					}
					
					BigDecimal deposit = BTCQMath.getDeposit(amount, price, 20, bqTransaction.getTrade_coefficient());
					
					int typeId = (Math.random() >= 0.5) ? 2 : 3;
					NTradeLog sellNTradeLog = new NTradeLog(codeId, contract, typeId, 2, BigDecimal.ZERO, price, amount, 20, deposit, 
							BigDecimal.ZERO, BigDecimal.ZERO, Contents.BQMEMBER_ADMIN, 0);
					tradeLogService.saveTradeLog(partition, sellNTradeLog);
					
					typeId = (Math.random() >= 0.5) ? 1 : 4;
					NTradeLog buyNTradeLog = new NTradeLog(codeId, contract, typeId, 1, BigDecimal.ZERO, price, amount, 20, deposit, 
							BigDecimal.ZERO, BigDecimal.ZERO, Contents.BQMEMBER_ADMIN, 0);
					tradeLogService.saveTradeLog(partition, buyNTradeLog);
					
					NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), Contents.BQMEMBER_ADMIN, "", Contents.BQMEMBER_ADMIN, "", 
							sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog.getTrade_type_id(), 1, isSell ? 2 : 1, price, amount, codeId, contract);
					memberTradeDetailService.save(partition, tradeDetail);

					spyMemcachedClient.set(partition + "_" + contract + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, price);
					
				} while (false);
				
				value = sleepTime / 1000 - 1;
			} else {
				value -= 1;
			}
			
			if (value < 0) value = 0;
			countDown.put(pContract, value);
		}
		
		if (timeCount % 1800 == 0) {
			timeCount = 0;
			for (String pContract : countDown.keySet()) {
				String[] strs = pContract.split("_");
				if (strs.length != 2) continue;
				String partition = strs[0];
				String contract = strs[1];
				
				long yesterday = System.currentTimeMillis() / 1000 - 3600*24;
				tradeLogService.deleteVTradeScalpingLog(partition, contract, Contents.BQMEMBER_ADMIN, yesterday);
				memberTradeDetailService.deleteVTradeScalpingDetail(partition, contract, Contents.BQMEMBER_ADMIN, yesterday);
			}
		}
		
		timeCount++;
	}
}
