package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.btc100.btc_q.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQFundingFeeSetting;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.FundingFeeRate;
import com.btc100.btc_q.entity.FundingFeeRecord;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQFundingFeeSettingService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.FundingFeeRateService;
import com.btc100.btc_q.service.FundingFeeRecordService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.TradeLogService;
import com.btc100.btc_q.util.TimeUtil;

public class TimerTaskFundingFee {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private BQFundingFeeSettingService bqFundingFeeSettingService;
	@Autowired
	private FundingFeeRateService fundingFeeRateService;
	@Autowired
	private FundingFeeRecordService fundingFeeRecordService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private TradeLogService tradeLogService;
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	
	private static String[] contractArray = null;

	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.contracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		Random r = new Random();
		int sleepTime = r.nextInt(5000) + 1;
		
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
		}
		
		long currTime = (System.currentTimeMillis() / 1000 + 1) / 60 * 60;
		for (String code : contractArray) {
			
			BQTransaction bq = null;
			for (BQTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equals(code)) {
					bq = bqTransaction;
					break;
				}
			}
			
			if (bq == null) {
				continue;
			}
			
			BQFundingFeeSetting setting = bqFundingFeeSettingService.getFundingFeeSettingByCode(code);
			if (setting == null || !setting.getRunning()) {
				continue;
			}
			
			BigDecimal indexPrice = spyMemcachedClient.get(code);
			if (indexPrice == null) {
				BQPriceIndexCache bqPriceIndexCache = bqPriceIndexCacheService.getBtcIndexPrice(bq.getTransaction_type_code());
				if (bqPriceIndexCache != null) {
					indexPrice = bqPriceIndexCache.getPrice();
				} else {
					indexPrice = BigDecimal.ZERO;
				}
			}
			
			if (indexPrice.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			
			PendingDelegation sell1 = pendingDelegationService.getSell1(code);
			PendingDelegation buy1 = pendingDelegationService.getBuy1(code);
			
			if (sell1 == null || buy1 == null) {
				continue;
			}
			
			BigDecimal buy1Price = buy1.getDelegation_price();
			BigDecimal sell1Price = sell1.getDelegation_price();
			
			// 溢价指数 = （（合约买一价+合约卖一价）／2 – 标记价格）／标记价格
			BigDecimal premiumPrice = buy1Price.add(sell1Price).multiply(new BigDecimal(0.5)).subtract(indexPrice).divide(indexPrice, 15, BigDecimal.ROUND_HALF_UP);
			// 资金费率 = 溢价指数/除数 – 利率差
			BigDecimal feeRate = premiumPrice.divide(setting.getDivisor(), 15, BigDecimal.ROUND_HALF_UP).subtract(setting.getInterest_rate_diff());
			
			String timeStr = setting.getRunning_time();
			String[] intervals = timeStr.split(",");
			List<Long> intervalList = new ArrayList<Long>();
			for (String interval : intervals) {
				int hour = Convert.toInt(interval.split(":")[0], -1);
				if (hour < 0) continue;
				
				int minute = Convert.toInt(interval.split(":")[1], -1);
				if (minute < 0) minute = 0;
				
				long morningTime = TimeUtil.getTimesmorning();
				long intervalTime = morningTime + hour * 3600 + minute * 60;
				if (intervalTime >= currTime) {
					intervalList.add(intervalTime);
				}
			}
			
			if (intervalList.size() == 0) {
				for (String interval : intervals) {
					int hour = Convert.toInt(interval.split(":")[0], -1);
					if (hour < 0) continue;
					
					int minute = Convert.toInt(interval.split(":")[1], -1);
					if (minute < 0) minute = 0;
					
					long morningTime = TimeUtil.getTimesmorning();
					long intervalTime = morningTime + hour * 3600 + minute * 60 + 24 * 3600;
					if (intervalTime >= currTime) {
						intervalList.add(intervalTime);
					}
				}
			}
			
			if (intervalList.size() == 0) continue;
			
			// 获取最小那个
			long minTime = intervalList.get(0);
			for (Long time : intervalList) {
				if (time < minTime) {
					minTime = time;
				}
			}
			
			updateFundingRate(code, feeRate, minTime, setting);
			if (minTime == currTime) {
				doFundingFee(setting, code, minTime);
			}
		}
	}
	
	private void doFundingFee(BQFundingFeeSetting setting, String symbol, long time) {
		BigDecimal feeRateVal = BigDecimal.ZERO;
		if (setting.getFixed_fee_open()) {
			feeRateVal = setting.getFixed_fee_rate();
			fundingFeeRateService.clampByTime(symbol, feeRateVal, time);
		} else {
			FundingFeeRate feeRate = fundingFeeRateService.getByTime(symbol, time);
			if (feeRate != null) {
				BigDecimal clampUp = setting.getClamp_up().multiply(new BigDecimal(0.01));
				BigDecimal clampDown = setting.getClamp_down().multiply(new BigDecimal(0.01));
				feeRateVal = feeRate.getFee_rate();
				if (clampUp.compareTo(BigDecimal.ZERO) != 0) {
					if (feeRateVal.compareTo(clampUp) > 0) {
						feeRateVal = clampUp;
					}
				}
				
				if (clampDown.compareTo(BigDecimal.ZERO) != 0) {
					if (feeRateVal.compareTo(clampDown) < 0) {
						feeRateVal = clampDown;
					}
				}
				
				feeRateVal = feeRateVal.setScale(6, BigDecimal.ROUND_HALF_UP);
				fundingFeeRateService.clampByTime(symbol, feeRateVal, time);
			}
		}
		
		// 最新价格
		BigDecimal lastPrice = tradeLogService.findPriceAtTime(symbol, time);
		if (lastPrice.compareTo(BigDecimal.ZERO) <= 0) return;
		
		BQTransaction transaction = bqTransactionService.findTransByCode(symbol);
		
		if (feeRateVal.compareTo(BigDecimal.ZERO) != 0) {
			BigDecimal allPay = BigDecimal.ZERO;
			List<BQMemberTransaction> memberTrans = bqMemberTransactionService.findTransByCode(symbol);
			
			int deduction = Contents.DIRECTION_BUY;
			if (feeRateVal.compareTo(BigDecimal.ZERO) < 0) {
				deduction = Contents.DIRECTION_SELL;
			}
			
			// 扣
			BigDecimal otherSideHolds = BigDecimal.ZERO;
			for (BQMemberTransaction trans : memberTrans) {
				if (trans.getF_user_id() == 1) continue;
				
				BigDecimal holds = trans.getHold_count().add(trans.getLock_count());
				if (holds.compareTo(BigDecimal.ZERO) <= 0) continue;
				
				if (trans.getTrade_type_id() == deduction) {
					BigDecimal posValue = lastPrice.multiply(holds).multiply(transaction.getTrade_coefficient());
					BigDecimal fundingFee = posValue.multiply(feeRateVal).abs().setScale(8, BigDecimal.ROUND_DOWN);
					FundingFeeRecord record = new FundingFeeRecord();
					record.setF_code(symbol);
					record.setCoefficient(transaction.getTrade_coefficient());
					record.setCurr_price(lastPrice);
					record.setDateline(time);
					record.setF_user_id(trans.getF_user_id());
					record.setTrans_type_id(trans.getTrade_type_id());
					record.setFee_rate(feeRateVal);
					record.setFunding_fee(fundingFee.negate());
					record.setHold_count(holds);
					record.setPosition_value(posValue);
					
					try {
						fundingFeeRecordService.doAccountFundingFee(record);
						allPay = allPay.add(fundingFee);
					} catch (ServiceException e) {
						
						try {
							boolean lock = false;
							int tryTimes  = 0;
							while (!lock && tryTimes < 30) {
								tryTimes++;
								lock = bqMemberTransactionService.lockToAdjustSpare(trans.getMt_id());
								Thread.sleep(10);
							}
							
							if (!lock) continue;
							
							try {
								fundingFeeRecordService.doMemberTransFundingFee(record, trans.getMt_id(), transaction.getTrade_coefficient());
								allPay = allPay.add(record.getFunding_fee().abs());
							} catch (Exception e2) {
							} finally {
								bqMemberTransactionService.unlockToAdjustSpare(trans.getMt_id());
							}
							
						} catch (Exception e1) {
						}
					}
				} else {
					otherSideHolds = otherSideHolds.add(holds);
				}
			}
			
			// 加
			BigDecimal payPerHold = allPay.divide(otherSideHolds, 15, BigDecimal.ROUND_DOWN);
			for (BQMemberTransaction trans : memberTrans) {
				if (trans.getF_user_id() == 1) continue;
				
				if (trans.getTrade_type_id() == deduction) {
					continue;
				}
				
				BigDecimal holds = trans.getHold_count().add(trans.getLock_count());
				if (holds.compareTo(BigDecimal.ZERO) <= 0) continue;
				
				BigDecimal fundingFee = holds.multiply(payPerHold).abs();
				
				FundingFeeRecord record = new FundingFeeRecord();
				record.setF_code(symbol);
				record.setCoefficient(transaction.getTrade_coefficient());
				record.setCurr_price(lastPrice);
				record.setDateline(time);
				record.setF_user_id(trans.getF_user_id());
				record.setTrans_type_id(trans.getTrade_type_id());
				record.setFee_rate(feeRateVal);
				record.setFunding_fee(fundingFee);
				record.setHold_count(holds);
				record.setPosition_value(lastPrice.multiply(holds).multiply(transaction.getTrade_coefficient()));
				
				try {
					fundingFeeRecordService.doAccountFundingFee(record);
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void updateFundingRate(String symbol, BigDecimal feeRateValue, long time, BQFundingFeeSetting setting) {
		FundingFeeRate feeRate = fundingFeeRateService.getByTime(symbol, time);
		if (feeRate != null) {
			if (feeRate.getDone() != 1) {
				if (setting.getFixed_fee_open()) {
					if (feeRate.getFee_rate().compareTo(setting.getFixed_fee_rate()) != 0) {
						fundingFeeRateService.updateRate(feeRate, setting.getFixed_fee_rate(), 1);
					}
				} else {
					fundingFeeRateService.updateByTime(symbol, feeRateValue, time);
				}
			}
		} else {
			feeRate = new FundingFeeRate();
			feeRate.setF_code(symbol);
			feeRate.setCount_times(1);
			feeRate.setDateline(time);
			feeRate.setDone(0);
			
			if (setting.getFixed_fee_open()) {
				feeRate.setFee_rate(setting.getFixed_fee_rate());
			} else {
				feeRate.setFee_rate(feeRateValue);
			}
			
			fundingFeeRateService.saveFeeRate(feeRate);
		}
	}
}
