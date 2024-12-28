package com.btc100.btc_q.action.websocket;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.TickerVo;

public class GetA50Client extends WebSocketClient {
	
	private static final Logger logger = Logger.getLogger(GetA50Client.class);

	private static SpyMemcachedClient spyMemcachedClient;
	private static BQPriceIndexCacheService bqPriceIndexCacheService;
	private static BQTransactionTypeService transTypeService;
	
	public static final String sub1 = "[\"{\\\"_event\\\":\\\"subscribe\\\",\\\"tzID\\\":28,\\\"message\\\":\\\"pid-28930:\\\"}\"]";//
	public static final String sub2 = "[\"{\\\"_event\\\":\\\"subscribe\\\",\\\"tzID\\\":28,\\\"message\\\":\\\"isOpenExch-54:\\\"}\"]";//
	public static final String sub3 = "[\"{\\\"_event\\\":\\\"subscribe\\\",\\\"tzID\\\":28,\\\"message\\\":\\\"isOpenExch-28930:\\\"}\"]";//
	public static final String sub4 = "[\"{\\\"_event\\\":\\\"subscribe\\\",\\\"tzID\\\":28,\\\"message\\\":\\\"pid-44486:\\\"}\"]";//
	public static final String sub5 = "[\"{\\\"_event\\\":\\\"subscribe\\\",\\\"tzID\\\":28,\\\"message\\\":\\\"isOpenPair-44486:\\\"}\"]";//
	
	private int heartbeat = 2;	// 0代表连接断开或者异常 1代表已连接.2代表正在连接
	
	public GetA50Client(URI serverUri) {
		super(serverUri);
		
		TrustManager[] trustAllCerts = new TrustManager[]{ new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[]{};
			}
			public void checkClientTrusted(X509Certificate[] chain,
										   String authType) throws CertificateException {
			}
			public void checkServerTrusted(X509Certificate[] chain,
										   String authType) throws CertificateException {
			}
		}};
		
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			//sslContext.init(null, null, null);
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		SSLSocketFactory factory = sslContext.getSocketFactory();
		try {
			this.setSocket(factory.createSocket());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setBean(SpyMemcachedClient client, BQPriceIndexCacheService cacheService, BQTransactionTypeService transTypeService) {
		GetA50Client.spyMemcachedClient = client;
		GetA50Client.bqPriceIndexCacheService = cacheService;
		GetA50Client.transTypeService = transTypeService;
	}
	
	public boolean isAlive() {
		return heartbeat > 0;
	}
	
	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		logger.info("GetA50Client关闭了");
		heartbeat = 0;
	}

	@Override
	public void onError(Exception e) {
		logger.info("GetA50Client异常： ", e);
		heartbeat = 0;
	}
	
	@Override
	public void onMessage(String message) {
		BQTransactionType transactionType = transTypeService.getTransTypeByCode("a50");
		if (transactionType == null) {
			return;
		}
		String code = transactionType.getType_code();

		BigDecimal last = BigDecimal.ZERO;
		BigDecimal previous = BigDecimal.ZERO;
		BigDecimal high = BigDecimal.ZERO;
		BigDecimal low = BigDecimal.ZERO;
		
		List<TickerVo> tickers = new ArrayList<TickerVo>();
		
		if (!StringUtil.isEmpty(message)) {
			// 解析message获取last_numeric数值，放入缓存 
			//a["{\"message\":\"pid-28930::{\\\"pid\\\":\\\"28930\\\",\\\"last_dir\\\":\\\"greenBg\\\",\\\"last_numeric\\\":12976.06,\\\"last\\\":\\\"12,976.06\\\",\\\"bid\\\":\\\"0.00    \\\",\\\"ask\\\":\\\"0.00    \\\",\\\"high\\\":\\\"13,236.02\\\",\\\"low\\\":\\\"12,879.60\\\",\\\"last_close\\\":\\\"12,879.60\\\",\\\"pc\\\":\\\"+164.64\\\",\\\"pcp\\\":\\\"+1.28%\\\",\\\"pc_col\\\":\\\"greenFont\\\",\\\"time\\\":\\\"2:28:44\\\",\\\"timestamp\\\":1551666480}\"}"]
			//a["{\"message\":\"pid-44486::{\\\"pid\\\":\\\"44486\\\",\\\"last_dir\\\":\\\"greenBg\\\",\\\"last_numeric\\\":13789   ,\\\"last\\\":\\\"13,789.0 \\\",\\\"bid\\\":\\\"13,783.0\\\",\\\"ask\\\":\\\"13,795.0\\\",\\\"high\\\":\\\"13,790. 5\\\",\\\"low\\\":\\\"13,519. 0\\\",\\\"pc\\\":\\\"+186.5\\\",\\\"pcp\\\":\\\"+1.37%\\\",\\\"pc_col\\\":\\\"greenFont\\\",\\\"time\\\":\\\"3:20:42\\\",\\\"timestamp\\\":1555384842}\"}"]
			// 解析成JsonObject
			if (message.contains("last_numeric")) {
				
//				logger.info(message);
				
				Integer digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.DIGITS);
				if (digits == null) {
					digits = Utils.getDigitsFromMinFloat(transactionType.getMin_float_price());
					spyMemcachedClient.set(code + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
				
				message = message.replace("\\", "");
				if (message.contains("::")) {
					message = message.substring(message.indexOf("::") + 2);
				}
				if (message.length() > 4)
					message = message.substring(0, message.length() - 4);
				
				do {
					try {
						JSONObject data = new JSONObject(message);
						double lastDou = data.getDouble("last_numeric");
						String preColseStr = "0";
						String highStr = data.getString("high");
						String lowStr = data.getString("low");
						String pcp = data.getString("pcp");
						String pid = data.getString("pid");
						
						highStr = highStr.replace(",", "");
						lowStr = lowStr.replace(",", "");
						
						if ("28930".equals(pid)) {
							preColseStr = data.getString("last_close");
							preColseStr = preColseStr.replace(",", "");
						}
						
						last = new BigDecimal(lastDou);
						previous = new BigDecimal(preColseStr);
						high = new BigDecimal(highStr);
						low = new BigDecimal(lowStr);
						last = last.setScale(digits, BigDecimal.ROUND_HALF_UP);
						previous = previous.setScale(digits, BigDecimal.ROUND_HALF_UP);
						high = high.setScale(digits, BigDecimal.ROUND_HALF_UP);
						low = low.setScale(digits, BigDecimal.ROUND_HALF_UP);
						
						if (!StringUtil.isEmpty(pcp)) {
							if (pcp.contains("+")) {
								pcp = pcp.replace("+", "");
							}
							
							if ("28930".equals(pid)) {
								spyMemcachedClient.set(BTCQMemecacheKeys.INDEX_FUDU + code, BTCQMemecacheKeys.TIMES.ONE_DAY, pcp);
							}
						}
						
						if ("44486".equals(pid)) {
							long ts = Long.parseLong(data.getString("timestamp"));
							Date dt = new Date();
							dt.setTime(ts * 1000);
							
							String pc = data.getString("pc");
							BigDecimal open = last;
							if (!StringUtil.isEmpty(pc)) {
								BigDecimal pcNum = new BigDecimal(pc);
								open = open.subtract(pcNum);
							}
							
							JSONObject obj = new JSONObject();
							obj.put("close", last.doubleValue());
							obj.put("high", high.doubleValue());
							obj.put("low", low.doubleValue());
							obj.put("open", open.doubleValue());
							obj.put("lastClose", open.doubleValue());
							
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String date = formatter.format(dt);
							
							String[] times = date.split(" ");
							
							obj.put("time", times[1]);
							obj.put("date", times[0]);
							
							spyMemcachedClient.set(BTCQMemecacheKeys.CFD_INDEX + "a50", BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, obj.toString());
							
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if (last.compareTo(BigDecimal.ZERO) > 0) {
						TickerVo ticker = new TickerVo();
						long time = System.currentTimeMillis() / 1000;
						
						ticker.setVol(BigDecimal.ZERO);
						ticker.setLast(last);
						ticker.setLastUsd(previous);
						ticker.setHigh(high);
						ticker.setLow(low);
						ticker.setRatio(BigDecimal.ONE);
						ticker.setBuy(BigDecimal.ZERO);
						ticker.setSell(BigDecimal.ZERO);
						ticker.setTime(time);
						ticker.setTrans_pair("");
						ticker.setMarket("Investing");
						ticker.setTypeId(transactionType.getType_id());
						ticker.setTypeCode(code);
						
						tickers.add(ticker);
						for (TickerVo vo : tickers) {
							spyMemcachedClient.set(vo.getTypeCode() + vo.getMarket(), BTCQMemecacheKeys.TIMES.BPI, last);
							bqPriceIndexCacheService.updateBtcCache(vo, last);
						}
						
						spyMemcachedClient.set(code, BTCQMemecacheKeys.TIMES.BPI, last);
						spyMemcachedClient.set(BTCQMemecacheKeys.TICKER + code, BTCQMemecacheKeys.TIMES.BPI, tickers);
					}
					if (previous.compareTo(BigDecimal.ZERO) > 0) {
						// 昨日收盘价
						spyMemcachedClient.set(BTCQMemecacheKeys.PRECLOSE + code, BTCQMemecacheKeys.TIMES.ONE_DAY, previous);
					}
				} while(false);
			}
			
		}
//		logger.info(spyMemcachedClient.get(code));
	}

	@Override
	public void onOpen(ServerHandshake shake) {
		logger.info("GetA50Client onOpen ...");
		heartbeat = 1;
		
		send(sub1);
		send(sub2);
		send(sub3);
		send(sub4);
		send(sub5);
	}

}
