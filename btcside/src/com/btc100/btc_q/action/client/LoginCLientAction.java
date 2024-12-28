package com.btc100.btc_q.action.client;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.auth0.jwt.interfaces.Claim;
import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.GoogleAuthenticator;
import com.btc100.btc_q.common.MessageInterface;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQCommonFailedlogin;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLoginLog;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQTelCode;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQCommonFailedloginService;
import com.btc100.btc_q.service.BQMemberLoginLogService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberStatusService;
import com.btc100.btc_q.util.CookieHelper;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.JwtToken;
import com.btc100.btc_q.util.SafeToken;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.JsonTrans;

@Controller
@RequestMapping("/login")
public class LoginCLientAction extends BaseAction {

	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQCommonFailedloginService bqCommonFailedloginService;
	@Autowired
	private BQMemberStatusService bqMemberStatusService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private BQMemberLoginLogService bqMemberLoginLogService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	@RequestMapping("/sliderLogin")
	public void sliderLogin(HttpServletRequest request, HttpServletResponse response) {
			toWeb(response, "true");
	}

	@RequestMapping("/login")
	public String login(HttpServletRequest request, Model model) {
		request.setAttribute("locale", Utils.getLocale(request));
		
		BQMember user = super.findCurrUser(request, false);
		if (user != null) {
			String basePath = super.getRequestRoot(request);
			return "redirect:" + basePath + "trade/trade.do";
		}
		
		String error = request.getParameter("error");
		if (!StringUtil.isEmpty(error)) {
			try {
				error = new String(error.getBytes("iso-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			model.addAttribute("error", error);
		}

		Cookie cookie = CookieHelper.getCookie(request, "email");
		if (cookie != null) {
			String cv = cookie.getValue();
			model.addAttribute("email", cv);
		}
		
		model.addAttribute("subUserLoign", request.getParameter("subUserLoign"));
		return "jsp/login";
	}

	@RequestMapping("/sendTelCode")
	public void sendTelCode(HttpServletRequest request, HttpServletResponse response, RedirectAttributes model) throws Exception {
		String email = (String) request.getSession().getAttribute("email");
		if (StringUtil.isEmpty(email)) {
			toWeb(response, "error");
			return;
		}
		
		BQMember user = super.findCurrUser(request, false);
		if (user != null) {
			toWeb(response, "error");
			return;
		}
		
		user = bqMemberService.readCheckUserEmail(email);
		if (user == null) {
			toWeb(response, "success");
			return;
		}
		
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (StringUtil.isEmpty(bqms.getF_user_tel())) {
			toWeb(response, "error");
			return;
		}
		
		int random = MessageInterface.random6();
		BQTelCode telCode = new BQTelCode();
		telCode.setF_error_times(0);
		telCode.setF_auth_code(random + "");
		telCode.setF_user_tel(bqms.getF_user_tel());
		telCode.setF_user_id(bqms.getF_user_id());

		String sendRet = "error";
		try {
			sendRet = sendTelCode(user.getF_user_id(), bqms.getF_user_tel(), random, bqms.getF_user_zone());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (StringUtil.isEmpty(sendRet)) {
			spyMemcachedClient.set("user_login_telcode_" + bqms.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, telCode);	
			toWeb(response, "success");
			return;
		} else {
			toWeb(response, sendRet);
			return;
		}
	}
	
	@RequestMapping("/sendEmailCode")
	public void sendEmailCode(HttpServletRequest request, HttpServletResponse response, RedirectAttributes model) throws Exception {
		String email = (String) request.getSession().getAttribute("email");
		if (StringUtil.isEmpty(email)) {
			toWeb(response, "error");
			return;
		}
		
		BQMember user = super.findCurrUser(request, false);
		if (user != null) {
			toWeb(response, "请重新登录！");
			return;
		}
		
		user = bqMemberService.readCheckUserEmail(email);
		if (user == null) {
			toWeb(response, "success");
			return;
		}
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		if (cacheEmailCode != null && cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
			toWeb(response, SpringContext.getText("msg.common.request.emailCodeTimes", Contents.Local.LOCALE.get()));
			return;
		}
		
		int random6 = MessageInterface.random6();
		String code = String.valueOf(random6);
		try {
			SendMail themail = new SendMail();
			themail.send(user.getF_real_email(), themail.getApiStr(Utils.getWebRoot(request), code));
			
			if (cacheEmailCode == null) {
				cacheEmailCode = new BQTelCode();
				cacheEmailCode.setF_error_times(0);
			}
			
			cacheEmailCode.setF_user_id(user.getF_user_id());
			cacheEmailCode.setF_auth_code(code);
			
			spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
			
		} catch (Exception e) {
			toWeb(response, "发送邮箱验证码失败，请稍后重试！");
			return;
		}
		
		toWeb(response, "success");
		return;
	}
	
	@RequestMapping(value="/logindo", method = RequestMethod.POST)
	public String logindo(HttpServletRequest request, HttpServletResponse response, RedirectAttributes model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		String basePath = super.getRequestRoot(request);
		if (user != null) {
			return "redirect:" + basePath + "spotTrade/spotTrade.do";
		}
		
		JsonTrans jstr = checkLogin(request, response);
		if (jstr.getState().equals("true")) {
			if (!StringUtil.isEmpty(jstr.getRef())) {
				return "redirect:" + basePath + "spotTrade/spotTrade.do";
			}
			return "redirect:" + basePath + "login/googleLogin.do";
		}

		String error = "";
		if (jstr.getMsg() == "-2") {
			error = SpringContext.getText("msg.common.request.emailOrPwdNull", Contents.Local.LOCALE.get());
		} else if (jstr.getMsg() == "-3") {
			error = SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get());
		} else if (jstr.getMsg() == "-4") {
			error = SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get());
		} else if (jstr.getMsg() == "-1") {
			error = SpringContext.getText("msg.login.request.accountIsFrozen", Contents.Local.LOCALE.get());
		}else if (jstr.getMsg() == "-6") {
			error = SpringContext.getText("msg.login.request.loginFailAccountFreeze", Contents.Local.LOCALE.get());
		} else {
			error = SpringContext.getText("msg.common.request.emailOrPwdError", Contents.Local.LOCALE.get());
		}

		model.addFlashAttribute("error", error);
		return "redirect:login.do";
	}
	
	@RequestMapping("/googleLogin")
	public String googleLogin(HttpServletRequest request, Model model) {
		request.setAttribute("locale", Utils.getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		if (user != null) {
			String basePath = super.getRequestRoot(request);
			return "redirect:" + basePath + "spotTrade/spotTrade.do";
		}
		
		String email = (String) request.getSession().getAttribute("email");
		if (!StringUtil.isEmpty(email)) {
			user = bqMemberService.readCheckUserEmail(email);
		}
		if (user == null) {
			return "redirect:login.do";
		} else {
			BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(user.getF_user_id());
			if (setting != null) {
				if (!StringUtil.isEmpty(setting.getF_user_safegoogle())) {
					model.addAttribute("validType", "google");
				} else if (!StringUtil.isEmpty(setting.getF_user_tel())) {
					model.addAttribute("validType", "phone");
					String phone = setting.getF_user_tel();
					if (phone.length() > 5) {
						phone = phone.substring(0, 3) + "******" + phone.substring(phone.length() - 2, phone.length());
					} else {
						phone = phone.substring(0, 1) + "******" + phone.substring(phone.length() - 1, phone.length());
					}
					
					model.addAttribute("phone", phone);
					
					int random = MessageInterface.random6();
					BQTelCode telCode = new BQTelCode();
					telCode.setF_error_times(0);
					telCode.setF_auth_code(random + "");
					telCode.setF_user_tel(setting.getF_user_tel());
					telCode.setF_user_id(setting.getF_user_id());

					String sendRet = "error";
					try {
						sendRet = sendTelCode(user.getF_user_id(), setting.getF_user_tel(), random, setting.getF_user_zone());

					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if (StringUtil.isEmpty(sendRet)) {
						spyMemcachedClient.set("user_login_telcode_" + setting.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, telCode);
						model.addAttribute("sendAlready", true);
					} else {
						model.addAttribute("sendAlready", false); 
					}
					
				} else {
					model.addAttribute("validType", "email");
					
					int random6 = MessageInterface.random6();
					String code = String.valueOf(random6);
					try {
						SendMail themail = new SendMail();
						themail.send(user.getF_real_email(), themail.getApiStr(Utils.getWebRoot(request), code));
						
						BQTelCode cacheEmailCode = new BQTelCode();
						cacheEmailCode.setF_error_times(0);
						cacheEmailCode.setF_user_id(user.getF_user_id());
						cacheEmailCode.setF_auth_code(code);
						spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
						model.addAttribute("sendAlready", true);
					} catch (Exception e) {
						model.addAttribute("sendAlready", false); 
					}
				}
			} else {
				model.addAttribute("validType", "email");
				
				int random6 = MessageInterface.random6();
				String code = String.valueOf(random6);
				try {
					SendMail themail = new SendMail();
					themail.send(user.getF_real_email(), themail.getApiStr(Utils.getWebRoot(request), code));
					
					BQTelCode cacheEmailCode = new BQTelCode();
					cacheEmailCode.setF_error_times(0);
					cacheEmailCode.setF_user_id(user.getF_user_id());
					cacheEmailCode.setF_auth_code(code);
					spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
					model.addAttribute("sendAlready", true);
				} catch (Exception e) {
					model.addAttribute("sendAlready", false);
				}
				
			}
		}
		
		String[] emails = email.split("@");
		if (emails.length == 2) {
			if (emails[0].length() > 4) {
				email = emails[0].substring(0, 3)  + "****"+ email.substring(emails[0].length() - 1, emails[0].length())
						+ "@" + emails[1];
			} else {
				email = emails[0].substring(0, 1)  + "****"+ email.substring(emails[0].length() - 1, emails[0].length())
						+ "@" + emails[1];
			}
			model.addAttribute("email", email);
		}

		return "jsp/googleLogin";
	}
	
	@RequestMapping(value="/doGoogleLogin", method = RequestMethod.POST)
	public String doGoogleLogin(HttpServletRequest request, HttpServletResponse response, RedirectAttributes model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, true);
		String basePath = super.getRequestRoot(request);
		if (user != null) {
			return "redirect:" + basePath + "spotTrade/spotTrade.do";
		}
		
		String email = (String)request.getSession().getAttribute("email");
		if (!StringUtil.isEmpty(email)) {
			user = bqMemberService.readCheckUserEmail(email);
		}
		if (user == null) {
			return "redirect:login.do";
		}
		
		JsonTrans jstr = checkGoogleLogin(request, response, user);
		if ("true".equals(jstr.getState())) {
			loginSuccess(request, response, user);
			return "redirect:" + basePath + "spotTrade/spotTrade.do";
		}
		
		String error = "";
		if (jstr.getMsg() == "-1") {
			error = SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get());
		} else if (jstr.getMsg() == "-2") {
			error = SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get());
		} else if (jstr.getMsg() == "-3") {
			error = SpringContext.getText("msg.login.request.accountIsFrozen", Contents.Local.LOCALE.get());
		}

		model.addFlashAttribute("error", error);
		return "redirect:googleLogin.do";
	}
	
	
	private void loginSuccess(HttpServletRequest request, HttpServletResponse response, BQMember user) {
		request.getSession().setAttribute("userinfo", user);
		// Session最大生命周期（秒） 20 * 60 = 20分钟
		request.getSession().setMaxInactiveInterval(60 * 60);
		CookieHelper.setCookie(response, "email", user.getF_user_email());
		CookieHelper.setCookie(response, "_token", new SafeToken().getToken(), -2, false);

		try {
			String IP = IpMacUtil.getIpAddr(request);// 用户登录IP
			
//			long startTime = TimeUtil.getTimesmorning();
//			long endTime = startTime + 86400l;
//			// 判断用户今天是否是第一次登陆，如果是奖励BFC
//			List<BQMemberLoginLog> logs = bqMemberLoginLogService.readTodayByUserId(startTime, endTime, user.getF_user_id());
//			if (logs == null || logs.size() <= 0) {
//				BQBftRewardSetting rewardSetting = bqBftRewardSettingService.readOne();
//				BQBftRewardRecord record = new BQBftRewardRecord();
//				record.setF_user_id(user.getF_user_id());
//				record.setCurrency_type_id(rewardSetting.getCurrency_type_id());
//				record.setReason(Contents.REWARD_REASON_LOGIN);
//				record.setAmount(rewardSetting.getLogin_reward());
//				record.setNote("每日登陆赠送");
//				bqBftRewardRecordService.bonus(record);
//			}
			
			bqMemberLoginLogService.save(new BQMemberLoginLog(user.getF_user_id(), getTime(), IP, "输入密码登陆成功"));
			// bq_member_status 修改用户登录状态
			bqMemberStatusService.updateStatus(user.getF_user_id(), IP);
			bqCommonFailedloginService.clearLoginTime(user.getF_user_email());// 登录成功后 把登录错误次数清空
		} catch (Exception e) {
		}
		
	}
	
	/**
	 * 用户退出
	 * 
	 * @return String
	 */
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getSession().removeAttribute("userinfo");
		request.getSession().removeAttribute("email");
		request.getSession().removeAttribute("googleCode");
		CookieHelper.setCookie(response, "_token", "", 0, false);
		return "redirect:/home/home.do";
	}
	
	/**
	 * 用户退出
	 * 
	 * @return String
	 */
	@RequestMapping("/otclogout")
	public String otclogout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getSession().removeAttribute("userinfo");
		request.getSession().removeAttribute("email");
		request.getSession().removeAttribute("googleCode");
		CookieHelper.setCookie(response, "_token", "", 0, false);
		return "redirect:/home/home.do?from=" + request.getParameter("from");
	}

	public JsonTrans checkLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JsonTrans jstr = new JsonTrans();
		String email = request.getParameter("email");
		String password = request.getParameter("password");
//		String ValidCode = request.getParameter("validcode");
		if (StringUtil.isEmpty(email) || StringUtil.isEmpty(password)) { // -2用户名密码为空
			jstr.setMsg("-2");
			jstr.setState("false");
			return jstr;
		}
		
		BQMember user = getUserByTelOrEmail(email);
		if (user == null) { // -5不存在该用户
			jstr.setState("false");
			jstr.setMsg("-5");
			return jstr;
		}
		email = user.getF_user_email();
		
		if (this.giveYZM(email)) { // 如果需要验证码
		// if (ValidCode == null || ValidCode.equals("")) {// -3验证码为空
			jstr.setState("false");
			jstr.setMsg("-6"); // 一天内登录失败5次
			return jstr;
			// }
			// String ValidCode_session = (String)
			// request.getSession().getAttribute("validCode");
			// request.getSession().removeAttribute("validCode");
			// String valid = MD5Service.encryptString(ValidCode);
			// if (ValidCode_session == null ||
			// !ValidCode_session.equals(valid)) {// -4验证码不正确
			// jstr.setState("false");
			// jstr.setMsg("-4");// 验证码错误
			// return jstr;
			// }
		}

		if (user.getF_user_state() != 1) { // -1冻结 0无效
			jstr.setState("false");
			jstr.setMsg(String.valueOf(user.getF_user_state()));
			return jstr;
		}
		if (user.getIs_sub_user() == 1 && user.getSub_user_delete() == 1) {
			jstr.setState("false");
			jstr.setMsg("Subaccount deleted");
			return jstr;
		}

		password = super.encryptPwd(password, user.getF_user_salt());

		if (!password.equals(user.getF_user_pwd())) {
			BQCommonFailedlogin bqFl;
			try {
				bqFl = bqCommonFailedloginService.readByName(email);
				String ip = IpMacUtil.getIpAddr(request);
				if (bqFl != null) {
					bqFl.setIp(ip);
					bqFl.setEmail(email);
					bqFl.setCount(bqFl.getCount() + 1);
					bqFl.setLastupdate(getTime());
					bqFl.setUsername(email);
					bqCommonFailedloginService.updateLoginTime(bqFl);
				} else {
					bqFl = new BQCommonFailedlogin();
					bqFl.setIp(ip);
					bqFl.setEmail(email);
					bqFl.setCount(1);
					bqFl.setLastupdate(getTime());
					bqFl.setUsername(email);
					bqFl.setGoogle_count(0);
					bqFl.setGoogle_last(0l);
					bqCommonFailedloginService.save(bqFl);// 更改登录次数
				}

			} catch (Exception e) {
			}
			jstr.setState("false");
			jstr.setMsg("4");
			return jstr;
		}
		
		bqMemberService.checkVipLevel(user);
		
		if (user.getIs_sub_user() == 1) {
			BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(user.getF_user_id());
			if (setting == null || setting.getF_user_safetype() == 0) {
				String basePath = super.getRequestRoot(request);
				loginSuccess(request, response, user);
				jstr.setRef(basePath + "trade/trade.do");
			}
		} 
		
		request.getSession().setAttribute("email", email);
		jstr.setState("true");
		return jstr;
	}
	
	public JsonTrans checkGoogleLogin(HttpServletRequest request, HttpServletResponse response, BQMember user) throws Exception {
		String googleCode = request.getParameter("googleCode");
		JsonTrans jstr = new JsonTrans();
		if (StringUtil.isEmpty(googleCode)) {
			jstr.setMsg("-1");
			jstr.setState("false");
			return jstr;
		}
		long lgoogleCode = 0;
		try {
			lgoogleCode = Long.parseLong(googleCode);
		} catch (Exception e) {
			jstr.setMsg("-2");
			jstr.setState("false");
			return jstr;
		}
		
		String email = user.getF_user_email();
		BQCommonFailedlogin failCount = bqCommonFailedloginService.readByName(email);
		if (failCount != null) {
			if ((System.currentTimeMillis() / 1000) - failCount.getGoogle_last() < 86400 && failCount.getGoogle_count() > 4) {
				request.getSession().removeAttribute("email");
				jstr.setMsg("-3");
				jstr.setState("false");
				return jstr;
			}
		}
		
		BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(user.getF_user_id());
		boolean result = false;
		if (setting != null && !StringUtil.isEmpty(setting.getF_user_safegoogle())) {
			result = GoogleAuthenticator.check_code(setting.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
		} else if (setting != null && !StringUtil.isEmpty(setting.getF_user_tel())) {
			// tel code
			String emailCode = googleCode;
			BQTelCode cacheEmailCode = spyMemcachedClient.get("user_login_telcode_" + user.getF_user_id());
			if (cacheEmailCode == null || !emailCode.equals(cacheEmailCode.getF_auth_code())) {
				result = false;
			} else {
				result = true;
				spyMemcachedClient.delete("user_login_telcode_" + user.getF_user_id());
			}
			
		} else {
			// email code
			String emailCode = googleCode;
			BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
			if (cacheEmailCode == null || !emailCode.equals(cacheEmailCode.getF_auth_code())) {
				result = false;
			} else {
				result = true;
				spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
			}
		}
		
		if (!result) {
			BQCommonFailedlogin bqFl;
			try {
				bqFl = bqCommonFailedloginService.readByName(email);
				String ip = IpMacUtil.getIpAddr(request);
				if (bqFl != null) {
					bqFl.setIp(ip);
					bqFl.setGoogle_count(bqFl.getGoogle_count() + 1);
					bqFl.setGoogle_last(getTime());
					bqCommonFailedloginService.updateLoginTime(bqFl);
				} else {
					bqFl = new BQCommonFailedlogin();
					bqFl.setIp(ip);
					bqFl.setEmail(email);
					bqFl.setCount(0);
					bqFl.setLastupdate(0l);
					bqFl.setUsername(email);
					bqFl.setGoogle_count(bqFl.getGoogle_count() + 1);
					bqFl.setGoogle_last(getTime());
					bqCommonFailedloginService.save(bqFl);// 更改登录次数
				}
			} catch (Exception e) {
			}
			
			jstr.setMsg("-2");
			jstr.setState("false");
			return jstr;
		}
		
		request.getSession().removeAttribute("email");
		request.getSession().setAttribute("googleCode", googleCode);
		jstr.setState("true");
		return jstr;
	}
	
	@RequestMapping("checkLogin")
	public String checkLogin(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String,Object> mapToken = new HashMap<String, Object>();
		String checkToken = checkToken(request, mapToken);
		
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			if (!StringUtil.isEmpty(checkToken)) {
				tickerJson.put("code", 10001);
				tickerJson.put("msg", checkToken);
				model.addAttribute("ticker", tickerJson.toString());
			} else {	// 当前未登录，之前已登录，且token有效，返回token
				String token = (String) mapToken.get("token");
				BQMember oldUser = (BQMember) mapToken.get("user");
				tickerJson.put("msg", "not exist");
				tickerJson.put("token", token);
				tickerJson.put("userName", oldUser.getF_user_email());
				
				loginSuccess(request, response, oldUser);
			}
		} else {
			tickerJson.put("userName", user.getF_user_email());
			if (!StringUtil.isEmpty(checkToken)) { // 当前登录，之前没有登录，创建新token返回
				String newToken = JwtToken.createToken(user.getF_user_id(), user.getF_user_email());
				tickerJson.put("msg", "new login");
				tickerJson.put("token", newToken);
			} else { // 当前登录，之前已登录，检查登录用户是否一致
				BQMember oldUser = (BQMember) mapToken.get("user");
				if (oldUser.getF_user_id() != user.getF_user_id()) {
					String newToken = JwtToken.createToken(user.getF_user_id(), user.getF_user_email());
					tickerJson.put("msg", "new login");
					tickerJson.put("token", newToken);
				} else {
					String token = (String) mapToken.get("token");
					tickerJson.put("msg", "not exist");
					tickerJson.put("token", token);
				}
			}
		}
		
		model.addAttribute("ticker", tickerJson);
		return "jsp/api/ticker";
	}
	
	private String checkToken(HttpServletRequest request, Map<String, Object> map) {
		String token = request.getParameter("token");
		if (StringUtil.isEmpty(token)) {
			return "参数错误，token参数错误";
		}
		
		String verifyToken = JwtToken.verifyToken(token, 3 * 24 * 60);
		if (StringUtil.isEmpty(verifyToken)) {
			return "参数错误，token参数错误";
		}
		map.put("token", verifyToken);
		
		Map<String, Claim> claims = JwtToken.decryptToken(token);
		String userId = claims.get("userId").asString();
		long uid = Convert.toLong(userId, 0);
		if (uid > 0) {
			BQMember user = bqMemberService.readUserById(uid);
			if (user == null) {
				return "参数错误，token参数错误，用户无效";
			}
			if (user.getF_user_state() != 1) {
				return "参数错误，token参数错误";
			}
			map.put("user", user);
		}

		return "";
	}

	public boolean giveYZM(String email) {
		BQCommonFailedlogin failCount = bqCommonFailedloginService.readByName(email);
		if (failCount != null) {
			if ((System.currentTimeMillis() / 1000) - failCount.getLastupdate() < 86400 && failCount.getCount() > 4) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public int getTime() {// 得到时间戳
		return (int) (System.currentTimeMillis() / 1000);
	}

	public BQMember getUserByTelOrEmail(String emailOrTel) {
		BQMember user = bqMemberService.readCheckUserEmail(emailOrTel);
		if (user != null) return user;
		
		if (emailOrTel.matches("[0-9]+")){
			String tel = emailOrTel;
			BQMemberSafeSettings setting = bqMemberSafeSettingsService.readByTel(tel);
			if (setting !=null) {
				user = bqMemberService.readUserById(setting.getF_user_id());
				return user;
			}
		} else {
			user = bqMemberService.readByRealEmail(emailOrTel);
			return user;
		}
		
		return null;
	}
}
