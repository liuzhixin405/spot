package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.TickerVo;

/**
 * 俄罗斯RTS api
 * @author administrator
 *
 */
public class CommonDataStockRTS implements CommonDataMarket {

	private static final long serialVersionUID = 3658625238470425501L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strRTSApiEastmoney");
		String strResult = HttpToServer.sendGet(url, "");
//		{"rc":0,"rt":4,"svr":183020035,"lt":1,"full":1,"data":{"f43":142636,"f44":143020,"f45":141596,"f46":142031,"f47":0,"f48":0.0,"f49":0,"f50":0,"f51":0,"f52":0,"f55":0.0,"f57":"RTS","f58":"俄罗斯RTS","f59":2,"f60":142120,"f62":0,"f71":0,"f78":0,"f80":"[{\"b\":201910281500,\"e\":201910282350}]","f84":0.0,"f85":0.0,
//		"f86":1572254440,"f92":0.0,"f103":0.0,"f104":0.0,"f105":0.0,"f106":1,"f107":100,"f108":0.0,"f109":0.0,"f110":335,"f111":0,"f112":"RS","f113":0,"f114":0,"f115":0,"f116":0.0,"f117":0.0,"f31":0,"f32":0,"f33":0,"f34":0,"f35":0,"f36":0,"f37":0,"f38":0,"f39":0,"f40":0,"f19":0,"f20":0,"f17":0,"f18":0,"f15":0,"f16":0,"f13":0,"f14":0,"f11":0,"f12":0}}
//		System.out.println("俄罗斯RTS---" + strResult);
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);

		if (StringUtil.isEmpty(strResult)) {
			return vo;
		}
		
		JSONObject obj = new JSONObject(strResult);
		if (0 != obj.getInt("rc") || "null".equals(obj.getString("data"))) {
			return vo;
		}
		
		JSONObject data = (JSONObject) obj.get("data");
		if (null == data) {
			return vo;
		}
		
		BigDecimal last = new BigDecimal(data.getString("f43")).divide(new BigDecimal(100));
		BigDecimal high = new BigDecimal(data.getString("f44")).divide(new BigDecimal(100));
		BigDecimal low = new BigDecimal(data.getString("f45")).divide(new BigDecimal(100));
		BigDecimal open = new BigDecimal(data.getString("f46")).divide(new BigDecimal(100));
		BigDecimal lastClose = new BigDecimal(data.getString("f60")).divide(new BigDecimal(100));
		
		long time = data.getInt("f86");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date(time * 1000);
		vo.setVol(BigDecimal.ZERO);
		vo.setLast(last);
		vo.setLastUsd(lastClose);
		vo.setHigh(high);
		vo.setLow(low);
		vo.setRatio(BigDecimal.ONE);
		vo.setBuy(BigDecimal.ZERO);
		vo.setSell(BigDecimal.ZERO);
		vo.setTime(time);
		vo.setTrans_pair("");
		vo.setMarket("eastmoney");
		vo.setFudu("");
		vo.setOpen(open);
		vo.setDate(dateFormat.format(date));
		vo.setUpdateTime(timeFormat.format(date));
		
		return vo;
	}

}
