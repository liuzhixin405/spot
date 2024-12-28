package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 离岸人民币（香港）(USDCNH) api
 * @author administrator
 *
 */
public class CommonDataStockUSDCNH implements CommonDataMarket {

	private static final long serialVersionUID = 8817447903451417730L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strUSDCNHApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		HSI,恒生指数,28677.760,28523.350,28701.600,28435.059,28566.908,43.560,0.150,0.000,0.000,88345193.507,0,0.000,0.000,31592.561,24540.631,2019/03/26,16:09:08,,,,,,
//		14:22:17,6.9334,6.93349,6.9385,238,6.9385,6.9405,6.9167,6.9334,离岸人民币（香港）,-0.07,-0.0051,0.00343,Forex Capital Markets. New York,6.9804,6.3421,+-+-+-**,2019-05-21
//		System.out.println("离岸人民币---" + strResult);
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
			String fudu = items[10];
			String open = items[5];
			String updateTime = items[0];
			String date = items[17];
			
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
