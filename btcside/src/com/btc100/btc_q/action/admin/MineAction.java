package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;

import com.btc100.btc_q.service.BQMinePersonalAmountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.entity.BQBonusSetting;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQIeoAccount;
import com.btc100.btc_q.entity.BQIeoRechargeRecord;
import com.btc100.btc_q.entity.BQIeoUnlockSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMineSetting;
import com.btc100.btc_q.entity.BQMineStageSetting;
import com.btc100.btc_q.service.BQBonusRecordService;
import com.btc100.btc_q.service.BQBonusSettingService;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQIeoAccountService;
import com.btc100.btc_q.service.BQIeoRechargeRecordService;
import com.btc100.btc_q.service.BQIeoUnlockRecordService;
import com.btc100.btc_q.service.BQIeoUnlockSettingService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMineRecordService;
import com.btc100.btc_q.service.BQMineSettingService;
import com.btc100.btc_q.service.BQMineStageSettingService;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping(value = "/managerMine")
public class MineAction extends BaseAction {
	
	@Autowired
	BQMineStageSettingService bqMineStageSettingService;
	
	@Autowired
	BQMineSettingService bqMineSettingService;
	
	@Autowired
	BQMineRecordService bqMineRecordService;
	
	@Autowired
	BQMemberService bqMemberService;
	
	@Autowired
	BQIeoRechargeRecordService bqIeoRechargeRecordService;
	
	@Autowired
	BQIeoUnlockRecordService bqIeoUnlockRecordService;
	
	@Autowired
	BQIeoAccountService bqIeoAccountService;
	
	@Autowired
	BQCurrencyTypeService bqCurrencyTypeService;
	
	@Autowired
	BQBonusRecordService bqBonusRecordService;
	
	@Autowired
	BQBonusSettingService bqBonusSettingService;
	
	@Autowired
	BQIeoUnlockSettingService bqIeoUnlockSettingService;
	@Autowired
	BQMinePersonalAmountService bqMinePersonalAmountService;

	@RequestMapping(value = "/mineManager")
	public String mineManager(String code, Model mo) {
		PagerModel pm = bqMineStageSettingService.readAllStages();
		mo.addAttribute("pm", pm);
		
		BQMineSetting setting = bqMineSettingService.get();
		if (setting == null) {
			setting = new BQMineSetting();
		}
		
		mo.addAttribute("setting", setting);
		return super.url_JSP("mineManager");
	}
	
	@RequestMapping(value = "/ieoSettingManager")
	public String ieoSettingManager(String id, Model mo) {
		PagerModel pm = bqIeoUnlockSettingService.readAll();
		mo.addAttribute("pm", pm);
		
		BQIeoUnlockSetting setting = new BQIeoUnlockSetting();
		if (!StringUtil.isEmpty(id) && Long.valueOf(id) > 0) {
			setting = bqIeoUnlockSettingService.readById(Long.valueOf(id));
		}
		
		mo.addAttribute("setting", setting);
		return super.url_JSP("ieoSettingManager");
	}
	
	@RequestMapping(value = "/updateIeoSetting")
	public String updateIeoSetting(BQIeoUnlockSetting setting, String unlockTime, String beginTime, String endTime, Model mo) throws ParseException {
		if (unlockTime != null) {
			DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
			long starttime = StringUtil.isEmpty(unlockTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(unlockTime));
			setting.setUnlock_time(starttime);
		}
		
		if (beginTime != null) {
			DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
			long time = StringUtil.isEmpty(beginTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(beginTime));
			setting.setLock_time_begin(time);
		}
		
		
		if (endTime != null) {
			DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
			long time = StringUtil.isEmpty(endTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(endTime));
			setting.setLock_time_end(time);
		}
		
		
		if (setting.getId() == 0) {
			bqIeoUnlockSettingService.save(setting);
		} else {
			bqIeoUnlockSettingService.update(setting);
		}
		
		return super.redirect("/managerMine/ieoSettingManager.do");
	}
	
	@RequestMapping(value = "/deleteIeoSetting")
	public String deleteIeoSetting(Long id, Model mo) throws ParseException {
		
		if (id != null && id > 0) {
			bqIeoUnlockSettingService.delete(id);
		}
		
		return super.redirect("/managerMine/ieoSettingManager.do");
	}
	
	private void updateMineSetting(BQMineSetting setting) {
		if (setting == null) return;
		
		if (setting.getCurrency_type_id() == 0) {
			return;
		}
		
		if (setting.getId() == 0) {
			bqMineSettingService.save(setting);
		} else {
			bqMineSettingService.update(setting);
		}
	}
	
	@RequestMapping(value = "/updateMineSetting", method = RequestMethod.POST)
	public ModelAndView updateDepthSetting(BQMineSetting setting, String beginTime, Model mo) throws ParseException {
		if (beginTime != null) {
			DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long starttime = StringUtil.isEmpty(beginTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(beginTime));
			setting.setBegin_time(starttime);
		}
		updateMineSetting(setting);
		return new ModelAndView(super.redirect("/managerMine/mineManager.do"));
	}
	
	@RequestMapping(value = "/getStage")
	public String getStage(int id, Model mo) {
		BQMineStageSetting setting = bqMineStageSettingService.readMineStageSettingById(id);
		if (setting == null) {
			setting = new BQMineStageSetting();
		}
		
		mo.addAttribute("setting", setting);
		
		return super.url_JSP("minStageSettingedit");
	}
	
	@RequestMapping(value = "/updateMineStageSetting", method = RequestMethod.POST)
	public ModelAndView updateMineStageSetting(BQMineStageSetting setting, Model mo) {
		if (setting != null) {
			if (setting.getstage_amount().compareTo(BigDecimal.ZERO) < 0
					|| setting.getRate().compareTo(BigDecimal.ZERO) < 0) {
				return new ModelAndView(super.redirect("/managerMine/mineManager.do"));
			}
			
			if (setting.getId() == 0) {
				bqMineStageSettingService.save(setting);
			} else {
				BQMineStageSetting ishaveSetting = bqMineStageSettingService.getNextSetting(setting.getNum());
				if (ishaveSetting != null) {
					if (ishaveSetting.getId() == setting.getId()) {
						bqMineStageSettingService.updateOutNum(setting);
					}
				} else {
					bqMineStageSettingService.update(setting);
				}

			}
		}
		
		return new ModelAndView(super.redirect("/managerMine/mineManager.do"));
	}

	@RequestMapping(value = "/deleteMineStageSetting")
	public ModelAndView deleteMineStageSetting(long id) {
		BQMineStageSetting setting = bqMineStageSettingService.readMineStageSettingById(id);
		if (setting != null) {
			bqMineStageSettingService.deleteById(id);
		}

		return new ModelAndView(super.redirect("/managerMine/mineManager.do"));
	}

	/**
	 * 挖矿记录
	 * @param userId 用户id
	 * @param email 邮箱
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param tradeMode 交易模式：0-全部，1-永续合约，2-现货
	 * @param mo
	 * @return
	 */
	@RequestMapping(value = "/mineRecords")
	public String mineRecords(Long userId, String email, Long startTime, Long endTime, Integer tradeMode, Model mo) {
		long uid = 0;
		if (userId != null) uid = userId;
		
		long start = 0;
		if (startTime != null) start = startTime;
		
		long end = 0;
		if (endTime != null) end = endTime;

		int mode = 0;
		if (tradeMode != null) {
			mode = tradeMode;
		}

		if (uid == 0 && !StringUtil.isEmpty(email)) {
			BQMember member = bqMemberService.readUserByEmail(email);
			if (member != null) {
				uid = member.getF_user_id();
			}
		}
		
		if (uid != 0) {
			mo.addAttribute("userId", uid);
			BigDecimal minedAmount = bqMinePersonalAmountService.getMinedAmountByUid(uid);
			mo.addAttribute("minedAmount", minedAmount);
		}
		
		mo.addAttribute("email", email);
		
		if (start != 0) {
			mo.addAttribute("startTime", start);
		}
		
		if (end != 0) {
			mo.addAttribute("endTime", end);
		}

		mo.addAttribute("mode", mode);
		
		PagerModel pm = bqMineRecordService.readRecords(uid, mode, start, end);
		mo.addAttribute("pm", pm);
		
		return super.url_JSP("mineRecords");
	}
	
	@RequestMapping(value = "/ieoAccounts")
	public String ieoAccounts(Long userId, String email, String order, Model mo) {
		long uid = 0;
		if (userId != null) uid = userId;
		
		if (uid == 0 && !StringUtil.isEmpty(email)) {
			BQMember member = bqMemberService.readUserByEmail(email);
			if (member != null) {
				uid = member.getF_user_id();
			}
		}
		
		if (uid != 0) {
			mo.addAttribute("userId", uid);
		} else {
			BQIeoAccount account = bqIeoAccountService.sumAll();
			mo.addAttribute("initial", account.getInitial());
			mo.addAttribute("locked", account.getLocked());
			mo.addAttribute("unlocked", account.getUnlocked());
		}
		
		PagerModel pm = bqIeoAccountService.findByUserId(uid, order);
		mo.addAttribute("pm", pm);
		
		return super.url_JSP("ieoAccountlist");
	}
	
	@RequestMapping(value = "/ieoRecords")
	public String ieoRecords(Long userId, String email, Long id, Model mo) {
		long uid = 0;
		if (userId != null) uid = userId;
		
		if (uid == 0 && !StringUtil.isEmpty(email)) {
			BQMember member = bqMemberService.readUserByEmail(email);
			if (member != null) {
				uid = member.getF_user_id();
			}
		}
		
		if (id != null && id > 0) {
			BQIeoRechargeRecord record = bqIeoRechargeRecordService.readRecord(id);
			if (record != null) {
				mo.addAttribute("recordId", record.getId());
				uid = record.getF_user_id();
				BQMember member = bqMemberService.readUserById(uid);
				if (member != null) {
					email = member.getF_user_email();
				}
			}
		}
		
		if (uid != 0) {
			mo.addAttribute("userId", uid);
		}
		
		mo.addAttribute("email", email);
		
		BQMineSetting setting = bqMineSettingService.read();
		if (setting != null) {
			BQCurrencyType currencyType = bqCurrencyTypeService.readByTypeId(setting.getCurrency_type_id());
			mo.addAttribute("currencyName", currencyType.getType_name());
		}
		
		PagerModel pm = bqIeoRechargeRecordService.findRecords(uid);
		mo.addAttribute("pm", pm);
		
		return super.url_JSP("ieoRechargelist");
	}
	
	@RequestMapping(value = "/ieoRecharge", method = RequestMethod.POST)
	public ModelAndView ieoRecharge(HttpServletRequest request, String email, Long id, int type, BigDecimal amt, String surepwd, String note, Model mo)
			throws Exception {
		
		BQMineSetting setting = bqMineSettingService.read();
		if (setting == null) {
			return new ModelAndView(super.errorPage(mo, "未配置挖矿币种"));
		}
		
		if (!super.checkYZPwd(request, surepwd)) {
			return new ModelAndView(super.errorPage(mo, "安全密码不正确"));
		}

		if (amt.doubleValue() <= 0) {
			if (type == Contents.RECHARGE_TYPE_CZ) {
				return new ModelAndView(super.errorPage(mo, "转入数量不正确"));
			} else if (type == Contents.RECHARGE_TYPE_TX) {
				return new ModelAndView(super.errorPage(mo, "转出数量不正确"));
			}
		}

		BQMember bqMember = super.findUsersByEmail(email);
		if (bqMember == null) {
			return new ModelAndView(super.errorPage(mo, "不存在该用户"));
		}

		Long uid = bqMember.getF_user_id();
		if (id == null || id == 0) {
			if (type == Contents.RECHARGE_TYPE_CZ) {
				bqIeoAccountService.recharge(uid, setting.getCurrency_type_id(), 1, amt, note);
			} else if (type == Contents.RECHARGE_TYPE_TX) {
				bqIeoAccountService.recharge(uid, setting.getCurrency_type_id(), 2, amt.negate(), note);
			}
		} else {
			BigDecimal amount = amt;
			if (type == Contents.RECHARGE_TYPE_TX) {
				amount = amt.negate();
			}
			
			bqIeoAccountService.changeRecord(id, amount, note);
		}
		
		return new ModelAndView(super.redirect("/managerMine/ieoRecords.do"));
	}
	
	@RequestMapping(value = "/ieoUnlockRecords")
	public String ieoUnlockRecords(Long userId, String email, Long startTime, Long endTime, Model mo) {
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
		
		PagerModel pm = bqIeoUnlockRecordService.findRecords(uid, start, end);
		mo.addAttribute("pm", pm);
		
		return super.url_JSP("ieoUnlockRecords");
	}
	
	@RequestMapping(value = "/bonusRecords")
	public String bonusRecords(Long userId, String email, Long startTime, Long endTime, Model mo) {
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
		
		PagerModel pm =  bqBonusRecordService.readRecords(uid, start, end);
		mo.addAttribute("pm", pm);
		
		return super.url_JSP("bonusRecords");
	}
	
	@RequestMapping(value = "/bonusManager")
	public String bonusManager(String code, Model mo) {

		BQBonusSetting setting = bqBonusSettingService.readOne();
		if (setting == null) {
			setting = new BQBonusSetting();
		}
		
		mo.addAttribute("setting", setting);
		return super.url_JSP("bonusManager");
	}
	
	private void updateBonusSetting(BQBonusSetting setting) {
		if (setting == null) return;

		if (setting.getId() == 0) {
			bqBonusSettingService.save(setting);
		} else {
			bqBonusSettingService.update(setting);
		}
	}
	
	@RequestMapping(value = "/updateBonusSetting", method = RequestMethod.POST)
	public ModelAndView updateBonusSetting(BQBonusSetting setting, String beginTime, Model mo) throws ParseException {
		if (beginTime != null) {
			DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long starttime = StringUtil.isEmpty(beginTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(beginTime));
			setting.setBegin_time(starttime);
		}
		updateBonusSetting(setting);
		return new ModelAndView(super.redirect("/managerMine/bonusManager.do"));
	}
}
