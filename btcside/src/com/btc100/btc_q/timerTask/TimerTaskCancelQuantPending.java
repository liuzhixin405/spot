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
import com.btc100.btc_q.entity.BQArbitrageSetting;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQQuantSetting;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.service.BQArbitrageSettingService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQQuantSettingService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.OpenPositionServer;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.Top5DelegationVO;

public class TimerTaskCancelQuantPending extends BaseTradeAction {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQQuantSettingService bqQuantSettingService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private BQTransactionTypeService transTypeService;
	@Autowired
	private OpenPositionServer openPositionServer;
	@Autowired
	private BQArbitrageSettingService bqArbitrageSettingService;
	
	private static String[] contractArray = null;
	
	Logger logger = Logger.getLogger(TimerTaskCancelQuantPending.class);
	
	private void doCancel(long uid, String contract, BigDecimal coefficient, int priority, int direction, long endTime, int start) {
		int dealCount = 0;
		List<BQMemberTransaction> transList = new ArrayList<BQMemberTransaction>();
		try {
			if (priority == Contents.PRIORITY_PC) {
				transList = bqMemberTransactionService.getTransactionByUserId(contract, uid);
				
				boolean locked = true;
				for (BQMemberTransaction trans : transList) {
					if (!bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id())) {
						locked = false;
						break;
					} 
					
					dealCount++;
				}
		
				if (!locked) return;
			}
			
			List<PendingDelegationCache> plistCache = pendingDelegationCacheService.findPendingByUserIdAndTime(contract, uid, priority, direction, 0, 0, start, 300, false);
			for (PendingDelegationCache pendingDelegationCache : plistCache) {
				String lock = pendingDelegationCacheService.lock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						pendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, coefficient, false);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegationCache != null) {
							pendingDelegationCacheService.unLock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id(), lock);
						}
					}
				}
			}
			
			List<PendingDelegation> plist = new ArrayList<PendingDelegation>();
			if (priority == Contents.PRIORITY_PC) {
				plist = pendingDelegationService.findPendingByUserIdAndTime(contract, uid, priority, direction, 0, endTime, 0, 0, false);
			} else {
				plist = pendingDelegationService.findPendingByUserIdAndTime(contract, uid, priority, direction, 0, 0, start, 300, true);
			}
			
			for (PendingDelegation pendingDelegation : plist) {
				String lock = pendingDelegationService.lock(pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						pendingDelegationService.cancelPending_one(pendingDelegation, coefficient, false);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegation != null) {
							pendingDelegationService.unLock(pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id(), lock);
						}
					}
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			for (BQMemberTransaction trans : transList) {
				if (dealCount == 0) {
					break;
				}
				
				if (trans != null) {
					bqMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
				}
				dealCount--;
			}
		}
		
	}
	
	public void run() {
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.contracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		BQQuantSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.QUANT_SETTING);
		if (setting == null) {
			setting = bqQuantSettingService.get("");
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.QUANT_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
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
		
		List<BQTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, lstTrads);
		}
		
		if (setting.getIs_auto_cancel()) {
			int start = 40;
			if (setting.getKeep_nums() != null && setting.getKeep_nums() > 0) {
				start = setting.getKeep_nums();
			}
			
			for (String contract : contractArray) {
				BQTransaction currTransaction = null;
				for (BQTransaction bqTransaction : lstTrads) {
					if (bqTransaction.getTransaction_code().equals(contract)) {
						currTransaction = bqTransaction;
						break;
					}
				}
				
				if (currTransaction == null) {
					continue;
				}
				
				if (currTransaction.getSuspend()) {
					continue;
				}
				
				for (String userId : users) {
					Long uid = Convert.toLong(userId, 0);
					if (uid <= 0) continue;
					
//					doCancel(uid, contract, currTransaction.getTrade_coefficient(), Contents.PRIORITY_KC, endTime, start);
					doCancel(uid, contract, currTransaction.getTrade_coefficient(), Contents.PRIORITY_PC, 0, endTime, start);
				}
			}
		}
		
		if (setting.getAuto_duiping() == null || setting.getAuto_duiping()) {
			for (String contract : contractArray) {
				BQTransaction currTransaction = null;
				for (BQTransaction bqTransaction : lstTrads) {
					if (bqTransaction.getTransaction_code().equals(contract)) {
						currTransaction = bqTransaction;
						break;
					}
				}
				
				if (currTransaction == null) {
					continue;
				}
				
				if (currTransaction.getSuspend()) {
					continue;
				}
				
				Integer digits = spyMemcachedClient.get(currTransaction.getTransaction_type_code() + BTCQMemecacheKeys.DIGITS);
				if (digits == null) {
					List<BQTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES);
					if (transTypes == null) {
						transTypes = transTypeService.findAllUsing();
						spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES, BTCQMemecacheKeys.TIMES.TEN_SECOND, transTypes);
					}
					
					BQTransactionType transType = null;
					for (BQTransactionType type : transTypes) {
						if (type.getType_code().equals(currTransaction.getTransaction_type_code())) {
							transType = type;
						}
					}
					
					if (transType == null) continue;
					
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(transType.getType_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
				
				for (String userId : users) {
					Long uid = Convert.toLong(userId, 0);
					if (uid <= 0) continue;
			
					// 多仓
					List<BQMemberTransaction> buyTransList = bqMemberTransactionService.getBQMemberTrasaction(uid, contract, 1);
					if (buyTransList.size() == 0) continue;
					// 空仓
					List<BQMemberTransaction> sellTransList = bqMemberTransactionService.getBQMemberTrasaction(uid, contract, 2);
					if (sellTransList.size() == 0) continue;
					
					BQMemberTransaction buyTrans = buyTransList.get(0);
					BQMemberTransaction sellTrans = sellTransList.get(0);
					
					if (buyTrans.getHold_count().compareTo(BigDecimal.ZERO) > 0 
							&& sellTrans.getHold_count().compareTo(BigDecimal.ZERO) > 0) {
						
						PendingDelegation sell1 = pendingDelegationService.getSell1(contract);
						PendingDelegation buy1 = pendingDelegationService.getBuy1(contract);
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
						
						
						BQArbitrageSetting arbitrageSetting = null;
						try {
							arbitrageSetting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + contract);
						} catch (Exception e) {}
						
						if (arbitrageSetting == null) {
							arbitrageSetting = bqArbitrageSettingService.getArbitrageSettingByCode(contract);
							if (arbitrageSetting == null) {
								arbitrageSetting = new BQArbitrageSetting();
								arbitrageSetting.setRunning(false);
								arbitrageSetting.setScalping(false);
								arbitrageSetting.setHedging(false);
								arbitrageSetting.setRisk_manage(false);
								arbitrageSetting.setAuto_trade(false);
								arbitrageSetting.setDepth_uid(0L);
							}
							
							spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + contract, BTCQMemecacheKeys.TIMES.TEN_SECOND, arbitrageSetting);
						}
						if (arbitrageSetting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0
								&& sell1.getDelegation_price().subtract(buy1.getDelegation_price()).compareTo(arbitrageSetting.getScalping_depth_range().multiply(buy1.getDelegation_price())) > 0) {
							System.out.println("buy1: " + buy1.getDelegation_price().doubleValue() + ", sell1: " + sell1.getDelegation_price() + ", time: " + System.currentTimeMillis());
							System.out.println("sells:");
							List<Top5DelegationVO> vos = pendingDelegationService.getSellTop5(contract);
							for (Top5DelegationVO vo : vos) {
								System.out.println(vo.getPrice() + ", " + vo.getCountNum());
							}
							System.out.println("buys:");
							vos = pendingDelegationService.getBuyTop5(contract);
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
										
										BigDecimal amount = buyTrans.getHold_count().compareTo(sellTrans.getHold_count()) > 0 ? sellTrans.getHold_count() : buyTrans.getHold_count();
										
										// 相当于平多仓
										BigDecimal deposit = amount.divide(buyTrans.getHold_count().add(buyTrans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
												.multiply(buyTrans.getDeposit());
										PendingDelegationCache sellcache = new PendingDelegationCache(buyTrans.getTransaction_id(), buyTrans.getTransaction_code(),Contents.DIRECTION_SELL, 
												amount, price, new BigDecimal(0), amount, buyTrans.getTrade_multiple().intValue(), 
												deposit, BigDecimal.ZERO, Contents.PRIORITY_PC, uid, BigDecimal.ZERO, BigDecimal.ZERO);
										sellcache.setFlag(1);
										
										int flag = bqMemberTransactionService.updateMemberTransByID(buyTrans.getMt_id(), amount.negate(), amount, 
												BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
										if (flag > 0) {
											BigDecimal deposit2 = amount.divide(sellTrans.getHold_count().add(sellTrans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
													.multiply(sellTrans.getDeposit());
											
											PendingDelegation buy = new PendingDelegation();
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
											int flag2 = bqMemberTransactionService.updateMemberTransByID(sellTrans.getMt_id(), amount.negate(), amount, 
													BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
											if (flag2 > 0) {
												try {
													openPositionServer.eatBuy1(contract, buy, sellcache, currTransaction.getTrade_coefficient(), true);
													success = true;
													
													if (buyTrans.getHold_count().compareTo(sellTrans.getHold_count()) <= 0) {
														bqMemberTransactionService.deleteZEROTran(buyTrans);
													}
													
												} catch (Exception e) {
													logger.error("量化用户对平错误，挂单量：" + sellcache.getUndeal_count() + "，多单持仓量：" + buyTrans.getHold_count() + "，空头持仓量：" + sellTrans.getHold_count());
													// e.printStackTrace();
												}
												
												if (!success) {
													// 需要恢复的持仓
													if (bqMemberTransactionService.updateMemberTransByID(sellTrans.getMt_id(), amount, amount.negate(), BigDecimal.ZERO,
															BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
														logger.error("量化用户对平撤单错误：用户" + buy.getF_user_id() + "的挂单量：" + buy.getUndeal_count() + "，持单量：" + sellTrans.getLock_count());
													}
												}
											} 
											
											if (!success) {
												// 需要恢复的持仓
												if (bqMemberTransactionService.updateMemberTransByID(buyTrans.getMt_id(), amount, amount.negate(), BigDecimal.ZERO,
														BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
													logger.error("量化用户对平撤单错误：用户" + buyTrans.getF_user_id() + "的挂单量：" + sellcache.getUndeal_count() + "，持单量：" + buyTrans.getLock_count());
												}
											} else {
												spyMemcachedClient.set(contract + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, price);
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
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
}
