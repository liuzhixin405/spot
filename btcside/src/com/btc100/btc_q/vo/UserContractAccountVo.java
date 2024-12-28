package com.btc100.btc_q.vo;

import java.io.Serializable;

public class UserContractAccountVo implements Serializable {

	private static final long serialVersionUID = -5646777428484304773L;

	private long userId;
	private double account; // 我的钱包(美元)
	private double cnyAccount; // 我的钱包(人民币)
	private double freez; // 合约挂单冻结：
	private double deposit; // 合约持仓保证金
	private double availableBtc; // 合约可用总计
	private double contractTotal; // 合约账户总资产
	private double cnyContractTotal; // 合约账户总资产(人民币)
	private double lock = 0;
	private double unlock = 0;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public double getAccount() {
		return account;
	}

	public void setAccount(double account) {
		this.account = account;
	}

	public double getCnyAccount() {
		return cnyAccount;
	}

	public void setCnyAccount(double cnyAccount) {
		this.cnyAccount = cnyAccount;
	}

	public double getFreez() {
		return freez;
	}

	public void setFreez(double freez) {
		this.freez = freez;
	}

	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}

	public double getAvailableBtc() {
		return availableBtc;
	}

	public void setAvailableBtc(double availableBtc) {
		this.availableBtc = availableBtc;
	}

	public double getContractTotal() {
		return contractTotal;
	}

	public void setContractTotal(double contractTotal) {
		this.contractTotal = contractTotal;
	}

	public double getCnyContractTotal() {
		return cnyContractTotal;
	}

	public void setCnyContractTotal(double cnyContractTotal) {
		this.cnyContractTotal = cnyContractTotal;
	}

	public double getLock() {
		return lock;
	}

	public void setLock(double lock) {
		this.lock = lock;
	}

	public double getUnlock() {
		return unlock;
	}

	public void setUnlock(double unlock) {
		this.unlock = unlock;
	}

}
