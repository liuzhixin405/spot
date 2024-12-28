package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_bfx_fee_setting")
public class BQBfxFeeSetting implements Serializable {
	
	private static final long serialVersionUID = 1483934196570475632L;
	
	private Long id;
	private int vip_state;
	private int fee_state;
	private int coin_id;
	private BigDecimal discount = BigDecimal.ONE;
	private BigDecimal bfx_avg_price = BigDecimal.ZERO;
	private long update_pirce_time;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVip_state() {
		return vip_state;
	}

	public void setVip_state(int vip_state) {
		this.vip_state = vip_state;
	}

	public int getFee_state() {
		return fee_state;
	}

	public void setFee_state(int fee_state) {
		this.fee_state = fee_state;
	}

	public int getCoin_id() {
		return coin_id;
	}

	public void setCoin_id(int coin_id) {
		this.coin_id = coin_id;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getBfx_avg_price() {
		return bfx_avg_price;
	}

	public void setBfx_avg_price(BigDecimal bfx_avg_price) {
		this.bfx_avg_price = bfx_avg_price;
	}

	public long getUpdate_pirce_time() {
		return update_pirce_time;
	}

	public void setUpdate_pirce_time(long update_pirce_time) {
		this.update_pirce_time = update_pirce_time;
	}

}
