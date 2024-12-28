package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.vo.TickerVo;

public class CommonDataAllcoin implements CommonDataMarket {

	private static final long serialVersionUID = -5625996765118963984L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strTickerAllcoin");
		String param = "symbol=" + symbol1.toLowerCase() + "2" +  symbol2.toLowerCase();
		String strResult = HttpToServer.sendGet(url, param);
		
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);
		
		if(TextUtils.isEmpty(strResult)){
			return vo;
		}

		JSONObject obj = new JSONObject(strResult);
		
		int status = obj.getInt("code");
		if (status != 0) {
			return vo;
		}
		
		JSONObject ticker = (JSONObject) obj.get("data");
		BigDecimal high = new BigDecimal(ConvertUtil.toDouble(ticker.get("high"), 0.0)); 		// 最高
		BigDecimal low = new BigDecimal(ConvertUtil.toDouble(ticker.get("low"), 0.0)); 			// 最低
		BigDecimal last = new BigDecimal(ConvertUtil.toDouble(ticker.get("last"), 0.0)); 		// 最新
		BigDecimal vol = new BigDecimal(ConvertUtil.toDouble(ticker.get("vol"), 0.0)); 			// 成交量
		long time = System.currentTimeMillis() / 1000;
		
		vo.setVol(vol);
		vo.setLast(last);
		vo.setHigh(high);
		vo.setLow(low);
		vo.setBuy(BigDecimal.ZERO);
		vo.setSell(BigDecimal.ZERO);
		vo.setTime(time);
		vo.setTrans_pair(symbol1.toUpperCase() + "/" + symbol2.toUpperCase());
		vo.setMarket("Allcoin");
		
		return vo;
	}

}
