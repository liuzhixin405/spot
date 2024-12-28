package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQArbitrageSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.service.BQArbitrageSettingService;
import com.btc100.btc_q.service.BQMemberInviteRebateLogService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;

@Controller
@RequestMapping("/managerTradeStat")
public class TradesStatAction extends BaseAction {
	
	@Autowired
	private BQTransactionService transactionService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private BQMemberInviteRebateLogService bqMemberInviteRebateLogService;

	@RequestMapping("/statsTrades")
	public String statsTrades(Model model, String code, String startTimeStr, String endTimeStr, String userName, String userId)
			throws Exception {
		List<BQTransaction> transactionList = transactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		model.addAttribute("lstTrans", transactionList);
		
		if (transactionList.size() > 0) {
			if (StringUtil.isEmpty(code)) {
				code = transactionList.get(0).getTransaction_code();
			}
		}
		if (StringUtil.isEmpty(code)) {
			return super.errorPage(model, "合约号错误");
		}
		
		long uId = ConvertUtil.toLong(userId, 0l);
		BQMember user = new BQMember();
		if (uId > 0) {
			user = bqMemberService.readUserById(uId);
		}
		if (!StringUtil.isEmpty(userName)) {
			user = bqMemberService.readUserByEmail(userName);
		}
		if (user == null) {
			return super.errorPage(model, "查不到该用户");
		}

		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (StringUtil.isEmpty(startTimeStr)) {
			startTime = (long) TimeUtil.getTimesmorning();
		} else {
			startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr));
		}
		if (StringUtil.isEmpty(endTimeStr)) {
			endTime = startTime + 86400L;
		} else {
			endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr));
		}
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
		}

		BQArbitrageSetting setting = bqArbitrageSettingService.readArbitrageSettingByCode(code);
		if (setting == null) {
			setting = new BQArbitrageSetting();
			setting.setDepth_uid(0L);
		}
		
		BigDecimal ddkcVol = tradeLogService.getVolByTimeAndTradeType(code, startTime, endTime,
				setting.getDepth_uid(), user.getF_user_id(), Contents.CZLX_DDKC);
		BigDecimal ddpcVol = tradeLogService.getVolByTimeAndTradeType(code, startTime, endTime,
				setting.getDepth_uid(), user.getF_user_id(), Contents.CZLX_DDPC);
		BigDecimal ktkcVol = tradeLogService.getVolByTimeAndTradeType(code, startTime, endTime,
				setting.getDepth_uid(), user.getF_user_id(), Contents.CZLX_KTKC);
		BigDecimal ktpcVol = tradeLogService.getVolByTimeAndTradeType(code, startTime, endTime,
				setting.getDepth_uid(), user.getF_user_id(), Contents.CZLX_KTPC);
		BigDecimal fee = tradeLogService.getFeeByTimeAndUser(code, startTime, endTime, setting.getDepth_uid(),
				user.getF_user_id());
		BigDecimal rebate = bqMemberInviteRebateLogService.readSumRebateByTimeAndUser("", code, startTime, endTime,
				user.getF_user_id(), Contents.TRADE_CURRENCY_TYPE_ID);
		fee = fee.subtract(rebate);
		model.addAttribute("current", code);
		model.addAttribute("startTime", startTimeStr);
		model.addAttribute("endTime", endTimeStr);
		model.addAttribute("mdVol", ddkcVol);
		model.addAttribute("pdVol", ddpcVol);
		model.addAttribute("mkVol", ktkcVol);
		model.addAttribute("pkVol", ktpcVol);
		model.addAttribute("fee", fee);
		model.addAttribute("rebate", rebate);
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("email", StringUtil.isEmpty(user.getF_user_email()) ? "" : user.getF_user_email());
		return super.url_JSP("statsTrades");
	}
}
