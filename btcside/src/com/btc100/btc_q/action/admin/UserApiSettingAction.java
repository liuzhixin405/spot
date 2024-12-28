package com.btc100.btc_q.action.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQUserApiSetting;
import com.btc100.btc_q.service.BQUserApiSettingService;

@Controller
@RequestMapping("/managerUserApiSetting")
public class UserApiSettingAction extends BaseAction {

	@Autowired
	private BQUserApiSettingService bqUserApiSettingService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@RequestMapping(value = "/load")
	public String getQuantSetting(Model mo) {
		BQUserApiSetting setting = bqUserApiSettingService.readOne();
		if (setting == null) {
			setting = new BQUserApiSetting();
		}

		mo.addAttribute("setting", setting);

		return super.url_JSP("userApiSettingManager");
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateQuantSetting(BQUserApiSetting setting, Model mo) {

		bqUserApiSettingService.update(setting);
		spyMemcachedClient.set(BTCQMemecacheKeys.API_TRADE_WHITE_SETTING, BTCQMemecacheKeys.TIMES.FIVE_MIN, setting);

		return super.redirect("/managerUserApiSetting/load.do");
	}
}
