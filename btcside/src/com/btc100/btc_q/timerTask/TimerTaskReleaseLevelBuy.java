package com.btc100.btc_q.timerTask;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQMemberLevelBuyAmountLog;
import com.btc100.btc_q.entity.BQMemberLevelBuySetting;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.service.BQLevelBuyBfxUnlockRecordService;
import com.btc100.btc_q.service.BQMemberLevelBuyAmountLogService;
import com.btc100.btc_q.service.BQMemberLevelBuySettingService;
import com.btc100.btc_q.util.TimeUtil;

public class TimerTaskReleaseLevelBuy {

	@Autowired
	private BQMemberLevelBuySettingService bqMemberLevelBuySettingService;
	@Autowired
	private BQLevelBuyBfxUnlockRecordService bqLevelBuyBfxUnlockRecordService;
	@Autowired
	private BQMemberLevelBuyAmountLogService bqMemberLevelBuyAmountLogService;
	@Autowired
	private BQCurrencyTypeService bqCurrencyTypeService;
	
	public void run() {
		
		long now = System.currentTimeMillis() + 1000;
		long morning = TimeUtil.getTimesmorning(now);
		BQMemberLevelBuySetting setting = bqMemberLevelBuySettingService.get();
		if (setting == null) return;
		
		BQCurrencyType coin = bqCurrencyTypeService.getByTypeName("BFX");
		if (coin == null) return;
		
		long endTime = morning - setting.getStart_unlock_gap_day() * 24 * 3600; 
		List<BQMemberLevelBuyAmountLog> records = bqMemberLevelBuyAmountLogService.findNeedUnlockRecordsByTime(endTime);
		for (BQMemberLevelBuyAmountLog record : records) {
			try {
				bqLevelBuyBfxUnlockRecordService.unlockBfx(record, setting, coin.getType_id());
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
	}
}
