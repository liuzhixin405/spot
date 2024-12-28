package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 美团点评-W（MEITUAN-W）03690.HK api
 * @author administrator
 *
 */
public class CommonDataStockHK03690 implements CommonDataMarket {

	private static final long serialVersionUID = 4710931690594094395L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("str03690HKApiSina");
		String strResult = HttpToServer.sendGet(url, "");
//		MEITUAN-W,美团点评－Ｗ,88.550,89.000,88.900,86.800,87.550,-1.450,-1.629,87.550,87.600,1101947227.460,12534810,0.000,0.000,89.250,40.250,2019/10/09,11:25:07,2|3,N|N|Y,0.000|0.000|0.000,0|||0.000|0.000|0.000,|0,Y
//		System.out.println("美团点评---" + strResult);
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
			String close = items[6];
//			String lastClose = items[3];
			String fudu = items[8];
			String open = items[2];
			String updateTime = items[18];
			String date = items[17];
			
			long time = System.currentTimeMillis() / 1000;
			vo.setVol(BigDecimal.ZERO);
			vo.setLast(new BigDecimal(close));
			vo.setLastUsd(new BigDecimal(close));
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
