package com.btc100.btc_q.service;

import java.math.BigDecimal;
import com.btc100.btc_q.entity.BQMemberTransactionSettle;

public interface BQMemberTransactionSettleService {

	public void saveSettle(BQMemberTransactionSettle settle);

	public int updateSettle(BigDecimal fentanbi, String scode);
	
}
