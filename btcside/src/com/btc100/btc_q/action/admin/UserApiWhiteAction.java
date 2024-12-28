package com.btc100.btc_q.action.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQUserApiWhite;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQUserApiWhiteService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerUserApiWhite")
public class UserApiWhiteAction extends BaseAction {

	@Autowired
	private BQUserApiWhiteService bqUserTradeWhiteService;
	@Autowired
	private BQMemberService bqMemberService;

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
				return super.url_JSP("userTradeWhiteList");
			}
		}

		PagerModel pm = bqUserTradeWhiteService.find(id);
		mo.addAttribute("pm", pm);

		return super.url_JSP("userApiWhiteList");
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(BQUserApiWhite data, Model mo) {
		if (data == null || data.getF_user_id() == 0) {
			return super.redirect("list.do?msg=添加失败");
		}

		BQUserApiWhite riskControl = bqUserTradeWhiteService.get(data.getF_user_id());
		if (riskControl == null) {
			BQMember user = bqMemberService.readUserById(data.getF_user_id());
			if (user != null) {
				data.setAdd_time(System.currentTimeMillis() / 1000);
				bqUserTradeWhiteService.save(data);
			} else {
				return super.redirect("list.do?msg=添加失败，没有该用户");
			}
		}

		return super.redirect("list.do?userId=" + data.getF_user_id());
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String delete(String userId, Model mo) {
		long id = Convert.toLong(userId, 0);
		if (id > 0) {
			bqUserTradeWhiteService.delete(id);
		}

		return super.redirect("list.do");
	}
}
