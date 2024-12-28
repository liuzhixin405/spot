package com.btc100.btc_q.action.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberRechargeService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.BalanceAccountVO;

@Controller
@RequestMapping("/managerCheckAccount")
public class CheckAccountAction extends BaseAction {
	
	@Autowired
	private BQMemberRechargeService bqMemberRechargeService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService; 
	
	@RequestMapping("/loadDetail")
	public String loadDetail(HttpServletRequest request, Model mo) throws Exception {
		int id = ConvertUtil.toInt(request.getParameter("id"), 0);
		int typeId = ConvertUtil.toInt(request.getParameter("typeId"), 0);
		if (typeId == 0) {
			typeId = Contents.TRADE_CURRENCY_TYPE_ID;
		}
		
		List<BQCurrencyType> currencyTypes = bqCurrencyTypeService.readAll();
		mo.addAttribute("currencyTypes", currencyTypes);
		mo.addAttribute("current", typeId);
		String code = "";
		for (BQCurrencyType type : currencyTypes) {
			if (type.getType_id() == typeId) {
				code = type.getType_name();
				break;
			}
		}
		
		String path = "";
		if ("USDT".equalsIgnoreCase(code)) {
			path = "";
		} else if ("BFC".equalsIgnoreCase(code)) {
			path = "simulation/";
		} else {
			path = "normal/";
		}
		mo.addAttribute("path", path);
		mo.addAttribute("code", code.toLowerCase());
		
		if (id > 0) {
			BQAccount bqAccount = bqAccountService.readAccount(id, typeId);
			mo.addAttribute("btc", bqAccount.getBtc());
			mo.addAttribute("lockbtc", bqAccount.getBtc_locked());
			mo.addAttribute("id", id);
			
			BalanceAccountVO allBalance = new BalanceAccountVO();
			BalanceAccountVO generalBalance = new BalanceAccountVO();
			List<BalanceAccountVO> contractBalances = new ArrayList<BalanceAccountVO>();
			List<BalanceAccountVO> deliveryContractBalances = new ArrayList<BalanceAccountVO>();
			bqMemberRechargeService.getBalanceAccount(id, typeId, allBalance, generalBalance, contractBalances, deliveryContractBalances);
			
			mo.addAttribute("generalBalance", generalBalance);
			mo.addAttribute("contractBalances", contractBalances);
			mo.addAttribute("allBalance", allBalance);
			mo.addAttribute("deliveryContractBalances", deliveryContractBalances);
		}

		return super.url_JSP("withdrawDetails");
	}

}
