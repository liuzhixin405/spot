package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.btc100.btc_q.entity.BQUserTradeWhite;
import com.btc100.btc_q.service.BQUserTradeWhiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.service.PendingDelegationCacheService;
import com.btc100.btc_q.service.PendingDelegationService;
import com.btc100.btc_q.service.PlanPendingDelegationService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerPending")
public class PendingsAction extends BaseAction {

	@Autowired
	private PendingDelegationService pendingDelegationService;
	@Autowired
	private PendingDelegationCacheService pendingDelegationCacheService;
	@Autowired
	private PlanPendingDelegationService planPendingDelegationService;
	@Autowired
	private BQUserTradeWhiteService bqUserTradeWhiteService;

	@RequestMapping("/loadBuy")
	public String loadBuyPending(Model mo) {
		List<BQTransaction> transactionList = super.findCurrTrans();
		if (transactionList != null && transactionList.size() > 0) {

			BQTransaction currTran = transactionList.get(0);
			PagerModel pm = pendingDelegationService.getPagerModel(Contents.DIRECTION_BUY, 0l, null, currTran.getTransaction_code());// (currTran.getTransaction_code());

			mo.addAttribute("currtran", currTran.getTransaction_code());
			mo.addAttribute("trans", transactionList);
			mo.addAttribute("pm", pm);

			mo.addAttribute("dk", pendingDelegationService.getSumByAction(currTran.getTransaction_code(), Contents.DIRECTION_BUY, Contents.PRIORITY_KC)); // 多头开仓总量
			mo.addAttribute("kp", pendingDelegationService.getSumByAction(currTran.getTransaction_code(), Contents.DIRECTION_SELL, Contents.PRIORITY_PC)); // 空头平仓总量

		}

		List<BQUserTradeWhite> whites = bqUserTradeWhiteService.findAll();
		List<String> whitesIds = new ArrayList<>();
		if (whites.size()>0 && null != whites) {
			for (BQUserTradeWhite tradeWhite : whites) {
				whitesIds.add(String.valueOf(tradeWhite.getF_user_id()));
			}
		}
		mo.addAttribute("white", whitesIds);
		mo.addAttribute("state", "submitted");
		mo.addAttribute("page", "买入");
		mo.addAttribute("url", "selectBuy");
		return super.url_JSP("pendingbuylist");
	}

	@RequestMapping("/selectBuy")
	public String selectBuyPending(String tcode, String userid, String useremail, String state, HttpServletRequest request, Model mo) {
		List<BQTransaction> transactionList = super.findCurrTrans();
		BQTransaction transaction = super.findTransByCode(tcode);

		if (transaction != null) {
			long iuserID = ConvertUtil.toLong(userid, 0l);
			if (iuserID == 0) {
				BQMember bqMember = super.findUsersByEmail(useremail);
				if (bqMember != null)
					iuserID = bqMember.getF_user_id();
			}
			
			if (StringUtil.isEmpty(state)) {
				state = "submitted";
			}

			PagerModel pm = new PagerModel();
			BigDecimal dk = BigDecimal.ZERO;
			BigDecimal kp = BigDecimal.ZERO;
			if ("submitted".equals(state)) {
				pm = pendingDelegationService.getPagerModel(Contents.DIRECTION_BUY, iuserID, useremail,
						transaction.getTransaction_code());
				dk = pendingDelegationService.getSumByUIDAction(transaction.getTransaction_code(), iuserID,
						Contents.DIRECTION_BUY, Contents.PRIORITY_KC);
				kp = pendingDelegationService.getSumByUIDAction(transaction.getTransaction_code(), iuserID,
						Contents.DIRECTION_SELL, Contents.PRIORITY_PC);
			} else {
				pm = pendingDelegationCacheService.readPagerModel(Contents.DIRECTION_BUY, iuserID, useremail,
						transaction.getTransaction_code());
				dk = pendingDelegationCacheService.getSumByUIDAction(transaction.getTransaction_code(), iuserID,
						Contents.DIRECTION_BUY, Contents.PRIORITY_KC);
				kp = pendingDelegationCacheService.getSumByUIDAction(transaction.getTransaction_code(), iuserID,
						Contents.DIRECTION_SELL, Contents.PRIORITY_PC);
			}
			
			mo.addAttribute("currtran", transaction.getTransaction_code());
			mo.addAttribute("pm", pm);
			mo.addAttribute("userid", userid);
			mo.addAttribute("useremail", useremail);
			mo.addAttribute("state", state);

			mo.addAttribute("dk", dk); // 多头开仓总量
			mo.addAttribute("kp", kp); // 空头平仓总量

		} else {
			PagerModel pm = new PagerModel();
			pm.setTotal(0);
			mo.addAttribute("currtran", tcode);
			mo.addAttribute("pm", pm);
			mo.addAttribute("userid", userid);
			mo.addAttribute("useremail", useremail);
			mo.addAttribute("state", state);
		}

		mo.addAttribute("trans", transactionList);
		mo.addAttribute("page", "买入");
		mo.addAttribute("url", "selectBuy");
		List<BQUserTradeWhite> whites = bqUserTradeWhiteService.findAll();
		List<String> whitesIds = new ArrayList<>();
		if (whites.size()>0 && null != whites) {
			for (BQUserTradeWhite tradeWhite : whites) {
				whitesIds.add(String.valueOf(tradeWhite.getF_user_id()));
			}
		}
		mo.addAttribute("white", whitesIds);

		return super.url_JSP("pendingbuylist");
	}

	@RequestMapping("/loadSell")
	public String loadSellPending(Model mo) {
		List<BQTransaction> transactionList = super.findCurrTrans();
		if (transactionList != null && transactionList.size() > 0) {

			BQTransaction currTran = transactionList.get(0);
			PagerModel pm = pendingDelegationService.getPagerModel(Contents.DIRECTION_SELL, 0l, null, currTran.getTransaction_code());

			mo.addAttribute("currtran", currTran.getTransaction_code());
			mo.addAttribute("trans", transactionList);
			mo.addAttribute("pm", pm);

			mo.addAttribute("kk", pendingDelegationService.getSumByAction(currTran.getTransaction_code(), Contents.DIRECTION_SELL, Contents.PRIORITY_KC)); // 空头开仓总量
			mo.addAttribute("dp", pendingDelegationService.getSumByAction(currTran.getTransaction_code(), Contents.DIRECTION_BUY, Contents.PRIORITY_PC)); // 多头平仓总量

		}
		mo.addAttribute("state", "submitted");
		mo.addAttribute("page", "卖出");
		mo.addAttribute("url", "selectSell");
		List<BQUserTradeWhite> whites = bqUserTradeWhiteService.findAll();
		List<String> whitesIds = new ArrayList<>();
		if (whites.size()>0 && null != whites) {
			for (BQUserTradeWhite tradeWhite : whites) {
				whitesIds.add(String.valueOf(tradeWhite.getF_user_id()));
			}
		}
		mo.addAttribute("white", whitesIds);

		return super.url_JSP("pendingbuylist");
	}

	@RequestMapping("/selectSell")
	public String selectSellPending(String tcode, String userid, String useremail, String state, HttpServletRequest request, Model mo) {
		List<BQTransaction> transactionList = super.findCurrTrans();
		BQTransaction transaction = super.findTransByCode(tcode);

		if (transaction != null) {
			long iuserID = ConvertUtil.toLong(userid, 0l);
			if (iuserID == 0) {
				BQMember bqMember = super.findUsersByEmail(useremail);
				if (bqMember != null)
					iuserID = bqMember.getF_user_id();
			}

			if (StringUtil.isEmpty(state)) {
				state = "submitted";
			}

			PagerModel pm = new PagerModel();
			BigDecimal kk = BigDecimal.ZERO;
			BigDecimal dp = BigDecimal.ZERO;
			if ("submitted".equals(state)) {
				pm = pendingDelegationService.getPagerModel(Contents.DIRECTION_SELL, iuserID, useremail,
						transaction.getTransaction_code());
				kk = pendingDelegationService.getSumByUIDAction(transaction.getTransaction_code(), iuserID,
						Contents.DIRECTION_SELL, Contents.PRIORITY_KC);
				dp = pendingDelegationService.getSumByUIDAction(transaction.getTransaction_code(), iuserID,
						Contents.DIRECTION_BUY, Contents.PRIORITY_PC);
			} else {
				pm = pendingDelegationCacheService.readPagerModel(Contents.DIRECTION_SELL, iuserID, useremail,
						transaction.getTransaction_code());
				kk = pendingDelegationCacheService.getSumByUIDAction(transaction.getTransaction_code(), iuserID,
						Contents.DIRECTION_SELL, Contents.PRIORITY_KC);
				dp = pendingDelegationCacheService.getSumByUIDAction(transaction.getTransaction_code(), iuserID,
						Contents.DIRECTION_BUY, Contents.PRIORITY_PC);
			}
			mo.addAttribute("currtran", transaction.getTransaction_code());
			mo.addAttribute("pm", pm);
			mo.addAttribute("userid", userid);
			mo.addAttribute("useremail", useremail);
			mo.addAttribute("state", state);

			mo.addAttribute("kk", kk); // 空头开仓总量
			mo.addAttribute("dp", dp); // 多头平仓总量

		} else {
			PagerModel pm = new PagerModel();
			pm.setTotal(0);
			mo.addAttribute("currtran", tcode);
			mo.addAttribute("pm", pm);
			mo.addAttribute("userid", userid);
			mo.addAttribute("useremail", useremail);
			mo.addAttribute("state", state);
		}

		mo.addAttribute("trans", transactionList);
		mo.addAttribute("page", "卖出");
		mo.addAttribute("url", "selectSell");
		List<BQUserTradeWhite> whites = bqUserTradeWhiteService.findAll();
		List<String> whitesIds = new ArrayList<>();
		if (whites.size()>0 && null != whites) {
			for (BQUserTradeWhite tradeWhite : whites) {
				whitesIds.add(String.valueOf(tradeWhite.getF_user_id()));
			}
		}
		mo.addAttribute("white", whitesIds);

		return super.url_JSP("pendingbuylist");
	}
	
	@RequestMapping("/planPending")
	public String planPending(String tcode, String userid, String useremail, String direction, String status, String type, Model mo) {
		List<BQTransaction> transactionList = super.findCurrTrans();
		BQTransaction currTran = transactionList.get(0);
		if (!StringUtil.isEmpty(tcode)) {
			for (BQTransaction bqTransaction : transactionList) {
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

		List<BQUserTradeWhite> whites = bqUserTradeWhiteService.findAll();
		List<String> whitesIds = new ArrayList<>();
		if (whites.size()>0 && null != whites) {
			for (BQUserTradeWhite tradeWhite : whites) {
				whitesIds.add(String.valueOf(tradeWhite.getF_user_id()));
			}
		}
		mo.addAttribute("white", whitesIds);

		return super.url_JSP("planPendingList");
	}

}
