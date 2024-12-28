package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * btc 币的价格指数记录日志表，每一种币种使用一个独立的指数表记录
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_price_index_btc_log")
public class BQPriceIndexBtcLog implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private int currency_type_id;
	private BigDecimal price;
	private long dateline;

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getCurrency_type_id() {
		return currency_type_id;
	}

	public void setCurrency_type_id(int currencyTypeId) {
		currency_type_id = currencyTypeId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

}
