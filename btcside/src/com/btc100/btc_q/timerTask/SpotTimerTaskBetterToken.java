package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.BetterTokenAccount;
import com.btc100.btc_q.vo.BetterTokenOrder;

/**
 * BetterToken BFXUSDT深度挂单
 * 
 * @author win7
 *
 */
public class SpotTimerTaskBetterToken {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotBetterTokenArbitrageSettingService bqSpotArbitrageSettingService;

	Logger logger = Logger.getLogger(SpotTimerTaskBetterToken.class);

	private static BetterTokenApiUtil apiUtil = null;
	private static String[] marketArray = null;
	private static String orderType = "limit";
	private static String source = "openapi";
	private final static BigDecimal ZERO = BigDecimal.ZERO;

	private static Map<String, Integer> speedMap = new HashMap<>();
	private static Map<String, Integer> countDown = new HashMap<String, Integer>();

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

		for (String symbol : marketArray) {
			BQSpotBetterTokenArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.BT_ARBITRAGE_SETTING);
			if (setting == null) {
				setting = bqSpotArbitrageSettingService.getArbitrageSetting();
				if (setting == null) {
					setting = new BQSpotBetterTokenArbitrageSetting();
					setting.setRunning(false);
					setting.setScalping(false);
				}

				spyMemcachedClient.set(BTCQMemecacheKeys.BT_ARBITRAGE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND,
						setting);
			}

			if (setting == null || !setting.getRunning()) {
				return;
			}

			// 涨跌幅邮件提醒
			String reminder = spyMemcachedClient.get("bt_reminder_email");
			if (reminder == null && setting.isReminder_open()) {
				emailReminder(setting, symbol);
			}
			
			// 1)、重置盘口，先挂单，后撤单
			if (setting.isDepth_reset_open_price() && setting.getOpen_price().compareTo(ZERO) > 0
					&& setting.getPrice_digits() >= 0 && setting.getAmount_digits() >= 0) {
				logger.info("reset ...");
				reset(setting, symbol);
				continue;
			}

			// 2)反手挂单
			if (setting.isDepth_backhand() && setting.getPrice_digits() >= 0 && setting.getAmount_digits() >= 0) {
				logger.info("backhand ...");
				backhand(setting, symbol);
//				continue;
			}
			
			if (setting != null && setting.getRunning() && setting.getScalping() && setting.getPrice_digits() >= 0
					&& setting.getAmount_digits() >= 0) {
				
				int value = countDown.get(symbol);
				if (value == 0) {
					int sleepTime = 1000;
					do {
						int min = setting.getScalping_frequency_min();
						int max = setting.getScalping_frequency_max();
						int num = min + (int)(Math.random() * (max - min));
						if (num == 0) {
							sleepTime = (60 + 30) * 50;
						} else {
							sleepTime = (int)(3000.0 / num);
						}
						
						int addi = (int)(Math.random() * 20) - 10;
						sleepTime += (addi * 50);
						if (sleepTime < 50) {
							sleepTime = 50;
						}
						
						scalaing(setting, symbol);
					} while (false);
					value = sleepTime / 50 - 1;
				} else {
					value -= 1;
				}
				
				if (value < 0) value = 0;
				countDown.put(symbol, value);
			}

			// 3)循环挂撤单
			if (setting.getCycle_time() == 0 || setting.getPrice_digits() <= 0 || setting.getAmount_digits() <= 0) {
				continue;
			}
			Integer number = 0;
			if (speedMap.containsKey(symbol)) {
				number = speedMap.get(symbol);
			}
			if (number % (setting.getCycle_time() * 10) != 0) { // cycle_time = 1，表示间隔0.5秒
				number++;
				speedMap.put(symbol, number);
				continue;
			}
			number = 1;
			speedMap.put(symbol, number);

			logger.info("start cycle ...");
			cycle(setting, symbol);
		}
	}

	private void emailReminder(final BQSpotBetterTokenArbitrageSetting setting, String symbol) {

		try {
			String ticker = apiUtil.getTicker(symbol);
			if (StringUtil.isEmpty(ticker)) {
				return;
			}
			if (setting.getVolatility().compareTo(ZERO) <= 0) {
				return;
			}
			String message = "";
			JSONObject obj = new JSONObject(ticker);
			BigDecimal rate = new BigDecimal(obj.getString("changeRate24"));
			rate = rate.abs();
			if (rate.compareTo(setting.getVolatility()) >= 0) {
				DecimalFormat df = new DecimalFormat("0.000");
				double changeRate = Double.parseDouble(obj.getString("changeRate24"));
				String changeRate24 = df.format(changeRate * 100) + "%";
				message += "BetterToken平台，XKD_USDT涨跌幅度超过设定值，达到：" + changeRate24;
			}

			if (!StringUtil.isEmpty(message)) {
				final String content = message;
				Runnable runnable = new Runnable() {
					public void run() {
						try {
							String email = setting.getReminder_email();
							SendMail themail = new SendMail();
							themail.send(email, content);
						} catch (Exception e) {
						}
					}
				};
				Thread thread = new Thread(runnable);
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		spyMemcachedClient.set("bt_reminder_email", BTCQMemecacheKeys.TIMES.ONE_DAY, symbol);
	}

	private void reset(BQSpotBetterTokenArbitrageSetting setting, String symbol) {
		List<Long> cancelIds = new ArrayList<Long>();
		try {
			List<BetterTokenOrder> orders = apiUtil.getOrders(symbol, orderType, source, 0);
			if (orders != null && orders.size() > 0) {
				for (BetterTokenOrder order : orders) {
					cancelIds.add(order.getId());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		// 挂单，若深度数50，则买卖各挂50
		BigDecimal openPrice = setting.getOpen_price();
		if (openPrice.compareTo(ZERO) <= 0)
			return;

		Map<BigDecimal, BigDecimal> depthSells = new HashMap<>();
		Map<BigDecimal, BigDecimal> depthBuys = new HashMap<>();

		BigDecimal buy1 = openPrice.subtract(setting.getDepth_buy_price_gap());
		BigDecimal sell1 = openPrice.add(setting.getDepth_sell_price_gap());

		BigDecimal random = new BigDecimal(Math.random());
		BigDecimal buyAmount = setting.getDepth_buy_amount_min()
				.add(random.multiply(setting.getDepth_buy_amount_max().subtract(setting.getDepth_buy_amount_min())))
				.setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);

		random = new BigDecimal(Math.random());
		BigDecimal sellAmount = setting.getDepth_sell_amount_min()
				.add(random.multiply(setting.getDepth_sell_amount_max().subtract(setting.getDepth_sell_amount_min())))
				.setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);

		depthSells.put(sell1, sellAmount);
		depthBuys.put(buy1, buyAmount);
		for (int i = 1; i < setting.getDepth_size(); i++) {
			BigDecimal buyGap = setting.getDepth_buy_price_gap().multiply(new BigDecimal(i));
			BigDecimal sellGap = setting.getDepth_sell_price_gap().multiply(new BigDecimal(i));
			BigDecimal sell = sell1.add(sellGap).setScale(setting.getPrice_digits(), BigDecimal.ROUND_UP);
			BigDecimal buy = buy1.subtract(buyGap).setScale(setting.getPrice_digits(), BigDecimal.ROUND_DOWN);

			random = new BigDecimal(Math.random());
			buyAmount = setting.getDepth_buy_amount_min()
					.add(random.multiply(setting.getDepth_buy_amount_max().subtract(setting.getDepth_buy_amount_min())))
					.setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);

			random = new BigDecimal(Math.random());
			sellAmount = setting.getDepth_sell_amount_min()
					.add(random
							.multiply(setting.getDepth_sell_amount_max().subtract(setting.getDepth_sell_amount_min())))
					.setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);

			depthSells.put(sell, sellAmount);
			depthBuys.put(buy, buyAmount);
		}

		addAllPending(depthBuys, symbol, Contents.DIRECTION_BUY);
		addAllPending(depthSells, symbol, Contents.DIRECTION_SELL);

		setting = bqSpotArbitrageSettingService.getArbitrageSetting();
		setting.setDepth_reset_open_price(false);
		bqSpotArbitrageSettingService.update(setting);

		spyMemcachedClient.set(BTCQMemecacheKeys.BT_ARBITRAGE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);

		spyMemcachedClient.set("better_token_balance_BFX", BTCQMemecacheKeys.TIMES.ONE_DAY, ZERO);
		if (cancelIds.size() > 0) {
			try {
				String result = apiUtil.cancelOrdersByIds(symbol, cancelIds);
				System.out.println(result);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	private void scalaing(BQSpotBetterTokenArbitrageSetting setting, String symbol) {
		int digits = setting.getPrice_digits();
		int amountDigits = setting.getAmount_digits();
		BigDecimal random = new BigDecimal(Math.random());
		BigDecimal amount = setting.getScalping_min_each().add(random.multiply(setting.getScalping_max_each().subtract(setting.getScalping_min_each())));
		amount = amount.setScale(amountDigits, BigDecimal.ROUND_DOWN);
		
		BigDecimal sell1 = BigDecimal.ZERO;
		BigDecimal buy1 = BigDecimal.ZERO;
		try {
			String ticker = apiUtil.getTicker(symbol);
			if (StringUtil.isEmpty(ticker)) {
				return;
			}
			JSONObject obj = new JSONObject(ticker);
			sell1 = new BigDecimal(obj.getString("sell"));
			buy1 = new BigDecimal(obj.getString("buy"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (setting.getScalping_depth_range().compareTo(BigDecimal.ZERO) > 0
				&& sell1.subtract(buy1).compareTo(setting.getScalping_depth_range().multiply(buy1)) > 0) {
			return;
		}
		
		BigDecimal minChange = new BigDecimal(1.0 / Math.pow(10, digits));
		minChange = minChange.setScale(digits, BigDecimal.ROUND_HALF_DOWN);
		if (sell1.subtract(buy1).abs().compareTo(minChange) <= 0) {
			return;
		}
		
		BigDecimal high = sell1.subtract(minChange);
		BigDecimal low = buy1.add(minChange);
		
		if (high.compareTo(low) < 0 || high.subtract(low).compareTo(low.multiply(new BigDecimal(0.05))) > 0) {
			return;
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
			return;
		}
		
		try {
			logger.info("TokenBetter 刷单,数量：" + amount + ",价格：" + price);
			double randomDir = Math.random();
			JSONObject buy = apiUtil.order(symbol, randomDir >= 0.5 ? Contents.DIRECTION_BUY :Contents.DIRECTION_SELL, orderType, amount, price);
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
	}

	private void backhand(BQSpotBetterTokenArbitrageSetting setting, String symbol) {
		String coin = "BFX";
		BigDecimal cacheBalance = spyMemcachedClient.get("better_token_balance_" + coin);
		
		try {
			BetterTokenAccount account = apiUtil.getAccount(coin);
			if (account == null || !account.getSymbol().equals(coin)) {
				return;
			}
			BigDecimal nowAvailable = new BigDecimal(account.getAvailable());
			if (cacheBalance == null || cacheBalance.compareTo(ZERO) == 0) {
				spyMemcachedClient.set("better_token_balance_" + coin, BTCQMemecacheKeys.TIMES.ONE_DAY, nowAvailable);
				return;
			}
			BigDecimal diff = nowAvailable.subtract(cacheBalance);
			
			int direction = Contents.DIRECTION_BUY;
			if (diff.signum() == 0) {// BFX余额不变
				return;
			} else if (diff.signum() == 1) {// BFX增加，说明买单被成交，反手挂卖单
				direction = Contents.DIRECTION_SELL;
			} else {// BFX减少，说明卖单被成交，挂反手买单
				direction = Contents.DIRECTION_BUY;
			}
			
			String ticker = apiUtil.getTicker(symbol);
			if (StringUtil.isEmpty(ticker)) {
				return;
			}
			JSONObject obj = new JSONObject(ticker);
			
			BigDecimal price = new BigDecimal(obj.getString("close"));
			BigDecimal amount = ZERO;
			if (direction == Contents.DIRECTION_BUY) {
				BigDecimal random = new BigDecimal(Math.random());
				amount = setting.getDepth_buy_amount_min()
						.add(random.multiply(setting.getDepth_buy_amount_max().subtract(setting.getDepth_buy_amount_min())))
						.setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);
			} else {
				BigDecimal random = new BigDecimal(Math.random());
				amount = setting.getDepth_sell_amount_min()
						.add(random.multiply(setting.getDepth_sell_amount_max().subtract(setting.getDepth_sell_amount_min())))
						.setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);
			}
			
			diff = diff.abs();
			if (diff.compareTo(amount) >= 0) {
				while (diff.compareTo(amount) >= 0) {
					diff = diff.subtract(amount);
					
					if (direction == Contents.DIRECTION_BUY) {
						price = price.subtract(setting.getDepth_sell_backhand_gap())
								.setScale(setting.getPrice_digits(), BigDecimal.ROUND_DOWN);
					} else {
						price = price.add(setting.getDepth_buy_backhand_gap())
								.setScale(setting.getPrice_digits(), BigDecimal.ROUND_UP);
					}
					JSONObject result = apiUtil.order(symbol, direction, orderType, amount, price);
					logger.info("BetterToken 反手挂单，方向：" + direction + ",数量：" + amount + ",价格：" + price);
					if (!"success".equals(result.getString("status"))) {
						logger.info("BetterToken 反手挂单出错，方向：" + direction + ",数量：" + amount + ",价格：" + price);
					}
				}
				if (diff.compareTo(ZERO) > 0) {
					if (direction == Contents.DIRECTION_SELL) {// BFX挂卖单，卖出（n*amount）之后，剩余diff未（卖出）挂单，现有余额=本次余额-diff
						diff = diff.negate();
					}
					nowAvailable = nowAvailable.add(diff);
				}
				spyMemcachedClient.set("better_token_balance_" + coin, BTCQMemecacheKeys.TIMES.ONE_DAY, nowAvailable);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private void cycle(BQSpotBetterTokenArbitrageSetting setting, String symbol) {
		try {
			List<Long> cancelIds = new ArrayList<Long>();

			Map<BigDecimal, BigDecimal> depthSells = new HashMap<>();
			Map<BigDecimal, BigDecimal> depthBuys = new HashMap<>();

			List<BetterTokenOrder> orders = apiUtil.getOrders(symbol, orderType, source, 0);
			if (orders.size() <= 0) {
				return;
			}

			int cycle_buy_size = setting.getCycle_buy_size();
			int cycle_sell_size = setting.getCycle_sell_size();
			List<BetterTokenOrder> buys = new ArrayList<BetterTokenOrder>();
			List<BetterTokenOrder> sells = new ArrayList<BetterTokenOrder>();

			for (BetterTokenOrder order : orders) {
				if ("buy".equals(order.getSide())) {
					buys.add(order);
				} else {
					sells.add(order);
				}
			}
			if (cycle_buy_size > buys.size()) {
				logger.info("买单循环挂撤条数大于盘口条数");
				cycle_buy_size = buys.size();
			}
			if (cycle_sell_size > sells.size()) {
				logger.info("卖单循环挂撤条数大于盘口条数");
				cycle_sell_size = sells.size();
			}

			sort(buys, "DESC");
			buys = buys.subList(0, cycle_buy_size);
			for (BetterTokenOrder order : buys) {
				cancelIds.add(order.getId());
				BigDecimal price = new BigDecimal(order.getEntrustPrice());
				BigDecimal amount = new BigDecimal(order.getOpenAmount());
				BigDecimal half = setting.getDepth_buy_amount_max().subtract(setting.getDepth_buy_amount_min())
						.divide(new BigDecimal(2)).setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);
				BigDecimal diff = amount.subtract(setting.getDepth_buy_amount_min());
				if (diff.compareTo(half) > 0) {
					diff = diff.subtract(half.multiply(new BigDecimal(Math.random())));
				} else {
					diff = diff.add(half.multiply(new BigDecimal(Math.random())));
				}
				amount = setting.getDepth_buy_amount_min().add(diff).setScale(setting.getAmount_digits(),
						BigDecimal.ROUND_HALF_UP);
				depthBuys.put(price, amount);
			}

			sort(sells, "ASC");
			sells = sells.subList(0, cycle_sell_size);
			for (BetterTokenOrder order : sells) {
				cancelIds.add(order.getId());

				BigDecimal price = new BigDecimal(order.getEntrustPrice());
				BigDecimal amount = new BigDecimal(order.getOpenAmount());
				BigDecimal half = setting.getDepth_sell_amount_max().subtract(setting.getDepth_sell_amount_min())
						.divide(new BigDecimal(2)).setScale(setting.getAmount_digits(), BigDecimal.ROUND_HALF_UP);
				BigDecimal diff = amount.subtract(setting.getDepth_sell_amount_min());
				if (diff.compareTo(half) > 0) {
					diff = diff.subtract(half.multiply(new BigDecimal(Math.random())));
				} else {
					diff = diff.add(half.multiply(new BigDecimal(Math.random())));
				}
				amount = setting.getDepth_sell_amount_min().add(diff).setScale(setting.getAmount_digits(),
						BigDecimal.ROUND_HALF_UP);
				depthSells.put(price, amount);
			}
			addAllPending(depthBuys, symbol, Contents.DIRECTION_BUY);
			addAllPending(depthSells, symbol, Contents.DIRECTION_SELL);

			apiUtil.cancelOrdersByIds(symbol, cancelIds);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private void addAllPending(Map<BigDecimal, BigDecimal> orders, String symbol, int direction) {
		List<Long> newOrders = new ArrayList<Long>();
		for (BigDecimal price : orders.keySet()) {
			BigDecimal amount = orders.get(price);
			try {
				JSONObject obj = apiUtil.order(symbol, direction, orderType, amount, price);
				if ("success".equals(obj.getString("status"))) {
					String orderId = obj.getString("orderId");
					newOrders.add(Long.getLong(orderId));
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		spyMemcachedClient.set("better_token_old_orders", BTCQMemecacheKeys.TIMES.ONE_MIN, newOrders);
	}
	
	/**
	 * 
	 * @param orders
	 * @param sort
	 *            ASC升序，DESC降序
	 */
	private static void sort(List<BetterTokenOrder> orders, final String sort) {
		Collections.sort(orders, new Comparator<BetterTokenOrder>() {
			@Override
			public int compare(BetterTokenOrder o1, BetterTokenOrder o2) {
				// o2-o1 降序， o1-o2升序
				if ("ASC".equals(sort)) {
					return new Double(o1.getEntrustPrice()).compareTo(new Double(o2.getEntrustPrice()));// 价格升序
				} else if ("DESC".equals(sort)) {
					return new Double(o2.getEntrustPrice()).compareTo(new Double(o1.getEntrustPrice()));// 价格降序
				} else {
					return new Double(o2.getEntrustPrice()).compareTo(new Double(o1.getEntrustPrice()));
				}
			}

		});
	}
}
