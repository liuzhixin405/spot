package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQLoanRateSetting;
import com.btc100.btc_q.vo.PagerModel;

public interface BQLoanRateSettingService {
	
	BQLoanRateSetting getLoanRateSettingByLoanIdAndCycle(long id, int cycle);
	
	List<BQLoanRateSetting> findAllByLoanId(long id);

	void save(BQLoanRateSetting setting);
	
	void update(BQLoanRateSetting setting);
	
	void delete(long id);
	
	
	/************************************ for read only **************************************/
	
	BQLoanRateSetting readLoanRateSettingByLoanIdAndCycle(long id, int cycle);
	
	List<BQLoanRateSetting> readAllByLoanId(long id);
	
	PagerModel readPagerByLoanId(long id);
	
	BQLoanRateSetting readById(long id);
}
