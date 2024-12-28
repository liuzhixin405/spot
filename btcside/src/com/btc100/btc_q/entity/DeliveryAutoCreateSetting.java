package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "delivery_auto_create_setting")
public class DeliveryAutoCreateSetting implements Serializable {

	private static final long serialVersionUID = -7935342140892005422L;

	private Integer id;
	private String delivery_type;
	private int transaction_type_id;
	private int transaction_kind;
	private String multiple; // 杠杆
	private String multiple_state; // 杠杆状态
	private BigDecimal trade_coefficient; // 交易系数
	private String end_time; // 交割时间点(16:00)
	private boolean is_open = true;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDelivery_type() {
		return delivery_type;
	}

	public void setDelivery_type(String delivery_type) {
		this.delivery_type = delivery_type;
	}

	public int getTransaction_type_id() {
		return transaction_type_id;
	}

	public void setTransaction_type_id(int transaction_type_id) {
		this.transaction_type_id = transaction_type_id;
	}

	public int getTransaction_kind() {
		return transaction_kind;
	}

	public void setTransaction_kind(int transaction_kind) {
		this.transaction_kind = transaction_kind;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public String getMultiple_state() {
		return multiple_state;
	}

	public void setMultiple_state(String multiple_state) {
		this.multiple_state = multiple_state;
	}

	public BigDecimal getTrade_coefficient() {
		return trade_coefficient;
	}

	public void setTrade_coefficient(BigDecimal trade_coefficient) {
		this.trade_coefficient = trade_coefficient;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public boolean getIs_open() {
		return is_open;
	}

	public void setIs_open(boolean is_open) {
		this.is_open = is_open;
	}

}
