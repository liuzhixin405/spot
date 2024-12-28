package com.btc100.btc_q.action.otc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberPayword;
import com.btc100.btc_q.entity.BQMemberSafeSettings;
import com.btc100.btc_q.entity.BQTransferWithOtcRecord;
import com.btc100.btc_q.entity.BQUserRiskControl;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberPaywordService;
import com.btc100.btc_q.service.BQMemberRechargeService;
import com.btc100.btc_q.service.BQMemberSafeSettingsService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQTransferWithOtcRecordService;
import com.btc100.btc_q.service.BQUserRiskControlService;
import com.btc100.btc_q.service.TransferAccountService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.JwtToken;
import com.btc100.btc_q.util.MD5Service;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.BalanceAccountVO;
import com.btc100.btc_q.vo.MemberVO;

@Controller
@RequestMapping("/bfxotc")
public class OtcAction extends BaseAction  {
	
	Logger logger = Logger.getLogger(OtcAction.class);
	
	@Autowired
	private BQMemberPaywordService bqMemberPaywordService;
	
	@Autowired
	private BQMemberService bqMemberService;
	
	@Autowired
	private BQAccountService bqAccountService;
	
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	
	@Autowired
	private TransferAccountService transferAccountService;
	
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	
	@Autowired
	private BQMemberRechargeService bqMemberRechargeService;
	
	@Autowired
	private BQUserRiskControlService bqUserRiskControlService;
	
	@Autowired
	private BQTransferWithOtcRecordService bqTransferWithOtcRecordService;
	
	@Autowired
	private BQMemberSafeSettingsService bqMemberSafeSettingsService;
	
	private static final class UserState {
		
		private UserState() {}
		
		public static final String NORMAL = "NORMAL";
		public static final String INVALID = "INVALID";
		public static final String FROZEN = "FROZEN";
	}
	
	public static final int ALLOW_MAX_FAILED_TIMES = 4;
	private static final String SECRET = "858161f4021a01028e024d0b0661f404";
	
	private String createErrorCode(int errorCode, String errorMsg) {
		JSONObject json = new JSONObject();
		json.put("code", errorCode);
		json.put("msg", errorMsg);
		
		return json.toString();
	}
	
	protected String formatEmail(String email) {
		String[] emails = email.split("@");
		if (emails.length > 1) {
			String name = emails[0];
			if (name.length() < 4) {
				email = name + "****@" + emails[1];
			} else {
				email = name.substring(0, 3) + "****" + name.substring(name.length() - 1, name.length()) + "@" + emails[1];
			}
		} else {
			if (email.length() < 4) {
				email = email.substring(0, 1) + "****" + email.substring(email.length() - 1, email.length());
			} else {
				email = email.substring(0, 3) + "****" + email.substring(email.length() - 1, email.length());
			}
		}

		return email;
	}
	
	private MemberVO getMemberVOFromUser(BQMember user) {
		MemberVO member = new MemberVO();
		member.setId(user.getF_user_id() + "");
		member.setUserName(user.getF_user_email());
		member.setNickName(formatEmail(user.getF_user_email()));
		member.setIsSubUser(user.getIs_sub_user() + "");
		member.setIdentity(user.getF_user_identity() + "");
		
		String state = UserState.INVALID;
		// '用户状态（0:无效,1:有效,-1:冻结）'
		if (user.getF_user_state() == 1) {
			state = UserState.NORMAL;
		} else if (user.getF_user_state() == -1) {
			state = UserState.FROZEN;
		}
		member.setState(state);
		
		BQMemberPayword payPwd = bqMemberPaywordService.readById(user.getF_user_id());
		member.setHavePayPwd(payPwd == null ? false : (!StringUtil.isEmpty(payPwd.getPwd()) && payPwd.getState() == 1));
		member.setUsePayPwd(payPwd == null ? false : (payPwd.getState() == 1));
		member.setRegisterTime(user.getF_user_time() * 1000L);
		member.setImg(user.getF_user_head_img());
		
		member.setLevelId(user.getF_user_level_id() + "");
		BQMemberLevel userLevel = bqMemberLevelService.findById(user.getF_user_level_id()); 
		if (userLevel != null) {
			member.setLevelName(userLevel.getLevel_name());
		} else {
			member.setLevelName("");
		}
		
		BQAccount bqa = bqAccountService.getAccount(user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
		if (bqa == null) {
			member.setAvailable(BigDecimal.ZERO);
		} else {
			member.setAvailable(bqa.getBtc().setScale(4, RoundingMode.HALF_DOWN));
		}
		
		BQMemberSafeSettings safeSetting = bqMemberSafeSettingsService.readById(user.getF_user_id());
		if (safeSetting.getF_user_safetype() == 1 || safeSetting.getF_user_safetype() == 3) {
			member.setTelNum(safeSetting.getF_user_tel());
		}
		member.setZone(safeSetting.getF_user_zone());
			
		return member;
	}

	@RequestMapping("/loginUser")
	public String getLoginUser(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try {
			BQMember user = super.findCurrUser(request, true);
			if (user == null) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "not login");
				model.addAttribute("ticker", tradesStr);
				return "jsp/api/ticker";
			}

			JSONObject tickerJson = new JSONObject();
			tickerJson.put("code", 0);
			tickerJson.put("msg", "");

			MemberVO member = getMemberVOFromUser(user);

			tickerJson.put("data", JSONObject.toJSON(member));
			model.addAttribute("ticker", tickerJson);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "jsp/api/ticker";
	}
	
	@RequestMapping("/userInfo")
	public String getUserInfo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		BQMember user = null;
		String userId = request.getParameter("userId");
		String userName = request.getParameter("userName");
		String nickName = request.getParameter("nickName");
		if (!StringUtil.isEmpty(userId)) {
			long uid = Convert.toLong(userId, 0);
			if (uid == 0) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "userId is 0");
				model.addAttribute("ticker", tradesStr);
				return "jsp/api/ticker";
			}
			
			user = bqMemberService.findUserById(uid);
			if (user == null || user.getF_user_id() != uid) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "user not find");
				model.addAttribute("ticker", tradesStr);
				return "jsp/api/ticker";
			}
		} else if (!StringUtil.isEmpty(userName)) {
			user = bqMemberService.readUserByEmail(userName);
			if (user == null || !user.getF_user_email().equals(userName)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "user not find");
				model.addAttribute("ticker", tradesStr);
				return "jsp/api/ticker";
			}
			
		} else if (!StringUtil.isEmpty(nickName)) {
			user = bqMemberService.readUserByNickName(nickName);
			if (user == null || !user.getF_user_nick_name().equals(nickName)) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "user not find");
				model.addAttribute("ticker", tradesStr);
				return "jsp/api/ticker";
			}
		}
		
		if (user == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "user not find");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");

		MemberVO member = getMemberVOFromUser(user);
		tickerJson.put("data", JSONObject.toJSON(member));
		model.addAttribute("ticker", tickerJson);
		
		return "jsp/api/ticker";
	}
	
	@RequestMapping("/updateHeadImg")
	public String updateHeadImg(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "not login");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String path = request.getParameter("imgPath");
		String uid = request.getParameter("userId");
		if (Convert.toLong(uid, 0) != user.getF_user_id()) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "diff user");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		if (StringUtil.isEmpty(path)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "no path");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		if (!StringUtil.validateString(path)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "error path");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		bqMemberService.updateHeadImg(user.getF_user_id(), path);
		model.addAttribute("ticker", tickerJson);
		
		super.findCurrUser(request, true);
		
		return "jsp/api/ticker";
	}
	
	@RequestMapping("/getAvailable")
	public String getAvailable(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		BQMember user = checkLogin(request, model);
		if (user == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "not login");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol errror");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQCurrencyType currency = bqCurrencyTypeService.getByTypeName(symbol);
		if (currency == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol not find");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQAccount bqa = bqAccountService.getAccount(user.getF_user_id(), currency.getType_id());
		BigDecimal btc = (bqa == null ? BigDecimal.ZERO : bqa.getBtc().setScale(4, RoundingMode.HALF_DOWN));
		tickerJson.put("data",  btc.doubleValue());
		
		model.addAttribute("ticker", tickerJson);
		return "jsp/api/ticker"; 
	}
	
	@RequestMapping("/transferFrom")
	public String transferFrom(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		BQMember user = checkLogin(request, model);
		if (user == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "not login");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String uid = request.getParameter("userId");
		
		String amtStr = request.getParameter("amount");
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(amtStr, 0.0)).setScale(8, RoundingMode.HALF_UP);
		String symbol = request.getParameter("symbol");
		if (amount.compareTo(BigDecimal.ZERO) <= 0 || StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "param error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String time = request.getParameter("time");
		if (StringUtil.isEmpty(time)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		long ltime = Convert.toLong(time, 0);
		if (Math.abs(System.currentTimeMillis() - ltime) > 60000) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String sign = request.getParameter("sign");
		if (StringUtil.isEmpty(sign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String params = "userId=" + uid + "&amount=" + amtStr + "&symbol=" + symbol + "&time=" + time;
		String checkSign = sha256_HMAC(params, SECRET);
		if (!sign.equals(checkSign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQCurrencyType coin = bqCurrencyTypeService.getByTypeName(symbol);
		if (coin == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQAccount account = bqAccountService.getAccount(user.getF_user_id(), coin.getType_id());
		if (account == null || account.getBtc().compareTo(amount) < 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "balance error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONObject data = new JSONObject();
		String note = checkTransferFromRisk(user, coin);
		if (StringUtil.isEmpty(note)) {
			try {
				String transNo = transferAccountService.updateAccountToOTC(coin, user.getF_user_id(), amount);
				if (!StringUtil.isEmpty(transNo)) {
					data.put("transNo", transNo);
					tickerJson.put("data", data);
				}
			} catch (Exception e) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "update account error");
				model.addAttribute("ticker", tradesStr);
				return "jsp/api/ticker";
			}
		} else {
			// 提交记录，冻结金额
			BQTransferWithOtcRecord record = new BQTransferWithOtcRecord();
			record.setF_user_id(user.getF_user_id());
			record.setCurrency_type_id(coin.getType_id());
			record.setCurrency_name(coin.getType_name());
			record.setDirection(2);
			record.setAmount(amount);
			record.setFrozen_amount(amount);
			record.setState(Contents.TRANSFER_OTC_STATE_MANUAL);
			record.setNote(note);
			try {
				String transNo = bqTransferWithOtcRecordService.saveRecord(record);
				if (!StringUtil.isEmpty(transNo)) {
					data.put("transNo", transNo);
					data.put("note", note);
					data.put("recordId", record.getId());
					tickerJson.put("data", data);
				}
			} catch (ServiceException e) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "Risk control user saving transfer out record error");
				model.addAttribute("ticker", tradesStr);
				return "jsp/api/ticker";
			}
		}
		
		model.addAttribute("ticker", tickerJson);
		return "jsp/api/ticker"; 
	}
	
	/**
	 * 人工审核转出
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/manualTransferFrom")
	public String manualTransferFrom(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String userId = request.getParameter("userId");
		long uid = ConvertUtil.toLong(userId, 0l);
		
		BQMember user = bqMemberService.findUserById(uid);
		if (user == null || user.getF_user_id() != uid) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "user not find");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String operation = request.getParameter("operation"); // 值：refuse，accept
		if (!"refuse".equals(operation) && !"accept".equals(operation)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "operation error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String recordId = request.getParameter("recordId");
		long id = Convert.toLong(recordId, 0);
		if (id <= 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "param error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String time = request.getParameter("time");
		if (StringUtil.isEmpty(time)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		long ltime = Convert.toLong(time, 0);
		if (Math.abs(System.currentTimeMillis() - ltime) > 60000) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String sign = request.getParameter("sign");
		if (StringUtil.isEmpty(sign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String params = "userId=" + userId + "&operation=" + operation + "&recordId=" + recordId + "&time=" + time;
		String checkSign = sha256_HMAC(params, SECRET);
		if (!sign.equals(checkSign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQTransferWithOtcRecord record = bqTransferWithOtcRecordService.readById(id);
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		try {
			String transNo = "";
			if ("refuse".equals(operation)) {
				transNo = bqTransferWithOtcRecordService.cancelRecord(record, "转出到OTC，人工审核取消转出");
			} else if ("accept".equals(operation)) {
				transNo = bqTransferWithOtcRecordService.confirmRecord(record, "转出到OTC，人工审核通过");
			}
			if (!StringUtil.isEmpty(transNo)) {
				tickerJson.put("data", transNo);
			}
		} catch (Exception e) {
			String tradesStr = createErrorCode(Contents.API_ERROR_UNKNOW, e.toString());
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		model.addAttribute("ticker", tickerJson);
		return "jsp/api/ticker"; 
	}
	
	@RequestMapping("/transferTo")
	public String transferTo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		BQMember user = checkLogin(request, model);
		if (user == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "not login");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String uid = request.getParameter("userId");
		
		String amtStr = request.getParameter("amount");
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(amtStr, 0.0)).setScale(8, RoundingMode.HALF_UP);
		String symbol = request.getParameter("symbol");
		if (amount.compareTo(BigDecimal.ZERO) <= 0 || StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "param error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String time = request.getParameter("time");
		if (StringUtil.isEmpty(time)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		long ltime = Convert.toLong(time, 0);
		if (Math.abs(System.currentTimeMillis() - ltime) > 60000) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String sign = request.getParameter("sign");
		if (StringUtil.isEmpty(sign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String params = "userId=" + uid + "&amount=" + amtStr + "&symbol=" + symbol + "&time=" + time;
		String checkSign = sha256_HMAC(params, SECRET);
		if (!sign.equals(checkSign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}

		BQCurrencyType coin = bqCurrencyTypeService.getByTypeName(symbol);
		if (coin == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQAccount account = bqAccountService.getAccount(user.getF_user_id(), coin.getType_id());
		if (account == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "account error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		try {
			String transNo = transferAccountService.updateAccountFromOTC(coin, user.getF_user_id(), amount);
			if (!StringUtil.isEmpty(transNo)) {
				tickerJson.put("data", transNo);
			}
		} catch (Exception e) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "update account error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		model.addAttribute("ticker", tickerJson);
		
		return "jsp/api/ticker";  
	}
	
	@RequestMapping("/validatePayPwd")
	public String validatePayPwd(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		BQMember user = checkLogin(request, model);
		if (user == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "login");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String payPwd = request.getParameter("payPwd");
		if (StringUtil.isEmpty(payPwd)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "empty");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String uid = request.getParameter("userId");
		
		String time = request.getParameter("time");
		if (StringUtil.isEmpty(time)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		long ltime = Convert.toLong(time, 0);
		if (Math.abs(System.currentTimeMillis() - ltime) > 60000) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String sign = request.getParameter("sign");
		if (StringUtil.isEmpty(sign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String params = "userId=" + uid + "&payPwd=" + payPwd + "&time=" + time;
		String checkSign = sha256_HMAC(params, SECRET);
		if (!sign.equals(checkSign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		boolean bol_paypwd = false;
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() == 1) {
			
			if (payword.getFailed_times() > ALLOW_MAX_FAILED_TIMES) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "times");
				model.addAttribute("ticker", tradesStr);
				return "jsp/api/ticker";
			}
			
			if (!StringUtil.isEmpty(payPwd)) {
				try {
					payPwd = MD5Service.encryptString(payPwd).toLowerCase() + payword.getSalt().toLowerCase();
					payPwd = MD5Service.encryptString(payPwd).toLowerCase();
					if (payPwd.equals(payword.getPwd())) {
						bol_paypwd = true;
					}
				} catch (Exception e) {
					
				}
			}
		} else {
			bol_paypwd = true;
		}

		if (!bol_paypwd) {
			bqMemberPaywordService.addFailedTimes(user.getF_user_id());
			user = super.findCurrUser(request, true);
			
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "invalid");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		} else {
			if (payword.getFailed_times() > 0) {
				bqMemberPaywordService.resetFailedTimes(user.getF_user_id());
				user = super.findCurrUser(request, true);
			}
		}
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		model.addAttribute("ticker", tickerJson);
		return "jsp/api/ticker"; 
	}
	
	private BQMember checkLogin(HttpServletRequest request, Model model) {
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return null;
		}
		
		String uid = request.getParameter("userId");
		if (Convert.toLong(uid, 0) != user.getF_user_id()) {
			return null;
		}
		
		return user;
	}
	
	private static String sha256_HMAC(String message, String secret) {     
		String hash = "";      
		try {         
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");     
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");      
			sha256_HMAC.init(secret_key);       
			byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
			return Base64.encodeBase64String(bytes);
		} catch (Exception e) {     
		}    
		
		return hash;   
	}
	
	
	@RequestMapping("/apiGetAvailable")
	public String apiGetAvailable(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "login");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQMember user = (BQMember) map.get("user");
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		String symbol = request.getParameter("symbol");
		if (StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol errror");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQCurrencyType currency = bqCurrencyTypeService.getByTypeName(symbol);
		if (currency == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol not find");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQAccount bqa = bqAccountService.getAccount(user.getF_user_id(), currency.getType_id());
		BigDecimal btc = (bqa == null ? BigDecimal.ZERO : bqa.getBtc().setScale(4, RoundingMode.HALF_DOWN));
		tickerJson.put("data",  btc.doubleValue());
		
		model.addAttribute("ticker", tickerJson);
		return "jsp/api/ticker"; 
	}
	
	@RequestMapping("/apiTransferFrom")
	public String apiTransferFrom(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "login");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		
		String amtStr = request.getParameter("amount");
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(amtStr, 0.0)).setScale(8, RoundingMode.HALF_UP);
		String symbol = request.getParameter("symbol");
		if (amount.compareTo(BigDecimal.ZERO) <= 0 || StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "param error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String time = request.getParameter("time");
		if (StringUtil.isEmpty(time)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		long ltime = Convert.toLong(time, 0);
		if (Math.abs(System.currentTimeMillis() - ltime) > 60000) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String sign = request.getParameter("sign");
		if (StringUtil.isEmpty(sign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String params = "token=" + token + "&amount=" + amtStr + "&symbol=" + symbol + "&time=" + time;
		String checkSign = sha256_HMAC(params, SECRET);
		if (!sign.equals(checkSign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQCurrencyType coin = bqCurrencyTypeService.getByTypeName(symbol);
		if (coin == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQAccount account = bqAccountService.getAccount(user.getF_user_id(), coin.getType_id());
		if (account == null || account.getBtc().compareTo(amount) < 0) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "balance error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		JSONObject data = new JSONObject();
		String note = checkTransferFromRisk(user, coin);
		if (StringUtil.isEmpty(note)) {
			try {
				String transNo = transferAccountService.updateAccountToOTC(coin, user.getF_user_id(), amount);
				if (!StringUtil.isEmpty(transNo)) {
					data.put("transNo", transNo);
					tickerJson.put("data", data);
				}
			} catch (Exception e) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "update account error");
				model.addAttribute("ticker", tradesStr);
				return "jsp/api/ticker";
			}
		} else {
			// 提交记录，冻结金额
			BQTransferWithOtcRecord record = new BQTransferWithOtcRecord();
			record.setF_user_id(user.getF_user_id());
			record.setCurrency_type_id(coin.getType_id());
			record.setCurrency_name(coin.getType_name());
			record.setDirection(2);
			record.setAmount(amount);
			record.setFrozen_amount(amount);
			record.setState(Contents.TRANSFER_OTC_STATE_MANUAL);
			record.setNote(note);
			try {
				bqTransferWithOtcRecordService.saveRecord(record);
				data.put("note", note);
				data.put("recordId", record.getId());
				tickerJson.put("data", data);
			} catch (ServiceException e) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "Risk control user saving transfer out record error");
				model.addAttribute("ticker", tradesStr);
				return "jsp/api/ticker";
			}
		}
		
		model.addAttribute("ticker", tickerJson);
		return "jsp/api/ticker"; 
	}
	
	@RequestMapping("/apiTransferTo")
	public String apiTransferTo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "login");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		
		String amtStr = request.getParameter("amount");
		BigDecimal amount = new BigDecimal(ConvertUtil.toDouble(amtStr, 0.0)).setScale(8, RoundingMode.HALF_UP);
		String symbol = request.getParameter("symbol");
		if (amount.compareTo(BigDecimal.ZERO) <= 0 || StringUtil.isEmpty(symbol)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "param error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String time = request.getParameter("time");
		if (StringUtil.isEmpty(time)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		long ltime = Convert.toLong(time, 0);
		if (Math.abs(System.currentTimeMillis() - ltime) > 60000) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String sign = request.getParameter("sign");
		if (StringUtil.isEmpty(sign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String params = "token=" + token + "&amount=" + amtStr + "&symbol=" + symbol + "&time=" + time;
		String checkSign = sha256_HMAC(params, SECRET);
		if (!sign.equals(checkSign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}

		BQCurrencyType coin = bqCurrencyTypeService.getByTypeName(symbol);
		if (coin == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "symbol error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQAccount account = bqAccountService.getAccount(user.getF_user_id(), coin.getType_id());
		if (account == null) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "account error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		try {
			String transNo = transferAccountService.updateAccountFromOTC(coin, user.getF_user_id(), amount);
			if (!StringUtil.isEmpty(transNo)) {
				tickerJson.put("data", transNo);
			}
		} catch (Exception e) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "update account error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		model.addAttribute("ticker", tickerJson);
		
		return "jsp/api/ticker";  
	}
	
	@RequestMapping("/apiValidatePayPwd")
	public String apiValidatePayPwd(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		String checkToken = checkToken(request, map);
		if (!StringUtil.isEmpty(checkToken)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_RELOGIN, "login");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		BQMember user = (BQMember) map.get("user");
		String token = (String) map.get("token");
		
		String payPwd = request.getParameter("payPwd");
		if (StringUtil.isEmpty(payPwd)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "empty");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String time = request.getParameter("time");
		if (StringUtil.isEmpty(time)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		long ltime = Convert.toLong(time, 0);
		if (Math.abs(System.currentTimeMillis() - ltime) > 60000) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "time");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String sign = request.getParameter("sign");
		if (StringUtil.isEmpty(sign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		String params = "token=" + token + "&payPwd=" + payPwd + "&time=" + time;
		String checkSign = sha256_HMAC(params, SECRET);
		if (!sign.equals(checkSign)) {
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "sign error");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		}
		
		boolean bol_paypwd = false;
		// 判断用户的交易密码是否存在，如果存在，查找出来，否则不查
		BQMemberPayword payword = bqMemberPaywordService.readById(user.getF_user_id());
		if (payword != null && payword.getState() == 1) {
			
			if (payword.getFailed_times() > ALLOW_MAX_FAILED_TIMES) {
				String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "times");
				model.addAttribute("ticker", tradesStr);
				return "jsp/api/ticker";
			}
			
			if (!StringUtil.isEmpty(payPwd)) {
				try {
					payPwd = MD5Service.encryptString(payPwd).toLowerCase() + payword.getSalt().toLowerCase();
					payPwd = MD5Service.encryptString(payPwd).toLowerCase();
					if (payPwd.equals(payword.getPwd())) {
						bol_paypwd = true;
					}
				} catch (Exception e) {
					
				}
			}
		} else {
			bol_paypwd = true;
		}

		if (!bol_paypwd) {
			bqMemberPaywordService.addFailedTimes(user.getF_user_id());
//			user = super.findCurrUser(request, true);
			
			String tradesStr = createErrorCode(Contents.API_ERROR_PARAM, "invalid");
			model.addAttribute("ticker", tradesStr);
			return "jsp/api/ticker";
		} else {
			if (payword.getFailed_times() > 0) {
				bqMemberPaywordService.resetFailedTimes(user.getF_user_id());
//				user = super.findCurrUser(request, true);
			}
		}
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		
		model.addAttribute("ticker", tickerJson);
		return "jsp/api/ticker"; 
	}
	
	@RequestMapping("/changeTo")
	public String changeTo(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String language = request.getParameter("language");
		
		if (!StringUtil.isEmpty(language)) {
			request.getSession().removeAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		}
		
		JSONObject tickerJson = new JSONObject();
		tickerJson.put("code", 0);
		tickerJson.put("msg", "");
		model.addAttribute("ticker", tickerJson);
		
		return "jsp/api/ticker"; 
	}
	
	private String checkTransferFromRisk (BQMember user, BQCurrencyType coin) {
		String note = "";
		
		// 风控用户，转人工处理。
		BQUserRiskControl riskControl = bqUserRiskControlService.readOne(user.getF_user_id());
		if (riskControl != null) {
			note = "风控用户，转人工处理";
		} else {
			BalanceAccountVO allBalance = new BalanceAccountVO();
			BalanceAccountVO generalBalance = new BalanceAccountVO();
			List<BalanceAccountVO> contractBalances = new ArrayList<BalanceAccountVO>();
			List<BalanceAccountVO> deliveryContractBalances = new ArrayList<BalanceAccountVO>();
			try {
				bqMemberRechargeService.getBalanceAccount(user.getF_user_id(), coin.getType_id(), allBalance, generalBalance, contractBalances, deliveryContractBalances);
			} catch (ParseException e) {
				allBalance.setNetWorth(1000);
				e.printStackTrace();
			}
			
			double netWorth = allBalance.getNetWorth();
			if (coin.getType_id() == Contents.TRADE_CURRENCY_TYPE_ID || coin.getType_name().equalsIgnoreCase("usdr")) {
				if (Math.abs(netWorth) < 1) netWorth = 0;
			}
			
			if (Math.abs(netWorth) > 0) {
				// 净值不为0，转人工处理。
				note = "净值不为0，转人工处理。";
			} else if (generalBalance.getManualRecharge() > 0) {
				// 人工充值大于0，转人工处理。
				note = "人工充值大于0，转人工处理。";
			}
		}
		return note;
	}
	
	private String checkToken(HttpServletRequest request, Map<String, Object> map) {
		String token = request.getParameter("token");
		if (StringUtil.isEmpty(token)) {
			return "参数错误，token参数错误";
		}
		
		String verifyToken = JwtToken.verifyToken(token);
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
	
}
