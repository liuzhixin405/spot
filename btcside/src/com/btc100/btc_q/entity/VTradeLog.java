package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

/**
 * 交易记录日志表。 一个交易会产生两个用户对这个交易的操作记录，基本一份是更新，另一份是写入新记录
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "trade_log")
// TODO:怎么对应多个不同的表
@Immutable
public class VTradeLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5178299137410478962L;

	private long trade_id;// 交易ID
	private long transaction_id;// 合约号id
	private String transaction_code;// 合约号
	private int trade_type_id;// 成交类型(1:多单开仓/2:多单平仓/3:空头开仓/4:空头平仓)
	private int trade_trans_dirction;// 这毛交易的成交方向,1买，2卖

	private BigDecimal hold_price;
	private BigDecimal trade_average_price;// 成交均价
	private BigDecimal trade_count;// 成交数量
	private int trade_multiple;// 杠杆
	private BigDecimal trade_deposit;// 保证金
	private BigDecimal trade_income;// 盈亏额
	private BigDecimal trade_fee;// 手续费
	private BigDecimal trade_bfx_fee = BigDecimal.ZERO;// 手续费
	private long f_user_id;// 用户ID
	private long trade_time;// 成交时间,unix时间格式
	private long pending_no = 0;//对应挂单流水号
	
	@Transient
	private BigDecimal force_price = BigDecimal.ZERO;
	
	public VTradeLog() {

	}
	
	public VTradeLog(long userId, BigDecimal tradeCount) {
		this.trade_count = tradeCount;
		this.f_user_id = userId;
	}
	
	public VTradeLog(long time, BigDecimal lowPrice, BigDecimal highPrice, BigDecimal firstPrice, BigDecimal lastPrice) {
		super();
		trade_time = time;
		trade_average_price = lowPrice;
		trade_average_price = highPrice;
		trade_average_price = firstPrice;
		trade_average_price = lastPrice;
	}

	public VTradeLog(long transactionId, String transactionCode,
			int tradeTypeId, int tradeTransDirction,BigDecimal holdPrice,
			BigDecimal tradeAveragePrice, BigDecimal tradeCount,
			int tradeMultiple, BigDecimal tradeDeposit, BigDecimal tradeIncome,
			BigDecimal tradeFee, long fUserId, long pendingNo) {
		super();
		transaction_id = transactionId;
		transaction_code = transactionCode;
		trade_type_id = tradeTypeId;
		trade_trans_dirction = tradeTransDirction;
		hold_price = holdPrice;
		trade_average_price = tradeAveragePrice;
		trade_count = tradeCount;
		trade_multiple = tradeMultiple;
		trade_deposit = tradeDeposit;
		trade_income = tradeIncome;
		trade_fee = tradeFee;
		f_user_id = fUserId;
		pending_no = pendingNo;
	}

	@Id
	@GeneratedValue
	public long getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(long tradeId) {
		trade_id = tradeId;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transactionId) {
		transaction_id = transactionId;
	}

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String transactionCode) {
		transaction_code = transactionCode;
	}

	public int getTrade_type_id() {
		return trade_type_id;
	}

	public void setTrade_type_id(int tradeTypeId) {
		trade_type_id = tradeTypeId;
	}
	
	public BigDecimal getHold_price() {
		return hold_price;
	}
	
	public void setHold_price(BigDecimal holdPrice) {
		this.hold_price = holdPrice;
	}

	public BigDecimal getTrade_average_price() {
		return trade_average_price;
	}

	public void setTrade_average_price(BigDecimal tradeAveragePrice) {
		trade_average_price = tradeAveragePrice;
	}

	public BigDecimal getTrade_count() {
		return trade_count;
	}

	public void setTrade_count(BigDecimal tradeCount) {
		trade_count = tradeCount;
	}

	public int getTrade_multiple() {
		return trade_multiple;
	}

	public void setTrade_multiple(int tradeMultiple) {
		trade_multiple = tradeMultiple;
	}

	public BigDecimal getTrade_deposit() {
		return trade_deposit;
	}

	public void setTrade_deposit(BigDecimal tradeDeposit) {
		trade_deposit = tradeDeposit;
	}

	public BigDecimal getTrade_income() {
		return trade_income;
	}

	public void setTrade_income(BigDecimal tradeIncome) {
		trade_income = tradeIncome;
	}

	public BigDecimal getTrade_fee() {
		return trade_fee;
	}

	public void setTrade_fee(BigDecimal tradeFee) {
		trade_fee = tradeFee;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public long getTrade_time() {
		return trade_time;
	}

	public void setTrade_time(long tradeTime) {
		trade_time = tradeTime;
	}

	public int getTrade_trans_dirction() {
		return trade_trans_dirction;
	}

	public void setTrade_trans_dirction(int tradeTransDirction) {
		trade_trans_dirction = tradeTransDirction;
	}

	public BigDecimal getTrade_bfx_fee() {
		return trade_bfx_fee;
	}

	public void setTrade_bfx_fee(BigDecimal trade_bfx_fee) {
		this.trade_bfx_fee = trade_bfx_fee;
	}

	public long getPending_no() {
		return pending_no;
	}

	public void setPending_no(long pending_no) {
		this.pending_no = pending_no;
	}

	@Transient
	public BigDecimal getForce_price() {
		return force_price;
	}

	public void setForce_price(BigDecimal force_price) {
		this.force_price = force_price;
	}

}
