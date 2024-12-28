package com.btc100.btc_q.timerTask.ws;

import com.btc100.btc_q.action.websocket.WebSocket;

/**
 * WebSocket api 数据
 * 
 * @author Administrator
 * 
 */
public class TimerTaskPushWebsocketApi {

	private static int COUNT_ALIVE_DOWN_TIMES = 5;
	
	private int countAliveDown = COUNT_ALIVE_DOWN_TIMES;
	
	public void run() {
		
		WebSocket.checkPushNewOrderPending();
		WebSocket.checkPushOrderTrade();
		WebSocket.checkPushOrderPending();
		
		WebSocket.checkDPushNewOrderPending();
		WebSocket.checkDPushOrderTrade();
		WebSocket.checkDPushOrderPending();
		
		countAliveDown--;
		if (countAliveDown <= 0) {
			countAliveDown = COUNT_ALIVE_DOWN_TIMES;
			WebSocket.checkPushAlive();
		}
		
	}
	
}