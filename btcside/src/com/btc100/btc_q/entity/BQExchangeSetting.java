package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_exchange_setting")
public class BQExchangeSetting implements Serializable {

	private static final long serialVersionUID = 8609801548465756922L;
	
	private Long id;
	private int currency_type_id;
	private String symbol;
	private BigDecimal frozen_rate = BigDecimal.ZERO;
	private BigDecimal fee_rate_to_usdt = BigDecimal.ZERO;
	private BigDecimal fee_rate_from_usdt = BigDecimal.ZERO;
	private BigDecimal outside_buy_range = BigDecimal.ZERO;
	private BigDecimal outside_sell_range = BigDecimal.ZERO;
	private int digits = 2;
	private BigDecimal min_amount = BigDecimal.ZERO;
	private boolean running = false;
	private boolean inside_exchange = false;
	private BigDecimal exchange_rate = BigDecimal.ZERO;
	
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
	
	public void setCurrency_type_id(int uid) {
		currency_type_id = uid;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public BigDecimal getFrozen_rate() {
		return frozen_rate;
	}
	
	public void setFrozen_rate(BigDecimal rate) {
		frozen_rate = rate;
	}
	
	public BigDecimal getFee_rate_to_usdt() {
		return fee_rate_to_usdt;
	}
	
	public void setFee_rate_to_usdt(BigDecimal rate) {
		fee_rate_to_usdt = rate;
	}
	
	public BigDecimal getFee_rate_from_usdt() {
		return fee_rate_from_usdt; 
	}
	
	public void setFee_rate_from_usdt(BigDecimal rate) {
		fee_rate_from_usdt = rate;
	}

	public BigDecimal getOutside_buy_range() {
		return outside_buy_range;
	}
	
	public void setOutside_buy_range(BigDecimal range) {
		outside_buy_range = range;
	}
	
	public BigDecimal getOutside_sell_range() {
		return outside_sell_range;
	}
	
	public void setOutside_sell_range(BigDecimal range) {
		outside_sell_range = range;
	}
	
	public int getDigits() {
		return digits;
	}
	
	public void setDigits(int digits) {
		this.digits = digits;
	}
	
	public BigDecimal getMin_amount() {
		return min_amount;
	}
	
	public void setMin_amount(BigDecimal minAmount) {
		this.min_amount = minAmount;
	}
	
	public boolean getRunning() {
		return running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean getInside_exchange() {
		return inside_exchange;
	}
	
	public void setInside_exchange(boolean inside) {
		inside_exchange = inside;
	}
	
	public BigDecimal getExchange_rate() {
		return exchange_rate;
	}
	
	public void setExchange_rate(BigDecimal rate) {
		exchange_rate = rate;
	}
}
