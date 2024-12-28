package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 交易记录对账表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "delivery_trade_log_reconcilia")
public class DeliveryTradeLogReconcilia implements Serializable {

	private static final long serialVersionUID = 5848016107686883975L;

	private long id;// ID
	private long transaction_id;// 合约号id
	private String transaction_code;// 合约号
	private BigDecimal trade_income;// 盈亏额
	private BigDecimal trade_fee;// 手续费
	private long f_user_id;// 用户ID
	private long time;// 每月月1号0点,unix时间格式

	public DeliveryTradeLogReconcilia() {

	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String transaction_code) {
		this.transaction_code = transaction_code;
	}

	public BigDecimal getTrade_income() {
		return trade_income;
	}

	public void setTrade_income(BigDecimal trade_income) {
		this.trade_income = trade_income;
	}

	public BigDecimal getTrade_fee() {
		return trade_fee;
	}

	public void setTrade_fee(BigDecimal trade_fee) {
		this.trade_fee = trade_fee;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
