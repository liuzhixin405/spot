package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.vo.TickerVo;

public class CommonDataBinance implements CommonDataMarket {
	
	private static final long serialVersionUID = -230337902948039522L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		
		String url = UrlConfig.getValue("strTickerBinance");
//		if ("BCH".equals(symbol1.toUpperCase())) {
//			symbol1 = "BCHABC";
//		} else if ("BSV".equals(symbol1.toUpperCase())) {
//			symbol1 = "BCHSV";
//		}
		
		String param = "symbol=" + symbol1.toUpperCase() + symbol2.toUpperCase();
		String strResult = HttpToServer.sendGet(url, param);
		
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);
		
		if(TextUtils.isEmpty(strResult)) {
			return vo;
		}
		
		JSONObject ticker = new JSONObject(strResult);
		BigDecimal high = new BigDecimal(ConvertUtil.toDouble(ticker.get("highPrice"), 0.0)); 	// 最高
		BigDecimal low = new BigDecimal(ConvertUtil.toDouble(ticker.get("lowPrice"), 0.0)); 	// 最低
		BigDecimal last = new BigDecimal(ConvertUtil.toDouble(ticker.get("lastPrice"), 0.0)); 	// 最新
		BigDecimal vol = new BigDecimal(ConvertUtil.toDouble(ticker.get("volume"), 0.0)); 		// 成交量
		long time = ConvertUtil.toLong(ticker.get("closeTime"), 0L) / 1000;
		
		vo.setVol(vol);
		vo.setLast(last);
		vo.setHigh(high);
		vo.setLow(low);
		vo.setBuy(BigDecimal.ZERO);
		vo.setSell(BigDecimal.ZERO);
		vo.setTime(time);
		vo.setTrans_pair(symbol1.toUpperCase() + "/" + symbol2.toUpperCase());
		vo.setMarket("Binance");
		
		return vo;
	}

}
