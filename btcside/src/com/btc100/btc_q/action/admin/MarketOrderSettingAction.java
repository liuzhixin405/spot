package com.btc100.btc_q.action.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMarketOrderSetting;
import com.btc100.btc_q.service.BQMarketOrderSettingService;

@Controller
@RequestMapping("/managerMarketOrder")
public class MarketOrderSettingAction extends BaseAction {

	@Autowired
	private BQMarketOrderSettingService bqMarketOrderSettingService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@RequestMapping(value = "/load")
	public String getSetting(Model mo) {
		BQMarketOrderSetting setting = bqMarketOrderSettingService.read();
		if (setting == null) {
			setting = new BQMarketOrderSetting();
		}

		mo.addAttribute("setting", setting);

		return super.url_JSP("marketOrderSetting");
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateSetting(BQMarketOrderSetting setting, Model mo) {
		if (setting.getId() == 0) {
			bqMarketOrderSettingService.save(setting);
		} else {
			bqMarketOrderSettingService.update(setting);
		}
		
		spyMemcachedClient.set(BTCQMemecacheKeys.MARKET_ORDER_SETTING, BTCQMemecacheKeys.TIMES.ONE_DAY, setting);

		return super.redirect("/managerMarketOrder/load.do");
	}
}
