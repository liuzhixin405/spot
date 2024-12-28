package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 纳斯达克OMX集团 NASDAQ:NDAQ api
 * @author administrator
 *
 */
public class CommonDataStockNDAQ implements CommonDataMarket {

	private static final long serialVersionUID = 2381526367051926863L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strNDAQApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		纳斯达克集团,100.0800,-1.71,2019-09-28 05:14:18,-1.7400,102.5700,102.8000,99.7700,105.2600,75.4888,464915,528483,
//		16717505213,3.27,30.61,0.00,0.00,0.47,0.00,167041419,80.00,100.0800,0.00,0.00,Sep 27 04:43PM EDT,Sep 27 04:00PM EDT,101.8200,10279.00
//		System.out.println("纳斯达克集团---" + strResult);
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
