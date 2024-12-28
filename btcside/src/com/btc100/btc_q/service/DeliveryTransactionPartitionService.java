package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.DeliveryTransactionPartition;
import com.btc100.btc_q.vo.PagerModel;

public interface DeliveryTransactionPartitionService {
	
	public void delete(int id);
	
	public void update(DeliveryTransactionPartition o);
	
	
	/************************************ for read only **************************************/
	
	public List<DeliveryTransactionPartition> readAll();
	
	public DeliveryTransactionPartition get(int id);
	
	public PagerModel readPager();
}
