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
@Table(name = "bq_composite_index_detail")
public class BQCompositeIndexDetail implements Serializable {

	private static final long serialVersionUID = -5133819085601425290L;

	private long id;
	private int index_type; // 目前类型，1：平台币指数合约，2：主流币指数合约，3：创新币指数合约
	private String transaction_code; // 合约号
	private BigDecimal rate; // 所占比重
	private BigDecimal last; // 最新价
	private String market; // 市场
	private long dateline; // 最新价更新时间

	public BQCompositeIndexDetail() {

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

	public int getIndex_type() {
		return index_type;
	}

	public void setIndex_type(int index_type) {
		this.index_type = index_type;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getLast() {
		return last;
	}

	public void setLast(BigDecimal last) {
		this.last = last;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

}
