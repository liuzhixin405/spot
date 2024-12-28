package com.btc100.btc_q.vo;

import java.io.Serializable;

public class BetterTokenOrder implements Serializable, Comparable<BetterTokenOrder> {

	private static final long serialVersionUID = -8692549171234023165L;

	private long id;// 订单id
	private String pairCode;// 是Base和quote之间的组合 BTC_USD
	private long userId;// 用户id
	private long brokerId;// 券商id
	private String side;// 方向 买(buy)、卖(sell)
	private double entrustPrice;// 下单价格
	private String amount;// 下单数量
	private String dealAmount;// 成交数量
	private String quoteAmount;// 基准币数量 只有在市价买的情况下会用到
	private String dealQuoteAmount;// 基准币已成交数量
	private String systemOrderType;// 0:限价 1:市价
	private int status;// 0:未成交 1:部分成交 2:完全成交 3:撤单中 -1:已撤单
	private String sourceInfo;// 下单来源 web,api,Ios,android
	private long createOn;// 创建时间
	private long updateOn;// 修改时间
	private String symbol;// 币种
	private String trunOver; // 成交金额 dealQuoteAmount * dealAmount
	private String notStrike; // 尚未成交的数量
	private String averagePrice; // 平均成交价
	private String openAmount; // 下单数量

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPairCode() {
		return pairCode;
	}

	public void setPairCode(String pairCode) {
		this.pairCode = pairCode;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(long brokerId) {
		this.brokerId = brokerId;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public double getEntrustPrice() {
		return entrustPrice;
	}

	public void setEntrustPrice(double entrustPrice) {
		this.entrustPrice = entrustPrice;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDealAmount() {
		return dealAmount;
	}

	public void setDealAmount(String dealAmount) {
		this.dealAmount = dealAmount;
	}

	public String getQuoteAmount() {
		return quoteAmount;
	}

	public void setQuoteAmount(String quoteAmount) {
		this.quoteAmount = quoteAmount;
	}

	public String getDealQuoteAmount() {
		return dealQuoteAmount;
	}

	public void setDealQuoteAmount(String dealQuoteAmount) {
		this.dealQuoteAmount = dealQuoteAmount;
	}

	public String getSystemOrderType() {
		return systemOrderType;
	}

	public void setSystemOrderType(String systemOrderType) {
		this.systemOrderType = systemOrderType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSourceInfo() {
		return sourceInfo;
	}

	public void setSourceInfo(String sourceInfo) {
		this.sourceInfo = sourceInfo;
	}

	public long getCreateOn() {
		return createOn;
	}

	public void setCreateOn(long createOn) {
		this.createOn = createOn;
	}

	public long getUpdateOn() {
		return updateOn;
	}

	public void setUpdateOn(long updateOn) {
		this.updateOn = updateOn;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getTrunOver() {
		return trunOver;
	}

	public void setTrunOver(String trunOver) {
		this.trunOver = trunOver;
	}

	public String getNotStrike() {
		return notStrike;
	}

	public void setNotStrike(String notStrike) {
		this.notStrike = notStrike;
	}

	public String getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(String averagePrice) {
		this.averagePrice = averagePrice;
	}

	public String getOpenAmount() {
		return openAmount;
	}

	public void setOpenAmount(String openAmount) {
		this.openAmount = openAmount;
	}
	
	@Override
	public int compareTo(BetterTokenOrder o) {
		if (this.entrustPrice > o.getEntrustPrice()) {
			return -1;
		}
		return 1;
	}

}
