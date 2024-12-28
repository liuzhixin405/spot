package com.btc100.btc_q.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

import com.okcoin.commons.okex.open.api.bean.spot.param.PlaceOrderParam;
import com.okcoin.commons.okex.open.api.bean.spot.result.Account;
import com.okcoin.commons.okex.open.api.bean.spot.result.OrderResult;
import com.okcoin.commons.okex.open.api.config.APIConfiguration;
import com.okcoin.commons.okex.open.api.exception.APIException;
import com.okcoin.commons.okex.open.api.service.spot.SpotAccountAPIService;
import com.okcoin.commons.okex.open.api.service.spot.SpotOrderAPIServive;
import com.okcoin.commons.okex.open.api.service.spot.impl.SpotAccountAPIServiceImpl;
import com.okcoin.commons.okex.open.api.service.spot.impl.SpotOrderApiServiceImpl;

public class OKexApiUtil {

	private static final String HUOBI_API_BASE_URL = "https://www.okex.com";
	
	private String secretKey = "";
	private String accessKey = "";
	private String password = "";
	private SpotAccountAPIService spotAccountAPIService = null;
	private SpotOrderAPIServive spotOrderAPIServive = null;
	
	public OKexApiUtil(String secretKey, String accessKey, String password) {
		this.secretKey = secretKey;
		this.accessKey = accessKey;
		this.password = password;
		
		if (!StringUtil.isEmpty(secretKey) && !StringUtil.isEmpty(accessKey) && !StringUtil.isEmpty(password)) {
			this.spotOrderAPIServive = new SpotOrderApiServiceImpl(this.okexApiConfig());
			this.spotAccountAPIService = new SpotAccountAPIServiceImpl(this.okexApiConfig());
		}
	}
	
	public Map<String, BigDecimal> getAccounts() {
		Map<String, BigDecimal> balanceMap = new HashMap<String, BigDecimal>();
		if (spotAccountAPIService == null) {
			return balanceMap;
		}
		
		List<Account> accounts = spotAccountAPIService.getAccounts();
		for (Account account : accounts) {
			balanceMap.put(account.getCurrency().toLowerCase(), new BigDecimal(account.getAvailable()));
		}
		
		return balanceMap;
	}
	
	public JSONObject orderUsdt(String symbol, String partition, int direction, BigDecimal amount, BigDecimal price) throws Exception {
		return order(symbol, partition, direction, amount, price);
	}
	
	public JSONObject order(String symbol, String marketSymbol, int direction, BigDecimal amount, BigDecimal price) throws Exception {
		JSONObject retObj = new JSONObject();
		retObj.put("status", "retry");
		retObj.put("error", "");
		
		if (spotOrderAPIServive == null) {
			return retObj;
		}
		
		PlaceOrderParam order = new PlaceOrderParam();
		order.setInstrument_id(symbol.toLowerCase() + "-" + marketSymbol.toLowerCase());
		order.setPrice(price.toString());
		order.setType("limit");
		if (direction == 1) {
			order.setSide("buy");
		} else {
			order.setSide("sell");
		}
		
		order.setSize(amount.toString());
		order.setMargin_trading((byte) 1);

		try {
			OrderResult orderResult = this.spotOrderAPIServive.addOrder(order);
			if (!orderResult.isResult()) {
				retObj.put("error", "下单失败!!!");
				return retObj;
			}
			
			String orderId = String.valueOf(orderResult.getOrder_id());
			
			retObj.put("status", "ok");
			retObj.put("orderId", orderId);
			return retObj;
		} catch (APIException e) {
			String msg = e.getMessage();
			if (msg.contains("time out") && msg.contains("read")) {
				retObj.put("status", "error");
				retObj.put("error", msg);
			} else {
				retObj.put("status", "retry");
				retObj.put("error", msg);
			}
			
			return retObj;

		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg.contains("time out") && msg.contains("read")) {
				retObj.put("status", "error");
				retObj.put("error", msg);
			} else {
				retObj.put("status", "retry");
				retObj.put("error", msg);
			}
			
			return retObj;
		}
	}
	
	 public APIConfiguration okexApiConfig() {
        APIConfiguration config = new APIConfiguration();
        config.setEndpoint(HUOBI_API_BASE_URL);
        config.setApiKey(this.accessKey);
        config.setSecretKey(this.secretKey);
        config.setPassphrase(this.password);
        config.setPrint(false);
        return config;
    }
}
