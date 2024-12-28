package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.NTransactionPartition;
import com.btc100.btc_q.vo.PagerModel;

public interface NTransactionPartitionService {
	
	public void delete(String partition, int id);
	
	public void update(String partition, NTransactionPartition o);
	
	
	/************************************ for read only **************************************/
	
	public List<NTransactionPartition> readAll(String partition);
	
	public NTransactionPartition get(String partition, int id);
	
	public PagerModel readPager(String partition);
}
