package com.btc100.btc_q.vo;

import java.io.Serializable;

public class TransferVO implements Serializable {

	private static final long serialVersionUID = -8585547795480555567L;
	
	private long recordId;
	private String symbol;
	private long recordTime;
	private double amount;
	private int operation;
	private String subUser;
	
	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public long getRecordTime() {
		return recordTime;
	}
	
	public void setRecordTime(long recordTime) {
		this.recordTime = recordTime;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public int getOperation() {
		return operation;
	}
	
	public void setOperation(int operation) {
		this.operation = operation;
	}

	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	public String getSubUser() {
		return subUser;
	}

	public void setSubUser(String subUser) {
		this.subUser = subUser;
	}

}
