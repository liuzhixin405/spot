package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户合约表/持仓表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "n_member_transaction")
public class NMemberTransaction implements Serializable {

	private static final long serialVersionUID = 5292791950900655808L;

	@Id
	@GeneratedValue
	private long mt_id;// 用户合约挂单号
	private long transaction_id;// 合约id
	private String transaction_code;// 合约号
	private long f_user_id;// 用户ID
	private int trade_type_id;// 成交类型(1:多单开仓,买多/2:空头开仓,卖空)
	private BigDecimal trade_multiple;// 杠杆
	private BigDecimal hold_count;// 持仓量
	private BigDecimal lock_count;// 冻结量
	private BigDecimal average_price;// 合约均价
	private BigDecimal deposit;// 保证金
	private BigDecimal deposit2;// 追加保证金
	private BigDecimal commission;// 佣金
	private BigDecimal f_fromremain = BigDecimal.ZERO; // 来自余额的
	private BigDecimal f_min_frozenremain = BigDecimal.ZERO;
	private BigDecimal f_frozenremain = BigDecimal.ZERO; //冻结备用金
	private BigDecimal f_totalmoney = BigDecimal.ZERO; // 持仓总价值
	private BigDecimal f_lockmoney = BigDecimal.ZERO; // 冻结总价值
	private BigDecimal f_frozen_count = BigDecimal.ZERO; // 冻结备用金所占持仓量
	private int f_type_forjs = 0;
	private int flag = 0;
	private int flag_time = 0;

	public NMemberTransaction() {}

	public NMemberTransaction(long transactionId, String transactionCode, long fUserId, int tradeTypeId, BigDecimal tradeMultiple, BigDecimal holdCount,
			BigDecimal lockCount, BigDecimal averagePrice, BigDecimal deposit, BigDecimal deposit2, BigDecimal commission, BigDecimal ffromremain,
			BigDecimal totalmoney, BigDecimal lockmoney, int ijslx) {
		transaction_id = transactionId;
		transaction_code = transactionCode;
		f_user_id = fUserId;
		trade_type_id = tradeTypeId;
		trade_multiple = tradeMultiple;
		hold_count = holdCount;
		lock_count = lockCount;
		average_price = averagePrice;
		this.deposit = deposit;
		this.deposit2 = deposit2;
		this.commission = commission;
		f_fromremain = ffromremain;
		f_totalmoney = totalmoney;
		f_lockmoney = lockmoney;
		f_type_forjs = ijslx;
	}

	public long getMt_id() {
		return mt_id;
	}

	public void setMt_id(long mtId) {
		mt_id = mtId;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transactionId) {
		transaction_id = transactionId;
	}

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String transactionCode) {
		transaction_code = transactionCode;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public int getTrade_type_id() {
		return trade_type_id;
	}

	public void setTrade_type_id(int tradeTypeId) {
		trade_type_id = tradeTypeId;
	}

	public BigDecimal getTrade_multiple() {
		return trade_multiple;
	}

	public void setTrade_multiple(BigDecimal tradeMultiple) {
		trade_multiple = tradeMultiple;
	}

	public BigDecimal getHold_count() {
		return hold_count;
	}

	public void setHold_count(BigDecimal holdCount) {
		hold_count = holdCount;
	}

	public BigDecimal getLock_count() {
		return lock_count;
	}

	public void setLock_count(BigDecimal lockCount) {
		lock_count = lockCount;
	}

	public BigDecimal getAverage_price() {
		return average_price;
	}

	public void setAverage_price(BigDecimal averagePrice) {
		average_price = averagePrice;
	}

	public BigDecimal getDeposit() {
		return deposit;
	}

	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}

	public BigDecimal getDeposit2() {
		return deposit2;
	}

	public void setDeposit2(BigDecimal deposit2) {
		this.deposit2 = deposit2;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getF_fromremain() {
		return f_fromremain;
	}

	public void setF_fromremain(BigDecimal fFromremain) {
		f_fromremain = fFromremain;
	}
	
	public BigDecimal  getF_frozenremain() {
		return f_frozenremain;
	}
	
	public void setF_frozenmain(BigDecimal fFrozenremain) {
		f_frozenremain = fFrozenremain;
	}

	public BigDecimal getF_totalmoney() {
		return f_totalmoney;
	}

	public void setF_totalmoney(BigDecimal fTotalmoney) {
		f_totalmoney = fTotalmoney;
	}

	public BigDecimal getF_lockmoney() {
		return f_lockmoney;
	}

	public void setF_lockmoney(BigDecimal fLockmoney) {
		f_lockmoney = fLockmoney;
	}

	public int getF_type_forjs() {
		return f_type_forjs;
	}

	public void setF_type_forjs(int fTypeForjs) {
		f_type_forjs = fTypeForjs;
	}
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public BigDecimal getF_frozen_count() {
		return f_frozen_count;
	}
	
	public void setF_frozen_count(BigDecimal count) {
		f_frozen_count = count;
	}
	
	public BigDecimal getF_min_frozenremain() {
		return f_min_frozenremain;
	}
	
	public void setF_min_frozenremain(BigDecimal f_min_frozenremain) {
		this.f_min_frozenremain = f_min_frozenremain; 
	}

	public int getFlag_time() {
		return flag_time;
	}

	public void setFlag_time(int flag_time) {
		this.flag_time = flag_time;
	}
}
