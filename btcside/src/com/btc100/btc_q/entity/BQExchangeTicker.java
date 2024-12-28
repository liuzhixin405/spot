package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_exchange_ticker")
public class BQExchangeTicker implements Serializable {

	private static final long serialVersionUID = -3108732702015852387L;
	
	private long id;
	private int currency_type_id;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal last;
	private BigDecimal buy;
	private BigDecimal sell;
	private BigDecimal vol;
	private long dateline;
	private String market;
	private String trans_pair;
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long value) {
		id = value;
	}

	public int getCurrency_type_id() {
		return currency_type_id;
	}

	public void setCurrency_type_id(int value) {
		currency_type_id = value;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal value) {
		high = value;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal value) {
		low = value;
	}

	public BigDecimal getBuy() {
		return buy;
	}

	public void setBuy(BigDecimal value) {
		buy = value;
	}

	public BigDecimal getSell() {
		return sell;
	}

	public void setSell(BigDecimal value) {
		sell = value;
	}

	public BigDecimal getLast() {
		return last;
	}

	public void setLast(BigDecimal value) {
		last = value;
	}

	public BigDecimal getVol() {
		return vol;
	}

	public void setVol(BigDecimal value) {
		vol = value;
	}
	
	public long getDateline() {
		return dateline;
	}
	
	public void setDateline(long time) {
		this.dateline = time;
	}
	
	public String getMarket() {
		return market;
	}
	
	public void setMarket(String market) {
		this.market = market;
	}
	
	public String getTrans_pair() {
		return trans_pair;
	}
	
	public void setTrans_pair(String pair) {
		this.trans_pair = pair;
	}

}
