package com.btc100.btc_q.vo;

import java.math.BigDecimal;

public class BQTransVo {

	private long mt_id;
	private int trade_type_id;
	private String bqCode;// 合约号
	private String direct;// 成交类型(1:多单开仓,买多/2:空头开仓,卖空)
	private int multiple;// 杠杆
	private BigDecimal allCount;// 总持仓
	private BigDecimal holdCount;// 可用持仓
	private BigDecimal lockCount;// 冻结量
	private BigDecimal averagePrice;// 合约均价
	private BigDecimal accuratePrice;// 合约均价（精确）
	private BigDecimal deposit;// 保证金
	private BigDecimal floatingProfitLoss; // 浮动盈亏
	private String profitLossRatio;// 盈亏比
	private BigDecimal commission;// 佣金
	private BigDecimal qpPrice;// 强平价
	private BigDecimal lastPrice;// 最新价
	private int stopLoss;
	private int digits;	

	public int getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(int stopLoss) {
		this.stopLoss = stopLoss;
	}

	public long getMt_id() {
		return mt_id;
	}

	public void setMt_id(long mtId) {
		mt_id = mtId;
	}

	public int getTrade_type_id() {
		return trade_type_id;
	}

	public void setTrade_type_id(int tradeTypeId) {
		trade_type_id = tradeTypeId;
	}

	public String getBqCode() {
		return bqCode;
	}

	public void setBqCode(String bqCode) {
		this.bqCode = bqCode;
	}

	public String getDirect() {
		return direct;
	}

	public void setDirect(String direct) {
		this.direct = direct;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	public BigDecimal getAllCount() {
		return allCount;
	}

	public void setAllCount(BigDecimal allCount) {
		this.allCount = allCount;
	}

	public BigDecimal getHoldCount() {
		return holdCount;
	}

	public void setHoldCount(BigDecimal holdCount) {
		this.holdCount = holdCount;
	}

	public BigDecimal getLockCount() {
		return lockCount;
	}

	public void setLockCount(BigDecimal lockCount) {
		this.lockCount = lockCount;
	}

	public BigDecimal getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(BigDecimal averagePrice) {
		this.averagePrice = averagePrice;
	}

	public BigDecimal getDeposit() {
		return deposit;
	}

	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}

	public BigDecimal getFloatingProfitLoss() {
		return floatingProfitLoss;
	}

	public void setFloatingProfitLoss(BigDecimal floatingProfitLoss) {
		this.floatingProfitLoss = floatingProfitLoss;
	}

	public String getProfitLossRatio() {
		return profitLossRatio;
	}

	public void setProfitLossRatio(String profitLossRatio) {
		this.profitLossRatio = profitLossRatio;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getQpPrice() {
		return qpPrice;
	}
	
	public void setQpPrice(BigDecimal price) {
		this.qpPrice = price;
	}
	
	public int getDigits() {
		return this.digits;
	}
	
	public void setDigits(int digits) {
		this.digits = digits;
	}
	
	public BigDecimal getLastPrice() {
		return this.lastPrice;
	}
	
	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public BigDecimal getAccuratePrice() {
		return accuratePrice;
	}

	public void setAccuratePrice(BigDecimal accuratePrice) {
		this.accuratePrice = accuratePrice;
	}

}
