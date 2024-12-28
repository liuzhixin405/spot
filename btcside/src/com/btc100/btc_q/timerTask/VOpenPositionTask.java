package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.VArbitrageSetting;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.entity.VPendingDelegation;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.VArbitrageSettingService;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VOpenPositionServer;
import com.btc100.btc_q.service.VPendingDelegationCacheService;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

public class VOpenPositionTask {
	
	private static final Logger logger = Logger.getLogger(VOpenPositionTask.class);
	
	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	private VPendingDelegationCacheService vPendingDelegationCacheService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VArbitrageSettingService vArbitrageSettingService;
	@Autowired
	private VOpenPositionServer vOpenPositionServer;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	@Autowired
	private BQMemberService bqMemberService;

	private void cancelPendingCache(VPendingDelegationCache pending, int currencyId) {
		String lock = vPendingDelegationCacheService.lock(pending.getTransaction_code(), pending.getDelegation_id());
		if (!StringUtil.isEmpty(lock)) {
			try {
				vPendingDelegationCacheService.cancelPendingCache_one(pending, currencyId, false);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pending != null) {
					vPendingDelegationCacheService.unLock(pending.getTransaction_code(), pending.getDelegation_id(), lock);
				}
			}
		}
	}
	
	private void cancelPending(VPendingDelegation pending, int currencyId) {
		String lock = vPendingDelegationService.lock(pending.getTransaction_code(), pending.getDelegation_id());
		if (!StringUtil.isEmpty(lock)) {
			try {
				vPendingDelegationService.cancelPending_one(pending, currencyId, false);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pending != null) {
					vPendingDelegationService.unLock(pending.getTransaction_code(), pending.getDelegation_id(), lock);
				}
			}
		}
	}
	
	private boolean checkDepthAccountBalance(VArbitrageSetting setting, String code, VPendingDelegation pd, VPendingDelegationCache pdc, int currencyId) {
		if (setting == null || !setting.getRisk_manage()) return true;
		
		BigDecimal amount = pd.getUndeal_count();
		if (amount.compareTo(pdc.getUndeal_count()) > 0) amount = pdc.getUndeal_count();
		
		int direction = Contents.DIRECTION_BUY;
		BigDecimal price = pd.getDelegation_price();
		if (pd.getF_user_id() == setting.getDepth_uid() && pdc.getF_user_id() != setting.getDepth_uid()) {
			if (pdc.getPriority() != Contents.PRIORITY_KC) {
				return true;
			}
			direction = pdc.getDirection();
		} else if (pd.getF_user_id() != setting.getDepth_uid() && pdc.getF_user_id() == setting.getDepth_uid()) {
			if (pd.getPriority() != Contents.PRIORITY_KC) {
				return true;
			}
			direction = pd.getDirection();
		} else {
			return true;
		}
		
		String symbol = "usdt";
		if (direction == Contents.DIRECTION_SELL) {
			symbol = code;
		}
		
		BigDecimal balance = BigDecimal.ZERO;
		if ("ft".equals(code) && "usdt".equals(symbol)) {
			balance = spyMemcachedClient.get("Fcoin_" + BTCQMemecacheKeys.V_HEDGING_BALANCE + "usdt");
		} else {
			balance = spyMemcachedClient.get(BTCQMemecacheKeys.V_HEDGING_BALANCE + symbol);
		}
		
		if (balance == null) {
			 balance = BigDecimal.ZERO;
		}
		
		if (direction == Contents.DIRECTION_BUY) {
			price = price.multiply(BigDecimal.ONE.subtract(setting.getHedging_buy_range().multiply(new BigDecimal(0.01))));
			
			int digtis = Utils.getAmountDigtis(code);
			BigDecimal canOpen = balance.divide(price, digtis, BigDecimal.ROUND_DOWN);
			if (amount.compareTo(canOpen) > 0) {
				if (pdc.getF_user_id() != setting.getDepth_uid()) {
					cancelPendingCache(pdc, currencyId);
					sendEmail(pdc.getF_user_id(), pdc.getTransaction_code(), pdc.getDelegation_price(), pdc.getUndeal_count(), pdc.getDirection());
				} else {
					cancelPending(pd, currencyId);
					sendEmail(pd.getF_user_id(), pd.getTransaction_code(), pd.getDelegation_price(), pd.getUndeal_count(), pd.getDirection());
				}
				
				return false;
			}
		} else {
			if (amount.compareTo(balance) > 0) {
				if (pdc.getF_user_id() != setting.getDepth_uid()) {
					cancelPendingCache(pdc, currencyId);
					sendEmail(pdc.getF_user_id(), pdc.getTransaction_code(), pdc.getDelegation_price(), pdc.getUndeal_count(), pdc.getDirection());
				} else {
					cancelPending(pd, currencyId);
					sendEmail(pd.getF_user_id(), pd.getTransaction_code(), pd.getDelegation_price(), pd.getUndeal_count(), pd.getDirection());
				}
				
				return false;
			}
		}
		
		
		return true;
	}
	
	private boolean checkIfAllDepthUserKC(VArbitrageSetting setting, String code, VPendingDelegation pd, VPendingDelegationCache pdc, int currencyId) {
		if (pd.getF_user_id() != pdc.getF_user_id()) return false;
		
		if (pd.getPriority() != Contents.PRIORITY_KC || pdc.getPriority() != Contents.PRIORITY_KC) {
			return false;
		}
		
		if (setting == null || !setting.getRunning() || setting.isCan_trade_self()) return false;
		
		long depthUid = setting.getDepth_uid();
		if (pd.getF_user_id() == depthUid) {
			cancelPendingCache(pdc, currencyId);
			cancelPending(pd, currencyId);
			return true;
		}
		
		return false;
	}
	
	private void doCancelQuant(VArbitrageSetting arbitrageSetting, int direction, String contract, int currencyId) {
		if (arbitrageSetting != null && arbitrageSetting.getRunning()) {
			List<VPendingDelegation> plist = vPendingDelegationService.findPendingByUserIdAndTime(contract, arbitrageSetting.getDepth_uid(), Contents.PRIORITY_KC, direction, 0, 0, arbitrageSetting.getDepth_size(), 100, true);
			for (VPendingDelegation pendingDelegation : plist) {
				String lock = vPendingDelegationService.lock(pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id());
				if (!StringUtil.isEmpty(lock)) {
					try {
						vPendingDelegationService.cancelPending_one(pendingDelegation, currencyId, false);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (pendingDelegation != null) {
							vPendingDelegationService.unLock(pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id(), lock);
						}
					}
				}
			}
			
			return;
		}
	}
	
	protected boolean dealBuyCacheAndSell(String code, int currencyId, VPendingDelegationCache buycache, VPendingDelegation sell, Boolean isQP) {
		VArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.V_ARBITRAGE_SETTING + code);
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
		
		try {
			doCancelQuant(setting, Contents.DIRECTION_BUY, code, currencyId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean ret = false;
		if (buycache != null && sell != null) {
			// 买1价>= 卖1价
			if (buycache.getDelegation_price().compareTo(sell.getDelegation_price()) >= 0) {
				if (checkIfAllDepthUserKC(setting, code, sell, buycache, currencyId)) return false;
				if (!checkDepthAccountBalance(setting, code, sell, buycache, currencyId)) return false; 
				
				String flagbuy = null;
				if (!isQP) {
					flagbuy = vPendingDelegationCacheService.lock(code, buycache.getDelegation_id());
					if (StringUtil.isEmpty(flagbuy)) return ret;
				}
				
				try {
					String flagsell = vPendingDelegationService.lock(code, sell.getDelegation_id());
					if (StringUtil.isEmpty(flagsell)) {
						return ret;
					}
					
					try {
						// 重新获取，避免改变flag之前挂单被修改
						long buycacheId = buycache.getDelegation_id();
						long sellId = sell.getDelegation_id();
						if (!isQP) {
							buycache = vPendingDelegationCacheService.findPendingById(buycacheId, code);
						}
						sell = vPendingDelegationService.findPendingById(sellId, code);
						
						if (buycache == null || sell == null) {
							return ret;
						}
						
						if (buycache.getUndeal_count().signum() <= 0 
								|| buycache.getDeal_count().signum() < 0 
								|| buycache.getDelegation_count().signum() <= 0
								|| buycache.getDelegation_price().signum() <= 0) {
							if (!isQP) {
								vOpenPositionServer.movePendingLog(buycache);
							}
							logger.error(buycache.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
							return ret;
						}
						if (sell.getUndeal_count().signum() <= 0 || sell.getDeal_count().signum() < 0 || sell.getDelegation_count().signum() < 0
								|| sell.getDelegation_price().signum() <= 0) {
							vOpenPositionServer.movePendingLog(sell);
							logger.error(sell.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
							return ret;
						}
						
						
						boolean memberLock = true;
						VMemberTransaction buyMemberTran = null;
						if (!isQP && buycache.getF_user_id() > 1) {
							int type = buycache.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
							buyMemberTran = vMemberTransactionService.getMemberTransaction(buycache.getTransaction_code(), buycache.getF_user_id(), type, buycache.getMultiple());
							if (buyMemberTran != null) {
								if (buyMemberTran.getFlag() < 0) return ret;
								memberLock = vMemberTransactionService.lockToTrade(buyMemberTran.getMt_id());
							}
							if (!memberLock) return ret;
						}
						
						try {
							memberLock = true;
							int type = sell.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
							VMemberTransaction sellMemberTran = null;
							if (sell.getF_user_id() > 1) {
								sellMemberTran = vMemberTransactionService.getMemberTransaction(sell.getTransaction_code(), sell.getF_user_id(), type, sell.getMultiple());
							}
							if (sellMemberTran != null) {
								if (sellMemberTran.getFlag() < 0) return ret;
								memberLock = vMemberTransactionService.lockToTrade(sellMemberTran.getMt_id());
							}
							
							if (!memberLock) return ret;
							
							try {
		
								BigDecimal buyTosell = buycache.getUndeal_count().subtract(sell.getUndeal_count());
								if (buyTosell.signum() < 0) {
									// buycache1数量小，吃掉buycache1
									try {
										vOpenPositionServer.eatBuyCache1(code, currencyId, buycache, sell, false, isQP);
										ret = true;
									} catch (ServiceException e) {
										if (e.getMessage().equals("buy")) {
											if (!isQP) {
												vOpenPositionServer.movePendingLog(buycache);
											}
											logger.error(buycache.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
										} else {
											vOpenPositionServer.movePendingLog(sell);
											logger.error(sell.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
										}
									}
								} else if (buyTosell.signum() > 0) {
									// buycache1数量大，吃掉sell1
									try {
										vOpenPositionServer.eatSell1(code, currencyId, buycache, sell, isQP);
										ret = true;
									} catch (ServiceException e) {
										if (e.getMessage().equals("buy")) {
											if (!isQP) {
												vOpenPositionServer.movePendingLog(buycache);
											}
											logger.error(buycache.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
										} else {
											vOpenPositionServer.movePendingLog(sell);
											logger.error(sell.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
										}
									}
								} else {
									try {
										vOpenPositionServer.eatBuyCache1(code, currencyId, buycache, sell, true, isQP);
										ret = true;
									} catch (ServiceException e) {
										if (e.getMessage().equals("buy")) {
											if (!isQP) {
												vOpenPositionServer.movePendingLog(buycache);
											}
											logger.error(buycache.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
										} else {
											vOpenPositionServer.movePendingLog(sell);
											logger.error(sell.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (sellMemberTran != null) {
									vMemberTransactionService.unlockToTrade(sellMemberTran.getMt_id());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (buyMemberTran != null) {
								vMemberTransactionService.unlockToTrade(buyMemberTran.getMt_id());
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (sell != null) {
							vPendingDelegationService.unLock(code, sell.getDelegation_id(), flagsell);
						}
					}
				 
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (!isQP) {
						if (buycache != null) {
							vPendingDelegationCacheService.unLock(code, buycache.getDelegation_id(), flagbuy);
						}
					}
				}
				
//				for (VTradeLog trade : trades) {
//					if (trade.getTrade_fee().compareTo(BigDecimal.ZERO) > 0) {
//						doRebateAndMine(trade, currencyId);
//					}
//				}
			} else if (!isQP) {
				moveBuyCache(code, buycache, sell, currencyId);
//				List<VPendingDelegationCache> buyCaches = vPendingDelegationCacheService.findBuyBelowPrice(code, buycache.getDelegation_price(), 10);
//				for (VPendingDelegationCache cache : buyCaches) {
//					moveBuyCache(code, cache, sell, currencyId);
//				}
			}
		} else if (buycache != null && sell == null && !isQP) {
			moveBuyCache(code, buycache, currencyId);
//			List<VPendingDelegationCache> buyCaches = vPendingDelegationCacheService.findBuyBelowPrice(code, buycache.getDelegation_price(), 10);
//			for (VPendingDelegationCache cache : buyCaches) {
//				moveBuyCache(code, cache, currencyId);
//			}
		} else if (buycache == null && sell != null) {
			// 什么也不做
			return ret;
		} else {
			// 什么也不做
			return ret;
		}
		
		try {
			doCancelQuant(setting, Contents.DIRECTION_BUY, code, currencyId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}

	protected boolean dealBuyAndSellCache(String code, int currencyId, VPendingDelegation buy, VPendingDelegationCache sellcache, Boolean isQP) {
		VArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.V_ARBITRAGE_SETTING + code);
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
		
		try {
			doCancelQuant(setting, Contents.DIRECTION_SELL, code, currencyId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean ret = false;
		if (sellcache != null && buy != null) {
			// 卖1价<=买1价
			if (sellcache.getDelegation_price().compareTo(buy.getDelegation_price()) != 1) {
				if (checkIfAllDepthUserKC(setting, code, buy, sellcache, currencyId)) return false;
				if (!checkDepthAccountBalance(setting, code, buy, sellcache, currencyId)) return false; 
				
				String flagbuy = vPendingDelegationService.lock(code, buy.getDelegation_id());
				if (StringUtil.isEmpty(flagbuy)) {
					return ret;
				}
				
				try {
					String flagsell = null;
					if (!isQP) {
						flagsell = vPendingDelegationCacheService.lock(code, sellcache.getDelegation_id());
						if (StringUtil.isEmpty(flagsell)) {
							return ret;
						}
					}
					
					try {
						// 重新获取，避免改变flag之前挂单被修改
						long sellcacheId = sellcache.getDelegation_id();
						long buyId = buy.getDelegation_id();
						buy = vPendingDelegationService.findPendingById(buyId, code);
						if (!isQP) {
							sellcache = vPendingDelegationCacheService.findPendingById(sellcacheId, code);
						}
						
						if (sellcache == null || buy == null) {
							return ret;
						}
			
						if (buy.getUndeal_count().signum() <= 0 || buy.getDeal_count().signum() < 0 || buy.getDelegation_count().signum() <= 0
								|| buy.getDelegation_price().signum() <= 0) {
							vOpenPositionServer.movePendingLog(buy);
							logger.error(buy.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
							return ret;
						}
						
						if (sellcache.getUndeal_count().signum() <= 0 || sellcache.getDeal_count().signum() < 0 || sellcache.getDelegation_count().signum() <= 0
								|| sellcache.getDelegation_price().signum() <= 0) {
							if (!isQP) {
								vOpenPositionServer.movePendingLog(sellcache);
							}
							logger.error(sellcache.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
							return ret;
						}
			
						
						boolean memberLock = true;
						VMemberTransaction buyMemberTran = null;
						int type = buy.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
						if (buy.getF_user_id() > 1) {
							buyMemberTran = vMemberTransactionService.getMemberTransaction(buy.getTransaction_code(), buy.getF_user_id(), type, buy.getMultiple());
						}
						if (buyMemberTran != null) {
							if (buyMemberTran.getFlag() < 0) return ret;
							memberLock = vMemberTransactionService.lockToTrade(buyMemberTran.getMt_id());
						}
						if (!memberLock) return ret;
						
						try {
							VMemberTransaction sellMemberTran = null;
							if (!isQP && sellcache.getF_user_id() > 1) {
								memberLock = true;
								type = sellcache.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
								sellMemberTran = vMemberTransactionService.getMemberTransaction(sellcache.getTransaction_code(), sellcache.getF_user_id(), type, sellcache.getMultiple());
								if (sellMemberTran != null) {
									if (sellMemberTran.getFlag() < 0) return ret;
									memberLock = vMemberTransactionService.lockToTrade(sellMemberTran.getMt_id());
								}
								
								if (!memberLock) return ret;
							}
							
							try {
								// 调整空头开仓sellcache保证金，买价>卖价，此时成交价为买价
								if (sellcache.getPriority() == Contents.PRIORITY_KC 
										&& buy.getDelegation_price().compareTo(sellcache.getDelegation_price()) > 0) {
									vOpenPositionServer.adjustSellCacheFrozen(buy, sellcache, currencyId);
								}
								
								int sellToBuy = sellcache.getUndeal_count().compareTo(buy.getUndeal_count());
								if (sellToBuy == 1) {
									// //把买1吃掉//sellcache的数量大，吃掉buy
									try {
										vOpenPositionServer.eatBuy1(code, currencyId, buy, sellcache, isQP);
										ret = true;
									} catch (ServiceException e) {
										if (e.getMessage().equals("buy")) {
											vOpenPositionServer.movePendingLog(buy);
											logger.error(buy.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
										} else {
											if (!isQP) {
												vOpenPositionServer.movePendingLog(sellcache);
											}
											logger.error(sellcache.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
										}
									}
								} else if (sellToBuy == -1) {
									// 把买1吃掉//sellcache的数量小，吃掉sellcache
									try {
										vOpenPositionServer.eatSellCache1(code, currencyId, buy, sellcache, false, isQP);
										ret = true;
									} catch (ServiceException e) {
										if (e.getMessage().equals("buy")) {
											vOpenPositionServer.movePendingLog(buy);
											logger.error(buy.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
										} else {
											if (!isQP) {
												vOpenPositionServer.movePendingLog(sellcache);
											}
											logger.error(sellcache.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
										}
									}
								} else {
									try {
										vOpenPositionServer.eatSellCache1(code, currencyId, buy, sellcache, true, isQP);
										ret = true;
									} catch (ServiceException e) {
										if (e.getMessage().equals("buy")) {
											vOpenPositionServer.movePendingLog(buy);
											logger.error(buy.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
										} else {
											if (!isQP) {
												vOpenPositionServer.movePendingLog(sellcache);
											}
											logger.error(sellcache.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (sellMemberTran != null) {
									vMemberTransactionService.unlockToTrade(sellMemberTran.getMt_id());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (buyMemberTran != null) {
								vMemberTransactionService.unlockToTrade(buyMemberTran.getMt_id());
							}
						}
								
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (!isQP) {
							if (sellcache != null) {
								vPendingDelegationCacheService.unLock(code, sellcache.getDelegation_id(), flagsell);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (buy != null) {
						vPendingDelegationService.unLock(code, buy.getDelegation_id(), flagbuy);
					}
				}
				
//				for (VTradeLog trade : trades) {
//					if (trade.getTrade_fee().compareTo(BigDecimal.ZERO) > 0) {
//						doRebateAndMine(trade, currencyId);
//					}
//				}
			} else if (!isQP) {
				moveSellCache(code, sellcache, buy, currencyId);
//				List<VPendingDelegationCache> sellCaches = vPendingDelegationCacheService.findSellAbovePrice(code, sellcache.getDelegation_price(), 2);
//				for (VPendingDelegationCache cache : sellCaches) {
//					moveSellCache(code, cache, buy, currencyId);
//				}
			}
		} else if (sellcache != null && buy == null && !isQP) {
			moveSellCache(code, sellcache, currencyId);
//			List<VPendingDelegationCache> sellCaches = vPendingDelegationCacheService.findSellAbovePrice(code, sellcache.getDelegation_price(), 2);
//			for (VPendingDelegationCache cache : sellCaches) {
//				moveSellCache(code, cache, currencyId);
//			}
		} else if (sellcache == null && buy != null) {
			// do nothing
			return ret;
		} else {
			// do nothing
			return ret;
		}
		
		try {
			doCancelQuant(setting, Contents.DIRECTION_SELL, code, currencyId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	private void moveBuyCache(String code, VPendingDelegationCache buycache, VPendingDelegation sell, int currencyId) {
		String flagbuy = vPendingDelegationCacheService.lock(code, buycache.getDelegation_id());
		if (StringUtil.isEmpty(flagbuy)) {
			return;
		}
		
		try {
			boolean memberLock = true;
			VMemberTransaction buyMemberTran = null;
			int type = buycache.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			if (buycache.getF_user_id() > 1) {
				buyMemberTran = vMemberTransactionService.getMemberTransaction(buycache.getTransaction_code(), buycache.getF_user_id(), type, buycache.getMultiple());
			}
			if (buyMemberTran != null) {
				if (buyMemberTran.getFlag() < 0) return;
				memberLock = vMemberTransactionService.lockToTrade(buyMemberTran.getMt_id());
			}
			if (!memberLock) return;
			
			try {
		
				// 重新获取，避免改变flag之前挂单被修改
				long buycacheId = buycache.getDelegation_id();
				buycache = vPendingDelegationCacheService.findPendingById(buycacheId, code);
				if (buycache == null || buycache.getDelegation_price().compareTo(sell.getDelegation_price()) >= 0) {
					return;
				}
				
				vOpenPositionServer.moveBuyCache1(code, currencyId, buycache);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (buyMemberTran != null) {
					vMemberTransactionService.unlockToTrade(buyMemberTran.getMt_id());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buycache != null) {
				vPendingDelegationCacheService.unLock(code, buycache.getDelegation_id(), flagbuy);
			}
		}
	}
	
	private void moveBuyCache(String code, VPendingDelegationCache buycache, int currencyId) {
		String flagbuy = vPendingDelegationCacheService.lock(code, buycache.getDelegation_id());
		if (StringUtil.isEmpty(flagbuy)) {
			return;
		}
		
		long buycache1Id = buycache.getDelegation_id();
		buycache = vPendingDelegationCacheService.findPendingById(buycache1Id, code);
		if (buycache == null) {
			return;
		}
		
		try {
			boolean memberLock = true;
			VMemberTransaction buyMemberTran = null;
			int type = buycache.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			if (buycache.getF_user_id() > 1) {
				buyMemberTran = vMemberTransactionService.getMemberTransaction(buycache.getTransaction_code(), buycache.getF_user_id(), type, buycache.getMultiple());
			}
			if (buyMemberTran != null) {
				if (buyMemberTran.getFlag() < 0) return;
				memberLock = vMemberTransactionService.lockToTrade(buyMemberTran.getMt_id());
			}
			if (!memberLock) return;
			
			// 把买1转移到PendingDelegation中
			try {
				vOpenPositionServer.moveBuyCache1(code, currencyId, buycache);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (buyMemberTran != null) {
					vMemberTransactionService.unlockToTrade(buyMemberTran.getMt_id());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buycache != null) {
				vPendingDelegationCacheService.unLock(code, buycache.getDelegation_id(), flagbuy);
			}
		}
	}
	
	private void moveSellCache(String code, VPendingDelegationCache sellcache, VPendingDelegation buy, int currencyId) {
		String flagsell = vPendingDelegationCacheService.lock(code, sellcache.getDelegation_id());
		if (StringUtil.isEmpty(flagsell)) {
			return;
		}
		
		try {
			// 重新获取，避免改变flag之前挂单被修改
			long sellcacheId = sellcache.getDelegation_id();
			sellcache = vPendingDelegationCacheService.findPendingById(sellcacheId, code);
			if (sellcache == null || sellcache.getDelegation_price().compareTo(buy.getDelegation_price()) != 1) {
				return;
			}
			
			boolean memberLock = true;
			int type = sellcache.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			VMemberTransaction sellMemberTran = null;
			if (sellcache.getF_user_id() > 1) {
				sellMemberTran = vMemberTransactionService.getMemberTransaction(sellcache.getTransaction_code(), sellcache.getF_user_id(), type, sellcache.getMultiple());
			}
			if (sellMemberTran != null) {
				if (sellMemberTran.getFlag() < 0) return;
				memberLock = vMemberTransactionService.lockToTrade(sellMemberTran.getMt_id());
			}
			
			if (!memberLock) return;
			
			try {
				// 把卖1转移到PendingDelegation中
				vOpenPositionServer.moveSellCache1(code, currencyId,  sellcache);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (sellMemberTran != null) {
					vMemberTransactionService.unlockToTrade(sellMemberTran.getMt_id());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sellcache != null) {
				vPendingDelegationCacheService.unLock(code, sellcache.getDelegation_id(), flagsell);
			}
		}
	}
	
	private void moveSellCache(String code, VPendingDelegationCache sellcache, int currencyId) {
		String flagsell = vPendingDelegationCacheService.lock(code, sellcache.getDelegation_id());
		if (StringUtil.isEmpty(flagsell)) {
			return;
		}

		try {
			// 重新获取，避免改变flag之前挂单被修改
			long sellcacheId = sellcache.getDelegation_id();
			sellcache = vPendingDelegationCacheService.findPendingById(sellcacheId, code);
			if (sellcache == null) {
				return;
			}

			boolean memberLock = true;
			int type = sellcache.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			VMemberTransaction sellMemberTran = null;
			if (sellcache.getF_user_id() > 1) {
				sellMemberTran= vMemberTransactionService.getMemberTransaction(sellcache.getTransaction_code(), sellcache.getF_user_id(), type, sellcache.getMultiple());
			}
			if (sellMemberTran != null) {
				if (sellMemberTran.getFlag() < 0) return;
				memberLock = vMemberTransactionService.lockToTrade(sellMemberTran.getMt_id());
			}
			
			if (!memberLock) return;
			
			try {
				// 把卖1转移到PendingDelegation中
				vOpenPositionServer.moveSellCache1(code, currencyId, sellcache);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (sellMemberTran != null) {
					vMemberTransactionService.unlockToTrade(sellMemberTran.getMt_id());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sellcache != null) {
				vPendingDelegationCacheService.unLock(code, sellcache.getDelegation_id(), flagsell);
			}
		}
	}

//	private void doRebateAndMine(VTradeLog trade, int currencyId) {
//		long childUserId = trade.getF_user_id();
//		if (childUserId <= Contents.BQMEMBER_ADMIN) return;
//		
//		BQMineSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MINE_SETTING);
//		if (setting == null) {
//			setting = bqMineSettingService.get();
//			if (setting != null) {
//				spyMemcachedClient.set(BTCQMemecacheKeys.MINE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
//			} else {
//				return;
//			}
//		}
//		
//		if (setting.getRebate()) {
//			long inviterId = bqMemberInviteLogService.findInviterByUserId(childUserId);
//			if (inviterId > 0L) {
//				BQMemberLevel maxLevel = bqMemberLevelService.getMaxLevel();
//				BQMember parentUser = bqMemberService.findUserById(inviterId);
//				while (parentUser != null) {
//					VMemberInviteRebateLog record = null;
//					try {
//						record = vMemberInviteRebateLogService.createAndSaveRecord(parentUser.getF_user_id(), childUserId, trade.getTrade_fee(), trade.getF_user_id(),
//								trade.getTrade_id(), trade.getTransaction_code(), maxLevel.getLevel_id(), currencyId);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					
//					if (record == null) {
//						break;
//					}
//					
//					if (record.getUser_id() > 0) {
//						childUserId = parentUser.getF_user_id();
//					}
//					
//					inviterId = bqMemberInviteLogService.findInviterByUserId(parentUser.getF_user_id());
//					if (inviterId <= 0L) {
//						break;
//					}
//					
//					parentUser = bqMemberService.findUserById(inviterId);
//				}
//			}
//		}
//	}
	
	private void sendEmail(long uid, String code, BigDecimal price, BigDecimal amount, int direction) {
		BQMember member = bqMemberService.findUserById(uid);
		if (member == null) return;
		
		String email = member.getF_real_email();
		String type = "多开";
		if (direction == Contents.DIRECTION_SELL) {
			type = "空仓";
		}
		
		StringBuffer sb = new StringBuffer("尊敬的xkd用户，您好！<br>");
        sb.append("&nbsp;&nbsp;&nbsp;&nbsp;风控系统当前限制" + code.toUpperCase() + "合约可" + type + "额度为0，您委托价格" + price.doubleValue() + "usdt，数量" + amount.doubleValue() + "个的委托单，");
        sb.append("已撤单，请您知晓！<br><br>");
        sb.append("感谢选择xkd<br>XKD官方<br><br>");
		sb.append("");
		
		final String mail = email;
		final String cnt = sb.toString();
		Runnable runnable = new Runnable() {
            public void run() {
            	try {
            		SendMail themail = new SendMail();
        			themail.send(mail, cnt);
            	} catch (Exception e) {
            	}
            }
        };
        
        Thread thread = new Thread(runnable);
        thread.start();
	}
}
