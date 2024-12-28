package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionFeeRate;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.BQUserMonitor;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQTransactionFeeRateService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.BQUserMonitorService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

@Component
public class UserMonitorTimerTask extends BaseMemberLevelTask {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQUserMonitorService bqUserMonitorService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private BQTransactionTypeService bqTransactionTypeService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQTransactionFeeRateService bqTransactionFeeRateService;

	public void run() {
		List<BQUserMonitor> userMonitors = bqUserMonitorService.findAll();
		for (BQUserMonitor userMonitor : userMonitors) {
			long uid = userMonitor.getF_user_id();
			String userId = "'" + uid + "'";
			List<BQMemberTransaction> memberTransList = bqMemberTransactionService.getBQMemberTrasactionByUsers(userId);
			if (memberTransList.size() == 0) continue;
			
			// 持仓保证金
			BigDecimal deposit = BigDecimal.ZERO;
			for (BQMemberTransaction trans : memberTransList) {
				deposit = deposit.add(trans.getDeposit().add(trans.getDeposit2()));
			}
			
			BQMember user = bqMemberService.findUserById(uid);
			if (user == null) continue;
			BQMemberLevel userlevel = getLevelByUserBfx(user);
			
			// 账户余额 + 冻结
			BQAccount account = bqAccountService.getAccount(uid, Contents.TRADE_CURRENCY_TYPE_ID);
			BigDecimal accountBalance = account.getBtc().add(account.getBtc_locked());
			
			// 合约余额 - 为0时，还需要计算亏损
			List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
			if (transactionList == null) {
				transactionList = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
				spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
			}
			
			BigDecimal allContractBalance = BigDecimal.ZERO; 
			for (BQMemberTransaction trans : memberTransList) {
				
				BQTransaction currBqt = null;
				for (BQTransaction bq : transactionList) {
					if (bq.getTransaction_code().equals(trans.getTransaction_code())) {
						currBqt = bq;
					}
				}
				
				if (currBqt == null) continue;
				
				BQAccountContract contractAccount = bqAccountContractService.findByUidCode(uid, currBqt.getTransaction_code());
				BigDecimal balance = contractAccount.getF_btc();
				BigDecimal frozen = contractAccount.getF_lockbtc();
				
				BigDecimal price = spyMemcachedClient.get(currBqt.getTransaction_type_code());
				if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}
				
				BigDecimal thold = new BigDecimal(0.001);
				if (balance.compareTo(thold) <= 0) {
					// 检查亏损
					BigDecimal income = price.subtract(trans.getAverage_price()).multiply(trans.getHold_count().add(trans.getLock_count())).multiply(currBqt.getTrade_coefficient());
					if (trans.getTrade_type_id() == 2) {	//	持空仓
						income = income.negate();
					}
					
					if (income.compareTo(BigDecimal.ZERO) < 0) {
						balance = income.add(trans.getF_fromremain()).add(trans.getF_frozenremain()).add(trans.getF_min_frozenremain());
					}
				}
				
				allContractBalance = allContractBalance.add(balance.add(frozen));
			}
			
			BigDecimal allBalance = deposit.add(accountBalance).add(allContractBalance);
			if (allBalance.compareTo(userMonitor.getThreshold()) <= 0) {
				
				// 平仓
				List<String> dealCodeType = new ArrayList<String>();
				for (BQMemberTransaction trans : memberTransList) {
					BigDecimal price = null;
					Integer digits = null;
					BQTransaction currBqt = null;
					for (BQTransaction bq : transactionList) {
						if (bq.getTransaction_code().equals(trans.getTransaction_code())) {
							price = spyMemcachedClient.get(bq.getTransaction_type_code());
							currBqt = bq;
							digits = spyMemcachedClient.get(bq.getTransaction_type_code() + BTCQMemecacheKeys.DIGITS);
							if (digits == null) {
								BQTransactionType transType = bqTransactionTypeService.getTransactionType(bq.getTransaction_type());
								digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
								spyMemcachedClient.set(transType.getType_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
							}
							
							break;
						}
					}
					
					if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
						continue;
					}
					
					if (digits == null) digits = 2;
					
					// 先撤单所有
					boolean transLock = bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
					if (transLock) {
						try {
							if (!dealCodeType.contains(trans.getTransaction_code())) {
								dealCodeType.add(trans.getTransaction_code());
								List<PendingDelegationCache> plistCache = pendingDelegationCacheService.getAllByUserId(0, trans.getTransaction_code(), uid);
								for (PendingDelegationCache pendingDelegationCache : plistCache) {
									String lock = pendingDelegationCacheService.lock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id());
									if (!StringUtil.isEmpty(lock)) {
										try {
											pendingDelegationCacheService.cancelPendingCache_one(pendingDelegationCache, currBqt.getTrade_coefficient(), true);
										} catch (Exception e) {
											e.printStackTrace();
										} finally {
											if (pendingDelegationCache != null) {
												pendingDelegationCacheService.unLock(pendingDelegationCache.getTransaction_code(), pendingDelegationCache.getDelegation_id(), lock);
											}
										}
									}
								}
								
								List<PendingDelegation> plist = pendingDelegationService.getAllByUserId(0, trans.getTransaction_code(), uid);
								for (PendingDelegation pendingDelegation : plist) {
									String lock = pendingDelegationService.lock(pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id());
									if (!StringUtil.isEmpty(lock)) {
										try {
											pendingDelegationService.cancelPending_one(pendingDelegation, currBqt.getTrade_coefficient(), true);
										} catch (Exception e) {
											e.printStackTrace();
										} finally {
											if (pendingDelegation != null) {
												pendingDelegationService.unLock(pendingDelegation.getTransaction_code(), pendingDelegation.getDelegation_id(), lock);
											}
										}
									}
								}
							}
							
							trans = bqMemberTransactionService.get(trans.getMt_id());
							
							if (trans != null && trans.getHold_count().compareTo(BigDecimal.ZERO) > 0 && trans.getLock_count().compareTo(BigDecimal.ZERO) == 0) {
								// 以指定价格挂平仓单
								BigDecimal multiple = trans.getTrade_multiple();
								BigDecimal dp = trans.getHold_count().divide(trans.getHold_count().add(trans.getLock_count()), 8, BigDecimal.ROUND_HALF_UP)
										.multiply(trans.getDeposit());
	
								int direction = Contents.DIRECTION_SELL;
								BigDecimal amount_client = trans.getHold_count();
								BigDecimal price_client =  price.multiply(BigDecimal.ONE.subtract(userMonitor.getPrice_rate()));
								if (trans.getTrade_type_id() == Contents.DIRECTION_SELL) {
									price_client =  price.multiply(BigDecimal.ONE.add(userMonitor.getPrice_rate()));
									direction = Contents.DIRECTION_BUY;
								}
								
								price_client = price_client.setScale(digits, BigDecimal.ROUND_HALF_UP);
								
								BigDecimal feeRate = userlevel.getEat_commission_rate();
								BQTransactionFeeRate transactionFeeRate = bqTransactionFeeRateService.getByCodeAndLevel(trans.getTransaction_code(), userlevel.getLevel_id());
								if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
									feeRate = transactionFeeRate.getEat_commission_rate();
								}
								
								PendingDelegationCache cahce = new PendingDelegationCache(trans.getTransaction_id(), trans.getTransaction_code(),
										direction, amount_client, price_client, new BigDecimal(0), amount_client, multiple.intValue(), dp, 
										feeRate, Contents.PRIORITY_PC, uid, BigDecimal.ZERO, BigDecimal.ZERO);
								
								if (trans.getTrade_type_id() == Contents.DIRECTION_BUY) {
									pendingDelegationCacheService.insertPCD(cahce, trans.getMt_id(), currBqt.getTrade_coefficient(), Contents.DEVICE_SERVER_MONITOR, "");
								} else {
									pendingDelegationCacheService.insertPCK(cahce, trans.getMt_id(), currBqt.getTrade_coefficient(), Contents.DEVICE_SERVER_MONITOR, "");
								}
							}
						
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (trans != null) {
								bqMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
							}
						}
					}
				}
			}
		}
	}
}
