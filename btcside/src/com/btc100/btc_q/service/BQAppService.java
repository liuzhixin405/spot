package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQApp;

public interface BQAppService {
	
	public void addIOSTimes();
	
	public void addAndroidTimes();
	
	public void update(BQApp o);
	
	
	/************************************ for read only **************************************/
	
	public BQApp readOne();
	
	public BQApp readByPlatform(String platform);
	
	public List<BQApp> readAll();
}
