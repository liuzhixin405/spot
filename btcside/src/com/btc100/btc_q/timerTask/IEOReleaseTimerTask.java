package com.btc100.btc_q.timerTask;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.entity.BQIeoRechargeRecord;
import com.btc100.btc_q.entity.BQIeoUnlockSetting;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQIeoRechargeRecordService;
import com.btc100.btc_q.service.BQIeoUnlockRecordService;
import com.btc100.btc_q.service.BQIeoUnlockSettingService;
import com.btc100.btc_q.util.TimeUtil;

public class IEOReleaseTimerTask {

	@Autowired
	private BQIeoUnlockSettingService bqIeoUnlockSettingService;
	@Autowired
	private BQIeoUnlockRecordService bqIeoUnlockRecordService;
	@Autowired
	private BQIeoRechargeRecordService bqIeoRechargeRecordService;
	
	public void run() {
		
		long now = System.currentTimeMillis() + 1000;
		long morning = TimeUtil.getTimesmorning(now);
		BQIeoUnlockSetting setting = bqIeoUnlockSettingService.getByTime(morning);
		if (setting == null) return;
		
		List<BQIeoRechargeRecord> records = bqIeoRechargeRecordService.findNeedUnlockRecordsByTime(setting.getLock_time_begin(), setting.getLock_time_end());
		for (BQIeoRechargeRecord record : records) {
			try {
				bqIeoUnlockRecordService.unlockIeo(record, setting);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
	}
}
