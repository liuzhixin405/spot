package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "delivery_member_transaction_settle")
public class DeliveryMemberTransactionSettle implements Serializable {
	
	private static final long serialVersionUID = -4935405491794626491L;

	private long id;
	private long transaction_id;
	private String transaction_code;
	private long f_user_id;
	private int trade_type_id;
	private int trade_multiple;
	private BigDecimal hold_count;
	private BigDecimal lock_count;
	private BigDecimal average_price;
	private BigDecimal deposit;
	private BigDecimal profit_lost;
	private BigDecimal apportion;
	private long dateline;
	private BigDecimal trade_fee;

	public DeliveryMemberTransactionSettle() {
	}

	public DeliveryMemberTransactionSettle(long transactionId,
			String transactionCode, long fUserId, int tradeTypeId,
			int tradeMultiple, BigDecimal holdCount, BigDecimal lockCount,
			BigDecimal averagePrice, BigDecimal deposit, BigDecimal profitLost,
			BigDecimal apportion, long dateline, BigDecimal tradeFee) {
		transaction_id = transactionId;
		transaction_code = transactionCode;
		f_user_id = fUserId;
		trade_type_id = tradeTypeId;
		trade_multiple = tradeMultiple;
		hold_count = holdCount;
		lock_count = lockCount;
		average_price = averagePrice;
		this.deposit = deposit;
		profit_lost = profitLost;
		this.apportion = apportion;
		this.dateline = dateline;
		trade_fee = tradeFee;
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long value) {
		id = value;
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

	public int getTrade_multiple() {
		return trade_multiple;
	}

	public void setTrade_multiple(int tradeMultiple) {
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

	public BigDecimal getProfit_lost() {
		return profit_lost;
	}

	public void setProfit_lost(BigDecimal profitLost) {
		profit_lost = profitLost;
	}

	public BigDecimal getApportion() {
		return apportion;
	}

	public void setApportion(BigDecimal apportion) {
		this.apportion = apportion;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public BigDecimal getTrade_fee() {
		return trade_fee;
	}

	public void setTrade_fee(BigDecimal tradeFee) {
		trade_fee = tradeFee;
	}

}
