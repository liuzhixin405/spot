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
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQExchangeRecord;
import com.btc100.btc_q.entity.BQExchangeSetting;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQExchangeRecordService;
import com.btc100.btc_q.service.BQExchangeSettingService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerExchange")
public class ExchangeAction extends BaseAction {
	
	@Autowired
	private BQExchangeSettingService bqExchangeSettingService;
	@Autowired
	private BQExchangeRecordService bqExchangeRecordService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQAccountService bqAccountService;

	@RequestMapping("/loadRecords")
	public String load(long id, Long uid, boolean toUsd, Model model) {
		
		BQExchangeSetting current = null;
		List<BQExchangeSetting> settings = bqExchangeSettingService.readAll();
		model.addAttribute("settings", settings);
		if (settings.size() == 0) {
			model.addAttribute("pm", new PagerModel());
			return super.url_JSP("exchangeRecords");
		}
		
		for (BQExchangeSetting setting : settings) {
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
		
		if (toUsd) {
			model.addAttribute("pm", bqExchangeRecordService.readRecords(uid, 0, current.getCurrency_type_id(), Contents.TRADE_CURRENCY_TYPE_ID));
		} else {
			model.addAttribute("pm", bqExchangeRecordService.readRecords(uid, 0, Contents.TRADE_CURRENCY_TYPE_ID, current.getCurrency_type_id()));
		}
		
		return super.url_JSP("exchangeRecords");
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
		
		BQExchangeRecord record = bqExchangeRecordService.readById(recordId);
		if (record.getState() != Contents.EXCHANGE_STATE_MANUAL && record.getState() != Contents.EXCHANGE_STATE_SUBMIT) {
			retObj.put("code", "error");
			retObj.put("msg", "记录状态不对！");
			toWeb(response, retObj);
			return;
		}
		
		bqExchangeRecordService.changeState(record.getId(), Contents.EXCHANGE_STATE_CHECKED, record.getState(), "管理员审核通过！");
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
		
		BQExchangeRecord record = bqExchangeRecordService.readById(recordId);
		if (record.getState() != Contents.EXCHANGE_STATE_MANUAL 
				&& record.getState() != Contents.EXCHANGE_STATE_SUBMIT 
				&& record.getState() != Contents.EXCHANGE_STATE_CHECKED) {
			retObj.put("code", "error");
			retObj.put("msg", "记录状态不对！");
			toWeb(response, retObj);
			return;
		}
		
		try {
			BigDecimal dealCash = BigDecimal.ZERO;
			if (record.getDirection() == Contents.DIRECTION_BUY) {
				dealCash = record.getTarget_amount().multiply(record.getOrder_price());
			} else {
				dealCash = record.getOri_amount().multiply(record.getOrder_price());
			}
			bqExchangeRecordService.confirmRecord(record, record.getOri_amount(), record.getTarget_amount(), dealCash, "管理员确认！");
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
		
		BQExchangeRecord record = bqExchangeRecordService.readById(recordId);
		if (record.getState() != Contents.EXCHANGE_STATE_MANUAL 
				&& record.getState() != Contents.EXCHANGE_STATE_SUBMIT 
				&& record.getState() != Contents.EXCHANGE_STATE_CHECKED) {
			retObj.put("code", "error");
			retObj.put("msg", "记录状态不对！");
			toWeb(response, retObj);
			return;
		}
		
		try {
			bqExchangeRecordService.cancelRecord(record, "管理员取消！");
		} catch (ServiceException e) {
			retObj.put("code", "error");
			retObj.put("msg", e.getMessage());
			toWeb(response, retObj);
			return;
		}
		retObj.put("code", "success");
		toWeb(response, retObj);
	}
	
	@RequestMapping(value = "/exchangeAllAccount", method = RequestMethod.POST)
	public String exchangeAllAccount(int oriCoinId, int targetCoinId, BigDecimal rate, Model model) {
		
		BQCurrencyType originCoin = bqCurrencyTypeService.readByTypeId(oriCoinId); // 源币种
		BQCurrencyType targetCoin = bqCurrencyTypeService.readByTypeId(targetCoinId);
		
		boolean doExchange = true;
		if (originCoin == null || targetCoin == null || rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
			doExchange = false;
		}
		
		if (doExchange) {
			List<BQAccount> accounts = bqAccountService.readAboveZero(oriCoinId);
			
			for (BQAccount account : accounts) {
				if (account.getBtc().compareTo(new BigDecimal(0.0001)) <= 0) {
					continue;
				}
				
				BQExchangeRecord exchangeRecord = new BQExchangeRecord();
				exchangeRecord.setF_user_id(account.getF_user_id());
				exchangeRecord.setOri_currency_type_id(originCoin.getType_id());
				exchangeRecord.setOri_currency_name(originCoin.getType_name());
				exchangeRecord.setTarget_currency_type_id(targetCoin.getType_id());
				exchangeRecord.setTarget_currency_name(targetCoin.getType_name());
				exchangeRecord.setFrozen_rate(BigDecimal.ZERO);
				exchangeRecord.setFrozen_amount(BigDecimal.ZERO);
				exchangeRecord.setOri_amount(account.getBtc());
				exchangeRecord.setTarget_amount(account.getBtc().multiply(rate));
				exchangeRecord.setPresent_price(rate);
				exchangeRecord.setState(Contents.EXCHANGE_STATE_PROCESSING);
				exchangeRecord.setNote(originCoin.getType_name() + "兑换" + targetCoin.getType_name()		);
				exchangeRecord.setPrice_range(BigDecimal.ZERO);
				exchangeRecord.setDirection(Contents.DIRECTION_SELL);
				exchangeRecord.setOrder_price(BigDecimal.ONE);
				exchangeRecord.setFee_rate(BigDecimal.ZERO);
				
				try {
					bqExchangeRecordService.adminExchange(exchangeRecord);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		List<BQCurrencyType> coins = bqCurrencyTypeService.readAll(true);
		model.addAttribute("coins", coins);
		model.addAttribute("msg", "兑换完毕");
		
		return super.url_JSP("exchangeAdmin");
	}
	
	@RequestMapping("/adminExchange")
	public String adminExchange(Model model) {
		List<BQCurrencyType> coins = bqCurrencyTypeService.readAll(true);
		model.addAttribute("coins", coins);
		return super.url_JSP("exchangeAdmin");
	}
	
	@RequestMapping("/exchangeSetting")
	public String exchangeSetting(long id, Model model) {
		
		List<BQExchangeSetting> settings = bqExchangeSettingService.readAll();
		model.addAttribute("settings", settings);
		if (settings.size() == 0) {
			return super.url_JSP("exchangeSetting");
		}
		
		BQExchangeSetting setting = null;
		for (BQExchangeSetting st : settings) {
			if (st.getId() == id) {
				setting = st;
				break;
			}
		}
		
		if (setting == null) {
			setting = settings.get(0);
		}
		
		model.addAttribute("setting", setting);
		return super.url_JSP("exchangeSetting");
	}
	
	@RequestMapping(value = "/updateSetting", method = RequestMethod.POST)
	public String update(BQExchangeSetting setting, Model mo) {
		if (setting.getId() == null || StringUtil.isEmpty(setting.getSymbol()) || setting.getId() <= 0) {
			return super.redirect("/managerExchange/exchangeSetting.do");
		}

		bqExchangeSettingService.update(setting);
		return super.redirect("/managerExchange/exchangeSetting.do?id=" + setting.getId());
	}

}
