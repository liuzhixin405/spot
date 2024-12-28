package com.btc100.btc_q.action.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.service.BQMemberInviteRebateLogService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerUserRebate")
public class ManagerUserRebateAction extends BaseAction {
	
	@Autowired
	private BQMemberInviteRebateLogService bqMemberInviteRebateLogService;
	@Autowired
	private BQMemberService bqMemberService;
	
	@RequestMapping("/loadUserRebateLog")
	public String order(String uid, String email, String bqCode, Model model) {
		long userId = 0;
		if (!StringUtil.isEmpty(uid)) {
			userId = Convert.toLong(uid, 0);
		}
		
		if (userId <= 0 && !StringUtil.isEmpty(email)) {
			BQMember user = bqMemberService.readUserByEmail(email);
			if (null != user) {
				userId = user.getF_user_id();
			}
		}
		
		model.addAttribute("bqCode", bqCode);
		model.addAttribute("email", email);
		model.addAttribute("uid", uid);
		
		PagerModel pm = bqMemberInviteRebateLogService.readAll(bqCode, userId);
		model.addAttribute("pm", pm);
		return super.url_JSP("userRebateLoglist");
	}
}
