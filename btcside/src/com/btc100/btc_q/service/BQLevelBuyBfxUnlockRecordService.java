package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.entity.BQLevelBuyBfxUnlockRecord;
import com.btc100.btc_q.entity.BQMemberLevelBuyAmountLog;
import com.btc100.btc_q.entity.BQMemberLevelBuySetting;

public interface BQLevelBuyBfxUnlockRecordService {
	
	PagerModel findRecords(long uid, long startTime, long endTime);
	
	List<BQLevelBuyBfxUnlockRecord> findRecords(long uid, int limit);
	
	void unlockBfx(BQMemberLevelBuyAmountLog record, BQMemberLevelBuySetting setting, int currencyId) throws ServiceException;

}
