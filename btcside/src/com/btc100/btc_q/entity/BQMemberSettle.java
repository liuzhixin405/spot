package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_settle")
public class BQMemberSettle implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BQMemberSettle() {
	}

	public BQMemberSettle(String transactionCode, long fUserId, long fSettleTime, BigDecimal fSettlePrice, BigDecimal fSettleAmount, BigDecimal fSettleGain,
			BigDecimal fSettleLoss, BigDecimal fSettleApportion, BigDecimal fsettlerate) {
		transaction_code = transactionCode;
		f_user_id = fUserId;
		f_settle_time = fSettleTime;
		f_settle_price = fSettlePrice;
		f_settle_amount = fSettleAmount;
		f_settle_gain = fSettleGain;
		f_settle_loss = fSettleLoss;
		f_settle_apportion = fSettleApportion;
		f_settle_rate = fsettlerate;
	}

	private BigDecimal f_settle_rate;

	public BigDecimal getF_settle_rate() {
		return f_settle_rate;
	}

	public void setF_settle_rate(BigDecimal fSettleRate) {
		f_settle_rate = fSettleRate;
	}

	private long id;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long value) {
		id = value;
	}

	private String transaction_code;

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String value) {
		transaction_code = value;
	}

	private long f_user_id;

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long value) {
		f_user_id = value;
	}

	private long f_settle_time;

	public long getF_settle_time() {
		return f_settle_time;
	}

	public void setF_settle_time(long value) {
		f_settle_time = value;
	}

	private BigDecimal f_settle_price;

	public BigDecimal getF_settle_price() {
		return f_settle_price;
	}

	public void setF_settle_price(BigDecimal value) {
		f_settle_price = value;
	}

	private BigDecimal f_settle_amount;

	public BigDecimal getF_settle_amount() {
		return f_settle_amount;
	}

	public void setF_settle_amount(BigDecimal value) {
		f_settle_amount = value;
	}

	private BigDecimal f_settle_gain;

	public BigDecimal getF_settle_gain() {
		return f_settle_gain;
	}

	public void setF_settle_gain(BigDecimal value) {
		f_settle_gain = value;
	}

	private BigDecimal f_settle_loss;

	public BigDecimal getF_settle_loss() {
		return f_settle_loss;
	}

	public void setF_settle_loss(BigDecimal value) {
		f_settle_loss = value;
	}

	private BigDecimal f_settle_apportion;

	public BigDecimal getF_settle_apportion() {
		return f_settle_apportion;
	}

	public void setF_settle_apportion(BigDecimal value) {
		f_settle_apportion = value;
	}

}
