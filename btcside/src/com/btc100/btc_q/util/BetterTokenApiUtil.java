package com.btc100.btc_q.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.btc100.btc_q.vo.BetterTokenAccount;
import com.btc100.btc_q.vo.BetterTokenOrder;

public class BetterTokenApiUtil {

	private static final Logger logger = Logger.getLogger(BetterTokenApiUtil.class);

	private static final String BASE_URL = "https://www.tokenbetter.com";
	private static final String GET_TIME_PATH = "/openapi/exchange/public/time"; // GET,获取服务器时间
	private static final String GET_TICKER_PATH = "/openapi/exchange/public/{pairCode}/ticker"; // GET,获取ticker
	private static final String GET_DEPTH_PATH = "/openapi/exchange/public/{pairCode}/orderBook"; // GET,获取深度
	private static final String GET_ACCOUNTS_PATH = "/openapi/exchange/assets"; // GET,获取所有币种账户
	private static final String GET_ACCOUNT_PATH = "/openapi/exchange/{symbol}/assets"; // GET,获取所有币种账户
	private static final String ORDER_PATH = "/openapi/exchange/{pairCode}/orders"; // POST,下单
	private static final String CANCEL_ORDER_BY_ID_PATH = "/openapi/exchange/{pairCode}/orders/{id}"; // DELETE,根据id撤单
	private static final String CANCEL_ORDER_BY_IDS_PATH = "/openapi/exchange/{pairCode}/cancelByIds"; // DELETE,根据ids撤销挂单列表
	private static final String CANCEL_ALL_ORDERS_PATH = "/openapi/exchange/{pairCode}/cancel-all"; // DELETE,撤销所有挂单
	private static final String GET_ALL_ORDERS_PATH = "/openapi/exchange/orders";// GET,获取挂单,
	private static final String GET_ORDER_BY_ID_PATH = "/openapi/exchange/{pairCode}/orders/{id}"; // GTE,根据id查挂单
	private static final String GET_TRADES_PATH = "/openapi/exchange/{pairCode}/fulfillment"; // GTE,获取成交记录

	private String secretKey = "";
	private String accessKey = "";
	private String passphraseKey = "";
	private List<BetterTokenAccount> accounts = null;

	public BetterTokenApiUtil(String secretKey, String accessKey, String passphraseKey) {
		this.secretKey = secretKey;
		this.accessKey = accessKey;
		this.passphraseKey = passphraseKey;
	}

	public long getTimeStamp() throws JSONException {
		String strResult = "";
		try {
			strResult = HttpToServer.sendGet(BASE_URL + GET_TIME_PATH, "");
		} catch (Exception e) {
			logger.error("getTimeStamp error: " + e);
			return 0;
		}

		if (!StringUtil.isEmpty(strResult)) {
			JSONObject obj = new JSONObject(strResult);
			return obj.getLong("timestamp");
		}

		return 0;
	}

	public String getTicker(String symbol) throws Exception {
		String path = GET_TICKER_PATH;
		path = path.replace("{pairCode}", symbol.toUpperCase());
		String strResult = "";
		try {
			strResult = HttpToServer.sendGet(BASE_URL + path, "");
		} catch (Exception e) {
			e.printStackTrace();
			String msg = e.getMessage();

			return msg;
		}

		if (!StringUtil.isEmpty(strResult)) {
			return strResult;
		}

		return "";
	}

	public String getDepth(String symbol) throws Exception {
		String path = GET_DEPTH_PATH;
		path = path.replace("{pairCode}", symbol.toUpperCase());
		String strResult = "";
		try {
			strResult = HttpToServer.sendGet(BASE_URL + path, "");
		} catch (Exception e) {
			e.printStackTrace();
			String msg = e.getMessage();

			return msg;
		}

		if (!StringUtil.isEmpty(strResult)) {
			return strResult;
		}

		return "";
	}

	public List<BetterTokenAccount> getAccounts() throws UnsupportedEncodingException, JSONException {
		if (accounts == null) {
			accounts = new ArrayList<BetterTokenAccount>();
		}

		Map<String, Object> map = new HashMap<String, Object>();
		String strResult = "";
		try {
			strResult = sendRequest(HttpToServer.METHOD_GET, GET_ACCOUNTS_PATH, map, "");
		} catch (Exception e) {
			logger.error("getAccounts error: " + e);
		}

		if (StringUtil.isEmpty(strResult)) {
			return accounts;
		}

		JSONArray accoutArray = new JSONArray(strResult);
		for (int i = 0; i < accoutArray.length(); i++) {
			JSONObject accountObj = accoutArray.getJSONObject(i);

			BetterTokenAccount account = new BetterTokenAccount();
			account.setBrokerId(accountObj.getLong("brokerId"));
			account.setSymbol(accountObj.getString("symbol"));
			account.setAvailable(accountObj.getString("available"));
			account.setHold(accountObj.getString("hold"));
			account.setBaseBTC(accountObj.getLong("baseBTC"));
			account.setWithdrawLimit(accountObj.getString("withdrawLimit"));
			accounts.add(account);
		}

		return accounts;
	}
	
	public BetterTokenAccount getAccount(String symbol) throws UnsupportedEncodingException, JSONException {
		BetterTokenAccount account = new BetterTokenAccount();
		
		String path = GET_ACCOUNT_PATH;
		path = path.replace("{symbol}", symbol.toUpperCase());
		Map<String, Object> map = new HashMap<String, Object>();
//		if (!StringUtil.isEmpty(symbol)) {
//			map.put("symbol", symbol.toUpperCase());
//		}
		String strResult = "";
		try {
			strResult = sendRequest(HttpToServer.METHOD_GET, path, map, "");
		} catch (Exception e) {
			logger.error("getAccount error: " + e);
		}
		
		if (StringUtil.isEmpty(strResult)) {
			return account;
		}
		
		JSONObject accountObj = new JSONObject(strResult);
		account.setBrokerId(accountObj.getLong("brokerId"));
		account.setSymbol(accountObj.getString("symbol"));
		account.setAvailable(accountObj.getString("available"));
		account.setHold(accountObj.getString("hold"));
		account.setBaseBTC(accountObj.getLong("baseBTC"));
		account.setWithdrawLimit(accountObj.getString("withdrawLimit"));
		
		return account;
	}

	/**
	 * 下单接口 成功会返回订单id
	 * @param symbol 交易对
	 * @param direction 方向1-buy，2-sell
	 * @param orderType 下单类型，limit-限价，market-市价
	 * @param amount
	 * @param price
	 * @return 
	 * @throws Exception
	 */
	public JSONObject order(String symbol, int direction, String orderType, BigDecimal amount, BigDecimal price)
			throws Exception {
		JSONObject retObj = new JSONObject();
		
		if (amount.compareTo(new BigDecimal(20)) < 0) {
			retObj.put("status", "error");
			retObj.put("error", "最低下单量：20");
			return retObj;
		}
		String path = ORDER_PATH;
		path = path.replace("{pairCode}", symbol.toUpperCase());

		// eg:body ={"price":"1","side":"buy","source":"web","systemOrderType":"limit","volume":"1"}
		JSONObject dataObj = new JSONObject();
		dataObj.put("price", String.valueOf(price.doubleValue()));
		dataObj.put("source", "api");
		dataObj.put("systemOrderType", orderType);
		dataObj.put("volume", String.valueOf(amount.doubleValue()));
		if (direction == 1) {
			dataObj.put("side", "buy");
		} else {
			dataObj.put("side", "sell");
		}
		String data = dataObj.toString();

		String strResult = "";
		try {
			strResult = sendRequest(HttpToServer.METHOD_POST, path, null, data);
		} catch (Exception e) {
			String msg = e.getMessage();
			retObj.put("status", "error");
			retObj.put("error", msg);
			logger.error("order error: " + e);
			return retObj;
		}

		if (StringUtil.isEmpty(strResult)) {
			retObj.put("status", "error");
			retObj.put("error", "下单失败，请求结果返回空！");
			return retObj;
		}

		logger.info(strResult);

		retObj.put("status", "success");
		retObj.put("orderId", strResult);
		return retObj;
	}

	public String cancelOrder(String symbol, String id) throws Exception {
		String path = CANCEL_ORDER_BY_ID_PATH;
		path = path.replace("{pairCode}", symbol.toUpperCase());
		path = path.replace("{id}", id);
		
		JSONObject dataObj = new JSONObject();
		dataObj.put("pairCode", symbol);
		dataObj.put("id", id);
		
		String data = dataObj.toString();
		String strResult = "";
		try {
			strResult = sendRequest(HttpToServer.METHOD_DELETE, path, null, data);
		} catch (Exception e) {
			logger.error("cancelOrder error: " + e);
			return e.getMessage();
		}

		if (StringUtil.isEmpty(strResult)) {
			return "";
		}

		return "error";
	}
	
	public String cancelOrdersByIds(String symbol, List<Long> ids) throws Exception {
		String path = CANCEL_ORDER_BY_IDS_PATH;
		path = path.replace("{pairCode}", symbol.toUpperCase());
		
		if (ids == null || ids.size() == 0) {
			return "error, ids == null";
		}
		String strResult = "";
		try {
			strResult = sendRequestForCancel(HttpToServer.METHOD_DELETE, path, ids);
		} catch (Exception e) {
			logger.error("cancelOrdersByIds error: " + e);
			return "error";
		}
		
		
//		System.out.println("strResult:" + strResult);
		if (!StringUtil.isEmpty(strResult)) {
			return strResult;
		}
		
		return "error";
	}

	public String cancelAllOrders(String symbol) throws Exception {
		String path = CANCEL_ALL_ORDERS_PATH;
		path = path.replace("{pairCode}", symbol.toUpperCase());
		
		JSONObject dataObj = new JSONObject();
		dataObj.put("pairCode", symbol);
		String data = dataObj.toString();
		String strResult = "";
		try {
			strResult = sendRequest(HttpToServer.METHOD_DELETE, path, null, data);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("cancelAllOrders error: " + e);
			return "error";
		}

		if (StringUtil.isEmpty(strResult)) {
			return "";
		}

		return "error";
	}
	
	/**
	 * 获取挂单列表
	 * @param symbol 交易对名称BFX_USDT
	 * @param orderType
	 * @param source 下单来源 web,openapi,Ios,android
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws JSONException
	 */
	public List<BetterTokenOrder> getOrders(String symbol, String orderType, String source, int pageSize)
			throws UnsupportedEncodingException, JSONException {

		List<BetterTokenOrder> orders = new ArrayList<BetterTokenOrder>();

		String path = GET_ALL_ORDERS_PATH;

		Map<String, Object> map = new HashMap<String, Object>();
		if (!StringUtil.isEmpty(symbol)) {
			map.put("pairCode", symbol.toUpperCase());
		}
		if (!StringUtil.isEmpty(orderType)) {
			Integer type = "limit".equals(orderType) ? 0 : 1;
			map.put("systemOrderType", type);
		}
		if (!StringUtil.isEmpty(source)) {
			map.put("source", source);
		}
		if (pageSize > 0) {
			map.put("pageSize", pageSize);
		} else {
			map.put("pageSize", 1000);
		}
		map.put("page", 1);

		String strResult = "";
		try {
			strResult = sendRequest(HttpToServer.METHOD_GET, path, map, "");
		} catch (Exception e) {
			logger.error("getOrders error: " + e);
		}

		if (StringUtil.isEmpty(strResult)) {
			return orders;
		}

		JSONArray orderArray = new JSONArray(strResult);
		for (int i = 0; i < orderArray.length(); i++) {
			JSONObject orderObj = orderArray.getJSONObject(i);

			BetterTokenOrder order = new BetterTokenOrder();
			order.setId(orderObj.getLong("id"));
			order.setPairCode(orderObj.getString("pairCode"));
			order.setUserId(orderObj.getLong("userId"));
			order.setBrokerId(orderObj.getLong("brokerId"));
			order.setSide(orderObj.getString("side"));
			order.setEntrustPrice(orderObj.getDouble("entrustPrice"));
			order.setAmount(orderObj.getString("amount"));
			order.setDealAmount(orderObj.getString("dealAmount"));
			order.setQuoteAmount(StringUtil.isEmpty(orderObj.getString("quoteAmount")) ? "" : orderObj.getString("quoteAmount"));
			order.setDealQuoteAmount(orderObj.getString("dealQuoteAmount"));
			order.setSystemOrderType(orderObj.getString("systemOrderType"));
			order.setStatus(orderObj.getInt("status"));
			order.setSourceInfo(orderObj.getString("sourceInfo"));
			order.setCreateOn(orderObj.getLong("createOn"));
			order.setUpdateOn(orderObj.getLong("updateOn"));
			order.setSymbol(orderObj.getString("symbol"));
			order.setTrunOver(orderObj.getString("trunOver"));
			order.setNotStrike(orderObj.getString("notStrike"));
			order.setAveragePrice(orderObj.getString("averagePrice"));
			order.setOpenAmount(orderObj.getString("openAmount"));
			orders.add(order);
		}

		return orders;
	}
	
	/**
	 * 根据id获取挂单
	 * @param symbol 交易对
	 * @param id
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws JSONException
	 */
	public BetterTokenOrder getOrder(String symbol, String id) throws UnsupportedEncodingException, JSONException {
		BetterTokenOrder order = new BetterTokenOrder();
		
		String path = GET_ORDER_BY_ID_PATH;
		path = path.replace("{pairCode}", symbol.toUpperCase());
		path = path.replace("{id}", id);
		
		Map<String, Object> map = new HashMap<String, Object>();
//		if (!StringUtil.isEmpty(symbol)) {
//			map.put("pairCode", symbol.toUpperCase());
//		}
//		if (!StringUtil.isEmpty(id)) {
//			map.put("id", id);
//		}
		
		String strResult = "";
		try {
			strResult = sendRequest(HttpToServer.METHOD_GET, path, map, "");
		} catch (Exception e) {
			logger.error("getOrder error: " + e);
		}
		
		if (StringUtil.isEmpty(strResult)) {
			return null;
		}
		
		JSONObject orderObj = new JSONObject(strResult); 
		order.setId(orderObj.getLong("id"));
		order.setPairCode(orderObj.getString("pairCode"));
		order.setUserId(orderObj.getLong("userId"));
		order.setBrokerId(orderObj.getLong("brokerId"));
		order.setSide(orderObj.getString("side"));
		order.setEntrustPrice(orderObj.getDouble("entrustPrice"));
		order.setAmount(orderObj.getString("amount"));
		order.setDealAmount(orderObj.getString("dealAmount"));
		order.setQuoteAmount(StringUtil.isEmpty(orderObj.getString("quoteAmount")) ? "" : orderObj.getString("quoteAmount"));
		order.setDealQuoteAmount(orderObj.getString("dealQuoteAmount"));
		order.setSystemOrderType(orderObj.getString("systemOrderType"));
		order.setStatus(orderObj.getInt("status"));
		order.setSourceInfo(orderObj.getString("sourceInfo"));
		order.setCreateOn(orderObj.getLong("createOn"));
		order.setUpdateOn(orderObj.getLong("updateOn"));
		order.setSymbol(orderObj.getString("symbol"));
		order.setTrunOver(orderObj.getString("trunOver"));
		order.setNotStrike(orderObj.getString("notStrike"));
		order.setAveragePrice(orderObj.getString("averagePrice"));
		order.setOpenAmount(orderObj.getString("openAmount"));
		
		return order;
	}
	
	public List<BetterTokenOrder> getTrades(String symbol, String orderType, String source, Long startTime, Long endTime)
			throws UnsupportedEncodingException, JSONException {

		List<BetterTokenOrder> trades = new ArrayList<BetterTokenOrder>();

		String path = GET_TRADES_PATH;
		path = path.replace("{pairCode}", symbol.toUpperCase());

		Map<String, Object> map = new HashMap<String, Object>();
		if (!StringUtil.isEmpty(orderType)) {
			Integer type = "limit".equals(orderType) ? 0 : 1;
			map.put("systemOrderType", type);
		}
		if (!StringUtil.isEmpty(source)) {
			map.put("source", source);
		}
		if (startTime > 0) {
			map.put("startDate", startTime);
		}
		if (endTime > 0) {
			map.put("endDate", endTime);
		}
		map.put("page", 1);
		map.put("pageSize", 100);

		String strResult = "";
		try {
			strResult = sendRequest(HttpToServer.METHOD_GET, path, map, "");
		} catch (Exception e) {
			logger.error("getTrades error: " + e);
		}

		if (StringUtil.isEmpty(strResult)) {
			return trades;
		}

//		logger.info(strResult);
		JSONArray orderArray = new JSONArray(strResult);
		for (int i = 0; i < orderArray.length(); i++) {
			JSONObject orderObj = orderArray.getJSONObject(i);

			BetterTokenOrder order = new BetterTokenOrder();
			order.setId(orderObj.getLong("id"));
			order.setPairCode(orderObj.getString("pairCode"));
			order.setUserId(orderObj.getLong("userId"));
			order.setBrokerId(orderObj.getLong("brokerId"));
			order.setSide(orderObj.getString("side"));
			order.setEntrustPrice(orderObj.getDouble("entrustPrice"));
			order.setAmount(orderObj.getString("amount"));
			order.setDealAmount(orderObj.getString("dealAmount"));
			order.setQuoteAmount(StringUtil.isEmpty(orderObj.getString("quoteAmount")) ? "" : orderObj.getString("quoteAmount"));
			order.setDealQuoteAmount(orderObj.getString("dealQuoteAmount"));
			order.setSystemOrderType(orderObj.getString("systemOrderType"));
			order.setStatus(orderObj.getInt("status"));
			order.setSourceInfo(orderObj.getString("sourceInfo"));
			order.setCreateOn(orderObj.getLong("createOn"));
			order.setUpdateOn(orderObj.getLong("updateOn"));
			order.setSymbol(orderObj.getString("symbol"));
			order.setTrunOver(orderObj.getString("trunOver"));
			order.setNotStrike(orderObj.getString("notStrike"));
			order.setAveragePrice(orderObj.getString("averagePrice"));
			order.setOpenAmount(orderObj.getString("openAmount"));
			trades.add(order);
		}

		return trades;
	}
	
	private String sendRequestForCancel(String method, final String path, List<Long> ids) throws Exception {
		String queryString = "";
		StringBuilder params = new StringBuilder();
		for (Long id : ids) {
			params.append("ids=" + id + "&");
		}

		queryString = params.toString();
		if (!StringUtil.isEmpty(queryString) && queryString.lastIndexOf("&") == queryString.length() - 1) {
			queryString = queryString.substring(0, queryString.length() - 1);
		}
		
		return sendRequestForBT(method, path, queryString, "");
	}

	private String sendRequest(String method, final String path, Map<String, Object> map, String data)
			throws Exception {
		String queryString = "";
		StringBuilder params = new StringBuilder();
		if (HttpToServer.METHOD_GET.equals(method)) {
			for (String key : map.keySet()) {
				params.append(key + "=" + map.get(key) + "&");
			}

			queryString = params.toString();
			if (!StringUtil.isEmpty(queryString) && queryString.lastIndexOf("&") == queryString.length() - 1) {
				queryString = queryString.substring(0, queryString.length() - 1);
			}
		}
		
		return sendRequestForBT(method, path, queryString, data);
	}
	
	private String sendRequestForBT(String method, final String path, String queryString, String data)
			throws Exception {
		
		long timeStamp = getTimeStamp();
		String timestampStr = String.valueOf(timeStamp);
		String sign = generate(timestampStr, method.toUpperCase(), path, queryString, data, this.secretKey);

		Map<String, String> heads = new HashMap<String, String>();
		heads.put("ACCESS-KEY", this.accessKey);
		heads.put("ACCESS-SIGN", sign);
		heads.put("ACCESS-TIMESTAMP", timestampStr);
		heads.put("ACCESS-PASSPHRASE", this.passphraseKey);

		// System.out.println(generate("1582192437148","POST","/openapi/exchange/BTC_USDT/orders",null,"{\"volume\":\"0.1\",\"side\":\"sell\",\"systemOrderType\":\"limit\",\"pairCode\":\"BTC_USDT\",\"price\":\"9400\"}",this.secretKey));
		return HttpToServer.sendGetErrorForBT(method, BASE_URL + path, data, queryString, heads);
	}

	/**
	 * epoch time eg: 1517662142.557
	 */
	@SuppressWarnings("unused")
	private String getEpochTime(final Date time) {
		return getEpochTime(time.getTime());
	}

	/**
	 * epoch time eg: 1517662142.557
	 */
	private String getEpochTime(final long epochMills) {
		final BigDecimal bd1 = new BigDecimal(epochMills);
		final BigDecimal bd2 = new BigDecimal(1000);
		return bd1.divide(bd2, 3, BigDecimal.ROUND_DOWN).toString();
	}

	private String generate(final String timestamp, String method, final String requestPath, String queryString,
			String body, final String secretKey) {
		String hash = "";
		method = method.toUpperCase();
		body = StringUtil.isEmpty(body) ? "" : body;
		queryString = StringUtil.isEmpty(queryString) ? "" : "?" + queryString;
		final String preHash = timestamp + method + requestPath + queryString + body;
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			byte[] bytes = sha256_HMAC.doFinal(preHash.getBytes("UTF-8"));
			hash = Base64.encodeBase64String(bytes);

//			System.out.println("sing:" + hash);
		} catch (Exception e) {
			System.out.println("Error HmacSHA256 ==== " + e.getMessage());
		}
		return hash;
	}

}
