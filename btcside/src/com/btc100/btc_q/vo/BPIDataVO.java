package com.btc100.btc_q.vo;

import java.io.Serializable;

public class BPIDataVO implements Serializable {
	
	private static final long serialVersionUID = -2249545470621399444L;
	
	private long date;
	private long timestamp;
	private long tid;
	private double price;
	private double amount;
	private String code;
	private boolean up;
	private String fudu;
	private String showName;

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public long getTid() {
		return tid;
	}

	public void setTid(long tid) {
		this.tid = tid;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public boolean getUp() {
		return up;
	}
	
	public void setUp(boolean up) {
		this.up = up;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getFudu() {
		return fudu;
	}

	public void setFudu(String fudu) {
		this.fudu = fudu;
	}
}
