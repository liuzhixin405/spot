package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQIndexDescription;
import com.btc100.btc_q.vo.PagerModel;

public interface BQIndexDescriptionService {

	int add(BQIndexDescription obj);

	int update(BQIndexDescription obj);

	int delById(int id);
	
	
	/************************************ for read only **************************************/

	BQIndexDescription readById(int id);
	
	BQIndexDescription readByCode(String code);

	PagerModel readPagerModel();

}
