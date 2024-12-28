package com.btc100.btc_q.action.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.DATA_CreditNumber;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberInviteLog;
import com.btc100.btc_q.service.BQMemberInviteLogService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.RegisterSerivce;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerAgent")
public class AgentAction extends BaseAction {

	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private RegisterSerivce registerService;
	@Autowired
	private BQMemberInviteLogService bqMemberInviteLogService;

	@RequestMapping(value = "/load")
	public String load(HttpServletRequest request, Model model, String userid, String useremail) {
		BQMember user = new BQMember();
		if (!StringUtil.isEmpty(userid) || !StringUtil.isEmpty(useremail)) {
			user = selectUser(userid, useremail);
			if (user == null) {
				return super.errorPage(model, "用户不存在");
			}
		}

		PagerModel pm = bqMemberService.readByIdentity(1, user.getF_user_id(), user.getF_user_email());
		model.addAttribute("pm", pm);
		model.addAttribute("userid", userid);
		model.addAttribute("useremail", useremail);
		model.addAttribute("uid", userid);
		model.addAttribute("umail", useremail);
		model.addAttribute("pgN", "/managerAgent/load.do");

		return super.url_JSP("managerAgentList");
	}
	
	@RequestMapping(value = "/loadInviter")
	public String loadInviter(HttpServletRequest request, Model model, String userid, String useremail) {
		BQMember user = new BQMember();
		if (!StringUtil.isEmpty(userid) || !StringUtil.isEmpty(useremail)) {
			user = selectUser(userid, useremail);
			if (user == null) {
				return super.errorPage(model, "用户不存在");
			}
		}
		BQMemberInviteLog inviteLog = bqMemberInviteLogService.readByUserId(user.getF_user_id());
		if (inviteLog == null) {
			return super.errorPage(model, "代理人不存在");
		}
		BQMember inviter = bqMemberService.findUserById(inviteLog.getInvite_uid());
		if (inviter == null) {
			return super.errorPage(model, "代理人不存在");
		}
		
		PagerModel pm = bqMemberService.readByIdentity(1, inviter.getF_user_id(), inviter.getF_user_email());
		model.addAttribute("pm", pm);
		model.addAttribute("userid", userid);
		model.addAttribute("useremail", useremail);
		model.addAttribute("userId", userid);
		model.addAttribute("userEmail", useremail);
		model.addAttribute("pgN", "/managerAgent/loadInviter.do");
		
		return super.url_JSP("managerAgentList");
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(HttpServletRequest request, Model model) {
		String useremail = request.getParameter("useremail");
		String pwd1 = request.getParameter("pwd1");
		String pwd2 = request.getParameter("pwd2");
		if (StringUtil.isEmpty(useremail) || StringUtil.isEmpty(pwd1)
				|| StringUtil.isEmpty(pwd2)) {
			return super.errorPage(model, "请填写完整信息");
		}
		
		String reg = "^[A-Za-z0-9][\\w\\-\\.]{3,12}@([\\w\\-]+\\.)+[\\w]{2,3}$";
		if (!useremail.matches(reg)) {
			return super.errorPage(model, "邮箱格式不正确");
		}
		BQMember bqm = bqMemberService.readByRealEmail(useremail);
		if (bqm != null) {
			return super.errorPage(model, "该邮箱已注册");
		}
		if (!pwd1.equals(pwd2)) {
			return super.errorPage(model, "两次密码输入错误");
		}
		if (pwd1.length() < 8 || pwd1.length() > 20) {
			return super.errorPage(model, "密码长度在8到20之间");
		}
		
		String random6 = RandomUtil.getRandomString(6);
		try {
			pwd1 = MD5Service.encryptString(pwd1).toLowerCase();
			pwd1 = MD5Service.encryptString(pwd1 + random6).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		BQMember bqMem = new BQMember();
		bqMem.setF_user_email(useremail);
		bqMem.setF_real_email(useremail);
		bqMem.setF_user_email_status(1);
		bqMem.setF_user_pwd(pwd1);
		bqMem.setF_user_credits(DATA_CreditNumber.CREDIT_ONE_REGISTER + DATA_CreditNumber.CREDIT_ONE_SUREEMAIL);
		bqMem.setF_user_level_id(1);// 用户VIP级别(bq_member_level:level_id)',
		bqMem.setF_user_state(1);// '用户状态（0:无效,1:有效,-1:冻结）'
		bqMem.setF_user_identity(1);// 代理人
		bqMem.setF_user_salt(random6);
		bqMem.setF_user_origin("");
		bqMem.setF_first_login(1);
		// java获取时间戳
		long agentTime = System.currentTimeMillis() / 1000;
		bqMem.setF_user_time((int) agentTime);
		
		String sip = IpMacUtil.getIpAddr(request);
		
		registerService.RegisterSuccess(bqMem, "", sip, "cn", "110000", "110100", "中国");
		
		return super.redirect("load.do");
	}
	
	/**
	 * 普通用户转代理人
	 * @param request
	 * @param model
	 * @param userid
	 * @param useremail
	 * @return
	 */
	@RequestMapping(value = "/userToAgent", method = RequestMethod.POST)
	public String userToAgent(HttpServletRequest request, Model model, String userid, String useremail) {
		if (StringUtil.isEmpty(userid) && StringUtil.isEmpty(useremail)) {
			return super.errorPage(model, "用户Id和用户邮箱不能同时为空");
		}

		BQMember user = selectUser(userid, useremail);
		if (user == null) {
			return super.errorPage(model, "用户不存在");
		}
		if (user.getF_user_identity() == 1) {
			return super.errorPage(model, "该用户已经是代理人");
		}
		user.setF_user_identity(1);
		user.setF_user_oms(1);
		bqMemberService.update(user);
		
		// 查询该用户底下所有层级被邀请人(不包含非代理人，以及不包含该名非代理人底下的所有层级被邀请人)，更新被邀请人的f_user_oms为1
		List<Integer> invitees = bqMemberInviteLogService.readAllInviteesNotAgent(user.getF_user_id(), 0);
		if (invitees.size() > 0) {
			for (Integer uid : invitees) {
				bqMemberService.updateOms(uid.longValue(), 1);
			}
		}

		return super.redirect("load.do");
	}
	
	/**
	 * 代理人转普通用户
	 * @param request
	 * @param model
	 * @param userid
	 * @param useremail
	 * @return
	 */
	@RequestMapping(value = "/agentToUser", method = RequestMethod.POST)
	public String agentToUser(HttpServletRequest request, Model model, String userid, String useremail) {
		if (StringUtil.isEmpty(userid) && StringUtil.isEmpty(useremail)) {
			return super.errorPage(model, "用户Id和用户邮箱不能同时为空");
		}
		
		BQMember user = selectUser(userid, useremail);
		if (user == null) {
			return super.errorPage(model, "用户不存在");
		}
		if (user.getF_user_identity() == 0) {
			return super.errorPage(model, "该用户不是代理人");
		}
		user.setF_user_identity(0);
		bqMemberService.update(user);
		
		// 先判断该用户上级用户身份是否有代理人，有则不修改f_user_oms为0
		BQMemberInviteLog log = bqMemberInviteLogService.readAgent(user.getF_user_id());
		if (log != null) {
			return super.redirect("load.do");
		}
		
		bqMemberService.updateOms(user.getF_user_id(), 0);
		// 查询该用户底下所有层级被邀请人(不包含非代理人，以及不包含该名非代理人底下的所有层级被邀请人)，更新被邀请人的f_user_oms为0
		List<Integer> invitees = bqMemberInviteLogService.readAllInviteesNotAgent(user.getF_user_id(), 1);
		if (invitees.size() > 0) {
			for (Integer uid : invitees) {
				bqMemberService.updateOms(uid.longValue(), 0);
			}
		}
		
		return super.redirect("load.do");
	}

	private BQMember selectUser(String userId, String userEmail) {
		long uid = ConvertUtil.toLong(userId, 0l);
		BQMember user = bqMemberService.findUserById(uid);
		if (user == null) {
			user = bqMemberService.findUserByEmail(userEmail);
		}
		return user;
	}

}
