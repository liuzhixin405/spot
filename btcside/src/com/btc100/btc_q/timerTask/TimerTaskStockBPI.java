package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.timerTask.common.CommonDataMarket;
import com.btc100.btc_q.timerTask.common.CommonDataStockA50;
import com.btc100.btc_q.timerTask.common.CommonDataStockAlibaba;
import com.btc100.btc_q.timerTask.common.CommonDataStockAmazon;
import com.btc100.btc_q.timerTask.common.CommonDataStockApple;
import com.btc100.btc_q.timerTask.common.CommonDataStockCME;
import com.btc100.btc_q.timerTask.common.CommonDataStockDINIW;
import com.btc100.btc_q.timerTask.common.CommonDataStockDowJones;
import com.btc100.btc_q.timerTask.common.CommonDataStockFacebook;
import com.btc100.btc_q.timerTask.common.CommonDataStockGoogle;
import com.btc100.btc_q.timerTask.common.CommonDataStockHK00700;
import com.btc100.btc_q.timerTask.common.CommonDataStockHK01611;
import com.btc100.btc_q.timerTask.common.CommonDataStockHK03690;
import com.btc100.btc_q.timerTask.common.CommonDataStockHKHsi;
import com.btc100.btc_q.timerTask.common.CommonDataStockICE;
import com.btc100.btc_q.timerTask.common.CommonDataStockInx;
import com.btc100.btc_q.timerTask.common.CommonDataStockJD;
import com.btc100.btc_q.timerTask.common.CommonDataStockNDAQ;
import com.btc100.btc_q.timerTask.common.CommonDataStockNK225;
import com.btc100.btc_q.timerTask.common.CommonDataStockNasdaq;
import com.btc100.btc_q.timerTask.common.CommonDataStockOIL;
import com.btc100.btc_q.timerTask.common.CommonDataStockPDD;
import com.btc100.btc_q.timerTask.common.CommonDataStockRTS;
import com.btc100.btc_q.timerTask.common.CommonDataStockSZ;
import com.btc100.btc_q.timerTask.common.CommonDataStockSZ399001;
import com.btc100.btc_q.timerTask.common.CommonDataStockSZ399006;
import com.btc100.btc_q.timerTask.common.CommonDataStockSZ399286;
import com.btc100.btc_q.timerTask.common.CommonDataStockTesla;
import com.btc100.btc_q.timerTask.common.CommonDataStockUSDCNH;
import com.btc100.btc_q.timerTask.common.CommonDataStockXAG;
import com.btc100.btc_q.timerTask.common.CommonDataStockXAU;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.TickerVo;

public class TimerTaskStockBPI {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private BQTransactionTypeService transTypeService;
	private static final Logger logger = Logger.getLogger(TimerTaskStockBPI.class);
	
	private static final CommonDataMarket[] MARKETS = { new CommonDataStockNasdaq(),
			new CommonDataStockNK225(), /*new CommonDataStockHKHsi(),*/ new CommonDataStockXAU(),
			new CommonDataStockXAG(), new CommonDataStockSZ(), new CommonDataStockUSDCNH(), new CommonDataStockAmazon(),
			new CommonDataStockApple(), new CommonDataStockGoogle(), new CommonDataStockFacebook(),
			new CommonDataStockHK01611(), new CommonDataStockDINIW(), new CommonDataStockNDAQ(),
			new CommonDataStockICE(), new CommonDataStockCME(), new CommonDataStockHK03690(),
			new CommonDataStockSZ399006(), new CommonDataStockSZ399001(), new CommonDataStockAlibaba(),
			new CommonDataStockJD(), new CommonDataStockPDD(), new CommonDataStockTesla(),
			new CommonDataStockHK00700(), new CommonDataStockInx(), new CommonDataStockRTS(),
			new CommonDataStockOIL(), new CommonDataStockSZ399286(), /*new CommonDataStockDowJones(),*/
			new CommonDataStockA50()};

	private static String[] contractArray = null;

	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.contracts");
			contractArray = contracts.split(",");
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		for (String contract : contractArray) {
			if (StringUtil.isEmpty(contract)) {
				continue;
			}

			BQTransaction bqTransaction = transactionService.findTransByCode(contract);
			if (bqTransaction == null || bqTransaction.getTransaction_kind() != 1) continue;
			
			String typeCode = bqTransaction.getTransaction_type_code();
			if ("uk100".equals(typeCode) || "france40".equals(typeCode)
					|| "germany30".equals(typeCode) || "eustoxx50".equals(typeCode) || "kospi".equals(typeCode)
					|| "sensex".equals(typeCode) || "hkhsi".equals(typeCode)) {
				continue;
			}
			
			Integer digits = spyMemcachedClient.get(typeCode + BTCQMemecacheKeys.DIGITS);
			if (digits == null) {
				BQTransactionType transType = null;
				List<BQTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES);
				if (transTypes == null) {
					transType = transTypeService.getTransTypeByCode(typeCode);
				} else {
					for (BQTransactionType type : transTypes) {
						if (type.getType_code().equalsIgnoreCase(typeCode)) {
							transType = type;
						}
					}
				}
				
				if (transType == null) continue;
				
				digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
				spyMemcachedClient.set(transType.getType_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
			}
			
			doGetIndex(bqTransaction.getTransaction_type(), typeCode, digits);
		}
	}
	
	private void doGetIndex(int typeId, String typeCode, int digits) {
		List<TickerVo> tickers = new ArrayList<TickerVo>();
		BigDecimal last = BigDecimal.ZERO;
		BigDecimal preClose = BigDecimal.ZERO;
		
		String code = typeCode.toLowerCase();
		for (CommonDataMarket market : MARKETS) {
			if (code.equals("ixic") && !market.getClass().equals(CommonDataStockNasdaq.class)) {
				continue;
			}
			
			if (code.equals("dji") && !market.getClass().equals(CommonDataStockDowJones.class)) {
				continue;
			}
			
			if (code.equals("hkhsi") && !market.getClass().equals(CommonDataStockHKHsi.class)) {
				continue;
			}
			
			if (code.equals("nky") && !market.getClass().equals(CommonDataStockNK225.class)) {
				continue;
			}
			
			if (code.equals("oil") && !market.getClass().equals(CommonDataStockOIL.class)) {
				continue;
			}
			
			if (code.equals("xau") && !market.getClass().equals(CommonDataStockXAU.class)) {
				continue;
			}
			
			if (code.equals("xag") && !market.getClass().equals(CommonDataStockXAG.class)) {
				continue;
			}
			
			if (code.equals("sh000001") && !market.getClass().equals(CommonDataStockSZ.class)) {
				continue;
			}
			
			if (code.equals("usdcnh") && !market.getClass().equals(CommonDataStockUSDCNH.class)) {
				continue;
			}
			
			if (code.equals("amzn") && !market.getClass().equals(CommonDataStockAmazon.class)) {
				continue;
			}
			
			if (code.equals("aapl") && !market.getClass().equals(CommonDataStockApple.class)) {
				continue;
			}
			
			if (code.equals("goog") && !market.getClass().equals(CommonDataStockGoogle.class)) {
				continue;
			}
			
			if (code.equals("fb") && !market.getClass().equals(CommonDataStockFacebook.class)) {
				continue;
			}
			
			if (code.equals("hk01611") && !market.getClass().equals(CommonDataStockHK01611.class)) {
				continue;
			}
			
			if (code.equals("diniw") && !market.getClass().equals(CommonDataStockDINIW.class)) {
				continue;
			}
			
			if (code.equals("ndaq") && !market.getClass().equals(CommonDataStockNDAQ.class)) {
				continue;
			}
			
			if (code.equals("ice") && !market.getClass().equals(CommonDataStockICE.class)) {
				continue;
			}
			
			if (code.equals("cme") && !market.getClass().equals(CommonDataStockCME.class)) {
				continue;
			}
			
			if (code.equals("hk03690") && !market.getClass().equals(CommonDataStockHK03690.class)) {
				continue;
			}
			
			if (code.equals("sz399006") && !market.getClass().equals(CommonDataStockSZ399006.class)) {
				continue;
			}
			
			if (code.equals("sz399001") && !market.getClass().equals(CommonDataStockSZ399001.class)) {
				continue;
			}
			
			if (code.equals("baba") && !market.getClass().equals(CommonDataStockAlibaba.class)) {
				continue;
			}
			
			if (code.equals("jd") && !market.getClass().equals(CommonDataStockJD.class)) {
				continue;
			}
			
			if (code.equals("pdd") && !market.getClass().equals(CommonDataStockPDD.class)) {
				continue;
			}
			
			if (code.equals("tsla") && !market.getClass().equals(CommonDataStockTesla.class)) {
				continue;
			}
			
			if (code.equals("hk00700") && !market.getClass().equals(CommonDataStockHK00700.class)) {
				continue;
			}
			
			if (code.equals("inx") && !market.getClass().equals(CommonDataStockInx.class)) {
				continue;
			}
			
			if (code.equals("rts") && !market.getClass().equals(CommonDataStockRTS.class)) {
				continue;
			}
			
			if (code.equals("bc50") && !market.getClass().equals(CommonDataStockSZ399286.class)) {
				continue;
			}
			
			if (code.equals("a50") && !market.getClass().equals(CommonDataStockA50.class)) {
				continue;
			}
			
			TickerVo ticker = null;
			try {
				ticker = market.findTicker(typeCode, "");
				ticker.setLastUsd(ticker.getLast());
			} catch (Exception e) {
				logger.error("getStockTick - " + market.getClass() + " error: " + e);
				continue;
			}
			
			if (!code.equals("nky")) {
				JSONObject obj = new JSONObject();
				obj.put("close", ticker.getLast());
				obj.put("high", ticker.getHigh());
				obj.put("low", ticker.getLow());
				obj.put("open", ticker.getOpen());
				obj.put("lastClose", ticker.getLastUsd());
				obj.put("time", ticker.getUpdateTime());
				obj.put("date", ticker.getDate());
				spyMemcachedClient.set(BTCQMemecacheKeys.CFD_INDEX + code, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, obj.toJSONString());
			}
			
			if (ticker != null && !StringUtil.isEmpty(ticker.getFudu()) && !code.equals("nky")) {
				String fudu = ticker.getFudu();
				
				String pointStr = fudu.substring(fudu.indexOf(".") + 1, fudu.length());
				if (pointStr.length() > 2) {
					fudu = fudu.substring(0, fudu.indexOf(".") + 3);
				}
				fudu = fudu + "%";
				
				spyMemcachedClient.set(BTCQMemecacheKeys.INDEX_FUDU + typeCode, BTCQMemecacheKeys.TIMES.ONE_DAY, fudu);
			}

			if (ticker != null && ticker.getLast().compareTo(BigDecimal.ZERO) > 0) {
				last = ticker.getLast();
				preClose = ticker.getLastUsd();
				ticker.setTypeId(typeId);
				ticker.setTypeCode(typeCode);
				tickers.add(ticker);
			}
		}
		
		last = last.setScale(digits, BigDecimal.ROUND_HALF_UP);
		preClose = preClose.setScale(digits, BigDecimal.ROUND_HALF_UP);
		
		if (preClose.compareTo(BigDecimal.ZERO) > 0) {
			// 昨日收盘价
			spyMemcachedClient.set(BTCQMemecacheKeys.PRECLOSE + typeCode, BTCQMemecacheKeys.TIMES.ONE_DAY, preClose);
		}
		
		if (!code.equals("nky")) {
			for (TickerVo vo : tickers) {
				spyMemcachedClient.set(vo.getTypeCode() + vo.getMarket(), BTCQMemecacheKeys.TIMES.BPI, last);
				bqPriceIndexCacheService.updateBtcCache(vo, last);
			}
			
			spyMemcachedClient.set(typeCode, BTCQMemecacheKeys.TIMES.BPI, last);
			spyMemcachedClient.set(BTCQMemecacheKeys.TICKER + typeCode, BTCQMemecacheKeys.TIMES.BPI, tickers);
		}
	}
	
	/*
	@SuppressWarnings("unused")
	private void getStockIndex(String code) throws JSONException {
		if (!code.equals("hkhsi")) {
			return;
		}
		String url = UrlConfig.getValue("strStockHSIApiSina");
		
		String strResult = HttpToServer.sendGet(url, "");
//		logger.info(strResult);
//		var hq_str_hf_NAS="4533.84,0.0000,0.00,0.00,4533.84,4533.84,21:11:17,4533.84,4533.84,1,0,0,2019-03-25,纳指期货";
//		var hq_str_hf_DJS="18079,0.0000,0,0,18079,18079,21:11:17,18079,18079,1,0,0,2019-03-25,道指期货";

		if (StringUtil.isEmpty(strResult)) {
			return;
		}

		String[] values = strResult.split("\"");
//		logger.info(values.length);
		if (values.length == 3) {
			strResult = values[1];
			String[] items = strResult.split(",");
			String high = items[4];
			String low = items[5];
			String close = items[0];
			String lastClose = items[7];
			String open = items[8];
			String updateTime = items[6];
			String date = items[12];
			
			JSONObject obj = new JSONObject();
			obj.put("close", Double.valueOf(close));
			obj.put("high", Double.valueOf(high));
			obj.put("low", Double.valueOf(low));
			obj.put("open", Double.valueOf(open));
			obj.put("lastClose", Double.valueOf(lastClose));
			obj.put("time", updateTime);
			obj.put("date", date);
			
			spyMemcachedClient.set(BTCQMemecacheKeys.CFD_INDEX + code, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, obj.toJSONString());
		}

	}
	*/
}
