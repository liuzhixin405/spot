package com.btc100.btc_q.vo;

import java.io.Serializable;

public class UserBonusRecordVo implements Serializable {

	private static final long serialVersionUID = 4064199306019163958L;

	private long recordId;
	private String currencyName; // 资产名称
	private String day; // 记录时间
	private double bfxHold; // 持有BFX数量
	private double bonusRate; // 每百万份返利金额
	private double bonusAmount; // 返利金额

	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public double getBfxHold() {
		return bfxHold;
	}

	public void setBfxHold(double bfxHold) {
		this.bfxHold = bfxHold;
	}

	public double getBonusRate() {
		return bonusRate;
	}

	public void setBonusRate(double bonusRate) {
		this.bonusRate = bonusRate;
	}

	public double getBonusAmount() {
		return bonusAmount;
	}

	public void setBonusAmount(double bonusAmount) {
		this.bonusAmount = bonusAmount;
	}

}
