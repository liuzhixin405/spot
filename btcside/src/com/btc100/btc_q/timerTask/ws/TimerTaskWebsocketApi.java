package com.btc100.btc_q.timerTask.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.action.websocket.WebSocket;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NTransaction;
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
import com.btc100.btc_q.service.NContractPartitionService;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.PendingLogService;
import com.btc100.btc_q.service.TradeLogService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.vo.Top5DelegationVO;

/**
 * WebSocket api 数据
 * 
 * @author Administrator
 * 
 */
public class TimerTaskWebsocketApi {

	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VTransactionTypeService vTransactionTypeService;
	@Autowired
	private BQTransactionTypeService bqTransTypeService;
	@Autowired
	private BQNotradetimeService bqNotradetimeService;
	@Autowired
	private KLineTradeService kLineTradeService;
	@Autowired
	private BQUserAppKeyService bqUserAppKeyService;
	@Autowired
	private TradeLogService tradeLogService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private PendingLogService pendingLogService;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private DeliveryTransactionService dTransactionService;
	@Autowired
	private DeliveryTransactionTypeService dTransactionTypeService;
	@Autowired
	private DeliveryTradeLogService dTradeLogService;
	@Autowired
	private DeliveryPendingDelegationCacheService dPendingDelegationCacheService;
	@Autowired
	private DeliveryPendingDelegationService dPendingDelegationService;
	@Autowired
	private DeliveryPendingLogService dPendingLogService;
	@Autowired
	protected NContractPartitionService nContractPartitionService;
	@Autowired
	private NTransactionService nTransactionService;
	@Autowired
    private NTransactionTypeService nTransactionTypeService;
	
	private static int COUNT_ALIVE_DOWN_TIMES = 5;
	
	private static int COUNT_DATA_DOWN_TIMES = 3;
	
	private int countAliveDown = COUNT_ALIVE_DOWN_TIMES;
	
	private int countDataDown = COUNT_DATA_DOWN_TIMES;
	
	public void run() {

		WebSocket.setMemClient(spyMemcachedClient, bqNotradetimeService, transactionService, bqTransTypeService,
				vTransactionService, vTransactionTypeService, kLineTradeService, bqUserAppKeyService, tradeLogService,
				pendingDelegationCacheService, pendingDelegationService, pendingLogService, bqSpotMarketCoinPairService,
				dTransactionService, dTransactionTypeService, dTradeLogService, dPendingDelegationCacheService,
				dPendingDelegationService, dPendingLogService);
		WebSocket.nTransactionService = nTransactionService;
		WebSocket.nTransactionTypeService = nTransactionTypeService;

		List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transactionList == null) {
			transactionList = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
		}
		
		for (BQTransaction bqTransaction : transactionList) {
			String bqCode = bqTransaction.getTransaction_code();

			List<Top5DelegationVO> buytopList = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.BUYTOP5LIST);
			List<Top5DelegationVO> selltopList = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.SELLTOP5LIST);
			
			if (buytopList == null || selltopList == null) continue;
			
			// market.$code.depth.step0
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
			WebSocket.notifyMessage("market." + bqCode + ".depth.step0", dataJson);
			
			// trade.$contractType.$code
			JSONObject dataObj = new JSONObject();
			dataObj.put("commondata", getCommonData("contract", bqCode));
			WebSocket.notifyMessage("trade.contract." + bqCode, dataObj);
		}
		
		List<DeliveryTransaction> dTransList = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (dTransList == null) {
			dTransList = dTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, dTransList);
		}
		
		for (DeliveryTransaction bqTransaction : dTransList) {
			String bqCode = bqTransaction.getTransaction_code();

			List<Top5DelegationVO> buytopList = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.D_BUYTOP5LIST);
			List<Top5DelegationVO> selltopList = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.D_SELLTOP5LIST);
			
			if (buytopList == null || selltopList == null) continue;
			
			// market.$code.depth.step0
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
			WebSocket.notifyMessage("market." + bqCode + ".depth.step0", dataJson);
			
			// trade.$contractType.$code
			JSONObject dataObj = new JSONObject();
			dataObj.put("commondata", getCommonData("delivery", bqCode));
			WebSocket.notifyMessage("trade.delivery." + bqCode, dataObj);
		}
		
		/*
		List<VTransaction> vTransList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (vTransList == null) {
			vTransList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, vTransList);
		}
		
		for (VTransaction vTransaction : vTransList) {
			String bqCode = vTransaction.getTransaction_code();
			JSONObject dataObj = new JSONObject();
			dataObj.put("commondata", getCommonData("vcontract", bqCode));
			WebSocket.notifyMessage("trade.vcontract." + bqCode, dataObj);
		}
		*/
		
		
		List<NContractPartition> partitions = nContractPartitionService.findAll();
		Map<String, List<NTransaction>> partitionMap = new HashMap<>();
		for (NContractPartition partition : partitions) {
			String type = partition.getPart();
			
			List<NTransaction> nTransactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + type);
			if (nTransactionList == null) {
				nTransactionList = nTransactionService.findTransByState_new(type, Contents.TRANSACTION_STATE_ZC);
				spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST + "_" + type, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, nTransactionList);
			}
			
			partitionMap.put(type, nTransactionList);
			
			for (NTransaction bqTransaction : nTransactionList) {
				String bqCode = bqTransaction.getTransaction_code();

				List<Top5DelegationVO> buytopList = spyMemcachedClient.get(type + "_" + bqCode + BTCQMemecacheKeys.BUYTOP5LIST);
				List<Top5DelegationVO> selltopList = spyMemcachedClient.get(type + "_" + bqCode + BTCQMemecacheKeys.SELLTOP5LIST);
				
				if (buytopList == null || selltopList == null) continue;
				
				// market.$code.depth.step0
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
				WebSocket.notifyMessage("market." + type + "." + bqCode + ".depth.step0", dataJson);
				
				// trade.$contractType.$code
				JSONObject dataObj = new JSONObject();
				dataObj.put("commondata", getCommonData(type, bqCode));
				WebSocket.notifyMessage("trade."+ type + "." + bqCode, dataObj);
			}
			
		}
		
		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.findByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}
		
		for (BQSpotMarketCoinPair bqTransaction : coinPairs) {
			String bqCode = bqTransaction.getCoin_pair_name();
			
			// trade.$contractType.$code
			JSONObject dataObj = new JSONObject();
			dataObj.put("commondata", getCommonData("spot", bqCode));
			WebSocket.notifyMessage("trade.spot." + bqCode, dataObj);

			List<Top5DelegationVO> buytopList = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.BUYTOP5LIST);
			List<Top5DelegationVO> selltopList = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.SELLTOP5LIST);
			
			if (buytopList == null || selltopList == null) continue;
			
			// market.$code.depth.step0
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
			WebSocket.notifyMessage("market." + bqCode + ".depth.step0", dataJson);
		}
		
		countAliveDown--;
		if (countAliveDown <= 0) {
			countAliveDown = COUNT_ALIVE_DOWN_TIMES;
			WebSocket.checkAlive();
		}
		
		countDataDown--;
		if (countDataDown <= 0) {
			countDataDown = COUNT_DATA_DOWN_TIMES;

			for (BQTransaction bqTransaction : transactionList) {
				String bqCode = bqTransaction.getTransaction_code();
				JSONObject dataObj = new JSONObject();
				dataObj.put("tradesdata", trades("contract", bqCode));
				dataObj.put("tickersdata", tickersData("contract"));
				dataObj.put("bpidata", getBPIData());
				dataObj.put("symbolsdata", getSymbols("contract"));
				dataObj.put("depthdata", getDepth("contract", bqCode));
				WebSocket.notifyMessage("trade.contract." + bqCode, dataObj);
			}
			
			for (String type : partitionMap.keySet()) {
				List<NTransaction> nTransactionList = partitionMap.get(type);
				for (NTransaction bqTransaction : nTransactionList) {
					String bqCode = bqTransaction.getTransaction_code();
					JSONObject dataObj = new JSONObject();
					dataObj.put("tradesdata", trades(type, bqCode));
					dataObj.put("tickersdata", tickersData(type));
					dataObj.put("bpidata", getBPIData());
					dataObj.put("symbolsdata", getSymbols(type));
					dataObj.put("depthdata", getDepth(type, bqCode));
					WebSocket.notifyMessage("trade." + type + "." + bqCode, dataObj);
				}
			}

//			for (VTransaction vTransaction : vTransList) {
//				String bqCode = vTransaction.getTransaction_code();
//				JSONObject dataObj = new JSONObject();
//				dataObj.put("tradesdata", trades("vcontract", bqCode));
//				dataObj.put("tickersdata", tickersData("vcontract"));
//				dataObj.put("bpidata", getBPIData());
//				dataObj.put("symbolsdata", getSymbols("vcontract"));
//				dataObj.put("depthdata", getDepth("vcontract", bqCode));
//				WebSocket.notifyMessage("trade.vcontract." + bqCode, dataObj);
//			}
			
			for (DeliveryTransaction dTransaction : dTransList) {
				String bqCode = dTransaction.getTransaction_code();
				JSONObject dataObj = new JSONObject();
				dataObj.put("tradesdata", trades("delivery", bqCode));
				dataObj.put("tickersdata", tickersData("delivery"));
				dataObj.put("bpidata", getBPIData());
				dataObj.put("symbolsdata", getSymbols("delivery"));
				dataObj.put("depthdata", getDepth("delivery", bqCode));
				WebSocket.notifyMessage("trade.delivery." + bqCode, dataObj);
			}
			
			for (BQSpotMarketCoinPair coinPair : coinPairs) {
				String code = coinPair.getCoin_pair_name();
				JSONObject dataObj = new JSONObject();
				dataObj.put("tickersdata", tickersData("spot"));
				dataObj.put("tradesdata", trades("spot", code));
				dataObj.put("symbolsdata", getSymbols("spot"));
				dataObj.put("depthdata", getDepth("spot", code));
				WebSocket.notifyMessage("trade.spot." + code, dataObj);
			}
		}
	}
	
	private JSONObject trades(String type, String code) {
		return WebSocket.trades(type, code);
	}
	
	private JSONObject tickersData(String type) {
		return WebSocket.tickersData(type);
	}
	
	private JSONObject getBPIData() {
		return WebSocket.getBPIData();
	}
	
	private JSONObject getCommonData(String type, String code) {
		return WebSocket.getCommonData(type, code);
	}
	
	private JSONObject getSymbols(String type) {
		return WebSocket.getSymbols(type);
	}
	
	private JSONObject getDepth(String type, String code) {
		return WebSocket.getDepth(type, code);
	}
}
