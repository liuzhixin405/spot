package com.btc100.btc_q.action.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQUserAppKey;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQUserAppKeyService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerUserApi")
public class ManagerUserApiAction extends BaseAction {

	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQUserAppKeyService bqUserAppKeyService;

	@RequestMapping(value = "/load")
	public String load(HttpServletRequest request, Model model, String userId, String userEmail, String state) {
		PagerModel pm = new PagerModel();
		if (StringUtil.isEmpty(userId) && StringUtil.isEmpty(userEmail) && StringUtil.isEmpty(state)) {
			pm = bqUserAppKeyService.readPagerModel(0);
		} else {
			if (!StringUtil.isEmpty(state)) {
				pm = bqUserAppKeyService.readByState(ConvertUtil.toInt(state, 0));
			} else {
				BQMember user = this.selectUser(userId, userEmail);
				if (user == null) {
					return super.errorPage(model, "查不到该用户");
				}
				pm = bqUserAppKeyService.readPagerModel(user.getF_user_id());
			}
		}

		model.addAttribute("pm", pm);
		model.addAttribute("userId", userId);
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("state", state);
		
		return super.url_JSP("userApiList");
	}
	
	@RequestMapping("/get")
	public String get(long uid, Model model) {
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(uid);
		if (bqUserAppKey == null) {
			return super.errorPage(model, "该api已不存在");
		}
		
		model.addAttribute("bqUserAppKey", bqUserAppKey);
		return super.url_JSP("userApiEdit");
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(BQUserAppKey bqUserAppKey, Model model) {
		bqUserAppKeyService.update(bqUserAppKey);
		
		return super.redirect("load.do");
	}
	
	@RequestMapping("/delete")
	public String delete(String uid) {
		long userId = ConvertUtil.toLong(uid, 0l);
		bqUserAppKeyService.delete(userId);
		return super.redirect("load.do");
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
