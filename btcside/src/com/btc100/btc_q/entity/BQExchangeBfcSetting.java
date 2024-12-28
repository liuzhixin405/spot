package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_exchange_bfc_setting")
public class BQExchangeBfcSetting implements Serializable {

	private static final long serialVersionUID = 7897853272995542492L;

	private Long id;
	private int currency_type_id;
	private String symbol;
	private BigDecimal min_amount;
	private BigDecimal exchange_rate = BigDecimal.ZERO;
	private boolean running;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCurrency_type_id() {
		return currency_type_id;
	}

	public void setCurrency_type_id(int currency_type_id) {
		this.currency_type_id = currency_type_id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public BigDecimal getMin_amount() {
		return min_amount;
	}

	public void setMin_amount(BigDecimal min_amount) {
		this.min_amount = min_amount;
	}

	public BigDecimal getExchange_rate() {
		return exchange_rate;
	}

	public void setExchange_rate(BigDecimal exchange_rate) {
		this.exchange_rate = exchange_rate;
	}

	public boolean getRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
