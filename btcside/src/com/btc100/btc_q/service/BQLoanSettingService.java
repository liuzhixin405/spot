package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQLoanSetting;
import com.btc100.btc_q.vo.PagerModel;

public interface BQLoanSettingService {

	BQLoanSetting getLoanSettingByCode(String borrow, String pledge);

	void save(BQLoanSetting setting);
	
	void update(BQLoanSetting setting);
	
	List<BQLoanSetting> findAll();
	
	/************************************ for read only **************************************/
	
	BQLoanSetting readLoanSettingByCode(String borrow, String pledge);
	
	BQLoanSetting readOne();
	
	BQLoanSetting readOne(long id);
	
	PagerModel readAll();
	
	List<BQLoanSetting> readAllList();
	
}

