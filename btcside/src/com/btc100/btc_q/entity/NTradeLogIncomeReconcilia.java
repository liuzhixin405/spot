package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 交易收益统计表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "n_trade_log_income_reconcilia")
public class NTradeLogIncomeReconcilia implements Serializable {

	private static final long serialVersionUID = 6540615752509352611L;
	
	private long id;
	private long f_user_id;// 用户ID
	private BigDecimal income;// 收益
	private long cut_off_time;// 截止时间,unix时间格式

	public NTradeLogIncomeReconcilia() {

	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public long getCut_off_time() {
		return cut_off_time;
	}

	public void setCut_off_time(long cut_off_time) {
		this.cut_off_time = cut_off_time;
	}

	
}
