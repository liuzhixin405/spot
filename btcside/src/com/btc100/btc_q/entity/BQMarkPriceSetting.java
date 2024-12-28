package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_mark_price_setting")
public class BQMarkPriceSetting implements Serializable {

	private static final long serialVersionUID = -3744115134775577527L;
	
	private long id;
	private String code;	// 是否固定价格
	private boolean running;
	private boolean fixed = false;	// 是否固定价格
	private BigDecimal fixed_price = BigDecimal.ZERO;	// 固定价格
	private int moving_period;	// 移动周期
	private BigDecimal open_width_filter;	// 盘口宽度过滤比例
	private BigDecimal index_rate_filter;	// 指数范围比例
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public BigDecimal getOpen_width_filter() {
		return open_width_filter;
	}

	public void setOpen_width_filter(BigDecimal open_width_filter) {
		this.open_width_filter = open_width_filter;
	}

	public BigDecimal getIndex_rate_filter() {
		return index_rate_filter;
	}

	public void setIndex_rate_filter(BigDecimal index_rate_filter) {
		this.index_rate_filter = index_rate_filter;
	}

	public int getMoving_period() {
		return moving_period;
	}

	public void setMoving_period(int moving_period) {
		this.moving_period = moving_period;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public BigDecimal getFixed_price() {
		return fixed_price;
	}

	public void setFixed_price(BigDecimal fixed_price) {
		this.fixed_price = fixed_price;
	}
	
}
