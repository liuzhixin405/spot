package com.btc100.btc_q.vo;

import java.io.Serializable;

public class QuantContractVo implements Serializable {

	private static final long serialVersionUID = 8653018580055314726L;

	private String code; // 合约号
	private double amount = 0; // 交易额
	private double makerAmount = 0; // maker交易额
	private double takerAmount = 0; // taker交易额

	public QuantContractVo() {

	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getMakerAmount() {
		return makerAmount;
	}

	public void setMakerAmount(double makerAmount) {
		this.makerAmount = makerAmount;
	}

	public double getTakerAmount() {
		return takerAmount;
	}

	public void setTakerAmount(double takerAmount) {
		this.takerAmount = takerAmount;
	}

}
