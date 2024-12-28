package com.btc100.btc_q.service;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.DeliveryMemberInviteRebateLog;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryMemberInviteRebateLogService {

	long getTime();
	
	BigDecimal getSumRebateByTime(long startTime, long endTime, long userId, int currencyId);
	
	DeliveryMemberInviteRebateLog createAndSaveRecord(long userId, long childUserId, BigDecimal tradeFee, long tradeUserId, 
			long tradeId, String bqCode, long maxLevelId, int currencyId, BQMemberLevel childLevel, BQMemberLevel userLevel) throws Exception;
	
	
	BigDecimal getSumRebateByTimeAndUser(String code, long startTime, long endTime, long userId, int currencyId);
	
	List<Object[]> findByTime(long startTime, long endTime, int currencyId);
	
	
	/************************************ for read only **************************************/
	
	PagerModel readAll(String code, long userId);
	
	BigDecimal readSumRebateByTime(long startTime, long endTime, long userId, int currencyId);
	
	BigDecimal readSumRebateByUser(long userId, int currencyId);
	
	BigDecimal readSumRebateByTimeAndUser(String code, long startTime, long endTime, long userId, int currencyId);
	
	BigDecimal readSumRebateByUserAndSub(long userId, long subUserId, int currencyId);
	
	BigDecimal readSumRebateByTimeAndSub(long startTime, long endTime, long userId, long subUserId, int currencyId);
}
