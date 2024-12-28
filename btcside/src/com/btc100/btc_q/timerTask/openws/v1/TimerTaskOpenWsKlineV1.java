package com.btc100.btc_q.timerTask.openws.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.action.websocket.WebSocket;
import com.btc100.btc_q.action.websocket.open.v1.OpenWsServer;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;

public class TimerTaskOpenWsKlineV1 {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	private static String[] intervals = { "1min", "5min", "15min", "30min", "1hour", "2hour", "4hour", "6hour", "1day", "1week", "1month" };
	
	public void run() {
		
		Map<String, String> oldKlineStrMap = new HashMap<String, String>();
		Map<String, JSONArray> klineMap = new HashMap<String, JSONArray>();
		List<String> intervalList = new ArrayList<String>(Arrays.asList(intervals));
    	long time = System.currentTimeMillis() / 1000;
    	
    	for (OpenWsServer item: OpenWsServer.webSocketSet) {
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
	    			
	    			if (!oldKlineStrMap.containsKey(klineType)) {
	    				String oldStr = spyMemcachedClient.get("last_openws_kline_" + klineType);
	    				if (oldStr == null) oldStr = "";
	    				
	    				oldKlineStrMap.put(klineType, oldStr);
	    			}
	    			
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
		    			
		    			String dataStr = data.toString();
		    			String oldDataStr = oldKlineStrMap.get(klineType);
		    			if (dataStr.equals(oldDataStr)) {
		    				data = new JSONArray();
		    			} else {
		    				spyMemcachedClient.set("last_openws_kline_" + klineType, BTCQMemecacheKeys.TIMES.ONE_HOUR, dataStr);
		    			}
		    			
		    			klineMap.put(klineType, data);
	    			}
	    			
	    			if (data == null) continue;
	    			
	    			try {
	    				if (data.size() > 0) {
	    					msgObj.put("tick", data);
	    					item.sendMessage(msgObj.toString());
	    				}
	    			} catch (Exception e) {
	    			}
	    		}
    		}
    	}
	}
}
