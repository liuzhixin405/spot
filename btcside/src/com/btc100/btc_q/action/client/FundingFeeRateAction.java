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
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQFundingFeeSetting;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NFundingFeeSetting;
import com.btc100.btc_q.service.BQFundingFeeSettingService;
import com.btc100.btc_q.service.FundingFeeRateService;
import com.btc100.btc_q.service.NContractPartitionService;
import com.btc100.btc_q.service.NFundingFeeRateService;
import com.btc100.btc_q.service.NFundingFeeSettingService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/fundingFee")
public class FundingFeeRateAction extends BaseAction  {
	
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQFundingFeeSettingService bqFundingFeeSettingService;
	@Autowired
	private FundingFeeRateService fundingFeeRateService;
	@Autowired
	private NContractPartitionService nContractPartitionService;
	@Autowired
	private NFundingFeeSettingService nFundingFeeSettingService;
	@Autowired
	private NFundingFeeRateService nFundingFeeRateService;
	
	@RequestMapping("/feeRate")
	public String feeRate(HttpServletRequest request, Model model) {
		
		String symbol = request.getParameter("symbol");
		String partition = "usdt";
		List<NContractPartition> partitions = new ArrayList<>();
		NContractPartition usdtPart = new NContractPartition();
		usdtPart.setPart("usdt");
		usdtPart.setCurrency_type_id(Contents.TRADE_CURRENCY_TYPE_ID);
		partitions.add(0, usdtPart);
		
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

		PagerModel pm = null;
		if (StringUtil.isEmpty(symbol)) {
			symbol = "btc";
		}

		boolean have = false;
		for (BQFundingFeeSetting setting : settings) {
			if (setting.getF_code().equals(symbol)) {
				have = true;
				break;
			}
		}

		if (have) {
			pm = fundingFeeRateService.readBySymbol(symbol);
		} else {
			if (settings.size() > 0) symbol = settings.get(0).getF_code();
			pm = fundingFeeRateService.readBySymbol(symbol);
		}

		if (pm == null) {
			pm = new PagerModel();
		}

		model.addAttribute("pm", pm);
		model.addAttribute("settings", settings);

		model.addAttribute("partitions", partitions);
		model.addAttribute("partition", partition);
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
		
		BQFundingFeeSetting fundingFeeSetting = bqFundingFeeSettingService.getFundingFeeSettingByCode(symbol);
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
