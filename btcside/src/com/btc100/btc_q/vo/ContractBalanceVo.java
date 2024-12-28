package com.btc100.btc_q.vo;

import java.io.Serializable;

public class ContractBalanceVo implements Serializable, Comparable<ContractBalanceVo>{

	private static final long serialVersionUID = 355012110929449940L;
	
	private long userId;
	private String userName;
	private double accountBalance = 0;
	private double accountFrozen = 0;
	private double accountAll = 0;
	private double contractBalance = 0;
	private double contractFrozen = 0;
	private double contractDeposit = 0;
	private double contractFloatGainLoss = 0;
	private double contractAll = 0;
	private double deliveryContractBalance = 0;
	private double deliveryContractFrozen = 0;
	private double deliveryContractDeposit = 0;
	private double deliveryContractFloatGainLoss = 0;
	private double deliveryContractAll = 0;
	private double all = 0;
	
	public long getUserId() {
		return this.userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public double getAccountBalance() {
		return this.accountBalance;
	}
	
	public void setAccountBalance(double value) {
		this.accountBalance = value;
	}
	
	public double getAccountFrozen() {
		return this.accountFrozen;
	}
	
	public void setAccountFrozen(double value) {
		this.accountFrozen = value;
	}
	
	public double getAccountAll() {
		return this.accountAll;
	}
	
	public void setAccountAll(double value) {
		this.accountAll = value;
	}
	
	public double getContractBalance() {
		return this.contractBalance;
	}
	
	public void setContractBalance(double value) {
		this.contractBalance = value;
	}
	
	public double getContractFrozen() {
		return this.contractFrozen;
	}
	
	public void setContractFrozen(double value) {
		this.contractFrozen = value;
	}
	
	public double getContractDeposit() {
		return this.contractDeposit;
	}
	
	public void setContractDeposit(double value) {
		this.contractDeposit = value;
	}
	
	public double getContractFloatGainLoss() {
		return contractFloatGainLoss;
	}

	public void setContractFloatGainLoss(double contractFloatGainLoss) {
		this.contractFloatGainLoss = contractFloatGainLoss;
	}

	public double getContractAll() {
		return this.contractAll;
	}
	
	public void setContractAll(double value) {
		this.contractAll = value;
	}
	
	public double getDeliveryContractBalance() {
		return deliveryContractBalance;
	}

	public void setDeliveryContractBalance(double deliveryContractBalance) {
		this.deliveryContractBalance = deliveryContractBalance;
	}

	public double getDeliveryContractFrozen() {
		return deliveryContractFrozen;
	}

	public void setDeliveryContractFrozen(double deliveryContractFrozen) {
		this.deliveryContractFrozen = deliveryContractFrozen;
	}

	public double getDeliveryContractDeposit() {
		return deliveryContractDeposit;
	}

	public void setDeliveryContractDeposit(double deliveryContractDeposit) {
		this.deliveryContractDeposit = deliveryContractDeposit;
	}
	
	public double getDeliveryContractFloatGainLoss() {
		return deliveryContractFloatGainLoss;
	}
	
	public void setDeliveryContractFloatGainLoss(double deliveryContractFloatGainLoss) {
		this.deliveryContractFloatGainLoss = deliveryContractFloatGainLoss;
	}

	public double getDeliveryContractAll() {
		return deliveryContractAll;
	}

	public void setDeliveryContractAll(double deliveryContractAll) {
		this.deliveryContractAll = deliveryContractAll;
	}

	public double getAll() {
		return this.all;
	}
	
	public void setAll(double value) {
		this.all = value;
	}

	@Override
	public int compareTo(ContractBalanceVo o) {
		if(this.all < o.getAll()){
			return 1;
		}
		
		if (this.all == o.getAll()) {
			return 0;
		}

		return -1;
	}
}
