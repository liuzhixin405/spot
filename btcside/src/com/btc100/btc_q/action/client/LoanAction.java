package com.btc100.btc_q.action.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.btc100.base.BaseAction;
import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQLoanRateSetting;
import com.btc100.btc_q.entity.BQLoanRecord;
import com.btc100.btc_q.entity.BQLoanSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQLoanAdjustRecordService;
import com.btc100.btc_q.service.BQLoanBackRecordServcie;
import com.btc100.btc_q.service.BQLoanCloseRecordService;
import com.btc100.btc_q.service.BQLoanRateSettingService;
import com.btc100.btc_q.service.BQLoanRecordService;
import com.btc100.btc_q.service.BQLoanSettingService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.Utils;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/financial")
public class LoanAction extends BaseAction {
	
	@Autowired
	private BQLoanSettingService bqLoanSettingService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQLoanRateSettingService bqLoanRateSettingService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private BQLoanRecordService bqLoanRecordService;
	@Autowired
	private BQLoanBackRecordServcie bqLoanBackRecordServcie;
	@Autowired
	private BQLoanAdjustRecordService bqLoanAdjustRecordService;
	@Autowired
	private BQLoanCloseRecordService bqLoanCloseRecordService;
	
	@RequestMapping("/pledge")
	public ModelAndView index(HttpServletRequest request, Model model) {
		request.setAttribute("locale", Utils.getLocale(request));

		BigDecimal balance = BigDecimal.ZERO;
		BQLoanSetting setting = bqLoanSettingService.readOne();
		if (setting == null) {
			setting = new BQLoanSetting();
			model.addAttribute("lastPrice", 0);
		} else {
			BigDecimal lastPrice = null;
			if (setting.getId() > 0) {
				String symbol = setting.getPledge_symbol().toLowerCase() + setting.getBorrow_symbol().toLowerCase();
				lastPrice = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.BTCLATESTPRICE);
				if (lastPrice == null) {
					BQSpotMarketCoinPair currentCoinPair = bqSpotMarketCoinPairService.readByName(symbol);
					if (currentCoinPair != null) {
						lastPrice = currentCoinPair.getLatest_price();
					}
				}
			}
			
			if (lastPrice == null) lastPrice = BigDecimal.ZERO;
			model.addAttribute("lastPrice", lastPrice.doubleValue());
			
			BQMember user = super.findCurrUser(request, false);
			if (user != null) {
				BQAccount account = bqAccountService.getAccount(user.getF_user_id(), setting.getPledge_coin_id());
				if (account != null) {
					balance = account.getBtc();
				}
				
				PagerModel pm = bqLoanRecordService.readAllByUserId(user.getF_user_id(), 0, 1);
				model.addAttribute("pm", pm);
				
				if (!StringUtil.isEmpty(setting.getBorrow_uids())) {
					String[] uids = setting.getBorrow_uids().split(",");
					List<String> uidList = new ArrayList<>();
					for (String s : uids) {
						uidList.add(s);
					}
					
					if (uidList.contains(user.getF_user_id() + "")) {
						model.addAttribute("isBorrow", true);
						
						BigDecimal nowBorrowOut = bqLoanRecordService.readSumBorrowOut(user.getF_user_id(), setting.getBorrow_coin_id(), 1);
						BigDecimal allBorrowOut = bqLoanRecordService.readSumBorrowOut(user.getF_user_id(), setting.getBorrow_coin_id(), 0);
						
						BigDecimal willIncome = bqLoanRecordService.readSumBorrowIncome(user.getF_user_id(), setting.getBorrow_coin_id(), 1);
						BigDecimal haveIncome = bqLoanRecordService.readSumBorrowIncome(user.getF_user_id(), setting.getBorrow_coin_id(), 0);
						
						BQAccount borrowAccount = bqAccountService.getAccount(user.getF_user_id(), setting.getBorrow_coin_id());
				
						model.addAttribute("nowBorrowOut", nowBorrowOut.setScale(8, BigDecimal.ROUND_DOWN));
						model.addAttribute("allBorrowOut", allBorrowOut.setScale(8, BigDecimal.ROUND_DOWN));
						model.addAttribute("willIncome", willIncome.setScale(8, BigDecimal.ROUND_DOWN));
						model.addAttribute("haveIncome", haveIncome.setScale(8, BigDecimal.ROUND_DOWN));
						model.addAttribute("borrowBalance", borrowAccount.getBtc());
					}
				}
				
				BigDecimal allBorrow = bqLoanRecordService.readSumBorrow(user.getF_user_id(), setting.getBorrow_coin_id(),  1);
				BigDecimal allPledge = bqLoanRecordService.readSumPledge(user.getF_user_id(), setting.getPledge_coin_id(), 1).multiply(lastPrice).setScale(8, BigDecimal.ROUND_DOWN);
				model.addAttribute("allBorrow", allBorrow);
				model.addAttribute("allPledge", allPledge);
				
			} else {
				model.addAttribute("pm", new PagerModel());
				model.addAttribute("allBorrow", "0.00000000");
				model.addAttribute("allPledge", "0.00000000");
			}
		}
		
		model.addAttribute("setting", setting);
		model.addAttribute("balance", balance);
		
		List<BQLoanRateSetting> rateList = bqLoanRateSettingService.readAllByLoanId(setting.getId());
		model.addAttribute("rateList", rateList);
		
		model.addAttribute("nowTime", System.currentTimeMillis()/1000);
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("jsp/loan_index");
		return mv;
	}
	
	@RequestMapping(value = "/adjustCoin", method = RequestMethod.POST)
	public String adjustCoin(HttpServletRequest request, Long recordId, BigDecimal amount, Model model) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("code", "success");
		jsonObj.put("msg", "");
		
		if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "请输入正确数量");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "请先登录");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		if (recordId == null) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "无此借币记录");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		BQLoanRecord record = bqLoanRecordService.readById(recordId);
		if (record == null || record.getF_user_id() != user.getF_user_id()) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "无此借币记录");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		if (record.getState() != 1) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "借币订单状态错误");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		BigDecimal lastPrice = null;
		String symbol = record.getPledge_symbol().toLowerCase() + record.getBorrow_symbol().toLowerCase();
		lastPrice = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.BTCLATESTPRICE);
		if (lastPrice == null) {
			BQSpotMarketCoinPair currentCoinPair = bqSpotMarketCoinPairService.readByName(symbol);
			if (currentCoinPair != null) {
				lastPrice = currentCoinPair.getLatest_price();
			}
		}
		
		if (lastPrice == null || lastPrice.compareTo(BigDecimal.ZERO) <= 0) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "获取价格指数失败！");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		if (amount.signum() < 0) {		// 减少质押金
			BigDecimal realPledge = record.getBorrow_amount().divide(lastPrice, 8, BigDecimal.ROUND_HALF_UP);
			BigDecimal nowRate = realPledge.divide(record.getPledge_amount().add(amount), 8, BigDecimal.ROUND_HALF_UP);
			if (nowRate.compareTo(record.getPledge_rate()) > 0) {
				jsonObj.put("code", "failed");
				jsonObj.put("msg", "质押率不能超过初始质押率");
				
				model.addAttribute("ticker", jsonObj);
				return "jsp/api/ticker";
			}
		}
		
		String lock = spyMemcachedClient.lock(BTCQMemecacheKeys.LOAN_LOCK + record.getId());
		if (!StringUtil.isEmpty(lock)) {
			try {
				bqLoanRecordService.adjustPledge(record, amount, record.getPledge_rate(), lastPrice);
			} catch (ServiceException e) {
				e.printStackTrace();
				
				jsonObj.put("code", "failed");
				jsonObj.put("msg", "调整质押金失败！");
				
				model.addAttribute("ticker", jsonObj);
				return "jsp/api/ticker";
			} finally {
				spyMemcachedClient.unlock(BTCQMemecacheKeys.LOAN_LOCK + record.getId(), lock);
			}
		}
		
		model.addAttribute("ticker", jsonObj);
		return "jsp/api/ticker";
	}
	
	
	
	@RequestMapping(value = "/backCoin", method = RequestMethod.POST)
	public String backCoin(HttpServletRequest request, Long recordId, Model model) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("code", "success");
		jsonObj.put("msg", "");
		
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "请先登录");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		if (recordId == null) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "无此借币记录");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		BQLoanRecord record = bqLoanRecordService.readById(recordId);
		if (record == null || record.getF_user_id() != user.getF_user_id()) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "无此借币记录");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		if (record.getState() != 1) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "借币订单状态错误");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		long nowTime = System.currentTimeMillis() / 1000;
		long backTime = record.getCycle() * 24 * 3600 + record.getRecord_time();
		if (nowTime < backTime) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "未到还款日期！");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		BigDecimal interest = record.getBorrow_amount().multiply(new BigDecimal(record.getCycle())).multiply(record.getDay_rate()).setScale(8, BigDecimal.ROUND_HALF_UP);
		BQAccount account = bqAccountService.readAccount(user.getF_user_id(), record.getBorrow_coin_id());
		if (account.getBtc().compareTo(interest.add(record.getBorrow_amount())) < 0) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "余额不足");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		String lock = spyMemcachedClient.lock(BTCQMemecacheKeys.LOAN_LOCK + record.getId());
		if (!StringUtil.isEmpty(lock)) {
			try {
				bqLoanRecordService.backCoin(record, null);
			} catch (ServiceException e) {
				e.printStackTrace();
				
				jsonObj.put("code", "failed");
				jsonObj.put("msg", "还币失败！");
				
				model.addAttribute("ticker", jsonObj);
				return "jsp/api/ticker";
			} finally {
				spyMemcachedClient.unlock(BTCQMemecacheKeys.LOAN_LOCK + record.getId(), lock);
			}
		}
		
		model.addAttribute("ticker", jsonObj);
		return "jsp/api/ticker";
	}
	
	
	@RequestMapping(value = "/pledgedo", method = RequestMethod.POST)
	public String pledgedo(HttpServletRequest request, BigDecimal borrow, int cycle, int loanId, Model model) throws JSONException {
		Contents.Local.LOCALE.set(getLocale(request));
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("code", "success");
		jsonObj.put("msg", "");
		
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "请先登录");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		if (borrow == null || borrow.compareTo(BigDecimal.ZERO) <= 0) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "请输入正确的借入数量");
			
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}

		if (cycle <= 0) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "无此借币期限");
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		BQLoanSetting setting = bqLoanSettingService.readOne(loanId);
		if (setting == null) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "无此借币信息");
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		if (setting.getMin_borrow_amount().signum() > 0 && borrow.compareTo(setting.getMin_borrow_amount()) < 0) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "起借数量" + setting.getMin_borrow_amount().doubleValue() + setting.getBorrow_symbol());
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		if (setting.getMax_borrow_amount().signum() > 0 && borrow.compareTo(setting.getMax_borrow_amount()) > 0) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "最大可借数量" + setting.getMax_borrow_amount().doubleValue() + setting.getBorrow_symbol());
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		BQLoanRateSetting rateSetting =  bqLoanRateSettingService.readLoanRateSettingByLoanIdAndCycle(loanId, cycle);
		if (rateSetting == null) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "无此借币期限");
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		BigDecimal lastPrice = null;
		if (setting.getId() > 0) {
			String symbol = setting.getPledge_symbol().toLowerCase() + setting.getBorrow_symbol().toLowerCase();
			lastPrice = spyMemcachedClient.get(symbol + BTCQMemecacheKeys.BTCLATESTPRICE);
			if (lastPrice == null) {
				BQSpotMarketCoinPair currentCoinPair = bqSpotMarketCoinPairService.readByName(symbol);
				if (currentCoinPair != null) {
					lastPrice = currentCoinPair.getLatest_price();
				}
			}
		}
		
		if (lastPrice == null || lastPrice.compareTo(BigDecimal.ZERO) <= 0) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "获取最新价失败");
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		BigDecimal balance = BigDecimal.ZERO;
		BQAccount account = bqAccountService.getAccount(user.getF_user_id(), setting.getPledge_coin_id());
		if (account != null) {
			balance = account.getBtc();
		}
		
		borrow = borrow.setScale(setting.getBorrow_digits(), BigDecimal.ROUND_DOWN);
		double pldg = borrow.doubleValue() / lastPrice.doubleValue() / setting.getInit_pledge_rate().doubleValue();
		BigDecimal pledge = new BigDecimal(pldg).setScale(8, BigDecimal.ROUND_HALF_UP);
		if (pledge.compareTo(balance) > 0) {
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "质押币种余额不足");
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		BigDecimal interest = borrow.multiply(new BigDecimal(cycle)).multiply(rateSetting.getDay_rate()).setScale(8, BigDecimal.ROUND_HALF_UP);
		
		BQLoanRecord record = new BQLoanRecord();
		record.setF_user_id(user.getF_user_id());
		record.setPledge_coin_id(setting.getPledge_coin_id());
		record.setPledge_symbol(setting.getPledge_symbol());
		record.setBorrow_coin_id(setting.getBorrow_coin_id());
		record.setBorrow_symbol(setting.getBorrow_symbol());
		record.setBorrow_amount(borrow);
		record.setPledge_amount(pledge);
		record.setPledge_rate(setting.getInit_pledge_rate());
		record.setDay_rate(rateSetting.getDay_rate());
		record.setFee_rate(setting.getFee_rate());
		record.setCycle(cycle);
		record.setInterest(interest);
		record.setReceived_amount(borrow.subtract(interest));
		
		try {
			bqLoanRecordService.saveRecord(record, setting);
		} catch (ServiceException e) {
			e.printStackTrace();
			
			jsonObj.put("code", "failed");
			jsonObj.put("msg", "借币失败，请稍后再试！");
			model.addAttribute("ticker", jsonObj);
			return "jsp/api/ticker";
		}
		
		model.addAttribute("ticker", jsonObj);
		return "jsp/api/ticker";
	}
	

	@RequestMapping("/orders")
	public ModelAndView orders(HttpServletRequest request, Model model) {
		request.setAttribute("locale", Utils.getLocale(request));
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		String type = request.getParameter("type");
		if (StringUtil.isEmpty(type)) type = "1";
		int itype = Convert.toInt(type, 1);
		
		model.addAttribute("type", itype);
		ModelAndView mv = new ModelAndView();
		if (itype == 1) {
			model.addAttribute("pm", bqLoanBackRecordServcie.readAllByUserId(user.getF_user_id(), 0));
			mv.setViewName("jsp/loan_repayment");
		} else if (itype == 2) {
			model.addAttribute("pm", bqLoanAdjustRecordService.readAllByUserId(user.getF_user_id()));
			mv.setViewName("jsp/loan_adjust");
		} else if (itype == 3) {
			model.addAttribute("pm", bqLoanCloseRecordService.readAllByUserId(user.getF_user_id(), 0));
			mv.setViewName("jsp/loan_close");
		} else {
			model.addAttribute("pm", new PagerModel());
			mv.setViewName("jsp/loan_repayment");
		}
		
		return mv;
	}
	
	@RequestMapping("/out/orders")
	public ModelAndView outOrders(HttpServletRequest request, Model model) {
		request.setAttribute("locale", Utils.getLocale(request));
		
		BQMember user = super.findCurrUser(request, false);
		if (user == null) {
			return new ModelAndView(super.toLogin());
		}
		
		BQLoanSetting setting = bqLoanSettingService.readOne();
		if (setting == null) {
			ModelAndView mv = new ModelAndView();
			mv.setViewName("jsp/loan_out_record");
			return mv;
		}
		
		if (!StringUtil.isEmpty(setting.getBorrow_uids())) {
			String[] uids = setting.getBorrow_uids().split(",");
			List<String> uidList = new ArrayList<>();
			for (String s : uids) {
				uidList.add(s);
			}
			
			if (!uidList.contains(user.getF_user_id() + "")) {
				ModelAndView mv = new ModelAndView();
				mv.setViewName("jsp/loan_out_record");
				return mv;
			}
		} else {
			ModelAndView mv = new ModelAndView();
			mv.setViewName("jsp/loan_out_record");
			return mv;
		}
		
		String type = request.getParameter("type");
		if (StringUtil.isEmpty(type)) type = "0";
		int itype = Convert.toInt(type, 0);
		
		model.addAttribute("type", itype);
		ModelAndView mv = new ModelAndView();
		if (itype == 0) {
			model.addAttribute("pm", bqLoanRecordService.readAll(user.getF_user_id(), 1));
			mv.setViewName("jsp/loan_out_record");
		} else if (itype == 1) {
			model.addAttribute("pm", bqLoanBackRecordServcie.readAllByUserId(0, user.getF_user_id()));
			mv.setViewName("jsp/loan_out_repayment");
		} else if (itype == 2) {
			model.addAttribute("pm", bqLoanCloseRecordService.readAllByUserId(0, user.getF_user_id()));
			mv.setViewName("jsp/loan_out_close");
		} else {
			model.addAttribute("pm", new PagerModel());
			mv.setViewName("jsp/loan_out_record");
		}
		
		return mv;
	}
	
}