package com.btc100.btc_q.vo;

import java.io.Serializable;

public class PendingLogVO implements Serializable {
	
	private static final long serialVersionUID = 7342805019538797538L;
	
	private long recordId;
	private String orderId;	// 委托流水
	private String symbol;	// 合约名称
	private String state;	// 提交中，已提交，成交，撤销
	private double amount;
	private double dealAmount;
	private double undealAmount;
	private double price;
	private double tradeAvgPrice;
	private long createTime;
	private long updateTime;
	private int direction;
	private int tradeType;
	private int multiple;
	private double deposit;
	private double income;
	
	public String getOrderId() {
		return orderId;
	}
	
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public double getDealAmount() {
		return dealAmount;
	}
	
	public void setDealAmount(double dealAmount) {
		this.dealAmount = dealAmount;
	}
	
	public double getUndealAmount() {
		return undealAmount;
	}
	
	public void setUndealAmount(double undealAmount) {
		this.undealAmount = undealAmount;
	}
	
	public long getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
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
	
	public double getDeposit() {
		return deposit;
	}
	
	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int getTradeType() {
		return tradeType;
	}

	public void setTradeType(int tradeType) {
		this.tradeType = tradeType;
	}

	public double getTradeAvgPrice() {
		return tradeAvgPrice;
	}

	public void setTradeAvgPrice(double tradeAvgPrice) {
		this.tradeAvgPrice = tradeAvgPrice;
	}

	public double getIncome() {
		return income;
	}

	public void setIncome(double income) {
		this.income = income;
	}

}
