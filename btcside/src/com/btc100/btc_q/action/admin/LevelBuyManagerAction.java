package com.btc100.btc_q.action.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMemberLevelBuySetting;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberLevelBuySettingService;

@Controller
@RequestMapping("/managerLevelBuy")
public class LevelBuyManagerAction extends BaseAction {
	
	@Autowired
	private BQMemberLevelBuySettingService bqMemberLevelBuySettingService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	
	@RequestMapping("/setting")
	public String setting(HttpServletRequest request, Model mo) {
		
		BQMemberLevelBuySetting setting = bqMemberLevelBuySettingService.readById();
		if (setting == null) {
			setting = new BQMemberLevelBuySetting();
		}
		
		List<BQCurrencyType> coins = bqCurrencyTypeService.readAll();
		mo.addAttribute("setting", setting);
		mo.addAttribute("coins", coins);
		
		return super.url_JSP("levelBuySetting");
	}

	@RequestMapping("/update")
	public String update(HttpServletRequest request, BQMemberLevelBuySetting setting, Model mo) {
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(setting.getPay_coin_id());
		setting.setPay_coin_name(currencyType.getType_name());
		if (setting.getId() == 0) {
			bqMemberLevelBuySettingService.save(setting);
		} else {
			bqMemberLevelBuySettingService.update(setting);
		}
		
		return super.redirect("/managerLevelBuy/setting.do");
	}
}
