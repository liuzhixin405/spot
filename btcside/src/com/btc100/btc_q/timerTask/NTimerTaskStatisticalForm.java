package com.btc100.btc_q.timerTask;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.entity.NStatisticalForm;
import com.btc100.btc_q.service.NContractPartitionService;
import com.btc100.btc_q.service.NStatisticalFormService;
import com.btc100.btc_q.vo.StatisticalFormContractVo;
import com.btc100.btc_q.vo.StatisticalFormVo;

public class NTimerTaskStatisticalForm extends BaseTask {
	
	private static final Logger logger = Logger.getLogger(NTimerTaskStatisticalForm.class);

	@Autowired
	private NStatisticalFormService statisticalFormService;
	@Autowired
	private NContractPartitionService nContractPartitionService;
	
	public void run() {
		Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		time.set(Calendar.HOUR_OF_DAY, 0);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		
		long endTime = time.getTimeInMillis() / 1000;
		time.add(Calendar.DATE, -1);
		long startTime = time.getTimeInMillis() / 1000;
		
		List<NContractPartition> partitions = nContractPartitionService.findAll();
		for (NContractPartition partition : partitions) {
			try {
				StatisticalFormVo form = getNStatisticsReportForm(partition.getPart(), startTime, endTime);
				List<StatisticalFormContractVo> contractForms = form.getConForms();
				for (StatisticalFormContractVo vo : contractForms) {
					NStatisticalForm sform = new NStatisticalForm();
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
					
					statisticalFormService.save(partition.getPart(), sform);
				}
			} catch (Exception e) {
				logger.info("每日统计报表异常。。。。");
				e.printStackTrace();
			}
		}
	}
}
