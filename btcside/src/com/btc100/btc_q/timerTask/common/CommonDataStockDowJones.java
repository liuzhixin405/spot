package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 道琼斯期货 api
 * @author administrator
 *
 */
public class CommonDataStockDowJones implements CommonDataMarket {

	private static final long serialVersionUID = -348885429023553205L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
//		String url = UrlConfig.getValue("strDowJonesApiSina");
		String url = UrlConfig.getValue("strStockDJIApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		28239.800,,28240.000,28242.000,28322.000,28229.000,11:14:23,28312.000,28312.000,82694.000,2,3,2020-08-27,道琼斯指数期货,8841
//		System.out.println("道琼斯---" + strResult);
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
			vo.setMarket("NYSE");
			vo.setFudu(fudu);
			vo.setOpen(new BigDecimal(open));
			vo.setUpdateTime(updateTime);
			vo.setDate(date);
		}

		return vo;
	}

}
