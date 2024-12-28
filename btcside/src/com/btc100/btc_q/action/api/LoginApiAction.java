package com.btc100.btc_q.action.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.GoogleAuthenticator;
import com.btc100.btc_q.common.MessageInterface;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQCommonFailedlogin;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLoginLog;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQTelCode;
import com.btc100.btc_q.service.BQCommonFailedloginService;
import com.btc100.btc_q.service.BQMemberLoginLogService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberStatusService;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.JwtToken;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 用户登录API
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/loginApi")
public class LoginApiAction extends BaseApiAction {

	Logger logger = Logger.getLogger(LoginApiAction.class);
	
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
	public String sliderLogin(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String random = RandomUtil.getTransNo();
		userJson.put("data", random);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}

	/**
	 * 登录操作
	 * 
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		if (StringUtil.isEmpty(email) || StringUtil.isEmpty(password)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机号或密码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = getUserByTelOrEmail(email);
		if (user == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "邮箱/手机号或密码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		email = user.getF_user_email();
		
		if (user.getF_user_state() != 1) {// -1冻结 0无效
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "用户冻结或无效");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (user.getIs_sub_user() == 1 && user.getSub_user_delete() == 1) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "子账号无效");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQCommonFailedlogin failCount = bqCommonFailedloginService.readByName(email);
		if (failCount != null) {
			if ((System.currentTimeMillis() / 1000) - failCount.getLastupdate() < 86400 && failCount.getCount() > 4) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "密码输错超过4次，暂时被冻结");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
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
				logger.error("验证登录密码异常", e);
			}
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机号或密码错误");
			model.addAttribute("ticker", tradesStr);	
			return apiData(response);
		}
		
		int code = 0;
		BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (setting != null) {
			if (!StringUtil.isEmpty(setting.getF_user_safegoogle())) {
				code = 1;
			} else if (!StringUtil.isEmpty(setting.getF_user_tel())) {
				code = 2;
				String phone =  setting.getF_user_tel();
				phone = phone.substring(0, 3) + "******" + phone.substring(phone.length() - 2, phone.length());
				userJson.put("phone", phone);
			} else if (user.getIs_sub_user() == 0) {
				code = 3;
			}
		} else if (user.getIs_sub_user() == 0) {
			code = 3;
		}
		
		// for AppStore test
		if (user.getF_user_email().equals("smliufj@163.com")) {
			code = 0;
		}
		
		if (code != 0) {
			String number = RandomUtil.getTransNo();
			spyMemcachedClient.set("LoginApiAction_" + email, BTCQMemecacheKeys.TIMES.BPI, number);
			
			userJson.put("code", code);
			userJson.put("number", number);
			model.addAttribute("ticker", userJson);
			return apiData(response);
		}
		
		try {
			String IP = IpMacUtil.getIpAddr(request);// 用户登录IP
			doLoginRewardBft(user);
			bqMemberLoginLogService.save(new BQMemberLoginLog(user.getF_user_id(), getTime(), IP, "输入密码登陆成功"));
			// bq_member_status 修改用户登录状态
			bqMemberStatusService.updateStatus(user.getF_user_id(), IP);
			bqCommonFailedloginService.clearLoginTime(user.getF_user_email());// 登录成功后 把登录错误次数清空
		} catch (Exception e) {
			logger.error("登录异常", e);
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "登录异常");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String token = JwtToken.createToken(user.getF_user_id(), email);
		userJson.put("data", token);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/sendTelCode", method = RequestMethod.POST)
	public String sendTelCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String email = request.getParameter("email");
		if (StringUtil.isEmpty(email)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机号错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = getUserByTelOrEmail(email);
		if (user == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "登录超时，请退出重试！");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		email = user.getF_user_email();
		
		String number = request.getParameter("number");
		String cacheNum = spyMemcachedClient.get("LoginApiAction_" + email);
		if (StringUtil.isEmpty(cacheNum) || !cacheNum.equals(number)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "登录超时，请退出重试！");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (StringUtil.isEmpty(bqms.getF_user_tel())) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "邮箱/手机号错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		int random = MessageInterface.random6();
		BQTelCode telCode = new BQTelCode();
		telCode.setF_error_times(0);
		telCode.setF_auth_code(random + "");
		telCode.setF_user_tel(bqms.getF_user_tel());
		telCode.setF_user_id(bqms.getF_user_id());

		String sendRet = "error";
		try {
			sendRet = sendTelCode(user.getF_user_id(), bqms.getF_user_zone(), bqms.getF_user_tel(), random);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		if (StringUtil.isEmpty(sendRet)) {
			spyMemcachedClient.set("user_login_telcode_" + bqms.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, telCode);
		} else {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, sendRet);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/sendEmailCode", method = RequestMethod.POST)
	public String sendEmailCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String email = request.getParameter("email");
		if (StringUtil.isEmpty(email)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机号错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		BQMember user = getUserByTelOrEmail(email);
		if (user == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "登录超时，请退出重试！");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		email = user.getF_user_email();
		
		String number = request.getParameter("number");
		String cacheNum = spyMemcachedClient.get("LoginApiAction_" + email);
		if (StringUtil.isEmpty(cacheNum) || !cacheNum.equals(number)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "登录超时，请退出重试！");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
		if (cacheEmailCode != null && cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "错误次数过多！");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
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
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "发送失败，请稍后重试!");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 谷歌登录操作
	 * 
	 */
	@RequestMapping(value = "/googleLogin", method = RequestMethod.POST)
	public String googleLogin(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String email = request.getParameter("email");
		String code = request.getParameter("code");
		if (StringUtil.isEmpty(email) || StringUtil.isEmpty(code)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机号或验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = getUserByTelOrEmail(email);
		if (user == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "邮箱/手机号或密码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		email = user.getF_user_email();
		
		String number = request.getParameter("number");
		String cacheNum = spyMemcachedClient.get("LoginApiAction_" + email);
		if (StringUtil.isEmpty(cacheNum) || !cacheNum.equals(number)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "登录超时，请退出重试！");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		long googleCode = 0;
		try {
			googleCode = Long.parseLong(code);
		} catch (Exception e) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQCommonFailedlogin failCount = bqCommonFailedloginService.readByName(email);
		if (failCount != null) {
			if ((System.currentTimeMillis() / 1000) - failCount.getLastupdate() < 86400 && failCount.getCount() > 4) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "密码输错超过4次，暂时被冻结");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			if ((System.currentTimeMillis() / 1000) - failCount.getGoogle_last() < 86400 && failCount.getGoogle_count() > 4) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "验证码输错超过4次，暂时被冻结");
				model.addAttribute("ticker", tradesStr);
				
				spyMemcachedClient.delete("LoginApiAction_" + email);
				
				return apiData(response);
			}
		}
		
		boolean result = false;
		BQMemberSafeSettings setting = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (setting != null) {
			if (!StringUtil.isEmpty(setting.getF_user_safegoogle())) {
				result = GoogleAuthenticator.check_code(setting.getF_user_safegoogle(), googleCode, System.currentTimeMillis());
			} else if (!StringUtil.isEmpty(setting.getF_user_tel())) {
				// tel code
				String emailCode = code;
				BQTelCode cacheEmailCode = spyMemcachedClient.get("user_login_telcode_" + user.getF_user_id());
				if (cacheEmailCode == null || !emailCode.equals(cacheEmailCode.getF_auth_code())) {
					result = false;
				} else {
					result = true;
					spyMemcachedClient.delete("user_login_telcode_" + user.getF_user_id());
				}
			} else if (user.getIs_sub_user() == 0) {
				// email code
				String emailCode = code;
				BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
				if (cacheEmailCode == null || !emailCode.equals(cacheEmailCode.getF_auth_code())) {
					result = false;
				} else {
					result = true;
					spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + user.getF_user_id());
				}
			}
		} else if (user.getIs_sub_user() == 0) {
			// email code
			String emailCode = code;
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
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		try {
			String IP = IpMacUtil.getIpAddr(request);// 用户登录IP
			doLoginRewardBft(user);
			bqMemberLoginLogService.save(new BQMemberLoginLog(user.getF_user_id(), getTime(), IP, "输入密码登陆成功"));
			// bq_member_status 修改用户登录状态
			bqMemberStatusService.updateStatus(user.getF_user_id(), IP);
			bqCommonFailedloginService.clearLoginTime(user.getF_user_email());// 登录成功后 把登录错误次数清空
		} catch (Exception e) {
			logger.error("登录异常", e);
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "登录异常");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		spyMemcachedClient.delete("LoginApiAction_" + email);
		String token = JwtToken.createToken(user.getF_user_id(), email);
		userJson.put("data", token);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 手势或者指纹登录操作
	 * 
	 */
	@RequestMapping(value = "/quickLogin", method = RequestMethod.POST)
	public String quickLogin(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String email = request.getParameter("email");
		String token = request.getParameter("oldToken");
		String loginTag = request.getParameter("loginTag");
		if (StringUtil.isEmpty(email) || StringUtil.isEmpty(token)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机号或oldToken不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember user = getUserByTelOrEmail(email);
		if (user == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "用户不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		email = user.getF_user_email();
		
		boolean isThreeDay = JwtToken.checkTimeAndName(token, email);
		if (!isThreeDay) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "登录已失效，请使用账号密码重新登录");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (user.getF_user_state() != 1) {// -1冻结 0无效
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "用户冻结或无效");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (user.getIs_sub_user() == 1 && user.getSub_user_delete() == 1) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "子账号无效");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String note = "指纹解锁登录成功";
		if (!StringUtil.isEmpty(loginTag)) 
			note = "手势密码登录成功";
		try {
			String IP = IpMacUtil.getIpAddr(request);// 用户登录IP
			doLoginRewardBft(user);
			bqMemberLoginLogService.save(new BQMemberLoginLog(user.getF_user_id(), getTime(), IP, note));
			// bq_member_status 修改用户登录状态
			bqMemberStatusService.updateStatus(user.getF_user_id(), IP);
			bqCommonFailedloginService.clearLoginTime(user.getF_user_email());// 登录成功后 把登录错误次数清空
		} catch (Exception e) {
			logger.error("登录异常", e);
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "登录异常");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		token = JwtToken.createToken(user.getF_user_id(), email);
		userJson.put("data", token);
		
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	private void doLoginRewardBft(BQMember user) {
//		
//		long startTime = TimeUtil.getTimesmorning();
//		long endTime = startTime + 86400l;
//		// 判断用户今天是否是第一次登陆，如果是奖励BFC
//		List<BQMemberLoginLog> logs = bqMemberLoginLogService.readTodayByUserId(startTime, endTime, user.getF_user_id());
//		if (logs == null || logs.size() <= 0) {
//			BQBftRewardSetting rewardSetting = bqBftRewardSettingService.readOne();
//			BQBftRewardRecord record = new BQBftRewardRecord();
//			record.setF_user_id(user.getF_user_id());
//			record.setCurrency_type_id(rewardSetting.getCurrency_type_id());
//			record.setReason(Contents.REWARD_REASON_LOGIN);
//			record.setAmount(rewardSetting.getLogin_reward());
//			record.setNote("每日登陆赠送");
//			bqBftRewardRecordService.bonus(record);
//		}
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
