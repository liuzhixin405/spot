package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_exchange_bfc_record")
public class BQExchangeBfcRecord implements Serializable {

	private static final long serialVersionUID = -4973106224484852838L;

	private long id;
	private long f_user_id;
	private int ori_currency_type_id;
	private int target_currency_type_id;
	private String ori_currency_name;
	private String target_currency_name;
	private BigDecimal frozen_amount;
	private BigDecimal ori_amount;
	private BigDecimal target_amount;
	private BigDecimal order_price;
	private int state;
	private String note = "";
	private long oper_time;
	private long record_time;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long value) {
		id = value;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public int getOri_currency_type_id() {
		return ori_currency_type_id;
	}

	public void setOri_currency_type_id(int ori_currency_type_id) {
		this.ori_currency_type_id = ori_currency_type_id;
	}

	public int getTarget_currency_type_id() {
		return target_currency_type_id;
	}

	public void setTarget_currency_type_id(int target_currency_type_id) {
		this.target_currency_type_id = target_currency_type_id;
	}

	public String getOri_currency_name() {
		return ori_currency_name;
	}

	public void setOri_currency_name(String ori_currency_name) {
		this.ori_currency_name = ori_currency_name;
	}

	public String getTarget_currency_name() {
		return target_currency_name;
	}

	public void setTarget_currency_name(String target_currency_name) {
		this.target_currency_name = target_currency_name;
	}

	public BigDecimal getFrozen_amount() {
		return frozen_amount;
	}

	public void setFrozen_amount(BigDecimal frozen_amount) {
		this.frozen_amount = frozen_amount;
	}

	public BigDecimal getOri_amount() {
		return ori_amount;
	}

	public void setOri_amount(BigDecimal ori_amount) {
		this.ori_amount = ori_amount;
	}

	public BigDecimal getTarget_amount() {
		return target_amount;
	}

	public void setTarget_amount(BigDecimal target_amount) {
		this.target_amount = target_amount;
	}

	public BigDecimal getOrder_price() {
		return order_price;
	}

	public void setOrder_price(BigDecimal order_price) {
		this.order_price = order_price;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public long getOper_time() {
		return oper_time;
	}

	public void setOper_time(long oper_time) {
		this.oper_time = oper_time;
	}

	public long getRecord_time() {
		return record_time;
	}

	public void setRecord_time(long record_time) {
		this.record_time = record_time;
	}

}
