package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_redgift")
public class BQMemberRedgift implements Serializable {

	private static final long serialVersionUID = 9039592852919396993L;
	
	private long f_id;
	private BigDecimal f_amount;
	private int f_type;
	private String f_code;
	private int f_state;
	private long f_user_id;
	private long f_time;
	private long f_pendingcode;

	public BQMemberRedgift() {

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

	public int getF_type() {
		return f_type;
	}

	public void setF_type(int value) {
		f_type = value;
	}

	public String getF_code() {
		return f_code;
	}

	public void setF_code(String value) {
		f_code = value;
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

	public long getF_time() {
		return f_time;
	}

	public void setF_time(long value) {
		f_time = value;
	}

	public long getF_pendingcode() {
		return f_pendingcode;
	}

	public void setF_pendingcode(long fPendingcode) {
		f_pendingcode = fPendingcode;
	}

	
}
