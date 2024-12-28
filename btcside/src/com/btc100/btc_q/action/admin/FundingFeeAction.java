package com.btc100.btc_q.action.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQFundingFeeSetting;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.FundingFeeRate;
import com.btc100.btc_q.service.BQFundingFeeSettingService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.FundingFeeRateService;
import com.btc100.btc_q.service.FundingFeeRecordService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping(value = "/managerFundingFee")
public class FundingFeeAction extends BaseAction {

	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQFundingFeeSettingService bqFundingFeeSettingService;
	@Autowired
	private FundingFeeRecordService fundingFeeRecordService;
	@Autowired
	private FundingFeeRateService fundingFeeRateService;
	
	@RequestMapping(value = "/list")
	public String list(String code, Long userId,  Model mo) {
		
		List<BQFundingFeeSetting> settings = bqFundingFeeSettingService.readAll();
		
		if (StringUtil.isEmpty(code)) {
			if (settings.size() > 0) code = settings.get(0).getF_code();
		}
		mo.addAttribute("settings", settings);
		mo.addAttribute("code", code);
		mo.addAttribute("userId", userId);
		
		if (userId == null) {
			userId = 0L;
		}
		
		PagerModel pm = new PagerModel();
		if (!StringUtil.isEmpty(code)) {
			pm = fundingFeeRecordService.readBySymbol(code, userId);
		}
		
		mo.addAttribute("pm", pm);
		
		return super.url_JSP("fundingFeeList");
	}

	@RequestMapping(value = "/fundingFeeSettingManager")
	public String fundingFeeSettingManager(String code, Model mo) {
		BQTransaction currTrans = null;
		
		List<BQTransaction> transList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transList == null) {
			transList = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		mo.addAttribute("transList", transList);
		if (!StringUtil.isEmpty(code)) {
			for	(BQTransaction trans :transList) {
				if (trans.getTransaction_code().equals(code)) {
					currTrans = trans;
				}
			}
		}
		
		if (currTrans == null && transList.size() > 0) {
			currTrans = transList.get(0);
		}
		
		if (currTrans == null) {
			return super.url_JSP("fundingFeeSettingManager");
		}
		
		mo.addAttribute("current", currTrans);
		
		BQFundingFeeSetting setting = bqFundingFeeSettingService.getFundingFeeSettingByCode(currTrans.getTransaction_code());
		if (setting == null) {
			setting = new BQFundingFeeSetting();
			setting.setF_code(currTrans.getTransaction_code());
		}
		
		mo.addAttribute("setting", setting);
		return super.url_JSP("fundingFeeSettingManager");
	}
	
	@RequestMapping(value = "/updateFundingFeeSetting", method = RequestMethod.POST)
	public String updateFundingFeeSetting(BQFundingFeeSetting setting, Model mo) {
		updateFundingFeeSetting(setting);
		return super.redirect("/managerFundingFee/fundingFeeSettingManager.do?code=" + setting.getF_code());
	}

	private void updateFundingFeeSetting(BQFundingFeeSetting setting) {
		if (setting == null) return;
		
		if (StringUtil.isEmpty(setting.getF_code())) {
			return;
		}
		
		boolean isChangeFixed = false;
		BQFundingFeeSetting oldSetting = bqFundingFeeSettingService.getFundingFeeSettingByCode(setting.getF_code());
		if (oldSetting != null && (oldSetting.getFixed_fee_open() != setting.getFixed_fee_open() 
				|| oldSetting.getFixed_fee_rate().compareTo(setting.getFixed_fee_rate()) != 0)) {
			isChangeFixed = true;
		}
		
		if (setting.getId() == null) {
			bqFundingFeeSettingService.save(setting);
		} else {
			oldSetting.setRunning(setting.getRunning());
			oldSetting.setRunning_time(setting.getRunning_time());
			oldSetting.setInterest_rate_diff(setting.getInterest_rate_diff());
			oldSetting.setDivisor(setting.getDivisor());
			oldSetting.setClamp_down(setting.getClamp_down());
			oldSetting.setClamp_up(setting.getClamp_up());
			oldSetting.setFixed_fee_open(setting.getFixed_fee_open());
			oldSetting.setFixed_fee_rate(setting.getFixed_fee_rate());
			
			bqFundingFeeSettingService.update(oldSetting);
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
			
			if (intervalList.size() == 0) return;
			
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
	}
}
