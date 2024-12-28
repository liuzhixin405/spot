package com.btc100.btc_q.timerTask.openws.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.action.websocket.open.v1.OpenWsServer;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.Top5DelegationVO;

public class TimerTaskOpenWsDepthV1 {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	private static int COUNT_ALIVE_DOWN_TIMES = 5;
	
	private int countAliveDown = COUNT_ALIVE_DOWN_TIMES;
	
	public void run() {
		
		OpenWsServer.setMemClient(spyMemcachedClient);
		
		Map<String, String> depthMap = new HashMap<String, String>();
		long time = System.currentTimeMillis() / 1000;
		
		// market.$contractType.$code.depth
		for (OpenWsServer item: OpenWsServer.webSocketSet) {
			
			countAliveDown--;
			if (countAliveDown <= 0) {
				countAliveDown = COUNT_ALIVE_DOWN_TIMES;
				OpenWsServer.checkAlive();
			}
			
    		for (String subDepth : item.subDepthList) {
    			String[] subs = subDepth.split("[.]");
    			if (subs.length != 4) continue;
    			
    			JSONObject msgObj = new JSONObject();
    	    	msgObj.put("ts", time);
    			msgObj.put("ch", subDepth);
    			
    			String contractType = subs[1];
    			String symbol = subs[2];
    			
    			String data = "";
    			if (depthMap.containsKey(subDepth)) {
    				data = depthMap.get(subDepth);
    			} else {
    				List<Top5DelegationVO> buytopList = null;
    				List<Top5DelegationVO> selltopList = null;
	    			if (contractType == null || "".equals(contractType) || "contract".equals(contractType)) {
	    				buytopList = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.BUYTOP5LIST);
	    				selltopList = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.SELLTOP5LIST);
	    			} else if ("vcontract".equals(contractType)) {
	    				buytopList = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.V_BUYTOP5LIST);
	    				selltopList = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.V_SELLTOP5LIST);
	    			} else if ("delivery".equals(contractType)) {
	    				buytopList = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.D_BUYTOP5LIST);
	    				selltopList = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.D_SELLTOP5LIST);
	    			} else if ("spot".equals(contractType)) {
	    				buytopList = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.BUYTOP5LIST);
	    				selltopList = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.SELLTOP5LIST);
	    			} else {
	    				buytopList = spyMemcachedClient.get(contractType + "_" + symbol + BTCQMemecacheKeys.BUYTOP5LIST);
	    				selltopList = spyMemcachedClient.get(contractType + "_" + symbol + BTCQMemecacheKeys.SELLTOP5LIST);
	    				if (buytopList == null || selltopList == null) continue;
	    			}
	    			
	    			JSONArray buyArray = new JSONArray();
	    			for (Top5DelegationVO vo :  buytopList) {
	    				JSONArray order = new JSONArray();
	    				order.add(vo.getPrice().doubleValue());
	    				order.add(vo.getCountNum().doubleValue());
	    				buyArray.add(order);
	    			}
	    			
	    			JSONArray sellArray = new JSONArray();
	    			for (Top5DelegationVO vo :  selltopList) {
	    				JSONArray order = new JSONArray();
	    				order.add(vo.getPrice().doubleValue());
	    				order.add(vo.getCountNum().doubleValue());
	    				sellArray.add(order);
	    			}
	    			
	    			JSONObject dataOjb = new JSONObject();
    				dataOjb.put("bids", buyArray);
    				dataOjb.put("asks", sellArray);
	    			
    				data = dataOjb.toString();
	    			depthMap.put(subDepth, data);
    			}
    			
    			try {
    				if (!StringUtil.isEmpty(data)) {
    					msgObj.put("tick", JSONObject.parse(data));
    					item.sendMessage(msgObj.toString());
    				}
    			} catch (Exception e) {
    			}
    		}
		}
	}
}
