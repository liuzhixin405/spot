package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_market_order_setting")
public class BQMarketOrderSetting implements Serializable {

	private static final long serialVersionUID = 2532973437381427748L;

	private long id;
	private boolean running;
	private BigDecimal price_range; // 价格阈值百分比

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

	public BigDecimal getPrice_range() {
		return price_range;
	}

	public void setPrice_range(BigDecimal price_range) {
		this.price_range = price_range;
	}

}
