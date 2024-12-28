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
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.Top5DelegationVO;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 合约 API（市场深度）
 * 
 * @author Administrator
 * 
 */
public class VTimerTaskDepthJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	private VTransactionService vTransactionService;
	
	public void run() {
		
		List<VTransaction> vTransactionList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (vTransactionList == null) {
			vTransactionList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
			spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, vTransactionList);
		}
		
		for (VTransaction bq : vTransactionList) {
			String bqCode = bq.getTransaction_code(); // 获取合约号
			
			List<Top5DelegationVO> buytop5List = vPendingDelegationService.getBuyTop5(bqCode);
			List<Top5DelegationVO> selltop5List = vPendingDelegationService.getSellTop5(bqCode);

			spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.V_BUYTOP5LIST, BTCQMemecacheKeys.TIMES.BUYTOP5LIST, buytop5List);
			spyMemcachedClient.set(bqCode + BTCQMemecacheKeys.V_SELLTOP5LIST, BTCQMemecacheKeys.TIMES.SELLTOP5LIST, selltop5List);
			
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
			spyMemcachedClient.set(BTCQMemecacheKeys.V_DEPTH_CODE + bqCode.toLowerCase(), BTCQMemecacheKeys.TIMES.DEPTH_CODE, depthData);
		}
	}
}
