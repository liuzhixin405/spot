package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQSurecome;
import com.btc100.btc_q.vo.PagerModel;

public interface BQSurecomeService {

	boolean login(String suname, String spwd) throws Exception;

	boolean surepwd2(String suname, String spwd) throws Exception;

	public BQSurecome findUserByParams(String username, String password) throws Exception;
	
	int add(BQSurecome obj);

	int update(BQSurecome obj);

	int delById(int id);

	void updateCoinsByName(String name, String coins);

	BQSurecome findById(int id);
	
	PagerModel findAllModel();
	
	BQSurecome getByName(String username);
	
	void clearLoginTime(String username);

	void clearGoogleLoginTime(String username);

}
