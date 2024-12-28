package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class TradeAjaxVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String typeCode = "";
	private BigDecimal lastPrice;
	private BigDecimal lastpriceOneMinAgo;
	private BigDecimal btc_index_price;
	private BigDecimal indexPrice;
	private String indexFudu;
	private BigDecimal bpi_forfirst;
	private List<Top5DelegationVO> selltop5List;
	private List<Top5DelegationVO> buytop5List;
	private String transactionEndTime;
	private BigDecimal transCoeffcient;
	private int nomalTrade;
	private BigDecimal todayHigh; 	// 今日最低
	private BigDecimal todayLow; 	// 今日最高
	private BigDecimal tradevols; 	// 交易量
	private BigDecimal trades; 		// 交易额
	private BigDecimal holdvols; 	// 持仓量
	private BigDecimal maxPrice; 	// 浮动价格-最高
	private BigDecimal minPrice; 	// 浮动价格-最低
	private double cnyRate = 7; 	// 人民币汇率
	private int digits; 			// 价格保留小数味素
	private int amountDigits; 		// 数量保留小数味素
	private List<MulpitleAndStateVO> multiples; // 杠杆
	private Boolean suspend; 	// 暂停交易
	private Boolean tradeTime; 	// 是否为交易时间（股指）
	private BigDecimal fundingFeeRate = null; 	// 资金费率
	private String fudu = "0%";
	private int transactionKind = 0;
	private String markPrice = "--";
	private String showName;
	private String showNameEn;
	private String showNameJp;
	private String showNameKr;
	private String showNameHk;

	public BigDecimal getTransCoeffcient() {
		return transCoeffcient;
	}

	public void setTransCoeffcient(BigDecimal transCoeffcient) {
		this.transCoeffcient = transCoeffcient;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public BigDecimal getLastpriceOneMinAgo() {
		return lastpriceOneMinAgo;
	}

	public void setLastpriceOneMinAgo(BigDecimal lastpriceOneMinAgo) {
		this.lastpriceOneMinAgo = lastpriceOneMinAgo;
	}

	public BigDecimal getBtc_index_price() {
		return btc_index_price;
	}

	public void setBtc_index_price(BigDecimal btcIndexPrice) {
		btc_index_price = btcIndexPrice;
	}

	public List<Top5DelegationVO> getSelltop5List() {
		return selltop5List;
	}

	public void setSelltop5List(List<Top5DelegationVO> selltop5List) {
		this.selltop5List = selltop5List;
	}

	public List<Top5DelegationVO> getBuytop5List() {
		return buytop5List;
	}

	public void setBuytop5List(List<Top5DelegationVO> buytop5List) {
		this.buytop5List = buytop5List;
	}

	public String getTransactionEndTime() {
		return transactionEndTime;
	}

	public void setTransactionEndTime(String transactionEndTime) {
		this.transactionEndTime = transactionEndTime;
	}

	public int getNomalTrade() {
		return nomalTrade;
	}

	public void setNomalTrade(int nomalTrade) {
		this.nomalTrade = nomalTrade;
	}

	public BigDecimal getBpi_forfirst() {
		return bpi_forfirst;
	}

	public void setBpi_forfirst(BigDecimal bpiForfirst) {
		bpi_forfirst = bpiForfirst;
	}

	public BigDecimal getTodayHigh() {
		return todayHigh;
	}

	public void setTodayHigh(BigDecimal todayHigh) {
		this.todayHigh = todayHigh;
	}

	public BigDecimal getTodayLow() {
		return todayLow;
	}

	public void setTodayLow(BigDecimal todayLow) {
		this.todayLow = todayLow;
	}

	public BigDecimal getTradevols() {
		return tradevols;
	}

	public void setTradevols(BigDecimal tradevols) {
		this.tradevols = tradevols;
	}

	public BigDecimal getTrades() {
		return trades;
	}

	public void setTrades(BigDecimal trades) {
		this.trades = trades;
	}

	public BigDecimal getHoldvols() {
		return holdvols;
	}

	public void setHoldvols(BigDecimal holdvols) {
		this.holdvols = holdvols;
	}

	public BigDecimal getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}
	
	public BigDecimal getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}
	
	public int getDigits() {
		return this.digits;
	}
	
	public void setDigits(int digits) {
		this.digits = digits;
	}
	
	public int getAmountDigits() {
		return this.amountDigits;
	}
	
	public void setAmountDigits(int digits) {
		this.amountDigits = digits;
	}

	public List<MulpitleAndStateVO> getMultiples() {
		return multiples;
	}

	public void setMultiples(List<MulpitleAndStateVO> multiples) {
		this.multiples = multiples;
	}

	public Boolean isSuspend() {
		return suspend;
	}

	public void setSuspend(Boolean suspend) {
		this.suspend = suspend;
	}

	public Boolean getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Boolean tradeTime) {
		this.tradeTime = tradeTime;
	}

	public BigDecimal getFundingFeeRate() {
		return fundingFeeRate;
	}

	public void setFundingFeeRate(BigDecimal fundingFeeRate) {
		this.fundingFeeRate = fundingFeeRate;
	}

	public String getFudu() {
		return fudu;
	}

	public void setFudu(String fudu) {
		this.fudu = fudu;
	}

	public int getTransactionKind() {
		return transactionKind;
	}

	public void setTransactionKind(int transactionKind) {
		this.transactionKind = transactionKind;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getShowNameEn() {
		return showNameEn;
	}

	public void setShowNameEn(String showNameEn) {
		this.showNameEn = showNameEn;
	}

	public String getShowNameJp() {
		return showNameJp;
	}

	public void setShowNameJp(String showNameJp) {
		this.showNameJp = showNameJp;
	}

	public String getShowNameKr() {
		return showNameKr;
	}

	public void setShowNameKr(String showNameKr) {
		this.showNameKr = showNameKr;
	}

	public String getShowNameHk() {
		return showNameHk;
	}

	public void setShowNameHk(String showNameHk) {
		this.showNameHk = showNameHk;
	}

	public BigDecimal getIndexPrice() {
		return indexPrice;
	}

	public void setIndexPrice(BigDecimal indexPrice) {
		this.indexPrice = indexPrice;
	}

	public double getCnyRate() {
		return cnyRate;
	}

	public void setCnyRate(double cnyRate) {
		this.cnyRate = cnyRate;
	}

	public String getMarkPrice() {
		return markPrice;
	}

	public void setMarkPrice(String markPrice) {
		this.markPrice = markPrice;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getIndexFudu() {
		return indexFudu;
	}

	public void setIndexFudu(String indexFudu) {
		this.indexFudu = indexFudu;
	}
}
