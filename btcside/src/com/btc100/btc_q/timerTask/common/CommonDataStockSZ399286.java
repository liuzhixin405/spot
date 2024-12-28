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
 * 区块链50(399286.SZ) api
 * @author administrator
 *
 */
public class CommonDataStockSZ399286 implements CommonDataMarket {

	private static final long serialVersionUID = 4908936050752132137L;

	@Override
	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strSZ399286ApiSina");
		String strRes = HttpToServer.sendGet(url, "");
//		System.out.println("区块链50---" + strRes);
		
		String[] strR = strRes.split(";");
		String strResult = "";
		String strFuduResult = "";
		if (strR.length == 2) {
			strResult = strR[0];
			strFuduResult = strR[1];
		}
		
//		区块链50,3256.262,3437.525,3410.370,3449.046,3256.262,0.000,0.000,3050270624,35147815339.720,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,0,0.000,2020-03-13,15:00:03,00
//		区块链50,3410.37,-27.155,-0.79,30502706,3514781
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
			vo.setMarket("SZ");
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

	@SuppressWarnings("unused")
	private static TickerVo findTickerByEastmoney(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strSZ399286ApiEastmoney");
		String strResult = HttpToServer.sendGet(url, "");
//		{"rc":0,"rt":4,"svr":183018499,"lt":1,"full":1,"data":{"f43":356404,"f44":358748,"f45":343409,"f46":346377,"f47":35363233,"f48":40528079616.0,"f49":0,"f50":83,"f51":0,"f52":0,"f55":0.0,"f57":"399286","f58":"区块链50","f59":2,"f60":340766,"f62":0,"f71":0,"f78":0,"f80":"[{\"b\":202003020930,\"e\":202003021130},{\"b\":202003021300,\"e\":202003021500}]","f84":0.0,"f85":0.0,
//		"f86":1583134263,"f92":0.0,"f103":0.0,"f104":0.0,"f105":0.0,"f106":100,"f107":0,"f108":0.0,"f109":0.0,"f110":0,"f111":5,"f112":"5","f113":0,"f114":0,"f115":0,"f116":0.0,"f117":0.0,"f31":0,"f32":0,"f33":0,"f34":0,"f35":0,"f36":0,"f37":0,"f38":0,"f39":0,"f40":0,"f19":0,"f20":0,"f17":0,"f18":0,"f15":0,"f16":0,"f13":0,"f14":0,"f11":0,"f12":0}}
//		System.out.println("区块链50---" + strResult);
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
