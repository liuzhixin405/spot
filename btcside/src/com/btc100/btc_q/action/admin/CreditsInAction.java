package com.btc100.btc_q.action.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQCommonCreditLog;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.service.BQCommonCreditLogService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;

@Controller
@RequestMapping("/managerCredit")
public class CreditsInAction extends BaseAction {
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQCommonCreditLogService bqCommonCreditLogService;

	@RequestMapping("/loadRecords")
	public String load(String uid, String uemail, HttpServletRequest request, Model mo) {
		long uId = ConvertUtil.toLong(uid, 0l);
		BQMember user = new BQMember();
		if (uId > 0) {
			user = bqMemberService.readUserById(uId);
			if (user == null) {
				return super.errorPage(mo, "查不到该用户");
			}
		}
		if (!StringUtil.isEmpty(uemail)) {
			user = bqMemberService.readUserByEmail(uemail);
			if (user == null) {
				return super.errorPage(mo, "查不到该用户");
			}
			uId = user.getF_user_id();
		}
		
		mo.addAttribute("pm", bqCommonCreditLogService.readAddOrSubRecords(uId));
		mo.addAttribute("uid", uid);
		mo.addAttribute("uemail", uemail);
		mo.addAttribute("currCredit", user.getF_user_credits());
		
		return super.url_JSP("creditslist");

	}

	@RequestMapping("/addRecords")
	public String add(String uid, String credit, HttpServletRequest request, Model mo, String direction) {
		long iuid = ConvertUtil.toLong(uid, 0l);

		long icredit = ConvertUtil.toLong(credit, 0L);
		int operation = ConvertUtil.toInt(direction, 0);
		if (operation == 0) {
			return super.errorPage(mo, "积分方向错误");
		}

		BQMember user = new BQMember();
		if (iuid > 0) {
			user = bqMemberService.readUserById(iuid);
			if (user == null) {
				return super.errorPage(mo, "查不到该用户");
			}
		}
		
		long currCredit = user.getF_user_credits();
		// 增加积分记录
		if (icredit > 0) {
			BQCommonCreditLog bqcl2 = new BQCommonCreditLog();
			bqcl2.setF_user_id(iuid);
			bqcl2.setCredits(icredit);
			bqcl2.setDateline(bqCommonCreditLogService.getTime());
			if (operation > 0) {
				bqcl2.setCredit_type_id(Contents.CREDIT_TYPE_BUY);
				bqcl2.setTitle("后台添加");
				bqCommonCreditLogService.saveCreditsAndUpdateLevel(bqcl2, true, 1);
				currCredit += icredit;
			} else {
				if (icredit > user.getF_user_credits()) {
					return super.errorPage(mo, "扣除积分不能大于用户本身积分");
				}
				bqcl2.setCredit_type_id(Contents.CREDIT_TYPE_SUBTRACT);
				bqcl2.setTitle("扣除积分");
				bqCommonCreditLogService.saveCreditsAndUpdateLevel(bqcl2, true, -1);
				currCredit += -icredit;
			}
		}
		
		mo.addAttribute("uid", uid);
		mo.addAttribute("credit", credit);
		mo.addAttribute("direction", direction);
		mo.addAttribute("currCredit", currCredit);
		mo.addAttribute("pm", bqCommonCreditLogService.readAddOrSubRecords(iuid));
		
		return super.url_JSP("creditslist");

	}
	

}
