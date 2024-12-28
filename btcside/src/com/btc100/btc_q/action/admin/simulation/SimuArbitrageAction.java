package com.btc100.btc_q.action.admin.simulation;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.VArbitrageSetting;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.VArbitrageSettingService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VMemberTradeDetailService;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.CountVo;

@Controller
@RequestMapping(value = "/simulation/managerArbitrage")
public class SimuArbitrageAction extends BaseAction {

	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VArbitrageSettingService vArbitrageSettingService;
	@Autowired
	private VMemberTradeDetailService vMemberTradeDetailService;

	@RequestMapping(value = "/depthManager")
	public String depthManager(String code, Model mo) {
		VTransaction currTrans = null;
		
		List<VTransaction> transList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (transList == null) {
			transList = vTransactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		mo.addAttribute("transList", transList);
		if (!StringUtil.isEmpty(code)) {
			for	(VTransaction trans :transList) {
				if (trans.getTransaction_code().equals(code)) {
					currTrans = trans;
				}
			}
		}
		
		if (currTrans == null && transList.size() > 0) {
			currTrans = transList.get(0);
		}
		
		if (currTrans == null) {
			return super.url_JSP("depthManager");
		}
		mo.addAttribute("current", currTrans);
		
		String updateTime = "";
		Long time = spyMemcachedClient.get(BTCQMemecacheKeys.V_HUOBI_DEPTH_UPDATE + currTrans.getTransaction_code());
		if (time != null) {
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(time);
			updateTime = sdf.format(date);
		}
		mo.addAttribute("updateTime", updateTime);
		
		VArbitrageSetting setting = vArbitrageSettingService.getArbitrageSettingByCode(currTrans.getTransaction_code());
		if (setting == null) {
			setting = new VArbitrageSetting();
			setting.setF_code(currTrans.getTransaction_code());
		}
		
		mo.addAttribute("setting", setting);
		return super.url_JSP("depthManager");
	}
	
	@RequestMapping(value = "/updateDepthSetting", method = RequestMethod.POST)
	public String updateDepthSetting(VArbitrageSetting setting, Model mo) {
		updateArbitrageSetting(setting);
		return super.redirect("/simulation/managerArbitrage/depthManager.do?code=" + setting.getF_code());
	}
	
	@RequestMapping(value = "/updateScalpingSetting", method = RequestMethod.POST)
	public String updateScalpingSetting(VArbitrageSetting setting, Model mo) {
		updateArbitrageSetting(setting);
		return super.redirect("/simulation/managerArbitrage/scalpingManager.do?code=" + setting.getF_code());
	}
	
	@RequestMapping(value = "/updateHedgingSetting", method = RequestMethod.POST)
	public String updateHedgingSetting(VArbitrageSetting setting, Model mo) {
		updateArbitrageSetting(setting);
		return super.redirect("/simulation/managerArbitrage/hedgingManager.do?code=" + setting.getF_code());
	}
	
	@RequestMapping(value = "/scalpingManager")
	public String scalpingManager(String code, String startTime, String endTime, Model mo) throws ParseException {
		VTransaction currTrans = null;
		
		List<VTransaction> transList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (transList == null) {
			transList = vTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		mo.addAttribute("transList", transList);
		if (!StringUtil.isEmpty(code)) {
			for	(VTransaction trans :transList) {
				if (trans.getTransaction_code().equals(code)) {
					currTrans = trans;
				}
			}
		}
		
		if (currTrans == null && transList.size() > 0) {
			currTrans = transList.get(0);
		}
		
		if (currTrans == null) {
			return super.url_JSP("scalpingManager");
		}
		mo.addAttribute("current", currTrans);
		
		VArbitrageSetting setting = vArbitrageSettingService.getArbitrageSettingByCode(currTrans.getTransaction_code());
		if (setting == null) {
			setting = new VArbitrageSetting();
			setting.setF_code(currTrans.getTransaction_code());
		}
		
		mo.addAttribute("setting", setting);
		
		if (!StringUtil.isEmpty(startTime)) {
			DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long starttime = StringUtil.isEmpty(startTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(startTime));
			long endtime = StringUtil.isEmpty(endTime) ? vTransactionService.time() : DateUtil.DateToUnix(formatDate.parse(endTime));
			CountVo vo = vMemberTradeDetailService.findScalpingByTime(currTrans.getTransaction_code(), starttime, endtime);
			if (vo.getAveragePrice() != null) {
				vo.setAveragePrice(vo.getAveragePrice().setScale(4, BigDecimal.ROUND_DOWN));
			}
			mo.addAttribute("startTime", startTime);
			mo.addAttribute("endTime", endTime);
			mo.addAttribute("selectSum", vo);
		}
		
		return super.url_JSP("scalpingManager");
	}

	private void updateArbitrageSetting(VArbitrageSetting setting) {
		if (setting == null) return;
		
		if (StringUtil.isEmpty(setting.getF_code()) || setting.getDepth_uid() <= 0) {
			return;
		}
		
		if (setting.getId() == null) {
			vArbitrageSettingService.save(setting);
		} else {
			vArbitrageSettingService.update(setting);
		}
		
	}
}
