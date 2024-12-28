package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.util.StringUtil;

@Controller
@RequestMapping(value = "/managerHoldCostPrice")
public class ManagerCostAction extends BaseAction {

	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQTransactionService bqTransactionService;
	
	
	@RequestMapping(value = "/costManager")
	public String costManager(HttpServletRequest request, Model mo) {

		BQTransaction currTrans = null;
		
		List<BQTransaction> transList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
		if (transList == null) {
			transList = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		String code = request.getParameter("code");
		mo.addAttribute("transList", transList);
		if (!StringUtil.isEmpty(code)) {
			for	(BQTransaction trans :transList) {
				if (trans.getTransaction_code().equals(code)) {
					currTrans = trans;
					break;
				}
			}
		}
		
		if (currTrans == null && transList.size() > 0) {
			currTrans = transList.get(0);
		}
		
		if (currTrans == null) {
			return super.url_JSP("holdCostManager");
		}

		mo.addAttribute("current", currTrans);
		
		mo.addAttribute("pm", bqMemberTransactionService.readAllMemberTransacton(currTrans.getTransaction_code()));
		
		return super.url_JSP("holdCostManager");
	}
	
	@RequestMapping(value = "/updateCost")
	public String updateCost(HttpServletRequest request, String code, BigDecimal value) {
		if (StringUtil.isEmpty(code) || value == null || value.compareTo(BigDecimal.ZERO) == 0) {
			return super.redirect("/managerHoldCostPrice/costManager.do?code=" + code);
		}
		
		 bqMemberTransactionService.updateAveragePrice(code, value);
		 return super.redirect("/managerHoldCostPrice/costManager.do?code=" + code);
	}

}
