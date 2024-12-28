package com.btc100.btc_q.action.admin.simulation;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.VPendingDelegationService;
import com.btc100.btc_q.service.VPlanPendingDelegationService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/simulation/managerPending")
public class SimuPendingsAction extends BaseAction {

	@Autowired
	private VPendingDelegationService vPendingDelegationService;
	@Autowired
	private VPlanPendingDelegationService planPendingDelegationService;

	@RequestMapping("/loadBuy")
	public String loadBuyPending(Model mo) {
		List<VTransaction> transactionList = super.findCurrTrans();
		if (transactionList != null && transactionList.size() > 0) {

			VTransaction currTran = transactionList.get(0);
			PagerModel pm = vPendingDelegationService.getPagerModel(Contents.DIRECTION_BUY, 0l, null, currTran.getTransaction_code());// (currTran.getTransaction_code());

			mo.addAttribute("currtran", currTran.getTransaction_code());
			mo.addAttribute("trans", transactionList);
			mo.addAttribute("pm", pm);

			mo.addAttribute("dk", vPendingDelegationService.getSumByAction(currTran.getTransaction_code(), Contents.DIRECTION_BUY, Contents.PRIORITY_KC)); // 多头开仓总量
			mo.addAttribute("kp", vPendingDelegationService.getSumByAction(currTran.getTransaction_code(), Contents.DIRECTION_SELL, Contents.PRIORITY_PC)); // 空头平仓总量

		}

		mo.addAttribute("page", "买入");
		mo.addAttribute("url", "selectBuy");
		return super.url_JSP("pendingbuylist");
	}

	@RequestMapping("/selectBuy")
	public String selectBuyPending(String tcode, String userid, String useremail, HttpServletRequest request, Model mo) {
		List<VTransaction> transactionList = super.findCurrTrans();
		VTransaction transaction = super.findTransByCode(tcode);

		if (transaction != null) {
			long iuserID = ConvertUtil.toLong(userid, 0l);
			if (iuserID == 0) {
				BQMember bqMember = super.findUsersByEmail(useremail);
				if (bqMember != null)
					iuserID = bqMember.getF_user_id();
			}

			PagerModel pm = vPendingDelegationService.getPagerModel(Contents.DIRECTION_BUY, iuserID, useremail, transaction.getTransaction_code());// (currTran.getTransaction_code());
			mo.addAttribute("currtran", transaction.getTransaction_code());
			mo.addAttribute("pm", pm);
			mo.addAttribute("userid", userid);
			mo.addAttribute("useremail", useremail);

			mo.addAttribute("dk", vPendingDelegationService.getSumByUIDAction(transaction.getTransaction_code(), iuserID, Contents.DIRECTION_BUY,
					Contents.PRIORITY_KC)); // 多头开仓总量
			mo.addAttribute("kp", vPendingDelegationService.getSumByUIDAction(transaction.getTransaction_code(), iuserID, Contents.DIRECTION_SELL,
					Contents.PRIORITY_PC)); // 空头平仓总量

		} else {
			PagerModel pm = new PagerModel();
			pm.setTotal(0);
			mo.addAttribute("currtran", tcode);
			mo.addAttribute("pm", pm);
			mo.addAttribute("userid", userid);
			mo.addAttribute("useremail", useremail);
		}

		mo.addAttribute("trans", transactionList);
		mo.addAttribute("page", "买入");
		mo.addAttribute("url", "selectBuy");

		return super.url_JSP("pendingbuylist");
	}

	@RequestMapping("/loadSell")
	public String loadSellPending(Model mo) {
		List<VTransaction> transactionList = super.findCurrTrans();
		if (transactionList != null && transactionList.size() > 0) {

			VTransaction currTran = transactionList.get(0);
			PagerModel pm = vPendingDelegationService.getPagerModel(Contents.DIRECTION_SELL, 0l, null, currTran.getTransaction_code());

			mo.addAttribute("currtran", currTran.getTransaction_code());
			mo.addAttribute("trans", transactionList);
			mo.addAttribute("pm", pm);

			mo.addAttribute("kk", vPendingDelegationService.getSumByAction(currTran.getTransaction_code(), Contents.DIRECTION_SELL, Contents.PRIORITY_KC)); // 空头开仓总量
			mo.addAttribute("dp", vPendingDelegationService.getSumByAction(currTran.getTransaction_code(), Contents.DIRECTION_BUY, Contents.PRIORITY_PC)); // 多头平仓总量

		}
		mo.addAttribute("page", "卖出");
		mo.addAttribute("url", "selectSell");

		return super.url_JSP("pendingbuylist");
	}

	@RequestMapping("/selectSell")
	public String selectSellPending(String tcode, String userid, String useremail, HttpServletRequest request, Model mo) {
		List<VTransaction> transactionList = super.findCurrTrans();
		VTransaction transaction = super.findTransByCode(tcode);

		if (transaction != null) {
			long iuserID = ConvertUtil.toLong(userid, 0l);
			if (iuserID == 0) {
				BQMember bqMember = super.findUsersByEmail(useremail);
				if (bqMember != null)
					iuserID = bqMember.getF_user_id();
			}

			PagerModel pm = vPendingDelegationService.getPagerModel(Contents.DIRECTION_SELL, iuserID, useremail, transaction.getTransaction_code());
			mo.addAttribute("currtran", transaction.getTransaction_code());
			mo.addAttribute("pm", pm);
			mo.addAttribute("userid", userid);
			mo.addAttribute("useremail", useremail);

			mo.addAttribute("kk", vPendingDelegationService.getSumByUIDAction(transaction.getTransaction_code(), iuserID, Contents.DIRECTION_SELL,
					Contents.PRIORITY_KC)); // 空头开仓总量
			mo.addAttribute("dp", vPendingDelegationService.getSumByUIDAction(transaction.getTransaction_code(), iuserID, Contents.DIRECTION_BUY,
					Contents.PRIORITY_PC)); // 多头平仓总量

		} else {
			PagerModel pm = new PagerModel();
			pm.setTotal(0);
			mo.addAttribute("currtran", tcode);
			mo.addAttribute("pm", pm);
			mo.addAttribute("userid", userid);
			mo.addAttribute("useremail", useremail);
		}

		mo.addAttribute("trans", transactionList);
		mo.addAttribute("page", "卖出");
		mo.addAttribute("url", "selectSell");

		return super.url_JSP("pendingbuylist");
	}
	
	@RequestMapping("/planPending")
	public String planPending(String tcode, String userid, String useremail, String direction, String status, String type, Model mo) {
		List<VTransaction> transactionList = super.findCurrTrans();
		VTransaction currTran = transactionList.get(0);
		if (!StringUtil.isEmpty(tcode)) {
			for (VTransaction bqTransaction : transactionList) {
				if (tcode.equals(bqTransaction.getTransaction_code())) {
					currTran = bqTransaction;
				}
			}
		}
		
		PagerModel pm = new PagerModel();
		if (currTran != null) {
			long iuserID = ConvertUtil.toLong(userid, 0l);
			if (iuserID == 0) {
				BQMember bqMember = super.findUsersByEmail(useremail);
				if (bqMember != null)
					iuserID = bqMember.getF_user_id();
			}
			
			int iDirection = ConvertUtil.toInt(direction, 0);
			int iStatus = ConvertUtil.toInt(status, -1);
			int iType = ConvertUtil.toInt(type, 0);

			pm = planPendingDelegationService.getPagerModel(currTran.getTransaction_code(), iuserID, iDirection, iStatus, iType);
		}
		
		mo.addAttribute("currtran", currTran.getTransaction_code());
		mo.addAttribute("userid", userid);
		mo.addAttribute("useremail", useremail);
		mo.addAttribute("direction", direction);
		mo.addAttribute("status", status);
		mo.addAttribute("type", type);
		mo.addAttribute("trans", transactionList);
		mo.addAttribute("url", "planPending");
		mo.addAttribute("pm", pm);
		
		return super.url_JSP("planPendingList");
	}

}
