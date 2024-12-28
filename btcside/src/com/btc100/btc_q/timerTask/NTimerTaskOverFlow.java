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
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NMarkPriceSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NOverloadMatchSetting;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionFeeRate;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.NPendingDelegation;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.entity.NPendingLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.NAccountContractService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.NMemberTransactionService;
import com.btc100.btc_q.service.NNotradetimeService;
import com.btc100.btc_q.service.NOverloadMatchSettingService;
import com.btc100.btc_q.service.NPlanPendingDelegationService;
import com.btc100.btc_q.service.NStopLossService;
import com.btc100.btc_q.service.NTransactionFeeRateService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.service.NOpenPositionServer;
import com.btc100.btc_q.service.NOverFlowService;
import com.btc100.btc_q.service.NPendingDelegationCacheService;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.service.NPendingLogService;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 爆仓
 * 
 * @author Administrator
 * 
 */
public class NTimerTaskOverFlow  extends NOpenPositionTask {
	
	Logger logger = Logger.getLogger(NTimerTaskOverFlow.class);

	@Autowired
	private NPendingDelegationService pendingDelegationService;
	@Autowired
	private NPendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private NPendingLogService pendingLogService;
	@Autowired
	private NTransactionTypeService transTypeService;
	@Autowired
	private NStopLossService bqStopLossService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private NMemberTransactionService bqMemberTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NTransactionService bqTransactionService;
	@Autowired
	private NAccountContractService bqAccountContractService;
	@Autowired
	private NOverFlowService overFlowService;
	@Autowired
	private NNotradetimeService bqNotradetimeService;
	@Autowired
	private NOverloadMatchSettingService bqOverloadMatchSettingService;
	@Autowired
	private NOpenPositionServer openPositionServer;
	@Autowired
	private NTransactionFeeRateService bqTransactionFeeRateService;
	@Autowired
	private NPlanPendingDelegationService planPendingDelegationService;
	
	private static String[] contractArray = null;
	
	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.ncontracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		for (String pContract : contractArray) {
			String[] strs = pContract.split("_");
			if (strs.length != 2) continue;
			String partition = strs[0];
			String contract = strs[1];
			
			if (StringUtil.isEmpty(partition) || StringUtil.isEmpty(contract)) {
				continue;
			}
			
			NTransaction bqt = bqTransactionService.findTransByCode(partition, contract);
			if (bqt == null) continue;
			
			if (bqt.getSuspend()) {
				continue;
			}
			
			if (bqt.getTransaction_kind() != null && bqt.getTransaction_kind() == 1) {
				if (!bqNotradetimeService.isTradeTime(partition, bqt.getTransaction_id())) {
					continue;
				}
			}
			
			NTransactionType bqTransactionType = null;
			List<NTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition);
			if (types == null) {
				bqTransactionType = transTypeService.getTransTypeById(partition, bqt.getTransaction_type());
			} else {
				for (NTransactionType ty : types) {
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
				BigDecimal lastPrice = spyMemcachedClient.get(partition + "_" + bqt.getTransaction_type_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
				if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0) {
					price = lastPrice;
				}
			} else {
				NMarkPriceSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING + partition + "_" + bqt.getTransaction_code());
				if (setting == null) {
					setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING + partition);
				}
				if (setting != null && setting.isRunning()) {
					price = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE + partition + "_" + bqt.getTransaction_code());
				}
				
			}
			
			if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			
			adjustSpare(partition, bqt, price);
			doNOverFlow(partition, bqTransactionType, bqt, price);
			matchBCDeletation(partition, bqt);
		}
	}
	
	/**
	 * 调整持单备用金-余额爆仓制（余额补贴亏损）
	 * @param bqt
	 */
	private void adjustSpare(String partition, NTransaction bqt, BigDecimal price) {
		List<NMemberTransaction> list = bqMemberTransactionService.findLossOrRemain(partition, bqt.getTransaction_id(), price, bqt.getTrade_coefficient());
		for (NMemberTransaction trans : list) {
			boolean lock = bqMemberTransactionService.lockToAdjustSpare(partition, trans.getMt_id());
			if (!lock) continue;
			
			try {
				trans = bqMemberTransactionService.getMemberTransactionById(partition, trans.getMt_id());
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
					NAccountContract account = bqAccountContractService.findBTCByUidCode(partition, trans.getF_user_id(), trans.getTransaction_code());
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
									cancelPendingWithCode(partition, bqt.getCurrency_type_id(), bqt.getTransaction_code(), 0, trans.getF_user_id(), bqt.getTrade_coefficient());
								} catch (ServiceException e) {
									e.printStackTrace();
								}
								
								account = bqAccountContractService.findBTCByUidCode(partition, trans.getF_user_id(), trans.getTransaction_code());
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
					bqMemberTransactionService.adjustRemain(partition, trans.getMt_id(), trans.getF_user_id(), trans.getTransaction_code(), changeRemain, changeFrozenRemain);
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (trans != null) {
					bqMemberTransactionService.unlockToAdjustSpare(partition, trans.getMt_id());
				}
			}
		}
		
		List<NMemberTransaction> fundFeeAdjustList = bqMemberTransactionService.findHaveFundingFee(partition, bqt.getTransaction_id());
		for (NMemberTransaction trans : fundFeeAdjustList) {
			boolean lock = bqMemberTransactionService.lockToAdjustSpare(partition, trans.getMt_id());
			if (!lock) continue;
			
			try {
				trans = bqMemberTransactionService.getMemberTransactionById(partition, trans.getMt_id());
				if (trans == null || trans.getHold_count().add(trans.getLock_count()).compareTo(BigDecimal.ZERO) <= 0) continue;
				NAccountContract account = bqAccountContractService.getOverBalance(partition, trans.getF_user_id(), trans.getTransaction_code(), new BigDecimal(0.0001));
				if (account == null || account.getF_btc().compareTo(new BigDecimal(0.0001)) < 0) continue;
				
				BigDecimal changeAmount = trans.getDeposit2().abs().compareTo(account.getF_btc()) > 0 ? account.getF_btc() : trans.getDeposit2().abs();
				bqMemberTransactionService.adjustFundingFee(partition, trans.getMt_id(), trans.getF_user_id(), trans.getTransaction_code(), changeAmount);
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (trans != null) {
					bqMemberTransactionService.unlockToAdjustSpare(partition, trans.getMt_id());
				}
			}
		}
	}
	
	private void cancelPendingWithCode(String partition, int currencyId, String code, int direction, long uid, BigDecimal coefficient) throws ServiceException {
		List<NPendingDelegationCache> plistCache = pendingDelegationCacheService.getAllByUserId(partition, direction, code, uid);
		for (NPendingDelegationCache pendingDelegationCache : plistCache) {
			String lock = pendingDelegationCacheService.lock(partition, pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationCacheService.cancelPendingCache_one(partition, currencyId, pendingDelegationCache, coefficient, true);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pendingDelegationCache != null) {
						pendingDelegationCacheService.unLock(partition, pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id(), lock);
					}
				}
			}
		}
		List<NPendingDelegation> plist = pendingDelegationService.getAllByUserId(partition, direction, code, uid);
		for (NPendingDelegation pendingDelegation : plist) {
			String lock = pendingDelegationService.lock(partition, code, pendingDelegation.getDelegation_id()); 
			if (!StringUtil.isEmpty(lock)) {
				try {
					pendingDelegationService.cancelPending_one(partition, currencyId, pendingDelegation, coefficient, true);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pendingDelegation != null) {
						pendingDelegationService.unLock(partition, code, pendingDelegation.getDelegation_id(), lock);
					}
				}
			}
		}
	}
	
	/**
	 *	处理达到强平价格的持单 
	 * @param bqt
	 */
	private void doNOverFlow(String partition, NTransactionType bqTransactionType, NTransaction bqt, BigDecimal btc_index_price) {
		
//		bqMemberTransactionService.resetFlag(bqt.getTransaction_id(), -1);

		// 改变flag表明持仓单爆仓中，此时用户不可平仓操作，用户的平仓挂单只能减不能增
		bqMemberTransactionService.changeFlagForBC(partition, bqt.getTransaction_id(),  btc_index_price, bqt.getTrade_coefficient());
		// 查找爆仓中的单，上面flag改变的单
		List<NMemberTransaction> list = bqMemberTransactionService.findBCTransactions(partition, bqt.getTransaction_id());
		if (list == null || list.size() <= 0)
			return;		// 没有需要爆仓

		for (NMemberTransaction memeberTran : list) {
			
			// 止盈止损处理
			bqStopLossService.deleteByMid(partition, memeberTran.getMt_id(), Contents.STOP_LOSS_LOG_ACTION_QP, "爆仓强平，删除止盈止损单");

			planPendingDelegationService.updateStatusAndNoteByMid(partition, memeberTran.getTransaction_code(), memeberTran.getF_user_id(), memeberTran.getMt_id(), Contents.PLAN_PENDING_STATUS_FAIL, "爆仓强平，止盈止损失效");

			memeberTran = bqMemberTransactionService.getMemberTransactionById(partition, memeberTran.getMt_id());
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
			List<NPendingDelegationCache> pdcList = pendingDelegationCacheService.queryPCDelegations(partition, needPendDirection, scode, uid, memeberTran.getTrade_multiple().intValue());
			for (NPendingDelegationCache pd : pdcList) {
				if (pd.getPriority() == Contents.PRIORITY_PC) {
					String changeflag = pendingDelegationCacheService.lock(partition, scode, pd.getDelegation_id());
					int sleepCount = 0;
					boolean has = true;
					while (StringUtil.isEmpty(changeflag) && sleepCount < 100) {
						try {
							sleepCount += 10;
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						NPendingDelegationCache pending = pendingDelegationCacheService.findPendingById(partition, pd.getDelegation_id(), scode);
						if (pending == null) {	// 挂单已被撤销
							has = false;
							break;
						}
						changeflag = pendingDelegationCacheService.lock(partition, scode, pd.getDelegation_id());
					}
					
					if (!has) continue;
					
					try {
						pendingDelegationCacheService.cancelPendingCache_one(partition, bqt.getCurrency_type_id(), pd, bqt.getTrade_coefficient(), true);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (!StringUtil.isEmpty(changeflag)) {
							if (pd != null) {
								pendingDelegationCacheService.unLock(partition, scode, pd.getDelegation_id(), changeflag);
							}
						}
					}
				}
			}
			
			List<NPendingDelegation> pdList = pendingDelegationService.queryPCDelegations(partition, needPendDirection, scode, uid, memeberTran.getTrade_multiple().intValue());
			for (NPendingDelegation pd : pdList) {
				if (pd.getPriority() == Contents.PRIORITY_PC) {
					
					String changeflag = pendingDelegationService.lock(partition, scode, pd.getDelegation_id());
					int sleepCount = 0;
					boolean has = true;
					while (StringUtil.isEmpty(changeflag) && sleepCount < 100) {
						try {
							sleepCount += 10;
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						NPendingDelegation pending = pendingDelegationService.findPendingById(partition, pd.getDelegation_id(), scode);
						if (pending == null) {	// 挂单已被撤销
							break;
						}
						changeflag = pendingDelegationService.lock(partition, scode, pd.getDelegation_id());
					}
					
					if (!has) continue;
					
					
					try {
						pendingDelegationService.cancelPending_one(partition, bqt.getCurrency_type_id(), pd, bqt.getTrade_coefficient(), true);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (!StringUtil.isEmpty(changeflag)) {
							if (pd != null) {
								pendingDelegationService.unLock(partition, scode, pd.getDelegation_id(), changeflag);
							}
						}
					}
				}
			}

			NMemberTransaction newMemberTran = bqMemberTransactionService.getMemberTransactionById(partition, memeberTran.getMt_id());
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
				overFlowService.overFlowDelegate(partition, diection, btc_index_price, digits, newMemberTran, userLevel, bqt.getTrade_coefficient());
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		
		if (list.size() > 0) {
			new EmailNotify(true, list).start();
		}
	}
	
	private boolean overloadMatch(String partition, NTransaction bqTransaction) {
		
		NOverloadMatchSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.OVER_MATCH_SETTING + partition + "_" + bqTransaction.getTransaction_code());
		if (setting == null) {
			setting = bqOverloadMatchSettingService.getSettingByCode(partition, bqTransaction.getTransaction_code());
			if (setting == null) {
				setting = new NOverloadMatchSetting();
				setting.setRunning(false);
				setting.setF_user_id(0);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.OVER_MATCH_SETTING + partition + "_" + bqTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
		}
		
		if (setting == null || !setting.getRunning() || setting.getF_user_id() <= 1) return false;
		
		long matchUid = setting.getF_user_id();

		List<NPendingDelegation> buyList = pendingDelegationService.findBCPendingOverOneMinute(partition, bqTransaction.getTransaction_code(), Contents.DIRECTION_BUY);
		for (NPendingDelegation buy : buyList) {
			// 相当于平多仓
			BigDecimal amount = buy.getUndeal_count();
			BigDecimal price = buy.getDelegation_price();
			BigDecimal deposit = BTCQMath.getDeposit(amount, price, buy.getMultiple(), bqTransaction.getTrade_coefficient());
			BigDecimal fee = BigDecimal.ZERO;
			NPendingDelegationCache cache = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					Contents.DIRECTION_SELL, amount, price, BigDecimal.ZERO, amount, buy.getMultiple(), deposit,
					BigDecimal.ZERO, Contents.PRIORITY_KC, matchUid, BigDecimal.ZERO, BigDecimal.ZERO);
			cache.setDelegation_time(bqMemberTransactionService.getUnix_TIMESTAMP());
			
			BigDecimal changeAmount = deposit.add(fee);
			BigDecimal changeRemainNum = changeAmount.negate();
			if (bqAccountContractService.UpdateAmount(partition, matchUid, cache.getTransaction_code(), BigDecimal.ZERO, changeRemainNum, changeAmount, BigDecimal.ZERO) <= 0) {
				return false;
			}
			
			int type = Contents.CZLX_KTKC;
			if (cache.getDirection() == Contents.DIRECTION_BUY) {
				type = Contents.CZLX_DDKC;			
			}
			
			NPendingLog	pendingLog = new NPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(), cache.getTransaction_code(),
					type, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), BigDecimal.ZERO,
					cache.getDelegation_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), Contents.DEVICE_SERVER_QC, "");
			pendingLogService.savePendingLog(partition, pendingLog);

			if (!dealBuyAndSellCache(partition, bqTransaction.getCurrency_type_id(), bqTransaction.getTransaction_code(), buy, cache, bqTransaction.getTrade_coefficient(), true)) {
				cache.setDeal_count(BigDecimal.ZERO);
				cache.setUndeal_count(cache.getDelegation_count());
			} else {
				spyMemcachedClient.set(partition + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, buy.getDelegation_price());
			}
			
			if (cache.getUndeal_count().signum() > 0) {	// 没处理掉，撤单
				pendingLogService.updateActionByPendingNo(partition, cache.getTransaction_code(), cache.getPending_no(), cache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
				
				BigDecimal dp = cache.getUndeal_count().multiply(cache.getDelegation_price()).multiply(bqTransaction.getTrade_coefficient())
						.divide(new BigDecimal(cache.getMultiple()), 8, BigDecimal.ROUND_DOWN);
				// 恢复冻结资金
				BigDecimal change = dp.add(fee);
				if (bqAccountContractService.UpdateAmount(partition, cache.getF_user_id(), cache.getTransaction_code(), BigDecimal.ZERO, change, change.negate(), BigDecimal.ZERO) <= 0) {
					openPositionServer.movePendingLog(partition, cache);
				}
			}
		}
		
		List<NPendingDelegation> sellList = pendingDelegationService.findBCPendingOverOneMinute(partition, bqTransaction.getTransaction_code(), Contents.DIRECTION_SELL);
		for (NPendingDelegation sell : sellList) {
			// 相当于平多仓
			BigDecimal amount = sell.getUndeal_count();
			BigDecimal price = sell.getDelegation_price();
			BigDecimal deposit = BTCQMath.getDeposit(amount, price, sell.getMultiple(), bqTransaction.getTrade_coefficient());
			BigDecimal fee = BigDecimal.ZERO;
			NPendingDelegationCache cache = new NPendingDelegationCache(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
					Contents.DIRECTION_BUY, amount, price, BigDecimal.ZERO, amount, sell.getMultiple(), deposit,
					BigDecimal.ZERO, Contents.PRIORITY_KC, matchUid, BigDecimal.ZERO, BigDecimal.ZERO);
			cache.setDelegation_time(bqMemberTransactionService.getUnix_TIMESTAMP());
			
			BigDecimal changeAmount = deposit.add(fee);
			BigDecimal changeRemainNum = changeAmount.negate();
			if (bqAccountContractService.UpdateAmount(partition, matchUid, cache.getTransaction_code(), BigDecimal.ZERO, changeRemainNum, changeAmount, BigDecimal.ZERO) <= 0) {
				return false;
			}
			
			int type = Contents.CZLX_KTKC;
			if (cache.getDirection() == Contents.DIRECTION_BUY) {
				type = Contents.CZLX_DDKC;
			}
			
			NPendingLog	pendingLog = new NPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(), cache.getTransaction_code(),
					type, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), BigDecimal.ZERO,
					cache.getDelegation_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), Contents.DEVICE_SERVER_QC, "");
			pendingLogService.savePendingLog(partition, pendingLog);

			if (!dealBuyCacheAndSell(partition, bqTransaction.getCurrency_type_id(), bqTransaction.getTransaction_code(), cache, sell, bqTransaction.getTrade_coefficient(), true)) {
				cache.setDeal_count(BigDecimal.ZERO);
				cache.setUndeal_count(cache.getDelegation_count());
			} else {
				spyMemcachedClient.set(partition + "_" + bqTransaction.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, sell.getDelegation_price());
			}
			
			if (cache.getUndeal_count().signum() > 0) {	// 没处理掉，撤单
				pendingLogService.updateActionByPendingNo(partition, cache.getTransaction_code(), cache.getPending_no(), cache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
				
				BigDecimal dp = cache.getUndeal_count().multiply(cache.getDelegation_price()).multiply(bqTransaction.getTrade_coefficient())
						.divide(new BigDecimal(cache.getMultiple()), 8, BigDecimal.ROUND_DOWN);
				// 恢复冻结资金
				BigDecimal change = dp.add(fee);
				if (bqAccountContractService.UpdateAmount(partition, cache.getF_user_id(), cache.getTransaction_code(), BigDecimal.ZERO, change, change.negate(), BigDecimal.ZERO) <= 0) {
					openPositionServer.movePendingLog(partition, cache);
				}
			}
		}
			
		return true;
	}

	/**
	 * 撮合委托超过一分钟的爆仓单
	 * @param bqt
	 */
	private void matchBCDeletation(String partition, NTransaction bqt) {
		
		if (overloadMatch(partition, bqt)) return;
		
		// 获取超过一分钟的爆仓委托
		String code = bqt.getTransaction_code();
		List<NPendingDelegation> buyList = pendingDelegationService.findBCPendingOverOneMinute(partition, code, Contents.DIRECTION_BUY);
		List<NMemberTransaction> toNotifyTrans = new ArrayList<NMemberTransaction>();
		for (NPendingDelegation buy : buyList) {
			// 找盈利比最高的持多单，一个个找
			List<NMemberTransaction> tradeBuylist = bqMemberTransactionService.findMaxProfitRateTransactions(partition, bqt.getTransaction_id(), Contents.TRADE_TYPE_BUY, buy.getDelegation_price());
			for (NMemberTransaction memberTran : tradeBuylist) {
				if (memberTran.getHold_count().compareTo(buy.getUndeal_count()) < 0 && memberTran.getLock_count().compareTo(BigDecimal.ZERO) > 0) { 
					if (!bqMemberTransactionService.lockToAdjustSpare(partition, memberTran.getMt_id())) {
						break;
					}
				
					try {
						// 撮合cache委托
						matchSellCache(partition, bqt.getCurrency_type_id(), buy, memberTran.getMt_id(), bqt.getTrade_coefficient());
						buy = pendingDelegationService.findPendingById(partition, buy.getDelegation_id(), code);
						if (buy == null || buy.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
						
						// 撮合委托
						matchSell(partition, bqt.getCurrency_type_id(), buy, memberTran.getMt_id(), bqt.getTrade_coefficient());
						buy = pendingDelegationService.findPendingById(partition, buy.getDelegation_id(), code);
						if (buy == null || buy.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (memberTran != null) {
							bqMemberTransactionService.unlockToAdjustSpare(partition, memberTran.getMt_id());
						}
					}
				}
				
				if (!bqMemberTransactionService.lockToTrade(partition, memberTran.getMt_id())) {
					break;
				}
				
				try {
					// 撮合持单
					NMemberTransaction userTran = bqMemberTransactionService.getMemberTransactionById(partition, memberTran.getMt_id());
					if (userTran == null || userTran.getHold_count().signum() <= 0) continue;
					
					BigDecimal undealCount = buy.getUndeal_count();
					
					BQMember user = bqMemberService.findUserById(userTran.getF_user_id());
					if (user == null) continue;
					BQMemberLevel userLevel = getLevelByUserBfx(user);
					BigDecimal feeRate = userLevel.getEat_commission_rate();
					NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.getByCodeAndLevel(partition, userTran.getTransaction_code(), userLevel.getLevel_id());
					if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
						feeRate = transactionFeeRate.getEat_commission_rate();
					}
					
					// 相当于平多仓
					BigDecimal delegationCount = buy.getUndeal_count().compareTo(userTran.getHold_count()) > 0 ? userTran.getHold_count() : buy.getUndeal_count();
					BigDecimal deposit = delegationCount.divide(userTran.getHold_count().add(userTran.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
							.multiply(userTran.getDeposit());
					NPendingDelegationCache sellcache = new NPendingDelegationCache(userTran.getTransaction_id(), userTran.getTransaction_code(),Contents.DIRECTION_SELL, 
							delegationCount, buy.getDelegation_price(), new BigDecimal(0), delegationCount, userTran.getTrade_multiple().intValue(), 
							deposit, feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);
					sellcache.setDelegation_time(bqMemberTransactionService.getUnix_TIMESTAMP());
					sellcache.setFlag(1);
					
					int flag = bqMemberTransactionService.updateMemberTransByID(partition, userTran.getMt_id(), delegationCount.negate(), delegationCount, 
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
					if (flag > 0) {
						NPendingLog pendingLog = new NPendingLog(sellcache.getDelegation_id(), sellcache.getDelegation_time(), sellcache.getTransaction_id(),
								sellcache.getTransaction_code(), Contents.CZLX_DDPC, sellcache.getDelegation_count(), sellcache.getDelegation_price(), sellcache.getMultiple(), 
								sellcache.getDeal_count(), sellcache.getUndeal_count(), sellcache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, sellcache.getF_user_id(), 
								sellcache.getPending_no(), Contents.DEVICE_SERVER_QC, "");
						pendingLogService.savePendingLog(partition, pendingLog);
						
						boolean needBreak = false;
						if (!dealBuyAndSellCache(partition, bqt.getCurrency_type_id(), code, buy, sellcache, bqt.getTrade_coefficient(), true)) {
							sellcache.setDeal_count(BigDecimal.ZERO);
							sellcache.setUndeal_count(delegationCount);
							needBreak = true;
						} else {
							spyMemcachedClient.set(partition + "_" + code + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, buy.getDelegation_price());
						}
						
						if (sellcache.getUndeal_count().signum() > 0) {	// 没处理掉，撤单
							pendingLogService.updateActionByPendingNo(partition, sellcache.getTransaction_code(), sellcache.getPending_no(), sellcache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
							
							// 需要恢复的持仓
							if (bqMemberTransactionService.updateMemberTransByID(partition, userTran.getMt_id(), sellcache.getUndeal_count(), sellcache.getUndeal_count().negate(), BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
								logger.error("爆仓撤单错误：用户" + userTran.getF_user_id() + "的挂单量：" + sellcache.getUndeal_count() + "，持单冻结量：" + userTran.getLock_count());
							}
						}
						
						BigDecimal price = buy.getDelegation_price();
						buy = pendingDelegationService.findPendingById(partition, buy.getDelegation_id(), code);
						NMemberTransaction tr = new NMemberTransaction();
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
						bqMemberTransactionService.unlockToTrade(partition, memberTran.getMt_id());
					}
				}
			}
		}
		
		List<NPendingDelegation> sellList = pendingDelegationService.findBCPendingOverOneMinute(partition, code, Contents.DIRECTION_SELL);
		// 找盈利比最高的持空单，一个个找
		for (NPendingDelegation sell : sellList) {
			// 找盈利比最高的持空头，一个个找
			List<NMemberTransaction> tradeSelllist = bqMemberTransactionService.findMaxProfitRateTransactions(partition, bqt.getTransaction_id(), Contents.TRADE_TYPE_SELL, sell.getDelegation_price());
			for (NMemberTransaction memberTran : tradeSelllist) {
				if (memberTran.getHold_count().compareTo(sell.getUndeal_count()) < 0 && memberTran.getLock_count().compareTo(BigDecimal.ZERO) > 0) { 
					if (!bqMemberTransactionService.lockToAdjustSpare(partition, memberTran.getMt_id())) {
						break;
					}
				
					try {
						// 撮合cache委托
						matchBuyCache(partition, bqt.getCurrency_type_id(), sell, memberTran.getMt_id(), bqt.getTrade_coefficient());
						sell = pendingDelegationService.findPendingById(partition, sell.getDelegation_id(), code);
						if (sell == null || sell.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
						
						// 撮合委托
						matchBuy(partition, bqt.getCurrency_type_id(), sell, memberTran.getMt_id(), bqt.getTrade_coefficient());
						sell = pendingDelegationService.findPendingById(partition, sell.getDelegation_id(), code);
						if (sell == null || sell.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (memberTran != null) {
							bqMemberTransactionService.unlockToAdjustSpare(partition, memberTran.getMt_id());
						}
					}
				}
				
				if (!bqMemberTransactionService.lockToTrade(partition, memberTran.getMt_id())) {
					break;
				}
				
				try {
					// 撮合持单
					NMemberTransaction userTran = bqMemberTransactionService.getMemberTransactionById(partition, memberTran.getMt_id());
					if (userTran == null || userTran.getHold_count().signum() <= 0) continue;
					
					BigDecimal undealCount = sell.getUndeal_count();
					
					BQMember user = bqMemberService.findUserById(userTran.getF_user_id());
					if (user == null) continue;
					BQMemberLevel userLevel = bqMemberLevelService.findById(user.getF_user_level_id());
					BigDecimal feeRate = userLevel.getEat_commission_rate();
					NTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.getByCodeAndLevel(partition, userTran.getTransaction_code(), userLevel.getLevel_id());
					if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
						feeRate = transactionFeeRate.getEat_commission_rate();
					}
					
					// 相当于平空仓
					BigDecimal delegationCount = sell.getUndeal_count().compareTo(userTran.getHold_count()) > 0 ? userTran.getHold_count() : sell.getUndeal_count();
					BigDecimal deposit = delegationCount.divide(userTran.getHold_count().add(userTran.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
							.multiply(userTran.getDeposit());
					NPendingDelegationCache buycache = new NPendingDelegationCache(userTran.getTransaction_id(), userTran.getTransaction_code(),Contents.DIRECTION_BUY, 
							delegationCount, sell.getDelegation_price(), new BigDecimal(0), delegationCount, userTran.getTrade_multiple().intValue(), 
							deposit, feeRate, Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);
					buycache.setDelegation_time(bqMemberTransactionService.getUnix_TIMESTAMP());
					buycache.setFlag(1);
					
					int flag = bqMemberTransactionService.updateMemberTransByID(partition, userTran.getMt_id(), delegationCount.negate(), delegationCount, 
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
					if (flag > 0) {
						NPendingLog pendingLog = new NPendingLog(buycache.getDelegation_id(), buycache.getDelegation_time(), buycache.getTransaction_id(),
								buycache.getTransaction_code(), Contents.CZLX_KTPC, buycache.getDelegation_count(), buycache.getDelegation_price(), buycache.getMultiple(), 
								buycache.getDeal_count(), buycache.getUndeal_count(), buycache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, buycache.getF_user_id(), 
								buycache.getPending_no(), Contents.DEVICE_SERVER_QC, "");
						pendingLogService.savePendingLog(partition, pendingLog);
						
						boolean needBreak = false;
						if (!dealBuyCacheAndSell(partition, bqt.getCurrency_type_id(), code, buycache, sell, bqt.getTrade_coefficient(), true)) {
							buycache.setDeal_count(BigDecimal.ZERO);
							buycache.setUndeal_count(delegationCount);
							needBreak = true;
						} else {
							spyMemcachedClient.set(partition + "_" + code + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, sell.getDelegation_price());
						}
						
						if (buycache.getUndeal_count().signum() > 0) {	// 没处理掉，撤单
							pendingLogService.updateActionByPendingNo(partition, buycache.getTransaction_code(), buycache.getPending_no(), buycache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
							
							// 需要恢复的持仓
							if (bqMemberTransactionService.updateMemberTransByID(partition, userTran.getMt_id(), buycache.getUndeal_count(), buycache.getUndeal_count().negate(), BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
								logger.error("爆仓撤单错误：用户" + userTran.getF_user_id() + "的挂单量：" + buycache.getUndeal_count() + "，持单冻结量：" + userTran.getLock_count());
							}
						}
						
						NMemberTransaction tr = new NMemberTransaction();
						tr.setTrade_type_id(userTran.getTrade_type_id());
						tr.setTransaction_code(userTran.getTransaction_code());
						tr.setTransaction_id(userTran.getTransaction_id());
						tr.setF_user_id(userTran.getF_user_id());
						BigDecimal price = sell.getDelegation_price();
						sell = pendingDelegationService.findPendingById(partition, sell.getDelegation_id(), code);
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
						bqMemberTransactionService.unlockToTrade(partition, memberTran.getMt_id());
					}
				}
			}
		}
		
		if (toNotifyTrans.size() > 0) {
			new EmailNotify(false, toNotifyTrans).start();
		}
	}
	

	private NPendingDelegation matchSell(String partition, int currencyId, NPendingDelegation buy, long memberTranId, BigDecimal coefficient) {
		NMemberTransaction memberTran = bqMemberTransactionService.getMemberTransactionById(partition, memberTranId);
		if (memberTran == null || memberTran.getLock_count().signum() <= 0) return buy;

		BigDecimal needCancelCount = buy.getUndeal_count().subtract(memberTran.getHold_count());
		if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
			return buy;
		}
		
		String code = memberTran.getTransaction_code();
		List<NPendingDelegation> sells = pendingDelegationService.findPendingByMemberTransInfo(partition, code, 
				memberTran.getF_user_id(), Contents.PRIORITY_PC, Contents.DIRECTION_SELL, memberTran.getTrade_multiple().intValue());
		for (NPendingDelegation sell : sells) {
			String changeflag = pendingDelegationService.lock(partition, sell.getTransaction_code(), sell.getDelegation_id());
			if (!StringUtil.isEmpty(changeflag)) {
				try {
					NPendingDelegation pending = pendingDelegationService.findPendingById(partition, sell.getDelegation_id(), code);
					pendingDelegationService.cancelPending_one(partition, currencyId, pending, coefficient, true);
					needCancelCount = needCancelCount.subtract(pending.getUndeal_count());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (sell != null) {
						pendingDelegationService.unLock(partition, sell.getTransaction_code(), sell.getDelegation_id(), changeflag);
					}
				}
			}
			
			if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		
		return buy;
	}
	
	private NPendingDelegation matchSellCache(String partition, int currencyId, NPendingDelegation buy, long memberTranId, BigDecimal coefficient) {
		NMemberTransaction memberTran = bqMemberTransactionService.getMemberTransactionById(partition, memberTranId);
		if (memberTran == null || memberTran.getLock_count().signum() <= 0) return buy;
		
		BigDecimal needCancelCount = buy.getUndeal_count().subtract(memberTran.getHold_count());
		if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
			return buy;
		}
		
		String code = memberTran.getTransaction_code();
		List<NPendingDelegationCache> sellcaches = pendingDelegationCacheService.findPendingByMemberTransInfo(partition, code, 
				memberTran.getF_user_id(), Contents.PRIORITY_PC, Contents.DIRECTION_SELL, memberTran.getTrade_multiple().intValue());
		for (NPendingDelegationCache sellcache : sellcaches) {
			String changeflag = pendingDelegationCacheService.lock(partition, sellcache.getTransaction_code(), sellcache.getDelegation_id());
			if (!StringUtil.isEmpty(changeflag)) {
				try {
					NPendingDelegationCache pending = pendingDelegationCacheService.findPendingById(partition, sellcache.getDelegation_id(), code);
					pendingDelegationCacheService.cancelPendingCache_one(partition, currencyId, pending, coefficient, true);
					needCancelCount = needCancelCount.subtract(pending.getUndeal_count());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (sellcache != null) {
						pendingDelegationCacheService.unLock(partition, sellcache.getTransaction_code(), sellcache.getDelegation_id(), changeflag);
					}
				}
			}
			
			if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		
		return buy;
	}
	
	private NPendingDelegation matchBuy(String partition, int currencyId, NPendingDelegation sell, long memberTranId, BigDecimal coefficient) {
		NMemberTransaction memberTran = bqMemberTransactionService.getMemberTransactionById(partition, memberTranId);
		if (memberTran == null || memberTran.getLock_count().signum() <= 0) return sell;
		
		BigDecimal needCancelCount = sell.getUndeal_count().subtract(memberTran.getHold_count());
		if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
			return sell;
		}

		String code = memberTran.getTransaction_code();
		List<NPendingDelegation> buys = pendingDelegationService.findPendingByMemberTransInfo(partition, code, 
				memberTran.getF_user_id(), Contents.PRIORITY_PC, Contents.DIRECTION_BUY, memberTran.getTrade_multiple().intValue());
		for (NPendingDelegation buy : buys) {
			String changeflag = pendingDelegationService.lock(partition, buy.getTransaction_code(), buy.getDelegation_id());
			if (!StringUtil.isEmpty(changeflag)) {
				try {
					NPendingDelegation pending = pendingDelegationService.findPendingById(partition, buy.getDelegation_id(), code);
					pendingDelegationService.cancelPending_one(partition, currencyId, pending, coefficient, true);
					needCancelCount = needCancelCount.subtract(pending.getUndeal_count());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (buy != null) {
						pendingDelegationService.unLock(partition, buy.getTransaction_code(), buy.getDelegation_id(), changeflag);
					}
				}
			}
			
			if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		
		return sell;
	}
	
	private NPendingDelegation matchBuyCache(String partition, int currencyId, NPendingDelegation sell, long memberTranId, BigDecimal coefficient) {
		NMemberTransaction memberTran = bqMemberTransactionService.getMemberTransactionById(partition, memberTranId);
		if (memberTran == null || memberTran.getLock_count().signum() <= 0) return sell;
		
		BigDecimal needCancelCount = sell.getUndeal_count().subtract(memberTran.getHold_count());
		if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
			return sell;
		}
		
		String code = memberTran.getTransaction_code();
		List<NPendingDelegationCache> buycaches = pendingDelegationCacheService.findPendingByMemberTransInfo(partition, code, 
				memberTran.getF_user_id(), Contents.PRIORITY_PC, Contents.DIRECTION_BUY, memberTran.getTrade_multiple().intValue());
		for (NPendingDelegationCache buycache : buycaches) {
			String changeflag = pendingDelegationCacheService.lock(partition, buycache.getTransaction_code(), buycache.getDelegation_id());
			if (!StringUtil.isEmpty(changeflag)) {
				try {
					NPendingDelegationCache pending = pendingDelegationCacheService.findPendingById(partition, buycache.getDelegation_id(), code);
					pendingDelegationCacheService.cancelPendingCache_one(partition, currencyId, pending, coefficient, true);
					needCancelCount = needCancelCount.subtract(pending.getUndeal_count());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (buycache != null) {
						pendingDelegationCacheService.unLock(partition, buycache.getTransaction_code(), buycache.getDelegation_id(), changeflag);
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
		boolean isNOverFlow = true;
		List<NMemberTransaction> list = null;

		public EmailNotify(boolean overflow, List<NMemberTransaction> list) {
			this.isNOverFlow = overflow;
			this.list = list;
		}
		
	    public void run() {
	    	String basePath = UrlConfig.getValue("webRoot");
	        for (NMemberTransaction trans : list) {
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
	            if (isNOverFlow) {
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
