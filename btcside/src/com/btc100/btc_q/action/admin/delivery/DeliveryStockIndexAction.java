package com.btc100.btc_q.action.admin.delivery;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.DeliveryNotradetime;
import com.btc100.btc_q.entity.DeliveryStockSetting;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.DeliveryNotradetimeService;
import com.btc100.btc_q.service.DeliveryStockSettingService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/delivery/managerStockSetting")
public class DeliveryStockIndexAction extends BaseAction {
	
	@Autowired
	private DeliveryStockSettingService deliveryStockSettingService;
	@Autowired
	private DeliveryNotradetimeService deliveryNotradetimeService;
	@Autowired
	private DeliveryTransactionService deliveryTransactionService;
	
	@RequestMapping(value = "/load")
	public String getStockSetting(String transaction_id, Model mo) {
		List<DeliveryTransaction> transList = deliveryTransactionService.findTransByKind(1, Contents.TRANSACTION_STATE_ZC);
		
		mo.addAttribute("transList", transList);
		PagerModel pm = deliveryStockSettingService.readAll();
		mo.addAttribute("pm", pm);
		
		DeliveryStockSetting setting = null;
		if (!StringUtil.isEmpty(transaction_id)) {
			setting = deliveryStockSettingService.getByTransId(Integer.valueOf(transaction_id));
		} else {
			setting = deliveryStockSettingService.get();
		}
		
		if (setting == null) {
			setting = new DeliveryStockSetting();
			if (!StringUtil.isEmpty(transaction_id)) {
				setting.setTransaction_id(Integer.valueOf(transaction_id));
			}
		}
		
		mo.addAttribute("setting", setting);
		
		return super.url_JSP("stockSettingManager");
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addStockSetting(DeliveryStockSetting setting, Model mo) {
		if (StringUtil.isEmpty(setting.getTransaction_id())) {
			return super.errorPage(mo, "合约号错误");
		}
		
		if (StringUtil.isEmpty(setting.getId())) {
			deliveryStockSettingService.add(setting);
		} else {
			deliveryStockSettingService.update(setting);
		}
		
		return super.redirect("/delivery/managerStockSetting/load.do?transaction_id=" + setting.getTransaction_id());
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateStockSetting(DeliveryStockSetting setting, Model mo) {
		
		deliveryStockSettingService.update(setting);
		
		return super.redirect("/delivery/managerStockSetting/load.do");
	}
	
	@RequestMapping(value = "/edit")
	public String edit(Model mo, int id) {
		List<DeliveryTransaction> transList = deliveryTransactionService.findTransByKind(1, Contents.TRANSACTION_STATE_ZC);
		mo.addAttribute("transList", transList);
		DeliveryStockSetting setting = deliveryStockSettingService.readById(id);
		mo.addAttribute("setting", setting);
		
		return super.url_JSP("stockSettingEdit");
	}
	
	@RequestMapping(value = "/delete")
	public String delete(Model mo, int id) {
		
		deliveryStockSettingService.deleteById(id);
		
		return super.redirect("/delivery/managerStockSetting/load.do");
	}
	
	@RequestMapping(value = "/loadNotradeTime")
	public String loadNotradeTime(Model mo, String transId) {
		
		Long transactionId = ConvertUtil.toLong(transId, 0l);
		PagerModel pm = deliveryNotradetimeService.findAllpage(0, 0, transactionId);
		mo.addAttribute("pm", pm);
		List<DeliveryTransaction> transList = deliveryTransactionService.findTransByKind(1, Contents.TRANSACTION_STATE_ZC);
		mo.addAttribute("transList", transList);
		mo.addAttribute("current", transId);
		
		return super.url_JSP("stockNotradeTime");
	}
	
	@RequestMapping(value = "/deleteNotradeTime")
	public String deleteNoTradeTime(long id, Model mo, String transId) {
		DeliveryNotradetime notradetime = deliveryNotradetimeService.getById(id);
		if (notradetime == null) {
			return super.errorPage(mo, "不存在该记录");
		}
		deliveryNotradetimeService.deleteById(id);
		return super.redirect("/delivery/managerStockSetting/loadNotradeTime.do?transId=" + notradetime.getTransaction_id());
	}
	
	@RequestMapping(value = "/addNoTradeTime", method = RequestMethod.POST)
	public String addNoTradeTime(Date date, Model mo, String transId) {
		if (date == null) {
			return super.errorPage(mo, "请选择时间");
		}
		long time = date.getTime() / 1000;
		
		Long transactionId = ConvertUtil.toLong(transId, 0l);
		DeliveryTransaction transaction = deliveryTransactionService.get(transactionId);
		if (transaction == null) {
			return super.errorPage(mo, "合约号错误");
		}
		DeliveryNotradetime noTradeTime = new DeliveryNotradetime();
		noTradeTime.setH_time(time);
		noTradeTime.setH_type(0);
		noTradeTime.setTransaction_id(transactionId);
		deliveryNotradetimeService.add(noTradeTime);
		
		return super.redirect("/delivery/managerStockSetting/loadNotradeTime.do?transId=" + transId);
	}
}
