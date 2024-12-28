package com.btc100.btc_q.vo;

import java.io.Serializable;

public class UserRechargeVo implements Serializable {

	private static final long serialVersionUID = 2994314385375806051L;

	private long recordId;
	private int operation; // 操作类型(1:充值/2:提现)
	private String currencyName; // 资产名称
	private long dateline; // 申请时间
	private String address; // 地址
	private double amount; // 金额
	private String state; // 状态
	private String remark; // 备注
	private String txid; // 钱包交易ID

	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

}
