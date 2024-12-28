package com.btc100.btc_q.action.api.open.v1;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.action.api.BaseApiAction;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQExchangeRecord;
import com.btc100.btc_q.entity.BQExchangeSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQUserRiskControl;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQExchangeRecordService;
import com.btc100.btc_q.service.BQExchangeSettingService;
import com.btc100.btc_q.service.BQUserRiskControlService;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.ExchangeRecordVO;
import com.google.gson.Gson;

@Controller
@RequestMapping("/v1/exchange")
public class ExchangeOpenApiActionV1 extends BaseApiAction  {

	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQExchangeSettingService bqExchangeSettingService;
	@Autowired
	private BQExchangeRecordService bqExchangeRecordService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQUserRiskControlService bqUserRiskControlService;
	
	/**
	 * 历史兑换订单记录查询
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/orders")
	public String getOrders(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONArray array = new JSONArray();
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String startTime = request.getParameter("startTime");
		if (!StringUtil.isEmpty(startTime)) {
			map.put("startTime", startTime);
		}
		
		String endTime = request.getParameter("endTime");
		if (!StringUtil.isEmpty(endTime)) {
			map.put("endTime", endTime);
		}
		
		String originCoinName = request.getParameter("originCoinName");
		if (!StringUtil.isEmpty(originCoinName)) {
			map.put("originCoinName", originCoinName);
		}
		
		String targetCoinName = request.getParameter("targetCoinName");
		if (!StringUtil.isEmpty(targetCoinName)) {
			map.put("targetCoinName", targetCoinName);
		}
		
		String sizeStr = request.getParameter("size");
		if (!StringUtil.isEmpty(sizeStr)) {
			map.put("size", sizeStr);
		}
		
		String from = request.getParameter("from");
		String direct = request.getParameter("direct");
		long nFrom = Convert.toInt(from, -1);
		if (!StringUtil.isEmpty(from) || !StringUtil.isEmpty(direct)) {
			if (StringUtil.isEmpty(from) || StringUtil.isEmpty(direct)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (nFrom < 0 || (!"prev".equals(direct) && !"next".equals(direct))) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，from或direct");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			map.put("from", from);
			map.put("direct", direct);
		}
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int size = Convert.toInt(sizeStr, 10);
		if (size < 1) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，size错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (size > 100) size = 100;
		
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeName(originCoinName);
		if (originCoin == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知源币种");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeName(targetCoinName);
		if (targetCoin == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知目标币种");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		List<BQExchangeRecord> list = bqExchangeRecordService.readByUidAndTime(userId, start, end, nFrom,
				direct, size, originCoin.getType_id(), targetCoin.getType_id());
		if (list != null && list.size() > 0) {
			for (BQExchangeRecord record : list) {
				ExchangeRecordVO data = new ExchangeRecordVO();
				data.setRecordId(record.getId());
				data.setAvgPrice(record.getAvg_price().doubleValue());
				data.setObtainAmount(record.getObtain_amount().doubleValue());
				data.setOriAmount(record.getOri_amount().doubleValue());
				data.setOriginCoinName(record.getOri_currency_name().toLowerCase());
				data.setPresentPrice(record.getPresent_price().doubleValue());
				data.setRecordTime(record.getRecord_time());
				data.setTargetAmount(record.getTarget_amount().doubleValue());
				data.setTargetCoinName(record.getTarget_currency_name().toLowerCase());
				int state = record.getState();
				if (state == 4) {
					data.setState(Contents.EXCHANGE_STATE_DONE);
				} else if (state == 5) {
					data.setState(Contents.EXCHANGE_STATE_CANCEL);
				} else {
					data.setState(Contents.EXCHANGE_STATE_PROCESS);
				}
				Gson gson = new Gson();
				array.put(new JSONObject(gson.toJson(data)));
			}
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	/**
	 * 单条订单详情查询
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/order")
	public String getOrder(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String recordId = request.getParameter("recordId");
		if (StringUtil.isEmpty(recordId)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，没有recordId");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		map.put("recordId", recordId);
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		ExchangeRecordVO data = new ExchangeRecordVO();
		long rId = Convert.toLong(recordId, 0);
		BQExchangeRecord record = bqExchangeRecordService.readByIdAndURecordId(userId, rId);
		if (record != null) {
			data.setRecordId(record.getId());
			data.setAvgPrice(record.getAvg_price().doubleValue());
			data.setObtainAmount(record.getObtain_amount().doubleValue());
			data.setOriAmount(record.getOri_amount().doubleValue());
			data.setOriginCoinName(record.getOri_currency_name().toLowerCase());
			data.setPresentPrice(record.getPresent_price().doubleValue());
			data.setRecordTime(record.getRecord_time());
			data.setTargetAmount(record.getTarget_amount().doubleValue());
			data.setTargetCoinName(record.getTarget_currency_name().toLowerCase());
			int state = record.getState();
			if (state == 4) {
				data.setState(Contents.EXCHANGE_STATE_DONE);
			} else if (state == 5) {
				data.setState(Contents.EXCHANGE_STATE_CANCEL);
			} else {
				data.setState(Contents.EXCHANGE_STATE_PROCESS);
			}
			
			Gson gson = new Gson();
			tickerJson.put("data", new JSONObject(gson.toJson(data)));
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_NOORDER, "无此记录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	/**
	 * 兑换下单
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/orders/place", method = RequestMethod.POST)
	public String doOrdersPlace(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String originCoinName = request.getParameter("originCoinName");
		String targetCoinName = request.getParameter("targetCoinName");
		String quantity = request.getParameter("quantity");
		if (StringUtil.isEmpty(originCoinName) || StringUtil.isEmpty(targetCoinName) || StringUtil.isEmpty(quantity)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!originCoinName.equalsIgnoreCase("USDT") && !targetCoinName.equalsIgnoreCase("USDT")) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (originCoinName.equalsIgnoreCase(targetCoinName)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeName(originCoinName);
		if (originCoin == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知源币种");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeName(targetCoinName);
		if (targetCoin == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知目标币种");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		map.put("originCoinName", originCoinName);
		map.put("targetCoinName", targetCoinName);
		map.put("quantity", quantity);
		
		long userId = 0;
		if (StringUtil.isEmpty(map.get("token"))) {
			String checkKeyAndSign = checkAppKeyAndSign(request, map);
			if (!StringUtil.isEmpty(checkKeyAndSign)) {
				model.addAttribute("ticker", checkKeyAndSign);
				return apiData(response);
			}
			userId = Convert.toLong(map.get("userId"), 0);
		} else {
			Map<String,Object> mapToken = new HashMap<String, Object>();
			String checkToken = checkToken(request, mapToken);
			if (!StringUtil.isEmpty(checkToken)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			BQMember user = (BQMember) mapToken.get("user");
			String token = (String) mapToken.get("token");
			JSONObject userJson = new JSONObject();
			userJson.put("token", token);
			userId = user.getF_user_id();
		}
		
		BigDecimal toAmount = new BigDecimal(quantity);	// 用户兑换的币个数
		BigDecimal fromAmount = BigDecimal.ZERO;
		BigDecimal frozenAmount = BigDecimal.ZERO;
		BigDecimal frozenRate = fromAmount;
		BigDecimal price = getBuyOrSellPrice(originCoin, targetCoin);
		if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "获取市场价格错误，请稍后再试");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQExchangeSetting setting = null;
		if ("USDT".equalsIgnoreCase(originCoinName.toUpperCase())) {
			setting = bqExchangeSettingService.readExchangeSettingBySymbol(targetCoin.getType_name());
			if (setting == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "提交出错，请稍后再试");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			fromAmount = toAmount.multiply(price).setScale(4, BigDecimal.ROUND_UP);
			frozenRate = setting.getFrozen_rate().multiply(new BigDecimal(0.01));
			frozenAmount = fromAmount.multiply(frozenRate.add(new BigDecimal(1)));
			
			if (toAmount.compareTo(setting.getMin_amount()) < 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_EXCHANGEMIN, "兑换数量小于系统最小兑换");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		} else if ("USDT".equalsIgnoreCase(targetCoinName.toUpperCase())) {
			setting = bqExchangeSettingService.readExchangeSettingBySymbol(originCoin.getType_name());
			if (setting == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "提交出错，请稍后再试");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			fromAmount = toAmount.divide(price, 4, BigDecimal.ROUND_UP);
			frozenAmount = fromAmount;
			
			if (fromAmount.compareTo(setting.getMin_amount()) < 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_EXCHANGEMIN, "兑换数量小于系统最小兑换");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "不支持的兑换");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (setting != null && !setting.getRunning()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_EXCHANGEMIN, "兑换交易暂停");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BigDecimal balance = bqAccountService.readAccount(userId, originCoin.getType_id()).getBtc();
		if (balance.compareTo(fromAmount) < 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_INSUFFICIENTBALANCE, "可用余额不足");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		int state = Contents.EXCHANGE_STATE_SUBMIT;
		String riskNote = ""; 
		BQUserRiskControl riskControl = bqUserRiskControlService.readOne(userId);
		if (riskControl != null) {
			state = Contents.EXCHANGE_STATE_MANUAL;
			riskNote = ",风控用户，转人工";
		}
		
		BQExchangeRecord exchangeRecord = new BQExchangeRecord();
		exchangeRecord.setF_user_id(userId);
		exchangeRecord.setOri_currency_type_id(originCoin.getType_id());
		exchangeRecord.setOri_currency_name(originCoin.getType_name());
		exchangeRecord.setTarget_currency_type_id(targetCoin.getType_id());
		exchangeRecord.setTarget_currency_name(targetCoin.getType_name());
		exchangeRecord.setFrozen_rate(frozenRate);
		exchangeRecord.setFrozen_amount(frozenAmount);
		exchangeRecord.setOri_amount(fromAmount);
		exchangeRecord.setTarget_amount(toAmount);
		exchangeRecord.setPresent_price(price);
		exchangeRecord.setState(state);
		exchangeRecord.setNote("用户提交兑换：" + originCoin.getType_name() + "兑换" + targetCoin.getType_name() + riskNote);
		if ("USDT".equalsIgnoreCase(originCoinName.toUpperCase())) { // USDT兑换=>BTC/ETH/EOS 外部市场买
			exchangeRecord.setPrice_range(setting.getOutside_buy_range());
			exchangeRecord.setDirection(Contents.DIRECTION_BUY);
			exchangeRecord.setOrder_price(price.multiply(BigDecimal.ONE.add(exchangeRecord.getPrice_range().multiply(new BigDecimal(0.01)))).setScale(setting.getDigits(), BigDecimal.ROUND_HALF_DOWN));
			exchangeRecord.setFee_rate(setting.getFee_rate_from_usdt());
		} else {
			exchangeRecord.setPrice_range(setting.getOutside_sell_range());
			exchangeRecord.setDirection(Contents.DIRECTION_SELL);
			exchangeRecord.setOrder_price(price.multiply(BigDecimal.ONE.subtract(exchangeRecord.getPrice_range().multiply(new BigDecimal(0.01)))).setScale(setting.getDigits(), BigDecimal.ROUND_HALF_DOWN));
			exchangeRecord.setFee_rate(setting.getFee_rate_to_usdt());
		}
		
		try {
			bqExchangeRecordService.saveRecord(exchangeRecord);
			
			if (state == Contents.EXCHANGE_STATE_MANUAL) {
				final String email = UrlConfig.getValue("exchange.manualNotify.email");
				final String msg = exchangeRecord.getOri_currency_name() + "兑换" + exchangeRecord.getTarget_currency_name();
				Runnable runnable = new Runnable() {
		            public void run() {
		            	try {
		            		SendMail themail = new SendMail();
		            		String content = msg + "需人工审核，请登录xkd后台管理处理";
		        			themail.send(email, content);
		            	} catch (Exception e) {
		            	}
		            }
		        };
		        
		        Thread thread = new Thread(runnable);
		        thread.start();
			}
			
			tickerJson.put("data", exchangeRecord.getId());
		} catch (Exception e) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "提交出错，请稍后再试");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
}
