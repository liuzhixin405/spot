package com.btc100.btc_q.action.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQOverloadMatchSetting;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.service.BQOverloadMatchSettingService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.util.StringUtil;

@Controller
@RequestMapping(value = "/managerOverloadMatch")
public class OverloadMatchAction extends BaseAction {

	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQOverloadMatchSettingService bqOverloadMatchSettingService;

	@RequestMapping(value = "/matchManager")
	public String depthManager(String code, Model mo) {
		BQTransaction currTrans = null;
		
		List<BQTransaction> transList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transList == null) {
			transList = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		mo.addAttribute("transList", transList);
		if (!StringUtil.isEmpty(code)) {
			for	(BQTransaction trans :transList) {
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
		
		BQOverloadMatchSetting setting = bqOverloadMatchSettingService.readSettingByCode(currTrans.getTransaction_code());
		if (setting == null) {
			setting = new BQOverloadMatchSetting();
			setting.setF_code(currTrans.getTransaction_code());
		}
		
		mo.addAttribute("setting", setting);
		return super.url_JSP("overloadMatchManager");
	}
	
	@RequestMapping(value = "/updateSetting", method = RequestMethod.POST)
	public String updateDepthSetting(BQOverloadMatchSetting setting, Model mo) {
		do {
			if (StringUtil.isEmpty(setting.getF_code()) || setting.getF_user_id() <= 1) {
				break;
			}
			
			if (setting.getId() == null) {
				bqOverloadMatchSettingService.save(setting);
			} else {
				bqOverloadMatchSettingService.update(setting);
			}
			
		} while (false);
		
		return super.redirect("/managerOverloadMatch/matchManager.do?code=" + setting.getF_code());
	}
}
