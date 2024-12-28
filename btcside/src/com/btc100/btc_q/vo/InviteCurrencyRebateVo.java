package com.btc100.btc_q.vo;

import java.io.Serializable;

public class InviteCurrencyRebateVo implements Serializable {

	private static final long serialVersionUID = 5856476272832062161L;

	private int currenyId; // 币种id
	private String currenyName; // 币种名称
	private double weekRebate; // 本周提成
	private double monthRebate; // 本月提成
	private double totalRebate; // 总提成

	public int getCurrenyId() {
		return currenyId;
	}

	public void setCurrenyId(int currenyId) {
		this.currenyId = currenyId;
	}

	public String getCurrenyName() {
		return currenyName;
	}

	public void setCurrenyName(String currenyName) {
		this.currenyName = currenyName;
	}

	public double getWeekRebate() {
		return weekRebate;
	}

	public void setWeekRebate(double weekRebate) {
		this.weekRebate = weekRebate;
	}

	public double getMonthRebate() {
		return monthRebate;
	}

	public void setMonthRebate(double monthRebate) {
		this.monthRebate = monthRebate;
	}

	public double getTotalRebate() {
		return totalRebate;
	}

	public void setTotalRebate(double totalRebate) {
		this.totalRebate = totalRebate;
	}

}
