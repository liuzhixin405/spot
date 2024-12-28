package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * Facebook NASDAQ:FB api
 * @author administrator
 *
 */
public class CommonDataStockFacebook implements CommonDataMarket {

	private static final long serialVersionUID = -1026689027295397055L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strFacebookApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		Facebook,191.1400,0.85,2019-06-22 09:31:49,1.6100,188.7500,192.0000,188.7500,218.6200,123.0200,22751159,19655059,
//		551438900000,6.79,28.15,0.00,0.00,0.00,0.00,2885000000,73.00,190.6200,-0.27,-0.52,Jun 21 08:00PM EDT,Jun 21 04:00PM EDT,189.5300,3340894.00
//		System.out.println("Facebook---" + strResult);
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
