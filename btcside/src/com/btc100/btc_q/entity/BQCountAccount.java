package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_count_account")
public class BQCountAccount implements Serializable {

	private static final long serialVersionUID = 7548929493197394164L;
	private long f_id;
	private BigDecimal f_amount;
	private int f_type;
	private String f_content;
	private long f_time;

	public BQCountAccount() {
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

	public String getF_content() {
		return f_content;
	}

	public void setF_content(String value) {
		f_content = value;
	}

	public long getF_time() {
		return f_time;
	}

	public void setF_time(long value) {
		f_time = value;
	}

}
