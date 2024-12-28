package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.exception.ServiceException;

public interface DeliveryOverFlowService {

	public void overFlowDelegate(int diection, BigDecimal qpj, int digits, DeliveryMemberTransaction trans,
			BQMemberLevel userLevel, BigDecimal coefficient) throws ServiceException;
	
	public void deliveryDelegate(BigDecimal price, int digits, DeliveryMemberTransaction trans, 
			BQMemberLevel userLevel, BQMember user, BigDecimal coefficient) throws ServiceException;

}