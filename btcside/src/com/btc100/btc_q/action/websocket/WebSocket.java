package com.btc100.btc_q.action.websocket;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.BQUserAppKey;
import com.btc100.btc_q.entity.DeliveryPendingDelegation;
import com.btc100.btc_q.entity.DeliveryPendingDelegationCache;
import com.btc100.btc_q.entity.DeliveryPendingLog;
import com.btc100.btc_q.entity.DeliveryTradeLog;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.entity.NPendingDelegationCache;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.PendingDelegation;
import com.btc100.btc_q.entity.PendingDelegationCache;
import com.btc100.btc_q.entity.PendingLog;
import com.btc100.btc_q.entity.TradeLog;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.BQUserAppKeyService;
import com.btc100.btc_q.service.DeliveryPendingDelegationCacheService;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryPendingLogService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.service.KLineTradeService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.PendingLogService;
import com.btc100.btc_q.service.TradeLogService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.PendingDelegationVO;
import com.btc100.btc_q.vo.TradeAjaxVO;
import com.btc100.btc_q.vo.TradeLogVo;

@ServerEndpoint(value = "/ws")
public class WebSocket {
	
	private static final Logger logger = Logger.getLogger(WebSocket.class);
	
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    public static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<WebSocket>();
    
    private static CopyOnWriteArraySet<WebSocket> pushWebSocketSet = new CopyOnWriteArraySet<WebSocket>();
    
    private static SpyMemcachedClient spyMemcachedClient = null;
    
    private static BQTransactionService bqTransactionService = null;
	
	private static BQTransactionTypeService bqTransactionTypeService = null;
	
	private static VTransactionService vTransactionService = null;
	
	private static VTransactionTypeService vTransactionTypeService = null;
	
	private static DeliveryTransactionService dTransactionService = null;
	
	private static DeliveryTransactionTypeService dTransactionTypeService = null;
	
	private static BQUserAppKeyService bqUserAppKeyService = null;
	
	private static TradeLogService tradeLogService = null;
	
	private static DeliveryTradeLogService dTradeLogService = null;
	
	private static PendingDelegationCacheService pendingDelegationCacheService = null;
	
	private static PendingDelegationService pendingDelegationService = null;
	
	private static PendingLogService pendingLogService = null;
	
	private static DeliveryPendingDelegationCacheService dPendingDelegationCacheService = null;
	
	private static DeliveryPendingDelegationService dPendingDelegationService = null;
	
	private static DeliveryPendingLogService dPendingLogService = null;
	
	public static  KLineTradeService kLineTradeService = null;
	
	public static  BQSpotMarketCoinPairService bqSpotMarketCoinPairService = null;
	
    public static NTransactionService nTransactionService = null;
    
    public static NTransactionTypeService nTransactionTypeService = null;

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    
    private List<String> subList = new ArrayList<String>();
    
    private List<String> subPushList = new ArrayList<String>();
    
    private List<String> deliverySubPushList = new ArrayList<String>();
    
    public CopyOnWriteArraySet<String> subKlineList = new CopyOnWriteArraySet<>();
    public CopyOnWriteArraySet<String> subDepthList = new CopyOnWriteArraySet<>();
    
    private long lastGotPongTime = 0;
    
    private long lastPingValue1 = 0;
    private long lastPintValue2 = 0;
    
    private Long userId = null;
    
	public static void setMemClient(SpyMemcachedClient client, BQNotradetimeService notradeService,
			BQTransactionService transactionService, BQTransactionTypeService transactionTypeService,
			VTransactionService vTransService, VTransactionTypeService vTransTypeService,
			KLineTradeService klineService, BQUserAppKeyService userAppKeyService, TradeLogService tLogService,
			PendingDelegationCacheService pdCacheDelegationService, PendingDelegationService pdDelegationService,
			PendingLogService pdLogService, BQSpotMarketCoinPairService spotMarketCoinPairService,
			DeliveryTransactionService dTransService, DeliveryTransactionTypeService dTransTypeService,
			DeliveryTradeLogService dTLogService, DeliveryPendingDelegationCacheService dPDCacheService,
			DeliveryPendingDelegationService dPDService, DeliveryPendingLogService dPDLogService) {
		spyMemcachedClient = client;
		bqTransactionService = transactionService;
		bqTransactionTypeService = transactionTypeService;
		vTransactionService = vTransService;
		vTransactionTypeService = vTransTypeService;
		kLineTradeService = klineService;
		bqUserAppKeyService = userAppKeyService;
		tradeLogService = tLogService;
		pendingDelegationCacheService = pdCacheDelegationService;
		pendingDelegationService = pdDelegationService;
		pendingLogService = pdLogService;
		bqSpotMarketCoinPairService = spotMarketCoinPairService;
		dTransactionService = dTransService;
		dTransactionTypeService = dTransTypeService;
		dTradeLogService = dTLogService;
		dPendingDelegationCacheService = dPDCacheService;
		dPendingDelegationService = dPDService;
		dPendingLogService = dPDLogService;
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
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);  //从set中删除
        closeRemoveSubbedUser();
        pushWebSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
//    	logger.info("来自客户端的消息:" + message + ", session: " + session.getId());
//        System.out.println("来自客户端的消息:" + message + ", session: " + session.getId());
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
        	} else if (messageObject.containsKey("op")) {
        		dealAuth(messageObject);
        	}
		} catch (Exception e) {
			e.printStackTrace();
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
//        error.printStackTrace();
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
    	for (WebSocket item: webSocketSet) {
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
    
    public static void checkPushAlive() {
    	long time = System.currentTimeMillis() / 1000;
    	JSONObject msgObj = new JSONObject();
    	msgObj.put("ping", time);
    	for (WebSocket item: pushWebSocketSet) {
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
    
    public static void notifyDepthMessage(String type, JSONObject data) {
    	long time = System.currentTimeMillis() / 1000;
    	JSONObject msgObj = new JSONObject();
    	msgObj.put("ch", type);
    	msgObj.put("ts", time);
    	msgObj.put("data", data);
    	for (WebSocket item: webSocketSet) {
    		if (item.subList.contains(type)) {    			
    			try {
    				item.sendMessage(msgObj.toString());
    			} catch (Exception e) {
    				//e.printStackTrace();
    			}
    		}
    	}
    }
    
    public static void notifyMessage(String type, JSONObject data) {
    	long time = System.currentTimeMillis() / 1000;
    	JSONObject msgObj = new JSONObject();
    	msgObj.put("ch", type);
    	msgObj.put("ts", time);
    	msgObj.put("data", data);
    	for (WebSocket item: webSocketSet) {
    		if (item.subList.contains(type)) {    			
    			try {
    				item.sendMessage(msgObj.toString());
    			} catch (Exception e) {
    				//e.printStackTrace();
    			}
    		}
    	}
    }
    
    public static void notifyPushMessage(String type, JSONObject data) {
    	long time = System.currentTimeMillis() / 1000;
    	JSONObject msgObj = new JSONObject();
    	msgObj.put("ch", type);
    	msgObj.put("ts", time);
    	msgObj.put("data", data);
    	for (WebSocket item: pushWebSocketSet) {
    		if (item.subList.contains(type)) {    			
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
    	WebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
    	WebSocket.onlineCount--;
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
    	if (this.subList.contains(subData)) {
    		this.subList.remove(subData);
    	}
    	
    	if (this.subKlineList.contains(subData)) {
    		this.subKlineList.remove(subData);
    	}
    	
    	if (this.subDepthList.contains(subData)) {
    		this.subDepthList.remove(subData);
    	}
    	
    	if (this.subPushList.contains(subData)) {
    		this.subPushList.remove(subData);
    	}
    	
    	if (this.deliverySubPushList.contains(subData)) {
    		this.deliverySubPushList.remove(subData);
    	}
    	String[] subs = subData.split("[.]");
    	if (subs.length == 4) {
    		if ("order".equals(subs[0]) && "sub".equals(subs[2]) && "pending".equals(subs[3])) {
	    		// 'unsub:order.$code.sub.pending'
	    		if (this.userId != null) {
		    		if (checkContractCode(subs[1])) {
		    			removeSubbedUser(subs[1], "contract");
		    		}
				}
	    	}
    	} else if (subs.length == 5) {
    		if ("order".equals(subs[0]) && "sub".equals(subs[2]) && "pending".equals(subs[3]) && "delivery".equals(subs[4])) {
	    		// 'unsub:order.$code.sub.pending.delivery'
	    		if (this.userId != null) {
		    		if (checkContractCode(subs[1])) {
		    			removeSubbedUser(subs[1], subs[4]);
		    		}
				}
	    	}
    	}
    	
    }
    
    private boolean checkContractCode(String code) {
    	boolean have = false;
    	List<BQTransaction> transactionList = findCurrTrans();
		if (transactionList != null) {
			for (BQTransaction trans : transactionList) {
				if (trans.getTransaction_code().equals(code)) {
					have = true;
					break;
				}
			}
		}
		return have;
    }
    
    private boolean checkDeliveryContractCode(String code) {
    	boolean have = false;
    	List<DeliveryTransaction> dTransactionList = findCurrDTrans();
		if (dTransactionList != null) {
			for (DeliveryTransaction trans : dTransactionList) {
				if (trans.getTransaction_code().equals(code)) {
					have = true;
					break;
				}
			}
		}
    	return have;
    }
    
    private boolean checkTradeSub(String[] subs) {
    	// trade.$contractType.$code
    	boolean have = false;
		if (subs[1] == null || "".equals(subs[1]) || "contract".equals(subs[1])) {
    		List<BQTransaction> transactionList = findCurrTrans();
    		if (transactionList != null) {
    			for (BQTransaction trans : transactionList) {
    				if (trans.getTransaction_code().equals(subs[2])) {
    					have = true;
    					break;
    				}
    			}
    		}
		} else if ("vcontract".equals(subs[1])) {
			List<VTransaction> transactionList = findCurrVTrans();
			if (transactionList != null) {
    			for (VTransaction trans : transactionList) {
    				if (trans.getTransaction_code().equals(subs[2])) {
    					have = true;
    					break;
    				}
    			}
    		}
		} else if ("delivery".equals(subs[1])) {
			List<DeliveryTransaction> transactionList = findCurrDTrans();
			if (transactionList != null) {
    			for (DeliveryTransaction trans : transactionList) {
    				if (trans.getTransaction_code().equals(subs[2])) {
    					have = true;
    					break;
    				}
    			}
    		}
		} else if ("spot".equals(subs[1])) {
			List<BQSpotMarketCoinPair> coinPairs = findCurrCoinPair();
			if (coinPairs != null) {
    			for (BQSpotMarketCoinPair trans : coinPairs) {
    				if (trans.getCoin_pair_name().equals(subs[2])) {
    					have = true;
    					break;
    				}
    			}
    		}
		} else {
			List<NTransaction> transactionList = findNCurrTrans(subs[1]);
    		if (transactionList != null) {
    			for (NTransaction trans : transactionList) {
    				if (trans.getTransaction_code().equals(subs[2])) {
    					have = true;
    					break;
    				}
    			}
    		}
		}
		
		return have;
    }
    
    private boolean checkKlineSub(String[] subs) {
    	// kline.$contractType.$code.$timeType
    	boolean have = false;
		if (spyMemcachedClient != null) {
			if (subs[1] == null || "".equals(subs[1]) || "contract".equals(subs[1])) {
	    		List<BQTransaction> transactionList = findCurrTrans();
	    		if (transactionList != null) {
	    			for (BQTransaction trans : transactionList) {
	    				if (trans.getTransaction_code().equals(subs[2])) {
	    					have = true;
	    					break;
	    				}
	    			}
	    		}
			} else if ("vcontract".equals(subs[1])) {
				List<VTransaction> transactionList = findCurrVTrans();
				if (transactionList != null) {
	    			for (VTransaction trans : transactionList) {
	    				if (trans.getTransaction_code().equals(subs[2])) {
	    					have = true;
	    					break;
	    				}
	    			}
	    		}
			} else if ("delivery".equals(subs[1])) {
				List<DeliveryTransaction> transactionList = findCurrDTrans();
				if (transactionList != null) {
	    			for (DeliveryTransaction trans : transactionList) {
	    				if (trans.getTransaction_code().equals(subs[2])) {
	    					have = true;
	    					break;
	    				}
	    			}
	    		}
			} else if ("spot".equals(subs[1])) {
				List<BQSpotMarketCoinPair> coinPairs = findCurrCoinPair();
				if (coinPairs != null) {
	    			for (BQSpotMarketCoinPair trans : coinPairs) {
	    				if (trans.getCoin_pair_name().equals(subs[2])) {
	    					have = true;
	    					break;
	    				}
	    			}
	    		}
			} else {
				List<NTransaction> transactionList = findNCurrTrans(subs[1]);
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
	    		if (checkTradeSub(subs)) {
	    			msg = "";
	    		}
	    	} else if ("kline".equals(subs[0])) {
	    		// kline.$contractType.$code.$timeType
	    		if (checkKlineSub(subs)) {
	    			msg = "";
	    		}
	    	} else if ("order".equals(subs[0]) && "sub".equals(subs[2]) && "trade".equals(subs[3])) {
	    		// order.$code.sub.trade
	    		if (this.userId != null) {
		    		if (checkContractCode(subs[1])) {
		    			msg = "";
		    		}
				}
	    	} else if ("order".equals(subs[0]) && "sub".equals(subs[2]) && "pending".equals(subs[3])) {
	    		// order.$code.sub.pending
	    		if (this.userId != null) {
		    		if (checkContractCode(subs[1])) {
		    			setSubbedUser(subs[1], "contract");
		    			msg = "";
		    		}
				}
	    	}
    	}
    	
    	if (subs.length == 5) {
    		if ("order".equals(subs[0]) && "sub".equals(subs[2]) && "trade".equals(subs[3]) && "delivery".equals(subs[4])) {
    			// order.$code.sub.trade.delivery
    			if (this.userId != null) {
    				if (checkDeliveryContractCode(subs[1])) {
    					msg = "";
    				}
    			}
    		} else if ("order".equals(subs[0]) && "sub".equals(subs[2]) && "pending".equals(subs[3]) && "delivery".equals(subs[4])) {
    			// order.$code.sub.pending.delivery
    			if (this.userId != null) {
    				if (checkDeliveryContractCode(subs[1])) {
    					setSubbedUser(subs[1], subs[4]);
    					msg = "";
    				}
    			}
    		}
    	}
    	
    	// trade.$contractType.$code
    	if (subs.length == 3) {
	    	if ("trade".equals(subs[0])) {
	    		if (checkTradeSub(subs)) {
	    			msg = "";
	    		}
	    	}
    	}
    	
    	if (StringUtil.isEmpty(msg)) {
    		if ("kline".equals(subs[0])) {
    			if (!this.subKlineList.contains(subData)) {
    				this.subKlineList.add(subData);
    			}
    			// market.$contractType.$code.depth
    		} else if ("market".equals(subs[0]) && "depth".equals(subs[3])) {
    			if (!this.subDepthList.contains(subData)) {
    				this.subDepthList.add(subData);
    			}
    		} else if ("order".equals(subs[0]) && "sub".equals(subs[2]) && subs.length == 4) {
    			if (this.userId != null) {
		    		if (checkContractCode(subs[1])) {
		    			if (!this.subPushList.contains(subData)) {
				    		this.subPushList.add(subData);
				    	}
		    		}
				}
    		} else if ("order".equals(subs[0]) && "sub".equals(subs[2]) && "delivery".equals(subs[4]) && subs.length == 5) {
    			if (this.userId != null) {
		    		if (checkDeliveryContractCode(subs[1])) {
		    			if (!this.deliverySubPushList.contains(subData)) {
				    		this.deliverySubPushList.add(subData);
				    	}
		    		}
				}
    		} else {
		    	if (!this.subList.contains(subData)) {
		    		if ("trade".equals(subs[0]) && subs.length == 3) {
		    			List<String> toRemove = new ArrayList<String>();
			    		for (String subStr : subList) {
			    			if (subStr.contains("trade.")) {
			    				toRemove.add(subStr);
			    			}
			    		}
			    		
			    		this.subList.removeAll(toRemove);
		    		}
		    		
		    		this.subList.add(subData);
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
//			e.printStackTrace();
		}
    }
    
    private void dealReq(JSONObject messageObject) {
    	String subData = messageObject.getString("req");
    	
    	String msg = "请求失败，不支持的类型: " + subData;
    	String[] subs = subData.split("[.]");
    	
    	if (subs.length == 4) {
    		// market.$contractType.$code.depth
	    	if ("market".equals(subs[0]) && "depth".equals(subs[3])) {
	    		if (checkTradeSub(subs)) {
	    			msg = "";
	    		}
	    	} else if ("kline".equals(subs[0])) {
	    		// kline.$contractType.$code.$timeType
	    		if (checkKlineSub(subs)) {
	    			msg = "";
	    		}
	    	} else if ("order".equals(subs[0]) && "req".equals(subs[2]) && "trade".equals(subs[3])) {
	    		// order.$code.req.trade
	    		if (this.userId != null && checkContractCode(subs[1])) {
					msg = "";
				}
	    	} else if ("order".equals(subs[0]) && "req".equals(subs[2]) && "pending".equals(subs[3])) {
	    		// order.$code.req.pending
	    		if (this.userId != null && checkContractCode(subs[1])) {
					msg = "";
				}
	    	}
    	}
    	
    	if (subs.length == 5) {
    		if ("order".equals(subs[0]) && "req".equals(subs[2]) && "trade".equals(subs[3]) && "delivery".equals(subs[4])) {
    			// order.$code.req.trade.delivery
    			if (this.userId != null && checkDeliveryContractCode(subs[1])) {
    				msg = "";
    			}
    		} else if ("order".equals(subs[0]) && "req".equals(subs[2]) && "pending".equals(subs[3]) && "delivery".equals(subs[4])) {
    			// order.$code.req.pending.delivery
    			if (this.userId != null && checkDeliveryContractCode(subs[1])) {
    				msg = "";
    			}
    		}
    	}
    	
    	if (subs.length == 3) {
	    	if ("trade".equals(subs[0]) && checkTradeSub(subs)) {
	    		// trade.$contractType.$code
	    		msg = "";
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
				//e.printStackTrace();
			}
	    	
	    	return;
	    }
    	
    	if (subs.length == 4) {
	    	// market.$contractType.$code.depth
	    	if ("market".equals(subs[0]) && "depth".equals(subs[3])) {
	    		try {
	    			JSONObject dataJson = getDepth(subs[1], subs[2]);
	    			msgObj.put("data", dataJson);
	    			this.sendMessage(msgObj.toString());
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	} else if ("kline".equals(subs[0])) {
	    		// kline.$contractType.$code.$timeType
	    		if ("vcontract".equals(subs[1])) {
	    			msgObj.put("data", getVKline(subs[2], subs[3], false));
	    		} else if ("spot".equals(subs[1])) {
	    			msgObj.put("data", getSpotKline(subs[2], subs[3], false));
	    		} else if ("delivery".equals(subs[1])) {
	    			msgObj.put("data", getDKline(subs[2], subs[3], false));
	    		} else if (subs[1] == null || "".equals(subs[1]) || "contract".equals(subs[1])) {
	    			msgObj.put("data", getKline(subs[2], subs[3], false));
	    		} else {
	    			msgObj.put("data", getNKline(subs[1], subs[2], subs[3], false));
	    		}
	    		
	    		try {
					this.sendMessage(msgObj.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	} else if ("order".equals(subs[0]) && "req".equals(subs[2]) && "trade".equals(subs[3])) {
	    		// 请求历史交易记录列表
	    		// order.$code.req.trade
	    		if (spyMemcachedClient == null) {
	    			return;
	    		}
	    		
	    		if (this.userId == null) {
	    			return;
	    		}
	    		
	    		JSONArray array = getReqOrderTrade(this.userId, subs[1]);
	    		msgObj.put("data", array);
	    		try {
					this.sendMessage(msgObj.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	} else if ("order".equals(subs[0]) && "req".equals(subs[2]) && "pending".equals(subs[3])) {
	    		// 请求当前委托列表
	    		// order.$code.req.pending
	    		if (spyMemcachedClient == null) {
	    			return;
	    		}
	    		
	    		if (this.userId == null) {
	    			return;
	    		}
	    		
	    		JSONArray array = getReqOrderPending(this.userId, subs[1]);
	    		msgObj.put("data", array);
	    		try {
	    			this.sendMessage(msgObj.toString());
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	}
    	}
    	
    	if (subs.length == 5) {
    		if ("order".equals(subs[0]) && "req".equals(subs[2]) && "trade".equals(subs[3]) && "delivery".equals(subs[4])) {
	    		// 请求历史交易记录列表
	    		// order.$code.req.trade.delivery
	    		if (spyMemcachedClient == null) {
	    			return;
	    		}
	    		
	    		if (this.userId == null) {
	    			return;
	    		}
	    		
//	    		getDReqOrderTrade
	    		JSONArray array = getDReqOrderTrade(this.userId, subs[1]);
	    		msgObj.put("data", array);
	    		try {
					this.sendMessage(msgObj.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	} else if ("order".equals(subs[0]) && "req".equals(subs[2]) && "pending".equals(subs[3]) && "delivery".equals(subs[4])) {
	    		// 请求当前委托列表
	    		// order.$code.req.pending.delivery
	    		if (spyMemcachedClient == null) {
	    			return;
	    		}
	    		
	    		if (this.userId == null) {
	    			return;
	    		}
	    		
//	    		getDReqOrderPending
	    		JSONArray array = getDReqOrderPending(this.userId, subs[1]);
	    		msgObj.put("data", array);
	    		try {
	    			this.sendMessage(msgObj.toString());
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	}
    	}
    	
    	// trade.$contractType.$code
    	if (subs.length == 3 && "trade".equals(subs[0])) {
    		if (spyMemcachedClient == null) {
    			return;
    		}
    		JSONObject dataObj = new JSONObject();
    		if ("spot".equals(subs[1])) {
    			dataObj.put("tickersdata", tickersData(subs[1]));
				dataObj.put("tradesdata", trades("spot", subs[2]));
				dataObj.put("depthdata", getDepth("spot", subs[2]));
				dataObj.put("symbolsdata", getSymbols(subs[1]));
				
				msgObj.put("data", dataObj);
	    		try {
					this.sendMessage(msgObj.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		
    			return;
    		}
    		dataObj.put("tradesdata", trades(subs[1], subs[2]));
    		dataObj.put("tickersdata", tickersData(subs[1]));
    		dataObj.put("bpidata", getBPIData());
    		dataObj.put("commondata", getCommonData(subs[1], subs[2]));
    		dataObj.put("symbolsdata", getSymbols(subs[1]));
    		dataObj.put("depthdata", getDepth(subs[1], subs[2]));
    		
    		msgObj.put("data", dataObj);
    		try {
				this.sendMessage(msgObj.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    private void dealAuth(JSONObject messageObject) throws Exception {
    	String msg = checkCommonParams(messageObject);
    	
    	JSONObject msgObj = new JSONObject();
    	long time = System.currentTimeMillis() / 1000;
    	msgObj.put("ts", time);
    	msgObj.put("op", messageObject.getString("op"));
    	msgObj.put("msg", msg);
    	msgObj.put("code", 0);
    	
    	System.out.println("this.userId:" + this.userId);
    	if (!StringUtil.isEmpty(msg)) {
    		msgObj.put("code", 10000);
	    } else {
	    	if (this.userId != null) {
	    		pushWebSocketSet.add(this);     //加入set中
	    	}
	    }
		
    	try {	
    		this.sendMessage(msgObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    private String checkCommonParams(JSONObject messageObject) throws Exception {
    	String op = messageObject.getString("op");
    	String appKey = messageObject.getString("AppKey");
    	String timestamp = messageObject.getString("Timestamp");
    	String signature = messageObject.getString("Signature");
		
		if (StringUtil.isEmpty(op) || StringUtil.isEmpty(appKey) || StringUtil.isEmpty(signature) || StringUtil.isEmpty(timestamp)) {
			return "参数错误";
		}
		
		if (!"auth".equals(op)) {
			return "参数错误，op错误";
		}
		
		BQUserAppKey userAppKey = bqUserAppKeyService.getByAppKey(appKey);
		if (userAppKey == null) {
			return "参数错误，AppKey错误";
		}
		
		long time = Convert.toLong(timestamp, 0);
		if (time == 0) {
			return "参数错误，Timestamp参数错误";
		}
		
		long now = System.currentTimeMillis() / 1000;
		if (Math.abs(now - time) > 300) {
			return "参数错误，Timestamp参数错误";
		}
		
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("op", op);
    	map.put("AppKey", appKey);
    	map.put("Timestamp", timestamp);

		String sign = signTopRequest(map, userAppKey.getApp_secret());
		if (!signature.equals(sign)) {
			return "签名错误，Signature错误";
		}
		
		this.userId = userAppKey.getF_user_id();
		return "";
	}
    
	private static String signTopRequest(Map<String, String> parameters, String secret) throws Exception {
		// 第一步：把字典按Key的字母顺序排序
		Map<String, String> orderMap = com.btc100.btc_q.util.Utils.sortMapByKey(parameters);    //按Key进行排序
		
        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        query.append(secret);
        Iterator<String> keys = orderMap.keySet().iterator();
        while (keys.hasNext()) {
			String key = keys.next();
			String value = orderMap.get(key);
            if (!StringUtil.isEmpty(key))
            {
                query.append(key).append(value);
            }
		}

        // 第三步：使用MD5加密
        query.append(secret);
        String utf8Str = new String(query.toString().getBytes("UTF-8"),"UTF-8");
        return MD5Service.encryptString(utf8Str);
    }
	
	private void setSubbedUser(String code, String type) {
		String key = BTCQMemecacheKeys.WS_SUBBED_USERS;
		if (!StringUtil.isEmpty(type) && "delivery".equals(type)) {
			key = BTCQMemecacheKeys.D_WS_SUBBED_USERS;
		}
		List<Long> users = spyMemcachedClient.get(code + key);
		if (users != null) {
			if (!users.contains(this.userId)) {
				users.add(this.userId);
				spyMemcachedClient.set(code + key, BTCQMemecacheKeys.TIMES.BPI, users);
			}
		} else {
			users = new ArrayList<Long>();
			users.add(this.userId);
			spyMemcachedClient.set(code + key, BTCQMemecacheKeys.TIMES.BPI, users);
		}
	}
	
	private void removeSubbedUser(String code, String type) {
		String key = BTCQMemecacheKeys.WS_SUBBED_USERS;
		if (!StringUtil.isEmpty(type) && "delivery".equals(type)) {
			key = BTCQMemecacheKeys.D_WS_SUBBED_USERS;
		}
		List<Long> users = spyMemcachedClient.get(code + key);
		if (users != null) {
			if (users.contains(this.userId)) {
				users.remove(this.userId);
				spyMemcachedClient.set(code + key, BTCQMemecacheKeys.TIMES.BPI, users);
			}
		}
	}
	
	private void closeRemoveSubbedUser() {
		if (this.subPushList.size() == 0 && this.deliverySubPushList.size() == 0) {
			return;
		}
		for (String subData : this.subPushList) {
			String[] subs = subData.split("[.]");
	    	if (subs.length == 4) {
	    		if ("order".equals(subs[0]) && "sub".equals(subs[2]) && "pending".equals(subs[3])) {
		    		// 'unsub:order.$code.sub.pending'
		    		if (this.userId != null) {
			    		if (checkContractCode(subs[1])) {
			    			removeSubbedUser(subs[1], "contract");
			    		}
					}
		    	}
	    	}
		}
		for (String subData : this.deliverySubPushList) {
			String[] subs = subData.split("[.]");
			if (subs.length == 5) {
				if ("order".equals(subs[0]) && "sub".equals(subs[2]) && "pending".equals(subs[3])  && "delivery".equals(subs[4])) {
					// 'unsub:order.$code.sub.pending.delivery'
					if (this.userId != null) {
						if (checkDeliveryContractCode(subs[1])) {
							removeSubbedUser(subs[1], subs[4]);
						}
					}
				}
			}
		}
	}
    
    public static JSONObject trades(String type, String code) {
    	
    	JSONObject dataObj = new JSONObject();
    	String data = "";
    	if ("vcontract".equals(type)) {
			data = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRADE_CODE + code.toLowerCase());
		} else if ("delivery".equals(type)) {
			data = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRADE_CODE + code.toLowerCase());
		} else if ("spot".equals(type)) {
			data = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_CODE + code.toLowerCase());
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			data = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_CODE + code.toLowerCase());
		} else {
			data = spyMemcachedClient.get(BTCQMemecacheKeys.TRADE_CODE + type + "_" + code.toLowerCase());
		}
    	
    	if (StringUtil.isEmpty(data)) {
    		data = "[]";
    	}
    	
    	dataObj.put("data", JSONArray.parse(data));
    	
    	int amountDigits = Utils.getAmountDigtis(code);
    	Integer digits = 0;
    	if ("vcontract".equals(type)) {
    		do {
				digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.V_DIGITS);
				if (digits == null) {
					List<VTransaction> transactionList = findCurrVTrans();
					if (transactionList == null) break;
					
					VTransaction trans = null;
					for (VTransaction bq : transactionList) {
						if (bq.getTransaction_code().equalsIgnoreCase(code)) {
							trans = bq;
							break;
						}
					}
					if (trans == null) break;
					
					VTransactionType transType = vTransactionTypeService.getTransactionType(trans.getTransaction_type());
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(code + BTCQMemecacheKeys.V_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
			} while(false);
    		
		} else if ("delivery".equals(type)) {
    		do {
				digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.D_DIGITS);
				if (digits == null) {
					List<DeliveryTransaction> transactionList = findCurrDTrans();
					if (transactionList == null) break;
					
					DeliveryTransaction trans = null;
					for (DeliveryTransaction bq : transactionList) {
						if (bq.getTransaction_code().equalsIgnoreCase(code)) {
							trans = bq;
							break;
						}
					}
					if (trans == null) break;
					
					DeliveryTransactionType transType = dTransactionTypeService.getTransactionType(trans.getTransaction_type());
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(code + BTCQMemecacheKeys.D_DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
			} while(false);
    		
		} else if ("spot".equals(type)) {
			do {
				List<BQSpotMarketCoinPair> transactionList = findCurrCoinPair();
				if (transactionList == null) break;
				
				BQSpotMarketCoinPair trans = null;
				for (BQSpotMarketCoinPair bq : transactionList) {
					if (bq.getCoin_pair_name().equalsIgnoreCase(code)) {
						trans = bq;
						break;
					}
				}
				if (trans == null) break;
				
				amountDigits = trans.getDecimals();
				digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.DIGITS);
				if (digits == null) {
					digits = Utils.getDigitsFromMinFloat(trans.getMin_float_price());
					spyMemcachedClient.set(code + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
			} while(false);
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			do {
				digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.DIGITS);
				if (digits == null) {
					List<BQTransaction> transactionList = findCurrTrans();
					if (transactionList == null) break;
					
					BQTransaction trans = null;
					for (BQTransaction bq : transactionList) {
						if (bq.getTransaction_code().equalsIgnoreCase(code)) {
							trans = bq;
							break;
						}
					}
					if (trans == null) break;
					
					BQTransactionType transType = bqTransactionTypeService.getTransactionType(trans.getTransaction_type());
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(code + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
			} while(false);
		} else {
			do {
				digits = spyMemcachedClient.get(type + "_" + code + BTCQMemecacheKeys.DIGITS);
				if (digits == null) {
					List<NTransaction> transactionList = findNCurrTrans(type);
					if (transactionList == null) break;
					
					NTransaction trans = null;
					for (NTransaction bq : transactionList) {
						if (bq.getTransaction_code().equalsIgnoreCase(code)) {
							trans = bq;
							break;
						}
					}
					if (trans == null) break;
					
					NTransactionType transType = nTransactionTypeService.getTransactionType(type, trans.getTransaction_type());
					digits = Utils.getDigitsFromMinFloat(transType.getMin_float_price());
					spyMemcachedClient.set(type + "_" + code + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
			} while(false);
		}
    	
    	dataObj.put("digits", digits);
    	dataObj.put("amountDigits", amountDigits);
    	
    	return dataObj;
	}
    
	public static JSONObject tickersData(String type) {
		JSONObject dataObj = new JSONObject();
		
		List<Map<String, Object>> lisMap = new ArrayList<Map<String,Object>>();
		if ("vcontract".equals(type)) {
			lisMap = spyMemcachedClient.get(BTCQMemecacheKeys.V_CONTRACT_BASE);
		} else if ("spot".equals(type)) {
			lisMap = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_BASE2);
			
			if (lisMap == null) {
				lisMap = new ArrayList<Map<String,Object>>();
				List<Map<String, Object>> tmpMap = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_BASE);
				List<BQSpotMarketCoinPair> transactionList = findCurrCoinPair();
				Map<String, Object> bfxusdrmap = null;
				for (BQSpotMarketCoinPair trans : transactionList) {
					if (tmpMap != null) {
						for (Map<String, Object> map : tmpMap) {
							if (map.get("pairName").equals(trans.getCoin_pair_name())) {
								if (trans.getCoin_pair_name().equalsIgnoreCase("bfxusdr")) {
									bfxusdrmap = map;
								} else {
									lisMap.add(map);
								}
								tmpMap.remove(map);
								break;
							}
						}
					}
				}
				if (bfxusdrmap != null) {
					lisMap.add(0, bfxusdrmap);
				}
				
				spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_BASE2, BTCQMemecacheKeys.TIMES.ONE_SECOND, lisMap);
			}
		} else if ("delivery".equals(type)) {
			lisMap = spyMemcachedClient.get(BTCQMemecacheKeys.DELIVERY_BASE2);
			
			if (lisMap == null) {
				lisMap = new ArrayList<Map<String,Object>>();
				List<Map<String, Object>> tmpMap = spyMemcachedClient.get(BTCQMemecacheKeys.DELIVERY_BASE);
				if (tmpMap != null) {
					List<DeliveryTransaction> transactionList = findCurrDTrans();
					for (DeliveryTransaction trans : transactionList) {
						for (Map<String, Object> map : tmpMap) {
							if (map.get("tranCode").equals(trans.getTransaction_code())) {
								lisMap.add(map);
								tmpMap.remove(map);
								break;
							}
						}
					}
					
					spyMemcachedClient.set(BTCQMemecacheKeys.DELIVERY_BASE2, BTCQMemecacheKeys.TIMES.ONE_SECOND, lisMap);
				}
			}
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			List<Map<String, Object>> tmpMap = spyMemcachedClient.get(BTCQMemecacheKeys.CONTRACT_BASE);
			if (tmpMap != null) {
				List<BQTransaction> transactionList = findCurrTrans();
				for (BQTransaction trans : transactionList) {
					for (Map<String, Object> map : tmpMap) {
						if (map.get("typeCode").equals(trans.getTransaction_code())) {
							BigDecimal lastPrice = spyMemcachedClient.get(trans.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
							if (lastPrice != null) {
								map.put("lastPrice", lastPrice.doubleValue());
								
								double high = (double) map.get("highPrice");
								double low = (double) map.get("lowPrice");

								boolean updateTicker = false;
								if (high < lastPrice.doubleValue() || high == 0) {
									high = lastPrice.doubleValue();
									updateTicker = true;
								}
								
								if (low > lastPrice.doubleValue() || low == 0) {
									low = lastPrice.doubleValue();
									updateTicker = true;
								}
								
								if (updateTicker) {
									map.put("highPrice", high);
									map.put("lowPrice", low);
								}
								
								double firstPrice = (double) map.get("firstPrice");
								
								// 涨跌幅
								if (lastPrice.doubleValue() <= 0.0) {
									map.put("fudu", "0%");
								} else {
									DecimalFormat df = new DecimalFormat("0.00");
									double rate = 0.0;
									if (firstPrice > 0) {
										rate =  (lastPrice.doubleValue() - firstPrice) / firstPrice;
									}
									
									String div = df.format(rate *100);
									map.put("fudu", div.toString() + "%");
								}
							}
							
							lisMap.add(map);
							tmpMap.remove(map);
							break;
						}
					}
				}
			}
				
		} else {
			List<Map<String, Object>> tmpMap = spyMemcachedClient.get(BTCQMemecacheKeys.CONTRACT_BASE + "_" + type);
			if (tmpMap != null) {
				List<NTransaction> transactionList = findNCurrTrans(type);
				for (NTransaction trans : transactionList) {
					for (Map<String, Object> map : tmpMap) {
						if (map.get("typeCode").equals(trans.getTransaction_code())) {
							BigDecimal lastPrice = spyMemcachedClient.get(type + "_" + trans.getTransaction_code() + BTCQMemecacheKeys.BTCLATESTPRICE);
							if (lastPrice != null) {
								map.put("lastPrice", lastPrice.doubleValue());
								
								double high = (double) map.get("highPrice");
								double low = (double) map.get("lowPrice");

								boolean updateTicker = false;
								if (high < lastPrice.doubleValue() || high == 0) {
									high = lastPrice.doubleValue();
									updateTicker = true;
								}
								
								if (low > lastPrice.doubleValue() || low == 0) {
									low = lastPrice.doubleValue();
									updateTicker = true;
								}
								
								if (updateTicker) {
									map.put("highPrice", high);
									map.put("lowPrice", low);
								}
								
								double firstPrice = (double) map.get("firstPrice");
								
								// 涨跌幅
								if (lastPrice.doubleValue() <= 0.0) {
									map.put("fudu", "0%");
								} else {
									DecimalFormat df = new DecimalFormat("0.00");
									double rate = 0.0;
									if (firstPrice > 0) {
										rate =  (lastPrice.doubleValue() - firstPrice) / firstPrice;
									}
									
									String div = df.format(rate *100);
									map.put("fudu", div.toString() + "%");
								}
							}
							
							lisMap.add(map);
							tmpMap.remove(map);
							break;
						}
					}
				}
			}
		}
		
		dataObj.put("data", JSONObject.toJSON(lisMap));
		return dataObj;
	}
	
	public static JSONObject getBPIData() {
		JSONObject dataObj = new JSONObject();
		
		String bpiData = spyMemcachedClient.get(BTCQMemecacheKeys.BPI_DATA);
		if (bpiData == null) {
			bpiData = "[]";
		}
		
		JSONArray array = JSONArray.parseArray(bpiData);
		dataObj.put("data", array);
		
		return dataObj;
	}
	
	public static JSONObject getCommonData(String type, String code) {
		JSONObject dataObj = new JSONObject();
		
		TradeAjaxVO vo = new TradeAjaxVO();
		if ("vcontract".equals(type)) {
			vo = spyMemcachedClient.get(BTCQMemecacheKeys.V_COMMON_DATA + code);
		} else if ("delivery".equals(type)) {
			vo = spyMemcachedClient.get(BTCQMemecacheKeys.D_COMMON_DATA + code);
		} else if ("spot".equals(type)) {
			vo = spyMemcachedClient.get(BTCQMemecacheKeys.COMMON_DATA + code);
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			vo = spyMemcachedClient.get(BTCQMemecacheKeys.COMMON_DATA + code);
		} else {
			vo = spyMemcachedClient.get(BTCQMemecacheKeys.COMMON_DATA + type + "_" + code);
		}
		
		dataObj.put("data",  JSONObject.toJSON(vo));
		
		return dataObj;
	}
	
	public static JSONObject getSymbols(String type) {
		JSONObject dataObj = new JSONObject();
		JSONArray array = new JSONArray();
		
		if ("vcontract".equals(type)) { 
			String arr = spyMemcachedClient.get(BTCQMemecacheKeys.V_SYMBOLS_DATA);
			if (StringUtil.isEmpty(arr)) arr = "[]";
			array = JSONArray.parseArray(arr);
		} else if ("spot".equals(type)) { 
			String arr = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_SYMBOLS_DATA2);
			
			if (arr == null) {
				
//				String tmp = spyMemcachedClient.get(BTCQMemecacheKeys.SPOT_SYMBOLS_DATA);
//				if (!StringUtil.isEmpty(tmp)) {
//					JSONArray tmpArray = JSONArray.parseArray(tmp);
//					List<BQSpotMarketCoinPair> transactionList = findCurrCoinPair();
//					for (BQSpotMarketCoinPair trans : transactionList) {
//						for (int i = 0; i < tmpArray.size(); i++) {
//							JSONObject obj = tmpArray.getJSONObject(i);
//							if (obj.getString("symbol").equals(trans.getCoin_pair_name())) {
//								array.add(obj);
//								break;
//							}
//						}
//					}
//					
//					arr = array.toString();
//					spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_SYMBOLS_DATA2, BTCQMemecacheKeys.TIMES.TWO_SECOND, arr);
//				}
				
				JSONArray tmpArray = new JSONArray();
				List<BQSpotMarketCoinPair> transactionList = findCurrCoinPair();
				for (BQSpotMarketCoinPair coinPair : transactionList) {
					String bqCode = coinPair.getCoin_pair_name();
					Integer digits = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.DIGITS);
					if (digits == null) {
						digits = Utils.getDigitsFromMinFloat(coinPair.getMin_float_price());
						spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
					}
					
					int amountPrecision = coinPair.getDecimals();
					JSONObject json = new JSONObject();
					json.put("symbol", bqCode);
					json.put("pricePrecision", digits);
					json.put("amountPrecision", amountPrecision);
					json.put("market", coinPair.getMarket_coin_name().toLowerCase());
					json.put("coin", coinPair.getCoin_name().toLowerCase());
					json.put("showName", coinPair.getCoin_name() + "/" + coinPair.getMarket_coin_name());
					
					tmpArray.add(json);
				}
				
				arr = tmpArray.toString();
				spyMemcachedClient.set(BTCQMemecacheKeys.SPOT_SYMBOLS_DATA2, BTCQMemecacheKeys.TIMES.ONE_MIN, arr);
			}
			
			if (StringUtil.isEmpty(arr)) arr = "[]";
			array = JSONArray.parseArray(arr);
		} else if ("delivery".equals(type)) {
			String arr = spyMemcachedClient.get(BTCQMemecacheKeys.D_SYMBOLS_DATA2);
			
			if (arr == null) {
				String tmp = spyMemcachedClient.get(BTCQMemecacheKeys.D_SYMBOLS_DATA);
				if (!StringUtil.isEmpty(tmp)) {
					JSONArray tmpArray = JSONArray.parseArray(tmp);
					List<DeliveryTransaction> transactionList = findCurrDTrans();
					for (DeliveryTransaction trans : transactionList) {
						for (int i = 0; i < tmpArray.size(); i++) {
							JSONObject obj = tmpArray.getJSONObject(i);
							if (obj.getString("symbol").equals(trans.getTransaction_code())) {
								array.add(obj);
								break;
							}
						}
					}
					
					arr = array.toString();
					spyMemcachedClient.set(BTCQMemecacheKeys.D_SYMBOLS_DATA2, BTCQMemecacheKeys.TIMES.TWO_SECOND, arr);
				}
			}
			
			if (StringUtil.isEmpty(arr)) arr = "[]";
			array = JSONArray.parseArray(arr);
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			String arr = spyMemcachedClient.get(BTCQMemecacheKeys.SYMBOLS_DATA2);
			
			if (arr == null) {
				String tmp = spyMemcachedClient.get(BTCQMemecacheKeys.SYMBOLS_DATA);
				if (!StringUtil.isEmpty(tmp)) {
					JSONArray tmpArray = JSONArray.parseArray(tmp);
					List<BQTransaction> transactionList = findCurrTrans();
					for (BQTransaction trans : transactionList) {
						for (int i = 0; i < tmpArray.size(); i++) {
							JSONObject obj = tmpArray.getJSONObject(i);
							if (obj.getString("symbol").equals(trans.getTransaction_code())) {
								array.add(obj);
								break;
							}
						}
					}
					
					arr = array.toString();
					spyMemcachedClient.set(BTCQMemecacheKeys.SYMBOLS_DATA2, BTCQMemecacheKeys.TIMES.TWO_SECOND, arr);
				}
			}
			
			if (StringUtil.isEmpty(arr)) arr = "[]";
			array = JSONArray.parseArray(arr);
		} else {
			String arr = spyMemcachedClient.get(BTCQMemecacheKeys.SYMBOLS_DATA2 + "_" + type);
			
			if (arr == null) {
				String tmp = spyMemcachedClient.get(BTCQMemecacheKeys.SYMBOLS_DATA + "_" + type);
				if (!StringUtil.isEmpty(tmp)) {
					JSONArray tmpArray = JSONArray.parseArray(tmp);
					List<NTransaction> transactionList = findNCurrTrans(type);
					for (NTransaction trans : transactionList) {
						for (int i = 0; i < tmpArray.size(); i++) {
							JSONObject obj = tmpArray.getJSONObject(i);
							if (obj.getString("symbol").equals(trans.getTransaction_code())) {
								array.add(obj);
								break;
							}
						}
					}
					
					arr = array.toString();
					spyMemcachedClient.set(BTCQMemecacheKeys.SYMBOLS_DATA2 + "_" + type, BTCQMemecacheKeys.TIMES.TWO_SECOND, arr);
				}
			}
			
			if (StringUtil.isEmpty(arr)) arr = "[]";
			array = JSONArray.parseArray(arr);
		}
		
		dataObj.put("data", array);
		
		return dataObj;
	}
	
	public static JSONObject getDepth(String type, String code) {
		String depthStr = null;
		if ("vcontract".equals(type)) {
			depthStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_DEPTH_CODE + code.toLowerCase());
		} else if ("delivery".equals(type)) {
			depthStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_DEPTH_CODE + code.toLowerCase());
		} else if ("spot".equals(type)) {
			depthStr = spyMemcachedClient.get(BTCQMemecacheKeys.DEPTH_CODE + code.toLowerCase());
		} else if (type == null || "".equals(type) || "contract".equals(type)) {
			depthStr = spyMemcachedClient.get(BTCQMemecacheKeys.DEPTH_CODE + code.toLowerCase());
		} else {
			depthStr = spyMemcachedClient.get(BTCQMemecacheKeys.DEPTH_CODE + type + "_" + code.toLowerCase());
		}
		
		JSONObject dataObj = new JSONObject();
		dataObj.put("data", JSONObject.parseObject(depthStr));
		
		return dataObj;
	}
	
	public static List<DeliveryTransaction> findCurrDTrans() {
		List<DeliveryTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = dTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
		}
		
		return transactionList;
	}
	
	public static List<VTransaction> findCurrVTrans() {
		List<VTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
		}
		
		return transactionList;
	}
	
	public static List<BQTransaction> findCurrTrans() {
		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = bqTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
		}
		
		return transactionList;
	}
	
	public static List<NTransaction> findNCurrTrans(String partition) {
		List<NTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + partition);
		if (transactionList == null) {
			transactionList = nTransactionService.findTransByState_new(partition, Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + partition, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
		}
		
		return transactionList;
	}
	
	public static List<BQSpotMarketCoinPair> findCurrCoinPair() {
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}
		return coinPairs;
	}
	
	public static JSONArray getDKline(String code, String timeType, boolean isLast) {
		boolean have = false;
		DeliveryTransaction currentTrans = null;
		List<DeliveryTransaction> trans = findCurrDTrans();
		for (DeliveryTransaction tran : trans) {
			if (tran.getTransaction_code().equals(code)) {
				currentTrans = tran;
				have = true;
				break;
			}
		}
		String prefixCode = "";
		if (have) {
			String typeCode = currentTrans.getTransaction_type_code();
			// 日交割（btc_d_one_minute_trade） 周交割（btc_w_one_minute_trade）季度交割（btc_q_one_minute_trade）
			prefixCode = typeCode + "_" + currentTrans.getDelivery_type();
		}
		
		JSONArray array = null;
		if (isLast) {
			String klineStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_LAST_KLINE + prefixCode + "_" + timeType);
			if (!StringUtil.isEmpty(klineStr)) {
				array = JSONArray.parseArray(klineStr);
			}
		} else {
			String klineStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_KLINE + prefixCode + "_" + timeType);
			if (!StringUtil.isEmpty(klineStr)) {
				array = JSONArray.parseArray(klineStr);
			}
		}

		if (array != null)
			return array;

		

		if (!have)
			return new JSONArray();

		String tradesStr = getDTradesStr(timeType, prefixCode);
		array = JSONArray.parseArray(tradesStr);
		if (isLast) {
			if (array.size() > 0) {
				JSONObject lastObj = array.getJSONObject(array.size() - 1);
				array = new JSONArray();
				array.add(lastObj);
				updateLastDTradesStr(array, timeType, prefixCode);
			} else {
				array = new JSONArray();
			}
			spyMemcachedClient.set(BTCQMemecacheKeys.D_LAST_KLINE + prefixCode + "_" + timeType,
					BTCQMemecacheKeys.TIMES.ONE_SECOND, array.toString());
		} else {
			updateLastDTradesStr(array, timeType, prefixCode);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_KLINE + prefixCode + "_" + timeType,
					BTCQMemecacheKeys.TIMES.ONE_SECOND, array.toString());
		}

		return array;
	}
	
	public static JSONArray getVKline(String code, String timeType, boolean isLast) {
		
		JSONArray array = null;
		if (isLast) {
			String klineStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_LAST_KLINE + code + "_" + timeType);
			 if (!StringUtil.isEmpty(klineStr)) {
				 array = JSONArray.parseArray(klineStr);
			 }
		} else {
			 String klineStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_KLINE + code + "_" + timeType);
			 if (!StringUtil.isEmpty(klineStr)) {
				 array = JSONArray.parseArray(klineStr);
			 }
		}
		
		if (array != null) return array;
		
		boolean have = false;
		List<VTransaction> trans = findCurrVTrans();
		for (VTransaction tran : trans) {
			if (tran.getTransaction_code().equals(code)) {
				have = true;
				break;
			}
		}
		
		if (!have) return new JSONArray();
		
		String tradesStr = getVTradesStr(timeType, code);
		array = JSONArray.parseArray(tradesStr);
		if (isLast) {
			if (array.size() > 0) {
				JSONObject lastObj = array.getJSONObject(array.size() - 1);
				array = new JSONArray();
				array.add(lastObj);
				updateLastVTradesStr(array, timeType, code);
			} else {
				array = new JSONArray();
			}
			spyMemcachedClient.set(BTCQMemecacheKeys.V_LAST_KLINE + code + "_" + timeType, BTCQMemecacheKeys.TIMES.ONE_SECOND, array.toString());
		} else {
			updateLastVTradesStr(array, timeType, code);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_KLINE + code + "_" + timeType, BTCQMemecacheKeys.TIMES.ONE_SECOND, array.toString());
		}
		
		return array;
	}
	
	public static JSONArray getNKline(String partition, String code, String timeType, boolean isLast) {
		JSONArray array = null;
		if (isLast) {
			String klineStr = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_KLINE + partition + "_" + code + "_" + timeType);
			if (!StringUtil.isEmpty(klineStr)) {
				array = JSONArray.parseArray(klineStr);
			}
		} else {
			String klineStr = spyMemcachedClient.get(BTCQMemecacheKeys.KLINE +  partition + "_" + code + "_" + timeType);
			if (!StringUtil.isEmpty(klineStr)) {
				array = JSONArray.parseArray(klineStr);
			}
		}
		
		if (array != null) return array;
		
		boolean have = false;
		List<NTransaction> trans = findNCurrTrans(partition);
		for (NTransaction tran : trans) {
			if (tran.getTransaction_code().equals(code)) {
				have = true;
				break;
			}
		}
		
		if (!have) return new JSONArray();
		
		String tradesStr = getNTradesStr(timeType, partition, code);
		array = JSONArray.parseArray(tradesStr);
		if (isLast) {
			if (array.size() > 0) {
				JSONObject lastObj = array.getJSONObject(array.size() - 1);
				array = new JSONArray();
				array.add(lastObj);
				updateLastNTradesStr(array, timeType, code, partition);
			} else {
				array = new JSONArray();
			}
			spyMemcachedClient.set(BTCQMemecacheKeys.LAST_KLINE +  partition + "_" + code + "_" + timeType, BTCQMemecacheKeys.TIMES.ONE_SECOND, array.toString());
		} else {
			updateLastNTradesStr(array, timeType, code, partition);
			spyMemcachedClient.set(BTCQMemecacheKeys.KLINE +  partition + "_" + code + "_" + timeType, BTCQMemecacheKeys.TIMES.ONE_SECOND, array.toString());
		}
		
		return array;
	}
	
	
	public static JSONArray getKline(String code, String timeType, boolean isLast) {
		JSONArray array = null;
		if (isLast) {
			String klineStr = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_KLINE + code + "_" + timeType);
			if (!StringUtil.isEmpty(klineStr)) {
				array = JSONArray.parseArray(klineStr);
			}
		} else {
			String klineStr = spyMemcachedClient.get(BTCQMemecacheKeys.KLINE + code + "_" + timeType);
			if (!StringUtil.isEmpty(klineStr)) {
				array = JSONArray.parseArray(klineStr);
			}
		}
		
		if (array != null) return array;
		
		boolean have = false;
		List<BQTransaction> trans = findCurrTrans();
		for (BQTransaction tran : trans) {
			if (tran.getTransaction_code().equals(code)) {
				have = true;
				break;
			}
		}
		
		if (!have) return new JSONArray();
		
		String tradesStr = getTradesStr(timeType, code);
		array = JSONArray.parseArray(tradesStr);
		if (isLast) {
			if (array.size() > 0) {
				JSONObject lastObj = array.getJSONObject(array.size() - 1);
				array = new JSONArray();
				array.add(lastObj);
				updateLastTradesStr(array, timeType, code);
			} else {
				array = new JSONArray();
			}
			spyMemcachedClient.set(BTCQMemecacheKeys.LAST_KLINE + code + "_" + timeType, BTCQMemecacheKeys.TIMES.ONE_SECOND, array.toString());
		} else {
			updateLastTradesStr(array, timeType, code);
			spyMemcachedClient.set(BTCQMemecacheKeys.KLINE + code + "_" + timeType, BTCQMemecacheKeys.TIMES.ONE_SECOND, array.toString());
		}
		
		return array;
	}
	
	public static JSONArray getSpotKline(String code, String timeType, boolean isLast) {
		JSONArray array = null;
		if (isLast) {
			String klineStr = spyMemcachedClient.get(BTCQMemecacheKeys.LAST_KLINE + code + "_" + timeType);
			 if (!StringUtil.isEmpty(klineStr)) {
				 array = JSONArray.parseArray(klineStr);
			 }
		} else {
			 String klineStr = spyMemcachedClient.get(BTCQMemecacheKeys.KLINE + code + "_" + timeType);
			 if (!StringUtil.isEmpty(klineStr)) {
				 array = JSONArray.parseArray(klineStr);
			 }
		}
		
		if (array != null) return array;
		
		boolean have = false;
		List<BQSpotMarketCoinPair> coinPairs = findCurrCoinPair();
		for (BQSpotMarketCoinPair coinPair : coinPairs) {
			if (coinPair.getCoin_pair_name().equals(code)) {
				have = true;
				break;
			}
		}
		
		if (!have) return new JSONArray();
		
		String tradesStr = getTradesStr(timeType, code);
		array = JSONArray.parseArray(tradesStr);
		if (isLast) {
			if (array.size() > 0) {
				JSONObject lastObj = array.getJSONObject(array.size() - 1);
				array = new JSONArray();
				array.add(lastObj);
				updateLastTradesStr(array, timeType, code);
			} else {
				array = new JSONArray();
			}
			spyMemcachedClient.set(BTCQMemecacheKeys.LAST_KLINE + code + "_" + timeType, BTCQMemecacheKeys.TIMES.ONE_SECOND, array.toString());
		} else {
			updateLastTradesStr(array, timeType, code);
			spyMemcachedClient.set(BTCQMemecacheKeys.KLINE + code + "_" + timeType, BTCQMemecacheKeys.TIMES.ONE_SECOND, array.toString());
		}
		
		return array;
	}
	
	private static String getDTradesStr(String _d_timeTypeStr, String dCode) {
		String tradesStr = "[]";
		if (!StringUtil.isEmpty(_d_timeTypeStr) && "1month".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_ONEMONTH + "_" + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_one_month").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_ONEMONTH + "_" + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_d_timeTypeStr) && "1week".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_ONEWEEK + "_" + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_one_week").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_ONEWEEK + "_" + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_d_timeTypeStr) && "1day".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_ONEDAY + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_one_day").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_ONEDAY + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_d_timeTypeStr) && "1min".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_ONEMINUTE + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_one_minute").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_ONEMINUTE + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_d_timeTypeStr) && "3min".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_THREEMINUTES + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_three_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_THREEMINUTES + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_d_timeTypeStr) && "5min".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_FIVEMINUTES + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_five_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_FIVEMINUTES + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_d_timeTypeStr) && "15min".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_FIFTEENMINUTES + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_fifteen_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_FIFTEENMINUTES + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_d_timeTypeStr) && "30min".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_HALFHOUR + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_half_hour").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_HALFHOUR + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_d_timeTypeStr) && "1hour".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_ONEHOUR + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_one_hour").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_ONEHOUR + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_d_timeTypeStr) && "2hour".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_TWOHOURS + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_two_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_TWOHOURS + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_d_timeTypeStr) && "4hour".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_FOURHOURS + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_four_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_FOURHOURS + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_d_timeTypeStr) && "6hour".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_SIXHOURS + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_six_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_SIXHOURS + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_d_timeTypeStr) && "12hour".equals(_d_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.D_TICKER_TWELVEHOURS + dCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(dCode, "_twelve_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TICKER_TWELVEHOURS + dCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		}
		
		return tradesStr;
	}
	
	private static String getVTradesStr(String _v_timeTypeStr,String vCode) {
		String tradesStr = "[]";
		if (!StringUtil.isEmpty(_v_timeTypeStr) && "1month".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_ONEMONTH + "_" + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_one_month").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEMONTH + "_" + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "1week".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_ONEWEEK + "_" + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_one_week").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEWEEK + "_" + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "1day".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_ONEDAY + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_one_day").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEDAY + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "1min".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_ONEMINUTE + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_one_minute").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEMINUTE + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "3min".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_THREEMINUTES + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_three_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_THREEMINUTES + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "5min".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_FIVEMINUTES + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_five_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_FIVEMINUTES + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "15min".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_FIFTEENMINUTES + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_fifteen_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_FIFTEENMINUTES + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "30min".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_HALFHOUR + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_half_hour").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_HALFHOUR + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "1hour".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_ONEHOUR + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_one_hour").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_ONEHOUR + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "2hour".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_TWOHOURS + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_two_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_TWOHOURS + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "4hour".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_FOURHOURS + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_four_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_FOURHOURS + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "6hour".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_SIXHOURS + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_six_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_SIXHOURS + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(_v_timeTypeStr) && "12hour".equals(_v_timeTypeStr)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.V_TICKER_TWELVEHOURS + vCode);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(vCode, "_v_twelve_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TICKER_TWELVEHOURS + vCode, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		}
		
		return tradesStr;
	}
	
	private static String getNTradesStr(String timeType, String partition,String code) {
		String tradesStr = "[]";
		if (!StringUtil.isEmpty(timeType) && "1month".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEMONTH + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_one_month").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEMONTH + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "1week".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEWEEK + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_one_week").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEWEEK + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("oneday".equals(timeType) || "1day".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEDAY + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_one_day").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEDAY + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("onemin".equals(timeType) || "1min".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEMINUTE + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_one_minute").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEMINUTE + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("threemin".equals(timeType) || "3min".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_THREEMINUTES + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_three_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_THREEMINUTES + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("fivemin".equals(timeType) || "5min".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_FIVEMINUTES + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_five_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_FIVEMINUTES + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("fifteenmin".equals(timeType) || "15min".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_FIFTEENMINUTES + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_fifteen_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_FIFTEENMINUTES + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("halfhour".equals(timeType) || "30min".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_HALFHOUR + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_half_hour").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_HALFHOUR + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("onehour".equals(timeType) || "1hour".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEHOUR + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_one_hour").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEHOUR + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("twohours".equals(timeType) || "2hour".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_TWOHOURS + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_two_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_TWOHOURS + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("fourhours".equals(timeType) || "4hour".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_FOURHOURS + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_four_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_FOURHOURS + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("sixhours".equals(timeType) || "6hour".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_SIXHOURS + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_six_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_SIXHOURS + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("twelvehours".equals(timeType) || "12hour".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_TWELVEHOURS + "_" + partition + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(partition + "_" + code, "_twelve_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_TWELVEHOURS + "_" + partition + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		}
		
		return tradesStr;
	}
	
	private static String getTradesStr(String timeType,String code) {
		String tradesStr = "[]";
		if (!StringUtil.isEmpty(timeType) && "1month".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEMONTH + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_one_month").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEMONTH + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && "1week".equals(timeType)) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEWEEK + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_one_week").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEWEEK + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("oneday".equals(timeType) || "1day".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEDAY + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_one_day").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEDAY + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("onemin".equals(timeType) || "1min".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEMINUTE + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_one_minute").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEMINUTE + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("threemin".equals(timeType) || "3min".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_THREEMINUTES + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_three_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_THREEMINUTES + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("fivemin".equals(timeType) || "5min".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_FIVEMINUTES + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_five_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_FIVEMINUTES + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("fifteenmin".equals(timeType) || "15min".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_FIFTEENMINUTES + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_fifteen_minutes").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_FIFTEENMINUTES + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("halfhour".equals(timeType) || "30min".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_HALFHOUR + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_half_hour").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_HALFHOUR + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("onehour".equals(timeType) || "1hour".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_ONEHOUR + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_one_hour").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_ONEHOUR + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("twohours".equals(timeType) || "2hour".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_TWOHOURS + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_two_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_TWOHOURS + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("fourhours".equals(timeType) || "4hour".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_FOURHOURS + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_four_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_FOURHOURS + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("sixhours".equals(timeType) || "6hour".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_SIXHOURS + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_six_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_SIXHOURS + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		} else if (!StringUtil.isEmpty(timeType) && ("twelvehours".equals(timeType) || "12hour".equals(timeType))) {
			tradesStr = spyMemcachedClient.get(BTCQMemecacheKeys.TICKER_TWELVEHOURS + "_" + code);
			if (null == tradesStr || "[]".equals(tradesStr)) {
				tradesStr = kLineTradeService.findPreJson(code, "_twelve_hours").toString();// 查询2000条数据
				spyMemcachedClient.set(BTCQMemecacheKeys.TICKER_TWELVEHOURS + "_" + code, BTCQMemecacheKeys.TIMES.TICKER_ONEMINUTE, tradesStr);
			}
		}
		
		return tradesStr;
	}
	
	private static void updateLastDTradesStr(JSONArray array, String timeType, String prefixCode) {
		if  (array.size() == 0) return;
		
		String typeStr = "";
		if (!StringUtil.isEmpty(timeType) && ("onemonth".equals(timeType) || "1month".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_ONEMONTH;
		} else if (!StringUtil.isEmpty(timeType) && ("oneweek".equals(timeType) || "1week".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_ONEWEEK;
		} else if (!StringUtil.isEmpty(timeType) && ("oneday".equals(timeType) || "1day".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_ONEDAY;
		} else if (!StringUtil.isEmpty(timeType) && ("onemin".equals(timeType) || "1min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_ONEMINUTE;
		} else if (!StringUtil.isEmpty(timeType) && ("threemin".equals(timeType) || "3min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_THREEMINUTES;
		} else if (!StringUtil.isEmpty(timeType) && ("fivemin".equals(timeType) || "5min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_FIVEMINUTES;
		} else if (!StringUtil.isEmpty(timeType) && ("fifteenmin".equals(timeType) || "15min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_FIFTEENMINUTES;
		} else if (!StringUtil.isEmpty(timeType) && ("halfhour".equals(timeType) || "30min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_HALFHOUR;
		} else if (!StringUtil.isEmpty(timeType) && ("onehour".equals(timeType) || "1hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_ONEHOUR;
		} else if (!StringUtil.isEmpty(timeType) && ("twohours".equals(timeType) || "2hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_TWOHOURS;
		} else if (!StringUtil.isEmpty(timeType) && ("fourhours".equals(timeType) || "4hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_FOURHOURS;
		} else if (!StringUtil.isEmpty(timeType) && ("sixhours".equals(timeType) || "6hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_SIXHOURS;
		} else if (!StringUtil.isEmpty(timeType) && ("twelvehours".equals(timeType) || "12hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.D_TICKER_TWELVEHOURS;
		}
		
		if (StringUtil.isEmpty(typeStr)) return;
		
		String lastTicker = spyMemcachedClient.get("last_" + typeStr + "_" + prefixCode);
		if (StringUtil.isEmpty(lastTicker)) return;
		
		JSONObject lastObj = JSONObject.parseObject(lastTicker);
		long lastTime = lastObj.getLong("time");
		
		JSONObject json = array.getJSONObject(array.size() - 1);
		long time = json.getLong("time");
		
		if (lastTime > time) {
			array.add(lastObj);
		} else if (!typeStr.equals(BTCQMemecacheKeys.TICKER_ONEMINUTE) && lastTime == time) {
			BigDecimal oldHigh = new BigDecimal(json.getDouble("high"));
			BigDecimal oldLow = new BigDecimal(json.getDouble("low"));
			
			BigDecimal lastHigh = new BigDecimal(lastObj.getDouble("high"));
			BigDecimal lastLow = new BigDecimal(lastObj.getDouble("low"));
			
			if (lastHigh.compareTo(oldHigh) > 0) {
				json.put("high", lastHigh);
			}
			if (lastLow.compareTo(oldLow) < 0) {
				json.put("low", lastLow);
			}
			
			json.put("last", new BigDecimal(lastObj.getDouble("last")).setScale(8, BigDecimal.ROUND_HALF_UP));
			json.put("vol", new BigDecimal(json.getDouble("vol")).add(new BigDecimal(lastObj.getDouble("vol"))).setScale(8, BigDecimal.ROUND_HALF_UP));
		}
	}
	
	private static void updateLastVTradesStr(JSONArray array, String timeType, String code) {
		if  (array.size() == 0) return;
		
		String typeStr = "";
		if (!StringUtil.isEmpty(timeType) && ("onemonth".equals(timeType) || "1month".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_ONEMONTH;
		} else if (!StringUtil.isEmpty(timeType) && ("oneweek".equals(timeType) || "1week".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_ONEWEEK;
		} else if (!StringUtil.isEmpty(timeType) && ("oneday".equals(timeType) || "1day".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_ONEDAY;
		} else if (!StringUtil.isEmpty(timeType) && ("onemin".equals(timeType) || "1min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_ONEMINUTE;
		} else if (!StringUtil.isEmpty(timeType) && ("threemin".equals(timeType) || "3min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_THREEMINUTES;
		} else if (!StringUtil.isEmpty(timeType) && ("fivemin".equals(timeType) || "5min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_FIVEMINUTES;
		} else if (!StringUtil.isEmpty(timeType) && ("fifteenmin".equals(timeType) || "15min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_FIFTEENMINUTES;
		} else if (!StringUtil.isEmpty(timeType) && ("halfhour".equals(timeType) || "30min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_HALFHOUR;
		} else if (!StringUtil.isEmpty(timeType) && ("onehour".equals(timeType) || "1hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_ONEHOUR;
		} else if (!StringUtil.isEmpty(timeType) && ("twohours".equals(timeType) || "2hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_TWOHOURS;
		} else if (!StringUtil.isEmpty(timeType) && ("fourhours".equals(timeType) || "4hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_FOURHOURS;
		} else if (!StringUtil.isEmpty(timeType) && ("sixhours".equals(timeType) || "6hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_SIXHOURS;
		} else if (!StringUtil.isEmpty(timeType) && ("twelvehours".equals(timeType) || "12hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.V_TICKER_TWELVEHOURS;
		}
		
		if (StringUtil.isEmpty(typeStr)) return;
		
		String lastTicker = spyMemcachedClient.get("last_" + typeStr + "_" + code);
		if (StringUtil.isEmpty(lastTicker)) return;
		
		JSONObject lastObj = JSONObject.parseObject(lastTicker);
		long lastTime = lastObj.getLong("time");
		
		JSONObject json = array.getJSONObject(array.size() - 1);
		long time = json.getLong("time");
		
		if (lastTime > time) {
			array.add(lastObj);
		} else if (!typeStr.equals(BTCQMemecacheKeys.V_TICKER_ONEMINUTE) && lastTime == time) {
			BigDecimal oldHigh = new BigDecimal(json.getDouble("high"));
			BigDecimal oldLow = new BigDecimal(json.getDouble("low"));
			
			BigDecimal lastHigh = new BigDecimal(lastObj.getDouble("high"));
			BigDecimal lastLow = new BigDecimal(lastObj.getDouble("low"));
			
			if (lastHigh.compareTo(oldHigh) > 0) {
				json.put("high", lastHigh);
			}
			if (lastLow.compareTo(oldLow) < 0) {
				json.put("low", lastLow);
			}
			
			json.put("last", new BigDecimal(lastObj.getDouble("last")).setScale(8, BigDecimal.ROUND_HALF_UP));
			json.put("vol", new BigDecimal(json.getDouble("vol")).add(new BigDecimal(lastObj.getDouble("vol"))).setScale(8, BigDecimal.ROUND_HALF_UP));
		}
	}
	
	private static void updateLastNTradesStr(JSONArray array, String timeType, String code, String partition) {
		if (array.size() == 0) return;
		if (StringUtil.isEmpty(partition)) return;
		
		String typeStr = "";
		if (!StringUtil.isEmpty(timeType) && ("onemonth".equals(timeType) || "1month".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_ONEMONTH;
		} else if (!StringUtil.isEmpty(timeType) && ("oneweek".equals(timeType) || "1week".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_ONEWEEK;
		} else if (!StringUtil.isEmpty(timeType) && ("oneday".equals(timeType) || "1day".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_ONEDAY;
		} else if (!StringUtil.isEmpty(timeType) && ("onemin".equals(timeType) || "1min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_ONEMINUTE;
		} else if (!StringUtil.isEmpty(timeType) && ("threemin".equals(timeType) || "3min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_THREEMINUTES;
		} else if (!StringUtil.isEmpty(timeType) && ("fivemin".equals(timeType) || "5min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_FIVEMINUTES;
		} else if (!StringUtil.isEmpty(timeType) && ("fifteenmin".equals(timeType) || "15min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_FIFTEENMINUTES;
		} else if (!StringUtil.isEmpty(timeType) && ("halfhour".equals(timeType) || "30min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_HALFHOUR;
		} else if (!StringUtil.isEmpty(timeType) && ("onehour".equals(timeType) || "1hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_ONEHOUR;
		} else if (!StringUtil.isEmpty(timeType) && ("twohours".equals(timeType) || "2hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_TWOHOURS;
		} else if (!StringUtil.isEmpty(timeType) && ("fourhours".equals(timeType) || "4hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_FOURHOURS;
		} else if (!StringUtil.isEmpty(timeType) && ("sixhours".equals(timeType) || "6hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_SIXHOURS;
		} else if (!StringUtil.isEmpty(timeType) && ("twelvehours".equals(timeType) || "12hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_TWELVEHOURS;
		}
		
		if (StringUtil.isEmpty(typeStr)) return;
		
		String lastTicker = spyMemcachedClient.get("last_" + typeStr + "_" + partition + "_" + code);
		if (StringUtil.isEmpty(lastTicker)) return;
		
		JSONObject lastObj = JSONObject.parseObject(lastTicker);
		long lastTime = lastObj.getLong("time");
		
		JSONObject json = array.getJSONObject(array.size() - 1);
		long time = json.getLong("time");
		
		if (lastTime > time) {
			array.add(lastObj);
		} else if (!typeStr.equals(BTCQMemecacheKeys.TICKER_ONEMINUTE) && lastTime == time) {
			BigDecimal oldHigh = new BigDecimal(json.getDouble("high"));
			BigDecimal oldLow = new BigDecimal(json.getDouble("low"));
			
			BigDecimal lastHigh = new BigDecimal(lastObj.getDouble("high"));
			BigDecimal lastLow = new BigDecimal(lastObj.getDouble("low"));
			
			if (lastHigh.compareTo(oldHigh) > 0) {
				json.put("high", lastHigh);
			}
			if (lastLow.compareTo(oldLow) < 0) {
				json.put("low", lastLow);
			}
			
			json.put("last", new BigDecimal(lastObj.getDouble("last")).setScale(8, BigDecimal.ROUND_HALF_UP));
			json.put("vol", new BigDecimal(json.getDouble("vol")).add(new BigDecimal(lastObj.getDouble("vol"))).setScale(8, BigDecimal.ROUND_HALF_UP));
		}
	}
	
	private static void updateLastTradesStr(JSONArray array, String timeType, String code) {
		if  (array.size() == 0) return;
		
		String typeStr = "";
		if (!StringUtil.isEmpty(timeType) && ("onemonth".equals(timeType) || "1month".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_ONEMONTH;
		} else if (!StringUtil.isEmpty(timeType) && ("oneweek".equals(timeType) || "1week".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_ONEWEEK;
		} else if (!StringUtil.isEmpty(timeType) && ("oneday".equals(timeType) || "1day".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_ONEDAY;
		} else if (!StringUtil.isEmpty(timeType) && ("onemin".equals(timeType) || "1min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_ONEMINUTE;
		} else if (!StringUtil.isEmpty(timeType) && ("threemin".equals(timeType) || "3min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_THREEMINUTES;
		} else if (!StringUtil.isEmpty(timeType) && ("fivemin".equals(timeType) || "5min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_FIVEMINUTES;
		} else if (!StringUtil.isEmpty(timeType) && ("fifteenmin".equals(timeType) || "15min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_FIFTEENMINUTES;
		} else if (!StringUtil.isEmpty(timeType) && ("halfhour".equals(timeType) || "30min".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_HALFHOUR;
		} else if (!StringUtil.isEmpty(timeType) && ("onehour".equals(timeType) || "1hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_ONEHOUR;
		} else if (!StringUtil.isEmpty(timeType) && ("twohours".equals(timeType) || "2hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_TWOHOURS;
		} else if (!StringUtil.isEmpty(timeType) && ("fourhours".equals(timeType) || "4hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_FOURHOURS;
		} else if (!StringUtil.isEmpty(timeType) && ("sixhours".equals(timeType) || "6hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_SIXHOURS;
		} else if (!StringUtil.isEmpty(timeType) && ("twelvehours".equals(timeType) || "12hour".equals(timeType))) {
			typeStr = BTCQMemecacheKeys.TICKER_TWELVEHOURS;
		}
		
		if (StringUtil.isEmpty(typeStr)) return;
		
		String lastTicker = spyMemcachedClient.get("last_" + typeStr + "_" + code);
		if (StringUtil.isEmpty(lastTicker)) return;
		
		JSONObject lastObj = JSONObject.parseObject(lastTicker);
		long lastTime = lastObj.getLong("time");
		
		JSONObject json = array.getJSONObject(array.size() - 1);
		long time = json.getLong("time");
		
		if (lastTime > time) {
			array.add(lastObj);
		} else if (!typeStr.equals(BTCQMemecacheKeys.TICKER_ONEMINUTE) && lastTime == time) {
			BigDecimal oldHigh = new BigDecimal(json.getDouble("high"));
			BigDecimal oldLow = new BigDecimal(json.getDouble("low"));
			
			BigDecimal lastHigh = new BigDecimal(lastObj.getDouble("high"));
			BigDecimal lastLow = new BigDecimal(lastObj.getDouble("low"));
			
			if (lastHigh.compareTo(oldHigh) > 0) {
				json.put("high", lastHigh);
			}
			if (lastLow.compareTo(oldLow) < 0) {
				json.put("low", lastLow);
			}
			
			json.put("last", new BigDecimal(lastObj.getDouble("last")).setScale(8, BigDecimal.ROUND_HALF_UP));
			json.put("vol", new BigDecimal(json.getDouble("vol")).add(new BigDecimal(lastObj.getDouble("vol"))).setScale(8, BigDecimal.ROUND_HALF_UP));
		}
	}
	
	public static void checkPushOrderTrade() {
		for (WebSocket item : pushWebSocketSet) {
			Long uId = item.userId;
			if (uId == null) {
				continue;
			}
			
			if (item.subPushList.size() == 0) {
				return;
			}
			
			for (String subData : item.subPushList) {
				String[] subs = subData.split("[.]");
				String code = subs[1];
		    	
				// order.$code.sub.trade
				String type = "order." + code + ".sub.trade";
				if (!item.subPushList.contains(type)) {
					continue;
				}

				do {
					Long lastOrderId = spyMemcachedClient.get(uId + BTCQMemecacheKeys.WS_ORDER_LAST_ORDER_ID + code);
					if (lastOrderId == null) {
						lastOrderId = tradeLogService.getLastTradeId(code, uId);
						spyMemcachedClient.set(uId + BTCQMemecacheKeys.WS_ORDER_LAST_ORDER_ID + code,
								BTCQMemecacheKeys.TIMES.BPI, lastOrderId);
						break;
					}
					
					List<TradeLog> lastTrades = tradeLogService.findGtFrom(code, uId, lastOrderId, 100);
					if (lastTrades == null || lastTrades.size() == 0) break;
					
					lastOrderId = lastTrades.get(0).getTrade_id();
					spyMemcachedClient.set(uId + BTCQMemecacheKeys.WS_ORDER_LAST_ORDER_ID + code,
							BTCQMemecacheKeys.TIMES.BPI, lastOrderId);
					
					List<TradeLogVo> lastTradeVos = tradesToTradeVos(lastTrades);
					long time = System.currentTimeMillis() / 1000;
					JSONObject msgObj = new JSONObject();
					msgObj.put("ch", type);
					msgObj.put("ts", time);
					msgObj.put("data", JSONArray.toJSON(lastTradeVos));
					try {
						item.sendMessage(msgObj.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					spyMemcachedClient.set(uId + BTCQMemecacheKeys.WS_ORDER_LIST + code,
							BTCQMemecacheKeys.TIMES.BPI, lastTradeVos);
					
				} while (false);
			}
		}
	}

	private static JSONArray getReqOrderTrade(Long userId, String code) {
		JSONArray array = null;
		List<BQTransaction> transList = findCurrTrans();
		BQTransaction bqTransaction = null;
		for (BQTransaction trans : transList) {
			if (trans.getTransaction_code().equals(code)) {
				bqTransaction = trans;
				break;
			}
		}

		if (bqTransaction == null) {
			return array;
		}

		List<TradeLogVo> lastTrades = spyMemcachedClient.get(userId + BTCQMemecacheKeys.WS_ORDER_LIST + code);
		if (lastTrades == null) {
			List<TradeLog> trades = tradeLogService.findByUserAndTime(code, userId, 0, "", 0, 0, 100);
			if (trades != null && trades.size() > 0) {
				lastTrades = tradesToTradeVos(trades);

				spyMemcachedClient.set(userId + BTCQMemecacheKeys.WS_ORDER_LIST + code, BTCQMemecacheKeys.TIMES.BPI,
						lastTrades);
			}
		}

		array = JSONArray.parseArray(JSON.toJSONString(lastTrades));
		return array;
	}

	private static List<TradeLogVo> tradesToTradeVos(List<TradeLog> trades) {
		List<TradeLogVo> lastTrades = new ArrayList<TradeLogVo>();

		if (trades == null || trades.size() <= 0)
			return lastTrades;
		for (TradeLog trade : trades) {
			TradeLogVo vo = new TradeLogVo();
			vo.setSymbol(trade.getTransaction_code());
			vo.setTradeId(trade.getTrade_id());
			vo.setTradeType(trade.getTrade_type_id());
			vo.setDirection(trade.getTrade_trans_dirction());
			vo.setMultiple(trade.getTrade_multiple());
			vo.setHoldPrice(trade.getHold_price().doubleValue());
			vo.setTradePrice(trade.getTrade_average_price().doubleValue());
			vo.setTradeCount(trade.getTrade_count().doubleValue());
			vo.setTradeDeposit(trade.getTrade_deposit().doubleValue());
			vo.setTradeIncome(trade.getTrade_income().doubleValue());
			vo.setTradeFee(trade.getTrade_fee().doubleValue());
			vo.setTradeTime(trade.getTrade_time());

			lastTrades.add(vo);
		}

		return lastTrades;
	}

	private static JSONArray getReqOrderPending(Long uId, String code) {
		JSONArray array = null;
		List<BQTransaction> transList = findCurrTrans();
		BQTransaction bqTransaction = null;
		for (BQTransaction trans : transList) {
			if (trans.getTransaction_code().equals(code)) {
				bqTransaction = trans;
				break;
			}
		}

		if (bqTransaction == null) {
			return array;
		}

		List<PendingDelegationVO> list = spyMemcachedClient.get(uId + BTCQMemecacheKeys.WS_ORDER_UPDATE + code);
		if (list == null) {
			list = getOrderPending(uId, code);
			if (list != null && list.size() > 0) {
				spyMemcachedClient.set(uId + BTCQMemecacheKeys.WS_ORDER_LIST + code, BTCQMemecacheKeys.TIMES.BPI, list);
			}
		}

		array = JSONArray.parseArray(JSON.toJSONString(list));
		return array;
	}
	
	public static void addPendingToMemcached(PendingDelegationCache cache, SpyMemcachedClient memClient) {
		if (cache == null) return;
		
		String code = cache.getTransaction_code();
		List<Long> subbedUsers = memClient.get(code + BTCQMemecacheKeys.WS_SUBBED_USERS);
		if (subbedUsers == null) {
			return;
		}
		Long uId = Long.valueOf(cache.getF_user_id());
		if (!subbedUsers.contains(uId)) {
			return;
		}

		String lk = memClient.lock(uId + BTCQMemecacheKeys.WS_ORDER_NEW_LIST_LOCK + code);
		if (!StringUtil.isEmpty(lk)) {
			try {
				List<PendingDelegationVO> newVoList = memClient.get(uId + BTCQMemecacheKeys.WS_ORDER_NEW_LIST + code);
				if (newVoList == null) {
					newVoList = new ArrayList<PendingDelegationVO>();
				}
				
				PendingDelegationVO newVo = new PendingDelegationVO();
				newVo.setDelegation_id(cache.getDelegation_id());
				newVo.setOrderId(cache.getPending_no() + "");
				newVo.setSymbol(cache.getTransaction_code());
				newVo.setAmount(cache.getDelegation_count().doubleValue());
				newVo.setDealAmount(cache.getDeal_count().doubleValue());
				newVo.setUndealAmount(cache.getUndeal_count().doubleValue());
				newVo.setPrice(cache.getDelegation_price().doubleValue());
				newVo.setCreateTime(cache.getDelegation_time());
				newVo.setMultiple(cache.getMultiple());
				newVo.setPriority(cache.getPriority());
				newVo.setDirection(cache.getDirection());
				newVo.setDeposit(cache.getDeposit().doubleValue());
				newVo.setState(Contents.PENDING_STATE_SUBMITTING);
				
				newVoList.add(newVo);
				
				memClient.set(uId + BTCQMemecacheKeys.WS_ORDER_NEW_LIST + code, BTCQMemecacheKeys.TIMES.BPI, newVoList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				memClient.unlock(uId + BTCQMemecacheKeys.WS_ORDER_NEW_LIST_LOCK + code, lk);
			}
		}
	}
	
	public static void addPendingToMemcached(String partition, NPendingDelegationCache cache, SpyMemcachedClient memClient) {
		if (cache == null) return;
		
		String code = cache.getTransaction_code();
		List<Long> subbedUsers = memClient.get(partition + "_" + code + BTCQMemecacheKeys.WS_SUBBED_USERS);
		if (subbedUsers == null) {
			return;
		}
		Long uId = Long.valueOf(cache.getF_user_id());
		if (!subbedUsers.contains(uId)) {
			return;
		}

		String lk = memClient.lock(uId + BTCQMemecacheKeys.WS_ORDER_NEW_LIST_LOCK + partition + "_" + code);
		if (!StringUtil.isEmpty(lk)) {
			try {
				List<PendingDelegationVO> newVoList = memClient.get(uId + BTCQMemecacheKeys.WS_ORDER_NEW_LIST + partition + "_" + code);
				if (newVoList == null) {
					newVoList = new ArrayList<PendingDelegationVO>();
				}
				
				PendingDelegationVO newVo = new PendingDelegationVO();
				newVo.setDelegation_id(cache.getDelegation_id());
				newVo.setOrderId(cache.getPending_no() + "");
				newVo.setSymbol(cache.getTransaction_code());
				newVo.setAmount(cache.getDelegation_count().doubleValue());
				newVo.setDealAmount(cache.getDeal_count().doubleValue());
				newVo.setUndealAmount(cache.getUndeal_count().doubleValue());
				newVo.setPrice(cache.getDelegation_price().doubleValue());
				newVo.setCreateTime(cache.getDelegation_time());
				newVo.setMultiple(cache.getMultiple());
				newVo.setPriority(cache.getPriority());
				newVo.setDirection(cache.getDirection());
				newVo.setDeposit(cache.getDeposit().doubleValue());
				newVo.setState(Contents.PENDING_STATE_SUBMITTING);
				
				newVoList.add(newVo);
				
				memClient.set(uId + BTCQMemecacheKeys.WS_ORDER_NEW_LIST + partition + "_" + code, BTCQMemecacheKeys.TIMES.BPI, newVoList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				memClient.unlock(uId + BTCQMemecacheKeys.WS_ORDER_NEW_LIST_LOCK + partition + "_" + code, lk);
			}
		}
	}
	
	public static void addNPendingToMemcached(String partition, NPendingDelegationCache cache) {
		addPendingToMemcached(partition, cache, spyMemcachedClient);
	}
	
	public static void addPendingToMemcached(PendingDelegationCache cache) {
		addPendingToMemcached(cache, spyMemcachedClient);
	}
	
	public static void checkPushNewOrderPending() {
		
		for (WebSocket item : pushWebSocketSet) {
			Long uId = item.userId;
			if (uId == null) {
				continue;
			}
			
			if (item.subPushList.size() == 0) {
				return;
			}
			
			for (String subData : item.subPushList) {
				String[] subs = subData.split("[.]");
				String code = subs[1];

				// order.$code.sub.pending
				String type = "order." + code + ".sub.pending";
				if (!item.subPushList.contains(type)) {
					continue;
				}
				List<PendingDelegationVO> newVoList = spyMemcachedClient.get(uId + BTCQMemecacheKeys.WS_ORDER_NEW_LIST + code);
				if (newVoList == null) {
					continue;
				}
				
				List<PendingDelegationVO> oldVoList = spyMemcachedClient.get(uId + BTCQMemecacheKeys.WS_ORDER_UPDATE + code);
				if (oldVoList == null) {
					oldVoList = new ArrayList<PendingDelegationVO>();
				}
				
				List<PendingDelegationVO> pushList = new ArrayList<PendingDelegationVO>();
				for (PendingDelegationVO newCache : newVoList) {
					boolean have = false;
					for (PendingDelegationVO oldCache : oldVoList) {
						if (newCache.getOrderId().equals(oldCache.getOrderId())) {
							have = true;
							break;
						}
					}
					if (!have) {
						oldVoList.add(newCache);
						pushList.add(newCache);
					}
				}
				if (oldVoList.size() > 0) {
					spyMemcachedClient.set(uId + BTCQMemecacheKeys.WS_ORDER_UPDATE + code, BTCQMemecacheKeys.TIMES.BPI, oldVoList);
				}
				
				if (pushList.size() > 0) {
					long time = System.currentTimeMillis() / 1000;
					JSONObject msgObj = new JSONObject();
					msgObj.put("ch", type);
					msgObj.put("ts", time);
					msgObj.put("data", JSONArray.toJSON(pushList));
					try {
						item.sendMessage(msgObj.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				String lk = spyMemcachedClient.lock(uId + BTCQMemecacheKeys.WS_ORDER_NEW_LIST_LOCK + code);
				if (!StringUtil.isEmpty(lk)) {
					try {
						spyMemcachedClient.delete(uId + BTCQMemecacheKeys.WS_ORDER_NEW_LIST + code);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						spyMemcachedClient.unlock(uId + BTCQMemecacheKeys.WS_ORDER_NEW_LIST_LOCK + code, lk);
					}
				}
			}
		}
	}

	public static void checkPushOrderPending() {

		for (WebSocket item : pushWebSocketSet) {
			Long uId = item.userId;
			if (uId == null) {
				continue;
			}
			
			if (item.subPushList.size() == 0) {
				return;
			}
			
			for (String subData : item.subPushList) {
				String[] subs = subData.split("[.]");
				String code = subs[1];

				// order.$code.sub.pending
				String type = "order." + code + ".sub.pending";
				if (!item.subPushList.contains(type)) {
					continue;
				}

				List<PendingDelegationVO> oldVoList = spyMemcachedClient.get(uId + BTCQMemecacheKeys.WS_ORDER_UPDATE + code);
				List<PendingDelegationVO> newVoList = getOrderPending(uId, code);
				do {
					spyMemcachedClient.set(uId + BTCQMemecacheKeys.WS_ORDER_UPDATE + code, BTCQMemecacheKeys.TIMES.BPI,
							newVoList);
					if (oldVoList == null) {
						oldVoList = newVoList;
					} else {
						List<PendingDelegationVO> pushPendingList = getPushPendingList(oldVoList, newVoList, uId);
						if (pushPendingList != null && pushPendingList.size() > 0) {
							oldVoList = pushPendingList;
							
							long time = System.currentTimeMillis() / 1000;
							JSONObject msgObj = new JSONObject();
							msgObj.put("ch", type);
							msgObj.put("ts", time);
							msgObj.put("data", JSONArray.toJSON(oldVoList));
							try {
								item.sendMessage(msgObj.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				} while (false);
			}
		}
	}

	private static List<PendingDelegationVO> getPushPendingList(List<PendingDelegationVO> oldVoList,
			List<PendingDelegationVO> newVoList, Long uid) {
		List<PendingDelegationVO> pushList = new ArrayList<PendingDelegationVO>();

		List<PendingDelegationVO> newOrders = new ArrayList<PendingDelegationVO>();// 新增挂单(增)
		List<PendingDelegationVO> alreadyOrCancle = new ArrayList<PendingDelegationVO>();// 已成交或撤销的挂单(删)
		List<PendingDelegationVO> changeOrders = new ArrayList<PendingDelegationVO>();// 相同挂单号已改变(改)
		
		for (PendingDelegationVO oldVo : oldVoList) {
			boolean change = false;
			boolean same = false;
			for (PendingDelegationVO newVo : newVoList) {
				if (oldVo.getOrderId().equals(newVo.getOrderId())) {
					if ((!oldVo.getState().equals(newVo.getState()) || oldVo.getUndealAmount() != newVo.getUndealAmount())) {
						changeOrders.add(newVo);
						change = true;
						break;
					}
					newVoList.remove(newVo);
					same = true;
					break;
				}
			}

			if (!change && !same) {
				alreadyOrCancle.add(oldVo);
			}
		}

		newVoList.removeAll(changeOrders);
		newOrders = newVoList;
		
		List<PendingDelegationVO> alreadyOrCancleVos = new ArrayList<PendingDelegationVO>();
		if (alreadyOrCancle.size() > 0) {
			for (PendingDelegationVO vo : alreadyOrCancle) {
				PendingDelegationVO data = new PendingDelegationVO();
				List<PendingLog> pendingLogs = pendingLogService.findByPendingNo(vo.getSymbol(), vo.getOrderId(), uid);
				if (pendingLogs.size() > 0) {
					PendingLog firstLog = pendingLogs.get(0);

					data.setSymbol(firstLog.getTransaction_code());
					data.setOrderId(firstLog.getPending_no() + "");
					data.setAmount(firstLog.getDelegation_count().doubleValue());
					data.setDealAmount(firstLog.getDeal_count().doubleValue());
					data.setUndealAmount(firstLog.getUndeal_count().doubleValue());
					data.setPrice(firstLog.getDelegation_price().doubleValue());
					data.setCreateTime(firstLog.getDelegation_time());
					data.setMultiple(firstLog.getMultiple());
					data.setDeposit(firstLog.getDeposit().doubleValue());

					if (firstLog.getTrade_type_id() == 1) {
						data.setPriority(Contents.PRIORITY_KC);
						data.setDirection(Contents.DIRECTION_BUY);
					} else if (firstLog.getTrade_type_id() == 2) {
						data.setPriority(Contents.PRIORITY_PC);
						data.setDirection(Contents.DIRECTION_SELL);
					} else if (firstLog.getTrade_type_id() == 3) {
						data.setPriority(Contents.PRIORITY_KC);
						data.setDirection(Contents.DIRECTION_SELL);
					} else if (firstLog.getTrade_type_id() == 4) {
						data.setPriority(Contents.PRIORITY_PC);
						data.setDirection(Contents.DIRECTION_BUY);
					}

					int action = 0;
					for (PendingLog log : pendingLogs) {
						if (log.getAction() != 0) {
							action = log.getAction();
							data.setDealAmount(log.getDeal_count().doubleValue());
							data.setUndealAmount(log.getUndeal_count().doubleValue());
							data.setDeposit(log.getDeposit().doubleValue());
							break;
						}
					}

					if (action == 1) {
						data.setState(Contents.PENDING_STATE_FILLED);
					} else if (action == 4) {
						data.setState(Contents.PENDING_STATE_CANCELED);
					} else {
						data.setState(Contents.PENDING_STATE_SUBMITTING);
					}
				}

				alreadyOrCancleVos.add(data);
			}
		}

		pushList.addAll(alreadyOrCancleVos);
		pushList.addAll(newOrders);
		pushList.addAll(changeOrders);

		return pushList;
	}

	private static List<PendingDelegationVO> getOrderPending(Long uId, String code) {
		List<PendingDelegationVO> newVoList = new ArrayList<PendingDelegationVO>();

		List<PendingDelegation> pdList = pendingDelegationService.findPendingByUserIdAndTime(code, uId, 0, 0, 0, "", 100);
		List<PendingDelegationCache> pdCacheList = pendingDelegationCacheService.findPendingByUserIdAndTime(code, uId, 0, 0, 0, "", 100);
		if (pdCacheList.size() > 0) {
			for (PendingDelegationCache cache : pdCacheList) {
				PendingDelegationVO data = new PendingDelegationVO();
				data.setDelegation_id(cache.getDelegation_id());
				data.setOrderId(cache.getPending_no() + "");
				data.setSymbol(cache.getTransaction_code());
				data.setAmount(cache.getDelegation_count().doubleValue());
				data.setDealAmount(cache.getDeal_count().doubleValue());
				data.setUndealAmount(cache.getUndeal_count().doubleValue());
				data.setPrice(cache.getDelegation_price().doubleValue());
				data.setCreateTime(cache.getDelegation_time());
				data.setMultiple(cache.getMultiple());
				data.setPriority(cache.getPriority());
				data.setDirection(cache.getDirection());
				data.setDeposit(cache.getDeposit().doubleValue());
				data.setState(Contents.PENDING_STATE_SUBMITTING);

				newVoList.add(data);
			}
		}

		if (pdList.size() > 0) {
			for (PendingDelegation pd : pdList) {
				PendingDelegationVO data = new PendingDelegationVO();
				data.setDelegation_id(pd.getDelegation_id());
				data.setOrderId(pd.getPending_no() + "");
				data.setSymbol(pd.getTransaction_code());
				data.setAmount(pd.getDelegation_count().doubleValue());
				data.setDealAmount(pd.getDeal_count().doubleValue());
				data.setUndealAmount(pd.getUndeal_count().doubleValue());
				data.setPrice(pd.getDelegation_price().doubleValue());
				data.setCreateTime(pd.getDelegation_time());
				data.setMultiple(pd.getMultiple());
				data.setPriority(pd.getPriority());
				data.setDirection(pd.getDirection());
				data.setDeposit(pd.getDeposit().doubleValue());
				data.setState(Contents.PENDING_STATE_SUBMITTED);

				newVoList.add(data);
			}
		}

		return newVoList;
	}
	
	/************************************************** 交割合约推送 *******************************************/
	public static void checkDPushOrderTrade() {
		for (WebSocket item : pushWebSocketSet) {
			Long uId = item.userId;
			if (uId == null) {
				continue;
			}
			
			if (item.deliverySubPushList.size() == 0) {
				return;
			}
			
			for (String subData : item.deliverySubPushList) {
				String[] subs = subData.split("[.]");
				String code = subs[1];
		    	
				// order.$code.sub.trade.delivery
				String type = "order." + code + ".sub.trade.delivery";
				if (!item.deliverySubPushList.contains(type)) {
					continue;
				}

				do {
					Long lastOrderId = spyMemcachedClient.get(uId + BTCQMemecacheKeys.D_WS_ORDER_LAST_ORDER_ID + code);
					if (lastOrderId == null) {
						lastOrderId = dTradeLogService.getLastTradeId(code, uId);
						spyMemcachedClient.set(uId + BTCQMemecacheKeys.D_WS_ORDER_LAST_ORDER_ID + code,
								BTCQMemecacheKeys.TIMES.BPI, lastOrderId);
						break;
					}
					
					List<DeliveryTradeLog> lastTrades = dTradeLogService.findGtFrom(code, uId, lastOrderId, 100);
					if (lastTrades == null || lastTrades.size() == 0) break;
					
					lastOrderId = lastTrades.get(0).getTrade_id();
					spyMemcachedClient.set(uId + BTCQMemecacheKeys.D_WS_ORDER_LAST_ORDER_ID + code,
							BTCQMemecacheKeys.TIMES.BPI, lastOrderId);
					
					List<TradeLogVo> lastTradeVos = tradesDToTradeVos(lastTrades);
					long time = System.currentTimeMillis() / 1000;
					JSONObject msgObj = new JSONObject();
					msgObj.put("ch", type);
					msgObj.put("ts", time);
					msgObj.put("data", JSONArray.toJSON(lastTradeVos));
					try {
						item.sendMessage(msgObj.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					spyMemcachedClient.set(uId + BTCQMemecacheKeys.D_WS_ORDER_LIST + code,
							BTCQMemecacheKeys.TIMES.BPI, lastTradeVos);
					
				} while (false);
			}
		}
	}

	private static JSONArray getDReqOrderTrade(Long userId, String code) {
		JSONArray array = null;
		List<DeliveryTransaction> transList = findCurrDTrans();
		DeliveryTransaction bqTransaction = null;
		for (DeliveryTransaction trans : transList) {
			if (trans.getTransaction_code().equals(code)) {
				bqTransaction = trans;
				break;
			}
		}

		if (bqTransaction == null) {
			return array;
		}

		List<TradeLogVo> lastTrades = spyMemcachedClient.get(userId + BTCQMemecacheKeys.D_WS_ORDER_LIST + code);
		if (lastTrades == null) {
			List<DeliveryTradeLog> trades = dTradeLogService.findByUserAndTime(code, userId, 0, "", 0, 0, 100);
			if (trades != null && trades.size() > 0) {
				lastTrades = tradesDToTradeVos(trades);

				spyMemcachedClient.set(userId + BTCQMemecacheKeys.D_WS_ORDER_LIST + code, BTCQMemecacheKeys.TIMES.BPI,
						lastTrades);
			}
		}

		array = JSONArray.parseArray(JSON.toJSONString(lastTrades));
		return array;
	}

	private static List<TradeLogVo> tradesDToTradeVos(List<DeliveryTradeLog> trades) {
		List<TradeLogVo> lastTrades = new ArrayList<TradeLogVo>();

		if (trades == null || trades.size() <= 0)
			return lastTrades;
		for (DeliveryTradeLog trade : trades) {
			TradeLogVo vo = new TradeLogVo();
			vo.setSymbol(trade.getTransaction_code());
			vo.setTradeId(trade.getTrade_id());
			vo.setTradeType(trade.getTrade_type_id());
			vo.setDirection(trade.getTrade_trans_dirction());
			vo.setMultiple(trade.getTrade_multiple());
			vo.setHoldPrice(trade.getHold_price().doubleValue());
			vo.setTradePrice(trade.getTrade_average_price().doubleValue());
			vo.setTradeCount(trade.getTrade_count().doubleValue());
			vo.setTradeDeposit(trade.getTrade_deposit().doubleValue());
			vo.setTradeIncome(trade.getTrade_income().doubleValue());
			vo.setTradeFee(trade.getTrade_fee().doubleValue());
			vo.setTradeTime(trade.getTrade_time());

			lastTrades.add(vo);
		}

		return lastTrades;
	}

	private static JSONArray getDReqOrderPending(Long uId, String code) {
		JSONArray array = null;
		List<DeliveryTransaction> transList = findCurrDTrans();
		DeliveryTransaction bqTransaction = null;
		for (DeliveryTransaction trans : transList) {
			if (trans.getTransaction_code().equals(code)) {
				bqTransaction = trans;
				break;
			}
		}

		if (bqTransaction == null) {
			return array;
		}

		List<PendingDelegationVO> list = spyMemcachedClient.get(uId + BTCQMemecacheKeys.D_WS_ORDER_UPDATE + code);
		if (list == null) {
			list = getDOrderPending(uId, code);
			if (list != null && list.size() > 0) {
				spyMemcachedClient.set(uId + BTCQMemecacheKeys.D_WS_ORDER_LIST + code, BTCQMemecacheKeys.TIMES.BPI, list);
			}
		}

		array = JSONArray.parseArray(JSON.toJSONString(list));
		return array;
	}
	
	public static void addDPendingToMemcached(DeliveryPendingDelegationCache cache, SpyMemcachedClient memClient) {
		if (cache == null) return;
		
		String code = cache.getTransaction_code();
		List<Long> subbedUsers = memClient.get(code + BTCQMemecacheKeys.D_WS_SUBBED_USERS);
		if (subbedUsers == null) {
			return;
		}
		Long uId = Long.valueOf(cache.getF_user_id());
		if (!subbedUsers.contains(uId)) {
			return;
		}

		String lk = memClient.lock(uId + BTCQMemecacheKeys.D_WS_ORDER_NEW_LIST_LOCK + code);
		if (!StringUtil.isEmpty(lk)) {
			try {
				List<PendingDelegationVO> newVoList = memClient.get(uId + BTCQMemecacheKeys.D_WS_ORDER_NEW_LIST + code);
				if (newVoList == null) {
					newVoList = new ArrayList<PendingDelegationVO>();
				}
				
				PendingDelegationVO newVo = new PendingDelegationVO();
				newVo.setDelegation_id(cache.getDelegation_id());
				newVo.setOrderId(cache.getPending_no() + "");
				newVo.setSymbol(cache.getTransaction_code());
				newVo.setAmount(cache.getDelegation_count().doubleValue());
				newVo.setDealAmount(cache.getDeal_count().doubleValue());
				newVo.setUndealAmount(cache.getUndeal_count().doubleValue());
				newVo.setPrice(cache.getDelegation_price().doubleValue());
				newVo.setCreateTime(cache.getDelegation_time());
				newVo.setMultiple(cache.getMultiple());
				newVo.setPriority(cache.getPriority());
				newVo.setDirection(cache.getDirection());
				newVo.setDeposit(cache.getDeposit().doubleValue());
				newVo.setState(Contents.PENDING_STATE_SUBMITTING);
				
				newVoList.add(newVo);
				
				memClient.set(uId + BTCQMemecacheKeys.D_WS_ORDER_NEW_LIST + code, BTCQMemecacheKeys.TIMES.BPI, newVoList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				memClient.unlock(uId + BTCQMemecacheKeys.D_WS_ORDER_NEW_LIST_LOCK + code, lk);
			}
		}
	}
	
	public static void addDPendingToMemcached(DeliveryPendingDelegationCache cache) {
		addDPendingToMemcached(cache, spyMemcachedClient);
	}
	
	public static void checkDPushNewOrderPending() {
		for (WebSocket item : pushWebSocketSet) {
			Long uId = item.userId;
			if (uId == null) {
				continue;
			}
			
			if (item.deliverySubPushList.size() == 0) {
				return;
			}
			
			for (String subData : item.deliverySubPushList) {
				String[] subs = subData.split("[.]");
				String code = subs[1];

				// order.$code.sub.pending.delivery
				String type = "order." + code + ".sub.pending.delivery";
				if (!item.deliverySubPushList.contains(type)) {
					continue;
				}
				List<PendingDelegationVO> newVoList = spyMemcachedClient.get(uId + BTCQMemecacheKeys.D_WS_ORDER_NEW_LIST + code);
				if (newVoList == null) {
					continue;
				}
				
				List<PendingDelegationVO> oldVoList = spyMemcachedClient.get(uId + BTCQMemecacheKeys.D_WS_ORDER_UPDATE + code);
				if (oldVoList == null) {
					oldVoList = new ArrayList<PendingDelegationVO>();
				}
				
				List<PendingDelegationVO> pushList = new ArrayList<PendingDelegationVO>();
				for (PendingDelegationVO newCache : newVoList) {
					boolean have = false;
					for (PendingDelegationVO oldCache : oldVoList) {
						if (newCache.getOrderId().equals(oldCache.getOrderId())) {
							have = true;
							break;
						}
					}
					if (!have) {
						oldVoList.add(newCache);
						pushList.add(newCache);
					}
				}
				if (oldVoList.size() > 0) {
					spyMemcachedClient.set(uId + BTCQMemecacheKeys.D_WS_ORDER_UPDATE + code, BTCQMemecacheKeys.TIMES.BPI, oldVoList);
				}
				
				if (pushList.size() > 0) {
					long time = System.currentTimeMillis() / 1000;
					JSONObject msgObj = new JSONObject();
					msgObj.put("ch", type);
					msgObj.put("ts", time);
					msgObj.put("data", JSONArray.toJSON(pushList));
					try {
						item.sendMessage(msgObj.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				String lk = spyMemcachedClient.lock(uId + BTCQMemecacheKeys.D_WS_ORDER_NEW_LIST_LOCK + code);
				if (!StringUtil.isEmpty(lk)) {
					try {
						spyMemcachedClient.delete(uId + BTCQMemecacheKeys.D_WS_ORDER_NEW_LIST + code);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						spyMemcachedClient.unlock(uId + BTCQMemecacheKeys.D_WS_ORDER_NEW_LIST_LOCK + code, lk);
					}
				}
			}
		}
	}

	public static void checkDPushOrderPending() {
		for (WebSocket item : pushWebSocketSet) {
			Long uId = item.userId;
			if (uId == null) {
				continue;
			}
			
			if (item.deliverySubPushList.size() == 0) {
				return;
			}
			
			for (String subData : item.deliverySubPushList) {
				String[] subs = subData.split("[.]");
				String code = subs[1];

				// order.$code.sub.pending.delivery
				String type = "order." + code + ".sub.pending.delivery";
				if (!item.deliverySubPushList.contains(type)) {
					continue;
				}

				List<PendingDelegationVO> oldVoList = spyMemcachedClient.get(uId + BTCQMemecacheKeys.D_WS_ORDER_UPDATE + code);
				List<PendingDelegationVO> newVoList = getDOrderPending(uId, code);
				do {
					spyMemcachedClient.set(uId + BTCQMemecacheKeys.D_WS_ORDER_UPDATE + code, BTCQMemecacheKeys.TIMES.BPI,
							newVoList);
					if (oldVoList == null) {
						oldVoList = newVoList;
					} else {
						List<PendingDelegationVO> pushPendingList = getDPushPendingList(oldVoList, newVoList, uId);
						if (pushPendingList != null && pushPendingList.size() > 0) {
							oldVoList = pushPendingList;
							
							long time = System.currentTimeMillis() / 1000;
							JSONObject msgObj = new JSONObject();
							msgObj.put("ch", type);
							msgObj.put("ts", time);
							msgObj.put("data", JSONArray.toJSON(oldVoList));
							try {
								item.sendMessage(msgObj.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				} while (false);
			}
		}
	}

	private static List<PendingDelegationVO> getDPushPendingList(List<PendingDelegationVO> oldVoList,
			List<PendingDelegationVO> newVoList, Long uid) {
		List<PendingDelegationVO> pushList = new ArrayList<PendingDelegationVO>();

		List<PendingDelegationVO> newOrders = new ArrayList<PendingDelegationVO>();// 新增挂单(增)
		List<PendingDelegationVO> alreadyOrCancle = new ArrayList<PendingDelegationVO>();// 已成交或撤销的挂单(删)
		List<PendingDelegationVO> changeOrders = new ArrayList<PendingDelegationVO>();// 相同挂单号已改变(改)
		
		for (PendingDelegationVO oldVo : oldVoList) {
			boolean change = false;
			boolean same = false;
			for (PendingDelegationVO newVo : newVoList) {
				if (oldVo.getOrderId().equals(newVo.getOrderId())) {
					if ((!oldVo.getState().equals(newVo.getState()) || oldVo.getUndealAmount() != newVo.getUndealAmount())) {
						changeOrders.add(newVo);
						change = true;
						break;
					}
					newVoList.remove(newVo);
					same = true;
					break;
				}
			}

			if (!change && !same) {
				alreadyOrCancle.add(oldVo);
			}
		}

		newVoList.removeAll(changeOrders);
		newOrders = newVoList;
		
		List<PendingDelegationVO> alreadyOrCancleVos = new ArrayList<PendingDelegationVO>();
		if (alreadyOrCancle.size() > 0) {
			for (PendingDelegationVO vo : alreadyOrCancle) {
				PendingDelegationVO data = new PendingDelegationVO();
				List<DeliveryPendingLog> pendingLogs = dPendingLogService.findByPendingNo(vo.getSymbol(), vo.getOrderId(), uid);
				if (pendingLogs.size() > 0) {
					DeliveryPendingLog firstLog = pendingLogs.get(0);

					data.setSymbol(firstLog.getTransaction_code());
					data.setOrderId(firstLog.getPending_no() + "");
					data.setAmount(firstLog.getDelegation_count().doubleValue());
					data.setDealAmount(firstLog.getDeal_count().doubleValue());
					data.setUndealAmount(firstLog.getUndeal_count().doubleValue());
					data.setPrice(firstLog.getDelegation_price().doubleValue());
					data.setCreateTime(firstLog.getDelegation_time());
					data.setMultiple(firstLog.getMultiple());
					data.setDeposit(firstLog.getDeposit().doubleValue());

					if (firstLog.getTrade_type_id() == 1) {
						data.setPriority(Contents.PRIORITY_KC);
						data.setDirection(Contents.DIRECTION_BUY);
					} else if (firstLog.getTrade_type_id() == 2) {
						data.setPriority(Contents.PRIORITY_PC);
						data.setDirection(Contents.DIRECTION_SELL);
					} else if (firstLog.getTrade_type_id() == 3) {
						data.setPriority(Contents.PRIORITY_KC);
						data.setDirection(Contents.DIRECTION_SELL);
					} else if (firstLog.getTrade_type_id() == 4) {
						data.setPriority(Contents.PRIORITY_PC);
						data.setDirection(Contents.DIRECTION_BUY);
					}

					int action = 0;
					for (DeliveryPendingLog log : pendingLogs) {
						if (log.getAction() != 0) {
							action = log.getAction();
							data.setDealAmount(log.getDeal_count().doubleValue());
							data.setUndealAmount(log.getUndeal_count().doubleValue());
							data.setDeposit(log.getDeposit().doubleValue());
							break;
						}
					}

					if (action == 1) {
						data.setState(Contents.PENDING_STATE_FILLED);
					} else if (action == 4) {
						data.setState(Contents.PENDING_STATE_CANCELED);
					} else {
						data.setState(Contents.PENDING_STATE_SUBMITTING);
					}
				}

				alreadyOrCancleVos.add(data);
			}
		}

		pushList.addAll(alreadyOrCancleVos);
		pushList.addAll(newOrders);
		pushList.addAll(changeOrders);

		return pushList;
	}

	private static List<PendingDelegationVO> getDOrderPending(Long uId, String code) {
		List<PendingDelegationVO> newVoList = new ArrayList<PendingDelegationVO>();

		List<DeliveryPendingDelegation> pdList = dPendingDelegationService.findPendingByUserIdAndTime(code, uId, 0, 0, 0, "", 100);
		List<DeliveryPendingDelegationCache> pdCacheList = dPendingDelegationCacheService.findPendingByUserIdAndTime(code, uId, 0, 0, 0, "", 100);
		if (pdCacheList.size() > 0) {
			for (DeliveryPendingDelegationCache cache : pdCacheList) {
				PendingDelegationVO data = new PendingDelegationVO();
				data.setDelegation_id(cache.getDelegation_id());
				data.setOrderId(cache.getPending_no() + "");
				data.setSymbol(cache.getTransaction_code());
				data.setAmount(cache.getDelegation_count().doubleValue());
				data.setDealAmount(cache.getDeal_count().doubleValue());
				data.setUndealAmount(cache.getUndeal_count().doubleValue());
				data.setPrice(cache.getDelegation_price().doubleValue());
				data.setCreateTime(cache.getDelegation_time());
				data.setMultiple(cache.getMultiple());
				data.setPriority(cache.getPriority());
				data.setDirection(cache.getDirection());
				data.setDeposit(cache.getDeposit().doubleValue());
				data.setState(Contents.PENDING_STATE_SUBMITTING);

				newVoList.add(data);
			}
		}

		if (pdList.size() > 0) {
			for (DeliveryPendingDelegation pd : pdList) {
				PendingDelegationVO data = new PendingDelegationVO();
				data.setDelegation_id(pd.getDelegation_id());
				data.setOrderId(pd.getPending_no() + "");
				data.setSymbol(pd.getTransaction_code());
				data.setAmount(pd.getDelegation_count().doubleValue());
				data.setDealAmount(pd.getDeal_count().doubleValue());
				data.setUndealAmount(pd.getUndeal_count().doubleValue());
				data.setPrice(pd.getDelegation_price().doubleValue());
				data.setCreateTime(pd.getDelegation_time());
				data.setMultiple(pd.getMultiple());
				data.setPriority(pd.getPriority());
				data.setDirection(pd.getDirection());
				data.setDeposit(pd.getDeposit().doubleValue());
				data.setState(Contents.PENDING_STATE_SUBMITTED);

				newVoList.add(data);
			}
		}

		return newVoList;
	}
}