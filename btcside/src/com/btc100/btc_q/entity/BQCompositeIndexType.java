package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 综合指数合约配置类型
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_composite_index_type")
public class BQCompositeIndexType implements Serializable {

	private static final long serialVersionUID = -2937430556262593866L;

	private Integer type_id;
	private String transaction_type_code; // 合约类型号
	private String type_name; // 类型，1：平台币指数合约，2：主流币指数合约，3：创新币指数合约
	private Boolean is_using; // 是否使用
	private BigDecimal benchmark_index = BigDecimal.ZERO; // 基准指数
	private BigDecimal closing_price = BigDecimal.ZERO; // 每日0点记录成交价格

	public BQCompositeIndexType() {

	}

	@Id
	@GeneratedValue
	public Integer getType_id() {
		return type_id;
	}

	public void setType_id(Integer type_id) {
		this.type_id = type_id;
	}

	public String getTransaction_type_code() {
		return transaction_type_code;
	}

	public void setTransaction_type_code(String transaction_type_code) {
		this.transaction_type_code = transaction_type_code;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}

	public Boolean getIs_using() {
		return is_using;
	}

	public void setIs_using(Boolean is_using) {
		this.is_using = is_using;
	}

	public BigDecimal getBenchmark_index() {
		return benchmark_index;
	}

	public void setBenchmark_index(BigDecimal benchmark_index) {
		this.benchmark_index = benchmark_index;
	}

	public BigDecimal getClosing_price() {
		return closing_price;
	}

	public void setClosing_price(BigDecimal closing_price) {
		this.closing_price = closing_price;
	}

}
