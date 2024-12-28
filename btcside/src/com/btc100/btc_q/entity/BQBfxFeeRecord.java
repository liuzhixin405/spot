package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_bfx_fee_record")
public class BQBfxFeeRecord implements Serializable {
	
	private static final long serialVersionUID = -3872410151206936665L;
	
	private long id;
	private int coin_id;
	private String f_code;
	private String part = "";
	private long f_user_id;
	private long trade_time;
	private BigDecimal fee;
	private BigDecimal amount;
	private BigDecimal price;
	private BigDecimal discount;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCoin_id() {
		return coin_id;
	}

	public void setCoin_id(int coin_id) {
		this.coin_id = coin_id;
	}

	public String getF_code() {
		return f_code;
	}

	public void setF_code(String f_code) {
		this.f_code = f_code;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public long getTrade_time() {
		return trade_time;
	}

	public void setTrade_time(long trade_time) {
		this.trade_time = trade_time;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}
}
