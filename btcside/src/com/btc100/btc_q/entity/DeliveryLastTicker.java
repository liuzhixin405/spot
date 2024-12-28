package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "delivery_last_ticker")
public class DeliveryLastTicker implements Serializable {

	private static final long serialVersionUID = 825468893098368937L;

	public DeliveryLastTicker() {
	}

	public DeliveryLastTicker(int fTickerId, String fTickerCode, BigDecimal fTickerHigh, BigDecimal fTickerLow,
			BigDecimal fTickerBuy, BigDecimal fTickerSell, BigDecimal fTickerLast, BigDecimal fTickerVol,
			BigDecimal trades) {
		f_ticker_id = fTickerId;
		f_ticker_code = fTickerCode;
		f_ticker_high = fTickerHigh;
		f_ticker_low = fTickerLow;
		f_ticker_buy = fTickerBuy;
		f_ticker_sell = fTickerSell;
		f_ticker_last = fTickerLast;
		f_ticker_vol = fTickerVol;
		f_ticker_trades = trades;
	}

	private int f_ticker_id;
	private String f_ticker_code;
	private BigDecimal f_ticker_high;
	private BigDecimal f_ticker_low;
	private BigDecimal f_ticker_buy;
	private BigDecimal f_ticker_sell;
	private BigDecimal f_ticker_last;
	private BigDecimal f_ticker_vol;
	private BigDecimal f_ticker_trades;

	@Id
	@GeneratedValue
	public int getF_ticker_id() {
		return f_ticker_id;
	}

	public void setF_ticker_id(int value) {
		f_ticker_id = value;
	}

	public String getF_ticker_code() {
		return f_ticker_code;
	}

	public void setF_ticker_code(String value) {
		f_ticker_code = value;
	}

	public BigDecimal getF_ticker_high() {
		return f_ticker_high;
	}

	public void setF_ticker_high(BigDecimal value) {
		f_ticker_high = value;
	}

	public BigDecimal getF_ticker_low() {
		return f_ticker_low;
	}

	public void setF_ticker_low(BigDecimal value) {
		f_ticker_low = value;
	}

	public BigDecimal getF_ticker_buy() {
		return f_ticker_buy;
	}

	public void setF_ticker_buy(BigDecimal value) {
		f_ticker_buy = value;
	}

	public BigDecimal getF_ticker_sell() {
		return f_ticker_sell;
	}

	public void setF_ticker_sell(BigDecimal value) {
		f_ticker_sell = value;
	}

	public BigDecimal getF_ticker_last() {
		return f_ticker_last;
	}

	public void setF_ticker_last(BigDecimal value) {
		f_ticker_last = value;
	}

	public BigDecimal getF_ticker_vol() {
		return f_ticker_vol;
	}

	public void setF_ticker_vol(BigDecimal value) {
		f_ticker_vol = value;
	}

	public BigDecimal getF_ticker_trades() {
		return f_ticker_trades;
	}

	public void setF_ticker_trades(BigDecimal trades) {
		f_ticker_trades = trades;
	}

}
