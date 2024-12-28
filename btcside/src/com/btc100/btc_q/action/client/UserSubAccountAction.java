package com.btc100.btc_q.action.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.DATA_CreditNumber;
import com.btc100.btc_q.common.GoogleAuthenticator;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQBftRewardRecord;
import com.btc100.btc_q.entity.BQBftRewardSetting;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQIeoAccount;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberArea;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQSubMember;
import com.btc100.btc_q.entity.BQSubMemberTransferLog;
import com.btc100.btc_q.entity.BQTelCode;
import com.btc100.btc_q.entity.BQUserAppKey;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQBftRewardRecordService;
import com.btc100.btc_q.service.BQBftRewardSettingService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQIeoAccountService;
import com.btc100.btc_q.service.BQMemberAreaService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQSubMemberService;
import com.btc100.btc_q.service.BQSubMemberTransferLogService;
import com.btc100.btc_q.service.BQUserAppKeyService;
import com.btc100.btc_q.service.RegisterSerivce;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.AjaxCheckVO;
import com.btc100.btc_q.vo.CoinAssetsVO;
import com.btc100.btc_q.vo.PagerModel;
import com.google.gson.Gson;

@Controller
@RequestMapping("/userSubAccount")
public class UserSubAccountAction extends BaseAction {

	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQSubMemberService bqSubMemberService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private RegisterSerivce registerService;
	@Autowired
	private BQBftRewardSettingService bqBftRewardSettingService;
	@Autowired
	private BQBftRewardRecordService bqBftRewardRecordService;
	@Autowired
	private BQMemberAreaService bqMemberAreaService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQSubMemberTransferLogService bqTransferLogService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQIeoAccountService bqIeoAccountService;
	@Autowired
	private BQUserAppKeyService bqUserAppKeyService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	// 查询当前登录用户（母账户）下所有账户信息
	@RequestMapping("/subUser")
	public ModelAndView subUser(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		String loginName = request.getParameter("loginName");
		String remark = request.getParameter("remark");
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		if (user.getIs_sub_user() == 1) {
			ModelAndView mv = new ModelAndView();
			model.addAttribute("isSubUser", true);
			PagerModel pm = new PagerModel();
			model.addAttribute("pm", pm);
			mv.setViewName("jsp/user_subAccount");
			return mv;
		}
		
		PagerModel pm = bqSubMemberService.readByUserId(user.getF_user_id(), loginName, remark);
		model.addAttribute("pm", pm);
		List<Object[]> subUserList = bqSubMemberService.readAllSubUser(user.getF_user_id());
		model.addAttribute("subUserList", subUserList);
		
		List<BQCurrencyType> currencyTypes = bqCurrencyTypeService.findAll();
		model.addAttribute("currencyTypes", currencyTypes);
		
		List<BQSubMemberTransferLog> logs = bqTransferLogService.readByUserId(user.getF_user_id(), "", 0, -1, "", 0, 0, 10);
		model.addAttribute("logs", logs);
		
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqss.getF_user_safetype() == 1) {
			model.addAttribute("tel", bqss.getF_user_tel());
			model.addAttribute("showtel", 1);
			model.addAttribute("showgoogle", 0);
		} else if (bqss.getF_user_safetype() == 2) {
			model.addAttribute("showgoogle", 1);
			model.addAttribute("showtel", 0);
		} else if (bqss.getF_user_safetype() == 3) {
			model.addAttribute("tel", bqss.getF_user_tel());
			model.addAttribute("showtel", 1);
			model.addAttribute("showgoogle", 1);
		} else {
			model.addAttribute("showgoogle", 0);
			model.addAttribute("showtel", 0);
			model.addAttribute("applayerror", true);
		}
		
		model.addAttribute("pageN", "userSubAccount/subUser.do");
		model.addAttribute("loginName", loginName);
		model.addAttribute("remark", remark);

		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_subAccount");
		return mv;
	}

	// 1、创建子账号
	// 、登录名（不要非法字符,唯一，4-32个字符或2-16个汉字），密码，备注，验证码（谷歌，手机）
	// 、子账号邀请人是母账户
	@RequestMapping("/subUserCreate")
	public void subUserCreate(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");

		BQMember motherUser = super.findCurrUser(request, true);
		if (motherUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (motherUser.getIs_sub_user() == 1) {
			checkVO.setMsg("很抱歉！该功能子账号未开放");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String code = request.getParameter("code");// 手机验证码
		String googleCode = request.getParameter("google");

		// 验证登录名
		String validateEmail = validateEmail(email, password);
		if (!StringUtil.isEmpty(validateEmail)) {
			checkVO.setMsg(validateEmail);
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		HttpSession session = request.getSession();
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(motherUser.getF_user_id());
		// 验证手机验证码和谷歌验证码
		String validateSafe = validateSafe(bqss, code, googleCode, session);
		if (!StringUtil.isEmpty(validateSafe)) {
			checkVO.setMsg(validateSafe);
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		BQMember subUser = new BQMember();
		subUser.setF_user_email(email);
		subUser.setF_real_email(email);
		subUser.setF_user_pwd(password);
		// 注册子账号，邀请人：母账户
		registerSub(request, motherUser, subUser);

		if (bqss.getF_user_safetype() == 1 || bqss.getF_user_safetype() == 3) {
			session.removeAttribute("telcode" + bqss.getF_user_tel());
		}

		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.pg.subUser.createSuccess", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	// 1、创建子账号，批量创建页面
	@RequestMapping("/toSubUserBatchCreate")
	public ModelAndView toSubUserBatchCreate(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		if (user.getIs_sub_user() == 1) {
			model.addAttribute("isSubUser", true);
		}
		
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (bqss.getF_user_safetype() == 1) {
			model.addAttribute("tel", bqss.getF_user_tel());
			model.addAttribute("showtel", 1);
			model.addAttribute("showgoogle", 0);
		} else if (bqss.getF_user_safetype() == 2) {
			model.addAttribute("showgoogle", 1);
			model.addAttribute("showtel", 0);
		} else if (bqss.getF_user_safetype() == 3) {
			model.addAttribute("tel", bqss.getF_user_tel());
			model.addAttribute("showtel", 1);
			model.addAttribute("showgoogle", 1);
		} else {
			model.addAttribute("showgoogle", 0);
			model.addAttribute("showtel", 0);
			model.addAttribute("applayerror", true);
		}
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_subBatchCreate");
		return mv;
	}

	// 1、创建子账号，批量创建
	@RequestMapping("/subUserBatchCreate")
	public void subUserBatchCreate(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");

		BQMember motherUser = super.findCurrUser(request, true);
		if (motherUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (motherUser.getIs_sub_user() == 1) {
			checkVO.setMsg("很抱歉！该功能子账号未开放");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String code = request.getParameter("code");// 手机验证码
		String googleCode = request.getParameter("google");
		String[] infoArr = request.getParameterValues("info");
		if (StringUtil.isEmpty(infoArr)) {
			checkVO.setMsg("不能提交空");
			toWeb(response, gson.toJson(checkVO));
			return;
		
		}
		if (infoArr.length > 20) {
			checkVO.setMsg("子账号超过20个");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		List<BQMember> subUsers = new ArrayList<BQMember>();
		for (String info : infoArr) {
			String[] userInfo = info.split(",");
			if (userInfo.length != 2) {
				continue;
			}

			String email = userInfo[0];
			String password = userInfo[1];
			// 验证邮箱唯一，邮箱密码是否非法字符
			String validateEmail = validateEmail(email, password);
			if (!StringUtil.isEmpty(validateEmail)) {
				checkVO.setMsg(validateEmail);
				toWeb(response, gson.toJson(checkVO));
				return;
			}

			BQMember subUser = new BQMember();
			subUser.setF_user_email(email);
			subUser.setF_real_email(email);
			subUser.setF_user_pwd(password);
			subUsers.add(subUser);
		}

		HttpSession session = request.getSession();
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(motherUser.getF_user_id());
		// 验证手机验证码和谷歌验证码
		String validateSafe = validateSafe(bqss, code, googleCode, session);
		if (!StringUtil.isEmpty(validateSafe)) {
			checkVO.setMsg(validateSafe);
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		for (BQMember subUser : subUsers) {
			// 注册子账号，邀请人：母账户
			registerSub(request, motherUser, subUser);
		}

		if (bqss.getF_user_safetype() == 1 || bqss.getF_user_safetype() == 3) {
			session.removeAttribute("telcode" + bqss.getF_user_tel());
		}

		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.pg.subUser.createSuccess", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	private String validateEmail(String email, String password) {

		if (!StringUtil.validateString(email) || !StringUtil.validateString(password)) {
			return "内容包含非法字符";
		}

		if (null != bqMemberService.readCheckUserEmail(email)) {
			return SpringContext.getText("msg.register.request.hasRegister", Contents.Local.LOCALE.get());
		}
		
		int emailLength = getLength(email);
		if (emailLength < 4 || emailLength > 32) {
			return SpringContext.getText("msg.register.request.loginNameLength", Contents.Local.LOCALE.get());
		}

		return null;
	}
	
	private int getLength(String value) {
		int length = 0;
		String chinese = "[\u0391-\uFFE5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				length += 2;
			} else {
				length += 1;
			}
		}
		return length;
	}

	private String validateSafe(BQMemberSafeSettings bqss, String code, String googleCode, HttpSession session) {

		// 手机验证码判断
		if (bqss.getF_user_safetype() == 1 || bqss.getF_user_safetype() == 3) {
			if (null == code || code.isEmpty()) {
				return SpringContext.getText("msg.common.request.codeNull", Contents.Local.LOCALE.get());
			}
			BQTelCode codenum = (BQTelCode) session.getAttribute("telcode" + bqss.getF_user_tel());
			if (codenum == null) {
				return SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get());
			}
			if (!codenum.getF_user_tel().equals(bqss.getF_user_tel())) {
				return SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get());
			}
			if (!code.equals(codenum.getF_auth_code())) {

				codenum.setF_error_times(codenum.getF_error_times() + 1);
				if (codenum.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					session.removeAttribute("telcode" + bqss.getF_user_tel());
				} else {
					session.setAttribute("telcode" + bqss.getF_user_tel(), codenum);
				}

				return SpringContext.getText("msg.common.request.codeError", Contents.Local.LOCALE.get());
			}
		}
		// 判断谷歌验证码
		if (bqss.getF_user_safetype() == 2 || bqss.getF_user_safetype() == 3) { // 谷歌验证
			if (null == googleCode || googleCode.isEmpty()) {
				return SpringContext.getText("msg.common.request.googleCodeNull", Contents.Local.LOCALE.get());
			}
			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				return SpringContext.getText("msg.common.request.googleCodeNull", Contents.Local.LOCALE.get());
			}
			boolean bresult = GoogleAuthenticator.check_code(bqss.getF_user_safegoogle(), lgoogleCode,
					System.currentTimeMillis());
			if (!bresult) {
				return SpringContext.getText("msg.common.request.googleCodeError", Contents.Local.LOCALE.get());
			}
		}

		return null;
	}

	private void registerSub(HttpServletRequest request, BQMember motherUser, BQMember subUser) {

		String password = subUser.getF_user_pwd();
		String salt = RandomUtil.getRandomString(6);
		try {
			password = MD5Service.encryptString(password).toLowerCase();
			password = MD5Service.encryptString(password + salt).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}

		subUser.setF_user_pwd(password);
		subUser.setF_user_salt(salt);
		subUser.setF_user_email_status(1);
		subUser.setF_user_credits(DATA_CreditNumber.CREDIT_ONE_REGISTER + DATA_CreditNumber.CREDIT_ONE_SUREEMAIL);
		subUser.setF_user_level_id(1);// 用户VIP级别(bq_member_level:level_id)',
		subUser.setF_user_state(1);// '用户状态（0:无效,1:有效,-1:冻结）'
		subUser.setF_user_salt(salt);
		subUser.setF_user_origin("");
		subUser.setF_first_login(1);
		subUser.setIs_sub_user(1);
		// 注册时间
		subUser.setF_user_time((int) (System.currentTimeMillis() / 1000));

		// 获取母账户地区
		BQMemberArea motherArea = bqMemberAreaService.readByUserId(motherUser.getF_user_id());
		BQMemberArea subArea = new BQMemberArea();
		subArea.setCountry_code(motherArea.getCountry_code());
		subArea.setCountry_name(motherArea.getCountry_name());
		subArea.setProvince_code(motherArea.getProvince_code());
		subArea.setProvince_name(motherArea.getProvince_name());
		subArea.setCity_code(motherArea.getCity_code());
		subArea.setCity_name(motherArea.getCity_name());
		registerService.registerSubUser(motherUser, subUser, IpMacUtil.getIpAddr(request), subArea);

		// 奖励BFC
		BQBftRewardSetting setting = bqBftRewardSettingService.readOne();
		doRegisterRewardBft(subUser.getF_user_id(), setting);
	}

	private void doRegisterRewardBft(long userId, BQBftRewardSetting setting) {
		if (setting != null && setting.getRegister_reward().compareTo(BigDecimal.ZERO) > 0 && setting.getOld_register_rewarded()) {
			BQBftRewardRecord record = new BQBftRewardRecord();
			record.setF_user_id(userId);
			record.setCurrency_type_id(setting.getCurrency_type_id());
			record.setReason(Contents.REWARD_REASON_REGISTER);
			record.setAmount(setting.getRegister_reward());
			record.setNote("用户注册赠送");

			bqBftRewardRecordService.bonus(record);
		}
	}
	
	@RequestMapping("/subUserAssets")
	public ModelAndView subUserAssets(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember motherUser = super.findCurrUser(request, false);
		if (motherUser == null) {
			return new ModelAndView(super.toLogin());
		}
		if (motherUser.getIs_sub_user() == 1) {
			model.addAttribute("isSubUser", true);
		}

		String subUidStr = request.getParameter("subUserId"); // 子账号id
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			return new ModelAndView("redirect:/userSubAccount/subUser.do");
		}
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			return new ModelAndView("redirect:/userSubAccount/subUser.do");
		}
		model.addAttribute("subUser", subUser);
		
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(motherUser.getF_user_id());
		if (bqss.getF_user_safetype() == 0) {
			model.addAttribute("applayerror", true);
		}

		ArrayList<CoinAssetsVO> bqAccounts = new ArrayList<CoinAssetsVO>();
		List<BQCurrencyType> currencies = bqCurrencyTypeService.readAll();
		List<BQAccount> assets = bqAccountService.readAllByUserId(subUid);
		for (BQAccount bqAccount : assets) {
			boolean had = false;
			for (BQCurrencyType currType : currencies) {
				if (currType.getType_id() == bqAccount.getCurrency_type_id()) {
					had = true;
					break;
				}
			}
			
			if (!had) continue;
			
			CoinAssetsVO coinAssetsVO = new CoinAssetsVO();
			coinAssetsVO.setCoinId(bqAccount.getCurrency_type_id());
			coinAssetsVO.setFreeze(bqAccount.getBtc_locked().doubleValue());
			coinAssetsVO.setAvailable(bqAccount.getBtc().doubleValue());
			BigDecimal total = bqAccount.getBtc_locked().add(bqAccount.getBtc());
			BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(bqAccount.getCurrency_type_id());
			coinAssetsVO.setTotal(total.doubleValue());
			coinAssetsVO.setCoinName(currencyType.getType_name());
			coinAssetsVO.setCoinDescribleZh(currencyType.getDescription_zh());
			coinAssetsVO.setCoinDescribleEn(currencyType.getDescription_en());
			coinAssetsVO.setCoinDescribleJp(currencyType.getDescription_jp());
			coinAssetsVO.setCoinDescribleKr(currencyType.getDescription_kr());
			coinAssetsVO.setCoinDescribleHk(currencyType.getDescription_hk());
			bqAccounts.add(coinAssetsVO);
		}
		
		BQIeoAccount account = bqIeoAccountService.getAccountByUserId(subUid);
		if (account != null) {
			BigDecimal ieoLock = account.getLocked();
			model.addAttribute("ieoLock", ieoLock.doubleValue());
			model.addAttribute("ieoUnlock", account.getUnlocked().doubleValue());
		}
		
		model.addAttribute("bqAccounts", bqAccounts);
		model.addAttribute("currencies", currencies);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_subAssets");
		return mv;
	}

	// 以下属于更新子账号用户（update）
	// 重置密码
	@RequestMapping("/resetPwd")
	public void resetPwd(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");

		BQMember motherUser = super.findCurrUser(request, true);
		if (motherUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (motherUser.getIs_sub_user() == 1) {
			checkVO.setMsg("很抱歉！该功能子账号未开放");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String subUidStr = request.getParameter("subUserId"); // 子账号id
		String newpassword = request.getParameter("newpasswd1"); // 新密码
		String newpassword2 = request.getParameter("newpasswd2"); // 确认新密码
		String code = request.getParameter("telCode");// 手机验证码
		String googleCode = request.getParameter("googleCode");
		
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subMemberNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		HttpSession session = request.getSession();
		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(motherUser.getF_user_id());
		// 验证手机验证码和谷歌验证码
		String validateSafe = validateSafe(bqss, code, googleCode, session);
		if (!StringUtil.isEmpty(validateSafe)) {
			checkVO.setMsg(validateSafe);
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subMemberIsDelete", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (newpassword == null || newpassword2 == null || newpassword.isEmpty() || newpassword2.isEmpty()) {
			checkVO.setMsg(SpringContext.getText("msg.password.request.pwdCantBeNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (!newpassword.equals(newpassword2)) {
			checkVO.setMsg(SpringContext.getText("msg.password.request.twicePwdNotEq", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String newpassword1;
		try {
			newpassword1 = MD5Service.encryptString(newpassword).toLowerCase();
			newpassword1 = MD5Service.encryptString(newpassword1 + subUser.getF_user_salt()).toLowerCase();

			subUser.setF_user_pwd(newpassword);
			bqMemberService.updatepassword(subUser.getF_user_id(), newpassword1);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if (bqss.getF_user_safetype() == 1 || bqss.getF_user_safetype() == 3) {
			session.removeAttribute("telcode" + bqss.getF_user_tel());
		}

		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.pg.trade.optionStatusSuc", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	// 冻结和解冻用户
	@RequestMapping("/updateFreeze")
	public void updateFreeze(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");

		BQMember motherUser = super.findCurrUser(request, true);
		if (motherUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (motherUser.getIs_sub_user() == 1) {
			checkVO.setMsg("很抱歉！该功能子账号未开放");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String subUidStr = request.getParameter("subUserId"); // 子账号id
		
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subMemberNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subMemberIsDelete", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		bqMemberService.updatefreeze(subUser.getF_user_id(), subUser.getF_user_state());

		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.pg.trade.optionStatusSuc", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	// 修改备注
	@RequestMapping("/updateSubNote")
	public void updateSubNote(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");

		BQMember motherUser = super.findCurrUser(request, true);
		if (motherUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (motherUser.getIs_sub_user() == 1) {
			checkVO.setMsg("很抱歉！该功能子账号未开放");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String subUidStr = request.getParameter("subUserId"); // 子账号id
		String note = request.getParameter("note");
		
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subMemberNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subMemberIsDelete", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (note == null) note = "";
		subUser.setSub_user_note(note);
		bqMemberService.update(subUser);

		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.pg.trade.optionStatusSuc", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	// 删除（is_delete）
	@RequestMapping("/deleteSubUser")
	public void deleteSubUser(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");

		BQMember motherUser = super.findCurrUser(request, true);
		if (motherUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (motherUser.getIs_sub_user() == 1) {
			checkVO.setMsg("很抱歉！该功能子账号未开放");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String subUidStr = request.getParameter("subUserId"); // 子账号id
		
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subMemberNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subMemberIsDelete", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQAccount subAccount = bqAccountService.readAccount(subUser.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
		if (subAccount != null) {
			BigDecimal usdt = subAccount.getBtc().add(subAccount.getBtc_locked());
			if (usdt.compareTo(BigDecimal.TEN) > 0) {
				checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subDeleteFail", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		subUser.setSub_user_delete(1);
		bqMemberService.update(subUser);

		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.pg.trade.optionStatusSuc", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}

	// 资金划转操作
	@RequestMapping("/transfer")
	public void transfer(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");

		BQMember motherUser = super.findCurrUser(request, true);
		if (motherUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (motherUser.getIs_sub_user() == 1) {
			checkVO.setMsg("很抱歉！该功能子账号未开放");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String subUidStr = request.getParameter("subUserId"); // 子账号id
		
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subMemberNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subMemberIsDelete", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(request.getParameter("amount"), 0.0));
		amount = amount.setScale(9, RoundingMode.HALF_UP);
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			checkVO.setMsg(SpringContext.getText("msg.transfer.request.errorOutNumber", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int direction = ConvertUtil.toInt(request.getParameter("direction"), 0);
		if (direction != 1 && direction != 2) {
			checkVO.setMsg("转账方向错误");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		int currencyId = ConvertUtil.toInt(request.getParameter("currencyId"), 0);
		BQCurrencyType currency = bqCurrencyTypeService.readByTypeId(currencyId);
		if (currency == null) {
			checkVO.setMsg("币种id错误");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (currency.getType_name().toUpperCase().equals("BFC")) {
			checkVO.setMsg("币种id错误");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		if (direction == 1) {
			BQAccount motherAccount = bqAccountService.readAccount(motherUser.getF_user_id(), currencyId);
			if (motherAccount == null) {
				checkVO.setMsg("币种id错误");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			if (motherAccount.getBtc().compareTo(amount) < 0) {
				checkVO.setMsg(SpringContext.getText("msg.withdraw.request.insufficientBalance", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} else {
			BQAccount subAccount = bqAccountService.readAccount(subUser.getF_user_id(), currencyId);
			if (subAccount == null) {
				checkVO.setMsg("币种id错误");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			if (subAccount.getBtc().compareTo(amount) < 0) {
				checkVO.setMsg(SpringContext.getText("msg.withdraw.request.insufficientBalance", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}

		BQSubMemberTransferLog log = new BQSubMemberTransferLog();
		log.setAmount(amount);
		log.setCurrency_type_id(currency.getType_id());
		log.setCurrency_name(currency.getType_name());
		log.setDateline(System.currentTimeMillis() / 1000);
		log.setMother_user_id(motherUser.getF_user_id());
		log.setMother_user_email(motherUser.getF_user_email());
		log.setSub_user_id(subUser.getF_user_id());
		log.setSub_user_email(subUser.getF_user_email());
		log.setOperation(direction);
		String remark = "转入子账号";
		if (direction == Contents.SUB_TRANSFER_OUT) {
			remark = "子账号转出";
		}
		log.setRemark(remark);
		try {
			boolean success = bqTransferLogService.updateAccountForTransfer(log);
			if (!success) {
				checkVO.setMsg("fail,失败");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.pg.trade.optionStatusSuc", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}
	
	// 划转时获取当前母账户余额
	@RequestMapping("/getAmount")
	public void getAmount(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");

		BQMember motherUser = super.findCurrUser(request, false);
		if (motherUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String subUidStr = request.getParameter("subUserId"); // 子账号id
		
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			checkVO.setMsg("请输入正确的子账号id");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			checkVO.setMsg("子账号无效");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int currencyId =ConvertUtil.toInt(request.getParameter("currencyId"), 0);
		BQCurrencyType currency = bqCurrencyTypeService.readByTypeId(currencyId);
		if (currency == null) {
			checkVO.setMsg("币种id错误");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (currency.getType_name().toUpperCase().equals("BFC")) {
			checkVO.setMsg("币种id错误");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		int direction = ConvertUtil.toInt(request.getParameter("direction"), 0);
		if (direction != 1 && direction != 2) {
			checkVO.setMsg("转账方向错误");
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BigDecimal banlance = BigDecimal.ZERO;
		if (direction == 1) {
			BQAccount motherAccount = bqAccountService.readAccount(motherUser.getF_user_id(), currencyId);
			if (motherAccount == null) {
				checkVO.setMsg("币种id错误");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			banlance = motherAccount.getBtc();
		} else {
			BQAccount subAccount = bqAccountService.readAccount(subUser.getF_user_id(), currencyId);
			if (subAccount == null) {
				checkVO.setMsg("币种id错误");
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			banlance = subAccount.getBtc();
		}
		
		checkVO.setCode("success");
		checkVO.setFrozen(banlance.doubleValue());
		toWeb(response, gson.toJson(checkVO));
		return;
	}
	
	// 转账记录分页
	@RequestMapping("/transferLogs")
	public ModelAndView transferLogs(HttpServletRequest request, Model model, String email, String note) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		PagerModel logs = bqTransferLogService.readByUserId(user.getF_user_id());
		model.addAttribute("pm", logs);

		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_subTransferLogs");
		return mv;
	}
	
	// 子账号api申请页面
	@RequestMapping("/bindSubAppKey")
	public ModelAndView bindAppKey(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember motherUser = super.findCurrUser(request, false);
		if (motherUser == null) {
			return new ModelAndView(super.toLogin());
		}
		if (motherUser.getIs_sub_user() == 1) {
			model.addAttribute("isSubUser", true);
		}
		
		String subUidStr = request.getParameter("subUserId"); // 子账号id
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			return new ModelAndView("redirect:/userSubAccount/subUser.do");
		}
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			return new ModelAndView("redirect:/userSubAccount/subUser.do");
		}
		model.addAttribute("subUser", subUser);
		Boolean bindSuccess = (Boolean)request.getSession().getAttribute("bindSubSuccess");
		if (bindSuccess != null && bindSuccess) {
			model.addAttribute("success", "success");
			request.getSession().removeAttribute("bindSubSuccess");
		}

		BQMemberSafeSettings bqss = bqMemberSafeSettingsService.readById(motherUser.getF_user_id());
		if (bqss.getF_user_safetype() == 1) {
			model.addAttribute("tel", bqss.getF_user_tel());
			model.addAttribute("showtel", 1);
			model.addAttribute("showgoogle", 0);
		} else if (bqss.getF_user_safetype() == 2) {
			model.addAttribute("showgoogle", 1);
			model.addAttribute("showtel", 0);
		} else if (bqss.getF_user_safetype() == 3) {
			model.addAttribute("tel", bqss.getF_user_tel());
			model.addAttribute("showtel", 1);
			model.addAttribute("showgoogle", 1);
		} else {
			model.addAttribute("showgoogle", 0);
			model.addAttribute("showtel", 0);
			model.addAttribute("applayerror", true);
		}
		
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(subUser.getF_user_id());
		if (bqUserAppKey != null) {
			model.addAttribute("appkey", bqUserAppKey);
			ModelAndView mv = new ModelAndView();
			mv.setViewName("jsp/user_subBindAppKey");
			return mv;
		}

		String num = request.getParameter("error");
		if (num != null) {
			if (num.equals("error1")) {
				model.addAttribute("error", SpringContext.getText("msg.bindApi.request.appKeyNotNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error2")) {
				model.addAttribute("error", SpringContext.getText("msg.request.notBindPhone", Contents.Local.LOCALE.get()));
			} else if (num.equals("error3")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.phoneCodeNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error4")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error5")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.googleCodeNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error6")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.googleCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error7")) {
				model.addAttribute("error", SpringContext.getText("msg.bindApi.request.appKeyNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error8")) {
				model.addAttribute("error", SpringContext.getText("msg.bindApi.request.formatNotMatch", Contents.Local.LOCALE.get()));
			} else if (num.equals("error9")) {
				model.addAttribute("error", SpringContext.getText("msg.bindApi.request.gtTenIp", Contents.Local.LOCALE.get()));
			}
		}
		
		String sus = request.getParameter("success");
		if (sus != null) {
			if (sus.equals("success1")) {
				model.addAttribute("success", SpringContext.getText("msg.request.deleteSuccess", Contents.Local.LOCALE.get()));
			}
		}
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/user_subBindAppKey");
		return mv;
	}
	
	@RequestMapping("/doBindAppKey")
	public ModelAndView doBindAppKey(HttpServletRequest request, ModelMap model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember motherUser = super.findCurrUser(request, true);
		if (motherUser == null) {
			return new ModelAndView(super.toLogin());
		}

		String subUidStr = request.getParameter("subUserId"); // 子账号id
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do");
		}
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do");
		}
		model.addAttribute("subUser", subUser);
		
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(subUser.getF_user_id());
		if (bqUserAppKey != null) {
			model.addAttribute("error", "error1");
			return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do?subUserId=" + subUid, model);
		}
		
		HttpSession session = request.getSession();
		String code = request.getParameter("code");
		String googleCode = request.getParameter("google");
		String tel = "";
		
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(motherUser.getF_user_id());
		if (bqms.getF_user_safetype() == 0) {
			model.addAttribute("error", "error2");// 未绑定手机号，也未绑定谷歌
			return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do?subUserId=" + subUid, model);
		}
		if (bqms.getF_user_safetype() == 1 || bqms.getF_user_safetype() == 3) {
			tel = bqms.getF_user_tel();
			BQTelCode bqTelCode = (BQTelCode) session.getAttribute("telcode" + tel);
			if (StringUtil.isEmpty(code)) {
				model.addAttribute("error", "error3");
				return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do?subUserId=" + subUid, model);
			}
			if (bqTelCode == null) {
				model.addAttribute("error", "error3");
				return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do?subUserId=" + subUid, model);
			}
			if (!bqTelCode.getF_auth_code().equals(code)) {
				bqTelCode.setF_error_times(bqTelCode.getF_error_times() + 1);
				if (bqTelCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					session.removeAttribute("telcode" + tel);
				} else {
					session.setAttribute("telcode" + tel, bqTelCode);
				}
				
				model.addAttribute("error", "error4");
				return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do?subUserId=" + subUid, model);
			}
		}
		
		// 判断谷歌验证码
		if (bqms.getF_user_safetype() == 2 || bqms.getF_user_safetype() == 3) {
			if (googleCode == null || googleCode.isEmpty()) {
				model.addAttribute("error", "error5");
				return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do?subUserId=" + subUid, model);
			}

			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				model.addAttribute("error", "error6");
				return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do?subUserId=" + subUid, model);
			}
			boolean bresult = GoogleAuthenticator.check_code(bqms.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				model.addAttribute("error", "error6");
				return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do?subUserId=" + subUid, model);
			}

		}
		
		String allowIp = request.getParameter("allowIp");
		if (!StringUtil.isEmpty(allowIp)) {
			// 验证IP格式
			String regex = "(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])"
					+ "(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3}"
					+ "(\\s*,\\s*(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3})*";
			if (!allowIp.matches(regex)) {
				model.addAttribute("error", "error8");
				return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do?subUserId=" + subUid, model);
			}
			// API最多只能绑定10个IP
			String[] ipArray = allowIp.split(",");
			if (ipArray.length > 10) {
				model.addAttribute("error", "error9");
				return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do?subUserId=" + subUid, model);
			}
		}
		BQUserAppKey userAppKey = new BQUserAppKey();
		userAppKey.setF_user_id(subUser.getF_user_id());
		userAppKey.setAllow_ip(allowIp);
		userAppKey.setApp_key(UUID.randomUUID().toString().replaceAll("-", ""));
		userAppKey.setApp_secret(UUID.randomUUID().toString().replaceAll("-", ""));
		long overTime = 0; 
		if (StringUtil.isEmpty(allowIp)) overTime = System.currentTimeMillis() / 1000 + 90 * 24 * 3600;
		userAppKey.setOver_time(overTime);
		userAppKey.setState(1);
		userAppKey.setUpdate_time(System.currentTimeMillis() / 1000);
		
		bqUserAppKeyService.save(userAppKey);
		if (!StringUtil.isEmpty(tel)) {
			session.removeAttribute("telcode" + tel);
		}
		
		request.getSession().setAttribute("bindSubSuccess", true);
		return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do?subUserId=" + subUid, model);
	}
	
	// 删除子账号api
	@RequestMapping("/deleteSubAppKey")
	public ModelAndView deleteSubAppKey(HttpServletRequest request, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember motherUser = super.findCurrUser(request, true);
		if(motherUser == null){
			return new ModelAndView(super.toLogin());
		}
		
		String subUidStr = request.getParameter("subUserId"); // 子账号id
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do");
		}
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do");
		}
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(subUid);
		if (bqUserAppKey == null) {
			model.addAttribute("error", "error7");
			return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do", model);
		}
		
		bqUserAppKeyService.delete(subUid);
		model.addAttribute("success", "success1");
		return new ModelAndView("redirect:/userSubAccount/bindSubAppKey.do?subUserId=" + subUid, model);
	}
	
	// 修改子账号api
	@RequestMapping("/updateSubAppKey")
	public void updateSubAppKey(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		Gson gson = new Gson();
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("error");

		BQMember motherUser = super.findCurrUser(request, true);
		if (motherUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.common.request.sessionTimeOut", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (motherUser.getIs_sub_user() == 1) {
			checkVO.setMsg("很抱歉！该功能子账号未开放");
			toWeb(response, gson.toJson(checkVO));
			return;
		}

		String subUidStr = request.getParameter("subUserId"); // 子账号id
		
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subMemberNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			checkVO.setMsg(SpringContext.getText("msg.pg.subUser.subMemberIsDelete", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(subUid);
		if (bqUserAppKey == null) {
			checkVO.setMsg(SpringContext.getText("msg.bindApi.request.appKeyNull", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		HttpSession session = request.getSession();
		String code = request.getParameter("code");
		String googleCode = request.getParameter("google");
		String tel = "";
		
		BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(motherUser.getF_user_id());
		if (bqms.getF_user_safetype() == 0) {
			checkVO.setMsg(SpringContext.getText("msg.request.notBindPhone", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		if (bqms.getF_user_safetype() == 1 || bqms.getF_user_safetype() == 3) {
			tel = bqms.getF_user_tel();
			BQTelCode bqTelCode = (BQTelCode) session.getAttribute("telcode" + tel);
			if (StringUtil.isEmpty(code)) {
				checkVO.setMsg(SpringContext.getText("msg.common.request.phoneCodeNull", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			if (bqTelCode == null) {
				checkVO.setMsg(SpringContext.getText("msg.common.request.phoneCodeNull", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			if (!bqTelCode.getF_auth_code().equals(code)) {
				bqTelCode.setF_error_times(bqTelCode.getF_error_times() + 1);
				if (bqTelCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					session.removeAttribute("telcode" + tel);
				} else {
					session.setAttribute("telcode" + tel, bqTelCode);
				}
				
				checkVO.setMsg(SpringContext.getText("msg.common.request.phoneCodeError", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
		}
		
		// 判断谷歌验证码
		if (bqms.getF_user_safetype() == 2 || bqms.getF_user_safetype() == 3) {
			if (googleCode == null || googleCode.isEmpty()) {
				checkVO.setMsg(SpringContext.getText("msg.common.request.googleCodeNull", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}

			long lgoogleCode = 0;
			try {
				lgoogleCode = Long.parseLong(googleCode);
			} catch (Exception e) {
				checkVO.setMsg(SpringContext.getText("msg.common.request.googleCodeError", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			boolean bresult = GoogleAuthenticator.check_code(bqms.getF_user_safegoogle(), lgoogleCode, System.currentTimeMillis());
			if (!bresult) {
				checkVO.setMsg(SpringContext.getText("msg.common.request.googleCodeError", Contents.Local.LOCALE.get()));
				toWeb(response, gson.toJson(checkVO));
				return;
			}

		}
		
		String allowIp = request.getParameter("allowIp");
		if (StringUtil.isEmpty(allowIp)) {
			checkVO.setMsg(SpringContext.getText("msg.bindApi.request.formatNotMatch", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		// 验证IP格式
		String regex = "(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])"
				+ "(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3}"
				+ "(\\s*,\\s*(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3})*";
		if (!allowIp.matches(regex)) {
			checkVO.setMsg(SpringContext.getText("msg.bindApi.request.formatNotMatch", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		// API最多只能绑定10个IP
		String[] ipArray = allowIp.split(",");
		if (ipArray.length > 10) {
			checkVO.setMsg(SpringContext.getText("msg.bindApi.request.gtTenIp", Contents.Local.LOCALE.get()));
			toWeb(response, gson.toJson(checkVO));
			return;
		}
		
		bqUserAppKey.setAllow_ip(allowIp);
		bqUserAppKeyService.update(bqUserAppKey);
		if (!StringUtil.isEmpty(tel)) {
			session.removeAttribute("telcode" + tel);
		}

		checkVO.setCode("success");
		checkVO.setMsg(SpringContext.getText("msg.pg.trade.optionStatusSuc", Contents.Local.LOCALE.get()));
		toWeb(response, gson.toJson(checkVO));
		return;
	}
	
	// 绑定谷歌
	@RequestMapping("/bindGoogle")
	public ModelAndView bindGoogle(HttpServletRequest request, Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		String num = request.getParameter("error");
		if (num != null) {
			if (num.equals("error1")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.googleCodeNull", Contents.Local.LOCALE.get()));
			} else if (num.equals("error2")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.googleCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error3")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeError", Contents.Local.LOCALE.get()));
			} else if (num.equals("error4")) {
				model.addAttribute("error", SpringContext.getText("msg.common.request.emailCodeTimes", Contents.Local.LOCALE.get()));
			} 
		}
		String sus = request.getParameter("success");
		if (sus != null) {
			if (sus.equals("success1")) {
				model.addAttribute("success", SpringContext.getText("msg.bindGoogle.request.success", Contents.Local.LOCALE.get()));
			}
		}
		ModelAndView mv = new ModelAndView();

		BQMember motherUser = super.findCurrUser(request, false);
		if (motherUser == null) {
			return new ModelAndView(super.toLogin());
		}
		String subUidStr = request.getParameter("subUserId"); // 子账号id
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			return new ModelAndView("redirect:/userSubAccount/subUser.do");
		}
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			return new ModelAndView("redirect:/userSubAccount/subUser.do");
		}
		model.addAttribute("subUser", subUser);
		BQMemberSafeSettings subBqs = bqMemberSafeSettingsService.readById(subUser.getF_user_id());
		if (subBqs.getF_user_safetype() == 2 || subBqs.getF_user_safetype() == 3) {
			if (sus == null) {
				return new ModelAndView("redirect:/userSubAccount/subUser.do");
			}
		}
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(motherUser.getF_user_id());
		String email = motherUser.getF_user_email();
		String mobile = bqs.getF_user_tel();
		model.addAttribute("tyle", bqs.getF_user_safetype());
		model.addAttribute("email", email);
		model.addAttribute("mobile", mobile);
		String secretKey = null;
		secretKey = GoogleAuthenticator.generateSecretKey();
		model.addAttribute("secretKey", secretKey);
		mv.setViewName("jsp/user_subBindGoogle");
		return mv;
	}

	@RequestMapping("/bindGoogledo")
	public ModelAndView bindGoogledo(HttpServletRequest request, ModelMap model) {
		Contents.Local.LOCALE.set(getLocale(request));

		HttpSession session = request.getSession();
		BQMember motherUser = super.findCurrUser(request, true);
		if (motherUser == null) {
			return new ModelAndView(super.toLogin());
		}
		String subUidStr = request.getParameter("subUserId"); // 子账号id
		long subUid = Convert.toLong(subUidStr, 0);
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(motherUser.getF_user_id(), subUid);
		if (bqSubMember == null) {
			return new ModelAndView("redirect:/userSubAccount/subUser.do");
		}
		BQMember subUser = bqMemberService.readUserById(subUid);
		if (subUser == null) {
			return new ModelAndView("redirect:/userSubAccount/subUser.do");
		}
		model.addAttribute("subUser", subUser);
		BQMemberSafeSettings subBqs = bqMemberSafeSettingsService.readById(subUser.getF_user_id());
		if (subBqs.getF_user_safetype() == 2 || subBqs.getF_user_safetype() == 3) {
			return new ModelAndView("redirect:/userSubAccount/subUser.do");
		}
		
		BQMemberSafeSettings bqs = bqMemberSafeSettingsService.readById(motherUser.getF_user_id());
		
		String code = request.getParameter("code");// 手机验证码
		String google = request.getParameter("google");
		String secretKey = request.getParameter("secretKey");
		String tel = request.getParameter("mobile");
		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) {
			if (code == null) {
				model.addAttribute("error", "error1");
				return new ModelAndView("redirect:/userSubAccount/bindGoogle.do?subUserId=" + subUid, model);
			}
			BQTelCode codenum = (BQTelCode) session.getAttribute("telcode" + tel);
			if (codenum == null) {
				model.addAttribute("error", "error1");
				return new ModelAndView("redirect:/userSubAccount/bindGoogle.do?subUserId=" + subUid, model);
			}
			if (!codenum.getF_user_tel().equals(tel)) {
				model.addAttribute("error", "error2");
				return new ModelAndView("redirect:/userSubAccount/bindGoogle.do?subUserId=" + subUid, model);
			}
			if (!codenum.getF_auth_code().equals(code)) {
				
				codenum.setF_error_times(codenum.getF_error_times() + 1);
				if (codenum.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					session.removeAttribute("telcode" + tel);
				} else {
					session.setAttribute("telcode" + tel, codenum);
				}
				
				model.addAttribute("error", "error2");
				return new ModelAndView("redirect:/userSubAccount/bindGoogle.do?subUserId=" + subUid, model);
			}
		} else {
			String emailCode = request.getParameter("emailCode");// 邮箱验证码
			if (StringUtil.isEmpty(emailCode)) {
				model.addAttribute("error", "error3");
				return new ModelAndView("redirect:/userSubAccount/bindGoogle.do?subUserId=" + subUid, model);
			}
			
			BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + motherUser.getF_user_id());
			if (cacheEmailCode == null || !emailCode.equals(cacheEmailCode.getF_auth_code())) {
				if (cacheEmailCode != null) {
					cacheEmailCode.setF_error_times(cacheEmailCode.getF_error_times() + 1);
					spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + motherUser.getF_user_id(), BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
				}
				
				if (cacheEmailCode != null && cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
					model.addAttribute("error", "error4");
					return new ModelAndView("redirect:/userSubAccount/bindGoogle.do?subUserId=" + subUid, model);
				} 
				
				model.addAttribute("error", "error3");
				return new ModelAndView("redirect:/userSubAccount/bindGoogle.do?subUserId=" + subUid, model);
			}
			
			if (cacheEmailCode.getF_error_times() > Contents.TEL_CODE_ERROR_LIMIT) {
				model.addAttribute("error", "error4");
				return new ModelAndView("redirect:/userSubAccount/bindGoogle.do?subUserId=" + subUid, model);
			}
		}
		
		if (google == null) {
			model.addAttribute("error", "error1");
			return new ModelAndView("redirect:/userSubAccount/bindGoogle.do?subUserId=" + subUid, model);
		}

		if (GoogleAuthenticator.check_code(secretKey, Convert.toLong(google, 0), System.currentTimeMillis())) {
			subBqs.setF_user_safegoogle(secretKey);
			if (subBqs.getF_user_safetype() == 1) {
				subBqs.setF_user_safetype(3);
			} else {
				subBqs.setF_user_safetype(2);
			}
			bqMemberSafeSettingsService.updateSafe(subBqs);
			
			/*
			if (subUser.getF_user_oms() == 0) {
				// 增加积分记录
				BQCommonCreditLog bqcl = new BQCommonCreditLog();
				bqcl.setF_user_id(subUser.getF_user_id());
				bqcl.setCredit_type_id(Contents.CREDIT_TYPE_BINDGOOGLE);
				bqcl.setCredits(DATA_CreditNumber.CREDIT_ONE_BINDGOOGLE);
				bqcl.setTitle("绑定谷歌");
				bqcl.setDateline(DateUtil.DateToUnix(new Date()));
				bqCommonCreditLogService.saveCreditsAndUpdateLevel(bqcl, false, 1);
			}
			*/
		} else {
			model.addAttribute("error", "error2");
			return new ModelAndView("redirect:/userSubAccount/bindGoogle.do?subUserId=" + subUid, model);
		}
		
		if (bqs.getF_user_safetype() == 1 || bqs.getF_user_safetype() == 3) {
			session.removeAttribute("telcode" + tel);
		} else {
			spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + motherUser.getF_user_id());
		}
		
		return new ModelAndView("redirect:/userSubAccount/subUser.do");
	}
}
