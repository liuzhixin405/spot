package com.btc100.btc_q.action.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberInviteLog;
import com.btc100.btc_q.service.BQMemberInviteLogService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerInviteManagement")
public class InviteManagementAction extends BaseAction {

	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberInviteLogService bqMemberInviteLogService;

	@RequestMapping("/list")
	public String load(String userId, String uemail, String msg, Model mo) {
		long id = Convert.toLong(userId, 0);
		if (id > 0) {
			mo.addAttribute("userId", userId);
			mo.addAttribute("uid", userId);
		}

		if (id == 0 && !StringUtil.isEmpty(uemail)) {
			mo.addAttribute("uemail", uemail);
			BQMember user = bqMemberService.readUserByEmail(uemail);
			if (user != null) {
				id = user.getF_user_id();
				mo.addAttribute("uid", id);
			} else {
				PagerModel pm = new PagerModel();
				mo.addAttribute("pm", pm);
				return super.url_JSP("inviteManagementList");
			}
		}

		int inviteManagement = 1;
		PagerModel pm = bqMemberService.readByInviteManagement(inviteManagement, id);
		mo.addAttribute("pm", pm);

		return super.url_JSP("inviteManagementList");
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(String userId, Model mo) {
		long id = Convert.toLong(userId, 0);
		if (id <= 0) {
			return super.errorPage(mo, "用户id错误");
		}
		BQMember user = bqMemberService.readUserById(id);
		int inviteManagement = 1;
		if (user.getOpen_invite_management() != inviteManagement) {
			user.setOpen_invite_management(inviteManagement);
			bqMemberService.update(user);
		}

		return super.redirect("list.do?userId=" + id);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String delete(String userId, Model mo) {
		long id = Convert.toLong(userId, 0);
		if (id <= 0) {
			return super.errorPage(mo, "用户id错误");
		}
		BQMember user = bqMemberService.readUserById(id);
		int inviteManagement = 0;
		if (user.getOpen_invite_management() != inviteManagement) {
			user.setOpen_invite_management(inviteManagement);
			bqMemberService.update(user);
		}

		return super.redirect("list.do");
	}
	
	@RequestMapping("/inviteList")
	public String inviteList(String userId, String uemail, String msg, Model mo) {
		long id = Convert.toLong(userId, 0);
		BQMember user = null;
		if (id > 0) {
			user = bqMemberService.readUserById(id);
		}

		if (user == null && !StringUtil.isEmpty(uemail)) {
			user = bqMemberService.readUserByEmail(uemail);
		}
		mo.addAttribute("userId", userId);
		mo.addAttribute("uemail", uemail);
		if (user != null) {
			mo.addAttribute("userId", user.getF_user_id());
			mo.addAttribute("uemail", user.getF_user_email());
		}

		String parentEmail = "";
		if (user != null) {
			BQMemberInviteLog log = bqMemberInviteLogService.readByUserId(user.getF_user_id());
			if (log != null) {
				BQMember parent = bqMemberService.readUserById(log.getInvite_uid());
				if (parent != null) {
					parentEmail = parent.getF_user_email();
				}
			}
		}
		
		mo.addAttribute("parentEmail", parentEmail);
		return super.url_JSP("inviteList");
	}
	
	@RequestMapping(value = "/updateInvite", method = RequestMethod.POST)
	public String updateInvite(String userId, String parentEmail, Model mo) {
		long id = Convert.toLong(userId, 0);
		if (id <= 0) {
			return super.errorPage(mo, "用户id错误");
		}
		
		BQMember user = bqMemberService.readUserById(id);
		if (user == null) {
			return super.errorPage(mo, "子用户找不到");
		}
		
		if (StringUtil.isEmpty(parentEmail)) {
			return super.errorPage(mo, "父用户错误");
		}
		
		BQMember parent = bqMemberService.readUserByEmail(parentEmail);
		if (StringUtil.isEmpty(parent)) {
			return super.errorPage(mo, "父用户错误");
		}
		
		if (parent.getF_user_id() >= user.getF_user_id()) {
			return super.errorPage(mo, "子账户id要大于父账户");
		}
		
		BQMemberInviteLog newLog = new BQMemberInviteLog();
		BQMemberInviteLog log = bqMemberInviteLogService.readByUserId(user.getF_user_id());
		if (log != null) {
			newLog.setF_user_id(log.getF_user_id());
			newLog.setInvite_time(log.getInvite_time());
			newLog.setInvite_uid(parent.getF_user_id());
			bqMemberInviteLogService.update(newLog);
		} else {
			newLog.setF_user_id(user.getF_user_id());
			newLog.setInvite_time((int)(System.currentTimeMillis() / 1000));
			newLog.setInvite_uid(parent.getF_user_id());
			bqMemberInviteLogService.save(newLog);
		}
		
		return super.redirect("inviteList.do?userId=" + user.getF_user_id());
	}
}
