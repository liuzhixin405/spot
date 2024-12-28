package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 腾讯控股（TENCENT）00700.HK api
 * @author administrator
 *
 */
public class CommonDataStockHK00700 implements CommonDataMarket {

	private static final long serialVersionUID = 3204380368260047617L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("str00700HKApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		TENCENT,腾讯控股,332.800,331.000,332.800,330.000,330.600,-0.400,-0.121,330.600,330.800,1603837365.110,4837141,34.749,0.000,400.400,251.400,2019/10/17,11:14:43,2|3,N|Y|Y,0.000|0.000|0.000,0|||0.000|0.000|0.000,|0,Y
//		System.out.println("腾讯控股---" + strResult);
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
			String close = items[6];
//			String lastClose = items[3];
			String fudu = items[8];
			String open = items[2];
			String updateTime = items[18];
			String date = items[17];
			
			long time = System.currentTimeMillis() / 1000;
			vo.setVol(BigDecimal.ZERO);
			vo.setLast(new BigDecimal(close));
			vo.setLastUsd(new BigDecimal(close));
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
