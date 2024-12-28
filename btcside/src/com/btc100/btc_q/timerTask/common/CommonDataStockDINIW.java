package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 美元指数(DINIW) api
 * @author administrator
 *
 */
public class CommonDataStockDINIW implements CommonDataMarket {

	private static final long serialVersionUID = -7968222369500651417L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strDINIWApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		05:07:04,99.1274,99.1274,99.2060,3376,99.2102,99.3154,98.9778,99.1274,美元指数,2019-09-28
//		System.out.println("美元指数---" + strResult);
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
			String lastClose = items[3];
			String fudu = items[2];
			String open = items[5];
			String updateTime = items[0];
			String date = items[10];
			
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
			vo.setMarket("SINA");
			vo.setFudu(fudu);
			vo.setOpen(new BigDecimal(open));
			vo.setUpdateTime(updateTime);
			vo.setDate(date);
		}

		return vo;
	}

}
