package com.btc100.btc_q.vo;

import java.io.Serializable;

public class BetterTokenAccount implements Serializable {

	private static final long serialVersionUID = 298570292760414503L;

	private long brokerId;
	private String symbol;
	private String available;
	private String hold;
	private long baseBTC;
	private String withdrawLimit;

	public long getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(long brokerId) {
		this.brokerId = brokerId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getAvailable() {
		return available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}

	public String getHold() {
		return hold;
	}

	public void setHold(String hold) {
		this.hold = hold;
	}

	public long getBaseBTC() {
		return baseBTC;
	}

	public void setBaseBTC(long baseBTC) {
		this.baseBTC = baseBTC;
	}

	public String getWithdrawLimit() {
		return withdrawLimit;
	}

	public void setWithdrawLimit(String withdrawLimit) {
		this.withdrawLimit = withdrawLimit;
	}

}
