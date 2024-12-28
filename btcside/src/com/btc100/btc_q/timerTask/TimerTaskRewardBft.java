package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQBftRewardRecord;
import com.btc100.btc_q.entity.BQBftRewardSetting;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQExchangeTicker;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQBftRewardRecordService;
import com.btc100.btc_q.service.BQBftRewardSettingService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQExchangeTickerService;
import com.btc100.btc_q.service.BQHourlyAccountService;
import com.btc100.btc_q.vo.TickerVo;

public class TimerTaskRewardBft {
	
	private static final Logger logger = Logger.getLogger(TimerTaskRewardBft.class);

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQHourlyAccountService bqHourlyAccountService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQBftRewardSettingService bqBftRewardSettingService;
	@Autowired
	private BQBftRewardRecordService bqBftRewardRecordService;
	@Autowired
	private BQExchangeTickerService bqExchangeTickerService;
	
	public void run() {
		try {
			Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			time.set(Calendar.HOUR_OF_DAY, 0);
			time.set(Calendar.MINUTE, 0);
			time.set(Calendar.SECOND, 0);
			time.set(Calendar.MILLISECOND, 0);
			
			long endTime = time.getTimeInMillis() / 1000;
			time.add(Calendar.DATE, -1);
			long startTime = time.getTimeInMillis() / 1000;
			
			// 统计前一天每个整点最少数量币种（例如是btc最少就取btc数量），并且折算（按指数价格）成USD
			List<BQCurrencyType> coins = bqCurrencyTypeService.findAll();
			for (BQCurrencyType coin : coins) {
				String coinName = coin.getType_name();
				if (coinName.equalsIgnoreCase("BFX") || coinName.equalsIgnoreCase("BFC"))
					continue;

				int coinId = coin.getType_id();
				List<Integer> userIds = bqHourlyAccountService.findUserIdOverZero(startTime, endTime, coinId);
				if (userIds != null && userIds.size() > 0) {
					
					for (Integer userId : userIds) {
						long uId = userId.longValue();
						BigDecimal minAmount = bqHourlyAccountService.getMinAmountByTime(startTime, endTime, uId, coinId);
						if (minAmount.compareTo(BigDecimal.ZERO) > 0) {
							BigDecimal price = BigDecimal.ZERO; // 价格指数
							if (!"USDT".equalsIgnoreCase(coinName)) { // 非USDT币种（BTC、ETH等）
								TickerVo ticker = spyMemcachedClient.get(BTCQMemecacheKeys.EXCHANGE_TICKER + coinName); // 从缓存中取出行情
								if (null != ticker) {
									price = ticker.getLast();
								}
								
								if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
									BQExchangeTicker exchangeTicker = bqExchangeTickerService.getExchangeTickerByCurrencyType(coinId);
									if (exchangeTicker != null) {
										price = exchangeTicker.getLast();
									}
								}
							} else {
								price = BigDecimal.ONE;
							}
							BigDecimal rewardAmount = minAmount.multiply(price).setScale(4, BigDecimal.ROUND_DOWN);
							if (rewardAmount.compareTo(BigDecimal.ZERO) > 0) {
								// 返利 doInviteRewardBft()
								doRewardBft(uId, rewardAmount);
							}
						}
					}
					
				}
			}

		} catch (Exception e) {
			logger.info("每日凌晨奖励用户BFC异常。。。。");
			e.printStackTrace();
		}
	}
	
	private void doRewardBft(long userId, BigDecimal amount) throws ServiceException{
		BQBftRewardSetting setting = bqBftRewardSettingService.get();
		BQBftRewardRecord record = new BQBftRewardRecord();
		record.setF_user_id(userId);
		record.setCurrency_type_id(setting.getCurrency_type_id());
		record.setReason(Contents.REWARD_REASON_ACCOUNT);
		record.setAmount(amount);
		record.setNote("每日统计账户奖励");
		bqBftRewardRecordService.bonus(record);
	}
	
}
