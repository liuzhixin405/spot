package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_redgift")
public class BQRedgift implements Serializable {

	private static final long serialVersionUID = 8266770762059428079L;
	private long f_id;
	private BigDecimal f_amount;
	private String f_code;
	private long f_time;
	private int f_state;
	private long f_user_id;

	public BQRedgift() {
	}

	@Id
	@GeneratedValue
	public long getF_id() {
		return f_id;
	}

	public void setF_id(long value) {
		f_id = value;
	}

	public BigDecimal getF_amount() {
		return f_amount;
	}

	public void setF_amount(BigDecimal value) {
		f_amount = value;
	}

	public String getF_code() {
		return f_code;
	}

	public void setF_code(String value) {
		f_code = value;
	}

	public long getF_time() {
		return f_time;
	}

	public void setF_time(long value) {
		f_time = value;
	}

	public int getF_state() {
		return f_state;
	}

	public void setF_state(int value) {
		f_state = value;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long value) {
		f_user_id = value;
	}

}
