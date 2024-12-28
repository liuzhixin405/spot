package com.btc100.btc_q.vo;

import java.io.Serializable;

public class PendingDelegationVO implements Serializable {

	private static final long serialVersionUID = -4402044701416859647L;
	
	private long delegation_id;
	private String orderId;	// 委托流水
	private String symbol;	// 合约名称
	private String state;	// 提交中，已提交，成交，撤销
	private double amount;
	private double dealAmount;
	private double undealAmount;
	private double price;
	private long createTime;
	private int direction;
	private int priority;
	private int multiple;
	private double deposit;
	private String coinPairName; // 币币对名称（ETH/BTC）
	
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
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
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

	public long getDelegation_id() {
		return delegation_id;
	}

	public void setDelegation_id(long delegation_id) {
		this.delegation_id = delegation_id;
	}

	public String getCoinPairName() {
		return coinPairName;
	}

	public void setCoinPairName(String coinPairName) {
		this.coinPairName = coinPairName;
	}
}
