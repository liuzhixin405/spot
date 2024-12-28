package com.btc100.btc_q.action.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.entity.BQSpotQuantSetting;
import com.btc100.btc_q.service.BQSpotQuantSettingService;

@Controller
@RequestMapping("/managerSpotQuantSetting")
public class SpotQuantSettingAction extends BaseAction {
	
	@Autowired
	private BQSpotQuantSettingService bqQuantSettingService;
	
	@RequestMapping(value = "/load")
	public String getQuantSetting(Model mo) {
		BQSpotQuantSetting setting = bqQuantSettingService.get();
		if (setting == null) {
			setting = new BQSpotQuantSetting();
		}
		
		mo.addAttribute("setting", setting);
		
		return super.url_JSP("spotQuantSettingManager");
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateQuantSetting(BQSpotQuantSetting setting, Model mo) {
		
		bqQuantSettingService.update(setting);
		
		return super.redirect("/managerSpotQuantSetting/load.do");
	}
}
