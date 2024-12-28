package com.btc100.btc_q.timerTask;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberInviteRebateLog;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQMineSetting;
import com.btc100.btc_q.entity.BQMineStageSetting;
import com.btc100.btc_q.entity.BQSpotArbitrageSetting;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQSpotQuantSetting;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.entity.SpotTradeLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQBfxFeeSettingService;
import com.btc100.btc_q.service.BQMemberInviteLogService;
import com.btc100.btc_q.service.BQMemberInviteRebateLogService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMinePersonalAmountService;
import com.btc100.btc_q.service.BQMineRecordService;
import com.btc100.btc_q.service.BQMineSettingService;
import com.btc100.btc_q.service.BQMineStageSettingService;
import com.btc100.btc_q.service.BQSpotArbitrageSettingService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQSpotQuantSettingService;
import com.btc100.btc_q.service.OpenPositionSpotServer;
import com.btc100.btc_q.service.SpotPendingDelegationCacheService;
import com.btc100.btc_q.service.SpotPendingDelegationService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SpotOpenPositionTimerTask extends BaseMemberLevelTask {
	
	private static final Logger logger = Logger.getLogger(SpotOpenPositionTimerTask.class);
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private SpotPendingDelegationCacheService spotPendingDelegationCacheService;
	@Autowired
	private SpotPendingDelegationService spotPendingDelegationService;
	@Autowired
	private OpenPositionSpotServer openPositionSpotServer;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	BQMemberInviteRebateLogService bqMemberInviteRebateLogService;
	@Autowired
	private BQSpotArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private BQSpotQuantSettingService bqQuantSettingService;
	@Autowired
	private BQMemberInviteLogService bqMemberInviteLogService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQMineSettingService bqMineSettingService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;
	@Autowired
	private BQSpotArbitrageSettingService bqSpotArbitrageSettingService;
	@Autowired
	private BQMineStageSettingService bqMineStageSettingService;
	@Autowired
	private BQMineRecordService bqMineRecordService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private BQMinePersonalAmountService bqMinePersonalAmountService;

	private static String[] marketArray = null;
	private static final long QUANT_ID = 6604;
	
	public void run() {
		
		if (marketArray == null) {
			String contracts = UrlConfig.getValue("spotTrade.markets");
			if (!StringUtil.isEmpty(contracts)) {
				marketArray = contracts.split(",");
			}
		}
		
		if (marketArray == null && marketArray.length == 0) return;
		
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}
		
		for (String market : marketArray) {
			BQSpotMarketCoinPair coinPair = null;
			for (BQSpotMarketCoinPair marketCoinPair : coinPairs) {
				if (marketCoinPair.getCoin_pair_name().equals(market)) {
					coinPair = marketCoinPair;
					break;
				}
			}
			
			if (coinPair == null) continue;
			if (coinPair.getSuspend()) continue;
			
			String bqCode = coinPair.getCoin_pair_name();
			BQSpotArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + bqCode);
			
			SpotPendingDelegationCache buycache1 = spotPendingDelegationCacheService.getBuy1(bqCode);
			SpotPendingDelegation sell1pending = spotPendingDelegationService.getSell1(bqCode);
			
			boolean ret = dealBuyCacheAndSell(setting, coinPair, buycache1, sell1pending);
			if (buycache1 != null && sell1pending != null && ret) {
				// 更新最新成交价
				BigDecimal lastPrice = sell1pending.getDelegation_price();
				spyMemcachedClient.set(coinPair.getCoin_pair_name() + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
			}

			/*******************************************************************************************************/

			SpotPendingDelegationCache sellcache1 = spotPendingDelegationCacheService.getSell1(bqCode);
			SpotPendingDelegation buy1pending = spotPendingDelegationService.getBuy1(bqCode);
			
			ret = dealBuyAndSellCache(setting, coinPair, buy1pending, sellcache1);
			if (buy1pending != null && sellcache1 != null && ret) {
				// 更新最新成交价
				BigDecimal lastPrice = buy1pending.getDelegation_price();
				spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
			}
			
			
			/******************* cache->pending *********************/
			if (sell1pending != null) {
				List<SpotPendingDelegationCache> sellCaches = spotPendingDelegationCacheService.findSellAbovePrice(bqCode, sell1pending.getDelegation_price(), 10);
				for (SpotPendingDelegationCache cache : sellCaches) {
					moveCache(cache);
				}
			}
			
			if (buy1pending != null) {
				List<SpotPendingDelegationCache> buyCaches = spotPendingDelegationCacheService.findBuyBelowPrice(bqCode, buy1pending.getDelegation_price(), 10);
				for (SpotPendingDelegationCache cache : buyCaches) {
					moveCache(cache);
				}
			}
			
			/******************* 深度系统下单 *********************/
			
			String code = coinPair.getCoin_pair_name();
			if (setting != null && setting.getRunning() && setting.getDepth_self() && setting.isDepth_reset_open_price() 
					&& setting.getPrice_digits() >= 0 && setting.getAmount_digits() >= 0) {
				
				removePendingAll(setting.getDepth_uid(), coinPair);
				
				BigDecimal openPrice = setting.getOpen_price();
				if (openPrice.compareTo(BigDecimal.ZERO) <= 0) continue;
				
				Map<BigDecimal, BigDecimal> depthSells = new HashMap<>();
				Map<BigDecimal, BigDecimal> depthBuys = new HashMap<>();
				
				BigDecimal buy1 = openPrice.subtract(setting.getDepth_buy_price_gap());
				BigDecimal sell1 = openPrice.add(setting.getDepth_sell_price_gap());
				
				BigDecimal random = new BigDecimal(Math.random());
				BigDecimal buyAmount = setting.getDepth_buy_amount_min().add(random.multiply(setting.getDepth_buy_amount_max().subtract(setting.getDepth_buy_amount_min()))).setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);
				
				random = new BigDecimal(Math.random());
				BigDecimal sellAmount = setting.getDepth_sell_amount_min().add(random.multiply(setting.getDepth_sell_amount_max().subtract(setting.getDepth_sell_amount_min()))).setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);
				
				depthSells.put(sell1, sellAmount);
				depthBuys.put(buy1, buyAmount);
				for (int i = 1; i < setting.getDepth_size(); i++) {
					BigDecimal buyGap = setting.getDepth_buy_price_gap().multiply(new BigDecimal(i));
					BigDecimal sellGap = setting.getDepth_sell_price_gap().multiply(new BigDecimal(i));
					BigDecimal sell = sell1.add(sellGap).setScale(setting.getPrice_digits(), BigDecimal.ROUND_UP);
					BigDecimal buy = buy1.subtract(buyGap).setScale(setting.getPrice_digits(), BigDecimal.ROUND_DOWN);
					
					random = new BigDecimal(Math.random());
					buyAmount = setting.getDepth_buy_amount_min().add(random.multiply(setting.getDepth_buy_amount_max().subtract(setting.getDepth_buy_amount_min()))).setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);
					
					random = new BigDecimal(Math.random());
					sellAmount = setting.getDepth_sell_amount_min().add(random.multiply(setting.getDepth_sell_amount_max().subtract(setting.getDepth_sell_amount_min()))).setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);
					
					depthSells.put(sell, sellAmount);
					depthBuys.put(buy, buyAmount);
				}
				
				addAllPending(setting.getDepth_uid(), depthSells, Contents.DIRECTION_SELL, coinPair, sell1pending);
				addAllPending(setting.getDepth_uid(), depthBuys, Contents.DIRECTION_BUY, coinPair, buy1pending);
				
				setting = bqSpotArbitrageSettingService.getArbitrageSettingByCode(code);
				setting.setDepth_reset_open_price(false);
				bqSpotArbitrageSettingService.update(setting);
				
				spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
		}
		
	}
	
	private void addAllPending(long userId, Map<BigDecimal, BigDecimal> orders, int direction, BQSpotMarketCoinPair bqTransaction, SpotPendingDelegation pending1) {
		BigDecimal changeCacheAmounts = BigDecimal.ZERO;
		BigDecimal changeAmounts = BigDecimal.ZERO;
		List<SpotPendingDelegationCache> caches = new ArrayList<>();
		List<SpotPendingDelegation> pendings = new ArrayList<>();
		
		for (BigDecimal price : orders.keySet()) {
			BigDecimal amount = orders.get(price);
			BigDecimal frozen = amount;
			if (direction == Contents.DIRECTION_BUY) {
				frozen = amount.multiply(price);
			}
			
			if (pending1 == null || (pending1 != null && ((price.compareTo(pending1.getDelegation_price()) >= 0 && direction == Contents.DIRECTION_BUY) 
					|| ((price.compareTo(pending1.getDelegation_price()) <= 0 && direction == Contents.DIRECTION_SELL))))) {
				changeCacheAmounts = changeCacheAmounts.add(frozen);
				SpotPendingDelegationCache cahce = new SpotPendingDelegationCache(bqTransaction.getId(), bqTransaction.getCoin_pair_name(),
						direction, amount, price, BigDecimal.ZERO, amount,
						BigDecimal.ZERO, userId, BigDecimal.ZERO);
				
				caches.add(cahce);
			} else {
				changeAmounts = changeAmounts.add(frozen);
				SpotPendingDelegation pending = new SpotPendingDelegation();
				pending.setCoin_pair_id(bqTransaction.getId());
				pending.setCoin_pair_name(bqTransaction.getCoin_pair_name());
				pending.setDirection(direction);
				pending.setDelegation_count(amount);
				pending.setUndeal_count(amount);
				pending.setDelegation_price(price);
				pending.setDeal_count(BigDecimal.ZERO);
				pending.setCommission_rate(BigDecimal.ZERO);
				pending.setF_frozenremain(BigDecimal.ZERO);
				pending.setF_user_id(userId);
				pending.setPending_no(RandomUtil.getPendingNo());
				
				pendings.add(pending);
			}
		}
		
		try {
			spotPendingDelegationCacheService.insertCacheForDepth(caches, userId, changeCacheAmounts, direction, bqTransaction);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		try {
			spotPendingDelegationService.insertPendingForDepth(pendings, userId, changeAmounts, direction, bqTransaction);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
		
	private boolean removePendingAll(long userId, BQSpotMarketCoinPair coinPair) {
		// 注：只有开仓单，不用锁持单
		boolean bRet = true;
		String code = coinPair.getCoin_pair_name();
		List<SpotPendingDelegationCache> plistCache = spotPendingDelegationCacheService.getAllByUserId(0, code, userId);
		for (SpotPendingDelegationCache pendingDelegationCache : plistCache) {
			String lock = spotPendingDelegationCacheService.lock(pendingDelegationCache.getCoin_pair_name(), pendingDelegationCache.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					spotPendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, false, coinPair);
				} catch (Exception e) {
					bRet = false;
					e.printStackTrace();
				} finally {
					if (pendingDelegationCache != null) {
						spotPendingDelegationCacheService.unLock(pendingDelegationCache.getCoin_pair_name(), pendingDelegationCache.getDelegation_id(), lock);
					}
				}
			}
		}
		
		List<SpotPendingDelegation> plist = spotPendingDelegationService.getAllByUserId(0, code, userId);
		for (SpotPendingDelegation pendingDelegation : plist) {
			String lock = spotPendingDelegationService.lock(pendingDelegation.getCoin_pair_name(), pendingDelegation.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					spotPendingDelegationService.cancelPending_one(pendingDelegation, false, coinPair);
				} catch (Exception e) {
					bRet = false;
					e.printStackTrace();
				} finally {
					if (pendingDelegation != null) {
						spotPendingDelegationService.unLock(pendingDelegation.getCoin_pair_name(), pendingDelegation.getDelegation_id(), lock);
					}
				}
			}
		}
		
		return bRet;
	}
	
	private boolean dealBuyCacheAndSell(BQSpotArbitrageSetting setting, BQSpotMarketCoinPair coinPair, SpotPendingDelegationCache buycache, SpotPendingDelegation sell) {
		boolean ret = false;
		if (buycache != null && sell != null) {
			// 买1价>= 卖1价
			if (buycache.getDelegation_price().compareTo(sell.getDelegation_price()) >= 0) {
				if (checkIfAllQuantUserKC(coinPair, sell, buycache)) return false;
				if (checkIfAllDepthUserKC(setting, coinPair, sell, buycache)) return false;
				if (!checkDepthAccountBalance(coinPair, sell, buycache)) return false; 
				
				List<SpotTradeLog> trades = new ArrayList<SpotTradeLog>();
				String flagbuy = "";
				flagbuy = spotPendingDelegationCacheService.lock(coinPair.getCoin_pair_name(), buycache.getDelegation_id());
				if (StringUtil.isEmpty(flagbuy)) return ret;
				
				try {
					String flagsell = spotPendingDelegationService.lock(coinPair.getCoin_pair_name(), sell.getDelegation_id());
					if (StringUtil.isEmpty(flagsell)) {
						return ret;
					}
					
					try {
						// 重新获取，避免改变flag之前挂单被修改
						long buycacheId = buycache.getDelegation_id();
						long sellId = sell.getDelegation_id();
						buycache = spotPendingDelegationCacheService.findPendingById(buycacheId, coinPair.getCoin_pair_name());
						sell = spotPendingDelegationService.findPendingById(sellId, coinPair.getCoin_pair_name());
						
						if (buycache == null || sell == null) {
							return ret;
						}
						
						if (buycache.getUndeal_count().signum() <= 0 
								|| buycache.getDeal_count().signum() < 0 
								|| buycache.getDelegation_count().signum() <= 0
								|| buycache.getDelegation_price().signum() <= 0) {
								openPositionSpotServer.moveSpotPendingLog(buycache);
								
							logger.error(buycache.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "买单出错：转移到挂单日志表里");
							return ret;
						}
						
						if (sell.getUndeal_count().signum() <= 0 || sell.getDeal_count().signum() < 0 || sell.getDelegation_count().signum() < 0
								|| sell.getDelegation_price().signum() <= 0) {
							openPositionSpotServer.moveSpotPendingLog(sell);
							logger.error(sell.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "卖单出错：转移到挂单日志表里");
							return ret;
						}
						
						BigDecimal buyTosell = buycache.getUndeal_count().subtract(sell.getUndeal_count());
						if (buyTosell.signum() < 0) {
							// buycache1数量小，吃掉buycache1
							try {
								trades = openPositionSpotServer.eatBuyCache1(coinPair, buycache, sell, false);
								ret = true;
							} catch (ServiceException e) {
								if (e.getMessage().equals("buy")) {
									openPositionSpotServer.moveSpotPendingLog(buycache);
									logger.error(buycache.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "买单出错：转移到挂单日志表里");
								} else {
									openPositionSpotServer.moveSpotPendingLog(sell);
									logger.error(sell.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "卖单出错：转移到挂单日志表里");
								}
							}
						} else if (buyTosell.signum() > 0) {
							// buycache1数量大，吃掉sell1
							try {
								trades = openPositionSpotServer.eatSell1(coinPair, buycache, sell);
								ret = true;
							} catch (ServiceException e) {
								if (e.getMessage().equals("buy")) {
									openPositionSpotServer.moveSpotPendingLog(buycache);
									logger.error(buycache.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "买单出错：转移到挂单日志表里");
								} else {
									openPositionSpotServer.moveSpotPendingLog(sell);
									logger.error(sell.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "卖单出错：转移到挂单日志表里");
								}
							}
						} else {
							try {
								trades = openPositionSpotServer.eatBuyCache1(coinPair, buycache, sell, true);
								ret = true;
							} catch (ServiceException e) {
								if (e.getMessage().equals("buy")) {
									openPositionSpotServer.moveSpotPendingLog(buycache);
									logger.error(buycache.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "买单出错：转移到挂单日志表里");
								} else {
									openPositionSpotServer.moveSpotPendingLog(sell);
									logger.error(sell.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "卖单出错：转移到挂单日志表里");
								}
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (sell != null) {
							spotPendingDelegationService.unLock(coinPair.getCoin_pair_name(), sell.getDelegation_id(), flagsell);
						}
					}
				 
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (buycache != null) {
						spotPendingDelegationCacheService.unLock(coinPair.getCoin_pair_name(), buycache.getDelegation_id(), flagbuy);
					}
				}
				
				for (SpotTradeLog trade : trades) {
					if (trade.getTrade_fee().compareTo(BigDecimal.ZERO) > 0 || trade.getTrade_bfx_fee().compareTo(BigDecimal.ZERO) > 0) {
						doRebateAndMine(trade);
					}
				}
				
				/*
				if (buycache.getF_user_id() == 8734 || sell.getF_user_id() == 8734) {
					BigDecimal pendignFrozen = spotPendingDelegationCacheService.findSumBuyFrozen(coinPair.getCoin_pair_name(), 8734);
					pendignFrozen = pendignFrozen.add(spotPendingDelegationService.findSumBuyFrozen(coinPair.getCoin_pair_name(), 8734));
					
					BigDecimal sellFrozen = spotPendingDelegationCacheService.findSumSellFrozen(coinPair.getCoin_pair_name(), 8734);
					sellFrozen = sellFrozen.add(spotPendingDelegationService.findSumSellFrozen(coinPair.getCoin_pair_name(), 8734));
					
					BQAccount account = bqAccountService.getAccount(8734, coinPair.getMarket_coin_id());
					BQAccount act = bqAccountService.getAccount(8734, coinPair.getCoin_id());
					if (account.getBtc_locked().compareTo(pendignFrozen) < 0 || act.getBtc_locked().compareTo(sellFrozen) < 0) {
						System.out.println("买挂单冻结：" + pendignFrozen.doubleValue() + ", 现有usdt冻结：" + account.getBtc_locked().doubleValue());
						System.out.println("卖挂单冻结：" + sellFrozen.doubleValue() + ", 现有apo冻结：" + act.getBtc_locked().doubleValue());
						System.out.println("当前处理挂单，buycache：" + buycache.getDelegation_id() + ", " + buycache.getPending_no() + ", " + buycache.getF_user_id() + ", " + buycache.getUndeal_count());
						System.out.println("当前处理挂单，sell：" + sell.getDelegation_id() + ", " + sell.getPending_no() + ", " + sell.getF_user_id() + ", " + sell.getUndeal_count());
						
						System.out.println("尚有caches: ");
						List<SpotPendingDelegationCache> caches = spotPendingDelegationCacheService.findPendingByUserIdAndTime(coinPair.getCoin_pair_name(), 8734, 0, 0, 0, false);
						for (SpotPendingDelegationCache cache : caches) {
							System.out.println("" + cache.getDelegation_id() + ", " + cache.getPending_no() + ", " + cache.getF_user_id() + ", " + cache.getUndeal_count());
						}
						
						System.out.println("尚有pendings: ");
						List<SpotPendingDelegation> penings = spotPendingDelegationService.findPendingByUserIdAndTime(coinPair.getCoin_pair_name(), 8734, 0, 0, 0, 0, false);
						for (SpotPendingDelegation pening : penings) {
							System.out.println("" + pening.getDelegation_id() + ", " + pening.getPending_no() + ", " + pening.getF_user_id() + ", " + pening.getUndeal_count());
						}
					}
					
				}
				*/
			} else {
				moveCache(buycache);
			}
		} else if (buycache != null && sell == null) {
			moveCache(buycache);
		} else if (buycache == null && sell != null) {
			// 什么也不做
		} else {
			// 什么也不做
		}
		
		return ret;
	}
	
	private boolean dealBuyAndSellCache(BQSpotArbitrageSetting setting, BQSpotMarketCoinPair coinPair, SpotPendingDelegation buy, SpotPendingDelegationCache sellcache) {
		boolean ret = false;
		if (sellcache != null && buy != null) {
			// 卖1价<=买1价
			if (sellcache.getDelegation_price().compareTo(buy.getDelegation_price()) != 1) {
				if (checkIfAllQuantUserKC(coinPair, buy, sellcache)) return false;
				if (checkIfAllDepthUserKC(setting, coinPair, buy, sellcache)) return false;
				if (!checkDepthAccountBalance(coinPair, buy, sellcache)) return false; 
				
				List<SpotTradeLog> trades = new ArrayList<SpotTradeLog>();
				String flagbuy = spotPendingDelegationService.lock(coinPair.getCoin_pair_name(), buy.getDelegation_id());
				if (StringUtil.isEmpty(flagbuy)) {
					return ret;
				}
				
				try {
					String flagsell = "";
					flagsell = spotPendingDelegationCacheService.lock(coinPair.getCoin_pair_name(), sellcache.getDelegation_id());
					if (StringUtil.isEmpty(flagsell)) {
						return ret;
					}
					
					try {
						// 重新获取，避免改变flag之前挂单被修改
						long sellcacheId = sellcache.getDelegation_id();
						long buyId = buy.getDelegation_id();
						buy = spotPendingDelegationService.findPendingById(buyId, coinPair.getCoin_pair_name());
						sellcache = spotPendingDelegationCacheService.findPendingById(sellcacheId, coinPair.getCoin_pair_name());
						
						if (sellcache == null || buy == null) {
							return ret;
						}
			
						if (buy.getUndeal_count().signum() <= 0 || buy.getDeal_count().signum() < 0 || buy.getDelegation_count().signum() <= 0
								|| buy.getDelegation_price().signum() <= 0) {
							openPositionSpotServer.moveSpotPendingLog(buy);
							logger.error(buy.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "买单出错：转移到挂单日志表里");
							return ret;
						}
						
						if (sellcache.getUndeal_count().signum() <= 0 || sellcache.getDeal_count().signum() < 0 || sellcache.getDelegation_count().signum() <= 0
								|| sellcache.getDelegation_price().signum() <= 0) {
							openPositionSpotServer.moveSpotPendingLog(sellcache);
							logger.error(sellcache.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "卖单出错：转移到挂单日志表里");
							return ret;
						}
						
						int sellToBuy = sellcache.getUndeal_count().compareTo(buy.getUndeal_count());
						if (sellToBuy == 1) {
							// //把买1吃掉//sellcache的数量大，吃掉buy
							try {
								trades = openPositionSpotServer.eatBuy1(coinPair, buy, sellcache);
								ret = true;
							} catch (ServiceException e) {
								if (e.getMessage().equals("buy")) {
									openPositionSpotServer.moveSpotPendingLog(buy);
									logger.error(buy.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "买单出错：转移到挂单日志表里");
								} else {
									openPositionSpotServer.moveSpotPendingLog(sellcache);
									logger.error(sellcache.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "卖单出错：转移到挂单日志表里");
								}
							}
						} else if (sellToBuy == -1) {
							// 把买1吃掉//sellcache的数量小，吃掉sellcache
							try {
								trades = openPositionSpotServer.eatSellCache1(coinPair, buy, sellcache, false);
								ret = true;
							} catch (ServiceException e) {
								if (e.getMessage().equals("buy")) {
									openPositionSpotServer.moveSpotPendingLog(buy);
									logger.error(buy.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "买单出错：转移到挂单日志表里");
								} else {
									openPositionSpotServer.moveSpotPendingLog(sellcache);
									logger.error(sellcache.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "卖单出错：转移到挂单日志表里");
								}
							}
						} else {
							try {
								trades = openPositionSpotServer.eatSellCache1(coinPair, buy, sellcache, true);
								ret = true;
							} catch (ServiceException e) {
								if (e.getMessage().equals("buy")) {
									openPositionSpotServer.moveSpotPendingLog(buy);
									logger.error(buy.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "买单出错：转移到挂单日志表里");
								} else {
									openPositionSpotServer.moveSpotPendingLog(sellcache);
									logger.error(sellcache.getF_user_id() + "的" + coinPair.getCoin_pair_name() + "卖单出错：转移到挂单日志表里");
								}
							}
						}
								
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (sellcache != null) {
							spotPendingDelegationCacheService.unLock(coinPair.getCoin_pair_name(), sellcache.getDelegation_id(), flagsell);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (buy != null) {
						spotPendingDelegationService.unLock(coinPair.getCoin_pair_name(), buy.getDelegation_id(), flagbuy);
					}
				}
				
				for (SpotTradeLog trade : trades) {
					if (trade.getTrade_fee().compareTo(BigDecimal.ZERO) > 0 || trade.getTrade_bfx_fee().compareTo(BigDecimal.ZERO) > 0) {
						doRebateAndMine(trade);
					}
				}
				/*
				if (sellcache.getF_user_id() == 8734 || buy.getF_user_id() == 8734) {
					BigDecimal pendignFrozen = spotPendingDelegationCacheService.findSumBuyFrozen(coinPair.getCoin_pair_name(), 8734);
					pendignFrozen = pendignFrozen.add(spotPendingDelegationService.findSumBuyFrozen(coinPair.getCoin_pair_name(), 8734));
					
					BigDecimal sellFrozen = spotPendingDelegationCacheService.findSumSellFrozen(coinPair.getCoin_pair_name(), 8734);
					sellFrozen = sellFrozen.add(spotPendingDelegationService.findSumSellFrozen(coinPair.getCoin_pair_name(), 8734));
					
					BQAccount account = bqAccountService.getAccount(8734, coinPair.getMarket_coin_id());
					BQAccount act = bqAccountService.getAccount(8734, coinPair.getCoin_id());
					if (account.getBtc_locked().compareTo(pendignFrozen) < 0 || act.getBtc_locked().compareTo(sellFrozen) < 0) {
						System.out.println("买挂单冻结：" + pendignFrozen.doubleValue() + ", 现有usdt冻结：" + account.getBtc_locked().doubleValue());
						System.out.println("卖挂单冻结：" + sellFrozen.doubleValue() + ", 现有apo冻结：" + act.getBtc_locked().doubleValue());
						System.out.println("当前处理挂单，sellcache：" + sellcache.getDelegation_id() + ", " + sellcache.getPending_no() + ", " + sellcache.getF_user_id() + ", " + sellcache.getUndeal_count());
						System.out.println("当前处理挂单，buy：" + buy.getDelegation_id() + ", " + buy.getPending_no() + ", " + buy.getF_user_id() + ", " + buy.getUndeal_count());
						
						System.out.println("尚有caches: ");
						List<SpotPendingDelegationCache> caches = spotPendingDelegationCacheService.findPendingByUserIdAndTime(coinPair.getCoin_pair_name(), 8734, 0, 0, 0, false);
						for (SpotPendingDelegationCache cache : caches) {
							System.out.println("" + cache.getDelegation_id() + ", " + cache.getPending_no() + ", " + cache.getF_user_id() + ", " + cache.getUndeal_count());
						}
						
						System.out.println("尚有pendings: ");
						List<SpotPendingDelegation> penings = spotPendingDelegationService.findPendingByUserIdAndTime(coinPair.getCoin_pair_name(), 8734, 0, 0, 0, 0, false);
						for (SpotPendingDelegation pening : penings) {
							System.out.println("" + pening.getDelegation_id() + ", " + pening.getPending_no() + ", " + pening.getF_user_id() + ", " + pening.getUndeal_count());
						}
					}
					
				}
				*/
			} else {
				moveCache(sellcache);
			}
		} else if (sellcache != null && buy == null) {
			moveCache(sellcache);
		} else if (sellcache == null && buy != null) {
			// do nothing
		} else {
			// do nothing
		}
		
		return ret;
	}
	
	private void moveCache(SpotPendingDelegationCache cache) {
		String flagbuy = spotPendingDelegationCacheService.lock(cache.getCoin_pair_name(), cache.getDelegation_id());
		if (StringUtil.isEmpty(flagbuy)) {
			return;
		}
		
		long buycache1Id = cache.getDelegation_id();
		cache = spotPendingDelegationCacheService.findPendingById(buycache1Id, cache.getCoin_pair_name());
		if (cache == null) {
			return;
		}
		
		try {
			openPositionSpotServer.moveCache1(cache);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cache != null) {
				spotPendingDelegationCacheService.unLock(cache.getCoin_pair_name(), cache.getDelegation_id(), flagbuy);
			}
		}
	}
	
	private boolean checkDepthAccountBalance(BQSpotMarketCoinPair coinPair, SpotPendingDelegation pd, SpotPendingDelegationCache pdc) {
		String code = coinPair.getCoin_pair_name();
		String marketName = coinPair.getMarket_coin_name();
		String coinName = coinPair.getCoin_name();
		BQSpotArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + code);
		if (setting == null) {
			setting = bqArbitrageSettingService.getArbitrageSettingByCode(code);
			if (setting == null) {
				setting = new BQSpotArbitrageSetting();
				setting.setRunning(false);
				setting.setScalping(false);
				setting.setHedging(false);
				setting.setRisk_manage(false);
				setting.setDepth_uid(0L);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
		}
		
		if (setting == null || !setting.getRisk_manage()) return true;
		
		BigDecimal amount = pd.getUndeal_count();
		if (amount.compareTo(pdc.getUndeal_count()) > 0) amount = pdc.getUndeal_count();
		
		int direction = Contents.DIRECTION_BUY;
		BigDecimal price = pd.getDelegation_price();
		if (pd.getF_user_id() == setting.getDepth_uid() && pdc.getF_user_id() != setting.getDepth_uid()) {
			direction = pdc.getDirection();
		} else if (pd.getF_user_id() != setting.getDepth_uid() && pdc.getF_user_id() == setting.getDepth_uid()) {
			direction = pd.getDirection();
		} else {
			return true;
		}
		
		String symbol = marketName.toLowerCase();
		if (direction == Contents.DIRECTION_SELL) {
			symbol = coinName.toLowerCase();
		}
		
		BigDecimal balance = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_HEDGING_BALANCE + symbol);
		if (symbol.equals("usdt") && coinName.equalsIgnoreCase("okb")) {
			balance = spyMemcachedClient.get("okex_" + BTCQMemecacheKeys.SPOT_HEDGING_BALANCE + symbol);
		}
		
		if (balance == null) {
			 balance = BigDecimal.ZERO;
		}
		
		if (direction == Contents.DIRECTION_BUY) {
			price = price.multiply(BigDecimal.ONE.subtract(setting.getHedging_buy_range().multiply(new BigDecimal(0.01))));
			BigDecimal canOpen = balance.divide(price, coinPair.getDecimals(), BigDecimal.ROUND_DOWN);
			if (amount.compareTo(canOpen) > 0) {
				if (pdc.getF_user_id() != setting.getDepth_uid()) {
					cancelPendingCache(pdc, coinPair);
					sendEmail(pdc.getF_user_id(), pdc.getCoin_pair_name(), pdc.getDelegation_price(), pdc.getUndeal_count(), pdc.getDirection());
				} else {
					cancelPending(pd, coinPair);
					sendEmail(pd.getF_user_id(), pd.getCoin_pair_name(), pd.getDelegation_price(), pd.getUndeal_count(), pd.getDirection());
				}
				
				return false;
			}
		} else {
			if (amount.compareTo(balance) > 0) {
				if (pdc.getF_user_id() != setting.getDepth_uid()) {
					cancelPendingCache(pdc, coinPair);
					sendEmail(pdc.getF_user_id(), pdc.getCoin_pair_name(), pdc.getDelegation_price(), pdc.getUndeal_count(), pdc.getDirection());
				} else {
					cancelPending(pd, coinPair);
					sendEmail(pd.getF_user_id(), pd.getCoin_pair_name(), pd.getDelegation_price(), pd.getUndeal_count(), pd.getDirection());
				}
				
				return false;
			}
		}
		
		
		return true;
	}
	
	private boolean checkIfAllDepthUserKC(BQSpotArbitrageSetting setting, BQSpotMarketCoinPair coinPair, SpotPendingDelegation pd, SpotPendingDelegationCache pdc) {
		if (pd.getF_user_id() != pdc.getF_user_id()) return false;
		if (setting == null || !setting.getRunning() || setting.isCan_trade_self()) return false;
		
		long depthUid = setting.getDepth_uid();
		if (pd.getF_user_id() == depthUid) {
			cancelPendingCache(pdc, coinPair);
			cancelPending(pd, coinPair);
			return true;
		}
		
		return false;
	}
	
	private boolean checkIfAllQuantUserKC(BQSpotMarketCoinPair coinPair, SpotPendingDelegation pd, SpotPendingDelegationCache pdc) {
		if (pd.getF_user_id() != pdc.getF_user_id()) return false;
		
		if (pd.getF_user_id() == QUANT_ID) {
			cancelPending(pd, coinPair);
			return true;
		}
		
		return false;
	}
	
	private void cancelPendingCache(SpotPendingDelegationCache pending, BQSpotMarketCoinPair coinPair) {
		String lock = spotPendingDelegationCacheService.lock(pending.getCoin_pair_name(), pending.getDelegation_id());
		if (!StringUtil.isEmpty(lock)) {
			try {
				spotPendingDelegationCacheService.cancelPendingCache_one(pending, false, coinPair);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pending != null) {
					spotPendingDelegationCacheService.unLock(pending.getCoin_pair_name(), pending.getDelegation_id(), lock);
				}
			}
		}
	}
	
	private void cancelPending(SpotPendingDelegation pending, BQSpotMarketCoinPair coinPair) {
		String lock = spotPendingDelegationService.lock(pending.getCoin_pair_name(), pending.getDelegation_id());
		if (!StringUtil.isEmpty(lock)) {
			try {
				spotPendingDelegationService.cancelPending_one(pending, false, coinPair);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pending != null) {
					spotPendingDelegationService.unLock(pending.getCoin_pair_name(), pending.getDelegation_id(), lock);
				}
			}
		}
	}
	
	private void doRebateAndMine(SpotTradeLog trade) {
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
		
		int currencyId = trade.getCoin_id();
		if (trade.getTrade_trans_dirction() == Contents.DIRECTION_SELL) {
			currencyId = trade.getMarket_coin_id();
		}
		
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
								trade.getTrade_id(), trade.getCoin_pair_name(), maxLevel.getLevel_id(), currencyId, childLevel, userLevel);
						
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

		//绑定手机用户具有挖矿权限
		long nowTime = System.currentTimeMillis() / 1000;
		if (setting.getRunning() && setting.getBegin_time() <= nowTime) {
			BQMemberSafeSettings safeSettings = bqMemberSafeSettingsService.getById(childUserId);
			if (safeSettings != null && (safeSettings.getF_user_safetype() == 1 || safeSettings.getF_user_safetype() == 3)) {
				if (trade.getTrade_fee().compareTo(BigDecimal.ZERO) <= 0) return;
				if (setting.getMined_amount().compareTo(setting.getTotal_amount()) >= 0) return;
				BigDecimal fee = trade.getTrade_trans_dirction() == 1 ?
						trade.getTrade_fee().multiply(trade.getTrade_average_price()) : trade.getTrade_fee();

				List<Map<String, BigDecimal>> list = getStageCharging(setting.getMined_amount(), fee);
				if (list.size() > 0) {
					for (Map<String, BigDecimal> map : list) {
						BigDecimal mine = map.get("mine");
						BigDecimal rate = map.get("rate");

						if (mine.add(setting.getMined_amount()).compareTo(setting.getTotal_amount()) > 0) {
							mine = setting.getTotal_amount().subtract(setting.getMined_amount());
						}

						BigDecimal userMinedAmount = bqMinePersonalAmountService.getMinedAmountByUid(childUserId);
						if (mine.add(userMinedAmount).compareTo(setting.getPersonal_max_amount()) > 0) {
							mine = setting.getPersonal_max_amount().subtract(userMinedAmount);
						}

						if (mine.compareTo(new BigDecimal("0.00000001")) <= 0) {return;}

						try {
							bqMineRecordService.mine(trade, setting.getCurrency_type_id(), mine, rate, setting.getId(), setting.getPersonal_max_amount(), fee);
						} catch (ServiceException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	}

	/**
	 * 获取阶段计费方式
	 * @param minedAmount 已挖矿数量
	 * @param fee 交易的手续费
	 * @return
	 */
	private List<Map<String, BigDecimal>> getStageCharging(BigDecimal minedAmount, BigDecimal fee) {
		List<Map<String, BigDecimal>> list = new ArrayList<>();

		BQMineStageSetting firstSetting = bqMineStageSettingService.getByStageAmount(minedAmount);
		if (firstSetting == null || firstSetting.getRate().compareTo(BigDecimal.ZERO) <= 0) {
			return list;
		}

		BigDecimal mine = fee.multiply(firstSetting.getRate());
		List<Map<String, BigDecimal>> newCharging = new ArrayList<>();
		BQMineStageSetting nextSetting = bqMineStageSettingService.getNextSetting(firstSetting.getNum());
		if (nextSetting == null) {
			return list;
		}
		BigDecimal realMine = mine.add(minedAmount).subtract(nextSetting.getstage_amount());
		if (realMine.signum() > 0) {
			mine = mine.subtract(realMine);
			newCharging = getStageCharging(nextSetting.getstage_amount(), fee.subtract(mine.divide(firstSetting.getRate(), 6, BigDecimal.ROUND_DOWN)));
		}

		Map<String, BigDecimal> map = new HashMap<>();
		map.put("rate", firstSetting.getRate());
		map.put("mine", mine);
		list.add(map);
		if (newCharging.size() > 0) {
			list.addAll(newCharging);
		}
		return list;
	}



	
	private void sendEmail(long uid, String code, BigDecimal price, BigDecimal amount, int direction) {
		BQMember member = bqMemberService.findUserById(uid);
		if (member == null) return;
		
		BQSpotQuantSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_QUANT_SETTING);
		if (setting == null) {
			setting = bqQuantSettingService.get();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_QUANT_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
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
		String type = "买入";
		if (direction == Contents.DIRECTION_SELL) {
			type = "卖出";
		}
		
		StringBuffer sb = new StringBuffer("尊敬的xkd用户，您好！<br>");
        sb.append("&nbsp;&nbsp;&nbsp;&nbsp;风控系统当前限制" + code.toUpperCase() + "币币交易可" + type + "额度为0，您委托价格" + price.doubleValue() + "usdt，数量" + amount.doubleValue() + "个的委托单，");
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
