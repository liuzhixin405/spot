package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_data_broad_cast_stats")
public class BQDataBroadCastStats implements Serializable {

	private static final long serialVersionUID = 8277803604381374503L;

	private Long id;
	private String f_code; // 合约号
	private BigDecimal buy_hold_count; // 买多持仓总量
	private BigDecimal sell_hold_count; // 卖空持仓总量
	private BigDecimal total_change; // 较上一日变化量（正数代表增加，负数代表减少）
	private BigDecimal elite_buy_hold_count; // 精英买多持仓总量
	private BigDecimal elite_buy_ratio; // 精英买多持仓比例
	private BigDecimal elite_sell_hold_count; // 精英卖空持仓总量
	private BigDecimal elite_sell_ratio; // 精英卖空持仓比例
	private long record_time; // 记录时间

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getF_code() {
		return f_code;
	}

	public void setF_code(String code) {
		f_code = code;
	}

	public BigDecimal getBuy_hold_count() {
		return buy_hold_count;
	}

	public void setBuy_hold_count(BigDecimal buy_hold_count) {
		this.buy_hold_count = buy_hold_count;
	}

	public BigDecimal getSell_hold_count() {
		return sell_hold_count;
	}

	public void setSell_hold_count(BigDecimal sell_hold_count) {
		this.sell_hold_count = sell_hold_count;
	}

	public BigDecimal getTotal_change() {
		return total_change;
	}

	public void setTotal_change(BigDecimal total_change) {
		this.total_change = total_change;
	}

	public BigDecimal getElite_buy_hold_count() {
		return elite_buy_hold_count;
	}

	public void setElite_buy_hold_count(BigDecimal elite_buy_hold_count) {
		this.elite_buy_hold_count = elite_buy_hold_count;
	}

	public BigDecimal getElite_buy_ratio() {
		return elite_buy_ratio;
	}

	public void setElite_buy_ratio(BigDecimal elite_buy_ratio) {
		this.elite_buy_ratio = elite_buy_ratio;
	}

	public BigDecimal getElite_sell_hold_count() {
		return elite_sell_hold_count;
	}

	public void setElite_sell_hold_count(BigDecimal elite_sell_hold_count) {
		this.elite_sell_hold_count = elite_sell_hold_count;
	}

	public BigDecimal getElite_sell_ratio() {
		return elite_sell_ratio;
	}

	public void setElite_sell_ratio(BigDecimal elite_sell_ratio) {
		this.elite_sell_ratio = elite_sell_ratio;
	}

	public long getRecord_time() {
		return record_time;
	}

	public void setRecord_time(long record_time) {
		this.record_time = record_time;
	}

}
