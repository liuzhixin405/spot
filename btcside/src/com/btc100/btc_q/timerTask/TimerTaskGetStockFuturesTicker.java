package com.btc100.btc_q.timerTask;

import java.net.URI;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.action.websocket.GetStockFuturesClient;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionTypeService;

/**
 * 获取A50行情
 * 
 * @author Administrator
 *
 */
public class TimerTaskGetStockFuturesTicker {

	private static final Logger logger = Logger.getLogger(TimerTaskGetStockFuturesTicker.class);
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private BQTransactionTypeService transTypeService;

	private static GetStockFuturesClient client = null;

	/*** 请求的服务端路径地址 */
//	public static final String url = "ws://demo.hq.sumbrisk.com:8090/"; // 测试地址
	public static final String url = "ws://47.112.136.89:8090/"; // 正式地址
	
	public void connect() throws Exception {
		
		logger.info("GetFuturesClient connect");
		
		if (client != null) client.close();
		
		client = new GetStockFuturesClient(new URI(url));
		client.setBean(spyMemcachedClient, bqPriceIndexCacheService, transTypeService);
		client.connect();
	}

	public void send() throws Exception {
//		client.send(data);
	}

	public void run() {
		try {
			if (client == null || !client.isAlive()) {
				connect();
			}
			
//			if (client != null && client.getReadyState().equals(READYSTATE.OPEN)) {
//				send();
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
