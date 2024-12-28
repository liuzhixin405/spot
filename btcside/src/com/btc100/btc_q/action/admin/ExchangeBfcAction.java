package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQExchangeBfcRecord;
import com.btc100.btc_q.entity.BQExchangeBfcSetting;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQExchangeBfcRecordService;
import com.btc100.btc_q.service.BQExchangeBfcSettingService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerExchangeBfc")
public class ExchangeBfcAction extends BaseAction {
	
	@Autowired
	private BQExchangeBfcSettingService bqExchangeBfcSettingService;
	@Autowired
	private BQExchangeBfcRecordService bqExchangeBfcRecordService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;

	@RequestMapping("/loadRecords")
	public String load(long id, Long uid, boolean toUsd, Model model) {
		
		BQExchangeBfcSetting current = null;
		List<BQExchangeBfcSetting> settings = bqExchangeBfcSettingService.readAll();
		model.addAttribute("settings", settings);
		if (settings.size() == 0) {
			model.addAttribute("pm", new PagerModel());
			return super.url_JSP("exchangeBfcRecords");
		}
		
		for (BQExchangeBfcSetting setting : settings) {
			if (setting.getId() == id) {
				current = setting;
				break;
			}
		}
		
		if (current == null) {
			current = settings.get(0);
		}
		model.addAttribute("current", current);
		model.addAttribute("toUsd", toUsd);
		
		if (uid == null) uid = 0L;
		if (uid > 0)
			model.addAttribute("uid", uid);
		
		BQCurrencyType bfc = bqCurrencyTypeService.readByTypeName("BFC");
		if (bfc == null) {
			return super.url_JSP("exchangeBfcRecords");
		}
		
		if (toUsd) {
			model.addAttribute("pm", bqExchangeBfcRecordService.readRecords(uid, 0, current.getCurrency_type_id(), bfc.getType_id()));
		} else {
			model.addAttribute("pm", bqExchangeBfcRecordService.readRecords(uid, 0, bfc.getType_id(), current.getCurrency_type_id()));
		}
		
		return super.url_JSP("exchangeBfcRecords");
	}
	
	@RequestMapping(value = "/checkRecord", method = RequestMethod.POST)
	public void checkRecord(HttpServletResponse response, long recordId, Model model) throws JSONException {
		JSONObject retObj = new JSONObject();
		if (recordId <= 0) {
			retObj.put("code", "error");
			retObj.put("msg", "记录ID小于 0");
			toWeb(response, retObj);
			return;
		}
		
		BQExchangeBfcRecord record = bqExchangeBfcRecordService.readById(recordId);
		if (record.getState() != Contents.EXCHANGE_STATE_MANUAL && record.getState() != Contents.EXCHANGE_STATE_SUBMIT) {
			retObj.put("code", "error");
			retObj.put("msg", "记录状态不对！");
			toWeb(response, retObj);
			return;
		}
		
		bqExchangeBfcRecordService.changeState(record.getId(), Contents.EXCHANGE_STATE_CHECKED, record.getState(), "管理员审核通过！");
		retObj.put("code", "success");
		toWeb(response, retObj);
	}
	
	@RequestMapping(value = "/confirmRecord", method = RequestMethod.POST)
	public void confirmRecord(HttpServletResponse response, long recordId, Model model) throws JSONException {
		JSONObject retObj = new JSONObject();
		if (recordId <= 0) {
			retObj.put("code", "error");
			retObj.put("msg", "记录ID小于 0");
			toWeb(response, retObj);
			return;
		}
		
		BQExchangeBfcRecord record = bqExchangeBfcRecordService.readById(recordId);
		if (record.getState() != Contents.EXCHANGE_STATE_MANUAL 
				&& record.getState() != Contents.EXCHANGE_STATE_SUBMIT 
				&& record.getState() != Contents.EXCHANGE_STATE_CHECKED) {
			retObj.put("code", "error");
			retObj.put("msg", "记录状态不对！");
			toWeb(response, retObj);
			return;
		}
		
		try {
			bqExchangeBfcRecordService.managerConfirmRecord(record, "管理员确认！");
		} catch (ServiceException e) {
			retObj.put("code", "error");
			retObj.put("msg", e.getMessage());
			toWeb(response, retObj);
			return;
		}
		
		retObj.put("code", "success");
		toWeb(response, retObj);
	}
	
	@RequestMapping(value = "/cancelRecord", method = RequestMethod.POST)
	public void cancelRecord(HttpServletResponse response, long recordId, Model model) throws JSONException {
		JSONObject retObj = new JSONObject();
		if (recordId <= 0) {
			retObj.put("code", "error");
			retObj.put("msg", "记录ID小于 0");
			toWeb(response, retObj);
			return;
		}
		
		BQExchangeBfcRecord record = bqExchangeBfcRecordService.readById(recordId);
		if (record.getState() != Contents.EXCHANGE_STATE_MANUAL 
				&& record.getState() != Contents.EXCHANGE_STATE_SUBMIT 
				&& record.getState() != Contents.EXCHANGE_STATE_CHECKED) {
			retObj.put("code", "error");
			retObj.put("msg", "记录状态不对！");
			toWeb(response, retObj);
			return;
		}
		
		try {
			bqExchangeBfcRecordService.cancelRecord(record, "管理员取消！");
		} catch (ServiceException e) {
			retObj.put("code", "error");
			retObj.put("msg", e.getMessage());
			toWeb(response, retObj);
			return;
		}
		retObj.put("code", "success");
		toWeb(response, retObj);
	}
	
	@RequestMapping("/exchangeSetting")
	public String exchangeSetting(Long id, Model model) {
		List<BQExchangeBfcSetting> settings = bqExchangeBfcSettingService.readAll();
		
		BQExchangeBfcSetting setting = null;
		if (settings.size() == 0) {
			BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeName("XKD");
			if (currencyType != null) {
				setting = new BQExchangeBfcSetting();
				setting.setCurrency_type_id(currencyType.getType_id());
				setting.setMin_amount(BigDecimal.ONE);
				setting.setSymbol(currencyType.getType_name());
				setting.setRunning(true);
				settings.add(setting);
				model.addAttribute("setting", setting);
				model.addAttribute("settings", settings);
			}
			return super.url_JSP("exchangeBfcSetting");
		}
		model.addAttribute("settings", settings);
		
		for (BQExchangeBfcSetting st : settings) {
			if (st.getId() == id) {
				setting = st;
				break;
			}
		}
		
		if (setting == null) {
			setting = settings.get(0);
		}
		
		model.addAttribute("setting", setting);
		return super.url_JSP("exchangeBfcSetting");
	}
	
	@RequestMapping(value = "/updateSetting", method = RequestMethod.POST)
	public String update(BQExchangeBfcSetting setting, Model mo) {
		if (StringUtil.isEmpty(setting.getSymbol()) || !"BFX".equals(setting.getSymbol())) {
			return super.redirect("/managerExchangeBfc/exchangeSetting.do");
		}
		if (setting.getExchange_rate().compareTo(BigDecimal.ZERO) <= 0) {
			return super.redirect("/managerExchangeBfc/exchangeSetting.do");
		}

		bqExchangeBfcSettingService.update(setting);
		return super.redirect("/managerExchangeBfc/exchangeSetting.do?id=" + setting.getId());
	}

}
