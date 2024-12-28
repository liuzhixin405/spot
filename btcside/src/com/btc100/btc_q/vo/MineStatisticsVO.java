package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 首页挖矿数据
 * 
 */
public class MineStatisticsVO implements Serializable  {

	private static final long serialVersionUID = 2747616093346854252L;
	
	private int stage; // 挖矿阶段
	private BigDecimal todayAmount; // 今日挖矿时时数量
	private BigDecimal currentAmount; // 当前挖矿总数量
	private BigDecimal rebateAmount; // 当前享有返利权数量
	private BigDecimal parallelAmount; // 当前二级市场流通量
	private BigDecimal todayFee; // 今日待分配收入
	private BigDecimal yesterdayFee; // 昨日已分配收入
	private BigDecimal totalFee; // 累计年化返利收益率

	public MineStatisticsVO() {

	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public BigDecimal getTodayAmount() {
		return todayAmount;
	}

	public void setTodayAmount(BigDecimal todayAmount) {
		this.todayAmount = todayAmount;
	}

	public BigDecimal getCurrentAmount() {
		return currentAmount;
	}

	public void setCurrentAmount(BigDecimal currentAmount) {
		this.currentAmount = currentAmount;
	}

	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}

	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}

	public BigDecimal getParallelAmount() {
		return parallelAmount;
	}

	public void setParallelAmount(BigDecimal parallelAmount) {
		this.parallelAmount = parallelAmount;
	}

	public BigDecimal getTodayFee() {
		return todayFee;
	}

	public void setTodayFee(BigDecimal todayFee) {
		this.todayFee = todayFee;
	}

	public BigDecimal getYesterdayFee() {
		return yesterdayFee;
	}

	public void setYesterdayFee(BigDecimal yesterdayFee) {
		this.yesterdayFee = yesterdayFee;
	}
	
	public BigDecimal getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(BigDecimal totalFee) {
		this.totalFee = totalFee;
	}

}
