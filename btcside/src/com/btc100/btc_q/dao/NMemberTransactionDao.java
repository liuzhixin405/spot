package com.btc100.btc_q.dao;

import java.math.BigDecimal;
import java.util.List;

import com.btc100.btc_q.entity.NMemberTransaction;

public interface NMemberTransactionDao<T> extends BaseDaoI<T> {
	
	/**当查询系统持仓时候杠杆参数传入0*/
	NMemberTransaction getMemberTransaction(String partition, String code, long fUserId,int trade_type_id,int mutiple);
	
	NMemberTransaction getMemberTransactionById(String partition, long id);
	
	int updateTransPriceByID(String partition, long mtid, BigDecimal changePrice);

	/**增仓操作*/
	int updateMemberTransByID(String partition, long mtid, BigDecimal thisHoldCount, BigDecimal thisFreeze,BigDecimal thisPrice, BigDecimal thisDeposit, BigDecimal deposit2, BigDecimal thisFromremain,
			BigDecimal thisFrozenremain, BigDecimal thisFrozenCount, BigDecimal thisMinFrozenremain,BigDecimal newcommission);
	
	/**删除持仓为零的仓位*/
	int deleteZEROTran(String partition, NMemberTransaction trans);
	
	public List<Object[]> createSQL(String partition, String sql);
	
	public void changeFlagForBC(String partition, long transactionId, BigDecimal indexPrice, BigDecimal coefficient);
	
	public List<NMemberTransaction> findBCTransactions(String partition, long transactionId);
	
	public List<NMemberTransaction> findMaxProfitRateTransactions(String partition, long transactionId, int tradeType, BigDecimal price);
	
	public BigDecimal findSumDepositByUser(String partition, String code, long userId);
	
	public BigDecimal findSumMinDepositByUser(String partition, String code, long userId, long except);
	
	public BigDecimal findSumHoldsByUser(String partition, String code, long userId, long except);
	
	public BigDecimal findSumRemainByUser(String partition, String code, long userId, long except);
	
	public int insert(String partition, NMemberTransaction trans);
	
	public int delete(String partition, NMemberTransaction trans);
}

