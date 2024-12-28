package com.btc100.btc_q.action.websocket;

import java.math.BigDecimal;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.TickerVo;

/**
 * BIGI行情获取股指期货指数
 * @author win7
 *
 */
public class GetStockFuturesClient extends WebSocketClient {
	
	private static final Logger logger = Logger.getLogger(GetStockFuturesClient.class);

	private static SpyMemcachedClient spyMemcachedClient;
	private static BQPriceIndexCacheService bqPriceIndexCacheService;
	private static BQTransactionTypeService transTypeService;
	
	/*** 登录请求 */
//	public static final String login = "0=login|1=1|2=A8C7E85B958645419910F0E07F1D08F0\n"; // 测试账户
	public static final String login = "0=login|1=20040703|2=0143C1CF42F14D3C9ADBCEEDBE41B4F4\n"; // 正式账户
	/*** 订阅数据 */
	public static final String sub = "0=subscribe|1=YMML,A50ML,LLG,CNA50ML";
	
	private int heartbeat = 2;	// 0代表连接断开或者异常 1代表已连接.2代表正在连接
	
	public GetStockFuturesClient(URI serverUri) {
		super(serverUri);
	}

	public void setBean(SpyMemcachedClient client, BQPriceIndexCacheService cacheService, BQTransactionTypeService transTypeService) {
		GetStockFuturesClient.spyMemcachedClient = client;
		GetStockFuturesClient.bqPriceIndexCacheService = cacheService;
		GetStockFuturesClient.transTypeService = transTypeService;
	}
	
	public boolean isAlive() {
		return heartbeat > 0;
	}
	
	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		logger.info("GetFuturesClient关闭了");
		heartbeat = 0;
	}

	@Override
	public void onError(Exception e) {
		logger.info("GetFuturesClient异常： ", e);
		heartbeat = 0;
	}

	@Override
	public void onMessage(String message) {
//		logger.info(message);

		if (!StringUtil.isEmpty(message)) {
			if (message.contains("ok")) {
				logger.info(message);
				send(login);
			} else if (message.contains("login")) {
				logger.info(message);
				send(sub);
			} else if (message.contains("hq")) {
				handle(message);
			}
		}
	}

	@Override
	public void onOpen(ServerHandshake shake) {
		logger.info("GetFuturesClient onOpen ...");
		heartbeat = 1;
	}
	
	private void handle(String message) {
		if (StringUtil.isEmpty(message)) return;
		
		BigDecimal last = BigDecimal.ZERO;
		BigDecimal previous = BigDecimal.ZERO; // 昨日收盘价
		BigDecimal high = BigDecimal.ZERO;
		BigDecimal low = BigDecimal.ZERO;
//		BigDecimal preSettle = BigDecimal.ZERO; // 昨日结算价
		
		String market = "BIGI";
		
		List<TickerVo> tickers = new ArrayList<TickerVo>();
		do {
			try {
				String[] data = message.split("\\|");
				if (data.length < 4) return;
				
				String symbol = data[2].substring(data[2].indexOf("=") + 1);
				String code = null;
				if ("BRNML".equals(symbol)) {
					code = "oil";
					market = "ICE-EURO";
				} else if ("A50ML".equals(symbol)) { // 测试账户对应A50代码
					code = "a50";
					market = "SGX";
				} else if ("CNA50ML".equals(symbol)) { // 正式账户对应A50代码
					code = "a50";
					market = "SGX";
				} else if ("YMML".equals(symbol)) {
					code = "dji";
					market = "NYSE";
				} else if ("LLG".equals(symbol)) {
					code = "xau";
					market = "LONDON-BANK";
				}
				
				if (code == null) return;
				
				String lastStr = data[3].substring(data[3].indexOf("=") + 1);
				String timeStr = data[4].substring(data[4].indexOf("=") + 1);
				
//				logger.info(code + "-----last-----" + lastStr);
				
				BQTransactionType transactionType = findCurrTrans(code);
				if (transactionType == null) {
					return;
				}
				
				Integer digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.DIGITS);
				if (digits == null) {
					digits = Utils.getDigitsFromMinFloat(transactionType.getMin_float_price());
					spyMemcachedClient.set(code + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
				
				String highStr = data[12].substring(data[12].indexOf("=") + 1);
				String lowStr = data[13].substring(data[13].indexOf("=") + 1);
				String preColseStr = data[14].substring(data[14].indexOf("=") + 1); // 昨日收盘价
//				String preColseSt = data[16].substring(data[16].indexOf("=") + 1); // 昨日结算价
				
				last = new BigDecimal(lastStr).setScale(digits, BigDecimal.ROUND_HALF_UP);
				previous = new BigDecimal(preColseStr).setScale(digits, BigDecimal.ROUND_HALF_UP);
				high = new BigDecimal(highStr).setScale(digits, BigDecimal.ROUND_HALF_UP);
				low = new BigDecimal(lowStr).setScale(digits, BigDecimal.ROUND_HALF_UP);
//				preSettle = new BigDecimal(preSettleStr).setScale(digits, BigDecimal.ROUND_HALF_UP); // 昨日结算价（期货才有）
				
//				logger.info(code + "------昨日收盘价------" + previous);
				
				if (previous.compareTo(BigDecimal.ZERO) > 0) {
					// 昨日收盘价
					spyMemcachedClient.set(BTCQMemecacheKeys.PRECLOSE + code, BTCQMemecacheKeys.TIMES.ONE_DAY, previous);
				} else {
					if (previous.compareTo(BigDecimal.ZERO) <= 0) {
						BigDecimal preClose = spyMemcachedClient.get(BTCQMemecacheKeys.PRECLOSE + code);
						if (preClose != null && preClose.compareTo(BigDecimal.ZERO) > 0) {
							previous = preClose;
							spyMemcachedClient.set(BTCQMemecacheKeys.PRECLOSE + code, BTCQMemecacheKeys.TIMES.ONE_DAY, previous);
						}
					}
				}
				
				if (last.compareTo(BigDecimal.ZERO) > 0 && previous.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal indexFudu = last.subtract(previous).divide(previous, 4, BigDecimal.ROUND_HALF_UP);
					DecimalFormat df = new DecimalFormat("0.00");
					String fudu = df.format(indexFudu.doubleValue() * 100) + "%";
//					logger.info(code + "======fudu======" + fudu);
					spyMemcachedClient.set(BTCQMemecacheKeys.INDEX_FUDU + code, BTCQMemecacheKeys.TIMES.ONE_DAY, fudu);
				}
				
				long time = ConvertUtil.toLong(timeStr, System.currentTimeMillis() / 1000);
				
				if (last.compareTo(BigDecimal.ZERO) > 0) {
					TickerVo ticker = new TickerVo();
					
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
			
		} while (false);
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
