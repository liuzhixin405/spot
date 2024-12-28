package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.service.BQPriceIndexCacheService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.NContractPartitionService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.TickerVo;
import com.lmax.api.Callback;
import com.lmax.api.FailureResponse;
import com.lmax.api.LmaxApi;
import com.lmax.api.Session;
import com.lmax.api.account.LoginCallback;
import com.lmax.api.account.LoginRequest;
import com.lmax.api.orderbook.OrderBookEvent;
import com.lmax.api.orderbook.OrderBookEventListener;
import com.lmax.api.orderbook.OrderBookSubscriptionRequest;

public class TimerTaskLmaxBPI implements LoginCallback, OrderBookEventListener {
	
	@Autowired
	private BQPriceIndexCacheService bqPriceIndexCacheService;
	@Autowired
	private NContractPartitionService nContractPartitionService;
	@Autowired
	private NTransactionTypeService nTransTypeService;
	@Autowired
	private BQTransactionTypeService bqTransTypeService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	public static Map<Long, String> symbols = new HashMap<>();
	
	private Session session = null;
	
	private boolean isRunning = false;

    @Override
    public void onLoginSuccess(final Session session)
    {
    	isRunning = true;
    	this.session = session;
        session.registerOrderBookEventListener(this);
        
        for (Long key : symbols.keySet()) {
        	subscribeToInstrument(session, key);
        }
        
        session.start();
    }

    private void subscribeToInstrument(final Session session, final long instrumentId)
    {
        session.subscribe(new OrderBookSubscriptionRequest(instrumentId), new Callback()
        {
            public void onSuccess()
            {
            	isRunning = true;
                System.out.printf("Subscribed to instrument %d.%n", instrumentId);
            }

            public void onFailure(final FailureResponse failureResponse)
            {
                System.err.printf("Failed to subscribe to instrument %d: %s%n", instrumentId, failureResponse);
            }
        });
    }

    @Override
    @SuppressWarnings({})
    public void onLoginFailure(final FailureResponse failureResponse)
    {
        throw new RuntimeException("Unable to login: " + failureResponse.getDescription(), failureResponse.getException());
    }

    @Override
    public void notify(final OrderBookEvent orderBookEvent)
    {
    	isRunning = true;
    	
//    	System.out.println(orderBookEvent.getLastTradedPrice());
//
//		double bidPrice = Double.valueOf(orderBookEvent.getBidPrices().get(0).getPrice().toString());
//		double askPrice = Double.valueOf(orderBookEvent.getAskPrices().get(0).getPrice().toString());
//		double lastPrice = (bidPrice + askPrice) / 2;
//		System.out.println(lastPrice);
		
    	long id = orderBookEvent.getInstrumentId();
    	String code = symbols.get(id);
    	if (StringUtil.isEmpty(code)) {
    		return;
    	}
    	
		do {
			List<NContractPartition> partitions = nContractPartitionService.findAll();
			for (NContractPartition partition : partitions) {
				List<NTransactionType> nTransTypes = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES + "_" + partition.getPart());
				if (nTransTypes == null) {
					nTransTypes = nTransTypeService.findAllUsing(partition.getPart());
				}
				
				if (nTransTypes == null || nTransTypes.size() == 0) break;
				NTransactionType currType = null;
				for (NTransactionType transType : nTransTypes) {
					if (code.equalsIgnoreCase(transType.getType_code())) {
						currType = transType;
						break;
					}
				}
				
				if (currType == null) break;
				
				Integer digits = spyMemcachedClient.get(partition.getPart() + "_" + currType.getType_code() + BTCQMemecacheKeys.DIGITS);
				if (digits == null) {
					digits = Utils.getDigitsFromMinFloat(currType.getMin_float_price());
					spyMemcachedClient.set(partition.getPart() + "_" + currType.getType_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
				}
				
				doGetIndex(currType.getType_id(), currType.getType_code(), digits, orderBookEvent);
				return;
			}
		} while (false);
		
		do {
			List<BQTransactionType> transTypes = spyMemcachedClient.get(BTCQMemecacheKeys.TRANS_TYPES);
			if (transTypes == null) {
				transTypes = bqTransTypeService.findAllUsing();
			}
			
			if (transTypes == null || transTypes.size() == 0) break;
			BQTransactionType currType = null;
			for (BQTransactionType transType : transTypes) {
				if (code.equalsIgnoreCase(transType.getType_code())) {
					currType = transType;
					break;
				}
			}
			
			if (currType == null) break;
			Integer digits = spyMemcachedClient.get(currType.getType_code() + BTCQMemecacheKeys.DIGITS);
			if (digits == null) {
				digits = Utils.getDigitsFromMinFloat(currType.getMin_float_price());
				spyMemcachedClient.set(currType.getType_code() + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
			}
			
			doGetIndex(currType.getType_id(), currType.getType_code(), digits, orderBookEvent);
		} while (false);
    }
		
    private void doGetIndex(int typeId, String typeCode, int digits, OrderBookEvent orderBookEvent) {
		List<TickerVo> tickers = new ArrayList<TickerVo>();
		BigDecimal last = BigDecimal.ZERO;
		BigDecimal preClose = BigDecimal.ZERO;
		
		String code = typeCode.toLowerCase();
		TickerVo ticker = new TickerVo();
		ticker.setHigh(new BigDecimal(orderBookEvent.getDailyHighestTradedPrice() == null ? "0" : orderBookEvent.getDailyHighestTradedPrice().toString()));
		ticker.setLow(new BigDecimal(orderBookEvent.getDailyLowestTradedPrice() == null ? "0" : orderBookEvent.getDailyLowestTradedPrice().toString()));
		
		if (orderBookEvent.getBidPrices().size() <= 0 || orderBookEvent.getAskPrices().size() <= 0) {
			return;
		}

		if (orderBookEvent.getBidPrices().size() > 0) {
			ticker.setBuy(new BigDecimal(orderBookEvent.getValuationBidPrice().toString()));
		}
		
		if (orderBookEvent.getAskPrices().size() > 0) {
			ticker.setSell(new BigDecimal(orderBookEvent.getValuationAskPrice().toString()));
		}
		
		double bidPrice = Double.valueOf(orderBookEvent.getBidPrices().get(0).getPrice().toString());
		double askPrice = Double.valueOf(orderBookEvent.getAskPrices().get(0).getPrice().toString());
		double lastPrice = (bidPrice + askPrice) / 2;
		
		ticker.setOpen(new BigDecimal(orderBookEvent.getMarketClosePrice().toString()));
		ticker.setLast(new BigDecimal(lastPrice));
		ticker.setLastUsd(ticker.getLast());
		ticker.setVol(BigDecimal.ZERO);
		ticker.setTurnover(BigDecimal.ZERO);
		ticker.setCap(BigDecimal.ZERO);
		ticker.setRatio(BigDecimal.ONE);
		ticker.setTime(orderBookEvent.getTimeStamp() / 1000);
		ticker.setTrans_pair("");
		ticker.setMarket("LONDON-BANK");
		ticker.setTypeId(typeId);
		ticker.setTypeCode(typeCode);
		
		BigDecimal fudu = ticker.getLast().subtract(ticker.getOpen()).multiply(new BigDecimal(100)).divide(ticker.getOpen(), 2, BigDecimal.ROUND_HALF_UP);
		ticker.setFudu(fudu.toString() + "%");
		
		
		JSONObject obj = new JSONObject();
		obj.put("close", ticker.getLast());
		obj.put("high", ticker.getHigh());
		obj.put("low", ticker.getLow());
		obj.put("open", ticker.getOpen());
		obj.put("lastClose", ticker.getLastUsd());
		obj.put("time", ticker.getUpdateTime());
		obj.put("date", ticker.getDate());
		spyMemcachedClient.set(BTCQMemecacheKeys.CFD_INDEX + code, BTCQMemecacheKeys.TIMES.TICKER_ONEDAY, obj.toJSONString());
		
		spyMemcachedClient.set(BTCQMemecacheKeys.INDEX_FUDU + typeCode, BTCQMemecacheKeys.TIMES.ONE_DAY, ticker.getFudu());

		if (ticker != null && ticker.getLast().compareTo(BigDecimal.ZERO) > 0) {
			last = ticker.getLast();
			preClose = ticker.getLastUsd();
			ticker.setTypeId(typeId);
			ticker.setTypeCode(typeCode);
			tickers.add(ticker);
		}
		
		last = last.setScale(digits, BigDecimal.ROUND_HALF_UP);
		preClose = preClose.setScale(digits, BigDecimal.ROUND_HALF_UP);
		
		if (preClose.compareTo(BigDecimal.ZERO) > 0) {
			// 昨日收盘价
			spyMemcachedClient.set(BTCQMemecacheKeys.PRECLOSE + typeCode, BTCQMemecacheKeys.TIMES.ONE_DAY, preClose);
		}
		
		for (TickerVo vo : tickers) {
			spyMemcachedClient.set(vo.getTypeCode() + vo.getMarket(), BTCQMemecacheKeys.TIMES.BPI, last);
			bqPriceIndexCacheService.updateBtcCache(vo, last);
		}
		
		spyMemcachedClient.set(typeCode, BTCQMemecacheKeys.TIMES.BPI, last);
		spyMemcachedClient.set(BTCQMemecacheKeys.TICKER + typeCode, BTCQMemecacheKeys.TIMES.BPI, tickers);
	}

	public void run() {
		
		if (symbols.size() == 0) {
			String ids = UrlConfig.getValue("lmax.ids");
			if (!StringUtil.isEmpty(ids)) {
				String[] idPairs = ids.split(",");
				for (String idPair : idPairs) {
					String[] keyVal = idPair.split("_");
					if (keyVal.length == 2) {
						symbols.put(Long.valueOf(keyVal[0]), keyVal[1]);
					}
				}
			}
		}
		
		if (isRunning) {
			isRunning = false;
			return;
		}
		
		if (this.session != null) {
			final Session session = this.session;
			this.session = null;
			
			Thread stopThread = new Thread(new Runnable() {
				
		        public void run() {
		        	session.logout(new Callback() {
						@Override
						public void onFailure(FailureResponse arg0) {
							session.stop();
						}

						@Override
						public void onSuccess() {
							session.stop();
						}
						
					});
					
		    	}
		    });
			
			stopThread.start();
		}
		
		Thread thread = new Thread(new Runnable() {
			
	        public void run() {
	    	   	try {
	    	   		String url = "https://api.lmaxtrader.com";
	    	        String username = "yxt5188";
	    	        String password = "wmc625790";
	    	        LoginRequest.ProductType productType = LoginRequest.ProductType.valueOf("CFD_LIVE");

	    	        LmaxApi lmaxApi = new LmaxApi(url);
	    	        lmaxApi.login(new LoginRequest(username, password, productType), TimerTaskLmaxBPI.this);
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	}
	    	}
	    });
		
		thread.start();
	}
}
