package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

public class CommonDataA50 implements CommonDataMarket {

	private static final long serialVersionUID = -4482662958615502115L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strTickerA50");
		String strResult = HttpToServer.sendGet(url, "");
//		System.out.println("strTickerA50---" + strResult);
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);

		if (StringUtil.isEmpty(strResult)) {
			return vo;
		}

		JSONObject data = new JSONObject(strResult).getJSONObject("chart");
		JSONObject obj = (JSONObject) data.getJSONArray("result").get(0);
		
		JSONObject ticker = (JSONObject) obj.getJSONObject("indicators").getJSONArray("quote").get(0);
		JSONArray highObj = (JSONArray) ticker.get("high");
//		JSONArray openObj = (JSONArray) ticker.get("open");
		JSONArray lowObj = (JSONArray) ticker.get("low");
		JSONArray lastObj = (JSONArray) ticker.get("close");
		JSONArray volObj = (JSONArray) ticker.get("volume");

		double previousPrice = obj.getJSONObject("meta").getDouble("chartPreviousClose");
		BigDecimal previous = new BigDecimal(previousPrice); // 昨日收盘价
		BigDecimal high = new BigDecimal(ConvertUtil.toDouble(highObj.get(0), 0.0)); // 最高
		BigDecimal low = new BigDecimal(ConvertUtil.toDouble(lowObj.get(0), 0.0)); // 最低
		BigDecimal last = new BigDecimal(ConvertUtil.toDouble(lastObj.get(0), 0.0)); // 最新
		BigDecimal vol = new BigDecimal(ConvertUtil.toDouble(volObj.get(0), 0.0)); // 成交量
		long time = System.currentTimeMillis() / 1000;

		vo.setVol(vol);
		vo.setLast(last);
		vo.setLastUsd(previous);
		vo.setHigh(high);
		vo.setLow(low);
		vo.setRatio(BigDecimal.ZERO);
		vo.setBuy(BigDecimal.ZERO);
		vo.setSell(BigDecimal.ZERO);
		vo.setTime(time);
		vo.setTrans_pair("");
		vo.setMarket("yahooA50");

		return vo;
	}

}
