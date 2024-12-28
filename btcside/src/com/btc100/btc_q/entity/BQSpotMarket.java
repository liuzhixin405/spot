package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 现货交易区（币币交易）
 * 
 */
@Entity
@Table(name = "bq_spot_market")
public class BQSpotMarket implements Serializable {
	
	private static final long serialVersionUID = 5867050806188654809L;
	
	private Integer id;						// 交易区id
	private String market_name;				// 交易区名称（eg:btc,eth）
	private String coin_name;				// 交易区币种名称（eg:btc,eth）
	private Integer coin_id;				// 交易区币种id（与币种类型id关联）
	private Boolean is_using;				// 是否使用
	private String index_address;			// 指数地址
	private BigDecimal index_value;			// 指数值

	public BQSpotMarket() {

	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMarket_name() {
		return market_name;
	}

	public void setMarket_name(String market_name) {
		this.market_name = market_name;
	}

	public String getCoin_name() {
		return coin_name;
	}

	public void setCoin_name(String coin_name) {
		this.coin_name = coin_name;
	}

	public Integer getCoin_id() {
		return coin_id;
	}

	public void setCoin_id(Integer coin_id) {
		this.coin_id = coin_id;
	}

	public Boolean getIs_using() {
		return is_using;
	}

	public void setIs_using(Boolean is_using) {
		this.is_using = is_using;
	}

	public String getIndex_address() {
		return index_address;
	}

	public void setIndex_address(String index_address) {
		this.index_address = index_address;
	}

	public BigDecimal getIndex_value() {
		return index_value;
	}

	public void setIndex_value(BigDecimal index_value) {
		this.index_value = index_value;
	}

}
