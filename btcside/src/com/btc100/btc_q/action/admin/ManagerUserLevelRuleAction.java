package com.btc100.btc_q.action.admin;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.service.BQBfxFeeSettingService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberLevelBuyAmountLogService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/managerUserLevelRule")
public class ManagerUserLevelRuleAction extends BaseAction {
	
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberLevelBuyAmountLogService bqMemberLevelBuyAmountLogService;

	
	@RequestMapping("/loadLevels")
	public String loadLevels(Model model) {
		PagerModel pm = bqMemberLevelService.readLevels();
		model.addAttribute("pm", pm);
		BQBfxFeeSetting setting = bqBfxFeeSettingService.readOne();
		if (setting == null) {
			return super.url_JSP("levelslist");
		}
		BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(setting.getCoin_id());
		model.addAttribute("coinName", currencyType == null ? "" : currencyType.getType_name());
		return super.url_JSP("levelslist");
	}
	
	/**
	 * 保存VIP规则
	 */
	@RequestMapping("/saveLevel")
	public ModelAndView saveLevel(BQMemberLevel level) {
		if (!StringUtil.isEmpty(level.getLevel_name()) && !StringUtil.isEmpty(level.getLevel_credits())
				&& !StringUtil.isEmpty(level.getEat_commission_rate())
				&& !StringUtil.isEmpty(level.getPending_commission_rate())
				&& !StringUtil.isEmpty(level.getWithdraws_cash())
				&& !StringUtil.isEmpty(level.getBroker_commission_rate())
				&& !StringUtil.isEmpty(level.getTransaction_commission_rate())
				&& !StringUtil.isEmpty(level.getLevel_buy_amount()) && !StringUtil.isEmpty(level.getState())) {
			bqMemberLevelService.save(level);
		}
		return new ModelAndView(super.redirect("/managerUserLevelRule/loadLevels.do"));
	}
	
	/**
	 * 修改VIP规则
	 */
	@RequestMapping("/toUpdateLevel")
	public String toUpdateLevel(Model model, String id) {
		long levelId = ConvertUtil.toLong(id, 0l);
		BQMemberLevel level = bqMemberLevelService.findByLevelId(levelId);
		model.addAttribute("level", level);
		BQBfxFeeSetting setting = bqBfxFeeSettingService.readOne();
		if (setting == null) {
			model.addAttribute("coinName", "");
			return super.url_JSP("levelEdit"); 
		}
		BQCurrencyType currencyType = setting == null ? null : bqCurrencyTypeService.readByTypeId(setting.getCoin_id());
		model.addAttribute("coinName", currencyType == null ? "" : currencyType.getType_name());
		return super.url_JSP("levelEdit");
	}
	
	@RequestMapping("/doUpdateLevel")
	public String doUpdateLevel(BQMemberLevel level) {
		if (!StringUtil.isEmpty(level.getLevel_name()) && !StringUtil.isEmpty(level.getLevel_credits())
				&& !StringUtil.isEmpty(level.getEat_commission_rate())
				&& !StringUtil.isEmpty(level.getPending_commission_rate())
				&& !StringUtil.isEmpty(level.getWithdraws_cash())
				&& !StringUtil.isEmpty(level.getBroker_commission_rate())
				&& !StringUtil.isEmpty(level.getTransaction_commission_rate())
				&& !StringUtil.isEmpty(level.getLevel_buy_amount()) && !StringUtil.isEmpty(level.getState())) {
			bqMemberLevelService.update(level);
		}
		return super.redirect("/managerUserLevelRule/loadLevels.do");
	}
	
	@RequestMapping("/deleteLevel")
	public ModelAndView deleteLevel(String id) {
		long levelId = ConvertUtil.toLong(id, 0l);
		BQMemberLevel level = bqMemberLevelService.findByLevelId(levelId);
		bqMemberLevelService.delete(level);
		return new ModelAndView(super.redirect("/managerUserLevelRule/loadLevels.do"));
	}
	
	@RequestMapping("/loadLevelBuyAmountLog")
	public String loadLevelBuyAmountLog(String uid, String email, Model model) {
		long userId = 0;
		if (!StringUtil.isEmpty(uid)) {
			userId = Convert.toLong(uid, 0);
		}
		
		if (userId <= 0 && !StringUtil.isEmpty(email)) {
			BQMember user = bqMemberService.readUserByEmail(email);
			if (null != user) {
				userId = user.getF_user_id();
			}
		}
		
		model.addAttribute("email", email);
		model.addAttribute("uid", uid);
		
		PagerModel pm = bqMemberLevelBuyAmountLogService.readAllForAdmin(userId);
		model.addAttribute("pm", pm);
		return super.url_JSP("userLevelBuyAmountLog");
	}
	
}
