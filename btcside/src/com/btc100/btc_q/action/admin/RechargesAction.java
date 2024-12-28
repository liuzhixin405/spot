package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.dao.BQMemberWalletDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountAmountLog;
import com.btc100.btc_q.entity.BQCommonCreditLog;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberRecharge;
import com.btc100.btc_q.entity.BQMemberWallet;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQAccountAmountLogService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCommonCreditLogService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberRechargeService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerRecharge")
public class RechargesAction extends BaseAction {
	@Autowired
	private BQMemberRechargeService bqMemberRechargeService;
	@Autowired
	private BQMemberWalletDao<BQMemberWallet> bqMemberWalletDao;
	@Autowired
	private BQCommonCreditLogService bqCommonCreditLogService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService; 
	@Autowired
	private BQAccountAmountLogService bqAccountAmountLogService;
	@Autowired
	private BQMemberService bqMemberService;
	
	@RequestMapping("/loadRecords")
	public String load(HttpServletRequest request, Model mo) {
		String spage = request.getParameter("curr");
		int page = ConvertUtil.toInt(spage, 0);
		int type = 0;
		
		List<BQCurrencyType> currencyTypes = bqCurrencyTypeService.readAll();
		mo.addAttribute("currencyTypes", currencyTypes);
		
		String sCurrencyId = request.getParameter("currencyId");
		int currencyId = ConvertUtil.toInt(sCurrencyId, Contents.TRADE_CURRENCY_TYPE_ID);
		mo.addAttribute("currencyId", currencyId);
		
		if (page == 0 || page == 1) {
			type = 0;
		} else if (page == 2) {
			type = 1;
		} else if (page == 3) {
			type = 2;
		} else if (page == 4) {
			type = 1;
		} else if (page == 5) {
			type = 2;
		}

		String uaddr = request.getParameter("uaddr");
		String suid = request.getParameter("uid");
		String email = request.getParameter("uemail");
		String state = request.getParameter("state");
		mo.addAttribute("uid", suid);
		mo.addAttribute("uaddr", uaddr);
		mo.addAttribute("uemail", email);
		mo.addAttribute("state", state);
		
		long iuid = ConvertUtil.toLong(suid, 0l);
		if (page < 4) {
			mo.addAttribute("pm", bqMemberRechargeService.readCzByUserID(iuid, email, uaddr, type, Contents.RECHARGE_FLAG_NORMAL, currencyId, state));
		} else {
			BQMember user = null;
			if (iuid > 0) {
				user = bqMemberService.readUserById(iuid);
			} else if (!StringUtil.isEmpty(email)) {
				user = bqMemberService.readUserByEmail(email);
			}
			
			if (user != null) {
				iuid = user.getF_user_id();
			}
			
			PagerModel pm = bqAccountAmountLogService.readOTCRecords(iuid, currencyId, type);
			@SuppressWarnings("unchecked")
			List<BQAccountAmountLog> dataList = (List<BQAccountAmountLog>) pm.getDatas();
			if (pm.getTotal() > 0) {
				List<BQMemberRecharge> newList = new ArrayList<>();
				for (BQAccountAmountLog log : dataList) {
					BQMemberRecharge recharge = new BQMemberRecharge();
					recharge.setRecord_id(log.getLog_id());
					recharge.setCurrency_type_id(log.getCurrency_type_id());
					recharge.setOperation(type);
					recharge.setAmount(log.getChange_amount());
					recharge.setFee(BigDecimal.ZERO);
					recharge.setReceice_card_type(1);
					recharge.setReceice_card_no("");
					recharge.setIp("");
					recharge.setConfirm_time(log.getDateline());
					recharge.setState(1);
					recharge.setRemark(log.getTitle());
					recharge.setF_user_id(log.getF_user_id());
					recharge.setF_user_email(user == null ? "" : user.getF_user_email());
					recharge.setWallet_trade_id("");
					
					newList.add(recharge);
				}
				pm.setDatas(newList);
			}
			
			mo.addAttribute("pm", pm);
		}
//		mo.addAttribute("pm", bqMemberRechargeService.findRecord(type, Contents.RECHARGE_FLAG_NORMAL, currencyId));
		mo.addAttribute("currpage", page);

		return super.url_JSP("rechargelist");

	}

	@RequestMapping("/updateRecord")
	public String update(int curr, int currencyId, HttpServletRequest request, Model mo) throws ServiceException {

		long id = ConvertUtil.toLong(request.getParameter("id"), 0l);
		int action = ConvertUtil.toInt(request.getParameter("action"), 0);
		String remark = request.getParameter("remark");
		if (remark == null)
			remark = "";
		int type = ConvertUtil.toInt(request.getParameter("type"), 0);
		if (type == Contents.RECHARGE_TYPE_CZ)
			bqMemberRechargeService.rechargeCZ(id, action, remark);
		else if (type == Contents.RECHARGE_TYPE_TX)
			bqMemberRechargeService.rechargeTX(id, action, remark);

		mo.addAttribute("currencyId", currencyId);
		mo.addAttribute("curr", curr);

		return super.redirect("/managerRecharge/loadRecords.do");
	}

	@RequestMapping("/addRecord")
	public String add(int curr, HttpServletRequest request, Model mo) throws ServiceException {

		long id = ConvertUtil.toLong(request.getParameter("id"), 0l);
		int action = ConvertUtil.toInt(request.getParameter("action"), 0);
		String remark = request.getParameter("remark");
		if (remark == null)
			remark = "";
		int type = ConvertUtil.toInt(request.getParameter("type"), 0);
		if (type == Contents.RECHARGE_TYPE_CZ)
			bqMemberRechargeService.rechargeCZ(id, action, remark);

		mo.addAttribute("curr", curr);

		return super.redirect("/managerRecharge/loadRecords.do");// ("rechargelist");
	}

	@RequestMapping("/giveCZ")
	public String giveCZ(HttpServletRequest request, String address, int currencyId, String cid, String useremail, String amount, String curr, Model mo) throws Exception {
		String password = request.getParameter("password");
		if (!super.checkYZPwd(request, password)) {
			return super.errorPage(mo, "安全密码不正确");
		}
		
		if (ConvertUtil.toDouble(amount, 0) <= 0) {
			return super.errorPage(mo, "充值金额不正确");
		}
		
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(currencyId);
		if (currencyType == null) {
			return super.errorPage(mo, "币种不正确");
		}

		BigDecimal czamount = new BigDecimal(amount).setScale(8, BigDecimal.ROUND_DOWN);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("address", address.trim());
		BQMemberWallet bw = bqMemberWalletDao.get("FROM BQMemberWallet b WHERE b.wallet_address = :address", map);
		if (bw == null) {
			return super.errorPage(mo, "没有找到该地址对应的用户");
		}

		if (bqMemberRechargeService.readByTradeID(cid, currencyId) != null) {
			return super.errorPage(mo, "交易ID不能重复");
		}

		Long uid = bw.getF_user_id();
		bqMemberRechargeService.addCZ(uid, currencyId, czamount, address, cid, Contents.RECHARGE_FLAG_NORMAL, "");

		mo.addAttribute("currencyId", currencyId);
		mo.addAttribute("curr", curr);
		return super.redirect("/managerRecharge/loadRecords.do?");
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////管理员充值部分
	@RequestMapping("/loadAdmin")
	public String loadadmin(HttpServletRequest request, Model mo) {
		
		String spage = request.getParameter("curr");
		int page = ConvertUtil.toInt(spage, 0);
		int type = 0;
		int isadmin = 0;
		
		List<BQCurrencyType> currencyTypes = bqCurrencyTypeService.readAll();
		mo.addAttribute("currencyTypes", currencyTypes);
		
		String sCurrencyId = request.getParameter("currencyId");
		int currencyId = ConvertUtil.toInt(sCurrencyId, Contents.TRADE_CURRENCY_TYPE_ID);
		mo.addAttribute("currencyId", currencyId);
		
		if (page == 0) {
			type = 0;
		} else if (page == 1 || page == 3) {
			type = 1;
		} else if (page == 2 || page == 4) {
			type = 2;
		}
		if (page == 1 || page == 2) {
			isadmin = Contents.RECHARGE_FLAG_ADMIN;
		} else if (page == 3 || page == 4) {
			isadmin = Contents.RECHARGE_FLAG_ADMIN_AWARD;
		}

		String uaddr = request.getParameter("uaddr");
		String suid = request.getParameter("uid");
		String email = request.getParameter("uemail");
		mo.addAttribute("uid", suid);
		mo.addAttribute("uaddr", uaddr);
		mo.addAttribute("uemail", email);
		
		long iuid = ConvertUtil.toLong(suid, 0l);
		mo.addAttribute("pm", bqMemberRechargeService.readCzByUserID(iuid, email, uaddr, type, isadmin, currencyId));
		mo.addAttribute("currpage", page);
		
		return super.url_JSP("adminrechargelist");
	}

	@RequestMapping("/adminCZ")
	public String adminCZ(HttpServletRequest request, int currencyid, String uemail, int type, String amt, String remark, String surepwd, String curr, Model mo)
			throws Exception {
		if (!super.checkYZPwd(request, surepwd)) {
			return super.errorPage(mo, "安全密码不正确");
		}
		
		if (currencyid <= 0) {
			return super.errorPage(mo, "未知币种");
		}

		if (ConvertUtil.toDouble(amt, 0) <= 0) {
			if (type == Contents.RECHARGE_TYPE_CZ) {
				return super.errorPage(mo, "充值数量不正确");
			} else if (type == Contents.RECHARGE_TYPE_TX) {
				return super.errorPage(mo, "提现数量不正确");
			} else if (type == Contents.RECHARGE_TYPE_CZ_AWARD) {
				return super.errorPage(mo, "奖励数量不正确");			
			} else if (type == Contents.RECHARGE_TYPE_TX_AWARD) {
				return super.errorPage(mo, "扣除奖励数量不正确");
			}
		}

		BigDecimal czamount = new BigDecimal(amt);
		BQMember bqMember = super.findUsersByEmail(uemail);

		if (bqMember == null) {
			return super.errorPage(mo, "不存在该用户");
		}

		Long uid = bqMember.getF_user_id();

		if (type == Contents.RECHARGE_TYPE_CZ) {
			bqMemberRechargeService.addCZ(uid, currencyid, czamount, "", "", Contents.RECHARGE_FLAG_ADMIN, remark);
		} else if (type == Contents.RECHARGE_TYPE_TX) {
			BQAccount bqAccount = bqAccountService.readAccount(uid, currencyid);
			if (czamount.compareTo(bqAccount.getBtc()) > 0) {
				return super.errorPage(mo, "转出数量大于用户余额");
			}
			bqMemberRechargeService.addTX(uid, currencyid, czamount, "", "", Contents.RECHARGE_FLAG_ADMIN, remark);
		} else if (type == Contents.RECHARGE_TYPE_CZ_AWARD) {
			bqMemberRechargeService.addCZ(uid, currencyid, czamount, "", "", Contents.RECHARGE_FLAG_ADMIN_AWARD, remark);
		} else if (type == Contents.RECHARGE_TYPE_TX_AWARD) {
			BQAccount bqAccount = bqAccountService.readAccount(uid, currencyid);
			if (czamount.compareTo(bqAccount.getBtc()) > 0) {
				return super.errorPage(mo, "扣除数量大于用户余额");
			}
			bqMemberRechargeService.addTX(uid, currencyid, czamount, "", "", Contents.RECHARGE_FLAG_ADMIN_AWARD, remark);
		}
		mo.addAttribute("curr", curr);
		return super.redirect("/managerRecharge/loadAdmin.do?typeId=" + currencyid);
	}

	@RequestMapping("/adminTX")
	public String adminTX(HttpServletRequest request, int currencyid, String uemail, String amt, String remark, String surepwd, String curr, Model mo) throws Exception {
		if (!super.checkYZPwd(request, surepwd)) {
			return super.errorPage(mo, "安全密码不正确");
		}
		
		if (currencyid <= 0) {
			return super.errorPage(mo, "未知币种");
		}

		if (ConvertUtil.toDouble(amt, 0) <= 0) {
			return super.errorPage(mo, "转出数量不正确");
		}

		BigDecimal czamount = new BigDecimal(amt);
		BQMember bqMember = super.findUsersByEmail(uemail);

		if (bqMember == null) {
			return super.errorPage(mo, "不存在该用户");
		}

		Long uid = bqMember.getF_user_id();
		bqMemberRechargeService.addTX(uid, currencyid, czamount, "", "", Contents.RECHARGE_FLAG_ADMIN, remark);

		mo.addAttribute("curr", curr);
		return super.redirect("/managerRecharge/loadAdmin.do?typeId=" + currencyid);
	}

	@RequestMapping("/adminJF")
	public String adminJF(HttpServletRequest request, String uemail, String amount, String remark, String surepwd, String curr, Model mo) throws Exception {
		if (!super.checkYZPwd(request, surepwd)) {
			return super.errorPage(mo, "安全密码不正确");
		}

		long iamout = ConvertUtil.toLong(amount, 0L);
		if (iamout <= 0) {
			return super.errorPage(mo, "转出数量不正确");
		}

		BigDecimal czamount = new BigDecimal(amount);
		BQMember bqMember = super.findUsersByEmail(uemail);

		if (bqMember == null) {
			return super.errorPage(mo, "不存在该用户");
		}

		BQCommonCreditLog plog = new BQCommonCreditLog();
		plog.setCredit_type_id(Contents.CREDIT_TYPE_AWARD);
		plog.setCredits(iamout);
		plog.setDateline(System.currentTimeMillis() / 1000);
		plog.setF_user_id(bqMember.getF_user_id());
		plog.setTitle(remark);

		bqCommonCreditLogService.save(plog);

		Long uid = bqMember.getF_user_id();
		bqMemberRechargeService.addCZ(uid, Contents.TRADE_CURRENCY_TYPE_ID, czamount, "", "", Contents.RECHARGE_FLAG_ADMIN, remark);

		mo.addAttribute("curr", curr);
		return super.redirect("/managerRecharge/loadAdmin.do");
	}

	@RequestMapping("/loadZZRecords")
	public String loadZZ(HttpServletRequest request, Model mo) {
		String spage = request.getParameter("curr");
		int page = ConvertUtil.toInt(spage, 0);
		int type = 0;

		if (page == 4) {
			type = Contents.RECHARGE_TYPE_ZR;
		} else if (page == 5) {
			type = Contents.RECHARGE_TYPE_ZC;
		}

		mo.addAttribute("pm", bqMemberRechargeService.readRecords(type, Contents.RECHARGE_FLAG_NORMAL, Contents.TRADE_CURRENCY_TYPE_ID));
		mo.addAttribute("currpage", page);

		return super.url_JSP("rechargelist");

	}

}
