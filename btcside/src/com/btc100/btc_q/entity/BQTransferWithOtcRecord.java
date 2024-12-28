package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_transfer_with_otc_record")
public class BQTransferWithOtcRecord implements Serializable {

	private static final long serialVersionUID = -4719597295292472012L;
	
	private long id;
	private long f_user_id;
	private int currency_type_id;
	private String currency_name;
	private BigDecimal frozen_amount;
	private BigDecimal amount;
	private int direction;
	private int state;
	private String note = "";
	private long record_time;
	private long oper_time;
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long value) {
		id = value;
	}
	
	public long getF_user_id() {
		return f_user_id;
	}
	
	public void setF_user_id(long uid) {
		f_user_id = uid;
	}

	public int getCurrency_type_id() {
		return currency_type_id;
	}

	public void setCurrency_type_id(int currency_type_id) {
		this.currency_type_id = currency_type_id;
	}

	public String getCurrency_name() {
		return currency_name;
	}

	public void setCurrency_name(String currency_name) {
		this.currency_name = currency_name;
	}

	public BigDecimal getFrozen_amount() {
		return frozen_amount;
	}

	public void setFrozen_amount(BigDecimal frozen_amount) {
		this.frozen_amount = frozen_amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
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

	public void setRecord_time(long record_time) {
		this.record_time = record_time;
	}

	public long getOper_time() {
		return oper_time;
	}

	public void setOper_time(long oper_time) {
		this.oper_time = oper_time;
	}

}
