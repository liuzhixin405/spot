package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserAccountVo implements Serializable {

	private static final long serialVersionUID = 4874692791317503114L;
	
	private long userId;
	private int coinId;
	private String userEmail;
	private BigDecimal available; 
	private BigDecimal frozen;
	private String walletAddress;
	private BigDecimal bfxToUnlock = BigDecimal.ZERO;

	public UserAccountVo() {
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getCoinId() {
		return coinId;
	}

	public void setCoinId(int coinId) {
		this.coinId = coinId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public BigDecimal getAvailable() {
		return available;
	}

	public void setAvailable(BigDecimal available) {
		this.available = available;
	}

	public BigDecimal getFrozen() {
		return frozen;
	}

	public void setFrozen(BigDecimal frozen) {
		this.frozen = frozen;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public BigDecimal getBfxToUnlock() {
		return bfxToUnlock;
	}

	public void setBfxToUnlock(BigDecimal bfxToUnlock) {
		this.bfxToUnlock = bfxToUnlock;
	}

}
