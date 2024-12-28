package com.btc100.btc_q.action.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.entity.BQManagerMenu;
import com.btc100.btc_q.entity.BQSurecome;
import com.btc100.btc_q.service.BQManagerMenuService;
import com.btc100.btc_q.service.BQSurecomeService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping(value = "/managerAdmin")
public class ManagerAction extends BaseAction {

	@Autowired
	private BQSurecomeService bqSurecomeService;
	@Autowired
	private BQManagerMenuService bqManagerMenuService;

	@RequestMapping(value = "/loadManager")
	public String loadManager(Model mo) {

		PagerModel pm = bqSurecomeService.findAllModel();
		mo.addAttribute("pm", pm);

		return super.url_JSP("managerList");
	}
	
	@RequestMapping(value = "/loadAddManager")
	public String loadAdd(String id, Model mo) {
		int userId = ConvertUtil.toInt(id, 0);
		List<BQManagerMenu> menus = bqManagerMenuService.findAllList(1);
		if (userId > 0) {
			BQSurecome manager = bqSurecomeService.findById(userId);
			mo.addAttribute("manager", manager);
		}
		mo.addAttribute("menus", menus);
		
		return super.url_JSP("managerDetails");
	}

	@RequestMapping(value = "/addOrUpdateManager", method = RequestMethod.POST)
	public String addManager(HttpServletRequest request, Model mo, String menus) throws Exception {
		
		BQSurecome owner = (BQSurecome)request.getSession().getAttribute("owner");
		if (owner == null) {
			return super.url_ROOT("login");
		}

		int userId = ConvertUtil.toInt(request.getParameter("id"), 0);
		String userName = request.getParameter("userName");
		String loginPwd = request.getParameter("loginPwd");
		String safePwd = request.getParameter("safePwd");
		
		
		if (userId <= 0) {
			if (userName == null || userName.isEmpty()) {
				return super.errorPage(mo, "请填写管理员名称");
			}
			if (loginPwd == null || loginPwd.isEmpty()) {
				return super.errorPage(mo, "登录密码不能为空");
			}
			if (safePwd == null || safePwd.isEmpty()) {
				return super.errorPage(mo, "安全密码不能为空");
			}
		}
		
		BQSurecome manager = new BQSurecome();
		if (userId > 0) {
			manager = bqSurecomeService.findById(userId);
			manager.setUser_role(menus);
			bqSurecomeService.update(manager);
		} else {
			String salt = RandomUtil.getRandomString(6);
			String pwd = MD5Service.encryptString(MD5Service.encryptString(loginPwd) + userName);
			String thpwd = MD5Service.encryptString(salt + MD5Service.encryptString(safePwd));
			manager.setUser_name(userName);
			manager.setUser_pwd(pwd);
			manager.setUser_thpwd(thpwd);
			manager.setUser_role(menus);
			manager.setUser_salt(salt);
			bqSurecomeService.add(manager);
		}
		
		if (owner.getUser_id() == manager.getUser_id()) {
			owner = bqSurecomeService.getByName(owner.getUser_name());
			request.getSession().setAttribute("owner", owner);
		}

		return "redirect:/managerAdmin/loadManager.do";
	}

	@RequestMapping(value = "/delManager")
	public String delManager(String id) {
		int managerId = ConvertUtil.toInt(id, 0);
		if (managerId > 0) {
			bqSurecomeService.delById(managerId);
		}
		return "redirect:/managerAdmin/loadManager.do";
	}

}
