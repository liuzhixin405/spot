package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQSpotBetterTokenArbitrageSetting;
import com.btc100.btc_q.service.BQSpotBetterTokenArbitrageSettingService;
import com.btc100.btc_q.util.BetterTokenApiUtil;
import com.btc100.btc_q.util.StringUtil;

/**
 * BetterToken BFXUSDT刷单（交易）
 * @author win7
 *
 */
public class SpotTimerTaskBetterTokenScalping {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotBetterTokenArbitrageSettingService bqSpotArbitrageSettingService;
	
	Logger logger = Logger.getLogger(SpotTimerTaskBetterTokenScalping.class);
	
	private static Map<String, Integer> countDown = new HashMap<String, Integer>();
	private static BetterTokenApiUtil apiUtil = null;
	private static String[] marketArray = null;
	private static String orderType = "limit";
	
	public void run() {
		
		if (marketArray == null) {
			String contracts = UrlConfig.getValue("betterToken.markets");
			if (!StringUtil.isEmpty(contracts)) {
				marketArray = contracts.split(",");
			}
		}
		
		if (marketArray == null || marketArray.length == 0) return;
		
		if (countDown.size() == 0) {
			for (String market : marketArray) {
				countDown.put(market, 0);
			}
		}
		
		if (apiUtil == null) {
			apiUtil = new BetterTokenApiUtil(UrlConfig.getValue("BetterTokenSecretKey"),
					UrlConfig.getValue("BetterTokenAccessKey"), UrlConfig.getValue("BetterTokenPwdKey"));
		}
		
		for (String symbol : countDown.keySet()) {
			BQSpotBetterTokenArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.BT_ARBITRAGE_SETTING);
			if (setting == null) {
				setting = bqSpotArbitrageSettingService.getArbitrageSetting();
				if (setting == null) {
					setting = new BQSpotBetterTokenArbitrageSetting();
					setting.setRunning(false);
					setting.setScalping(false);
				}
				
				spyMemcachedClient.set(BTCQMemecacheKeys.BT_ARBITRAGE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
			
			if (setting != null && setting.getRunning() && setting.getScalping() && setting.getPrice_digits() >= 0
					&& setting.getAmount_digits() >= 0) {
				
				int value = countDown.get(symbol);
				if (value == 0) {
					int sleepTime = 1000;
					do {
						int digits = setting.getPrice_digits();
						int amountDigits = setting.getAmount_digits();
						
						int min = setting.getScalping_frequency_min();
						int max = setting.getScalping_frequency_max();
						int num = min + (int)(Math.random() * (max - min));
						if (num == 0) {
							sleepTime = (60 + 30) * 1000;
						} else {
							sleepTime = (int)(60000.0 / num);
						}
						
						int addi = (int)(Math.random() * 20) - 10;
						sleepTime += (addi * 1000);
						if (sleepTime < 1000) {
							sleepTime = 1000;
						}
						
						BigDecimal random = new BigDecimal(Math.random());
						BigDecimal amount = setting.getScalping_min_each().add(random.multiply(setting.getScalping_max_each().subtract(setting.getScalping_min_each())));
						amount = amount.setScale(amountDigits, BigDecimal.ROUND_DOWN);
						
						BigDecimal sell1 = BigDecimal.ZERO;
						BigDecimal buy1 = BigDecimal.ZERO;
						try {
							String ticker = apiUtil.getTicker(symbol);
							if (StringUtil.isEmpty(ticker)) {
								break;
							}
							JSONObject obj = new JSONObject(ticker);
							sell1 = new BigDecimal(obj.getString("sell"));
							buy1 = new BigDecimal(obj.getString("buy"));
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						if (setting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0
								&& sell1.subtract(buy1).compareTo(setting.getScalping_depth_range().multiply(buy1)) > 0) {
							break;
						}
						
						BigDecimal minChange = new BigDecimal(1.0 / Math.pow(10, digits));
						minChange = minChange.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
						if (sell1.subtract(buy1).abs().compareTo(minChange) <= 0) {
							break;
						}
						
						BigDecimal high = sell1.subtract(minChange);
						BigDecimal low = buy1.add(minChange);
						
						if (high.compareTo(low) < 0 || high.subtract(low).compareTo(low.multiply(new BigDecimal(0.05))) > 0) {
							continue;
						}
						
						BigDecimal price = low.add(new BigDecimal(Math.random()).multiply(high.subtract(low)));
						price = price.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
						
						if (sell1.compareTo(price) == 0) {
							price = sell1.subtract(minChange);
						}
						
						if (buy1.compareTo(price) == 0) {
							price = buy1.add(minChange);
						}
						
						price = price.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
						if (sell1.compareTo(price) == 0 || buy1.compareTo(price) == 0) {
							continue;
						}
						
						try {
							double randomDir = Math.random();
							JSONObject buy = apiUtil.order(symbol, Contents.DIRECTION_BUY, orderType, amount, price);
							if (!"success".equals(buy.getString("status"))) {
								logger.info("TokenBetter 刷单出错，方向：" + (randomDir >= 0.5 ? Contents.DIRECTION_BUY :Contents.DIRECTION_SELL) + ",数量：" + amount + ",价格：" + price);
							}
							JSONObject sell = apiUtil.order(symbol, randomDir >= 0.5 ? Contents.DIRECTION_SELL :Contents.DIRECTION_BUY, orderType, amount, price);
							if (!"success".equals(sell.getString("status"))) {
								logger.info("TokenBetter 刷单出错，方向：卖" + (randomDir >= 0.5 ? Contents.DIRECTION_SELL :Contents.DIRECTION_BUY) + ",数量：" + amount + ",价格：" + price);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} while (false);
					value = sleepTime / 1000 - 1;
				} else {
					value -= 1;
				}
				
				if (value < 0) value = 0;
				countDown.put(symbol, value);
			}
		}
	}

}
