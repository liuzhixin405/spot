package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.NAccountContractDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.NArbitrageSettingDao;
import com.btc100.btc_q.dao.BQBfxFeeRecordDao;
import com.btc100.btc_q.dao.BQBfxFeeSettingDao;
import com.btc100.btc_q.dao.NHedgingRecordDao;
import com.btc100.btc_q.dao.BQMemberDao;
import com.btc100.btc_q.dao.NMemberTransactionDao;
import com.btc100.btc_q.dao.NMemberTransactionOverloadLogDao;
import com.btc100.btc_q.dao.NPlanPendingDelegationDao;
import com.btc100.btc_q.dao.NStopLossDao;
import com.btc100.btc_q.dao.NTransactionFeeRateDao;
import com.btc100.btc_q.dao.NContractAccountLogDao;
import com.btc100.btc_q.dao.NMemberTradeDetailDao;
import com.btc100.btc_q.dao.NPendingDelegationCacheDao;
import com.btc100.btc_q.dao.NPendingDelegationDao;
import com.btc100.btc_q.dao.NPendingLogDao;
import com.btc100.btc_q.dao.NTradeLogDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NArbitrageSetting;
import com.btc100.btc_q.entity.BQBfxFeeRecord;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.NHedgingRecord;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NMemberTransactionOverloadLog;
import com.btc100.btc_q.entity.NPlanPendingDelegation;
import com.btc100.btc_q.entity.NStopLoss;
import com.btc100.btc_q.entity.NTransactionFeeRate;
import com.btc100.btc_q.entity.NContractAccountLog;
import com.btc100.btc_q.entity.NMemberTradeDetail;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.entity.NPendingLog;
import com.btc100.btc_q.entity.NTradeLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.NOpenPositionServer;
import com.btc100.btc_q.util.RandomUtil;

/**
 * 开仓服务类
 * 
 * @author gyl
 * 
 */
@Service("nNOpenPositionServer")
public class NOpenPositionServerImpl implements NOpenPositionServer {
	
	private static final Logger logger = Logger.getLogger(NOpenPositionServerImpl.class);
	@Autowired
	private NPendingDelegationCacheDao<NPendingDelegationCache> pendingDelegationCacheDao;
	@Autowired
	private NPendingDelegationDao<NPendingDelegation> pendingDelegationDao;
	@Autowired
	private NMemberTransactionDao<NMemberTransaction> bqMemberTransactionDao;
	@Autowired
	private NPendingLogDao<NPendingLog> pendingLogDao;
	@Autowired
	private NTradeLogDao<NTradeLog> tradeLogDao;
	@Autowired
	private NMemberTradeDetailDao<NMemberTradeDetail> memberTradeDetailDao;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQMemberDao<BQMember> bqMemberDao;
	@Autowired
	private NStopLossDao<NStopLoss> bqStopLossDao;
	@Autowired
	private NAccountContractDao<NAccountContract> bqAccountContractDao;
	@Autowired
	private NContractAccountLogDao<NContractAccountLog> contractAccountLogDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NHedgingRecordDao<NHedgingRecord> bqHedgingRecordDao;
	@Autowired
	private NArbitrageSettingDao<NArbitrageSetting> bqArbitrageSettingDao;
	@Autowired
	private NMemberTransactionOverloadLogDao<NMemberTransactionOverloadLog> bqMemberTransactionOverloadLogDao;
	@Autowired
	private BQBfxFeeSettingDao<BQBfxFeeSetting> bqBfxFeeSettingDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private BQBfxFeeRecordDao<BQBfxFeeRecord> bqBfxFeeRecordDao;
	@Autowired
	private NTransactionFeeRateDao<NTransactionFeeRate> bqTransactionFeeRateDao;
	@Autowired
	private NPlanPendingDelegationDao<NPlanPendingDelegation> planPendingDelegationDao;
	
	private BigDecimal doBfxFee(String partition, String code, BigDecimal fee, BQMember user) {
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
			record.setPart(partition);
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

	private NTradeLog ForKC(int currencyId, String partition, BQMember user, NPendingDelegation pending, NPendingDelegationCache pendingCache, BigDecimal coefficient, int trade_type, BigDecimal tradeCount, BigDecimal tradePrice,
			BigDecimal fromremain, int direct, int pczlx) throws ServiceException {
		long codeid = pending == null ? pendingCache.getTransaction_id() : pending.getTransaction_id();
		String code = pending == null ? pendingCache.getTransaction_code() : pending.getTransaction_code();
		long uid = pending == null ? pendingCache.getF_user_id() : pending.getF_user_id();
		BigDecimal delegateRate = pending == null ? pendingCache.getCommission_rate() : pending.getCommission_rate(); 
		int multiple = pending == null ? pendingCache.getMultiple() : pending.getMultiple();
		long pendingNo = pending == null ? pendingCache.getPending_no() : pending.getPending_no();

		// 委托变动的保证金 -只舍不入，防止由于小数进位导致冻结金额不足的情况
		BigDecimal tempTradeDeposit = tradeCount.multiply(tradePrice).multiply(coefficient).divide(new BigDecimal(multiple), 8, BigDecimal.ROUND_DOWN);

		// 手续费-只舍不入，防止由于小数进位导致冻结金额不足的情况
		BigDecimal fee = tradeCount.multiply(tradePrice).multiply(coefficient).multiply(delegateRate).setScale(8, BigDecimal.ROUND_DOWN);
		BigDecimal addAmount = BigDecimal.ZERO;
		BigDecimal changeFrozen = fee.add(tempTradeDeposit);
		BigDecimal bfxFee = BigDecimal.ZERO;
		if (fee.compareTo(BigDecimal.ZERO) > 0) {
			bfxFee = doBfxFee(partition, code, fee, user);
			if (bfxFee != null) {
				addAmount = addAmount.add(fee);
				fee = BigDecimal.ZERO;
			} else {
				bfxFee = BigDecimal.ZERO;
			}
		}
		
		BigDecimal changeDepositDiff = BigDecimal.ZERO;
		if (pendingCache != null && pendingCache.getDelegation_price().compareTo(tradePrice) > 0) {
			BigDecimal feeDiff = tradeCount.multiply(coefficient).multiply(pendingCache.getDelegation_price().subtract(tradePrice)).multiply(delegateRate).setScale(8, BigDecimal.ROUND_DOWN);
			BigDecimal depositDiff = tradeCount.multiply(coefficient).multiply(pendingCache.getDelegation_price().subtract(tradePrice)).divide(new BigDecimal(multiple), 8, BigDecimal.ROUND_DOWN);
			changeDepositDiff = feeDiff.add(depositDiff);
		}

		changeFrozen =  changeFrozen.add(changeDepositDiff);
		addAmount =  addAmount.add(changeDepositDiff);
		// 交易完成扣除冻结（先扣除冻结，再建仓或增仓）
		int flagbuyAccount = bqAccountContractDao.updateAmount(partition, uid, code, BigDecimal.ZERO, addAmount, changeFrozen.negate(), BigDecimal.ZERO);
		if (flagbuyAccount == 0) {
			ServiceException sex = new ServiceException(trade_type == Contents.TRADE_TYPE_BUY ? "buy" : "sell");
			logger.error("账户出现负数的挂单！");
			throw sex;
		}
		
		if (delegateRate.compareTo(BigDecimal.ZERO) != 0) {
			NAccountContract afterAccountContract = bqAccountContractDao.findByUserIDCode(partition, uid, code);
			NContractAccountLog accountLog = new NContractAccountLog("开仓扣除冻结，还原可用" + addAmount.doubleValue(), currencyId, Contents.TRANS_TYPE_JY, -1, 
					afterAccountContract.getF_btc().subtract(addAmount), addAmount.abs(), afterAccountContract.getF_btc(), afterAccountContract.getF_lockbtc().add(changeFrozen),
					afterAccountContract.getF_lockbtc(), bqAccountContractDao.getUnix_TIMESTAMP(), uid, RandomUtil.getTransNo(), 0, code);
			contractAccountLogDao.insertLog(partition, accountLog);
		}
		
		if (delegateRate.compareTo(BigDecimal.ZERO) != 0 || uid == 4240 || uid == 530597) {
			pendingLogDao.updateUndealByPendingNo(partition, code, pendingNo, uid, tradePrice, tradeCount, BigDecimal.ZERO, fee, bfxFee);
		}

		// 正常进位的保证金
		tempTradeDeposit = BTCQMath.getDeposit(tradeCount, tradePrice, multiple, coefficient);
		NTradeLog buyNTradeLog = new NTradeLog(codeid, code, pczlx, direct, BigDecimal.ZERO, tradePrice, tradeCount, multiple, 
				tempTradeDeposit, BigDecimal.ZERO, fee, uid, pendingNo);
		buyNTradeLog.setTrade_bfx_fee(bfxFee);
		buyNTradeLog = tradeLogDao.insertLog(partition, buyNTradeLog);

		// 持仓单
		BigDecimal newcommission = BTCQMath.getFee(tradeCount, tradePrice, delegateRate, coefficient);
		NMemberTransaction buy1MemberTransaction = bqMemberTransactionDao.getMemberTransaction(partition, code, uid, trade_type, multiple);
		if (buy1MemberTransaction == null) {
			buy1MemberTransaction = new NMemberTransaction(codeid, code, uid, trade_type, new BigDecimal(multiple), tradeCount, BigDecimal.ZERO, tradePrice, tempTradeDeposit, 
					BigDecimal.ZERO, newcommission, fromremain, tradeCount.multiply(tradePrice), BigDecimal.ZERO,trade_type==Contents.DIRECTION_BUY?1:-1);
			bqMemberTransactionDao.insert(partition, buy1MemberTransaction);
		} else {// 已有持仓, 增仓
			// 1、获取持仓均价
			BigDecimal oldAvgPrice = buy1MemberTransaction.getAverage_price();
			int flagBuyMt = bqMemberTransactionDao.updateMemberTransByID(partition, buy1MemberTransaction.getMt_id(), tradeCount, BigDecimal.ZERO, tradePrice, tempTradeDeposit, BigDecimal.ZERO, fromremain,
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, newcommission);
			if (flagBuyMt == 0) {
				ServiceException sex = new ServiceException(trade_type == Contents.TRADE_TYPE_BUY ? "buy" : "sell");
				logger.error("持仓出现负数的挂单，tradeCount: " + tradeCount.doubleValue() + ", tradePrice: " + tradePrice.doubleValue() + ", tempTradeDeposit: " + tempTradeDeposit.doubleValue()
						+ ", fromremain: " + fromremain.doubleValue() + ", newcommission: " + newcommission.doubleValue());
				throw sex;
			} else {
				// 2、获取增仓后均价
				buy1MemberTransaction = bqMemberTransactionDao.getMemberTransaction(partition, code, uid, trade_type, multiple);
				BigDecimal newAvgPrice = buy1MemberTransaction.getAverage_price();
				if (newAvgPrice.compareTo(oldAvgPrice) != 0) {
					// 3、获取平仓挂单（区分买单，卖单）
					int direction = Contents.DIRECTION_BUY;
					if (trade_type == Contents.TRADE_TYPE_BUY) {
						direction = Contents.DIRECTION_SELL;
					}
					
					NAccountContract afterAccountContract = bqAccountContractDao.findByUserIDCode(partition, uid, code);
					BigDecimal available = afterAccountContract.getF_btc();
					BigDecimal transFrozenremain = buy1MemberTransaction.getF_frozenremain();
					BigDecimal changeAccountAmount = BigDecimal.ZERO;
					BigDecimal changeMinFrozenremain = BigDecimal.ZERO;
					BigDecimal changeFrozenremain = BigDecimal.ZERO;
					
					BigDecimal frozenCount = BigDecimal.ZERO;
					List<NPendingDelegationCache> pendingCaches = pendingDelegationCacheDao.queryPCDelegations(partition, direction, code, uid, multiple);
					for (NPendingDelegationCache pd : pendingCaches) {
						if (pd.getUndeal_count().compareTo(BigDecimal.ZERO) == 0) {
							pd.setF_frozenremain(BigDecimal.ZERO);
							continue;
						}
						
						BigDecimal pdFee = BTCQMath.getFee(pd.getUndeal_count(), pd.getDelegation_price(), pd.getCommission_rate(), coefficient);
						BigDecimal income = pd.getDelegation_price().subtract(newAvgPrice).multiply(pd.getUndeal_count()).multiply(coefficient);
						if (pd.getDirection() == Contents.DIRECTION_BUY) {
							income = income.negate();
						}
						BigDecimal rate = pd.getUndeal_count().divide(buy1MemberTransaction.getHold_count().add(buy1MemberTransaction.getLock_count()), 9, BigDecimal.ROUND_HALF_DOWN).setScale(8, BigDecimal.ROUND_DOWN);
						BigDecimal deposit = rate.multiply(buy1MemberTransaction.getDeposit());
						
						// 4、计算挂单所需冻结备用金
						income = income.subtract(pdFee);
						BigDecimal needFrozen = income.add(deposit);
						if (needFrozen.compareTo(BigDecimal.ZERO) >= 0) {
							needFrozen = BigDecimal.ZERO;
						} else {
							frozenCount = frozenCount.add(pd.getUndeal_count());
						}
						
						// 挂单所需调整的备用金
						BigDecimal needChangeFrozen = needFrozen.add(pd.getF_frozenremain());
						changeMinFrozenremain = changeMinFrozenremain.add(needChangeFrozen);
						pd.setF_frozenremain(needChangeFrozen);
					}
					
					List<NPendingDelegation> pendings = pendingDelegationDao.queryPCDelegations(partition, direction, code, uid, multiple);
					for (NPendingDelegation pd : pendings) {
						if (pd.getUndeal_count().compareTo(BigDecimal.ZERO) == 0) {
							pd.setF_frozenremain(BigDecimal.ZERO);
							continue;
						}
						
						BigDecimal pdFee = BTCQMath.getFee(pd.getUndeal_count(), pd.getDelegation_price(), pd.getCommission_rate(), coefficient);
						BigDecimal income = pd.getDelegation_price().subtract(newAvgPrice).multiply(pd.getUndeal_count()).multiply(coefficient);
						if (pd.getDirection() == Contents.DIRECTION_BUY) {
							income = income.negate();
						}
						BigDecimal rate = pd.getUndeal_count().divide(buy1MemberTransaction.getHold_count().add(buy1MemberTransaction.getLock_count()), 9, BigDecimal.ROUND_HALF_DOWN).setScale(8, BigDecimal.ROUND_DOWN);
						BigDecimal deposit = rate.multiply(buy1MemberTransaction.getDeposit());
						
						// 4、计算挂单所需冻结备用金
						income = income.subtract(pdFee);
						BigDecimal needFrozen = income.add(deposit);
						if (needFrozen.compareTo(BigDecimal.ZERO) >= 0) {
							needFrozen = BigDecimal.ZERO;
						} else {
							frozenCount = frozenCount.add(pd.getUndeal_count());
						}
						
						// 挂单所需调整的备用金
						BigDecimal needChangeFrozen = needFrozen.add(pd.getF_frozenremain());
						changeMinFrozenremain = changeMinFrozenremain.add(needChangeFrozen);
						pd.setF_frozenremain(needChangeFrozen);
					}
					
					boolean needRevoke = false;
					if (changeMinFrozenremain.compareTo(BigDecimal.ZERO) < 0) {
						if (available.add(changeMinFrozenremain).compareTo(BigDecimal.ZERO) < 0) {
							BigDecimal minRemain = changeMinFrozenremain.add(available);
							changeAccountAmount = available;
							
							// 余额不够持仓冻结备用金凑
							if (transFrozenremain.add(minRemain).compareTo(BigDecimal.ZERO) < 0) {
								// 冻结备用金都不够--全部撤单
								needRevoke = true;
							} else {
								changeFrozenremain = minRemain.negate();
							}
						} else {
							changeAccountAmount = changeMinFrozenremain.negate();
						}
					} else {
						changeAccountAmount = changeMinFrozenremain.negate();
					}
					
					if (needRevoke) {
						// 撤单
						cancelPending(partition, pendingCaches, pendings);
					} else {
						int ret = 1;
						if (changeAccountAmount.compareTo(BigDecimal.ZERO) != 0) {
							ret = bqAccountContractDao.updateAmount(partition, uid, code, BigDecimal.ZERO, changeAccountAmount.negate(), BigDecimal.ZERO, BigDecimal.ZERO);
						}
						
						if (ret == 0) {
							// 撤单
							cancelPending(partition, pendingCaches, pendings);
						} else {
							BigDecimal changeFrozenCount = frozenCount.subtract(buy1MemberTransaction.getF_frozen_count());
							if (changeFrozenCount.compareTo(BigDecimal.ZERO) != 0
									|| changeFrozenremain.compareTo(BigDecimal.ZERO) != 0
									|| changeMinFrozenremain.compareTo(BigDecimal.ZERO) != 0) {
								ret = bqMemberTransactionDao.updateMemberTransByID(partition, buy1MemberTransaction.getMt_id(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
										changeFrozenremain.negate(), changeFrozenCount, changeMinFrozenremain, BigDecimal.ZERO);
							}
							
							if (ret == 0) {
								if (changeAccountAmount.compareTo(BigDecimal.ZERO) != 0) {
									bqAccountContractDao.updateAmount(partition, uid, code, BigDecimal.ZERO, changeAccountAmount, BigDecimal.ZERO, BigDecimal.ZERO);
								}
								
								// 撤单
								cancelPending(partition, pendingCaches, pendings);
							} else {
								for (NPendingDelegation pd : pendings) {
									if (pd.getF_frozenremain().compareTo(BigDecimal.ZERO) != 0) {
										pendingDelegationDao.updateNumByID(partition, pd.getDelegation_id(), code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, pd.getF_frozenremain().negate(), coefficient);
									}
								}
								
								for (NPendingDelegationCache pd : pendingCaches) {
									if (pd.getF_frozenremain().compareTo(BigDecimal.ZERO) != 0) {
										pendingDelegationCacheDao.updateCacheNumByID(partition, pd.getDelegation_id(), code, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, pd.getF_frozenremain().negate(), coefficient);
									}
								}
							}
						}
					}
				}
			}
		}
		
		if (code.equals("a50")) {
			return buyNTradeLog;
		}

		NArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + partition + "_" + code);
		if (setting == null) {
			setting = bqArbitrageSettingDao.getArbitrageSettingByCode(partition, code);
			if (setting == null) {
				setting = new NArbitrageSetting();
				setting.setRunning(false);
				setting.setScalping(false);
				setting.setHedging(false);
				setting.setRisk_manage(false);
				setting.setAuto_trade(false);
				setting.setDepth_uid(0L);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + partition + "_" + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
		}
		
		if (setting != null && setting.getHedging() && uid == setting.getDepth_uid()) {
			NHedgingRecord record = new NHedgingRecord();
			record.setTransaction_code(code);
			record.setTrade_id(buyNTradeLog.getTrade_id());
			record.setTrade_amount(tradeCount);
			record.setTrade_price(tradePrice);
			record.setTrade_direction(direct);
			record.setTrade_time(bqHedgingRecordDao.getUnix_TIMESTAMP());
			record.setF_user_id(uid);
			record.setDirection(direct == 1 ? 2 : 1);
			record.setPrice(BigDecimal.ZERO);
			record.setProcess_time(record.getTrade_time());
			record.setState(0);
			record.setNote("");
			record.setSend_times(0);
			
			bqHedgingRecordDao.insert(partition, record);
		}
		
		return buyNTradeLog;
	}
	
	private void cancelPending(String partition, List<NPendingDelegationCache> pendingCaches, List<NPendingDelegation> pendings) {
		for (NPendingDelegation pd : pendings) { 
			int type = pd.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			NMemberTransaction manyPosition = bqMemberTransactionDao.getMemberTransaction(partition, pd.getTransaction_code(), pd.getF_user_id(), type, pd.getMultiple());
	
			if (pd.getCommission_rate().compareTo(BigDecimal.ZERO) != 0 || pd.getF_user_id() == 4240 || pd.getF_user_id() == 530597) {
				pendingLogDao.updateActionByPendingNo(partition, pd.getTransaction_code(), pd.getPending_no(), pd.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
			}
			
			BigDecimal chagneRemain = pd.getF_frozenremain();
			BigDecimal changeFrozenCount = BigDecimal.ZERO;
			if (chagneRemain.compareTo(BigDecimal.ZERO) > 0) {
				changeFrozenCount = pd.getUndeal_count().negate();
			}
			if (bqMemberTransactionDao.updateMemberTransByID(partition, manyPosition.getMt_id(), pd.getUndeal_count(), pd.getUndeal_count().negate(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
					chagneRemain, BigDecimal.ZERO, changeFrozenCount, chagneRemain.negate(), BigDecimal.ZERO) <= 0) {
				logger.error("开仓导致持仓均价变化，资金已不满足挂单需求，撤单操作错误，更新持单信息失败，持单：" + manyPosition.getMt_id() + ", 恢复冻结持单量：" + pd.getUndeal_count() + ", remain: " + chagneRemain);
			}
		}
		
		for (NPendingDelegationCache pd : pendingCaches) { 
			int type = pd.getDirection() == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY;
			NMemberTransaction manyPosition = bqMemberTransactionDao.getMemberTransaction(partition, pd.getTransaction_code(), pd.getF_user_id(), type, pd.getMultiple());
	
			if (pd.getCommission_rate().compareTo(BigDecimal.ZERO) != 0) {
				pendingLogDao.updateActionByPendingNo(partition, pd.getTransaction_code(), pd.getPending_no(), pd.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
			}
			
			BigDecimal chagneRemain = pd.getF_frozenremain();
			if (bqMemberTransactionDao.updateMemberTransByID(partition, manyPosition.getMt_id(), pd.getUndeal_count(), pd.getUndeal_count().negate(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
					chagneRemain, BigDecimal.ZERO, pd.getUndeal_count().negate(), chagneRemain.negate(), BigDecimal.ZERO) <= 0) {
				logger.error("开仓导致持仓均价变化，资金已不满足挂单需求，撤单操作错误，更新持单信息失败，持单：" + manyPosition.getMt_id() + ", 恢复冻结持单量：" + pd.getUndeal_count() + ", remain: " + chagneRemain);
			}
		}
	}

	/**
	 * 开仓操作(针对委托单子)
	 * 
	 * @param code
	 *            合约号
	 * @param delegation
	 *            开仓方
	 * @param tradeCount
	 *            交易量
	 * @param tradePrice
	 *            交易价
	 * @param trade_type
	 *            交易方向
	 * @param direct
	 *            这笔交易的方向
	 * @param buyUser
	 *            用户
	 * @param isAll
	 *            是否是全部成交
	 * @throws ServiceException
	 */
	private NTradeLog DelegateKC(int currencyId, String partition, BQMember user, String code, NPendingDelegation delegation, BigDecimal tradeCount, BigDecimal tradePrice, int trade_type,
			BigDecimal fromremain, BigDecimal coefficient, boolean isAll) throws ServiceException {
		/*
		// 委托变动的保证金
		BigDecimal tempTradeDeposit = BTCQMath.getDeposit(tradeCount, tradePrice, delegation.getMultiple());
		// 挂单所占用的保证金
		BigDecimal pdDeposit = BTCQMath.getDeposit(delegation.getUndeal_count(), tradePrice, delegation.getMultiple());
		*/
		
		// ???? todo ....????
		BigDecimal changeFromRemain = BigDecimal.ZERO;
		/*
		if (pdDeposit.compareTo(fromremain) > 0) {	// 挂单所占用的保证金 > 来自账户的保证金,即含有来自待结算盈利的保证金
			BigDecimal tempGain = pdDeposit.subtract(fromremain);	// 来自待结算盈利的保证金
			if (tempGain.compareTo(tempTradeDeposit) >= 0) {	// 待结算盈利的保证金 > 本次交易消耗的保证金
				changeFromRemain = BigDecimal.ZERO;
			} else {	// 待结算盈利的部分不够支持本次撮合交易
				changeFromRemain = tempTradeDeposit.subtract(tempGain);
			}
		} else if (pdDeposit.compareTo(fromremain) == 0) {	// 挂单所占用的保证金 == 来自账户的保证金，即不含来自待结算盈利的保证金
			changeFromRemain = tempTradeDeposit;
		} else {
			ServiceException sex = new ServiceException(trade_type == Contents.TRADE_TYPE_BUY ? "buy" : "sell");
			logger.error("挂单保证金不能小于来自账户的保证金数！");
			throw sex;
		}
		*/
		
		// 减少挂单
		pendingDelegationDao.updateNumByID(partition, delegation.getDelegation_id(), code, tradeCount, tradeCount.negate(), changeFromRemain.negate(), BigDecimal.ZERO, coefficient);
		int pczlx = trade_type == Contents.TRADE_TYPE_BUY ? Contents.CZLX_DDKC : Contents.CZLX_KTKC;
		// 扣除冻结，建仓，增仓等
		NTradeLog log = ForKC(currencyId, partition, user, delegation, null, coefficient, trade_type, tradeCount, tradePrice, changeFromRemain, trade_type, pczlx);

		// 这个挂单要结束了，全部成交
//		if (isAll && delegation.getCommission_rate().compareTo(BigDecimal.ZERO) != 0) {
//			BigDecimal allDeposit = BTCQMath.getDeposit(delegation.getDelegation_count(), delegation.getDelegation_price(), delegation.getMultiple(), coefficient);
//			NPendingLog pendingLog = new NPendingLog(delegation.getDelegation_id(), pendingLogDao.getUnix_TIMESTAMP(), delegation.getTransaction_id(), code,
//					pczlx, delegation.getDelegation_count(), tradePrice, delegation.getMultiple(), delegation.getDelegation_count(), BigDecimal.ZERO, allDeposit,
//					Contents.PENDING_LOG_ACTION_CJ, delegation.getF_user_id(), delegation.getPending_no());
//			pendingLogDao.insertLog(pendingLog);
//		}

		return log;
	}

	private NTradeLog DelegateCacheKC(int currencyId, String partition, BQMember user, String code, NPendingDelegationCache delegation, BigDecimal tradeCount, BigDecimal tradePrice, int trade_type,
			BigDecimal fromremain, BigDecimal coefficient, boolean isAll, boolean isQP) throws ServiceException {
		
		BigDecimal changeFromRemain = BigDecimal.ZERO;
		if (isQP) {	// 强平挂单不在数据库，在内存中
			delegation.setDeal_count(delegation.getDeal_count().add(tradeCount));
			delegation.setUndeal_count(delegation.getUndeal_count().subtract(tradeCount));
		} else {
			// 减少挂单
			pendingDelegationCacheDao.updateCacheNumByID(partition, delegation.getDelegation_id(), code, tradeCount, tradeCount.negate(), changeFromRemain.negate(), BigDecimal.ZERO, coefficient);
		}
		
		int pczlx = trade_type == Contents.TRADE_TYPE_BUY ? Contents.CZLX_DDKC : Contents.CZLX_KTKC;
		// 扣除冻结，建仓，增仓等
		NTradeLog log = ForKC(currencyId, partition, user, null, delegation, coefficient, trade_type, tradeCount, tradePrice, changeFromRemain,  trade_type, pczlx);

		// 这个挂单要结束了，全部成交
//		if (isAll && delegation.getCommission_rate().compareTo(BigDecimal.ZERO) != 0) {
//			BigDecimal allDeposit = BTCQMath.getDeposit(delegation.getDelegation_count(), delegation.getDelegation_price(), delegation.getMultiple(), coefficient);
//			NPendingLog pendingLog = new NPendingLog(delegation.getDelegation_id(), pendingLogDao.getUnix_TIMESTAMP(), delegation.getTransaction_id(), code,
//					pczlx, delegation.getDelegation_count(), tradePrice, delegation.getMultiple(), delegation.getDelegation_count(), BigDecimal.ZERO, allDeposit,
//					Contents.PENDING_LOG_ACTION_CJ, delegation.getF_user_id(), delegation.getPending_no());
//			pendingLogDao.insertLog(pendingLog);
//			
//			
//		}

		return log;
	}

	private NTradeLog ForPC(int currencyId, String partition, BQMember user, NMemberTransaction sellMemTrans, NPendingDelegation delegation, BigDecimal coefficient, 
			BigDecimal tradeCount, BigDecimal tradePrice, BigDecimal changeMinremain, int trade_type, int direct, boolean isAll) throws ServiceException {
		
		String code = delegation.getTransaction_code();
		Long trandsID = delegation.getTransaction_id();
		Long uid = delegation.getF_user_id();
		int multiple = delegation.getMultiple();
		long pendingNo = delegation.getPending_no();
		
		// 总持仓
		BigDecimal oldAllCount = sellMemTrans.getHold_count().add(sellMemTrans.getLock_count());
		
		// 变化后的保证金
		BigDecimal newRate = (new BigDecimal(1)).subtract(tradeCount.divide(oldAllCount, 20, BigDecimal.ROUND_HALF_DOWN));
		BigDecimal newDep = sellMemTrans.getDeposit().multiply(newRate);
		BigDecimal changeDep = sellMemTrans.getDeposit().subtract(newDep);
		
		// 从余额转入的备用金（亏损）
		BigDecimal newRemain = BigDecimal.ZERO;
		BigDecimal newFrozenRemain = BigDecimal.ZERO;
		BigDecimal changeRemain = BigDecimal.ZERO;
		BigDecimal changeFrozenremain = BigDecimal.ZERO;
		BigDecimal changeFrozenCount = BigDecimal.ZERO;
		if (delegation.getF_frozenremain().compareTo(BigDecimal.ZERO) > 0) {
			newRemain = sellMemTrans.getF_fromremain();
			newFrozenRemain = sellMemTrans.getF_frozenremain().multiply((new BigDecimal(1)).subtract(tradeCount.divide(delegation.getUndeal_count(), 20, BigDecimal.ROUND_HALF_DOWN)));
//			newMinremain = delegation.getF_frozenremain().multiply((new BigDecimal(1)).subtract(tradeCount.divide(delegation.getUndeal_count(), 20, BigDecimal.ROUND_HALF_DOWN)));
//			changeMinremain = delegation.getF_frozenremain().subtract(newMinremain);
			changeFrozenremain = sellMemTrans.getF_frozenremain().subtract(newFrozenRemain);
			changeFrozenCount = tradeCount;
		} else {
			newRemain = sellMemTrans.getF_fromremain().multiply((new BigDecimal(1)).subtract(tradeCount.divide(oldAllCount, 20, BigDecimal.ROUND_HALF_DOWN)));
			changeRemain = sellMemTrans.getF_fromremain().subtract(newRemain);
		}
		
		// 盈亏额
		BigDecimal income = tradePrice.subtract(sellMemTrans.getAverage_price()).multiply(tradeCount).multiply(coefficient);
		if (trade_type != Contents.CZLX_DDPC) {
			income = income.negate();
		}
		
		// 手续费
		BigDecimal fee = BTCQMath.getFee(tradeCount, tradePrice, delegation.getCommission_rate(), coefficient);
		BigDecimal bfxFee = BigDecimal.ZERO;
		if (fee.compareTo(BigDecimal.ZERO) > 0) {
			bfxFee = doBfxFee(partition, code, fee, user);
			if (bfxFee != null) {
				fee = BigDecimal.ZERO;
			} else {
				bfxFee = BigDecimal.ZERO;
			}
		}
		
		BigDecimal newDep2 = sellMemTrans.getDeposit2().multiply(newRate);
		BigDecimal changeDep2 = sellMemTrans.getDeposit2().subtract(newDep2);
		
		// 总的变化：保证金+收入-手续费
		BigDecimal accountChangeNum = BigDecimal.ZERO;
		if (delegation.getF_frozenremain().compareTo(BigDecimal.ZERO) > 0) {
			accountChangeNum = changeDep.add(changeMinremain).add(income).add(changeFrozenremain).subtract(fee).add(changeDep2);
			if (accountChangeNum.signum() < 0) {
				// frozenremain补
				BigDecimal needChangeFrozenremain = accountChangeNum.negate();
				if (needChangeFrozenremain.compareTo(newFrozenRemain) > 0) {
					needChangeFrozenremain = newFrozenRemain;
				}
				
				newFrozenRemain = newFrozenRemain.subtract(needChangeFrozenremain);
				changeFrozenremain = changeFrozenremain.add(needChangeFrozenremain);
				
				accountChangeNum = accountChangeNum.add(needChangeFrozenremain);
				if (accountChangeNum.signum() < 0) {
					// fromremain补
					changeRemain = accountChangeNum.negate();
					if (changeRemain.compareTo(newRemain) > 0) {
						changeRemain = newRemain;
					}
					newRemain = newRemain.subtract(changeRemain);
					
					accountChangeNum = accountChangeNum.add(changeFrozenremain);
				}
			}
			
		} else {
			accountChangeNum = changeDep.add(changeRemain).add(income).subtract(fee).add(changeDep2);
			if (accountChangeNum.signum() < 0) {
				// fromremain补
				BigDecimal addChangeRemain = accountChangeNum.negate();
				if (addChangeRemain.compareTo(newRemain) > 0) {
					addChangeRemain = newRemain;
				}
				
				newRemain = newRemain.subtract(addChangeRemain);
				changeRemain = changeRemain.add(addChangeRemain);
				accountChangeNum = accountChangeNum.add(addChangeRemain);
				
				if (accountChangeNum.signum() < 0) {
					// frozenremain补
					changeFrozenremain = accountChangeNum.negate();
					if (changeFrozenremain.compareTo(newFrozenRemain) > 0) {
						changeFrozenremain = newFrozenRemain;
					}
					
					newFrozenRemain = newFrozenRemain.subtract(changeFrozenremain);
					accountChangeNum = accountChangeNum.add(changeFrozenremain);
				}
			}
		}
		
		if (accountChangeNum.signum() < 0 && (changeDep.add(changeRemain).add(changeMinremain).add(changeDep2).add(income).add(changeFrozenremain)).signum() >= 0) {		// 在爆仓边缘
			accountChangeNum = BigDecimal.ZERO;
		} else if (changeRemain.compareTo(BigDecimal.ZERO) == 0 && accountChangeNum.signum() > 0 && accountChangeNum.compareTo(new BigDecimal(0.000001)) < 0) {
			accountChangeNum = BigDecimal.ZERO;
		}
		
		// 减少冻结的持仓
		BigDecimal newcommission = BTCQMath.getFee(oldAllCount.subtract(tradeCount), tradePrice, delegation.getCommission_rate(), coefficient);
		int flagSellMt = bqMemberTransactionDao.updateMemberTransByID(partition, sellMemTrans.getMt_id(), BigDecimal.ZERO, tradeCount.negate(), BigDecimal.ZERO, changeDep.negate(), changeDep2.negate(), 
				changeRemain.negate(), changeFrozenremain.negate(), changeFrozenCount.negate(), changeMinremain.negate(), newcommission);
		if (flagSellMt <= 0) {
			ServiceException sex = new ServiceException(trade_type == Contents.CZLX_DDPC ? "sell" : "buy");
			logger.error("持仓出现负数的挂单 ！");
			throw sex;
		}

		// 账户资金变更
		if (accountChangeNum.compareTo(BigDecimal.ZERO) != 0) {
			int flagsellAccount = bqAccountContractDao.updateAmount(partition, uid, code, BigDecimal.ZERO, accountChangeNum, BigDecimal.ZERO, BigDecimal.ZERO);
			if (flagsellAccount == 0 && (accountChangeNum.signum() > 0 || accountChangeNum.compareTo(BigDecimal.ONE.negate()) <= 0)) {
				ServiceException sex = new ServiceException(trade_type == Contents.CZLX_DDPC ? "sell" : "buy");
				logger.error("未修改！");
				throw sex;
			}
		}
		
		NTradeLog sellNTradeLog = new NTradeLog(trandsID, code, trade_type, direct, sellMemTrans.getAverage_price(), tradePrice, tradeCount, multiple, changeDep, income, fee, uid, pendingNo);
		sellNTradeLog.setTrade_bfx_fee(bfxFee);
		sellNTradeLog = tradeLogDao.insertLog(partition, sellNTradeLog);

		long linux_time = bqAccountContractDao.getUnix_TIMESTAMP();
		String strDesip = trade_type == Contents.CZLX_DDPC ? "多单平仓" : "空单平仓";
		
		if (delegation.getCommission_rate().compareTo(BigDecimal.ZERO) != 0) {
			NAccountContract accountContract = bqAccountContractDao.findByUserIDCode(partition, uid, code);
			NContractAccountLog accountlog=new NContractAccountLog(strDesip + "交易，余额增加", currencyId, Contents.TRANS_TYPE_JY,accountChangeNum.signum(),
					accountContract.getF_btc(), accountChangeNum, accountContract.getF_btc().add(accountChangeNum), accountContract.getF_lockbtc(), 
					accountContract.getF_lockbtc(), linux_time, uid, RandomUtil.getTrans_no(), 0, accountContract.getF_code());
			contractAccountLogDao.insertLog(partition, accountlog);
		}
		
		if (delegation.getCommission_rate().compareTo(BigDecimal.ZERO) != 0 || uid == 4240 || uid == 530597) {
			pendingLogDao.updateUndealByPendingNo(partition, code, pendingNo, uid, tradePrice, tradeCount, income, fee, bfxFee);
		}
		
		return sellNTradeLog;
	}
	
	private void ForBC(String partition, String code, long uid, long pendingNo, BigDecimal feeRate, BigDecimal coefficient, BigDecimal tradeCount, BigDecimal tradePrice, 
			int direct) throws ServiceException {
		
		NMemberTransactionOverloadLog overload = bqMemberTransactionOverloadLogDao.getByPendingNo(partition, code, uid, pendingNo);
		if (overload == null) return ;
		
		if (overload.getTrade_overload_price().compareTo(tradePrice) == 0) {
			return;
		}
		
		// 总持仓
		BigDecimal oldAllCount = overload.getTrade_count();
		
		// 变化后的保证金
		// newDep=oldDep*(1-tradeCount/oldAllCount)
		BigDecimal newDep = overload.getTrade_deposit().multiply((new BigDecimal(1)).subtract(tradeCount.divide(oldAllCount, 20, BigDecimal.ROUND_HALF_DOWN)));
		//newDep = newDep.setScale(4, RoundingMode.HALF_DOWN);
		BigDecimal changeDep = overload.getTrade_deposit().subtract(newDep);
		
		// 从余额转入的备用金（亏损）
		BigDecimal newRemain = BigDecimal.ZERO;
		BigDecimal changeRemain = BigDecimal.ZERO;

		newRemain = overload.getF_fromremain().multiply((new BigDecimal(1)).subtract(tradeCount.divide(oldAllCount, 20, BigDecimal.ROUND_HALF_DOWN)));
		changeRemain = overload.getF_fromremain().subtract(newRemain);
		
		// 盈亏额
		BigDecimal income = tradePrice.subtract(overload.getTrade_price()).multiply(tradeCount).multiply(coefficient);
		if (direct != Contents.DIRECTION_SELL) {
			income = income.negate();
		}
		
		// 手续费
		BigDecimal fee = overload.getTrade_fee().multiply(tradeCount.divide(oldAllCount, 20, BigDecimal.ROUND_HALF_DOWN));
		
		// 总的变化：保证金+收入-手续费
		BigDecimal accountChangeNum  = changeDep.add(changeRemain).add(income).subtract(fee);
		if (accountChangeNum.signum() < 0) {
			accountChangeNum = BigDecimal.ZERO;
		}
		
		// 增加结余
		if (accountChangeNum.compareTo(new BigDecimal(0.00001)) > 0) {
			bqMemberTransactionOverloadLogDao.addBalance(partition, code, uid, pendingNo, accountChangeNum);
		}
	}

	private NTradeLog DelegateCachePC(int currencyId, String partition, BQMember user, String code, NPendingDelegationCache delegation, BigDecimal tradeCount, BigDecimal tradePrice, int trade_type,
			int tradeDirection, BigDecimal coefficient, boolean isAll, boolean isQP) throws ServiceException {
		// 减少挂单量
		BigDecimal changeMinremain = BigDecimal.ZERO;
		if (isQP) {	// 强平挂单不在数据库，在内存中
			delegation.setDeal_count(delegation.getDeal_count().add(tradeCount));
			delegation.setUndeal_count(delegation.getUndeal_count().subtract(tradeCount));
		} else {
			if (delegation.getF_frozenremain().compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal newMinremain = delegation.getF_frozenremain().multiply((new BigDecimal(1)).subtract(tradeCount.divide(delegation.getUndeal_count(), 20, BigDecimal.ROUND_HALF_DOWN)));
				changeMinremain = delegation.getF_frozenremain().subtract(newMinremain);
			}
			pendingDelegationCacheDao.updateCacheNumByID(partition, delegation.getDelegation_id(), code, tradeCount, tradeCount.negate(), BigDecimal.ZERO, changeMinremain.negate(), coefficient);	// 平仓单子这个字段从一开始就为0
		}

		// 用户持仓
		NMemberTransaction sellMemTrans = bqMemberTransactionDao.getMemberTransaction(partition, code, delegation.getF_user_id(),
				trade_type == Contents.CZLX_DDPC ? Contents.DIRECTION_BUY : Contents.DIRECTION_SELL, delegation.getMultiple());

		// 用户账户资金变更，持仓变更
		NPendingDelegation pending = new NPendingDelegation();
		pending.setF_user_id(delegation.getF_user_id());
		pending.setTransaction_code(delegation.getTransaction_code());
		pending.setTransaction_id(delegation.getTransaction_id());
		pending.setMultiple(delegation.getMultiple());
		pending.setF_fromremain(delegation.getF_fromremain());
		pending.setF_frozenremain(delegation.getF_frozenremain());
		pending.setDeal_count(delegation.getDeal_count());
		pending.setUndeal_count(delegation.getUndeal_count());
		pending.setCommission_rate(delegation.getCommission_rate());
		pending.setPending_no(delegation.getPending_no());
		
		NTradeLog log = ForPC(currencyId, partition, user, sellMemTrans, pending, coefficient, tradeCount, tradePrice, changeMinremain, trade_type, tradeDirection, false);

		if (isAll) {
//			if (delegation.getCommission_rate().compareTo(BigDecimal.ZERO) != 0) {
//				// 这个挂单要结束了，全部成交
//				BigDecimal allDeposit = BTCQMath.getDeposit(delegation.getDelegation_count(), delegation.getDelegation_price(), delegation.getMultiple(), coefficient);
//				NPendingLog pendingLog = new NPendingLog(delegation.getDelegation_id(), pendingLogDao.getUnix_TIMESTAMP(), delegation.getTransaction_id(), delegation
//						.getTransaction_code(), trade_type, delegation.getDelegation_count(), delegation.getDelegation_price(), delegation.getMultiple(),
//						delegation.getDelegation_count(), BigDecimal.ZERO, allDeposit, Contents.PENDING_LOG_ACTION_CJ, delegation.getF_user_id(), delegation.getPending_no());
//				pendingLogDao.insertLog(pendingLog);
//			}

			// 当持仓量、冻结持仓量均为空时，删除
			int flag = bqMemberTransactionDao.deleteZEROTran(partition, sellMemTrans);
			if (flag > 0) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("mtId", sellMemTrans.getMt_id());
				bqStopLossDao.deleteConditionBySql("delete from " + partition + "_stop_loss where transaction_member_id = :mtId", params);

				planPendingDelegationDao.updateStatusAndNoteByMid(partition, sellMemTrans.getTransaction_code(), sellMemTrans.getF_user_id(), sellMemTrans.getMt_id(), Contents.PLAN_PENDING_STATUS_FAIL, "无持仓");
			}
		}

		return log;
	}

	private NTradeLog DelegatePC(int currencyId, String partition, BQMember user, String code, NPendingDelegation delegation, BigDecimal tradeCount, BigDecimal tradePrice, int trade_type, 
			BigDecimal coefficient, int tradeDirection, boolean isAll) throws ServiceException {
		
		BigDecimal changeMinremain = BigDecimal.ZERO;
		if (delegation.getF_frozenremain().compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal newMinremain = delegation.getF_frozenremain().multiply((new BigDecimal(1)).subtract(tradeCount.divide(delegation.getUndeal_count(), 20, BigDecimal.ROUND_HALF_DOWN)));
			changeMinremain = delegation.getF_frozenremain().subtract(newMinremain);
		}
		
		// 减少挂单量
		pendingDelegationDao.updateNumByID(partition, delegation.getDelegation_id(), code, tradeCount, tradeCount.negate(), BigDecimal.ZERO, changeMinremain.negate(), coefficient);	// 平仓单子这个字段从一开始就为0

		// 用户持仓
		NMemberTransaction sellMemTrans = bqMemberTransactionDao.getMemberTransaction(partition, code, delegation.getF_user_id(),
				trade_type == Contents.CZLX_DDPC ? Contents.DIRECTION_BUY : Contents.DIRECTION_SELL, delegation.getMultiple());

		// 用户账户资金变更，持仓变更
		NTradeLog log = ForPC(currencyId, partition, user, sellMemTrans, delegation, coefficient, tradeCount, tradePrice, changeMinremain, trade_type, tradeDirection, false);

		if (isAll) {
//			if (delegation.getCommission_rate().compareTo(BigDecimal.ZERO) != 0) {
//				// 这个挂单要结束了，全部成交
//				BigDecimal allDeposit = BTCQMath.getDeposit(delegation.getDelegation_count(), delegation.getDelegation_price(), delegation.getMultiple(), coefficient);
//				NPendingLog pendingLog = new NPendingLog(delegation.getDelegation_id(), pendingLogDao.getUnix_TIMESTAMP(), delegation.getTransaction_id(), delegation
//						.getTransaction_code(), trade_type, delegation.getDelegation_count(), delegation.getDelegation_price(), delegation.getMultiple(),
//						delegation.getDelegation_count(), BigDecimal.ZERO, allDeposit, Contents.PENDING_LOG_ACTION_CJ, delegation.getF_user_id(), delegation.getPending_no());
//				pendingLogDao.insertLog(pendingLog);
//			}

			// 当持仓量、冻结持仓量均为空时，删除
			int flag = bqMemberTransactionDao.deleteZEROTran(partition, sellMemTrans);
			if (flag > 0) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("mtId", sellMemTrans.getMt_id());
				bqStopLossDao.deleteConditionBySql("delete from " + partition + "_stop_loss where transaction_member_id = :mtId", params);

				planPendingDelegationDao.updateStatusAndNoteByMid(partition, sellMemTrans.getTransaction_code(), sellMemTrans.getF_user_id(), sellMemTrans.getMt_id(), Contents.PLAN_PENDING_STATUS_FAIL, "无持仓");
			}
		}

		return log;
	}

	// 系统的持仓方向trade_type与挂单的买卖方向相反，因为该挂单是爆仓挂单
	private void DelegationBC(String partition, String code, long uid, long pendingNo, BigDecimal feeRate, BigDecimal coefficient, BigDecimal tradeCount, 
			BigDecimal tradePrice, int trade_type) throws ServiceException {
		NMemberTransaction sellMemberTransaction = bqMemberTransactionDao.getMemberTransaction(partition, code, Contents.BQMEMBER_ADMIN, trade_type, 0);

		int flagSellMt = bqMemberTransactionDao.updateMemberTransByID(partition, sellMemberTransaction.getMt_id(), tradeCount.negate(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		if (flagSellMt == 0) {
			ServiceException sex = new ServiceException(trade_type == Contents.DIRECTION_BUY ? "sell" : "buy");
			logger.error("引起系统持仓出现负值的爆仓挂单！");
			throw sex;
		}
		
		ForBC(partition, code, uid, pendingNo, feeRate, coefficient, tradeCount, tradePrice, trade_type == Contents.DIRECTION_BUY ? Contents.DIRECTION_SELL : Contents.DIRECTION_BUY);
	}

	// 1检查NMemberTransaction中是否有买方的记录
	/*
	 * 如果没有新建一条NMemberTransaction， 修改买方的账户表
	 * 在pendingLog,tradeLog,memberTrade,中新增买方记录NPendingDelegation中删除买方记录
	 * 修改卖方的NPendingDelegationCache的数量，在tradeLog,memberTrade,中新增卖方记录
	 * 在memberTradeDetail新增一条记录 如果有的话 修改持仓表，和账户表
	 * 在pendingLog,tradeLog,memberTrade,中新增买方记录NPendingDelegation中删除买方记录
	 * 修改卖方的NPendingDelegationCache的数量，在tradeLog,memberTrade,中新增卖方记录
	 * 在memberTradeDetail新增一条记录
	 */
	/**
	 * sellcache1的数量多于buy1的数量
	 * 
	 * @throws ServiceException
	 */
	public List<NTradeLog> eatBuy1(int currencyId, String partition, String code, NPendingDelegation buy1, NPendingDelegationCache sellcache1, BigDecimal coefficient, boolean isQP) throws ServiceException {
		
		// 调整空头开仓sellcache保证金，买价>卖价，此时成交价为买价
		if (sellcache1.getPriority() == Contents.PRIORITY_KC 
				&& buy1.getDelegation_price().compareTo(sellcache1.getDelegation_price()) > 0) {
			adjustSellCacheFrozen(currencyId, partition, buy1, sellcache1, coefficient);
		}
		
		if (buy1.getUndeal_count().compareTo(sellcache1.getUndeal_count()) >= 0) {
			sellcache1.setDelegation_price(buy1.getDelegation_price());
			return eatSellCache1(currencyId, partition, code, buy1, sellcache1, coefficient, buy1.getUndeal_count().compareTo(sellcache1.getUndeal_count()) == 0, isQP);
		}
		
		BigDecimal ZERO = BigDecimal.ZERO;
		List<NTradeLog> trades = new ArrayList<NTradeLog>();
//		logger.info("交易服务：sellcache1的数量多于buy1的数量--eatBuy1()");
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
		int tradeDirection = sellcache1.getDirection();

		// 开仓买单，持仓量++ 买单全被吃掉
		if (Contents.PRIORITY_KC == buy1.getPriority()) {
			/** 买单处理结束, 卖单开始 ***/
			// ***********************3
			long sellTradeID=0l;
			int sellTradeFlag=Contents.TRADE_FLAG_JY;
			int sellTradeTypeID=Contents.CZLX_DDBC;

			if (Contents.PRIORITY_KC == sellcache1.getPriority()) {	// 开仓卖单
				NTradeLog sellNTradeLog = DelegateCacheKC(currencyId, partition, sellUser, code, sellcache1, tradeCount, tradePrice, Contents.TRADE_TYPE_SELL, sellcache1.getF_fromremain(), coefficient, false, isQP);
				sellTradeID=sellNTradeLog.getTrade_id();
				sellTradeTypeID=sellNTradeLog.getTrade_type_id();
				trades.add(sellNTradeLog);
			} else if (Contents.PRIORITY_PC == sellcache1.getPriority()) {	// 平仓卖单
				NTradeLog sellNTradeLog = DelegateCachePC(currencyId, partition, sellUser, code, sellcache1, tradeCount, tradePrice, Contents.CZLX_DDPC, tradeDirection, coefficient, false, isQP);
				sellTradeID=sellNTradeLog.getTrade_id();
				sellTradeTypeID=sellNTradeLog.getTrade_type_id();
				trades.add(sellNTradeLog);
			} else {	// 爆仓卖单
				// 系统持仓(多仓)记录,(卖单的多仓):：卖完持仓量减少
				DelegationBC(partition, code, sellcache1.getF_user_id(), sellcache1.getPending_no(), sellcache1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_BUY);
				pendingDelegationCacheDao.updateNumByID(partition, sellcache1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO);
				sellTradeFlag=Contents.TRADE_FLAG_QP;
			}
			
			NTradeLog buyNTradeLog = DelegateKC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.TRADE_TYPE_BUY, buy1.getF_fromremain(), coefficient, true);
			NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
					.getF_user_id(), sellUser.getF_user_email(), sellTradeID, buyNTradeLog.getTrade_type_id(), sellTradeTypeID, sellTradeFlag, sellcache1.getDirection(), buy1.getDelegation_price(), tradeCount, buy1
					.getTransaction_id(), buy1.getTransaction_code());
			memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
			
			trades.add(buyNTradeLog);
		} else if (Contents.PRIORITY_PC == buy1.getPriority()) {	// 平仓买单，持仓冻结量--
			NTradeLog buyNTradeLog = DelegatePC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.CZLX_KTPC, coefficient, tradeDirection, true);
			
			if (Contents.PRIORITY_KC == sellcache1.getPriority()) {	// 开仓卖单
				NTradeLog sellNTradeLog = DelegateCacheKC(currencyId, partition, sellUser, code, sellcache1, tradeCount, tradePrice, Contents.TRADE_TYPE_SELL, sellcache1.getF_fromremain(), coefficient, false, isQP);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog
						.getTrade_type_id(), Contents.TRADE_FLAG_JY, sellcache1.getDirection(), buy1.getDelegation_price(), tradeCount, buy1
						.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				trades.add(sellNTradeLog);
			} else if (Contents.PRIORITY_PC == sellcache1.getPriority()) {	// 平仓卖单
				NTradeLog sellNTradeLog = DelegateCachePC(currencyId, partition, sellUser, code, sellcache1, tradeCount, tradePrice, Contents.CZLX_DDPC, tradeDirection, coefficient, false, isQP);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog
						.getTrade_type_id(), Contents.TRADE_FLAG_JY, sellcache1.getDirection(), buy1.getDelegation_price(), tradeCount, buy1
						.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				trades.add(sellNTradeLog);
			} else {	// 爆仓卖单
				// 系统持仓(多仓)记录,(卖单的多仓):：卖完持仓量减少
				DelegationBC(partition, code, sellcache1.getF_user_id(), sellcache1.getPending_no(), sellcache1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_BUY);
				// pendingDelegationDao.updateNumByID(sellcache1.getDelegation_id(),code,tradeCount,tradeCount.negate(),ZERO);
				pendingDelegationCacheDao.updateNumByID(partition, sellcache1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), 0l, buyNTradeLog.getTrade_type_id(), Contents.CZLX_DDBC, Contents.TRADE_FLAG_QP, sellcache1
						.getDirection(), buy1.getDelegation_price(), tradeCount, buy1.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
			}
			
			trades.add(buyNTradeLog);
		} else {// 爆仓买单
			// 系统持仓表(买单的空仓)，买进后持仓量减少
			DelegationBC(partition, code, buy1.getF_user_id(), buy1.getPending_no(), buy1.getCommission_rate(), coefficient, tradeCount,
					tradePrice, Contents.DIRECTION_SELL);

			// 这个挂单要结束了，全部成交
//			BigDecimal allDeposit = BTCQMath.getDeposit(buy1.getDelegation_count(), buy1.getDelegation_price(), buy1.getMultiple(), coefficient);
//			NPendingLog pendingLog = new NPendingLog(buy1.getDelegation_id(), buy1.getDelegation_time(), buy1.getTransaction_id(), buy1.getTransaction_code(),
//					Contents.CZLX_KTBC, buy1.getDelegation_count(), buy1.getDelegation_price(), buy1.getMultiple(), buy1.getDelegation_count(), ZERO, allDeposit, Contents.PENDING_LOG_ACTION_QP,
//					buy1.getF_user_id(), buy1.getPending_no());
//			pendingLogDao.insertLog(pendingLog);

			if (Contents.PRIORITY_KC == sellcache1.getPriority()) {	// 开仓卖单
				NTradeLog sellNTradeLog = DelegateCacheKC(currencyId, partition, sellUser, code, sellcache1, tradeCount, tradePrice, Contents.TRADE_TYPE_SELL, sellcache1.getF_fromremain(), coefficient, false, isQP);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), sellNTradeLog.getTrade_id(), Contents.CZLX_KTBC, sellNTradeLog.getTrade_type_id(), Contents.TRADE_FLAG_JY, sellcache1
						.getDirection(), buy1.getDelegation_price(), tradeCount, buy1.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				trades.add(sellNTradeLog);
			} else if (Contents.PRIORITY_PC == sellcache1.getPriority()) {	// 平仓卖单
				NTradeLog sellNTradeLog = DelegateCachePC(currencyId, partition, sellUser, code, sellcache1, tradeCount, tradePrice, Contents.CZLX_DDPC, tradeDirection, coefficient, false, isQP);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), sellNTradeLog.getTrade_id(), Contents.CZLX_KTBC, sellNTradeLog.getTrade_type_id(), Contents.TRADE_FLAG_JY, sellcache1
						.getDirection(), buy1.getDelegation_price(), tradeCount, buy1.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				trades.add(sellNTradeLog);
			} else {	// 爆仓卖单
				// 系统持仓记录(多仓),卖出后持仓量减少
				DelegationBC(partition, code, sellcache1.getF_user_id(), sellcache1.getPending_no(), sellcache1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_BUY);
				// 爆仓吃爆仓插入交易记录
				// 用户持仓
				NTradeLog bcNTradeLog = new NTradeLog(sellcache1.getTransaction_id(), code, 0, 1, ZERO, tradePrice, tradeCount, sellcache1.getMultiple(), ZERO, ZERO, ZERO, 0l, sellcache1.getPending_no());
				bcNTradeLog = tradeLogDao.insertLog(partition, bcNTradeLog);

				pendingDelegationCacheDao.updateNumByID(partition, sellcache1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), 0l, Contents.CZLX_KTBC, Contents.CZLX_DDBC, Contents.TRADE_FLAG_QP, sellcache1.getDirection(), buy1
						.getDelegation_price(), tradeCount, buy1.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				trades.add(bcNTradeLog);
			}
		}

		pendingDelegationDao.deletePending(partition, code, buy1.getDelegation_id());

		return trades; 
	}
	
	
	public List<NTradeLog> eatBuy1(int currencyId, String partition, String code, NPendingDelegation buy1, NPendingDelegation sell1, BigDecimal coefficient, boolean isQP) throws ServiceException {
		BigDecimal ZERO = BigDecimal.ZERO;
		List<NTradeLog> trades = new ArrayList<NTradeLog>();
//		logger.info("交易服务：sell1的数量多于buy1的数量--eatBuy1()");
		BQMember buyUser = bqMemberDao.get(BQMember.class, buy1.getF_user_id());
		BQMember sellUser = bqMemberDao.get(BQMember.class, sell1.getF_user_id());

		BigDecimal tradeCount = buy1.getUndeal_count();
		if ((buy1.getDelegation_count().subtract(buy1.getDeal_count())).compareTo(buy1.getUndeal_count()) != 0) {
			ServiceException sex = new ServiceException("buy");
			logger.error(buy1.getF_user_id() + "的买单" + buy1.getDelegation_id() + "有问题：");
			throw sex;
		}
		if ((sell1.getDelegation_count().subtract(sell1.getDeal_count())).compareTo(sell1.getUndeal_count()) != 0) {
			ServiceException sex = new ServiceException("sell");
			logger.error(sell1.getF_user_id() + "的卖单" + sell1.getDelegation_id() + "有问题：");
			throw sex;
		}

		BigDecimal tradePrice = sell1.getDelegation_price();
		int tradeDirection = sell1.getDirection();

		// 开仓买单，持仓量++ 买单全被吃掉
		if (Contents.PRIORITY_KC == buy1.getPriority()) {
			/** 买单处理结束, 卖单开始 ***/
			// ***********************3
			long sellTradeID=0l;
			int sellTradeFlag=Contents.TRADE_FLAG_JY;
			int sellTradeTypeID=Contents.CZLX_DDBC;

			if (Contents.PRIORITY_KC == sell1.getPriority()) {	// 开仓卖单
				NTradeLog sellNTradeLog = DelegateKC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.TRADE_TYPE_SELL, sell1.getF_fromremain(), coefficient, false);
				sellTradeID=sellNTradeLog.getTrade_id();
				sellTradeTypeID=sellNTradeLog.getTrade_type_id();
				trades.add(sellNTradeLog);
			} else if (Contents.PRIORITY_PC == sell1.getPriority()) {	// 平仓卖单
				NTradeLog sellNTradeLog = DelegatePC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.CZLX_DDPC, coefficient, tradeDirection, false);
				sellTradeID=sellNTradeLog.getTrade_id();
				sellTradeTypeID=sellNTradeLog.getTrade_type_id();
				trades.add(sellNTradeLog);
			} else {	// 爆仓卖单
				// 系统持仓(多仓)记录,(卖单的多仓):：卖完持仓量减少
				DelegationBC(partition, code, sell1.getF_user_id(), sell1.getPending_no(), sell1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_BUY);
				pendingDelegationDao.updateNumByID(partition, sell1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO, BigDecimal.ZERO, coefficient);
				sellTradeFlag=Contents.TRADE_FLAG_QP;
			}
			
			NTradeLog buyNTradeLog = DelegateKC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.TRADE_TYPE_BUY, buy1.getF_fromremain(), coefficient, true);
			NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
					.getF_user_id(), sellUser.getF_user_email(), sellTradeID, buyNTradeLog.getTrade_type_id(), sellTradeTypeID, sellTradeFlag, sell1.getDirection(), buy1.getDelegation_price(), tradeCount, buy1
					.getTransaction_id(), buy1.getTransaction_code());
			memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
			
			trades.add(buyNTradeLog);
		} else if (Contents.PRIORITY_PC == buy1.getPriority()) {	// 平仓买单，持仓冻结量--
			NTradeLog buyNTradeLog = DelegatePC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.CZLX_KTPC, coefficient, tradeDirection, true);
			
			if (Contents.PRIORITY_KC == sell1.getPriority()) {	// 开仓卖单
				NTradeLog sellNTradeLog = DelegateKC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.TRADE_TYPE_SELL, sell1.getF_fromremain(), coefficient, false);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog
						.getTrade_type_id(), Contents.TRADE_FLAG_JY, sell1.getDirection(), buy1.getDelegation_price(), tradeCount, buy1
						.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				trades.add(sellNTradeLog);
			} else if (Contents.PRIORITY_PC == sell1.getPriority()) {	// 平仓卖单
				NTradeLog sellNTradeLog = DelegatePC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.CZLX_DDPC, coefficient, tradeDirection, false);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog
						.getTrade_type_id(), Contents.TRADE_FLAG_JY, sell1.getDirection(), buy1.getDelegation_price(), tradeCount, buy1
						.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				trades.add(sellNTradeLog);
			} else {	// 爆仓卖单
				// 系统持仓(多仓)记录,(卖单的多仓):：卖完持仓量减少
				DelegationBC(partition, code, sell1.getF_user_id(), sell1.getPending_no(), sell1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_BUY);
				// pendingDelegationDao.updateNumByID(sell1.getDelegation_id(),code,tradeCount,tradeCount.negate(),ZERO);
				pendingDelegationDao.updateNumByID(partition, sell1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO, BigDecimal.ZERO, coefficient);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), 0l, buyNTradeLog.getTrade_type_id(), Contents.CZLX_DDBC, Contents.TRADE_FLAG_QP, sell1
						.getDirection(), buy1.getDelegation_price(), tradeCount, buy1.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
			}

			trades.add(buyNTradeLog);
		} else {// 爆仓买单
			// 系统持仓表(买单的空仓)，买进后持仓量减少
			DelegationBC(partition, code, buy1.getF_user_id(), buy1.getPending_no(), buy1.getCommission_rate(), coefficient, tradeCount,
					tradePrice, Contents.DIRECTION_SELL);

			// 这个挂单要结束了，全部成交
//			BigDecimal allDeposit = BTCQMath.getDeposit(buy1.getDelegation_count(), buy1.getDelegation_price(), buy1.getMultiple(), coefficient);
//			NPendingLog pendingLog = new NPendingLog(buy1.getDelegation_id(), buy1.getDelegation_time(), buy1.getTransaction_id(), buy1.getTransaction_code(),
//					Contents.CZLX_KTBC, buy1.getDelegation_count(), buy1.getDelegation_price(), buy1.getMultiple(), buy1.getDelegation_count(), ZERO, allDeposit, Contents.PENDING_LOG_ACTION_QP,
//					buy1.getF_user_id(), buy1.getPending_no());
//			pendingLogDao.insertLog(pendingLog);

			if (Contents.PRIORITY_KC == sell1.getPriority()) {	// 开仓卖单
				NTradeLog sellNTradeLog = DelegateKC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.TRADE_TYPE_SELL, sell1.getF_fromremain(), coefficient, false);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), sellNTradeLog.getTrade_id(), Contents.CZLX_KTBC, sellNTradeLog.getTrade_type_id(), Contents.TRADE_FLAG_JY, sell1
						.getDirection(), buy1.getDelegation_price(), tradeCount, buy1.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				trades.add(sellNTradeLog);
			} else if (Contents.PRIORITY_PC == sell1.getPriority()) {	// 平仓卖单
				NTradeLog sellNTradeLog = DelegatePC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.CZLX_DDPC, coefficient, tradeDirection, false);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), sellNTradeLog.getTrade_id(), Contents.CZLX_KTBC, sellNTradeLog.getTrade_type_id(), Contents.TRADE_FLAG_JY, sell1
						.getDirection(), buy1.getDelegation_price(), tradeCount, buy1.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				trades.add(sellNTradeLog);
			} else {	// 爆仓卖单
				// 系统持仓记录(多仓),卖出后持仓量减少
				DelegationBC(partition, code, sell1.getF_user_id(), sell1.getPending_no(), sell1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_BUY);
				// 爆仓吃爆仓插入交易记录
				NTradeLog bcNTradeLog = new NTradeLog(sell1.getTransaction_id(), code, 0, 1, ZERO, tradePrice, tradeCount, sell1.getMultiple(), ZERO, ZERO, ZERO, 0l, sell1.getPending_no());
				bcNTradeLog = tradeLogDao.insertLog(partition, bcNTradeLog);

				pendingDelegationDao.updateNumByID(partition, sell1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO, BigDecimal.ZERO, coefficient);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), 0l, Contents.CZLX_KTBC, Contents.CZLX_DDBC, Contents.TRADE_FLAG_QP, sell1.getDirection(), buy1
						.getDelegation_price(), tradeCount, buy1.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(bcNTradeLog);
			}
		}

		pendingDelegationDao.deletePending(partition, code, buy1.getDelegation_id());
		
		return trades;
	}


	// 1检查NMemberTransaction中是否有买方的记录
	/*
	 * 如果没有新建一条NMemberTransaction， 修改买方的账户表
	 * 在tradeLog,memberTrade,中新增买方记录NPendingDelegationCache中删除买方记录
	 * 修改卖方的NPendingDelegation的数量，在tradeLog,memberTrade,中新增卖方记录
	 * 检查NMemberTransaction中是否有卖方的记录，没有新建持仓记录，并修改账户表；有就更改持仓表和账户表
	 * 在memberTradeDetail新增一条记录 如果有的话 修改持仓表，和账户表
	 * 在tradeLog,memberTrade,中新增买方记录NPendingDelegation中删除买方记录
	 * 修改卖方的NPendingDelegationCache的数量，在tradeLog,memberTrade,中新增卖方记录
	 * 检查NMemberTransaction中是否有卖方的记录，没有新建持仓记录，并修改账户表；有就更改持仓表和账户表
	 * 在memberTradeDetail新增一条记录
	 */
	/** buycache1数量小于等于sell1的数量 吃掉buycache1 */
	public List<NTradeLog> eatBuyCache1(int currencyId, String partition, String code, NPendingDelegationCache buycache1, NPendingDelegation sell1, BigDecimal coefficient, boolean equal, boolean isQP) throws ServiceException {
		BigDecimal ZERO = BigDecimal.ZERO;
		List<NTradeLog> trades = new ArrayList<NTradeLog>();
//		logger.info("交易服务：buycache1数量小于等于sell1的数量  --eatBuyCache1()");
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
		int tradeDirection = buycache1.getDirection();

		if (Contents.PRIORITY_KC == buycache1.getPriority()) {	// 开仓买单，持仓量++
			long sellTradeID = 0l;
			int sellTradeFlag = Contents.TRADE_FLAG_JY;
			int sellTradeTypeID = Contents.CZLX_DDBC;
			
			if (Contents.PRIORITY_KC == sell1.getPriority()) {	// 开仓卖单
				NTradeLog sellNTradeLog = DelegateKC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.TRADE_TYPE_SELL, sell1.getF_fromremain(), coefficient, equal);
				sellTradeID = sellNTradeLog.getTrade_id();
				sellTradeTypeID = sellNTradeLog.getTrade_type_id();
				trades.add(sellNTradeLog);
			} else if (Contents.PRIORITY_PC == sell1.getPriority()) {	// 平仓卖单
				NTradeLog sellNTradeLog = DelegatePC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.CZLX_DDPC, coefficient, tradeDirection, equal);
				sellTradeID = sellNTradeLog.getTrade_id();
				sellTradeTypeID = sellNTradeLog.getTrade_type_id();
				trades.add(sellNTradeLog);
			} else {	// 爆仓卖单
				DelegationBC(partition, code, sell1.getF_user_id(), sell1.getPending_no(), sell1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_BUY);
				pendingDelegationDao.updateNumByID(partition, sell1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO, BigDecimal.ZERO, coefficient);
				sellTradeFlag = Contents.TRADE_FLAG_QP;
			}
			
			NTradeLog buyNTradeLog = DelegateCacheKC(currencyId, partition, buyUser, code, buycache1, tradeCount, tradePrice, Contents.TRADE_TYPE_BUY, buycache1.getF_fromremain(), coefficient, true, isQP);
			NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), 
					sellUser.getF_user_id(), sellUser.getF_user_email(), sellTradeID, buyNTradeLog.getTrade_type_id(), sellTradeTypeID, sellTradeFlag, 
					buycache1.getDirection(), sell1.getDelegation_price(), tradeCount, buycache1.getTransaction_id(), buycache1.getTransaction_code());
			memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);

			trades.add(buyNTradeLog);
		} else if (Contents.PRIORITY_PC == buycache1.getPriority()) {	// 平仓买单，持仓冻结量--
			NTradeLog buyNTradeLog = DelegateCachePC(currencyId, partition, buyUser, code, buycache1, tradeCount, tradePrice, Contents.CZLX_KTPC, tradeDirection, coefficient, true, isQP);
			
			if (Contents.PRIORITY_KC == sell1.getPriority()) {// 开仓卖单
				NTradeLog sellNTradeLog = DelegateKC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.TRADE_TYPE_SELL, sell1.getF_fromremain(), coefficient, equal);
				
				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog
						.getTrade_type_id(), Contents.TRADE_FLAG_JY, buycache1.getDirection(), sell1.getDelegation_price(), tradeCount, sell1
						.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(sellNTradeLog);
			} else if (Contents.PRIORITY_PC == sell1.getPriority()) {	// 平仓卖单
				NTradeLog sellNTradeLog = DelegatePC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.CZLX_DDPC, coefficient, tradeDirection, equal);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog
						.getTrade_type_id(), Contents.TRADE_FLAG_JY, buycache1.getDirection(), sell1.getDelegation_price(), tradeCount, sell1
						.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(sellNTradeLog);
			} else {	// 爆仓卖单
				DelegationBC(partition, code, sell1.getF_user_id(), sell1.getPending_no(), sell1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_BUY);
				pendingDelegationDao.updateNumByID(partition, sell1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO, BigDecimal.ZERO, coefficient);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), 0l, buyNTradeLog.getTrade_type_id(), Contents.CZLX_DDBC, Contents.TRADE_FLAG_JY, buycache1
						.getDirection(), sell1.getDelegation_price(), tradeCount, sell1.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
			}

			trades.add(buyNTradeLog);
		} else {	// 爆仓买单
			DelegationBC(partition, code, buycache1.getF_user_id(), buycache1.getPending_no(), buycache1.getCommission_rate(), coefficient, tradeCount,
					tradePrice, Contents.DIRECTION_SELL);

			// 这个挂单要结束了，全部成交
//			BigDecimal deposit = BTCQMath.getDeposit(buycache1.getDelegation_count(), buycache1.getDelegation_price(), buycache1.getMultiple(), coefficient);
//			NPendingLog pendingLog = new NPendingLog(buycache1.getDelegation_id(), buycache1.getDelegation_time(), buycache1.getTransaction_id(), buycache1
//					.getTransaction_code(), Contents.CZLX_KTBC, buycache1.getDelegation_count(), buycache1.getDelegation_price(), buycache1.getMultiple(),
//					buycache1.getDelegation_count(), ZERO, deposit, Contents.PENDING_LOG_ACTION_QP, buycache1.getF_user_id(), buycache1.getPending_no());
//			pendingLogDao.insertLog(pendingLog);

			if (Contents.PRIORITY_KC == sell1.getPriority()) {	// 开仓卖单
				NTradeLog sellNTradeLog = DelegateKC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.TRADE_TYPE_SELL, sell1.getF_fromremain(), coefficient, equal);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), sellNTradeLog.getTrade_id(), Contents.CZLX_KTBC, sellNTradeLog.getTrade_type_id(), Contents.TRADE_FLAG_QP, buycache1
						.getDirection(), sell1.getDelegation_price(), tradeCount, sell1.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(sellNTradeLog);
			} else if (Contents.PRIORITY_PC == sell1.getPriority()) {	// 平仓卖单
				NTradeLog sellNTradeLog = DelegatePC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.CZLX_DDPC, coefficient, tradeDirection, equal);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), sellNTradeLog.getTrade_id(), Contents.CZLX_KTBC, sellNTradeLog.getTrade_type_id(), Contents.TRADE_FLAG_QP, buycache1
						.getDirection(), sell1.getDelegation_price(), tradeCount, sell1.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(sellNTradeLog);
			} else {	// 爆仓卖单
				// 系统持仓(多仓)记录,(卖单的多仓):：卖完持仓量减少
				DelegationBC(partition, code, sell1.getF_user_id(), sell1.getPending_no(), sell1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_BUY);
				// 爆仓吃爆仓插入交易记录
				NTradeLog bcNTradeLog = new NTradeLog(sell1.getTransaction_id(), code, 0, 1, ZERO, tradePrice, tradeCount, sell1.getMultiple(), ZERO, ZERO, ZERO, 0l, sell1.getPending_no());
				bcNTradeLog = tradeLogDao.insertLog(partition, bcNTradeLog);
				
				pendingDelegationDao.updateNumByID(partition, sell1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO, BigDecimal.ZERO, coefficient);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), 0l, Contents.CZLX_KTBC, Contents.CZLX_DDBC, Contents.TRADE_FLAG_QP, buycache1.getDirection(), sell1
						.getDelegation_price(), tradeCount, sell1.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(bcNTradeLog);
			}
		}
		
		if (!isQP) {
			pendingDelegationCacheDao.deletePending(partition, code, buycache1.getDelegation_id());
		}
		
		if (equal) {
			pendingDelegationDao.deletePending(partition, code, sell1.getDelegation_id());
		}
		
		return trades;
	}

	// 1检查NMemberTransaction中是否有卖方的记录
	/*
	 * 如果没有新建一条NMemberTransaction， 修改卖方的账户表
	 * 在pendingLog,tradeLog,memberTrade,中新增卖方记录NPendingDelegation中删除卖方记录
	 * 修改买方的NPendingDelegationCache的数量，在tradeLog,memberTrade,中新增买方记录
	 * 在memberTradeDetail新增一条记录 如果有的话 修改持仓表，和账户表
	 * 在pendingLog,tradeLog,memberTrade,中新增卖方记录NPendingDelegation中删除卖方记录
	 * 修改买方的NPendingDelegationCache的数量，在tradeLog,memberTrade,中新增买方记录
	 * 在memberTradeDetail新增一条记录
	 */
	/**
	 * buycache1数量大于sell1的数量
	 * 
	 * @throws ServiceException
	 */
	public List<NTradeLog> eatSell1(int currencyId, String partition, String code, NPendingDelegationCache buycache1, NPendingDelegation sell1, BigDecimal coefficient, boolean isQP) throws ServiceException {
		BigDecimal ZERO = BigDecimal.ZERO;
		List<NTradeLog> trades = new ArrayList<NTradeLog>();
//		logger.info("交易服务：buycache1数量大于sell1的数量  --eatSell1()");
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
		int tradeDirection = buycache1.getDirection();
		if (Contents.PRIORITY_KC == sell1.getPriority()) {// 开仓卖单，持仓量++
			long buyTradeID=0l;
			int buyTradeFlag=Contents.TRADE_FLAG_JY;
			int buyTradeTypeID=Contents.CZLX_KTBC;
			
			if (Contents.PRIORITY_KC == buycache1.getPriority()) {// 开仓买单
				NTradeLog buyNTradeLog = DelegateCacheKC(currencyId, partition, buyUser, code, buycache1, tradeCount, tradePrice, Contents.TRADE_TYPE_BUY, buycache1.getF_fromremain(), coefficient, false, isQP);
				
				buyTradeID = buyNTradeLog.getTrade_id();
				buyTradeTypeID = buyNTradeLog.getTrade_type_id();
				
				trades.add(buyNTradeLog);
			} else if (Contents.PRIORITY_PC == buycache1.getPriority()) {// 平仓买单 // (空头平仓)
				NTradeLog buyNTradeLog = DelegateCachePC(currencyId, partition, buyUser, code, buycache1, tradeCount, tradePrice, Contents.CZLX_KTPC, tradeDirection, coefficient, false, isQP);
				
				buyTradeID = buyNTradeLog.getTrade_id();
				buyTradeTypeID = buyNTradeLog.getTrade_type_id();
				
				trades.add(buyNTradeLog);
			} else {// 爆仓买单
				DelegationBC(partition, code, buycache1.getF_user_id(), buycache1.getPending_no(), buycache1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_SELL);
				pendingDelegationCacheDao.updateNumByID(partition, buycache1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO);
				buyTradeFlag=Contents.TRADE_FLAG_QP;
			}
			
			NTradeLog sellNTradeLog = DelegateKC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.TRADE_TYPE_SELL, sell1.getF_fromremain(), coefficient, true);
			NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyTradeID, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
					.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyTradeTypeID, sellNTradeLog
					.getTrade_type_id(), buyTradeFlag, buycache1.getDirection(), sell1.getDelegation_price(), tradeCount, sell1
					.getTransaction_id(), sell1.getTransaction_code());
			memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);

			trades.add(sellNTradeLog);
		} else if (Contents.PRIORITY_PC == sell1.getPriority()) {// 平仓卖单，多仓持仓冻结量--
			NTradeLog sellNTradeLog = DelegatePC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.CZLX_DDPC, coefficient, tradeDirection, true);
			
			if (Contents.PRIORITY_KC == buycache1.getPriority()) {// 开仓买单
				NTradeLog buyNTradeLog = DelegateCacheKC(currencyId, partition, buyUser, code, buycache1, tradeCount, tradePrice, Contents.TRADE_TYPE_BUY, buycache1.getF_fromremain(), coefficient, false, isQP);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog
						.getTrade_type_id(), Contents.TRADE_FLAG_JY, buycache1.getDirection(), sell1.getDelegation_price(), tradeCount, sell1
						.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(buyNTradeLog);
			} else if (Contents.PRIORITY_PC == buycache1.getPriority()) {// 平仓买单
				NTradeLog buyNTradeLog = DelegateCachePC(currencyId, partition, buyUser, code, buycache1, tradeCount, tradePrice, Contents.CZLX_KTPC, tradeDirection, coefficient, false, isQP);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog
						.getTrade_type_id(), Contents.TRADE_FLAG_JY, buycache1.getDirection(), sell1.getDelegation_price(), tradeCount, sell1
						.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(buyNTradeLog);
			} else {// 爆仓买单
				// 系统持仓(空仓)记录,(买单的空仓):：买完持仓量减少
				DelegationBC(partition, code, buycache1.getF_user_id(), buycache1.getPending_no(), buycache1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_SELL);
				
				pendingDelegationCacheDao.updateNumByID(partition, buycache1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), sellNTradeLog.getTrade_id(), Contents.CZLX_KTBC, sellNTradeLog.getTrade_type_id(), Contents.TRADE_FLAG_QP, buycache1
						.getDirection(), sell1.getDelegation_price(), tradeCount, sell1.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
			}

			trades.add(sellNTradeLog);
		} else {// 爆仓卖单
			// 系统持仓表(卖单的多仓)，卖出后持仓量减少
			DelegationBC(partition, code, sell1.getF_user_id(), sell1.getPending_no(), sell1.getCommission_rate(), coefficient, tradeCount,
					tradePrice, Contents.DIRECTION_BUY);

			// 这个挂单要结束了，全部成交
//			BigDecimal allDeposit = BTCQMath.getDeposit(sell1.getDelegation_count(), sell1.getDelegation_price(), sell1.getMultiple(), coefficient);
//			NPendingLog pendingLog = new NPendingLog(sell1.getDelegation_id(), sell1.getDelegation_time(), sell1.getTransaction_id(),
//					sell1.getTransaction_code(), Contents.CZLX_KTBC, sell1.getDelegation_count(), sell1.getDelegation_price(), sell1.getMultiple(), sell1
//							.getDelegation_count(), ZERO, allDeposit, Contents.PENDING_LOG_ACTION_QP, sell1.getF_user_id(),
//					sell1.getPending_no());
//			pendingLogDao.insertLog(pendingLog);
			
			if (Contents.PRIORITY_KC == buycache1.getPriority()) {// 开仓买单
				NTradeLog buyNTradeLog = DelegateCacheKC(currencyId, partition, buyUser, code, buycache1, tradeCount, tradePrice, Contents.TRADE_TYPE_BUY, buycache1.getF_fromremain(), coefficient, false, isQP);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), 0l, buyNTradeLog.getTrade_type_id(), Contents.CZLX_DDBC, Contents.TRADE_FLAG_JY, buycache1
						.getDirection(), sell1.getDelegation_price(), tradeCount, sell1.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(buyNTradeLog);
			} else if (Contents.PRIORITY_PC == buycache1.getPriority()) {// 平仓买单
				NTradeLog buyNTradeLog = DelegateCachePC(currencyId, partition, buyUser, code, buycache1, tradeCount, tradePrice, Contents.CZLX_KTPC, tradeDirection, coefficient, false, isQP);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), 0l, buyNTradeLog.getTrade_type_id(), Contents.CZLX_DDBC, Contents.TRADE_FLAG_JY, buycache1
						.getDirection(), sell1.getDelegation_price(), tradeCount, sell1.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(buyNTradeLog);
			} else {// 爆仓买单
				DelegationBC(partition, code, buycache1.getF_user_id(), buycache1.getPending_no(), buycache1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_SELL);
				// 爆仓吃爆仓插入交易记录
				NTradeLog bcNTradeLog = new NTradeLog(buycache1.getTransaction_id(), code, 0, 1, ZERO, tradePrice, tradeCount, buycache1.getMultiple(), ZERO, ZERO, ZERO, 0l, buycache1.getPending_no());
				bcNTradeLog = tradeLogDao.insertLog(partition, bcNTradeLog);

				pendingDelegationCacheDao.updateNumByID(partition, buycache1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), 0l, Contents.CZLX_KTBC, Contents.CZLX_DDBC, Contents.TRADE_FLAG_QP, buycache1.getDirection(), sell1
						.getDelegation_price(), tradeCount, sell1.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(bcNTradeLog);
			}
		}
		
		pendingDelegationDao.deletePending(partition, code, sell1.getDelegation_id());
		
		return trades;
	}
	
	public List<NTradeLog> eatSell1(int currencyId, String partition, String code, NPendingDelegation buy1, NPendingDelegation sell1, BigDecimal coefficient, boolean isQP) throws ServiceException {
		BigDecimal ZERO = BigDecimal.ZERO;
		List<NTradeLog> trades = new ArrayList<NTradeLog>();
//		logger.info("交易服务：buy1数量大于sell1的数量  --eatSell1()");
		BQMember buyUser = bqMemberDao.get(BQMember.class, buy1.getF_user_id());
		BQMember sellUser = bqMemberDao.get(BQMember.class, sell1.getF_user_id());

		BigDecimal tradeCount = sell1.getUndeal_count();
		if ((buy1.getDelegation_count().subtract(buy1.getDeal_count())).compareTo(buy1.getUndeal_count()) != 0) {
			ServiceException sex = new ServiceException("buy");
			logger.error(buy1.getF_user_id() + "的买单" + buy1.getDelegation_id() + "有问题：");
			throw sex;
		}
		
		if ((sell1.getDelegation_count().subtract(sell1.getDeal_count())).compareTo(sell1.getUndeal_count()) != 0) {
			ServiceException sex = new ServiceException("sell");
			logger.error(sell1.getF_user_id() + "的买单" + sell1.getDelegation_id() + "有问题：");
			throw sex;
		}
		
		BigDecimal tradePrice = sell1.getDelegation_price();
		int tradeDirection = buy1.getDirection();
		if (Contents.PRIORITY_KC == sell1.getPriority()) {// 开仓卖单，持仓量++
			long buyTradeID=0l;
			int buyTradeFlag=Contents.TRADE_FLAG_JY;
			int buyTradeTypeID=Contents.CZLX_KTBC;
			
			if (Contents.PRIORITY_KC == buy1.getPriority()) {// 开仓买单
				NTradeLog buyNTradeLog = DelegateKC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.TRADE_TYPE_BUY, buy1.getF_fromremain(), coefficient, false);
				
				buyTradeID = buyNTradeLog.getTrade_id();
				buyTradeTypeID = buyNTradeLog.getTrade_type_id();
				
				trades.add(buyNTradeLog);
			} else if (Contents.PRIORITY_PC == buy1.getPriority()) {// 平仓买单 // (空头平仓)
				NTradeLog buyNTradeLog = DelegatePC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.CZLX_KTPC, coefficient, tradeDirection, false);
				
				buyTradeID = buyNTradeLog.getTrade_id();
				buyTradeTypeID = buyNTradeLog.getTrade_type_id();
				
				trades.add(buyNTradeLog);
			} else {// 爆仓买单
				DelegationBC(partition, code, buy1.getF_user_id(), buy1.getPending_no(), buy1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_SELL);
				pendingDelegationDao.updateNumByID(partition, buy1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO, BigDecimal.ZERO, coefficient);
				buyTradeFlag=Contents.TRADE_FLAG_QP;
			}
			
			NTradeLog sellNTradeLog = DelegateKC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.TRADE_TYPE_SELL, sell1.getF_fromremain(), coefficient, true);
			NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyTradeID, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
					.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyTradeTypeID, sellNTradeLog
					.getTrade_type_id(), buyTradeFlag, buy1.getDirection(), sell1.getDelegation_price(), tradeCount, sell1
					.getTransaction_id(), sell1.getTransaction_code());
			memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);

			trades.add(sellNTradeLog);
		} else if (Contents.PRIORITY_PC == sell1.getPriority()) {// 平仓卖单，多仓持仓冻结量--
			NTradeLog sellNTradeLog = DelegatePC(currencyId, partition, sellUser, code, sell1, tradeCount, tradePrice, Contents.CZLX_DDPC, coefficient, tradeDirection, true);
			
			if (Contents.PRIORITY_KC == buy1.getPriority()) {// 开仓买单
				NTradeLog buyNTradeLog = DelegateKC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.TRADE_TYPE_BUY, buy1.getF_fromremain(), coefficient, false);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog
						.getTrade_type_id(), Contents.TRADE_FLAG_JY, buy1.getDirection(), sell1.getDelegation_price(), tradeCount, sell1
						.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(buyNTradeLog);
			} else if (Contents.PRIORITY_PC == buy1.getPriority()) {// 平仓买单
				NTradeLog buyNTradeLog = DelegatePC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.CZLX_KTPC, coefficient, tradeDirection, false);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog
						.getTrade_type_id(), Contents.TRADE_FLAG_JY, buy1.getDirection(), sell1.getDelegation_price(), tradeCount, sell1
						.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(buyNTradeLog);
			} else {// 爆仓买单
				// 系统持仓(空仓)记录,(买单的空仓):：买完持仓量减少
				DelegationBC(partition, code, buy1.getF_user_id(), buy1.getPending_no(), buy1.getCommission_rate(), coefficient, tradeCount,
						tradePrice,  Contents.DIRECTION_SELL);
				
				pendingDelegationDao.updateNumByID(partition, buy1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO, BigDecimal.ZERO, coefficient);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), sellNTradeLog.getTrade_id(), Contents.CZLX_KTBC, sellNTradeLog.getTrade_type_id(), Contents.TRADE_FLAG_QP, buy1
						.getDirection(), sell1.getDelegation_price(), tradeCount, sell1.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
			}

			trades.add(sellNTradeLog);
		} else {// 爆仓卖单
			// 系统持仓表(卖单的多仓)，卖出后持仓量减少
			DelegationBC(partition, code, sell1.getF_user_id(), sell1.getPending_no(), sell1.getCommission_rate(), coefficient, tradeCount,
					tradePrice, Contents.DIRECTION_BUY);

			// 这个挂单要结束了，全部成交
//			BigDecimal allDeposit = BTCQMath.getDeposit(sell1.getDelegation_count(), sell1.getDelegation_price(), sell1.getMultiple(), coefficient);
//			NPendingLog pendingLog = new NPendingLog(sell1.getDelegation_id(), sell1.getDelegation_time(), sell1.getTransaction_id(),
//					sell1.getTransaction_code(), Contents.CZLX_KTBC, sell1.getDelegation_count(), sell1.getDelegation_price(), sell1.getMultiple(), sell1
//							.getDelegation_count(), ZERO, allDeposit, Contents.PENDING_LOG_ACTION_QP, sell1.getF_user_id(),
//					sell1.getPending_no());
//			pendingLogDao.insertLog(pendingLog);
			
			if (Contents.PRIORITY_KC == buy1.getPriority()) {// 开仓买单
				NTradeLog buyNTradeLog = DelegateKC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.TRADE_TYPE_BUY, buy1.getF_fromremain(), coefficient, false);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), 0l, buyNTradeLog.getTrade_type_id(), Contents.CZLX_DDBC, Contents.TRADE_FLAG_JY, buy1
						.getDirection(), sell1.getDelegation_price(), tradeCount, sell1.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(buyNTradeLog);
			} else if (Contents.PRIORITY_PC == buy1.getPriority()) {// 平仓买单
				NTradeLog buyNTradeLog = DelegatePC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.CZLX_KTPC, coefficient, tradeDirection, false);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), 0l, buyNTradeLog.getTrade_type_id(), Contents.CZLX_DDBC, Contents.TRADE_FLAG_JY, buy1
						.getDirection(), sell1.getDelegation_price(), tradeCount, sell1.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(buyNTradeLog);
			} else {// 爆仓买单
				DelegationBC(partition, code, buy1.getF_user_id(), buy1.getPending_no(), buy1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_SELL);
				// 爆仓吃爆仓插入交易记录
				NTradeLog bcNTradeLog = new NTradeLog(buy1.getTransaction_id(), code, 0, 1, ZERO, tradePrice, tradeCount, buy1.getMultiple(), ZERO, ZERO, ZERO, 0l, buy1.getPending_no());
				bcNTradeLog = tradeLogDao.insertLog(partition, bcNTradeLog);

				pendingDelegationDao.updateNumByID(partition, buy1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO, BigDecimal.ZERO, coefficient);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), 0l, Contents.CZLX_KTBC, Contents.CZLX_DDBC, Contents.TRADE_FLAG_QP, buy1.getDirection(), sell1
						.getDelegation_price(), tradeCount, sell1.getTransaction_id(), sell1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(bcNTradeLog);
			}

		}
		
		pendingDelegationDao.deletePending(partition, code, sell1.getDelegation_id());
		
		return trades;
	}

	// 1检查NMemberTransaction中是否有卖方的记录
	/*
	 * 如果没有新建一条NMemberTransaction， 修改卖方的账户表
	 * 在tradeLog,memberTrade,中新增卖方记录NPendingDelegationCache中删除卖方记录
	 * 修改买方的NPendingDelegation的数量，在tradeLog,memberTrade,中新增买方记录
	 * 检查NMemberTransaction中是否有买方的记录，没有新建持仓记录，并修改账户表；有就更改持仓表和账户表
	 * 在memberTradeDetail新增一条记录 如果有的话 修改持仓表，和账户表
	 * 在tradeLog,memberTrade,中新增卖方记录NPendingDelegationCache中删除卖方记录
	 * 修改买方的NPendingDelegation的数量，在tradeLog,memberTrade,中新增买方记录
	 * 检查NMemberTransaction中是否有买方的记录，没有新建持仓记录，并修改账户表；有就更改持仓表和账户表
	 * 在memberTradeDetail新增一条记录
	 */
	/** sellcache1数量小于buy1的数量 */
	public List<NTradeLog> eatSellCache1(int currencyId, String partition, String code, NPendingDelegation buy1, NPendingDelegationCache sellcache1, BigDecimal coefficient, boolean equal, boolean isQP) throws ServiceException {
		
		// 调整空头开仓sellcache保证金，买价>卖价，此时成交价为买价
		if (sellcache1.getPriority() == Contents.PRIORITY_KC 
				&& buy1.getDelegation_price().compareTo(sellcache1.getDelegation_price()) > 0) {
			adjustSellCacheFrozen(currencyId, partition, buy1, sellcache1, coefficient);
		}
		
		BigDecimal ZERO = BigDecimal.ZERO;
		List<NTradeLog> trades = new ArrayList<NTradeLog>();
//		logger.info("交易服务：sellcache1数量小于buy1的数量  --eatSellCache1()");
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
		int tradeDirection = sellcache1.getDirection();

		if (Contents.PRIORITY_KC == sellcache1.getPriority()) {		// 开仓卖单，持仓量++
			long buyTradeID=0l;
			int buyTradeFlag=Contents.TRADE_FLAG_JY;
			int buyTradeTypeID=Contents.CZLX_KTBC;

			if (Contents.PRIORITY_KC == buy1.getPriority()) {	// 开仓买单
				NTradeLog buyNTradeLog = DelegateKC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.TRADE_TYPE_BUY, buy1.getF_fromremain(), coefficient, equal);
				buyTradeID = buyNTradeLog.getTrade_id();
				buyTradeTypeID = buyNTradeLog.getTrade_type_id();
				
				trades.add(buyNTradeLog);
			} else if (Contents.PRIORITY_PC == buy1.getPriority()) {	// 平仓买单
				NTradeLog buyNTradeLog = DelegatePC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.CZLX_KTPC, coefficient, tradeDirection, equal);
				buyTradeID = buyNTradeLog.getTrade_id();
				buyTradeTypeID = buyNTradeLog.getTrade_type_id();
				
				trades.add(buyNTradeLog);
			} else {	// 爆仓买单
				DelegationBC(partition, code, buy1.getF_user_id(), buy1.getPending_no(), buy1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_SELL);
				pendingDelegationDao.updateNumByID(partition, buy1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO, BigDecimal.ZERO, coefficient);
				buyTradeFlag=Contents.TRADE_FLAG_QP;
			}
			
			NTradeLog sellNTradeLog = DelegateCacheKC(currencyId, partition, sellUser, code, sellcache1, tradeCount, tradePrice, Contents.TRADE_TYPE_SELL, sellcache1.getF_fromremain(), coefficient, true, isQP);
			
			NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyTradeID, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
					.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyTradeTypeID, sellNTradeLog
					.getTrade_type_id(), buyTradeFlag, sellcache1.getDirection(), buy1.getDelegation_price(), tradeCount, buy1
					.getTransaction_id(), buy1.getTransaction_code());
			memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);

			trades.add(sellNTradeLog);
		} else if (Contents.PRIORITY_PC == sellcache1.getPriority()) {	// 平仓卖单，持仓冻结量--
			NTradeLog sellNTradeLog = DelegateCachePC(currencyId, partition, sellUser, code, sellcache1, tradeCount, tradePrice, Contents.CZLX_DDPC, tradeDirection, coefficient, true, isQP);

			if (Contents.PRIORITY_KC == buy1.getPriority()) {	// 开仓买单
				NTradeLog buyNTradeLog = DelegateKC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.TRADE_TYPE_BUY, buy1.getF_fromremain(), coefficient, equal);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog
						.getTrade_type_id(), Contents.TRADE_FLAG_JY, sellcache1.getDirection(), buy1.getDelegation_price(), tradeCount, buy1
						.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(buyNTradeLog);
			} else if (Contents.PRIORITY_PC == buy1.getPriority()) {	// 平仓买单
				NTradeLog buyNTradeLog = DelegatePC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.CZLX_KTPC, coefficient, tradeDirection, equal);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), sellNTradeLog.getTrade_id(), buyNTradeLog.getTrade_type_id(), sellNTradeLog
						.getTrade_type_id(), Contents.TRADE_FLAG_JY, sellcache1.getDirection(), buy1.getDelegation_price(), tradeCount, buy1
						.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(buyNTradeLog);
			} else {	// 爆仓买单
				DelegationBC(partition, code, buy1.getF_user_id(), buy1.getPending_no(), buy1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_SELL);
				pendingDelegationDao.updateNumByID(partition, buy1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO, BigDecimal.ZERO, coefficient);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), sellNTradeLog.getTrade_id(), Contents.CZLX_KTBC, sellNTradeLog.getTrade_type_id(), Contents.TRADE_FLAG_JY, sellcache1
						.getDirection(), buy1.getDelegation_price(), tradeCount, buy1.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
			}

			trades.add(sellNTradeLog);
		} else {// 爆仓卖单
			// 系统持仓表(卖单的多仓)，卖出后持仓量减少
			DelegationBC(partition, code, sellcache1.getF_user_id(), sellcache1.getPending_no(), sellcache1.getCommission_rate(), coefficient, tradeCount,
					tradePrice, Contents.DIRECTION_BUY);

			// 这个挂单要结束了，全部成交
//			BigDecimal allDeposit = BTCQMath.getDeposit(sellcache1.getDelegation_count(), sellcache1.getDelegation_price(), sellcache1.getMultiple(), coefficient);
//			NPendingLog pendingLog = new NPendingLog(sellcache1.getDelegation_id(), sellcache1.getDelegation_time(), sellcache1.getTransaction_id(), sellcache1
//					.getTransaction_code(), Contents.CZLX_DDBC, sellcache1.getDelegation_count(), sellcache1.getDelegation_price(), sellcache1.getMultiple(),
//					sellcache1.getDelegation_count(), ZERO, allDeposit, Contents.PENDING_LOG_ACTION_QP, sellcache1.getF_user_id(), sellcache1.getPending_no());
//			pendingLogDao.insertLog(pendingLog);

			if (Contents.PRIORITY_KC == buy1.getPriority()) {	// 开仓买单
				NTradeLog buyNTradeLog = DelegateKC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.TRADE_TYPE_BUY, buy1.getF_fromremain(), coefficient, equal);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), 0l, buyNTradeLog.getTrade_type_id(), Contents.CZLX_DDBC, Contents.TRADE_FLAG_QP, sellcache1
						.getDirection(), buy1.getDelegation_price(), tradeCount, buy1.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(buyNTradeLog);
			} else if (Contents.PRIORITY_PC == buy1.getPriority()) {	// 平仓买单
				NTradeLog buyNTradeLog = DelegatePC(currencyId, partition, buyUser, code, buy1, tradeCount, tradePrice, Contents.CZLX_KTPC, coefficient, tradeDirection, equal);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(buyNTradeLog.getTrade_id(), buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser
						.getF_user_id(), sellUser.getF_user_email(), 0l, buyNTradeLog.getTrade_type_id(), Contents.CZLX_DDBC, Contents.TRADE_FLAG_QP, sellcache1
						.getDirection(), buy1.getDelegation_price(), tradeCount, buy1.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(buyNTradeLog);
			} else {	// 爆仓买单
				DelegationBC(partition, code, buy1.getF_user_id(), buy1.getPending_no(), buy1.getCommission_rate(), coefficient, tradeCount,
						tradePrice, Contents.DIRECTION_SELL);
				// 爆仓吃爆仓插入交易记录
				NTradeLog bcNTradeLog = new NTradeLog(buy1.getTransaction_id(), code, 0, 1, ZERO, tradePrice, tradeCount, buy1.getMultiple(), ZERO, ZERO, ZERO, 0l, buy1.getPending_no());
				bcNTradeLog = tradeLogDao.insertLog(partition, bcNTradeLog);

				pendingDelegationDao.updateNumByID(partition, buy1.getDelegation_id(), code, tradeCount, tradeCount.negate(), ZERO, BigDecimal.ZERO, coefficient);

				NMemberTradeDetail tradeDetail = new NMemberTradeDetail(0l, buyUser.getF_user_id(), buyUser.getF_user_email(), sellUser.getF_user_id(), sellUser
						.getF_user_email(), 0l, Contents.CZLX_KTBC, Contents.CZLX_DDBC, Contents.TRADE_FLAG_QP, sellcache1.getDirection(), buy1
						.getDelegation_price(), tradeCount, buy1.getTransaction_id(), buy1.getTransaction_code());
				memberTradeDetailDao.insertTradeDetail(partition, tradeDetail);
				
				trades.add(bcNTradeLog);
			}

		}

		if (!isQP) {
			pendingDelegationCacheDao.deletePending(partition, code, sellcache1.getDelegation_id());
		}
		
		if (equal) {
			pendingDelegationDao.deletePending(partition, code, buy1.getDelegation_id());
		}

		return trades;
	}

	// 检查buycache1是否发生过成交，
	/*
	 * 发生过： 检查持仓表中是否有相关记录，没有就新增一条并修改bq_account表；有就修改持仓表和bq_account表
	 * 
	 * 没发生过： 不动持仓表，和账户表 向NPendingDelegation，penddingLog中新增一条记录，
	 */
	public void moveBuyCache1(int currencyId, String partition, String code, NPendingDelegationCache buycache1, BigDecimal coefficient) throws ServiceException {
//		logger.info("交易服务： --moveBuyCache1()");
		
		long uid = buycache1.getF_user_id();
		
		// 吃单转挂单，手续费有变
		BigDecimal pendingFeeRate = buycache1.getCommission_rate();
		if (pendingFeeRate.compareTo(BigDecimal.ZERO) != 0) {
			NTransactionFeeRate rate = bqTransactionFeeRateDao.findLevelByEatRate(partition, code, pendingFeeRate);
			if (rate != null && rate.getPending_commission_rate().compareTo(BigDecimal.ZERO) > 0) {
				pendingFeeRate = rate.getPending_commission_rate();
			} else {
				BQMemberLevel userlevel = bqMemberLevelService.findLevelByEatRate(pendingFeeRate);
				if (userlevel != null)
					pendingFeeRate = userlevel.getPending_commission_rate();
			}
		}
		
		if (buycache1.getPriority() == Contents.PRIORITY_KC) {
			BigDecimal changeFee = buycache1.getUndeal_count().multiply(buycache1.getDelegation_price()).multiply(coefficient)
					.multiply(pendingFeeRate.subtract(buycache1.getCommission_rate())).setScale(8, BigDecimal.ROUND_DOWN);
			if (changeFee.compareTo(BigDecimal.ZERO) != 0) {
				if (bqAccountContractDao.updateAmount(partition, uid, buycache1.getTransaction_code(), BigDecimal.ZERO, changeFee.negate(), changeFee, BigDecimal.ZERO) <= 0) {
					logger.info("moveBuyCache1 - updateAmount failed, change Btc: " + changeFee.negate().doubleValue() + ", chagne lockBtc: " + changeFee.doubleValue());
					throw new ServiceException("系统繁忙, 请重试");
				}
				
				NAccountContract bqContract = bqAccountContractDao.findByUserIDCode(partition, uid, buycache1.getTransaction_code());
				NContractAccountLog log = new NContractAccountLog("吃单(buycache)转挂单，调整冻结", currencyId, Contents.TRANS_TYPE_JY, changeFee.negate().signum(), bqContract
						.getF_btc().add(changeFee), changeFee.negate(), bqContract.getF_btc(), bqContract.getF_lockbtc().subtract(changeFee),
						bqContract.getF_lockbtc(), bqAccountContractDao.getUnix_TIMESTAMP(), uid,
						RandomUtil.getTrans_no(), buycache1.getDelegation_id(), buycache1.getTransaction_code());
				contractAccountLogDao.insertLog(partition, log);
			}
		}
		
		if (buycache1.getPriority() != Contents.PRIORITY_BC) {
			buycache1.setCommission_rate(pendingFeeRate);
		}
		
		if(pendingDelegationCacheDao.deletePending(partition, code, buycache1.getDelegation_id())<=0){
			throw new ServiceException("删除buycache1失败!");
		}
		pendingDelegationDao.moveFromCache(partition, buycache1);
	}

	// 检查sellcache1是否发生过成交，
	/*
	 * 发生过： 检查持仓表中是否有相关记录，没有就新增一条并修改bq_account表；有就修改持仓表和bq_account表
	 * 
	 * 没发生过： 不动持仓表，和账户表 向NPendingDelegation，penddingLog中新增一条记录，
	 */
	public void moveSellCache1(int currencyId, String partition, String code, NPendingDelegationCache sellcache1, BigDecimal coefficient) throws ServiceException {
//		logger.info("交易服务： --moveSellCache1()");
		
		long uid = sellcache1.getF_user_id();
		
		// 吃单转挂单，手续费有变
		BigDecimal pendingFeeRate = sellcache1.getCommission_rate();
		if (pendingFeeRate.compareTo(BigDecimal.ZERO) != 0) {
			NTransactionFeeRate rate = bqTransactionFeeRateDao.findLevelByEatRate(partition, code, pendingFeeRate);
			if (rate != null && rate.getPending_commission_rate().compareTo(BigDecimal.ZERO) > 0) {
				pendingFeeRate = rate.getPending_commission_rate();
			} else {
				BQMemberLevel userlevel = bqMemberLevelService.findLevelByEatRate(pendingFeeRate);
				if (userlevel != null)
					pendingFeeRate = userlevel.getPending_commission_rate();
			}
		}
		
		if (sellcache1.getPriority() == Contents.PRIORITY_KC) {
			BigDecimal changeFee = sellcache1.getUndeal_count().multiply(sellcache1.getDelegation_price()).multiply(coefficient)
					.multiply(pendingFeeRate.subtract(sellcache1.getCommission_rate())).setScale(8, BigDecimal.ROUND_DOWN);
			if (changeFee.compareTo(BigDecimal.ZERO) != 0) {
				if (bqAccountContractDao.updateAmount(partition, uid, sellcache1.getTransaction_code(), BigDecimal.ZERO, changeFee.negate(), changeFee, BigDecimal.ZERO) <= 0) {
					logger.info("moveSellCache1 - updateAmount failed, change Btc: " + changeFee.negate().doubleValue() + ", chagne lockBtc: " + changeFee.doubleValue());
					throw new ServiceException("系统繁忙, 请重试");
				}
				
				NAccountContract bqContract = bqAccountContractDao.findByUserIDCode(partition, uid, sellcache1.getTransaction_code());
				NContractAccountLog log = new NContractAccountLog("吃单(sellcache)转挂单，调整冻结", currencyId, Contents.TRANS_TYPE_JY, changeFee.negate().signum(), bqContract
						.getF_btc().add(changeFee), changeFee.negate(), bqContract.getF_btc(), bqContract.getF_lockbtc().subtract(changeFee),
						bqContract.getF_lockbtc(), bqAccountContractDao.getUnix_TIMESTAMP(), uid,
						RandomUtil.getTrans_no(), sellcache1.getDelegation_id(), sellcache1.getTransaction_code());
				contractAccountLogDao.insertLog(partition, log);
			}
		}
		
		if (sellcache1.getPriority() != Contents.PRIORITY_BC) {
			sellcache1.setCommission_rate(pendingFeeRate);
		}
		
		if(pendingDelegationCacheDao.deletePending(partition, code, sellcache1.getDelegation_id())<=0){
			throw new ServiceException("删除sellcache1失败! ");
		}
		
		pendingDelegationDao.moveFromCache(partition, sellcache1);
	}

	@Override
	public void movePendingLog(String partition, NPendingDelegationCache pd) {
		NPendingLog pendingLog = new NPendingLog(pd.getDelegation_id(), pd.getDelegation_time(), pd.getTransaction_id(), pd.getTransaction_code(),
				Contents.CZLX_DDPC, pd.getDelegation_count(), pd.getDelegation_price(), pd.getMultiple(), pd.getDeal_count(), pd.getUndeal_count(), 
				pd.getDeposit(), Contents.PENDING_LOG_ACTION_CD, pd.getF_user_id(), pd.getPending_no(), Contents.DEVICE_SERVER_ERROR, "");
		if (pd.getDirection() == Contents.DIRECTION_BUY) {
			if (pd.getPriority() == Contents.PRIORITY_KC) {
				pendingLog.setTrade_type_id(Contents.CZLX_DDKC);
			} else if (pd.getPriority() == Contents.PRIORITY_PC) {
				pendingLog.setTrade_type_id(Contents.CZLX_DDPC);
			} else {
				pendingLog.setTrade_type_id(Contents.CZLX_DDBC);
			}
		} else {
			if (pd.getPriority() == Contents.PRIORITY_KC) {
				pendingLog.setTrade_type_id(Contents.CZLX_KTKC);
			} else if (pd.getPriority() == Contents.PRIORITY_PC) {
				pendingLog.setTrade_type_id(Contents.CZLX_KTPC);
			} else {
				pendingLog.setTrade_type_id(Contents.CZLX_KTBC);
			}
		}
		
		pendingLog.setFlag(1);
		pendingLogDao.insertLog(partition, pendingLog);
		pendingDelegationCacheDao.deletePending(partition, pd.getTransaction_code(), pd.getDelegation_id());
	}

	@Override
	public void movePendingLog(String partition, NPendingDelegation pd) {
		NPendingLog pendingLog = new NPendingLog(pd.getDelegation_id(), pd.getDelegation_time(), pd.getTransaction_id(), pd.getTransaction_code(),
				Contents.CZLX_DDPC, pd.getDelegation_count(), pd.getDelegation_price(), pd.getMultiple(), pd.getDeal_count(), pd.getUndeal_count(), 
				pd.getDeposit(), Contents.PENDING_LOG_ACTION_CD, pd.getF_user_id(), pd.getPending_no(), Contents.DEVICE_SERVER_ERROR, "");
		if (pd.getDirection() == Contents.DIRECTION_BUY) {
			if (pd.getPriority() == Contents.PRIORITY_KC) {
				pendingLog.setTrade_type_id(Contents.CZLX_DDKC);
			} else if (pd.getPriority() == Contents.PRIORITY_PC) {
				pendingLog.setTrade_type_id(Contents.CZLX_DDPC);
			} else {
				pendingLog.setTrade_type_id(Contents.CZLX_DDBC);
			}
		} else {
			if (pd.getPriority() == Contents.PRIORITY_KC) {
				pendingLog.setTrade_type_id(Contents.CZLX_KTKC);
			} else if (pd.getPriority() == Contents.PRIORITY_PC) {
				pendingLog.setTrade_type_id(Contents.CZLX_KTPC);
			} else {
				pendingLog.setTrade_type_id(Contents.CZLX_KTBC);
			}
		}
		
		pendingLog.setFlag(1);
		pendingLogDao.insertLog(partition, pendingLog);
		pendingDelegationDao.deletePending(partition, pd.getTransaction_code(), pd.getDelegation_id());
	}

	private void adjustSellCacheFrozen(int currencyId, String partition, NPendingDelegation buy, NPendingDelegationCache sellcache, BigDecimal coefficient) throws ServiceException {
		BigDecimal needAdjustCount = buy.getUndeal_count().compareTo(sellcache.getUndeal_count()) > 0 ? sellcache.getUndeal_count() : buy.getUndeal_count();
		BigDecimal needDeposit = needAdjustCount.multiply(coefficient).multiply(buy.getDelegation_price().subtract(sellcache.getDelegation_price()))
				.divide(new BigDecimal(sellcache.getMultiple()), 8, BigDecimal.ROUND_UP);
		BigDecimal needFee =  needAdjustCount.multiply(coefficient).multiply(buy.getDelegation_price().subtract(sellcache.getDelegation_price()))
				.multiply(sellcache.getCommission_rate());
		BigDecimal needChangeAmount = needDeposit.add(needFee).setScale(8, BigDecimal.ROUND_UP);
		NAccountContract accountContract = bqAccountContractDao.findByUserIDCode(partition, sellcache.getF_user_id(), sellcache.getTransaction_code());
		
		if (accountContract.getF_btc().compareTo(needChangeAmount) < 0) {
			needChangeAmount = accountContract.getF_btc();
			
			BigDecimal oldDeposit = sellcache.getUndeal_count().multiply(sellcache.getDelegation_price()).multiply(coefficient)
					.divide(new BigDecimal(sellcache.getMultiple()), 8, BigDecimal.ROUND_DOWN);
			BigDecimal oldFee = sellcache.getUndeal_count().multiply(sellcache.getDelegation_price()).multiply(coefficient).multiply(sellcache.getCommission_rate())
					.setScale(8, BigDecimal.ROUND_DOWN);
			
			BigDecimal alreadyFrozen = oldDeposit.add(oldFee).setScale(8, BigDecimal.ROUND_DOWN);
			BigDecimal allHave = needChangeAmount.add(alreadyFrozen);
			
			BigDecimal canDealCount = allHave.divide(buy.getDelegation_price().multiply(coefficient).divide(new BigDecimal(sellcache.getMultiple()), 20, BigDecimal.ROUND_HALF_UP)
					.add(buy.getDelegation_price().multiply(coefficient).multiply(sellcache.getCommission_rate())), 8, BigDecimal.ROUND_DOWN);
			
			if (canDealCount.compareTo(needAdjustCount) < 0) {
				int digits = com.btc100.btc_q.util.Utils.getAmountDigtis(sellcache.getTransaction_code());
				needAdjustCount = canDealCount.setScale(digits, BigDecimal.ROUND_DOWN);
			}
			
			BigDecimal needAllDeposit = needAdjustCount.multiply(coefficient).multiply(buy.getDelegation_price())
					.divide(new BigDecimal(sellcache.getMultiple()), 8, BigDecimal.ROUND_UP);
			BigDecimal needAllFee = needAdjustCount.multiply(coefficient).multiply(buy.getDelegation_price())
					.multiply(sellcache.getCommission_rate()); 
			BigDecimal needFrozen = needAllDeposit.add(needAllFee);
			needChangeAmount = needFrozen.subtract(alreadyFrozen);
			if (needChangeAmount.compareTo(BigDecimal.ZERO) < 0) {
				needChangeAmount.setScale(8, BigDecimal.ROUND_DOWN);
			} else {
				needChangeAmount.setScale(8, BigDecimal.ROUND_UP);
			}
			
			if (sellcache.getCommission_rate().compareTo(BigDecimal.ZERO) != 0 && sellcache.getUndeal_count().compareTo(needAdjustCount) > 0) {
				// 撤单logo
				NPendingLog pendingLog = new NPendingLog(sellcache.getDelegation_id(), sellcache.getDelegation_time(), sellcache.getTransaction_id(), sellcache.getTransaction_code(),
						Contents.CZLX_KTKC, sellcache.getDelegation_count(), sellcache.getDelegation_price(), sellcache.getMultiple(), sellcache.getDeal_count().add(needAdjustCount), 
						sellcache.getUndeal_count().subtract(needAdjustCount), needChangeAmount.negate(), Contents.PENDING_LOG_ACTION_CD, sellcache.getF_user_id(), 
						sellcache.getPending_no(), Contents.DEVICE_SERVER_BFC, "");

				pendingLogDao.insertLog(partition, pendingLog);
				
			}
			
			// 撮合交易后会被删掉
			sellcache.setUndeal_count(needAdjustCount);
			sellcache.setDeal_count(sellcache.getDelegation_count().subtract(sellcache.getUndeal_count()));
		}
		
		if (needChangeAmount.compareTo(BigDecimal.ZERO) != 0) {
			int flagbuyAccount = bqAccountContractDao.updateAmount(partition, sellcache.getF_user_id(), sellcache.getTransaction_code(), BigDecimal.ZERO, needChangeAmount.negate(), needChangeAmount, BigDecimal.ZERO);
			if (flagbuyAccount == 0) {
				ServiceException sex = new ServiceException("sell");
				logger.error("调整sellcache冻结资金出错，账户更新冻结失败！userId: " + sellcache.getF_user_id() + ", needChangeAmount: " + needChangeAmount);
				throw sex;
			}
			
			if (sellcache.getCommission_rate().compareTo(BigDecimal.ZERO) != 0) {
				NContractAccountLog accountLog = new NContractAccountLog("调整sellcache冻结资金，可用转冻结", currencyId, Contents.TRANS_TYPE_JY, needChangeAmount.negate().signum(), 
						accountContract.getF_btc(), needChangeAmount.abs(), accountContract.getF_btc().add(needChangeAmount.negate()), accountContract.getF_lockbtc(),
						accountContract.getF_lockbtc().add(needChangeAmount), 0, accountContract.getF_user_id(), RandomUtil.getTransNo(), 0, sellcache.getTransaction_code());
				contractAccountLogDao.insertLog(partition, accountLog);
			}
		}
		
	}
}
