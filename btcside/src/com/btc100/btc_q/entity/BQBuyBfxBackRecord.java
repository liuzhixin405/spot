package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_buy_bfx_back_record")
public class BQBuyBfxBackRecord implements Serializable {

	private static final long serialVersionUID = -4719597295292472012L;
	
	private long id;
	private long f_user_id;							// 用户Id
	private int currency_type_id;					// 回购所用币种Id
	private int bfx_currency_id;					// Bfx币种Id
	private BigDecimal price = BigDecimal.ZERO;		// 回购价格
	private BigDecimal amount = BigDecimal.ZERO; 	// 回购数量
	private BigDecimal money = BigDecimal.ZERO; 	// 金额（usdr）
	private long record_time;						// 记录时间
	private long confirm_time;						// 确认时间
	private int state = 0;							// 状态 0-提交（回购中），1-完成，2-失败（驳回），3-取消
	private String note = "";						// 备注
	
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
	
	public void setF_user_id(long uid) {
		f_user_id = uid;
	}

	public long getRecord_time() {
		return record_time;
	}
	
	public void setRecord_time(long time) {
		this.record_time = time;
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

	public int getCurrency_type_id() {
		return currency_type_id;
	}

	public void setCurrency_type_id(int currency_type_id) {
		this.currency_type_id = currency_type_id;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public long getConfirm_time() {
		return confirm_time;
	}

	public void setConfirm_time(long confirm_time) {
		this.confirm_time = confirm_time;
	}

	public int getBfx_currency_id() {
		return bfx_currency_id;
	}

	public void setBfx_currency_id(int bfx_currency_id) {
		this.bfx_currency_id = bfx_currency_id;
	}
	
}
