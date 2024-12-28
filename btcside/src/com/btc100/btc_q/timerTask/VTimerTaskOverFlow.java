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
import com.btc100.btc_q.entity.BQMarkPriceSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.VOverloadMatchSetting;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.entity.VPendingDelegation;
import com.btc100.btc_q.entity.VPendingDelegationCache;
import com.btc100.btc_q.entity.VPendingLog;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.VAccountContractService;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VOpenPositionServer;
import com.btc100.btc_q.service.VStopLossService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.service.VOverFlowService;
import com.btc100.btc_q.service.VOverloadMatchSettingService;
import com.btc100.btc_q.service.VPendingDelegationCacheService;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.service.VPendingLogService;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 爆仓
 * 
 * @author Administrator
 * 
 */
public class VTimerTaskOverFlow  extends VOpenPositionTask {
	
	Logger logger = Logger.getLogger(VTimerTaskOverFlow.class);

	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	private VPendingDelegationCacheService vPendingDelegationCacheService;
	@Autowired
	private VPendingLogService vPendingLogService;
	@Autowired
	private VTransactionTypeService vTransTypeService;
	@Autowired
	private VStopLossService vStopLossService;
	@Autowired
	public BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VAccountContractService vAccountContractService;
	@Autowired
	private VOverFlowService vOverFlowService;
	@Autowired
	private VOverloadMatchSettingService vOverloadMatchSettingService;
	@Autowired
	private VOpenPositionServer vOpenPositionServer;
	
	public void run() {
		List<VTransaction> list = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		for(VTransaction bqt :list){
			if (bqt.getSuspend()) {
				continue;
			}
			
			BigDecimal price = spyMemcachedClient.get(bqt.getTransaction_type_code());
			BQMarkPriceSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.MARK_PRICE_SETTING);
			if (setting != null && setting.isRunning()) {
				price = spyMemcachedClient.get(BTCQMemecacheKeys.V_MARK_PRICE + bqt.getTransaction_code());
			}
			
			if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			
			adjustSpare(bqt, price);
			doOverFlow(bqt, price);
			matchBCDeletation(bqt);
		}
	}
	
	/**
	 * 调整持单备用金-余额爆仓制（余额补贴亏损）
	 * @param bqt
	 */
	private void adjustSpare(VTransaction bqt, BigDecimal price) {
		List<VMemberTransaction> list = vMemberTransactionService.findLossOrRemain(bqt.getTransaction_id(), price);
		for (VMemberTransaction trans : list) {
			boolean lock = vMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
			if (!lock) continue;
			
			try {
				trans = vMemberTransactionService.getMemberTransactionById(trans.getMt_id());
				if (trans == null || trans.getHold_count().add(trans.getLock_count()).compareTo(BigDecimal.ZERO) <= 0) continue;
				
				BigDecimal changeRemain = BigDecimal.ZERO;
				BigDecimal changeFrozenRemain = BigDecimal.ZERO;
				BigDecimal income = price.subtract(trans.getAverage_price()).multiply(trans.getHold_count().add(trans.getLock_count()));
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
					VAccountContract account = vAccountContractService.findBTCByUidCode(trans.getF_user_id(), trans.getTransaction_code());
					if (account == null) {
						logger.info("account == null: " + trans.getF_user_id() + " : " + trans.getTransaction_code());
					}
					if (account.getF_btc().subtract(changeRemain.add(changeFrozenRemain)).compareTo(BigDecimal.ZERO) < 0) {	//	余额不够
						if (account.getF_lockbtc().compareTo(BigDecimal.ZERO) > 0) {	// 有挂单冻结
							// 判断是否达到强平
							BigDecimal qpj = BTCQMath.QPJ(trans.getTrade_type_id(), trans.getAverage_price(), 
									trans.getDeposit().add(trans.getDeposit2()).add(trans.getF_fromremain()).add(account.getF_btc()).add(trans.getF_frozenremain()).add(trans.getF_min_frozenremain()), 
									trans.getTrade_multiple(), trans.getHold_count().add(trans.getLock_count()), BigDecimal.ONE, trans.getDeposit());
							boolean isQP = false;
							if (trans.getTrade_type_id() == 1) {
								isQP = (price.compareTo(qpj) <= 0);
							} else {
								isQP = (price.compareTo(qpj) >= 0);
							}
							
							if (isQP) {
								try {
									cancelPendingWithCode(bqt.getTransaction_code(), bqt.getCurrency_type_id(), 0, trans.getF_user_id());
								} catch (ServiceException e) {
									e.printStackTrace();
								}
								
								account = vAccountContractService.findBTCByUidCode(trans.getF_user_id(), trans.getTransaction_code());
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
					vMemberTransactionService.adjustRemain(trans.getMt_id(), trans.getF_user_id(), trans.getTransaction_code(), changeRemain, changeFrozenRemain);
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (trans != null) {
					vMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
				}
			}
		}
		
		List<VMemberTransaction> fundFeeAdjustList = vMemberTransactionService.findHaveFundingFee(bqt.getTransaction_id());
		for (VMemberTransaction trans : fundFeeAdjustList) {
			boolean lock = vMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
			if (!lock) continue;
			
			try {
				trans = vMemberTransactionService.getMemberTransactionById(trans.getMt_id());
				if (trans == null || trans.getHold_count().add(trans.getLock_count()).compareTo(BigDecimal.ZERO) <= 0) continue;
				VAccountContract account = vAccountContractService.getOverBalance(trans.getF_user_id(), trans.getTransaction_code(), new BigDecimal(0.0001));
				if (account == null || account.getF_btc().compareTo(new BigDecimal(0.0001)) < 0) continue;
				
				BigDecimal changeAmount = trans.getDeposit2().abs().compareTo(account.getF_btc()) > 0 ? account.getF_btc() : trans.getDeposit2().abs();
				vMemberTransactionService.adjustFundingFee(trans.getMt_id(), trans.getF_user_id(), trans.getTransaction_code(), changeAmount);
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (trans != null) {
					vMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
				}
			}
		}
	}
	
	private void cancelPendingWithCode(String code, int currencyId, int direction, long uid) throws ServiceException {
		List<VPendingDelegationCache> plistCache = vPendingDelegationCacheService.getAllByUserId(direction, code, uid);
		for (VPendingDelegationCache vPendingDelegationCache : plistCache) {
			String lock = vPendingDelegationCacheService.lock(vPendingDelegationCache.getTransaction_code(), vPendingDelegationCache.getDelegation_id());
			if (!StringUtil.isEmpty(lock)) {
				try {
					vPendingDelegationCacheService.cancelPendingCache_one(vPendingDelegationCache, currencyId, true);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (vPendingDelegationCache != null) {
						vPendingDelegationCacheService.unLock(vPendingDelegationCache.getTransaction_code(), vPendingDelegationCache.getDelegation_id(), lock);
					}
				}
			}
		}
		List<VPendingDelegation> plist = vPendingDelegationService.getAllByUserId(direction, code, uid);
		for (VPendingDelegation vPendingDelegation : plist) {
			String lock = vPendingDelegationService.lock(code, vPendingDelegation.getDelegation_id()); 
			if (!StringUtil.isEmpty(lock)) {
				try {
					vPendingDelegationService.cancelPending_one(vPendingDelegation, currencyId, true);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (vPendingDelegation != null) {
						vPendingDelegationService.unLock(code, vPendingDelegation.getDelegation_id(), lock);
					}
				}
			}
		}
	}
	
	/**
	 *	处理达到强平价格的持单 
	 * @param bqt
	 */
	private void doOverFlow(VTransaction bqt, BigDecimal btc_index_price) {
		VTransactionType bqTransactionType = null;
		List<VTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANS_TYPES);
		if (types == null) {
			bqTransactionType = vTransTypeService.getTransTypeById(bqt.getTransaction_type());
		} else {
			for (VTransactionType ty : types) {
				if (ty.getType_id() == bqt.getTransaction_type()) {
					bqTransactionType = ty;
					break;
				}
			}
		}
		if (bqTransactionType == null) {
			return;
		}

		// 改变flag表明持仓单爆仓中，此时用户不可平仓操作，用户的平仓挂单只能减不能增
		vMemberTransactionService.changeFlagForBC(bqt.getTransaction_id(),  btc_index_price);
		// 查找爆仓中的单，上面flag改变的单
		List<VMemberTransaction> list = vMemberTransactionService.findBCTransactions(bqt.getTransaction_id());
		if (list == null || list.size() <= 0)
			return;		// 没有需要爆仓
		
		for (VMemberTransaction memeberTran : list) {
			// 止盈止损处理
			vStopLossService.deleteByMid(memeberTran.getMt_id());
			
			memeberTran = vMemberTransactionService.getMemberTransactionById(memeberTran.getMt_id());
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
			List<VPendingDelegationCache> pdcList = vPendingDelegationCacheService.queryPCDelegations(needPendDirection, scode, uid, memeberTran.getTrade_multiple().intValue());
			for (VPendingDelegationCache pd : pdcList) {
				if (pd.getPriority() == Contents.PRIORITY_PC) {
					String changeflag = vPendingDelegationCacheService.lock(scode, pd.getDelegation_id());
					int sleepCount = 0;
					boolean has = true;
					while (StringUtil.isEmpty(changeflag) && sleepCount < 100) {
						try {
							sleepCount += 10;
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						VPendingDelegationCache pending = vPendingDelegationCacheService.findPendingById(pd.getDelegation_id(), scode);
						if (pending == null) {	// 挂单已被撤销
							has = false;
							break;
						}
						changeflag = vPendingDelegationCacheService.lock(scode, pd.getDelegation_id());
					}
					
					if (!has) continue;
					
					try {
						vPendingDelegationCacheService.cancelPendingCache_one(pd, bqt.getCurrency_type_id(), true);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (!StringUtil.isEmpty(changeflag)) {
							if (pd != null) {
								vPendingDelegationCacheService.unLock(scode, pd.getDelegation_id(), changeflag);
							}
						}
					}
				}
			}
			
			List<VPendingDelegation> pdList = vPendingDelegationService.queryPCDelegations(needPendDirection, scode, uid, memeberTran.getTrade_multiple().intValue());
			for (VPendingDelegation pd : pdList) {
				if (pd.getPriority() == Contents.PRIORITY_PC) {
					String changeflag = vPendingDelegationService.lock(scode, pd.getDelegation_id());
					int sleepCount = 0;
					boolean has = true;
					while (StringUtil.isEmpty(changeflag) && sleepCount < 100) {
						try {
							sleepCount += 10;
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						VPendingDelegation pending = vPendingDelegationService.findPendingById(pd.getDelegation_id(), scode);
						if (pending == null) {	// 挂单已被撤销
							break;
						}
						changeflag = vPendingDelegationService.lock(scode, pd.getDelegation_id());
					}
					
					if (!has) continue;
					
					try {
						vPendingDelegationService.cancelPending_one(pd, bqt.getCurrency_type_id(), true);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (!StringUtil.isEmpty(changeflag)) {
							if (pd != null) {
								vPendingDelegationService.unLock(scode, pd.getDelegation_id(), changeflag);
							}
						}
					}
				}
			}

			VMemberTransaction newMemberTran = vMemberTransactionService.getMemberTransactionById(memeberTran.getMt_id());
			if (newMemberTran == null || newMemberTran.getHold_count().compareTo(BigDecimal.ZERO) <= 0) continue;
			
			if (newMemberTran.getLock_count().compareTo(BigDecimal.ZERO) != 0) {
				logger.error("爆仓撤单错误:用户" + uid + "所有平仓撤单之后依然冻结：" + newMemberTran.getLock_count());
			}
			
			BQMember user = bqMemberService.findUserById(newMemberTran.getF_user_id());
			if (user == null) continue;
			BQMemberLevel userLevel = bqMemberLevelService.findById(user.getF_user_level_id());
			int digits = Utils.getDigitsFromMinFloat(bqTransactionType.getMin_float_price());
			
			try {
				vOverFlowService.overFlowDelegate(diection, btc_index_price, digits, newMemberTran, userLevel, bqt.getTrade_coefficient());
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean overloadMatch(VTransaction vTransaction) {
		VOverloadMatchSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.V_OVER_MATCH_SETTING + vTransaction.getTransaction_code());
		if (setting == null) {
			setting = vOverloadMatchSettingService.getSettingByCode(vTransaction.getTransaction_code());
			if (setting == null) {
				setting = new VOverloadMatchSetting();
				setting.setRunning(false);
				setting.setF_user_id(0);
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.V_OVER_MATCH_SETTING + vTransaction.getTransaction_code(), BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
		}
		
		if (setting == null || !setting.getRunning() || setting.getF_user_id() <= 1) return false;
		
		long matchUid = setting.getF_user_id();

		List<VPendingDelegation> buyList = vPendingDelegationService.findBCPendingOverOneMinute(vTransaction.getTransaction_code(), Contents.DIRECTION_BUY);
		for (VPendingDelegation buy : buyList) {
			// 相当于平多仓
			BigDecimal amount = buy.getUndeal_count();
			BigDecimal price = buy.getDelegation_price();
			BigDecimal deposit = BTCQMath.getDeposit(amount, price, buy.getMultiple(), vTransaction.getTrade_coefficient());
			BigDecimal fee = BigDecimal.ZERO;
			VPendingDelegationCache cache = new VPendingDelegationCache(vTransaction.getTransaction_id(), vTransaction.getTransaction_code(),
					Contents.DIRECTION_SELL, amount, price, BigDecimal.ZERO, amount, buy.getMultiple(), deposit,
					BigDecimal.ZERO, Contents.PRIORITY_KC, matchUid, BigDecimal.ZERO, BigDecimal.ZERO);
			cache.setDelegation_time(vMemberTransactionService.getUnix_TIMESTAMP());
			
			BigDecimal changeAmount = deposit.add(fee);
			BigDecimal changeRemainNum = changeAmount.negate();
			if (vAccountContractService.UpdateAmount(matchUid, cache.getTransaction_code(), BigDecimal.ZERO, changeRemainNum, changeAmount, BigDecimal.ZERO) <= 0) {
				return false;
			}
			
			int type = Contents.CZLX_KTKC;
			if (cache.getDirection() == Contents.DIRECTION_BUY) {
				type = Contents.CZLX_DDKC;			
			}
			
			VPendingLog	pendingLog = new VPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(), cache.getTransaction_code(),
					type, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), BigDecimal.ZERO,
					cache.getDelegation_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), Contents.DEVICE_SERVER_QC, "");
			vPendingLogService.savePendingLog(pendingLog);

			if (!dealBuyAndSellCache(vTransaction.getTransaction_code(), vTransaction.getCurrency_type_id(), buy, cache, true)) {
				cache.setDeal_count(BigDecimal.ZERO);
				cache.setUndeal_count(cache.getDelegation_count());
			} else {
				BigDecimal lastPrice = buy.getDelegation_price();
				VTransaction bqt_temp = vTransactionService.findTransToUpdateByCode(vTransaction.getTransaction_code());
				BigDecimal highPrice = bqt_temp.getTransaction_highest();
				bqt_temp.setLatest_transaction_price(lastPrice);
				if (lastPrice.compareTo(highPrice) > 0) {
					bqt_temp.setTransaction_highest(lastPrice);
				}
				BigDecimal lowPrice = bqt_temp.getTransaction_lowest();
				if (lastPrice.compareTo(lowPrice) < 0 || lowPrice.signum() <= 0) {
					bqt_temp.setTransaction_lowest(lastPrice);
				}
				vTransactionService.update(bqt_temp);
				spyMemcachedClient.set(vTransaction.getTransaction_code() + BTCQMemecacheKeys.V_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
			}
			
			if (cache.getUndeal_count().signum() > 0) {	// 没处理掉，撤单
				vPendingLogService.updateActionByPendingNo(cache.getTransaction_code(), cache.getPending_no(), cache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
				
				BigDecimal dp = cache.getUndeal_count().multiply(cache.getDelegation_price()).multiply(vTransaction.getTrade_coefficient())
						.divide(new BigDecimal(cache.getMultiple()), 8, BigDecimal.ROUND_DOWN);
				// 恢复冻结资金
				BigDecimal change = dp.add(fee);
				if (vAccountContractService.UpdateAmount(cache.getF_user_id(), cache.getTransaction_code(), BigDecimal.ZERO, change, change.negate(), BigDecimal.ZERO) <= 0) {
					vOpenPositionServer.movePendingLog(cache);
				}
			}
		}
		
		List<VPendingDelegation> sellList = vPendingDelegationService.findBCPendingOverOneMinute(vTransaction.getTransaction_code(), Contents.DIRECTION_SELL);
		for (VPendingDelegation sell : sellList) {
			// 相当于平多仓
			BigDecimal amount = sell.getUndeal_count();
			BigDecimal price = sell.getDelegation_price();
			BigDecimal deposit = BTCQMath.getDeposit(amount, price, sell.getMultiple(), vTransaction.getTrade_coefficient());
			BigDecimal fee = BigDecimal.ZERO;
			VPendingDelegationCache cache = new VPendingDelegationCache(vTransaction.getTransaction_id(), vTransaction.getTransaction_code(),
					Contents.DIRECTION_BUY, amount, price, BigDecimal.ZERO, amount, sell.getMultiple(), deposit,
					BigDecimal.ZERO, Contents.PRIORITY_KC, matchUid, BigDecimal.ZERO, BigDecimal.ZERO);
			cache.setDelegation_time(vMemberTransactionService.getUnix_TIMESTAMP());
			
			BigDecimal changeAmount = deposit.add(fee);
			BigDecimal changeRemainNum = changeAmount.negate();
			if (vAccountContractService.UpdateAmount(matchUid, cache.getTransaction_code(), BigDecimal.ZERO, changeRemainNum, changeAmount, BigDecimal.ZERO) <= 0) {
				return false;
			}
			
			int type = Contents.CZLX_KTKC;
			if (cache.getDirection() == Contents.DIRECTION_BUY) {
				type = Contents.CZLX_DDKC;
			}
			
			VPendingLog pendingLog = new VPendingLog(cache.getDelegation_id(), cache.getDelegation_time(), cache.getTransaction_id(), cache.getTransaction_code(),
					type, cache.getDelegation_count(), cache.getDelegation_price(), cache.getMultiple(), BigDecimal.ZERO,
					cache.getDelegation_count(), cache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, cache.getF_user_id(), cache.getPending_no(), Contents.DEVICE_SERVER_QC, "");
			vPendingLogService.savePendingLog(pendingLog);

			if (!dealBuyCacheAndSell(vTransaction.getTransaction_code(), vTransaction.getCurrency_type_id(), cache, sell, true)) {
				cache.setDeal_count(BigDecimal.ZERO);
				cache.setUndeal_count(cache.getDelegation_count());
			} else {
				BigDecimal lastPrice = sell.getDelegation_price();
				VTransaction bqt_temp = vTransactionService.findTransToUpdateByCode(vTransaction.getTransaction_code());
				BigDecimal highPrice = bqt_temp.getTransaction_highest();
				bqt_temp.setLatest_transaction_price(lastPrice);
				if (lastPrice.compareTo(highPrice) > 0) {
					bqt_temp.setTransaction_highest(lastPrice);
				}
				BigDecimal lowPrice = bqt_temp.getTransaction_lowest();
				if (lastPrice.compareTo(lowPrice) < 0 || lowPrice.signum() <= 0) {
					bqt_temp.setTransaction_lowest(lastPrice);
				}
				vTransactionService.update(bqt_temp);
				spyMemcachedClient.set(vTransaction.getTransaction_code() + BTCQMemecacheKeys.V_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
			}
			
			if (cache.getUndeal_count().signum() > 0) {	// 没处理掉，撤单
				vPendingLogService.updateActionByPendingNo(cache.getTransaction_code(), cache.getPending_no(), cache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
				
				BigDecimal dp = cache.getUndeal_count().multiply(cache.getDelegation_price()).multiply(vTransaction.getTrade_coefficient())
						.divide(new BigDecimal(cache.getMultiple()), 8, BigDecimal.ROUND_DOWN);
				// 恢复冻结资金
				BigDecimal change = dp.add(fee);
				if (vAccountContractService.UpdateAmount(cache.getF_user_id(), cache.getTransaction_code(), BigDecimal.ZERO, change, change.negate(), BigDecimal.ZERO) <= 0) {
					vOpenPositionServer.movePendingLog(cache);
				}
			}
		}
			
		return true;
	}

	/**
	 * 撮合委托超过一分钟的爆仓单
	 * @param bqt
	 */
	private void matchBCDeletation(VTransaction bqt) {
		
		if (overloadMatch(bqt)) return;
		
		// 获取超过一分钟的爆仓委托
		String code = bqt.getTransaction_code();
		List<VPendingDelegation> buyList = vPendingDelegationService.findBCPendingOverOneMinute(code, Contents.DIRECTION_BUY);
		List<VMemberTransaction> toNotifyTrans = new ArrayList<VMemberTransaction>();
		for (VPendingDelegation buy : buyList) {
			// 找盈利比最高的持多单，一个个找
			List<VMemberTransaction> tradeBuylist = vMemberTransactionService.findMaxProfitRateTransactions(bqt.getTransaction_id(), Contents.TRADE_TYPE_BUY, buy.getDelegation_price());
			for (VMemberTransaction memberTran : tradeBuylist) {
				if (memberTran.getHold_count().compareTo(buy.getUndeal_count()) < 0 && memberTran.getLock_count().compareTo(BigDecimal.ZERO) > 0) { 
					if (!vMemberTransactionService.lockToAdjustSpare(memberTran.getMt_id())) {
						break;
					}
				
					try {
						// 撮合cache委托
						matchSellCache(buy, memberTran.getMt_id(), bqt.getCurrency_type_id());
						buy = vPendingDelegationService.findPendingById(buy.getDelegation_id(), code);
						if (buy == null || buy.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
						
						// 撮合委托
						matchSell(buy, memberTran.getMt_id(), bqt.getCurrency_type_id());
						buy = vPendingDelegationService.findPendingById(buy.getDelegation_id(), code);
						if (buy == null || buy.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (memberTran != null) {
							vMemberTransactionService.unlockToAdjustSpare(memberTran.getMt_id());
						}
					}
				}
				
				if (!vMemberTransactionService.lockToTrade(memberTran.getMt_id())) {
					break;
				}
				
				try {
					// 撮合持单
					VMemberTransaction userTran = vMemberTransactionService.getMemberTransactionById(memberTran.getMt_id());
					if (userTran == null || userTran.getHold_count().signum() <= 0) continue;
					
					BigDecimal undealCount = buy.getUndeal_count();
					
					BQMember user = bqMemberService.findUserById(userTran.getF_user_id());
					if (user == null) continue;
					BQMemberLevel userLevel = bqMemberLevelService.findById(user.getF_user_level_id());
					
					// 相当于平多仓
					BigDecimal delegationCount = buy.getUndeal_count().compareTo(userTran.getHold_count()) > 0 ? userTran.getHold_count() : buy.getUndeal_count();
					BigDecimal deposit = delegationCount.divide(userTran.getHold_count().add(userTran.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
							.multiply(userTran.getDeposit());
					VPendingDelegationCache sellcache = new VPendingDelegationCache(userTran.getTransaction_id(), userTran.getTransaction_code(),Contents.DIRECTION_SELL, 
							delegationCount, buy.getDelegation_price(), new BigDecimal(0), delegationCount, userTran.getTrade_multiple().intValue(), 
							deposit, userLevel.getEat_commission_rate(), Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);
					sellcache.setDelegation_time(vMemberTransactionService.getUnix_TIMESTAMP());
					sellcache.setFlag(1);
					
					boolean needBreak = false;
					int flag = vMemberTransactionService.updateMemberTransByID(userTran.getMt_id(), delegationCount.negate(), delegationCount, 
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
					if (flag > 0) {
						VPendingLog vPendingLog = new VPendingLog(sellcache.getDelegation_id(), sellcache.getDelegation_time(), sellcache.getTransaction_id(),
								sellcache.getTransaction_code(), Contents.CZLX_DDPC, sellcache.getDelegation_count(), sellcache.getDelegation_price(), sellcache.getMultiple(), 
								sellcache.getDeal_count(), sellcache.getUndeal_count(), sellcache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, sellcache.getF_user_id(), 
								sellcache.getPending_no(), Contents.DEVICE_SERVER_QC, "");
						vPendingLogService.savePendingLog(vPendingLog);
						
						if (!dealBuyAndSellCache(code, bqt.getCurrency_type_id(), buy, sellcache, true)) {
							sellcache.setDeal_count(BigDecimal.ZERO);
							sellcache.setUndeal_count(delegationCount);
							needBreak = true;
						} else {
							
							BigDecimal lastPrice = buy.getDelegation_price();
							VTransaction bqt_temp = vTransactionService.findTransToUpdateByCode(code);
							BigDecimal highPrice = bqt_temp.getTransaction_highest();
							bqt_temp.setLatest_transaction_price(lastPrice);
							if (lastPrice.compareTo(highPrice) > 0) {
								bqt_temp.setTransaction_highest(lastPrice);
							}
							BigDecimal lowPrice = bqt_temp.getTransaction_lowest();
							if (lastPrice.compareTo(lowPrice) < 0 || lowPrice.signum() <= 0) {
								bqt_temp.setTransaction_lowest(lastPrice);
							}
							vTransactionService.update(bqt_temp);
							spyMemcachedClient.set(code + BTCQMemecacheKeys.V_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);
						}
						
						if (sellcache.getUndeal_count().signum() > 0) {	// 没处理掉，撤单
							vPendingLogService.updateActionByPendingNo(sellcache.getTransaction_code(), sellcache.getPending_no(), sellcache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
							
							// 需要恢复的持仓
							if (vMemberTransactionService.updateMemberTransByID(userTran.getMt_id(), sellcache.getUndeal_count(), sellcache.getUndeal_count().negate(), BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
								logger.error("爆仓撤单错误：用户" + userTran.getF_user_id() + "的挂单量：" + sellcache.getUndeal_count() + "，持单冻结量：" + userTran.getLock_count());
							}
						}
						
						BigDecimal price = buy.getDelegation_price();
						buy = vPendingDelegationService.findPendingById(buy.getDelegation_id(), code);
						VMemberTransaction tr = new VMemberTransaction();
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
					} else {
						break;
					}
					
					if (needBreak) {
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (memberTran != null) {
						vMemberTransactionService.unlockToTrade(memberTran.getMt_id());
					}
				}
			}
		}
		
		List<VPendingDelegation> sellList = vPendingDelegationService.findBCPendingOverOneMinute(code, Contents.DIRECTION_SELL);
		// 找盈利比最高的持空单，一个个找
		for (VPendingDelegation sell : sellList) {
			// 找盈利比最高的持空头，一个个找
			List<VMemberTransaction> tradeSelllist = vMemberTransactionService.findMaxProfitRateTransactions(bqt.getTransaction_id(), Contents.TRADE_TYPE_SELL, sell.getDelegation_price());
			for (VMemberTransaction memberTran : tradeSelllist) {
				if (memberTran.getHold_count().compareTo(sell.getUndeal_count()) < 0 && memberTran.getLock_count().compareTo(BigDecimal.ZERO) > 0) { 
					if (!vMemberTransactionService.lockToAdjustSpare(memberTran.getMt_id())) {
						break;
					}
					
					try {
						// 撮合cache委托
						matchBuyCache(sell, memberTran.getMt_id(), bqt.getCurrency_type_id());
						sell = vPendingDelegationService.findPendingById(sell.getDelegation_id(), code);
						if (sell == null || sell.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
						
						// 撮合委托
						matchBuy(sell, memberTran.getMt_id(), bqt.getCurrency_type_id());
						sell = vPendingDelegationService.findPendingById(sell.getDelegation_id(), code);
						if (sell == null || sell.getUndeal_count().compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (memberTran != null) {
							vMemberTransactionService.unlockToAdjustSpare(memberTran.getMt_id());
						}
					}
				}
				
				if (!vMemberTransactionService.lockToTrade(memberTran.getMt_id())) {
					break;
				}
				
				try {
					// 撮合持单
					VMemberTransaction userTran = vMemberTransactionService.getMemberTransactionById(memberTran.getMt_id());
					
					if (userTran == null || userTran.getHold_count().signum() <= 0) continue;
					
					BigDecimal undealCount = sell.getUndeal_count();
					
					BQMember user = bqMemberService.findUserById(userTran.getF_user_id());
					if (user == null) continue;
					BQMemberLevel userLevel = bqMemberLevelService.findById(user.getF_user_level_id());
					
					// 相当于平空仓
					BigDecimal delegationCount = sell.getUndeal_count().compareTo(userTran.getHold_count()) > 0 ? userTran.getHold_count() : sell.getUndeal_count();
					BigDecimal deposit = delegationCount.divide(userTran.getHold_count().add(userTran.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
							.multiply(userTran.getDeposit());
					VPendingDelegationCache buycache = new VPendingDelegationCache(userTran.getTransaction_id(), userTran.getTransaction_code(),Contents.DIRECTION_BUY, 
							delegationCount, sell.getDelegation_price(), new BigDecimal(0), delegationCount, userTran.getTrade_multiple().intValue(), 
							deposit, userLevel.getEat_commission_rate(), Contents.PRIORITY_PC, user.getF_user_id(), BigDecimal.ZERO, BigDecimal.ZERO);
					buycache.setDelegation_time(vMemberTransactionService.getUnix_TIMESTAMP());
					buycache.setFlag(1);
					
					boolean needBreak = false;
					int flag = vMemberTransactionService.updateMemberTransByID(userTran.getMt_id(), delegationCount.negate(), delegationCount, 
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
					if (flag > 0) {
						VPendingLog vPendingLog = new VPendingLog(buycache.getDelegation_id(), buycache.getDelegation_time(), buycache.getTransaction_id(),
								buycache.getTransaction_code(), Contents.CZLX_KTPC, buycache.getDelegation_count(), buycache.getDelegation_price(), buycache.getMultiple(), 
								buycache.getDeal_count(), buycache.getUndeal_count(), buycache.getDeposit(), Contents.PENDING_LOG_ACTION_CS, buycache.getF_user_id(), 
								buycache.getPending_no(), Contents.DEVICE_SERVER_QC, "");
						vPendingLogService.savePendingLog(vPendingLog);
						
						if (!dealBuyCacheAndSell(code, bqt.getCurrency_type_id(), buycache, sell, true)) {
							buycache.setDeal_count(BigDecimal.ZERO);
							buycache.setUndeal_count(delegationCount);
							needBreak = true;
						} else {
							BigDecimal lastPrice = sell.getDelegation_price();
							VTransaction bqt_temp = vTransactionService.findTransToUpdateByCode(code);
							BigDecimal highPrice = bqt_temp.getTransaction_highest();
							bqt_temp.setLatest_transaction_price(lastPrice);
							if (lastPrice.compareTo(highPrice) > 0) {
								bqt_temp.setTransaction_highest(lastPrice);
							}
							BigDecimal lowPrice = bqt_temp.getTransaction_lowest();
							if (lastPrice.compareTo(lowPrice) < 0 || lowPrice.signum() <= 0) {
								bqt_temp.setTransaction_lowest(lastPrice);
							}
							vTransactionService.update(bqt_temp);
							spyMemcachedClient.set(code + BTCQMemecacheKeys.V_BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, lastPrice);

						}
						
						if (buycache.getUndeal_count().signum() > 0) {	// 没处理掉，撤单
							vPendingLogService.updateActionByPendingNo(buycache.getTransaction_code(), buycache.getPending_no(), buycache.getF_user_id(), Contents.PENDING_LOG_ACTION_CD);
							
							// 需要恢复的持仓
							if (vMemberTransactionService.updateMemberTransByID(userTran.getMt_id(), buycache.getUndeal_count(), buycache.getUndeal_count().negate(), BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
								logger.error("爆仓撤单错误：用户" + userTran.getF_user_id() + "的挂单量：" + buycache.getUndeal_count() + "，持单冻结量：" + userTran.getLock_count());
							}
						}
						
						VMemberTransaction tr = new VMemberTransaction();
						tr.setTrade_type_id(userTran.getTrade_type_id());
						tr.setTransaction_code(userTran.getTransaction_code());
						tr.setTransaction_id(userTran.getTransaction_id());
						tr.setF_user_id(userTran.getF_user_id());
						BigDecimal price = sell.getDelegation_price();
						sell = vPendingDelegationService.findPendingById(sell.getDelegation_id(), code);
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
					} else {
						break;
					}
					
					if (needBreak) {
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (memberTran != null) {
						vMemberTransactionService.unlockToTrade(memberTran.getMt_id());
					}
				}
			}
		}
		
		if (toNotifyTrans.size() > 0) {
			new VEmailNotify(false, toNotifyTrans).start();
		}
	}
	

	private VPendingDelegation matchSell(VPendingDelegation buy, long memberTranId, int currencyId) {
		VMemberTransaction memberTran = vMemberTransactionService.getMemberTransactionById(memberTranId);
		if (memberTran == null || memberTran.getLock_count().signum() <= 0) return buy;
		
		BigDecimal needCancelCount = buy.getUndeal_count().subtract(memberTran.getHold_count());
		if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
			return buy;
		}

		String code = memberTran.getTransaction_code();
		List<VPendingDelegation> sells = vPendingDelegationService.findPendingByMemberTransInfo(code, 
				memberTran.getF_user_id(), Contents.PRIORITY_PC, Contents.DIRECTION_SELL, memberTran.getTrade_multiple().intValue());
		for (VPendingDelegation sell : sells) {
			String changeflag = vPendingDelegationService.lock(sell.getTransaction_code(), sell.getDelegation_id());
			if (!StringUtil.isEmpty(changeflag)) {
				try {
					VPendingDelegation pending = vPendingDelegationService.findPendingById(sell.getDelegation_id(), code);
					vPendingDelegationService.cancelPending_one(pending, currencyId, true);
					needCancelCount = needCancelCount.subtract(pending.getUndeal_count());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (sell != null) {
						vPendingDelegationService.unLock(sell.getTransaction_code(), sell.getDelegation_id(), changeflag);
					}
				}
			}
			
			if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		
		return buy;
	}
	
	private VPendingDelegation matchSellCache(VPendingDelegation buy, long memberTranId, int currencyId) {
		VMemberTransaction memberTran = vMemberTransactionService.getMemberTransactionById(memberTranId);
		if (memberTran == null || memberTran.getLock_count().signum() <= 0) return buy;
		
		BigDecimal needCancelCount = buy.getUndeal_count().subtract(memberTran.getHold_count());
		if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
			return buy;
		}
		
		String code = memberTran.getTransaction_code();
		List<VPendingDelegationCache> sellcaches = vPendingDelegationCacheService.findPendingByMemberTransInfo(code, 
				memberTran.getF_user_id(), Contents.PRIORITY_PC, Contents.DIRECTION_SELL, memberTran.getTrade_multiple().intValue());
		for (VPendingDelegationCache sellcache : sellcaches) {
			String changeflag = vPendingDelegationCacheService.lock(sellcache.getTransaction_code(), sellcache.getDelegation_id());
			if (!StringUtil.isEmpty(changeflag)) {
				try {
					VPendingDelegationCache pending = vPendingDelegationCacheService.findPendingById(sellcache.getDelegation_id(), code);
					vPendingDelegationCacheService.cancelPendingCache_one(pending, currencyId, true);
					needCancelCount = needCancelCount.subtract(pending.getUndeal_count());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (sellcache != null) {
						vPendingDelegationCacheService.unLock(sellcache.getTransaction_code(), sellcache.getDelegation_id(), changeflag);
					}
				}
			}
			
			if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		
		return buy;
	}
	
	private VPendingDelegation matchBuy(VPendingDelegation sell, long memberTranId, int currencyId) {
		VMemberTransaction memberTran = vMemberTransactionService.getMemberTransactionById(memberTranId);
		if (memberTran == null || memberTran.getLock_count().signum() <= 0) return sell;
		
		BigDecimal needCancelCount = sell.getUndeal_count().subtract(memberTran.getHold_count());
		if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
			return sell;
		}

		String code = memberTran.getTransaction_code();
		List<VPendingDelegation> buys = vPendingDelegationService.findPendingByMemberTransInfo(code, 
				memberTran.getF_user_id(), Contents.PRIORITY_PC, Contents.DIRECTION_BUY, memberTran.getTrade_multiple().intValue());
		for (VPendingDelegation buy : buys) {
			String changeflag = vPendingDelegationService.lock(buy.getTransaction_code(), buy.getDelegation_id());
			if (!StringUtil.isEmpty(changeflag)) {
				try {
					VPendingDelegation pending = vPendingDelegationService.findPendingById(buy.getDelegation_id(), code);
					vPendingDelegationService.cancelPending_one(pending, currencyId, true);
					needCancelCount = needCancelCount.subtract(pending.getUndeal_count());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (buy != null) {
						vPendingDelegationService.unLock(buy.getTransaction_code(), buy.getDelegation_id(), changeflag);
					}
				}
			}
			
			if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		
		return sell;
	}
	
	private VPendingDelegation matchBuyCache(VPendingDelegation sell, long memberTranId, int currencyId) {
		VMemberTransaction memberTran = vMemberTransactionService.getMemberTransactionById(memberTranId);
		if (memberTran == null || memberTran.getLock_count().signum() <= 0) return sell;
		
		BigDecimal needCancelCount = sell.getUndeal_count().subtract(memberTran.getHold_count());
		if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
			return sell;
		}
		
		String code = memberTran.getTransaction_code();
		List<VPendingDelegationCache> buycaches = vPendingDelegationCacheService.findPendingByMemberTransInfo(code, 
				memberTran.getF_user_id(), Contents.PRIORITY_PC, Contents.DIRECTION_BUY, memberTran.getTrade_multiple().intValue());
		for (VPendingDelegationCache buycache : buycaches) {
			String changeflag = vPendingDelegationCacheService.lock(buycache.getTransaction_code(), buycache.getDelegation_id());
			if (!StringUtil.isEmpty(changeflag)) {
				try {
					VPendingDelegationCache pending = vPendingDelegationCacheService.findPendingById(buycache.getDelegation_id(), code);
					vPendingDelegationCacheService.cancelPendingCache_one(pending, currencyId, true);
					needCancelCount = needCancelCount.subtract(pending.getUndeal_count());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (buycache != null) {
						vPendingDelegationCacheService.unLock(buycache.getTransaction_code(), buycache.getDelegation_id(), changeflag);
					}
				}
			}
			
			if (needCancelCount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		
		return sell;
	}
	
	class VEmailNotify extends Thread {
		boolean isOverFlow = true;
		List<VMemberTransaction> list = null;

		public VEmailNotify(boolean overflow, List<VMemberTransaction> list) {
			this.isOverFlow = overflow;
			this.list = list;
		}
		
	    public void run() {
	    	String basePath = UrlConfig.getValue("webRoot");
	        for (VMemberTransaction trans : list) {
	        	 String isSent = spyMemcachedClient.get("V_OverEmailNotify:" + trans.getF_user_id());
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
	        	
	        	StringBuffer sb = new StringBuffer("尊敬的xkd用户，您好！<br>");
	            if (isOverFlow) {
	            	sb.append("&nbsp;&nbsp;&nbsp;&nbsp;您的模拟交易" + trans.getTransaction_code() + "合约" + transType + "已因标记价格达到您的强平触发价而造成穿仓。请您知晓！<br><br>");
	            } else {
	            	sb.append("&nbsp;&nbsp;&nbsp;&nbsp;您的模拟交易" + trans.getTransaction_code() + "合约" + transType + "盈利比例最高，以" + trans.getAverage_price().doubleValue() + "价格与穿仓单撮合成交");
	            	sb.append(trans.getHold_count().doubleValue() + "个，请您合理安排自己合约的仓位。<br><br>");
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
