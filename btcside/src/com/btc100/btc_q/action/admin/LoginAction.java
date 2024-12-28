package com.btc100.btc_q.action.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.GoogleAuthenticator;
import com.btc100.btc_q.entity.BQManagerMenu;
import com.btc100.btc_q.entity.BQSurecome;
import com.btc100.btc_q.service.BQManagerMenuService;
import com.btc100.btc_q.service.BQSurecomeService;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.StringUtil;

/**
 * 后台管理员登录
 *
 * */
@Controller
@RequestMapping("/qtner")
public class LoginAction extends BaseAction {

	@Autowired
	private BQSurecomeService bqSurecomeService;
	
	@Autowired
	private BQManagerMenuService bqManagerMenuService;

	/**
	 * 登录--验证管理员用户名密码
	 * @param request
	 * @param username
	 * @param password
	 * @param phonepwd
	 * @param mo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/owner", method = RequestMethod.POST)
	public String Login(HttpServletRequest request, String username, String password, String phonepwd, Model mo) throws Exception {
		if (StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
			mo.addAttribute("msg", "用户名或密码不能为空！");
			return super.url_ROOT("login");
		}
		
		BQSurecome manager = bqSurecomeService.getByName(username);
		if (manager != null) {
			if (manager.getPwd_count() > 4) {
				mo.addAttribute("msg", "密码错误5次，账户暂被锁定！");
				return super.url_ROOT("login");
			}

			boolean flag = bqSurecomeService.login(username, password);
			if (!flag) {
				manager.setPwd_count(manager.getPwd_count() + 1);
				long now = (long) DateUtil.DateToUnix(new Date());
				manager.setPwd_error_time(now);
				bqSurecomeService.update(manager);
				mo.addAttribute("msg", "用户名或密码错误！");
				return super.url_ROOT("login");
			} else {
				request.getSession().setAttribute("owner", manager);
				// 清空密码错误登录次数
				bqSurecomeService.clearLoginTime(username);
				if (StringUtil.isEmpty(manager.getGoogle_no())) {
					String secretKey = GoogleAuthenticator.generateSecretKey();
					mo.addAttribute("secretKey", secretKey);
					mo.addAttribute("managerName", manager.getUser_name());
					return super.url_ROOT("googleValidate");
				} else {
					return super.url_ROOT("googleLogin");
				}
			}
		}
		
		mo.addAttribute("msg", "用户名或密码错误！");
		return super.url_ROOT("login");
	}
	
	/**
	 * 登录--验证管理员谷歌验证码
	 * @param request
	 * @param googleCode
	 * @param mo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/googleLogin", method = RequestMethod.POST)
	public String googleLogin(HttpServletRequest request, String googleCode, Model mo) throws Exception {
		BQSurecome manager = (BQSurecome)request.getSession().getAttribute("owner");
		if (manager == null) {
			return super.url_ROOT("login");
		}
		
		manager = bqSurecomeService.getByName(manager.getUser_name());
		if (manager == null) {
			return super.url_ROOT("login");
		}
		if (StringUtil.isEmpty(googleCode)) {
			request.getSession().setAttribute("owner", manager);
			mo.addAttribute("msg", "验证码不能为空！");
			return super.url_ROOT("googleLogin");
		}
		
		if (manager.getGoogle_count() > 4) {
			mo.addAttribute("msg", "谷歌验证错误5次，账户暂被锁定！");
			return super.url_ROOT("googleLogin");
		}
		
		String glCode = (String) request.getSession().getAttribute("googleCode");
		if (StringUtil.isEmpty(glCode)) {
			String secretKey = manager.getGoogle_no();
			boolean success = GoogleAuthenticator.check_code(secretKey, Convert.toLong(googleCode, 0), System.currentTimeMillis());
			
			if (!success) {
				manager = bqSurecomeService.findById(manager.getUser_id());
				request.getSession().setAttribute("owner", manager);
				manager.setGoogle_count(manager.getGoogle_count() + 1);
				long now = (long) DateUtil.DateToUnix(new Date());
				manager.setGoogle_error_time(now);
				bqSurecomeService.update(manager);
				mo.addAttribute("msg", "验证码错误！");
				return super.url_ROOT("googleLogin");
			}
	
			request.getSession().setAttribute("owner", manager);
	//		request.getSession().setMaxInactiveInterval(60 * 60);
			mo.addAttribute("managerName", manager.getUser_name());
			
			String IP = IpMacUtil.getIpAddr(request);
			long now = (long) DateUtil.DateToUnix(new Date());
			manager.setUser_last_time(now);
			manager.setUser_lastip(IP);
			// 清空验证码错误登录次数
			bqSurecomeService.clearGoogleLoginTime(manager.getUser_name());
			bqSurecomeService.update(manager);
			request.getSession().setAttribute("googleCode", googleCode);
		}
		
		String[] role = manager.getUser_role().split(",");
		List<String> lst_Roles=new ArrayList<String>();
		for(String s:role){
			lst_Roles.add(s);
		}
		
		List<String> menuRoles = new ArrayList<String>();
		List<BQManagerMenu> menus = bqManagerMenuService.findAllList(1);
		for (BQManagerMenu menu : menus) {
			if (lst_Roles.contains(menu.getMenu_no())) {
				menuRoles.add(menu.getLink());
			}
		}
		
		request.getSession().setAttribute("menuRoles", menuRoles);
		return super.url_ROOT("mainweb");
	}
	
	/**
	 * 后台管理员绑定谷歌验证
	 * @param request
	 * @param mo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/bindGoogle", method = RequestMethod.POST)
	public String bindGoogle(HttpServletRequest request, Model mo) throws Exception {
		String googleCode = request.getParameter("googleCode");
		String secretKey = request.getParameter("secretKey");
		BQSurecome manager = (BQSurecome)request.getSession().getAttribute("owner");
		if (manager == null) {
			return super.url_ROOT("login");
		}
		manager = bqSurecomeService.findById(manager.getUser_id());
		request.getSession().setAttribute("owner", manager);
		
		if (StringUtil.isEmpty(googleCode)) {
			mo.addAttribute("managerName", manager.getUser_name());
			mo.addAttribute("secretKey", secretKey);
			return super.url_ROOT("googleValidate");
		}
		
		if (!StringUtil.isEmpty(manager.getGoogle_no())) {
			return super.url_ROOT("googleLogin");
		}
		
		boolean success = GoogleAuthenticator.check_code(secretKey, Convert.toLong(googleCode, 0), System.currentTimeMillis());
		
		if (!success) {
			mo.addAttribute("managerName", manager.getUser_name());
			mo.addAttribute("secretKey", secretKey);
			return super.url_ROOT("googleValidate");
		}
		manager.setGoogle_no(secretKey);
		bqSurecomeService.update(manager);
		
		return super.url_ROOT("googleLogin");
	}
	
	/**
	 * 管理员退出
	 * 
	 * @throws Exception
	 * */
	@RequestMapping(value = "/userout")
	public String Logout(HttpServletRequest request) throws Exception {
		request.getSession().removeAttribute("owner");
		request.getSession().removeAttribute("managerName");
		request.getSession().removeAttribute("googleCode");
		return super.redirect("/"+super.url_ROOT("login"))+".jsp";
	}

}
