package com.btc100.btc_q.timerTask.openws.v1;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.action.websocket.open.v1.OpenWsServer;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.service.DeliveryMemberTradeDetailService;
import com.btc100.btc_q.service.MemberTradeDetailService;
import com.btc100.btc_q.service.NContractPartitionService;
import com.btc100.btc_q.service.NMemberTradeDetailService;
import com.btc100.btc_q.service.SpotMemberTradeDetailService;
import com.btc100.btc_q.service.VMemberTradeDetailService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.RandomUtil;

public class TimerTaskOpenWsTradeV1 {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private MemberTradeDetailService memberTradeDetailService;
	@Autowired
	private VMemberTradeDetailService vMemberTradeDetailService;
	@Autowired
	private SpotMemberTradeDetailService spotMemberTradeDetailService;
	@Autowired
	private DeliveryMemberTradeDetailService dMemberTradeDetailService;
	@Autowired
	private NMemberTradeDetailService nMemberTradeDetailService;
	@Autowired
	private NContractPartitionService nContractPartitionService;
	
	private static String indenty = "";
	
	public void run() {
		
		if (indenty.isEmpty()) {
			indenty = RandomUtil.getTransNo();
		}
		
		Map<String, Long> lastTradeIdMap = spyMemcachedClient.get("timetask_open_ws_trade_v1_" + indenty);
		if (lastTradeIdMap == null) {
			lastTradeIdMap = new HashMap<>();
		}
		Map<String, Long> newTradeIdMap = new HashMap<>();
		Map<String, JSONArray> tradeMap = new HashMap<String, JSONArray>();
		
		long time = System.currentTimeMillis() / 1000;
		// market.$contractType.$code.trade
		for (OpenWsServer item: OpenWsServer.webSocketSet) {
    		for (String subTrade : item.subTradeList) {
    			String[] subs = subTrade.split("[.]");
    			if (subs.length != 4) continue;
    			
    			JSONObject msgObj = new JSONObject();
    	    	msgObj.put("ts", time);
    			msgObj.put("ch", subTrade);
    			
    			String contractType = subs[1];
    			String symbol = subs[2];
    			
    			JSONArray data = new JSONArray();
    			if (tradeMap.containsKey(subTrade)) {
    				data = tradeMap.get(subTrade);
    			} else {
	    			if (contractType == null || "".equals(contractType) || "contract".equals(contractType)) {
	    				if (!newTradeIdMap.containsKey(subTrade)) {
	    					if (lastTradeIdMap.containsKey(subTrade)) {
	    						Long lastId = lastTradeIdMap.get(subTrade);
	    						List<Object[]> list = memberTradeDetailService.findNextFromId(symbol, lastId, 300);
	    						if (list.size() > 0) {
	    							for (int i = 0; i < list.size(); i++) {
	    								Object[] objs = list.get(i);
	    								if (i == 0) {
	    									lastId = Long.parseLong(objs[3].toString());
	    								}
	    								
	    								JSONObject json = new JSONObject();
	    								BigDecimal amount = new BigDecimal(objs[2].toString());
	    								BigDecimal price = new BigDecimal(objs[1].toString());

	    								json.put("date", objs[0]); 	// trade_time
	    								json.put("price", price); 	// trade_average_price
	    								json.put("amount", amount); // trade_count
	    								json.put("tid", objs[3]); 	// trade_id
	    								String type = "";
	    								if (objs[4].toString().equals("1"))
	    									type = "buy";
	    								if (objs[4].toString().equals("2"))
	    									type = "sell";

	    								int buyID = ConvertUtil.toInt(objs[5], 0);
	    								int sellID = ConvertUtil.toInt(objs[6], 0);

	    								BigDecimal zc = BigDecimal.ZERO;
	    								if (buyID == 1 || buyID == 3) { // 买者开仓
	    									if (sellID == 1 || sellID == 3) { // 卖者开仓
	    										zc = zc.add(amount.multiply(new BigDecimal(2)));
	    									}
	    								} else {
	    									if (sellID == 2 || sellID == 4) { // 卖者开仓
	    										zc = zc.subtract(amount.multiply(new BigDecimal(2)));
	    									}
	    								}

	    								json.put("type", type); // trade_trans_dirction
	    								json.put("change", zc);
	    								
	    								data.add(json);
	    							}
	    						}
	    						
	    						newTradeIdMap.put(subTrade, lastId);
	    					} else {
	    						long lastId = 0;
	    						List<Object[]> list = memberTradeDetailService.findNextFromId(symbol, 0, 1);
	    						if (list.size() > 0) {
	    							lastId = Long.parseLong(list.get(0)[3].toString());
	    						}
	    						newTradeIdMap.put(subTrade, lastId);
	    					}
	    					
	    					tradeMap.put(subTrade, data);
	    				}
	    			} else if ("vcontract".equals(contractType)) {
	    				if (!newTradeIdMap.containsKey(subTrade)) {
	    					if (lastTradeIdMap.containsKey(subTrade)) {
	    						Long lastId = lastTradeIdMap.get(subTrade);
	    						List<Object[]> list = vMemberTradeDetailService.findNextFromId(symbol, lastId, 300);
	    						if (list.size() > 0) {
	    							for (int i = 0; i < list.size(); i++) {
	    								Object[] objs = list.get(i);
	    								if (i == 0) {
	    									lastId = Long.parseLong(objs[3].toString());
	    								}
	    								
	    								JSONObject json = new JSONObject();
	    								BigDecimal amount = new BigDecimal(objs[2].toString());
	    								BigDecimal price = new BigDecimal(objs[1].toString());

	    								json.put("date", objs[0]); 	// trade_time
	    								json.put("price", price); 	// trade_average_price
	    								json.put("amount", amount); // trade_count
	    								json.put("tid", objs[3]); 	// trade_id
	    								String type = "";
	    								if (objs[4].toString().equals("1"))
	    									type = "buy";
	    								if (objs[4].toString().equals("2"))
	    									type = "sell";

	    								int buyID = ConvertUtil.toInt(objs[5], 0);
	    								int sellID = ConvertUtil.toInt(objs[6], 0);

	    								BigDecimal zc = BigDecimal.ZERO;
	    								if (buyID == 1 || buyID == 3) { // 买者开仓
	    									if (sellID == 1 || sellID == 3) { // 卖者开仓
	    										zc = zc.add(amount.multiply(new BigDecimal(2)));
	    									}
	    								} else {
	    									if (sellID == 2 || sellID == 4) { // 卖者开仓
	    										zc = zc.subtract(amount.multiply(new BigDecimal(2)));
	    									}
	    								}

	    								json.put("type", type); // trade_trans_dirction
	    								json.put("change", zc);
	    								
	    								data.add(json);
	    							}
	    						}
	    						
	    						newTradeIdMap.put(subTrade, lastId);
	    					} else {
	    						long lastId = 0;
	    						List<Object[]> list = vMemberTradeDetailService.findNextFromId(symbol, 0, 1);
	    						if (list.size() > 0) {
	    							lastId = Long.parseLong(list.get(0)[3].toString());
	    						}
	    						
	    						newTradeIdMap.put(subTrade, lastId);
	    					}
	    					
	    					tradeMap.put(subTrade, data);
	    				}
	    				
	    			} else if ("delivery".equals(contractType)) {
	    				if (!newTradeIdMap.containsKey(subTrade)) {
	    					if (lastTradeIdMap.containsKey(subTrade)) {
	    						Long lastId = lastTradeIdMap.get(subTrade);
	    						List<Object[]> list = dMemberTradeDetailService.findNextFromId(symbol, lastId, 300);
	    						if (list.size() > 0) {
	    							for (int i = 0; i < list.size(); i++) {
	    								Object[] objs = list.get(i);
	    								if (i == 0) {
	    									lastId = Long.parseLong(objs[3].toString());
	    								}
	    								
	    								JSONObject json = new JSONObject();
	    								BigDecimal amount = new BigDecimal(objs[2].toString());
	    								BigDecimal price = new BigDecimal(objs[1].toString());

	    								json.put("date", objs[0]); 	// trade_time
	    								json.put("price", price); 	// trade_average_price
	    								json.put("amount", amount); // trade_count
	    								json.put("tid", objs[3]); 	// trade_id
	    								String type = "";
	    								if (objs[4].toString().equals("1"))
	    									type = "buy";
	    								if (objs[4].toString().equals("2"))
	    									type = "sell";

	    								int buyID = ConvertUtil.toInt(objs[5], 0);
	    								int sellID = ConvertUtil.toInt(objs[6], 0);

	    								BigDecimal zc = BigDecimal.ZERO;
	    								if (buyID == 1 || buyID == 3) { // 买者开仓
	    									if (sellID == 1 || sellID == 3) { // 卖者开仓
	    										zc = zc.add(amount.multiply(new BigDecimal(2)));
	    									}
	    								} else {
	    									if (sellID == 2 || sellID == 4) { // 卖者开仓
	    										zc = zc.subtract(amount.multiply(new BigDecimal(2)));
	    									}
	    								}

	    								json.put("type", type); // trade_trans_dirction
	    								json.put("change", zc);
	    								
	    								data.add(json);
	    							}
	    						}
	    						
	    						newTradeIdMap.put(subTrade, lastId);
	    					} else {
	    						long lastId = 0;
	    						List<Object[]> list = dMemberTradeDetailService.findNextFromId(symbol, 0, 1);
	    						if (list.size() > 0) {
	    							lastId = Long.parseLong(list.get(0)[3].toString());
	    						}
	    						
	    						newTradeIdMap.put(subTrade, lastId);
	    					}
	    					
	    					tradeMap.put(subTrade, data);
	    				}
	    				
	    			} else if ("spot".equals(contractType)) {
	    				if (!newTradeIdMap.containsKey(subTrade)) {
	    					if (lastTradeIdMap.containsKey(subTrade)) {
	    						Long lastId = lastTradeIdMap.get(subTrade);
	    						List<Object[]> list = spotMemberTradeDetailService.findNextFromId(symbol, lastId, 300);
	    						if (list.size() > 0) {
	    							for (int i = 0; i < list.size(); i++) {
	    								Object[] objs = list.get(i);
	    								if (i == 0) {
	    									lastId = Long.parseLong(objs[3].toString());
	    								}
	    								
	    								JSONObject json = new JSONObject();
	    								BigDecimal amount = new BigDecimal(objs[2].toString());
	    								amount.setScale(2, BigDecimal.ROUND_HALF_UP);
	    								BigDecimal price = new BigDecimal(objs[1].toString());
	    								price.setScale(2, BigDecimal.ROUND_HALF_UP);

	    								json.put("date", objs[0]); // trade_time
	    								json.put("price", price); // trade_average_price
	    								json.put("amount", amount); // trade_count
	    								json.put("tid", objs[3]); // trade_id
	    								String type = "";
	    								if (objs[4].toString().equals("1"))
	    									type = "buy";
	    								if (objs[4].toString().equals("2"))
	    									type = "sell";

	    								json.put("type", type); // trade_trans_dirction
	    								
	    								data.add(json);
	    							}
	    						}
	    						
	    						newTradeIdMap.put(subTrade, lastId);
	    					} else {
	    						long lastId = 0;
	    						List<Object[]> list = spotMemberTradeDetailService.findNextFromId(symbol, 0, 1);
	    						if (list.size() > 0) {
	    							lastId = Long.parseLong(list.get(0)[3].toString());
	    						}
	    						
	    						newTradeIdMap.put(subTrade, lastId);
	    					}
	    					
	    					tradeMap.put(subTrade, data);
	    				}
	    			} else {
	    				NContractPartition part = nContractPartitionService.readByPartition(contractType);
	    				if (part == null) {
	    					continue;
	    				}
	    				
	    				if (!newTradeIdMap.containsKey(subTrade)) {
	    					if (lastTradeIdMap.containsKey(subTrade)) {
	    						Long lastId = lastTradeIdMap.get(subTrade);
	    						List<Object[]> list = nMemberTradeDetailService.findNextFromId(contractType, symbol, lastId, 300);
	    						if (list.size() > 0) {
	    							for (int i = 0; i < list.size(); i++) {
	    								Object[] objs = list.get(i);
	    								if (i == 0) {
	    									lastId = Long.parseLong(objs[3].toString());
	    								}
	    								
	    								JSONObject json = new JSONObject();
	    								BigDecimal amount = new BigDecimal(objs[2].toString());
	    								BigDecimal price = new BigDecimal(objs[1].toString());

	    								json.put("date", objs[0]); 	// trade_time
	    								json.put("price", price); 	// trade_average_price
	    								json.put("amount", amount); // trade_count
	    								json.put("tid", objs[3]); 	// trade_id
	    								String type = "";
	    								if (objs[4].toString().equals("1"))
	    									type = "buy";
	    								if (objs[4].toString().equals("2"))
	    									type = "sell";

	    								int buyID = ConvertUtil.toInt(objs[5], 0);
	    								int sellID = ConvertUtil.toInt(objs[6], 0);

	    								BigDecimal zc = BigDecimal.ZERO;
	    								if (buyID == 1 || buyID == 3) { // 买者开仓
	    									if (sellID == 1 || sellID == 3) { // 卖者开仓
	    										zc = zc.add(amount.multiply(new BigDecimal(2)));
	    									}
	    								} else {
	    									if (sellID == 2 || sellID == 4) { // 卖者开仓
	    										zc = zc.subtract(amount.multiply(new BigDecimal(2)));
	    									}
	    								}

	    								json.put("type", type); // trade_trans_dirction
	    								json.put("change", zc);
	    								
	    								data.add(json);
	    							}
	    						}
	    						
	    						newTradeIdMap.put(subTrade, lastId);
	    					} else {
	    						long lastId = 0;
	    						List<Object[]> list = nMemberTradeDetailService.findNextFromId(contractType, symbol, 0, 1);
	    						if (list.size() > 0) {
	    							lastId = Long.parseLong(list.get(0)[3].toString());
	    						}
	    						newTradeIdMap.put(subTrade, lastId);
	    					}
	    					
	    					tradeMap.put(subTrade, data);
	    				}
	    			}
    			}
    			
    			try {
    				if (data.size() > 0) {
    					msgObj.put("tick", data);
    					item.sendMessage(msgObj.toString());
    				}
    			} catch (Exception e) {
    			}
    		}
		}
		
		spyMemcachedClient.set("timetask_open_ws_trade_v1_" + indenty, BTCQMemecacheKeys.TIMES.ONE_HOUR,  newTradeIdMap);
	}
}
