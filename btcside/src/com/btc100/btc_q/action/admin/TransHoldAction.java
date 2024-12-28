package com.btc100.btc_q.action.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.service.TradeLogService;

@Controller
@RequestMapping("/managerTransHold")
public class TransHoldAction extends BaseAction {

	@Autowired
	private BQTransactionService bqTransactionService;
	@Autowired
	TradeLogService tradeLogService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	
	@RequestMapping("/loadRankings")
	public String transHoleRanking(HttpServletRequest request, Model model) {
		String tcode = request.getParameter("tcode");
		String buyOrderBy = request.getParameter("buyOrderBy");
		String sellOrderBy = request.getParameter("sellOrderBy");
		List<BQTransaction> lstBQTrans = bqTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		if (lstBQTrans == null || lstBQTrans.size() <= 0) {
			return super.url_JSP("oneProfitloss");
		}
		tcode = tcode == null ? lstBQTrans.get(0).getTransaction_code() : tcode;
		BQTransaction trans = null;
		for (BQTransaction bqTransaction : lstBQTrans) {
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
		lstBuyMembTrans = bqMemberTransactionService.readHolds(bqCode, Contents.DIRECTION_BUY, trans.getTrade_coefficient(), buyOrderBy);
		lstSellMembTrans = bqMemberTransactionService.readHolds(bqCode, Contents.DIRECTION_SELL, trans.getTrade_coefficient(), sellOrderBy);
		
		model.addAttribute("buyOrderBy", buyOrderBy);
		model.addAttribute("sellOrderBy", sellOrderBy);
		model.addAttribute("lstBuyMembTrans", lstBuyMembTrans);
		model.addAttribute("lstSellMembTrans", lstSellMembTrans);
		
		return super.url_JSP("userHoldRanking");
	}

}
