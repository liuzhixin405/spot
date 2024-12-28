package com.btc100.btc_q.action.client;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.DATA_SessionKeys;
import com.btc100.btc_q.common.GoogleAuthenticator;
import com.btc100.btc_q.common.MessageInterface;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberPayword;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQTelCode;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQMemberPaywordService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.Security;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;

@Controller
@RequestMapping("/password")
public class PasswordAction extends BaseAction {

	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private BQMemberPaywordService bqMemberPaywordService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	Logger logger = Logger.getLogger(PasswordAction.class);

	@RequestMapping("/sliderFindPwd")
	public void sliderFindPwd(HttpServletRequest request, HttpServletResponse response) {
			toWeb(response, "true");
	}
	
	/* 找回密码 */
	@RequestMapping("/findPwd")
	public ModelAndView findPwd(HttpServletRequest request, HttpServletResponse response, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_findpwd1");
		return mv;
	}

	/* 找回密码 */
	@RequestMapping("/findPwddo")
	public ModelAndView findPwddo(HttpServletRequest request, HttpServletResponse response, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));
		
		String email = request.getParameter("email");
		if (StringUtil.isEmpty(email)) {
			return new ModelAndView("redirect:/password/findPwd.do");
		}
		
		ModelAndView mv = new ModelAndView();

		boolean isEmail = true;
		if (email.matches("^1[0-9]{10}$")) { 
			isEmail = false;
		}
		
		BQMember user = null;
		if (isEmail) {
			user = bqMemberService.readByRealEmail(email);
			
			model.addAttribute("email", email);
			String slast = "http://mail." + email.substring(email.indexOf("@") + 1);
			model.addAttribute("inemail", slast);
			
			if (user != null) {
				sendEmailCode(response, user, request);
			}
			
			mv.setViewName("jsp/user_findLoginPwdSuccess");
			return mv;
		} else {
			int random = MessageInterface.random6();
			
			BQTelCode telCode = new BQTelCode();
			telCode.setF_error_times(0);
			telCode.setF_auth_code(random + "");
			telCode.setF_user_tel(email);
			telCode.setF_user_id(Convert.toLong(email, 0));
			
			BQMemberSafeSettings setting = bqMemberSafeSettingsService.readByTel(email);
			if (setting != null) {
				try {
					telCode.setF_user_id(setting.getF_user_id());
					telCode.setF_comment1(setting.getF_user_zone());
					sendTelCode(setting.getF_user_id(), setting.getF_user_tel(), random, setting.getF_user_zone());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			request.getSession().setAttribute("phone_findPwd", telCode);
			
			model.addAttribute("email", email);
			mv.setViewName("jsp/user_findPwd_phone");
			return mv;
		}
	}
	
	@RequestMapping("/sendTelCode")
	public void sendTelCode( HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));
		
		BQTelCode telCode = (BQTelCode) request.getSession().getAttribute("phone_findPwd");
		if (StringUtil.isEmpty(telCode)) {
			toWeb(response, "发送失败！");
			return;
		}
		
		int random = MessageInterface.random6();
		String sendRet = "";
		long tel = Convert.toLong(telCode.getF_user_tel(), 0);
		if (tel != 0 && telCode.getF_user_id() != tel) {
			try {
				sendRet = sendTelCode(telCode.getF_user_id(), telCode.getF_user_tel(), random, telCode.getF_comment1());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (StringUtil.isEmpty(sendRet)) {
			telCode.setF_error_times(0);
			telCode.setF_auth_code(random + "");
			request.getSession().setAttribute("phone_findPwd", telCode);
			toWeb(response, "success");
			return;
		} else {
			toWeb(response, sendRet);
			return;
		}
	}

	public void sendEmailCode(HttpServletResponse response, BQMember user, HttpServletRequest request) {
		try {
			String random6 = RandomUtil.getRandomString(6);
			String email = user.getF_real_email();
			String email2 = Security.encryptToHex(email.toString() + "-" + random6, Security.des_registerkey);
			spyMemcachedClient.set("findPwddo_" + email2, BTCQMemecacheKeys.TIMES.FIVE_MIN, random6);
			SendMail themail = new SendMail();
			try {
				String basepath = super.getRequestRoot(request);
				String sendstr = basepath + "password/findPwd2.do?email=" + email2;
				themail.send(email, themail.getResetLoginPWD(basepath, sendstr));
				request.getSession().setAttribute(DATA_SessionKeys.FIND_LOGINPWD, sendstr);
				request.getSession().setAttribute(DATA_SessionKeys.FIND_LOGINPWD_EMAIL, email);
				spyMemcachedClient.set("email" + email2, 20 * 60, "email");
			} catch (Exception e) {
				toWeb(response, "error");
				return;
			}
		} catch (Exception e) {
			logger.info("发送邮箱验证码崩溃");
			logger.error(e.toString());
			toWeb(response, "error");
			return;
		}
	}

	@RequestMapping("/resend")
	public void resendEmail(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		Object emailStr = request.getSession().getAttribute(DATA_SessionKeys.FIND_LOGINPWD);
		Object email = request.getSession().getAttribute(DATA_SessionKeys.FIND_LOGINPWD_EMAIL);
		if (emailStr == null || email == null)
			toWeb(response, 2);
		else {
			SendMail sm = new SendMail();
			sm.send(email.toString(), sm.getResetLoginPWD(super.getRequestRoot(request), emailStr.toString()));
			toWeb(response, 1);
		}

	}

	@RequestMapping("/findPwd2")
	public ModelAndView findPwd2(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		String email = request.getParameter("email");
		model.addAttribute("email", email);
		String eml = spyMemcachedClient.get("email" + email);
		if (eml == "" || eml == null) {
			mv.setViewName("jsp/error");
			return mv;
		}
		spyMemcachedClient.delete("email" + email);
		mv.setViewName("jsp/user_findpwd2");
		return mv;
	}

	@RequestMapping("/findPwd2do")
	public ModelAndView findPwd2do(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		BQMember infoUser = null;
		String startEmail = email;
		
		if (!StringUtil.isEmpty(phone)) {
			BQTelCode telCode = (BQTelCode) request.getSession().getAttribute("phone_findPwd");
			if (telCode == null) {
				return new ModelAndView("redirect:/password/findPwd.do");
			}
			
			if (telCode.getF_error_times() >= 5) {
				request.getSession().removeAttribute("phone_findPwd");
				return new ModelAndView("redirect:/password/findPwd.do");
			}
			
			long tel = Convert.toLong(telCode.getF_user_tel(), 0);
			if (tel == 0 || tel == telCode.getF_user_id() || !phone.equals(telCode.getF_auth_code())) {
				request.getSession().removeAttribute("phone_findPwd");
				return new ModelAndView("redirect:/password/findPwd.do");
			}
			
			BQMemberSafeSettings setting = bqMemberSafeSettingsService.readByTel(telCode.getF_user_tel());
			if (setting != null) {
				infoUser =  bqMemberService.readUserById(setting.getF_user_id());
			}
		} else {
			try {
				String random = spyMemcachedClient.get("findPwddo_" + startEmail);
				if (StringUtil.isEmpty(random)) {
					mv.setViewName("jsp/error");
					return mv;
				}
				
				email = Security.decryptByHex(email, Security.des_registerkey);
				if (email.endsWith("-" + random)) {
					email = email.replace("-" + random, "");
				} else {
					mv.setViewName("jsp/error");
					return mv;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String newpassword = request.getParameter("newpasswd1"); // 新密码
		String newpassword2 = request.getParameter("newpasswd2"); // 确认新密码
		if (newpassword == null || newpassword2 == null || newpassword.isEmpty() || newpassword2.isEmpty()) {
			model.addAttribute("error", SpringContext.getText("msg.password.request.pwdCantBeNull", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_findpwd2");
			return mv;
		}

		if (!newpassword.equals(newpassword2)) {
			model.addAttribute("error", SpringContext.getText("msg.password.request.twicePwdNotEq", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_findpwd2");
			return mv;
		}
		
		infoUser = bqMemberService.readByRealEmail(email);
		if (infoUser == null) {
			return new ModelAndView("redirect:/password/findPwd.do");
		}
		
		String newpassword1;
		try {
			newpassword1 = MD5Service.encryptString(newpassword).toLowerCase() + infoUser.getF_user_salt();
			newpassword1 = MD5Service.encryptString(newpassword1).toLowerCase();
			bqMemberService.updatepassword(infoUser.getF_user_id(), newpassword1);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", SpringContext.getText("msg.password.request.resetFail", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_findpwd2");
			return mv;
		}
		model.addAttribute("success", SpringContext.getText("msg.password.request.resetLoginSuccess", Contents.Local.LOCALE.get()));
		mv.setViewName("jsp/user_findpwd2");
		
		if (StringUtil.isEmpty(phone)) {
			spyMemcachedClient.delete("findPwddo_" + startEmail);
		} else {
			 request.getSession().removeAttribute("phone_findPwd");
		}
		
		return mv;
	}

	@RequestMapping("/changePwd")
	public String changePwd(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		String num = request.getParameter("error");
		if (num != null) {
			if (num.equals("error1")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.phoneCodeNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error2")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error3")) {
				model.addAttribute("error", SpringContext.getText("msg.password.request.pwdCantBeNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error4")) {
				model.addAttribute("error", SpringContext.getText("msg.password.request.twicePwdNotEq", Contents.Local.LOCALE.get()));
			} else if (num.equals("error5")) {
				model.addAttribute("error", SpringContext.getText("msg.password.request.oldPwdError", Contents.Local.LOCALE.get()));
			}
		}
		String sus = request.getParameter("success");
		if (sus != null) {
			if (sus.equals("success1")) {
				model.addAttribute("success", SpringContext.getText("msg.password.request.changeLoginSuccess", Contents.Local.LOCALE.get()));
			}
		}
		BQMember user = super.findCurrUser(request, true);
		if(user==null){
			return super.toLogin();
		}
		
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqss.getF_user_safetype() == 1 || bqss.getF_user_safetype() == 3) {
			model.addAttribute("tel", bqss.getF_user_tel());
			model.addAttribute("showtel", 1);
		} else {
			model.addAttribute("showtel", 0);
		}

		model.addAttribute("email", user.getF_user_email());
		return "jsp/user_changpwd";
	}
	
	@RequestMapping(value="/phoneChangePwddo", method = RequestMethod.POST)
	public ModelAndView phoneChangePwddo(HttpServletRequest request, String code, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));
		
		ModelAndView mv = new ModelAndView();
		if (StringUtil.isEmpty(code)) {
			model.addAttribute("error", SpringContext.getText("msg.common.request.phoneCodeNull", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_findPwd_phone");
			return mv;
		}
		
		BQTelCode telCode = (BQTelCode) request.getSession().getAttribute("phone_findPwd");
		if (telCode == null) {
			return new ModelAndView("redirect:/password/findPwd.do");
		}
		
		if (telCode.getF_error_times() >= 5) {
			request.getSession().removeAttribute("phone_findPwd");
			return new ModelAndView("redirect:/password/findPwd.do");
		}
		
		long tel = Convert.toLong(telCode.getF_user_tel(), 0);
		if (tel == 0 || tel == telCode.getF_user_id() || !code.equals(telCode.getF_auth_code())) {
			telCode.setF_error_times(telCode.getF_error_times() + 1);
			request.getSession().setAttribute("phone_findPwd", telCode);
			
			model.addAttribute("error", SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_findPwd_phone");
			return mv;
		}
		
		model.addAttribute("phone", code);
		mv.setViewName("jsp/user_findpwd2");
		return mv;
	}

	/* 修改登录登录密码 */
	@RequestMapping("/changePwddo")
	public ModelAndView changePwddo(HttpServletRequest request, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));

		HttpSession session = request.getSession();
		BQMember user = super.findCurrUser(request, true);
		if(user==null){
			return new ModelAndView(super.toLogin());
		}
		String password = request.getParameter("password"); // 旧密码
		String newpassword = request.getParameter("newpasswd1"); // 新密码
		String newpassword2 = request.getParameter("newpasswd2"); // 确认新密码
		String code = request.getParameter("code");

		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqss.getF_user_safetype() == 1 || bqss.getF_user_safetype() == 3) {
			if (null == code || code.isEmpty()) {
				model.addAttribute("error", "error1");
				return new ModelAndView("redirect:/password/changePwd.do", model);
			}
			BQTelCode bt_code1 = (BQTelCode) session.getAttribute("telcode" + bqss.getF_user_tel());// 得缓存中的验证吗
			if (bt_code1 == null) {
				model.addAttribute("error", "error2");
				return new ModelAndView("redirect:/password/changePwd.do", model);
			}
			session.removeAttribute("telcode" + bqss.getF_user_tel());
		}
		if (password == null || newpassword == null || newpassword2 == null || password.isEmpty() || newpassword.isEmpty() || newpassword2.isEmpty()) {
			model.addAttribute("error", "error3");
			return new ModelAndView("redirect:/password/changePwd.do", model);
		}
		if (!newpassword.equals(newpassword2)) {
			model.addAttribute("error", "error4");
			return new ModelAndView("redirect:/password/changePwd.do", model);
		}

		try {
			password = MD5Service.encryptString(password).toLowerCase();
			password = MD5Service.encryptString(password + user.getF_user_salt()).toLowerCase();
			if (!password.equals(user.getF_user_pwd())) {
				model.addAttribute("error", "error5");
				return new ModelAndView("redirect:/password/changePwd.do", model);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String newpassword1;
		try {
			newpassword1 = MD5Service.encryptString(newpassword).toLowerCase();
			newpassword1 = MD5Service.encryptString(newpassword1 + user.getF_user_salt()).toLowerCase();

			user.setF_user_pwd(newpassword);
			bqMemberService.updatepassword(user.getF_user_id(), newpassword1);

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			SendMail themail = new SendMail();
			String content = SpringContext.getText("msg.password.request.content1", Contents.Local.LOCALE.get());
			themail.send(user.getF_real_email(), content);
		} catch (Exception e) {
			logger.error("修改密码时，发送邮件失败", e);
		}
		model.addAttribute("success", "success1");
		return new ModelAndView("redirect:/password/changePwd.do", model);
	}

	@RequestMapping("/changeTradePwd")
	public ModelAndView changeTradePwd(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		String num = request.getParameter("error");
		if (num != null) {
			if (num.equals("error3")) {
				model.addAttribute("error", SpringContext.getText("msg.password.request.pwdCantBeNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error4")) {
				model.addAttribute("error", SpringContext.getText("msg.password.request.twicePwdNotEq", Contents.Local.LOCALE.get()));
			} else if (num.equals("error5")) {
				model.addAttribute("error", SpringContext.getText("msg.password.request.oldPwdError", Contents.Local.LOCALE.get()));
			}
		}
		String sus = request.getParameter("success");
		if (sus != null) {
			if (sus.equals("success1")) {
				model.addAttribute("success", SpringContext.getText("msg.password.request.changeTradeSuccess", Contents.Local.LOCALE.get()));
			}
		}
		ModelAndView mv = new ModelAndView();
		BQMember user = super.findCurrUser(request, true);
		if(user==null){
			return new ModelAndView(super.toLogin());
		}
		model.addAttribute("email", user.getF_user_email());
		mv.setViewName("jsp/user_changTradepwd");
		return mv;
	}

	/* 修改交易密码 */
	@RequestMapping("/changeTradePwddo")
	public ModelAndView changeTradePwddo(HttpServletRequest request, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, true);
		if(user==null){
			return new ModelAndView(super.toLogin());
		}
		String password = request.getParameter("password"); // 旧密码
		String newpassword = request.getParameter("newpasswd1"); // 新密码
		String newpassword2 = request.getParameter("newpasswd2"); // 确认新密码
		if (password == null || newpassword == null || newpassword2 == null || password.isEmpty() || newpassword.isEmpty() || newpassword2.isEmpty()) {
			model.addAttribute("error", "error3");
			return new ModelAndView("redirect:/password/changeTradePwd.do", model);
		}
		if (!newpassword.equals(newpassword2)) {
			model.addAttribute("error", "error4");
			return new ModelAndView("redirect:/password/changeTradePwd.do", model);
		}

		BQMemberPayword bqmP = bqMemberPaywordService.readById(user.getF_user_id());
		try {
			password = MD5Service.encryptString(password).toLowerCase() + bqmP.getSalt().toLowerCase();
			password = MD5Service.encryptString(password).toLowerCase();

			if (!password.equals(bqmP.getPwd())) {
				model.addAttribute("error", "error5");
				return new ModelAndView("redirect:/password/changeTradePwd.do", model);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String newpassword1;
		try {
			newpassword1 = MD5Service.encryptString(newpassword).toLowerCase() + bqmP.getSalt().toLowerCase();
			newpassword1 = MD5Service.encryptString(newpassword1).toLowerCase();
			bqmP.setPwd(newpassword1);
			bqMemberPaywordService.save(bqmP);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			SendMail themail = new SendMail();
			String content = SpringContext.getText("msg.password.request.content2", Contents.Local.LOCALE.get());
			themail.send(user.getF_real_email(), content);
		} catch (Exception e) {
			logger.error("修改密码时，发送邮件失败", e);
		}
		model.addAttribute("success", SpringContext.getText("msg.password.request.resetTradeSuccess", Contents.Local.LOCALE.get()));
		return new ModelAndView("jsp/user_changTradepwd", model); 
	}

	@RequestMapping("/tradePwd")
	public ModelAndView tradePwd(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		BQMember user = super.findCurrUser(request, true);
		if(user == null){
			return new ModelAndView(super.toLogin());
		}
		
		BQMemberPayword bqp = bqMemberPaywordService.readById(user.getF_user_id());// 得到交易表
		if (bqp != null) {
			String findEmail = request.getParameter("email");
			if (findEmail != null) {
				String spy = spyMemcachedClient.get("email" + findEmail);
				if (spy == null) {
					model.addAttribute("type", 2);
					model.addAttribute("info", SpringContext.getText("msg.password.request.linksInvalid", Contents.Local.LOCALE.get()));
					return new ModelAndView("error/SystemInfo");
				}
			}
			
			String random = spyMemcachedClient.get("findTradePwddo_" + findEmail);
			if (StringUtil.isEmpty(random)) {
				model.addAttribute("type", 2);
				model.addAttribute("info", SpringContext.getText("msg.password.request.linksInvalid", Contents.Local.LOCALE.get()));
				return new ModelAndView("error/SystemInfo");
			}
			
			String email = Security.decryptByHex(findEmail, Security.des_registerkey);
			if (email.endsWith("-" + random)) {
				email = email.replace("-" + random, "");
			} else {
				model.addAttribute("type", 2);
				model.addAttribute("info", SpringContext.getText("msg.password.request.linksInvalid", Contents.Local.LOCALE.get()));
				return new ModelAndView("error/SystemInfo");
			}
			
			if (!user.getF_user_email().equals(email)){
				return new ModelAndView(super.toLogin());
			}
		}
		
		String num = request.getParameter("error");
		if (num != null) {
			if (num.equals("error1")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.phoneCodeNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error2")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error3")) {
				model.addAttribute("error", SpringContext.getText("msg.password.request.pwdCantBeNull", Contents.Local.LOCALE.get()));
			}
		}
		String sus = request.getParameter("success");
		if (sus != null) {
			if (sus.equals("success1")) {
				model.addAttribute("success", SpringContext.getText("msg.password.request.resetTradeSuccess", Contents.Local.LOCALE.get()));
			}
		}


		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqss.getF_user_safetype() == 1) {
			model.addAttribute("tel", bqss.getF_user_tel());
			model.addAttribute("showtel", 1);
			model.addAttribute("showgoogle", 0);
		} else if (bqss.getF_user_safetype() == 2) {
			model.addAttribute("secretKey", bqss.getF_user_safegoogle());
			model.addAttribute("showgoogle", 1);
			model.addAttribute("showtel", 0);
		} else if (bqss.getF_user_safetype() == 3) {
			model.addAttribute("tel", bqss.getF_user_tel());
			model.addAttribute("showtel", 1);
			model.addAttribute("secretKey", bqss.getF_user_safegoogle());
			model.addAttribute("showgoogle", 1);
		} else {
			model.addAttribute("showgoogle", 0);
			model.addAttribute("showtel", 0);
		}
		// model.addAttribute("tel", user.getF_user_tel());
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_trade_password");
		return mv;
	}

	/* 设置交易密码 */
	@RequestMapping("/tradePwddo")
	public ModelAndView tradePwddo(HttpServletRequest request, ModelMap model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		HttpSession session = request.getSession();
		// 手机验证码
		String code = request.getParameter("code");
		String newpassword1 = request.getParameter("newpasswd1");
		String newpassword2 = request.getParameter("newpasswd2");
		String googleCode = request.getParameter("google");

		BQMember user = super.findCurrUser(request, true);
		if(user==null){
			return new ModelAndView(super.toLogin());
		}
		BQMemberPayword bqp = bqMemberPaywordService.readById(user.getF_user_id());// 得到交易表
		if (bqp == null) {
			bqp = new BQMemberPayword();
		}
		// 判断是否绑定手机
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqss.getF_user_safetype() == 1 || bqss.getF_user_safetype() == 3) {
			if (null == code || code.isEmpty()) {
				model.addAttribute("error", "error1");
				return new ModelAndView("redirect:/password/tradePwd.do", model);
			}
			BQTelCode bt_code1 = (BQTelCode) session.getAttribute("telcode" + bqss.getF_user_tel());// 得缓存中的验证吗
			if (bt_code1 == null) {
				model.addAttribute("error", "error2");
				return new ModelAndView("redirect:/password/tradePwd.do", model);
			}
			session.removeAttribute("telcode" + bqss.getF_user_tel());
		}
		// 判断谷歌验证码
		if (bqss.getF_user_safetype() == 2 || bqss.getF_user_safetype() == 3) {
			if (googleCode == null || googleCode.isEmpty()) {
				model.addAttribute("error", "error1");
				return new ModelAndView("redirect:/password/tradePwd.do", model);
			}

			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				model.addAttribute("error", "error2");
				return new ModelAndView("redirect:/password/tradePwd.do", model);
			}
			boolean bresult = GoogleAuthenticator.check_code(bqss.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				model.addAttribute("error", "error2");
				return new ModelAndView("redirect:/password/tradePwd.do", model);
			}

		}

		if (null == newpassword1 || null == newpassword2 || newpassword1.isEmpty() || newpassword2.isEmpty() || !newpassword1.equals(newpassword2)) {
			model.addAttribute("error", "error3");
			return new ModelAndView("redirect:/password/tradePwd.do", model);
		}

		String random6 = RandomUtil.getRandomString(6);
		String newpassword = MD5Service.encryptString(newpassword1).toLowerCase() + random6.toLowerCase();
		newpassword = MD5Service.encryptString(newpassword).toLowerCase();

		bqp.setF_user_id(user.getF_user_id());
		bqp.setPwd(newpassword);
		bqp.setSalt(random6);
		bqp.setState(0);
		bqp.setF_readszms(0);
		bqMemberPaywordService.save(bqp);
		SendMail themail = new SendMail();
		String content = SpringContext.getText("msg.password.request.content2", Contents.Local.LOCALE.get());
		try {
			themail.send(user.getF_real_email(), content);
		} catch (Exception e) {
			logger.error("修改交易密码时，发送邮件失败", e);
		}
		model.addAttribute("success", "success1");
		return new ModelAndView("redirect:/center/accountSafe.do", model);
	}

	/* 修改交易密码 */
	@RequestMapping("/udPayPwd")
	public ModelAndView udPayPwd(HttpServletRequest request, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		HttpSession session = request.getSession();
		// 手机验证码
		String code = request.getParameter("code");
		String googleCode = request.getParameter("googleCode");
		BQMember user = super.findCurrUser(request, true);
		if(user==null){
			return new ModelAndView(super.toLogin());
		}
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());// 得到安全设置
		BQMemberPayword bqp = bqMemberPaywordService.readById(user.getF_user_id());// 得到交易表
		ModelAndView mv = new ModelAndView();
		// 判断是否绑定手机谷歌
		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) {
			if (null == code || code.isEmpty()) {
				model.addAttribute("code", SpringContext.getText("msg.common.request.phoneCodeNull", Contents.Local.LOCALE.get()));
				mv.setViewName("jsp/user_center");
				return mv;
			}
			BQTelCode bt_code1 = (BQTelCode) session.getAttribute("telcode" + bqs.getF_user_tel());// 得缓存中的验证吗
			if (bt_code1 == null) {
				model.addAttribute("code", SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
				mv.setViewName("jsp/user_center");
				return mv;
			}
			session.removeAttribute("telcode" + bqs.getF_user_tel());
		}

		if (bqs.getF_user_safetype() == 2 || bqs.getF_user_safetype() == 3) {
			if (null == googleCode || googleCode.isEmpty()) {
				model.addAttribute("code", SpringContext.getText("msg.common.request.phoneCodeNull", Contents.Local.LOCALE.get()));
				mv.setViewName("jsp/user_center");
				return mv;
			}
//			long lgoogleCode = 0;
			try {
				Long.parseLong(googleCode);
			} catch (Exception e) {
				model.addAttribute("code", SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
				mv.setViewName("jsp/user_center");
				return mv;
			}
//			GoogleAuthenticator authenticator = new GoogleAuthenticator();
			// boolean bresult =
			// authenticator.check_code(infoUserExtend.getF_user_gotoken(),
			// lgoogleCode, System.currentTimeMillis());
			// if (!bresult) {
			// model.addAttribute("code", "验证码不正确");
			// mv.setViewName("user_center");
			// return mv;
			// }
		}

		String newpassword1 = request.getParameter("newpasswd1");
		String newpassword2 = request.getParameter("newpasswd2");

		if (null == newpassword1 || null == newpassword2 || newpassword1.isEmpty() || newpassword2.isEmpty() || !newpassword1.equals(newpassword2)) {
			model.addAttribute("password", SpringContext.getText("msg.password.request.errorPwd", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_center");
			return mv;
		}

		String newpassword = MD5Service.encryptString(newpassword1).toLowerCase() + bqp.getSalt().toLowerCase();
		newpassword = MD5Service.encryptString(newpassword1).toLowerCase();

		bqp.setPwd(newpassword);
		bqMemberPaywordService.save(bqp);
		SendMail themail = new SendMail();
		String content = SpringContext.getText("msg.password.request.content2", Contents.Local.LOCALE.get());
		try {
			themail.send(user.getF_real_email(), content);
		} catch (Exception e) {
			logger.error("修改交易密码时，发送邮件失败", e);
		}
		model.addAttribute("password", SpringContext.getText("msg.password.request.changeTradeSuccess", Contents.Local.LOCALE.get()));
		mv.setViewName("jsp/user_center");
		return mv;
	}

	// 判断资金密码输入
	@RequestMapping("/changTradeInputOrNot")
	public void changTradeInputOrNot(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		String state = request.getParameter("state");
		BQMember user = super.findCurrUser(request, true);
		if(user==null){
			toWeb(response, "false");
			return;
		}
		// bqm=bqMemberService.findUserById(user.getF_user_id());
		BQMemberPayword bqmP = bqMemberPaywordService.readById(user.getF_user_id());
		if (bqmP == null) {
			toWeb(response, "false");
			return;
		}

		bqmP.setState(Integer.parseInt(state));
		bqMemberPaywordService.save(bqmP);
	}

	// 判断资金密码不输入
	@RequestMapping("/changTradeInputNot")
	public void changTradeInputNot(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		HttpSession session = request.getSession();
		BQMember user = super.findCurrUser(request, true);
		if(user==null){
			toWeb(response, SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			return;
		}

		String telCode = request.getParameter("code");
		String googleCode = request.getParameter("google");
		String paypass = request.getParameter("password");

		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) { // 手机验证
			if (null == telCode || telCode.isEmpty()) {
				toWeb(response, SpringContext.getText("msg.common.request.phoneCodeNull", Contents.Local.LOCALE.get()));
				return;
			}
			BQTelCode tecode = (BQTelCode) session.getAttribute("telcode" + bqs.getF_user_tel());
			if (tecode == null) {
				toWeb(response, SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
				return;
			}
			if (!tecode.getF_auth_code().equals(telCode)) {
				toWeb(response, SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
				return;
			}
			session.removeAttribute("telcode" + bqs.getF_user_tel());
		}
		if (bqs.getF_user_safetype() == 2 || bqs.getF_user_safetype() == 3) { // 谷歌验证
			if (null == googleCode || googleCode.isEmpty()) {
				toWeb(response, SpringContext.getText("msg.common.request.googleCodeNull", Contents.Local.LOCALE.get()));
				return;
			}
			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				toWeb(response, SpringContext.getText("msg.common.request.googleCodeNull", Contents.Local.LOCALE.get()));
				return;
			}
			boolean bresult = GoogleAuthenticator.check_code(bqs.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				toWeb(response, SpringContext.getText("msg.common.request.googleCodeError", Contents.Local.LOCALE.get()));
				return;
			}
		}

		BQMemberPayword bqp = bqMemberPaywordService.readById(user.getF_user_id());
		if (bqp == null) {
			toWeb(response, SpringContext.getText("msg.password.request.tradePwdNull", Contents.Local.LOCALE.get()));
			return;
		}
		if (bqp.getPwd() == "") {// 交易密码启用
			toWeb(response, false);
			return;
		}
		if (paypass == null || paypass.equals("")) {
			toWeb(response, SpringContext.getText("msg.password.request.tradePwdNull", Contents.Local.LOCALE.get()));
			return;
		}
		try {
			paypass = MD5Service.encryptString(paypass).toLowerCase() + bqp.getSalt().toLowerCase();
			paypass = MD5Service.encryptString(paypass).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 比较交易密码（交易密码放到最前面，有利于用户余额信息不外泄）
		if (!paypass.equals(bqp.getPwd())) {
			toWeb(response, SpringContext.getText("msg.password.request.tradePwdError", Contents.Local.LOCALE.get()));
			return;
		}
		bqp.setState(0);
		bqMemberPaywordService.save(bqp);
		toWeb(response, SpringContext.getText("msg.password.request.setNoTradePwdSuccess", Contents.Local.LOCALE.get()));
		return;
	}

	/* 找回资金密码 */
	@RequestMapping("/findTradePwddo")
	public ModelAndView findTradePwddo(HttpServletRequest request, HttpServletResponse response, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		BQMember user = super.findCurrUser(request, true);
		if(user==null){
			return new ModelAndView(super.toLogin());
		}
		
		BQMemberSafeSettings setting =  bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (setting != null && !StringUtil.isEmpty(setting.getF_user_tel())) {
			mv.setViewName("jsp/user_changeAssertPwd");
			return mv;
		}
		
		String email = user.getF_user_email();
		model.addAttribute("email", email);
		String slast = "http://mail." + email.substring(email.indexOf("@") + 1);
		model.addAttribute("inemail", slast);
		sendEmailCode2(response, user, request);
		mv.setViewName("jsp/user_findTradePwdsuccess");
		return mv;
	}
	
	/* 找回资金密码 */
	@RequestMapping("/resetTradePwddo")
	public ModelAndView resetTradePwddo(HttpServletRequest request, HttpServletResponse response, String code, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));

		ModelAndView mv = new ModelAndView();
		BQMember user = super.findCurrUser(request, true);
		if(user==null){
			return new ModelAndView(super.toLogin());
		}
		
		if (StringUtil.isEmpty(code)) {
			model.addAttribute("error",  SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_changeAssertPwd");
			return mv;
		}

		String newpassword1 = request.getParameter("newpasswd1");
		String newpassword2 = request.getParameter("newpasswd2");
		if (StringUtil.isEmpty(newpassword1) || StringUtil.isEmpty(newpassword2) || !newpassword1.equals(newpassword2)) {
			model.addAttribute("error", SpringContext.getText("msg.password.request.errorPwd", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_changeAssertPwd");
			return mv;
		}
		
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqms == null || StringUtil.isEmpty(bqms.getF_user_tel())) {
			return new ModelAndView("redirect:/center/accountSafe.do");
		}
		
		BQTelCode telCode = (BQTelCode) request.getSession().getAttribute("telcode" + bqms.getF_user_tel());
		if (telCode == null) {
			return new ModelAndView("redirect:/center/accountSafe.do");
		}
		
		if (telCode.getF_error_times() >= 5) {
			request.getSession().removeAttribute("telcode" + bqms.getF_user_tel());
			return new ModelAndView("redirect:/center/accountSafe.do");
		}
		
		if (!code.equals(telCode.getF_auth_code())) {
			telCode.setF_error_times(telCode.getF_error_times() + 1);
			
			request.getSession().setAttribute("telcode" + bqms.getF_user_tel(), telCode);
			model.addAttribute("error",  SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/user_changeAssertPwd");
			return mv;
		}
		
		BQMemberPayword bqmP = bqMemberPaywordService.readById(user.getF_user_id());
		try {
			String newpassword = MD5Service.encryptString(newpassword1).toLowerCase() + bqmP.getSalt().toLowerCase();
			newpassword = MD5Service.encryptString(newpassword).toLowerCase();
			bqmP.setPwd(newpassword);
			bqMemberPaywordService.save(bqmP);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		model.addAttribute("success", SpringContext.getText("msg.password.request.resetTradeSuccess", Contents.Local.LOCALE.get()));
		return new ModelAndView("jsp/user_changeAssertPwd", model);
	}

	public void sendEmailCode2(HttpServletResponse response, BQMember user, HttpServletRequest request) {
		Contents.Local.LOCALE.set(getLocale(request));
		try {
			String random6 = RandomUtil.getRandomString(6);
			String email = user.getF_real_email();
			String email2 = Security.encryptToHex(email.toString() + "-" + random6, Security.des_registerkey);
			spyMemcachedClient.set("findTradePwddo_" + email2, BTCQMemecacheKeys.TIMES.FIVE_MIN, random6);
			SendMail themail = new SendMail();
			StringBuffer sb = new StringBuffer(SpringContext.getText("msg.password.request.content3", Contents.Local.LOCALE.get()));
			sb.append(super.getRequestRoot(request) + "password/tradePwd.do?email=");
			sb.append(email2);
			sb.append("");
			try {
				themail.send(email, sb.toString());
				spyMemcachedClient.set("email" + email2, 15 * 60, email2);

			} catch (Exception e) {
				toWeb(response, "error");
				return;
			}
		} catch (Exception e) {
			logger.info("发送邮箱验证码崩溃");
			logger.error(e.toString());
			toWeb(response, "error");
			return;
		}

	}

}
