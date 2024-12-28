package com.btc100.btc_q.vo;

import java.io.Serializable;

public class MemberTransactionVo implements Serializable {

	private static final long serialVersionUID = -6424650863077649589L;

	private long userId;
	private String positionId;
	private String symbol;
	private int direction;
	private int multiple;
	private double allCount;
	private double holdCount;
	private double lockCount;
	private double avgPrice;
	private double deposit;
	private double floatingProfitLoss;
	private String profitLossRatio;
	private double forcedLiquidationPrice;
	private boolean stopLoss;
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	public double getAllCount() {
		return allCount;
	}

	public void setAllCount(double allCount) {
		this.allCount = allCount;
	}

	public double getHoldCount() {
		return holdCount;
	}

	public void setHoldCount(double holdCount) {
		this.holdCount = holdCount;
	}

	public double getLockCount() {
		return lockCount;
	}

	public void setLockCount(double lockCount) {
		this.lockCount = lockCount;
	}

	public double getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(double avgPrice) {
		this.avgPrice = avgPrice;
	}

	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}

	public double getFloatingProfitLoss() {
		return floatingProfitLoss;
	}

	public void setFloatingProfitLoss(double floatingProfitLoss) {
		this.floatingProfitLoss = floatingProfitLoss;
	}

	public String getProfitLossRatio() {
		return profitLossRatio;
	}

	public void setProfitLossRatio(String profitLossRatio) {
		this.profitLossRatio = profitLossRatio;
	}

	public double getForcedLiquidationPrice() {
		return forcedLiquidationPrice;
	}

	public void setForcedLiquidationPrice(double forcedLiquidationPrice) {
		this.forcedLiquidationPrice = forcedLiquidationPrice;
	}

	public String getPositionId() {
		return positionId;
	}

	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}

	public boolean isStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(boolean stopLoss) {
		this.stopLoss = stopLoss;
	}
	
}
