package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQCommonCreditLog;
import com.btc100.btc_q.vo.PagerModel;

public interface BQCommonCreditLogService {
	
	void save(BQCommonCreditLog bqCc);
	
	void saveCreditsAndUpdateLevel(BQCommonCreditLog bqCc, boolean withHiddenLevel, int operation);
	
	public long getTime();
	
	int count(long uid, int type);
	
	
	/************************************ for read only **************************************/
	
	List<BQCommonCreditLog> readByUserAndTime(long uid, long from, String direct, long startTime, long endTime, int limit);
	
	PagerModel readVipRecords(long fUserId);
	
	PagerModel readAddOrSubRecords(long fUserId);
	
	int readCount(long uid, int type);

}
