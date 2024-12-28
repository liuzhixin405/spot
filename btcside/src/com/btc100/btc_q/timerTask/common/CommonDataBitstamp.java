package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;

import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.vo.TickerVo;

public class CommonDataBitstamp implements CommonDataMarket{
	private static final long serialVersionUID = 1L;
	public BigDecimal count_bitstamp = BigDecimal.ZERO;
	public BigDecimal amount_bitstamp = BigDecimal.ZERO;

	private BigDecimal dtcount = BigDecimal.ZERO;
	private BigDecimal dtamount = BigDecimal.ZERO;
	
	public BigDecimal latestPrice = BigDecimal.ZERO;
	public BigDecimal highestPrice = BigDecimal.ZERO;
	public BigDecimal lowestPrice = BigDecimal.ZERO;

	/**
	 * 递归算出前1小时在哪个100条记录里面
	 * 
	 * @param since
	 * @param lTime
	 *            当前时间
	 * @return
	 */
	public void findTradeCount_Bitstamp() {
		String strResult = HttpToServer.sendGet(UrlConfig.getValue("strTradeBitstamp"), ""); // 最近24小时的交易数据(时间倒序)
		System.out.println("BITSTAMP---"+strResult.length());
		try {
			JSONArray array = new JSONArray(strResult);
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				BigDecimal iprice = new BigDecimal(json.getString("price"));
				BigDecimal iamount = new BigDecimal(json.getString("amount"));
				dtcount = dtcount.add(iprice.multiply(iamount));
				dtamount = dtamount.add(iamount);
				if(i==0){
					latestPrice = iprice;
				}
				//给最高最低成交价赋值
				if(highestPrice.signum() ==0 || highestPrice.compareTo(iprice)<=0){
					highestPrice = iprice;
				}
				if(lowestPrice.signum() ==0 || lowestPrice.compareTo(iprice)>=0){
					lowestPrice = iprice;
				}
			}
			count_bitstamp = dtcount;
			amount_bitstamp = dtamount;
			return;

		} catch (JSONException e) {
			e.printStackTrace();
		}
//		try {
//			JSONArray array = new JSONArray(strResult);
//			for (int i = 0; i < array.length(); i++) {
//				JSONObject json = array.getJSONObject(i);
//				BigDecimal iprice = new BigDecimal(json.getString("price"));
//				BigDecimal iamount = new BigDecimal(json.getString("amount"));
//				dtcount = dtcount.add(iprice.multiply(iamount));
//				dtamount = dtamount.add(iamount);
//				if(i==0){
//					latestPrice = iprice;
//				}
//				//给最高最低成交价赋值
//				if(highestPrice.signum() ==0 || highestPrice.compareTo(iprice)<=0){
//					highestPrice = iprice;
//				}
//				if(lowestPrice.signum() ==0 || lowestPrice.compareTo(iprice)>=0){
//					lowestPrice = iprice;
//				}
//			}
//			count_bitstamp = dtcount;
//			amount_bitstamp = dtamount;
//			return;
//			
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
	}

	public TickerVo findTicker() throws JSONException {
		String strResult = HttpToServer.sendGet(UrlConfig.getValue("strTickerBitstamp"), "");
		if(TextUtils.isEmpty(strResult)){
			return new TickerVo();
		}
//		JSONObject ticker = new JSONObject(strResult);
//		BigDecimal high = new BigDecimal(ConvertUtil.toDouble(ticker.get("high"), 0.0)); // 最高
//		BigDecimal low = new BigDecimal(ConvertUtil.toDouble(ticker.get("low"), 0.0)); // 最低
//		BigDecimal buy = new BigDecimal(ConvertUtil.toDouble(ticker.get("bid"), 0.0)); // 买一
//		BigDecimal sell = new BigDecimal(ConvertUtil.toDouble(ticker.get("ask"), 0.0)); // 卖一
//		BigDecimal last = new BigDecimal(ConvertUtil.toDouble(ticker.get("last"), 0.0)); // 最新
//		BigDecimal vol = new BigDecimal(ConvertUtil.toDouble(ticker.get("volume"), 0.0)); // 成交量
		return new TickerVo();
	}

	@Override
	public TickerVo findTicker(String symbol, String transPair) throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
