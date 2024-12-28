package com.btc100.btc_q.timerTask;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.util.StringUtil;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQSpotArbitrageSetting;
import com.btc100.btc_q.entity.BQSpotHedgingRecord;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.service.BQSpotArbitrageSettingService;
import com.btc100.btc_q.service.BQSpotHedgingRecordService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.util.HuobiApiUtil;
import com.btc100.btc_q.util.OKexApiUtil;
import com.btc100.btc_q.util.Utils;

public class SpotTimerTaskHedging {

	@Autowired
	private BQSpotHedgingRecordService bqHedgingRecordService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private BQSpotArbitrageSettingService bqSpotArbitrageSettingService;
	
	Logger logger = Logger.getLogger(SpotTimerTaskHedging.class);
	
	private static HuobiApiUtil apiUtil = null; 
	private static OKexApiUtil okecUtil = null; 
	private static String[] marketArray = null;
	
	public void run() {
		
		if (marketArray == null) {
			String contracts = UrlConfig.getValue("spotTrade.markets");
			if (!StringUtil.isEmpty(contracts)) {
				marketArray = contracts.split(",");
			}
		}
		
		if (marketArray == null || marketArray.length == 0) return;
		
		// 交易对冲
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}
		
		for (String market : marketArray) {
			BQSpotMarketCoinPair coinPair = null;
			for (BQSpotMarketCoinPair marketCoinPair : coinPairs) {
				if (marketCoinPair.getCoin_pair_name().equals(market)) {
					coinPair = marketCoinPair;
					break;
				}
			}
			
			if (coinPair == null) continue;
			
			String code = coinPair.getCoin_pair_name();
			BQSpotArbitrageSetting setting = spyMemcachedClient.get(BTCQMemecacheKeys.ARBITRAGE_SETTING + code);
			if (setting == null) {
				setting = bqSpotArbitrageSettingService.getArbitrageSettingByCode(code);
				if (setting == null) {
					setting = new BQSpotArbitrageSetting();
					setting.setRunning(false);
					setting.setScalping(false);
					setting.setHedging(false);
					setting.setRisk_manage(false);
					setting.setDepth_uid(0L);
				}
				
				spyMemcachedClient.set(BTCQMemecacheKeys.ARBITRAGE_SETTING + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, setting);
			}
			
			if (setting.getHedging()) {
				Integer digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.DIGITS);
				if (digits == null) {
					digits = Utils.getDigitsFromMinFloat(coinPair.getMin_float_price());
					spyMemcachedClient.set(code + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
				
				// 外部对冲
				List<BQSpotHedgingRecord> records = bqHedgingRecordService.findRecordsByCodeAndState(code, 0);
				for (BQSpotHedgingRecord record : records) {
					BigDecimal amount = record.getTrade_amount();
					BigDecimal price = record.getTrade_price();
					int direction = record.getDirection();
					String symbol = record.getCoin_name();
					String marketSymbol = record.getMarket_coin_name();
					if (direction == 1) {	// 买入
						price = price.multiply(BigDecimal.ONE.subtract(setting.getHedging_buy_range().multiply(new BigDecimal(0.01))));
						price = price.setScale(digits, BigDecimal.ROUND_DOWN);
					} else {
						price = price.multiply(BigDecimal.ONE.add(setting.getHedging_buy_range().multiply(new BigDecimal(0.01))));
						price = price.setScale(digits, BigDecimal.ROUND_UP);
					}
					
					JSONObject retObj = null;
					String error = "";
					try {
						if (setting.getCurr_use_state() == 0) {
							
							if (record.getCoin_name().equalsIgnoreCase("okb")) {
								if (okecUtil == null) {
									okecUtil = new OKexApiUtil(UrlConfig.getValue("SpotOkexSecretKey"), UrlConfig.getValue("SpotOkexAccessKey"), UrlConfig.getValue("SpotOkexPwd"));
								}
								if (okecUtil != null) {
									retObj = okecUtil.order(symbol, marketSymbol, direction, amount, price);
								}
							} else {
								if (apiUtil == null) {
									try {
										apiUtil = new HuobiApiUtil(UrlConfig.getValue("SpotHuobiSecretKey"), UrlConfig.getValue("SpotHuobiAccessKey"));
										apiUtil.getAccounts();
									} catch (UnsupportedEncodingException | JSONException e) {
										e.printStackTrace();
									}
								}
								
								retObj = apiUtil.order(symbol, marketSymbol, direction, amount, price);
							}
						} else {
							
						}
					} catch (Exception e) {
						error = e.getMessage();
						e.printStackTrace();
					}
					
					if (retObj == null) {
						bqHedgingRecordService.updateState(record.getId(), 2, price, "下单失败: " + error);
					} else {
						try {
							if ("ok".equals(retObj.getString("status"))) {
								bqHedgingRecordService.updateState(record.getId(), 1, price, "订单号: " + retObj.getString("orderId"));
							} else if ("retry".equals(retObj.getString("status"))) {
								bqHedgingRecordService.updateSendTimesAndNote(record.getId(), price, "下单失败: " + retObj.getString("error"));
								BQSpotHedgingRecord rd = bqHedgingRecordService.getRecordById(record.getId());
								if (rd.getSend_times() >= 5) {
									bqHedgingRecordService.updateState(record.getId(), 2, price, record.getNote() + "，下单失败，尝试次数达到5次！！ ");
								}
							} else {
								bqHedgingRecordService.updateState(record.getId(), 2, price, "下单失败: " + retObj.getString("error"));
							}
						} catch (JSONException e) {
							bqHedgingRecordService.updateState(record.getId(), 2, price, "下单失败: " + e.getMessage());
						}
					}
					
				}
			}
			
		}
	}
}
