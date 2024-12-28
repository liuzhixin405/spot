package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * A50期指当月连续 api
 * @author administrator
 *
 */
public class CommonDataStockA50 implements CommonDataMarket {

	private static final long serialVersionUID = -3671675581896861201L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		
		String url = UrlConfig.getValue("strA50CFDApiSina");
		// "12422.50,1.2400,0.00,0.00,12449.00,12190.50,16:29:59,12270.00,12270.00,0,0,0,2019-03-11,富时中国A50指数期货"
		// "14662.700,,14682.000,14684.000,14902.000,14480.000,14:41:41,14754.000,14757.000,738493.000,18,21,2021-08-31,富时中国A50指数,315641";
		String strResult = HttpToServer.sendGet(url, "");
//		System.out.println(strResult);
		
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);
		
		if (!StringUtil.isEmpty(strResult)) {
			String[] values = strResult.split("\"");
			if (values.length == 3) {
				strResult = values[1];
				String[] items = strResult.split(",");
				if (items.length == 15) {
					String close = items[0];
					String high = items[4];
					String low = items[5];
					String open = items[8];
					String lastClose = items[7];
					String time = items[6];
					String date = items[12];
							
					BigDecimal blast = new BigDecimal(Double.valueOf(close));
					BigDecimal bhigh = new BigDecimal(Double.valueOf(high));
					BigDecimal blow = new BigDecimal(Double.valueOf(low));
					BigDecimal bopen = new BigDecimal(Double.valueOf(open));
					BigDecimal blastClose = new BigDecimal(Double.valueOf(lastClose));
					
					BigDecimal fd = (blast.subtract(blastClose)).divide(blastClose, BigDecimal.ROUND_HALF_UP, 2).multiply((new BigDecimal(100)));
					String fudu = fd.toString();
					
//					long time = data.getInt("f86");
//					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//					SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//					Date date = new Date(time * 1000);
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date dt = null;
					try {
						dt = sdf.parse(date + " " + time);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					vo.setVol(BigDecimal.ZERO);
					vo.setLast(blast);
					vo.setLastUsd(blastClose);
					vo.setHigh(bhigh);
					vo.setLow(blow);
					vo.setRatio(BigDecimal.ONE);
					vo.setBuy(BigDecimal.ZERO);
					vo.setSell(BigDecimal.ZERO);
					vo.setTime(dt == null ? System.currentTimeMillis()/1000 : dt.getTime()/1000);
					vo.setTrans_pair("");
					vo.setMarket("SGX");
					vo.setFudu(fudu);
					vo.setOpen(bopen);
					vo.setDate(date);
					vo.setUpdateTime(time);
				}
			}
		}
		
		return vo;
		
		
//		String url = UrlConfig.getValue("strA50ApiEastmoney");
//		String strResult = HttpToServer.sendGet(url, "");
////		{"rc":0,"rt":4,"svr":183020035,"lt":1,"full":1,"data":{"f43":142636,"f44":143020,"f45":141596,"f46":142031,"f47":0,"f48":0.0,"f49":0,"f50":0,"f51":0,"f52":0,"f55":0.0,"f57":"RTS","f58":"俄罗斯RTS","f59":2,"f60":142120,"f62":0,"f71":0,"f78":0,"f80":"[{\"b\":201910281500,\"e\":201910282350}]","f84":0.0,"f85":0.0,
////		"f86":1572254440,"f92":0.0,"f103":0.0,"f104":0.0,"f105":0.0,"f106":1,"f107":100,"f108":0.0,"f109":0.0,"f110":335,"f111":0,"f112":"RS","f113":0,"f114":0,"f115":0,"f116":0.0,"f117":0.0,"f31":0,"f32":0,"f33":0,"f34":0,"f35":0,"f36":0,"f37":0,"f38":0,"f39":0,"f40":0,"f19":0,"f20":0,"f17":0,"f18":0,"f15":0,"f16":0,"f13":0,"f14":0,"f11":0,"f12":0}}
////		System.out.println("A50---" + strResult);
//		TickerVo vo = new TickerVo();
//		vo.setLast(BigDecimal.ZERO);
//		vo.setVol(BigDecimal.ZERO);
//
//		if (StringUtil.isEmpty(strResult)) {
//			return vo;
//		}
//		
//		JSONObject obj = new JSONObject(strResult);
//		if (0 != obj.getInt("rc") || "null".equals(obj.getString("data"))) {
//			return vo;
//		}
//		
//		JSONObject data = (JSONObject) obj.get("data");
//		if (null == data) {
//			return vo;
//		}
//		
//		BigDecimal last = new BigDecimal(data.getString("f43")).divide(new BigDecimal(10));
//		BigDecimal high = new BigDecimal(data.getString("f44")).divide(new BigDecimal(10));
//		BigDecimal low = new BigDecimal(data.getString("f45")).divide(new BigDecimal(10));
//		BigDecimal open = new BigDecimal(data.getString("f46")).divide(new BigDecimal(10));
//		BigDecimal lastClose = new BigDecimal(data.getString("f60")).divide(new BigDecimal(10));
//		
//		BigDecimal fd = (last.subtract(lastClose)).divide(lastClose, BigDecimal.ROUND_HALF_UP, 2).multiply((new BigDecimal(100)));
//		String fudu = fd.toString();
//		
//		long time = data.getInt("f86");
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//		Date date = new Date(time * 1000);
//		vo.setVol(BigDecimal.ZERO);
//		vo.setLast(last);
//		vo.setLastUsd(lastClose);
//		vo.setHigh(high);
//		vo.setLow(low);
//		vo.setRatio(BigDecimal.ONE);
//		vo.setBuy(BigDecimal.ZERO);
//		vo.setSell(BigDecimal.ZERO);
//		vo.setTime(time);
//		vo.setTrans_pair("");
//		vo.setMarket("SGX");
//		vo.setFudu(fudu);
//		vo.setOpen(open);
//		vo.setDate(dateFormat.format(date));
//		vo.setUpdateTime(timeFormat.format(date));
//		
//		return vo;
	}

}
