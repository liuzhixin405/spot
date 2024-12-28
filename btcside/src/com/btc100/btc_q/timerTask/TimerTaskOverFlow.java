package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMath;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQMarkPriceSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQOverloadMatchSetting;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionFeeRate;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.entity.PendingLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.BQOverloadMatchSettingService;
import com.btc100.btc_q.service.BQStopLossService;
import com.btc100.btc_q.service.BQTransactionFeeRateService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.OpenPositionServer;
import com.btc100.btc_q.service.OverFlowService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.PendingLogService;
import com.btc100.btc_q.service.PlanPendingDelegationService;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 爆仓
 * 
 * @author Administrator
 * 
 */
public class TimerTaskOverFlow  extends OpenPositionTask {
	
	Logger logger = Logger.getLogger(TimerTaskOverFlow.class);

	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private PendingLogService pendingLogService;
	@Autowired
	private BQTransactionTypeService transTypeService;
	@Autowired
	private BQStopLossService bqStopLossService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private OverFlowService overFlowService;
	@Autowired
	private BQNotradetimeService bqNotradetimeService;
	@Autowired
	private BQOverloadMatchSettingService bqOverloadMatchSettingService;
	@Autowired
	private OpenPositionServer openPositionServer;
	@Autowired
	private BQTransactionFeeRateService bqTransactionFeeRateService;
	@Autowired
	private PlanPendingDelegationService planPendingDelegationService;
	
	private static String[] contractArray = null;
	
	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.contracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		List<BQTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, lstTrads);
		}
		
		for (String contract : contractArray) {
			BQTransaction bqt = null;
			for (BQTransaction bqTransaction : lstTrads) {
				if (bqTransaction.getTransaction_code().equals(contract)) {
					bqt = bqTransaction;
					break;
				}
			}
			
			if (bqt == null) {
				continue;
			}
			
			if (bqt.getSuspend()) {
				continue;
			}
			
			if (bqt.getTransaction_kind() != null && bqt.getTransaction_kind() == 1) {
				if (!bqNotradetimeService.isTradeTime(bqt.getTransaction_id())) {
					continue;
				}
			}
			
			BQTransactionType bqTransactionType = null;
			List<BQTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES);
			if (types == null) {
				bqTransactionType = transTypeService.getTransTypeById(bqt.getTransaction_type());
			} else {
				for (BQTransactionType ty : types) {
					if (ty.getType_id() == bqt.getTransaction_type()) {
						bqTransactionType = ty;
						break;
					}
				}
			}
			
			if (bqTransactionType == null) {
				continue;
			}
			
			BigDecimal price = spyMemcachedClient.get(bqt.getTransaction_type_code());
			if (bqt.getTransaction_kind() == 2) {
				BigDecimal lastPrice = spyMemcachedClient.get(bqt.getTransaction_type_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
				if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0) {
					price = lastPrice;
				}
			} else {
				BQMarkPriceSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING + bqt.getTransaction_code());
				if (setting == null) {
					setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING);
				}
				if (setting != null && setting.isRunning()) {
					price = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + bqt.getTransaction_code());
				}
				
			}
			
			if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			
			adjustSpare(bqt, price);
			doOverFlow(bqTransactionType, bqt, price);
			matchBCDeletation(bqt);
		}
	}
	
	/**
	 * 调整持单备用金-余额爆仓制（余额补贴亏损）
	 * @param bqt
	 */
	private void adjustSpare(BQTransaction bqt, BigDecimal price) {
		List<BQMemberTransaction> list = bqMemberTransactionService.findLossOrRemain(bqt.getTransaction_id(), price, bqt.getTrade_coefficient());
		for (BQMemberTransaction trans : list) {
			boolean lock = bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
			if (!lock) continue;
			
			try {
				trans = bqMemberTransactionService.getMemberTransactionById(trans.getMt_id());
				if (trans == null || trans.getHold_count().add(trans.getLock_count()).compareTo(BigDecimal.ZERO) <= 0) continue;
				
				BigDecimal changeRemain = BigDecimal.ZERO;
				BigDecimal changeFrozenRemain = BigDecimal.ZERO;
				BigDecimal income = price.subtract(trans.getAverage_price()).multiply(trans.getHold_count().add(trans.getLock_count())).multiply(bqt.getTrade_coefficient());
				if (trans.getTrade_type_id() == 2) {	//	持空仓
					income = income.negate();
				}
				
				BigDecimal hungUpFrozenIncome = income.multiply(trans.getF_frozen_count().divide(trans.getHold_count().add(trans.getLock_count()), 8, BigDecimal.ROUND_HALF_DOWN));
				BigDecimal unHungIncome = income.subtract(hungUpFrozenIncome);
				
				hungUpFrozenIncome = hungUpFrozenIncome.add(trans.getF_min_frozenremain());
				if (hungUpFrozenIncome.compareTo(BigDecimal.ZERO) > 0) {
					hungUpFrozenIncome = BigDecimal.ZERO;
					changeFrozenRemain = trans.getF_frozenremain().negate();
				} else {
					changeFrozenRemain = hungUpFrozenIncome.negate().subtract(trans.getF_frozenremain());
				}
				
				if (unHungIncome.compareTo(BigDecimal.ZERO) >= 0) {
					changeRemain = trans.getF_fromremain().negate();
				} else {
					changeRemain = unHungIncome.negate().subtract(trans.getF_fromremain());
				}
	
				if (changeRemain.add(changeFrozenRemain).compareTo(BigDecimal.ZERO) > 0) {
					BQAccountContract account = bqAccountContractService.findBTCByUidCode(trans.getF_user_id(), trans.getTransaction_code());
					if (account == null) {
						logger.info("account == null: " + trans.getF_user_id() + " : " + trans.getTransaction_code());
					}
					if (account.getF_btc().subtract(changeRemain.add(changeFrozenRemain)).compareTo(BigDecimal.ZERO) < 0) {	//	余额不够
						if (account.getF_lockbtc().compareTo(BigDecimal.ZERO) > 0) {	// 有挂单冻结
							// 判断是否达到强平
							BigDecimal qpj = BTCQMath.QPJ(trans.getTrade_type_id(), trans.getAverage_price(), 
									trans.getDeposit().add(trans.getDeposit2()).add(trans.getF_fromremain()).add(account.getF_btc()).add(trans.getF_frozenremain()).add(trans.getF_min_frozenremain()), 
									trans.getTrade_multiple(), trans.getHold_count().add(trans.getLock_count()), bqt.getTrade_coefficient(), trans.getDeposit());
							boolean isQP = false;
							if (trans.getTrade_type_id() == 1) {
								isQP = (price.compareTo(qpj) <= 0);
							} else {
								isQP = (price.compareTo(qpj) >= 0);
							}
							
							if (isQP) {
								try {
									cancelPendingWithCode(bqt.getTransaction_code(), 0, trans.getF_user_id(), bqt.getTrade_coefficient());
								} catch (ServiceException e) {
									e.printStackTrace();
								}
								
								account = bqAccountContractService.findBTCByUidCode(trans.getF_user_id(), trans.getTransaction_code());
							}
						}
					}
					
					changeRemain = changeRemain.compareTo(account.getF_btc()) > 0 ? account.getF_btc() : changeRemain;
					BigDecimal available = account.getF_btc().subtract(changeRemain);
					changeFrozenRemain = changeFrozenRemain.compareTo(available) > 0 ? available : changeFrozenRemain;
				}
				
				BigDecimal thold = new BigDecimal(0.0001);
				if (changeRemain.abs().compareTo(thold) < 0 && changeFrozenRemain.abs().compareTo(thold) < 0) 
					continue;
	
				try {
					bqMemberTransactionService.adjustRemain(trans.getMt_id(), trans.getF_user_id(), trans.getTransaction_code(), changeRemain, changeFrozenRemain);
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (trans != null) {
					bqMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
				}
			}
		}
		
		List<BQMemberTransaction> fundFeeAdjustList = bqMemberTransactionService.findHaveFundingFee(bqt.getTransaction_id());
		for (BQMemberTransaction trans : fundFeeAdjustList) {
			boolean lock = bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
			if (!lock) continue;
			
			try {
				trans = bqMemberTransactionService.getMemberTransactionById(trans.getMt_id());
				if (trans == null || trans.getHold_count().add(trans.getLock_count()).compareTo(BigDecimal.ZERO) <= 0) continue;
				BQAccountContract account = bqAccountContractService.getOverBalance(trans.getF_user_id(), trans.getTransaction_code(), new BigDecimal(0.0001));
				if (account == null || account.getF_btc().compareTo(new BigDecimal(0.0001)) < 0) continue;
				
				BigDecimal changeAmount = trans.getDeposit2().abs().compareTo(account.getF_btc()) > 0 ? account.getF_btc() : trans.getDeposit2().abs();
				bqMemberTransactionService.adjustFundingFee(trans.getMt_id(), trans.getF_user_id(), trans.getTransaction_code(), changeAmount);
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (trans != null) {
					bqMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
				}
			}
		}
	}
	
	private void cancelPendingWithCode(String code, int direction, long uid, BigDecimal coefficient) throws ServiceException {
		List<PendingDelegationCache> plistCache = pendingDelegationCacheService.getAllByUserId(direction, code, uid);
		for (PendingDelegationCache pendingDelegationCache : plistCache) {
			String lock = pendingDelegationCacheService.lock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, coefficient, true);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pendingDelegationCache != null) {
						pendingDelegationCacheService.unLock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id(), lock);
					}
				}
			}
		}
		List<PendingDelegation> plist = pendingDelegationService.getAllByUserId(direction, code, uid);
		for (PendingDelegation pendingDelegation : plist) {
			String lock = pendingDelegationService.lock(code, pendingDelegation.getDelegation_id()); 
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationService.cancelPending_one(pendingDelegation, coefficient, true);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pendingDelegation != null) {
						pendingDelegationService.unLock(code, pendingDelegation.getDelegation_id(), lock);
					}
				}
			}
		}
	}
	
	/**
	 *	处理达到强平价格的持单 
	 * @param bqt
	 */
	private void doOverFlow(BQTransactionType bqTransactionType, BQTransaction bqt, BigDecimal btc_index_price) {
		
//		bqMemberTransactionService.resetFlag(bqt.getTransaction_id(), -1);

		// 改变flag表明持仓单爆仓中，此时用户不可平仓操作，用户的平仓挂单只能减不能增
		bqMemberTransactionService.changeFlagForBC(bqt.getTransaction_id(),  btc_index_price, bqt.getTrade_coefficient());
		// 查找爆仓中的单，上面flag改变的单
		List<BQMemberTransaction> list = bqMemberTransactionService.findBCTransactions(bqt.getTransaction_id());
		if (list == null || list.size() <= 0)
			return;		// 没有需要爆仓

		for (BQMemberTransaction memeberTran : list) {
			
			// 止盈止损处理
			bqStopLossService.deleteByMid(memeberTran.getMt_id(), Contents.STOP_LOSS_LOG_ACTION_QP, "爆仓强平，删除止盈止损单");

			planPendingDelegationService.updateStatusAndNoteByMid(memeberTran.getTransaction_code(), memeberTran.getF_user_id(), memeberTran.getMt_id(), Contents.PLAN_PENDING_STATUS_FAIL, "爆仓强平，止盈止损失效");
			
			memeberTran = bqMemberTransactionService.getMemberTransactionById(memeberTran.getMt_id());
			if (memeberTran == null || memeberTran.getHold_count().add(memeberTran.getLock_count()).compareTo(BigDecimal.ZERO) <= 0) continue;

			String scode = memeberTran.getTransaction_code();
			int diection = memeberTran.getTrade_type_id();
			Long uid = memeberTran.getF_user_id();
			int needPendDirection = 0;	// 交易方向
			if (diection == Contents.DIRECTION_BUY) {
				needPendDirection = Contents.DIRECTION_SELL;
			} else {
				needPendDirection = Contents.DIRECTION_BUY;
			}

			// 撤单-获取该持仓的平仓挂单，在爆仓操作期间该持仓的挂单只减不能增，先获取挂单，如果该挂单正在操作，则等待（共100ms），如果没有操作则锁住（置flag）
			List<PendingDelegationCache> pdcList = pendingDelegationCacheService.queryPCDelegations(needPendDirection, scode, uid, memeberTran.getTrade_multiple().intValue());
			for (PendingDelegationCache pd : pdcList) {
				if (pd.getPriority() == Contents.PRIORITY_PC) {
					String changeflag = pendingDelegationCacheService.lock(scode, pd.getDelegation_id());
					int sleepCount = 0;
					boolean has = true;
					while (StringUtil.isEmpty(changeflag) && sleepCount < 100) {
						try {
							sleepCount += 10;
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						PendingDelegationCache pending = pendingDelegationCacheService.findPendingById(pd.getDelegation_id(), scode);
						if (pending == null) {	// 挂单已被撤销
							has = false;
							break;
						}
						changeflag = pendingDelegationCacheService.lock(scode, pd.getDelegation_id());
					}
					
					if (!has) continue;
					
					try {
						pendingDelegationCacheService.cancelPendingCache_one(pd, bqt.getTrade_coefficient(), true);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (!StringUtil.isEmpty(changeflag)) {
							if (pd != null) {
								pendingDelegationCacheService.unLock(scode, pd.getDelegation_id(), changeflag);
							}
						}
					}
				}
			}
			
			List<PendingDelegation> pdList = pendingDelegationService.queryPCDelegations(needPendDirection, scode, uid, memeberTran.getTrade_multiple().intValue());
			for (PendingDelegation pd : pdList) {
				if (pd.getPriority() == Contents.PRIORITY_PC) {
					
					String changeflag = pendingDelegationService.lock(scode, pd.getDelegation_id());
					int sleepCount = 0;
					boolean has = true;
					while (StringUtil.isEmpty(changeflag) && sleepCount < 100) {
						try {
							sleepCount += 10;
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						PendingDelegation pending = pendingDelegationService.findPendingById(pd.getDelegation_id(), scode);
						if (pending == null) {	// 挂单已被撤销
							break;
						}
						changeflag = pendingDelegationService.lock(scode, pd.getDelegation_id());
					}
					
					if (!has) continue;
					
					
					try {
						pendingDelegationService.cancelPending_one(pd, bqt.getTrade_coefficient(), true);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (!StringUtil.isEmpty(changeflag)) {
							if (pd != null) {
								pendingDelegationService.unLock(scode, pd.getDelegation_id(), changeflag);
							}
						}
					}
				}
			}

			BQMemberTransaction newMemberTran = bqMemberTransactionService.getMemberTransactionById(memeberTran.getMt_id());
			if (newMemberTran == null || newMemberTran.getHold_count().compareTo(BigDecimal.ZERO) <= 0) continue;
			
			if (newMemberTran.getLock_count().compareTo(BigDecimal.ZERO) != 0) {
				logger.error("爆仓撤单错误:用户" + uid + "所有平仓撤单之后依然冻结：" + newMemberTran.getLock_count());
			}
			
			// 以爆仓价挂单
			int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
			BQMember user = bqMemberService.findUserById(newMemberTran.getF_user_id());
			if (user == null) continue;
			BQMemberLevel userLevel = getLevelByUserBfx(user);
			
			try {
				overFlowService.overFlowDelegate(diection, btc_index_price, digits, newMemberTran, userLevel, bqt.getTrade_coefficient());
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		
		if (list.size() > 0) {
			new EmailNotify(true, list).start();
		}
	}
	
	private boolean overloadMatch(BQTransaction bqTransaction) {
		
		BQOverloadMatchSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.OVER_MATCH_SETTING + bqTransaction.getTransaction_code());
		if (setting == null) {
			setting = bqOverloadMatchSettingService.getSettingByCode(bqTransaction.getTransaction_code());
			if (setting == null) {
				setting = new BQOverloadMatchSetting();
				setting.setRunning(false);
				setting.setF_user_id(0);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.OVER_MATCH_SETTING + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
		}
		
		if (setting == null || !setting.getRunning() || setting.getF_user_id() <= 1) return false;
		
		long matchUid = setting.getF_user_id();

		List<PendingDelegation> buyList = pendingDelegationService.findBCPendingOverOneMinute(bqTransaction.getTransaction_code(), Contents.DIRECTION_BUY);
		for (PendingDelegation buy : buyList) {
			// 相当于平多仓
			BigDecimal amount = buy.getUndeal_count();
			BigDecimal price = buy.getDelegation_price();
			BigDecimal deposit = BTCQMath.getDeposit(amount, price, buy.getMultiple(), bqTransaction.getTrade_coefficient());
			BigDecimal fee = BigDecimal.ZERO;
			PendingDelegationCache cache = new PendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					Contents.DIRECTION_SELL, amount, price, BigDecimal.ZERO, amount, buy.getMultiple(), deposit,
					BigDecimal.ZERO, Contents.PRIORITY_KC, matchUid, BigDecimal.ZERO, BigDecimal.ZERO);
			cache.setDelegation_time(bqMemberTransactionService.getUnix_TIMESTAMP());
			
			BigDecimal changeAmount = deposit.add(fee);
			BigDecimal changeRemainNum = changeAmount.negate();
			if (bqAccountContractService.UpdateAmount(matchUid, cache.getTransaction_code(), BigDecimal.ZERO, changeRemainNum, changeAmount, BigDecimal.ZERO) <= 0) {
				return false;
			}
			
			int type = Contents.CZLX_KTKC;
			if (cache.getDirection() == Contents.DIRECTION_BUY) {
				type = Contents.CZLX_DDKC;			
			}
			
			PendingLog	pendingLog = new PendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(), cache.getTransaction_code(),
					type, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), BigDecimal.ZERO,
					cache.getDelegation_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), Contents.DEVICE_SERVER_QC, "");
			pendingLogService.savePendingLog(pendingLog);

			if (!dealBuyAndSellCache(bqTransaction.getTransaction_code(), buy, cache, bqTransaction.getTrade_coefficient(), true)) {
				cache.setDeal_count(BigDecimal.ZERO);
				cache.setUndeal_count(cache.getDelegation_count());
			} else {
				spyMemcachedClient.set(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, buy.getDelegation_price());
			}
			
			if (cache.getUndeal_count().signum() > 0) {	// 没处理掉，撤单
				pendingLogService.updateActionByPendingNo(cache.getTransaction_code(), cache.getPending_no(), cache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
				
				BigDecimal dp = cache.getUndeal_count().multiply(cache.getDelegation_price()).multiply(bqTransaction.getTrade_coefficient())
						.divide(new BigDecimal(cache.getMultiple()), 8, BigDecimal.ROUND_DOWN);
				// 恢复冻结资金
				BigDecimal change = dp.add(fee);
				if (bqAccountContractService.UpdateAmount(cache.getF_user_id(), cache.getTransaction_code(), BigDecimal.ZERO, change, change.negate(), BigDecimal.ZERO) <= 0) {
					openPositionServer.movePendingLog(cache);
				}
			}
		}
		
		List<PendingDelegation> sellList = pendingDelegationService.findBCPendingOverOneMinute(bqTransaction.getTransaction_code(), Contents.DIRECTION_SELL);
		for (PendingDelegation sell : sellList) {
			// 相当于平多仓
			BigDecimal amount = sell.getUndeal_count();
			BigDecimal price = sell.getDelegation_price();
			BigDecimal deposit = BTCQMath.getDeposit(amount, price, sell.getMultiple(), bqTransaction.getTrade_coefficient());
			BigDecimal fee = BigDecimal.ZERO;
			PendingDelegationCache cache = new PendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					Contents.DIRECTION_BUY, amount, price, BigDecimal.ZERO, amount, sell.getMultiple(), deposit,
					BigDecimal.ZERO, Contents.PRIORITY_KC, matchUid, BigDecimal.ZERO, BigDecimal.ZERO);
			cache.setDelegation_time(bqMemberTransactionService.getUnix_TIMESTAMP());
			
			BigDecimal changeAmount = deposit.add(fee);
			BigDecimal changeRemainNum = changeAmount.negate();
			if (bqAccountContractService.UpdateAmount(matchUid, cache.getTransaction_code(), BigDecimal.ZERO, changeRemainNum, changeAmount, BigDecimal.ZERO) <= 0) {
				return false;
			}
			
			int type = Contents.CZLX_KTKC;
			if (cache.getDirection() == Contents.DIRECTION_BUY) {
				type = Contents.CZLX_DDKC;
			}
			
			PendingLog	pendingLog = new PendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(), cache.getTransaction_code(),
					type, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), BigDecimal.ZERO,
					cache.getDelegation_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), Contents.DEVICE_SERVER_QC, "");
			pendingLogService.savePendingLog(pendingLog);

			if (!dealBuyCacheAndSell(bqTransaction.getTransaction_code(), cache, sell, bqTransaction.getTrade_coefficient(), true)) {
				cache.setDeal_count(BigDecimal.ZERO);
				cache.setUndeal_count(cache.getDelegation_count());
			} else {
				spyMemcachedClient.set(bqTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, sell.getDelegation_price());
			}
			
			if (cache.getUndeal_count().signum() > 0) {	// 没处理掉，撤单
				pendingLogService.updateActionByPendingNo(cache.getTransaction_code(), cache.getPending_no(), cache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
				
				BigDecimal dp = cache.getUndeal_count().multiply(cache.getDelegation_price()).multiply(bqTransaction.getTrade_coefficient())
						.divide(new BigDecimal(cache.getMultiple()), 8, BigDecimal.ROUND_DOWN);
				// 恢复冻结资金
				BigDecimal change = dp.add(fee);
				if (bqAccountContractService.UpdateAmount(cache.getF_user_id(), cache.getTransaction_code(), BigDecimal.ZERO, change, change.negate(), BigDecimal.ZERO) <= 0) {
					openPositionServer.movePendingLog(cache);
				}
			}
		}
			
		return true;
	}

	/**
	 * 撮合委托超过一分钟的爆仓单
	 * @param bqt
	 */
	private void matchBCDeletation(BQTransaction bqt) {
		
		if (overloadMatch(bqt)) return;
		
		// 获取超过一分钟的爆仓委托
		String code = bqt.getTransaction_code();
		List<PendingDelegation> buyList = pendingDelegationService.findBCPendingOverOneMinute(code, Contents.DIRECTION_BUY);
		List<BQMemberTransaction> toNotifyTrans = new ArrayList<BQMemberTransaction>();
		for (PendingDelegation buy : buyList) {
			// 找盈利比最高的持多单，一个个找
			List<BQMemberTransaction> tradeBuylist = bqMemberTransactionService.findMaxProfitRateTransactions(bqt.getTransaction_id(), Contents.TRADE_TYPE_BUY, buy.getDelegation_price());
			for (BQMemberTransaction memberTran : tradeBuylist) {
				if (memberTran.getHold_count().compareTo(buy.getUndeal_count()) < 0 && memberTran.getLock_count().compareTo(BigDecimal.ZERO) > 0) { 
					if (!bqMemberTransactionService.lockToAdjustSpare(memberTran.getMt_id())) {
						break;
					}
				
					try {
						// 撮合cache委托
						matchSellCache(buy, memberTran.getMt_id(), bqt.getTrade_coefficient());
						buy = pendingDelegationService.findPendingById(buy.getDelegation_id(), code);
						if (buy == null || buy.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
						
						// 撮合委托
						matchSell(buy, memberTran.getMt_id(), bqt.getTrade_coefficient());
						buy = pendingDelegationService.findPendingById(buy.getDelegation_id(), code);
						if (buy == null || buy.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (memberTran != null) {
							bqMemberTransactionService.unlockToAdjustSpare(memberTran.getMt_id());
						}
					}
				}
				
				if (!bqMemberTransactionService.lockToTrade(memberTran.getMt_id())) {
					break;
				}
				
				try {
					// 撮合持单
					BQMemberTransaction userTran = bqMemberTransactionService.getMemberTransactionById(memberTran.getMt_id());
					if (userTran == null || userTran.getHold_count().signum() <= 0) continue;
					
					BigDecimal undealCount = buy.getUndeal_count();
					
					BQMember user = bqMemberService.findUserById(userTran.getF_user_id());
					if (user == null) continue;
					BQMemberLevel userLevel = getLevelByUserBfx(user);
					BigDecimal feeRate = userLevel.getEat_commission_rate();
					BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.getByCodeAndLevel(userTran.getTransaction_code(), userLevel.getLevel_id());
					if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
						feeRate = transactionFeeRate.getEat_commission_rate();
					}
					
					// 相当于平多仓
					BigDecimal delegationCount = buy.getUndeal_count().compareTo(userTran.getHold_count()) > 0 ? userTran.getHold_count() : buy.getUndeal_count();
					BigDecimal deposit = delegationCount.divide(userTran.getHold_count().add(userTran.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
							.multiply(userTran.getDeposit());
					PendingDelegationCache sellcache = new PendingDelegationCache(userTran.getTransaction_id(), userTran.getTransaction_code(),Contents.DIRECTION_SELL, 
							delegationCount, buy.getDelegation_price(), new BigDecimal(0), delegationCount, userTran.getTrade_multiple().intValue(), 
							deposit, feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);
					sellcache.setDelegation_time(bqMemberTransactionService.getUnix_TIMESTAMP());
					sellcache.setFlag(1);
					
					int flag = bqMemberTransactionService.updateMemberTransByID(userTran.getMt_id(), delegationCount.negate(), delegationCount, 
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
					if (flag > 0) {
						PendingLog pendingLog = new PendingLog(sellcache.getDelegation_id(), sellcache.getDelegation_time(), sellcache.getTransaction_id(),
								sellcache.getTransaction_code(), Contents.CZLX_DDPC, sellcache.getDelegation_count(), sellcache.getDelegation_price(), sellcache.getMultiple(), 
								sellcache.getDeal_count(), sellcache.getUndeal_count(), sellcache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, sellcache.getF_user_id(), 
								sellcache.getPending_no(), Contents.DEVICE_SERVER_QC, "");
						pendingLogService.savePendingLog(pendingLog);
						
						boolean needBreak = false;
						if (!dealBuyAndSellCache(code, buy, sellcache, bqt.getTrade_coefficient(), true)) {
							sellcache.setDeal_count(BigDecimal.ZERO);
							sellcache.setUndeal_count(delegationCount);
							needBreak = true;
						} else {
							spyMemcachedClient.set(code + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, buy.getDelegation_price());
						}
						
						if (sellcache.getUndeal_count().signum() > 0) {	// 没处理掉，撤单
							pendingLogService.updateActionByPendingNo(sellcache.getTransaction_code(), sellcache.getPending_no(), sellcache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
							
							// 需要恢复的持仓
							if (bqMemberTransactionService.updateMemberTransByID(userTran.getMt_id(), sellcache.getUndeal_count(), sellcache.getUndeal_count().negate(), BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
								logger.error("爆仓撤单错误：用户" + userTran.getF_user_id() + "的挂单量：" + sellcache.getUndeal_count() + "，持单冻结量：" + userTran.getLock_count());
							}
						}
						
						BigDecimal price = buy.getDelegation_price();
						buy = pendingDelegationService.findPendingById(buy.getDelegation_id(), code);
						BQMemberTransaction tr = new BQMemberTransaction();
						tr.setTrade_type_id(userTran.getTrade_type_id());
						tr.setTransaction_code(userTran.getTransaction_code());
						tr.setTransaction_id(userTran.getTransaction_id());
						tr.setF_user_id(userTran.getF_user_id());
						if (buy == null || buy.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							BigDecimal deal = undealCount;
							if (deal.compareTo(BigDecimal.ZERO) > 0) {
								tr.setHold_count(deal);
								tr.setAverage_price(price);
								toNotifyTrans.add(tr);
							}
							
							break;
						} else {
							BigDecimal deal = undealCount.subtract(buy.getUndeal_count());
							if (deal.compareTo(BigDecimal.ZERO) > 0) {
								tr.setHold_count(deal);
								tr.setAverage_price(price);
								toNotifyTrans.add(tr);
							}
						}
						
						if (needBreak) {
							logger.info("needBreak !!!");
							break;
						}
						
					} else {
						logger.info("updateMemberTransByID failed !!!");
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (memberTran != null) {
						bqMemberTransactionService.unlockToTrade(memberTran.getMt_id());
					}
				}
			}
		}
		
		List<PendingDelegation> sellList = pendingDelegationService.findBCPendingOverOneMinute(code, Contents.DIRECTION_SELL);
		// 找盈利比最高的持空单，一个个找
		for (PendingDelegation sell : sellList) {
			// 找盈利比最高的持空头，一个个找
			List<BQMemberTransaction> tradeSelllist = bqMemberTransactionService.findMaxProfitRateTransactions(bqt.getTransaction_id(), Contents.TRADE_TYPE_SELL, sell.getDelegation_price());
			for (BQMemberTransaction memberTran : tradeSelllist) {
				if (memberTran.getHold_count().compareTo(sell.getUndeal_count()) < 0 && memberTran.getLock_count().compareTo(BigDecimal.ZERO) > 0) { 
					if (!bqMemberTransactionService.lockToAdjustSpare(memberTran.getMt_id())) {
						break;
					}
				
					try {
						// 撮合cache委托
						matchBuyCache(sell, memberTran.getMt_id(), bqt.getTrade_coefficient());
						sell = pendingDelegationService.findPendingById(sell.getDelegation_id(), code);
						if (sell == null || sell.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
						
						// 撮合委托
						matchBuy(sell, memberTran.getMt_id(), bqt.getTrade_coefficient());
						sell = pendingDelegationService.findPendingById(sell.getDelegation_id(), code);
						if (sell == null || sell.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (memberTran != null) {
							bqMemberTransactionService.unlockToAdjustSpare(memberTran.getMt_id());
						}
					}
				}
				
				if (!bqMemberTransactionService.lockToTrade(memberTran.getMt_id())) {
					break;
				}
				
				try {
					// 撮合持单
					BQMemberTransaction userTran = bqMemberTransactionService.getMemberTransactionById(memberTran.getMt_id());
					if (userTran == null || userTran.getHold_count().signum() <= 0) continue;
					
					BigDecimal undealCount = sell.getUndeal_count();
					
					BQMember user = bqMemberService.findUserById(userTran.getF_user_id());
					if (user == null) continue;
					BQMemberLevel userLevel = bqMemberLevelService.findById(user.getF_user_level_id());
					BigDecimal feeRate = userLevel.getEat_commission_rate();
					BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.getByCodeAndLevel(userTran.getTransaction_code(), userLevel.getLevel_id());
					if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
						feeRate = transactionFeeRate.getEat_commission_rate();
					}
					
					// 相当于平空仓
					BigDecimal delegationCount = sell.getUndeal_count().compareTo(userTran.getHold_count()) > 0 ? userTran.getHold_count() : sell.getUndeal_count();
					BigDecimal deposit = delegationCount.divide(userTran.getHold_count().add(userTran.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
							.multiply(userTran.getDeposit());
					PendingDelegationCache buycache = new PendingDelegationCache(userTran.getTransaction_id(), userTran.getTransaction_code(),Contents.DIRECTION_BUY, 
							delegationCount, sell.getDelegation_price(), new BigDecimal(0), delegationCount, userTran.getTrade_multiple().intValue(), 
							deposit, feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);
					buycache.setDelegation_time(bqMemberTransactionService.getUnix_TIMESTAMP());
					buycache.setFlag(1);
					
					int flag = bqMemberTransactionService.updateMemberTransByID(userTran.getMt_id(), delegationCount.negate(), delegationCount, 
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
					if (flag > 0) {
						PendingLog pendingLog = new PendingLog(buycache.getDelegation_id(), buycache.getDelegation_time(), buycache.getTransaction_id(),
								buycache.getTransaction_code(), Contents.CZLX_KTPC, buycache.getDelegation_count(), buycache.getDelegation_price(), buycache.getMultiple(), 
								buycache.getDeal_count(), buycache.getUndeal_count(), buycache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, buycache.getF_user_id(), 
								buycache.getPending_no(), Contents.DEVICE_SERVER_QC, "");
						pendingLogService.savePendingLog(pendingLog);
						
						boolean needBreak = false;
						if (!dealBuyCacheAndSell(code, buycache, sell, bqt.getTrade_coefficient(), true)) {
							buycache.setDeal_count(BigDecimal.ZERO);
							buycache.setUndeal_count(delegationCount);
							needBreak = true;
						} else {
							spyMemcachedClient.set(code + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, sell.getDelegation_price());
						}
						
						if (buycache.getUndeal_count().signum() > 0) {	// 没处理掉，撤单
							pendingLogService.updateActionByPendingNo(buycache.getTransaction_code(), buycache.getPending_no(), buycache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
							
							// 需要恢复的持仓
							if (bqMemberTransactionService.updateMemberTransByID(userTran.getMt_id(), buycache.getUndeal_count(), buycache.getUndeal_count().negate(), BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
								logger.error("爆仓撤单错误：用户" + userTran.getF_user_id() + "的挂单量：" + buycache.getUndeal_count() + "，持单冻结量：" + userTran.getLock_count());
							}
						}
						
						BQMemberTransaction tr = new BQMemberTransaction();
						tr.setTrade_type_id(userTran.getTrade_type_id());
						tr.setTransaction_code(userTran.getTransaction_code());
						tr.setTransaction_id(userTran.getTransaction_id());
						tr.setF_user_id(userTran.getF_user_id());
						BigDecimal price = sell.getDelegation_price();
						sell = pendingDelegationService.findPendingById(sell.getDelegation_id(), code);
						if (sell == null || sell.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							BigDecimal deal = undealCount;
							if (deal.compareTo(BigDecimal.ZERO) > 0) {
								tr.setHold_count(deal);
								tr.setAverage_price(price);
								toNotifyTrans.add(tr);
							}
							break;
						} else {
							BigDecimal deal = undealCount.subtract(sell.getUndeal_count());
							if (deal.compareTo(BigDecimal.ZERO) > 0) {
								tr.setHold_count(deal);
								tr.setAverage_price(price);
								toNotifyTrans.add(tr);
							}
						}
						
						if (needBreak) {
							logger.info("needBreak !!!");
							break;
						}
						
					} else {
						logger.info("updateMemberTransByID failed !!!");
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (memberTran != null) {
						bqMemberTransactionService.unlockToTrade(memberTran.getMt_id());
					}
				}
			}
		}
		
		if (toNotifyTrans.size() > 0) {
			new EmailNotify(false, toNotifyTrans).start();
		}
	}
	

	private PendingDelegation matchSell(PendingDelegation buy, long memberTranId, BigDecimal coefficient) {
		BQMemberTransaction memberTran = bqMemberTransactionService.getMemberTransactionById(memberTranId);
		if (memberTran == null || memberTran.getLock_count().signum() <= 0) return buy;

		BigDecimal needCancelCount = buy.getUndeal_count().subtract(memberTran.getHold_count());
		if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
			return buy;
		}
		
		String code = memberTran.getTransaction_code();
		List<PendingDelegation> sells = pendingDelegationService.findPendingByMemberTransInfo(code, 
				memberTran.getF_user_id(), Contents.PRIORITY_PC, Contents.DIRECTION_SELL, memberTran.getTrade_multiple().intValue());
		for (PendingDelegation sell : sells) {
			String changeflag = pendingDelegationService.lock(sell.getTransaction_code(), sell.getDelegation_id());
			if (!StringUtil.isEmpty(changeflag)) {
				try {
					PendingDelegation pending = pendingDelegationService.findPendingById(sell.getDelegation_id(), code);
					pendingDelegationService.cancelPending_one(pending, coefficient, true);
					needCancelCount = needCancelCount.subtract(pending.getUndeal_count());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (sell != null) {
						pendingDelegationService.unLock(sell.getTransaction_code(), sell.getDelegation_id(), changeflag);
					}
				}
			}
			
			if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		
		return buy;
	}
	
	private PendingDelegation matchSellCache(PendingDelegation buy, long memberTranId, BigDecimal coefficient) {
		BQMemberTransaction memberTran = bqMemberTransactionService.getMemberTransactionById(memberTranId);
		if (memberTran == null || memberTran.getLock_count().signum() <= 0) return buy;
		
		BigDecimal needCancelCount = buy.getUndeal_count().subtract(memberTran.getHold_count());
		if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
			return buy;
		}
		
		String code = memberTran.getTransaction_code();
		List<PendingDelegationCache> sellcaches = pendingDelegationCacheService.findPendingByMemberTransInfo(code, 
				memberTran.getF_user_id(), Contents.PRIORITY_PC, Contents.DIRECTION_SELL, memberTran.getTrade_multiple().intValue());
		for (PendingDelegationCache sellcache : sellcaches) {
			String changeflag = pendingDelegationCacheService.lock(sellcache.getTransaction_code(), sellcache.getDelegation_id());
			if (!StringUtil.isEmpty(changeflag)) {
				try {
					PendingDelegationCache pending = pendingDelegationCacheService.findPendingById(sellcache.getDelegation_id(), code);
					pendingDelegationCacheService.cancelPendingCache_one(pending, coefficient, true);
					needCancelCount = needCancelCount.subtract(pending.getUndeal_count());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (sellcache != null) {
						pendingDelegationCacheService.unLock(sellcache.getTransaction_code(), sellcache.getDelegation_id(), changeflag);
					}
				}
			}
			
			if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		
		return buy;
	}
	
	private PendingDelegation matchBuy(PendingDelegation sell, long memberTranId, BigDecimal coefficient) {
		BQMemberTransaction memberTran = bqMemberTransactionService.getMemberTransactionById(memberTranId);
		if (memberTran == null || memberTran.getLock_count().signum() <= 0) return sell;
		
		BigDecimal needCancelCount = sell.getUndeal_count().subtract(memberTran.getHold_count());
		if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
			return sell;
		}

		String code = memberTran.getTransaction_code();
		List<PendingDelegation> buys = pendingDelegationService.findPendingByMemberTransInfo(code, 
				memberTran.getF_user_id(), Contents.PRIORITY_PC, Contents.DIRECTION_BUY, memberTran.getTrade_multiple().intValue());
		for (PendingDelegation buy : buys) {
			String changeflag = pendingDelegationService.lock(buy.getTransaction_code(), buy.getDelegation_id());
			if (!StringUtil.isEmpty(changeflag)) {
				try {
					PendingDelegation pending = pendingDelegationService.findPendingById(buy.getDelegation_id(), code);
					pendingDelegationService.cancelPending_one(pending, coefficient, true);
					needCancelCount = needCancelCount.subtract(pending.getUndeal_count());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (buy != null) {
						pendingDelegationService.unLock(buy.getTransaction_code(), buy.getDelegation_id(), changeflag);
					}
				}
			}
			
			if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		
		return sell;
	}
	
	private PendingDelegation matchBuyCache(PendingDelegation sell, long memberTranId, BigDecimal coefficient) {
		BQMemberTransaction memberTran = bqMemberTransactionService.getMemberTransactionById(memberTranId);
		if (memberTran == null || memberTran.getLock_count().signum() <= 0) return sell;
		
		BigDecimal needCancelCount = sell.getUndeal_count().subtract(memberTran.getHold_count());
		if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
			return sell;
		}
		
		String code = memberTran.getTransaction_code();
		List<PendingDelegationCache> buycaches = pendingDelegationCacheService.findPendingByMemberTransInfo(code, 
				memberTran.getF_user_id(), Contents.PRIORITY_PC, Contents.DIRECTION_BUY, memberTran.getTrade_multiple().intValue());
		for (PendingDelegationCache buycache : buycaches) {
			String changeflag = pendingDelegationCacheService.lock(buycache.getTransaction_code(), buycache.getDelegation_id());
			if (!StringUtil.isEmpty(changeflag)) {
				try {
					PendingDelegationCache pending = pendingDelegationCacheService.findPendingById(buycache.getDelegation_id(), code);
					pendingDelegationCacheService.cancelPendingCache_one(pending, coefficient, true);
					needCancelCount = needCancelCount.subtract(pending.getUndeal_count());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (buycache != null) {
						pendingDelegationCacheService.unLock(buycache.getTransaction_code(), buycache.getDelegation_id(), changeflag);
					}
				}
			}
			
			if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		
		return sell;
	}
	
	
	class EmailNotify extends Thread {
		boolean isOverFlow = true;
		List<BQMemberTransaction> list = null;

		public EmailNotify(boolean overflow, List<BQMemberTransaction> list) {
			this.isOverFlow = overflow;
			this.list = list;
		}
		
	    public void run() {
	    	String basePath = UrlConfig.getValue("webRoot");
	        for (BQMemberTransaction trans : list) {
	        	 String isSent = spyMemcachedClient.get("OverEmailNotify:" + trans.getF_user_id());
	        	 if (!StringUtil.isEmpty(isSent)) {
	        		 continue;
	        	 }
	        	 spyMemcachedClient.set("OverEmailNotify:" + trans.getF_user_id(), BTCQMemecacheKeys.TIMES.FIVE_MIN , "sent");
	        	 
	        	String email = "";
	        	BQMember member = bqMemberService.findUserById(trans.getF_user_id());
	        	if (member == null) {
	        		continue;
	        	}
	        	if (member.getIs_sub_user() == 1) continue;
	        	
	        	email = member.getF_real_email();
	        	if (StringUtil.isEmpty(email)) continue;
	        	
	        	String transType = "多仓";
	        	if (trans.getTrade_type_id() == Contents.TRADE_TYPE_SELL) {
	        		transType = "空仓";
	        	}
	        	
	        	StringBuffer sb = new StringBuffer("尊敬的XKD用户，您好！<br>");
	            if (isOverFlow) {
	            	sb.append("&nbsp;&nbsp;&nbsp;&nbsp;您的" + trans.getTransaction_code() + "合约" + transType + "已因标记价格达到您的强平触发价而造成穿仓。请您知晓！<br><br>");
	            } else {
	            	sb.append("&nbsp;&nbsp;&nbsp;&nbsp;您的" + trans.getTransaction_code() + "合约" + transType + "盈利比例最高，以" + trans.getAverage_price().doubleValue() + "价格与穿仓单撮合成交");
	            	sb.append(trans.getHold_count().doubleValue() + "个" + trans.getTransaction_code() + "合约，请您合理安排自己合约的仓位。<br><br>");
	            }
	            
	            sb.append("感谢选择xkd<br>XKD官方<br><br>");
				sb.append("联系我们：" + basePath + "order/orderList.do");
				sb.append("");
				
				SendMail themail = new SendMail();
				try {
					themail.send(email, sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	    }
	} 
}
