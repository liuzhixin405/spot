package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 布伦特原油CFD(OIL) api
 * @author administrator
 *
 */
public class CommonDataStockOIL implements CommonDataMarket {

	private static final long serialVersionUID = 5092557192566771442L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strOILApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		63.221,,63.220,63.230,63.430,63.140,14:46:47,63.300,63.270,0.000,6,14,2019-07-05,布伦特原油
//		System.out.println("布伦特原油---" + strResult);
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);

		if (StringUtil.isEmpty(strResult)) {
			return vo;
		}

		String[] values = strResult.split("\"");
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
			
			BigDecimal fd = (new BigDecimal(100)).multiply(new BigDecimal(close).subtract(new BigDecimal(lastClose))).divide(new BigDecimal(lastClose), BigDecimal.ROUND_HALF_UP, 2);;
			String fudu = fd.toString();
			
			long time = System.currentTimeMillis() / 1000;
			vo.setVol(BigDecimal.ZERO);
			vo.setLast(new BigDecimal(close));
			vo.setLastUsd(new BigDecimal(lastClose));
			vo.setHigh(new BigDecimal(high));
			vo.setLow(new BigDecimal(low));
			vo.setRatio(BigDecimal.ONE);
			vo.setBuy(BigDecimal.ZERO);
			vo.setSell(BigDecimal.ZERO);
			vo.setTime(time);
			vo.setTrans_pair("");
			vo.setMarket("ICE-EURO");
			vo.setFudu(fudu);
			vo.setOpen(new BigDecimal(open));
			vo.setUpdateTime(updateTime);
			vo.setDate(date);
		}

		return vo;
	}

}
