package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.util.List;

public class StatisticalFormVo implements Serializable {

	private static final long serialVersionUID = 6070829776071777505L;

	private String time;
	private long todayRegister; // 当前时间注册用户数量
	private long loginUserCount; // 当前时间登录用户数量
	private long totalRegister; // 总注册人数
	private long totalTradeUser = 0; // 所有合约总交易人数
	private double totalFee = 0.0;// 手续费总额
	private double totalBfxFee = 0.0;// bfx手续费总额
	private double totalRebate = 0.0;// 邀请返佣总额
	private double totalCoinRebate = 0.0;// 邀请返佣总额
	private double totalMarketRebate = 0.0;// 邀请返佣总额
	private double totalBfxRebate = 0.0;// 邀请返佣总额
	private long vtotalTradeUser = 0; // 模拟交易-所有合约总交易人数
	private double vtotalFee = 0.0;// 模拟交易-手续费总额
	private List<StatisticalFormContractVo> conForms;
	private List<VStatisticalFormContractVo> vconForms;
	private List<StatisticalFormSpotVo> spotFormVos;
	private List<QuantContractVo> quantContractVos;

	public StatisticalFormVo() {

	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public long getTodayRegister() {
		return todayRegister;
	}

	public void setTodayRegister(long todayRegister) {
		this.todayRegister = todayRegister;
	}

	public long getLoginUserCount() {
		return loginUserCount;
	}

	public void setLoginUserCount(long loginUserCount) {
		this.loginUserCount = loginUserCount;
	}

	public long getTotalRegister() {
		return totalRegister;
	}

	public void setTotalRegister(long totalRegister) {
		this.totalRegister = totalRegister;
	}

	public long getTotalTradeUser() {
		return totalTradeUser;
	}

	public void setTotalTradeUser(long totalTradeUser) {
		this.totalTradeUser = totalTradeUser;
	}

	public double getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(double totalFee) {
		this.totalFee = totalFee;
	}

	public double getTotalBfxFee() {
		return totalBfxFee;
	}

	public void setTotalBfxFee(double totalBfxFee) {
		this.totalBfxFee = totalBfxFee;
	}

	public double getTotalRebate() {
		return totalRebate;
	}

	public void setTotalRebate(double totalRebate) {
		this.totalRebate = totalRebate;
	}

	public List<StatisticalFormContractVo> getConForms() {
		return conForms;
	}

	public void setConForms(List<StatisticalFormContractVo> conForms) {
		this.conForms = conForms;
	}

	public long getVtotalTradeUser() {
		return vtotalTradeUser;
	}

	public void setVtotalTradeUser(long vtotalTradeUser) {
		this.vtotalTradeUser = vtotalTradeUser;
	}

	public double getVtotalFee() {
		return vtotalFee;
	}

	public void setVtotalFee(double vtotalFee) {
		this.vtotalFee = vtotalFee;
	}

	public List<VStatisticalFormContractVo> getVconForms() {
		return vconForms;
	}

	public void setVconForms(List<VStatisticalFormContractVo> vconForms) {
		this.vconForms = vconForms;
	}

	public List<StatisticalFormSpotVo> getSpotFormVos() {
		return spotFormVos;
	}

	public void setSpotFormVos(List<StatisticalFormSpotVo> spotFormVos) {
		this.spotFormVos = spotFormVos;
	}

	public double getTotalCoinRebate() {
		return totalCoinRebate;
	}

	public void setTotalCoinRebate(double totalCoinRebate) {
		this.totalCoinRebate = totalCoinRebate;
	}

	public double getTotalMarketRebate() {
		return totalMarketRebate;
	}

	public void setTotalMarketRebate(double totalMarketRebate) {
		this.totalMarketRebate = totalMarketRebate;
	}

	public double getTotalBfxRebate() {
		return totalBfxRebate;
	}

	public void setTotalBfxRebate(double totalBfxRebate) {
		this.totalBfxRebate = totalBfxRebate;
	}

	public List<QuantContractVo> getQuantContractVos() {
		return quantContractVos;
	}

	public void setQuantContractVos(List<QuantContractVo> quantContractVos) {
		this.quantContractVos = quantContractVos;
	}

}
