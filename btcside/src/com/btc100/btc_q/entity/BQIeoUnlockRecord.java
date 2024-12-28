package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_ieo_unlock_record")
public class BQIeoUnlockRecord implements Serializable {

	private static final long serialVersionUID = -2169263365519113684L;
	
	private long id;
	private long f_user_id;
	private int currency_type_id;
	private BigDecimal total_mine;
	private BigDecimal unlock_rate;
	private BigDecimal amount;
	private String day;
	private long record_time;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public int getCurrency_type_id() {
		return this.currency_type_id;
	}
	
	public void setCurrency_type_id(int typeId) {
		this.currency_type_id = typeId;
	}

	public long getF_user_id() {
		return f_user_id;
	}
	
	public void setF_user_id(long uid) {
		this.f_user_id = uid;
	}
	
	public BigDecimal getTotal_mine() {
		return total_mine;
	}
	
	public void setTotal_mine(BigDecimal total_mine) {
		this.total_mine = total_mine;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public BigDecimal getUnlock_rate() {
		return unlock_rate;
	}
	
	public void setUnlock_rate(BigDecimal unlock_rate) {
		this.unlock_rate = unlock_rate;
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

}
