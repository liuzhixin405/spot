package com.btc100.btc_q.vo;

import java.math.BigDecimal;

public class BpiVO implements Comparable<BpiVO> {
	private String siteName;
	private Double latestPrice;
	private Double latestUsdPrice;
	private Double highestPrice;
	private Double lowestPrice;
	private String dealCount;
	private Double upDownRate;
	private Long   volume;
	private Double ratio;
	private Double turnover;
	private Double cap;
	
	private Double change_1h;
	private Double change_24h;
	private Double change_7d;
	
	private Boolean largeVol;
	private String transPair;
	private String updateTime;

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Double getLatestPrice() {
		return latestPrice;
	}

	public void setLatestPrice(Double latestPrice) {
		this.latestPrice = latestPrice;
	}
	
	public Double getLatestUsdPrice() {
		return latestUsdPrice;
	}

	public void setLatestUsdPrice(Double latestUsdPrice) {
		this.latestUsdPrice = latestUsdPrice;
	}

	public Double getHighestPrice() {
		return highestPrice;
	}

	public void setHighestPrice(Double highestPrice) {
		this.highestPrice = highestPrice;
	}

	public Double getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(Double lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	public String getDealCount() {
		return dealCount;
	}

	public void setDealCount(String dealCount) {
		this.dealCount = dealCount;
	}

	public Double getUpDownRate() {
		return upDownRate;
	}

	public void setUpDownRate(Double upDownRate) {
		this.upDownRate = upDownRate;
	}

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	public int compareTo(BpiVO o) {
		return new BigDecimal(o.ratio - this.ratio).signum();
	}
	
	public void setVolume(Long vol) {
		this.volume = vol;
	}
	
	public Long getVolume() {
		return volume;
	}
	
	public void setTurnover(Double turnover) {
		this.turnover = turnover;
	}
	
	public Double getTurnover() {
		return turnover;
	}
	
	public void setCap(Double cap) {
		this.cap = cap;
	}
	
	public Double getCap() {
		return cap;
	}
	
	public void setChange_1h(Double change_1h) {
		this.change_1h = change_1h;
	}
	
	public Double getChange_1h() {
		return change_1h;
	}
	
	public void setChange_24h(Double change_24h) {
		this.change_24h = change_24h;
	}
	
	public Double getChange_24h() {
		return change_24h;
	}
	
	public void setChange_7d(Double change_7d) {
		this.change_7d = change_7d;
	}
	
	public Double getChange_7d() {
		return change_7d;
	}

	public String getTransPair() {
		return transPair;
	}
	
	public void setTransPair(String transPair) {
		this.transPair = transPair;
	}
	
	public Boolean getLargeVol() {
		return largeVol;
	}
	
	public void setLargeVol(Boolean largeVol) {
		this.largeVol = largeVol;
	}
	
	public String getUpdateTime() {
		return updateTime;
	}
	
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
}
