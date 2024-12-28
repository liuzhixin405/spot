package com.btc100.btc_q.service;
import java.util.List;

import com.btc100.btc_q.entity.BQMemberLevelBuyAmountLog;
import com.btc100.btc_q.entity.BQMemberLevelBuySetting;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

public interface BQMemberLevelBuyAmountLogService {
	
	void save(BQMemberLevelBuyAmountLog level);

	void update(BQMemberLevelBuyAmountLog level);
	
	void delete(BQMemberLevelBuyAmountLog level);
	
	void updateAccountAndLog(BQMemberLevelBuyAmountLog bqmlba, String title, BQMemberLevelBuySetting setting) throws ServiceException;
	
	List<BQMemberLevelBuyAmountLog> findNeedUnlockRecordsByTime(long beginTime);

	
	/************************************ for read only **************************************/
	
	List<BQMemberLevelBuyAmountLog> readLevelBuyAmountLog(long uid, long from, String direct, long startTime, long endTime, int limit);
	
	PagerModel readLevelBuyAmountLogPager(long fUserId);
	
	PagerModel readAllForAdmin(long userId);
	
	Object readSumBfxLock(long userId);
}
