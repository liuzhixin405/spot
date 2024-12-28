package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;
import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.vo.TickerVo;

public class CommonDataHuobi implements CommonDataMarket {
	private static final long serialVersionUID = 1L;
	
	public static TickerVo findDetailMerged(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strTickerHuobi") + "/merged";
		String param = "symbol=" + symbol1.toLowerCase() + symbol2.toLowerCase();
		String strResult = HttpToServer.sendGet(url, param);
		
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);
		vo.setBuy(BigDecimal.ZERO);
		vo.setSell(BigDecimal.ZERO);
		
		if(TextUtils.isEmpty(strResult)){
			return vo;
		}

		JSONObject obj = new JSONObject(strResult);
		
		String status = obj.getString("status");
		if (!"ok".equals(status)) {
			return vo;
		}
		
		JSONObject ticker = (JSONObject) obj.get("tick");
		BigDecimal high = new BigDecimal(ConvertUtil.toDouble(ticker.get("high"), 0.0)); 		// 最高
		BigDecimal low = new BigDecimal(ConvertUtil.toDouble(ticker.get("low"), 0.0)); 			// 最低
		BigDecimal last = new BigDecimal(ConvertUtil.toDouble(ticker.get("close"), 0.0)); 		// 最新
		BigDecimal vol = new BigDecimal(ConvertUtil.toDouble(ticker.get("amount"), 0.0)); 		// 成交量
		BigDecimal turnover = new BigDecimal(ConvertUtil.toDouble(ticker.get("vol"), 0.0)); 	// 成交额
		long time = obj.getLong("ts") / 1000;
		
		JSONArray buyArray = ticker.getJSONArray("bid");
		JSONArray sellArray = ticker.getJSONArray("ask");
		BigDecimal buy = new BigDecimal(buyArray.getDouble(0));
		BigDecimal sell = new BigDecimal(sellArray.getDouble(0));
		
		vo.setBuy(buy);
		vo.setSell(sell);
		vo.setVol(vol);
		vo.setLast(last);
		vo.setHigh(high);
		vo.setLow(low);
		vo.setTurnover(turnover);
		vo.setTime(time);
		vo.setTrans_pair(symbol1.toUpperCase() + "/" + symbol2.toUpperCase());
		vo.setMarket("Huobi-Pro");
		
		return vo;
	}

	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strTickerHuobi");
		String param = "symbol=" + symbol1.toLowerCase() + symbol2.toLowerCase();
		String strResult = HttpToServer.sendGet(url, param);
		
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);
		
		if(TextUtils.isEmpty(strResult)){
			return vo;
		}

		JSONObject obj = new JSONObject(strResult);
		
		String status = obj.getString("status");
		if (!"ok".equals(status)) {
			return vo;
		}
		
		JSONObject ticker = (JSONObject) obj.get("tick");
		BigDecimal high = new BigDecimal(ConvertUtil.toDouble(ticker.get("high"), 0.0)); 		// 最高
		BigDecimal low = new BigDecimal(ConvertUtil.toDouble(ticker.get("low"), 0.0)); 			// 最低
		BigDecimal last = new BigDecimal(ConvertUtil.toDouble(ticker.get("close"), 0.0)); 		// 最新
		BigDecimal vol = new BigDecimal(ConvertUtil.toDouble(ticker.get("amount"), 0.0)); 		// 成交量
		BigDecimal turnover = new BigDecimal(ConvertUtil.toDouble(ticker.get("vol"), 0.0)); 	// 成交额
		long time = obj.getLong("ts") / 1000;
		
		vo.setVol(vol);
		vo.setLast(last);
		vo.setHigh(high);
		vo.setLow(low);
		vo.setBuy(BigDecimal.ZERO);
		vo.setSell(BigDecimal.ZERO);
		vo.setTurnover(turnover);
		vo.setTime(time);
		vo.setTrans_pair(symbol1.toUpperCase() + "/" + symbol2.toUpperCase());
		vo.setMarket("Huobi-Pro");
		
		return vo;
	}

}
