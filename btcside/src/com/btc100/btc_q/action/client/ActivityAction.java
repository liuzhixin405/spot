package com.btc100.btc_q.action.client;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.VTradeWeekStats;
import com.btc100.btc_q.service.VTradeWeekStatsService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.VTradeStatsVo;

@Controller
@RequestMapping("/activity")
public class ActivityAction extends BaseAction {
	
	@Autowired
	private VTradeWeekStatsService vTradeWeekStatsService;
//	@Autowired
//	private VTradeDayStatsService vTradeDayStatsService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	@RequestMapping("ranking")
	public String getActivity(HttpServletRequest request,Model model) {
		Contents.Local.LOCALE.set(getLocale(request));

		BQMember user = super.findCurrUser(request, false);
		
		String dayStr = request.getParameter("day");
		String weekStr = request.getParameter("week");
		model.addAttribute("day", dayStr);
		model.addAttribute("week", weekStr);
		model.addAttribute("select", request.getParameter("select"));

//		List<VTradeStatsVo> dayVts = new ArrayList<VTradeStatsVo>();
//		if (StringUtil.isEmpty(dayStr) || "0".equals(dayStr)) {
//			dayVts = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRADE_DAY_RANKING);
//			if (dayVts == null) {
//				dayVts = new ArrayList<VTradeStatsVo>();
//			}
//		} else {
//			long time = Convert.toLong(dayStr, 0);
//			if (time > 0) {
//				List<VTradeStatsVo> vos = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRADE_DAY_RANKING + time);
//				if (vos != null) {
//					dayVts = vos;
//				} else {
//					List<VTradeDayStats> stats = vTradeDayStatsService.findAll(time, 10);
//					for (VTradeDayStats st : stats) {
//						VTradeStatsVo vo = new VTradeStatsVo();
//						vo.setUserId(st.getUser_id());
//						vo.setUserName(formatEmail(st.getUser_name()));
//						vo.setYieldRate(st.getYield_rate().doubleValue());
//						vo.setSumDepoist(st.getSum_depoist().doubleValue());
//						vo.setSumIncome(st.getSum_income().doubleValue());
//						
//						dayVts.add(vo);
//					}
//					
//					spyMemcachedClient.set(BTCQMemecacheKeys.V_TRADE_DAY_RANKING + time, BTCQMemecacheKeys.TIMES.FIVE_MIN, dayVts);
//				}
//			}
//		}
//		
//		int dayRank = 0;
//		if(user != null) {
//			for (int i = 0; i < dayVts.size(); i++) {
//				if (dayVts.get(i).getUserId() == user.getF_user_id()) {
//					dayRank = i + 1;
//					break;
//				} else {
//					dayRank = 0;
//				}
//			}
//		}
//		
//		List<VTradeStatsVo> halfOneDay = new ArrayList<VTradeStatsVo>();
//		List<VTradeStatsVo> halfTwoDay = new ArrayList<VTradeStatsVo>();
//		if (dayVts.size() <= 5) {
//			halfOneDay = dayVts.subList(0, dayVts.size());
//		} else if (dayVts.size() > 5 && dayVts.size() <= 10) {
//			halfOneDay = dayVts.subList(0, 5);
//			halfTwoDay = dayVts.subList(5, dayVts.size());
//		} else {
//			halfOneDay = dayVts.subList(0, 5);
//			halfTwoDay = dayVts.subList(5, 10);
//		}
//		
//		model.addAttribute("halfOneDay", halfOneDay);
//		model.addAttribute("halfTwoDay", halfTwoDay);
//		model.addAttribute("dayRank", dayRank);
		
		List<VTradeStatsVo> weekVts = new ArrayList<VTradeStatsVo>();
		if (StringUtil.isEmpty(weekStr) || "0".equals(weekStr)) {
			weekVts = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRADE_WEEK_RANKING);
			if (weekVts == null) {
				weekVts = new ArrayList<VTradeStatsVo>();
			}
		} else {
			long time = Convert.toLong(weekStr, 0);
			if (time > 0) {
				List<VTradeStatsVo> vos = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRADE_WEEK_RANKING + time);
				if (vos != null) {
					weekVts = vos;
				} else {
					List<VTradeWeekStats> stats = vTradeWeekStatsService.readAll(time, 10);
					for (VTradeWeekStats st : stats) {
						VTradeStatsVo vo = new VTradeStatsVo();
						vo.setUserId(st.getUser_id());
						vo.setUserName(formatEmail(st.getUser_name()));
						vo.setYieldRate(st.getYield_rate().doubleValue());
						vo.setSumDepoist(st.getSum_depoist().doubleValue());
						vo.setSumIncome(st.getSum_income().doubleValue());
						
						weekVts.add(vo);
					}
				}
				
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TRADE_WEEK_RANKING + time, BTCQMemecacheKeys.TIMES.FIVE_MIN, weekVts);
			}
		}
		
		int weekRank = 0;
		if(user != null) {
			for (int i = 0; i < weekVts.size(); i++) {
				if (weekVts.get(i).getUserId() == user.getF_user_id()) {
					weekRank = i + 1;
					break;
				} else {
					weekRank = 0;
				}
			}
		}
		
		List<VTradeStatsVo> halfOneWeek = new ArrayList<VTradeStatsVo>();
		List<VTradeStatsVo> halfTwoWeek = new ArrayList<VTradeStatsVo>();
		if (weekVts.size() <= 5) {
			halfOneWeek = weekVts.subList(0, weekVts.size());
		} else if (weekVts.size() > 5 && weekVts.size() <= 10) {
			halfOneWeek = weekVts.subList(0, 5);
			halfTwoWeek = weekVts.subList(5, weekVts.size());
		} else {
			halfOneWeek = weekVts.subList(0, 5);
			halfTwoWeek = weekVts.subList(5, 10);
		}
		
		model.addAttribute("halfOneWeek", halfOneWeek);
		model.addAttribute("halfTwoWeek", halfTwoWeek);
		model.addAttribute("weekRank", weekRank);
		
		List<VTradeStatsVo> monthVts = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRADE_MONTH_RANKING);
		if (monthVts == null) {
			monthVts = new ArrayList<VTradeStatsVo>();
		}
		
		int monthRank = 0;
		if(user != null) {
			for (int i = 0; i < monthVts.size(); i++) {
				if (monthVts.get(i).getUserId() == user.getF_user_id()) {
					monthRank = i + 1;
					break;
				} else {
					monthRank = 0;
				}
			}
		}
		
		List<VTradeStatsVo> halfOneMonth = new ArrayList<VTradeStatsVo>();
		List<VTradeStatsVo> halfTwoMonth = new ArrayList<VTradeStatsVo>();
		if (monthVts.size() <= 5) {
			halfOneMonth = monthVts.subList(0, monthVts.size());
		} else if (monthVts.size() > 5 && monthVts.size() <= 10) {
			halfOneMonth = monthVts.subList(0, 5);
			halfTwoMonth = monthVts.subList(5, monthVts.size());
		} else {
			halfOneMonth = monthVts.subList(0, 5);
			halfTwoMonth = monthVts.subList(5, 10);
		}
		
		model.addAttribute("halfOneMonth", halfOneMonth);
		model.addAttribute("halfTwoMonth", halfTwoMonth);
		model.addAttribute("monthRank", monthRank);
		
		return "jsp/lastActivity";
	}

}
