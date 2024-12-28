package com.btc100.btc_q.action.admin.delivery;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.DeliveryOverloadMatchSetting;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.DeliveryOverloadMatchSettingService;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.util.StringUtil;

@Controller
@RequestMapping("/delivery/managerOverloadMatch")
public class DeliveryOverloadMatchAction extends BaseAction {

	@Autowired
	private DeliveryTransactionService transactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private DeliveryOverloadMatchSettingService deliveryOverloadMatchSettingService;

	@RequestMapping(value = "/matchManager")
	public String depthManager(String code, Model mo) {
		DeliveryTransaction currTrans = null;
		
		List<DeliveryTransaction> transList = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTIONLIST);
		if (transList == null) {
			transList = transactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		mo.addAttribute("transList", transList);
		if (!StringUtil.isEmpty(code)) {
			for	(DeliveryTransaction trans :transList) {
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
		
		DeliveryOverloadMatchSetting setting = deliveryOverloadMatchSettingService.getSettingByCode(currTrans.getTransaction_code());
		if (setting == null) {
			setting = new DeliveryOverloadMatchSetting();
			setting.setF_code(currTrans.getTransaction_code());
		}
		
		mo.addAttribute("setting", setting);
		return super.url_JSP("overloadMatchManager");
	}
	
	@RequestMapping(value = "/updateSetting", method = RequestMethod.POST)
	public String updateDepthSetting(DeliveryOverloadMatchSetting setting, Model mo) {
		do {
			if (StringUtil.isEmpty(setting.getF_code()) || setting.getF_user_id() <= 1) {
				break;
			}
			
			if (setting.getId() == null) {
				deliveryOverloadMatchSettingService.save(setting);
			} else {
				deliveryOverloadMatchSettingService.update(setting);
			}
			
		} while (false);
		
		return super.redirect("/delivery/managerOverloadMatch/matchManager.do?code=" + setting.getF_code());
	}
}
