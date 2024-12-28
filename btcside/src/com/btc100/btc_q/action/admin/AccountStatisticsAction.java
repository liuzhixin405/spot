package com.btc100.btc_q.action.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

/**
 * 财务管理
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping("/accountStatistics")
public class AccountStatisticsAction extends BaseAction {

	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	
	@RequestMapping("/statisticsData")
	public String statisticsData(HttpServletRequest request, String orderBy, String userName, String userId, String coinId, Model model) {
		int currencyId = ConvertUtil.toInt(coinId, Contents.TRADE_CURRENCY_TYPE_ID);
		long uId = ConvertUtil.toLong(userId, 0l);
		if (!StringUtil.isEmpty(userName)) {
			uId = bqMemberService.readUserByEmail(userName).getF_user_id();
		}
		
		BQAccount bqAccount = bqAccountService.readAccountStatistics(currencyId);
		List<BQCurrencyType> coins = bqCurrencyTypeService.readAll();
		PagerModel pm = bqAccountService.readStatistics(orderBy, uId, currencyId);
		model.addAttribute("allCoin", coins);
		model.addAttribute("bqAccount", bqAccount);
		model.addAttribute("pm", pm);
		model.addAttribute("orderBy", StringUtil.isEmpty(orderBy) ? "ALL" : orderBy);
		model.addAttribute("userId", uId);
		model.addAttribute("userName", userName);
		model.addAttribute("coinId", currencyId);

		return super.url_JSP("AccountStatistics");
	}

}
