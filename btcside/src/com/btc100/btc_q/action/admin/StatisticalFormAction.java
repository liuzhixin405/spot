package com.btc100.btc_q.action.admin;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.BQArbitrageSetting;
import com.btc100.btc_q.entity.BQBfxFeeSetting;
import com.btc100.btc_q.entity.BQSpotArbitrageSetting;
import com.btc100.btc_q.entity.BQSpotMarketCoinPair;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.DeliveryArbitrageSetting;
import com.btc100.btc_q.entity.DeliveryStatisticalForm;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.SpotStatisticalForm;
import com.btc100.btc_q.entity.StatisticalForm;
import com.btc100.btc_q.entity.StatisticalHourlyForm;
import com.btc100.btc_q.entity.VArbitrageSetting;
import com.btc100.btc_q.entity.VStatisticalForm;
import com.btc100.btc_q.entity.VStatisticalHourlyForm;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.service.BQAccountContractService;
import com.btc100.btc_q.service.BQArbitrageSettingService;
import com.btc100.btc_q.service.BQBfxFeeRecordService;
import com.btc100.btc_q.service.BQBfxFeeSettingService;
import com.btc100.btc_q.service.BQMemberInviteRebateLogService;
import com.btc100.btc_q.service.BQMemberLoginLogService;
import com.btc100.btc_q.service.BQMemberService;
import com.btc100.btc_q.service.BQMemberTransactionService;
import com.btc100.btc_q.service.BQSpotArbitrageSettingService;
import com.btc100.btc_q.service.BQSpotMarketCoinPairService;
import com.btc100.btc_q.service.DeliveryAccountContractService;
import com.btc100.btc_q.service.DeliveryArbitrageSettingService;
import com.btc100.btc_q.service.DeliveryMemberInviteRebateLogService;
import com.btc100.btc_q.service.DeliveryMemberTransactionService;
import com.btc100.btc_q.service.DeliveryStatisticalFormService;
import com.btc100.btc_q.service.DeliveryTradeLogService;
import com.btc100.btc_q.service.SpotMemberTradeDetailService;
import com.btc100.btc_q.service.SpotStatisticalFormService;
import com.btc100.btc_q.service.SpotTradeLogService;
import com.btc100.btc_q.service.StatisticalFormService;
import com.btc100.btc_q.service.StatisticalHourlyFormService;
import com.btc100.btc_q.service.TradeLogService;
import com.btc100.btc_q.service.VAccountContractService;
import com.btc100.btc_q.service.VArbitrageSettingService;
import com.btc100.btc_q.service.VMemberTransactionService;
import com.btc100.btc_q.service.VStatisticalFormService;
import com.btc100.btc_q.service.VStatisticalHourlyFormService;
import com.btc100.btc_q.service.VTradeLogService;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.util.TimeUtil;
import com.btc100.btc_q.vo.StatisticalFormContractVo;
import com.btc100.btc_q.vo.StatisticalFormSpotVo;
import com.btc100.btc_q.vo.StatisticalFormVo;
import com.btc100.btc_q.vo.VStatisticalFormContractVo;

@Controller
@RequestMapping("/statisticalForm")
public class StatisticalFormAction extends BaseAction {

	@Autowired
	private TradeLogService tradeLogService;
	@Autowired
	private DeliveryTradeLogService dTradeLogService;
	@Autowired
	private BQArbitrageSettingService bqArbitrageSettingService;
	@Autowired
	private DeliveryArbitrageSettingService dArbitrageSettingService;
	@Autowired
	private BQMemberService bqMemberService;
	@Autowired
	private BQMemberLoginLogService bqMemberLoginLogService;
	@Autowired
	private StatisticalFormService statisticalFormService;
	@Autowired
	private DeliveryStatisticalFormService dStatisticalFormService;
	@Autowired
	private StatisticalHourlyFormService statisticalHourlyFormService;
	@Autowired
	private VStatisticalFormService vstatisticalFormService;
	@Autowired
	private VStatisticalHourlyFormService vstatisticalHourlyFormService;
	@Autowired
	private BQMemberTransactionService bqMemberTransactionService;
	@Autowired
	private DeliveryMemberTransactionService dMemberTransactionService;
	@Autowired
	private BQAccountContractService bqAccountContractService;
	@Autowired
	private DeliveryAccountContractService dAccountContractService;
	@Autowired
	private VTradeLogService vTradeLogService;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private VTransactionService vTransactionService;
	@Autowired
	private VArbitrageSettingService vArbitrageSettingService;
	@Autowired
	private VMemberTransactionService vMemberTransactionService;
	@Autowired
	private VAccountContractService vAccountContractService;
	@Autowired
	private BQMemberInviteRebateLogService bqMemberInviteRebateLogService;
	@Autowired
	private DeliveryMemberInviteRebateLogService dMemberInviteRebateLogService;
	@Autowired
	private BQBfxFeeRecordService bqBfxFeeRecordService;
	@Autowired
	private BQSpotMarketCoinPairService bqSpotMarketCoinPairService;
	@Autowired
	private BQSpotArbitrageSettingService bqSpotArbitrageSettingService;
	@Autowired
	private SpotTradeLogService spotTradeLogService;
	@Autowired
	private SpotMemberTradeDetailService spotMemberTradeDetailService;
	@Autowired
	private SpotStatisticalFormService spotStatisticalFormService;
	@Autowired
	private BQBfxFeeSettingService bqBfxFeeSettingService;

	@RequestMapping("/dayDatas")
	public String statisticsData(HttpServletRequest request, String startTimeStr, String endTimeStr, Model model) throws Exception {

		ArrayList<StatisticalFormVo> reportForms = new ArrayList<StatisticalFormVo>();
		
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (StringUtil.isEmpty(startTimeStr)) {
			startTime = (long) TimeUtil.getTimesmorning();
		} else {
			startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr));
		}
		if (StringUtil.isEmpty(endTimeStr)) {
			endTime = startTime + 86400L;
		} else {
			endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr));
		}
		
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
		}
		
		endTime += 86400L;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Long startDay = dateFormat.parse(dateFormat.format(startTime * 1000L)).getTime() / 1000L;
		Long endDay = dateFormat.parse(dateFormat.format(endTime * 1000L)).getTime() / 1000L;
		String tag = request.getParameter("tag");
		
		long start = startDay;
		long totalDays = (endDay - startDay) / 86400L;
		for (int i = 0; i < totalDays; i++) {
			long end = 86400L + start;
			StatisticalFormVo form = getStatisticsDayForm(start, end, tag);
			form.setTime(dateFormat.format(start * 1000L));
			reportForms.add(form);
			start = end;
		}
		
		if (!StringUtil.isEmpty(tag)) {
			model.addAttribute("tag", "addSimulate");
		}
		model.addAttribute("startTime", startTimeStr);
		model.addAttribute("endTime", endTimeStr);
		model.addAttribute("reportForms", reportForms);
		
		return super.url_JSP("statisticalForm");
	}
	
	private StatisticalFormVo getStatisticsDayForm(long startTime, long endTime, String tag) throws Exception {
		StatisticalFormVo formVo = new StatisticalFormVo();
		List<StatisticalFormContractVo> contractForms = new ArrayList<StatisticalFormContractVo>();
		
		List<StatisticalForm> statisticalForms = statisticalFormService.findStatisticalFormByTime(startTime);
		// 判断数据库是否有统计数据
		if (statisticalForms.size() > 0) {// 数据库有统计数据
			for (int i = 0; i < statisticalForms.size(); i++) {
				StatisticalFormContractVo contractFormVo = new StatisticalFormContractVo();
				StatisticalForm sForm = statisticalForms.get(i);
				String code = sForm.getCode();
				
				contractFormVo.setCode(code);
				contractFormVo.setDepositAmount(sForm.getDeposit_mount().doubleValue());
				contractFormVo.setContractBalance(sForm.getContract_balance().doubleValue());
				contractFormVo.setHoldAmount(sForm.getHold_mount().doubleValue());
				contractFormVo.setTradeAmount(sForm.getTrade_count().doubleValue());
				contractFormVo.setTradeUser(sForm.getTrade_user());
				contractFormVo.setFeeAmount(sForm.getTrade_fee().doubleValue());
				contractFormVo.setBfxFeeAmount(sForm.getTrade_bfx_fee().doubleValue());
				contractFormVo.setRebate(sForm.getRebate().doubleValue());
				contractFormVo.setBfxRebate(sForm.getBfx_rebate().doubleValue());
				
				contractForms.add(contractFormVo);
				if (i == 0) {
					formVo.setTotalFee(sForm.getTotal_fee().doubleValue());
					formVo.setTotalBfxFee(sForm.getTotal_bfx_fee().doubleValue());
					formVo.setTotalRebate(sForm.getTotal_rebate().doubleValue());
					formVo.setTotalBfxRebate(sForm.getTotal_bfx_rebate().doubleValue());
					formVo.setTodayRegister(sForm.getToday_register());
					formVo.setTotalRegister(sForm.getTotal_register());
					formVo.setLoginUserCount(sForm.getToday_login());
					formVo.setTotalTradeUser(sForm.getTotal_trade_user());
				}
			}
			
			formVo.setConForms(contractForms);
			
		} else {
			formVo = getTransStatsIntime(startTime, endTime);// 数据库中无数据，重新统计
		}
		
		// 判断是否带模拟盘查询
		if (!StringUtil.isEmpty(tag)) {// 带模拟盘查询
			// 判断数据库是否存在带模拟盘的统计数据
			List<VStatisticalFormContractVo> vconForms = new ArrayList<VStatisticalFormContractVo>();
			List<VStatisticalForm> vstatisticalForms = vstatisticalFormService.findStatisticalFormByTime(startTime);
			if (vstatisticalForms.size() > 0) {// 数据库有统计数据
				for (int j = 0; j < vstatisticalForms.size(); j++) {
					VStatisticalFormContractVo vcontractFormVo = new VStatisticalFormContractVo();
					VStatisticalForm vForm = vstatisticalForms.get(j);
					vcontractFormVo.setCode(vForm.getCode());
					vcontractFormVo.setVcontractBalance(vForm.getV_contract_balance().doubleValue());
					vcontractFormVo.setVdepositAmount(vForm.getV_deposit_mount().doubleValue());
					vcontractFormVo.setVfeeAmount(vForm.getV_trade_fee().doubleValue());
					vcontractFormVo.setVholdAmount(vForm.getV_hold_mount().doubleValue());
					vcontractFormVo.setVtradeAmount(vForm.getV_trade_count().doubleValue());
					vcontractFormVo.setVtradeUser(vForm.getV_trade_user());
					
					vconForms.add(vcontractFormVo);
					if (j == 0) {
						formVo.setVtotalFee(vForm.getV_total_fee().doubleValue());
						formVo.setVtotalTradeUser(vForm.getV_total_trade_user());
					}
				}
				formVo.setVconForms(vconForms);
			} else {
				StatisticalFormVo vTransIntime = getVTransIntime(startTime, endTime);
				formVo.setVconForms(vTransIntime.getVconForms());
				formVo.setVtotalFee(vTransIntime.getVtotalFee());
				formVo.setVtotalTradeUser(vTransIntime.getVtotalTradeUser());
			}
		}
		
		return formVo;
	}

	@RequestMapping("/hourlyDatas")
	public String hourlyDatas(HttpServletRequest request, String startTimeStr, String endTimeStr, Model model) throws Exception {

		ArrayList<StatisticalFormVo> reportForms = new ArrayList<StatisticalFormVo>();
		
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (StringUtil.isEmpty(startTimeStr)) {
			Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.MILLISECOND, 0);
			startTime = now.getTimeInMillis() / 1000;
		} else {
			startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr));
		}
		if (StringUtil.isEmpty(endTimeStr)) {
			endTime = startTime + 3600L;
		} else {
			endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr));
		}
		
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
		}
		
		endTime += 3600L;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
		Long startDay = dateFormat.parse(dateFormat.format(startTime * 1000L)).getTime() / 1000L;
		Long endDay = dateFormat.parse(dateFormat.format(endTime * 1000L)).getTime() / 1000L;
		
		String tag = request.getParameter("tag");
		long start = startDay;
		long totalDays = (endDay - startDay) / 3600L;
		for (int i = 0; i < totalDays; i++) {
			long end = 3600L + start;
			StatisticalFormVo form = getStatisticHourlyForm(start, end, tag);
			form.setTime(dateFormat.format(start * 1000L));
			reportForms.add(form);
			start = end;
		}
		
		if (!StringUtil.isEmpty(tag)) {
			model.addAttribute("tag", "addSimulate");
		}
		model.addAttribute("startTime", startTimeStr);
		model.addAttribute("endTime", endTimeStr);
		model.addAttribute("reportForms", reportForms);
		
		return super.url_JSP("statisticalHourlyForm");
	}
	
	private StatisticalFormVo getStatisticHourlyForm(long startTime, long endTime, String tag) throws Exception {
		StatisticalFormVo formVo = new StatisticalFormVo();
		List<StatisticalFormContractVo> contractForms = new ArrayList<StatisticalFormContractVo>();
		
		List<StatisticalHourlyForm> statisticalForms = statisticalHourlyFormService.findStatisticalFormByTime(startTime);
		if (statisticalForms.size() > 0) {
			for (int i = 0; i < statisticalForms.size(); i++) {
				StatisticalFormContractVo contractFormVo = new StatisticalFormContractVo();
				StatisticalHourlyForm sForm = statisticalForms.get(i);
				String code = sForm.getCode();
				
				contractFormVo.setCode(code);
				contractFormVo.setDepositAmount(sForm.getDeposit_mount().doubleValue());
				contractFormVo.setContractBalance(sForm.getContract_balance().doubleValue());
				contractFormVo.setHoldAmount(sForm.getHold_mount().doubleValue());
				contractFormVo.setTradeAmount(sForm.getTrade_count().doubleValue());
				contractFormVo.setTradeUser(sForm.getTrade_user());
				contractFormVo.setFeeAmount(sForm.getTrade_fee().doubleValue());
				contractFormVo.setBfxFeeAmount(sForm.getTrade_bfx_fee().doubleValue());
				contractFormVo.setRebate(sForm.getRebate().doubleValue());
				contractFormVo.setBfxRebate(sForm.getBfx_rebate().doubleValue());
				
				contractForms.add(contractFormVo);
				if (i == 0) {
					formVo.setTotalFee(sForm.getTotal_fee().doubleValue());
					formVo.setTotalBfxFee(sForm.getTotal_bfx_fee().doubleValue());
					formVo.setTotalRebate(sForm.getTotal_rebate().doubleValue());
					formVo.setTotalBfxRebate(sForm.getTotal_bfx_rebate().doubleValue());
					formVo.setTodayRegister(sForm.getHourly_register());
					formVo.setTotalRegister(sForm.getTotal_register());
					formVo.setLoginUserCount(sForm.getHourly_login());
					formVo.setTotalTradeUser(sForm.getTotal_trade_user());
				}
			}
			
			formVo.setConForms(contractForms);
		} else {
			formVo = getTransStatsIntime(startTime, endTime);
		}
		
		// 判断是否带模拟盘查询
		if (!StringUtil.isEmpty(tag)) {// 带模拟盘查询
			// 判断数据库是否存在带模拟盘的统计数据
			List<VStatisticalFormContractVo> vconForms = new ArrayList<VStatisticalFormContractVo>();
			List<VStatisticalHourlyForm> vstatisticalForms = vstatisticalHourlyFormService.findStatisticalFormByTime(startTime);
			if (vstatisticalForms.size() > 0) {// 数据库有统计数据
				for (int j = 0; j < vstatisticalForms.size(); j++) {
					VStatisticalFormContractVo vcontractFormVo = new VStatisticalFormContractVo();
					VStatisticalHourlyForm vForm = vstatisticalForms.get(j);
					vcontractFormVo.setCode(vForm.getCode());
					vcontractFormVo.setVcontractBalance(vForm.getV_contract_balance().doubleValue());
					vcontractFormVo.setVdepositAmount(vForm.getV_deposit_mount().doubleValue());
					vcontractFormVo.setVfeeAmount(vForm.getV_trade_fee().doubleValue());
					vcontractFormVo.setVholdAmount(vForm.getV_hold_mount().doubleValue());
					vcontractFormVo.setVtradeAmount(vForm.getV_trade_count().doubleValue());
					vcontractFormVo.setVtradeUser(vForm.getV_trade_user());
					
					vconForms.add(vcontractFormVo);
					if (j == 0) {
						formVo.setVtotalFee(vForm.getV_total_fee().doubleValue());
						formVo.setVtotalTradeUser(vForm.getV_total_trade_user());
					}
				}
				
				formVo.setVconForms(vconForms);
			} else {
				StatisticalFormVo vTransIntime = getVTransIntime(startTime, endTime);
				formVo.setVconForms(vTransIntime.getVconForms());
				formVo.setVtotalFee(vTransIntime.getVtotalFee());
				formVo.setVtotalTradeUser(vTransIntime.getVtotalTradeUser());
			}
		}

		return formVo;
	}
	
	@RequestMapping("/daySpotDatas")
	public String statisticsSpotData(HttpServletRequest request, String startTimeStr, String endTimeStr, Model model) throws Exception {

		ArrayList<StatisticalFormVo> reportForms = new ArrayList<StatisticalFormVo>();
		
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (StringUtil.isEmpty(startTimeStr)) {
			startTime = (long) TimeUtil.getTimesmorning();
		} else {
			startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr));
		}
		if (StringUtil.isEmpty(endTimeStr)) {
			endTime = startTime + 86400L;
		} else {
			endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr));
		}
		
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
		}
		
		endTime += 86400L;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Long startDay = dateFormat.parse(dateFormat.format(startTime * 1000L)).getTime() / 1000L;
		Long endDay = dateFormat.parse(dateFormat.format(endTime * 1000L)).getTime() / 1000L;
		
		long start = startDay;
		long totalDays = (endDay - startDay) / 86400L;
		for (int i = 0; i < totalDays; i++) {
			long end = 86400L + start;
			StatisticalFormVo form = getStatisticsSpotDayForm(start, end);
			form.setTime(dateFormat.format(start * 1000L));
			reportForms.add(form);
			start = end;
		}
		
		model.addAttribute("startTime", startTimeStr);
		model.addAttribute("endTime", endTimeStr);
		model.addAttribute("reportForms", reportForms);
		
		return super.url_JSP("spotStatisticalForm");
	}
	
	private StatisticalFormVo getStatisticsSpotDayForm(long startTime, long endTime) throws Exception {
		StatisticalFormVo formVo = new StatisticalFormVo();
		List<StatisticalFormSpotVo> spotForms = new ArrayList<StatisticalFormSpotVo>();
		
		List<SpotStatisticalForm> statisticalForms = spotStatisticalFormService.findStatisticalFormByTime(startTime);
		// 判断数据库是否有统计数据
		if (statisticalForms.size() > 0) {// 数据库有统计数据
			for (int i = 0; i < statisticalForms.size(); i++) {
				StatisticalFormSpotVo spotFormVo = new StatisticalFormSpotVo();
				SpotStatisticalForm sForm = statisticalForms.get(i);
				String code = sForm.getCode();
				
				spotFormVo.setCode(code);
				spotFormVo.setMarket(sForm.getMarket());
				spotFormVo.setCoin(sForm.getCoin());
				spotFormVo.setTradeUser(sForm.getTrade_user());
				spotFormVo.setTradeAmount(sForm.getTrade_amount().doubleValue());
				spotFormVo.setBuyTradeFee(sForm.getBuy_trade_fee().doubleValue());
				spotFormVo.setSellTradeFee(sForm.getSell_trade_fee().doubleValue());
				spotFormVo.setBuyTradeBfxFee(sForm.getBuy_trade_bfx_fee().doubleValue());
				spotFormVo.setSellTradeBfxFee(sForm.getSell_trade_bfx_fee().doubleValue());
				spotFormVo.setBfxRebate(sForm.getBfx_rebate().doubleValue());
				spotFormVo.setCoinRebate(sForm.getCoin_rebate().doubleValue());
				spotFormVo.setMarketRebate(sForm.getMarket_rebate().doubleValue());

				spotForms.add(spotFormVo);
				if (i == 0) {
					formVo.setTotalTradeUser(sForm.getTotal_trade_user());
					formVo.setTotalBfxFee(sForm.getTotal_bfx_fee().doubleValue());
					formVo.setTotalFee(sForm.getTotal_market_fee().doubleValue());
					formVo.setTotalCoinRebate(sForm.getTotal_coin_rebate().doubleValue());
					formVo.setTotalMarketRebate(sForm.getTotal_market_rebate().doubleValue());
					formVo.setTotalBfxRebate(sForm.getTotal_bfx_rebate().doubleValue());
				}
			}
			
			formVo.setSpotFormVos(spotForms);
			
		} else {
			formVo = getSpotStatsInTime(startTime, endTime);// 数据库中无数据，重新统计
		}
		
		return formVo;
	}
	
	@RequestMapping("/deliveryDayDatas")
	public String deliveryDayDatas(HttpServletRequest request, String startTimeStr, String endTimeStr, Model model) throws Exception {

		List<StatisticalFormVo> reportForms = new ArrayList<StatisticalFormVo>();
		
		DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = 0l;
		long endTime = 0l;
		if (StringUtil.isEmpty(startTimeStr)) {
			startTime = (long) TimeUtil.getTimesmorning();
		} else {
			startTime = DateUtil.DateToUnix(formatDate.parse(startTimeStr));
		}
		if (StringUtil.isEmpty(endTimeStr)) {
			endTime = startTime + 86400L;
		} else {
			endTime = DateUtil.DateToUnix(formatDate.parse(endTimeStr));
		}
		
		long now = System.currentTimeMillis() / 1000;
		if (endTime > now) {
			endTime = now;
		}
		
		endTime += 86400L;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Long startDay = dateFormat.parse(dateFormat.format(startTime * 1000L)).getTime() / 1000L;
		Long endDay = dateFormat.parse(dateFormat.format(endTime * 1000L)).getTime() / 1000L;
		
		long start = startDay;
		long totalDays = (endDay - startDay) / 86400L;
		for (int i = 0; i < totalDays; i++) {
			long end = 86400L + start;
			StatisticalFormVo form = getDeliveryDayForm(start, end);
			form.setTime(dateFormat.format(start * 1000L));
			reportForms.add(form);
			start = end;
		}
		
		model.addAttribute("startTime", startTimeStr);
		model.addAttribute("endTime", endTimeStr);
		model.addAttribute("reportForms", reportForms);
		
		return super.url_JSP("statisticalDeliveryForm");
	}
	
	private StatisticalFormVo getDeliveryDayForm(long startTime, long endTime) throws Exception {
		StatisticalFormVo formVo = new StatisticalFormVo();
		List<StatisticalFormContractVo> contractForms = new ArrayList<StatisticalFormContractVo>();
		
		List<DeliveryStatisticalForm> statisticalForms = dStatisticalFormService.findStatisticalFormByTime(startTime);
		// 判断数据库是否有统计数据
		if (statisticalForms.size() > 0) {// 数据库有统计数据
			for (int i = 0; i < statisticalForms.size(); i++) {
				StatisticalFormContractVo contractFormVo = new StatisticalFormContractVo();
				DeliveryStatisticalForm sForm = statisticalForms.get(i);
				String code = sForm.getCode();
				
				contractFormVo.setCode(code);
				contractFormVo.setDepositAmount(sForm.getDeposit_mount().doubleValue());
				contractFormVo.setContractBalance(sForm.getContract_balance().doubleValue());
				contractFormVo.setHoldAmount(sForm.getHold_mount().doubleValue());
				contractFormVo.setTradeAmount(sForm.getTrade_count().doubleValue());
				contractFormVo.setTradeUser(sForm.getTrade_user());
				contractFormVo.setFeeAmount(sForm.getTrade_fee().doubleValue());
				contractFormVo.setBfxFeeAmount(sForm.getTrade_bfx_fee().doubleValue());
				contractFormVo.setRebate(sForm.getRebate().doubleValue());
				contractFormVo.setBfxRebate(sForm.getBfx_rebate().doubleValue());
				
				contractForms.add(contractFormVo);
				if (i == 0) {
					formVo.setTotalFee(sForm.getTotal_fee().doubleValue());
					formVo.setTotalBfxFee(sForm.getTotal_bfx_fee().doubleValue());
					formVo.setTotalRebate(sForm.getTotal_rebate().doubleValue());
					formVo.setTotalBfxRebate(sForm.getTotal_bfx_rebate().doubleValue());
					formVo.setTotalTradeUser(sForm.getTotal_trade_user());
				}
			}
			
			formVo.setConForms(contractForms);
			
		} else {
			formVo = getDeliveryStatsIntime(startTime, endTime);// 数据库中无数据，重新统计
		}
		
		return formVo;
	}

	
	private StatisticalFormVo getTransStatsIntime(long startTime, long endTime) throws Exception {
		StatisticalFormVo form = new StatisticalFormVo();
		List<StatisticalFormContractVo> contractForms = new ArrayList<StatisticalFormContractVo>();
		form.setTodayRegister(bqMemberService.findCountByTime(startTime, endTime));
		form.setTotalRegister(bqMemberService.findCount());
		form.setLoginUserCount(bqMemberLoginLogService.getTotalLoginUserByTime(startTime, endTime));
		
		BQBfxFeeSetting bfxFeeSetting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (bfxFeeSetting == null) {
			bfxFeeSetting = bqBfxFeeSettingService.readOne();
			if (bfxFeeSetting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, bfxFeeSetting);
			}
		}
		
		List<BQTransaction> lstTrads = findCurrTrans();
		List<Integer> users = new ArrayList<Integer>();
		for (BQTransaction bqTransaction : lstTrads) {
			StatisticalFormContractVo contractForm = new StatisticalFormContractVo();
			
			String traCode = bqTransaction.getTransaction_code();
			BQArbitrageSetting setting = bqArbitrageSettingService.readArbitrageSettingByCode(traCode);
			if (setting == null) {
				setting = new BQArbitrageSetting();
				setting.setDepth_uid(0L);
			}
			
			contractForm.setCode(traCode);
			contractForm.setTradeUser(tradeLogService.getTradeUserCount(traCode, startTime, endTime, setting.getDepth_uid()));
			contractForm.setTradeAmount(
					tradeLogService.getInTimeNoScalpingVol(traCode, startTime, endTime, setting.getDepth_uid()).doubleValue());
			contractForm.setFeeAmount(
					tradeLogService.getInTimeNoScalpingFee(traCode, startTime, endTime, setting.getDepth_uid()).doubleValue());
			contractForm.setBfxFeeAmount(bqBfxFeeRecordService.readSumBfxFee("", 0, traCode, startTime, endTime).doubleValue());
			contractForm.setRebate(bqMemberInviteRebateLogService
					.readSumRebateByTimeAndUser("", traCode, startTime, endTime, 0, Contents.TRADE_CURRENCY_TYPE_ID).doubleValue());
			
			if (startTime == TimeUtil.getTimesmorning() || startTime == getCurrentStartHour()) {
				contractForm.setDepositAmount(bqMemberTransactionService.findSumMinDepositByUser(traCode, 0, setting.getDepth_uid()).doubleValue());
				contractForm.setHoldAmount(bqMemberTransactionService.findSumHoldsByUser(traCode, 0, setting.getDepth_uid()).doubleValue());
				BigDecimal balance = bqAccountContractService.readSumBalanceByUser(traCode, 0, setting.getDepth_uid());
				contractForm.setContractBalance(balance.doubleValue());
			}

			if (bfxFeeSetting != null && bfxFeeSetting.getCoin_id() > 0) {
				contractForm.setBfxRebate(bqMemberInviteRebateLogService.readSumRebateByTimeAndUser("", traCode, startTime, endTime, 
						0, bfxFeeSetting.getCoin_id()).doubleValue());
			}
			
			contractForms.add(contractForm);
			
			List<Integer> ids = tradeLogService.getTradeUserIds(traCode, startTime, endTime, setting.getDepth_uid());
			for (Integer id : ids) {
				boolean have = false;
				for (Integer user : users) {
					if (id.longValue() == user.longValue()) {
						have = true;
						break;
					}
				}
				
				if (!have) {
					users.add(id);
				}
			}
			
			form.setTotalBfxFee(contractForm.getBfxFeeAmount() + form.getTotalBfxFee());
			form.setTotalFee(contractForm.getFeeAmount() + form.getTotalFee());
			form.setTotalRebate(contractForm.getRebate() + form.getTotalRebate());
			form.setTotalBfxRebate(contractForm.getBfxRebate() + form.getTotalBfxRebate());
		}

		form.setTotalTradeUser(users.size());
		form.setConForms(contractForms);
		
		return form;
	}
	
	private StatisticalFormVo getDeliveryStatsIntime(long startTime, long endTime) throws Exception {
		StatisticalFormVo form = new StatisticalFormVo();
		List<StatisticalFormContractVo> contractForms = new ArrayList<StatisticalFormContractVo>();
		
		BQBfxFeeSetting bfxFeeSetting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (bfxFeeSetting == null) {
			bfxFeeSetting = bqBfxFeeSettingService.readOne();
			if (bfxFeeSetting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, bfxFeeSetting);
			}
		}
		
		List<DeliveryTransaction> lstTrads = findCurrDTrans();
		List<Integer> users = new ArrayList<Integer>();
		for (DeliveryTransaction bqTransaction : lstTrads) {
			StatisticalFormContractVo contractForm = new StatisticalFormContractVo();
			
			String traCode = bqTransaction.getTransaction_code();
			DeliveryArbitrageSetting setting = dArbitrageSettingService.readArbitrageSettingByCode(traCode);
			if (setting == null) {
				setting = new DeliveryArbitrageSetting();
				setting.setDepth_uid(0L);
			}
			
			contractForm.setCode(traCode);
			contractForm.setTradeUser(dTradeLogService.getTradeUserCount(traCode, startTime, endTime, setting.getDepth_uid()));
			contractForm.setTradeAmount(
					dTradeLogService.getInTimeNoScalpingVol(traCode, startTime, endTime, setting.getDepth_uid()).doubleValue());
			contractForm.setFeeAmount(
					dTradeLogService.getInTimeNoScalpingFee(traCode, startTime, endTime, setting.getDepth_uid()).doubleValue());
			contractForm.setBfxFeeAmount(bqBfxFeeRecordService.readSumBfxFee("", 0, traCode, startTime, endTime).doubleValue());
			contractForm.setRebate(dMemberInviteRebateLogService
					.readSumRebateByTimeAndUser(traCode, startTime, endTime, 0, Contents.TRADE_CURRENCY_TYPE_ID).doubleValue());
			
			if (startTime == TimeUtil.getTimesmorning() || startTime == getCurrentStartHour()) {
				contractForm.setDepositAmount(dMemberTransactionService.findSumMinDepositByUser(traCode, 0, setting.getDepth_uid()).doubleValue());
				contractForm.setHoldAmount(dMemberTransactionService.findSumHoldsByUser(traCode, 0, setting.getDepth_uid()).doubleValue());
				BigDecimal balance = dAccountContractService.readSumBalanceByUser(traCode, 0, setting.getDepth_uid());
				contractForm.setContractBalance(balance.doubleValue());
			}

			if (bfxFeeSetting != null && bfxFeeSetting.getCoin_id() > 0) {
				contractForm.setBfxRebate(dMemberInviteRebateLogService.readSumRebateByTimeAndUser(traCode, startTime, endTime, 
						0, bfxFeeSetting.getCoin_id()).doubleValue());
			}
			
			contractForms.add(contractForm);
			
			List<Integer> ids = dTradeLogService.getTradeUserIds(traCode, startTime, endTime, setting.getDepth_uid());
			for (Integer id : ids) {
				boolean have = false;
				for (Integer user : users) {
					if (id.longValue() == user.longValue()) {
						have = true;
						break;
					}
				}
				
				if (!have) {
					users.add(id);
				}
			}
			
			form.setTotalBfxFee(contractForm.getBfxFeeAmount() + form.getTotalBfxFee());
			form.setTotalFee(contractForm.getFeeAmount() + form.getTotalFee());
			form.setTotalRebate(contractForm.getRebate() + form.getTotalRebate());
			form.setTotalBfxRebate(contractForm.getBfxRebate() + form.getTotalBfxRebate());
		}

		form.setTotalTradeUser(users.size());
		form.setConForms(contractForms);
		
		return form;
	}
	
	private StatisticalFormVo getVTransIntime(long startTime, long endTime) throws Exception {
		StatisticalFormVo form = new StatisticalFormVo();
		List<VStatisticalFormContractVo> vContractForms = new ArrayList<VStatisticalFormContractVo>();
		
		List<VTransaction> vlstTrads = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTIONLIST);
		if (vlstTrads == null) {
			vlstTrads = vTransactionService.readTransByState_new(Contents.TRANSACTION_STATE_ZC);
		}
		
		List<Integer> vusers = new ArrayList<Integer>();
		for (VTransaction vTrans : vlstTrads) {
			VStatisticalFormContractVo vContractForm = new VStatisticalFormContractVo();
			String vTraCode = vTrans.getTransaction_code();
			VArbitrageSetting vArbitrageSetting = vArbitrageSettingService.getArbitrageSettingByCode(vTraCode);
			if (vArbitrageSetting == null) {
				vArbitrageSetting = new VArbitrageSetting();
				vArbitrageSetting.setDepth_uid(0L);
			}
			
			long vtradeUser = vTradeLogService.getTradeUserCount(vTraCode, startTime, endTime,
					vArbitrageSetting.getDepth_uid());
			double vtradeAmount = vTradeLogService.getInTimeNoScalpingVol(vTraCode, startTime, endTime, vArbitrageSetting.getDepth_uid())
					.doubleValue();
			double vfeeAmount = vTradeLogService.getInTimeNoScalpingFee(vTraCode, startTime, endTime, vArbitrageSetting.getDepth_uid()).doubleValue();
			
			vContractForm.setCode(vTraCode);
			vContractForm.setVtradeUser(vtradeUser);
			vContractForm.setVtradeAmount(vtradeAmount);
			vContractForm.setVfeeAmount(vfeeAmount);
			if (startTime == TimeUtil.getTimesmorning() || startTime == getCurrentStartHour()) {
				double vdepositAmount = vMemberTransactionService
						.findSumMinDepositByUser(vTraCode, 0, vArbitrageSetting.getDepth_uid()).doubleValue();
				double vholdAmount = vMemberTransactionService
						.findSumHoldsByUser(vTraCode, 0, vArbitrageSetting.getDepth_uid()).doubleValue();
				double vcontractBalance = vAccountContractService
						.findSumBalanceByUser(vTraCode, 0, vArbitrageSetting.getDepth_uid()).doubleValue();
				vContractForm.setVdepositAmount(vdepositAmount);
				vContractForm.setVholdAmount(vholdAmount);
				vContractForm.setVcontractBalance(vcontractBalance);
			}
			
			vContractForms.add(vContractForm);
			
			List<Integer> ids = vTradeLogService.getTradeUserIds(vTraCode, startTime, endTime, vArbitrageSetting.getDepth_uid());
			for (Integer id : ids) {
				boolean have = false;
				for (Integer user : vusers) {
					if (id.longValue() == user.longValue()) {
						have = true;
						break;
					}
				}
				
				if (!have) {
					vusers.add(id);
				}
			}
			
			form.setVtotalFee(vContractForm.getVfeeAmount() + form.getVtotalFee());
		}
		
		form.setVtotalTradeUser(vusers.size());
		form.setVconForms(vContractForms);
		
		return form;
	}
	
	private StatisticalFormVo getSpotStatsInTime(long startTime, long endTime) throws Exception {
		StatisticalFormVo form = new StatisticalFormVo();
		List<StatisticalFormSpotVo> spotFormVos = new ArrayList<StatisticalFormSpotVo>();

		List<BQSpotMarketCoinPair> coinPairs = spyMemcachedClient.get(BTCQMemecacheKeys.SPOTCOINPAIRLIST);
		if (coinPairs == null) {
			coinPairs = bqSpotMarketCoinPairService.readByState_new(Contents.COINPAIR_STATE_NORMAL);
			spyMemcachedClient.set(BTCQMemecacheKeys.SPOTCOINPAIRLIST, BTCQMemecacheKeys.TIMES.SPOTCOINPAIRLIST, coinPairs);
		}
		
        BQBfxFeeSetting bfxFeeSetting = spyMemcachedClient.get(BTCQMemecacheKeys.BFX_FEE_SETTING);
		if (bfxFeeSetting == null) {
			bfxFeeSetting = bqBfxFeeSettingService.readOne();
			if (bfxFeeSetting != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.BFX_FEE_SETTING, BTCQMemecacheKeys.TIMES.TEN_SECOND, bfxFeeSetting);
			}
		}
		
		List<Integer> users = new ArrayList<Integer>();
		for (BQSpotMarketCoinPair coinPair : coinPairs) {
			
			StatisticalFormSpotVo spotForm = new StatisticalFormSpotVo();

			String code = coinPair.getCoin_pair_name();
			BQSpotArbitrageSetting setting = bqSpotArbitrageSettingService.readArbitrageSettingByCode(code);
			if (setting == null) {
				setting = new BQSpotArbitrageSetting();
				setting.setDepth_uid(0L);
			}

			spotForm.setCode(code);
			spotForm.setMarket(coinPair.getMarket_coin_name());
			spotForm.setCoin(coinPair.getCoin_name());
			spotForm.setTradeUser(
					spotTradeLogService.getTradeUserCount(code, startTime, endTime, setting.getDepth_uid()));
			spotForm.setTradeAmount(spotMemberTradeDetailService
					.getInTimeNoScalpingAmount(code, startTime, endTime, setting.getDepth_uid()).doubleValue());
			spotForm.setBuyTradeFee(spotTradeLogService
					.getFeeInTimeNoScalping(code, Contents.TRADE_TYPE_BUY, startTime, endTime, setting.getDepth_uid())
					.doubleValue());
			spotForm.setSellTradeFee(spotTradeLogService
					.getFeeInTimeNoScalping(code, Contents.TRADE_TYPE_SELL, startTime, endTime, setting.getDepth_uid())
					.doubleValue());
			spotForm.setBuyTradeBfxFee(spotTradeLogService
					.getBfxFeeNoScalping(code, Contents.TRADE_TYPE_BUY, startTime, endTime, setting.getDepth_uid())
					.doubleValue());
			spotForm.setSellTradeBfxFee(spotTradeLogService
					.getBfxFeeNoScalping(code, Contents.TRADE_TYPE_SELL, startTime, endTime, setting.getDepth_uid())
					.doubleValue());
			
			spotForm.setCoinRebate(bqMemberInviteRebateLogService.readSumRebateByTimeAndUser("", code, startTime, endTime, 
					0, coinPair.getCoin_id()).doubleValue());
			spotForm.setMarketRebate(bqMemberInviteRebateLogService.readSumRebateByTimeAndUser("", code, startTime, endTime, 
					0, coinPair.getMarket_coin_id()).doubleValue());
			spotForm.setMarketRebate(bqMemberInviteRebateLogService.readSumRebateByTimeAndUser("", code, startTime, endTime, 
					0, coinPair.getMarket_coin_id()).doubleValue());

			if (bfxFeeSetting != null && bfxFeeSetting.getCoin_id() > 0) {
				spotForm.setBfxRebate(bqMemberInviteRebateLogService.readSumRebateByTimeAndUser("", code, startTime, endTime, 
						0, bfxFeeSetting.getCoin_id()).doubleValue());
			}

			spotFormVos.add(spotForm);

			List<Integer> ids = spotTradeLogService.getTradeUserIds(code, startTime, endTime, setting.getDepth_uid());
			for (Integer id : ids) {
				boolean have = false;
				for (Integer user : users) {
					if (id.longValue() == user.longValue()) {
						have = true;
						break;
					}
				}

				if (!have) {
					users.add(id);
				}
			}
			
			form.setTotalCoinRebate(form.getTotalCoinRebate() + spotForm.getCoinRebate());
			form.setTotalMarketRebate(form.getTotalMarketRebate() + spotForm.getMarketRebate());
			form.setTotalBfxRebate(form.getTotalBfxRebate() + spotForm.getBfxRebate());
			form.setTotalBfxFee(spotForm.getBuyTradeBfxFee() + spotForm.getSellTradeBfxFee() + form.getTotalBfxFee());
			form.setTotalFee(form.getTotalFee() + spotForm.getSellTradeFee());
		}

		form.setTotalTradeUser(users.size());
		form.setSpotFormVos(spotFormVos);

		return form;
	}
	
	private long getCurrentStartHour() {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		long startTime = now.getTimeInMillis() / 1000;
		return startTime;
	}

}
