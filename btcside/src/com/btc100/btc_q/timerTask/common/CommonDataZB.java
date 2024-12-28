package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.vo.TickerVo;

public class CommonDataZB implements CommonDataMarket {

	private static final long serialVersionUID = 853207115799553944L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {

		String url = UrlConfig.getValue("strTickerZB");

		String param = "market=" + symbol1 + "_" + symbol2;
		String strResult = HttpToServer.sendGetForHttp(url, param);

		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);

		if (TextUtils.isEmpty(strResult)) {
			return vo;
		}

		JSONObject str = new JSONObject(strResult);
		if (str.has("error")) {
			return vo;
		}
		JSONObject ticker = str.getJSONObject("ticker");
		BigDecimal high = new BigDecimal(ConvertUtil.toDouble(ticker.get("high"), 0.0)); // 最高
		BigDecimal low = new BigDecimal(ConvertUtil.toDouble(ticker.get("low"), 0.0)); // 最低
		BigDecimal last = new BigDecimal(ConvertUtil.toDouble(ticker.get("last"), 0.0)); // 最新
		BigDecimal buy = new BigDecimal(ConvertUtil.toDouble(ticker.get("buy"), 0.0)); // 买一
		BigDecimal sell = new BigDecimal(ConvertUtil.toDouble(ticker.get("sell"), 0.0)); // 卖一
		BigDecimal vol = new BigDecimal(ConvertUtil.toDouble(ticker.get("vol"), 0.0)); // 成交量
		long time = ConvertUtil.toLong(str.get("date"), 0l) / 1000;

		vo.setVol(vol);
		vo.setLast(last);
		vo.setHigh(high);
		vo.setLow(low);
		vo.setBuy(buy);
		vo.setSell(sell);
		vo.setTime(time);
		vo.setTrans_pair(symbol1.toUpperCase() + "/" + symbol2.toUpperCase());
		vo.setMarket("ZB");

		return vo;
	}

}
