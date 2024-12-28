package com.btc100.btc_q.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.btc100.btc_q.common.Contents;

public class FCoinApiUtil {

	private static final Logger logger = Logger.getLogger(FCoinApiUtil.class);
	
	private static final String FCOIN_API_BASE_URL = "https://api.fcoin.com/v2";
	private static final String FCOIN_API_ORDER = "/orders";
	private static final String FCOIN_API_BALANCE = "/accounts/balance";
	
	private String secretKey = "";
	private String accessKey = "";
	
	public FCoinApiUtil(String secretKey, String accessKey) {
		this.secretKey = secretKey;
		this.accessKey = accessKey;
	}
	
	public Map<String, BigDecimal> getBalance() throws JSONException {
		Map<String, BigDecimal> ret = new HashMap<String, BigDecimal>();
		Map<String, String> map = new HashMap<String, String>();
		String strResult = null;
		try {
			strResult = sendRequest(HttpToServer.METHOD_GET, map, FCOIN_API_BALANCE, "");
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		if (StringUtil.isEmpty(strResult)) {
			return ret;
		}
		
		JSONObject obj = new JSONObject(strResult);
		int status = obj.getInt("status");
		if (status != 0) {
			return ret;
		}
		
		JSONArray subList = obj.getJSONArray("data");
		for (int i = 0; i < subList.length(); i++) {
			JSONObject subAccount = subList.getJSONObject(i);
			String symbol = subAccount.getString("currency").toLowerCase();
			if ("ft".equals(symbol)) {
				BigDecimal balance = new BigDecimal(subAccount.getString("available"));
				ret.put(symbol, balance);
			} else if ("usdt".equals(symbol)) {
				BigDecimal balance = new BigDecimal(subAccount.getString("available"));
				ret.put(symbol, balance);
			}
		}
		
		return ret;
	}
	
	public JSONObject orderUsdt(String smb, int direction, BigDecimal amount, BigDecimal price) throws Exception {
		JSONObject retObj = new JSONObject();
		retObj.put("status", "retry");
		retObj.put("error", "");

		String symbol = smb + "usdt";
		String type = "limit";
		String side = "buy";
		if (direction == Contents.DIRECTION_SELL) {
			side = "sell";
		}
		
		String amountStr = amount.floatValue() + "";
		String priceStr = price.floatValue() + "";
		Map<String, String> map = new HashMap<String, String>();
		map.put("type", type);
		map.put("side", side);
		map.put("amount", amountStr);
		map.put("price", priceStr);
		map.put("symbol", symbol);
		
		JSONObject paramObj = new JSONObject();
		paramObj.put("type", type);
		paramObj.put("side", side);
		paramObj.put("amount", amountStr);
		paramObj.put("price", priceStr);
		paramObj.put("symbol", symbol);
		
		String data = paramObj.toString();
		
		String strResult = "";
		try {
			strResult = sendRequest(HttpToServer.METHOD_POST, map, FCOIN_API_ORDER, data.toString());
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
		int status = obj.getInt("status");
		if (status != 0) {
			retObj.put("error", obj.getString("msg"));
			return retObj;
		}
		
		String orderId = obj.getString("data");
		
		retObj.put("status", "ok");
		retObj.put("orderId", orderId);
		return retObj;
	}
	
	private String sendRequest(String method, Map<String,String> map, String path, String data) throws Exception {
		long time = System.currentTimeMillis();
		String sign = SignTopRequest(map, path, method, this.secretKey, time);
		
		String paramStr = "";
		StringBuilder params = new StringBuilder();
		if (HttpToServer.METHOD_GET.equals(method)) {
			for (String key : map.keySet()) {
				params.append(key + "=" + map.get(key) + "&");
			}
			
			paramStr = params.toString();
	        if (!StringUtil.isEmpty(paramStr) && paramStr.lastIndexOf("&") == paramStr.length() - 1) {
	        	paramStr = paramStr.substring(0, paramStr.length() - 1);
	        }
		}
		
		Map<String, String> heads = new HashMap<String, String>();
		heads.put("FC-ACCESS-KEY", this.accessKey);
		heads.put("FC-ACCESS-SIGNATURE", sign);
		heads.put("FC-ACCESS-TIMESTAMP", String.valueOf(time));
		
		return HttpToServer.sendGetError(method, FCOIN_API_BASE_URL + path, data, paramStr, heads);
	}
	
	
	private String SignTopRequest(Map<String, String> parameters, String path, String method, String secretKey, long time) throws UnsupportedEncodingException
    {
		// 第一步：把字典按Key的字母顺序排序
		Map<String, String> orderMap = Utils.sortMapByKey(parameters);    //按Key进行排序
		
        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        query.append( method);
        query.append(FCOIN_API_BASE_URL);
        query.append(path );
        query.append(time);
        if (orderMap != null) {
	        Iterator<String> keys = orderMap.keySet().iterator();
	        while (keys.hasNext()) {
				String key = keys.next();
				String value = orderMap.get(key);
	            if (!StringUtil.isEmpty(key))
	            {
	                query.append(key + "=" + value + "&");
	            }
			}
        }
        
        String url = query.toString();
        if (url.lastIndexOf("&") == url.length() - 1) {
        	url = url.substring(0, url.length() - 1);
        }
        
        String data = new String(Base64.encodeBase64(url.getBytes()));
        // 第四步：签名
        return sha256_HMAC(data, secretKey);
    }

	private String sha256_HMAC(String message, String secret) {     
		String hash = "";      
		try {         
			Mac sha256_HMAC = Mac.getInstance("HmacSHA1");     
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA1");      
			sha256_HMAC.init(secret_key);       
			byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
			String data = Base64.encodeBase64String(bytes);
			return data;
		} catch (Exception e) {     
//			System.out.println("Error HmacSHA256 ==== " + e.getMessage());   
		}    
		
		return hash;   
	}
}
