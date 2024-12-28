package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQArbitrageSetting;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberInviteRebateLog;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQMineSetting;
import com.btc100.btc_q.entity.BQQuantSetting;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQArbitrageSettingService;
import com.btc100.btc_q.service.BQBfxFeeSettingService;
import com.btc100.btc_q.service.BQMemberInviteLogService;
import com.btc100.btc_q.service.BQMemberInviteRebateLogService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQMineRecordService;
import com.btc100.btc_q.service.BQMineSettingService;
import com.btc100.btc_q.service.BQMineStageSettingService;
import com.btc100.btc_q.service.BQQuantSettingService;
import com.btc100.btc_q.service.OpenPositionServer;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

public class OpenPositionTask extends BaseMemberLevelTask {
	private static final Logger logger = Logger.getLogger(OpenPositionTask.class);
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private OpenPositionServer openPositionServer;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	BQMemberInviteLogService bqMemberInviteLogService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberInviteRebateLogService bqMemberInviteRebateLogService;
	@Autowired
	private BQArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private BQMineSettingService bqMineSettingService;
	@Autowired
	private BQMineStageSettingService bqMineStageSettingService;
	@Autowired
	private BQMineRecordService bqMineRecordService;
	@Autowired
	private BQQuantSettingService bqQuantSettingService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;
	
	private void cancelPendingCache(PendingDelegationCache pending, BigDecimal coefficient, boolean needLog) {
		String lock = pendingDelegationCacheService.lock(pending.getTransaction_code(), pending.getDelegation_id());
		if (!StringUtil.isEmpty(lock)) {
			try {
				pendingDelegationCacheService.cancelPendingCache_one(pending, coefficient, needLog);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pending != null) {
					pendingDelegationCacheService.unLock(pending.getTransaction_code(), pending.getDelegation_id(), lock);
				}
			}
		}
	}
	
	private void cancelPending(PendingDelegation pending, BigDecimal coefficient, boolean needLog) {
		String lock = pendingDelegationService.lock(pending.getTransaction_code(), pending.getDelegation_id());
		if (!StringUtil.isEmpty(lock)) {
			try {
				pendingDelegationService.cancelPending_one(pending, coefficient, needLog);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pending != null) {
					pendingDelegationService.unLock(pending.getTransaction_code(), pending.getDelegation_id(), lock);
				}
			}
		}
	}
	
	private void doCancelQuant(BQArbitrageSetting arbitrageSetting, int direction, String contract, BigDecimal coefficient) {
		if (arbitrageSetting != null && arbitrageSetting.getRunning()) {
			List<PendingDelegation> plist = pendingDelegationService.findPendingByUserIdAndTime(contract, arbitrageSetting.getDepth_uid(), Contents.PRIORITY_KC, direction, 0, 0, arbitrageSetting.getDepth_size(), 100, true);
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
			
			return;
		}
		
		
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
		
		int start = 10;
		long quantUid = 8670;
		if (!setting.getIs_auto_cancel() || !setting.getUids().contains(quantUid + "")) {
			return;
		}
		
		if (setting.getKeep_nums() != null && setting.getKeep_nums() > 0) {
			start = setting.getKeep_nums() / 2;
		}
			
		try {
			List<PendingDelegationCache> plistCache = pendingDelegationCacheService.findPendingByUserIdAndTime(contract, quantUid, Contents.PRIORITY_KC, direction, 0, 0, start, 100, true);
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
			
			List<PendingDelegation> plist = pendingDelegationService.findPendingByUserIdAndTime(contract, quantUid, Contents.PRIORITY_KC, direction, 0, 0, start, 100, true);
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
		}
	}
	
	private boolean checkDepthAccountBalance(BQArbitrageSetting setting, String code, PendingDelegation pd, PendingDelegationCache pdc, BigDecimal coefficient) {
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
			balance = spyMemcachedClient.get("Fcoin_" + BTCQMemecacheKeys.HEDGING_BALANCE + "usdt");
		} else if ("elf".equals(code) && "usdt".equals(symbol)) {
			balance = spyMemcachedClient.get("ELF_" + BTCQMemecacheKeys.HEDGING_BALANCE + "usdt");
		} else {
			balance = spyMemcachedClient.get(BTCQMemecacheKeys.HEDGING_BALANCE + symbol);
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
					cancelPendingCache(pdc, coefficient, true);
					sendEmail(pdc.getF_user_id(), pdc.getTransaction_code(), pdc.getDelegation_price(), pdc.getUndeal_count(), pdc.getDirection());
				} else {
					cancelPending(pd, coefficient, true);
					sendEmail(pd.getF_user_id(), pd.getTransaction_code(), pd.getDelegation_price(), pd.getUndeal_count(), pd.getDirection());
				}
				
				return false;
			}
		} else {
			if (amount.compareTo(balance) > 0) {
				if (pdc.getF_user_id() != setting.getDepth_uid()) {
					cancelPendingCache(pdc, coefficient, true);
					sendEmail(pdc.getF_user_id(), pdc.getTransaction_code(), pdc.getDelegation_price(), pdc.getUndeal_count(), pdc.getDirection());
				} else {
					cancelPending(pd, coefficient, true);
					sendEmail(pd.getF_user_id(), pd.getTransaction_code(), pd.getDelegation_price(), pd.getUndeal_count(), pd.getDirection());
				}
				
				return false;
			}
		}
		
		
		return true;
	}
	
	private boolean checkIfAllDepthUserKC(BQArbitrageSetting setting, String code, PendingDelegation pd, PendingDelegationCache pdc, BigDecimal coefficient) {
		if (pd.getF_user_id() != pdc.getF_user_id()) return false;
		
		if (pd.getPriority() != Contents.PRIORITY_KC || pdc.getPriority() != Contents.PRIORITY_KC) {
			return false;
		}

		if (setting == null || !setting.getRunning()) return false;
		
		long depthUid = setting.getDepth_uid();
		if (!setting.getCan_trade_self() && pd.getF_user_id() == depthUid) {
			cancelPendingCache(pdc, coefficient, false);
			cancelPending(pd, coefficient, false);
			return true;
		}
		
		return false;
	}

	private void moveBuyCache(String code, PendingDelegationCache buycache, PendingDelegation sell, BigDecimal coefficient) {
		String flagbuy = pendingDelegationCacheService.lock(code, buycache.getDelegation_id());
		if (StringUtil.isEmpty(flagbuy)) {
			return;
		}
		
		try {
			boolean memberLock = true;
			BQMemberTransaction buyMemberTran = null;
			int type = buycache.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			if (buycache.getF_user_id() > 1) {
				buyMemberTran = bqMemberTransactionService.getMemberTransaction(buycache.getTransaction_code(), buycache.getF_user_id(), type, buycache.getMultiple());
			}
			if (buyMemberTran != null) {
				if (buyMemberTran.getFlag() < 0) return;
				memberLock = bqMemberTransactionService.lockToTrade(buyMemberTran.getMt_id());
			}
			if (!memberLock) return;
			
			try {
		
				// 重新获取，避免改变flag之前挂单被修改
				long buycacheId = buycache.getDelegation_id();
				buycache = pendingDelegationCacheService.findPendingById(buycacheId, code);
				if (buycache == null || buycache.getDelegation_price().compareTo(sell.getDelegation_price()) >= 0) {
					return;
				}
				
				openPositionServer.moveBuyCache1(code, buycache, coefficient);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (buyMemberTran != null) {
					bqMemberTransactionService.unlockToTrade(buyMemberTran.getMt_id());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buycache != null) {
				pendingDelegationCacheService.unLock(code, buycache.getDelegation_id(), flagbuy);
			}
		}
	}
	
	private void moveBuyCache(String code, PendingDelegationCache buycache, BigDecimal coefficient) {
		String flagbuy = pendingDelegationCacheService.lock(code, buycache.getDelegation_id());
		if (StringUtil.isEmpty(flagbuy)) {
			return;
		}
		
		long buycache1Id = buycache.getDelegation_id();
		buycache = pendingDelegationCacheService.findPendingById(buycache1Id, code);
		if (buycache == null) {
			return;
		}
		
		try {
			boolean memberLock = true;
			BQMemberTransaction buyMemberTran = null;
			int type = buycache.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			if (buycache.getF_user_id() > 1) {
				buyMemberTran = bqMemberTransactionService.getMemberTransaction(buycache.getTransaction_code(), buycache.getF_user_id(), type, buycache.getMultiple());
			}
			if (buyMemberTran != null) {
				if (buyMemberTran.getFlag() < 0) return;
				memberLock = bqMemberTransactionService.lockToTrade(buyMemberTran.getMt_id());
			}
			if (!memberLock) return;
			
			// 把买1转移到PendingDelegation中
			try {
				openPositionServer.moveBuyCache1(code, buycache, coefficient);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (buyMemberTran != null) {
					bqMemberTransactionService.unlockToTrade(buyMemberTran.getMt_id());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buycache != null) {
				pendingDelegationCacheService.unLock(code, buycache.getDelegation_id(), flagbuy);
			}
		}
	}
	
	private void moveSellCache(String code, PendingDelegationCache sellcache, PendingDelegation buy, BigDecimal coefficient) {
		String flagsell = pendingDelegationCacheService.lock(code, sellcache.getDelegation_id());
		if (StringUtil.isEmpty(flagsell)) {
			return;
		}
		
		try {
			// 重新获取，避免改变flag之前挂单被修改
			long sellcacheId = sellcache.getDelegation_id();
			sellcache = pendingDelegationCacheService.findPendingById(sellcacheId, code);
			if (sellcache == null || sellcache.getDelegation_price().compareTo(buy.getDelegation_price()) != 1) {
				return;
			}
			
			boolean memberLock = true;
			int type = sellcache.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			BQMemberTransaction sellMemberTran = null;
			if (sellcache.getF_user_id() > 1) {
				sellMemberTran = bqMemberTransactionService.getMemberTransaction(sellcache.getTransaction_code(), sellcache.getF_user_id(), type, sellcache.getMultiple());
			}
			if (sellMemberTran != null) {
				if (sellMemberTran.getFlag() < 0) return;
				memberLock = bqMemberTransactionService.lockToTrade(sellMemberTran.getMt_id());
			}
			
			if (!memberLock) return;
			
			try {
				// 把卖1转移到PendingDelegation中
				openPositionServer.moveSellCache1(code, sellcache, coefficient);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (sellMemberTran != null) {
					bqMemberTransactionService.unlockToTrade(sellMemberTran.getMt_id());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sellcache != null) {
				pendingDelegationCacheService.unLock(code, sellcache.getDelegation_id(), flagsell);
			}
		}
	}
	
	private void moveSellCache(String code, PendingDelegationCache sellcache, BigDecimal coefficient) {
		String flagsell = pendingDelegationCacheService.lock(code, sellcache.getDelegation_id());
		if (StringUtil.isEmpty(flagsell)) {
			return;
		}

		try {
			// 重新获取，避免改变flag之前挂单被修改
			long sellcacheId = sellcache.getDelegation_id();
			sellcache = pendingDelegationCacheService.findPendingById(sellcacheId, code);
			if (sellcache == null) {
				return;
			}

			boolean memberLock = true;
			int type = sellcache.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			BQMemberTransaction sellMemberTran = null;
			if (sellcache.getF_user_id() > 1) {
				sellMemberTran= bqMemberTransactionService.getMemberTransaction(sellcache.getTransaction_code(), sellcache.getF_user_id(), type, sellcache.getMultiple());
			}
			if (sellMemberTran != null) {
				if (sellMemberTran.getFlag() < 0) return;
				memberLock = bqMemberTransactionService.lockToTrade(sellMemberTran.getMt_id());
			}
			
			if (!memberLock) return;
			
			try {
				// 把卖1转移到PendingDelegation中
				openPositionServer.moveSellCache1(code, sellcache, coefficient);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (sellMemberTran != null) {
					bqMemberTransactionService.unlockToTrade(sellMemberTran.getMt_id());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sellcache != null) {
				pendingDelegationCacheService.unLock(code, sellcache.getDelegation_id(), flagsell);
			}
		}
	}
	
	public boolean dealBuyCacheAndSell(String code, PendingDelegationCache buycache, PendingDelegation sell, BigDecimal coefficient, Boolean isQP) {
		BQArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + code);
		if (setting == null) {
			setting = bqArbitrageSettingService.getArbitrageSettingByCode(code);
			if (setting == null) {
				setting = new BQArbitrageSetting();
				setting.setRunning(false);
				setting.setScalping(false);
				setting.setHedging(false);
				setting.setRisk_manage(false);
				setting.setAuto_trade(false);
				setting.setDepth_uid(0L);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
		}
		
		try {
			doCancelQuant(setting, Contents.DIRECTION_BUY, code, coefficient);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean ret = false;
		if (buycache != null && sell != null) {
			// 买1价>= 卖1价
			if (buycache.getDelegation_price().compareTo(sell.getDelegation_price()) >= 0) {
				if (checkIfAllDepthUserKC(setting, code, sell, buycache, coefficient)) return false;
				if (!checkDepthAccountBalance(setting, code, sell, buycache, coefficient)) return false; 
				
				List<TradeLog> trades = new ArrayList<TradeLog>();
				String flagbuy = null;
				if (!isQP) {
					flagbuy = pendingDelegationCacheService.lock(code, buycache.getDelegation_id());
					if (StringUtil.isEmpty(flagbuy)) return ret;
				}
				
				try {
					String flagsell = pendingDelegationService.lock(code, sell.getDelegation_id());
					if (StringUtil.isEmpty(flagsell)) {
						return ret;
					}
					
					try {
						// 重新获取，避免改变flag之前挂单被修改
						long buycacheId = buycache.getDelegation_id();
						long sellId = sell.getDelegation_id();
						if (!isQP) {
							buycache = pendingDelegationCacheService.findPendingById(buycacheId, code);
						}
						sell = pendingDelegationService.findPendingById(sellId, code);
						
						if (buycache == null || sell == null) {
							return ret;
						}
						
						if (buycache.getUndeal_count().signum() <= 0 
								|| buycache.getDeal_count().signum() < 0 
								|| buycache.getDelegation_count().signum() <= 0
								|| buycache.getDelegation_price().signum() <= 0) {
							if (!isQP) {
								openPositionServer.movePendingLog(buycache);
							}
							logger.error(buycache.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
							return ret;
						}
						if (sell.getUndeal_count().signum() <= 0 || sell.getDeal_count().signum() < 0 || sell.getDelegation_count().signum() < 0
								|| sell.getDelegation_price().signum() <= 0) {
							openPositionServer.movePendingLog(sell);
							logger.error(sell.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
							return ret;
						}
						
						
						boolean memberLock = true;
						BQMemberTransaction buyMemberTran = null;
						if (!isQP && buycache.getF_user_id() > 1) {
							int type = buycache.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
							buyMemberTran = bqMemberTransactionService.getMemberTransaction(buycache.getTransaction_code(), buycache.getF_user_id(), type, buycache.getMultiple());
							if (buyMemberTran != null) {
								if (buyMemberTran.getFlag() < 0) return ret;
								memberLock = bqMemberTransactionService.lockToTrade(buyMemberTran.getMt_id());
							}
							if (!memberLock) return ret;
						}
						
						try {
							memberLock = true;
							int type = sell.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
							BQMemberTransaction sellMemberTran = null;
							if (sell.getF_user_id() > 1) {
								sellMemberTran = bqMemberTransactionService.getMemberTransaction(sell.getTransaction_code(), sell.getF_user_id(), type, sell.getMultiple());
							}
							if (sellMemberTran != null) {
								if (sellMemberTran.getFlag() < 0) return ret;
								memberLock = bqMemberTransactionService.lockToTrade(sellMemberTran.getMt_id());
							}
							
							if (!memberLock) return ret;
							
							try {
		
								BigDecimal buyTosell = buycache.getUndeal_count().subtract(sell.getUndeal_count());
								if (buyTosell.signum() < 0) {
									// buycache1数量小，吃掉buycache1
									try {
										trades = openPositionServer.eatBuyCache1(code, buycache, sell, coefficient, false, isQP);
										ret = true;
									} catch (ServiceException e) {
										if (e.getMessage().equals("buy")) {
											if (!isQP) {
												openPositionServer.movePendingLog(buycache);
											}
											logger.error(buycache.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
										} else {
											openPositionServer.movePendingLog(sell);
											logger.error(sell.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
										}
									}
								} else if (buyTosell.signum() > 0) {
									// buycache1数量大，吃掉sell1
									try {
										trades = openPositionServer.eatSell1(code, buycache, sell, coefficient, isQP);
										ret = true;
									} catch (ServiceException e) {
										if (e.getMessage().equals("buy")) {
											if (!isQP) {
												openPositionServer.movePendingLog(buycache);
											}
											logger.error(buycache.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
										} else {
											openPositionServer.movePendingLog(sell);
											logger.error(sell.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
										}
									}
								} else {
									try {
										trades = openPositionServer.eatBuyCache1(code, buycache, sell, coefficient, true, isQP);
										ret = true;
									} catch (ServiceException e) {
										if (e.getMessage().equals("buy")) {
											if (!isQP) {
												openPositionServer.movePendingLog(buycache);
											}
											logger.error(buycache.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
										} else {
											openPositionServer.movePendingLog(sell);
											logger.error(sell.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (sellMemberTran != null) {
									bqMemberTransactionService.unlockToTrade(sellMemberTran.getMt_id());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (buyMemberTran != null) {
								bqMemberTransactionService.unlockToTrade(buyMemberTran.getMt_id());
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (sell != null) {
							pendingDelegationService.unLock(code, sell.getDelegation_id(), flagsell);
						}
					}
				 
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (!isQP) {
						if (buycache != null) {
							pendingDelegationCacheService.unLock(code, buycache.getDelegation_id(), flagbuy);
						}
					}
				}
				
				try {
					for (TradeLog trade : trades) {
						if (trade.getTrade_fee().compareTo(BigDecimal.ZERO) > 0 || trade.getTrade_bfx_fee().compareTo(BigDecimal.ZERO) > 0) {
							doRebateAndMine(trade);
						}
					} 
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (!isQP) {
				moveBuyCache(code, buycache, sell, coefficient);
				List<PendingDelegationCache> buyCaches = pendingDelegationCacheService.findBuyBelowPrice(code, buycache.getDelegation_price(), 10);
				for (PendingDelegationCache cache : buyCaches) {
					moveBuyCache(code, cache, sell, coefficient);
				}
			}
		} else if (buycache != null && sell == null && !isQP) {
			moveBuyCache(code, buycache, coefficient);
			List<PendingDelegationCache> buyCaches = pendingDelegationCacheService.findBuyBelowPrice(code, buycache.getDelegation_price(), 10);
			for (PendingDelegationCache cache : buyCaches) {
				moveBuyCache(code, cache, coefficient);
			}
		} else if (buycache == null && sell != null) {
			// 什么也不做
			return ret;
		} else {
			// 什么也不做
			return ret;
		}
		
		try {
			doCancelQuant(setting, Contents.DIRECTION_BUY, code, coefficient);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}

	public boolean dealBuyAndSellCache(String code, PendingDelegation buy, PendingDelegationCache sellcache, BigDecimal coefficient, Boolean isQP) {
		BQArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + code);
		if (setting == null) {
			setting = bqArbitrageSettingService.getArbitrageSettingByCode(code);
			if (setting == null) {
				setting = new BQArbitrageSetting();
				setting.setRunning(false);
				setting.setScalping(false);
				setting.setHedging(false);
				setting.setRisk_manage(false);
				setting.setAuto_trade(false);
				setting.setDepth_uid(0L);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
		}
		
		try {
			doCancelQuant(setting, Contents.DIRECTION_SELL, code, coefficient);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean ret = false;
		if (sellcache != null && buy != null) {
			// 卖1价<=买1价
			if (sellcache.getDelegation_price().compareTo(buy.getDelegation_price()) != 1) {
				if (checkIfAllDepthUserKC(setting, code, buy, sellcache, coefficient)) return false;
				if (!checkDepthAccountBalance(setting, code, buy, sellcache, coefficient)) return false; 
				
				List<TradeLog> trades = new ArrayList<TradeLog>();
				String flagbuy = pendingDelegationService.lock(code, buy.getDelegation_id());
				if (StringUtil.isEmpty(flagbuy)) {
					return ret;
				}
				
				try {
					String flagsell = null;
					if (!isQP) {
						flagsell = pendingDelegationCacheService.lock(code, sellcache.getDelegation_id());
						if (StringUtil.isEmpty(flagsell)) {
							return ret;
						}
					}
					
					try {
						// 重新获取，避免改变flag之前挂单被修改
						long sellcacheId = sellcache.getDelegation_id();
						long buyId = buy.getDelegation_id();
						buy = pendingDelegationService.findPendingById(buyId, code);
						if (!isQP) {
							sellcache = pendingDelegationCacheService.findPendingById(sellcacheId, code);
						}
						
						if (sellcache == null || buy == null) {
							return ret;
						}
			
						if (buy.getUndeal_count().signum() <= 0 || buy.getDeal_count().signum() < 0 || buy.getDelegation_count().signum() <= 0
								|| buy.getDelegation_price().signum() <= 0) {
							openPositionServer.movePendingLog(buy);
							logger.error(buy.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
							return ret;
						}
						
						if (sellcache.getUndeal_count().signum() <= 0 || sellcache.getDeal_count().signum() < 0 || sellcache.getDelegation_count().signum() <= 0
								|| sellcache.getDelegation_price().signum() <= 0) {
							if (!isQP) {
								openPositionServer.movePendingLog(sellcache);
							}
							logger.error(sellcache.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
							return ret;
						}
			
						
						boolean memberLock = true;
						BQMemberTransaction buyMemberTran = null;
						int type = buy.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
						if (buy.getF_user_id() > 1) {
							buyMemberTran = bqMemberTransactionService.getMemberTransaction(buy.getTransaction_code(), buy.getF_user_id(), type, buy.getMultiple());
						}
						if (buyMemberTran != null) {
							if (buyMemberTran.getFlag() < 0) return ret;
							memberLock = bqMemberTransactionService.lockToTrade(buyMemberTran.getMt_id());
						}
						if (!memberLock) return ret;
						
						try {
							BQMemberTransaction sellMemberTran = null;
							if (!isQP && sellcache.getF_user_id() > 1) {
								memberLock = true;
								type = sellcache.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
								sellMemberTran = bqMemberTransactionService.getMemberTransaction(sellcache.getTransaction_code(), sellcache.getF_user_id(), type, sellcache.getMultiple());
								if (sellMemberTran != null) {
									if (sellMemberTran.getFlag() < 0) return ret;
									memberLock = bqMemberTransactionService.lockToTrade(sellMemberTran.getMt_id());
								}
								
								if (!memberLock) return ret;
							}
							
							try {
								int sellToBuy = sellcache.getUndeal_count().compareTo(buy.getUndeal_count());
								if (sellToBuy == 1) {
									// //把买1吃掉//sellcache的数量大，吃掉buy
									try {
										trades = openPositionServer.eatBuy1(code, buy, sellcache, coefficient, isQP);
										ret = true;
									} catch (ServiceException e) {
										if (e.getMessage().equals("buy")) {
											openPositionServer.movePendingLog(buy);
											logger.error(buy.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
										} else {
											if (!isQP) {
												openPositionServer.movePendingLog(sellcache);
											}
											logger.error(sellcache.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
										}
									}
								} else if (sellToBuy == -1) {
									// 把买1吃掉//sellcache的数量小，吃掉sellcache
									try {
										trades = openPositionServer.eatSellCache1(code, buy, sellcache, coefficient, false, isQP);
										ret = true;
									} catch (ServiceException e) {
										if (e.getMessage().equals("buy")) {
											openPositionServer.movePendingLog(buy);
											logger.error(buy.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
										} else {
											if (!isQP) {
												openPositionServer.movePendingLog(sellcache);
											}
											logger.error(sellcache.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
										}
									}
								} else {
									try {
										trades = openPositionServer.eatSellCache1(code, buy, sellcache, coefficient, true, isQP);
										ret = true;
									} catch (ServiceException e) {
										if (e.getMessage().equals("buy")) {
											openPositionServer.movePendingLog(buy);
											logger.error(buy.getF_user_id() + "的" + code + "买单出错：转移到挂单日志表里");
										} else {
											if (!isQP) {
												openPositionServer.movePendingLog(sellcache);
											}
											logger.error(sellcache.getF_user_id() + "的" + code + "卖单出错：转移到挂单日志表里");
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (sellMemberTran != null) {
									bqMemberTransactionService.unlockToTrade(sellMemberTran.getMt_id());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (buyMemberTran != null) {
								bqMemberTransactionService.unlockToTrade(buyMemberTran.getMt_id());
							}
						}
								
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (!isQP) {
							if (sellcache != null) {
								pendingDelegationCacheService.unLock(code, sellcache.getDelegation_id(), flagsell);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (buy != null) {
						pendingDelegationService.unLock(code, buy.getDelegation_id(), flagbuy);
					}
				}
				
				try {
					for (TradeLog trade : trades) {
						if (trade.getTrade_fee().compareTo(BigDecimal.ZERO) > 0 || trade.getTrade_bfx_fee().compareTo(BigDecimal.ZERO) > 0) {
							doRebateAndMine(trade);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (!isQP) {
				moveSellCache(code, sellcache, buy, coefficient);
				List<PendingDelegationCache> sellCaches = pendingDelegationCacheService.findSellAbovePrice(code, sellcache.getDelegation_price(), 10);
				for (PendingDelegationCache cache : sellCaches) {
					moveSellCache(code, cache, buy, coefficient);
				}
			}
			
		} else if (sellcache != null && buy == null && !isQP) {
			moveSellCache(code, sellcache, coefficient);
			List<PendingDelegationCache> sellCaches = pendingDelegationCacheService.findSellAbovePrice(code, sellcache.getDelegation_price(), 10);
			for (PendingDelegationCache cache : sellCaches) {
				moveSellCache(code, cache, coefficient);
			}
		} else if (sellcache == null && buy != null) {
			// do nothing
			return ret;
		} else {
			// do nothing
			return ret;
		}
		
		try {
			doCancelQuant(setting, Contents.DIRECTION_SELL, code, coefficient);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	private void doRebateAndMine(TradeLog trade) {
		long childUserId = trade.getF_user_id();
		if (childUserId <= Contents.BQMEMBER_ADMIN) return;
		
		BQMineSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MINE_SETTING);
		if (setting == null) {
			setting = bqMineSettingService.get();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.MINE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			} else {
				return;
			}
		}
		
		int currencyId = Contents.TRADE_CURRENCY_TYPE_ID;
		BigDecimal tradeFee = trade.getTrade_fee();
		if (trade.getTrade_bfx_fee().compareTo(BigDecimal.ZERO) > 0) {
			BQBfxFeeSetting bfxSetting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
			if (bfxSetting == null) {
				bfxSetting = bqBfxFeeSettingService.get();
				if (bfxSetting != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, bfxSetting);
				}
			}
			
			if (bfxSetting == null) {
				return;
			}
			
			tradeFee = trade.getTrade_bfx_fee();
			currencyId = bfxSetting.getCoin_id();
		}
		
		BQMember childUser = bqMemberService.findUserById(childUserId);
		if (childUser == null) return;
		if (setting.getRebate()) {
			long inviterId = bqMemberInviteLogService.findInviterByUserId(childUserId);
			if (inviterId > 0L) {
				BQMemberLevel maxLevel = bqMemberLevelService.getMaxLevelExclusionState();
				BQMember parentUser = bqMemberService.findUserById(inviterId);
				while (parentUser != null) {
					BQMemberInviteRebateLog record = null;
					try {
						BQMemberLevel childLevel = getLevelByUserBfx(childUser);
						BQMemberLevel userLevel = getLevelByUserBfx(parentUser);
						record = bqMemberInviteRebateLogService.createAndSaveRecord("", parentUser.getF_user_id(), childUser.getF_user_id(), tradeFee, trade.getF_user_id(),
								trade.getTrade_id(), trade.getTransaction_code(), maxLevel.getLevel_id(), currencyId, childLevel, userLevel);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if (record == null) {
						break;
					}
					
					if (record.getUser_id() > 0) {
						childUser = parentUser;
					}
					
					inviterId = bqMemberInviteLogService.findInviterByUserId(parentUser.getF_user_id());
					if (inviterId <= 0L) {
						break;
					}
					
					parentUser = bqMemberService.findUserById(inviterId);
				}
			}
		}
		
		long nowTime = System.currentTimeMillis() / 1000;
		if (setting.getRunning() && setting.getBegin_time() <= nowTime) {
			if (trade.getTrade_fee().compareTo(BigDecimal.ZERO) <= 0) return;
			if (setting.getMined_amount().compareTo(setting.getTotal_amount()) >= 0) return;
			BigDecimal rate = bqMineStageSettingService.getRateByStageAmount(setting.getMined_amount());
			if (rate.compareTo(BigDecimal.ZERO) <= 0) return;

			BigDecimal mine = trade.getTrade_fee().multiply(rate);
			if (mine.add(setting.getMined_amount()).compareTo(setting.getTotal_amount()) > 0) {
				mine = setting.getTotal_amount().subtract(setting.getMined_amount());
			}
			
			if (mine.compareTo(new BigDecimal(0.00000001)) <= 0) return;
			
			try {
				bqMineRecordService.mine(trade, setting.getCurrency_type_id(), mine, rate, setting.getId());
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private void sendEmail(long uid, String code, BigDecimal price, BigDecimal amount, int direction) {
		BQMember member = bqMemberService.findUserById(uid);
		if (member == null) return;
		
		BQQuantSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.QUANT_SETTING);
		if (setting == null) {
			setting = bqQuantSettingService.get("");
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.QUANT_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
		}
		
		if (setting != null) {
			String ids = setting.getUids();
			if (!StringUtil.isEmpty(ids)) {
				String[] users = ids.split(",");
				for (String userId : users) {
					long id = Long.valueOf(userId);
					if (id == uid) return;
				}
			}
		}
		
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
