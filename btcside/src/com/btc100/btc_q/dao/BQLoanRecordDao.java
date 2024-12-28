package com.btc100.btc_q.dao;

import java.math.BigDecimal;

public interface BQLoanRecordDao<T> extends BaseDaoI<T> {
	
	public BigDecimal getSumBorrow(long userId, int borrowId, int state);
	
	public BigDecimal getSumRecived(long userId, int borrowId);
	
	public BigDecimal getSumBorrowOut(long borrowUid, int borrowId, int state);
	
	public BigDecimal getSumInterest(long userId, int borrowId, int state);
	
	public BigDecimal getSumInterestObtain(long userId, int borrowId, int state);
	
	public BigDecimal getSumPaymentPledgeObtain(long borrowUid, int pledgeId, int state);
	
	public BigDecimal getSumPledge(long userId, int pledgeId, int state);
	
	public BigDecimal getSumReleasePledge(long userId, int pledgeId, int state);

	public BigDecimal getSumBorrowIncome(long borrowUid, int borrowId, int state);
	
}
