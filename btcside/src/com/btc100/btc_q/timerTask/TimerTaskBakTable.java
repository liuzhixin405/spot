package com.btc100.btc_q.timerTask;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.service.BQAccountAmountLogService;
import com.btc100.btc_q.service.BQHourlyAccountService;

/**
 * 备份数据表(30天前的数据)
 * 
 * @author Administrator
 * 
 */
public class TimerTaskBakTable extends BaseTask {

	@Autowired
	private BQAccountAmountLogService bqAccountAmountLogService;
	@Autowired
	private BQHourlyAccountService bqHourlyAccountService;

	public void run() {

		long currHourMillis = System.currentTimeMillis() / 1000 / 3600 * 3600;
		long bakEndTime = currHourMillis - 90 * 24 * 3600L;

		bqAccountAmountLogService.createAccountAmountLogBak();
		bqHourlyAccountService.createHourlyAccountBak();

		bqAccountAmountLogService.insertAccountAmountLogBak(bakEndTime);
		bqHourlyAccountService.insertHourlyAccountBak(bakEndTime);
	}

}
