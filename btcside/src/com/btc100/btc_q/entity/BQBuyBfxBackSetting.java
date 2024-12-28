package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_buy_bfx_back_setting")
public class BQBuyBfxBackSetting implements Serializable {

	private static final long serialVersionUID = 8609801548465756922L;
	
	private Long id;
	private int currency_type_id;						// 回购所用币种Id
	private int bfx_currency_id;						// Bfx币种Id
	private String symbol;								// 回购所用币种名称
	private BigDecimal price = BigDecimal.ZERO;			// 回购价格 
	private BigDecimal min_amount = BigDecimal.ZERO;	// 最少数量
	private boolean running = false;					// 是否开启
	
	
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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getBfx_currency_id() {
		return bfx_currency_id;
	}

	public void setBfx_currency_id(int bfx_currency_id) {
		this.bfx_currency_id = bfx_currency_id;
	}
}
