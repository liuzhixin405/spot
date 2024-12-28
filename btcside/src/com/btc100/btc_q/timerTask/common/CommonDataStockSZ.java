package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 上证综合指数(000001.SH) api
 * @author administrator
 *
 */
public class CommonDataStockSZ implements CommonDataMarket {

	private static final long serialVersionUID = 1809619738827475715L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strSZApiSina");
		String strRes = HttpToServer.sendGet(url, "");
//		System.out.println("上证---" + strRes);
		
		String[] strR = strRes.split(";");
		String strResult = "";
		String strFuduResult = "";
		if (strR.length == 2) {
			strResult = strR[0];
			strFuduResult = strR[1];
		}
		
//		上证指数,3183.2736,3170.3610,3183.3120,3188.2210,3165.7067,0,0,260845918,270437094322,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2019-04-02,11:33:03,00
//		上证指数,3183.3120,12.9510,0.41,2608459,27043709
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
