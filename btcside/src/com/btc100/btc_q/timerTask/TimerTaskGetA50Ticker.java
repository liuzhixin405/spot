package com.btc100.btc_q.timerTask;

import java.net.URI;

import org.apache.log4j.Logger;
import org.java_websocket.WebSocket.READYSTATE;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.action.websocket.GetA50Client;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionTypeService;

/**
 * 获取A50行情
 * 
 * @author Administrator
 *
 */
public class TimerTaskGetA50Ticker {

	private static final Logger logger = Logger.getLogger(TimerTaskGetA50Ticker.class);
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQTransactionTypeService transTypeService;

	private static GetA50Client client = null;

	// wss://stream338.forexpros.com/echo/645/wpdn9ogl/websocket
	public static final String url = "wss://stream17.forexpros.com/echo/318/6cbuw1zm/websocket";// 请求的服务端路径地址
	public static final String data = "[\"{\\\"_event\\\":\\\"heartbeat\\\",\\\"data\\\":\\\"h\\\"}\"]";//

	public void connect() throws Exception {
		
		logger.info("ws connect");
		
		if (client != null) client.close();
		
		client = new GetA50Client(new URI(url));
		client.setBean(spyMemcachedClient, bqPriceIndexCacheService, transTypeService);
		client.connect();
	}

	public void send() throws Exception {
		client.send(data);
	}

	public void run() {
		try {
			if (client == null || !client.isAlive()) {
				connect();
			}
			
			if (client != null && client.getReadyState().equals(READYSTATE.OPEN)) {
				send();
			}
			
			/*
			String url = UrlConfig.getValue("strA50CFDApiSina");
			// "12422.50,1.2400,0.00,0.00,12449.00,12190.50,16:29:59,12270.00,12270.00,0,0,0,2019-03-11,富时中国A50指数期货"
			String strResult = HttpToServer.sendGet(url, "");
			if (!StringUtil.isEmpty(strResult)) {
				String[] values = strResult.split("\"");
				if (values.length == 3) {
					strResult = values[1];
					String[] items = strResult.split(",");
					if (items.length == 14) {
						String close = items[0];
						String high = items[4];
						String low = items[5];
						String open = items[7];
						String lastClose = items[8];
						String time = items[6];
						String date = items[12];
						
						JSONObject obj = new JSONObject();
						obj.put("close", Double.valueOf(close));
						obj.put("high", Double.valueOf(high));
						obj.put("low", Double.valueOf(low));
						obj.put("open", Double.valueOf(open));
						obj.put("lastClose", Double.valueOf(lastClose));
						obj.put("time", time);
						obj.put("date", date);
						
						spyMemcachedClient.set(BTCQMemecacheKeys.CFD_INDEX + "a50", BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, obj.toJSONString());
					}
				}
			}
			
			*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
