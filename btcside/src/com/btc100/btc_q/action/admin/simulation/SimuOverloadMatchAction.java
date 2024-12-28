package com.btc100.btc_q.action.admin.simulation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.VOverloadMatchSetting;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.VOverloadMatchSettingService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.util.StringUtil;

@Controller
@RequestMapping("/simulation/managerOverloadMatch")
public class SimuOverloadMatchAction extends BaseAction {

	@Autowired
	private VTransactionService bqTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VOverloadMatchSettingService vOverloadMatchSettingService;

	@RequestMapping(value = "/matchManager")
	public String depthManager(String code, Model mo) {
		VTransaction currTrans = null;
		
		List<VTransaction> transList = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (transList == null) {
			transList = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
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
			return super.url_JSP("overloadMatchManager");
		}

		mo.addAttribute("current", currTrans);
		
		VOverloadMatchSetting setting = vOverloadMatchSettingService.getSettingByCode(currTrans.getTransaction_code());
		if (setting == null) {
			setting = new VOverloadMatchSetting();
			setting.setF_code(currTrans.getTransaction_code());
		}
		
		mo.addAttribute("setting", setting);
		return super.url_JSP("overloadMatchManager");
	}
	
	@RequestMapping(value = "/updateSetting", method = RequestMethod.POST)
	public String updateDepthSetting(VOverloadMatchSetting setting, Model mo) {
		do {
			if (StringUtil.isEmpty(setting.getF_code()) || setting.getF_user_id() <= 1) {
				break;
			}
			
			if (setting.getId() == null) {
				vOverloadMatchSettingService.save(setting);
			} else {
				vOverloadMatchSettingService.update(setting);
			}
			
		} while (false);
		
		return super.redirect("/simulation/managerOverloadMatch/matchManager.do?code=" + setting.getF_code());
	}
}
