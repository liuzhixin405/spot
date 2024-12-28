package com.btc100.btc_q.action.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.Convert;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQBfxVipSetting;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.service.BQBfxFeeRecordService;
import com.btc100.btc_q.service.BQBfxFeeSettingService;
import com.btc100.btc_q.service.BQBfxVipSettingService;
import com.btc100.btc_q.service.BQMemberLevelService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping(value = "/managerBfx")
public class BfxManagerAction extends BaseAction {

	@Autowired
	private BQBfxVipSettingService bqBfxVipSettingService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;
	@Autowired
	private BQMemberLevelService bqMemberLevelService;
	@Autowired
	private BQBfxFeeRecordService bqBfxFeeRecordService;
	
	@RequestMapping(value = "/records")
	public String records(Long userId, Model mo) {
		if (userId == null) userId = 0L;
		
		PagerModel pm = bqBfxFeeRecordService.readRecords(userId, 0, 0);
		mo.addAttribute("pm", pm);
		
		if (userId > 0) {
			mo.addAttribute("userId", userId);
		}
		
		return super.url_JSP("bfxFeeList");
	}
	
	@RequestMapping(value = "/bfxVipManager")
	public String bfxVipList(String id, Model mo) {
		List<BQBfxVipSetting> settings = bqBfxVipSettingService.readAll();
		mo.addAttribute("settings", settings);
		
		BQBfxVipSetting setting = new BQBfxVipSetting();
		if (!StringUtil.isEmpty(id)) {
			long settingId = Convert.toLong(id, 0);
			if (settingId > 0) {
				setting = bqBfxVipSettingService.getBfxVipSettingById(settingId);
			}
		}
		
		mo.addAttribute("setting", setting);
		
		List<BQMemberLevel> levels = bqMemberLevelService.findAll();
		mo.addAttribute("levels", levels);
		
		return super.url_JSP("bfxVipManager");
	}
	
	@RequestMapping(value = "/updateBfxVipSetting", method = RequestMethod.POST)
	public String updateBfxVipSetting(BQBfxVipSetting setting, Model mo) {
		updateBfxVipSetting(setting);
		return super.redirect("/managerBfx/bfxVipManager.do");
	}
	
	@RequestMapping(value = "/deleteBfxVip")
	public String deleteBfxVip(Long id, Model mo) {
		if (id != null && id > 0) {
			bqBfxVipSettingService.delete(id);
		}
		
		return super.redirect("/managerBfx/bfxVipManager.do");
	}

	private void updateBfxVipSetting(BQBfxVipSetting setting) {
		if (setting == null) return;
		
		BQMemberLevel level = bqMemberLevelService.findByLevelId(setting.getLevel_id());
		if (level == null) {
			return;
		}
		
		setting.setLevel_name(level.getLevel_name());
			
		if (setting.getId() == null) {
			bqBfxVipSettingService.save(setting);
		} else {
			bqBfxVipSettingService.update(setting);
		}
	}

	@RequestMapping(value = "/bfxFeeManager")
	public String bfxFeeManager(Model mo) {
		BQBfxFeeSetting setting = bqBfxFeeSettingService.readOne();
		if (setting == null) {
			setting = new BQBfxFeeSetting();
		}
		
		mo.addAttribute("setting", setting);
		return super.url_JSP("bfxFeeManager");
	}
	
	@RequestMapping(value = "/updateBfxFeeSetting", method = RequestMethod.POST)
	public String updateBfxFeeSetting(BQBfxFeeSetting setting, Model mo) {
		updateBfxFeeSetting(setting);
		return super.redirect("/managerBfx/bfxFeeManager.do");
	}

	private void updateBfxFeeSetting(BQBfxFeeSetting setting) {
		if (setting == null) return;
		
		if (setting.getId() == null) {
			bqBfxFeeSettingService.save(setting);
		} else {
			bqBfxFeeSettingService.update(setting);
		}
	}
}
