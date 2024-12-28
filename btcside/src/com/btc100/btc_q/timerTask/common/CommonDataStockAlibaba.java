package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 阿里巴巴 NYSE:BABA api
 * @author administrator
 *
 */
public class CommonDataStockAlibaba implements CommonDataMarket {

	private static final long serialVersionUID = 8891936764863537169L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strAlibabaApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		阿里巴巴,177.1200,1.04,2019-10-17 09:34:48,1.8300,175.8000,177.3500,174.1200,195.7200,129.7700,10903835,12258493,
//		461137533464,6.17,28.71,0.00,0.00,0.00,0.00,2603531693,40.00,176.9000,-0.12,-0.22,Oct 16 08:00PM EDT,Oct 16 04:01PM EDT,175.2900,104836.00
//		System.out.println("阿里巴巴---" + strResult);
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
