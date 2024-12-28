package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 创业板指数P(399006.SZ) api
 * @author administrator
 *
 */
public class CommonDataStockSZ399006 implements CommonDataMarket {

	private static final long serialVersionUID = -3500666103409820108L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strSZ399006ApiSina");
		String strRes = HttpToServer.sendGet(url, "");
//		System.out.println("创业板---" + strRes);
		
		String[] strR = strRes.split(";");
		String strResult = "";
		String strFuduResult = "";
		if (strR.length == 2) {
			strResult = strR[0];
			strFuduResult = strR[1];
		}
		
//		创业板指,1632.218,1622.415,1666.966,1668.174,1627.433,0.000,0.000,6545411947,94999841701.540,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,2019-10-10,15:00:03,00
//		创业板指,1666.97,44.551,2.75,19323030,3470440
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);

		if (StringUtil.isEmpty(strResult) && StringUtil.isEmpty(strFuduResult)) {
			return vo;
		}

		String[] str = strResult.split("\"");
		if (str.length == 2) {
			strResult = str[1];
			String[] items = strResult.split(",");
			String high = items[4];
			String low = items[5];
			String close = items[3];
			String lastClose = items[2];
			String open = items[1];
			String updateTime = items[items.length - 2];
			String date = items[items.length - 3];
			
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
			vo.setOpen(new BigDecimal(open));
			vo.setUpdateTime(updateTime);
			vo.setDate(date);
		}
		
		String[] strFudu = strFuduResult.split("\"");
		if (strFudu.length == 2) {
			strFuduResult = strFudu[1];
			String[] items = strFuduResult.split(",");
			String fudu = items[3];
			vo.setFudu(fudu);
		}
		
		return vo;
	}

}
