package com.btc100.btc_q.action.admin;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.entity.BQIndexDescription;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.service.BQIndexDescriptionService;
import com.btc100.btc_q.service.BQTransactionTypeService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping(value = "/managerIndexDesc")
public class ManagerIndexDescription extends BaseAction {

	@Autowired
	private BQTransactionTypeService bqTransTypeService;
	@Autowired
	private BQIndexDescriptionService bqIndexDescService;

	@RequestMapping(value = "/loadIndexDesc")
	public String loadIndexDesc(Model mo) {

		PagerModel pm = bqIndexDescService.readPagerModel();
		mo.addAttribute("pm", pm);

		return super.url_JSP("indexDescList");
	}
	
	@RequestMapping(value = "/loadAddIndexDesc")
	public String loadAddIndexDesc(String id, Model mo) {
		List<BQTransactionType> bqTransTypes = bqTransTypeService.readAllUsing();
		int indexId = ConvertUtil.toInt(id, 0);
		if (indexId > 0) {
			BQIndexDescription indexDesc = bqIndexDescService.readById(indexId);
			mo.addAttribute("indexDesc", indexDesc);
		}
		
		mo.addAttribute("bqTransTypes", bqTransTypes);
		return super.url_JSP("indexDescDetails");
	}

	@RequestMapping(value = "/addIndexDesc", method = RequestMethod.POST)
	public String addIndexDesc(Model mo, String id, String typeCode, String zhDesc, String enDesc, String jaDesc, String koDesc, String hkDesc) {

		if (typeCode == null || typeCode.isEmpty()) {
			return super.errorPage(mo, "缺少类型码");
		}
		
		if (zhDesc == null || zhDesc.isEmpty()) {
			return super.errorPage(mo, "中文说明不能为空");
		}
		
		if (enDesc == null || enDesc.isEmpty()) {
			return super.errorPage(mo, "英文说明不能为空");
		}
		
		if (jaDesc == null || jaDesc.isEmpty()) {
			return super.errorPage(mo, "日文说明不能为空");
		}
		
		if (koDesc == null || koDesc.isEmpty()) {
			return super.errorPage(mo, "韩文说明不能为空");
		}
		
		long now = (long) DateUtil.DateToUnix(new Date());
		BQIndexDescription bqIndexDesc = new BQIndexDescription();
		
		int indexId = ConvertUtil.toInt(id, 0);
		if (indexId > 0) {
			bqIndexDesc = bqIndexDescService.readById(indexId);
			bqIndexDesc.setEn_description(enDesc);
			bqIndexDesc.setZh_description(zhDesc);
			bqIndexDesc.setJa_description(jaDesc);
			bqIndexDesc.setKo_description(koDesc);
			bqIndexDesc.setHk_description(hkDesc);
			bqIndexDesc.setType_code(typeCode);
			bqIndexDescService.update(bqIndexDesc);
		} else {
			BQIndexDescription bqIndexDescription = bqIndexDescService.readByCode(typeCode);
			if (null != bqIndexDescription) {
				return super.errorPage(mo, "已存在同名类型指数说明");
			}
			bqIndexDesc.setDateline(now);
			bqIndexDesc.setEn_description(enDesc);
			bqIndexDesc.setZh_description(zhDesc);
			bqIndexDesc.setJa_description(jaDesc);
			bqIndexDesc.setKo_description(koDesc);
			bqIndexDesc.setHk_description(hkDesc);
			bqIndexDesc.setType_code(typeCode);
			bqIndexDescService.add(bqIndexDesc);
		}

		return "redirect:/managerIndexDesc/loadIndexDesc.do";
	}

	@RequestMapping(value = "/delIndexDesc")
	public String delIndexDesc(String id) {
		int indexId = ConvertUtil.toInt(id, 0);
		if (indexId > 0) {
			bqIndexDescService.delById(indexId);
		}
		return "redirect:/managerIndexDesc/loadIndexDesc.do";
	}

}
