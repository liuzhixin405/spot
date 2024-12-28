package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_funding_fee_setting")
public class VFundingFeeSetting implements Serializable {

	private static final long serialVersionUID = 2762310992720556598L;

	private Long id;
	private String f_code;
	private boolean running;
	private String running_time;
	private BigDecimal interest_rate_diff = BigDecimal.ZERO;
	private BigDecimal divisor = BigDecimal.ONE;
	private BigDecimal clamp_up = new BigDecimal(0.2);
	private BigDecimal clamp_down = new BigDecimal(-0.2);
	private boolean fixed_fee_open = false;// 固定资金费率
	private BigDecimal fixed_fee_rate = BigDecimal.ZERO;// 固定资金费率

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getF_code() {
		return f_code;
	}

	public void setF_code(String code) {
		f_code = code;
	}

	public boolean getRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public String getRunning_time() {
		return running_time;
	}

	public void setRunning_time(String running_time) {
		this.running_time = running_time;
	}

	public BigDecimal getInterest_rate_diff() {
		return interest_rate_diff;
	}

	public void setInterest_rate_diff(BigDecimal interest_rate_diff) {
		this.interest_rate_diff = interest_rate_diff;
	}

	public BigDecimal getDivisor() {
		return divisor;
	}

	public void setDivisor(BigDecimal divisor) {
		this.divisor = divisor;
	}

	public BigDecimal getClamp_up() {
		return clamp_up;
	}

	public void setClamp_up(BigDecimal clamp_up) {
		this.clamp_up = clamp_up;
	}

	public BigDecimal getClamp_down() {
		return clamp_down;
	}

	public void setClamp_down(BigDecimal clamp_down) {
		this.clamp_down = clamp_down;
	}

	public boolean getFixed_fee_open() {
		return fixed_fee_open;
	}

	public void setFixed_fee_open(boolean fixed_fee_open) {
		this.fixed_fee_open = fixed_fee_open;
	}

	public BigDecimal getFixed_fee_rate() {
		return fixed_fee_rate;
	}

	public void setFixed_fee_rate(BigDecimal fixed_fee_rate) {
		this.fixed_fee_rate = fixed_fee_rate;
	}

}
