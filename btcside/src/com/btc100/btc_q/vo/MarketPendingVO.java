package com.btc100.btc_q.vo;

import java.math.BigDecimal;

/**
 * 市场挂单 页面VO类
 * 
 * @author Administrator
 * 
 */
public class MarketPendingVO {

	private BigDecimal price;// 单价
	private BigDecimal count;// 数量

	public MarketPendingVO() {
	}

	public MarketPendingVO(BigDecimal price, BigDecimal count) {
		this.price = price;
		this.count = count;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getCount() {
		return count;
	}

	public void setCount(BigDecimal count) {
		this.count = count;
	}

}
