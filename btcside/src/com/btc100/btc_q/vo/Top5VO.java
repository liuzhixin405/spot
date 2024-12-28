package com.btc100.btc_q.vo;

import java.math.BigDecimal;

public class Top5VO {

	private BigDecimal price;
	private BigDecimal countNum;
	private int priority;

	public Top5VO() {

	}

	public Top5VO(BigDecimal price, BigDecimal countNum, int priority) {
		this.price = price;
		this.countNum = countNum;
		this.priority = priority;
	}
	
	public Top5VO(BigDecimal price, BigDecimal countNum) {
		this.price = price;
		this.countNum = countNum;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getCountNum() {
		return countNum;
	}

	public void setCountNum(BigDecimal countNum) {
		this.countNum = countNum;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
