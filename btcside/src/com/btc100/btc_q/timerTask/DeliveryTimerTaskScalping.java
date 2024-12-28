package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.DeliveryArbitrageSetting;
import com.btc100.btc_q.entity.DeliveryMemberTradeDetail;
import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryTradeLog;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.service.DeliveryArbitrageSettingService;
import com.btc100.btc_q.service.DeliveryMemberTradeDetailService;
import com.btc100.btc_q.service.DeliveryNotradetimeService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.util.Utils;

public class DeliveryTimerTaskScalping {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryTransactionService bqTransactionService;
	@Autowired
	private DeliveryArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private DeliveryPendingDelegationService pendingDelegationService;
	@Autowired
	private DeliveryTradeLogService tradeLogService;
	@Autowired
	private DeliveryMemberTradeDetailService memberTradeDetailService;
	@Autowired
	private DeliveryTransactionTypeService transTypeService;
	@Autowired
	private DeliveryNotradetimeService bqNoTradeService;
	
	private static Map<String, Integer> countDown = new HashMap<String, Integer>();
	private static Map<String, Long> transIdMap = new HashMap<String, Long>();
	
	private static int timeCount = 0;
	
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
		
		if (countDown.size() == 0) {
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
				
				if (!countDown.containsKey(transaction.getTransaction_code())) {
					countDown.put(transaction.getTransaction_code(), 0);
					transIdMap.put(transaction.getTransaction_code(), transaction.getTransaction_id());
				}
			}
		}
		
		for (String code : countDown.keySet()) {
			DeliveryTransaction bqTransaction = null;
			for(DeliveryTransaction trans :lstTrads) {
				if (trans.getTransaction_code().equals(code)) {
					bqTransaction = trans;
					break;
				}
			}
			
			if (bqTransaction == null) continue;
			
			if (bqTransaction.getSuspend() || bqTransaction.getTransaction_state() != 1) {
				continue;
			}
			
			if (bqTransaction.getTransaction_kind() == 1 && !bqNoTradeService.isTradeTime(bqTransaction.getTransaction_id())) {
				continue;
			}
			
			int value = countDown.get(code);
			if (value == 0) {
				long codeId = transIdMap.get(code);
				int sleepTime = 1000;
				do {
					DeliveryArbitrageSetting setting = null;
					try {
						setting = spyMemcachedClient.get(BTCQMemecacheKeys.D_ARBITRAGE_SETTING + code);
					} catch (Exception e) {}
					
					if (setting == null) {
						setting = bqArbitrageSettingService.getArbitrageSettingByCode(code);
						if (setting == null) {
							setting = new DeliveryArbitrageSetting();
							setting.setRunning(false);
							setting.setScalping(false);
							setting.setHedging(false);
							setting.setRisk_manage(false);
							setting.setAuto_trade(false);
							setting.setDepth_uid(0L);
						}
						
						spyMemcachedClient.set(BTCQMemecacheKeys.D_ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
					}
					
					if (setting == null || !setting.getScalping()) break;
					
					DeliveryPendingDelegation sell1 = pendingDelegationService.getSell1(code);
					DeliveryPendingDelegation buy1 = pendingDelegationService.getBuy1(code);
					
					if (sell1 == null || buy1 == null) break;
					
					if (setting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0
							&& sell1.getDelegation_price().subtract(buy1.getDelegation_price()).compareTo(setting.getScalping_depth_range().multiply(buy1.getDelegation_price())) > 0) {
						break;
					}
					
					Integer digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_DIGITS);
					if (digits == null) {
						List<DeliveryTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANS_TYPES);
						if (transTypes == null) {
							transTypes = transTypeService.findAllUsing();
							spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
						}
						
						DeliveryTransactionType transType = null;
						for (DeliveryTransactionType type : transTypes) {
							if (type.getType_code().equals(bqTransaction.getTransaction_type_code())) {
								transType = type;
							}
						}
						
						if (transType == null) continue;
						
						digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
						spyMemcachedClient.set(code + BTCQMemecacheKeys.D_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
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
					
					int amountDigits = Utils.getAmountDigtis(code);
					BigDecimal random = new BigDecimal(Math.random());
					BigDecimal amount = setting.getScalping_min_each().add(random.multiply(setting.getScalping_max_each().subtract(setting.getScalping_min_each())));
					amount = amount.setScale(amountDigits, BigDecimal.ROUND_DOWN);
					
//					指数价格从typeCode获取
					BigDecimal btc_index_price = spyMemcachedClient.get(bqTransaction.getTransaction_type_code());
					if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
						BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.D_PRECLOSE + bqTransaction.getTransaction_code());
						if (preClose != null)
							btc_index_price = preClose;
					}
					
					if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}
					
					BigDecimal range = new BigDecimal(0.01);
					if (setting.getScalping_index_range().compareTo(BigDecimal.ZERO) > 0) {
						range = setting.getScalping_index_range();
					}
					
					BigDecimal high = btc_index_price.multiply(BigDecimal.ONE.add(range));
					BigDecimal low = btc_index_price.multiply(BigDecimal.ONE.subtract(range));
					
					if (sell1.getDelegation_price().compareTo(high) <= 0) {
						high = sell1.getDelegation_price().subtract(minChange);;
					}
					
					if (buy1.getDelegation_price().compareTo(low) >= 0) {
						low = buy1.getDelegation_price().add(minChange);;
					}
					
					if (high.compareTo(low) < 0) {
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
					DeliveryTradeLog sellTradeLog = new DeliveryTradeLog(codeId, code, typeId, 2, BigDecimal.ZERO, price, amount, 20, deposit, 
							BigDecimal.ZERO, BigDecimal.ZERO, Contents.BQMEMBER_ADMIN, 0);
					tradeLogService.saveTradeLog(sellTradeLog);
					
					typeId = (Math.random() >= 0.5) ? 1 : 4;
					DeliveryTradeLog buyTradeLog = new DeliveryTradeLog(codeId, code, typeId, 1, BigDecimal.ZERO, price, amount, 20, deposit, 
							BigDecimal.ZERO, BigDecimal.ZERO, Contents.BQMEMBER_ADMIN, 0);
					tradeLogService.saveTradeLog(buyTradeLog);
					
					DeliveryMemberTradeDetail tradeDetail = new DeliveryMemberTradeDetail(buyTradeLog.getTrade_id(), Contents.BQMEMBER_ADMIN, "", Contents.BQMEMBER_ADMIN, "", 
							sellTradeLog.getTrade_id(), buyTradeLog.getTrade_type_id(), sellTradeLog.getTrade_type_id(), 1, isSell ? 2 : 1, price, amount, codeId, code);
					memberTradeDetailService.save(tradeDetail);

					spyMemcachedClient.set(code + BTCQMemecacheKeys.D_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, price);
					
				} while (false);
				
				value = sleepTime / 1000 - 1;
			} else {
				value -= 1;
			}
			
			if (value < 0) value = 0;
			countDown.put(code, value);
		}
		
		if (timeCount % 1800 == 0) {
			timeCount = 0;
			for (String code : countDown.keySet()) {
				DeliveryTransaction bqTransaction = null;
				for(DeliveryTransaction trans :lstTrads) {
					if (trans.getTransaction_code().equals(code)) {
						bqTransaction = trans;
						break;
					}
				}
				
				if (bqTransaction == null) continue;
				
				long yesterday = System.currentTimeMillis() / 1000 - 3600*24;
				tradeLogService.deleteVTradeScalpingLog(code, Contents.BQMEMBER_ADMIN, yesterday);
				memberTradeDetailService.deleteVTradeScalpingDetail(code, Contents.BQMEMBER_ADMIN, yesterday);
			}
		}
		
		timeCount++;
	}
}
