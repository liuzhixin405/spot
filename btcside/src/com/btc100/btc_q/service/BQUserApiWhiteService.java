package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQUserApiWhite;
import com.btc100.btc_q.vo.PagerModel;

public interface BQUserApiWhiteService {

	PagerModel find(long uid);

	BQUserApiWhite get(long uid);

	void save(BQUserApiWhite obj);

	void update(BQUserApiWhite obj);

	void delete(long uid);
	
	
	/************************************ for read only **************************************/

	List<BQUserApiWhite> readAll();
}
