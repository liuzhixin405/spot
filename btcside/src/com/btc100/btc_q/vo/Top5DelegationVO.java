package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 市场挂单VO类
 * 
 * @author Administrator
 * 
 */
public class Top5DelegationVO implements Comparable<Top5DelegationVO>, Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private BigDecimal price;
	private BigDecimal countNum;
	private int priority;
	private BigDecimal sumNum;

	public Top5DelegationVO() {
	}

	public Top5DelegationVO(String name, BigDecimal price, BigDecimal countNum, int priority,BigDecimal _sumNum) {
		this.name = name;
		this.price = price;
		this.countNum = countNum;
		this.priority = priority;
		this.sumNum=_sumNum;
	}
	
	public Top5DelegationVO(String name, BigDecimal price, BigDecimal countNum, BigDecimal _sumNum) {
		this.name = name;
		this.price = price;
		this.countNum = countNum;
		this.sumNum=_sumNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int compareTo(Top5DelegationVO o) {
		int res = this.price.compareTo(o.getPrice());
		return 0 - res;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public BigDecimal getSumNum() {
		return sumNum;
	}

	public void setSumNum(BigDecimal sumNum) {
		this.sumNum = sumNum;
	}
	
	

}
