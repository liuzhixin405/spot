package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQBfxFeeRecordDao;
import com.btc100.btc_q.dao.BQBfxFeeSettingDao;
import com.btc100.btc_q.dao.BQMemberDao;
import com.btc100.btc_q.dao.BQSpotArbitrageSettingDao;
import com.btc100.btc_q.dao.BQSpotHedgingRecordDao;
import com.btc100.btc_q.dao.BQSpotMarketCoinPairDao;
import com.btc100.btc_q.dao.SpotAccountLogDao;
import com.btc100.btc_q.dao.SpotMemberTradeDao;
import com.btc100.btc_q.dao.SpotMemberTradeDetailDao;
import com.btc100.btc_q.dao.SpotPendingDelegationCacheDao;
import com.btc100.btc_q.dao.SpotPendingDelegationDao;
import com.btc100.btc_q.dao.SpotPendingLogDao;
import com.btc100.btc_q.dao.SpotTradeLogDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQBfxFeeRecord;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQSpotArbitrageSetting;
import com.btc100.btc_q.entity.BQSpotHedgingRecord;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.SpotAccountLog;
import com.btc100.btc_q.entity.SpotMemberTrade;
import com.btc100.btc_q.entity.SpotMemberTradeDetail;
import com.btc100.btc_q.entity.SpotPendingDelegation;
import com.btc100.btc_q.entity.SpotPendingDelegationCache;
import com.btc100.btc_q.entity.SpotPendingLog;
import com.btc100.btc_q.entity.SpotTradeLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.OpenPositionSpotServer;
import com.btc100.btc_q.util.RandomUtil;

/**
 * 开仓服务类(spot)
 * 
 */
@Service("openPositionSpotServer")
public class OpenPositionSpotServerImpl implements OpenPositionSpotServer {
	private static final Logger logger = Logger.getLogger(OpenPositionServerImpl.class);
	@Autowired
	private SpotPendingDelegationCacheDao<SpotPendingDelegationCache> spotPendingDelegationCacheDao;
	@Autowired
	private SpotPendingDelegationDao<SpotPendingDelegation> spotPendingDelegationDao;
	@Autowired
	private SpotPendingLogDao<SpotPendingLog> spotPendingLogDao;
	@Autowired
	private SpotTradeLogDao<SpotTradeLog> spotTradeLogDao;
	@Autowired
	private SpotMemberTradeDao<SpotMemberTrade> spotMemberTradeDao;
	@Autowired
	private SpotMemberTradeDetailDao<SpotMemberTradeDetail> spotMemberTradeDetailDao;
	@Autowired
	private BQMemberDao<BQMember> bqMemberDao;
	@Autowired
	private SpotAccountLogDao<SpotAccountLog> spotAccountLogDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	BQSpotMarketCoinPairDao<BQSpotMarketCoinPair> bqSpotMarketCoinPairDao;
	@Autowired
	SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotHedgingRecordDao<BQSpotHedgingRecord> bqSpotHedgingRecordDao;
	@Autowired
	private BQSpotArbitrageSettingDao<BQSpotArbitrageSetting> bqSpotArbitrageSettingDao;
	@Autowired
	private BQBfxFeeSettingDao<BQBfxFeeSetting> bqBfxFeeSettingDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private BQBfxFeeRecordDao<BQBfxFeeRecord> bqBfxFeeRecordDao;
	
	private BigDecimal doBfxFee(String code, BigDecimal fee, BQMember user) {
		if (user == null || user.getF_fee_rate_type() != 1 || user.getF_user_oms() != 0) return null;
		
		BQBfxFeeSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (setting == null) {
			setting = bqBfxFeeSettingDao.get();
			if (setting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
		}
		
		if (setting == null || setting.getFee_state() != 1 || setting.getBfx_avg_price().compareTo(BigDecimal.ZERO) <= 0) return null;
		BigDecimal bfxFeeAmount = fee.multiply(setting.getDiscount()).divide(setting.getBfx_avg_price(), 8, BigDecimal.ROUND_UP);
		
		BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(user.getF_user_id(), setting.getCoin_id());
		if (bqAccount.getBtc().compareTo(bfxFeeAmount) < 0) return null;
		
		int ret = bqAccountDao.updateAmount(user.getF_user_id(), setting.getCoin_id(), bfxFeeAmount.negate(), BigDecimal.ZERO);
		if (ret > 0) {
			long currTime = bqAccountAmountLogDao.getUnix_TIMESTAMP();
			BQAccountAmountLog pAccountLog = new BQAccountAmountLog("bfx抵扣手续费，数量" + bfxFeeAmount.doubleValue(), setting.getCoin_id(), 0, -1,
					bqAccount.getBtc(), bfxFeeAmount, bqAccount.getBtc().subtract(bfxFeeAmount), bqAccount.getBtc_locked(), bqAccount.getBtc_locked(), 
					currTime, user.getF_user_id(), RandomUtil.getTransNo(), "抵扣交易手续费", 0);
			bqAccountAmountLogDao.save(pAccountLog);
		
			BQBfxFeeRecord record = new BQBfxFeeRecord();
			record.setCoin_id(setting.getCoin_id());
			record.setFee(fee);
			record.setDiscount(setting.getDiscount());
			record.setF_code(code);
			record.setAmount(bfxFeeAmount);
			record.setPrice(setting.getBfx_avg_price());
			record.setTrade_time(currTime);
			record.setF_user_id(user.getF_user_id());
			bqBfxFeeRecordDao.save(record);
			
			return bfxFeeAmount;
		}
		
		return null;
	}

	private SpotTradeLog doTrade(BQMember user, BQSpotMarketCoinPair coinPair, BigDecimal feeRate, long uid,  BigDecimal tradeCount,
			BigDecimal tradePrice, BigDecimal delegationPrice, int direct, long pendingNo) throws ServiceException {
		
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal spotChange = BigDecimal.ZERO;
		BigDecimal fundChange = BigDecimal.ZERO;
		BigDecimal spotFrozenChange = BigDecimal.ZERO;
		BigDecimal fundFrozenChange = BigDecimal.ZERO;
		BigDecimal tradeAmount = BigDecimal.ZERO;
		String direction = "";
		BigDecimal bfxFee = BigDecimal.ZERO;
		if (direct == Contents.DIRECTION_BUY) {
			fee = tradeCount.multiply(feeRate);
			tradeAmount = tradeCount.multiply(tradePrice);
			
			if (fee.compareTo(BigDecimal.ZERO) > 0) {
				bfxFee = doBfxFee(coinPair.getCoin_pair_name(), tradeAmount.multiply(feeRate), user);
				if (bfxFee != null) {
					fee = BigDecimal.ZERO;
				} else {
					bfxFee = BigDecimal.ZERO;
				}
			}
			
			spotChange = tradeCount.subtract(fee);
			if (delegationPrice.compareTo(tradePrice) > 0) {
				BigDecimal diff = tradeCount.multiply(delegationPrice.subtract(tradePrice));
				fundChange = fundChange.add(diff);
				fundFrozenChange = diff.negate();
			}
			
			fundFrozenChange = fundFrozenChange.add(tradeAmount.negate());
			direction = "买入";
		} else {
			tradeAmount = tradeCount.multiply(tradePrice);
			fundChange = tradeAmount;
			fee = fundChange.multiply(feeRate);
			
			if (fee.compareTo(BigDecimal.ZERO) > 0) {
				bfxFee = doBfxFee(coinPair.getCoin_pair_name(), fee, user);
				if (bfxFee != null) {
					fee = BigDecimal.ZERO;
				} else {
					bfxFee = BigDecimal.ZERO;
				}
			}
			
			fundChange = fundChange.subtract(fee);
			spotFrozenChange = tradeCount.negate();
			direction = "卖出";
		}
	
		SpotTradeLog spotTradeLog = new SpotTradeLog(coinPair.getId(), coinPair.getCoin_pair_name(), direct, tradePrice, 
				tradeCount, fee, uid, coinPair.getCoin_id(), coinPair.getMarket_coin_id(), tradeAmount, pendingNo);
		spotTradeLog.setTrade_bfx_fee(bfxFee);
		spotTradeLog = spotTradeLogDao.insertLog(spotTradeLog);

		SpotMemberTrade memberTrade = new SpotMemberTrade();
		memberTrade.setF_user_id(uid);
		memberTrade.setTrade_id(spotTradeLog.getTrade_id());
		spotMemberTradeDao.insertSpotMemberTrade(memberTrade, coinPair.getCoin_pair_name());
		
		BQAccount spotAccount = bqAccountDao.getByUerAndCurrencyId(uid, coinPair.getCoin_id());
		BQAccount fundAccount = bqAccountDao.getByUerAndCurrencyId(uid, coinPair.getMarket_coin_id());
		
		if (bqAccountDao.updateAmount(uid, coinPair.getMarket_coin_id(), fundChange, fundFrozenChange) <= 0) {
			logger.error("用户：" + uid + direction + coinPair.getCoin_name() + "，更新资金账户出错：" + fundChange.add(fundFrozenChange) + "，交易对：" + coinPair.getCoin_pair_name());
			throw new ServiceException(direct == Contents.DIRECTION_BUY ? "buy" : "sell");
		}
		
		Long dbTime = spotAccountLogDao.getUnix_TIMESTAMP();
		if (feeRate.compareTo(BigDecimal.ZERO) > 0) {
			SpotAccountLog log = new SpotAccountLog();
			log.setTitle("现货交易：" + coinPair.getCoin_pair_name() + direction);
			log.setCurrency_type_id(coinPair.getMarket_coin_id());
			log.setOperation(3);	// 交易
			log.setDirection(fundChange.add(fundFrozenChange).signum());
			log.setCurrent_amount(fundAccount.getBtc());
			log.setChange_amount(fundChange.add(fundFrozenChange).abs());
			log.setLastest_amount(fundAccount.getBtc().add(fundChange));
			log.setBefore_frozen(fundAccount.getBtc_locked());
			log.setAfter_frozen(fundAccount.getBtc_locked().add(fundFrozenChange));
			log.setDateline(dbTime);
			log.setF_user_id(uid);
			log.setTrans_no(spotTradeLog.getTrade_id() + "");
			log.setRecord_id(0);
			log.setF_coin_pair_name(coinPair.getCoin_pair_name());
			spotAccountLogDao.insertLog(log);
		}
		
		if (bqAccountDao.updateAmount(uid, coinPair.getCoin_id(), spotChange, spotFrozenChange) <= 0) {
			logger.error("用户：" + uid + direction + coinPair.getCoin_name() + "，更新货币账户出错：" + spotChange + "，交易对：" + coinPair.getCoin_pair_name());
			throw new ServiceException(direct == Contents.DIRECTION_BUY ? "buy" : "sell");
		}
		
		if (feeRate.compareTo(BigDecimal.ZERO) > 0) {
			SpotAccountLog log1 = new SpotAccountLog();
			log1.setTitle("现货交易：" + coinPair.getCoin_pair_name() + direction);
			log1.setCurrency_type_id(coinPair.getCoin_id());
			log1.setOperation(3);	// 交易
			log1.setDirection(spotChange.add(spotFrozenChange).signum());
			log1.setCurrent_amount(spotAccount.getBtc());
			log1.setChange_amount(spotChange.add(spotFrozenChange).abs());
			log1.setLastest_amount(spotAccount.getBtc().add(spotChange));
			log1.setBefore_frozen(spotAccount.getBtc_locked());
			log1.setAfter_frozen(spotAccount.getBtc_locked().add(spotFrozenChange));
			log1.setDateline(dbTime);
			log1.setF_user_id(uid);
			log1.setTrans_no(spotTradeLog.getTrade_id() + "");
			log1.setRecord_id(0);
			log1.setF_coin_pair_name(coinPair.getCoin_pair_name());
			spotAccountLogDao.insertLog(log1);
			
			spotPendingLogDao.updateUndealByPendingNo(coinPair.getCoin_pair_name(), pendingNo, uid, tradePrice, tradeCount, BigDecimal.ZERO, fee, bfxFee);
		}
		
		BQSpotArbitrageSetting setting = null;
		try {
			setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + coinPair.getCoin_pair_name());
		} catch (Exception e) {}
		
		if (setting == null) {
			setting = bqSpotArbitrageSettingDao.getArbitrageSettingByCode(coinPair.getCoin_pair_name());
			if (setting == null) {
				setting = new BQSpotArbitrageSetting();
				setting.setRunning(false);
				setting.setScalping(false);
				setting.setHedging(false);
				setting.setRisk_manage(false);
				setting.setDepth_uid(0L);
			}
				
			spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + coinPair.getCoin_pair_name(), BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
		}
		
		if (setting != null && uid == setting.getDepth_uid()) {
			BQSpotHedgingRecord record = new BQSpotHedgingRecord();
			record.setTransaction_code(coinPair.getCoin_pair_name());
			record.setCoin_name(coinPair.getCoin_name());
			record.setMarket_coin_name(coinPair.getMarket_coin_name());
			record.setTrade_id(spotTradeLog.getTrade_id());
			record.setTrade_amount(tradeCount);
			record.setTrade_price(tradePrice);
			record.setTrade_direction(direct);
			record.setTrade_time(dbTime);
			record.setF_user_id(uid);
			record.setDirection(direct == 1 ? 2 : 1);
			record.setPrice(BigDecimal.ZERO);
			record.setProcess_time(record.getTrade_time());
			record.setState(0);
			record.setNote("");
			record.setSend_times(0);
			
			bqSpotHedgingRecordDao.save(record);
		}

		return spotTradeLog;
	}
	
	private SpotTradeLog DelegateTrade(BQMember user, BQSpotMarketCoinPair coinPair, SpotPendingDelegation delegation, BigDecimal tradeCount, 
			BigDecimal tradePrice, boolean isAll) throws ServiceException {
		// 减少挂单
		spotPendingDelegationDao.updateNumByID(delegation.getDelegation_id(), delegation.getCoin_pair_name(), tradeCount, tradeCount.negate());
		SpotTradeLog log = doTrade(user, coinPair, delegation.getCommission_rate(), delegation.getF_user_id(), tradeCount, tradePrice, 
				delegation.getDelegation_price(), delegation.getDirection(), delegation.getPending_no());
		
		// 这个挂单要结束了，全部成交
//		if (isAll && delegation.getCommission_rate().compareTo(BigDecimal.ZERO) != 0) {
//			SpotPendingLog spotPendingLog = new SpotPendingLog(delegation.getDelegation_id(), spotPendingLogDao.getUnix_TIMESTAMP(), coinPair.getId(), 
//					coinPair.getCoin_pair_name(), delegation.getDirection(), delegation.getDelegation_count(), tradePrice, delegation.getDelegation_count(), 
//					BigDecimal.ZERO, Contents.PENDING_LOG_ACTION_CJ, delegation.getF_user_id(), delegation.getPending_no());
//			spotPendingLogDao.insertLog(spotPendingLog);
//		}

		return log;
	}

	private SpotTradeLog DelegateCacheTrade(BQMember user, BQSpotMarketCoinPair coinPair, SpotPendingDelegationCache delegation, BigDecimal tradeCount,
			BigDecimal tradePrice, boolean isAll) throws ServiceException {
		
		// 减少挂单
		spotPendingDelegationCacheDao.updateNumByID(delegation.getDelegation_id(), delegation.getCoin_pair_name(), tradeCount, tradeCount.negate());
		SpotTradeLog log = doTrade(user, coinPair, delegation.getCommission_rate(), delegation.getF_user_id(), tradeCount, tradePrice, 
				delegation.getDelegation_price(), delegation.getDirection(), delegation.getPending_no());

		// 这个挂单要结束了，全部成交
//		if (isAll && delegation.getCommission_rate().compareTo(BigDecimal.ZERO) != 0) {
//			SpotPendingLog spotPendingLog = new SpotPendingLog(delegation.getDelegation_id(), spotPendingLogDao.getUnix_TIMESTAMP(),  coinPair.getId(), 
//					coinPair.getCoin_pair_name(), delegation.getDirection(), delegation.getDelegation_count(), tradePrice, delegation.getDelegation_count(),
//					BigDecimal.ZERO, Contents.PENDING_LOG_ACTION_CJ, delegation.getF_user_id(), delegation.getPending_no());
//			spotPendingLogDao.insertLog(spotPendingLog);
//		}

		return log;
	}

	/**
	 * sellcache1的数量多于buy1的数量
	 * 
	 * @throws ServiceException
	 */
	public List<SpotTradeLog> eatBuy1(BQSpotMarketCoinPair coinPair, SpotPendingDelegation buy1, SpotPendingDelegationCache sellcache1) throws ServiceException {
		List<SpotTradeLog> trades = new ArrayList<SpotTradeLog>();
		BQMember buyUser = bqMemberDao.get(BQMember.class, buy1.getF_user_id());
		BQMember sellUser = bqMemberDao.get(BQMember.class, sellcache1.getF_user_id());

		BigDecimal tradeCount = buy1.getUndeal_count();
		if ((buy1.getDelegation_count().subtract(buy1.getDeal_count())).compareTo(buy1.getUndeal_count()) != 0) {
			ServiceException sex = new ServiceException("buy");
			logger.error(buy1.getF_user_id() + "的买单" + buy1.getDelegation_id() + "有问题：");
			throw sex;
		}
		if ((sellcache1.getDelegation_count().subtract(sellcache1.getDeal_count())).compareTo(sellcache1.getUndeal_count()) != 0) {
			ServiceException sex = new ServiceException("sell");
			logger.error(sellcache1.getF_user_id() + "的卖单" + sellcache1.getDelegation_id() + "有问题：");
			throw sex;
		}

		BigDecimal tradePrice = buy1.getDelegation_price();
		long sellTradeID=0l;
		int sellTradeFlag=Contents.TRADE_FLAG_JY;

		SpotTradeLog sellSpotTradeLog = DelegateCacheTrade(sellUser, coinPair, sellcache1, tradeCount, tradePrice, false);
		sellTradeID = sellSpotTradeLog.getTrade_id();
		trades.add(sellSpotTradeLog);
		
		SpotTradeLog buySpotTradeLog = DelegateTrade(buyUser, coinPair, buy1, tradeCount, tradePrice, true);
		trades.add(buySpotTradeLog);
		
		SpotMemberTradeDetail tradeDetail = new SpotMemberTradeDetail(buySpotTradeLog.getTrade_id(), buyUser.getF_user_id(),buyUser.getF_user_email(), 
				sellUser.getF_user_id(), sellUser.getF_user_email(), sellTradeID, sellTradeFlag, sellcache1.getDirection(), 
				buy1.getDelegation_price(), tradeCount, coinPair.getId(), coinPair.getCoin_pair_name());
		spotMemberTradeDetailDao.insertTradeDetail(tradeDetail);
			
		spotPendingDelegationDao.deletePending(coinPair.getCoin_pair_name(), buy1.getDelegation_id());
		
		// 深度系统方向挂单
		String code = coinPair.getCoin_pair_name();
		BQSpotArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + code);
		if (setting != null && setting.getRunning() && setting.getDepth_self() && setting.isDepth_backhand()
				&& setting.getPrice_digits() >= 0 && setting.getAmount_digits() >= 0) {
			if (buy1.getF_user_id() == setting.getDepth_uid()) {
				// sell cache
				BigDecimal price = buy1.getDelegation_price().add(setting.getDepth_buy_backhand_gap()).setScale(setting.getPrice_digits(),BigDecimal.ROUND_UP);
				addPendingCache(setting.getDepth_uid(), price, tradeCount.setScale(setting.getAmount_digits(), BigDecimal.ROUND_DOWN), Contents.DIRECTION_SELL, coinPair);
				// 补足盘口
				price = buy1.getDelegation_price().subtract(setting.getDepth_buy_price_gap().multiply(new BigDecimal(setting.getDepth_size()))).setScale(setting.getPrice_digits(),BigDecimal.ROUND_UP);
				BigDecimal amount = setting.getDepth_buy_amount_min().add( new BigDecimal(Math.random()).multiply(setting.getDepth_sell_amount_max().subtract(setting.getDepth_buy_amount_min()))).setScale(setting.getAmount_digits(),BigDecimal.ROUND_HALF_UP);
				addPendingCache(setting.getDepth_uid(), price, amount, Contents.DIRECTION_BUY, coinPair);
			}
			
			if (sellcache1.getF_user_id() == setting.getDepth_uid()) {
				// buy cache
				BigDecimal price = sellcache1.getDelegation_price().subtract(setting.getDepth_sell_backhand_gap()).setScale(setting.getPrice_digits(),BigDecimal.ROUND_DOWN);
				addPendingCache(setting.getDepth_uid(), price, tradeCount.setScale(setting.getAmount_digits(), BigDecimal.ROUND_DOWN), Contents.DIRECTION_BUY, coinPair);
			}
		}

		return trades; 
	}

	/** buycache1数量小于等于sell1的数量 吃掉buycache1 */
	public List<SpotTradeLog> eatBuyCache1(BQSpotMarketCoinPair coinPair, SpotPendingDelegationCache buycache1, SpotPendingDelegation sell1, boolean equal) throws ServiceException {
		List<SpotTradeLog> trades = new ArrayList<SpotTradeLog>();
		BQMember buyUser = bqMemberDao.get(BQMember.class, buycache1.getF_user_id());
		BQMember sellUser = bqMemberDao.get(BQMember.class, sell1.getF_user_id());

		BigDecimal tradeCount = buycache1.getUndeal_count();
		if ((buycache1.getDelegation_count().subtract(buycache1.getDeal_count())).compareTo(buycache1.getUndeal_count()) != 0) {
			ServiceException sex = new ServiceException("buy");
			logger.error(buycache1.getF_user_id() + "的买单" + buycache1.getDelegation_id() + "有问题：");
			throw sex;
		}
		if ((sell1.getDelegation_count().subtract(sell1.getDeal_count())).compareTo(sell1.getUndeal_count()) != 0) {
			ServiceException sex = new ServiceException("sell");
			logger.error(sell1.getF_user_id() + "的买单" + sell1.getDelegation_id() + "有问题：");
			throw sex;
		}
		BigDecimal tradePrice = sell1.getDelegation_price();
		long sellTradeID = 0l;
		int sellTradeFlag = Contents.TRADE_FLAG_JY;
		
		SpotTradeLog sellSpotTradeLog = DelegateTrade(sellUser, coinPair, sell1, tradeCount, tradePrice, equal);
		sellTradeID = sellSpotTradeLog.getTrade_id();
		trades.add(sellSpotTradeLog);
		
		SpotTradeLog buySpotTradeLog = DelegateCacheTrade(buyUser, coinPair, buycache1, tradeCount, tradePrice, true);
		trades.add(buySpotTradeLog);
		
		SpotMemberTradeDetail tradeDetail = new SpotMemberTradeDetail(buySpotTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), 
				sellUser.getF_user_id(), sellUser.getF_user_email(), sellTradeID, sellTradeFlag, buycache1.getDirection(), 
				sell1.getDelegation_price(), tradeCount, coinPair.getId(), coinPair.getCoin_pair_name());
		spotMemberTradeDetailDao.insertTradeDetail(tradeDetail);
		
		spotPendingDelegationCacheDao.deletePending(coinPair.getCoin_pair_name(), buycache1.getDelegation_id());
		
		if (equal) {
			spotPendingDelegationDao.deletePending(coinPair.getCoin_pair_name(), sell1.getDelegation_id());
		}
		
		// 深度系统方向挂单
		String code = coinPair.getCoin_pair_name();
		BQSpotArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + code);
		if (setting != null && setting.getRunning() && setting.getDepth_self() && setting.isDepth_backhand()
				&& setting.getPrice_digits() >= 0 && setting.getAmount_digits() >= 0) {
			if (buycache1.getF_user_id() == setting.getDepth_uid()) {
				// sell cache
				BigDecimal price = buycache1.getDelegation_price().add(setting.getDepth_buy_backhand_gap()).setScale(setting.getPrice_digits(),BigDecimal.ROUND_UP);
				addPendingCache(setting.getDepth_uid(), price, tradeCount.setScale(setting.getAmount_digits(), BigDecimal.ROUND_DOWN), Contents.DIRECTION_SELL, coinPair);
				
				
				// 补足盘口
				price = buycache1.getDelegation_price().subtract(setting.getDepth_buy_price_gap().multiply(new BigDecimal(setting.getDepth_size()))).setScale(setting.getPrice_digits(),BigDecimal.ROUND_UP);
				BigDecimal amount = setting.getDepth_buy_amount_min().add( new BigDecimal(Math.random()).multiply(setting.getDepth_buy_amount_max().subtract(setting.getDepth_buy_amount_min()))).setScale(setting.getAmount_digits(),BigDecimal.ROUND_HALF_UP);
				addPendingCache(setting.getDepth_uid(), price, amount, Contents.DIRECTION_BUY, coinPair);
			}
			
			if (sell1.getF_user_id() == setting.getDepth_uid()) {
				// buy cache
				BigDecimal price = sell1.getDelegation_price().subtract(setting.getDepth_sell_backhand_gap()).setScale(setting.getPrice_digits(),BigDecimal.ROUND_DOWN);
				addPendingCache(setting.getDepth_uid(), price, tradeCount.setScale(setting.getAmount_digits(), BigDecimal.ROUND_DOWN), Contents.DIRECTION_BUY, coinPair);
				
				// 补足盘口
				if (equal) {
					price = sell1.getDelegation_price().add(setting.getDepth_sell_price_gap().multiply(new BigDecimal(setting.getDepth_size()))).setScale(setting.getPrice_digits(),BigDecimal.ROUND_UP);
					BigDecimal amount = setting.getDepth_sell_amount_min().add( new BigDecimal(Math.random()).multiply(setting.getDepth_sell_amount_max().subtract(setting.getDepth_sell_amount_min()))).setScale(setting.getAmount_digits(),BigDecimal.ROUND_HALF_UP);
					addPendingCache(setting.getDepth_uid(), price, amount, Contents.DIRECTION_SELL, coinPair);
				}
			}
		}
		
		return trades;
	}

	/**
	 * buycache1数量大于sell1的数量
	 * 
	 * @throws ServiceException
	 */
	public List<SpotTradeLog> eatSell1(BQSpotMarketCoinPair coinPair, SpotPendingDelegationCache buycache1, SpotPendingDelegation sell1) throws ServiceException {
		List<SpotTradeLog> trades = new ArrayList<SpotTradeLog>();
		BQMember buyUser = bqMemberDao.get(BQMember.class, buycache1.getF_user_id());
		BQMember sellUser = bqMemberDao.get(BQMember.class, sell1.getF_user_id());

		BigDecimal tradeCount = sell1.getUndeal_count();
		if ((buycache1.getDelegation_count().subtract(buycache1.getDeal_count())).compareTo(buycache1.getUndeal_count()) != 0) {
			ServiceException sex = new ServiceException("buy");
			logger.error(buycache1.getF_user_id() + "的买单" + buycache1.getDelegation_id() + "有问题：");
			throw sex;
		}
		
		if ((sell1.getDelegation_count().subtract(sell1.getDeal_count())).compareTo(sell1.getUndeal_count()) != 0) {
			ServiceException sex = new ServiceException("sell");
			logger.error(sell1.getF_user_id() + "的买单" + sell1.getDelegation_id() + "有问题：");
			throw sex;
		}
		
		BigDecimal tradePrice = sell1.getDelegation_price();
		long buyTradeID=0l;
		int buyTradeFlag=Contents.TRADE_FLAG_JY;
		
		SpotTradeLog buySpotTradeLog = DelegateCacheTrade(buyUser, coinPair, buycache1, tradeCount, tradePrice, false);
		buyTradeID = buySpotTradeLog.getTrade_id();
		trades.add(buySpotTradeLog);
	
		SpotTradeLog sellSpotTradeLog = DelegateTrade(sellUser, coinPair, sell1, tradeCount, tradePrice, true);
		trades.add(sellSpotTradeLog);
		
		SpotMemberTradeDetail tradeDetail = new SpotMemberTradeDetail(buyTradeID, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
				.getF_user_id(), sellUser.getF_user_email(), sellSpotTradeLog.getTrade_id(), buyTradeFlag, buycache1.getDirection(), 
				sell1.getDelegation_price(), tradeCount, coinPair.getId(), coinPair.getCoin_pair_name());
		spotMemberTradeDetailDao.insertTradeDetail(tradeDetail);

		spotPendingDelegationDao.deletePending(coinPair.getCoin_pair_name(), sell1.getDelegation_id());
		
		// 深度系统方向挂单
		String code = coinPair.getCoin_pair_name();
		BQSpotArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + code);
		if (setting != null && setting.getRunning() && setting.getDepth_self() && setting.isDepth_backhand()
				&& setting.getPrice_digits() >= 0 && setting.getAmount_digits() >= 0) {
			if (buycache1.getF_user_id() == setting.getDepth_uid()) {
				// sell cache
				BigDecimal price = buycache1.getDelegation_price().add(setting.getDepth_buy_backhand_gap()).setScale(setting.getPrice_digits(),BigDecimal.ROUND_UP);
				addPendingCache(setting.getDepth_uid(), price, tradeCount.setScale(setting.getAmount_digits(), BigDecimal.ROUND_DOWN), Contents.DIRECTION_SELL, coinPair);
			}
			
			if (sell1.getF_user_id() == setting.getDepth_uid()) {
				// buy cache
				BigDecimal price = sell1.getDelegation_price().subtract(setting.getDepth_sell_backhand_gap()).setScale(setting.getPrice_digits(),BigDecimal.ROUND_DOWN);
				addPendingCache(setting.getDepth_uid(), price, tradeCount.setScale(setting.getAmount_digits(), BigDecimal.ROUND_DOWN), Contents.DIRECTION_BUY, coinPair);
				
				// 补足盘口
				price = sell1.getDelegation_price().add(setting.getDepth_sell_price_gap().multiply(new BigDecimal(setting.getDepth_size()))).setScale(setting.getPrice_digits(),BigDecimal.ROUND_UP);
				BigDecimal amount = setting.getDepth_sell_amount_min().add( new BigDecimal(Math.random()).multiply(setting.getDepth_sell_amount_max().subtract(setting.getDepth_sell_amount_min()))).setScale(setting.getAmount_digits(),BigDecimal.ROUND_HALF_UP);
				addPendingCache(setting.getDepth_uid(), price, amount, Contents.DIRECTION_SELL, coinPair);
			}
		}
		
		return trades;
	}
	
	/** sellcache1数量小于buy1的数量 */
	public List<SpotTradeLog> eatSellCache1(BQSpotMarketCoinPair coinPair, SpotPendingDelegation buy1, SpotPendingDelegationCache sellcache1, boolean equal) throws ServiceException {
		List<SpotTradeLog> trades = new ArrayList<SpotTradeLog>();
		BQMember buyUser = bqMemberDao.get(BQMember.class, buy1.getF_user_id());
		BQMember sellUser = bqMemberDao.get(BQMember.class, sellcache1.getF_user_id());

		BigDecimal tradeCount = sellcache1.getUndeal_count();
		if ((buy1.getDelegation_count().subtract(buy1.getDeal_count())).compareTo(buy1.getUndeal_count()) != 0) {
			ServiceException sex = new ServiceException("buy");
			logger.error(buy1.getF_user_id() + "的买单" + buy1.getDelegation_id() + "有问题：");
			throw sex;
		}
		if ((sellcache1.getDelegation_count().subtract(sellcache1.getDeal_count())).compareTo(sellcache1.getUndeal_count()) != 0) {
			ServiceException sex = new ServiceException("sell");
			logger.error(sellcache1.getF_user_id() + "的卖单" + sellcache1.getDelegation_id() + "有问题：");
			throw sex;
		}
		
		BigDecimal tradePrice = buy1.getDelegation_price();
		long buyTradeID=0l;
		int buyTradeFlag=Contents.TRADE_FLAG_JY;

		SpotTradeLog buySpotTradeLog = DelegateTrade(buyUser, coinPair, buy1, tradeCount, tradePrice, equal);
		buyTradeID = buySpotTradeLog.getTrade_id();
		trades.add(buySpotTradeLog);
		
		
		SpotTradeLog sellSpotTradeLog = DelegateCacheTrade(sellUser, coinPair, sellcache1, tradeCount, tradePrice, true);
		trades.add(sellSpotTradeLog);
		
		SpotMemberTradeDetail tradeDetail = new SpotMemberTradeDetail(buyTradeID, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
				.getF_user_id(), sellUser.getF_user_email(), sellSpotTradeLog.getTrade_id(),  buyTradeFlag, sellcache1.getDirection(), 
				buy1.getDelegation_price(), tradeCount, coinPair.getId(), coinPair.getCoin_pair_name());
		spotMemberTradeDetailDao.insertTradeDetail(tradeDetail);
	
		spotPendingDelegationCacheDao.deletePending(coinPair.getCoin_pair_name(), sellcache1.getDelegation_id());
		
		if (equal) {
			spotPendingDelegationDao.deletePending(coinPair.getCoin_pair_name(), buy1.getDelegation_id());
		}
		
		// 深度系统方向挂单
		String code = coinPair.getCoin_pair_name();
		BQSpotArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + code);
		if (setting != null && setting.getRunning() && setting.getDepth_self() && setting.isDepth_backhand() 
				&& setting.getPrice_digits() >= 0 && setting.getAmount_digits() >= 0) {
			if (buy1.getF_user_id() == setting.getDepth_uid()) {
				// sell cache
				BigDecimal price = buy1.getDelegation_price().add(setting.getDepth_buy_backhand_gap()).setScale(setting.getPrice_digits(),BigDecimal.ROUND_UP);
				addPendingCache(setting.getDepth_uid(), price, tradeCount.setScale(setting.getAmount_digits(), BigDecimal.ROUND_DOWN), Contents.DIRECTION_SELL, coinPair);
				
				if (equal) {
					// 补足盘口
					price = buy1.getDelegation_price().subtract(setting.getDepth_buy_price_gap().multiply(new BigDecimal(setting.getDepth_size()))).setScale(setting.getPrice_digits(),BigDecimal.ROUND_UP);
					BigDecimal amount = setting.getDepth_buy_amount_min().add( new BigDecimal(Math.random()).multiply(setting.getDepth_buy_amount_max().subtract(setting.getDepth_buy_amount_min()))).setScale(setting.getAmount_digits(),BigDecimal.ROUND_HALF_UP);
					addPendingCache(setting.getDepth_uid(), price, amount, Contents.DIRECTION_BUY, coinPair);
				}
			}
			
			if (sellcache1.getF_user_id() == setting.getDepth_uid()) {
				// buy cache
				BigDecimal price = sellcache1.getDelegation_price().subtract(setting.getDepth_sell_backhand_gap()).setScale(setting.getPrice_digits(),BigDecimal.ROUND_DOWN);
				addPendingCache(setting.getDepth_uid(), price, tradeCount.setScale(setting.getAmount_digits(), BigDecimal.ROUND_DOWN), Contents.DIRECTION_BUY, coinPair);
				
				// 补足盘口
				price = sellcache1.getDelegation_price().add(setting.getDepth_sell_price_gap().multiply(new BigDecimal(setting.getDepth_size()))).setScale(setting.getPrice_digits(),BigDecimal.ROUND_UP);
				BigDecimal amount = setting.getDepth_sell_amount_min().add( new BigDecimal(Math.random()).multiply(setting.getDepth_sell_amount_max().subtract(setting.getDepth_sell_amount_min()))).setScale(setting.getAmount_digits(),BigDecimal.ROUND_HALF_UP);
				addPendingCache(setting.getDepth_uid(), price, amount, Contents.DIRECTION_SELL, coinPair);
			}
		}

		return trades;
	}
	
	private void addPendingCache(long userId, BigDecimal price, BigDecimal amount, int direction, BQSpotMarketCoinPair bqTransaction) throws ServiceException {
		
		int remainCoinId = 0;
		
		if (direction == Contents.DIRECTION_BUY) {
			remainCoinId = bqTransaction.getMarket_coin_id();
		} else {
			remainCoinId = bqTransaction.getCoin_id();
		}
		
		BQAccount bqAccount = bqAccountDao.getByUerAndCurrencyId(userId, remainCoinId);
		if (bqAccount == null) return;
		
		BigDecimal frozen = amount;
		if (direction == Contents.DIRECTION_BUY) {
			frozen = amount.multiply(price);
		}
		
		SpotPendingDelegationCache cahce = new SpotPendingDelegationCache(bqTransaction.getId(), bqTransaction.getCoin_pair_name(),
				direction, amount, price, BigDecimal.ZERO, amount,
				BigDecimal.ZERO, userId, BigDecimal.ZERO);

		spotPendingDelegationCacheDao.insertCache(cahce);
		
		if (bqAccountDao.updateAmount(userId, remainCoinId, frozen.negate(), frozen) <= 0) {
			throw new ServiceException("深度系统反向挂单失败，余额不足...");
		}
	}

	// 检查buycache1是否发生过成交，
	/*
	 * 发生过： 检查持仓表中是否有相关记录，没有就新增一条并修改bq_account表；有就修改持仓表和bq_account表
	 * 
	 * 没发生过： 不动持仓表，和账户表 向SpotPendingDelegation，penddingLog中新增一条记录，
	 */
	public void moveCache1(SpotPendingDelegationCache cache1) throws ServiceException {
		if(spotPendingDelegationCacheDao.deletePending(cache1.getCoin_pair_name(), cache1.getDelegation_id())<=0){
			throw new ServiceException("删除cache1失败!");
		}
		
		spotPendingDelegationDao.moveFromCache(cache1);
	}

	@Override
	public void moveSpotPendingLog(SpotPendingDelegationCache pd) {
		SpotPendingLog spotPendingLog = new SpotPendingLog(pd.getDelegation_id(), pd.getDelegation_time(), pd.getCoin_pair_id(), pd.getCoin_pair_name(),
				pd.getDirection(), pd.getDelegation_count(), pd.getDelegation_price(), pd.getDeal_count(), pd.getUndeal_count(), 
				Contents.PENDING_LOG_ACTION_CD, pd.getF_user_id(), pd.getPending_no(), Contents.DEVICE_SERVER_ERROR, "");
		
		spotPendingLog.setFlag(1);
		spotPendingLogDao.insertLog(spotPendingLog);
		
		spotPendingDelegationCacheDao.deletePending(pd.getCoin_pair_name(), pd.getDelegation_id());
	}

	@Override
	public void moveSpotPendingLog(SpotPendingDelegation pd) {
		SpotPendingLog spotPendingLog = new SpotPendingLog(pd.getDelegation_id(), pd.getDelegation_time(), pd.getCoin_pair_id(), pd.getCoin_pair_name(),
				pd.getDirection(), pd.getDelegation_count(), pd.getDelegation_price(), pd.getDeal_count(), pd.getUndeal_count(), 
				Contents.PENDING_LOG_ACTION_CD, pd.getF_user_id(), pd.getPending_no(), Contents.DEVICE_SERVER_ERROR, "");
		
		spotPendingLog.setFlag(1);
		spotPendingLogDao.insertLog(spotPendingLog);
		
		spotPendingDelegationDao.deletePending(pd.getCoin_pair_name(), pd.getDelegation_id());
	}
}
