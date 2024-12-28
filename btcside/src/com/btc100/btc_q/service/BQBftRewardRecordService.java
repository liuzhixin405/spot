package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQBftRewardRecord;
import com.btc100.btc_q.vo.PagerModel;
import com.btc100.btc_q.vo.RewardRecordVo;

public interface BQBftRewardRecordService {

	void bonus(BQBftRewardRecord record);
	
	
	/************************************ for read only **************************************/
	
	PagerModel findRecords(long uid, long startTime, long endTime);
	
	List<RewardRecordVo> readRewardAndMineRecords(long uid, int currencyId, int limit);
}
