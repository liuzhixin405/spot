package com.btc100.btc_q.action.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberStatusService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;

@Controller
@RequestMapping(value = "/managerUserIp")
public class UserIpAction extends BaseAction {

	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberStatusService bqMemberStatusService;
	
	@RequestMapping(value = "/loadUserIp")
	public String loadUserIP(HttpServletRequest request, Model model) {
		String userId = request.getParameter("userId");
		String userEmail = request.getParameter("email");
		BQMember user = this.selectUser(userId, userEmail);
		if (user == null) {
			model.addAttribute("pm", bqMemberStatusService.readUserIP(0));
		} else {
			model.addAttribute("pm", bqMemberStatusService.readUserIP(user.getF_user_id()));
			model.addAttribute("userId", user.getF_user_id());
			model.addAttribute("email", user.getF_user_email());
		}
		model.addAttribute("pageUrl", "loadUserIp");
		return super.url_JSP("userIPManage");
	}

	@RequestMapping(value = "/searchUserIp")
	public String searchUserIp(HttpServletRequest request, Model model) {
		String registIp = request.getParameter("registIp");
		String lastIp = request.getParameter("lastIp");

		if (StringUtil.isEmpty(registIp) && StringUtil.isEmpty(lastIp)) {
			model.addAttribute("pm", bqMemberStatusService.readUserIP(0));
		} else {

			model.addAttribute("pm", bqMemberStatusService.readUserIP(registIp.trim(), lastIp.trim()));
			model.addAttribute("lastIp", lastIp);
			model.addAttribute("registIp", registIp);
		}
		model.addAttribute("pageUrl", "searchUserIp");
		return super.url_JSP("userIPManage");
	}

	private BQMember selectUser(String userId, String userEmail) {
		long uid = ConvertUtil.toLong(userId, 0l);
		BQMember user = bqMemberService.readUserById(uid);
		if (user == null) {
			user = bqMemberService.readUserByEmail(userEmail);
		}
		return user;
	}

}
