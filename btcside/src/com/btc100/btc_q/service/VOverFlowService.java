package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.BQMemberLevel;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.exception.ServiceException;

public interface VOverFlowService {

	public void overFlowDelegate(int diection, BigDecimal qpj, int digits, VMemberTransaction trans, BQMemberLevel userLevel, BigDecimal coefficient) throws ServiceException;
}
