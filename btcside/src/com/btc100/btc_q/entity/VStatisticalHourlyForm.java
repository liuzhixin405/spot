package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_statistical_hourly_form")
public class VStatisticalHourlyForm implements Serializable {

	private static final long serialVersionUID = -2207530456448023092L;
	
	private long id;
	private long statistical_time;

	private String code; // 模拟交易-合约号
	private long v_total_trade_user; // 模拟交易-所有合约总交易人数
	private BigDecimal v_total_fee;// 模拟交易-手续费总额
	private BigDecimal v_hold_mount; // 模拟交易-持仓数量
	private BigDecimal v_deposit_mount; // 模拟交易-保证金总量
	private BigDecimal v_contract_balance; // 模拟交易-合约可用总额
	private BigDecimal v_trade_count; // 模拟交易-真实交易总量
	private long v_trade_user; // 模拟交易-交易人数
	private BigDecimal v_trade_fee;// 模拟交易-手续费金额

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

	public long getV_total_trade_user() {
		return v_total_trade_user;
	}

	public void setV_total_trade_user(long v_total_trade_user) {
		this.v_total_trade_user = v_total_trade_user;
	}

	public BigDecimal getV_total_fee() {
		return v_total_fee;
	}

	public void setV_total_fee(BigDecimal v_total_fee) {
		this.v_total_fee = v_total_fee;
	}

	public BigDecimal getV_hold_mount() {
		return v_hold_mount;
	}

	public void setV_hold_mount(BigDecimal v_hold_mount) {
		this.v_hold_mount = v_hold_mount;
	}

	public BigDecimal getV_deposit_mount() {
		return v_deposit_mount;
	}

	public void setV_deposit_mount(BigDecimal v_deposit_mount) {
		this.v_deposit_mount = v_deposit_mount;
	}

	public BigDecimal getV_contract_balance() {
		return v_contract_balance;
	}

	public void setV_contract_balance(BigDecimal v_contract_balance) {
		this.v_contract_balance = v_contract_balance;
	}

	public BigDecimal getV_trade_count() {
		return v_trade_count;
	}

	public void setV_trade_count(BigDecimal v_trade_count) {
		this.v_trade_count = v_trade_count;
	}

	public long getV_trade_user() {
		return v_trade_user;
	}

	public void setV_trade_user(long v_trade_user) {
		this.v_trade_user = v_trade_user;
	}

	public BigDecimal getV_trade_fee() {
		return v_trade_fee;
	}

	public void setV_trade_fee(BigDecimal v_trade_fee) {
		this.v_trade_fee = v_trade_fee;
	}

}
