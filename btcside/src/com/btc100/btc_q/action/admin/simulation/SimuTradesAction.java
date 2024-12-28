package com.btc100.btc_q.action.admin.simulation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.service.VMemberTradeDetailService;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/simulation/managerTrade")
public class SimuTradesAction extends BaseAction {

	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VMemberTradeDetailService vMemberTradeDetailService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	private VTradeLogService vTradeLogService;
	
	@RequestMapping("/loadTradeLogs") 
	public String loadDetail(int id, String code, Model model) throws Exception {
		if (id > 0) {
			VTransaction currentTransaction = null;
			if (!StringUtil.isEmpty(code) ) {
				currentTransaction = vTransactionService.readTransByCode(code);
			}
			
			if (currentTransaction == null) {
				return super.url_JSP("tradeLogList");
			}
			
			model.addAttribute("currentTransaction", currentTransaction);
			PagerModel tradeLogList = new PagerModel();
			tradeLogList = vTradeLogService.findByUserIdAndTransaction(currentTransaction.getTransaction_code(), id, 0);

			model.addAttribute("pm", tradeLogList);
		}
		
		model.addAttribute("id", id);
		model.addAttribute("code", code);
		return super.url_JSP("tradeLogList");
	}

	@RequestMapping("/loadTrades")
	public String loadTrades(Model mo, String tcode, String buyuser, String selluser) {
		List<VTransaction> transactionList = vTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		mo.addAttribute("lstTrans", transactionList);
		
		if (transactionList.size() > 0) {
			if (StringUtil.isEmpty(tcode)) {
				tcode = transactionList.get(0).getTransaction_code();
			}
		} else {
			PagerModel pm = new PagerModel();
			pm.setDatas(null);
			pm.setTotal(0);
			mo.addAttribute("pm", pm);
			
			return super.url_JSP("tradelists");
		}
		
		int buyuserid = ConvertUtil.toInt(buyuser, 0);
		int selluserid = ConvertUtil.toInt(selluser, 0);
		if (buyuserid > 0)
			mo.addAttribute("buyuser", buyuserid);
		if (selluserid > 0)
			mo.addAttribute("selluser", selluserid);
		
		mo.addAttribute("hyh", tcode);
		mo.addAttribute("current", tcode);
		
		mo.addAttribute("pm", vMemberTradeDetailService.findModel(tcode, buyuserid, selluserid));
		double itotal = vMemberTradeDetailService.findTotal(tcode, buyuserid, selluserid);
		mo.addAttribute("total", itotal);
		
		mo.addAttribute("dc", vMemberTransactionService.findAllUserAmount(tcode, 1, 1)); //② 多单持仓量 多单持仓可用
		mo.addAttribute("dd", vMemberTransactionService.findAllUserAmount(tcode, 0, 1)); //③ 多单冻结量 多单持仓冻结
		mo.addAttribute("db", vPendingDelegationService.getSumByAction(tcode, Contents.DIRECTION_BUY, Contents.PRIORITY_BC)); //④ 多单爆仓量
		
		mo.addAttribute("kc", vMemberTransactionService.findAllUserAmount(tcode, 1, 2)); //⑥ 空单持仓量 空单持仓可用
		mo.addAttribute("kd", vMemberTransactionService.findAllUserAmount(tcode, 0, 2)); //⑦ 空单冻结量 空单持仓冻结
		mo.addAttribute("kb", vPendingDelegationService.getSumByAction(tcode, Contents.DIRECTION_SELL, Contents.PRIORITY_BC)); //⑧ 空单爆仓量
		
		return super.url_JSP("tradelists");
	}
}
