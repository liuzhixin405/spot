package com.btc100.btc_q.action.client;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.VFundingFeeSetting;
import com.btc100.btc_q.service.VFundingFeeRateService;
import com.btc100.btc_q.service.VFundingFeeSettingService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/vfundingFee")
public class VFundingFeeRateAction extends BaseAction  {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VFundingFeeSettingService bqFundingFeeSettingService;
	@Autowired
	private VFundingFeeRateService fundingFeeRateService;
	
	@RequestMapping("/vFeeRate")
	public String feeRate(HttpServletRequest request, Model model) {
		
		List<VFundingFeeSetting> settings = spyMemcachedClient.get(BTCQMemecacheKeys.V_FUNDING_FEE_SETTINGS);
		if (settings == null) {
			settings = bqFundingFeeSettingService.readAll();
			if (settings != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.V_FUNDING_FEE_SETTINGS, BTCQMemecacheKeys.TIMES.TEN_SECOND, settings);
			}
		}
		
		if (settings == null) {
			settings = new ArrayList<VFundingFeeSetting>(); 
		}
		
		PagerModel pm = null;
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			symbol = "btc";
		}
		
		boolean have = false;
		for (VFundingFeeSetting setting : settings) {
			if (setting.getF_code().equals(symbol)) {
				have = true;
				break;
			}
		}
		
		if (have) {
			pm = fundingFeeRateService.readBySymbol(symbol);
		}
		
		if (pm == null) {
			pm = new PagerModel();
		}
		
		model.addAttribute("pm", pm);
		model.addAttribute("settings", settings);
		model.addAttribute("symbol", symbol);
		
		return "jsp/user_fundingFeeRate";
	}
	
	@RequestMapping("/getRunningTime")
	public String getRunningTime(HttpServletRequest request, Model model) throws JSONException {
		
		JSONObject tickerJson = new JSONObject();
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			tickerJson.put("code", "error");
			tickerJson.put("msg", "symbol错误");
			model.addAttribute("ticker", tickerJson);
			return "jsp/api/ticker";
		}
		
		VFundingFeeSetting fundingFeeSetting = bqFundingFeeSettingService.getFundingFeeSettingByCode(symbol);
		if (fundingFeeSetting == null) {
			tickerJson.put("code", "error");
			tickerJson.put("msg", "symbol错误,未找到该设置");
			model.addAttribute("ticker", tickerJson);
			return "jsp/api/ticker";
		}
		
		if (!fundingFeeSetting.getRunning()) {
			tickerJson.put("code", "error");
			tickerJson.put("msg", "已暂停");
			model.addAttribute("ticker", tickerJson);
			return "jsp/api/ticker";
		}
		
		tickerJson.put("code", "success");
		tickerJson.put("msg", "");
		tickerJson.put("time", fundingFeeSetting.getRunning_time());
		model.addAttribute("ticker", tickerJson);
		
		return "jsp/api/ticker";
	}
}
