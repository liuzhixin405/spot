package com.btc100.btc_q.action.websocket.open.v1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.action.websocket.WebSocket;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.Top5DelegationVO;

@ServerEndpoint(value = "/v1/ws")
public class OpenWsServer {
	
	private static final Logger logger = Logger.getLogger(OpenWsServer.class);
	
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    public static CopyOnWriteArraySet<OpenWsServer> webSocketSet = new CopyOnWriteArraySet<OpenWsServer>();
    
    private static SpyMemcachedClient spyMemcachedClient = null;

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    
    public CopyOnWriteArraySet<String> subDepthList = new CopyOnWriteArraySet<>();
    public CopyOnWriteArraySet<String> subTradeList = new CopyOnWriteArraySet<>();
    public CopyOnWriteArraySet<String> subKlineList = new CopyOnWriteArraySet<>();
    
    private long lastGotPongTime = 0;
    private long lastPingValue1 = 0;
    private long lastPintValue2 = 0;
    
	public static void setMemClient(SpyMemcachedClient client) {
		spyMemcachedClient = client;
	}

    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session) {
    	System.out.println("session: " + session.getId());
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        lastGotPongTime = System.currentTimeMillis() / 1000;
        logger.info("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        logger.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
        	JSONObject messageObject = JSONObject.parseObject(message);
        	if (messageObject.containsKey("ping")) {
        		dealPing(messageObject);
        	} else if (messageObject.containsKey("pong")) {
        		dealPong(messageObject);
        	} else if (messageObject.containsKey("sub")) {
        		dealSub(messageObject);
        	} else if (messageObject.containsKey("unsub")) {
        		dealUnSub(messageObject);
        	} else if (messageObject.containsKey("req")) {
        		dealReq(messageObject);
        	}
		} catch (Exception e) {
//			e.printStackTrace();
		}
    }

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
		logger.info("发生错误");
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     * @param message
     * @throws IOException
     */
    public synchronized void sendMessage(String message) throws IOException{
//        this.session.getBasicRemote().sendText(message);
        this.session.getAsyncRemote().sendText(message);
    }
    
    public static void checkAlive() {
    	long time = System.currentTimeMillis() / 1000;
    	JSONObject msgObj = new JSONObject();
    	msgObj.put("ping", time);
    	for (OpenWsServer item: webSocketSet) {
    		item.lastPintValue2 = item.lastPingValue1;
    		item.lastPingValue1 = time;
    		if (item.lastGotPongTime > 0 && time - item.lastGotPongTime > 20) {
    			try {
					item.session.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    		} else {
				try {
					item.sendMessage(msgObj.toString());
				} catch (Exception e) {
					//e.printStackTrace();
				}
    		}
    	}
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
    	OpenWsServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
    	OpenWsServer.onlineCount--;
    }
    
    private void dealPong(JSONObject messageObject) {
    	long pongData = messageObject.getLong("pong");
    	if (pongData == this.lastPingValue1 || pongData == this.lastPintValue2) {
    		lastGotPongTime = System.currentTimeMillis() / 1000; 
    	}
    }
    
    private void dealPing(JSONObject messageObject) throws IOException {
    	String pingData = messageObject.getString("ping");
		
		JSONObject responObj = new JSONObject();
		responObj.put("pong", pingData);
		this.sendMessage(responObj.toString());
    }
    
    private void dealUnSub(JSONObject messageObject) throws IOException {
    	String subData = messageObject.getString("unsub");
    	if (this.subDepthList.contains(subData)) {
    		this.subDepthList.remove(subData);
    	}
    	
    	if (this.subTradeList.contains(subData)) {
    		this.subTradeList.remove(subData);
    	}
    	
    	if (this.subKlineList.contains(subData)) {
    		this.subKlineList.remove(subData);
    	}
    }
    
    private boolean checkNContractCode(String type, String code) {
    	List<NTransaction> transactionList = WebSocket.findNCurrTrans(type);
		if (transactionList != null) {
			for (NTransaction trans : transactionList) {
				if (trans.getTransaction_code().equals(code)) {
					return true;
				}
			}
		}
		
		return false;
    }
    
    private boolean checkContractCode(String code) {
    	List<BQTransaction> transactionList = WebSocket.findCurrTrans();
		if (transactionList != null) {
			for (BQTransaction trans : transactionList) {
				if (trans.getTransaction_code().equals(code)) {
					return true;
				}
			}
		}
		
		return false;
    }
    
    private boolean checkDeliveryContractCode(String code) {
    	List<DeliveryTransaction> dTransactionList = WebSocket.findCurrDTrans();
		if (dTransactionList != null) {
			for (DeliveryTransaction trans : dTransactionList) {
				if (trans.getTransaction_code().equals(code)) {
					return true;
				}
			}
		}
    	return false;
    }
    
    private boolean checkVContractCode(String code) {
    	List<VTransaction> transactionList = WebSocket.findCurrVTrans();
		if (transactionList != null) {
			for (VTransaction trans : transactionList) {
				if (trans.getTransaction_code().equals(code)) {
					return true;
				}
			}
		}
		
		return false;
    }
    
    private boolean checkSpotCode(String code) {
    	List<BQSpotMarketCoinPair> coinPairs = WebSocket.findCurrCoinPair();
		if (coinPairs != null) {
			for (BQSpotMarketCoinPair trans : coinPairs) {
				if (trans.getCoin_pair_name().equals(code)) {
					return true;
				}
			}
		}
		
		return false;
    }
    
    private boolean checkTypeAndCode(String type, String code) {
    	if (type == null || "".equals(type) || "contract".equals(type)) {
    		return checkContractCode(code);
		} else if ("delivery".equals(type)) {
			return checkDeliveryContractCode(code);
		} else if ("vcontract".equals(type)) {
			return checkVContractCode(code);
		} else if ("spot".equals(type)) {
			return checkSpotCode(code);
		} else {
			return checkNContractCode(type, code);
		}
    }
    
    private boolean checkKlineSub(String[] subs) {
    	// kline.$contractType.$code.$timeType
    	boolean have = false;
		if (spyMemcachedClient != null) {
			if ("contract".equals(subs[1])) {
	    		List<BQTransaction> transactionList = WebSocket.findCurrTrans();
	    		if (transactionList != null) {
	    			for (BQTransaction trans : transactionList) {
	    				if (trans.getTransaction_code().equals(subs[2])) {
	    					have = true;
	    					break;
	    				}
	    			}
	    		}
			} else if ("delivery".equals(subs[1])) {
				List<DeliveryTransaction> transactionList = WebSocket.findCurrDTrans();
				if (transactionList != null) {
	    			for (DeliveryTransaction trans : transactionList) {
	    				if (trans.getTransaction_code().equals(subs[2])) {
	    					have = true;
	    					break;
	    				}
	    			}
	    		}
			} else if ("vcontract".equals(subs[1])) {
				List<VTransaction> transactionList = WebSocket.findCurrVTrans();
				
				if (transactionList != null) {
	    			for (VTransaction trans : transactionList) {
	    				if (trans.getTransaction_code().equals(subs[2])) {
	    					have = true;
	    					break;
	    				}
	    			}
	    		}
			} else if ("spot".equals(subs[1])) {
				List<BQSpotMarketCoinPair> coinPairs = WebSocket.findCurrCoinPair();
				if (coinPairs != null) {
	    			for (BQSpotMarketCoinPair trans : coinPairs) {
	    				if (trans.getCoin_pair_name().equals(subs[2])) {
	    					have = true;
	    					break;
	    				}
	    			}
	    		}
			} else {
				List<NTransaction> transactionList = WebSocket.findNCurrTrans(subs[1]);
	    		if (transactionList != null) {
	    			for (NTransaction trans : transactionList) {
	    				if (trans.getTransaction_code().equals(subs[2])) {
	    					have = true;
	    					break;
	    				}
	    			}
	    		}
			}
		}
		
		if (have) {
			String[] intervals = { "1min", "5min", "15min", "30min", "1hour", "2hour", "4hour", "6hour", "1day", "1week", "1month" };
			List<String> intervalList = new ArrayList<String>(Arrays.asList(intervals));
			if (intervalList.contains(subs[3])) {
				have = true;
			} else {
				have = false;
			}
		}
		
		return have;
    }
    
    private void dealSub(JSONObject messageObject) {
    	String subData = messageObject.getString("sub");
    	
    	JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		tickerJson.put("ts", System.currentTimeMillis() / 1000);
		
    	String msg = "订阅失败，不支持的类型: " + subData;
    	String[] subs = subData.split("[.]");
    	if (subs.length == 4) {
	    	if ("market".equals(subs[0]) && "depth".equals(subs[3])) {
	    		// market.$contractType.$code.depth
	    		if (checkTypeAndCode(subs[1], subs[2])) {
	    			msg = "";
	    		}
	    	} else if ("market".equals(subs[0]) && "trade".equals(subs[3])) {
	    		// market.$contractType.$code.trade
	    		if (checkTypeAndCode(subs[1], subs[2])) {
    				msg = "";
	    		}
	    	} else if ("kline".equals(subs[0])) {
	    		// kline.$contractType.$code.$timeType
	    		if (checkKlineSub(subs)) {
	    			msg = "";
	    		}
	    	}
    	}
    	
    	if (StringUtil.isEmpty(msg)) {
    		if ("kline".equals(subs[0])) {
    			if (!this.subKlineList.contains(subData)) {
    				this.subKlineList.add(subData);
    			}
    		} else {
    			if ("depth".equals(subs[3])) {
    				if (!this.subDepthList.contains(subData)) {
    		    		this.subDepthList.add(subData);
    		    	}
    			} else if ("trade".equals(subs[3])) {
    				if (!this.subTradeList.contains(subData)) {
    		    		this.subTradeList.add(subData);
    		    	}
    			}
    		}
    		
		    tickerJson.put("subbed", subData);
    	} else {
    		tickerJson.put("code", 10000);
    		tickerJson.put("msg", msg);
    	}
    	
		try {
			this.sendMessage(tickerJson.toString());
		} catch (Exception e) {
		}
    }
    
    private void dealReq(JSONObject messageObject) {
    	String subData = messageObject.getString("req");
    	
    	String msg = "请求失败，不支持的类型: " + subData;
    	String[] subs = subData.split("[.]");
    	
    	if (subs.length == 4) {
	    	if ("market".equals(subs[0]) && "depth".equals(subs[3])) {
	    		// market.$contractType.$code.depth
	    		if (checkTypeAndCode(subs[1], subs[2])) {
	    			msg = "";
	    		}
	    	} else if ("market".equals(subs[0]) && "trade".equals(subs[3])) {
	    		// market.$contractType.$code.trade
	    		if (checkTypeAndCode(subs[1], subs[2])) {
    				msg = "";
	    		}
	    	} else if ("kline".equals(subs[0])) {
	    		// kline.$contractType.$code.$timeType
	    		if (checkKlineSub(subs)) {
	    			msg = "";
	    		}
	    	}
    	}
    	
    	long time = System.currentTimeMillis() / 1000;
    	JSONObject msgObj = new JSONObject();
    	msgObj.put("rep", subData);
    	msgObj.put("ts", time);
    	msgObj.put("code", 0);
    	msgObj.put("msg", "");
    	
    	if (!StringUtil.isEmpty(msg)) {
    		msgObj.put("code", 10000);
    		msgObj.put("msg", msg);
    		
	    	try {	
	    		this.sendMessage(msgObj.toString());
			} catch (Exception e) {
				
			}
	    	
	    	return;
	    }
    	
    	if (subs.length == 4) {
    		// market.$contractType.$code.depth
	    	if ("market".equals(subs[0]) && "depth".equals(subs[3])) {
	    		List<Top5DelegationVO> buytopList = null;
    			List<Top5DelegationVO> selltopList = null;
	    		if ("vcontract".equals(subs[1])) {
	    			buytopList = spyMemcachedClient.get(subs[2] + BTCQMemecacheKeys.V_BUYTOP5LIST);
	    			selltopList = spyMemcachedClient.get(subs[2] + BTCQMemecacheKeys.V_SELLTOP5LIST);
	    		} else if ("delivery".equals(subs[1])) {
	    			buytopList = spyMemcachedClient.get(subs[2] + BTCQMemecacheKeys.D_BUYTOP5LIST);
	    			selltopList = spyMemcachedClient.get(subs[2] + BTCQMemecacheKeys.D_SELLTOP5LIST);
	    		} else if ("spot".equals(subs[1])) {
	    			buytopList = spyMemcachedClient.get(subs[2] + BTCQMemecacheKeys.BUYTOP5LIST);
	    			selltopList = spyMemcachedClient.get(subs[2] + BTCQMemecacheKeys.SELLTOP5LIST);
	    		} else if (subs[1] == null || "".equals(subs[1]) || "contract".equals(subs[1])) {
	    			buytopList = spyMemcachedClient.get(subs[2] + BTCQMemecacheKeys.BUYTOP5LIST);
	    			selltopList = spyMemcachedClient.get(subs[2] + BTCQMemecacheKeys.SELLTOP5LIST);
	    		} else {
	    			buytopList = spyMemcachedClient.get(subs[1] + "_" + subs[2] + BTCQMemecacheKeys.BUYTOP5LIST);
	    			selltopList = spyMemcachedClient.get(subs[1] + "_" + subs[2] + BTCQMemecacheKeys.SELLTOP5LIST);
	    		}
	    		
    			if (buytopList == null) {
    				buytopList = new ArrayList<Top5DelegationVO>();
    			}
    			
    			if (selltopList == null) {
    				selltopList = new ArrayList<Top5DelegationVO>();
    			}
    			
    			JSONObject dataJson = new JSONObject();
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
    			
    			dataJson.put("bids", buyArray);
    			dataJson.put("asks", sellArray);
    			
    			msgObj.put("tick", dataJson);
    			
    			try {
					this.sendMessage(msgObj.toString());
				} catch (IOException e) {
					
				}
	    	} else if ("market".equals(subs[0]) && "trade".equals(subs[3])) {
	    		// market.$contractType.$code.trade
	    		String data = "[]";
	    		if ("vcontract".equals(subs[1])) {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRADE_CODE + subs[2]);
				} else if ("delivery".equals(subs[1])) {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRADE_CODE + subs[2]);
				} else if ("spot".equals(subs[1])) {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_CODE + subs[2]);
				} else if (subs[1] == null || "".equals(subs[1]) || "contract".equals(subs[1])) {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_CODE + subs[2]);
				} else {
					data = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_CODE + subs[1] + "_" + subs[2]);
				}
	    		
				msgObj.put("tick", JSONArray.parse(data));
				try {
					this.sendMessage(msgObj.toString());
				} catch (IOException e) {
					
				}
				
	    	} else  if ("kline".equals(subs[0])) {
	    		// kline.$contractType.$code.$timeType
	    		if ("vcontract".equals(subs[1])) {
	    			msgObj.put("tick", WebSocket.getVKline(subs[2], subs[3], false));
	    		} else if ("delivery".equals(subs[1])) {
	    			msgObj.put("tick", WebSocket.getDKline(subs[2], subs[3], false));
	    		} else if ("spot".equals(subs[1])) {
	    			msgObj.put("tick", WebSocket.getSpotKline(subs[2], subs[3], false));
	    		} else if (subs[1] == null || "".equals(subs[1]) || "contract".equals(subs[1])) {
	    			msgObj.put("tick", WebSocket.getKline(subs[2], subs[3], false));
	    		} else {
	    			msgObj.put("tick", WebSocket.getNKline(subs[1], subs[2], subs[3], false));
	    		}
	    		
	    		try {
					this.sendMessage(msgObj.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	} 
    	}
    }
}
