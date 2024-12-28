package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQManagerMenu;
import com.btc100.btc_q.vo.PagerModel;

public interface BQManagerMenuService {

	int add(BQManagerMenu menu);

	int update(BQManagerMenu menu);

	int delById(int id);

	
	List<BQManagerMenu> findAllList(int state);
	
	
	/************************************ for read only **************************************/
	
	BQManagerMenu readById(int id);

	BQManagerMenu readByMenuNo(String menuNo);
	
	PagerModel readPagerModel();
}
