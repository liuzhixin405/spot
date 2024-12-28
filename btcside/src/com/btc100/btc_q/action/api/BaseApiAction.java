package com.btc100.btc_q.action.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.auth0.jwt.interfaces.Claim;
import com.btc100.base.BaseTradeAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.entity.BQUserAppKey;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.service.BQUserAppKeyService;
import com.btc100.btc_q.service.DeliveryTransactionTypeService;
import com.btc100.btc_q.service.NTransactionTypeService;
import com.btc100.btc_q.service.VTransactionTypeService;
import com.btc100.btc_q.util.APIEnDecrype;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.JwtToken;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.SendtelcodeUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

public class BaseApiAction extends BaseTradeAction {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionTypeService bqTransactionTypeService;
	@Autowired
	private VTransactionTypeService vTransactionTypeService;
	@Autowired
	private DeliveryTransactionTypeService dTransactionTypeService;
	@Autowired
	private BQUserAppKeyService bqUserAppKeyService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	protected NTransactionTypeService nTransactionTypeService;
	
	/**
	 * 通关partner算出userid，没有则返回0
	 * 
	 * @return
	 */
	protected long getUseridByPartner(String partner) {
		partner = APIEnDecrype.decrypt(partner);
		return Convert.toLong(partner, 0);
	}

	/**
	 * 错误编号转成json格式
	 * 
	 * @param errorCode
	 *            错误编号（ 10000：必选参数不能为空 10001：不存在此合约或者该合约已失效 10005：密钥不存在
	 *            10006：用户不存在 10007：签名不匹配 10009 ：订单不存在 10010：余额不足
	 *            10011：买卖的数量小于BTC/LTC最小买卖额度 10012 ：当前网站暂时只支持btc_cny ltc_cny
	 *            10013 ：平仓价格和数量必须大于0 10014：数量不得超过了持仓量 10015：下单价格与最新成交价偏差过大 ）
	 * @return
	 */
	protected String createErrorCode(int errorCode, String errorMsg) {
		JSONObject json = new JSONObject();
		try {
			json.put("code", errorCode);
			json.put("msg", errorMsg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	protected String createJwtCode(String token, int errorCode, String errorMsg) {
		JSONObject json = new JSONObject();
		try {
			json.put("code", errorCode);
			json.put("msg", errorMsg);
			json.put("token", token);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	/**
	 * 正确编号转成json格式
	 * 
	 * @param rightCode
	 *            正确编号（订单id）
	 * @return
	 */
	protected String createRightCode(long rightCode) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("result", true);
			if (rightCode > 0)
				obj.put("order_id", rightCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

	protected Map<Integer, Integer> findCurrMultiple(BQTransaction bqTransaction) {
		Map<Integer, Integer> pmap = new HashMap<Integer, Integer>();
		String[] smultiples = bqTransaction.getMultiple().split(",");
		String[] sstate = bqTransaction.getMultiple_state().split(",");
		for (int i = 0; i < smultiples.length; i++) {
			int multiple = Convert.toInt(smultiples[i], 0);
			int state = Convert.toInt(sstate[i], 0);
			if (multiple != 0)
				pmap.put(multiple, state);
		}

		return pmap;
	}
	
	protected int getDigits(String bqCode, String type) {
		String key = bqCode + BTCQMemecacheKeys.DIGITS;
		if ("vcontract".equals(type)) {
			key = bqCode + BTCQMemecacheKeys.V_DIGITS;
		} else if ("delivery".equals(type)) {
			key = bqCode + BTCQMemecacheKeys.D_DIGITS;
		}
		Integer digits = spyMemcachedClient.get(key);
		if (digits == null) {
			BigDecimal price = BigDecimal.ZERO;
			if ("vcontract".equals(type)) {
				VTransaction trans = super.findCurrVTrans(bqCode);
				if (trans == null) return 0;
				
				VTransactionType transType = vTransactionTypeService.readTransactionType(trans.getTransaction_type());
				price = transType.getMin_float_price();
			} else if ("delivery".equals(type)) {
				DeliveryTransaction trans = super.findCurrDTrans(bqCode);
				if (trans == null) return 0;
				
				DeliveryTransactionType transType = dTransactionTypeService.readTransactionType(trans.getTransaction_type());
				price = transType.getMin_float_price();
			} else if (type == null || "".equals(type) || "contract".equals(type)) {
				BQTransaction trans = super.findCurrTrans(bqCode);
				if (trans == null) return 0;
				
				BQTransactionType transType = bqTransactionTypeService.readTransactionType(trans.getTransaction_type());
				price = transType.getMin_float_price();
			} else {
				NTransaction trans = super.findNCurrTrans(type, bqCode);
				if (trans == null) return 0;
				
				NTransactionType transType = nTransactionTypeService.readTransactionType(type, trans.getTransaction_type());
				price = transType.getMin_float_price();
			}
			
			digits = Utils.getDigitsFromMinFloat(price);
			spyMemcachedClient.set(key, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}
		
		return digits;
	}
	
	protected int getSpotDigits(String code) {
		Integer digits = spyMemcachedClient.get(code + BTCQMemecacheKeys.DIGITS);
		if (digits == null) {
			BQSpotMarketCoinPair pair = super.findCurrCoinPairByName(code);
			if (pair == null) {
				return 0;
			}
			digits = Utils.getDigitsFromMinFloat(pair.getMin_float_price());
			spyMemcachedClient.set(code + BTCQMemecacheKeys.DIGITS, BTCQMemecacheKeys.TIMES.TEN_SECOND, digits);
		}
		
		return digits;
	}
	
	protected String checkCommonParams(HttpServletRequest request, Map<String, String> map) {
		String token = request.getParameter("token");
		if (!StringUtil.isEmpty(token)) {
			map.put("token", token);
			return "";
		}
		
		String appKey = request.getParameter("AppKey");
		String signature = request.getParameter("Signature");
		String timestamp = request.getParameter("Timestamp");
		
		if (StringUtil.isEmpty(appKey) || StringUtil.isEmpty(signature) || StringUtil.isEmpty(timestamp)) {
			return "参数错误";
		}
		
		long time = Convert.toLong(timestamp, 0);
		if (time == 0) {
			return "参数错误，Timestamp参数错误";
		}
		
		long now = System.currentTimeMillis() / 1000;
		if (Math.abs(now - time) > 300) {
			return "参数错误，Timestamp参数错误";
		}
		
		map.put("AppKey", appKey);
		map.put("Signature", signature);
		map.put("Timestamp", timestamp);
		
		return "";
	}
	
	protected String checkAppKeyAndSign(HttpServletRequest request, Map<String, String> map) throws Exception {
		return checkAppKeyAndSign(request, map, false);
	}
	
	protected String checkAppKeyAndSign(HttpServletRequest request, Map<String, String> map, boolean encode) throws Exception {
		String appKey = map.get("AppKey");
		BQUserAppKey userAppKey = bqUserAppKeyService.readByAppKey(appKey);
		if (userAppKey == null) {
			return createErrorCode(Contents.API_ERROR_APPKEY, "AppKey错误");
		}
		
		if (userAppKey.getState() != 1) {
			return createErrorCode(Contents.API_ERROR_APPKEYDISABLED, "AppKey已禁用");
		}
		
		String ips = userAppKey.getAllow_ip();
		String[] ipArray = new String[0];
		if (!StringUtil.isEmpty(ips)) {
			ipArray = ips.split(",");
		}
		
		if (ipArray == null || ipArray.length == 0) {
			long now = System.currentTimeMillis() / 1000;
			if (now > userAppKey.getOver_time()) {
				return createErrorCode(Contents.API_ERROR_OVERTIME, "AppKey已过期");
			}
		}
		
		if (ipArray.length > 0) {
			boolean contain = false;
			String ip = IpMacUtil.getIpAddr(request);
			for (String allowip : ipArray) {
				if (allowip.equals(ip)) {
					contain = true;
					break;
				}
			}
			
			if (!contain) {
				return createErrorCode(Contents.API_ERROR_IPNOTALLOW, "未授权IP");
			}
		}
		
		String signature = map.get("Signature");
		map.remove("Signature");
		
		String sign = signTopRequest(map, userAppKey.getApp_secret(), encode);
		if (!signature.equals(sign)) {
			return createErrorCode(Contents.API_ERROR_SIGN, "签名错误");
		}
		
		map.put("userId", "" + userAppKey.getF_user_id());
		
		return "";
	}
	
	protected static String signTopRequest(Map<String, String> parameters, String secret) throws Exception {
		return signTopRequest(parameters, secret, false);
	}
	
	protected static String signTopRequest(Map<String, String> parameters, String secret, boolean encode) throws Exception {
		// 第一步：把字典按Key的字母顺序排序
		Map<String, String> orderMap = com.btc100.btc_q.util.Utils.sortMapByKey(parameters);    //按Key进行排序
		
        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        query.append(secret);
        Iterator<String> keys = orderMap.keySet().iterator();
        while (keys.hasNext()) {
			String key = keys.next();
			String value = orderMap.get(key);
			if (encode) {
				value = URLEncoder.encode(value, "UTF8");
			}
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
	
	protected String checkToken(HttpServletRequest request, Map<String, Object> map) {
		String token = request.getParameter("token");
		if (StringUtil.isEmpty(token)) {
			return "参数错误，token参数错误";
		}
		
		String verifyToken = JwtToken.verifyToken(token);
		if (StringUtil.isEmpty(verifyToken)) {
			return "参数错误，token参数错误";
		}
		map.put("token", verifyToken);
		
		Map<String, Claim> claims = JwtToken.decryptToken(token);
		String userId = claims.get("userId").asString();
		long uid = Convert.toLong(userId, 0);
		if (uid > 0) {
			BQMember user = bqMemberService.readUserById(uid);
			if (user == null) {
				return "参数错误，token参数错误，用户无效";
			}
			if (user.getF_user_state() != 1) {
				return "参数错误，token参数错误";
			}
			map.put("user", user);
		}

		return "";
	}
	
	public String sendTelCode(long userId, String zone, String phone, int code) throws IOException {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String nowDay = sdf.format(d);
		
		Integer sendLimit = spyMemcachedClient.get(nowDay + BTCQMemecacheKeys.TEL_CODE_LIMIT + userId);
		if (sendLimit != null && sendLimit > Contents.TEL_CODE_LIMIT) {
			return "超过日发送限额";
		}
		
		String sendRet = SendtelcodeUtil.sendTelCode(zone, phone, code);
		if (StringUtil.isEmpty(sendRet)) {
			sendLimit = spyMemcachedClient.get(nowDay + BTCQMemecacheKeys.TEL_CODE_LIMIT + userId);
			if (sendLimit == null) sendLimit = 0;
			sendLimit += 1;
			
			spyMemcachedClient.set(nowDay + BTCQMemecacheKeys.TEL_CODE_LIMIT + userId, BTCQMemecacheKeys.TIMES.ONE_DAY, sendLimit);
		}
		
		return sendRet;
	}
	
	protected String apiData(HttpServletResponse response) {
		addCrossOrigin(response);
		return "jsp/api/ticker";
	}
	
	private void addCrossOrigin(HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
	}
	
}
