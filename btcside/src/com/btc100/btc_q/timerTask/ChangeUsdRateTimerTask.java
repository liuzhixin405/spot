package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQUsdrate;
import com.btc100.btc_q.service.BQUsdrateService;
import com.btc100.btc_q.timerTask.common.CommonDataUsdCny;

/**
 * 改变实时汇率
 * @author Administrator
 *
 */
public class ChangeUsdRateTimerTask {

	@Autowired
	private BQUsdrateService bqUsdrateService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	public void run() {
		// 获取美元汇率(1美元=？人民币)
		CommonDataUsdCny usdcny = new CommonDataUsdCny();
		BigDecimal rate = usdcny.findUsdRate(System.currentTimeMillis());
		if (rate != null && rate.compareTo(BigDecimal.ZERO) > 0) {
			spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, rate);
			
			BQUsdrate usd = bqUsdrateService.findUsdOnuse(Contents.COIN_STATE_ONUSE);
			usd.setEx_rate(rate);
			usd.setEx_time(bqUsdrateService.getTime());
			bqUsdrateService.updateUsdRate(usd);
		}
	}

}
