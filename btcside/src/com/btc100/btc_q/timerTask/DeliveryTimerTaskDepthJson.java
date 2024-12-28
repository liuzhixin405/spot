package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.DeliveryPendingDelegationService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.Top5DelegationVO;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 合约 API（市场深度）
 * 
 * @author Administrator
 * 
 */
public class DeliveryTimerTaskDepthJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private DeliveryPendingDelegationService pendingDelegationService;
	
	public static String[] contractArray = null;

	public void run() {

		if (contractArray == null) {
			String contracts = UrlConfig.getValue("deliveryTrade.contracts");
			contractArray = contracts.split(",");
		}

		if (contractArray == null || contractArray.length == 0)
			return;
		
		List<DeliveryTransaction> lstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (lstTrads == null) {
			lstTrads = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, lstTrads);
		}
		if (lstTrads == null || lstTrads.size() == 0) return;
		
		for (String contract : contractArray) {
			String[] codeDelivery = contract.split("_");
			if (codeDelivery.length == 0) {
				continue;
			}
			String typeCode = codeDelivery[0];
			String deliveryType = codeDelivery[1];
			
			DeliveryTransaction transaction = null;
			for (DeliveryTransaction bqTransaction : lstTrads) {
				if (bqTransaction.getTransaction_type_code().equals(typeCode) && bqTransaction.getDelivery_type().equals(deliveryType)) {
					transaction = bqTransaction;
					break;
				}
			}

			if (transaction == null) {
				continue;
			}
			
			if (transaction.getTransaction_state() != 1) {
				continue;
			}
			
			String bqCode = transaction.getTransaction_code(); // 获取合约号
			
			List<Top5DelegationVO> buytop5List = pendingDelegationService.getBuyTop5(bqCode);
			List<Top5DelegationVO> selltop5List = pendingDelegationService.getSellTop5(bqCode);

			spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.D_BUYTOP5LIST, BTCQMemecacheKeys.TIMES.BUYTOP5LIST, buytop5List);
			spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.D_SELLTOP5LIST, BTCQMemecacheKeys.TIMES.SELLTOP5LIST, selltop5List);
			
			List<Object[]> lstBuyObjs = new ArrayList<Object[]>();
			for (Top5DelegationVO vo : buytop5List) {
				Object[] objs = new Object[3];
				objs[0] = vo.getPrice();
				objs[1] = vo.getCountNum();
				objs[2] = vo.getPriority();
				
				lstBuyObjs.add(objs);
			}
			
			List<Object[]> lstSellObjs = new ArrayList<Object[]>();
			for (Top5DelegationVO vo : selltop5List) {
				Object[] objs = new Object[3];
				objs[0] = vo.getPrice();
				objs[1] = vo.getCountNum();
				objs[2] = vo.getPriority();
				
				lstSellObjs.add(objs);
			}
			
			String sell = "asks", buy = "bids";
			JSONObject json = new JSONObject();

			try {
				List<JSONArray> lstBuyArray2 = new ArrayList<JSONArray>();
				for (Object[] obj : lstBuyObjs) {
					JSONArray array = new JSONArray();
					BigDecimal price = new BigDecimal(obj[0].toString());
					BigDecimal amount = new BigDecimal(obj[1].toString());

					array.put(price);
					array.put(amount);
					array.put(ConvertUtil.toInt(obj[2], 0));
					lstBuyArray2.add(array);
				}

				Collections.reverse(lstSellObjs);
				List<JSONArray> lstSellArray2 = new ArrayList<JSONArray>();
				for (Object[] obj : lstSellObjs) {
					JSONArray array = new JSONArray();
					BigDecimal price = new BigDecimal(obj[0].toString());
					BigDecimal amount = new BigDecimal(obj[1].toString());

					array.put(price);
					array.put(amount);
					array.put(ConvertUtil.toInt(obj[2], 0));
					lstSellArray2.add(array);
				}
				json.put(buy, lstBuyArray2);
				json.put(sell, lstSellArray2);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			String depthData = json.toString();
			spyMemcachedClient.set(BTCQMemecacheKeys.D_DEPTH_CODE + bqCode.toLowerCase(), BTCQMemecacheKeys.TIMES.DEPTH_CODE, depthData);
		}

	}
}
