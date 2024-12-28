package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "n_transaction_rate")
public class NTransactionRate implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String transaction_code;
	private BigDecimal uprate;
	private BigDecimal downrate;

	public NTransactionRate() {
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long value) {
		id = value;
	}

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String value) {
		transaction_code = value;
	}

	public BigDecimal getUprate() {
		return uprate;
	}

	public void setUprate(BigDecimal uprate) {
		this.uprate = uprate;
	}

	public BigDecimal getDownrate() {
		return downrate;
	}

	public void setDownrate(BigDecimal downrate) {
		this.downrate = downrate;
	}

}
