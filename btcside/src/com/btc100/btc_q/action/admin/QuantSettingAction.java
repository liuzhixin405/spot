package com.btc100.btc_q.action.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.entity.BQQuantSetting;
import com.btc100.btc_q.service.BQQuantSettingService;

@Controller
@RequestMapping("/managerQuantSetting")
public class QuantSettingAction extends BaseAction {
	
	@Autowired
	private BQQuantSettingService bqQuantSettingService;
	
	@RequestMapping(value = "/load")
	public String getQuantSetting(Model mo) {
		BQQuantSetting app = bqQuantSettingService.get("");
		if (app == null) {
			app = new BQQuantSetting();
		}
		
		mo.addAttribute("setting", app);
		
		return super.url_JSP("quantSettingManager");
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateQuantSetting(BQQuantSetting setting, Model mo) {
		
		bqQuantSettingService.update(setting);
		
		return super.redirect("/managerQuantSetting/load.do");
	}
}
