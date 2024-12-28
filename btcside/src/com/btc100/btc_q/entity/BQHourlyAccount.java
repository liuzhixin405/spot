package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_hourly_account")
public class BQHourlyAccount implements Serializable {

	private static final long serialVersionUID = -2829549389539260088L;

	private long id;
	private long user_id; // 用户ID
	private int currency_id; // 币种ID
	private BigDecimal currency_amount; // 币种数量
	private long hour_time; // 记录时间

	public BQHourlyAccount() {
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public int getCurrency_id() {
		return currency_id;
	}

	public void setCurrency_id(int currency_id) {
		this.currency_id = currency_id;
	}

	public BigDecimal getCurrency_amount() {
		return currency_amount;
	}

	public void setCurrency_amount(BigDecimal currency_amount) {
		this.currency_amount = currency_amount;
	}

	public long getHour_time() {
		return hour_time;
	}

	public void setHour_time(long hour_time) {
		this.hour_time = hour_time;
	}

}
