package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQWorkOrder;
import com.btc100.btc_q.vo.PagerModel;

public interface BQWorkOrderService {
	
	public PagerModel findOrdersByState(String state);
	
	public PagerModel findOrdersByType(String type);

	public void saveWorkOrder(BQWorkOrder workOrder, BQMember user);
	
	public void updateWorkOrder(BQWorkOrder workOrder);

	
	/************************************ for read only **************************************/

	public List<BQWorkOrder> readByUserAndSize(long userId, long from, String direct, long startTime, long endTime, int limit);
	
	public List<BQWorkOrder> readListByState(String state, long userId);
	
	public PagerModel readOrderByUserId(long userId, String direction);
	
	public long readTime();
	
	public BQWorkOrder readById(long id);
	
}
