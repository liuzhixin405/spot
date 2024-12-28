package com.btc100.btc_q.action.api;

import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.util.TimeUtil;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.service.BQFundingFeeSettingService;
import com.btc100.btc_q.service.FundingFeeRateService;
import com.btc100.btc_q.service.NFundingFeeRateService;
import com.btc100.btc_q.service.NFundingFeeSettingService;
import com.btc100.btc_q.service.VFundingFeeRateService;
import com.btc100.btc_q.service.VFundingFeeSettingService;
import com.btc100.btc_q.util.StringUtil;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/fundingFeeRateApi")
public class FundingFeeRateApiAction  extends BaseApiAction {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQFundingFeeSettingService bqFundingFeeSettingService;
	@Autowired
	private FundingFeeRateService fundingFeeRateService;
	@Autowired
	private VFundingFeeSettingService vFundingFeeSettingService;
	@Autowired
	private VFundingFeeRateService vFundingFeeRateService;
	@Autowired
	private NFundingFeeSettingService nFundingFeeSettingService;
	@Autowired
	private NFundingFeeRateService nFundingFeeRateService;
	
	@RequestMapping("/feeRateSymbols")
	public String feeRateSymbols(HttpServletRequest request, HttpServletResponse response,  Model model) throws Exception {

		JSONArray array = new JSONArray();
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		String type = request.getParameter("type");
		if ("vcontract".equals(type)) {
			List<VFundingFeeSetting> settings = spyMemcachedClient.get(BTCQMemecacheKeys.V_FUNDING_FEE_SETTINGS);
			if (settings == null) {
				settings = vFundingFeeSettingService.readAll();
				if (settings != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.V_FUNDING_FEE_SETTINGS, BTCQMemecacheKeys.TIMES.TEN_SECOND, settings);
				}
			}
			
			if (settings == null) {
				settings = new ArrayList<VFundingFeeSetting>(); 
			}
			
			for (int i = 0; i < settings.size(); i++) {
				VFundingFeeSetting setting = settings.get(i);
				array.add(setting.getF_code());
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			List<BQFundingFeeSetting> settings = spyMemcachedClient.get(BTCQMemecacheKeys.FUNDING_FEE_SETTINGS);
			if (settings == null) {
				settings = bqFundingFeeSettingService.readAll();
				if (settings != null) {
					spyMemcachedClient.set(BTCQMemecacheKeys.FUNDING_FEE_SETTINGS, BTCQMemecacheKeys.TIMES.TEN_SECOND, settings);
				}
			}
			
			if (settings == null) {
				settings = new ArrayList<BQFundingFeeSetting>(); 
			}
			
			for (int i = 0; i < settings.size(); i++) {
				BQFundingFeeSetting setting = settings.get(i);
				array.add(setting.getF_code());
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NFundingFeeSetting> settings = spyMemcachedClient.get(BTCQMemecacheKeys.FUNDING_FEE_SETTINGS + "_" + type);
				if (settings == null) {
					settings = nFundingFeeSettingService.readAll(type);
					if (settings != null) {
						spyMemcachedClient.set(BTCQMemecacheKeys.FUNDING_FEE_SETTINGS + "_" + type, BTCQMemecacheKeys.TIMES.TEN_SECOND, settings);
					}
				}
				
				if (settings == null) {
					settings = new ArrayList<NFundingFeeSetting>(); 
				}
				
				for (int i = 0; i < settings.size(); i++) {
					NFundingFeeSetting setting = settings.get(i);
					array.add(setting.getF_code());
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		
		return apiData(response);
	}
	
	@RequestMapping("/feeRate")
	public String feeRate(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONArray array = new JSONArray();
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			symbol = "btc";
		}
		
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sizeStr = request.getParameter("size");
		
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
		}
		
		long start = Convert.toLong(startTime, 0);
		long end = Convert.toLong(endTime, 0);
		int limit = Convert.toInt(sizeStr, 10);
		if (limit > 100) limit = 100;
		
		long fromL = Convert.toLong(from, 0);
		
		String type = request.getParameter("type");
		if ("vcontract".equals(type)) {
			List<VFundingFeeRate> rates = vFundingFeeRateService.readList(symbol, fromL, direct, start, end, limit);
			for (int i = 0; i < rates.size(); i++) {
				VFundingFeeRate rate = rates.get(i);
				JSONObject obj = new JSONObject();
				obj.put("recordId", rate.getId());
				obj.put("time", rate.getDateline());
				obj.put("symbol", rate.getF_code());
				obj.put("feeRate", rate.getFee_rate().doubleValue());
				
				array.add(obj);
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			List<FundingFeeRate> rates = fundingFeeRateService.readList(symbol, fromL, direct, start, end, limit);
			for (int i = 0; i < rates.size(); i++) {
				FundingFeeRate rate = rates.get(i);
				JSONObject obj = new JSONObject();
				obj.put("recordId", rate.getId());
				obj.put("time", rate.getDateline());
				obj.put("symbol", rate.getF_code());
				obj.put("feeRate", rate.getFee_rate().doubleValue());
				
				array.add(obj);
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NFundingFeeRate> rates = nFundingFeeRateService.readList(type, symbol, fromL, direct, start, end, limit);
				for (int i = 0; i < rates.size(); i++) {
					NFundingFeeRate rate = rates.get(i);
					JSONObject obj = new JSONObject();
					obj.put("recordId", rate.getId());
					obj.put("time", rate.getDateline());
					obj.put("symbol", rate.getF_code());
					obj.put("feeRate", rate.getFee_rate().doubleValue());
					
					array.add(obj);
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		
		return apiData(response);
	}
	
	@RequestMapping("/singleFeeRate")
	public String singleFeeRate(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			symbol = "btc";
		}
		
		JSONObject obj = new JSONObject();
		BigDecimal rate = BigDecimal.ZERO;
		
		String type = request.getParameter("type");
		if ("vcontract".equals(type)) {
			rate = spyMemcachedClient.get(BTCQMemecacheKeys.V_FUNDING_FEE_RATE + symbol);
			if (rate == null) {
				VFundingFeeSetting feeRateSetting = vFundingFeeSettingService.getFundingFeeSettingByCode(symbol);
				if (feeRateSetting != null && feeRateSetting.getRunning()) {
					VFundingFeeRate feeRate = vFundingFeeRateService.readLast(symbol);
					if (feeRate != null) {
						rate = feeRate.getFee_rate();
						if (rate != null) {
							if (rate.compareTo(feeRateSetting.getClamp_up().multiply(new BigDecimal(0.01))) > 0) {
								rate = feeRateSetting.getClamp_up().multiply(new BigDecimal(0.01));
							} else if (rate.compareTo(feeRateSetting.getClamp_down().multiply(new BigDecimal(0.01))) < 0) {
								rate = feeRateSetting.getClamp_down().multiply(new BigDecimal(0.01));
							}
							
							spyMemcachedClient.set(BTCQMemecacheKeys.V_FUNDING_FEE_RATE + symbol, BTCQMemecacheKeys.TIMES.FIVE_MIN, rate);
						}
					}
				}
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			rate = spyMemcachedClient.get(BTCQMemecacheKeys.FUNDING_FEE_RATE + symbol);
			if (rate == null) {
				BQFundingFeeSetting feeRateSetting = bqFundingFeeSettingService.getFundingFeeSettingByCode(symbol);
				if (feeRateSetting != null && feeRateSetting.getRunning()) {
					FundingFeeRate feeRate = fundingFeeRateService.readLast(symbol);
					if (feeRate != null) {
						rate = feeRate.getFee_rate();
						if (rate != null) {
							if (rate.compareTo(feeRateSetting.getClamp_up().multiply(new BigDecimal(0.01))) > 0) {
								rate = feeRateSetting.getClamp_up().multiply(new BigDecimal(0.01));
							} else if (rate.compareTo(feeRateSetting.getClamp_down().multiply(new BigDecimal(0.01))) < 0) {
								rate = feeRateSetting.getClamp_down().multiply(new BigDecimal(0.01));
							}
							
							spyMemcachedClient.set(BTCQMemecacheKeys.FUNDING_FEE_RATE + symbol, BTCQMemecacheKeys.TIMES.FIVE_MIN, rate);
						}
					}
				}
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				rate = spyMemcachedClient.get(BTCQMemecacheKeys.FUNDING_FEE_RATE + type + "_" + symbol);
				if (rate == null) {
					NFundingFeeSetting feeRateSetting = nFundingFeeSettingService.getFundingFeeSettingByCode(type, symbol);
					if (feeRateSetting != null && feeRateSetting.getRunning()) {
						NFundingFeeRate feeRate = nFundingFeeRateService.readLast(type, symbol);
						if (feeRate != null) {
							rate = feeRate.getFee_rate();
							if (rate != null) {
								if (rate.compareTo(feeRateSetting.getClamp_up().multiply(new BigDecimal(0.01))) > 0) {
									rate = feeRateSetting.getClamp_up().multiply(new BigDecimal(0.01));
								} else if (rate.compareTo(feeRateSetting.getClamp_down().multiply(new BigDecimal(0.01))) < 0) {
									rate = feeRateSetting.getClamp_down().multiply(new BigDecimal(0.01));
								}
								
								spyMemcachedClient.set(BTCQMemecacheKeys.FUNDING_FEE_RATE + type + "_" + symbol, BTCQMemecacheKeys.TIMES.FIVE_MIN, rate);
							}
						}
					}
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		if (rate != null && rate.compareTo(BigDecimal.ZERO) > 0) {
			obj.put("symbol", symbol);
			obj.put("feeRate", rate.doubleValue());
			tickerJson.put("data", obj);
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "暂无资金费率");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		model.addAttribute("ticker", tickerJson);
		
		return apiData(response);
	}
	
	@RequestMapping("/getRunningTime")
	public String getRunningTime(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String type = request.getParameter("type");
		if ("vcontract".equals(type)) {
			VFundingFeeSetting fundingFeeSetting = vFundingFeeSettingService.getFundingFeeSettingByCode(symbol);
			if (fundingFeeSetting == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol错误，无该设置");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (!fundingFeeSetting.getRunning()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "已暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			JSONObject obj = new JSONObject();
			obj.put("time", fundingFeeSetting.getRunning_time());
			
			tickerJson.put("data", obj);
			tickerJson.put("time", fundingFeeSetting.getRunning_time());
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			BQFundingFeeSetting fundingFeeSetting = bqFundingFeeSettingService.getFundingFeeSettingByCode(symbol);
			if (fundingFeeSetting == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol错误，无该设置");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if (!fundingFeeSetting.getRunning()) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "已暂停");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			JSONObject obj = new JSONObject();
			obj.put("time", fundingFeeSetting.getRunning_time());
			
			tickerJson.put("data", obj);
			tickerJson.put("time", fundingFeeSetting.getRunning_time());
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NFundingFeeSetting fundingFeeSetting = nFundingFeeSettingService.getFundingFeeSettingByCode(type, symbol);
				if (fundingFeeSetting == null) {
					String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol错误，无该设置");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				if (!fundingFeeSetting.getRunning()) {
					String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "已暂停");
					model.addAttribute("ticker", tradesStr);
					return apiData(response);
				}
				
				JSONObject obj = new JSONObject();
				obj.put("time", fundingFeeSetting.getRunning_time());
				
				tickerJson.put("data", obj);
				tickerJson.put("time", fundingFeeSetting.getRunning_time());
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		model.addAttribute("ticker", tickerJson);
		
		return apiData(response);
	}

	@RequestMapping(value = "/updateFundingFeeSetting", method = RequestMethod.POST)
	public String updateFundingFeeSetting(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");

		String type = request.getParameter("type");
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}

		String symbol = request.getParameter("symbol");
		int running = Convert.toInt(request.getParameter("running"), -1);
		String running_time = request.getParameter("running_time");
		int fixed_fee_open = Convert.toInt(request.getParameter("fixed_fee_open"), -1);
		String interest_rate_diff = request.getParameter("interest_rate_diff");
		String divisor = request.getParameter("divisor");
		String clamp_up = request.getParameter("clamp_up");
		String clamp_down = request.getParameter("clamp_down");
		String fixed_fee_rate = request.getParameter("fixed_fee_rate");

		if(StringUtil.isEmpty(interest_rate_diff) || StringUtil.isEmpty(divisor) || StringUtil.isEmpty(clamp_up) || StringUtil.isEmpty(clamp_down) || StringUtil.isEmpty(fixed_fee_rate) || StringUtil.isEmpty(symbol) || StringUtil.isEmpty(running_time) || (running != 1 && running != 0) || (fixed_fee_open != 1 && fixed_fee_open != 0)){
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知参数");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		map.put("symbol", symbol);
		map.put("running", String.valueOf(running));
		map.put("running_time", running_time);
		map.put("fixed_fee_open", String.valueOf(fixed_fee_open));
		map.put("interest_rate_diff", interest_rate_diff);
		map.put("divisor", divisor);
		map.put("clamp_up", clamp_up);
		map.put("clamp_down", clamp_down);
		map.put("fixed_fee_rate", fixed_fee_rate);

		long userId = 0;
		String checkKeyAndSign = checkAppKeyAndSign(request, map);
		if (!StringUtil.isEmpty(checkKeyAndSign)) {
			model.addAttribute("ticker", checkKeyAndSign);
			return apiData(response);
		}
		userId = Convert.toLong(map.get("userId"), 0);

		if (8670 != userId){
			String tradesStr = createErrorCode(Contents.API_ERROR_APPKEYDISABLED, "该用户没有权限");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		boolean isChangeFixed = false;
		boolean isRunning = running != 0;
		boolean isFixed_fee_open = fixed_fee_open != 0;

		if(type == null || "".equals(type) || "contract".equals(type)) {
			BQFundingFeeSetting setting = bqFundingFeeSettingService.getFundingFeeSettingByCode(symbol);
			if (setting != null && (setting.getFixed_fee_open() != isFixed_fee_open
					|| setting.getFixed_fee_rate().compareTo(new BigDecimal(fixed_fee_rate)) != 0)) {
				isChangeFixed = true;
			}
			
			if (setting == null) {
				setting = new BQFundingFeeSetting();
			}
			setting.setF_code(symbol);
			setting.setRunning(isRunning);
			setting.setRunning_time(running_time);
			setting.setInterest_rate_diff(new BigDecimal(interest_rate_diff));
			setting.setDivisor(new BigDecimal(divisor));
			setting.setClamp_up(new BigDecimal(clamp_up));
			setting.setClamp_down(new BigDecimal(clamp_down));
			setting.setFixed_fee_open(isFixed_fee_open);
			setting.setFixed_fee_rate(new BigDecimal(fixed_fee_rate));

			if (setting.getId() == null) {
				bqFundingFeeSettingService.save(setting);
			} else {
				bqFundingFeeSettingService.update(setting);
			}

			if (isChangeFixed) {
				long currTime = (System.currentTimeMillis() / 1000 + 1) / 60 * 60;
				String timeStr = setting.getRunning_time();
				String[] intervals = timeStr.split(",");
				List<Long> intervalList = new ArrayList<Long>();
				for (String interval : intervals) {
					int hour = Convert.toInt(interval.split(":")[0], -1);
					if (hour < 0) continue;

					int minute = Convert.toInt(interval.split(":")[1], -1);
					if (minute < 0) minute = 0;

					long morningTime = TimeUtil.getTimesmorning();
					long intervalTime = morningTime + hour * 3600 + minute * 60;
					if (intervalTime >= currTime) {
						intervalList.add(intervalTime);
					}
				}

				if (intervalList.size() == 0) {
					for (String interval : intervals) {
						int hour = Convert.toInt(interval.split(":")[0], -1);
						if (hour < 0) continue;

						int minute = Convert.toInt(interval.split(":")[1], -1);
						if (minute < 0) minute = 0;

						long morningTime = TimeUtil.getTimesmorning();
						long intervalTime = morningTime + hour * 3600 + minute * 60 + 24 * 3600;
						if (intervalTime >= currTime) {
							intervalList.add(intervalTime);
						}
					}
				}

				if (intervalList.size() == 0) {
					model.addAttribute("ticker", tickerJson);
					return apiData(response);
				}

				// 获取最小那个
				long minTime = intervalList.get(0);
				for (Long time : intervalList) {
					if (time < minTime) {
						minTime = time;
					}
				}
				FundingFeeRate feeRate = fundingFeeRateService.getByTime(setting.getF_code(), minTime);
				if (feeRate != null) {
					if (setting.getFixed_fee_open()) {
						fundingFeeRateService.updateRate(feeRate, setting.getFixed_fee_rate(), 1);
					} else {
						fundingFeeRateService.deleteFeeRate(feeRate);
					}
				} else {
					if (setting.getFixed_fee_open()) {
						feeRate = new FundingFeeRate();
						feeRate.setF_code(setting.getF_code());
						feeRate.setCount_times(1);
						feeRate.setDateline(minTime);
						feeRate.setDone(0);
						feeRate.setFee_rate(setting.getFixed_fee_rate());

						fundingFeeRateService.saveFeeRate(feeRate);
					}
				}
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				NFundingFeeSetting nsetting = nFundingFeeSettingService.getFundingFeeSettingByCode(part.getPart(), symbol);
				if (nsetting != null && (nsetting.getFixed_fee_open() != isFixed_fee_open
						|| nsetting.getFixed_fee_rate().compareTo(new BigDecimal(fixed_fee_rate)) != 0)) {
					isChangeFixed = true;
				}
				
				if (nsetting == null) {
					nsetting = new NFundingFeeSetting();
				}
				nsetting.setF_code(symbol);
				nsetting.setRunning(isRunning);
				nsetting.setRunning_time(running_time);
				nsetting.setInterest_rate_diff(new BigDecimal(interest_rate_diff));
				nsetting.setDivisor(new BigDecimal(divisor));
				nsetting.setClamp_up(new BigDecimal(clamp_up));
				nsetting.setClamp_down(new BigDecimal(clamp_down));
				nsetting.setFixed_fee_open(isFixed_fee_open);
				nsetting.setFixed_fee_rate(new BigDecimal(fixed_fee_rate));

				if (nsetting.getId() == null) {
					nFundingFeeSettingService.save(part.getPart(), nsetting);
				} else {
					nFundingFeeSettingService.update(part.getPart(), nsetting);
				}

				if (isChangeFixed) {
					long currTime = (System.currentTimeMillis() / 1000 + 1) / 60 * 60;
					String timeStr = nsetting.getRunning_time();
					String[] intervals = timeStr.split(",");
					List<Long> intervalList = new ArrayList<Long>();
					for (String interval : intervals) {
						int hour = Convert.toInt(interval.split(":")[0], -1);
						if (hour < 0) continue;

						int minute = Convert.toInt(interval.split(":")[1], -1);
						if (minute < 0) minute = 0;

						long morningTime = TimeUtil.getTimesmorning();
						long intervalTime = morningTime + hour * 3600 + minute * 60;
						if (intervalTime >= currTime) {
							intervalList.add(intervalTime);
						}
					}

					if (intervalList.size() == 0) {
						for (String interval : intervals) {
							int hour = Convert.toInt(interval.split(":")[0], -1);
							if (hour < 0) continue;

							int minute = Convert.toInt(interval.split(":")[1], -1);
							if (minute < 0) minute = 0;

							long morningTime = TimeUtil.getTimesmorning();
							long intervalTime = morningTime + hour * 3600 + minute * 60 + 24 * 3600;
							if (intervalTime >= currTime) {
								intervalList.add(intervalTime);
							}
						}
					}

					if (intervalList.size() == 0) {
						model.addAttribute("ticker", tickerJson);
						return apiData(response);
					}

					// 获取最小那个
					long minTime = intervalList.get(0);
					for (Long time : intervalList) {
						if (time < minTime) {
							minTime = time;
						}
					}

					NFundingFeeRate feeRate = nFundingFeeRateService.getByTime(part.getPart(), nsetting.getF_code(), minTime);
					if (feeRate != null) {
						if (nsetting.getFixed_fee_open()) {
							nFundingFeeRateService.updateRate(part.getPart(), feeRate, nsetting.getFixed_fee_rate(), 1);
						} else {
							nFundingFeeRateService.deleteFeeRate(part.getPart(), feeRate);
						}
					} else {
						if (nsetting.getFixed_fee_open()) {
							feeRate = new NFundingFeeRate();
							feeRate.setF_code(nsetting.getF_code());
							feeRate.setCount_times(1);
							feeRate.setDateline(minTime);
							feeRate.setDone(0);
							feeRate.setFee_rate(nsetting.getFixed_fee_rate());

							nFundingFeeRateService.saveFeeRate(part.getPart(), feeRate);
						}
					}
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
}
