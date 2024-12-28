package com.btc100.btc_q.vo;

import java.io.Serializable;

public class TradeDetailVo implements Serializable {
	
	private static final long serialVersionUID = 2824483732098210235L;

	private long id;
	private String code;
	private String buyer;
	private String seller;
	private double tradePrice;
	private double tradeCount;
	private String tradeTime;

	public TradeDetailVo() {
		super();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBuyer() {
		return buyer;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public double getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(double tradePrice) {
		this.tradePrice = tradePrice;
	}

	public double getTradeCount() {
		return tradeCount;
	}

	public void setTradeCount(double tradeCount) {
		this.tradeCount = tradeCount;
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

}
