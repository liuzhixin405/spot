package com.btc100.btc_q.action.admin.delivery;

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
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;

@Controller
@RequestMapping("/delivery/managerOneProfitloss")
public class DeliveryOneProfitlossAction extends BaseAction {

	@Autowired
	private DeliveryMemberTransactionService bqMemberTransactionService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@RequestMapping("/loadDegreeRisk")
	public String transOnuse(HttpServletRequest request, Model model) {
		String tcode = request.getParameter("tcode");
		List<DeliveryTransaction> lstBQTrans = super.findCurrTrans();
		if (lstBQTrans == null || lstBQTrans.size() <= 0) {
			return super.url_JSP("oneProfitloss");
		}
		tcode = tcode == null ? lstBQTrans.get(0).getTransaction_code() : tcode;
		DeliveryTransaction trans = null;
		for (DeliveryTransaction bqTransaction : lstBQTrans) {
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
		
		// 获取当前最新成交价
		BigDecimal lastPrice = spyMemcachedClient.get(bqCode + BTCQMemecacheKeys.D_BTCLATESTPRICE); // 获取该合约最新交易价
		if (lastPrice == null) {
			lastPrice = trans.getLatest_transaction_price();
		}
		
		List<Object[]> lstBuyMembTrans = bqMemberTransactionService.readDegreerisk(lastPrice, bqCode, trans.getTrade_coefficient(), Contents.DIRECTION_BUY);
		List<Object[]> lstSellMembTrans = bqMemberTransactionService.readDegreerisk(lastPrice, bqCode, trans.getTrade_coefficient(), Contents.DIRECTION_SELL);
		model.addAttribute("lstBuyMembTrans", lstBuyMembTrans);
		model.addAttribute("lstSellMembTrans", lstSellMembTrans);
		
		return super.url_JSP("oneProfitloss");
	}

}
