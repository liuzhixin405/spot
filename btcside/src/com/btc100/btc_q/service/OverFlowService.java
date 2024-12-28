package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.exception.ServiceException;

public interface OverFlowService {

	public void overFlowDelegate(int diection, BigDecimal qpj, int digits, BQMemberTransaction trans, BQMemberLevel userLevel, BigDecimal coefficient) throws ServiceException;
}
