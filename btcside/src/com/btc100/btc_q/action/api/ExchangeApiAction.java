package com.btc100.btc_q.action.api;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.TypeParse;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.service.*;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.ExchangeRecordVO;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 兑换
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/exchangeApi")
public class ExchangeApiAction extends BaseApiAction {

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
	@Autowired
	private BQExchangeBfcSettingService bqExchangeBfcSettingService;
	@Autowired
	private BQExchangeBfcRecordService bqExchangeBfcRecordService;

	/**
	 * 多条兑换记录查询
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/records")
	public String getList(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
		
		if ("USDT".equals(originCoinName.toUpperCase()) || "USDT".equals(targetCoinName.toUpperCase())) {
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
		} else if ("BFC".equals(originCoinName.toUpperCase()) || "BFC".equals(targetCoinName.toUpperCase())) {
			List<BQExchangeBfcRecord> list = bqExchangeBfcRecordService.readByUidAndTime(userId, start, end, nFrom,
					direct, size, originCoin.getType_id(), targetCoin.getType_id());
			if (list != null && list.size() > 0) {
				for (BQExchangeBfcRecord record : list) {
					ExchangeRecordVO data = new ExchangeRecordVO();
					data.setRecordId(record.getId());
					data.setAvgPrice(record.getOrder_price().doubleValue());
					data.setObtainAmount(record.getTarget_amount().doubleValue());
					data.setOriAmount(record.getOri_amount().doubleValue());
					data.setOriginCoinName(record.getOri_currency_name().toLowerCase());
					data.setPresentPrice(record.getOrder_price().doubleValue());
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
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	/**
	 * 单条兑换记录查询
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/record")
	public String getInfo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
			BQExchangeBfcRecord bfcRecord = bqExchangeBfcRecordService.readByIdAndURecordId(userId, rId);
			if (bfcRecord != null) {
				data.setRecordId(bfcRecord.getId());
				data.setAvgPrice(bfcRecord.getOrder_price().doubleValue());
				data.setObtainAmount(bfcRecord.getTarget_amount().doubleValue());
				data.setOriAmount(bfcRecord.getOri_amount().doubleValue());
				data.setOriginCoinName(bfcRecord.getOri_currency_name().toLowerCase());
				data.setPresentPrice(bfcRecord.getOrder_price().doubleValue());
				data.setRecordTime(bfcRecord.getRecord_time());
				data.setTargetAmount(bfcRecord.getTarget_amount().doubleValue());
				data.setTargetCoinName(bfcRecord.getTarget_currency_name().toLowerCase());
				int state = bfcRecord.getState();
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
	@RequestMapping(value = "/exchange", method = RequestMethod.POST)
	public String ordersPlace(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
		
		if (!originCoinName.equalsIgnoreCase("USDT") && !targetCoinName.equalsIgnoreCase("USDT")
				&& !originCoinName.equalsIgnoreCase("BFC") && !targetCoinName.equalsIgnoreCase("BFC")) {
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
		BigDecimal price = BigDecimal.ZERO;
		
		BQExchangeSetting setting = null;
		if ("USDT".equals(originCoinName.toUpperCase()) || "USDT".equals(targetCoinName.toUpperCase())) {
			price = getBuyOrSellPrice(originCoin, targetCoin);
			if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "获取市场价格错误，请稍后再试");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
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
			}
			
			if (setting != null && !setting.getRunning()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_EXCHANGEMIN, "兑换交易暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
		} else if ("BFC".equals(originCoinName.toUpperCase()) || "BFC".equals(targetCoinName.toUpperCase())) {
			BQExchangeBfcSetting bfcSetting = null;
			if ("BFC".equals(originCoinName.toUpperCase())) {
				bfcSetting = bqExchangeBfcSettingService.readBySymbol(targetCoin.getType_name());
				if (bfcSetting == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "提交出错，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				price = bfcSetting.getExchange_rate();
				fromAmount = toAmount.multiply(price).setScale(4, BigDecimal.ROUND_UP);
				
				if (new BigDecimal(toAmount.intValue()).compareTo(toAmount) != 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "只接受整数兑换");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (toAmount.compareTo(bfcSetting.getMin_amount()) < 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_EXCHANGEMIN, "兑换数量小于系统最小兑换");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
			} else if ("BFC".equals(targetCoinName.toUpperCase())) {
				bfcSetting = bqExchangeBfcSettingService.readBySymbol(originCoin.getType_name());
				if (bfcSetting == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "提交出错，请稍后再试");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				price = bfcSetting.getExchange_rate();
				fromAmount = toAmount.divide(price, 4, BigDecimal.ROUND_UP);
				
				if (fromAmount.compareTo(bfcSetting.getMin_amount()) < 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_EXCHANGEMIN, "兑换数量小于系统最小兑换");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (new BigDecimal(fromAmount.intValue()).compareTo(fromAmount) != 0) {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "只接受整数兑换");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			if (bfcSetting != null && !bfcSetting.getRunning()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_EXCHANGEMIN, "兑换交易暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "不支持的兑换");
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
		
		long dataId = 0;
		try {
			if ("USDT".equals(originCoinName.toUpperCase()) || "USDT".equals(targetCoinName.toUpperCase())) {

				state = Contents.EXCHANGE_STATE_MANUAL;
				riskNote = ", 转人工";

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
				bqExchangeRecordService.saveRecord(exchangeRecord);
				dataId = exchangeRecord.getId();
			}
			
			if ("BFC".equals(originCoinName.toUpperCase()) || "BFC".equals(targetCoinName.toUpperCase())) {
				BQExchangeBfcRecord exchangeBfcRecord = new BQExchangeBfcRecord();
				exchangeBfcRecord.setF_user_id(userId);
				exchangeBfcRecord.setOri_currency_type_id(originCoin.getType_id());
				exchangeBfcRecord.setOri_currency_name(originCoin.getType_name());
				exchangeBfcRecord.setTarget_currency_type_id(targetCoin.getType_id());
				exchangeBfcRecord.setTarget_currency_name(targetCoin.getType_name());
				exchangeBfcRecord.setOri_amount(fromAmount);
				exchangeBfcRecord.setFrozen_amount(fromAmount);
				exchangeBfcRecord.setTarget_amount(toAmount);
				exchangeBfcRecord.setState(state);
				exchangeBfcRecord.setOrder_price(price);
				exchangeBfcRecord.setNote("用户提交兑换：" + originCoin.getType_name() + "兑换" + targetCoin.getType_name() + riskNote);
				bqExchangeBfcRecordService.saveRecord(exchangeBfcRecord);
				dataId = exchangeBfcRecord.getId();
			}
			
			if (state == Contents.EXCHANGE_STATE_MANUAL) {
				final String email = UrlConfig.getValue("exchange.manualNotify.email");
				final String msg = originCoin.getType_name() + "兑换" + targetCoin.getType_name();
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
			
			tickerJson.put("data", dataId);
		} catch (Exception e) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "提交出错，请稍后再试");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	/**
	 * 用户可兑换币的全部数量和价格
	 * 
	 * @param request
	 * @param response
	 * @throws JSONException
	 */
	@RequestMapping("/checkExchangeableAmount")
	public String checkExchangeableAmount(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));	
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);
		
		String originCoinName = request.getParameter("originCoinName");
		String targetCoinName = request.getParameter("targetCoinName");
		
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeName(originCoinName); // 源币种
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeName(targetCoinName); // 目标币种
		
		BQExchangeSetting setting = null;
		BQExchangeBfcSetting bfcSetting = null;
		BigDecimal price = BigDecimal.ZERO;
		
		BigDecimal frozenRate = BigDecimal.ZERO; // 冻结资金率
		BigDecimal exchangeableAmount = BigDecimal.ZERO; // 可兑换的数量
		BigDecimal balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc().setScale(4, BigDecimal.ROUND_DOWN); // 对应币种账户余额
		if ("USDT".equals(originCoinName)) {
			setting = bqExchangeSettingService.readExchangeSettingBySymbol(targetCoin.getType_name());
		} else if ("USDT".equals(targetCoinName)) {
			setting = bqExchangeSettingService.readExchangeSettingBySymbol(originCoin.getType_name());
		} else if ("BFC".equals(originCoinName)) {
			bfcSetting = bqExchangeBfcSettingService.readBySymbol(targetCoin.getType_name());
		} else if ("BFC".equals(targetCoinName)) {
			bfcSetting = bqExchangeBfcSettingService.readBySymbol(originCoin.getType_name());
		}
		
		if (setting == null && bfcSetting == null) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "未知错误，请稍候重试，或联系客服");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if ("USDT".equals(originCoinName.toUpperCase()) || "USDT".equals(targetCoinName.toUpperCase())) {
			price = getBuyOrSellPrice(originCoin, targetCoin);
			if ("USDT".equals(originCoin.getType_name().toUpperCase())) { // USD兑换=>BTC/ETH/EOS
				frozenRate = setting.getFrozen_rate().multiply(new BigDecimal(0.01));
				exchangeableAmount = balance.divide(price.multiply(new BigDecimal(1).add(frozenRate)), 4, BigDecimal.ROUND_DOWN);
			} else { // BTC/ETH/EOS兑换=>USDT
				exchangeableAmount = balance.multiply(price).setScale(4, BigDecimal.ROUND_DOWN);
			}
		} else if ("BFC".equals(originCoinName.toUpperCase()) || "BFC".equals(targetCoinName.toUpperCase())) {
			price = bfcSetting.getExchange_rate();
			if ("BFC".equals(originCoin.getType_name().toUpperCase())) { // BFC兑换=>BFX
				exchangeableAmount = balance.divide(price, 4, BigDecimal.ROUND_DOWN);
			} else { // BFX兑换=>BFC
				exchangeableAmount = balance.multiply(price).setScale(4, BigDecimal.ROUND_DOWN);
			}
		}
		
		Map<String, Object> exchangeMap = new HashMap<String, Object>();
		exchangeMap.put("exchangeableAmount", exchangeableAmount.doubleValue());
		exchangeMap.put("price", price.doubleValue());
		
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(exchangeMap)));
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}

	/**
	 * 兑换之前：判断账户余额是否足够，计算兑换金额
	 * 输入兑换数量onblur
	 * @param request
	 * @param model
	 * @param response
	 * @throws JSONException
	 */
	@RequestMapping("/checkExchange")
	public String checkExchange(HttpServletRequest request, Model model, HttpServletResponse response) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "token错误，请重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		userJson.put("token", token);

		String originCoinName = request.getParameter("originCoinName");
		String targetCoinName = request.getParameter("targetCoinName");
		String quantity = request.getParameter("quantity");
		
		if (StringUtil.isEmpty(quantity)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "请输入正确的兑换数量");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!TypeParse.IsDouble(quantity)) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, "请输入正确的兑换数量");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BigDecimal toAmount = new BigDecimal(quantity);	// 用户兑换的币个数
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeName(originCoinName); // 源币种
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeName(targetCoinName); // 目标币种
		
		BigDecimal fromAmount = BigDecimal.ZERO;
		BigDecimal price = BigDecimal.ZERO;
		boolean check = false;
		BigDecimal frozenRate = BigDecimal.ZERO; // 冻结资金率
		BigDecimal frozee = BigDecimal.ZERO; // 冻结资金
		BigDecimal balance = BigDecimal.ZERO; // 对应币种账户余额
		if ("USDT".equals(originCoinName.toUpperCase()) || "USDT".equals(targetCoinName.toUpperCase())) {
			BQExchangeSetting setting = null;
			price = getBuyOrSellPrice(originCoin, targetCoin);
			if ("USDT".equals(originCoin.getType_name().toUpperCase())) {
				setting = bqExchangeSettingService.readExchangeSettingBySymbol(targetCoin.getType_name());
				if (setting == null) {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "未知错误，请稍候重试，或联系客服");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (toAmount.compareTo(setting.getMin_amount()) < 0) {
					String errorStr = "最小兑换数量" + setting.getMin_amount().doubleValue() + targetCoin.getType_name();
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, errorStr);
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				fromAmount = toAmount.multiply(price).setScale(4, BigDecimal.ROUND_UP);
				
				frozenRate = setting.getFrozen_rate().multiply(new BigDecimal(0.01));
				frozee = fromAmount.multiply(frozenRate.add(new BigDecimal(1)));
				balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc();
				if (balance.compareTo(frozee) >= 0) {
					check = true;
				}
			} else {
				setting = bqExchangeSettingService.readExchangeSettingBySymbol(originCoin.getType_name());
				if (setting == null) {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "未知错误，请稍候重试，或联系客服");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				fromAmount = toAmount.divide(price, 4, BigDecimal.ROUND_UP);
				
				if (fromAmount.compareTo(setting.getMin_amount()) < 0) {
					String errorStr = "最小兑换数量" + setting.getMin_amount().doubleValue() + originCoin.getType_name();
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, errorStr);
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc();
				if (balance.compareTo(fromAmount) >= 0) {
					check = true;
				}
			}
		} else if ("BFC".equals(originCoinName.toUpperCase()) || "BFC".equals(targetCoinName.toUpperCase())) {
			BQExchangeBfcSetting bfcSetting = null;
			if ("BFC".equals(originCoinName)) {
				bfcSetting = bqExchangeBfcSettingService.readBySymbol(targetCoin.getType_name());
				if (bfcSetting == null) {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "未知错误，请稍候重试，或联系客服");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (new BigDecimal(toAmount.intValue()).compareTo(toAmount) != 0) {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "只接受整数兑换");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}

				if (toAmount.compareTo(bfcSetting.getMin_amount()) < 0) {
					String errorStr = "最小兑换数量" + bfcSetting.getMin_amount().doubleValue() + targetCoin.getType_name();
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, errorStr);
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				price = bfcSetting.getExchange_rate();
				fromAmount = toAmount.multiply(price).setScale(4, BigDecimal.ROUND_UP);
			} else {
				bfcSetting = bqExchangeBfcSettingService.readBySymbol(originCoin.getType_name());
				if (bfcSetting == null) {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "未知错误，请稍候重试，或联系客服");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				price = bfcSetting.getExchange_rate();
				fromAmount = toAmount.divide(price, 4, BigDecimal.ROUND_UP);
				
				if (fromAmount.compareTo(bfcSetting.getMin_amount()) < 0) {
					String errorStr = "最小兑换数量" + bfcSetting.getMin_amount().doubleValue() + targetCoin.getType_name();
					String tradesStr = createJwtCode(token, Contents.API_ERROR_PARAM, errorStr);
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (new BigDecimal(fromAmount.intValue()).compareTo(fromAmount) != 0) {
					String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "只接受整数兑换");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
			}
			
			balance = bqAccountService.readAccount(user.getF_user_id(), originCoin.getType_id()).getBtc();
			if (balance.compareTo(fromAmount) >= 0) {
				check = true;
			}
		}
		
		if (!check) {
			String tradesStr = createJwtCode(token, Contents.API_ERROR_UNKNOW, "余额不足");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		Map<String, Object> exchangeMap = new HashMap<String, Object>();
		exchangeMap.put("fromAmount", fromAmount.doubleValue());
		exchangeMap.put("frozee", frozee.doubleValue());
		
		Gson gson = new Gson();
		userJson.put("data", new JSONObject(gson.toJson(exchangeMap)));
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}

}
