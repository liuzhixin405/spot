package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.VMemberInviteRebateLog;

public interface VMemberInviteRebateLogService {

	long getTime();
	
	VMemberInviteRebateLog createAndSaveRecord(long userId, long childUserId, BigDecimal tradeFee, long tradeUserId, 
			long tradeId, String vCode, long maxLevelId, int currencyId) throws Exception;

}
