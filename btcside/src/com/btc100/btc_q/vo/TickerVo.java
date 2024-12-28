package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class TickerVo implements Serializable {

	private static final long serialVersionUID = 1056988800347635511L;
	BigDecimal high;
	BigDecimal low;

	BigDecimal buy;
	BigDecimal sell;

	BigDecimal open;
	BigDecimal last;
	BigDecimal last_usd;
	BigDecimal vol;
	BigDecimal turnover;
	BigDecimal cap;
	
	BigDecimal change_1h;
	BigDecimal change_24h;
	BigDecimal change_7d;
	
	BigDecimal ratio;
	
	String market;
	String trans_pair;
	String update_time;
	long time;
	int type_id;
	String type_code;
	
	String fudu;
	String date;
	
	public int getTypeId() {
		return type_id;
	}
	
	public void setTypeId(int typeId) {
		this.type_id = typeId;
	}
	
	public String getTypeCode() {
		return type_code;
	}
	
	public void setTypeCode(String typeCode) {
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

	public BigDecimal getBuy() {
		return buy;
	}

	public void setBuy(BigDecimal buy) {
		this.buy = buy;
	}

	public BigDecimal getSell() {
		return sell;
	}

	public void setSell(BigDecimal sell) {
		this.sell = sell;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	public BigDecimal getLast() {
		return last;
	}

	public void setLast(BigDecimal last) {
		this.last = last;
	}
	
	public BigDecimal getLastUsd() {
		return last_usd;
	}

	public void setLastUsd(BigDecimal lastUsd) {
		this.last_usd = lastUsd;
	}

	public BigDecimal getVol() {
		return vol;
	}

	public void setVol(BigDecimal vol) {
		this.vol = vol;
	}
	
	public BigDecimal getTurnover() {
		return turnover;
	}

	public void setTurnover(BigDecimal turnover) {
		this.turnover = turnover;
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
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public String getMarket() {
		return market;
	}
	
	public void setMarket(String market) {
		this.market = market;
	}
	
	public BigDecimal getRatio() {
		return ratio;
	}
	
	public void setRatio(BigDecimal ratio) {
		this.ratio = ratio;
	}
	
	public String getUpdateTime() {
		return update_time;
	}
	
	public void setUpdateTime(String timeStr) {
		this.update_time = timeStr;
	}

	public String getFudu() {
		return fudu;
	}

	public void setFudu(String fudu) {
		this.fudu = fudu;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
