package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户监控
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_user_monitor")
public class BQUserMonitor implements Serializable {
	
	private static final long serialVersionUID = 2900065772954487160L;

	private long f_user_id;
	private BigDecimal threshold;
	private BigDecimal price_rate;

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		this.f_user_id = fUserId;
	}
	
	public BigDecimal getThreshold() {
		return threshold;
	}

	public void setThreshold(BigDecimal threshold) {
		this.threshold = threshold;
	}

	public BigDecimal getPrice_rate() {
		return price_rate;
	}

	public void setPrice_rate(BigDecimal price_rate) {
		this.price_rate = price_rate;
	}

}
