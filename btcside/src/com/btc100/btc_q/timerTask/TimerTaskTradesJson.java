package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQPriceIndexCache;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.BQUsdrateService;
import com.btc100.btc_q.service.MemberTradeDetailService;
import com.btc100.btc_q.service.TradeLogService;
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
public class TimerTaskTradesJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private BQTransactionTypeService bqTransactionTypeService;
	@Autowired
	private MemberTradeDetailService memberTradeDetailService;
	@Autowired
	private TradeLogService tradeLogService;
	@Autowired
	private BQUsdrateService bqUsdrateService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	
	private static String[] contractArray = null;
	
	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.contracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		
		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
		}
		
		for (String contract : contractArray) {
			BQTransaction bq = null;
			for (BQTransaction bqTransaction : transactionList) {
				if (bqTransaction.getTransaction_code().equals(contract)) {
					bq = bqTransaction;
					break;
				}
			}
			
			if (bq == null) {
				continue;
			}
			
			String bqCode = bq.getTransaction_code(); // 获取合约号
			// 1.搜索成交记录最新的50条数据
			spyMemcachedClient.set(BTCQMemecacheKeys.TRADE_CODE + bqCode, BTCQMemecacheKeys.TIMES.TRADE_CODE, memberTradeDetailService.getAllJsonTopN(bqCode, Contents.TRADE_JSON).toString());
			
			tickersData(bq);
			getBPIData(bq);
			
			Integer pricePrecision = spyMemcachedClient.get(bq.getTransaction_code() + BTCQMemecacheKeys.DIGITS);
			if (pricePrecision == null) {
				pricePrecision = getDigits(bq);
			}
			
			int amountPrecision = Utils.getAmountDigtis(bq.getTransaction_code());
			JSONObject json = new JSONObject();
			json.put("symbol", bq.getTransaction_code());
			json.put("pricePrecision", pricePrecision);
			json.put("amountPrecision", amountPrecision);
			json.put("multiples", bq.getMultiple());
			json.put("showName", bq.getTransaction_show_name());
			json.put("enName", bq.getTransaction_show_name_en());
			json.put("jpName", bq.getTransaction_show_name_jp());
			json.put("krName", bq.getTransaction_show_name_kr());
			json.put("hkName", bq.getTransaction_show_name_hk());
			
			JSONArray array = new JSONArray();
			String symbols = spyMemcachedClient.get(BTCQMemecacheKeys.SYMBOLS_DATA);
			if (symbols == null) {
				array.put(json);
			} else {
				try {
					array = new JSONArray(symbols);
					boolean have = false;
					for (int i = 0; i < array.length(); i++) {
						org.json.JSONObject o = array.getJSONObject(i);
						if (o.getString("symbol").equals(json.getString("symbol"))) {
							have = true;
							o.put("symbol", bq.getTransaction_code());
							o.put("pricePrecision", pricePrecision);
							o.put("amountPrecision", amountPrecision);
							o.put("multiples", bq.getMultiple());
							o.put("showName", bq.getTransaction_show_name());
							o.put("enName", bq.getTransaction_show_name_en());
							o.put("jpName", bq.getTransaction_show_name_jp());
							o.put("krName", bq.getTransaction_show_name_kr());
							o.put("hkName", bq.getTransaction_show_name_hk());
							break;
						}
					}
					
					if (!have) {
						array.put(json);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			spyMemcachedClient.set(BTCQMemecacheKeys.SYMBOLS_DATA, BTCQMemecacheKeys.TIMES.ONE_HOUR, array.toString());
		}
	}
	
	protected int getDigits(BQTransaction trans) {
		Integer digits = spyMemcachedClient.get(trans.getTransaction_code() + BTCQMemecacheKeys.DIGITS);
		if (digits == null) {
			BQTransactionType transType = bqTransactionTypeService.getTransactionType(trans.getTransaction_type());
			digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			spyMemcachedClient.set(trans.getTransaction_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}
		
		return digits;
	}
	
	private void getBPIData(BQTransaction bqt) {
		
		BigDecimal rate = spyMemcachedClient.get(BTCQMemecacheKeys.USD_RATE);
		if (StringUtil.isEmpty(rate)) {
			rate = bqUsdrateService.findUsdRateOnuse(1);
			spyMemcachedClient.set(BTCQMemecacheKeys.USD_RATE,BTCQMemecacheKeys.TIMES.USD_RATE, rate);
		}
		
		JSONArray array = new JSONArray();
		JSONObject obj = new JSONObject();
		BigDecimal price = spyMemcachedClient.get(bqt.getTransaction_type_code());
		if (price == null) return;
		
		obj.put("typeId", bqt.getTransaction_type());
		obj.put("typeCode", bqt.getTransaction_type_code());
		obj.put("last", price.doubleValue());
		BigDecimal cnyPrice = price.multiply(rate);
		obj.put("symbol", bqt.getTransaction_type_code()); // 合约号
		obj.put("usdPrice", price.doubleValue()); // 最新价(美元)
		obj.put("cnyPrice", cnyPrice.doubleValue()); // 最新价(人民币)
		obj.put("showName", bqt.getTransaction_show_name());
		obj.put("enName", bqt.getTransaction_show_name_en());
		obj.put("jpName", bqt.getTransaction_show_name_jp());
		obj.put("krName", bqt.getTransaction_show_name_kr());
		obj.put("hkName", bqt.getTransaction_show_name_kr());
		
		boolean up = true;
		String fudu = spyMemcachedClient.get(BTCQMemecacheKeys.INDEX_FUDU + bqt.getTransaction_type_code());
		if (!StringUtil.isEmpty(fudu) && fudu.contains("-")) {
			up = false;
		}
		
		if (StringUtil.isEmpty(fudu)) {
			fudu = "0.00%";
		}
		
		obj.put("up", up); // 是否上涨
		obj.put("fudu", fudu);
		
		String bpiData = spyMemcachedClient.get(BTCQMemecacheKeys.BPI_DATA);
		if (bpiData == null) {
			array.put(obj);
		} else {
			try {
				array = new JSONArray(bpiData);
				boolean have = false;
				for (int i = 0; i < array.length(); i++) {
					org.json.JSONObject o = array.getJSONObject(i);
					if (o.getString("typeCode").equals(obj.getString("typeCode"))) {
						have = true;
						o.put("typeId", bqt.getTransaction_type());
						o.put("typeCode", bqt.getTransaction_type_code());
						o.put("last", price.doubleValue());
						o.put("symbol", bqt.getTransaction_code()); // 合约号
						o.put("usdPrice", price.doubleValue()); // 最新价(美元)
						o.put("cnyPrice", cnyPrice.doubleValue()); // 最新价(人民币)
						o.put("showName", bqt.getTransaction_show_name());
						obj.put("enName", bqt.getTransaction_show_name_en());
						obj.put("jpName", bqt.getTransaction_show_name_jp());
						obj.put("krName", bqt.getTransaction_show_name_kr());
						obj.put("hkName", bqt.getTransaction_show_name_hk());
						o.put("up", up);
						o.put("fudu", fudu);
						break;
					}
				}
				
				if (!have) {
					array.put(obj);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.BPI_DATA, BTCQMemecacheKeys.TIMES.ONE_HOUR, array.toString());
	}
	
	private void tickersData(BQTransaction bqt) {
		// 获取当前时间的24小时时间（时间戳）
		long yesterdayTime = TimeUtil.getYesterdayTime();
		Map<String, Object> map = new HashMap<String, Object>();
		String tranCode = bqt.getTransaction_code();
		String typeCode = tranCode.toLowerCase();
		map.put("tradeType", bqt.getTransaction_type());
		map.put("typeCode", typeCode);
		map.put("time", DateUtil.dateToStr(bqt.getTransaction_end_time(), "MM月dd日"));
		map.put("tranCode", tranCode);
		map.put("transactionType", bqt.getTransaction_type());
		map.put("transactionKind", bqt.getTransaction_kind());
		map.put("showName", bqt.getTransaction_show_name());
		map.put("enName", bqt.getTransaction_show_name_en());
		map.put("jpName", bqt.getTransaction_show_name_jp());
		map.put("krName", bqt.getTransaction_show_name_kr());
		map.put("hkName", bqt.getTransaction_show_name_hk());
		
		Integer digits = getDigits(bqt);
		
		JSONObject bqLastTicker = null;
		String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_BTC + "_" + bqt.getTransaction_code());
		if (!StringUtil.isEmpty(ticker)) {
			bqLastTicker = JSONObject.parseObject(ticker);
		}
		
		double high = 0.0;
		double low = 0.0;
		double vol = 0.0;
		double lastPrice = 0.0;
		double firstPrice = 0.0;
		
		if (bqLastTicker == null) {
			high = tradeLogService.findHighPrice(bqt.getTransaction_code(), yesterdayTime).setScale(digits, BigDecimal.ROUND_DOWN).doubleValue();
			low = tradeLogService.findLowPrice(bqt.getTransaction_code(), yesterdayTime).setScale(digits, BigDecimal.ROUND_DOWN).doubleValue();
			String strLast = bqt.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE;
			BigDecimal last = spyMemcachedClient.get(strLast);
			if (last == null) {
				// 从数据库中算出最新成交价 
				last = bqt.getLatest_transaction_price();
			}
			
			if (last == null) last = BigDecimal.ZERO;
			lastPrice = last.doubleValue();
			
			BigDecimal volb = tradeLogService.findBTCVol(bqt.getTransaction_code(), yesterdayTime);
			volb = volb == null ? new BigDecimal(0) : volb;
			vol = volb.doubleValue();
		} else {

			high = bqLastTicker.getDouble("high");
			low = bqLastTicker.getDouble("low");
			vol = bqLastTicker.getDouble("vol");
			lastPrice = bqLastTicker.getDouble("last");
		}
		
		BigDecimal first = spyMemcachedClient.get(tranCode + BTCQMemecacheKeys.FIRST_PRICE);
		if (first == null) {
			first = tradeLogService.findFirstPrice(bqt.getTransaction_code(), TimeUtil.getTimesmorning());
			spyMemcachedClient.set(tranCode + BTCQMemecacheKeys.FIRST_PRICE, BTCQMemecacheKeys.TIMES.BPI, first);
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
		
		map.put("firstPrice", firstPrice);
		map.put("lastPrice", lastPrice);
		map.put("lastCnyPrice", lastCnyPrice.doubleValue());
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
			BQPriceIndexCache bqPriceIndexCache = bqPriceIndexCacheService.getBtcIndexPrice(bqt.getTransaction_type_code());
			if (bqPriceIndexCache != null) {
				btc_index_price = bqPriceIndexCache.getPrice();
			} else {
				btc_index_price = BigDecimal.ZERO;
			}
		}
		map.put("indexPrice", btc_index_price);
		
		BigDecimal indexCnyPrice = btc_index_price.multiply(usdRate).setScale(digits, BigDecimal.ROUND_HALF_UP);
		map.put("indexCnyPrice", indexCnyPrice.doubleValue());
		
		// 持仓量
		double holdCount = 0;
		BigDecimal hold = spyMemcachedClient.get(tranCode + BTCQMemecacheKeys.HOLD_COUNT);
		if (hold == null) {
			double count = bqMemberTransactionService.getHoldCount(tranCode);
			if (count > 0) {
				hold = new BigDecimal(count);
				spyMemcachedClient.set(tranCode + BTCQMemecacheKeys.HOLD_COUNT, BTCQMemecacheKeys.TIMES.TEN_SECOND, hold);
			}
		}
		
		if (hold != null) holdCount = hold.doubleValue();
		map.put("holdCount", holdCount);
		map.put("tradeAmount", 0);
		
		List<Map<String, Object>> lisMap = spyMemcachedClient.get(BTCQMemecacheKeys.CONTRACT_BASE); 
		if (lisMap == null) {
			lisMap = new ArrayList<Map<String, Object>>();
		}
		
		for (Map<String, Object> item : lisMap) {
			if (item.get("typeCode").equals(map.get("typeCode"))) {
				lisMap.remove(item);
				break;
			}
		}
		lisMap.add(map);
		
		spyMemcachedClient.set(BTCQMemecacheKeys.CONTRACT_BASE, BTCQMemecacheKeys.TIMES.ONE_HOUR, lisMap);
	}
}
