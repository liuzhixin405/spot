package com.btc100.btc_q.timerTask;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.DeliveryArbitrageSetting;
import com.btc100.btc_q.entity.DeliveryHedgingRecord;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryPendingLog;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.service.DeliveryArbitrageSettingService;
import com.btc100.btc_q.service.DeliveryHedgingRecordService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryOpenPositionServer;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryPendingLogService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.util.FCoinApiUtil;
import com.btc100.btc_q.util.HuobiApiUtil;
import com.btc100.btc_q.util.OKexApiUtil;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.Utils;

public class DeliveryTimerTaskHedging {

	@Autowired
	private DeliveryHedgingRecordService bqHedgingRecordService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryTransactionService bqTransactionService;
	@Autowired
	private DeliveryArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private DeliveryTransactionTypeService transTypeService;
	@Autowired
	private DeliveryMemberTransactionService bqMemberTransactionService; 
	@Autowired
	private DeliveryPendingDelegationService pendingDelegationService;
	@Autowired
	private DeliveryPendingLogService pendingLogService;
	@Autowired
	private DeliveryOpenPositionServer openPositionServer;
	
	Logger logger = Logger.getLogger(DeliveryTimerTaskHedging.class);
	
	private static HuobiApiUtil apiUtil = null; 
	private static HuobiApiUtil elfUtil = null; 
	private static OKexApiUtil okecUtil = null; 
	private static FCoinApiUtil fCoinUtil = null; 
	
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
			
			DeliveryArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.D_ARBITRAGE_SETTING + transaction.getTransaction_code());
			if (setting == null) {
				setting = bqArbitrageSettingService.getArbitrageSettingByCode(transaction.getTransaction_code());
				if (setting == null) {
					setting = new DeliveryArbitrageSetting();
					setting.setRunning(false);
					setting.setScalping(false);
					setting.setHedging(false);
					setting.setRisk_manage(false);
					setting.setAuto_trade(false);
					setting.setDepth_uid(0L);
				}
				
				spyMemcachedClient.set(BTCQMemecacheKeys.D_ARBITRAGE_SETTING + transaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
			
			if (setting.getHedging()) {
				Integer digits = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.D_DIGITS);
				if (digits == null) {
					List<DeliveryTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANS_TYPES);
					if (transTypes == null) {
						transTypes = transTypeService.findAllUsing();
						spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANS_TYPES, BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
					}
					
					DeliveryTransactionType transType = null;
					for (DeliveryTransactionType type : transTypes) {
						if (type.getType_code().equals(transaction.getTransaction_type_code())) {
							transType = type;
						}
					}
					
					if (transType == null) continue;
					
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(transaction.getTransaction_code() + BTCQMemecacheKeys.D_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
				
				// 外部对冲
				List<DeliveryHedgingRecord> records = bqHedgingRecordService.findRecordsByCodeAndState(transaction.getTransaction_code(), 0);
//				对冲下单用到symbol，应该改成transaction_type_code
//				String typeCode = transaction.getTransaction_type_code();
				for (DeliveryHedgingRecord record : records) {
					BigDecimal amount = record.getTrade_amount();
					BigDecimal price = record.getTrade_price();
					int direction = record.getDirection();
					if (direction == 1) {	// 买入
						price = price.multiply(BigDecimal.ONE.subtract(setting.getHedging_buy_range().multiply(new BigDecimal(0.01))));
						price = price.setScale(digits, BigDecimal.ROUND_DOWN);
					} else {
						price = price.multiply(BigDecimal.ONE.add(setting.getHedging_buy_range().multiply(new BigDecimal(0.01))));
						price = price.setScale(digits, BigDecimal.ROUND_UP);
					}
					
					JSONObject retObj = null;
					String error = "";
					try {
						if (setting.getCurr_use_state() == 0) {
							if (setting.getF_code().equals("ft")) {
								if (fCoinUtil == null) {
									fCoinUtil = new FCoinApiUtil(UrlConfig.getValue("DeliveryFCoinSecretKey"), UrlConfig.getValue("DeliveryFCoinAccessKey"));
								}
								
								retObj = fCoinUtil.orderUsdt(typeCode, direction, amount, price);
							} else if (setting.getF_code().equals("elf")) {
								if (elfUtil == null) {
									try {
										elfUtil = new HuobiApiUtil(UrlConfig.getValue("DeliveryELFSecretKey"), UrlConfig.getValue("DeliveryELFAccessKey"));
										elfUtil.getAccounts();
									} catch (UnsupportedEncodingException | JSONException e) {
										e.printStackTrace();
									}
								}
								
								if (elfUtil != null) {
									retObj = elfUtil.orderUsdt(typeCode, "usdt", direction, amount, price);
								}
							} else {
								if (apiUtil == null) {
									try {
										apiUtil = new HuobiApiUtil(UrlConfig.getValue("DeliveryHuobiSecretKey"), UrlConfig.getValue("DeliveryHuobiAccessKey"));
										apiUtil.getAccounts();
									} catch (UnsupportedEncodingException | JSONException e) {
										e.printStackTrace();
									}
								}
								
								retObj = apiUtil.orderUsdt(typeCode, "usdt", direction, amount, price);
							}
						} else if (!setting.getF_code().equals("ft")) {
							if (okecUtil == null) {
								okecUtil = new OKexApiUtil(UrlConfig.getValue("DeliveryOkexSecretKey"), UrlConfig.getValue("DeliveryOkexAccessKey"), UrlConfig.getValue("DeliveryOkexPwd"));
							}
							if (okecUtil != null) {
								retObj = okecUtil.orderUsdt(typeCode, "usdt", direction, amount, price);
							}
						}
					} catch (Exception e) {
						error = e.getMessage();
						e.printStackTrace();
					}
					
					if (retObj == null) {
						bqHedgingRecordService.updateState(record.getId(), 2, price, "下单失败: " + error);
					} else {
						try {
							if ("ok".equals(retObj.getString("status"))) {
								bqHedgingRecordService.updateState(record.getId(), 1, price, "订单号: " + retObj.getString("orderId"));
							} else if ("retry".equals(retObj.getString("status"))) {
								bqHedgingRecordService.updateSendTimesAndNote(record.getId(), price, "下单失败: " + retObj.getString("error"));
								DeliveryHedgingRecord rd = bqHedgingRecordService.getRecordById(record.getId());
								if (rd.getSend_times() >= 5) {
									bqHedgingRecordService.updateState(record.getId(), 2, price, record.getNote() + "，下单失败，尝试次数达到5次！！ ");
								}
							} else {
								bqHedgingRecordService.updateState(record.getId(), 2, price, "下单失败: " + retObj.getString("error"));
							}
						} catch (JSONException e) {
							bqHedgingRecordService.updateState(record.getId(), 2, price, "下单失败: " + e.getMessage());
						}
					}
					
				}
			}
			
			if (setting.getAuto_trade()) {
				
				Integer digits = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.D_DIGITS);
				if (digits == null) {
					List<DeliveryTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANS_TYPES);
					if (transTypes == null) {
						transTypes = transTypeService.findAllUsing();
						spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANS_TYPES, BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
					}
					
					DeliveryTransactionType transType = null;
					for (DeliveryTransactionType type : transTypes) {
						if (type.getType_code().equals(transaction.getTransaction_type_code())) {
							transType = type;
						}
					}
					
					if (transType == null) continue;
					
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(transaction.getTransaction_code() + BTCQMemecacheKeys.D_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
				
				// 多仓
				List<DeliveryMemberTransaction> buyTransList = bqMemberTransactionService.getBQMemberTrasaction(setting.getDepth_uid(), setting.getF_code(), 1);
				if (buyTransList.size() == 0) continue;
				// 空仓
				List<DeliveryMemberTransaction> sellTransList = bqMemberTransactionService.getBQMemberTrasaction(setting.getDepth_uid(), setting.getF_code(), 2);
				if (sellTransList.size() == 0) continue;
				
				DeliveryMemberTransaction buyTrans = buyTransList.get(0);
				DeliveryMemberTransaction sellTrans = sellTransList.get(0);
				
				
				if (buyTrans.getHold_count().compareTo(BigDecimal.ZERO) > 0 
						&& sellTrans.getHold_count().compareTo(BigDecimal.ZERO) > 0) {
					
					DeliveryPendingDelegation sell1 = pendingDelegationService.getSell1(setting.getF_code());
					DeliveryPendingDelegation buy1 = pendingDelegationService.getBuy1(setting.getF_code());
					if (sell1 == null || buy1 == null) continue;
					
					BigDecimal btc_index_price = spyMemcachedClient.get(setting.getF_code());
					if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}
					
					if (setting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0
							&& sell1.getDelegation_price().subtract(buy1.getDelegation_price()).compareTo(setting.getScalping_depth_range().multiply(buy1.getDelegation_price())) > 0) {
						continue;
					}
					
					BigDecimal range = new BigDecimal(0.01);
					if (setting.getScalping_index_range().compareTo(BigDecimal.ZERO) > 0) {
						range = setting.getScalping_index_range();
					}
					
					BigDecimal minChange = new BigDecimal(1.0 / Math.pow(10, digits));
					minChange = minChange.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
					if (sell1.getDelegation_price().subtract(buy1.getDelegation_price()).abs().compareTo(minChange) <= 0) {
						continue;
					}
					
					BigDecimal highPrice = btc_index_price.multiply(BigDecimal.ONE.add(range));
					BigDecimal lowPrice = btc_index_price.multiply(BigDecimal.ONE.subtract(range));
					
					if (sell1.getDelegation_price().compareTo(highPrice) <= 0) {
						highPrice = sell1.getDelegation_price().subtract(minChange);
					}
					
					if (buy1.getDelegation_price().compareTo(lowPrice) >= 0) {
						lowPrice = buy1.getDelegation_price().add(minChange);;
					}
					
					if (highPrice.compareTo(lowPrice) < 0) {
						continue;
					}
					
					boolean buyLock = bqMemberTransactionService.lockToTrade(buyTrans.getMt_id());
					if (buyLock) {
						try {
							boolean sellLock = bqMemberTransactionService.lockToTrade(sellTrans.getMt_id());
							if (sellLock) {
								try {
									buyTrans = bqMemberTransactionService.getMemberTransactionById(buyTrans.getMt_id());
									sellTrans = bqMemberTransactionService.getMemberTransactionById(sellTrans.getMt_id());
									
									if (buyTrans == null || sellTrans == null 
											|| buyTrans.getHold_count().compareTo(BigDecimal.ZERO) == 0
											|| sellTrans.getHold_count().compareTo(BigDecimal.ZERO) == 0) {
										continue;
									}
									
									BigDecimal price = lowPrice.add(new BigDecimal(Math.random()).multiply(highPrice.subtract(lowPrice))) ;
									price = price.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
									BigDecimal amount = buyTrans.getHold_count().compareTo(sellTrans.getHold_count()) > 0 ? sellTrans.getHold_count() : buyTrans.getHold_count();
									
									// 相当于平多仓
									BigDecimal deposit = amount.divide(buyTrans.getHold_count().add(buyTrans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
											.multiply(buyTrans.getDeposit());
									DeliveryPendingDelegationCache sellcache = new DeliveryPendingDelegationCache(buyTrans.getTransaction_id(), buyTrans.getTransaction_code(),Contents.DIRECTION_SELL, 
											amount, price, new BigDecimal(0), amount, buyTrans.getTrade_multiple().intValue(), 
											deposit, BigDecimal.ZERO, Contents.PRIORITY_PC, setting.getDepth_uid(), BigDecimal.ZERO, BigDecimal.ZERO);
									sellcache.setFlag(1);
									
									int flag = bqMemberTransactionService.updateMemberTransByID(buyTrans.getMt_id(), amount.negate(), amount, 
											BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
									if (flag > 0) {
										DeliveryPendingLog pendingLog = new DeliveryPendingLog(sellcache.getDelegation_id(), sellcache.getDelegation_time(), sellcache.getTransaction_id(),
												sellcache.getTransaction_code(), Contents.CZLX_DDPC, sellcache.getDelegation_count(), sellcache.getDelegation_price(), sellcache.getMultiple(), 
												sellcache.getDeal_count(), sellcache.getUndeal_count(), sellcache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, sellcache.getF_user_id(), 
												sellcache.getPending_no(), Contents.DEVICE_SERVER_DEPTH, "");
										pendingLogService.savePendingLog(pendingLog);
										
										BigDecimal deposit2 = amount.divide(sellTrans.getHold_count().add(sellTrans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
												.multiply(sellTrans.getDeposit());
										
										DeliveryPendingDelegation buy = new DeliveryPendingDelegation();
										buy.setTransaction_id(sellTrans.getTransaction_id());
										buy.setTransaction_code(sellTrans.getTransaction_code());
										buy.setDirection(Contents.DIRECTION_BUY);
										buy.setDelegation_count(amount);
										buy.setDelegation_price(price);
										buy.setDeal_count(BigDecimal.ZERO);
										buy.setUndeal_count(amount);
										buy.setMultiple(sellTrans.getTrade_multiple().intValue());
										buy.setDeposit(deposit2);
										buy.setPriority(Contents.PRIORITY_PC);
										buy.setF_user_id(setting.getDepth_uid());
										buy.setF_fromremain(BigDecimal.ZERO);
										buy.setF_enforce_price(BigDecimal.ZERO);
										buy.setPending_no(RandomUtil.getPendingNo());
										buy.setCommission_rate(BigDecimal.ZERO);
										buy.setFlag(1);
										
										boolean success = false;
										int flag2 = bqMemberTransactionService.updateMemberTransByID(sellTrans.getMt_id(), amount.negate(), amount, 
												BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
										if (flag2 > 0) {
											DeliveryPendingLog pendingLog2 = new DeliveryPendingLog(buy.getDelegation_id(), buy.getDelegation_time(), buy.getTransaction_id(),
													buy.getTransaction_code(), Contents.CZLX_KTPC, buy.getDelegation_count(), buy.getDelegation_price(), buy.getMultiple(), 
													buy.getDeal_count(), buy.getUndeal_count(), buy.getDeposit(), Contents.PENDING_LOG_ACTION_CS, buy.getF_user_id(), 
													buy.getPending_no(), Contents.DEVICE_SERVER_DEPTH, "");
											pendingLogService.savePendingLog(pendingLog2);
											
											try {
												openPositionServer.eatBuy1(setting.getF_code(), buy, sellcache, transaction.getTrade_coefficient(), true);
												success = true;
												
												if (buyTrans.getHold_count().compareTo(sellTrans.getHold_count()) <= 0) {
													bqMemberTransactionService.deleteZEROTran(buyTrans);
												}
												
											} catch (Exception e) {
												logger.error("深度用户对平错误，挂单量：" + sellcache.getUndeal_count() + "，多单持仓量：" + buyTrans.getHold_count() + "，空头持仓量：" + sellTrans.getHold_count());
											}
											
											if (!success) {
												pendingLogService.updateActionByPendingNo(buy.getTransaction_code(), buy.getPending_no(), buy.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
												
												// 需要恢复的持仓
												if (bqMemberTransactionService.updateMemberTransByID(sellTrans.getMt_id(), amount, amount.negate(), BigDecimal.ZERO,
														BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
													logger.error("深度用户对平撤单错误：用户" + buy.getF_user_id() + "的挂单量：" + buy.getUndeal_count() + "，持单量：" + sellTrans.getLock_count());
												}
											}
										} 
										
										if (!success) {
											// 撤单
											pendingLogService.updateActionByPendingNo(sellcache.getTransaction_code(), sellcache.getPending_no(), sellcache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
											
											// 需要恢复的持仓
											if (bqMemberTransactionService.updateMemberTransByID(buyTrans.getMt_id(), amount, amount.negate(), BigDecimal.ZERO,
													BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
												logger.error("深度用户对平撤单错误：用户" + buyTrans.getF_user_id() + "的挂单量：" + sellcache.getUndeal_count() + "，持单量：" + buyTrans.getLock_count());
											}
										} else {
											spyMemcachedClient.set(transaction.getTransaction_code() + BTCQMemecacheKeys.D_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, price);
										}
									}
								} catch (Exception e) {
									
								} finally {
									if (sellTrans != null) {
										bqMemberTransactionService.unlockToTrade(sellTrans.getMt_id());
									}
								}
							}
						} catch (Exception e) {
							
						} finally {
							if (buyTrans != null) {
								bqMemberTransactionService.unlockToTrade(buyTrans.getMt_id());
							}
						}
					}
				}
				
			}
		}
	}
}
