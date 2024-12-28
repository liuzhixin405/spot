package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQBfxMinHold;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQBfxFeeSettingService;
import com.btc100.btc_q.service.BQBfxMinHoldService;
import com.btc100.btc_q.service.SpotMemberTradeDetailService;
import com.btc100.btc_q.util.TimeUtil;

/**
 * 获取一天内用户平台币（BFX）最少持仓
 * @author Administrator
 *
 */
public class TimerTaskBfxBalanceCheck {

	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;
	@Autowired
	private BQBfxMinHoldService bqBfxMinHoldService;
	@Autowired
	private SpotMemberTradeDetailService spotMemberTradeDetailService;
	
	public void run() {
		
		BQBfxFeeSetting setting = bqBfxFeeSettingService.get();
		if (setting == null) return;
		
		long dayTime = TimeUtil.getTimesmorning();
		
		if (setting.getUpdate_pirce_time() != dayTime) {
			BigDecimal avgPrice = spotMemberTradeDetailService.findAvgPriceBetweenTime("bfxusdt", dayTime - 24 * 3600, dayTime);
			if (avgPrice.compareTo(BigDecimal.ZERO) == 0) {
				if (setting.getBfx_avg_price().compareTo(BigDecimal.ZERO) <= 0) {
					Integer tradeTime = spotMemberTradeDetailService.getLatesetTradeTime("bfxusdt", dayTime);
					if (tradeTime != null) {
						long time = tradeTime;
						time = TimeUtil.getTimesmorning(time * 1000);
						avgPrice = spotMemberTradeDetailService.findAvgPriceBetweenTime("bfxusdt", time, time + 24 * 3600);
					}
				}
			}
			
			avgPrice = avgPrice.setScale(3,  BigDecimal.ROUND_DOWN);
			if (avgPrice.compareTo(BigDecimal.ZERO) > 0) {
				setting.setBfx_avg_price(avgPrice);
			}
			
			setting.setUpdate_pirce_time(dayTime);
			bqBfxFeeSettingService.update(setting);
		}
		
		int coinId = setting.getCoin_id();
		List<Object[]> balanceObjs = bqAccountService.findBalance(coinId);
		for (Object[] obj : balanceObjs) {
			long userId = (Integer) obj[0];
			BigDecimal balance = (BigDecimal) obj[2];
			
			BQBfxMinHold minHold = bqBfxMinHoldService.getBfxMinHoldByUserAndTime(userId, dayTime);
			if (minHold == null) {
				minHold = new BQBfxMinHold();
				minHold.setCoin_id(coinId);
				minHold.setF_user_id(userId);
				minHold.setDay_time(dayTime);
				minHold.setMin_hold(balance);
				
				bqBfxMinHoldService.save(minHold);
			} else {
				if (minHold.getMin_hold().compareTo(balance) > 0) {
					minHold.setMin_hold(balance);
					bqBfxMinHoldService.update(minHold);
				}
			}
		}
	}
}
