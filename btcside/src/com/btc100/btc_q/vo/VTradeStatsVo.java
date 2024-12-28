package com.btc100.btc_q.vo;

import java.io.Serializable;

public class VTradeStatsVo implements Serializable, Comparable<VTradeStatsVo>{

	private static final long serialVersionUID = 1384488238365939940L;
	
	private long userId;
	private double sumIncome; // 总收入
	private double sumDepoist; // 总保证金
	private double yieldRate; // 收益率
	private String userName; // 用户名
	
	public long getUserId() {
		return this.userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}

	public double getSumIncome() {
		return sumIncome;
	}

	public void setSumIncome(double sumIncome) {
		this.sumIncome = sumIncome;
	}

	public double getSumDepoist() {
		return sumDepoist;
	}

	public void setSumDepoist(double sumDepoist) {
		this.sumDepoist = sumDepoist;
	}

	public double getYieldRate() {
		return yieldRate;
	}

	public void setYieldRate(double yieldRate) {
		this.yieldRate = yieldRate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public int compareTo(VTradeStatsVo o) {
		if (this.yieldRate > o.getYieldRate()) {
			return -1;
		}
		return 1;
	}
	
}
