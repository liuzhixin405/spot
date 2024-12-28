package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 合约类型表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "delivery_transaction_type")
public class DeliveryTransactionType implements Serializable {

	private static final long serialVersionUID = -6850854416139292997L;

	private Integer type_id;				// 类型id
	private String type_name;				// 类型名称
	private String type_code;				// 类型号
	private String default_multiple;		// 默认杆杠
	private String default_multiple_state;	// 默认杠杠状态
	private BigDecimal default_coefficient;	// 默认交易系数
	private Boolean is_using;				// 是否使用
	private String index_address;			// 指数地址
	private BigDecimal index_value;			// 指数值
	private BigDecimal min_kc_nums;			// 最小开仓数量
	private BigDecimal min_float_price;		// 最小浮动价格
	private BigDecimal max_kdc_nums;		// 最大可开多仓数量
	private BigDecimal max_kkc_nums;		// 最大可开空仓数量

	public DeliveryTransactionType() {

	}

	public DeliveryTransactionType(String type_name) {
		this.type_name = type_name;
	}

	@Id
	@GeneratedValue
	public Integer getType_id() {
		return type_id;
	}

	public void setType_id(Integer type_id) {
		this.type_id = type_id;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	
	public String getType_code() {
		return type_code;
	}

	public void setType_code(String type_code) {
		this.type_code = type_code;
	}
	
	public String getDefault_multiple() {
		return default_multiple;
	}
	
	public void setDefault_multiple(String multiple) {
		this.default_multiple = multiple;
	}
	
	public String getDefault_multiple_state() {
		return default_multiple_state;
	}
	
	public void setDefault_multiple_state(String multiple_state) {
		this.default_multiple_state = multiple_state;
	}
	
	public BigDecimal getDefault_coefficient() {
		return default_coefficient;
	}
	
	public void setDefault_coefficient(BigDecimal default_coefficient) {
		this.default_coefficient = default_coefficient;
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
	
	public void setIndex_address(String address) {
		this.index_address = address;
	}
	
	public BigDecimal getIndex_value() {
		return index_value;
	}
	
	public void setIndex_value(BigDecimal index_value) {
		this.index_value = index_value;
	}
	
	public BigDecimal getMin_kc_nums() {
		return min_kc_nums;
	}
	
	public void setMin_kc_nums(BigDecimal min_kc_nums) {
		this.min_kc_nums = min_kc_nums;
	}

	public BigDecimal getMin_float_price() {
		return min_float_price;
	}
	
	public void setMin_float_price(BigDecimal min_float_price) {
		this.min_float_price = min_float_price;
	}
	
	public BigDecimal getMax_kdc_nums() {
		return max_kdc_nums;
	}
	
	public void setMax_kdc_nums(BigDecimal nums) {
		this.max_kdc_nums = nums;
	}
	
	public BigDecimal getMax_kkc_nums() {
		return max_kkc_nums;
	}
	
	public void setMax_kkc_nums(BigDecimal nums) {
		this.max_kkc_nums = nums;
	}
}
