package com.btc100.btc_q.action.admin.delivery;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;

@Controller
@RequestMapping("managerDeliveryTransHold")
public class DeliveryTransHoldAction extends BaseAction {

	@Autowired
	private DeliveryMemberTransactionService deliveryMemberTransactionService;
	
	@RequestMapping("/loadRankings")
	public String transHoleRanking(HttpServletRequest request, Model model) {
		String tcode = request.getParameter("tcode");
		String buyOrderBy = request.getParameter("buyOrderBy");
		String sellOrderBy = request.getParameter("sellOrderBy");
		List<DeliveryTransaction> lstBQTrans = super.findCurrTrans();
		
		tcode = tcode == null ? lstBQTrans.get(0).getTransaction_code() : tcode;
		
		DeliveryTransaction trans = super.findTransByCode(tcode);

		if (trans == null) {
			return super.errorPage(model, "错误的合约");
		}
		String bqCode = trans.getTransaction_code();
		
		model.addAttribute("lstBQTrans", lstBQTrans);
		model.addAttribute("bqCode", bqCode);

		List<Object[]> lstBuyMembTrans = null;
		List<Object[]> lstSellMembTrans = null;
		lstBuyMembTrans = deliveryMemberTransactionService.readHolds(bqCode, Contents.DIRECTION_BUY, trans.getTrade_coefficient(), buyOrderBy);
		lstSellMembTrans = deliveryMemberTransactionService.readHolds(bqCode, Contents.DIRECTION_SELL, trans.getTrade_coefficient(), sellOrderBy);
		
		model.addAttribute("buyOrderBy", buyOrderBy);
		model.addAttribute("sellOrderBy", sellOrderBy);
		
		model.addAttribute("lstBuyMembTrans", lstBuyMembTrans);
		model.addAttribute("lstSellMembTrans", lstSellMembTrans);
		
		return super.url_JSP("userHoldRanking");
	}

}
