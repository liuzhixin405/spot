package com.btc100.btc_q.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.btc100.btc_q.vo.HuobiAccount;
import com.btc100.btc_q.vo.HuobiOrderInfo;


public class HuobiApiUtil {
	
	private static final Logger logger = Logger.getLogger(HuobiApiUtil.class);
	
	private static final String HUOBI_API_BASE_URL = "https://api.huobi.pro";
	private static final String HUOBI_API_ACCOUNTS_PATH = "/v1/account/accounts";
	private static final String HUOBI_API_ACCOUNT_BALANCE = "/v1/account/accounts/{account-id}/balance";
	private static final String HUOBI_API_ORDER = "/v1/order/orders/place";
	private static final String HUOBI_API_GET_ORDER = "/v1/order/orders/";
	
	private String secretKey = "";
	private String accessKey = "";
	private List<HuobiAccount> accounts = null;
	
	public HuobiApiUtil(String secretKey, String accessKey) {
		this.secretKey = secretKey;
		this.accessKey = accessKey;
	}
	
	public List<HuobiAccount> getAccounts() throws UnsupportedEncodingException, JSONException {
		
		if (accounts == null) {
			accounts = new ArrayList<HuobiAccount>();
		}
		
		Map<String, String> map = getCommonParams();
		String strResult = "";
		try {
			strResult = sendRequest(HttpToServer.METHOD_GET, map, HUOBI_API_ACCOUNTS_PATH, "");
		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("getAccounts error: " + e.getMessage());
		}
		
		if (StringUtil.isEmpty(strResult)) {
			return accounts;
		}
		
		JSONObject obj = new JSONObject(strResult);
		String status = obj.getString("status");
		if (!"ok".equals(status)) {
			return accounts;
		}
		
		JSONArray accoutArray = obj.getJSONArray("data");
		for (int i = 0; i < accoutArray.length(); i++) {
			JSONObject accountObj = accoutArray.getJSONObject(i);
			long accountId = accountObj.getLong("id");
			String type = accountObj.getString("type");
			String state = accountObj.getString("state");
			
			boolean have = false;
			for (HuobiAccount act : accounts) {
				if (act.getId() == accountId) {
					have = true;
					break;
				}
			}
			
			if (have) continue;
			
			HuobiAccount account = new HuobiAccount();
			account.setId(accountId);
			account.setType(type);
			account.setState(state);
			
			accounts.add(account);
		}
		
		return accounts;
	}
	
	public HuobiOrderInfo queryOrder(String orderId) throws Exception {
		if (accounts == null || accounts.size() == 0) {
			try {
				getAccounts();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (accounts == null || accounts.size() == 0) {
			return null;
		}
		
		HuobiAccount spotAccount = null;
		for (HuobiAccount account : accounts) {
			if ("spot".equals(account.getType())) {
				spotAccount = account;
				break;
			}
		}
		
		if (spotAccount == null) {
			return null;
		}

		Map<String, String> map = getCommonParams();
		String strResult = "";
		try {
			String path = HUOBI_API_GET_ORDER + orderId;
			strResult = sendRequest(HttpToServer.METHOD_GET, map, path, "");
		} catch (Exception e) {
			return null;
		}
		
		if (StringUtil.isEmpty(strResult)) {
			return null;
		}
		
		logger.info(strResult);
		JSONObject obj = new JSONObject(strResult);
		String status = obj.getString("status");
		if (!"ok".equals(status)) {
			return null;
		}
		
		JSONObject orderInfo = obj.getJSONObject("data");
		HuobiOrderInfo info = new HuobiOrderInfo();
		info.setId(orderInfo.getLong("id"));
		info.setSymbol(orderInfo.getString("symbol"));
		info.setAmount(new BigDecimal(orderInfo.getString("amount")));
		info.setFieldAmount(new BigDecimal(orderInfo.getString("field-amount")));
		info.setDealCash(new BigDecimal(orderInfo.getString("field-cash-amount")));
		info.setPrice(new BigDecimal(orderInfo.getString("price")));
		info.setState(orderInfo.getString("state"));
		info.setType(orderInfo.getString("type"));
		
		return info;
	}
	
	public JSONObject orderUsdt(String symbol, String partition, int direction, BigDecimal amount, BigDecimal price) throws Exception {
		return order(symbol, partition, direction, amount, price);
	}
	
	public JSONObject order(String symbol, String marketSymbol, int direction, BigDecimal amount, BigDecimal price) throws Exception {
		
		JSONObject retObj = new JSONObject();
		retObj.put("status", "retry");
		retObj.put("error", "");
		
		if (accounts == null || accounts.size() == 0) {
			try {
				getAccounts();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (accounts == null || accounts.size() == 0) {
			retObj.put("error", "获取账户失败！");
			return retObj;
		}
		
		HuobiAccount spotAccount = null;
		for (HuobiAccount account : accounts) {
			if ("spot".equals(account.getType())) {
				spotAccount = account;
				break;
			}
		}
		
		if (spotAccount == null) {
			retObj.put("error", "获取现货账户失败！");
			return retObj;
		}

		Map<String, String> map = getCommonParams();
		
		JSONObject dataObj = new JSONObject();
		dataObj.put("account-id", String.valueOf(spotAccount.getId()));
		dataObj.put("amount", String.valueOf(amount.doubleValue()));
		dataObj.put("price", String.valueOf(price.doubleValue()));
		dataObj.put("symbol", symbol.toLowerCase() + marketSymbol.toLowerCase());
		if (direction == 1) {
			dataObj.put("type", "buy-limit");
		} else {
			dataObj.put("type", "sell-limit");
		}
		String data = dataObj.toString();
		
		String strResult = "";
		try {
			strResult = sendRequest(HttpToServer.METHOD_POST, map, HUOBI_API_ORDER, data);
		} catch (Exception e) {
			e.printStackTrace();
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
		
		if (StringUtil.isEmpty(strResult)) {
			retObj.put("error", "下单失败，请求结果返回空！");
			return retObj;
		}
		
		logger.info(strResult);
		JSONObject obj = new JSONObject(strResult);
		String status = obj.getString("status");
		if (!"ok".equals(status)) {
			retObj.put("error", obj.getString("err-msg"));
			return retObj;
		}
		
		String orderId = obj.getString("data");
		
		retObj.put("status", "ok");
		retObj.put("orderId", orderId);
		return retObj;
	}
	
	public Map<String, BigDecimal> getBalance() throws JSONException, UnsupportedEncodingException {
		Map<String, BigDecimal> balanceMap = new HashMap<String, BigDecimal>();
		
		if (accounts == null || accounts.size() == 0) {
			try {
				getAccounts();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (accounts == null || accounts.size() == 0) {
			return balanceMap;
		}
		
		HuobiAccount spotAccount = null;
		for (HuobiAccount account : accounts) {
			if ("spot".equals(account.getType())) {
				spotAccount = account;
				break;
			}
		}
		
		if (spotAccount == null) return balanceMap;
		
		String path = HUOBI_API_ACCOUNT_BALANCE;
		path = path.replace("{account-id}", String.valueOf(spotAccount.getId()));
		Map<String, String> map = getCommonParams();
		String strResult = "";
		try {
			strResult = sendRequest(HttpToServer.METHOD_GET, map, path, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (StringUtil.isEmpty(strResult)) {
			return balanceMap;
		}
		
		JSONObject obj = new JSONObject(strResult);
		String status = obj.getString("status");
		if (!"ok".equals(status)) {
			return balanceMap;
		}
		
		JSONObject accoutObj = obj.getJSONObject("data");
		JSONArray subList = accoutObj.getJSONArray("list");
		for (int i = 0; i < subList.length(); i++) {
			JSONObject subAccount = subList.getJSONObject(i);
			String symbol = subAccount.getString("currency").toLowerCase();
			String type = subAccount.getString("type").toLowerCase();
			if ("trade".equals(type)) {
				BigDecimal balance = new BigDecimal(subAccount.getString("balance"));
				balanceMap.put(symbol, balance);
			}
		}
		
		return balanceMap;
	}
	
	private String sendRequest(String method, Map<String,String> map, String path, String data) throws Exception {

		String sign = SignTopRequest(map, path, method, this.secretKey);
		sign = URLEncoder.encode(sign, "UTF-8");
		
		StringBuilder params = new StringBuilder();
		for (String key : map.keySet()) {
			params.append(key + "=" + map.get(key) + "&");
		}
		params.append("Signature" + "=" + sign);
		
		return HttpToServer.sendGetError(method, HUOBI_API_BASE_URL + path, data, params.toString());
	}
	
	
	private String SignTopRequest(Map<String, String> parameters, String path, String method, String secretKey) throws UnsupportedEncodingException
    {
		// 第一步：把字典按Key的字母顺序排序
		Map<String, String> orderMap = Utils.sortMapByKey(parameters);    //按Key进行排序
		
        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        query.append( method + "\n");
        query.append("api.huobi.pro\n");
        query.append(path + "\n");
        Iterator<String> keys = orderMap.keySet().iterator();
        while (keys.hasNext()) {
			String key = keys.next();
			String value = orderMap.get(key);
            if (!StringUtil.isEmpty(key))
            {
                query.append(key + "=" + value + "&");
            }
		}
        
        String url = query.toString();
        if (url.lastIndexOf("&") == url.length() - 1) {
        	url = url.substring(0, url.length() - 1);
        }
 
        // 第四步：签名
        return sha256_HMAC(url, secretKey);
    }

	private String sha256_HMAC(String message, String secret) {     
		String hash = "";      
		try {         
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");     
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");      
			sha256_HMAC.init(secret_key);       
			byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
			return Base64.encodeBase64String(bytes);
		} catch (Exception e) {     
//			System.out.println("Error HmacSHA256 ==== " + e.getMessage());   
		}    
		
		return hash;   
	}
	
	private Map<String, String> getCommonParams() throws UnsupportedEncodingException {
		String time = getUTCTime();
		Map<String, String> map = new HashMap<String, String>();
		map.put("AccessKeyId", this.accessKey);
		map.put("SignatureMethod", "HmacSHA256");
		map.put("SignatureVersion", "2");
		map.put("Timestamp", URLEncoder.encode(time, "UTF-8"));
		
		return map;
	}
	
	private String getUTCTime() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		int year = calendar.get(Calendar.YEAR);
	    int month = calendar.get(Calendar.MONTH) + 1; 
	    int day = calendar.get(Calendar.DAY_OF_MONTH);
	    int hour = calendar.get(Calendar.HOUR_OF_DAY);
	    int minute = calendar.get(Calendar.MINUTE);
	    int second = calendar.get(Calendar.SECOND);
	    
	    StringBuilder timeSb = new StringBuilder();
	    timeSb.append(year + "-");
	    timeSb.append(month<10?"0":"").append(month + "-");
	    timeSb.append(day<10?"0":"").append(day);
	    timeSb.append("T");
	    timeSb.append(hour<10?"0":"").append(hour + ":");
	    timeSb.append(minute<10?"0":"").append(minute + ":");
	    timeSb.append(second<10?"0":"").append(second);
	    
	    return timeSb.toString();
	}
	
}
