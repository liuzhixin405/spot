package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.service.NPendingDelegationService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.Top5DelegationVO;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 合约 API（市场深度）
 * 
 * @author Administrator
 * 
 */
public class NTimerTaskDepthJson {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private NTransactionService transactionService;
	@Autowired
	private NPendingDelegationService pendingDelegationService;
	
	private static String[] contractArray = null;

	public void run() {
		
		if (contractArray == null) {
			String contracts = UrlConfig.getValue("openPosition.ncontracts");
			if (!StringUtil.isEmpty(contracts)) {
				contractArray = contracts.split(",");
			}
		}
		
		if (contractArray == null || contractArray.length == 0) return;
		
		
		for (String pContract : contractArray) {
			String[] strs = pContract.split("_");
			if (strs.length != 2) continue;
			String partition = strs[0];
			String contract = strs[1];
			
			if (StringUtil.isEmpty(partition) || StringUtil.isEmpty(contract)) {
				continue;
			}
			
			NTransaction bq = transactionService.findTransByCode(partition, contract);
			if (bq == null) continue;
			
			String bqCode = bq.getTransaction_code(); // 获取合约号
			
			List<Top5DelegationVO> buytop5List = pendingDelegationService.getBuyTop5(partition, bqCode);
			List<Top5DelegationVO> selltop5List = pendingDelegationService.getSellTop5(partition, bqCode);

			spyMemcachedClient.set(partition + "_" + bqCode + BTCQMemecacheKeys.BUYTOP5LIST, BTCQMemecacheKeys.TIMES.BUYTOP5LIST, buytop5List);
			spyMemcachedClient.set(partition + "_" + bqCode + BTCQMemecacheKeys.SELLTOP5LIST, BTCQMemecacheKeys.TIMES.SELLTOP5LIST, selltop5List);
			
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
			spyMemcachedClient.set(BTCQMemecacheKeys.DEPTH_CODE + partition + "_" + bqCode.toLowerCase(), BTCQMemecacheKeys.TIMES.DEPTH_CODE, depthData);
		}

	}
}
