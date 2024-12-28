package com.btc100.btc_q.action.admin;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQNotradetime;
import com.btc100.btc_q.entity.BQStockSetting;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.service.BQNotradetimeService;
import com.btc100.btc_q.service.BQStockSettingService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerStockSetting")
public class StockIndexAction extends BaseAction {
	
	@Autowired
	private BQStockSettingService bqStockSettingService;
	@Autowired
	private BQNotradetimeService bqNotradetimeService;
	@Autowired
	private BQTransactionService bqTransactionService;
	
	@RequestMapping(value = "/load")
	public String getStockSetting(String transaction_id, Model mo) {
		List<BQTransaction> transList = bqTransactionService.findTransByKind(1, Contents.TRANSACTION_STATE_ZC);
		
		mo.addAttribute("transList", transList);
		PagerModel pm = bqStockSettingService.readAll();
		mo.addAttribute("pm", pm);
		
		BQStockSetting setting = null;
		if (!StringUtil.isEmpty(transaction_id)) {
			setting = bqStockSettingService.getByTransId(Integer.valueOf(transaction_id));
		} else {
			setting = bqStockSettingService.get();
		}
		
		if (setting == null) {
			setting = new BQStockSetting();
			if (!StringUtil.isEmpty(transaction_id)) {
				setting.setTransaction_id(Integer.valueOf(transaction_id));
			}
		}
		
		mo.addAttribute("setting", setting);
		
		return super.url_JSP("stockSettingManager");
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addStockSetting(BQStockSetting setting, Model mo) {
		if (StringUtil.isEmpty(setting.getTransaction_id())) {
			return super.errorPage(mo, "合约号错误");
		}
		
		if (StringUtil.isEmpty(setting.getId())) {
			bqStockSettingService.add(setting);
		} else {
			bqStockSettingService.update(setting);
		}
		
		return super.redirect("/managerStockSetting/load.do?transaction_id=" + setting.getTransaction_id());
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateStockSetting(BQStockSetting setting, Model mo) {
		
		bqStockSettingService.update(setting);
		
		return super.redirect("/managerStockSetting/load.do");
	}
	
	@RequestMapping(value = "/edit")
	public String edit(Model mo, int id) {
		List<BQTransaction> transList = bqTransactionService.findTransByKind(1, Contents.TRANSACTION_STATE_ZC);
		mo.addAttribute("transList", transList);
		BQStockSetting setting = bqStockSettingService.readById(id);
		mo.addAttribute("setting", setting);
		
		return super.url_JSP("stockSettingEdit");
	}
	
	@RequestMapping(value = "/delete")
	public String delete(Model mo, int id) {
		
		bqStockSettingService.deleteById(id);
		
		return super.redirect("/managerStockSetting/load.do");
	}
	
	@RequestMapping(value = "/loadNotradeTime")
	public String loadNotradeTime(Model mo, String transId) {
		
		Long transactionId = ConvertUtil.toLong(transId, 0l);
		PagerModel pm = bqNotradetimeService.findAllpage(0, 0, transactionId);
		mo.addAttribute("pm", pm);
		List<BQTransaction> transList = bqTransactionService.findTransByKind(1, Contents.TRANSACTION_STATE_ZC);
		mo.addAttribute("transList", transList);
		mo.addAttribute("current", transId);
		
		return super.url_JSP("stockNotradeTime");
	}
	
	@RequestMapping(value = "/deleteNotradeTime")
	public String deleteNoTradeTime(long id, Model mo, String transId) {
		BQNotradetime notradetime = bqNotradetimeService.getById(id);
		if (notradetime == null) {
			return super.errorPage(mo, "不存在该记录");
		}
		bqNotradetimeService.deleteById(id);
		return super.redirect("/managerStockSetting/loadNotradeTime.do?transId=" + notradetime.getTransaction_id());
	}
	
	@RequestMapping(value = "/addNoTradeTime", method = RequestMethod.POST)
	public String addNoTradeTime(Date date, Model mo, String transId) {
		if (date == null) {
			return super.errorPage(mo, "请选择时间");
		}
		long time = date.getTime() / 1000;
		
		Long transactionId = ConvertUtil.toLong(transId, 0l);
		BQTransaction transaction = bqTransactionService.get(transactionId);
		if (transaction == null) {
			return super.errorPage(mo, "合约号错误");
		}
		BQNotradetime noTradeTime = new BQNotradetime();
		noTradeTime.setH_time(time);
		noTradeTime.setH_type(0);
		noTradeTime.setTransaction_id(transactionId);
		bqNotradetimeService.add(noTradeTime);
		
		return super.redirect("/managerStockSetting/loadNotradeTime.do?transId=" + transId);
	}
}
