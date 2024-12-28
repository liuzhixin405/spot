package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 综合指数合约配置详情
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_benchmark_price")
public class BQBenchmarkPrice implements Serializable {

	private static final long serialVersionUID = 8289053902338775661L;
	
	private long id;
	private String transaction_code; // 合约号
	private BigDecimal benchmark_price; // 基准价格
	private long dateline;

	public BQBenchmarkPrice() {

	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String transaction_code) {
		this.transaction_code = transaction_code;
	}

	public BigDecimal getBenchmark_price() {
		return benchmark_price;
	}

	public void setBenchmark_price(BigDecimal benchmark_price) {
		this.benchmark_price = benchmark_price;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

}
