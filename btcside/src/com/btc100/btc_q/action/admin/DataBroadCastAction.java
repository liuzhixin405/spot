package com.btc100.btc_q.action.admin;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQDataBroadCastSetting;
import com.btc100.btc_q.entity.BQDataBroadCastStats;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.service.BQDataBroadCastSettingService;
import com.btc100.btc_q.service.BQDataBroadCastStatsService;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Controller
@RequestMapping(value = "/managerDataBroadCast")
public class DataBroadCastAction extends BaseAction {

	@Autowired
	private BQDataBroadCastSettingService bqSettingService;
	@Autowired
	private BQDataBroadCastStatsService bqStatsService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;

	@RequestMapping(value = "/dataBroadCastSetting")
	public String dataBroadCastSetting(String code, Model mo) {
		BQTransaction currTrans = null;

		List<BQTransaction> transList = findCurrTrans();

		mo.addAttribute("transList", transList);
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
			return super.url_JSP("dataBroadCastSetting");
		}

		mo.addAttribute("current", currTrans);

		BQDataBroadCastSetting setting = bqSettingService.getByCode(currTrans.getTransaction_code());
		if (setting == null) {
			setting = new BQDataBroadCastSetting();
			setting.setF_code(currTrans.getTransaction_code());
		}

		mo.addAttribute("setting", setting);
		return super.url_JSP("dataBroadCastSetting");
	}

	@RequestMapping(value = "/updateDataBroadCastSetting", method = RequestMethod.POST)
	public String updateDataBroadCastSetting(BQDataBroadCastSetting setting, Model mo) {
		updateDataBroadCastSetting(setting);
		return super.redirect("/managerDataBroadCast/dataBroadCastSetting.do?code=" + setting.getF_code());
	}

	private void updateDataBroadCastSetting(BQDataBroadCastSetting setting) {
		if (setting == null)
			return;

		if (StringUtil.isEmpty(setting.getF_code())) {
			return;
		}

		if (setting.getId() == null) {
			bqSettingService.save(setting);
		} else {
			bqSettingService.update(setting);
		}
	}

	@RequestMapping(value = "/data")
	public String list(String code, String startTime, String endTime, Model mo) throws ParseException {

		List<BQDataBroadCastSetting> settings = bqSettingService.readAll();

		if (StringUtil.isEmpty(code)) {
			if (settings.size() > 0)
				code = settings.get(0).getF_code();
		}
		
		BQDataBroadCastStats lastDataStats = spyMemcachedClient.get(BTCQMemecacheKeys.DATA_BROAD_CAST + code);
		if (lastDataStats == null) {
			lastDataStats = bqStatsService.getLast(code);
			if (lastDataStats != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.DATA_BROAD_CAST + code, BTCQMemecacheKeys.TIMES.TWO_DAY, lastDataStats);
			}
		}
		mo.addAttribute("dataStats", lastDataStats);
		mo.addAttribute("settings", settings);
		mo.addAttribute("code", code);
		mo.addAttribute("startTime", startTime);
		mo.addAttribute("endTime", endTime);
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long starttime = StringUtil.isEmpty(startTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(startTime));
		long endtime = StringUtil.isEmpty(endTime) ? 0l : DateUtil.DateToUnix(formatDate.parse(endTime));

		PagerModel pm = new PagerModel();
		if (!StringUtil.isEmpty(code)) {
			pm = bqStatsService.readBySymbol(code, starttime, endtime);
		}

		mo.addAttribute("pm", pm);

		return super.url_JSP("dataBroadCastStats");
	}
}
