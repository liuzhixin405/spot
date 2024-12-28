package com.btc100.btc_q.timerTask.ws;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.action.websocket.WebSocket;
import com.btc100.btc_q.util.StringUtil;

/**
 * WebSocket 盘口深度 数据
 * 
 * @author Administrator
 * 
 */

public class TimerTaskDepthWebsocketApi {
	
	public void run() {
		
		Map<String, JSONObject> depthMap = new HashMap<String, JSONObject>();
    	long time = System.currentTimeMillis() / 1000;
    	
    	for (WebSocket item: WebSocket.webSocketSet) {
    		for (String subDepth : item.subDepthList) {
	    		if (StringUtil.isEmpty(subDepth)) {
	    			continue;
	    		}
	    		
    			JSONObject msgObj = new JSONObject();
    	    	msgObj.put("ts", time);
    			msgObj.put("ch", subDepth);
    			
    			String[] subs = subDepth.split("[.]");
    			if (subs.length != 4) continue;
    			
    			String contractType = subs[1];
    			String code = subs[2];
    			
    			if (StringUtil.isEmpty(contractType) || StringUtil.isEmpty(code)) continue;
    			
    			JSONObject data = null;
    			if (depthMap.containsKey(subDepth)) {
    				data = depthMap.get(subDepth);
    			} else {
	    			data = WebSocket.getDepth(contractType, code);
    			}
    			
    			if (data == null) continue;
    			
    			try {
    				msgObj.put("data", data);
    				item.sendMessage(msgObj.toString());
    			} catch (Exception e) {
    				//e.printStackTrace();
    			}
    		}
    	}
	}
}
