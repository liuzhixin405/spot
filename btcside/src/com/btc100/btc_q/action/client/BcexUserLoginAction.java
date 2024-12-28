package com.btc100.btc_q.action.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.DATA_CreditNumber;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQBcexMember;
import com.btc100.btc_q.entity.BQBftRewardRecord;
import com.btc100.btc_q.entity.BQBftRewardSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLoginLog;
import com.btc100.btc_q.service.BQBcexMemberService;
import com.btc100.btc_q.service.BQBftRewardRecordService;
import com.btc100.btc_q.service.BQBftRewardSettingService;
import com.btc100.btc_q.service.BQCommonFailedloginService;
import com.btc100.btc_q.service.BQMemberLoginLogService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberStatusService;
import com.btc100.btc_q.service.RegisterSerivce;
import com.btc100.btc_q.util.APIEnDecrype;
import com.btc100.btc_q.util.CookieHelper;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.IpMacUtil;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.SafeToken;
import com.btc100.btc_q.util.StringUtil;

/**
 * BCEX用户登陆
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("")
public class BcexUserLoginAction extends BaseAction {

	Logger logger = Logger.getLogger(BcexUserLoginAction.class);
	
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQBcexMemberService bqBcexMemberService;
	@Autowired
	private RegisterSerivce registerService;
	@Autowired
	private BQCommonFailedloginService bqCommonFailedloginService;
	@Autowired
	private BQMemberStatusService bqMemberStatusService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQMemberLoginLogService bqMemberLoginLogService;
	@Autowired
	private BQBftRewardSettingService bqBftRewardSettingService;
	@Autowired
	private BQBftRewardRecordService bqBftRewardRecordService;
	
	private static String rsaPublickey = "";
	
	@RequestMapping("/bcexLogin")
	public String findUserInfo(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, false);
		String basePath = super.getRequestRoot(request);
		if (user != null) {
			return "redirect:" + basePath + "trade/trade.do";
		}

		String param = request.getParameter("Param");
		if (StringUtil.isEmpty(param)) {
			logger.info("Param参数为空");
			response.sendRedirect(basePath + "trade/trade.do");
			return null;
		}
		
		param = param.replaceAll(" ", "+");
		
		logger.info(param);
		byte[] encrypted = Base64.decodeBase64(param);
		if (encrypted == null || encrypted.length == 0) {
			logger.info("解密失败");
			response.sendRedirect(basePath + "trade/trade.do");
			return null;
		}
		
		if (StringUtil.isEmpty(rsaPublickey)) {
			String keyPath = UrlConfig.getValue("taiyi.bcex.rsakeypath");
			rsaPublickey = getKey(keyPath);
		}
		
		String signInfo = "";
		try {
			signInfo = new String(APIEnDecrype.decryptRSAByPublicKey(encrypted, rsaPublickey));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info(signInfo);
		if (StringUtil.isEmpty(signInfo)) {
			logger.info("数据为空");
			response.sendRedirect(basePath + "trade/trade.do");
			return null;
		}
		
		try {
			JSONObject jsonInfo = new JSONObject(signInfo);
			Map<String, String> map = new HashMap<String, String>();
			if (jsonInfo.has("AppKey")) {
				String appkey = jsonInfo.getString("AppKey");
				map.put("AppKey", appkey);
				String key = UrlConfig.getValue("taiyi.bcex.appkey");
				if (!appkey.equals(key)) {
					logger.info("AppKey不匹配");
					response.sendRedirect(basePath + "trade/trade.do");
					return null;
				}
				
			} else {
				response.sendRedirect(basePath + "trade/trade.do");
				return null;
			}
			
			String userName = "";
			String timestamp = "";
			
			if (jsonInfo.has("UserName")) {
				userName = jsonInfo.getString("UserName");
				map.put("UserName", userName);
			} else {
				logger.info("UserName找不到");
				response.sendRedirect(basePath + "trade/trade.do");
				return null;
			}
			
			if (jsonInfo.has("TimeStamp")) {
				timestamp = jsonInfo.getString("TimeStamp");
				map.put("TimeStamp", timestamp);
			} else {
				logger.info("TimeStamp找不到");
				response.sendRedirect(basePath + "trade/trade.do");
				return null;
			}
			
			long time = Long.parseLong(timestamp);
			long now = DateUtil.DateToUnix(new Date());
			
			if (StringUtil.isEmpty(userName) || Math.abs(now - time) > 600) {
				logger.info("TimeStamp超时， 当前时间：" + now);
				response.sendRedirect(basePath + "trade/trade.do");
				return null;
			}
	
			String sign = "";
			try {
				sign = signTopRequest(map, UrlConfig.getValue("taiyi.bcex.appSecret"));
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("签名失败");
				response.sendRedirect(basePath + "trade/trade.do");
				return null;
			}
			
			if (jsonInfo.has("Sign")) {
				String netSign = jsonInfo.getString("Sign");
				if (!sign.equalsIgnoreCase(netSign)) {
					logger.info("签名不匹配");
					response.sendRedirect(basePath + "trade/trade.do");
					return null;
				}
			} else {
				logger.info("签名为空");
				response.sendRedirect(basePath + "trade/trade.do");
				return null;
			}
			
			// 用户名放入缓存(10分钟)
			spyMemcachedClient.set("bcexlogin-" + request.getSession().getId(), BTCQMemecacheKeys.TIMES.BPI, userName);
			
			// 根据email查询bfx用户表是否有该用户
			BQMember member = bqMemberService.readUserByEmail(userName);
			if (null == member) {
				response.sendRedirect(basePath + "home/home.do?bcexLogin=1&newUser=1");
				return null;
			} else {
				response.sendRedirect(basePath + "doBcexLogin.do");
				return null;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		response.sendRedirect(basePath + "trade/trade.do");
		return null;
	}
	
	@RequestMapping("/doBcexLogin")
	public String doBcexLogin(HttpServletRequest request, HttpServletResponse response, RedirectAttributes model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		BQMember user = super.findCurrUser(request, false);
		String basePath = super.getRequestRoot(request);
		if (user != null) {
			return "redirect:" + basePath + "trade/trade.do";
		}
		
		String email = spyMemcachedClient.get("bcexlogin-" + request.getSession().getId());
		if (StringUtil.isEmpty(email)) {
			return "redirect:" + basePath + "trade/trade.do";
		}
		
		user = bqMemberService.readUserByEmail(email);
		if (user != null) {
			bcexLoginSuccess(request, response, user.getF_user_email());
			return "redirect:" + basePath + "trade/trade.do";
		}
		
		BQBcexMember bcexMember = bqBcexMemberService.readByEmail(email);
		if (null != bcexMember) {
			String password = request.getParameter("password");
			String random6 = RandomUtil.getRandomString(6);
			try {
				password = MD5Service.encryptString(password).toLowerCase();
				password = MD5Service.encryptString(password + random6).toLowerCase();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 注册用户
			BQMember regMember = new BQMember();
			long now = DateUtil.DateToUnix(new Date());
			int credits = DATA_CreditNumber.CREDIT_ONE_REGISTER + DATA_CreditNumber.CREDIT_ONE_SUREEMAIL;
			if (!StringUtil.isEmpty(bcexMember.getF_user_tel())) {
				credits = DATA_CreditNumber.CREDIT_ONE_REGISTER + DATA_CreditNumber.CREDIT_ONE_SUREEMAIL
						+ DATA_CreditNumber.CREDIT_ONE_BINDPHONE;
			}
			
			regMember.setF_user_email(email);
			regMember.setF_real_email(email);
			regMember.setF_user_email_status(1);
			regMember.setF_user_pwd(password);
			regMember.setF_user_credits(credits);
			regMember.setF_user_level_id(1);// 用户VIP级别(bq_member_level:level_id)',
			regMember.setF_user_state(1);// '用户状态（0:无效,1:有效,-1:冻结）'
			regMember.setF_user_salt(random6);
			regMember.setF_first_login(1);//第一次登录
			regMember.setF_user_origin("bcex");
			regMember.setF_user_time((int) now);
			
			registerService.bcexRegister(regMember, IpMacUtil.getIpAddr(request), bcexMember);
			
			BQBftRewardSetting setting = bqBftRewardSettingService.readOne();
			if (setting != null && setting.getRegister_reward().compareTo(BigDecimal.ZERO) > 0 
					&& setting.getOld_register_rewarded()) {
				BQBftRewardRecord record = new BQBftRewardRecord();
				record.setF_user_id(regMember.getF_user_id());
				record.setCurrency_type_id(setting.getCurrency_type_id());
				record.setReason(Contents.REWARD_REASON_REGISTER);
				record.setAmount(new BigDecimal(credits));
				record.setNote("bcex用户注册赠送");
				
				bqBftRewardRecordService.bonus(record);
			}
			
			bcexLoginSuccess(request, response, regMember.getF_user_email());
			return "redirect:" + basePath + "trade/trade.do";
		}

		return "redirect:" + basePath + "login/login.do";
	}
	
	private void bcexLoginSuccess(HttpServletRequest request, HttpServletResponse response, String userName) {
		BQMember user = bqMemberService.readUserByEmail(userName);
		if (user == null) return;
		
		request.getSession().setAttribute("userinfo", user);
		// Session最大生命周期（秒） 20 * 60 = 20分钟
		request.getSession().setMaxInactiveInterval(20 * 60);
		CookieHelper.setCookie(response, "email", user.getF_user_email());
		CookieHelper.setCookie(response, "_token", new SafeToken().getToken(), -2, false);

		try {
			String IP = IpMacUtil.getIpAddr(request);// 用户登录IP
			
//			long startTime = TimeUtil.getTimesmorning();
//			long endTime = startTime + 86400l;
			// 判断用户今天是否是第一次登陆，如果是奖励BFC
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
			
			if (user.getF_first_login() == 1) {
				user.setF_first_login(0);
				bqMemberService.update(user);
			}
			bqMemberLoginLogService.save(new BQMemberLoginLog(user.getF_user_id(), DateUtil.DateToUnix(new Date()), IP, "bcex跳转登陆成功"));
			// bq_member_status 修改用户登录状态
			bqMemberStatusService.updateStatus(user.getF_user_id(), IP);
			bqCommonFailedloginService.clearLoginTime(user.getF_user_email());// 登录成功后 把登录错误次数清空
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private String getKey(String path) {
    	File file = new File(path);
        StringBuilder localStrBulider = new StringBuilder();
        if(file.isFile() && file.exists()) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "utf-8");
                BufferedReader bufferReader = new BufferedReader(inputStreamReader);
                String lineStr = null;
                try {
                    while((lineStr = bufferReader.readLine()) != null) {
                        localStrBulider.append(lineStr);
                    }
                    bufferReader.close();
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } 
        
        return localStrBulider.toString();
    }
	
	private static String signTopRequest(Map<String, String> parameters, String secret) throws Exception
    {
		// 第一步：把字典按Key的字母顺序排序
		Map<String, String> orderMap = com.btc100.btc_q.util.Utils.sortMapByKey(parameters);    //按Key进行排序
		
        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        query.append(secret);
        Iterator<String> keys = orderMap.keySet().iterator();
        while (keys.hasNext()) {
			String key = keys.next();
			String value = orderMap.get(key);
            if (!StringUtil.isEmpty(key))
            {
                query.append(key).append(value);
            }
		}

        // 第三步：使用MD5加密
        query.append(secret);
        String utf8Str = new String(query.toString().getBytes("UTF-8"),"UTF-8");
        return MD5Service.encryptString(utf8Str);
    }
	
}
