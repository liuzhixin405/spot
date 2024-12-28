package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.NContractPartition;
import com.btc100.btc_q.vo.PagerModel;

public interface NContractPartitionService {
	
	public void update(NContractPartition o);
	
	public NContractPartition getByPartition(String partition);
	
	public List<NContractPartition> findAll();
	
	
	/************************************ for read only **************************************/
	
	public NContractPartition readByPartition(String partition);
	
	public List<NContractPartition> readAll();
	
	public List<NContractPartition> readUpdateAll();
	
	public PagerModel readAllPages();
}
