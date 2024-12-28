package com.btc100.btc_q.timerTask.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.action.websocket.WebSocket;
import com.btc100.btc_q.util.StringUtil;

/**
 * WebSocket api 数据
 * 
 * @author Administrator
 * 
 */
public class TimerTaskKLineWebsocketApi {

	private static String[] intervals = { "1min", "5min", "15min", "30min", "1hour", "2hour", "4hour", "6hour", "1day", "1week", "1month" };
	
	public void run() {
		Map<String, JSONArray> klineMap = new HashMap<String, JSONArray>();
		List<String> intervalList = new ArrayList<String>(Arrays.asList(intervals));
		
    	long time = System.currentTimeMillis() / 1000;
    	
    	for (WebSocket item: WebSocket.webSocketSet) {
    		for (String subKline : item.subKlineList) {
	    		if (!StringUtil.isEmpty(subKline)) {
	    			String klineType = subKline;
	    			JSONObject msgObj = new JSONObject();
	    	    	msgObj.put("ts", time);
	    			msgObj.put("ch", klineType);
	    			
	    			String[] subs = klineType.split("[.]");
	    			if (subs.length != 4) continue;
	    			
	    			String contractType = subs[1];
	    			String code = subs[2];
	    			String interval = subs[3];
	    			
	    			if (StringUtil.isEmpty(contractType) || StringUtil.isEmpty(code) || StringUtil.isEmpty(interval)) continue;
	    			if (!intervalList.contains(interval)) continue;
	    			
	    			JSONArray data = null;
	    			if (klineMap.containsKey(klineType)) {
	    				data = klineMap.get(klineType);
	    			} else {
		    			if (contractType == null || "".equals(contractType) || "contract".equals(contractType)) {
		    				data = WebSocket.getKline(code, interval, true);
		    			} else if ("vcontract".equals(contractType)) {
		    				data = WebSocket.getVKline(code, interval, true);
		    			} else if ("delivery".equals(contractType)) {
		    				data = WebSocket.getDKline(code, interval, true);
		    			} else if ("spot".equals(contractType)) {
		    				data = WebSocket.getSpotKline(code, interval, true);
		    			} else {
		    				data = WebSocket.getNKline(contractType, code, interval, true);
		    			}
		    			
		    			if (data == null) continue;
	    				klineMap.put(klineType, data);
	    			}
	    			
	    			if (data == null) continue;
	    			
	    			try {
	    				JSONObject dataObj = new JSONObject();
	    				dataObj.put("data", data);
	    				msgObj.put("data", dataObj);
	    				item.sendMessage(msgObj.toString());
	    			} catch (Exception e) {
	    				//e.printStackTrace();
	    			}
	    			
	    		}
    		}
    	}
	}
}
