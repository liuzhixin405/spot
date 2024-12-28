package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQDataBroadCastSetting;
import com.btc100.btc_q.entity.BQDataBroadCastStats;
import com.btc100.btc_q.service.BQDataBroadCastSettingService;
import com.btc100.btc_q.service.BQDataBroadCastStatsService;
import com.btc100.btc_q.service.BQMemberTransactionService;

public class TimerTaskDataBroadCast {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQDataBroadCastSettingService bqSettingService;
	@Autowired
	private BQDataBroadCastStatsService bqStatsService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;

	public void run() {

		long currTime = System.currentTimeMillis() / 1000;
		long lastDayTime = currTime - 86400;

		List<BQDataBroadCastStats> dataStatsList = new ArrayList<BQDataBroadCastStats>();

		List<Object[]> buySumHold = bqMemberTransactionService.findSumHold(Contents.DIRECTION_BUY);
		for (Object[] obj : buySumHold) {
			String code = (String) obj[0];
			BQDataBroadCastSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.DATA_BROAD_CAST_SETTING + code);
			if (setting == null) {
				setting = bqSettingService.getByCode(code);
				if (setting != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.DATA_BROAD_CAST_SETTING + code,
							BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
				}
			}
			if (setting == null || !setting.getRunning()) {
				continue;
			}

			BQDataBroadCastStats stats = new BQDataBroadCastStats();
			stats.setF_code(code);
			stats.setRecord_time(currTime);

			BigDecimal multiple = setting.getMagnified_multiple();
			BigDecimal buyHoldCount = (BigDecimal) obj[1];
			buyHoldCount = buyHoldCount.multiply(multiple);
			stats.setBuy_hold_count(buyHoldCount);

			int eliteCount = setting.getElite_count();
			BigDecimal eliteBuyHold = bqMemberTransactionService.getSumEliteHold(code, Contents.DIRECTION_BUY,
					eliteCount);
			BigDecimal eliteSellHold = bqMemberTransactionService.getSumEliteHold(code, Contents.DIRECTION_SELL,
					eliteCount);
			eliteBuyHold = eliteBuyHold.multiply(multiple);
			eliteSellHold = eliteSellHold.multiply(multiple);
			stats.setElite_buy_hold_count(eliteBuyHold);
			stats.setElite_sell_hold_count(eliteSellHold);

			dataStatsList.add(stats);
		}

		List<Object[]> sellSumHold = bqMemberTransactionService.findSumHold(Contents.DIRECTION_SELL);
		for (Object[] obj : sellSumHold) {
			String code = (String) obj[0];
			BQDataBroadCastSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.DATA_BROAD_CAST_SETTING + code);
			if (setting == null) {
				setting = bqSettingService.getByCode(code);
				if (setting != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.DATA_BROAD_CAST_SETTING + code,
							BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
				}
			}
			if (setting == null || !setting.getRunning()) {
				continue;
			}

			BigDecimal multiple = setting.getMagnified_multiple();
			BigDecimal sellHoldCount = (BigDecimal) obj[1];
			sellHoldCount = sellHoldCount.multiply(multiple);
			for (BQDataBroadCastStats stats : dataStatsList) {
				if (code.equals(stats.getF_code())) {
					stats.setSell_hold_count(sellHoldCount);
					break;
				}
			}
		}

		for (BQDataBroadCastStats stats : dataStatsList) {
			BigDecimal sumCount = stats.getBuy_hold_count().add(stats.getSell_hold_count());
			stats.setElite_buy_ratio(stats.getElite_buy_hold_count().divide(sumCount, 8, BigDecimal.ROUND_HALF_UP));
			stats.setElite_sell_ratio(stats.getElite_sell_hold_count().divide(sumCount, 8, BigDecimal.ROUND_HALF_UP));

			BigDecimal totalChange = BigDecimal.ZERO;
			BQDataBroadCastStats lastDataStats = spyMemcachedClient
					.get(BTCQMemecacheKeys.DATA_BROAD_CAST + stats.getF_code());
			if (lastDataStats == null) {
				lastDataStats = bqStatsService.getByCodeAndTime(stats.getF_code(), lastDayTime);
			}
			if (lastDataStats != null) {
				totalChange = sumCount.subtract(lastDataStats.getBuy_hold_count())
						.subtract(lastDataStats.getSell_hold_count());
			}
			stats.setTotal_change(totalChange);

			bqStatsService.save(stats);
			spyMemcachedClient.set(BTCQMemecacheKeys.DATA_BROAD_CAST + stats.getF_code(),
					BTCQMemecacheKeys.TIMES.TWO_DAY, stats);
		}
	}
}
