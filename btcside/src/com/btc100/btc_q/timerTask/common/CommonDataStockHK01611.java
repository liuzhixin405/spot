package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 桐成控股 （PANTRONICS HLDG） 01611.HK api
 * @author administrator
 *
 */
public class CommonDataStockHK01611 implements CommonDataMarket {

	private static final long serialVersionUID = 1768019798577512185L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("str01611HKApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		PANTRONICS HLDG,桐成控股,3.390,3.100,7.210,3.390,5.000,1.900,61.290,4.890,5.020,16447020.000,3163000,0.000,0.000,7.210,2.900,2019/09/11,11:35:12,2|3,N|N|N,0.000|0.000|0.000,0|||0.000|0.000|0.000,|0,Y
//		System.out.println("桐成控股---" + strResult);
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
