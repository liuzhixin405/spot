package com.btc100.btc_q.action.admin.simulation;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VTransactionService;

@Controller
@RequestMapping("/simulation/managerTransHold")
public class SimuTransHoldAction extends BaseAction {

	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	
	@RequestMapping("/loadRankings")
	public String transHoleRanking(HttpServletRequest request, Model model) {
		String tcode = request.getParameter("tcode");
		List<VTransaction> lstBQTrans = vTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		if (lstBQTrans == null || lstBQTrans.size() <= 0) {
			return super.url_JSP("oneProfitloss");
		}
		tcode = tcode == null ? lstBQTrans.get(0).getTransaction_code() : tcode;
		VTransaction trans = null;
		for (VTransaction bqTransaction : lstBQTrans) {
			if (bqTransaction.getTransaction_code().equals(tcode)) {
				trans = bqTransaction;
				break;
			}
		}
		if (trans == null) {
			return super.errorPage(model, "错误的合约");
		}
		String bqCode = trans.getTransaction_code();
		
		model.addAttribute("lstBQTrans", lstBQTrans);
		model.addAttribute("bqCode", bqCode);

		List<Object[]> lstBuyMembTrans = null;
		List<Object[]> lstSellMembTrans = null;
		lstBuyMembTrans = vMemberTransactionService.findHolds(bqCode, Contents.DIRECTION_BUY, trans.getTrade_coefficient());
		lstSellMembTrans = vMemberTransactionService.findHolds(bqCode, Contents.DIRECTION_SELL, trans.getTrade_coefficient());
		
		model.addAttribute("lstBuyMembTrans", lstBuyMembTrans);
		model.addAttribute("lstSellMembTrans", lstSellMembTrans);
		
		return super.url_JSP("userHoldRanking");
	}

}
