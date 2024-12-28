package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 亚马逊公司 NASDAQ:AMZN api
 * @author administrator
 *
 */
public class CommonDataStockAmazon implements CommonDataMarket {

	private static final long serialVersionUID = 209495209124483114L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strAmazonApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		亚马逊,1823.2800,0.43,2019-05-28 16:24:22,7.8000,1835.8900,1841.7600,1817.8500,2050.5000,1307.0000,3369673,4302485,
//		897150393840,24.54,74.30,0.00,0.00,0.00,0.00,492053000,61.00,1830.9800,0.42,7.70,May 28 04:03AM EDT,May 24 04:00PM EDT,1815.4800,610.00
//		System.out.println("亚马逊---" + strResult);
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
			String high = items[6];
			String low = items[7];
			String close = items[1];
			String lastClose = items[26];
			String vol = items[10];
			String fudu = items[2];
			String open = items[5];
			String timeStr = items[3];
			String[] timeArr = timeStr.split("\\s+");
			String date = timeArr[0];
			String updateTime = timeArr[1];
			
			long time = System.currentTimeMillis() / 1000;
			vo.setVol(new BigDecimal(vol));
			vo.setLast(new BigDecimal(close));
			vo.setLastUsd(new BigDecimal(lastClose));
			vo.setHigh(new BigDecimal(high));
			vo.setLow(new BigDecimal(low));
			vo.setRatio(BigDecimal.ONE);
			vo.setBuy(BigDecimal.ZERO);
			vo.setSell(BigDecimal.ZERO);
			vo.setTime(time);
			vo.setTrans_pair("");
			vo.setMarket("SINA");
			vo.setFudu(fudu);
			vo.setOpen(new BigDecimal(open));
			vo.setUpdateTime(updateTime);
			vo.setDate(date);
		}

		return vo;
	}

}
