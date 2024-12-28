package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * 资金费率表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "funding_fee_rate")
@Immutable
public class FundingFeeRate implements Serializable {
	
	private static final long serialVersionUID = 1510643493228690137L;
	
	private long id;
	private String f_code;	// 合约号
	private int count_times;// 当前统计次数
	private BigDecimal fee_rate;// 资金费率
	private long dateline;// 更新时间
	private int done;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getF_code() {
		return f_code;
	}

	public void setF_code(String f_code) {
		this.f_code = f_code;
	}

	public int getCount_times() {
		return count_times;
	}

	public void setCount_times(int count_times) {
		this.count_times = count_times;
	}

	public BigDecimal getFee_rate() {
		return fee_rate;
	}

	public void setFee_rate(BigDecimal fee_rate) {
		this.fee_rate = fee_rate;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public int getDone() {
		return done;
	}

	public void setDone(int done) {
		this.done = done;
	}

}
