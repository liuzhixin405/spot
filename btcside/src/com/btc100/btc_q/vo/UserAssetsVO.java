package com.btc100.btc_q.vo;

import java.math.BigDecimal;

/**
 * 用户中心 页面VO类
 * 
 * @author Administrator
 *
 */
public class UserAssetsVO {

	private String code;// 合约号
	private int transactionType;// 合约类型
	private BigDecimal totalAmount;// 持仓总量
	private BigDecimal buyAmount;// 做多持仓
	private int buyMul;// 做多杠杆
	private BigDecimal sellAmount;// 做空持仓
	private int sellMul;// 做空杠杆
	private BigDecimal holdDeposit;// 持仓保证金
	private BigDecimal floatGainLoss;// 浮动盈亏
	private BigDecimal delegationAmount;// 委托总量
	private BigDecimal buydelegation;// 多委托
	private BigDecimal selldelegation;// 空委托
	private BigDecimal delegationDeposit;// 委托保证金
	private BigDecimal f_btc;// 合约可用
	private BigDecimal f_lockbtc;// 合约冻结
	private BigDecimal contractValue;// 合约价值=持仓保证金+浮动盈亏+委托保证金+合约可用
	private String showName;
	private boolean transfer_in_enable;
	private boolean transfer_out_enable;

	public UserAssetsVO() {
	}

	public UserAssetsVO(String code, BigDecimal totalAmount, BigDecimal buyAmount, int buyMul, BigDecimal sellAmount,
			int sellMul, BigDecimal delegationAmount, BigDecimal buydelegation, BigDecimal selldelegation,
			BigDecimal fBtc, BigDecimal flockBtc) {
		this.code = code;
		this.totalAmount = totalAmount;
		this.buyAmount = buyAmount;
		this.buyMul = buyMul;
		this.sellAmount = sellAmount;
		this.sellMul = sellMul;
		this.delegationAmount = delegationAmount;
		this.buydelegation = buydelegation;
		this.selldelegation = selldelegation;
		f_btc = fBtc;
		f_lockbtc = flockBtc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getBuyAmount() {
		return buyAmount;
	}

	public void setBuyAmount(BigDecimal buyAmount) {
		this.buyAmount = buyAmount;
	}

	public int getBuyMul() {
		return buyMul;
	}

	public void setBuyMul(int buyMul) {
		this.buyMul = buyMul;
	}

	public BigDecimal getSellAmount() {
		return sellAmount;
	}

	public void setSellAmount(BigDecimal sellAmount) {
		this.sellAmount = sellAmount;
	}

	public int getSellMul() {
		return sellMul;
	}

	public void setSellMul(int sellMul) {
		this.sellMul = sellMul;
	}

	public BigDecimal getHoldDeposit() {
		return holdDeposit;
	}

	public void setHoldDeposit(BigDecimal holdDeposit) {
		this.holdDeposit = holdDeposit;
	}

	public BigDecimal getFloatGainLoss() {
		return floatGainLoss;
	}

	public void setFloatGainLoss(BigDecimal floatGainLoss) {
		this.floatGainLoss = floatGainLoss;
	}

	public BigDecimal getDelegationAmount() {
		return delegationAmount;
	}

	public void setDelegationAmount(BigDecimal delegationAmount) {
		this.delegationAmount = delegationAmount;
	}

	public BigDecimal getBuydelegation() {
		return buydelegation;
	}

	public void setBuydelegation(BigDecimal buydelegation) {
		this.buydelegation = buydelegation;
	}

	public BigDecimal getSelldelegation() {
		return selldelegation;
	}

	public void setSelldelegation(BigDecimal selldelegation) {
		this.selldelegation = selldelegation;
	}

	public BigDecimal getDelegationDeposit() {
		return delegationDeposit;
	}

	public void setDelegationDeposit(BigDecimal delegationDeposit) {
		this.delegationDeposit = delegationDeposit;
	}

	public BigDecimal getF_btc() {
		return f_btc;
	}

	public void setF_btc(BigDecimal f_btc) {
		this.f_btc = f_btc;
	}

	public BigDecimal getF_lockbtc() {
		return f_lockbtc;
	}

	public void setF_lockbtc(BigDecimal f_lockbtc) {
		this.f_lockbtc = f_lockbtc;
	}

	public BigDecimal getContractValue() {
		return contractValue;
	}

	public void setContractValue(BigDecimal contractValue) {
		this.contractValue = contractValue;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public boolean isTransfer_in_enable() {
		return transfer_in_enable;
	}

	public void setTransfer_in_enable(boolean transfer_in_enable) {
		this.transfer_in_enable = transfer_in_enable;
	}

	public boolean isTransfer_out_enable() {
		return transfer_out_enable;
	}

	public void setTransfer_out_enable(boolean transfer_out_enable) {
		this.transfer_out_enable = transfer_out_enable;
	}

}
