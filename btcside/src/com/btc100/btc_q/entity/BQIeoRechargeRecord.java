package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_ieo_recharge_record")
public class BQIeoRechargeRecord implements Serializable {

	private static final long serialVersionUID = -498970655637048629L;
	
	private long id;
	private long f_user_id;
	private int currency_type_id;
	private int operation;
	private BigDecimal amount;
	private BigDecimal unlock_amount;
	private String note;
	private long record_time;
	private long update_time;

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
	
	public void setF_user_id(long id) {
		f_user_id = id;
	}
	
	public int getCurrency_type_id() {
		return currency_type_id;
	}
	
	public void setCurrency_type_id(int typeId) {
		currency_type_id = typeId;
	}
	
	public int getOperation() {
		return operation;
	}
	
	public void setOperation(int operation) {
		this.operation = operation;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public long getRecord_time() {
		return record_time;
	}
	
	public void setRecord_time(long time) {
		record_time = time;
	}

	public long getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(long update_time) {
		this.update_time = update_time;
	}

	public BigDecimal getUnlock_amount() {
		return unlock_amount;
	}

	public void setUnlock_amount(BigDecimal unlock_amount) {
		this.unlock_amount = unlock_amount;
	}

}
