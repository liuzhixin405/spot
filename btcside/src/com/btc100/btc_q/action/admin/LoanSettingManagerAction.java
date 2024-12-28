package com.btc100.btc_q.action.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQLoanRateSetting;
import com.btc100.btc_q.entity.BQLoanSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQLoanRateSettingService;
import com.btc100.btc_q.service.BQLoanRecordService;
import com.btc100.btc_q.service.BQLoanSettingService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping(value = "/managerLoanSetting")
public class LoanSettingManagerAction extends BaseAction {

	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQLoanRateSettingService bqLoanRateSettingService;
	@Autowired
	private BQLoanSettingService bqLoanSettingService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQLoanRecordService bqLoanRecordService;
	

	@RequestMapping(value = "/loanSettingManager")
	public String loanSettingManager(Long id, Model mo) {
		List<BQCurrencyType> coins = bqCurrencyTypeService.readAll();
		mo.addAttribute("coins", coins);
		
		BQLoanSetting setting = null;
		if (id != null && id > 0) {
			setting = bqLoanSettingService.readOne(id);
		}
		if (setting == null) {
			setting = new BQLoanSetting();
		}
		mo.addAttribute("setting", setting);
		
		PagerModel pm = bqLoanSettingService.readAll();
		mo.addAttribute("pm", pm);
		
		return super.url_JSP("loanSettingManager");
	}
	
	@RequestMapping(value = "/updateLoanSetting", method = RequestMethod.POST)
	public String updateLoanSetting(BQLoanSetting setting, Model mo) {
		if (setting.getId() == null || setting.getId() == 0) {
			do {
				if (setting.getPledge_coin_id() == setting.getBorrow_coin_id()) {
					break;
				}
				
				BQCurrencyType pledgeCoin = bqCurrencyTypeService.readByTypeId(setting.getPledge_coin_id());
				BQCurrencyType borrowCoin = bqCurrencyTypeService.readByTypeId(setting.getBorrow_coin_id());
				if (pledgeCoin == null || borrowCoin == null) {
					break;
				}
				
				setting.setPledge_symbol(pledgeCoin.getType_name());
				setting.setBorrow_symbol(borrowCoin.getType_name());
				bqLoanSettingService.save(setting);
			} while (false);
		} else {
			bqLoanSettingService.update(setting);
		}
		
		return super.redirect("/managerLoanSetting/loanSettingManager.do");
	}
	
	@RequestMapping(value = "/loanRateSettingManager")
	public String loanRateSettingManager(Long id, Long loanId, Model mo) {
		List<BQLoanSetting> loanList = bqLoanSettingService.readAllList();
		mo.addAttribute("loanList", loanList);
		
		BQLoanRateSetting setting = null;
		if (id != null && id > 0) {
			setting = bqLoanRateSettingService.readById(id);
		}
		
		if ((loanId == null || loanId == 0) && loanList.size() > 0) {
			loanId = loanList.get(0).getId();
		}
		
		if (loanId > 0) {
			PagerModel pm = bqLoanRateSettingService.readPagerByLoanId(loanId);
			mo.addAttribute("pm", pm);
		} else {
			mo.addAttribute("pm", new PagerModel());
		}
		
		if (setting == null) {
			setting = new BQLoanRateSetting();
			setting.setLoan_setting_id(loanId);
		}

		mo.addAttribute("setting", setting);
		
		mo.addAttribute("loanId", loanId);
		return super.url_JSP("loanRateSettingManager");
	}
	
	@RequestMapping(value = "/updateLoanRateSetting", method = RequestMethod.POST)
	public String updateLoanRateSetting(BQLoanRateSetting setting, Model mo) {
		if (setting.getId() == null || setting.getId() == 0) {
			if (setting.getLoan_setting_id() > 0) {
				bqLoanRateSettingService.save(setting);
			}
		} else {
			bqLoanRateSettingService.update(setting);
		}
		
		return super.redirect("/managerLoanSetting/loanRateSettingManager.do?loanId=" + setting.getLoan_setting_id());
	}
	
	@RequestMapping(value = "/deleteLoanRateSetting")
	public String deleteLoanRateSetting(long id, long loanId, Model mo) {
		if (id > 0) {
			bqLoanRateSettingService.delete(id);
		}
		
		return super.redirect("/managerLoanSetting/loanRateSettingManager.do?loanId=" + loanId);
	}
	
	@RequestMapping(value = "/loanRecordManager")
	public String loanRecordManager(Long userId, String email, Long borrowId, Model mo) {
		
		BQMember member = null;
		if (userId != null && userId > 0) {
			member = bqMemberService.readUserById(userId);
		}
		
		if (member == null && !StringUtil.isEmpty(email)) {
			member = bqMemberService.readUserByEmail(email);
		}
		
		if (member != null) {
			userId = member.getF_user_id();
			email = member.getF_user_email();
		}
		mo.addAttribute("userId", userId);
		mo.addAttribute("email", email);
		mo.addAttribute("borrowId", borrowId);
		
		if (borrowId == null) borrowId = 0L;
		
		if (member != null) {
			PagerModel pm = bqLoanRecordService.readAllByUserId(member.getF_user_id(), 0, 0);
			mo.addAttribute("pm",pm);
		} else {
			PagerModel pm = bqLoanRecordService.readAll(borrowId, 0);
			mo.addAttribute("pm",pm);
		}
		
		return super.url_JSP("loanRecordManager");
	}
	
	
}
