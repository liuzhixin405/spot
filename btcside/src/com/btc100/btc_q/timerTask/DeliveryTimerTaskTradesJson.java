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
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQUsdrateService;
import com.btc100.btc_q.service.DeliveryMemberTradeDetailService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
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
public class DeliveryTimerTaskTradesJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryTransactionService dTransactionService;
	@Autowired
	private DeliveryTransactionTypeService dTransactionTypeService;
	@Autowired
	private DeliveryMemberTradeDetailService dMemberTradeDetailService;
	@Autowired
	private DeliveryTradeLogService dTradeLogService;
	@Autowired
	private BQUsdrateService bqUsdrateService;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private DeliveryMemberTransactionService dMemberTransactionService;
	
	public static String[] contractArray = null;

	public void run() {

		if (contractArray == null) {
			String contracts = UrlConfig.getValue("deliveryTrade.contracts");
			contractArray = contracts.split(",");
		}

		if (contractArray == null || contractArray.length == 0)
			return;
		
		List<DeliveryTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = dTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, lstTrads);
		}
		if (lstTrads == null || lstTrads.size() == 0) return;
		
		for (String contract : contractArray) {
			String[] codeDelivery = contract.split("_");
			if (codeDelivery.length == 0) {
				continue;
			}
			String typeCode = codeDelivery[0];
			String deliveryType = codeDelivery[1];
			
			DeliveryTransaction transaction = null;
			for (DeliveryTransaction bqTransaction : lstTrads) {
				if (bqTransaction.getTransaction_type_code().equals(typeCode) && bqTransaction.getDelivery_type().equals(deliveryType)) {
					transaction = bqTransaction;
					break;
				}
			}

			if (transaction == null) {
				continue;
			}
			
			if (transaction.getTransaction_state() != 1) {
				continue;
			}
			
			String bqCode = transaction.getTransaction_code(); // 获取合约号
			// 1.搜索成交记录最新的50条数据
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRADE_CODE + bqCode, BTCQMemecacheKeys.TIMES.TRADE_CODE, dMemberTradeDetailService.getAllJsonTopN(bqCode, Contents.TRADE_JSON).toString());
			
			tickersData(transaction);
//			getBPIData(bq);
			
			Integer pricePrecision = spyMemcachedClient.get(transaction.getTransaction_code() + BTCQMemecacheKeys.D_DIGITS);
			if (pricePrecision == null) {
				pricePrecision = getDigits(transaction);
			}
			
			int amountPrecision = Utils.getAmountDigtis(transaction.getTransaction_code());
			JSONObject json = new JSONObject();
			json.put("symbol", transaction.getTransaction_code());
			json.put("pricePrecision", pricePrecision);
			json.put("amountPrecision", amountPrecision);
			json.put("multiples", transaction.getMultiple());
			json.put("showName", transaction.getTransaction_show_name());
			json.put("enName", transaction.getTransaction_show_name_en());
			json.put("jpName", transaction.getTransaction_show_name_jp());
			json.put("krName", transaction.getTransaction_show_name_kr());
			json.put("hkName", transaction.getTransaction_show_name_hk());
			
			JSONArray array = new JSONArray();
			String symbols = spyMemcachedClient.get(BTCQMemecacheKeys.D_SYMBOLS_DATA);
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
							o.put("symbol", transaction.getTransaction_code());
							o.put("pricePrecision", pricePrecision);
							o.put("amountPrecision", amountPrecision);
							o.put("multiples", transaction.getMultiple());
							o.put("showName", transaction.getTransaction_show_name());
							o.put("enName", transaction.getTransaction_show_name_en());
							o.put("jpName", transaction.getTransaction_show_name_jp());
							o.put("krName", transaction.getTransaction_show_name_kr());
							o.put("hkName", transaction.getTransaction_show_name_hk());
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
			
			spyMemcachedClient.set(BTCQMemecacheKeys.D_SYMBOLS_DATA, BTCQMemecacheKeys.TIMES.ONE_HOUR, array.toString());
		}
	}
	
	protected int getDigits(DeliveryTransaction trans) {
		Integer digits = spyMemcachedClient.get(trans.getTransaction_code() + BTCQMemecacheKeys.D_DIGITS);
		if (digits == null) {
			DeliveryTransactionType transType = dTransactionTypeService.getTransactionType(trans.getTransaction_type());
			digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
			spyMemcachedClient.set(trans.getTransaction_code() + BTCQMemecacheKeys.D_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}
		
		return digits;
	}
	
	private void tickersData(DeliveryTransaction bqt) {
		// 获取当前时间的24小时时间（时间戳）
		long yesterdayTime = TimeUtil.getYesterdayTime();
		Map<String, Object> map = new HashMap<String, Object>();
		String tranCode = bqt.getTransaction_code();
		String typeCode = bqt.getTransaction_type_code().toLowerCase();
		map.put("tradeType", bqt.getTransaction_type());
		map.put("typeCode", typeCode);
		map.put("time", DateUtil.dateToStr(bqt.getTransaction_end_time(), "MM月dd日"));
		map.put("tranCode", tranCode);
		map.put("transactionType", bqt.getTransaction_type());
		map.put("transactionKind", bqt.getTransaction_kind());
		map.put("deliveryType", bqt.getDelivery_type());
		map.put("showName", bqt.getTransaction_show_name());
		map.put("enName", bqt.getTransaction_show_name_en());
		map.put("jpName", bqt.getTransaction_show_name_jp());
		map.put("krName", bqt.getTransaction_show_name_kr());
		map.put("hkName", bqt.getTransaction_show_name_hk());
		
		Integer digits = getDigits(bqt);
		
		JSONObject bqLastTicker = null;
		String ticker = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_BTC + "_" + bqt.getTransaction_code());
		if (!StringUtil.isEmpty(ticker)) {
			bqLastTicker = JSONObject.parseObject(ticker);
		}
		
		double high = 0.0;
		double low = 0.0;
		double vol = 0.0;
		double lastPrice = 0.0;
		double firstPrice = 0.0;
		
		if (bqLastTicker == null) {
			high = dTradeLogService.findHighPrice(bqt.getTransaction_code(), yesterdayTime).setScale(digits, BigDecimal.ROUND_DOWN).doubleValue();
			low = dTradeLogService.findLowPrice(bqt.getTransaction_code(), yesterdayTime).setScale(digits, BigDecimal.ROUND_DOWN).doubleValue();
			String strLast = bqt.getTransaction_code() + BTCQMemecacheKeys.D_BTCLATESTPRICE;
			BigDecimal last = spyMemcachedClient.get(strLast);
			if (last == null) {
				// 从数据库中算出最新成交价 
				last = bqt.getLatest_transaction_price();
			}
			
			if (last == null) last = BigDecimal.ZERO;
			lastPrice = last.doubleValue();
			
			BigDecimal volb = dTradeLogService.findBTCVol(bqt.getTransaction_code(), yesterdayTime);
			volb = volb == null ? new BigDecimal(0) : volb;
			vol = volb.doubleValue();
		} else {

			high = bqLastTicker.getDouble("high");
			low = bqLastTicker.getDouble("low");
			vol = bqLastTicker.getDouble("vol");
			lastPrice = bqLastTicker.getDouble("last");
		}
		
		BigDecimal first = spyMemcachedClient.get(tranCode + BTCQMemecacheKeys.D_FIRST_PRICE);
		if (first == null) {
			first = dTradeLogService.findFirstPrice(bqt.getTransaction_code(), TimeUtil.getTimesmorning());
			spyMemcachedClient.set(tranCode + BTCQMemecacheKeys.D_FIRST_PRICE, BTCQMemecacheKeys.TIMES.BPI, first);
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

		String indexFudu = spyMemcachedClient.get(BTCQMemecacheKeys.INDEX_FUDU + typeCode);
		if (StringUtil.isEmpty(indexFudu)) {
			map.put("indexFudu", "0.00%");
		} else {
			map.put("indexFudu", indexFudu);
		}
		
		BigDecimal btc_index_price = spyMemcachedClient.get(typeCode);
		if (btc_index_price == null) {
			BQPriceIndexCache bqPriceIndexCache = bqPriceIndexCacheService.getBtcIndexPrice(typeCode);
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
		BigDecimal hold = spyMemcachedClient.get(tranCode + BTCQMemecacheKeys.D_HOLD_COUNT);
		if (hold == null) {
			double count = dMemberTransactionService.getHoldCount(tranCode);
			if (count > 0) {
				hold = new BigDecimal(count);
				spyMemcachedClient.set(tranCode + BTCQMemecacheKeys.D_HOLD_COUNT, BTCQMemecacheKeys.TIMES.TEN_SECOND, hold);
			}
		}
		
		if (hold != null) holdCount = hold.doubleValue();
		map.put("holdCount", holdCount);
		map.put("tradeAmount", 0);
		
		List<Map<String, Object>> lisMap = spyMemcachedClient.get(BTCQMemecacheKeys.DELIVERY_BASE); 
		if (lisMap == null) {
			lisMap = new ArrayList<Map<String, Object>>();
		}
		
		for (Map<String, Object> item : lisMap) {
			if (item.get("tranCode").equals(map.get("tranCode"))) {
				lisMap.remove(item);
				break;
			}
		}
		lisMap.add(map);
		
		spyMemcachedClient.set(BTCQMemecacheKeys.DELIVERY_BASE, BTCQMemecacheKeys.TIMES.ONE_HOUR, lisMap);
	}
}
