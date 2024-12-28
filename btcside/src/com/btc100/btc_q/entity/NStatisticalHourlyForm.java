package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "n_statistical_hourly_form")
public class NStatisticalHourlyForm implements Serializable {

	private static final long serialVersionUID = -2207530456448023093L;
	
	private long id;
	private long statistical_time; // 统计时间（前一个小时的时间点）
	private long total_trade_user; // 所有合约总交易人数
	private BigDecimal total_fee;// 手续费总额
	private BigDecimal total_bfx_fee;// bfx抵扣手续费总额
	private BigDecimal total_rebate;// 返佣总额
	private BigDecimal total_bfx_rebate;// bfx返佣总额

	private String code; // 合约号
	private BigDecimal hold_mount; // 持仓数量
	private BigDecimal deposit_mount; // 保证金总量
	private BigDecimal contract_balance; // 合约可用总额
	private BigDecimal trade_count; // 真实交易总量
	private long trade_user; // 交易人数
	private BigDecimal trade_fee;// 手续费金额
	private BigDecimal trade_bfx_fee;// bfx抵扣手续费
	private BigDecimal rebate;// 返佣
	private BigDecimal bfx_rebate;// bfx返佣

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

	public BigDecimal getTotal_bfx_fee() {
		return total_bfx_fee;
	}

	public void setTotal_bfx_fee(BigDecimal total_bfx_fee) {
		this.total_bfx_fee = total_bfx_fee;
	}

	public BigDecimal getTotal_rebate() {
		return total_rebate;
	}

	public void setTotal_rebate(BigDecimal total_rebate) {
		this.total_rebate = total_rebate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public BigDecimal getTrade_bfx_fee() {
		return trade_bfx_fee;
	}

	public void setTrade_bfx_fee(BigDecimal trade_bfx_fee) {
		this.trade_bfx_fee = trade_bfx_fee;
	}

	public BigDecimal getRebate() {
		return rebate;
	}

	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public BigDecimal getBfx_rebate() {
		return bfx_rebate;
	}

	public void setBfx_rebate(BigDecimal bfx_rebate) {
		this.bfx_rebate = bfx_rebate;
	}

	public BigDecimal getTotal_bfx_rebate() {
		return total_bfx_rebate;
	}

	public void setTotal_bfx_rebate(BigDecimal total_bfx_rebate) {
		this.total_bfx_rebate = total_bfx_rebate;
	}

}
