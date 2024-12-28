package com.btc100.btc_q.vo;

import java.io.Serializable;

public class ExchangeRecordVO implements Serializable {

	private static final long serialVersionUID = 6391389706009108361L;

	private long recordId;
	private String originCoinName;
	private String targetCoinName;
	private double oriAmount;
	private double targetAmount;
	private double presentPrice;
	private double obtainAmount;
	private String state;
	private long recordTime;
	private double avgPrice;

	public String getOriginCoinName() {
		return originCoinName;
	}

	public void setOriginCoinName(String originCoinName) {
		this.originCoinName = originCoinName;
	}

	public String getTargetCoinName() {
		return targetCoinName;
	}

	public void setTargetCoinName(String targetCoinName) {
		this.targetCoinName = targetCoinName;
	}

	public double getOriAmount() {
		return oriAmount;
	}

	public void setOriAmount(double oriAmount) {
		this.oriAmount = oriAmount;
	}

	public double getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(double targetAmount) {
		this.targetAmount = targetAmount;
	}

	public double getPresentPrice() {
		return presentPrice;
	}

	public void setPresentPrice(double presentPrice) {
		this.presentPrice = presentPrice;
	}

	public double getObtainAmount() {
		return obtainAmount;
	}

	public void setObtainAmount(double obtainAmount) {
		this.obtainAmount = obtainAmount;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(long recordTime) {
		this.recordTime = recordTime;
	}

	public double getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(double avgPrice) {
		this.avgPrice = avgPrice;
	}

	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

}
