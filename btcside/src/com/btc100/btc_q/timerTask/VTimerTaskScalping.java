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
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.entity.VArbitrageSetting;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VMemberTradeDetail;
import com.btc100.btc_q.entity.VPendingDelegation;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.entity.VPendingLog;
import com.btc100.btc_q.entity.VTradeLog;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.VArbitrageSettingService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.service.VMemberTradeDetailService;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VOpenPositionServer;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.service.VPendingLogService;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.Utils;

public class VTimerTaskScalping {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VArbitrageSettingService vArbitrageSettingService;
	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	private VTradeLogService vTradeLogService;
	@Autowired
	private VMemberTradeDetailService vMemberTradeDetailService;
	@Autowired
	private VTransactionTypeService vTransTypeService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private VPendingLogService vPendingLogService;
	@Autowired
	private VOpenPositionServer vOpenPositionServer;
	
	Logger logger = Logger.getLogger(VTimerTaskScalping.class);
	
	private static Map<String, Integer> countDown = new HashMap<String, Integer>();
	private static Map<String, Long> transIdMap = new HashMap<String, Long>();
	
	private static int timeCount = 0;
	
	public void run() {
		
		List<VTransaction> list = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if(list == null) {
			list = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, list);
		}
		
		for(VTransaction trans :list) {
			if (!countDown.containsKey(trans.getTransaction_code())) {
				countDown.put(trans.getTransaction_code(), 0);
				transIdMap.put(trans.getTransaction_code(), trans.getTransaction_id());
			}
		}
		
		for (String code : countDown.keySet()) {
			int value = countDown.get(code);
			if (value == 0) {
				long codeId = transIdMap.get(code);
				int sleepTime = 1000;
				
				VTransaction vTransaction = null;
				for(VTransaction trans : list) {
					if (trans.getTransaction_code().equals(code)) {
						vTransaction = trans;
						break;
					}
				}
				
				if (vTransaction == null) continue;
				
				if (vTransaction.getSuspend()) continue;
				
				do {
					VArbitrageSetting setting = null;
					try {
						setting = spyMemcachedClient.get(BTCQMemecacheKeys.V_ARBITRAGE_SETTING + code);
					} catch (Exception e) {}
					
					if (setting == null) {
						setting = vArbitrageSettingService.getArbitrageSettingByCode(code);
						if (setting == null) {
							setting = new VArbitrageSetting();
							setting.setRunning(false);
							setting.setScalping(false);
							setting.setHedging(false);
							setting.setRisk_manage(false);
							setting.setDepth_uid(0L);
							setting.setAuto_trade(false);
						}
						
						spyMemcachedClient.set(BTCQMemecacheKeys.V_ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.ONE_SECOND, setting);
					}
					
					if (setting == null || !setting.getScalping()) break;
					
					VPendingDelegation sell1 = vPendingDelegationService.getSell1(code);
					VPendingDelegation buy1 = vPendingDelegationService.getBuy1(code);
					
					if (sell1 == null || buy1 == null) break;
					
					if (setting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0
							&& sell1.getDelegation_price().subtract(buy1.getDelegation_price()).compareTo(setting.getScalping_depth_range().multiply(buy1.getDelegation_price())) > 0) {
						break;
					}
					
					Integer digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.V_DIGITS);
					if (digits == null) {
						List<VTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANS_TYPES);
						if (transTypes == null) {
							transTypes = vTransTypeService.findAllUsing();
							spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
						}
						
						VTransactionType transType = null;
						for (VTransactionType type : transTypes) {
							if (type.getType_code().equals(code)) {
								transType = type;
							}
						}
						
						if (transType == null) continue;
						
						digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
						spyMemcachedClient.set(transType.getType_code() + BTCQMemecacheKeys.V_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
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
					
					BigDecimal indexPrice = spyMemcachedClient.get(code);
					if (indexPrice == null || indexPrice.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}

					BigDecimal range = new BigDecimal(0.01);
					if (setting.getScalping_index_range().compareTo(BigDecimal.ZERO) > 0) {
						range = setting.getScalping_index_range();
					}
					
					BigDecimal high = indexPrice.multiply(BigDecimal.ONE.add(range));
					BigDecimal low = indexPrice.multiply(BigDecimal.ONE.subtract(range));
					
					if (sell1.getDelegation_price().compareTo(high) <= 0) {
						high = sell1.getDelegation_price().subtract(minChange);;
					}
					
					if (buy1.getDelegation_price().compareTo(low) >= 0) {
						low = buy1.getDelegation_price().add(minChange);;
					}
					
					if (high.compareTo(low) < 0) {
						continue;
					}
					
					BigDecimal price = low.add( new BigDecimal(Math.random()).multiply(high.subtract(low)));
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
					
					BigDecimal deposit = BTCQMath.getDeposit(amount, price, 20, BigDecimal.ONE);
					
					int typeId = (Math.random() >= 0.5) ? 2 : 3;
					VTradeLog sellVTradeLog = new VTradeLog(codeId, code, typeId, 2, BigDecimal.ZERO, price, amount, 20, deposit, 
							BigDecimal.ZERO, BigDecimal.ZERO, Contents.BQMEMBER_ADMIN, 0);
					vTradeLogService.saveVTradeLog(sellVTradeLog);
					
					typeId = (Math.random() >= 0.5) ? 1 : 4;
					VTradeLog buyVTradeLog = new VTradeLog(codeId, code, typeId, 1, BigDecimal.ZERO, price, amount, 20, deposit, 
							BigDecimal.ZERO, BigDecimal.ZERO, Contents.BQMEMBER_ADMIN, 0);
					vTradeLogService.saveVTradeLog(buyVTradeLog);
					
					VMemberTradeDetail tradeDetail = new VMemberTradeDetail(buyVTradeLog.getTrade_id(), Contents.BQMEMBER_ADMIN, "", Contents.BQMEMBER_ADMIN, "", 
							sellVTradeLog.getTrade_id(), buyVTradeLog.getTrade_type_id(), sellVTradeLog.getTrade_type_id(), 1, isSell ? 2 : 1, price, amount, codeId, code);
					vMemberTradeDetailService.save(tradeDetail);
					
					BigDecimal lastPrice = price;
					VTransaction bqt_temp = vTransactionService.findTransToUpdateByCode(code);
					BigDecimal highPrice = bqt_temp.getTransaction_highest();
					bqt_temp.setLatest_transaction_price(lastPrice);
					if (lastPrice.compareTo(highPrice) > 0) {
						bqt_temp.setTransaction_highest(lastPrice);
					}
					BigDecimal lowPrice = bqt_temp.getTransaction_lowest();
					if (lastPrice.compareTo(lowPrice) < 0 || lowPrice.signum() <= 0) {
						bqt_temp.setTransaction_lowest(lastPrice);
					}
					vTransactionService.update(bqt_temp);
					spyMemcachedClient.set(code + BTCQMemecacheKeys.V_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
					
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
				VTransaction bqTransaction = null;
				for(VTransaction trans :list) {
					if (trans.getTransaction_code().equals(code)) {
						bqTransaction = trans;
						break;
					}
				}
				
				if (bqTransaction == null) continue;
				
				long yesterday = System.currentTimeMillis() / 1000 - 3600*24;
				vTradeLogService.deleteVTradeScalpingLog(code, Contents.BQMEMBER_ADMIN, yesterday);
				vMemberTradeDetailService.deleteVTradeScalpingDetail(code, Contents.BQMEMBER_ADMIN, yesterday);
			}
		}
		
		timeCount++;
		
		// 深度用户自动对平
		for(VTransaction trans :list) {
			VArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.V_ARBITRAGE_SETTING + trans.getTransaction_code());
			if (setting == null) {
				setting = vArbitrageSettingService.getArbitrageSettingByCode(trans.getTransaction_code());
				if (setting != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.V_ARBITRAGE_SETTING + trans.getTransaction_code(), BTCQMemecacheKeys.TIMES.ONE_SECOND, setting);
				}
			}
			
			if (setting == null) continue;
			
			Integer digits = spyMemcachedClient.get(trans.getTransaction_type_code() + BTCQMemecacheKeys.V_DIGITS);
			if (digits == null) {
				List<VTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANS_TYPES);
				if (transTypes == null) {
					transTypes = vTransTypeService.findAllUsing();
					spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANS_TYPES, BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
				}
				
				VTransactionType transType = null;
				for (VTransactionType type : transTypes) {
					if (type.getType_code().equals(trans.getTransaction_type_code())) {
						transType = type;
					}
				}
				
				if (transType == null) continue;
				
				digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
				spyMemcachedClient.set(transType.getType_code() + BTCQMemecacheKeys.V_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
			}
			
			if (setting.getAuto_trade()) {
				// 多仓
				List<VMemberTransaction> buyTransList = vMemberTransactionService.getVMemberTrasaction(setting.getDepth_uid(), setting.getF_code(), 1);
				if (buyTransList.size() == 0) continue;
				// 空仓
				List<VMemberTransaction> sellTransList = vMemberTransactionService.getVMemberTrasaction(setting.getDepth_uid(), setting.getF_code(), 2);
				if (sellTransList.size() == 0) continue;
				
				BQMember user = bqMemberService.findUserById(setting.getDepth_uid());
				if (user == null) continue;
				BQMemberLevel userLevel = bqMemberLevelService.findById(user.getF_user_level_id());
				
				VMemberTransaction buyTrans = buyTransList.get(0);
				VMemberTransaction sellTrans = sellTransList.get(0);
				
				if (buyTrans.getHold_count().compareTo(BigDecimal.ZERO) > 0 
						&& sellTrans.getHold_count().compareTo(BigDecimal.ZERO) > 0) {
					
					VPendingDelegation sell1 = vPendingDelegationService.getSell1(setting.getF_code());
					VPendingDelegation buy1 = vPendingDelegationService.getBuy1(setting.getF_code());
					if (sell1 == null || buy1 == null) continue;
					
					if (setting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0
							&& sell1.getDelegation_price().subtract(buy1.getDelegation_price()).compareTo(setting.getScalping_depth_range().multiply(buy1.getDelegation_price())) > 0) {
						continue;
					}
					
					BigDecimal btc_index_price = spyMemcachedClient.get(setting.getF_code());
					if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
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
					
					boolean buyLock = vMemberTransactionService.lockToTrade(buyTrans.getMt_id());
					if (buyLock) {
						try {
							boolean sellLock = vMemberTransactionService.lockToTrade(sellTrans.getMt_id());
							if (sellLock) {
								try {
									buyTrans = vMemberTransactionService.getMemberTransactionById(buyTrans.getMt_id());
									sellTrans = vMemberTransactionService.getMemberTransactionById(sellTrans.getMt_id());
									
									if (buyTrans == null || sellTrans == null 
											|| buyTrans.getHold_count().compareTo(BigDecimal.ZERO) == 0
											|| sellTrans.getHold_count().compareTo(BigDecimal.ZERO) == 0) {
										continue;
									}
									
									BigDecimal price = lowPrice.add( new BigDecimal(Math.random()).multiply(highPrice.subtract(lowPrice))) ;
									price = price.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
									BigDecimal amount = buyTrans.getHold_count().compareTo(sellTrans.getHold_count()) > 0 ? sellTrans.getHold_count() : buyTrans.getHold_count();
									
									// 相当于平多仓
									BigDecimal deposit = amount.divide(buyTrans.getHold_count().add(buyTrans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
											.multiply(buyTrans.getDeposit());
									VPendingDelegationCache sellcache = new VPendingDelegationCache(buyTrans.getTransaction_id(), buyTrans.getTransaction_code(),Contents.DIRECTION_SELL, 
											amount, price, new BigDecimal(0), amount, buyTrans.getTrade_multiple().intValue(), 
											deposit, userLevel.getEat_commission_rate(), Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);
									sellcache.setFlag(1);
									
									int flag = vMemberTransactionService.updateMemberTransByID(buyTrans.getMt_id(), amount.negate(), amount, 
											BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
									if (flag > 0) {
										VPendingLog pendingLog = new VPendingLog(sellcache.getDelegation_id(), sellcache.getDelegation_time(), sellcache.getTransaction_id(),
												sellcache.getTransaction_code(), Contents.CZLX_DDPC, sellcache.getDelegation_count(), sellcache.getDelegation_price(), sellcache.getMultiple(), 
												sellcache.getDeal_count(), sellcache.getUndeal_count(), sellcache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, sellcache.getF_user_id(), 
												sellcache.getPending_no(), Contents.DEVICE_SERVER_DEPTH, "");
										vPendingLogService.savePendingLog(pendingLog);
										
										BigDecimal deposit2 = amount.divide(sellTrans.getHold_count().add(sellTrans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
												.multiply(sellTrans.getDeposit());
										
										VPendingDelegation buy = new VPendingDelegation();
										buy.setTransaction_id(sellTrans.getTransaction_id());
										buy.setTransaction_code(sellTrans.getTransaction_code());
										buy.setDirection(Contents.DIRECTION_BUY);
										buy.setDelegation_count(amount);
										buy.setDelegation_price(price);
										buy.setDeal_count(BigDecimal.ZERO);
										buy.setUndeal_count(amount);
										buy.setMultiple(sellTrans.getTrade_multiple().intValue());
										buy.setDeposit(deposit2);
										buy.setCommission_rate(userLevel.getPending_commission_rate());
										buy.setPriority(Contents.PRIORITY_PC);
										buy.setF_user_id(user.getF_user_id());
										buy.setF_fromremain(BigDecimal.ZERO);
										buy.setF_enforce_price(BigDecimal.ZERO);
										buy.setPending_no(RandomUtil.getPendingNo());
										buy.setFlag(1);
										
										boolean success = false;
										int flag2 = vMemberTransactionService.updateMemberTransByID(sellTrans.getMt_id(), amount.negate(), amount, 
												BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
										if (flag2 > 0) {
											VPendingLog pendingLog2 = new VPendingLog(buy.getDelegation_id(), buy.getDelegation_time(), buy.getTransaction_id(),
													buy.getTransaction_code(), Contents.CZLX_KTPC, buy.getDelegation_count(), buy.getDelegation_price(), buy.getMultiple(), 
													buy.getDeal_count(), buy.getUndeal_count(), buy.getDeposit(), Contents.PENDING_LOG_ACTION_CS, buy.getF_user_id(), 
													buy.getPending_no(), Contents.DEVICE_SERVER_DEPTH, "");
											vPendingLogService.savePendingLog(pendingLog2);
											
											try {
												vOpenPositionServer.eatBuy1(setting.getF_code(), trans.getCurrency_type_id(), buy, sellcache, true);
												success = true;
												
												if (buyTrans.getHold_count().compareTo(sellTrans.getHold_count()) <= 0) {
													vMemberTransactionService.deleteZEROTran(buyTrans.getMt_id());
												}
											} catch (Exception e) {
												logger.error("模拟交易深度用户对平错误，挂单量：" + sellcache.getUndeal_count() + "，多单持仓量：" + buyTrans.getHold_count() + "，空头持仓量：" + sellTrans.getHold_count());
											}
											
											if (!success) {
												vPendingLogService.updateActionByPendingNo(buy.getTransaction_code(), buy.getPending_no(), buy.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
												
												// 需要恢复的持仓
												if (vMemberTransactionService.updateMemberTransByID(sellTrans.getMt_id(), amount, amount.negate(), BigDecimal.ZERO,
														BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
													logger.error("模拟交易深度用户对平撤单错误：用户" + buy.getF_user_id() + "的挂单量：" + buy.getUndeal_count() + "，持单量：" + sellTrans.getLock_count());
												}
											}
										} 
										
										if (!success) {
											// 撤单
											vPendingLogService.updateActionByPendingNo(buy.getTransaction_code(), buy.getPending_no(), buy.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
											
											// 需要恢复的持仓
											if (vMemberTransactionService.updateMemberTransByID(buyTrans.getMt_id(), amount, amount.negate(), BigDecimal.ZERO,
													BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
												logger.error("模拟交易深度用户对平撤单错误：用户" + buyTrans.getF_user_id() + "的挂单量：" + sellcache.getUndeal_count() + "，持单量：" + buyTrans.getLock_count());
											}
										}
									}
								} catch (Exception e) {
									
								} finally {
									if (sellTrans != null) {
										vMemberTransactionService.unlockToTrade(sellTrans.getMt_id());
									}
								}
							}
						} catch (Exception e) {
							
						} finally {
							if (buyTrans != null) {
								vMemberTransactionService.unlockToTrade(buyTrans.getMt_id());
							}
						}
					}
				}
			}
		}
	}
}
