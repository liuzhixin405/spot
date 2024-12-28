package com.btc100.btc_q.vo;

import java.io.Serializable;

public class StatisticalFormContractVo implements Serializable {

	private static final long serialVersionUID = 7251730784626821107L;
	
	private String code; // 合约号
	private double holdAmount = 0; // 持仓数量
	private double depositAmount = 0; // 保证金总量
	private double contractBalance = 0; // 合约可用总额
	private double tradeAmount; // 真实交易总量
	private long tradeUser; // 交易人数
	private double feeAmount;// 手续费金额
	private double rebate;// 返佣
	private double bfxFeeAmount = 0;// bfx手续费
	private double bfxRebate = 0;// bfx返佣

	public StatisticalFormContractVo() {

	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getHoldAmount() {
		return holdAmount;
	}

	public void setHoldAmount(double holdAmount) {
		this.holdAmount = holdAmount;
	}

	public double getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(double depositAmount) {
		this.depositAmount = depositAmount;
	}

	public double getContractBalance() {
		return contractBalance;
	}

	public void setContractBalance(double contractBalance) {
		this.contractBalance = contractBalance;
	}

	public double getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(double tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public long getTradeUser() {
		return tradeUser;
	}

	public void setTradeUser(long tradeUser) {
		this.tradeUser = tradeUser;
	}

	public double getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(double feeAmount) {
		this.feeAmount = feeAmount;
	}

	public double getRebate() {
		return rebate;
	}

	public void setRebate(double rebate) {
		this.rebate = rebate;
	}

	public double getBfxFeeAmount() {
		return bfxFeeAmount;
	}

	public void setBfxFeeAmount(double bfxFeeAmount) {
		this.bfxFeeAmount = bfxFeeAmount;
	}

	public double getBfxRebate() {
		return bfxRebate;
	}

	public void setBfxRebate(double bfxRebate) {
		this.bfxRebate = bfxRebate;
	}

}
