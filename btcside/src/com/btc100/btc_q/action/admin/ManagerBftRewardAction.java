package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQBftRewardRecord;
import com.btc100.btc_q.entity.BQBftRewardSetting;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQAccountService;
import com.btc100.btc_q.service.BQBftRewardRecordService;
import com.btc100.btc_q.service.BQBftRewardSettingService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.AjaxCheckVO;
import com.btc100.btc_q.vo.PagerModel;
import com.google.gson.Gson;

@Controller
@RequestMapping(value = "/managerBftReward")
public class ManagerBftRewardAction extends BaseAction  {
	
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQBftRewardSettingService bqBftRewardSettingService;
	@Autowired
	private BQBftRewardRecordService bqBftRewardRecordService;
	@Autowired
	private BQAccountService bqAccountService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	
	@RequestMapping(value = "/bftRewardManager")
	public String bftRewardManager(Model mo) {
		BQBftRewardSetting setting = bqBftRewardSettingService.get();
		
		if (setting == null) {
			setting = new BQBftRewardSetting();
		}
		
		mo.addAttribute("setting", setting);
		return super.url_JSP("bftRewardManager");
	}
	
	@RequestMapping(value = "/updateBftRewardSetting", method = RequestMethod.POST)
	public ModelAndView updateBftRewardSetting(BQBftRewardSetting setting, Model mo) throws ParseException {
		if (setting != null && setting.getCurrency_type_id() != 0) {
			if (setting.getId() == 0) {
				bqBftRewardSettingService.save(setting);
			} else {
				bqBftRewardSettingService.update(setting);
			}
		}
		
		return new ModelAndView(super.redirect("/managerBftReward/bftRewardManager.do"));
	}
	
	@RequestMapping(value = "/rewardRecords")
	public String mineRecords(Long userId, String email, Long startTime, Long endTime, Model mo) {
		long uid = 0;
		if (userId != null) uid = userId;
		
		long start = 0;
		if (startTime != null) start = startTime;
		
		long end = 0;
		if (endTime != null) end = endTime;
		
		
		if (uid == 0 && !StringUtil.isEmpty(email)) {
			BQMember member = bqMemberService.readUserByEmail(email);
			if (member != null) {
				uid = member.getF_user_id();
			}
		}
		
		if (uid != 0) {
			mo.addAttribute("userId", uid);
		}
		
		mo.addAttribute("email", email);
		
		if (start != 0) {
			mo.addAttribute("startTime", start);
		}
		
		if (end != 0) {
			mo.addAttribute("endTime", end);
		}
		
		PagerModel pm = bqBftRewardRecordService.findRecords(uid, start, end);
		mo.addAttribute("pm", pm);
		
		return super.url_JSP("bftRewardRecords");
	}
	
	@RequestMapping(value = "/bfxReward", method = RequestMethod.POST)
	public void bfxReward(HttpServletRequest request, Model mo, HttpServletResponse response) throws ParseException, ServiceException {
		BQBftRewardSetting setting = bqBftRewardSettingService.readOne();
		if (setting != null && !setting.getBfx_rewarded()) {
			if (!bqBftRewardSettingService.bfxRewarded(setting.getId())) {
				AjaxCheckVO checkVO = new AjaxCheckVO();
				checkVO.setCode("error");
				checkVO.setMsg("已赠送！！");
				
				Gson gson = new Gson();
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			// 根据bfx赠送bft
			BQCurrencyType type = bqCurrencyTypeService.readByTypeName("BFX");
			List<BQAccount> accounts = bqAccountService.readAboveZero(type.getType_id());
			for (BQAccount account : accounts) {
				BQBftRewardRecord record = new BQBftRewardRecord();
				record.setF_user_id(account.getF_user_id());
				record.setCurrency_type_id(setting.getCurrency_type_id());
				record.setReason(Contents.REWARD_REASON_BFX);
				record.setAmount(account.getBtc().add(account.getBtc_locked()));
				record.setNote("BFX余额赠送等量BFC");
				bqBftRewardRecordService.bonus(record);
			}
		}
		
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("success");
		
		Gson gson = new Gson();
		toWeb(response, gson.toJson(checkVO));
	}
	
	@RequestMapping(value = "/oldRegisterReward", method = RequestMethod.POST)
	public void oldRegisterReward(HttpServletRequest request, Model mo, HttpServletResponse response) throws ParseException, ServiceException {
		BQBftRewardSetting setting = bqBftRewardSettingService.readOne();
		if (setting != null && !setting.getOld_register_rewarded() && setting.getRegister_reward().compareTo(BigDecimal.ZERO) > 0) {
			if (!bqBftRewardSettingService.oldRegisterRewarded(setting.getId())) {
				AjaxCheckVO checkVO = new AjaxCheckVO();
				checkVO.setCode("error");
				checkVO.setMsg("已赠送！！");
				
				Gson gson = new Gson();
				toWeb(response, gson.toJson(checkVO));
				return;
			}
			
			// 老用户赠送
			List<Integer> uids = bqAccountService.readAllUserIds(setting.getCurrency_type_id());
			for (int i = 0; i < uids.size(); i++) {
				System.out.println(i);
				
				if (i > 0 && i % 50 == 0) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				Integer uid = uids.get(i);
				BQBftRewardRecord record = new BQBftRewardRecord();
				record.setF_user_id(uid);
				record.setCurrency_type_id(setting.getCurrency_type_id());
				record.setReason(Contents.REWARD_REASON_OLD_REGISTER);
				record.setAmount(setting.getRegister_reward());
				record.setNote("注册用户赠送BFC");
				bqBftRewardRecordService.bonus(record);
				
			}
		}
		
		AjaxCheckVO checkVO = new AjaxCheckVO();
		checkVO.setCode("success");
		
		Gson gson = new Gson();
		toWeb(response, gson.toJson(checkVO));
	}
}
