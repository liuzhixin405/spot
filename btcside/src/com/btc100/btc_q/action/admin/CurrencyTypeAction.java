package com.btc100.btc_q.action.admin;

import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/managerCurrencyType")
public class CurrencyTypeAction extends BaseAction {
	
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService; 

	@RequestMapping("/load")
	public String load(Model model) {
		PagerModel pm = bqCurrencyTypeService.readAllTypes();
		model.addAttribute("pm", pm);
		
		return super.url_JSP("currencyTypelist");
	}
	
	@RequestMapping("/get")
	public String get(int typeId, Model model) {
		BQCurrencyType type = bqCurrencyTypeService.readByTypeId(typeId);
		if (type == null) {
			type = new BQCurrencyType();
		}
		
		model.addAttribute("currencyType", type);
		
		return super.url_JSP("currencyTypeedit");
	}


	@RequestMapping("/updateRecharge")
	public String get(int id, int type, int state) {
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(id);
		if (currencyType != null) {
			bqCurrencyTypeService.updateRecharge(id, type == 1, state == 1);
		}

		return super.redirect("/managerCurrencyType/load.do");
	}

	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(BQCurrencyType type, Model mo) {

		if (StringUtil.isEmpty(type.getType_name())) {
			return super.redirect("/managerCurrencyType/load.do");
		}
		
		if (type.getType_id() == null || type.getType_id() <= 0) {
			type.setType_id(null);
			bqCurrencyTypeService.save(type);
		} else {
			bqCurrencyTypeService.update(type);
		}
		
		return super.redirect("/managerCurrencyType/load.do");
	}

}
