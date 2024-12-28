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

public class CommonDataFCoin implements CommonDataMarket {

	private static final long serialVersionUID = 4583415371062102681L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strTickerFCoin") + symbol1 + symbol2;
		String strResult = HttpToServer.sendGet(url, "");
		
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);
		
		if(TextUtils.isEmpty(strResult)){
			return vo;
		}

		JSONObject obj = new JSONObject(strResult);
		int status = obj.getInt("status");
		if (status != 0) {
			return vo;
		}
		
		JSONObject data = (JSONObject) obj.get("data");
		JSONArray ticker = data.getJSONArray("ticker");
		
		BigDecimal last = new BigDecimal(ConvertUtil.toDouble(ticker.get(0), 0.0)); 		// 最新
		BigDecimal high = new BigDecimal(ConvertUtil.toDouble(ticker.get(7), 0.0)); 		// 最高
		BigDecimal low = new BigDecimal(ConvertUtil.toDouble(ticker.get(8), 0.0)); 			// 最低
		
		BigDecimal vol = new BigDecimal(ConvertUtil.toDouble(ticker.get(9), 0.0)); 		// 成交量
		BigDecimal turnover = new BigDecimal(ConvertUtil.toDouble(ticker.get(10), 0.0)); 	// 成交额
		long time = System.currentTimeMillis() / 1000;
		
		vo.setVol(vol);
		vo.setLast(last);
		vo.setHigh(high);
		vo.setLow(low);
		vo.setBuy(BigDecimal.ZERO);
		vo.setSell(BigDecimal.ZERO);
		vo.setTurnover(turnover);
		vo.setTime(time);
		vo.setTrans_pair(symbol1.toUpperCase() + "/" + symbol2.toUpperCase());
		vo.setMarket("FCoin");
		
		return vo;
	}

}
