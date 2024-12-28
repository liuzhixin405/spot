package com.btc100.btc_q.action.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.entity.DeliveryQuantSetting;
import com.btc100.btc_q.service.DeliveryQuantSettingService;

@Controller
@RequestMapping("/managerDeliveryQuantSetting")
public class DeliveryQuantSettingAction extends BaseAction {
	
	@Autowired
	private DeliveryQuantSettingService bqQuantSettingService;
	
	@RequestMapping(value = "/load")
	public String getQuantSetting(Model mo) {
		DeliveryQuantSetting setting = bqQuantSettingService.get();
		if (setting == null) {
			setting = new DeliveryQuantSetting();
		}
		
		mo.addAttribute("setting", setting);
		
		return super.url_JSP("deliveryQuantSettingManager");
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateQuantSetting(DeliveryQuantSetting setting, Model mo) {
		
		bqQuantSettingService.update(setting);
		
		return super.redirect("/managerDeliveryQuantSetting/load.do");
	}
}
