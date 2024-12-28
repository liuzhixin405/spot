package com.btc100.btc_q.vo;

import java.io.Serializable;

public class RebateRankingVo implements Serializable, Comparable<RebateRankingVo> {

	private static final long serialVersionUID = -8069173222435123717L;

	private long userId; // 用户id
	private double rebate; // 返利
	private String userName; // 用户名

	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public double getRebate() {
		return rebate;
	}

	public void setRebate(double rebate) {
		this.rebate = rebate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public int compareTo(RebateRankingVo o) {
		if (this.rebate > o.getRebate()) {
			return -1;
		}
		return 1;
	}

}
