package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.exception.ServiceException;

public interface NOverFlowService {

	public void overFlowDelegate(String partition, int diection, BigDecimal qpj, int digits, NMemberTransaction trans, BQMemberLevel userLevel, BigDecimal coefficient) throws ServiceException;
}
