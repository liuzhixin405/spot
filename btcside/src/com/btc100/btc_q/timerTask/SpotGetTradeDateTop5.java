package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQUsdrateService;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.Top5DelegationVO;
import com.btc100.btc_q.vo.TradeAjaxVO;

/**
 * 获取市场挂单top5
 * 
 * @author Administrator
 * 
 */
public class SpotGetTradeDateTop5 {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private SpotTradeLogService spotTradeLogService;
	@Autowired
	private  BQUsdrateService bqUsdrateService;
	
	private static String[] marketArray = null;

	public void run() {
		
		if (marketArray == null) {
			String contracts = UrlConfig.getValue("spotTrade.markets");
			if (!StringUtil.isEmpty(contracts)) {
				marketArray = contracts.split(",");
			}
		}
		
		if (marketArray == null || marketArray.length == 0) return;
		
		List<BQSpotMarketCoinPair> coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
		spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		
		for (String market : marketArray) {
			BQSpotMarketCoinPair coinPair = null;
			for (BQSpotMarketCoinPair marketCoinPair : coinPairs) {
				if (marketCoinPair.getCoin_pair_name().equals(market)) {
					coinPair = marketCoinPair;
					break;
				}
			}
			
			if (coinPair == null) continue;
			
			getCommonData(coinPair);
		}

	}
	
	private void getCommonData(BQSpotMarketCoinPair coinPair) {
		
		String code = coinPair.getCoin_pair_name();
		
		// 获取当前最新成交价
		BigDecimal lastPrice = spyMemcachedClient.get(code + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			spyMemcachedClient.set(code + BTCQMemecacheKeys.BTCLATESTPRICE, BTCQMemecacheKeys.TIMES.BTCLATESTPRICE, 
					coinPair.getLatest_price());
			lastPrice = coinPair.getLatest_price();
		} else {
			BQSpotMarketCoinPair bqt_temp = bqSpotMarketCoinPairService.getByName(code);
			if (lastPrice.compareTo(bqt_temp.getLatest_price()) != 0) {
				
				BigDecimal highPrice = bqt_temp.getHighest_price();
				BigDecimal changeHigh = BigDecimal.ZERO;
				if (lastPrice.compareTo(highPrice) > 0) {
					changeHigh = lastPrice;
				}
				
				BigDecimal lowPrice = bqt_temp.getLowest_price();
				BigDecimal changeLow = BigDecimal.ZERO;
				if (lastPrice.compareTo(lowPrice) < 0 || lowPrice.signum() <= 0) {
					changeLow = lastPrice;
				}
				
				bqSpotMarketCoinPairService.updateHighAndLowAndLast(coinPair.getId(), changeHigh, changeLow, lastPrice);
			}
		}

		// 获取委托前5单
		List<Top5DelegationVO> buytop5List = spyMemcachedClient.get(code + BTCQMemecacheKeys.BUYTOP5LIST);
		List<Top5DelegationVO> selltop5List = spyMemcachedClient.get(code + BTCQMemecacheKeys.SELLTOP5LIST);
		if (buytop5List == null) buytop5List = new ArrayList<>();
		if (selltop5List == null) selltop5List = new ArrayList<>();

		TradeAjaxVO vo = new TradeAjaxVO();
		vo.setLastPrice(lastPrice);
		vo.setBuytop5List(buytop5List);
		vo.setSelltop5List(selltop5List);
		vo.setShowName(code.toUpperCase());
		
		BigDecimal cnyRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
		if (StringUtil.isEmpty(cnyRate)) {
			cnyRate = bqUsdrateService.findUsdRateOnuse(1);
			if (cnyRate == null) cnyRate = new BigDecimal(7);
			spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, cnyRate);
		}
		vo.setCnyRate(cnyRate.doubleValue());
		
		Integer digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.DIGITS);
		if (digits == null) {
			digits = Utils.getDigitsFromMinFloat(coinPair.getMin_float_price());
			spyMemcachedClient.set(code + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}
		vo.setDigits(digits);
		vo.setAmountDigits(coinPair.getDecimals());
		vo.setSuspend(coinPair.getSuspend());
		
		JSONObject bqLastTicker = null;
		String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + code);
		if (!StringUtil.isEmpty(ticker)) {
			bqLastTicker = JSONObject.parseObject(ticker);
		}
		
		BigDecimal first = spyMemcachedClient.get(code + BTCQMemecacheKeys.FIRST_PRICE);
		if (first == null) {
			first = spotTradeLogService.findFirstPrice(code, TimeUtil.getTimesmorning());
			spyMemcachedClient.set(code + BTCQMemecacheKeys.FIRST_PRICE, BTCQMemecacheKeys.TIMES.BPI, first);
		}
		
		double firstPrice = 0.0;
		if (first != null) {
			firstPrice = first.doubleValue();
		}
		// 涨跌幅
		if (lastPrice.doubleValue() <= 0.0) {
			vo.setFudu("0%");
		} else {
			DecimalFormat df = new DecimalFormat("0.00");
			double rate = 0.0;
			if (firstPrice > 0) {
				rate =  (lastPrice.doubleValue() - firstPrice) / firstPrice;
			}
			
			String div = df.format(rate *100);
			vo.setFudu(div.toString() + "%");
		}
		
		if (bqLastTicker == null) {
			long time = TimeUtil.getYesterdayTime();
			BigDecimal high = spotTradeLogService.findHighPrice(code, time).setScale(digits, BigDecimal.ROUND_DOWN);
			BigDecimal low = spotTradeLogService.findLowPrice(code, time).setScale(digits, BigDecimal.ROUND_DOWN);
			
			BigDecimal vol = spotTradeLogService.findBTCVol(code, time);
			vol = vol == null ? new BigDecimal(0) : vol;
			
			BigDecimal trades = spotTradeLogService.findTradeBTC(code, time);
			trades = (trades == null ? BigDecimal.ZERO : trades);

			BigDecimal buy = new BigDecimal(0);
			BigDecimal sell = new BigDecimal(0);
			
			if (high.doubleValue() < lastPrice.doubleValue() || high.doubleValue() == 0) {
				high = lastPrice;
			}
			
			if (low.doubleValue() > lastPrice.doubleValue() || low.doubleValue() == 0) {
				low = lastPrice;
			}

			high = high.setScale(digits, BigDecimal.ROUND_DOWN);
			low = low.setScale(digits, BigDecimal.ROUND_DOWN);
			buy = buy.setScale(digits, BigDecimal.ROUND_DOWN);
			sell = sell.setScale(digits, BigDecimal.ROUND_DOWN);
			vol = vol.setScale(digits, BigDecimal.ROUND_DOWN);
			trades = trades.setScale(digits, BigDecimal.ROUND_DOWN);
			
			bqLastTicker = new JSONObject();
			bqLastTicker.put("bqCode", code);
			bqLastTicker.put("high", high);
			bqLastTicker.put("low", low);
			bqLastTicker.put("buy", 0);
			bqLastTicker.put("sell", 0);
			bqLastTicker.put("last", lastPrice.doubleValue());
			bqLastTicker.put("vol", vol);
			bqLastTicker.put("trades", trades);
			
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_BTC + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_BTC, bqLastTicker.toString());
		}
		
		Double high = bqLastTicker.getDouble("high");
		Double low = bqLastTicker.getDouble("low");
		Double vol = bqLastTicker.getDouble("vol");
		Double trades = bqLastTicker.getDouble("trades");
		
		boolean updateTicker = false;
		if (high < lastPrice.doubleValue() || high == 0) {
			high = lastPrice.doubleValue();
			updateTicker = true;
		}
		
		if (low > lastPrice.doubleValue() || low == 0) {
			low = lastPrice.doubleValue();
			updateTicker = true;
		}
		
		if (updateTicker) {
			bqLastTicker.put("high", high);
			bqLastTicker.put("low", low);
			bqLastTicker.put("last", lastPrice.doubleValue());
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_BTC + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_BTC, bqLastTicker.toString());
		}
		
		vo.setTodayHigh(new BigDecimal(high + "").setScale(digits, BigDecimal.ROUND_DOWN)); 	// 今日最高
		vo.setTodayLow(new BigDecimal(low + "").setScale(digits, BigDecimal.ROUND_DOWN)); 	// 今日最低
		vo.setTradevols(new BigDecimal(vol + "").setScale(4, BigDecimal.ROUND_DOWN)); 		// 因为定时器里面获取的成交量是24小时前的成交量，时间不对照，所以此方法不用memcache
		vo.setTrades(new BigDecimal(trades + "").setScale(4, BigDecimal.ROUND_DOWN));

		spyMemcachedClient.set(BTCQMemecacheKeys.COMMON_DATA + code, BTCQMemecacheKeys.TIMES.ONE_HOUR, vo);
	}
}
