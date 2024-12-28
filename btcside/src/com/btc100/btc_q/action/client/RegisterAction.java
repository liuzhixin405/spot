package com.btc100.btc_q.action.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.DATA_CreditNumber;
import com.btc100.btc_q.common.DATA_SessionKeys;
import com.btc100.btc_q.common.MessageInterface;
import com.btc100.btc_q.entity.BQAreaCountry;
import com.btc100.btc_q.entity.BQBftRewardRecord;
import com.btc100.btc_q.entity.BQBftRewardSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberModifyInformationRecord;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.init.SpringContext;
import com.btc100.btc_q.service.BQAreaCountryService;
import com.btc100.btc_q.service.BQBftRewardRecordService;
import com.btc100.btc_q.service.BQBftRewardSettingService;
import com.btc100.btc_q.service.BQMemberInviteAmountService;
import com.btc100.btc_q.service.BQMemberModifyInformationRecordService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.RegisterSerivce;
import com.btc100.btc_q.util.*;


@Controller
@RequestMapping("/register")
public class RegisterAction extends BaseAction {

	private static final Logger logger = Logger.getLogger(RegisterAction.class);
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQAreaCountryService bqAreaCountryService;
	@Autowired
	RegisterSerivce registerService;
	@Autowired
	private BQMemberInviteAmountService bqMemberInviteAmountService;
	@Autowired
	private BQBftRewardSettingService bqBftRewardSettingService;
	@Autowired
	private BQBftRewardRecordService bqBftRewardRecordService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private BQMemberModifyInformationRecordService bqMemberModifyInformationRecordService;
	
	public String s_count = "";
	public boolean b_result = false;
	
	@RequestMapping("/sliderRegister")
	public void sliderRegister(HttpServletRequest request, HttpServletResponse response) {
			toWeb(response, "true");
	}

	@RequestMapping("/register")
	public ModelAndView register(HttpServletRequest request, HttpServletResponse response, Model model) throws UnsupportedEncodingException {
		Locale locale = Utils.getLocale(request);
		
		request.setAttribute("locale", locale);
		request.setAttribute("invid", request.getParameter("invid")); 
		
		String email = request.getParameter("type");
		if (!StringUtil.isEmpty(email) && "email".equals(email)) {
			model.addAttribute("isEmail", true);
		}
		
		String error = request.getParameter("error");
		if (!StringUtil.isEmpty(error)) {
			String err = URLDecoder.decode(error, "UTF-8");
			model.addAttribute("error", err);
		}

		String action = request.getParameter("action");
		if (!StringUtil.isEmpty(action)) {
			String id = request.getParameter("invite");
			if (!StringUtil.isEmpty(id)) {
				// 当点击邀请链接时，要增加该邀请者的点击次数（如果有邀请者的话）
				if (action.equals("invite")) {
					try {
						CookieHelper.setCookie(response, "inviter", id);
						id = Security.decryptByHex(id, Security.des_registerkey);
						long userId = Long.parseLong(id);
						bqMemberInviteAmountService.updateClickByUserId(userId);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		String countryID = "";
		String country = "";
		int proID = 0;
		String pro = "";
		int cityID = 0;
		String city = "";
		super.getIpAddr(request);
		
//		try {
//			DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AccessKeyId, AccessKeySecret);
//	        IAcsClient client = new DefaultAcsClient(profile);
//	        DescribeIpv4LocationRequest ipLocation = new DescribeIpv4LocationRequest();
//	        ipLocation.setIp(sip);
//	        try {
//	            DescribeIpv4LocationResponse ipLocalResponse = client.getAcsResponse(ipLocation);
//				countryID = ipLocalResponse.getCountryCode();
//				country = ipLocalResponse.getCountry();
//				if (countryID.equals("IANA") || "未分配或者内网IP".equals(country) || country.contains("本机")) {
//					countryID = "CN";
//					// country = "中国";
//					proID = 0;
//					pro = "";
//					cityID = 0;
//					city = "";
//				} else {
//					pro = ipLocalResponse.getProvince();
//					city = ipLocalResponse.getCity();
//
//					BQAreaProvince province = bqAreaProvinceService.readProviceByName(pro);
//					if (province != null) {
//						proID = ConvertUtil.toInt(province.getCode(), 0);
//					}
//
//					BQAreaCity ct = bqAreaCityService.readCityByName(city);
//					if (ct != null) {
//						cityID = ConvertUtil.toInt(ct.getCode(), 0);
//					}
//				}
//
//	        } catch (ServerException e) {
//	            e.printStackTrace();
//	        } catch (ClientException e) {
//	            System.out.println("ErrCode:" + e.getErrCode());
//	            System.out.println("ErrMsg:" + e.getErrMsg());
//	            System.out.println("RequestId:" + e.getRequestId());
//	        }
//
//			String sresult = HttpToServer.sendGetForHttp("http://ip.taobao.com/service/getIpInfo.php", "ip=" + sip, 500, 2000);
//			if (!StringUtil.isEmpty(sresult)) {
//				JSONObject jo = new JSONObject(sresult);
//				int icode = ConvertUtil.toInt(jo.get("code"), 1);
//				if (icode == 0) {
//					JSONObject jodata = (JSONObject) jo.get("data");
//					countryID = jodata.get("country_id").toString();
//					country = jodata.getString("country").toString();
//					if (countryID.equals("IANA") && "未分配或者内网IP".equals(country)) {
//						countryID = "CN";
//						// country = "中国";
//						proID = 410000;
//						pro = "河南省";
//						cityID = 410100;
//						city = "郑州市";
//					} else {
//						proID = ConvertUtil.toInt(jodata.get("region_id"), 0);
//						pro = jodata.get("region").toString();
//						cityID = ConvertUtil.toInt(jodata.get("city_id"), 0);
//						city = jodata.get("city").toString();
//						city = "内网IP".equals(city) ? "XX" : city;
//					}
//				}
//			}
//		} catch (Exception e) {
//			logger.error("通过IP判断所在地出错" + e.toString(), e);
//		}
		// 等到省

		List<BQAreaCountry> lis = bqAreaCountryService.readCountry();
		if ("en_US".equals(locale.toString())) {
			if ("CN".equals(countryID)) {
				country = "China";
				pro = StringUtil.getPinyinString(pro);
				city = StringUtil.getPinyinString(city);
			} else if (!"xx".equals(countryID)) {
				for (BQAreaCountry bqAreaCountry : lis) {
					if (bqAreaCountry.getCode().equals(countryID)) {
						country = bqAreaCountry.getEn_name();
						pro = "";
						city = "";
					}
				}
			}
		}
		
		model.addAttribute("listCountry", lis);
		model.addAttribute("countryCode", countryID);
		model.addAttribute("currCountryName", country);
		model.addAttribute("proviceCode", proID);
		model.addAttribute("currProvinceName", pro);
		model.addAttribute("cityCode", cityID);
		model.addAttribute("currCityName", city);
//		model.addAttribute("countryCode", countryID != "" ? lis.get(0).getCode() : "CN");

		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/register");
		return mv;
	}

	@RequestMapping("/registerdo")
	public ModelAndView registerDo(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes attr) throws UnsupportedEncodingException {
		Locale locale = Utils.getLocale(request);
		request.setAttribute("locale", locale);

		String ip = IpMacUtil.getIpAddr(request);
		BQMember bqMem = new BQMember();
		ModelAndView mav = new ModelAndView();
		String action = request.getParameter("action");

		if ("register".equals(action)) {// 普通注册

			String id = request.getParameter("id");// 邀请注册人的邀请者的ID
			if (StringUtil.isEmpty(id)) {
				Cookie oid = CookieHelper.getCookie(request, "inviter");
				if (oid != null) {
					id = oid.getValue();
				}
			}

			if (!StringUtil.isEmpty(id)) {
				id = id.toUpperCase();
			}

			String zone = request.getParameter("zone");
			String email = request.getParameter("email");
			if (StringUtil.isEmpty(email)) {
				return new ModelAndView("redirect:/register/register.do"); 
			}
			boolean isEmail = true;
			if (!email.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")) {
				isEmail = false;
			} else {
				attr.addAttribute("type", "email");
				attr.addFlashAttribute("type", "email");
			}
			
			String password = request.getParameter("password");
			String validcode = request.getParameter("validcode");
			String provice1 = request.getParameter("proviceCode");
			String city1 = request.getParameter("cityCode");
			String country = request.getParameter("countryCode");

			if (StringUtil.isEmpty(country) || "xx".equalsIgnoreCase(country)) {
				country = "CN";
			}
			country = country.toUpperCase();
			String countryName = request.getParameter("currCountryName");
			
			if (validcode == null || validcode.equals("")) {
				attr.addFlashAttribute("error", SpringContext.getText("msg.common.request.codeNull", locale));
				attr.addFlashAttribute("email", email);
				attr.addFlashAttribute("zone", zone);
				return new ModelAndView("redirect:/register/register.do");
			}
			
			try {
				validcode = MD5Service.encryptString(validcode.toLowerCase());
			} catch (Exception e) {
				e.printStackTrace();
			}
			String validCode2 = (String) request.getSession().getAttribute(DATA_SessionKeys.REGISTER_VALIDCODE);// 获取session中验证码
			if (!validcode.equals(validCode2)) {
				attr.addFlashAttribute("error", SpringContext.getText("msg.common.request.codeError", locale));
				attr.addFlashAttribute("email", email);
				attr.addFlashAttribute("zone", zone);
				return new ModelAndView("redirect:/register/register.do"); 
			}
			
			String random6 = RandomUtil.getRandomString(6);
			try {
				password = MD5Service.encryptString(password).toLowerCase();
				password = MD5Service.encryptString(password + random6).toLowerCase();
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("email", email);
				model.addAttribute("zone", zone);
				model.addAttribute("error", SpringContext.getText("msg.register.request.tryAgain", locale));
				mav.setViewName("jsp/register");
				return mav;
			}

			bqMem.setF_user_email(email);
			bqMem.setF_real_email(email);
			bqMem.setF_user_pwd(password);
			bqMem.setF_user_salt(random6);
			
			if (!isEmail) {
				if (StringUtil.isEmpty(zone)) {
					model.addAttribute("email", email);
					model.addAttribute("zone", zone);
					model.addAttribute("error", SpringContext.getText("msg.pg.register.bindZone", locale));
					mav.setViewName("jsp/register");
					return mav;
				}

				BQMemberSafeSettings setting = bqMemberSafeSettingsService.readByTel(email);
				if (setting != null) {
					model.addAttribute("email", email);
					model.addAttribute("zone", zone);
					model.addAttribute("error", SpringContext.getText("msg.register.request.hasRegister", locale));
					mav.setViewName("jsp/register");
					return mav;
				}
				
				int random = MessageInterface.random6();
				String sendRet = "error";
				try {
					sendRet = sendTelCode(Convert.toLong(email, 0), email, random, zone);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (StringUtil.isEmpty(sendRet)) {
					model.addAttribute("alreadySend", true);
				} else {
					model.addAttribute("alreadySend", false);
				}
				
				bqMem.setF_user_credits(random); // code
				bqMem.setF_user_time(0); // error times
				bqMem.setF_real_email(id);
				bqMem.setF_user_nick_name(zone);
				request.getSession().setAttribute("phone_register", bqMem);
				request.getSession().setAttribute(email + "_phone_register_contry", country);
				request.getSession().setAttribute(email + "_phone_register_countryName", countryName);
				
				model.addAttribute("email", email);
				mav.setViewName("jsp/register_phone");
				return mav;
			} else {
				BQMember bqm = bqMemberService.readByRealEmail(email);
				if (bqm != null) {
					model.addAttribute("error", SpringContext.getText("msg.register.request.hasRegister", locale));
					mav.setViewName("jsp/register");
					return mav;
				}
				
				sendEmailCode(request, response, bqMem, model, provice1, city1, country, id, countryName);
				// mav.addObject("text", "注册成功");// 也可以通过setViewName()方法跳转到指定的页面
				model.addAttribute("email", email);
				String slast = "http://mail." + email.substring(email.indexOf("@") + 1);
				model.addAttribute("inemail", slast);

				// ,使用addObject()设置需要返回的值
				mav.setViewName("jsp/register_success");
				
			}
		} else if ("activate".equals(action)) {
			try {
				String parasalt = request.getParameter("salt");

				if (parasalt == null || parasalt.isEmpty()) {
					model.addAttribute("error", SpringContext.getText("msg.common.request.illegalParameter", locale));
					mav.setViewName("jsp/activate_failure");
					return mav;
				}

				String ssaltString = Security.decryptByHex(parasalt, Security.des_registerkey);
				if (ssaltString.isEmpty()) {
					model.addAttribute("error", SpringContext.getText("msg.common.request.illegalParameter", locale));
					mav.setViewName("jsp/activate_failure");
					return mav;
				}

				JSONObject jo = new JSONObject(ssaltString);
				String email = jo.get("email").toString();
				String password = jo.get("pwd").toString();
				String country = jo.get("country").toString();
				String countryName = jo.get("countryName").toString();
				String provice = "0";
				String city = "0";
				if (country.equalsIgnoreCase("cn")) {
					if (!jo.isNull("province")) {
						provice = jo.get("province").toString();
						city = jo.get("city").toString();
					}
				}
				String invid = jo.get("invid").toString();
				try {
					invid = Security.decryptByHex(invid, Security.des_registerkey);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				String salt = jo.get("salt").toString();
				long time = ConvertUtil.toLong(jo.get("time"), 0l);
				
				BQMember bqm = bqMemberService.readCheckUserEmail(email);
				if (bqm != null) {
					model.addAttribute("error", SpringContext.getText("msg.register.request.tryAgain", locale));
					mav.setViewName("jsp/activate_failure");
					return mav;
				}

				if (processActivate(email, time)) {
					bqMem.setF_user_email(email);
					bqMem.setF_real_email(email);
					bqMem.setF_user_email_status(1);
					bqMem.setF_user_pwd(password);
					bqMem.setF_user_credits(DATA_CreditNumber.CREDIT_ONE_REGISTER + DATA_CreditNumber.CREDIT_ONE_SUREEMAIL);
					bqMem.setF_user_level_id(1);// 用户VIP级别(bq_member_level:level_id)',
					bqMem.setF_user_state(1);// '用户状态（0:无效,1:有效,-1:冻结）'
					bqMem.setF_user_identity(0);// 普通用户
					bqMem.setF_user_salt(salt);
					bqMem.setF_user_origin("");
					bqMem.setF_first_login(1);
					// java获取时间戳
					bqMem.setF_user_time((int) time);

					registerService.RegisterSuccess(bqMem, invid, ip, country, provice, city, countryName);

					BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
					informationRecord.setF_user_id(bqMem.getF_user_id());
					informationRecord.setF_old_information("");
					informationRecord.setF_new_information(email);
					informationRecord.setF_information_type(2);
					informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
					informationRecord.setF_modify_ip(StringUtil.isEmpty(ip) ? "" : ip);
					informationRecord.setF_modify_server(1);
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
					
					BQBftRewardSetting setting = bqBftRewardSettingService.readOne();
					doRegisterRewardBft(bqMem.getF_user_id(), setting);
					
					mav.setViewName("jsp/activate_success");
				} else {
					model.addAttribute("error", SpringContext.getText("msg.register.request.emailActivated", locale));
					mav.setViewName("jsp/activate_failure");
				}
			} catch (Exception e) {
				model.addAttribute("error", SpringContext.getText("msg.register.request.tryAgain", locale));
				mav.setViewName("jsp/activate_failure");
			}
		}
		return mav;
	}
	
	@RequestMapping(value = "/registerPhonedo", method = RequestMethod.POST)
	public ModelAndView registerPhonedo(HttpServletRequest request, HttpServletResponse response, String code, Model model) {
		Locale locale = Utils.getLocale(request);
		
		ModelAndView mav = new ModelAndView();
		if (StringUtil.isEmpty(code)) {
			model.addAttribute("alreadySend", false);
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeNull", locale));
			mav.setViewName("jsp/register_phone");
			
			return mav;
		}
		
		BQMember bqMem = (BQMember) request.getSession().getAttribute("phone_register");
		if (StringUtil.isEmpty(bqMem) || StringUtil.isEmpty(bqMem.getF_user_email()) 
				|| StringUtil.isEmpty(bqMem.getF_user_pwd()) 
				|| StringUtil.isEmpty(bqMem.getF_user_salt())) {
			model.addAttribute("error", SpringContext.getText("msg.register.request.tryAgain", locale));
			mav.setViewName("jsp/register");
			
			return mav;
		}
		
		String invid = "";
		if (!StringUtil.isEmpty(bqMem.getF_real_email())) {
			invid = bqMem.getF_real_email();
		}
		
		String oldCode = bqMem.getF_user_credits() + "";
		int times = bqMem.getF_user_time();
		if (times >= 5) {
			model.addAttribute("alreadySend", false);
			model.addAttribute("email", bqMem.getF_user_email());
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeError", locale));
			mav.setViewName("jsp/register_phone");
			
			return mav;
		}
		
		if (!code.equals(oldCode)) {
			bqMem.setF_user_time(bqMem.getF_user_time() + 1);
			
			model.addAttribute("alreadySend", false);
			model.addAttribute("email", bqMem.getF_user_email());
			request.getSession().setAttribute("phone_register", bqMem);
			model.addAttribute("error", SpringContext.getText("msg.common.request.codeError", locale));
			mav.setViewName("jsp/register_phone");
			
			return mav;
		}
		
		BQMemberSafeSettings setting = bqMemberSafeSettingsService.readByTel(bqMem.getF_user_email());
		if (setting != null) {
			model.addAttribute("email", bqMem.getF_user_email());
			model.addAttribute("error", SpringContext.getText("msg.register.request.tryAgain", locale));
			mav.setViewName("jsp/register");
			return mav;
		}
		
		bqMem.setF_real_email("");
		bqMem.setF_user_email_status(0);
		bqMem.setF_user_credits(DATA_CreditNumber.CREDIT_ONE_REGISTER + DATA_CreditNumber.CREDIT_ONE_BINDPHONE);
		bqMem.setF_user_level_id(1);// 用户VIP级别(bq_member_level:level_id)',
		bqMem.setF_user_state(1);// '用户状态（0:无效,1:有效,-1:冻结）'
		bqMem.setF_user_identity(0);// 普通用户
		bqMem.setF_user_origin("");
		bqMem.setF_first_login(1);
		String zone = bqMem.getF_user_nick_name();
		bqMem.setF_user_nick_name(null);
		// java获取时间戳
		bqMem.setF_user_time((int)(System.currentTimeMillis() / 1000));


		String country = "CN";
		String countryName = "中国";
		String provice = "";
		String city = "";

		String memContry = (String) request.getSession().getAttribute(bqMem.getF_user_email() + "_phone_register_contry");
		if (!StringUtil.isEmpty(memContry)) {
			country = memContry;
		}
		String memContryName = (String) request.getSession().getAttribute(bqMem.getF_user_email() + "_phone_register_countryName");
		if (!StringUtil.isEmpty(memContryName)) {
			countryName = memContryName;
		}

		String sip = super.getIpAddr(request);
//		try {
//			DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", AccessKeyId, AccessKeySecret);
//	        IAcsClient client = new DefaultAcsClient(profile);
//	        DescribeIpv4LocationRequest ipLocation = new DescribeIpv4LocationRequest();
//	        ipLocation.setIp(sip);
//
//            DescribeIpv4LocationResponse ipLocalResponse = client.getAcsResponse(ipLocation);
//            country = ipLocalResponse.getCountryCode();
//			countryName = ipLocalResponse.getCountry();
//			if (country.equals("IANA") && "未分配或者内网IP".equals(country)) {
//				country = "CN";
//				provice = "";
//				city = "";
//			} else {
//				String proName = ipLocalResponse.getProvince();
//				String cityName = ipLocalResponse.getCity();
//
//				BQAreaProvince province = bqAreaProvinceService.readProviceByName(proName);
//				if (province != null) {
//					provice = province.getCode();
//				}
//
//				BQAreaCity ct = bqAreaCityService.readCityByName(cityName);
//				if (ct != null) {
//					city = ct.getCode();
//				}
//			}
//
//		} catch (Exception e) {
//        	e.printStackTrace();
//        }
		
		registerService.RegisterSuccess(bqMem, invid, sip, country, provice, city, countryName, zone, true);

		BQMemberModifyInformationRecord informationRecord = new BQMemberModifyInformationRecord();
		informationRecord.setF_user_id(bqMem.getF_user_id());
		informationRecord.setF_old_information("");
		informationRecord.setF_new_information(bqMem.getF_user_email());
		informationRecord.setF_information_type(1);
		informationRecord.setF_modify_time(System.currentTimeMillis() / 1000);
		informationRecord.setF_modify_ip(StringUtil.isEmpty(sip) ? "" : sip);
		informationRecord.setF_modify_server(1);
		informationRecord.setF_modify_user("");
		informationRecord.setF_operation_type(1);
		bqMemberModifyInformationRecordService.save(informationRecord);
		request.getSession().removeAttribute("phone_register");
		
		mav.setViewName("jsp/activate_success");
		return mav;
	}
	
	@RequestMapping("/sendTelCode")
	public void sendTelCode(HttpServletRequest request, HttpServletResponse response, RedirectAttributes model) throws Exception {
		BQMember registerMember = (BQMember) request.getSession().getAttribute("phone_register");
		if (StringUtil.isEmpty(registerMember)) {
			toWeb(response, "error");
			return;
		}
		
		int random = MessageInterface.random6();
		String sendRet = "error";
		try {
			sendRet = sendTelCode(Convert.toLong(registerMember.getF_user_email(), 0), registerMember.getF_user_email(), random, registerMember.getF_user_nick_name());
		} catch (Exception e) {
			toWeb(response, sendRet);
			e.printStackTrace();
		}
		
		if (StringUtil.isEmpty(sendRet)) {
			registerMember.setF_user_credits(random); // code
			registerMember.setF_user_time(0); // error times
			request.getSession().setAttribute("phone_register", registerMember);
			
			toWeb(response, "success");
			return;
		} else {
			toWeb(response, sendRet);
			return;
		}
	}
	
	private void doRegisterRewardBft(long userId, BQBftRewardSetting setting) {
		if (setting != null && setting.getRegister_reward().compareTo(BigDecimal.ZERO) > 0
				&& setting.getOld_register_rewarded()) {
			BQBftRewardRecord record = new BQBftRewardRecord();
			record.setF_user_id(userId);
			record.setCurrency_type_id(setting.getCurrency_type_id());
			record.setReason(Contents.REWARD_REASON_REGISTER);
			record.setAmount(setting.getRegister_reward());
			record.setNote("用户注册赠送");

			bqBftRewardRecordService.bonus(record);
		}

	}
	
	// 检查邮箱唯一
	@RequestMapping("/checkUserEmail")
	public void checkUserEmail(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

//		String email = request.getParameter("email");
		try {
//			if (null == bqMemberService.readCheckUserEmail(email)) {
				toWeb(response, "true");
//			} else {
//				toWeb(response, "false");
//			}
		} catch (Exception e) {
		}
	}

	@RequestMapping("/resend")
	public void resendEmail(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		Object emailStr = request.getSession().getAttribute(DATA_SessionKeys.REGISTER_EMAIL_STRING);
		Object email = request.getSession().getAttribute(DATA_SessionKeys.REGISTER_EMAIL);
		if (emailStr == null || email == null)
			toWeb(response, 2);
		else {
			SendMail sm = new SendMail();
			sm.send(email.toString(), sm.getReister(Utils.getWebRoot(request), email.toString(), emailStr.toString()));
			toWeb(response, 1);
		}

	}

	// 检查验证码是否正确
	@RequestMapping("/checkValidcode")
	public void checkValidcode(HttpServletRequest request, HttpServletResponse response) {
		Contents.Local.LOCALE.set(getLocale(request));

		try {
			String validcode = request.getParameter("validcode");
			if(!StringUtil.isEmpty(validcode)) {
				validcode = validcode.toLowerCase();
			}
			validcode = MD5Service.encryptString(validcode);
			String validCode2 = (String) request.getSession().getAttribute("validCode");// 获取session中验证码
			
			if (validcode.equals(validCode2)) {
				toWeb(response, "true");
			} else {
				request.getSession().removeAttribute("validCode");
				toWeb(response, "false");
			}
		} catch (Exception e) {
		}
	}

	@RequestMapping("/rechargeService")
	public String registerService(HttpServletRequest request) {
		request.setAttribute("locale", Utils.getLocale(request));
		return "jsp/register_service";
	}

	/**
	 * 发送邮箱验证码
	 */
	// @RequestMapping("/sendEmailCode")
	public ModelAndView sendEmailCode(HttpServletRequest request, HttpServletResponse response, BQMember user, Model model, String provice, String city,
			String country, String invid, String countryName) {
		Contents.Local.LOCALE.set(getLocale(request));
		ModelAndView mv = new ModelAndView();
		try {
			if (user == null) {
				model.addAttribute("error", SpringContext.getText("msg.register.request.fail", Contents.Local.LOCALE.get()));
				mv.setViewName("jsp/register");
			}
			String email = user.getF_real_email();

			if (invid == null) {
				invid = "";
			}

			SendMail themail = new SendMail();

			JSONObject jo = new JSONObject();
			jo.put("email", email);
			jo.put("pwd", user.getF_user_pwd());
			jo.put("province", provice);
			jo.put("country", country);
			jo.put("countryName", countryName);
			jo.put("city", city);
			jo.put("invid", invid);
			jo.put("salt", user.getF_user_salt());
			jo.put("time", System.currentTimeMillis() / 1000);

			String slj = Utils.getWebRoot(request) + "register/registerdo.do?action=activate&salt="
					+ Security.encryptToHex(jo.toString(), Security.des_registerkey);
			System.out.println(email+",  "+slj);
			// StringBuffer sb = new
			// StringBuffer("感谢您对BTCQ的支持！赶快点击下面的链接激活您的帐号吧<br /><br /><br />");
			// sb.append(UrlConfig.getWebRoot() +
			// "register/registerdo.do?action=activate&salt=");
			// sb.append(Security.encryptToHex(jo.toString(),
			// Security.des_registerkey) + "<br /><br />");
			//
			// sb.append("如果链接无法点击, 请完整拷贝到浏览器地址栏里直接访问.<br />");
			// sb.append("20分钟之后链接失效, 请重新注册! <br /><br /><br />");
			// sb.append("Thanks, <br />The BTCQ Team");
			request.getSession().setAttribute(DATA_SessionKeys.REGISTER_EMAIL_STRING, slj);
			request.getSession().setAttribute(DATA_SessionKeys.REGISTER_EMAIL, email);

			themail.send(email, themail.getReister(Utils.getWebRoot(request), email, slj));
		} catch (Exception e) {
			logger.info("发送邮箱验证码失败");
			logger.error(e.toString());
			toWeb(response, "error");
			model.addAttribute("error", SpringContext.getText("msg.register.request.sendCodeFail", Contents.Local.LOCALE.get()));
			mv.setViewName("jsp/register");
		}
		return mv;

	}

	// /email过来
	public boolean processActivate(String email, long time) throws ServiceException {
		BQMember bqMem = bqMemberService.readByRealEmail(email);
		// 验证用户是否存在
		if (bqMem == null) {
			Date datecur = new Date();
			Date dateBef = DateUtil.UnixToDate(time); // getDate(Integer.parseInt(time));
			Date dateAft = getLastActivateTime(dateBef);
			if (datecur.before(dateAft)) {// 判断时间
				return true;
			} else {
				return false;
			}
		}
		return false;

	}

	public Date getLastActivateTime(Date date) {
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		cl.add(Calendar.DATE, 2);
		return cl.getTime();
	}

	public int getTime(Date date) {// 得到当前时间戳
		Timestamp appointTime = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// Date date;
		try {
			date = df.parse(String.valueOf(appointTime));
			long s = date.getTime();
			String unixDate = String.valueOf(s).substring(0, 10);
			return Integer.parseInt(unixDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public Date getDatetimestamp() {// 得到指定时间戳
		String str = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Date getDate(int i) {
		// 把时间戳转换成java时间
		Date date = new Date(new Integer(i).longValue() * 1000);
		return date;
	}

	// 判断日期
	public static boolean isSameWeek(Date date1, Date date2) {

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		// subYear==0,说明是同一年
		if (subYear == 0) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	}

	// 判断日期
	public static boolean isSameMonth(Date date1, Date date2) {

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		// subYear==0,说明是同一年
		if (subYear == 0) {
			if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
				return true;
		}
		return false;
	}
}
