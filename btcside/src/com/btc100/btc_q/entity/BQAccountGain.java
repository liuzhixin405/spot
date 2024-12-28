package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_account_gain")
public class BQAccountGain implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long f_gain_id;
	private long f_user_id;
	private long transaction_id;
	private String transaction_code;
	private BigDecimal f_btc;

	public BQAccountGain() {
	}

	public long getF_gain_id() {
		return f_gain_id;
	}

	public void setF_gain_id(long fGainId) {
		f_gain_id = fGainId;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transactionId) {
		transaction_id = transactionId;
	}

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String transactionCode) {
		transaction_code = transactionCode;
	}

	public BigDecimal getF_btc() {
		return f_btc;
	}

	public void setF_btc(BigDecimal fBtc) {
		f_btc = fBtc;
	}

}
