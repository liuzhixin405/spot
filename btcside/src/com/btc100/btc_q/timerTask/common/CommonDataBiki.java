package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.vo.TickerVo;

public class CommonDataBiki implements CommonDataMarket {

	private static final long serialVersionUID = 3379266502222653569L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strTickerBiki") + "?symbol=" + symbol1 + symbol2;
		String strResult = HttpToServer.sendGet(url, "");
		
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
		
		JSONObject data = (JSONObject) obj.get("data");
		
		BigDecimal last = new BigDecimal(ConvertUtil.toDouble(data.get("last"), 0.0)); // 最新
		BigDecimal high = new BigDecimal(ConvertUtil.toDouble(data.get("high"), 0.0)); // 最高
		BigDecimal low = new BigDecimal(ConvertUtil.toDouble(data.get("low"), 0.0)); // 最低
		BigDecimal buy = new BigDecimal(ConvertUtil.toDouble(data.get("buy"), 0.0)); // 买一
		BigDecimal sell = new BigDecimal(ConvertUtil.toDouble(data.get("sell"), 0.0)); // 卖一
		BigDecimal vol = new BigDecimal(ConvertUtil.toDouble(data.get("vol"), 0.0)); // 成交量
		long time = System.currentTimeMillis() / 1000;

		vo.setVol(vol);
		vo.setLast(last);
		vo.setHigh(high);
		vo.setLow(low);
		vo.setBuy(buy);
		vo.setSell(sell);
		vo.setTime(time);
		vo.setTrans_pair(symbol1.toUpperCase() + "/" + symbol2.toUpperCase());
		vo.setMarket("biki");
		
		return vo;
	}

}
