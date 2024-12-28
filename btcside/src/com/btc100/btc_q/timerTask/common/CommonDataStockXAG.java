package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 伦敦银CFD(XAG) api
 * @author administrator
 *
 */
public class CommonDataStockXAG implements CommonDataMarket {
	
	private static final long serialVersionUID = 5356672612666747394L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strXAGApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		15.21,15.287,15.20,15.22,15.34,15.21,14:46:00,15.29,15.29,0,0,0,2019-07-05,伦敦银
//		System.out.println("伦敦银---" + strResult);
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);

		if (StringUtil.isEmpty(strResult)) {
			return vo;
		}

		String[] values = strResult.split("\"");
		if (values.length == 2) {
			strResult = values[1];
			String[] items = strResult.split(",");
			String high = items[4];
			String low = items[5];
			String close = items[0];
			String lastClose = items[7];
			String open = items[8];
			String updateTime = items[6];
			String date = items[12];
			
			BigDecimal fd = (new BigDecimal(100)).multiply(new BigDecimal(close).subtract(new BigDecimal(lastClose))).divide(new BigDecimal(lastClose), BigDecimal.ROUND_HALF_UP, 2);;
			String fudu = fd.toString();
			
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
