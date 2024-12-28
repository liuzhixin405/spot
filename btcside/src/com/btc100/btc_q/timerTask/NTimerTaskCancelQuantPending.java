package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.service.NMemberTransactionService;
import com.btc100.btc_q.service.NPendingDelegationCacheService;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.util.StringUtil;

public class NTimerTaskCancelQuantPending {

	@Autowired
	private NPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private NPendingDelegationService pendingDelegationService;
	@Autowired
	private NMemberTransactionService bqMemberTransactionService;
	
	private static String[] contractArray = null;
	
	Logger logger = Logger.getLogger(NTimerTaskCancelQuantPending.class);
	
	@SuppressWarnings("unused")
	private void doCancel(String partition, int currencyId, long uid, String contract, BigDecimal coefficient, int priority, int direction, long endTime, int start) {
		int dealCount = 0;
		List<NMemberTransaction> transList = new ArrayList<NMemberTransaction>();
		try {
			if (priority == Contents.PRIORITY_PC) {
				transList = bqMemberTransactionService.getTransactionByUserId(partition, contract, uid);
				
				boolean locked = true;
				for (NMemberTransaction trans : transList) {
					if (!bqMemberTransactionService.lockToAdjustSpare(partition, trans.getMt_id())) {
						locked = false;
						break;
					} 
					
					dealCount++;
				}
		
				if (!locked) return;
			}
			
			List<NPendingDelegationCache> plistCache = pendingDelegationCacheService.findPendingByUserIdAndTime(partition, contract, uid, priority, direction, 0, 0, start, 300, false);
			for (NPendingDelegationCache pendingDelegationCache : plistCache) {
				String lock = pendingDelegationCacheService.lock(partition, pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						pendingDelegationCacheService.cancelPendingCache_one(partition, currencyId, pendingDelegationCache, coefficient, false);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegationCache != null) {
							pendingDelegationCacheService.unLock(partition, pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id(), lock);
						}
					}
				}
			}
			
			List<NPendingDelegation> plist = new ArrayList<NPendingDelegation>();
			if (priority == Contents.PRIORITY_PC) {
				plist = pendingDelegationService.findPendingByUserIdAndTime(partition, contract, uid, priority, direction, 0, endTime, 0, 0, false);
			} else {
				plist = pendingDelegationService.findPendingByUserIdAndTime(partition, contract, uid, priority, direction, 0, 0, start, 300, true);
			}
			
			for (NPendingDelegation pendingDelegation : plist) {
				String lock = pendingDelegationService.lock(partition, pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						pendingDelegationService.cancelPending_one(partition, currencyId, pendingDelegation, coefficient, false);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegation != null) {
							pendingDelegationService.unLock(partition, pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id(), lock);
						}
					}
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			for (NMemberTransaction trans : transList) {
				if (dealCount == 0) {
					break;
				}
				
				if (trans != null) {
					bqMemberTransactionService.unlockToAdjustSpare(partition, trans.getMt_id());
				}
				dealCount--;
			}
		}
		
	}
	
	public void run() {
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.ncontracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		

		
		if (contractArray == null || contractArray.length == 0) return;
		
		/*
		for (String pContract : contractArray) {
			String[] strs = pContract.split("_");
			if (strs.length != 2) continue;
			String partition = strs[0];
			String contract = strs[1];
			
			if (StringUtil.isEmpty(partition) || StringUtil.isEmpty(contract)) {
				continue;
			}
			
			NTransaction currTransaction = transactionService.findTransByCode(partition, contract);
			if (currTransaction == null) continue;
			
			if (currTransaction.getSuspend()) {
				continue;
			}
			

			BQQuantSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.QUANT_SETTING + "_" + partition);
			if (setting == null) {
				setting = bqQuantSettingService.get(partition);
				if (setting == null) {
					setting = new BQQuantSetting();
					setting.setAuto_duiping(false);
					setting.setUids("");
					setting.setCancel_timeout(0);
				}
				
				spyMemcachedClient.set(BTCQMemecacheKeys.QUANT_SETTING  + "_" + partition, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
			
			String ids = setting.getUids();
			if (StringUtil.isEmpty(ids)) continue;
			
			int timeout = setting.getCancel_timeout();
			if (timeout <= 0) continue;
			
			long currTime = pendingDelegationCacheService.getUnix_TIMESTAMP();
			long endTime = currTime - timeout;
			
			String[] users = ids.split(",");
			
			if (setting.getIs_auto_cancel()) {
				int start = 40;
				if (setting.getKeep_nums() != null && setting.getKeep_nums() > 0) {
					start = setting.getKeep_nums();
				}
			
				for (String userId : users) {
					Long uid = Convert.toLong(userId, 0);
					if (uid <= 0) continue;
					
//					doCancel(uid, contract, currTransaction.getTrade_coefficient(), Contents.PRIORITY_KC, endTime, start);
					doCancel(partition, currTransaction.getCurrency_type_id(), uid, contract, currTransaction.getTrade_coefficient(), Contents.PRIORITY_PC, 0, endTime, start);
				}
			}
			
			
			
			if (setting.getAuto_duiping() == null || setting.getAuto_duiping()) {
				Integer digits = spyMemcachedClient.get(partition + "_" + currTransaction.getTransaction_type_code() + BTCQMemecacheKeys.DIGITS);
				if (digits == null) {
					List<NTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition);
					if (transTypes == null) {
						transTypes = transTypeService.findAllUsing(partition);
						spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition, BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
					}
					
					NTransactionType transType = null;
					for (NTransactionType type : transTypes) {
						if (type.getType_code().equals(currTransaction.getTransaction_type_code())) {
							transType = type;
						}
					}
					
					if (transType == null) continue;
					
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(partition + "_" + transType.getType_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
				
				for (String userId : users) {
					Long uid = Convert.toLong(userId, 0);
					if (uid <= 0) continue;
			
					// 多仓
					List<NMemberTransaction> buyTransList = bqMemberTransactionService.getMemberTrasaction(partition, uid, contract, 1);
					if (buyTransList.size() == 0) continue;
					// 空仓
					List<NMemberTransaction> sellTransList = bqMemberTransactionService.getMemberTrasaction(partition, uid, contract, 2);
					if (sellTransList.size() == 0) continue;
					
					NMemberTransaction buyTrans = buyTransList.get(0);
					NMemberTransaction sellTrans = sellTransList.get(0);
					
					if (buyTrans.getHold_count().compareTo(BigDecimal.ZERO) > 0 
							&& sellTrans.getHold_count().compareTo(BigDecimal.ZERO) > 0) {
						
						NPendingDelegation sell1 = pendingDelegationService.getSell1(partition, contract);
						NPendingDelegation buy1 = pendingDelegationService.getBuy1(partition, contract);
						if (sell1 == null || buy1 == null) continue;
						
						BigDecimal btc_index_price = spyMemcachedClient.get(contract);
						if (btc_index_price == null || btc_index_price.compareTo(BigDecimal.ZERO) <= 0) {
							continue;
						}
						
						BigDecimal minChange = new BigDecimal(1.0 / Math.pow(10, digits));
						minChange = minChange.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
						if (sell1.getDelegation_price().subtract(buy1.getDelegation_price()).abs().compareTo(minChange) <= 0) {
							continue;
						}
						
						
						NArbitrageSetting arbitrageSetting = null;
						try {
							arbitrageSetting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + partition + "_" + contract);
						} catch (Exception e) {}
						
						if (arbitrageSetting == null) {
							arbitrageSetting = bqArbitrageSettingService.getArbitrageSettingByCode(partition, contract);
							if (arbitrageSetting == null) {
								arbitrageSetting = new NArbitrageSetting();
								arbitrageSetting.setRunning(false);
								arbitrageSetting.setScalping(false);
								arbitrageSetting.setHedging(false);
								arbitrageSetting.setRisk_manage(false);
								arbitrageSetting.setAuto_trade(false);
								arbitrageSetting.setDepth_uid(0L);
							}
							
							spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + partition + "_" + contract, BTCQMemecacheKeys.TIMES.TEN_SECOND, arbitrageSetting);
						}
						if (arbitrageSetting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0
								&& sell1.getDelegation_price().subtract(buy1.getDelegation_price()).compareTo(arbitrageSetting.getScalping_depth_range().multiply(buy1.getDelegation_price())) > 0) {
							System.out.println("buy1: " + buy1.getDelegation_price().doubleValue() + ", sell1: " + sell1.getDelegation_price() + ", time: " + System.currentTimeMillis());
							System.out.println("sells:");
							List<Top5DelegationVO> vos = pendingDelegationService.getSellTop5(partition, contract);
							for (Top5DelegationVO vo : vos) {
								System.out.println(vo.getPrice() + ", " + vo.getCountNum());
							}
							System.out.println("buys:");
							vos = pendingDelegationService.getBuyTop5(partition, contract);
							for (Top5DelegationVO vo : vos) {
								System.out.println(vo.getPrice() + ", " + vo.getCountNum());
							}
							
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
										
										BigDecimal amount = buyTrans.getHold_count().compareTo(sellTrans.getHold_count()) > 0 ? sellTrans.getHold_count() : buyTrans.getHold_count();
										
										// 相当于平多仓
										BigDecimal deposit = amount.divide(buyTrans.getHold_count().add(buyTrans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
												.multiply(buyTrans.getDeposit().add(buyTrans.getDeposit2()));
										NPendingDelegationCache sellcache = new NPendingDelegationCache(buyTrans.getTransaction_id(), buyTrans.getTransaction_code(),Contents.DIRECTION_SELL, 
												amount, price, new BigDecimal(0), amount, buyTrans.getTrade_multiple().intValue(), 
												deposit, BigDecimal.ZERO, Contents.PRIORITY_PC, uid, BigDecimal.ZERO, BigDecimal.ZERO);
										sellcache.setFlag(1);
										
										int flag = bqMemberTransactionService.updateMemberTransByID(partition, buyTrans.getMt_id(), amount.negate(), amount, 
												BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
										if (flag > 0) {
											BigDecimal deposit2 = amount.divide(sellTrans.getHold_count().add(sellTrans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
													.multiply(sellTrans.getDeposit().add(sellTrans.getDeposit2()));
											
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
											buy.setCommission_rate(BigDecimal.ZERO);
											buy.setPriority(Contents.PRIORITY_PC);
											buy.setF_user_id(uid);
											buy.setF_fromremain(BigDecimal.ZERO);
											buy.setF_enforce_price(BigDecimal.ZERO);
											buy.setPending_no(RandomUtil.getPendingNo());
											buy.setFlag(1);
											
											boolean success = false;
											int flag2 = bqMemberTransactionService.updateMemberTransByID(partition, sellTrans.getMt_id(), amount.negate(), amount, 
													BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
											if (flag2 > 0) {
												try {
													openPositionServer.eatBuy1(currTransaction.getCurrency_type_id(), partition, contract, buy, sellcache, currTransaction.getTrade_coefficient(), true);
													success = true;
													
													if (buyTrans.getHold_count().compareTo(sellTrans.getHold_count()) <= 0) {
														bqMemberTransactionService.deleteZEROTran(partition, buyTrans);
													}
													
												} catch (Exception e) {
													logger.error("量化用户对平错误，挂单量：" + sellcache.getUndeal_count() + "，多单持仓量：" + buyTrans.getHold_count() + "，空头持仓量：" + sellTrans.getHold_count());
													// e.printStackTrace();
												}
												
												if (!success) {
													// 需要恢复的持仓
													if (bqMemberTransactionService.updateMemberTransByID(partition, sellTrans.getMt_id(), amount, amount.negate(), BigDecimal.ZERO,
															BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
														logger.error("量化用户对平撤单错误：用户" + buy.getF_user_id() + "的挂单量：" + buy.getUndeal_count() + "，持单量：" + sellTrans.getLock_count());
													}
												}
											} 
											
											if (!success) {
												// 需要恢复的持仓
												if (bqMemberTransactionService.updateMemberTransByID(partition, buyTrans.getMt_id(), amount, amount.negate(), BigDecimal.ZERO,
														BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
													logger.error("量化用户对平撤单错误：用户" + buyTrans.getF_user_id() + "的挂单量：" + sellcache.getUndeal_count() + "，持单量：" + buyTrans.getLock_count());
												}
											} else {
												spyMemcachedClient.set(partition + "_" + contract + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, price);
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
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
		*/
	}
}
