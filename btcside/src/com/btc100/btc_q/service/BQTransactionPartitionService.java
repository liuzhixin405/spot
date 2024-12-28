package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQTransactionPartition;
import com.btc100.btc_q.vo.PagerModel;

public interface BQTransactionPartitionService {
	
	public void delete(int id);
	
	public void update(BQTransactionPartition o);
	
	
	/************************************ for read only **************************************/
	
	public List<BQTransactionPartition> readAll();
	
	public BQTransactionPartition get(int id);
	
	public PagerModel readPager();
}
