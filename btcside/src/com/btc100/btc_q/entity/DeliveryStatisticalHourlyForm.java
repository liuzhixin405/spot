package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "delivery_statistical_hourly_form")
public class DeliveryStatisticalHourlyForm implements Serializable {

	private static final long serialVersionUID = -6452525684481312527L;

	private long id;
	private long statistical_time;
	private String code; // 合约号
	private long total_trade_user; // 总交易人数
	private BigDecimal total_fee;// 手续费总额
	private BigDecimal hold_mount; // 持仓数量
	private BigDecimal deposit_mount; // 保证金总量
	private BigDecimal contract_balance; // 合约可用总额
	private BigDecimal trade_count; // 真实交易总量
	private long trade_user; // 交易人数
	private BigDecimal trade_fee;// 手续费金额

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getStatistical_time() {
		return statistical_time;
	}

	public void setStatistical_time(long statistical_time) {
		this.statistical_time = statistical_time;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getTotal_trade_user() {
		return total_trade_user;
	}

	public void setTotal_trade_user(long total_trade_user) {
		this.total_trade_user = total_trade_user;
	}

	public BigDecimal getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(BigDecimal total_fee) {
		this.total_fee = total_fee;
	}

	public BigDecimal getHold_mount() {
		return hold_mount;
	}

	public void setHold_mount(BigDecimal hold_mount) {
		this.hold_mount = hold_mount;
	}

	public BigDecimal getDeposit_mount() {
		return deposit_mount;
	}

	public void setDeposit_mount(BigDecimal deposit_mount) {
		this.deposit_mount = deposit_mount;
	}

	public BigDecimal getContract_balance() {
		return contract_balance;
	}

	public void setContract_balance(BigDecimal contract_balance) {
		this.contract_balance = contract_balance;
	}

	public BigDecimal getTrade_count() {
		return trade_count;
	}

	public void setTrade_count(BigDecimal trade_count) {
		this.trade_count = trade_count;
	}

	public long getTrade_user() {
		return trade_user;
	}

	public void setTrade_user(long trade_user) {
		this.trade_user = trade_user;
	}

	public BigDecimal getTrade_fee() {
		return trade_fee;
	}

	public void setTrade_fee(BigDecimal trade_fee) {
		this.trade_fee = trade_fee;
	}

}
