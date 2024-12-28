package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQIeoRechargeRecord;
import com.btc100.btc_q.vo.PagerModel;

public interface BQIeoRechargeRecordService {

	PagerModel findRecords(long uid);
	
	BQIeoRechargeRecord readRecord(long id);
	
	List<BQIeoRechargeRecord> findNeedUnlockRecordsByTime(long beginTime, long endTime);
	
}
