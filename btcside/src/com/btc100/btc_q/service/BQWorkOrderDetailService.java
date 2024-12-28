package com.btc100.btc_q.service;

import java.util.List;

import com.btc100.btc_q.entity.BQWorkOrderDetail;

public interface BQWorkOrderDetailService {

	void saveWorkOrderDetail(BQWorkOrderDetail orderDetail);
	
	
	/************************************ for read only **************************************/
	
	List<BQWorkOrderDetail> readListByOrderId(long orderId);
	
	long readTime();

}
