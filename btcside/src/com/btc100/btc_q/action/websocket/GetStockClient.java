package com.btc100.btc_q.action.websocket;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
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

public class GetStockClient extends WebSocketClient {
	
	private static final Logger logger = Logger.getLogger(GetStockClient.class);

	private static SpyMemcachedClient spyMemcachedClient;
	private static BQPriceIndexCacheService bqPriceIndexCacheService;
	private static BQTransactionTypeService transTypeService;
	
	// 上证-40820， 日经-178，道琼斯：169，纳斯达克：14958
	// 富时100：27，法国CAC40指数：167，德国DAX30指数：172，欧洲斯托克50指数：175，韩国KOSPI指数：37426，印度孟买30指数：39929
	// 原油-8833，a50-28930
	public static final String bulkSub = "[\"{\\\"_event\\\":\\\"bulk-subscribe\\\",\\\"tzID\\\":28,\\\"message\\\":\\\""
//			+ "pid-178:%%isOpenExch-178:%%isOpenExch-20:"
//			+ "%%pid-27:%%isOpenExch-27:%%isOpenExch-3:"
//			+ "%%pid-167:%%isOpenExch-167:%%isOpenExch-9:"
			+ "%%pid-169:%%isOpenExch-169:%%isOpenExch-1:"		// dji
			+ "%%pid-69:%%isOpenExch-69:%%isOpenExch-1002:"		// xag
			+ "%%pid-14958:%%pidExt-14958:%%isOpenExch-2:"		// ixic
			+ "%%pid-166:%%pidExt-166:%%isOpenExch-1:"			// spx
			+ "%%pid-179:%%pidExt-179:%%isOpenExch-21:"			// hk50
			+ "%%pid-1043109:%%isOpenExch-1043109:%%isOpenExch-1002:"	// 美油
			+ "%%pid-68:%%isOpenExch-68:%%isOpenExch-1002:"		// xau
			+ "%%pid-172:%%isOpenExch-172:%%isOpenExch-4:"		// dax30
//			+ "%%pid-175:%%isOpenExch-175:%%isOpenExch-4:"
//			+ "%%pid-37426:%%isOpenExch-37426:%%isOpenExch-60:"
			+ "%%pid-8833:%%isOpenPair-8833:"						// 特伦布原油
//			+ "%%pid-28930:%%pidExt-28930:%%isOpenExch-54:"		// a50
//			+ "%%pid-39929:%%isOpenExch-39929:%%isOpenExch-74:"
			+ "\\\"}\"]";
	public static final String initMsg = "[\"{\\\"_event\\\":\\\"UID\\\",\\\"UID\\\":0}\"]";
	
	private int heartbeat = 2;	// 0代表连接断开或者异常 1代表已连接.2代表正在连接
	
	public GetStockClient(URI serverUri) {
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
		GetStockClient.spyMemcachedClient = client;
		GetStockClient.bqPriceIndexCacheService = cacheService;
		GetStockClient.transTypeService = transTypeService;
	}
	
	public boolean isAlive() {
		return heartbeat > 0;
	}
	
	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		logger.info("GetStockClient关闭了");
		heartbeat = 0;
	}

	@Override
	public void onError(Exception e) {
		logger.info("GetStockClient异常： ", e);
		heartbeat = 0;
	}

	@Override
	public void onMessage(String message) {

		BigDecimal last = BigDecimal.ZERO;
		BigDecimal previous = BigDecimal.ZERO;
		BigDecimal high = BigDecimal.ZERO;
		BigDecimal low = BigDecimal.ZERO;
		
		String market = "Investing";
		
		List<TickerVo> tickers = new ArrayList<TickerVo>();
		
//		logger.info(message);

		if (!StringUtil.isEmpty(message)) {
			// 解析message获取last_numeric数值，放入缓存 
			//a["{\"message\":\"pid-28930::{\\\"pid\\\":\\\"28930\\\",\\\"last_dir\\\":\\\"greenBg\\\",\\\"last_numeric\\\":12976.06,\\\"last\\\":\\\"12,976.06\\\",\\\"bid\\\":\\\"0.00    \\\",\\\"ask\\\":\\\"0.00    \\\",\\\"high\\\":\\\"13,236.02\\\",\\\"low\\\":\\\"12,879.60\\\",\\\"last_close\\\":\\\"12,879.60\\\",\\\"pc\\\":\\\"+164.64\\\",\\\"pcp\\\":\\\"+1.28%\\\",\\\"pc_col\\\":\\\"greenFont\\\",\\\"time\\\":\\\"2:28:44\\\",\\\"timestamp\\\":1551666480}\"}"]
			//a["{\"message\":\"pid-44486::{\\\"pid\\\":\\\"44486\\\",\\\"last_dir\\\":\\\"greenBg\\\",\\\"last_numeric\\\":13789   ,\\\"last\\\":\\\"13,789.0 \\\",\\\"bid\\\":\\\"13,783.0\\\",\\\"ask\\\":\\\"13,795.0\\\",\\\"high\\\":\\\"13,790. 5\\\",\\\"low\\\":\\\"13,519. 0\\\",\\\"pc\\\":\\\"+186.5\\\",\\\"pcp\\\":\\\"+1.37%\\\",\\\"pc_col\\\":\\\"greenFont\\\",\\\"time\\\":\\\"3:20:42\\\",\\\"timestamp\\\":1555384842}\"}"]
			// 解析成JsonObject
			if (message.contains("last_numeric")) {
				message = message.replace("\\", "");
				if (message.contains("::")) {
					message = message.substring(message.indexOf("::") + 2);
				}
				if (message.length() > 4)
					message = message.substring(0, message.length() - 4);
				
				do {
					try {
						JSONObject data = new JSONObject(message);
						String pid = data.getString("pid");
	
						String code = null;
						// 上证-40820， 日经-178，道琼斯：169，纳斯达克：14958
						// 富时100：27，法国CAC40指数：167，德国DAX30指数：172，欧洲斯托克50指数：175，韩国KOSPI指数：37426，印度孟买30指数：39929
						if ("40820".equals(pid)) {
							code = "sh000001";
						} else if ("178".equals(pid)) {
							code = "nky";
						} else if ("169".equals(pid)) {
							code = "dji";
							market = "NYSE";
						} else if ("14958".equals(pid)) {
							code = "ixic";
						} else if ("27".equals(pid)) {
							code = "uk100";
						} else if ("167".equals(pid)) {
							code = "france40";
						} else if ("172".equals(pid)) {
							code = "dax30";
						} else if ("175".equals(pid)) {
							code = "eustoxx50";
						} else if ("37426".equals(pid)) {
							code = "kospi";
						} else if ("39929".equals(pid)) {
							code = "sensex";
						} else if ("8833".equals(pid)) {
							code = "oil";
							market = "ICE-EURO";
						} else if ("28930".equals(pid)) {
							code = "a50";
							market = "SGX";
						} else if ("69".equals(pid)) {
							code = "xag";
						} else if ("166".equals(pid)) {
							code = "spx";
						} else if ("179".equals(pid)) {
							code = "hk50";
						} else if ("1043109".equals(pid)) {
							code = "usoil";
						} else if ("1043109".equals(pid)) {
							code = "usoil";
						} else if ("68".equals(pid)) {
							code = "xau";
						}
						
						if (code == null) return;
						
						double lastDou = data.getDouble("last_numeric");
//						logger.info(code + "---" + lastDou);
						
						BQTransactionType transactionType = findCurrTrans(code);
						if (transactionType == null) {
							return;
						}
						
						Integer digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.DIGITS);
						if (digits == null) {
							digits = Utils.getDigitsFromMinFloat(transactionType.getMin_float_price());
							spyMemcachedClient.set(code + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
						}
						
						
						String preColseStr = "0";
						String highStr = data.getString("high");
						String lowStr = data.getString("low");
						String pcp = data.getString("pcp");
						
						highStr = highStr.replace(",", "");
						lowStr = lowStr.replace(",", "");
						if (data.has("last_close")) {
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
							
							spyMemcachedClient.set(BTCQMemecacheKeys.INDEX_FUDU + code, BTCQMemecacheKeys.TIMES.ONE_DAY, pcp);
						}
						
						if (previous.compareTo(BigDecimal.ZERO) <= 0) {
							previous = last;
						}
						
						if (previous.compareTo(BigDecimal.ZERO) > 0) {
							// 昨日收盘价
							spyMemcachedClient.set(BTCQMemecacheKeys.PRECLOSE + code, BTCQMemecacheKeys.TIMES.ONE_DAY, previous);
						} else {
//							if (previous.compareTo(BigDecimal.ZERO) <= 0) {
//								BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + code);
//								if (preClose != null && preClose.compareTo(BigDecimal.ZERO) > 0) {
//									previous = preClose;
//								}
//							}
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
							ticker.setMarket(market);
							ticker.setTypeId(transactionType.getType_id());
							ticker.setTypeCode(code);
							
							tickers.add(ticker);
							for (TickerVo vo : tickers) {
								spyMemcachedClient.set(vo.getTypeCode() + vo.getMarket(), BTCQMemecacheKeys.TIMES.BPI, last);
								bqPriceIndexCacheService.updateBtcCache(vo, last);
							}
							
							spyMemcachedClient.set(code, BTCQMemecacheKeys.TIMES.BPI, last);
							spyMemcachedClient.set(BTCQMemecacheKeys.TICKER + code, BTCQMemecacheKeys.TIMES.BPI, tickers);
							
							JSONObject obj = new JSONObject();
							obj.put("close", ticker.getLast());
							obj.put("high", ticker.getHigh());
							obj.put("low", ticker.getLow());
							obj.put("open", ticker.getOpen());
							obj.put("lastClose", ticker.getLastUsd());
							obj.put("time", ticker.getUpdateTime());
							obj.put("date", ticker.getDate());
							
							spyMemcachedClient.set(BTCQMemecacheKeys.CFD_INDEX + code, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, obj.toString());
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				} while(false);
			}
		}
	}

	@Override
	public void onOpen(ServerHandshake shake) {
		logger.info("GetStockClient onOpen ...");
		heartbeat = 1;
		
		send(bulkSub);
		send(initMsg);
	}
	
	private static BQTransactionType findCurrTrans(String scode) {
		List<BQTransactionType> types = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES);
		if (types == null) {
			types = transTypeService.findAllUsing();
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANS_TYPES,BTCQMemecacheKeys.TIMES.TEN_SECOND, types);
		}
		
		if (types != null) {
			for (BQTransactionType type : types) {
				if (type.getType_code().equals(scode)) {
					return type;
				}
			}
		}

		return null;
	}

}
