package com.btc100.btc_q.vo;

import java.io.Serializable;

public class UserAccountAmountLogVo implements Serializable {

	private static final long serialVersionUID = -4706477996392708215L;

	private long recordId;
	private long dateline; // 记录时间
	private String currencyName; // 币种名称
	private String title; // 标题
	private int direction; // 方向
	private double currentAmount; // 原有数量
	private double changeAmount; // 增减值
	private double lastestAmount; // 现有数量

	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public double getCurrentAmount() {
		return currentAmount;
	}

	public void setCurrentAmount(double currentAmount) {
		this.currentAmount = currentAmount;
	}

	public double getChangeAmount() {
		return changeAmount;
	}

	public void setChangeAmount(double changeAmount) {
		this.changeAmount = changeAmount;
	}

	public double getLastestAmount() {
		return lastestAmount;
	}

	public void setLastestAmount(double lastestAmount) {
		this.lastestAmount = lastestAmount;
	}

}
