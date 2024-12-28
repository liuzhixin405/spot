package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.entity.DeliveryStatisticalForm;
import com.btc100.btc_q.entity.StatisticalForm;
import com.btc100.btc_q.entity.VStatisticalForm;
import com.btc100.btc_q.service.DeliveryStatisticalFormService;
import com.btc100.btc_q.service.StatisticalFormService;
import com.btc100.btc_q.service.VStatisticalFormService;
import com.btc100.btc_q.vo.StatisticalFormContractVo;
import com.btc100.btc_q.vo.StatisticalFormVo;
import com.btc100.btc_q.vo.VStatisticalFormContractVo;

public class TimerTaskStatisticalForm extends BaseTask {
	
	private static final Logger logger = Logger.getLogger(TimerTaskStatisticalForm.class);

	@Autowired
	private StatisticalFormService statisticalFormService;
	@Autowired
	private VStatisticalFormService vstatisticalFormService;
	@Autowired
	private DeliveryStatisticalFormService deliveryStatisticalFormService;
	
	public void run() {
		Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		time.set(Calendar.HOUR_OF_DAY, 0);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		
		long endTime = time.getTimeInMillis() / 1000;
		time.add(Calendar.DATE, -1);
		long startTime = time.getTimeInMillis() / 1000;
		
		try {
			StatisticalFormVo form = getStatisticsReportForm(startTime, endTime);
			List<StatisticalFormContractVo> contractForms = form.getConForms();
			List<VStatisticalFormContractVo> vconForms = form.getVconForms();
			for (StatisticalFormContractVo vo : contractForms) {
				StatisticalForm sform = new StatisticalForm();
				sform.setStatistical_time(startTime);
				sform.setToday_login(form.getLoginUserCount());
				sform.setToday_register(form.getTodayRegister());
				sform.setTotal_register(form.getTotalRegister());
				sform.setTotal_trade_user(form.getTotalTradeUser());
				sform.setTotal_fee(new BigDecimal(form.getTotalFee()));
				sform.setTotal_bfx_fee(new BigDecimal(form.getTotalBfxFee()));
				sform.setTotal_bfx_rebate(new BigDecimal(form.getTotalBfxRebate()));
				sform.setTotal_rebate(new BigDecimal(form.getTotalRebate()));
				sform.setCode(vo.getCode());
				sform.setHold_mount(new BigDecimal(vo.getHoldAmount()));
				sform.setDeposit_mount(new BigDecimal(vo.getDepositAmount()));
				sform.setContract_balance(new BigDecimal(vo.getContractBalance()));
				sform.setTrade_count(new BigDecimal(vo.getTradeAmount()));
				sform.setTrade_user(vo.getTradeUser());
				sform.setTrade_fee(new BigDecimal(vo.getFeeAmount()));
				sform.setTrade_bfx_fee(new BigDecimal(vo.getBfxFeeAmount()));
				sform.setRebate(new BigDecimal(vo.getRebate()));
				sform.setBfx_rebate(new BigDecimal(vo.getBfxRebate()));
				
				statisticalFormService.save(sform);
			}
			
			for (VStatisticalFormContractVo vContractVo : vconForms) {
				VStatisticalForm vForm = new VStatisticalForm();
				vForm.setStatistical_time(startTime);
				vForm.setCode(vContractVo.getCode());
				vForm.setV_total_fee(new BigDecimal(form.getVtotalFee()));
				vForm.setV_total_trade_user(form.getVtotalTradeUser());
				vForm.setV_contract_balance(new BigDecimal(vContractVo.getVcontractBalance()));
				vForm.setV_deposit_mount(new BigDecimal(vContractVo.getVdepositAmount()));
				vForm.setV_hold_mount(new BigDecimal(vContractVo.getVholdAmount()));
				vForm.setV_trade_count(new BigDecimal(vContractVo.getVtradeAmount()));
				vForm.setV_trade_fee(new BigDecimal(vContractVo.getVfeeAmount()));
				vForm.setV_trade_user(vContractVo.getVtradeUser());
				
				vstatisticalFormService.save(vForm);
			}
			
		} catch (Exception e) {
			logger.info("每日统计报表异常。。。。");
			e.printStackTrace();
		}
		
		try {
			StatisticalFormVo form = getDeliveryForm(startTime, endTime);
			for (StatisticalFormContractVo vo : form.getConForms()) {
				DeliveryStatisticalForm sform = new DeliveryStatisticalForm();
				sform.setStatistical_time(startTime);
				sform.setTotal_trade_user(form.getTotalTradeUser());
				sform.setTotal_fee(new BigDecimal(form.getTotalFee()));
				sform.setTotal_bfx_fee(new BigDecimal(form.getTotalBfxFee()));
				sform.setTotal_bfx_rebate(new BigDecimal(form.getTotalBfxRebate()));
				sform.setTotal_rebate(new BigDecimal(form.getTotalRebate()));
				sform.setCode(vo.getCode());
				sform.setHold_mount(new BigDecimal(vo.getHoldAmount()));
				sform.setDeposit_mount(new BigDecimal(vo.getDepositAmount()));
				sform.setContract_balance(new BigDecimal(vo.getContractBalance()));
				sform.setTrade_count(new BigDecimal(vo.getTradeAmount()));
				sform.setTrade_user(vo.getTradeUser());
				sform.setTrade_fee(new BigDecimal(vo.getFeeAmount()));
				sform.setTrade_bfx_fee(new BigDecimal(vo.getBfxFeeAmount()));
				sform.setRebate(new BigDecimal(vo.getRebate()));
				sform.setBfx_rebate(new BigDecimal(vo.getBfxRebate()));
				
				deliveryStatisticalFormService.save(sform);
				logger.info("每日统计交割合约报表");
			}
		} catch (Exception e) {
			logger.info("每日统计交割合约报表异常。。。");
			e.printStackTrace();
		}
	}
	
}
