package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_trade_month_stats")
public class VTradeMonthStats implements Serializable {
	
private static final long serialVersionUID = -3673950707494408266L;
	
	private long id;
	private long user_id;
	private BigDecimal sum_income; // 总收入
	private BigDecimal sum_depoist; // 总保证金
	private BigDecimal yield_rate; // 收益率
	private String user_name; // 用户名
	private long record_time; // 周开始时间
	
	@Id
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public BigDecimal getSum_income() {
		return sum_income;
	}

	public void setSum_income(BigDecimal sum_income) {
		this.sum_income = sum_income;
	}

	public BigDecimal getSum_depoist() {
		return sum_depoist;
	}

	public void setSum_depoist(BigDecimal sum_depoist) {
		this.sum_depoist = sum_depoist;
	}

	public BigDecimal getYield_rate() {
		return yield_rate;
	}

	public void setYield_rate(BigDecimal yield_rate) {
		this.yield_rate = yield_rate;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public long getRecord_time() {
		return record_time;
	}
	
	public void setRecord_time(long time) {
		record_time = time;
	}
	
}
