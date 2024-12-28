package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_member_transaction_overload_log")
public class VMemberTransactionOverloadLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6231188224971094718L;


	public VMemberTransactionOverloadLog() {
	}

	private Long id;
	private Long f_user_id;
	private Long transaction_id;
	private String transaction_code;
	private BigDecimal trade_price;
	private BigDecimal trade_count;
	private BigDecimal trade_deposit;
	private BigDecimal trade_overload_price;
	private BigDecimal trade_force_price;
	private BigDecimal trade_fee;
	private Long delegation_id;
	private Long dateline;
	private int direction;
	private BigDecimal balance;
	private long pending_no;
	private BigDecimal f_fromremain;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long value) {
		id = value;
	}

	public Long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(Long value) {
		f_user_id = value;
	}

	public Long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(Long value) {
		transaction_id = value;
	}

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String value) {
		transaction_code = value;
	}

	public BigDecimal getTrade_price() {
		return trade_price;
	}

	public void setTrade_price(BigDecimal value) {
		trade_price = value;
	}

	public BigDecimal getTrade_count() {
		return trade_count;
	}

	public void setTrade_count(BigDecimal value) {
		trade_count = value;
	}

	public BigDecimal getTrade_deposit() {
		return trade_deposit;
	}

	public void setTrade_deposit(BigDecimal value) {
		trade_deposit = value;
	}

	public BigDecimal getTrade_overload_price() {
		return trade_overload_price;
	}

	public void setTrade_overload_price(BigDecimal value) {
		trade_overload_price = value;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int value) {
		direction = value;
	}

	public Long getDateline() {
		return dateline;
	}

	public void setDateline(Long value) {
		dateline = value;
	}

	public Long getDelegation_id() {
		return delegation_id;
	}

	public void setDelegation_id(Long value) {
		delegation_id = value;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public long getPending_no() {
		return pending_no;
	}

	public void setPending_no(long pending_no) {
		this.pending_no = pending_no;
	}

	public BigDecimal getF_fromremain() {
		return f_fromremain;
	}

	public void setF_fromremain(BigDecimal f_fromremain) {
		this.f_fromremain = f_fromremain;
	}

	public BigDecimal getTrade_force_price() {
		return trade_force_price;
	}

	public void setTrade_force_price(BigDecimal trade_force_price) {
		this.trade_force_price = trade_force_price;
	}

	public BigDecimal getTrade_fee() {
		return trade_fee;
	}

	public void setTrade_fee(BigDecimal trade_fee) {
		this.trade_fee = trade_fee;
	}
}
