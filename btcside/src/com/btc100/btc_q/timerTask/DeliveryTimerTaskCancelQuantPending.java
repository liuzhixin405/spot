package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.base.BaseTradeAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.DeliveryArbitrageSetting;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryQuantSetting;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.service.DeliveryArbitrageSettingService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryQuantSettingService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.service.DeliveryOpenPositionServer;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

public class DeliveryTimerTaskCancelQuantPending extends BaseTradeAction {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryQuantSettingService deliveryQuantSettingService;
	@Autowired
	private DeliveryPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private DeliveryPendingDelegationService pendingDelegationService;
	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private DeliveryMemberTransactionService memberTransactionService;
	@Autowired
	private DeliveryTransactionTypeService transTypeService;
	@Autowired
	private DeliveryOpenPositionServer openPositionServer;
	@Autowired
	private DeliveryArbitrageSettingService dArbitrageSettingService;
	
	public static String[] contractArray = null;
	
	Logger logger = Logger.getLogger(DeliveryTimerTaskCancelQuantPending.class);
	
	private void doCancel(long uid, String code, BigDecimal coefficient, int priority, int direction, long endTime, int start) {
		int dealCount = 0;
		List<DeliveryMemberTransaction> transList = new ArrayList<DeliveryMemberTransaction>();
		try {
			if (priority == Contents.PRIORITY_PC) {
				transList = memberTransactionService.getTransactionByUserId(code, uid);
				
				boolean locked = true;
				for (DeliveryMemberTransaction trans : transList) {
					if (!memberTransactionService.lockToAdjustSpare(trans.getMt_id())) {
						locked = false;
						break;
					} 
					
					dealCount++;
				}
		
				if (!locked) return;
			}
			
			List<DeliveryPendingDelegationCache> plistCache = pendingDelegationCacheService.findPendingByUserIdAndTime(code, uid, priority, direction, 0, 0, start, 300, false);
			for (DeliveryPendingDelegationCache cache : plistCache) {
				String lock = pendingDelegationCacheService.lock(cache.getTransaction_code(), cache.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						pendingDelegationCacheService.cancelPendingCache_one(cache, coefficient, false);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (cache != null) {
							pendingDelegationCacheService.unLock(cache.getTransaction_code(), cache.getDelegation_id(), lock);
						}
					}
				}
			}
			
			List<DeliveryPendingDelegation> plist = new ArrayList<DeliveryPendingDelegation>();
			if (priority == Contents.PRIORITY_PC) {
				plist = pendingDelegationService.findPendingByUserIdAndTime(code, uid, priority, direction, 0, endTime, 0, 0, false);
			} else {
				plist = pendingDelegationService.findPendingByUserIdAndTime(code, uid, priority, direction, 0, 0, start, 300, true);
			}
			
			for (DeliveryPendingDelegation pd : plist) {
				String lock = pendingDelegationService.lock(pd.getTransaction_code(), pd.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						pendingDelegationService.cancelPending_one(pd, coefficient, false);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pd != null) {
							pendingDelegationService.unLock(pd.getTransaction_code(), pd.getDelegation_id(), lock);
						}
					}
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			for (DeliveryMemberTransaction trans : transList) {
				if (dealCount == 0) {
					break;
				}
				
				if (trans != null) {
					memberTransactionService.unlockToAdjustSpare(trans.getMt_id());
				}
				dealCount--;
			}
		}
		
	}
	
	public void run() {
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("deliveryTrade.contracts");
			contractArray = contracts.split(",");
		}

		if (contractArray == null || contractArray.length == 0)
			return;
		
		DeliveryQuantSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.D_QUANT_SETTING);
		if (setting == null) {
			setting = deliveryQuantSettingService.get();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.D_QUANT_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
		}
		
		if (setting == null) {
			return;
		}
		
		String ids = setting.getUids();
		if (StringUtil.isEmpty(ids)) return;
		
		int timeout = setting.getCancel_timeout();
		if (timeout <= 0) return;
		
		long currTime = pendingDelegationCacheService.getUnix_TIMESTAMP();
		long endTime = currTime - timeout;
		
		String[] users = ids.split(",");
		
		List<DeliveryTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, lstTrads);
		}
		if (lstTrads == null || lstTrads.size() == 0) return;
		
		if (setting.getIs_auto_cancel()) {
			int start = 40;
			if (setting.getKeep_nums() != null && setting.getKeep_nums() > 0) {
				start = setting.getKeep_nums();
			}
			
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
				
				if (transaction.getSuspend() || transaction.getTransaction_state() != 1) {
					continue;
				}
				
				for (String userId : users) {
					Long uid = Convert.toLong(userId, 0);
					if (uid <= 0) continue;
					
//					doCancel(uid, contract, transaction.getTrade_coefficient(), Contents.PRIORITY_KC, endTime, start);
					doCancel(uid, transaction.getTransaction_code(), transaction.getTrade_coefficient(), Contents.PRIORITY_PC, 0, endTime, start);
				}
			}
		}
		
		if (setting.getAuto_duiping() == null || setting.getAuto_duiping()) {
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
				
				if (transaction.getSuspend() || transaction.getTransaction_state() != 1) {
					continue;
				}
				
				String code = transaction.getTransaction_code();
				Integer digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_DIGITS);
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
					spyMemcachedClient.set(code + BTCQMemecacheKeys.D_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
				
				for (String userId : users) {
					Long uid = Convert.toLong(userId, 0);
					if (uid <= 0) continue;
			
					// 多仓
					List<DeliveryMemberTransaction> buyTransList = memberTransactionService.getBQMemberTrasaction(uid, code, 1);
					if (buyTransList.size() == 0) continue;
					// 空仓
					List<DeliveryMemberTransaction> sellTransList = memberTransactionService.getBQMemberTrasaction(uid, code, 2);
					if (sellTransList.size() == 0) continue;
					
					DeliveryMemberTransaction buyTrans = buyTransList.get(0);
					DeliveryMemberTransaction sellTrans = sellTransList.get(0);
					
					if (buyTrans.getHold_count().compareTo(BigDecimal.ZERO) > 0 
							&& sellTrans.getHold_count().compareTo(BigDecimal.ZERO) > 0) {
						
						DeliveryPendingDelegation sell1 = pendingDelegationService.getSell1(code);
						DeliveryPendingDelegation buy1 = pendingDelegationService.getBuy1(code);
						if (sell1 == null || buy1 == null) continue;
						
						BigDecimal btc_index_price = spyMemcachedClient.get(transaction.getTransaction_type_code());
						if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
							continue;
						}
						
						BigDecimal minChange = new BigDecimal(1.0 / Math.pow(10, digits));
						minChange = minChange.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
						if (sell1.getDelegation_price().subtract(buy1.getDelegation_price()).abs().compareTo(minChange) <= 0) {
							continue;
						}

						
						DeliveryArbitrageSetting arbitrageSetting = null;
						try {
							arbitrageSetting = spyMemcachedClient.get(BTCQMemecacheKeys.D_ARBITRAGE_SETTING + code);
						} catch (Exception e) {}
						
						if (arbitrageSetting == null) {
							arbitrageSetting = dArbitrageSettingService.getArbitrageSettingByCode(code);
							if (arbitrageSetting == null) {
								arbitrageSetting = new DeliveryArbitrageSetting();
								arbitrageSetting.setRunning(false);
								arbitrageSetting.setScalping(false);
								arbitrageSetting.setHedging(false);
								arbitrageSetting.setRisk_manage(false);
								arbitrageSetting.setAuto_trade(false);
								arbitrageSetting.setDepth_uid(0L);
							}
							
							spyMemcachedClient.set(BTCQMemecacheKeys.D_ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, arbitrageSetting);
						}
						if (arbitrageSetting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0
								&& sell1.getDelegation_price().subtract(buy1.getDelegation_price()).compareTo(arbitrageSetting.getScalping_depth_range().multiply(buy1.getDelegation_price())) > 0) {
							break;
						}
						
						BigDecimal range = new BigDecimal(0.02);
						if (arbitrageSetting.getScalping_index_range().compareTo(BigDecimal.ZERO) > 0) {
							range = arbitrageSetting.getScalping_index_range();
						}
						
						BigDecimal highPrice = btc_index_price.multiply(BigDecimal.ONE.add(range));
						BigDecimal lowPrice = btc_index_price.multiply(BigDecimal.ONE.subtract(range));
						
						if (sell1.getDelegation_price().compareTo(highPrice) <= 0) {
							highPrice = sell1.getDelegation_price().subtract(minChange);
						}
						
						if (buy1.getDelegation_price().compareTo(lowPrice) >= 0) {
							lowPrice = buy1.getDelegation_price().add(minChange);
						}
						
						BigDecimal depthWidth = new BigDecimal(0.02);
						if (arbitrageSetting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0) {
							depthWidth = arbitrageSetting.getScalping_depth_range();
						}
						
						if (highPrice.compareTo(lowPrice) < 0 || highPrice.subtract(lowPrice).divide(lowPrice, 4, BigDecimal.ROUND_HALF_UP).compareTo(depthWidth) > 0) {
							break;
						}
						
						BigDecimal price = lowPrice.add(new BigDecimal(Math.random()).multiply(highPrice.subtract(lowPrice))) ;
						price = price.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
						
						if (sell1.getDelegation_price().compareTo(price) <= 0 
								|| buy1.getDelegation_price().compareTo(price) >= 0) {
							continue;
						}
						
						boolean buyLock = memberTransactionService.lockToTrade(buyTrans.getMt_id());
						if (buyLock) {
							try {
								boolean sellLock = memberTransactionService.lockToTrade(sellTrans.getMt_id());
								if (sellLock) {
									try {
										buyTrans = memberTransactionService.getMemberTransactionById(buyTrans.getMt_id());
										sellTrans = memberTransactionService.getMemberTransactionById(sellTrans.getMt_id());
										
										if (buyTrans == null || sellTrans == null 
												|| buyTrans.getHold_count().compareTo(BigDecimal.ZERO) == 0
												|| sellTrans.getHold_count().compareTo(BigDecimal.ZERO) == 0) {
											continue;
										}
										
										BigDecimal amount = buyTrans.getHold_count().compareTo(sellTrans.getHold_count()) > 0 ? sellTrans.getHold_count() : buyTrans.getHold_count();
										
										// 相当于平多仓
										BigDecimal deposit = amount.divide(buyTrans.getHold_count().add(buyTrans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
												.multiply(buyTrans.getDeposit());
										DeliveryPendingDelegationCache sellcache = new DeliveryPendingDelegationCache(buyTrans.getTransaction_id(), buyTrans.getTransaction_code(),Contents.DIRECTION_SELL, 
												amount, price, new BigDecimal(0), amount, buyTrans.getTrade_multiple().intValue(), 
												deposit, BigDecimal.ZERO, Contents.PRIORITY_PC, uid, BigDecimal.ZERO, BigDecimal.ZERO);
										sellcache.setFlag(1);
										
										int flag = memberTransactionService.updateMemberTransByID(buyTrans.getMt_id(), amount.negate(), amount, 
												BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
										if (flag > 0) {
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
											buy.setCommission_rate(BigDecimal.ZERO);
											buy.setPriority(Contents.PRIORITY_PC);
											buy.setF_user_id(uid);
											buy.setF_fromremain(BigDecimal.ZERO);
											buy.setF_enforce_price(BigDecimal.ZERO);
											buy.setPending_no(RandomUtil.getPendingNo());
											buy.setFlag(1);
											
											boolean success = false;
											int flag2 = memberTransactionService.updateMemberTransByID(sellTrans.getMt_id(), amount.negate(), amount, 
													BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
											if (flag2 > 0) {
												try {
													openPositionServer.eatBuy1(code, buy, sellcache, transaction.getTrade_coefficient(), true);
													success = true;
													
													if (buyTrans.getHold_count().compareTo(sellTrans.getHold_count()) <= 0) {
														memberTransactionService.deleteZEROTran(buyTrans);
													}
													
												} catch (Exception e) {
													logger.error("量化用户对平错误，挂单量：" + sellcache.getUndeal_count() + "，多单持仓量：" + buyTrans.getHold_count() + "，空头持仓量：" + sellTrans.getHold_count());
													// e.printStackTrace();
												}
												
												if (!success) {
													// 需要恢复的持仓
													if (memberTransactionService.updateMemberTransByID(sellTrans.getMt_id(), amount, amount.negate(), BigDecimal.ZERO,
															BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
														logger.error("量化用户对平撤单错误：用户" + buy.getF_user_id() + "的挂单量：" + buy.getUndeal_count() + "，持单量：" + sellTrans.getLock_count());
													}
												}
											} 
											
											if (!success) {
												// 需要恢复的持仓
												if (memberTransactionService.updateMemberTransByID(buyTrans.getMt_id(), amount, amount.negate(), BigDecimal.ZERO,
														BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
													logger.error("量化用户对平撤单错误：用户" + buyTrans.getF_user_id() + "的挂单量：" + sellcache.getUndeal_count() + "，持单量：" + buyTrans.getLock_count());
												}
											} else {
												spyMemcachedClient.set(code + BTCQMemecacheKeys.D_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, price);
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										if (sellTrans != null) {
											memberTransactionService.unlockToTrade(sellTrans.getMt_id());
										}
									}
								}
							} catch (Exception e) {
								
							} finally {
								if (buyTrans != null) {
									memberTransactionService.unlockToTrade(buyTrans.getMt_id());
								}
							}
						}
					}
				}
			}
		}
	}
}
