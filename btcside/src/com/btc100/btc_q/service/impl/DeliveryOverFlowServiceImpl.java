package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.btc100.btc_q.dao.DeliveryAccountContractDao;
import com.btc100.btc_q.dao.DeliveryContractAccountLogDao;
import com.btc100.btc_q.dao.DeliveryMemberTransactionDao;
import com.btc100.btc_q.dao.DeliveryMemberTransactionOverloadLogDao;
import com.btc100.btc_q.dao.BQAccountAmountLogDao;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQBfxFeeRecordDao;
import com.btc100.btc_q.dao.BQBfxFeeSettingDao;
import com.btc100.btc_q.dao.DeliveryPendingDelegationCacheDao;
import com.btc100.btc_q.dao.DeliveryPendingLogDao;
import com.btc100.btc_q.dao.DeliveryTradeLogDao;
import com.btc100.btc_q.dao.DeliveryTransactionFeeRateDao;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.DeliveryContractAccountLog;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQBfxFeeRecord;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryMemberTransactionOverloadLog;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryPendingLog;
import com.btc100.btc_q.entity.DeliveryTradeLog;
import com.btc100.btc_q.entity.DeliveryTransactionFeeRate;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.DeliveryOverFlowService;
import com.btc100.btc_q.util.RandomUtil;

@Service("deliveryOverFlowService")
public class DeliveryOverFlowServiceImpl implements DeliveryOverFlowService {
	
	private static final Logger logger = Logger.getLogger(DeliveryOverFlowServiceImpl.class);
	
	@Autowired
	private DeliveryMemberTransactionDao<DeliveryMemberTransaction> deliveryMemberTransactionDao;
	@Autowired
	private DeliveryPendingDelegationCacheDao<DeliveryPendingDelegationCache> deliveryPendingDelegationCacheDao;
	@Autowired
	private DeliveryPendingLogDao<DeliveryPendingLog> deliveryPendingLogDao;
	@Autowired
	private DeliveryMemberTransactionOverloadLogDao<DeliveryMemberTransactionOverloadLog> deliveryMemberTransactionOverloadLogDao;
	@Autowired
	private DeliveryTradeLogDao<DeliveryTradeLog> deliveryTradeLogDao;
	@Autowired
	private DeliveryAccountContractDao<DeliveryAccountContract> deliveryAccountContractDao;
	@Autowired
	private DeliveryContractAccountLogDao<DeliveryContractAccountLog> deliveryContractAccountLogDao;
	@Autowired
	private DeliveryTransactionFeeRateDao<DeliveryTransactionFeeRate> bqTransactionFeeRateDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQBfxFeeSettingDao<BQBfxFeeSetting> bqBfxFeeSettingDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
	@Autowired
	private BQBfxFeeRecordDao<BQBfxFeeRecord> bqBfxFeeRecordDao;
	
	private BigDecimal doBfxFee(String code, BigDecimal fee, BQMember user) {
		if (user == null || user.getF_fee_rate_type() != 1) return null;
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
	
	@Override
	public void overFlowDelegate(int diection, BigDecimal qpj, int digits, DeliveryMemberTransaction trans, BQMemberLevel userLevel, BigDecimal coefficient) throws ServiceException {
		BigDecimal changeRemain = BigDecimal.ZERO;
		if (trans.getF_lockmoney().compareTo(BigDecimal.ZERO) > 0) {
			
			DeliveryAccountContract account = deliveryAccountContractDao.findByUserIDCode(trans.getF_user_id(), trans.getTransaction_code());
			if (account.getF_btc().compareTo(trans.getF_lockmoney()) >= 0) {
				changeRemain = trans.getF_lockmoney();
			} else {
				changeRemain = account.getF_btc();
			}
			
			if (changeRemain.compareTo(BigDecimal.ZERO) != 0) {
				if (deliveryAccountContractDao.updateAmount(trans.getF_user_id(), trans.getTransaction_code(), BigDecimal.ZERO, changeRemain.negate(), BigDecimal.ZERO, BigDecimal.ZERO) <= 0) {
					throw new ServiceException("");
				}
			}
		}
		
		// 上面有进行强平操作，用户持单可能有变，需重新获取
		DeliveryMemberTransaction newMemberTran = deliveryMemberTransactionDao.getMemberTransactionById(trans.getMt_id());
		BigDecimal tradeCount = newMemberTran.getHold_count().add(newMemberTran.getLock_count());
		if (newMemberTran == null || tradeCount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RuntimeException("获取用户持仓为空或持仓为0");	
		}
		
		// 删除该持仓
		deliveryMemberTransactionDao.delete(newMemberTran);
		
		BigDecimal feeRate = userLevel.getEat_commission_rate();
		DeliveryTransactionFeeRate transactionFeeRate = bqTransactionFeeRateDao.getByCodeAndLevel(trans.getTransaction_code(), userLevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}
		
		BigDecimal haveCapital = newMemberTran.getDeposit().add(newMemberTran.getDeposit2()).add(newMemberTran.getF_fromremain())
				.add(newMemberTran.getF_frozenremain()).add(newMemberTran.getF_min_frozenremain()).add(changeRemain);
		BigDecimal bcj = BTCQMath.BCJ(diection, newMemberTran.getAverage_price(), haveCapital,
				feeRate, newMemberTran.getHold_count().add(newMemberTran.getLock_count()), coefficient);	// 爆仓价
		
		int needPendDirection = 0;	// 交易方向
		int needTradeTypeID = 0;	// 交易类型
		
		// 未被强平掉的仓位按爆仓价进行委托
		if (diection == Contents.DIRECTION_BUY) {	// 挂卖单
			bcj = bcj.setScale(digits, RoundingMode.UP);
			needPendDirection = Contents.DIRECTION_SELL;
			needTradeTypeID = Contents.CZLX_DDBC;
		} else if (diection == Contents.DIRECTION_SELL) {	// 挂买单
			bcj = bcj.setScale(digits, RoundingMode.DOWN);
			needPendDirection = Contents.DIRECTION_BUY;
			needTradeTypeID = Contents.CZLX_KTBC;
		}

		if (bcj.compareTo(BigDecimal.ZERO) <= 0) {
			logger.error("爆仓为0, 不予爆仓, 用户ID--" + trans.getF_user_id());
			throw new RuntimeException("爆仓为0, 不予爆仓, 用户ID--" + trans.getF_user_id());
		}
		
		// 爆仓价挂单
		BigDecimal income = bcj.subtract(newMemberTran.getAverage_price()).multiply(tradeCount).multiply(coefficient);
		if (diection == Contents.DIRECTION_SELL) {
			income = income.negate();
		}

		BigDecimal fee = haveCapital.add(income);
		if (fee.compareTo(BigDecimal.ZERO) < 0) fee = BigDecimal.ZERO;
		
		// 扣除资金费(算在盈亏里面)
		income= income.add(newMemberTran.getDeposit2());
		
		BigDecimal deposit = BTCQMath.getDeposit(tradeCount, bcj, newMemberTran.getTrade_multiple(), coefficient);
		DeliveryPendingDelegationCache cache = new DeliveryPendingDelegationCache(newMemberTran.getTransaction_id(), newMemberTran.getTransaction_code(), needPendDirection, tradeCount, bcj, 
				BigDecimal.ZERO, tradeCount,newMemberTran.getTrade_multiple().intValue(), deposit, 
				feeRate, Contents.PRIORITY_BC, newMemberTran.getF_user_id(), BigDecimal.ZERO, newMemberTran.getAverage_price());
		DeliveryPendingDelegationCache resultCache = deliveryPendingDelegationCacheDao.insertCache(cache);	// 挂单
		Long pendingID = 0l;
		if (resultCache != null) {
			pendingID = resultCache.getDelegation_id();
			if (pendingID == 0) {
				logger.error("爆仓挂单未获取到用户的挂单ID, 用户ID--" + newMemberTran.getF_user_id());
			}
		}

		// 挂单日志
		DeliveryPendingLog DeliveryPendingLog = new DeliveryPendingLog();
		DeliveryPendingLog.setAction(Contents.PENDING_LOG_ACTION_QP);	// 强平
		DeliveryPendingLog.setDateline(deliveryPendingLogDao.getUnix_TIMESTAMP());
		DeliveryPendingLog.setDeal_count(BigDecimal.ZERO);
		DeliveryPendingLog.setDelegation_count(cache.getDelegation_count());
		DeliveryPendingLog.setDelegation_id(pendingID);
		DeliveryPendingLog.setDelegation_price(bcj);
		DeliveryPendingLog.setDelegation_time(deliveryPendingLogDao.getUnix_TIMESTAMP());
		DeliveryPendingLog.setDeposit(cache.getDeposit());
		DeliveryPendingLog.setF_user_id(newMemberTran.getF_user_id());
		DeliveryPendingLog.setMultiple(cache.getMultiple());
		DeliveryPendingLog.setTrade_type_id(needTradeTypeID);
		DeliveryPendingLog.setTransaction_code(newMemberTran.getTransaction_code());
		DeliveryPendingLog.setTransaction_id(cache.getTransaction_id());
		DeliveryPendingLog.setUndeal_count(cache.getUndeal_count());
		DeliveryPendingLog.setPending_no(cache.getPending_no());
		DeliveryPendingLog.setAvg_price(bcj);
		DeliveryPendingLog.setDevice(Contents.DEVICE_SERVER_QP);
		deliveryPendingLogDao.insertLog(DeliveryPendingLog);

		// 记录爆仓日志
		DeliveryMemberTransactionOverloadLog olog = new DeliveryMemberTransactionOverloadLog();
		olog.setDateline(deliveryMemberTransactionOverloadLogDao.getUnix_TIMESTAMP());
		olog.setDelegation_id(pendingID);
		olog.setDirection(needPendDirection);
		olog.setF_user_id(newMemberTran.getF_user_id());
		olog.setTrade_count(cache.getDelegation_count());
		olog.setTrade_deposit(newMemberTran.getDeposit());
		olog.setTrade_overload_price(bcj);
		olog.setTrade_force_price(qpj);
		olog.setTrade_price(newMemberTran.getAverage_price());
		olog.setTrade_fee(fee);
		olog.setTransaction_code(newMemberTran.getTransaction_code());
		olog.setTransaction_id(cache.getTransaction_id());
		olog.setBalance(BigDecimal.ZERO);
		olog.setPending_no(cache.getPending_no());
		olog.setF_fromremain(newMemberTran.getF_fromremain().add(newMemberTran.getF_frozenremain()).add(newMemberTran.getF_min_frozenremain()));
		deliveryMemberTransactionOverloadLogDao.save(olog);
		
		// 插入交易记录
		DeliveryTradeLog DeliveryTradeLog = new DeliveryTradeLog(newMemberTran.getTransaction_id(), newMemberTran.getTransaction_code(), needTradeTypeID, needPendDirection, newMemberTran.getAverage_price(), bcj, cache.getDelegation_count(), 
				newMemberTran.getTrade_multiple().intValue(), newMemberTran.getDeposit(), income, fee, newMemberTran.getF_user_id(), cache.getPending_no());
		deliveryTradeLogDao.insertLog(DeliveryTradeLog);
		
		// 记录爆仓数量-系统用户持单
		transInsertOrUpdate(newMemberTran.getTrade_type_id(), newMemberTran.getTransaction_code(), newMemberTran.getTransaction_id(), cache.getDelegation_count());
	}
	
	@Override
	public void deliveryDelegate(BigDecimal price, int digits, DeliveryMemberTransaction trans, BQMemberLevel userLevel, BQMember user, BigDecimal coefficient) throws ServiceException {
		DeliveryMemberTransaction newMemberTran = deliveryMemberTransactionDao.getMemberTransactionById(trans.getMt_id());
		BigDecimal tradeCount = newMemberTran.getHold_count().add(newMemberTran.getLock_count());
		if (newMemberTran == null || tradeCount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RuntimeException("获取用户持仓为空或持仓为0");	
		}
		
		// 删除该持仓
		deliveryMemberTransactionDao.delete(newMemberTran);
		
		BigDecimal feeRate = userLevel.getEat_commission_rate();
		DeliveryTransactionFeeRate transactionFeeRate = bqTransactionFeeRateDao.getByCodeAndLevel(newMemberTran.getTransaction_code(), userLevel.getLevel_id());
		if (transactionFeeRate != null && transactionFeeRate.getEat_commission_rate().compareTo(BigDecimal.ZERO) != 0) {
			feeRate = transactionFeeRate.getEat_commission_rate();
		}

		int needPendDirection = 0;	// 交易方向
		int needTradeTypeID = 0;	// 交易类型
		
		// 未被强平掉的仓位按爆仓价进行委托
		int diection = trans.getTrade_type_id();
		if (diection == Contents.DIRECTION_BUY) {	// 挂卖单
			price = price.setScale(digits, RoundingMode.UP);
			needPendDirection = Contents.DIRECTION_SELL;
			needTradeTypeID = Contents.CZLX_DDJG;
		} else if (diection == Contents.DIRECTION_SELL) {	// 挂买单
			price = price.setScale(digits, RoundingMode.DOWN);
			needPendDirection = Contents.DIRECTION_BUY;
			needTradeTypeID = Contents.CZLX_KTJG;
		}
		
		if (price.compareTo(BigDecimal.ZERO) <= 0) {
			logger.error("交割价为0, 不予交割, 用户ID--" + newMemberTran.getF_user_id());
			throw new RuntimeException("交割价为0, 不予交割, 用户ID--" + newMemberTran.getF_user_id());
		}
		
		// 变化后的保证金
		BigDecimal newDep = BigDecimal.ZERO;
		BigDecimal changeDep = newMemberTran.getDeposit().subtract(newDep);
		
		// 从余额转入的备用金（亏损）
		BigDecimal newRemain = BigDecimal.ZERO;
		BigDecimal newFrozenRemain = BigDecimal.ZERO;
		BigDecimal changeRemain = BigDecimal.ZERO;
		BigDecimal changeFrozenremain = BigDecimal.ZERO;

		newRemain = BigDecimal.ZERO;
		changeRemain = newMemberTran.getF_fromremain().subtract(newRemain);
		
		// 盈亏额
		BigDecimal income = price.subtract(newMemberTran.getAverage_price()).multiply(tradeCount).multiply(coefficient);
		if (diection == Contents.DIRECTION_SELL) {
			income = income.negate();
		}
		
		// 手续费
		BigDecimal fee = BTCQMath.getFee(tradeCount, price, feeRate, coefficient);
		BigDecimal bfxFee = BigDecimal.ZERO;
		if (fee.compareTo(BigDecimal.ZERO) > 0) {
			bfxFee = doBfxFee(newMemberTran.getTransaction_code(), fee, user);
			if (bfxFee != null) {
				fee = BigDecimal.ZERO;
			} else {
				bfxFee = BigDecimal.ZERO;
			}
		}
		
		// 总的变化：保证金+收入-手续费
		BigDecimal accountChangeNum = changeDep.add(changeRemain).add(income).subtract(fee);
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
		
		if (accountChangeNum.signum() < 0 && (changeDep.add(changeRemain).add(income).add(changeFrozenremain)).signum() >= 0) {		// 在爆仓边缘
			accountChangeNum = BigDecimal.ZERO;
		} else if (changeRemain.compareTo(BigDecimal.ZERO) == 0 && accountChangeNum.signum() > 0 && accountChangeNum.compareTo(new BigDecimal(0.000001)) < 0) {
			accountChangeNum = BigDecimal.ZERO;
		}
		
		Long linux_time = deliveryPendingLogDao.getUnix_TIMESTAMP();
		
		// 账户资金变更
		if (accountChangeNum.compareTo(BigDecimal.ZERO) != 0) {
			int flagsellAccount = deliveryAccountContractDao.updateAmount(newMemberTran.getF_user_id(), newMemberTran.getTransaction_code(), BigDecimal.ZERO, accountChangeNum, BigDecimal.ZERO, BigDecimal.ZERO);
			if (flagsellAccount == 0) {
				ServiceException sex = new ServiceException(diection == Contents.DIRECTION_SELL ? "sell" : "buy");
				logger.error("交割合约账户资金变更失败，用户：" + newMemberTran.getF_user_id() + ", 合约: " + newMemberTran.getTransaction_code());
				throw sex;
			}
			
			String strDesip = needTradeTypeID == Contents.CZLX_DDJG ? "多单交割" : "空单交割";
			DeliveryAccountContract accountContract = deliveryAccountContractDao.findByUserIDCode(newMemberTran.getF_user_id(), newMemberTran.getTransaction_code());
			DeliveryContractAccountLog accountlog = new DeliveryContractAccountLog(strDesip + "交易，余额增加", Contents.TRADE_CURRENCY_TYPE_ID, Contents.TRANS_TYPE_JY,accountChangeNum.signum(),
					accountContract.getF_btc(), accountChangeNum, accountContract.getF_btc().add(accountChangeNum), accountContract.getF_lockbtc(), 
					accountContract.getF_lockbtc(), linux_time, newMemberTran.getF_user_id(), RandomUtil.getTrans_no(), 0, accountContract.getF_code());
			deliveryContractAccountLogDao.insertLog(accountlog);
		}
		
		// 挂单日志
		DeliveryPendingLog deliveryPendingLog = new DeliveryPendingLog();
		deliveryPendingLog.setAction(Contents.PENDING_LOG_ACTION_JS);	// 结算
		deliveryPendingLog.setDateline(linux_time);
		deliveryPendingLog.setDeal_count(BigDecimal.ZERO);
		deliveryPendingLog.setDelegation_count(tradeCount);
		deliveryPendingLog.setDelegation_id(0);
		deliveryPendingLog.setDelegation_price(price);
		deliveryPendingLog.setDelegation_time(linux_time);
		deliveryPendingLog.setDeposit(newMemberTran.getDeposit());
		deliveryPendingLog.setF_user_id(newMemberTran.getF_user_id());
		deliveryPendingLog.setMultiple(newMemberTran.getTrade_multiple().intValue());
		deliveryPendingLog.setTrade_type_id(needTradeTypeID);
		deliveryPendingLog.setTransaction_code(newMemberTran.getTransaction_code());
		deliveryPendingLog.setTransaction_id(newMemberTran.getTransaction_id());
		deliveryPendingLog.setUndeal_count(BigDecimal.ZERO);
		deliveryPendingLog.setPending_no(RandomUtil.getPendingNo());
		deliveryPendingLog.setAvg_price(price);
		deliveryPendingLog.setDevice(Contents.DEVICE_SERVER_DELIVERY);
		deliveryPendingLogDao.insertLog(deliveryPendingLog);
		
		// 插入交易记录 加入类型：交割
		DeliveryTradeLog deliveryTradeLog = new DeliveryTradeLog(newMemberTran.getTransaction_id(), newMemberTran.getTransaction_code(), needTradeTypeID, needPendDirection, newMemberTran.getAverage_price(), price, tradeCount, 
				newMemberTran.getTrade_multiple().intValue(), newMemberTran.getDeposit(), income, fee, newMemberTran.getF_user_id(), deliveryPendingLog.getPending_no());
		deliveryTradeLog.setTrade_bfx_fee(bfxFee);
		deliveryTradeLogDao.insertLog(deliveryTradeLog);
	}
	

	private void transInsertOrUpdate(int typeid, String scode, long icode, BigDecimal amount) {
		long uid = Contents.BQMEMBER_ADMIN;
		String selectHQL = "FROM DeliveryMemberTransaction b WHERE f_user_id=:uid AND trade_type_id=:type AND transaction_code=:tranid";
		Map<String, Object> selectMap = new HashMap<String, Object>();
		selectMap.put("uid", uid);
		selectMap.put("type", typeid);
		selectMap.put("tranid", scode);
		List<DeliveryMemberTransaction> trans = deliveryMemberTransactionDao.find(selectHQL, selectMap);

		if (trans != null && trans.size() > 0) {
			String hql = "UPDATE DeliveryMemberTransaction b SET b.hold_count=b.hold_count+:amount WHERE mt_id=:id";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("amount", amount);
			map.put("id", trans.get(0).getMt_id());

			deliveryMemberTransactionDao.updateCondition(hql, map);
		} else {
			DeliveryMemberTransaction o = new DeliveryMemberTransaction();

			o.setAverage_price(BigDecimal.ZERO);
			o.setCommission(BigDecimal.ZERO);
			o.setDeposit(BigDecimal.ZERO);
			o.setDeposit2(BigDecimal.ZERO);
			o.setF_user_id(uid);
			o.setHold_count(amount);
			o.setLock_count(BigDecimal.ZERO);
			o.setTrade_type_id(typeid);
			o.setTrade_multiple(BigDecimal.ZERO);
			o.setTransaction_code(scode);
			o.setTransaction_id(icode);
			o.setF_type_forjs(typeid == Contents.DIRECTION_BUY ? 1 : -1);

			deliveryMemberTransactionDao.save(o);
		}
	}

}
