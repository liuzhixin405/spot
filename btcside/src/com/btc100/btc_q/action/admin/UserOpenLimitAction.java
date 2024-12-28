package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQUserOpenLimit;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQUserOpenLimitService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping("/managerUserOpenLimit")
public class UserOpenLimitAction extends BaseAction {
	
	@Autowired
	private BQUserOpenLimitService bqUserOpenLimitService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	@RequestMapping("/list")
	public String load(String code, String userId, String uemail, String msg, Model mo) {
		BQTransaction currTrans = null;
		List<BQTransaction> transList = findCurrTrans();
		mo.addAttribute("lstTrans", transList);
		if (!StringUtil.isEmpty(code)) {
			for (BQTransaction trans : transList) {
				if (trans.getTransaction_code().equals(code)) {
					currTrans = trans;
					break;
				}
			}
		}

		if (currTrans == null && transList.size() > 0) {
			currTrans = transList.get(0);
		}

		if (currTrans == null) {
			PagerModel pm = new PagerModel();
			mo.addAttribute("pm", pm);
			return super.url_JSP("userOpenLimitList");
		}

		code = currTrans.getTransaction_code();
		mo.addAttribute("current", code);
		long id = Convert.toLong(userId, 0);
		if (id > 0) {
			mo.addAttribute("userId", userId);
			mo.addAttribute("uid", userId);
		}
		
		if (id == 0 && !StringUtil.isEmpty(uemail)) {
			mo.addAttribute("uemail", uemail);
			BQMember user = bqMemberService.findUserByEmail(uemail);
			if (user != null) {
				id = user.getF_user_id();
				mo.addAttribute("uid", id);
			} else {
				PagerModel pm = new PagerModel();
				mo.addAttribute("pm", pm);
				return super.url_JSP("userOpenLimitList");
			}
		}
		
		PagerModel pm = bqUserOpenLimitService.read(code, id);
		mo.addAttribute("pm", pm);
		
		return super.url_JSP("userOpenLimitList");
	}
	
	@RequestMapping("/toAddOrUpdate")
	public String toAddOrUpdate(String code, String id, Model mo) {
		BQUserOpenLimit limit = null;
		long recordId = Convert.toLong(id, 0);
		if (recordId > 0) {
			limit = bqUserOpenLimitService.readById(recordId);
		}
		if (limit != null) {
			mo.addAttribute("limit", limit);
			return super.url_JSP("userOpenLimitEdit");
		}
		
		BQTransaction currTrans = null;
		List<BQTransaction> transList = findCurrTrans();
		mo.addAttribute("lstTrans", transList);
		if (!StringUtil.isEmpty(code)) {
			for (BQTransaction trans : transList) {
				if (trans.getTransaction_code().equals(code)) {
					currTrans = trans;
				}
			}
		}

		if (currTrans == null && transList.size() > 0) {
			currTrans = transList.get(0);
		}

		if (currTrans == null) {
			return super.redirect("list.do");
		}
		mo.addAttribute("current", currTrans.getTransaction_code());

		return super.url_JSP("userOpenLimitEdit");
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(HttpServletRequest request, Model mo) {
		String id = request.getParameter("id");
		String f_user_id = request.getParameter("f_user_id");
		String code = request.getParameter("f_code");
		String max_kdc_nums = request.getParameter("max_kdc_nums");
		String max_kkc_nums = request.getParameter("max_kkc_nums");
		
		if (StringUtil.isEmpty(f_user_id) || StringUtil.isEmpty(code) || StringUtil.isEmpty(max_kdc_nums)
				|| StringUtil.isEmpty(max_kkc_nums)) {
			return super.errorPage(mo, "缺少参数");
		}
		
		long recordId = Convert.toLong(id, 0);
		if (recordId > 0) {
			BQUserOpenLimit limit = bqUserOpenLimitService.readById(recordId);
			if (limit != null) {
				limit.setMax_kdc_nums(new BigDecimal(max_kdc_nums));
				limit.setMax_kkc_nums(new BigDecimal(max_kkc_nums));
				bqUserOpenLimitService.update(limit);
			}
		} else {
			long userId = Convert.toLong(f_user_id, 0);
			BQMember user = bqMemberService.findUserById(userId);
			if (user != null) {
				BQUserOpenLimit limit = new BQUserOpenLimit();
				limit.setF_user_id(userId);
				limit.setF_code(code);
				limit.setMax_kdc_nums(new BigDecimal(max_kdc_nums));
				limit.setMax_kkc_nums(new BigDecimal(max_kkc_nums));
				limit.setAdd_time(System.currentTimeMillis() / 1000);
				bqUserOpenLimitService.save(limit);
			} else {
				return super.errorPage(mo, "没有该用户");
			}
		}
		List<BQUserOpenLimit> list = bqUserOpenLimitService.readAll();
		spyMemcachedClient.set(BTCQMemecacheKeys.TRADE_OPEN_LIMTI_LIST, BTCQMemecacheKeys.TIMES.FIVE_MIN, list);
		
		return super.redirect("list.do?code=" + code + "&userId=" + f_user_id);
	}
	
	@RequestMapping(value = "/delete")
	public String delete(String id, Model mo) {
		long recordId = Convert.toLong(id, 0);
		if (recordId > 0) {
			BQUserOpenLimit limit = bqUserOpenLimitService.readById(recordId);
			if (limit != null) {
				bqUserOpenLimitService.delete(limit);
			}
		}
		
		return super.redirect("list.do");
	}
}
