package com.btc100.btc_q.vo;

import java.io.Serializable;

public class PlanPendingDelegationVO implements Serializable {

	private static final long serialVersionUID = -2148175174383736516L;

	private long delegation_id;
	private String symbol; // 合约名称
	private double amount;
	private double price;
	private double triPrice; // 触发价格
	private long createTime;
	private int multiple;
	private int direction; // 操作方向（买，卖）
	private int status; // 状态（0-未生效，1-已生效，2-失败，3-已撤销）
	private int type; // 类型（1-现价大于触发价时进行委托，2-现价低于触发价时进行委托）
	private int trade_type;//挂单类型
	private String note;// 备注
	private int stop_loss_type_id;//1-止盈，2-止损

	public long getDelegation_id() {
		return delegation_id;
	}

	public void setDelegation_id(long delegation_id) {
		this.delegation_id = delegation_id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getTriPrice() {
		return triPrice;
	}

	public void setTriPrice(double triPrice) {
		this.triPrice = triPrice;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(int trade_type) {
		this.trade_type = trade_type;
	}

	public int getStop_loss_type_id() {
		return stop_loss_type_id;
	}

	public void setStop_loss_type_id(int stop_loss_type_id) {
		this.stop_loss_type_id = stop_loss_type_id;
	}
}
