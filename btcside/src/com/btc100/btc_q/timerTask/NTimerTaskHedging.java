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
import com.btc100.btc_q.entity.NArbitrageSetting;
import com.btc100.btc_q.entity.NHedgingRecord;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.entity.NPendingLog;
import com.btc100.btc_q.service.NArbitrageSettingService;
import com.btc100.btc_q.service.NHedgingRecordService;
import com.btc100.btc_q.service.NMemberTransactionService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.service.NOpenPositionServer;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.service.NPendingLogService;
import com.btc100.btc_q.util.FCoinApiUtil;
import com.btc100.btc_q.util.HuobiApiUtil;
import com.btc100.btc_q.util.OKexApiUtil;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

public class NTimerTaskHedging {

	@Autowired
	private NHedgingRecordService bqHedgingRecordService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NTransactionService bqTransactionService;
	@Autowired
	private NArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private NTransactionTypeService transTypeService;
	@Autowired
	private NMemberTransactionService bqMemberTransactionService; 
	@Autowired
	private NPendingDelegationService pendingDelegationService;
	@Autowired
	private NPendingLogService pendingLogService;
	@Autowired
	private NOpenPositionServer openPositionServer;
	
	Logger logger = Logger.getLogger(NTimerTaskHedging.class);
	
	private static HuobiApiUtil apiUtil = null; 
	private static HuobiApiUtil elfUtil = null; 
	private static OKexApiUtil okecUtil = null; 
	private static FCoinApiUtil fCoinUtil = null; 
	private static String[] contractArray = null;
	
	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.ncontracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
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
			
			NArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + partition + "_" + trans.getTransaction_code());
			if (setting == null) {
				setting = bqArbitrageSettingService.getArbitrageSettingByCode(partition, trans.getTransaction_code());
				if (setting == null) {
					setting = new NArbitrageSetting();
					setting.setRunning(false);
					setting.setScalping(false);
					setting.setHedging(false);
					setting.setRisk_manage(false);
					setting.setAuto_trade(false);
					setting.setDepth_uid(0L);
				}
				
				spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + partition + "_" + trans.getTransaction_code(), BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
			
			if (setting.getHedging()) {
				Integer digits = spyMemcachedClient.get(partition + "_" + trans.getTransaction_type_code() + BTCQMemecacheKeys.DIGITS);
				if (digits == null) {
					List<NTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition);
					if (transTypes == null) {
						transTypes = transTypeService.findAllUsing(partition);
						spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition, BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
					}
					
					NTransactionType transType = null;
					for (NTransactionType type : transTypes) {
						if (type.getType_code().equals(trans.getTransaction_type_code())) {
							transType = type;
						}
					}
					
					if (transType == null) continue;
					
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(partition + "_" + transType.getType_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
				
				// 外部对冲
				List<NHedgingRecord> records = bqHedgingRecordService.findRecordsByCodeAndState(partition, trans.getTransaction_code(), 0);
				for (NHedgingRecord record : records) {
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
									fCoinUtil = new FCoinApiUtil(UrlConfig.getValue("FCoinSecretKey"), UrlConfig.getValue("FCoinAccessKey"));
								}
								
								retObj = fCoinUtil.orderUsdt(record.getTransaction_code(), direction, amount, price);
							} else if (setting.getF_code().equals("elf")) {
								if (elfUtil == null) {
									try {
										elfUtil = new HuobiApiUtil(UrlConfig.getValue("ELFSecretKey"), UrlConfig.getValue("ELFAccessKey"));
										elfUtil.getAccounts();
									} catch (UnsupportedEncodingException | JSONException e) {
										e.printStackTrace();
									}
								}
								
								if (elfUtil != null) {
									retObj = elfUtil.orderUsdt(record.getTransaction_code(), partition, direction, amount, price);
								}
							} else {
								if (apiUtil == null) {
									try {
										apiUtil = new HuobiApiUtil(UrlConfig.getValue("HuobiSecretKey"), UrlConfig.getValue("HuobiAccessKey"));
										apiUtil.getAccounts();
									} catch (UnsupportedEncodingException | JSONException e) {
										e.printStackTrace();
									}
								}
								
								retObj = apiUtil.orderUsdt(record.getTransaction_code(), partition, direction, amount, price);
							}
						} else if (!setting.getF_code().equals("ft")) {
							if (okecUtil == null) {
								okecUtil = new OKexApiUtil(UrlConfig.getValue("OkexSecretKey"), UrlConfig.getValue("OkexAccessKey"), UrlConfig.getValue("OkexPwd"));
							}
							if (okecUtil != null) {
								retObj = okecUtil.orderUsdt(record.getTransaction_code(), partition, direction, amount, price);
							}
						}
					} catch (Exception e) {
						error = e.getMessage();
						e.printStackTrace();
					}
					
					if (retObj == null) {
						bqHedgingRecordService.updateState(partition, record.getId(), 2, price, "下单失败: " + error);
					} else {
						try {
							if ("ok".equals(retObj.getString("status"))) {
								bqHedgingRecordService.updateState(partition, record.getId(), 1, price, "订单号: " + retObj.getString("orderId"));
							} else if ("retry".equals(retObj.getString("status"))) {
								bqHedgingRecordService.updateSendTimesAndNote(partition, record.getId(), price, "下单失败: " + retObj.getString("error"));
								NHedgingRecord rd = bqHedgingRecordService.getRecordById(partition, record.getId());
								if (rd.getSend_times() >= 5) {
									bqHedgingRecordService.updateState(partition, record.getId(), 2, price, record.getNote() + "，下单失败，尝试次数达到5次！！ ");
								}
							} else {
								bqHedgingRecordService.updateState(partition, record.getId(), 2, price, "下单失败: " + retObj.getString("error"));
							}
						} catch (JSONException e) {
							bqHedgingRecordService.updateState(partition, record.getId(), 2, price, "下单失败: " + e.getMessage());
						}
					}
					
				}
			}
			
			if (setting.getAuto_trade()) {
				
				Integer digits = spyMemcachedClient.get(partition + "_" + trans.getTransaction_type_code() + BTCQMemecacheKeys.DIGITS);
				if (digits == null) {
					List<NTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition);
					if (transTypes == null) {
						transTypes = transTypeService.findAllUsing(partition);
						spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition, BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
					}
					
					NTransactionType transType = null;
					for (NTransactionType type : transTypes) {
						if (type.getType_code().equals(trans.getTransaction_type_code())) {
							transType = type;
						}
					}
					
					if (transType == null) continue;
					
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(partition + "_" + transType.getType_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
				
				// 多仓
				List<NMemberTransaction> buyTransList = bqMemberTransactionService.getMemberTrasaction(partition, setting.getDepth_uid(), setting.getF_code(), 1);
				if (buyTransList.size() == 0) continue;
				// 空仓
				List<NMemberTransaction> sellTransList = bqMemberTransactionService.getMemberTrasaction(partition, setting.getDepth_uid(), setting.getF_code(), 2);
				if (sellTransList.size() == 0) continue;
				
				NMemberTransaction buyTrans = buyTransList.get(0);
				NMemberTransaction sellTrans = sellTransList.get(0);
				
				if (buyTrans.getHold_count().compareTo(BigDecimal.ZERO) > 0 
						&& sellTrans.getHold_count().compareTo(BigDecimal.ZERO) > 0) {
					
					NPendingDelegation sell1 = pendingDelegationService.getSell1(partition, setting.getF_code());
					NPendingDelegation buy1 = pendingDelegationService.getBuy1(partition, setting.getF_code());
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
					
					boolean buyLock = bqMemberTransactionService.lockToTrade(partition, buyTrans.getMt_id());
					if (buyLock) {
						try {
							boolean sellLock = bqMemberTransactionService.lockToTrade(partition, sellTrans.getMt_id());
							if (sellLock) {
								try {
									buyTrans = bqMemberTransactionService.getMemberTransactionById(partition, buyTrans.getMt_id());
									sellTrans = bqMemberTransactionService.getMemberTransactionById(partition, sellTrans.getMt_id());
									
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
									NPendingDelegationCache sellcache = new NPendingDelegationCache(buyTrans.getTransaction_id(), buyTrans.getTransaction_code(),Contents.DIRECTION_SELL, 
											amount, price, new BigDecimal(0), amount, buyTrans.getTrade_multiple().intValue(), 
											deposit, BigDecimal.ZERO, Contents.PRIORITY_PC, setting.getDepth_uid(), BigDecimal.ZERO, BigDecimal.ZERO);
									sellcache.setFlag(1);
									
									int flag = bqMemberTransactionService.updateMemberTransByID(partition, buyTrans.getMt_id(), amount.negate(), amount, 
											BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
									if (flag > 0) {
										NPendingLog pendingLog = new NPendingLog(sellcache.getDelegation_id(), sellcache.getDelegation_time(), sellcache.getTransaction_id(),
												sellcache.getTransaction_code(), Contents.CZLX_DDPC, sellcache.getDelegation_count(), sellcache.getDelegation_price(), sellcache.getMultiple(), 
												sellcache.getDeal_count(), sellcache.getUndeal_count(), sellcache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, sellcache.getF_user_id(), 
												sellcache.getPending_no(), Contents.DEVICE_SERVER_DEPTH, "");
										pendingLogService.savePendingLog(partition, pendingLog);
										
										BigDecimal deposit2 = amount.divide(sellTrans.getHold_count().add(sellTrans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
												.multiply(sellTrans.getDeposit());
										
										NPendingDelegation buy = new NPendingDelegation();
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
										int flag2 = bqMemberTransactionService.updateMemberTransByID(partition, sellTrans.getMt_id(), amount.negate(), amount, 
												BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
										if (flag2 > 0) {
											NPendingLog pendingLog2 = new NPendingLog(buy.getDelegation_id(), buy.getDelegation_time(), buy.getTransaction_id(),
													buy.getTransaction_code(), Contents.CZLX_KTPC, buy.getDelegation_count(), buy.getDelegation_price(), buy.getMultiple(), 
													buy.getDeal_count(), buy.getUndeal_count(), buy.getDeposit(), Contents.PENDING_LOG_ACTION_CS, buy.getF_user_id(), 
													buy.getPending_no(), Contents.DEVICE_SERVER_DEPTH, "");
											pendingLogService.savePendingLog(partition, pendingLog2);
											
											try {
												openPositionServer.eatBuy1(trans.getCurrency_type_id(), partition, setting.getF_code(), buy, sellcache, trans.getTrade_coefficient(), true);
												success = true;
												
												if (buyTrans.getHold_count().compareTo(sellTrans.getHold_count()) <= 0) {
													bqMemberTransactionService.deleteZEROTran(partition, buyTrans);
												}
												
											} catch (Exception e) {
												logger.error("深度用户对平错误，挂单量：" + sellcache.getUndeal_count() + "，多单持仓量：" + buyTrans.getHold_count() + "，空头持仓量：" + sellTrans.getHold_count());
											}
											
											if (!success) {
												pendingLogService.updateActionByPendingNo(partition, buy.getTransaction_code(), buy.getPending_no(), buy.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
												
												// 需要恢复的持仓
												if (bqMemberTransactionService.updateMemberTransByID(partition, sellTrans.getMt_id(), amount, amount.negate(), BigDecimal.ZERO,
														BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
													logger.error("深度用户对平撤单错误：用户" + buy.getF_user_id() + "的挂单量：" + buy.getUndeal_count() + "，持单量：" + sellTrans.getLock_count());
												}
											}
										} 
										
										if (!success) {
											// 撤单
											pendingLogService.updateActionByPendingNo(partition, sellcache.getTransaction_code(), sellcache.getPending_no(), sellcache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
											
											// 需要恢复的持仓
											if (bqMemberTransactionService.updateMemberTransByID(partition, buyTrans.getMt_id(), amount, amount.negate(), BigDecimal.ZERO,
													BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
												logger.error("深度用户对平撤单错误：用户" + buyTrans.getF_user_id() + "的挂单量：" + sellcache.getUndeal_count() + "，持单量：" + buyTrans.getLock_count());
											}
										} else {
											spyMemcachedClient.set(partition + "_" + contract + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, price);
										}
									}
								} catch (Exception e) {
									
								} finally {
									if (sellTrans != null) {
										bqMemberTransactionService.unlockToTrade(partition, sellTrans.getMt_id());
									}
								}
							}
						} catch (Exception e) {
							
						} finally {
							if (buyTrans != null) {
								bqMemberTransactionService.unlockToTrade(partition, buyTrans.getMt_id());
							}
						}
					}
				}
				
			}
		}
	}
}
