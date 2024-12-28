package com.btc100.btc_q.action.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQUserMonitor;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQUserMonitorService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerUserMonitor")
public class UserMonitorAction extends BaseAction {
	
	@Autowired
	private BQUserMonitorService bqUserMonitorService;
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
				return super.url_JSP("userMonitorList");
			}
		}
		
		PagerModel pm = bqUserMonitorService.find(id);
		if (id > 0 && pm.getDatas().size() == 1) {
			mo.addAttribute("threshold", ((BQUserMonitor) pm.getDatas().get(0)).getThreshold());
			mo.addAttribute("priceRate",  ((BQUserMonitor) pm.getDatas().get(0)).getPrice_rate());
		}
		mo.addAttribute("pm", pm);
		
		return super.url_JSP("userMonitorList");
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(BQUserMonitor data, Model mo) {
		if (data == null || data.getF_user_id() == 0) {
			return super.redirect("list.do?msg=添加或修改失败");
		}
		
		BQUserMonitor monitor = bqUserMonitorService.get(data.getF_user_id());
		if (monitor == null) {
			BQMember user = bqMemberService.readUserById(data.getF_user_id());
			if (user != null) {
				bqUserMonitorService.save(data);
			} else {
				return super.redirect("list.do?msg=添加失败，没有该用户");
			}
		} else {
			monitor.setThreshold(data.getThreshold());
			monitor.setPrice_rate(data.getPrice_rate());
			bqUserMonitorService.update(monitor);
		}
		
		return super.redirect("list.do?userId=" + data.getF_user_id());
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String delete(String userId, Model mo) {
		long id = Convert.toLong(userId, 0);
		if (id > 0) {
			bqUserMonitorService.delete(id);
		}
		
		return super.redirect("list.do");
	}
}
