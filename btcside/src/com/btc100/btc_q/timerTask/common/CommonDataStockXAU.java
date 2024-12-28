package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 伦敦金CFD(XAU) api
 * @author administrator
 *
 */
public class CommonDataStockXAU implements CommonDataMarket {

	private static final long serialVersionUID = 1334819607358582857L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strXAUApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		1413.70,1415.840,1413.55,1413.85,1424.30,1412.23,14:46:00,1415.84,1416.01,0,0,0,2019-07-05,伦敦金
//		System.out.println("伦敦金---" + strResult);
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
			String open = items[8];
			String close = items[0];
			String lastClose = items[7];
			
			BigDecimal fd = (new BigDecimal(100)).multiply(new BigDecimal(close).subtract(new BigDecimal(lastClose))).divide(new BigDecimal(lastClose), BigDecimal.ROUND_HALF_UP, 2);;
			String fudu = fd.toString();
			String updateTime = items[6];
			String date = items[12];
			
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
			vo.setMarket("LONDON-BANK");
			vo.setFudu(fudu);
			vo.setOpen(new BigDecimal(open));
			vo.setUpdateTime(updateTime);
			vo.setDate(date);
		}

		return vo;
	}

}
