package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQMemberLoginLog;

public interface BQMemberLoginLogService {
	
	void save(BQMemberLoginLog bqMemberLoginLog);
	
	Long getTotalLoginUserByTime(Long startTime, Long endTime);
	
	
	/************************************ for read only **************************************/
	
	List<BQMemberLoginLog> readTodayByUserId(long startTime, long endTime, long userId);

}
