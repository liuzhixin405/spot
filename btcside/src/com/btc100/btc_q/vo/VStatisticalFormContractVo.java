package com.btc100.btc_q.vo;

import java.io.Serializable;

public class VStatisticalFormContractVo implements Serializable {

	private static final long serialVersionUID = 7251730784626821107L;
	
	private String code; // 合约号
 	
	private double vholdAmount = 0; // 模拟交易-持仓数量
	private double vdepositAmount = 0; // 模拟交易-保证金总量
	private double vcontractBalance = 0; // 模拟交易-合约可用总额
	private double vtradeAmount; // 模拟交易-真实交易总量
	private long vtradeUser; // 模拟交易-交易人数
	private double vfeeAmount;// 模拟交易-手续费金额

	public VStatisticalFormContractVo() {

	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getVholdAmount() {
		return vholdAmount;
	}

	public void setVholdAmount(double vholdAmount) {
		this.vholdAmount = vholdAmount;
	}

	public double getVdepositAmount() {
		return vdepositAmount;
	}

	public void setVdepositAmount(double vdepositAmount) {
		this.vdepositAmount = vdepositAmount;
	}

	public double getVcontractBalance() {
		return vcontractBalance;
	}

	public void setVcontractBalance(double vcontractBalance) {
		this.vcontractBalance = vcontractBalance;
	}

	public double getVtradeAmount() {
		return vtradeAmount;
	}

	public void setVtradeAmount(double vtradeAmount) {
		this.vtradeAmount = vtradeAmount;
	}

	public long getVtradeUser() {
		return vtradeUser;
	}

	public void setVtradeUser(long vtradeUser) {
		this.vtradeUser = vtradeUser;
	}

	public double getVfeeAmount() {
		return vfeeAmount;
	}

	public void setVfeeAmount(double vfeeAmount) {
		this.vfeeAmount = vfeeAmount;
	}

}
