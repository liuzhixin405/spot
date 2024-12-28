package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_level_buy_bfx_unlock_record")
public class BQLevelBuyBfxUnlockRecord implements Serializable {

	private static final long serialVersionUID = -3763568448291216566L;
	
	private long id;
	private long f_user_id;
	private int currency_type_id;
	private BigDecimal unlock_rate;
	private BigDecimal amount;
	private long record_time;
	private long level_buy_record_id;
	private BigDecimal total_lock;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public int getCurrency_type_id() {
		return this.currency_type_id;
	}
	
	public void setCurrency_type_id(int typeId) {
		this.currency_type_id = typeId;
	}

	public long getF_user_id() {
		return f_user_id;
	}
	
	public void setF_user_id(long uid) {
		this.f_user_id = uid;
	}
	
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public BigDecimal getUnlock_rate() {
		return unlock_rate;
	}
	
	public void setUnlock_rate(BigDecimal unlock_rate) {
		this.unlock_rate = unlock_rate;
	}
	
	public long getRecord_time() {
		return record_time;
	}
	
	public void setRecord_time(long time) {
		this.record_time = time;
	}

	public long getLevel_buy_record_id() {
		return level_buy_record_id;
	}

	public void setLevel_buy_record_id(long level_buy_record_id) {
		this.level_buy_record_id = level_buy_record_id;
	}

	public BigDecimal getTotal_lock() {
		return total_lock;
	}

	public void setTotal_lock(BigDecimal total_lock) {
		this.total_lock = total_lock;
	}

}
