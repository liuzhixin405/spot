package com.btc100.btc_q.timerTask.common;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.OTHERTradesdata;
import com.btc100.btc_q.service.OTHERTradesdataService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.HttpToServer;
import com.btc100.btc_q.vo.TickerVo;

public class CommonDataOkcoin implements CommonDataMarket {
	private static final long serialVersionUID = 1L;
	public BigDecimal count_Okcoin = BigDecimal.ZERO;
	public BigDecimal amount_Okcoin = BigDecimal.ZERO;

	private BigDecimal dtcount = BigDecimal.ZERO;
	private BigDecimal dtamount = BigDecimal.ZERO;

	private BigDecimal ddcount = BigDecimal.ZERO;
	private BigDecimal ddamount = BigDecimal.ZERO;

	public BigDecimal latestPrice = BigDecimal.ZERO;
	public BigDecimal highestPrice = BigDecimal.ZERO;
	public BigDecimal lowestPrice = BigDecimal.ZERO;
	private boolean flag = true;// 取最新成交价的标识

	/**
	 * 递归算出前1小时在哪个100条记录里面
	 * 
	 * @param since
	 * @param lTime
	 *            当前时间
	 * @return
	 */
	public void findTradeCount_Okcoin(int since, long lTime) {

		int time = 1000;

		String para = since == 0 ? "" : "since=" + since;
		try {
			String strResult = HttpToServer.sendGet(UrlConfig.getValue("strTradeOkcoin"), para); // 最后60条

			// 取出第一条，找出时间和交易id
			JSONArray array = new JSONArray(strResult);
			// 取出第一条数据
			JSONObject firstJson = array.getJSONObject(0);
			long lfirstDate = firstJson.getLong("date");
			int ifirstTId = firstJson.getInt("tid");
			// 取出最后一条数据
			JSONObject lastJson = array.getJSONObject(array.length() - 1);
			long llastDate = lastJson.getLong("date");

			// 如果超出此60条最后一条，则该交易所前一小时没有交易
			if (CommonDataUtil.compare_date(lTime, llastDate)) {
				return;
			}
			// 获取最新成交价
			if (flag) {
				latestPrice = new BigDecimal(lastJson.getString("price"));
			}
			// 如果在此60条内
			if (CommonDataUtil.compare_date(llastDate, lTime) && CommonDataUtil.compare_date(lTime, lfirstDate)) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					long ldate = json.getLong("date"); // 取出时间
					int iTId = Integer.parseInt(json.getString("tid"));
					if (CommonDataUtil.compare_date(ldate, lTime) && (since == 0 || since + time > iTId)) { // 找到了
						BigDecimal iprice = new BigDecimal(json.getString("price"));
						BigDecimal iamount = new BigDecimal(json.getString("amount"));
						dtcount = dtcount.add(iprice.multiply(iamount));
						dtamount = dtamount.add(iamount);

						// 给最高最低成交价赋值
						if (highestPrice.signum() == 0 || highestPrice.compareTo(iprice) <= 0) {
							highestPrice = iprice;
						}
						if (lowestPrice.signum() == 0 || lowestPrice.compareTo(iprice) >= 0) {
							lowestPrice = iprice;
						}
					}
				}
				count_Okcoin = dtcount.add(ddcount);
				amount_Okcoin = dtamount.add(ddamount);
				return;
			}

			// 如果不在此500条内，计算此500条的可用数据
			if (CommonDataUtil.compare_date(lfirstDate, lTime)) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = array.getJSONObject(i);
					int iTId = Integer.parseInt(json.getString("tid"));
					if (since == 0 || since + time > iTId) {
						BigDecimal iprice = new BigDecimal(json.getString("price"));
						BigDecimal iamount = new BigDecimal(json.getString("amount"));
						ddcount = ddcount.add(iprice.multiply(iamount));
						ddamount = ddamount.add(iamount);

						// 给最高最低成交价赋值
						if (highestPrice.signum() == 0 || highestPrice.compareTo(iprice) <= 0) {
							highestPrice = iprice;
						}
						if (lowestPrice.signum() == 0 || lowestPrice.compareTo(iprice) >= 0) {
							lowestPrice = iprice;
						}
					} else if (since + time < iTId) {
						break;
					}
				}

				since = ifirstTId - time;
				flag = false;
				findTradeCount_Okcoin(since, lTime);
			}

		} catch (Exception e) {
		}

	}

	public void findTrades_Okcoin(OTHERTradesdataService otherTradesdataService, long lTime) {
		int coinType = Contents.OTHERTRADEDATAS_OKCOIN;
		OTHERTradesdata plastData = otherTradesdataService.findLastByFlag(coinType);
		if (plastData == null)
			return;
		latestPrice = plastData.getD_price();
		OTHERTradesdata data = otherTradesdataService.findSumByFlag(coinType);
		if (data != null) {
			dtcount = data.getD_price();
			dtamount = data.getD_amount();
		}
		highestPrice = otherTradesdataService.findMaxByFlag(coinType);
		lowestPrice = otherTradesdataService.findMinByFlag(coinType);
		count_Okcoin = dtcount;
		amount_Okcoin = dtamount;
	}

	public TickerVo findTicker(String symbol1, String symbol2) throws JSONException {
		String url = UrlConfig.getValue("strTickerOkcoin");
		String param = symbol1.toUpperCase() + "-" + symbol2.toUpperCase();
		url = url.replace("{1}", param);
		String strResult = HttpToServer.sendGet(url, "");
		
		TickerVo vo = new TickerVo();
		vo.setLast(BigDecimal.ZERO);
		vo.setVol(BigDecimal.ZERO);
		
		if(TextUtils.isEmpty(strResult)) {
			return vo;
		}
		
		JSONObject ticker = new JSONObject(strResult);
		BigDecimal high = new BigDecimal(ConvertUtil.toDouble(ticker.get("high_24h"), 0.0)); 	// 最高
		BigDecimal low = new BigDecimal(ConvertUtil.toDouble(ticker.get("low_24h"), 0.0)); 		// 最低
		BigDecimal buy = new BigDecimal(ConvertUtil.toDouble(ticker.get("bid"), 0.0)); 		// 买一
		BigDecimal sell = new BigDecimal(ConvertUtil.toDouble(ticker.get("ask"), 0.0)); 	// 卖一
		BigDecimal last = new BigDecimal(ConvertUtil.toDouble(ticker.get("last"), 0.0)); 	// 最新
		BigDecimal vol = new BigDecimal(ConvertUtil.toDouble(ticker.get("base_volume_24h"), 0.0)); 		// 成交量
		String timestamp = (String) ticker.get("timestamp");

		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			date = sdf.parse(timestamp);//拿到Date对象
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long time = System.currentTimeMillis() / 1000;
		if (date != null) {
			time = date.getTime() / 1000;
		}
		
		vo.setVol(vol);
		vo.setLast(last);
		vo.setHigh(high);
		vo.setLow(low);
		vo.setBuy(buy);
		vo.setSell(sell);
		vo.setTime(time);
		vo.setTrans_pair(symbol1.toUpperCase() + "/" + symbol2.toUpperCase());
		vo.setMarket("OKEX");
		
		return vo;
	}

}
