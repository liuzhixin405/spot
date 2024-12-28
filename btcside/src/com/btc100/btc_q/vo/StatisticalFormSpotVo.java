package com.btc100.btc_q.vo;

import java.io.Serializable;

public class StatisticalFormSpotVo implements Serializable {

	private static final long serialVersionUID = -9121469299360147970L;

	private String code; // 交易对
	private String market; // 交易对市场
	private String coin; // 交易对币种
	private double tradeAmount; // 真实交易额
	private long tradeUser; // 交易人数
	private double buyTradeFee;// 买入手续费
	private double sellTradeFee;// 卖出手续费
	private double buyTradeBfxFee;// BFX抵扣买入手续费
	private double sellTradeBfxFee;// BFX抵扣卖出手续费
	private double coinRebate;// 交易币种返佣
	private double marketRebate;// 市场币种返佣
	private double bfxRebate = 0;// bfx抵扣返佣

	public StatisticalFormSpotVo() {

	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getCoin() {
		return coin;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public double getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(double tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public long getTradeUser() {
		return tradeUser;
	}

	public void setTradeUser(long tradeUser) {
		this.tradeUser = tradeUser;
	}

	public double getBuyTradeFee() {
		return buyTradeFee;
	}

	public void setBuyTradeFee(double buyTradeFee) {
		this.buyTradeFee = buyTradeFee;
	}

	public double getSellTradeFee() {
		return sellTradeFee;
	}

	public void setSellTradeFee(double sellTradeFee) {
		this.sellTradeFee = sellTradeFee;
	}

	public double getBuyTradeBfxFee() {
		return buyTradeBfxFee;
	}

	public void setBuyTradeBfxFee(double buyTradeBfxFee) {
		this.buyTradeBfxFee = buyTradeBfxFee;
	}

	public double getSellTradeBfxFee() {
		return sellTradeBfxFee;
	}

	public void setSellTradeBfxFee(double sellTradeBfxFee) {
		this.sellTradeBfxFee = sellTradeBfxFee;
	}

	public double getCoinRebate() {
		return coinRebate;
	}

	public void setCoinRebate(double coinRebate) {
		this.coinRebate = coinRebate;
	}

	public double getMarketRebate() {
		return marketRebate;
	}

	public void setMarketRebate(double marketRebate) {
		this.marketRebate = marketRebate;
	}

	public double getBfxRebate() {
		return bfxRebate;
	}

	public void setBfxRebate(double bfxRebate) {
		this.bfxRebate = bfxRebate;
	}

}
