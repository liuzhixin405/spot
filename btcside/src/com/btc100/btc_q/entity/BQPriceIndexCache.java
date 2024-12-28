package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 价格指数,一种币种对应一个价格指数
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_price_index_cache")
public class BQPriceIndexCache implements Serializable {
	
	private static final long serialVersionUID = -3931149940709373539L;

	private int id;
	private int currency_type_id;
	private BigDecimal price = BigDecimal.ZERO;
	private long dateline;
	private BigDecimal otherprice;
	private String type_code;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal last;
	private BigDecimal turnover;
	private BigDecimal vol;
	private BigDecimal cap;
	private BigDecimal change_1h;
	private BigDecimal change_24h;
	private BigDecimal change_7d;
	private BigDecimal ratio;
	private String trans_pair;
	private String market;
	
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

	public BigDecimal getOtherprice() {
		return otherprice;
	}

	public void setOtherprice(BigDecimal otherprice) {
		this.otherprice = otherprice;
	}
	
	public String getType_code() {
		return this.type_code;
	}
	
	public void setType_code(String typeCode) {
		this.type_code = typeCode;
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
	
	public BigDecimal getLast() {
		return last;
	}
	
	public void setLast(BigDecimal last) {
		this.last = last;
	}
	
	public BigDecimal getTurnover() {
		return turnover;
	}
	
	public void setTurnover(BigDecimal turnover) {
		this.turnover = turnover;
	}
	
	public BigDecimal getVol() {
		return vol;
	}
	
	public void setVol(BigDecimal vol) {
		this.vol = vol;
	}
	
	public BigDecimal getCap() {
		return cap;
	}
	
	public void setCap(BigDecimal cap) {
		this.cap = cap;
	}
	
	public BigDecimal getChange_1h() {
		return change_1h;
	}
	
	public void setChange_1h(BigDecimal change_1h) {
		this.change_1h = change_1h;
	}
	
	public BigDecimal getChange_24h() {
		return change_24h;
	}
	
	public void setChange_24h(BigDecimal change_24h) {
		this.change_24h = change_24h;
	}
	
	public BigDecimal getChange_7d() {
		return change_7d;
	}
	
	public void setChange_7d(BigDecimal change_7d) {
		this.change_7d = change_7d;
	}
	
	public String getTrans_pair() {
		return trans_pair;
	}
	
	public void setTrans_pair(String transPair) {
		this.trans_pair = transPair;
	}
	
	public String getMarket() {
		return market;
	}
	
	public void setMarket(String market) {
		this.market = market;
	}
	
	public BigDecimal getRatio() {
		return this.ratio;
	}
	
	public void setRatio(BigDecimal ratio) {
		this.ratio = ratio;
	}
}
