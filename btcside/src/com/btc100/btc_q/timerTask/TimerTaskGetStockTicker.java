package com.btc100.btc_q.timerTask;

import java.net.URI;

import org.apache.log4j.Logger;
import org.java_websocket.WebSocket.READYSTATE;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.action.websocket.GetStockClient;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionTypeService;

/**
 * 获取股指行情
 * 
 * @author Administrator
 *
 */
public class TimerTaskGetStockTicker {

	private static final Logger logger = Logger.getLogger(TimerTaskGetStockTicker.class);
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQTransactionTypeService transTypeService;

	private static GetStockClient client = null;

	public static final String url = "wss://stream327.forexpros.com/echo/619/lbudy238/websocket";// 请求的服务端路径地址
	public static final String data = "[\"{\\\"_event\\\":\\\"heartbeat\\\",\\\"data\\\":\\\"h\\\"}\"]";//

	public void connect() throws Exception {
		
		logger.info("ws connect");
		
		if (client != null) client.close();
		
		client = new GetStockClient(new URI(url));
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
