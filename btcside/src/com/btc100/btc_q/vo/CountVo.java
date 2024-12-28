package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 查询统计
 * 
 * @author Administrator
 * 
 */
public class CountVo implements Serializable {

	private static final long serialVersionUID = 3420302516287534162L;
	private long userId = 0; // 用户id
	private String bqCode; // 合约号
	private BigDecimal countAmount;// 成交量
	private Object type;
	private BigDecimal averagePrice;// 均价
	private long tradeTime; // 成交时间

	public CountVo() {

	}

	public CountVo(long userId, String bqCode, BigDecimal countAmount, Object type, BigDecimal averagePrice, long tradeTime) {
		this.userId = userId;
		this.bqCode = bqCode;
		this.countAmount = countAmount;
		this.type = type;
		this.averagePrice = averagePrice;
		this.tradeTime = tradeTime;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getBqCode() {
		return bqCode;
	}

	public void setBqCode(String bqCode) {
		this.bqCode = bqCode;
	}

	public BigDecimal getCountAmount() {
		return countAmount;
	}

	public void setCountAmount(BigDecimal countAmount) {
		this.countAmount = countAmount;
	}

	public Object getType() {
		return type;
	}

	public void setType(Object type) {
		this.type = type;
	}

	public BigDecimal getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(BigDecimal averagePrice) {
		this.averagePrice = averagePrice;
	}

	public long getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(long tradeTime) {
		this.tradeTime = tradeTime;
	}

}
