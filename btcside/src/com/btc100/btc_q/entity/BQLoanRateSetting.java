package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_loan_rate_setting")
public class BQLoanRateSetting implements Serializable {

	private static final long serialVersionUID = 8609801548465756923L;
	
	private Long id;
	private long loan_setting_id;	// 借贷设置Id
	private int cycle;				// 周期（天数）
	private BigDecimal day_rate;	// 利率（日）
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public BigDecimal getDay_rate() {
		return day_rate;
	}

	public void setDay_rate(BigDecimal day_rate) {
		this.day_rate = day_rate;
	}

	public long getLoan_setting_id() {
		return loan_setting_id;
	}

	public void setLoan_setting_id(long loan_setting_id) {
		this.loan_setting_id = loan_setting_id;
	}
	
	
	
}
