package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_bfx_min_hold")
public class BQBfxMinHold implements Serializable {
	
	private static final long serialVersionUID = -9101234417291041032L;
	
	private long id;
	private int coin_id;
	private long f_user_id;
	private BigDecimal min_hold;
	private long day_time;

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

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public BigDecimal getMin_hold() {
		return min_hold;
	}

	public void setMin_hold(BigDecimal min_hold) {
		this.min_hold = min_hold;
	}

	public long getDay_time() {
		return day_time;
	}

	public void setDay_time(long day_time) {
		this.day_time = day_time;
	}

}
