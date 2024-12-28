package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;
import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.vo.TickerVo;

public class CommonDataBitfinex implements CommonDataMarket {
	private static final long serialVersionUID = 1L;
	public BigDecimal count_bitfinex = BigDecimal.ZERO;
	public BigDecimal amount_bitfinex = BigDecimal.ZERO;

	public BigDecimal latestPrice = BigDecimal.ZERO;
	public BigDecimal highestPrice = BigDecimal.ZERO;
	public BigDecimal lowestPrice = BigDecimal.ZERO;
	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		if ("USDT".equals(symbol2.toUpperCase())) {
			symbol2 = "USD";
		} else {
			return null;
		}
		
		String url = UrlConfig.getValue("strTickerBitfinex") + symbol1.toUpperCase() + symbol2.toUpperCase();
		String strResult = HttpToServer.sendGet(url, "");
		
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);
		
		if(TextUtils.isEmpty(strResult)){
			return vo;
		}

		JSONArray array = new JSONArray(strResult);
		if ("error".equals(array.get(0))) {
			return vo;
		}
		
		BigDecimal last = new BigDecimal(ConvertUtil.toDouble(array.get(6), 0.0)); 		// 最新
		BigDecimal vol = new BigDecimal(ConvertUtil.toDouble(array.get(7), 0.0)); 		// 成交量
		BigDecimal high = new BigDecimal(ConvertUtil.toDouble(array.get(8), 0.0)); 		// 最高
		BigDecimal low = new BigDecimal(ConvertUtil.toDouble(array.get(9), 0.0));		// 最低
		long time = System.currentTimeMillis() / 1000;
		
		vo.setVol(vol);
		vo.setLast(last);
		vo.setHigh(high);
		vo.setLow(low);
		vo.setBuy(BigDecimal.ZERO);
		vo.setSell(BigDecimal.ZERO);
		vo.setTime(time);
		vo.setTrans_pair(symbol1.toUpperCase() + "/" + symbol2.toUpperCase());
		vo.setMarket("Bitfinex");
		
		return vo;
	}

}
