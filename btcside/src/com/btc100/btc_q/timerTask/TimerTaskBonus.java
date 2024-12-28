package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQBonusRecord;
import com.btc100.btc_q.entity.BQBonusSetting;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQIeoAccount;
import com.btc100.btc_q.entity.BQMineSetting;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQBonusRecordService;
import com.btc100.btc_q.service.BQBonusSettingService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQIeoAccountService;
import com.btc100.btc_q.service.BQMemberInviteRebateLogService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.service.TradeLogService;

public class TimerTaskBonus {
	
	@Autowired
	private BQIeoAccountService bqIeoAccountService;
	@Autowired
	private BQBonusRecordService bqBonusRecordService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQMemberInviteRebateLogService bqMemberInviteRebateLogService;
	@Autowired
	private BQBonusSettingService bqBonusSettingService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private TradeLogService tradeLogService;
	@Autowired
	private SpotTradeLogService spotTradeLogService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQTransactionService bqTransactionService;
	
	public void run() {
		
		Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		time.set(Calendar.HOUR_OF_DAY, 12);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		
		long endTime = time.getTimeInMillis() / 1000;
		time.add(Calendar.DATE, -1);
		long beginTime = time.getTimeInMillis() / 1000;
		
		BQCurrencyType bfx = bqCurrencyTypeService.getByTypeName("BFX");
		if (bfx == null) return;

		BQBonusSetting bonusSetting = bqBonusSettingService.get();
		if (bonusSetting == null || !bonusSetting.getRunning()) return;
		
		long nowTime = System.currentTimeMillis() / 1000;
		if (bonusSetting.getBegin_time() > nowTime) {
			return;
		}
		
//		if (!bonusSetting.getFirst_done()) {
//			bqBonusSettingService.firstDone(bonusSetting.getId());
//			beginTime = setting.getBegin_time();
//		}
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(beginTime * 1000);
		String day = sdf.format(date);
		
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
		}
		
		BigDecimal allMount = null;
		List<BQAccount> accounts = null;
		List<BQIeoAccount> ieoAccounts = null;
		
		
		
		// USDT分红
		do {
//			BigDecimal minToday = bqMineRecordService.sumAmountBetweenTime(0, beginTime, endTime);
//			if (minToday.compareTo(BigDecimal.ZERO) > 0) {
//				// 解冻ieo
//				unlockIeoAccount(setting, minToday, beginTime, endTime, day);
//			}
			
			// 用户分红
			// 合约交易挖矿手续费
//			BigDecimal feeToday = bqMineRecordService.sumFeeBetweenTime(0, beginTime, endTime);	// 从挖矿记录中获取手续费总额
			BigDecimal feeToday = BigDecimal.ZERO;
			if (feeToday.compareTo(BigDecimal.ZERO) <= 0) {
				List<BQTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
				if (lstTrads == null) {
					lstTrads = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
					spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, lstTrads);
				}
				
				// 从交易记录里获取手续费总额
				for (BQTransaction trans : lstTrads) {
					BigDecimal fee = tradeLogService.findFeeByTimeCode(trans.getTransaction_code(), 0,  beginTime, endTime);
					feeToday = feeToday.add(fee);
				}
			}
			
			// 币币交易手续费
			for (BQSpotMarketCoinPair coinPair : coinPairs) {
				if (coinPair.getCoin_id() == Contents.TRADE_CURRENCY_TYPE_ID) {
					try {
						BigDecimal fee = spotTradeLogService.findFeeByTimeCode(coinPair.getCoin_pair_name(), 0, Contents.DIRECTION_BUY, beginTime, endTime);
						feeToday = feeToday.add(fee);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (coinPair.getMarket_coin_id() == Contents.TRADE_CURRENCY_TYPE_ID) {
					try {
						BigDecimal fee = spotTradeLogService.findFeeByTimeCode(coinPair.getCoin_pair_name(), 0, Contents.DIRECTION_SELL, beginTime, endTime);
						feeToday = feeToday.add(fee);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			
			if (feeToday.compareTo(BigDecimal.ZERO) <= 0) break;
			
			BigDecimal rebateToday = bqMemberInviteRebateLogService.getSumRebateByTime(beginTime, endTime, 0, Contents.TRADE_CURRENCY_TYPE_ID);
			feeToday = feeToday.subtract(rebateToday);
			if (feeToday.compareTo(BigDecimal.ZERO) <= 0) break;
			
			allMount = BigDecimal.ZERO;
			accounts = bqAccountService.findAboveZero(bfx.getType_id());
			for (BQAccount account : accounts) {
				allMount = allMount.add(account.getBtc().add(account.getBtc_locked()));
			}
			ieoAccounts = bqIeoAccountService.findAboveZero();
			for (BQIeoAccount account : ieoAccounts) {
				allMount = allMount.add(account.getUnlocked());
			}
			
			if (allMount.compareTo(BigDecimal.ZERO) <= 0) return;
			doBonus(feeToday, allMount, Contents.TRADE_CURRENCY_TYPE_ID, day, accounts, ieoAccounts);
		} while (false);
		
		
		if (allMount == null) {
			allMount = BigDecimal.ZERO;
			accounts = bqAccountService.findAboveZero(bfx.getType_id());
			for (BQAccount account : accounts) {
				allMount = allMount.add(account.getBtc().add(account.getBtc_locked()));
			}
			ieoAccounts = bqIeoAccountService.findAboveZero();
			for (BQIeoAccount account : ieoAccounts) {
				allMount = allMount.add(account.getUnlocked());
			}
			
			if (allMount.compareTo(BigDecimal.ZERO) <= 0) return;
		}
		
		// USDT外的其他币种分红
		List<BQCurrencyType> currencyTypes = bqCurrencyTypeService.findAll();
		for (BQCurrencyType currencyType : currencyTypes) {
			if (currencyType.getType_id() == Contents.TRADE_CURRENCY_TYPE_ID) continue;
			
			BigDecimal feeToday = BigDecimal.ZERO;
			for (BQSpotMarketCoinPair coinPair : coinPairs) {
				if (coinPair.getCoin_id() == currencyType.getType_id()) {
					try {
						BigDecimal fee = spotTradeLogService.findFeeByTimeCode(coinPair.getCoin_pair_name(), 0, Contents.DIRECTION_BUY, beginTime, endTime);
						feeToday = feeToday.add(fee);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (coinPair.getMarket_coin_id() == currencyType.getType_id()) {
					try {
						BigDecimal fee = spotTradeLogService.findFeeByTimeCode(coinPair.getCoin_pair_name(), 0, Contents.DIRECTION_SELL, beginTime, endTime);
						feeToday = feeToday.add(fee);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			
			if (feeToday.compareTo(BigDecimal.ZERO) <= 0) continue;
			
			BigDecimal rebateToday = bqMemberInviteRebateLogService.getSumRebateByTime(beginTime, endTime, 0, currencyType.getType_id());
			feeToday = feeToday.subtract(rebateToday);
			if (feeToday.compareTo(BigDecimal.ZERO) <= 0) continue;
			
			doBonus(feeToday, allMount, currencyType.getType_id(), day, accounts, ieoAccounts);
		}
		
	}
	
	private void doBonus(BigDecimal feeToday, BigDecimal allMount, int currencyId, String day, List<BQAccount> accounts, List<BQIeoAccount> ieoAccounts) {

		if (allMount.compareTo(BigDecimal.ZERO) <= 0) return;
		
		BigDecimal bonusRate = feeToday.divide(allMount, 13, BigDecimal.ROUND_HALF_DOWN).setScale(12, BigDecimal.ROUND_DOWN);
		if (bonusRate.compareTo(BigDecimal.ZERO) <= 0) return;
		
		int dealCount = 0;
		for (BQAccount account : accounts) {
			BigDecimal balance = account.getBtc().add(account.getBtc_locked());
			if (balance.compareTo(BigDecimal.ZERO) <= 0) continue;
			
			BigDecimal bonus = balance.multiply(bonusRate).setScale(8, BigDecimal.ROUND_DOWN);
			if (bonus.compareTo(new BigDecimal(0.00000001)) <= 0) continue;
			
			BQBonusRecord record = new BQBonusRecord();
			record.setCurrency_type_id(currencyId);
			record.setAmount(bonus);
			record.setDay(day);
			record.setF_user_id(account.getF_user_id());
			record.setHold_amount(account.getBtc());
			record.setFrozen_amount(account.getBtc_locked());
			record.setRate(bonusRate);
			record.setTotal_amount(allMount);
			record.setTotal_fee(feeToday);
			try {
				bqBonusRecordService.bonus(record);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			
			dealCount++;
			if (dealCount == 100) {
				dealCount = 0;
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		for (BQIeoAccount account : ieoAccounts) {
			BigDecimal unlock = account.getUnlocked();
			if (unlock.compareTo(BigDecimal.ZERO) <= 0) continue;
			
			BigDecimal bonus = unlock.multiply(bonusRate).setScale(8, BigDecimal.ROUND_DOWN);
			if (bonus.compareTo(new BigDecimal(0.00000001)) < 0) continue;
			
			BQBonusRecord record = new BQBonusRecord();
			record.setCurrency_type_id(currencyId);
			record.setAmount(bonus);
			record.setDay(day);
			record.setF_user_id(account.getF_user_id());
			record.setHold_amount(unlock);
			record.setFrozen_amount(BigDecimal.ZERO);
			record.setRate(bonusRate);
			record.setTotal_amount(allMount);
			record.setTotal_fee(feeToday);
			record.setNote("IEO解冻分红");
			try {
				bqBonusRecordService.bonus(record);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unused")
	private BigDecimal unlockIeoAccount(BQMineSetting setting, BigDecimal minToday, long beginTime, long endTime, String day) {

		BigDecimal unlockRate = minToday.divide(setting.getTotal_amount(), 13, BigDecimal.ROUND_HALF_DOWN).setScale(12, BigDecimal.ROUND_DOWN);
		if (unlockRate.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
		
		BigDecimal allUnlock = BigDecimal.ZERO;
		List<BQIeoAccount> accounts = bqIeoAccountService.findAll();
		for (BQIeoAccount account : accounts) {
			BigDecimal amount = account.getInitial().multiply(unlockRate).setScale(8, BigDecimal.ROUND_DOWN);
			if (amount.compareTo(new BigDecimal(0.00000001)) <= 0) continue;
			allUnlock = allUnlock.add(amount);
			
//			try {
//				bqIeoUnlockRecordService.unlockIeo(account.getF_user_id(), setting.getCurrency_type_id(), amount, minToday, unlockRate, day);
//			} catch (ServiceException e) {
//				e.printStackTrace();
//			}
		}
		
		return allUnlock;
	}
}
