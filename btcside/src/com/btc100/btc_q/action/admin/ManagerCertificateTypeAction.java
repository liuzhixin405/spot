package com.btc100.btc_q.action.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.entity.BQCertificateType;
import com.btc100.btc_q.service.BQCertificateTypeService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerCertificateType")
public class ManagerCertificateTypeAction extends BaseAction {
	
	@Autowired
	private BQCertificateTypeService bqCertificateTypeService;

	@RequestMapping("/load")
	public String load(Model model) {
		PagerModel pm = bqCertificateTypeService.readAllTypes();
		model.addAttribute("pm", pm);
		
		return super.url_JSP("certificateTypelist");
	}
	
	@RequestMapping("/get")
	public String get(int typeId, Model model) {
		BQCertificateType type = bqCertificateTypeService.readById(typeId);
		if (type == null) {
			type = new BQCertificateType();
		}
		
		model.addAttribute("certificateType", type);
		
		return super.url_JSP("certificateTypeedit");
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(BQCertificateType type, Model mo) {

		if (StringUtil.isEmpty(type.getCertificate_name())) {
			return super.redirect("/managerCertificateType/load.do");
		}
		
		if (StringUtil.isEmpty(type.getId()) || type.getId() <= 0) {
			type.setCreate_time(System.currentTimeMillis() / 1000);
			bqCertificateTypeService.save(type);
		} else {
			bqCertificateTypeService.update(type);
		}
		
		return super.redirect("/managerCertificateType/load.do");
	}
	
	@RequestMapping("/delete")
	public String deleteLevel(String typeId) {
		long id = ConvertUtil.toLong(typeId, 0l);
		BQCertificateType type = bqCertificateTypeService.readById(id);
		bqCertificateTypeService.delete(type);
		return super.redirect("/managerCertificateType/load.do");
	}

}
