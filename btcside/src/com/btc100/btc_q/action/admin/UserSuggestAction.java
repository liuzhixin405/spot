package com.btc100.btc_q.action.admin;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQSuggestService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping(value = "/managerUserSuggest")
public class UserSuggestAction extends BaseAction {

	@Autowired
	private BQSuggestService bqSuggestService;
	@Autowired
	private BQMemberService bqMemberService;

	@RequestMapping("/loadUserSuggest")
	public String loadUserSuggest(Model model) {
		PagerModel pm = bqSuggestService.findAllSugg(-2);
		model.addAttribute("pm", pm);
		return super.url_JSP("suggestlist");
	}

	@RequestMapping("/searchUserSuggest")
	public String searchUserSuggest(String uid, String ustate, Model model) {
		long id = ConvertUtil.toLong(uid, 0l); // 用户ID

		int state = ConvertUtil.toInt(ustate, 0);
		if (ustate == null || ustate.equals("")) {
			state = -2;
		}

		if (id > 0) {
			PagerModel pm = bqSuggestService.findSuggByUserId(id);
			model.addAttribute("pm", pm);
			model.addAttribute("id", id);
		} else {
			PagerModel pm = bqSuggestService.findAllSugg(state);
			model.addAttribute("pm", pm);
			model.addAttribute("state", state);
		}
		return super.url_JSP("suggestlist");
	}

	@RequestMapping("/updateUserSuggest")
	public String updateUserSuggest(String id, String uid, String state, String sugReply) {
		long userId = ConvertUtil.toLong(uid, 0l); // 用户ID
		long sid = ConvertUtil.toLong(id, 0l); // 意见ID
		int ustate = ConvertUtil.toInt(state, 0); // 类型，标识通过1或拒绝2
		sugReply = sugReply == null ? "" : sugReply;

		try {
			sugReply = new String(sugReply.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		
		bqSuggestService.updateApplyByParas(sid, ustate, sugReply);
		
		BQMember user = bqMemberService.readUserById(userId);
		if (user == null) {
			return redirect("/managerUserSuggest/loadUserSuggest.do");
		}
		
		/*
		if (ustate == 1 && user.getF_user_oms() == 0) { // 意见采纳
			BQCommonCreditLog bqcl2 = new BQCommonCreditLog();
			bqcl2.setF_user_id(userId);
			bqcl2.setCredit_type_id(Contents.CREDIT_TYPE_AWARD);
			bqcl2.setCredits(DATA_CreditNumber.CREDIT_ONE_AGGEST);
			bqcl2.setTitle("意见采纳");
			bqcl2.setDateline(bqSuggestService.getTime());
			bqCommonCreditLogService.saveCreditsAndUpdateLevel(bqcl2, false, 1);
		}
		*/
		
		return redirect("/managerUserSuggest/loadUserSuggest.do");
	}

}
