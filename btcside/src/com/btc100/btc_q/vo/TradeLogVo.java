package com.btc100.btc_q.vo;

import java.io.Serializable;

public class TradeLogVo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long tradeId;
	private String symbol;
	private int tradeType;
	private int direction;
	private int multiple;
	private double holdPrice;
	private double tradePrice;
	private double tradeCount;
	private double tradeDeposit;
	private double tradeIncome;
	private double tradeFee;
	private double tradeBfxFee;
	private double forcePrice;
	private long tradeTime;
	
	public long getTradeId() {
		return tradeId;
	}
	
	public void setTradeId(long tradeId) {
		this.tradeId = tradeId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public int getTradeType() {
		return tradeType;
	}

	public void setTradeType(int tradeType) {
		this.tradeType = tradeType;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	public double getHoldPrice() {
		return holdPrice;
	}

	public void setHoldPrice(double holdPrice) {
		this.holdPrice = holdPrice;
	}

	public double getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(double tradePrice) {
		this.tradePrice = tradePrice;
	}

	public double getTradeCount() {
		return tradeCount;
	}

	public void setTradeCount(double tradeCount) {
		this.tradeCount = tradeCount;
	}

	public double getTradeDeposit() {
		return tradeDeposit;
	}

	public void setTradeDeposit(double tradeDeposit) {
		this.tradeDeposit = tradeDeposit;
	}

	public double getTradeIncome() {
		return tradeIncome;
	}

	public void setTradeIncome(double tradeIncome) {
		this.tradeIncome = tradeIncome;
	}

	public double getTradeFee() {
		return tradeFee;
	}

	public void setTradeFee(double tradeFee) {
		this.tradeFee = tradeFee;
	}

	public double getTradeBfxFee() {
		return tradeBfxFee;
	}

	public void setTradeBfxFee(double tradeBfxFee) {
		this.tradeBfxFee = tradeBfxFee;
	}

	public double getForcePrice() {
		return forcePrice;
	}

	public void setForcePrice(double forcePrice) {
		this.forcePrice = forcePrice;
	}

	public long getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(long tradeTime) {
		this.tradeTime = tradeTime;
	}

}
