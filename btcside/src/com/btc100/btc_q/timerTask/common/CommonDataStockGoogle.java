package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 谷歌公司 NASDAQ:GOOG api
 * @author administrator
 *
 */
public class CommonDataStockGoogle implements CommonDataMarket {

	private static final long serialVersionUID = 8352657885646789004L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strGoogleApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		谷歌,1133.4700,-0.64,2019-05-28 16:13:20,-7.3000,1147.3600,1149.7700,1131.6600,1289.2700,970.1100,1112341,1444691,
//		778195163200,40.26,28.15,0.00,0.00,0.00,0.00,686560000,71.00,1135.0600,0.14,1.59,May 28 04:13AM EDT,May 24 04:00PM EDT,1140.7700,200.00
//		System.out.println("谷歌---" + strResult);
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
