package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQHourlyAccount;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQHourlyAccountService;

public class TimerTaskStatsAccount {

	private static final Logger logger = Logger.getLogger(TimerTaskStatsAccount.class);

	@Autowired
	private BQHourlyAccountService bqHourlyAccountService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;

	public void run() {
		try {
			Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			time.set(Calendar.MINUTE, 0);
			time.set(Calendar.SECOND, 0);
			time.set(Calendar.MILLISECOND, 0);
			long hourTime = time.getTimeInMillis() / 1000;

			// 每隔一小时统计钱包各币种（btc、eth等）的数量并存入表（bq_hourly_account）中
			List<BQCurrencyType> coins = bqCurrencyTypeService.findAll();
			for (BQCurrencyType coin : coins) {
				String coinName = coin.getType_name();
				if (coinName.equalsIgnoreCase("BFX") || coinName.equalsIgnoreCase("BFC"))
					continue;

				int coinId = coin.getType_id();
				List<Integer> userIds = bqAccountService.findUserIdAboveZero(coinId);
				for (Integer userId : userIds) {
					long uId = userId.longValue();
					BQAccount statsAccount = bqAccountService.getAccount(uId, coinId);
					BigDecimal sumAmount = statsAccount.getBtc().add(statsAccount.getBtc_locked());

					BQHourlyAccount account = new BQHourlyAccount();
					account.setUser_id(statsAccount.getF_user_id());
					account.setCurrency_amount(sumAmount);
					account.setCurrency_id(coin.getType_id());
					account.setHour_time(hourTime);
					bqHourlyAccountService.save(account);
				}
			}

		} catch (Exception e) {
			logger.info("每小时统计钱包账户异常。。。。");
			e.printStackTrace();
		}
	}

}
