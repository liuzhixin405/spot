package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_bft_reward_record")
public class BQBftRewardRecord implements Serializable {
	
	private static final long serialVersionUID = 2369920995700046781L;
	
	private long id;
	private long f_user_id;
	private int currency_type_id;
	private int reason;
	private BigDecimal amount;
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
	
	public int getReason() {
		return reason;
	}
	
	public void setReason(int reason) {
		this.reason = reason;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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
