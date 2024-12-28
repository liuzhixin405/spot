package com.btc100.btc_q.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.btc100.btc_q.entity.PendingDelegation;

public class TradeDataVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BigDecimal remainAmount;
	private BigDecimal waitsettleAmount;
	private BigDecimal totalAmount;
	private BigDecimal btcoefficient;
	private String toEndTime;
	private String buyDepots = "<tr><td colspan='12'>暂无买多持仓</td></tr>";
	private String sellDepots = "<tr><td colspan='12'>暂无卖空持仓</td></tr>";
	private String pendings = "<tr><td colspan='9'>暂无委托信息</td></tr>";
	private String notice;
	private BigDecimal otherAmount = BigDecimal.ZERO;
	private BigDecimal userContractReamain = BigDecimal.ZERO;
	private BigDecimal holdvols = BigDecimal.ZERO;
	private List<BQTransVo> listTransVosBuy = new ArrayList<BQTransVo>();
	private List<BQTransVo> listTransVosSell = new ArrayList<BQTransVo>();
	private List<PendingDelegation> listPending = new ArrayList<PendingDelegation>();

	public TradeDataVO() {
	}

	public TradeDataVO(BigDecimal remainAmount, BigDecimal waitsettleAmount, BigDecimal totalAmount, BigDecimal btcoefficient, String toEndTime,
			String buyDepots, String sellDepots, String pendings, String notice,BigDecimal _otherAmount,BigDecimal contractRemain) {
		this.remainAmount = remainAmount;
		this.waitsettleAmount = waitsettleAmount;
		this.totalAmount = totalAmount;
		this.btcoefficient = btcoefficient;
		this.toEndTime = toEndTime;
		this.buyDepots = buyDepots;
		this.sellDepots = sellDepots;
		this.pendings = pendings;
		this.notice = notice;
		otherAmount=_otherAmount;
		userContractReamain=contractRemain;
	}

	public BigDecimal getRemainAmount() {
		return remainAmount;
	}

	public void setRemainAmount(BigDecimal remainAmount) {
		this.remainAmount = remainAmount;
	}

	public BigDecimal getWaitsettleAmount() {
		return waitsettleAmount;
	}

	public void setWaitsettleAmount(BigDecimal waitsettleAmount) {
		this.waitsettleAmount = waitsettleAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getBtcoefficient() {
		return btcoefficient;
	}

	public void setBtcoefficient(BigDecimal btcoefficient) {
		this.btcoefficient = btcoefficient;
	}

	public String getToEndTime() {
		return toEndTime;
	}

	public void setToEndTime(String toEndTime) {
		this.toEndTime = toEndTime;
	}

	public String getBuyDepots() {
		return buyDepots;
	}

	public void setBuyDepots(String buyDepots) {
		this.buyDepots = buyDepots;
	}

	public String getSellDepots() {
		return sellDepots;
	}

	public void setSellDepots(String sellDepots) {
		this.sellDepots = sellDepots;
	}

	public String getPendings() {
		return pendings;
	}

	public void setPendings(String pendings) {
		this.pendings = pendings;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public BigDecimal getOtherAmount() {
		return otherAmount;
	}

	public void setOtherAmount(BigDecimal otherAmount) {
		this.otherAmount = otherAmount;
	}

	public BigDecimal getUserContractReamain() {
		return userContractReamain;
	}

	public void setUserContractReamain(BigDecimal userContractReamain) {
		this.userContractReamain = userContractReamain;
	}
	
	public BigDecimal getHoldvols() {
		return this.holdvols;
	}
	
	public void setHoldvols(BigDecimal holdvols) {
		this.holdvols = holdvols;
	}
	
	public List<BQTransVo> getListTransVosBuy() {
		return this.listTransVosBuy;
	}
	
	public void setListTransVosBuy(List<BQTransVo> list) {
		this.listTransVosBuy = list;
	}
	
	public List<BQTransVo> getListTransVosSell() {
		return this.listTransVosSell;
	}
	
	public void setListTransVosSell(List<BQTransVo> list) {
		this.listTransVosSell = list;
	}

	public List<PendingDelegation> getListPending() {
		return this.listPending;
	}
	
	public void setListPending(List<PendingDelegation> list) {
		this.listPending = list;
	}
	
}
