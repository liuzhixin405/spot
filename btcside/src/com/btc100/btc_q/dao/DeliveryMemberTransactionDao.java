package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.DeliveryMemberTransaction;

public interface DeliveryMemberTransactionDao<T> extends BaseDaoI<T> {
	/**当查询系统持仓时候杠杆参数传入0*/
	DeliveryMemberTransaction getMemberTransaction(String code, long fUserId,int trade_type_id,int mutiple);
	
	DeliveryMemberTransaction getMemberTransactionById(long id);
	
	int updateTransPriceByID(long mtid, BigDecimal changePrice);

	/**增仓操作*/
	int updateMemberTransByID(long mtid, BigDecimal thisHoldCount, BigDecimal thisFreeze,BigDecimal thisPrice, BigDecimal thisDeposit, BigDecimal deposit2, BigDecimal thisFromremain,
			BigDecimal thisFrozenremain, BigDecimal thisFrozenCount, BigDecimal thisMinFrozenremain,BigDecimal newcommission);
	
	/**删除持仓为零的仓位*/
	int deleteZEROTran(DeliveryMemberTransaction trans);
	
	public List<Object[]> createSQL(String sql);
	
	public void changeFlagForBC(long transactionId, BigDecimal indexPrice, BigDecimal coefficient);
	
	public List<DeliveryMemberTransaction> findBCTransactions(long transactionId);
	
	public List<DeliveryMemberTransaction> findMaxProfitRateTransactions(long transactionId, int tradeType, BigDecimal price);
	
	public BigDecimal findSumDepositByUser(String code, long userId);
	
	public BigDecimal findSumMinDepositByUser(String code, long userId, long except);
	
	public BigDecimal findSumHoldsByUser(String code, long userId, long except);
	
	public BigDecimal findSumRemainByUser(String code, long userId, long except);
}

