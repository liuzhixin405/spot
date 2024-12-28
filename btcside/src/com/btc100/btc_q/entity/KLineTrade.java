package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class KLineTrade implements Serializable {

	private static final long serialVersionUID = -940138240400617417L;

	public KLineTrade() {
	}
	
	public KLineTrade(KLineTrade kline) {
		this.id = kline.getId();
		this.code = kline.getCode();
		this.first = kline.getFirst();
		this.high = kline.getHigh();
		this.low = kline.getLow();
		this.last = kline.getLast();
		this.time = kline.getTime();
		this.vol = kline.getVol();
	}

	public KLineTrade(long id, String code, BigDecimal high, BigDecimal low, BigDecimal first, BigDecimal last,
			BigDecimal vol, long time) {
		this.id = id;
		this.code = code;
		this.high = high;
		this.low = low;
		this.first = first;
		this.last = last;
		this.vol = vol;
		this.time = time;
	}

	private long id;
	private String code;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal first;
	private BigDecimal last;
	private BigDecimal vol;
	private long time;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public BigDecimal getFirst() {
		return first;
	}

	public void setFirst(BigDecimal first) {
		this.first = first;
	}

	public BigDecimal getLast() {
		return last;
	}

	public void setLast(BigDecimal last) {
		this.last = last;
	}

	public BigDecimal getVol() {
		return vol;
	}

	public void setVol(BigDecimal vol) {
		this.vol = vol;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
