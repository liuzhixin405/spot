package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.BQUsdrateService;
import com.btc100.btc_q.service.VMemberTradeDetailService;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 合约 API（交易记录）
 * 
 * @author Administrator
 * 
 */
public class VTimerTaskTradesJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VTransactionTypeService vTransactionTypeService;
	@Autowired
	private VMemberTradeDetailService vMemberTradeDetailService;
	@Autowired
	private VTradeLogService vTradeLogService;
	@Autowired
	private BQUsdrateService bqUsdrateService;
	@Autowired
	private BQTransactionTypeService bqTransTypeService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	
	public void run() {
		List<VTransaction> vTransactionList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (vTransactionList == null) {
			vTransactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, vTransactionList);
		}
		
		JSONArray array = new JSONArray();
		for (VTransaction bq : vTransactionList) {
			String bqCode = bq.getTransaction_code();
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRADE_CODE + bqCode, BTCQMemecacheKeys.TIMES.TRADE_CODE, vMemberTradeDetailService.getAllJsonTopN(bqCode, Contents.TRADE_JSON).toString());
			
			Integer pricePrecision = spyMemcachedClient.get(bq.getTransaction_code() + BTCQMemecacheKeys.V_DIGITS);
			if (pricePrecision == null) {
				pricePrecision = getDigits(bq);
			}
			
			int amountPrecision = Utils.getAmountDigtis(bq.getTransaction_code());
			JSONObject json = new JSONObject();
			json.put("symbol", bq.getTransaction_code());
			json.put("pricePrecision", pricePrecision);
			json.put("amountPrecision", amountPrecision);
			json.put("multiple", bq.getMultiple());
			json.put("showName", bq.getTransaction_code().toUpperCase());
			
			array.add(json);
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.V_SYMBOLS_DATA, BTCQMemecacheKeys.TIMES.ONE_HOUR, array.toString());
		
		tickersData(vTransactionList);
	}
	
	private int getDigits(VTransaction trans) {
		Integer digits = spyMemcachedClient.get(trans.getTransaction_code() + BTCQMemecacheKeys.V_DIGITS);
		if (digits == null) {
			VTransactionType transType = vTransactionTypeService.getTransactionType(trans.getTransaction_type());
			digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			spyMemcachedClient.set(trans.getTransaction_code() + BTCQMemecacheKeys.V_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}
		
		return digits;
	}
	
	private void tickersData(List<VTransaction> transactionList) {
		List<Map<String, Object>> lisMap = new ArrayList<Map<String, Object>>();
		// 获取当前时间的24小时时间（时间戳）
		long yesterdayTime = TimeUtil.getYesterdayTime();
		// 当前开放合约
		for (VTransaction bqt : transactionList) {
			Map<String, Object> map = new HashMap<String, Object>();
			String tranCode = bqt.getTransaction_code();
			String[] typeCodeArray = tranCode.toLowerCase().split("\\d");// '\d' 为正则表达式表示[0-9]数字
			String typeCode = "";
			for (String n : typeCodeArray) {
				typeCode += n;
			}
			
			map.put("tradeType", bqt.getTransaction_type());
			map.put("typeCode", typeCode);
			map.put("time", DateUtil.dateToStr(bqt.getTransaction_end_time(), "MM月dd日"));
			map.put("tranCode", tranCode);
			map.put("transactionType", bqt.getTransaction_type());
			map.put("transactionKind", 0);
			map.put("showName", tranCode.toUpperCase());
			
			Integer digits = getDigits(bqt);
			
			JSONObject bqLastTicker = null;
			String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_BTC + "_" + bqt.getTransaction_code());
			if (!StringUtil.isEmpty(ticker)) {
				bqLastTicker = JSONObject.parseObject(ticker);
			}
			
			double high = 0.0;
			double low = 0.0;
			double vol = 0.0;
			double lastPrice = 0.0;
			double firstPrice = 0.0;
			
			if (bqLastTicker == null) {
				high = vTradeLogService.findHighPrice(bqt.getTransaction_code(), yesterdayTime).setScale(digits, BigDecimal.ROUND_DOWN).doubleValue();
				low = vTradeLogService.findLowPrice(bqt.getTransaction_code(), yesterdayTime).setScale(digits, BigDecimal.ROUND_DOWN).doubleValue();
				String strLast = bqt.getTransaction_code() + BTCQMemecacheKeys.V_BTCLATESTPRICE;
				BigDecimal last = spyMemcachedClient.get(strLast);
				if (last == null) {
					// 从数据库中算出最新成交价 
					last = bqt.getLatest_transaction_price();
				}
				
				if (last == null) last = BigDecimal.ZERO;
				lastPrice = last.doubleValue();
				
				BigDecimal volb = vTradeLogService.findBTCVol(bqt.getTransaction_code(), yesterdayTime);
				volb = volb == null ? new BigDecimal(0) : volb;
				vol = volb.doubleValue();
			} else {

				high = bqLastTicker.getDouble("high");
				low = bqLastTicker.getDouble("low");
				vol = bqLastTicker.getDouble("vol");
				lastPrice = bqLastTicker.getDouble("last");
			}
			
			BigDecimal first = spyMemcachedClient.get(tranCode + BTCQMemecacheKeys.V_FIRST_PRICE);
			if (first == null) {
				first = vTradeLogService.findFirstPrice(bqt.getTransaction_code(), TimeUtil.getTimesmorning());
				spyMemcachedClient.set(tranCode + BTCQMemecacheKeys.V_FIRST_PRICE, BTCQMemecacheKeys.TIMES.TEN_SECOND, first);
			}
			
			if (first != null) {
				firstPrice = first.doubleValue();
			}
			
			BigDecimal usdRate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
			if (StringUtil.isEmpty(usdRate)) {
				usdRate = bqUsdrateService.findUsdRateOnuse(1);
				spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, usdRate);
			}
			BigDecimal lastCnyPrice = new BigDecimal(lastPrice).multiply(usdRate).setScale(digits, BigDecimal.ROUND_HALF_UP);
			
			map.put("lastCnyPrice", lastCnyPrice.doubleValue());
			map.put("firstPrice", firstPrice);
			map.put("lastPrice", lastPrice);
			map.put("highPrice", high);
			map.put("lowPrice", low);
			
			// 涨跌幅
			if (lastPrice <= 0.0) {
				map.put("fudu", "0%");
			} else {
				DecimalFormat df = new DecimalFormat("0.00");
				double rate = 0.0;
				if (firstPrice > 0) {
					rate =  (lastPrice - firstPrice) / firstPrice;
				}
				
				String div = df.format(rate *100);
				map.put("fudu", div.toString() + "%");
			}
			// 成交量
			map.put("vol", vol);

			String indexFudu = spyMemcachedClient.get(BTCQMemecacheKeys.INDEX_FUDU + tranCode);
			if (StringUtil.isEmpty(indexFudu)) {
				map.put("indexFudu", "0.00%");
			} else {
				map.put("indexFudu", indexFudu);
			}
			
			BigDecimal btc_index_price = spyMemcachedClient.get(typeCode);
			if (btc_index_price == null) {
				BQTransactionType transType = bqTransTypeService.getTransTypeByCode(typeCode);
				if (transType == null) {
					btc_index_price = BigDecimal.ZERO;
				} else {
					btc_index_price = bqPriceIndexCacheService.getBtcIndexPrice(transType.getType_code()).getPrice();
				}
			}
			map.put("indexPrice", btc_index_price);
			
			BigDecimal indexCnyPrice = btc_index_price.multiply(usdRate).setScale(digits, BigDecimal.ROUND_HALF_UP);
			map.put("indexCnyPrice", indexCnyPrice.doubleValue());
			
			// 持仓量
			double holdCount = 0;
			BigDecimal hold = spyMemcachedClient.get(tranCode + BTCQMemecacheKeys.V_HOLD_COUNT);
			if (hold == null) {
				double count = vMemberTransactionService.getHoldCount(tranCode);
				if (count > 0) {
					hold = new BigDecimal(count);
					spyMemcachedClient.set(tranCode + BTCQMemecacheKeys.V_HOLD_COUNT, BTCQMemecacheKeys.TIMES.TEN_SECOND, hold);
				}
			}
			
			if (hold != null) holdCount = hold.doubleValue();
			map.put("holdCount", holdCount);
			map.put("tradeAmount", 0);
			lisMap.add(map);
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.V_CONTRACT_BASE, BTCQMemecacheKeys.TIMES.ONE_HOUR, lisMap);
	}
}
