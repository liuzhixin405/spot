package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberInviteLog;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberLevelBuyRebateReconcilia;
import com.btc100.btc_q.service.BQMemberInviteLogService;
import com.btc100.btc_q.service.BQMemberLevelBuyRebateReconciliaService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;


@Controller
@RequestMapping(value = "/managerLevelBuyLock")
public class UserLevelBuyBfxLockAction extends BaseAction {
	
	@Autowired
	BQMemberLevelBuyRebateReconciliaService bqMemberLevelBuyRebateReconciliaService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	BQMemberInviteLogService bqMemberInviteLogService;
	
	@RequestMapping(value = "/levelBuyLock")
	public String loadUserIP(HttpServletRequest request, Model model) {
		
		String userId = request.getParameter("userId");
		String userEmail = request.getParameter("email");
		String subuserType = request.getParameter("subuserType");
		model.addAttribute("userId", userId);
		model.addAttribute("email", userEmail);
		
		BQMember user = null;
		if (!StringUtil.isEmpty(userId) && Long.valueOf(userId) > 0) {
			user = bqMemberService.readUserById(Long.valueOf(userId));
		} else if (!StringUtil.isEmpty(userEmail)) {
			user = bqMemberService.readUserByEmail(userEmail);
		} 
		
		model.addAttribute("allLock", bqMemberLevelBuyRebateReconciliaService.readSumSelfBfxLockByUser(0).doubleValue());
		
		if (user == null) {
			PagerModel pm = new PagerModel();
			model.addAttribute("pm", pm);
			return super.url_JSP("userLevelBuyLockBfx");
		}
		
		BQMemberLevel level = bqMemberLevelService.readByUserId(user.getF_user_id());
		BQMemberLevelBuyRebateReconcilia reconcilia = bqMemberLevelBuyRebateReconciliaService.getByUserId(user.getF_user_id());
		if (reconcilia == null) {
			reconcilia = new BQMemberLevelBuyRebateReconcilia();
		}
		reconcilia.setVip_name(level.getLevel_name());
		model.addAttribute("reconcilia", reconcilia);
		
		int userType = 0;
		if (!StringUtil.isEmpty(subuserType)) {
			userType = Convert.toInt(subuserType, 0);
		}
		
		model.addAttribute("subuserType", subuserType);
		PagerModel pm = bqMemberLevelBuyRebateReconciliaService.readSubUsers(user.getF_user_id(), userType);
		model.addAttribute("pm", pm);
		
		BQMemberInviteLog log = bqMemberInviteLogService.readByUserId(user.getF_user_id());
		if (log != null) {
			model.addAttribute("superior", log.getInvite_uid());
		}
		
		BigDecimal subuserLock = bqMemberLevelBuyRebateReconciliaService.readSumBfxLock(user.getF_user_id(), 1);
		BigDecimal subuser2Lock = bqMemberLevelBuyRebateReconciliaService.readSumBfxLock(user.getF_user_id(), 2);
		
		model.addAttribute("userEmail", user.getF_user_email());
		model.addAttribute("subuserLock", subuserLock.doubleValue());
		model.addAttribute("subuser2Lock", subuser2Lock.doubleValue());
		
		return super.url_JSP("userLevelBuyLockBfx");
	}
}
