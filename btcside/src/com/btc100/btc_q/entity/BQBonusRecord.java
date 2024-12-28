package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_bonus_record")
public class BQBonusRecord implements Serializable {
	
	private static final long serialVersionUID = 2369920995700046781L;
	
	private long id;
	private long f_user_id;
	private int currency_type_id;
	private BigDecimal rate;
	private BigDecimal amount;
	private BigDecimal total_fee;
	private BigDecimal total_amount;
	private BigDecimal hold_amount;
	private BigDecimal frozen_amount;
	private String day;
	private long record_time;
	private String note = "";

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
	
	public void setF_user_id(long uid) {
		this.f_user_id = uid;
	}
	
	public int getCurrency_type_id() {
		return currency_type_id;
	}
	
	public void setCurrency_type_id(int currencyId) {
		this.currency_type_id = currencyId;
	}
	
	public BigDecimal getRate() {
		return rate;
	}
	
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public BigDecimal getTotal_fee() {
		return total_fee;
	}
	
	public void setTotal_fee(BigDecimal fee) {
		this.total_fee = fee;
	}
	
	public BigDecimal getTotal_amount() {
		return total_amount;
	}
	
	public void setTotal_amount(BigDecimal amount) {
		this.total_amount = amount;
	}
	
	public BigDecimal getHold_amount() {
		return hold_amount;
	}
	
	public void setHold_amount(BigDecimal amount) {
		this.hold_amount = amount;
	}
	
	public BigDecimal getFrozen_amount() {
		return frozen_amount;
	}
	
	public void setFrozen_amount(BigDecimal amount) {
		this.frozen_amount = amount;
	}
	
	public String getDay() {
		return day;
	}
	
	public void setDay(String day) {
		this.day = day;
	}
	
	public long getRecord_time() {
		return record_time;
	}
	
	public void setRecord_time(long time) {
		this.record_time = time;
	}
	
	public String getNote() {
		return this.note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
}
