package com.btc100.btc_q.action.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQAccountContract;
import com.btc100.btc_q.entity.BQBcexMember;
import com.btc100.btc_q.entity.BQBftRewardRecord;
import com.btc100.btc_q.entity.BQBftRewardSetting;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberInviteLog;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQSubMember;
import com.btc100.btc_q.entity.BQUserAppKey;
import com.btc100.btc_q.entity.DeliveryAccountContract;
import com.btc100.btc_q.entity.NAccountContract;
import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.VAccountContract;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQBcexMemberService;
import com.btc100.btc_q.service.BQBftRewardRecordService;
import com.btc100.btc_q.service.BQBftRewardSettingService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberInviteLogService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQSubMemberService;
import com.btc100.btc_q.service.BQUserAppKeyService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.NAccountContractService;
import com.btc100.btc_q.service.VAccountContractService;
import com.btc100.btc_q.util.APIEnDecrype;
import com.btc100.btc_q.util.StringUtil;

/**
 * 用户信息API
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/userApi")
public class UserInfoApiAction extends BaseApiAction {

	Logger logger = Logger.getLogger(UserInfoApiAction.class);
	
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	@Autowired
	private BQBcexMemberService bqBcexMemberService;
	@Autowired
	private BQMemberInviteLogService bqMemberInviteLogService;
	@Autowired
	private BQBftRewardSettingService bqBftRewardSettingService;
	@Autowired
	private BQBftRewardRecordService bqBftRewardRecordService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private DeliveryAccountContractService dAccountContractService;
	@Autowired
	private VAccountContractService vAccountContractService;
	@Autowired
	private BQUserAppKeyService bqUserAppKeyService;
	@Autowired
	private BQSubMemberService bqSubMemberService;
	@Autowired
	private NAccountContractService nAccountContractService;
	
	private static String rsaPublickey = "";
	
	@RequestMapping("/bcex/newUser")
	public void bcexNewUser(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {	
		String param = request.getParameter("Param");
		if (StringUtil.isEmpty(param)) {
			toWeb(response, "参数错误");
			return;
		}
		
		param = param.replaceAll(" ", "+");
		
		logger.info(param);
		byte[] encrypted = Base64.decodeBase64(param);
		if (encrypted == null || encrypted.length == 0) {
			toWeb(response, "参数错误");
			return;
		}
		
		if (StringUtil.isEmpty(rsaPublickey)) {
			String keyPath = UrlConfig.getValue("taiyi.bcex.rsakeypath");
			rsaPublickey = getBcexKey(keyPath);
			logger.info(rsaPublickey);
		}
		
		String signInfo = "";
		try {
			signInfo = new String(APIEnDecrype.decryptRSAByPublicKey(encrypted, rsaPublickey));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (StringUtil.isEmpty(signInfo)) {
			toWeb(response, "参数错误");
			return;
		}
		
		logger.info(signInfo);
		try {
			JSONObject jsonInfo = new JSONObject(signInfo);
			Map<String, String> map = new HashMap<String, String>();
			if (jsonInfo.has("AppKey")) {
				String appkey = jsonInfo.getString("AppKey");
				map.put("AppKey", appkey);
				String key = UrlConfig.getValue("taiyi.bcex.appkey");
				if (!appkey.equals(key)) {
					toWeb(response, "AppKey错误");
					return;
				}
				
			} else {
				toWeb(response, "参数错误-AppKey");
				return;
			}
			
			String userName = "";
			String userPwd = "";
			String capitalPwd = "";
			
			if (jsonInfo.has("UserName")) {
				userName = jsonInfo.getString("UserName");
				map.put("UserName", userName);
			} else {
				toWeb(response, "参数错误-UserName");
				return;
			}
			
			if (jsonInfo.has("CapitalPwd")) {
				capitalPwd = jsonInfo.getString("CapitalPwd");
				map.put("CapitalPwd", capitalPwd);
			}
			
			if (StringUtil.isEmpty(userName)) {
				toWeb(response, "参数错误");
				return;
			}
	
			String sign = "";
			try {
				sign = signTopRequest(map, UrlConfig.getValue("taiyi.bcex.appSecret"));
			} catch (Exception e) {
				e.printStackTrace();
				toWeb(response, "参数错误");
				return;
			}
			
			if (jsonInfo.has("Sign")) {
				String netSign = jsonInfo.getString("Sign");
				if (!sign.equalsIgnoreCase(netSign)) {
					toWeb(response, "签名错误");
					return;
				}
			} else {
				toWeb(response, "参数错误");
				return;
			}
			
			BQBcexMember member = bqBcexMemberService.readByEmail(userName);
			if (member != null) {
				toWeb(response, "已有账户");
				return;
			}
			
			BQBcexMember tmpMember = new BQBcexMember();
			tmpMember.setF_user_email(userName);
			tmpMember.setF_user_pwd(userPwd);
			tmpMember.setF_user_paypwd(capitalPwd);
			tmpMember.setF_user_tel("");
			
			bqBcexMemberService.save(tmpMember);			
		} catch (JSONException e) {
			e.printStackTrace();
			toWeb(response, "参数错误");
			return;
		}
		
		toWeb(response, "true");
	}
	
	@RequestMapping("/bcex/bindingPhone")
	public void bcexBindingPhone(HttpServletRequest request, HttpServletResponse response, Model model) {
		String param = request.getParameter("Param");
		if (StringUtil.isEmpty(param)) {
			toWeb(response, "参数错误");
			return;
		}
		
		param = param.replaceAll(" ", "+");
		
		logger.info(param);
		byte[] encrypted = Base64.decodeBase64(param);
		if (encrypted == null || encrypted.length == 0) {
			toWeb(response, "参数错误");
			return;
		}
		
		if (StringUtil.isEmpty(rsaPublickey)) {
			String keyPath = UrlConfig.getValue("taiyi.bcex.rsakeypath");
			rsaPublickey = getBcexKey(keyPath);
		}
		
		String signInfo = "";
		try {
			signInfo = new String(APIEnDecrype.decryptRSAByPublicKey(encrypted, rsaPublickey));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (StringUtil.isEmpty(signInfo)) {
			toWeb(response, "参数错误");
			return;
		}
		
		logger.info(signInfo);
		try {
			JSONObject jsonInfo = new JSONObject(signInfo);
			Map<String, String> map = new HashMap<String, String>();
			if (jsonInfo.has("AppKey")) {
				String appkey = jsonInfo.getString("AppKey");
				map.put("AppKey", appkey);
				String key = UrlConfig.getValue("taiyi.bcex.appkey");
				if (!appkey.equals(key)) {
					toWeb(response, "AppKey错误");
					return;
				}
				
			} else {
				toWeb(response, "参数错误");
				return;
			}
			
			String userName = "";
			String phoneNumber = "";
			
			if (jsonInfo.has("UserName")) {
				userName = jsonInfo.getString("UserName");
				map.put("UserName", userName);
			} else {
				toWeb(response, "参数错误");
				return;
			}
			
			if (jsonInfo.has("PhoneNumber")) {
				phoneNumber = jsonInfo.getString("PhoneNumber");
				map.put("PhoneNumber", phoneNumber);
			} else {
				toWeb(response, "参数错误");
				return;
			}
			
			if (StringUtil.isEmpty(userName) || StringUtil.isEmpty(phoneNumber)) {
				toWeb(response, "参数错误");
				return;
			}
	
			String sign = "";
			try {
				sign = signTopRequest(map, UrlConfig.getValue("taiyi.bcex.appSecret"));
			} catch (Exception e) {
				e.printStackTrace();
				toWeb(response, "参数错误");
				return;
			}
			
			if (jsonInfo.has("Sign")) {
				String netSign = jsonInfo.getString("Sign");
				if (!sign.equalsIgnoreCase(netSign)) {
					toWeb(response, "签名错误");
					return;
				}
			} else {
				toWeb(response, "参数错误");
				return;
			}
			
			BQMember member = bqMemberService.readUserByEmail(userName);
			if (member != null) {
				BQMemberSafeSettings bqms = bqMemberSafeSettingsService.readById(member.getF_user_id());
				if (StringUtil.isEmpty(bqms.getF_user_tel())) {
					bqms.setF_user_safequertion(bqms.getF_user_safequertion());
					bqms.setF_user_id(bqms.getF_user_id());
					bqms.setF_user_safeanswer(bqms.getF_user_safeanswer());
					bqms.setF_user_safegoogle(bqms.getF_user_safegoogle());
					if (bqms.getF_user_safetype() == 2) {
						bqms.setF_user_safetype(3);
					} else {
						bqms.setF_user_safetype(1);
					}
					
					bqms.setF_user_tel(phoneNumber);
					bqMemberSafeSettingsService.updateTel(bqms);
					
					/*
					if (member.getF_user_oms() == 0) {
						// 增加积分记录
						BQCommonCreditLog bqcl = new BQCommonCreditLog();
						bqcl.setF_user_id(member.getF_user_id());
						bqcl.setCredit_type_id(Contents.CREDIT_TYPE_BINDPHONE);
						bqcl.setCredits(DATA_CreditNumber.CREDIT_ONE_BINDPHONE);
						bqcl.setTitle("绑定手机");
						bqcl.setDateline(DateUtil.DateToUnix(new Date()));
						bqCommonCreditLogService.saveCreditsAndUpdateLevel(bqcl, false, 1);
					}
					*/

					BQMemberInviteLog log = bqMemberInviteLogService.readByUserId(member.getF_user_id());
					if (log != null) {
						BQMember inviter = bqMemberService.readUserById(log.getInvite_uid());
						if (inviter != null) {
							BQBftRewardSetting setting = bqBftRewardSettingService.readOne();
							if (setting != null && setting.getInvite_reward().compareTo(BigDecimal.ZERO) > 0) {
								BQBftRewardRecord record = new BQBftRewardRecord();
								record.setF_user_id(inviter.getF_user_id());
								record.setCurrency_type_id(setting.getCurrency_type_id());
								record.setReason(Contents.REWARD_REASON_INVITE);
								record.setAmount(setting.getInvite_reward());
								record.setNote("邀请用户赠送");
								
								bqBftRewardRecordService.bonus(record);
							}
						}
					}
				}
				
			} else {
				BQBcexMember tmpMember = bqBcexMemberService.readByEmail(userName);
				tmpMember.setF_user_tel(phoneNumber);
				bqBcexMemberService.update(tmpMember);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			toWeb(response, "参数错误");
			return;
		}
		
		toWeb(response, "true");
	}
	
	@RequestMapping("/accounts")
	public String getAccounts(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String type = request.getParameter("type");
		JSONArray array = new JSONArray();
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		if (!StringUtil.isEmpty(type)) {
			map.put("type", type);
		}
		
		String checkKeyAndSign = checkAppKeyAndSign(request, map);
		if (!StringUtil.isEmpty(checkKeyAndSign)) {
			model.addAttribute("ticker", checkKeyAndSign);
			return apiData(response);
		}
		
		long userId = Convert.toLong(map.get("userId"), 0);
		if ("spot".equals(type)) {
			List<BQCurrencyType> currencyTypes = bqCurrencyTypeService.readAll();
			List<BQAccount> accounts = bqAccountService.readAllByUserId(userId);
			for (BQAccount account : accounts) {
				JSONObject json = new JSONObject();
				boolean contain = false;
				for (BQCurrencyType currency : currencyTypes) {
					if (currency.getType_id() == account.getCurrency_type_id()) {
						contain = true;
						json.put("symbol", currency.getType_name().toLowerCase());
						break;
					}
				}
				
				if (!contain) {
					continue;
				}
				
				json.put("available", account.getBtc().doubleValue());
				json.put("frozen", account.getBtc_locked().doubleValue());
				array.put(json);
			}
			
		} else if ("vcontract".equals(type)) {
			List<VAccountContract> accounts = vAccountContractService.readAllByUid(userId);
			for (VAccountContract account : accounts) {
				JSONObject json = new JSONObject();
				json.put("symbol", account.getF_code());
				json.put("available", account.getF_btc().doubleValue());
				json.put("frozen", account.getF_lockbtc().doubleValue());
				array.put(json);
			}
		} else if ("delivery".equals(type)) {
			List<DeliveryAccountContract> accounts = dAccountContractService.readAllByUid(userId);
			for (DeliveryAccountContract account : accounts) {
				JSONObject json = new JSONObject();
				json.put("symbol", account.getF_code());
				json.put("available", account.getF_btc().doubleValue());
				json.put("frozen", account.getF_lockbtc().doubleValue());
				array.put(json);
			}
		} else if(type == null || "".equals(type) || "contract".equals(type)) {
			List<BQAccountContract> accounts = bqAccountContractService.readAllByUid(userId);
			for (BQAccountContract account : accounts) {
				JSONObject json = new JSONObject();
				json.put("symbol", account.getF_code());
				json.put("available", account.getF_btc().doubleValue());
				json.put("frozen", account.getF_lockbtc().doubleValue());
				array.put(json);
			}
		} else {
			NContractPartition part = nContractPartitionService.readByPartition(type);
			if (part != null) {
				List<NAccountContract> accounts = nAccountContractService.readAllByUid(type, userId);
				for (NAccountContract account : accounts) {
					JSONObject json = new JSONObject();
					json.put("symbol", account.getF_code());
					json.put("available", account.getF_btc().doubleValue());
					json.put("frozen", account.getF_lockbtc().doubleValue());
					array.put(json);
				}
			} else {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "参数错误，未知type");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
		}
		
		tickerJson.put("data", array);
		model.addAttribute("ticker", tickerJson);
		
		return apiData(response);
	}
	
	private String getBcexKey(String path) {
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

	/**
	 * 母账户操作子账号申请appkey
	 * 
	 */
	@RequestMapping(value = "/subAccount/bindAppKey", method = RequestMethod.POST)
	public String doSubBindAppKey(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String subUserName = request.getParameter("subUserName"); // 子账号登录名
		if (StringUtil.isEmpty(subUserName)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "subUserName不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("subUserName", subUserName);
		
		String allowIp = request.getParameter("allowIp");
		if (StringUtil.isEmpty(allowIp)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "allowIp不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("allowIp", allowIp);
		
		String checkKeyAndSign = checkAppKeyAndSign(request, map);
		if (!StringUtil.isEmpty(checkKeyAndSign)) {
			model.addAttribute("ticker", checkKeyAndSign);
			return apiData(response);
		}
		
		long userId = Convert.toLong(map.get("userId"), 0);
		BQMember subUser = bqMemberService.findUserByEmail(subUserName);
		if (subUser == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "子账号不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		long subUid = subUser.getF_user_id();
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(userId, subUid);
		if (bqSubMember == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "subUserName错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(subUid);
		if (bqUserAppKey != null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "子账号已申请api");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		// 验证IP格式
		String regex = "(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])"
				+ "(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3}"
				+ "(\\s*,\\s*(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3})*";
		if (!allowIp.matches(regex)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "ip地址格式不正确");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		// API最多只能绑定10个IP
		String[] ipArray = allowIp.split(",");
		if (ipArray.length > 10) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "最多绑定10个IP");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
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
		
		JSONObject json = new JSONObject();
		json.put("AppKey", userAppKey.getApp_key());
		json.put("AppSecret", userAppKey.getApp_secret());
		json.put("IP", userAppKey.getAllow_ip());
		tickerJson.put("data", json);
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	/**
	 * 母账户操作子账号删除appkey
	 * 
	 */
	@RequestMapping(value = "/subAccount/deleteAppKey", method = RequestMethod.POST)
	public String deleteSubAppKey(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String subUserName = request.getParameter("subUserName"); // 子账号登录名
		if (StringUtil.isEmpty(subUserName)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "subUserName不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("subUserName", subUserName);
		
		String checkKeyAndSign = checkAppKeyAndSign(request, map);
		if (!StringUtil.isEmpty(checkKeyAndSign)) {
			model.addAttribute("ticker", checkKeyAndSign);
			return apiData(response);
		}
		
		long userId = Convert.toLong(map.get("userId"), 0);
		BQMember subUser = bqMemberService.findUserByEmail(subUserName);
		if (subUser == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "子账号不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		long subUid = subUser.getF_user_id();
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(userId, subUid);
		if (bqSubMember == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "subUserName错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(subUid);
		if (bqUserAppKey == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "appkey已删除");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		bqUserAppKeyService.delete(subUid);
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	/**
	 * 母账户操作子账号更新appkey的ip地址
	 * 
	 */
	@RequestMapping(value = "/subAccount/updateAppKey", method = RequestMethod.POST)
	public String updateSubAppKey(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String subUserName = request.getParameter("subUserName"); // 子账号登录名
		if (StringUtil.isEmpty(subUserName)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "subUserName不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("subUserName", subUserName);
		
		String allowIp = request.getParameter("allowIp");
		if (StringUtil.isEmpty(allowIp)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "allowIp不能为空");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		map.put("allowIp", allowIp);
		
		String checkKeyAndSign = checkAppKeyAndSign(request, map);
		if (!StringUtil.isEmpty(checkKeyAndSign)) {
			model.addAttribute("ticker", checkKeyAndSign);
			return apiData(response);
		}
		
		long userId = Convert.toLong(map.get("userId"), 0);
		BQMember subUser = bqMemberService.findUserByEmail(subUserName);
		if (subUser == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "子账号不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		long subUid = subUser.getF_user_id();
		BQSubMember bqSubMember = bqSubMemberService.readByUserId(userId, subUid);
		if (bqSubMember == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "subUserName错误");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(subUid);
		if (bqUserAppKey == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "子账号的appkey不存在");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		// 验证IP格式
		String regex = "(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])"
				+ "(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3}"
				+ "(\\s*,\\s*(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])){3})*";
		if (!allowIp.matches(regex)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "ip地址格式不正确");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		// API最多只能绑定10个IP
		String[] ipArray = allowIp.split(",");
		if (ipArray.length > 10) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "最多绑定10个IP");
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}

		bqUserAppKey.setAllow_ip(allowIp);
		bqUserAppKeyService.update(bqUserAppKey);
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
	
	/**
	 * 获取子账号appkey
	 * 
	 */
	@RequestMapping(value = "/subAccount/getAppKey", method = RequestMethod.POST)
	public String getSubAppKey(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Contents.Local.LOCALE.set(getLocale(request));

		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		Map<String, String> map = new HashMap<String, String>();
		String checkParams = checkCommonParams(request, map);
		if (!StringUtil.isEmpty(checkParams)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, checkParams);
			model.addAttribute("ticker", tradesStr);
			return apiData(response);
		}
		
		String subUserName = request.getParameter("subUserName"); // 子账号登录名
		if (!StringUtil.isEmpty(subUserName)) {
			map.put("subUserName", subUserName);
		}
		
		String checkKeyAndSign = checkAppKeyAndSign(request, map);
		if (!StringUtil.isEmpty(checkKeyAndSign)) {
			model.addAttribute("ticker", checkKeyAndSign);
			return apiData(response);
		}
		
		long userId = Convert.toLong(map.get("userId"), 0);
		
		if (!StringUtil.isEmpty(subUserName)) {
			BQMember subUser = bqMemberService.findUserByEmail(subUserName);
			if (subUser == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "子账号不存在");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			long subUid = subUser.getF_user_id();
			BQSubMember bqSubMember = bqSubMemberService.readByUserId(userId, subUid);
			if (bqSubMember == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "subUserName错误");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			BQUserAppKey bqUserAppKey = bqUserAppKeyService.readOne(subUid);
			if (bqUserAppKey == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, "appkey已删除");
				model.addAttribute("ticker", tradesStr);
				return apiData(response);
			}
			
			JSONObject json = new JSONObject();
			json.put("AppKey", bqUserAppKey.getApp_key());
			json.put("AppSecret", bqUserAppKey.getApp_secret());
			json.put("IP", bqUserAppKey.getAllow_ip());
			tickerJson.put("data", json);
		} else {
			List<Object[]> list = bqUserAppKeyService.readAllSubAppkey(userId);
			JSONArray array = new JSONArray();
			for (Object[] obj : list) {
				JSONObject json = new JSONObject();
				json.put("userName", (String) obj[1]);
				json.put("AppKey", (String) obj[2]);
				json.put("AppSecret", (String) obj[3]);
				json.put("IP", (String) obj[4]);
				array.put(json);
			}
			tickerJson.put("data", array);
		}
		
		model.addAttribute("ticker", tickerJson);
		return apiData(response);
	}
}
