package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 深证成份指数(399001.SZ) api
 * @author administrator
 *
 */
public class CommonDataStockSZ399001 implements CommonDataMarket {

	private static final long serialVersionUID = 7904996946075784608L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strSZ399001ApiSina");
		String strRes = HttpToServer.sendGet(url, "");
//		System.out.println("深证成指---" + strRes);
		
		String[] strR = strRes.split(";");
		String strResult = "";
		String strFuduResult = "";
		if (strR.length == 2) {
			strResult = strR[0];
			strFuduResult = strR[1];
		}
		
//		深证成指,9656.982,9638.102,9666.583,9704.677,9583.519,0.000,0.000,24353343748,273787771848.424,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,2019-10-11,15:00:03,00
//		深证成指,9666.58,28.481,0.30,243533437,27378777
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
