package com.btc100.btc_q.action.api;

import java.math.BigDecimal;
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

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.geoip.model.v20200101.DescribeIpv4LocationRequest;
import com.aliyuncs.geoip.model.v20200101.DescribeIpv4LocationResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.DATA_CreditNumber;
import com.btc100.btc_q.common.MessageInterface;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAreaCity;
import com.btc100.btc_q.entity.BQAreaProvince;
import com.btc100.btc_q.entity.BQBftRewardRecord;
import com.btc100.btc_q.entity.BQBftRewardSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberModifyInformationRecord;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQTelCode;
import com.btc100.btc_q.service.BQAreaCityService;
import com.btc100.btc_q.service.BQAreaProvinceService;
import com.btc100.btc_q.service.BQBftRewardRecordService;
import com.btc100.btc_q.service.BQBftRewardSettingService;
import com.btc100.btc_q.service.BQMemberModifyInformationRecordService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.RegisterSerivce;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.Security;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;

/**
 * 用户注册API
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/registerApi")
public class RegisterApiAction extends BaseApiAction {

	Logger logger = Logger.getLogger(RegisterApiAction.class);
	
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private RegisterSerivce registerService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQBftRewardSettingService bqBftRewardSettingService;
	@Autowired
	private BQBftRewardRecordService bqBftRewardRecordService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private BQAreaProvinceService bqAreaProvinceService;
	@Autowired
	private BQAreaCityService bqAreaCityService;
	@Autowired
	private BQMemberModifyInformationRecordService bqMemberModifyInformationRecordService;
	
	private final static String AccessKeyId = "";
	private final static String AccessKeySecret = "";
	
	@RequestMapping("/sliderRegister")
	public String sliderRegister(HttpServletRequest request, HttpServletResponse response, Model model) throws JSONException {
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String random = RandomUtil.getTransNo();
		userJson.put("data", random);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}

	/**
	 * 注册用户提交
	 * 
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String email = request.getParameter("email");
		String emailCode = request.getParameter("emailCode");// 邮箱验证码
		String password1 = request.getParameter("password1");
		String password2 = request.getParameter("password2");
		String invid = request.getParameter("invid");
		if (invid == null) invid = "";
		if (!StringUtil.isEmpty(invid)) {
			invid = Security.decryptByHex(invid.toUpperCase(), Security.des_registerkey);
		}
		
		if (StringUtil.isEmpty(email) || StringUtil.isEmpty(password1) || StringUtil.isEmpty(password2)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机或密码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (StringUtil.isEmpty(emailCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		if (!StringUtil.validateString(email) || !StringUtil.validateString(emailCode)
				|| !StringUtil.validateString(password1) || !StringUtil.validateString(password2)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "内容包含非法字符");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!password1.equals(password2)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "两次输入密码不一致");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + email);
		if (StringUtil.isEmpty(cacheEmailCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机验证码已过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (cacheEmailCode.getF_error_times() >= 5) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!emailCode.equals(cacheEmailCode.getF_auth_code())) {
			cacheEmailCode.setF_error_times(cacheEmailCode.getF_error_times() + 1);
			spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + email, BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
			
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMember bqm = bqMemberService.readCheckUserEmail(email);
		if (bqm != null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "该邮箱/手机已注册");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		boolean isEmail = true;
		if (email.matches("^1[0-9]{10}$")) { 
			isEmail = false;
			
			BQMemberSafeSettings setting = bqMemberSafeSettingsService.readByTel(email);
			if (setting != null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "该邮箱/手机已注册");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} else {
			bqm = bqMemberService.readByRealEmail(email);
			if (bqm != null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "该邮箱/手机已注册");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		String random6 = RandomUtil.getRandomString(6);
		try {
			password1 = MD5Service.encryptString(password1).toLowerCase();
			password1 = MD5Service.encryptString(password1 + random6).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}

		BQMember bqMem = new BQMember();
		bqMem.setF_user_email(email);
		if (isEmail) {
			bqMem.setF_real_email(email);
			bqMem.setF_user_email_status(1);
			bqMem.setF_user_credits(DATA_CreditNumber.CREDIT_ONE_REGISTER + DATA_CreditNumber.CREDIT_ONE_SUREEMAIL);
		} else {
			bqMem.setF_real_email("");
			bqMem.setF_user_email_status(0);
			bqMem.setF_user_credits(DATA_CreditNumber.CREDIT_ONE_REGISTER + DATA_CreditNumber.CREDIT_ONE_BINDPHONE);
		}
		bqMem.setF_user_pwd(password1);
		bqMem.setF_user_level_id(1);// 用户VIP级别(bq_member_level:level_id)',
		bqMem.setF_user_state(1);// '用户状态（0:无效,1:有效,-1:冻结）'
		bqMem.setF_user_identity(0);// 普通用户
		bqMem.setF_user_salt(random6);
		bqMem.setF_user_origin("");
		bqMem.setF_first_login(1);
		// java获取时间戳
		bqMem.setF_user_time(getTime());// 生成用户自己的邀请码
		
		String country = request.getParameter("country");
		String countryName = request.getParameter("countryName");
		String provice = "";
		String city = "";
		
		String sip = super.getIpAddr(request);
		if (StringUtil.isEmpty(country)) {
			country = "CN";
			countryName = "中国";
			try {
				DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AccessKeyId, AccessKeySecret);
		        IAcsClient client = new DefaultAcsClient(profile);
		        DescribeIpv4LocationRequest ipLocation = new DescribeIpv4LocationRequest();
		        ipLocation.setIp(sip);
		        
	            DescribeIpv4LocationResponse ipLocalResponse = client.getAcsResponse(ipLocation);
	            country = ipLocalResponse.getCountryCode();
				countryName = ipLocalResponse.getCountry();
				if (country.equals("IANA") && "未分配或者内网IP".equals(country)) {
					country = "CN";
					provice = "";
					city = "";
				} else {
					String proName = ipLocalResponse.getProvince();
					String cityName = ipLocalResponse.getCity();
					
					BQAreaProvince province = bqAreaProvinceService.readProviceByName(proName);
					if (province != null) {
						provice = province.getCode();
					}
					
					BQAreaCity ct = bqAreaCityService.readCityByName(cityName);
					if (ct != null) {
						city = ct.getCode();
					}
				}
					
			} catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
		
		if (isEmail) {
			registerService.RegisterSuccess(bqMem, invid, sip, country, provice, city, countryName);

			BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
			informationRecord.setF_user_id(bqMem.getF_user_id());
			informationRecord.setF_old_information("");
			informationRecord.setF_new_information(email);
			informationRecord.setF_information_type(2);
			informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
			informationRecord.setF_modify_ip(StringUtil.isEmpty(sip) ? "" : sip);
			informationRecord.setF_modify_server(2);
			informationRecord.setF_modify_user("");
			informationRecord.setF_operation_type(1);
			bqMemberModifyInformationRecordService.save(informationRecord);

			// 注册成功发送邮件
			SendMail themail = new SendMail();
			try {
				themail.send(bqMem.getF_real_email(), themail.getRegisterSuccessStr(super.getRequestRoot(request)));
			} catch (Exception e) {
				logger.error("注册成功发送邮件失败", e);
			}
		} else {
			registerService.RegisterSuccess(bqMem, invid, sip, country, provice, city, countryName, true);

			BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
			informationRecord.setF_user_id(bqMem.getF_user_id());
			informationRecord.setF_old_information("");
			informationRecord.setF_new_information(email);
			informationRecord.setF_information_type(1);
			informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
			informationRecord.setF_modify_ip(StringUtil.isEmpty(sip) ? "" : sip);
			informationRecord.setF_modify_server(2);
			informationRecord.setF_modify_user("");
			informationRecord.setF_operation_type(1);
			bqMemberModifyInformationRecordService.save(informationRecord);
		}
		
		BQBftRewardSetting setting = bqBftRewardSettingService.readOne();
		if (setting != null && setting.getRegister_reward().compareTo(BigDecimal.ZERO) > 0 
				&& setting.getOld_register_rewarded()) {
			BQBftRewardRecord record = new BQBftRewardRecord();
			record.setF_user_id(bqMem.getF_user_id());
			record.setCurrency_type_id(setting.getCurrency_type_id());
			record.setReason(Contents.REWARD_REASON_REGISTER);
			record.setAmount(setting.getRegister_reward());
			record.setNote("用户注册赠送");
			
			bqBftRewardRecordService.bonus(record);
		}
		
		spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + email);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 注册用户提交
	 * 
	 */
	@RequestMapping(value = "/phoneRegister", method = RequestMethod.POST)
	public String phoneRegister(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");
		
		String tel = request.getParameter("tel");
		String zone = request.getParameter("zone");
		if (StringUtil.isEmpty(zone)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "未选择区号");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		String emailCode = request.getParameter("emailCode");// 邮箱验证码
		String password1 = request.getParameter("password1");
		String password2 = request.getParameter("password2");
		String invid = request.getParameter("invid");
		if (invid == null) invid = "";
		if (!StringUtil.isEmpty(invid)) {
			invid = Security.decryptByHex(invid.toUpperCase(), Security.des_registerkey);
		}
		
		if (StringUtil.isEmpty(tel) || StringUtil.isEmpty(password1) || StringUtil.isEmpty(password2)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机或密码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		if (StringUtil.isEmpty(emailCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		if (!StringUtil.validateString(tel) || !StringUtil.validateString(emailCode)
				|| !StringUtil.validateString(password1) || !StringUtil.validateString(password2)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "内容包含非法字符");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!password1.equals(password2)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "两次输入密码不一致");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String key = (StringUtil.isEmpty(zone) ? "" : zone) + tel;
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + key);
		if (StringUtil.isEmpty(cacheEmailCode)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机验证码已过期");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (cacheEmailCode.getF_error_times() >= 5) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		
		if (!emailCode.equals(cacheEmailCode.getF_auth_code())) {
			cacheEmailCode.setF_error_times(cacheEmailCode.getF_error_times() + 1);
			spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + key, BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
			
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机验证码错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMemberSafeSettings setting = bqMemberSafeSettingsService.readByTel(tel);
		if (setting != null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "该邮箱/手机已注册");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String random6 = RandomUtil.getRandomString(6);
		try {
			password1 = MD5Service.encryptString(password1).toLowerCase();
			password1 = MD5Service.encryptString(password1 + random6).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}

		BQMember bqMem = new BQMember();
		bqMem.setF_user_email(tel);
		bqMem.setF_real_email("");
		bqMem.setF_user_email_status(0);
		bqMem.setF_user_credits(DATA_CreditNumber.CREDIT_ONE_REGISTER + DATA_CreditNumber.CREDIT_ONE_BINDPHONE);
		
		bqMem.setF_user_pwd(password1);
		bqMem.setF_user_level_id(1);// 用户VIP级别(bq_member_level:level_id)',
		bqMem.setF_user_state(1);// '用户状态（0:无效,1:有效,-1:冻结）'
		bqMem.setF_user_identity(0);// 普通用户
		bqMem.setF_user_salt(random6);
		bqMem.setF_user_origin("");
		bqMem.setF_first_login(1);
		// java获取时间戳
		bqMem.setF_user_time(getTime());// 生成用户自己的邀请码
		
		String country = request.getParameter("country");
		String countryName = request.getParameter("countryName");
		String provice = "";
		String city = "";
		
		String sip = super.getIpAddr(request);
		if (StringUtil.isEmpty(country)) {
			country = "CN";
			countryName = "中国";
			
			try {
				DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AccessKeyId, AccessKeySecret);
		        IAcsClient client = new DefaultAcsClient(profile);
		        DescribeIpv4LocationRequest ipLocation = new DescribeIpv4LocationRequest();
		        ipLocation.setIp(sip);
		        
	            DescribeIpv4LocationResponse ipLocalResponse = client.getAcsResponse(ipLocation);
	            country = ipLocalResponse.getCountryCode();
				countryName = ipLocalResponse.getCountry();
				if (country.equals("IANA") && "未分配或者内网IP".equals(country)) {
					country = "CN";
					provice = "";
					city = "";
				} else {
					String proName = ipLocalResponse.getProvince();
					String cityName = ipLocalResponse.getCity();
					
					BQAreaProvince province = bqAreaProvinceService.readProviceByName(proName);
					if (province != null) {
						provice = province.getCode();
					}
					
					BQAreaCity ct = bqAreaCityService.readCityByName(cityName);
					if (ct != null) {
						city = ct.getCode();
					}
				}
					
			} catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
		
		registerService.RegisterSuccess(bqMem, invid, sip, country, provice, city, countryName, zone, true);

		BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
		informationRecord.setF_user_id(bqMem.getF_user_id());
		informationRecord.setF_old_information("");
		informationRecord.setF_new_information(tel);
		informationRecord.setF_information_type(1);
		informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
		informationRecord.setF_modify_ip(StringUtil.isEmpty(sip) ? "" : sip);
		informationRecord.setF_modify_server(2);
		informationRecord.setF_modify_user("");
		informationRecord.setF_operation_type(1);
		bqMemberModifyInformationRecordService.save(informationRecord);

		BQBftRewardSetting set = bqBftRewardSettingService.readOne();
		if (set != null && set.getRegister_reward().compareTo(BigDecimal.ZERO) > 0 
				&& set.getOld_register_rewarded()) {
			BQBftRewardRecord record = new BQBftRewardRecord();
			record.setF_user_id(bqMem.getF_user_id());
			record.setCurrency_type_id(set.getCurrency_type_id());
			record.setReason(Contents.REWARD_REASON_REGISTER);
			record.setAmount(set.getRegister_reward());
			record.setNote("用户注册赠送");
			
			bqBftRewardRecordService.bonus(record);
		}
		
		spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + key);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	/**
	 * 用户注册，发送邮箱验证码，成功后存入缓存
	 * 
	 */
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
		
		boolean isEmail = true;
		if (email.matches("^1[0-9]{10}$")) { 
			isEmail = false;
		}
		
		BQMember user = bqMemberService.readByRealEmail(email);
		if (user != null) {
			model.addAttribute("ticker", userJson);
			return apiData(response);
		}
		
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + email);
		if (!StringUtil.isEmpty(cacheEmailCode)) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + email);
		}
		
		int random6 = MessageInterface.random6();
		String code = String.valueOf(random6);
		
		if (isEmail) {
			try {
				SendMail themail = new SendMail();
				themail.send(email, themail.getApiStr(Utils.getWebRoot(request), code));
			} catch (Exception e) {
				logger.error("发送邮箱验证码失败", e);
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "发送邮箱验证码失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		} else {
			String sendRet = "error";
			try {
				sendRet = sendTelCode(Convert.toLong(email, 0), "", email, random6);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (!StringUtil.isEmpty(sendRet)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "发送手机验证码失败");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		cacheEmailCode = new BQTelCode();
		cacheEmailCode.setF_auth_code(code);
		cacheEmailCode.setF_error_times(0);
		cacheEmailCode.setF_user_tel(email);
		
		spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + email, BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	@RequestMapping(value = "/sendTelCode", method = RequestMethod.POST)
	public String sendTelCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject userJson = new JSONObject();
		userJson.put("code", 0);
		userJson.put("msg", "");

		String tel = request.getParameter("tel");
		if (StringUtil.isEmpty(tel)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱/手机号错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQMemberSafeSettings setting =bqMemberSafeSettingsService.readByTel(tel);
		if (setting != null) {
			model.addAttribute("ticker", userJson);
			return apiData(response);
		}
		
		String zone = request.getParameter("zone");
		String key = (StringUtil.isEmpty(zone) ? "" : zone) + tel;
		BQTelCode cacheEmailCode = spyMemcachedClient.get(BTCQMemecacheKeys.EMAIL_CODE + key);
		if (!StringUtil.isEmpty(cacheEmailCode)) {
			spyMemcachedClient.delete(BTCQMemecacheKeys.EMAIL_CODE + key);
		}
		
		int random6 = MessageInterface.random6();
		String code = String.valueOf(random6);
		
		String sendRet = "error";
		try {
			sendRet = sendTelCode(Convert.toLong(tel, 0), zone, tel, random6);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (!StringUtil.isEmpty(sendRet)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "发送手机验证码失败");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		cacheEmailCode = new BQTelCode();
		cacheEmailCode.setF_auth_code(code);
		cacheEmailCode.setF_error_times(0);
		cacheEmailCode.setF_user_tel(tel);
		
		spyMemcachedClient.set(BTCQMemecacheKeys.EMAIL_CODE + key, BTCQMemecacheKeys.TIMES.BPI, cacheEmailCode);
		model.addAttribute("ticker", userJson);
		return apiData(response);
	}
	
	// 检查邮箱唯一
	@RequestMapping("/checkUserEmail")
	public String checkUserEmail(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		String email = request.getParameter("email");
		if (StringUtil.isEmpty(email)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "邮箱不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		try {
//			if (null == bqMemberService.readCheckUserEmail(email)) {
				tickerJson.put("data", "false");
//			} else {
//				tickerJson.put("data", "true");
//			}
		} catch (Exception e) {
		}
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	public int getTime() {// 得到时间戳
		return (int) (System.currentTimeMillis() / 1000);
	}
	
}
