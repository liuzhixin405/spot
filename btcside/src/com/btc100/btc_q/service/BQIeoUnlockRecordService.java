package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.entity.BQIeoRechargeRecord;
import com.btc100.btc_q.entity.BQIeoUnlockRecord;
import com.btc100.btc_q.entity.BQIeoUnlockSetting;

public interface BQIeoUnlockRecordService {
	
	PagerModel findRecords(long uid, long startTime, long endTime);
	
	List<BQIeoUnlockRecord> findRecords(long uid, int limit);
	
	void unlockIeo(BQIeoRechargeRecord record, BQIeoUnlockSetting setting) throws ServiceException;

}
