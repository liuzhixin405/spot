package com.btc100.btc_q.service;

import java.math.BigDecimal;

import com.btc100.btc_q.entity.DeliveryMemberTransactionSettle;

public interface DeliveryMemberTransactionSettleService {

	public void saveSettle(DeliveryMemberTransactionSettle settle);

	public int updateSettle(BigDecimal fentanbi, String scode);
	
}
