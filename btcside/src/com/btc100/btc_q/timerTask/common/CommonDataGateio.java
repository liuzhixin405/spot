package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.vo.TickerVo;

public class CommonDataGateio implements CommonDataMarket {

	private static final long serialVersionUID = -3874281023937139090L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strTickerGateio") + symbol1 + "_" + symbol2;
		String strResult = HttpToServer.sendGet(url, "");

		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);

		if (TextUtils.isEmpty(strResult)) {
			return vo;
		}

		JSONObject obj = new JSONObject(strResult);
		String result = obj.getString("result");
		if (!"true".equals(result)) {
			return vo;
		}

		BigDecimal last = new BigDecimal(ConvertUtil.toDouble(obj.get("last"), 0.0)); // 最新
		BigDecimal high = new BigDecimal(ConvertUtil.toDouble(obj.get("high24hr"), 0.0)); // 最高
		BigDecimal low = new BigDecimal(ConvertUtil.toDouble(obj.get("low24hr"), 0.0)); // 最低
		BigDecimal buy = new BigDecimal(ConvertUtil.toDouble(obj.get("highestBid"), 0.0)); // 买一
		BigDecimal sell = new BigDecimal(ConvertUtil.toDouble(obj.get("lowestAsk"), 0.0)); // 卖一
		BigDecimal vol = new BigDecimal(ConvertUtil.toDouble(obj.get("baseVolume"), 0.0)); // 成交量
		long time = System.currentTimeMillis() / 1000;

		vo.setVol(vol);
		vo.setLast(last);
		vo.setHigh(high);
		vo.setLow(low);
		vo.setBuy(buy);
		vo.setSell(sell);
		vo.setTime(time);
		vo.setTrans_pair(symbol1.toUpperCase() + "/" + symbol2.toUpperCase());
		vo.setMarket("gateio");

		return vo;
	}

}
