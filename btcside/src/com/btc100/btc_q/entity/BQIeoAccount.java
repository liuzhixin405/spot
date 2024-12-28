package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_ieo_account")
public class BQIeoAccount implements Serializable {

	private static final long serialVersionUID = -9062884674426209225L;
	
	private long id;
	private long f_user_id;
	private int currency_type_id;
	private BigDecimal initial;
	private BigDecimal locked;
	private BigDecimal unlocked;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getF_user_id() {
		return f_user_id;
	}
	
	public void setF_user_id(long uid) {
		this.f_user_id = uid;
	}

	public int getCurrency_type_id() {
		return currency_type_id;
	}
	
	public void setCurrency_type_id(int typeId) {
		currency_type_id = typeId;
	}
	
	public BigDecimal getInitial() {
		return initial;
	}
	
	public void setInitial(BigDecimal initial) {
		this.initial = initial;
	}
	
	public BigDecimal getLocked() {
		return locked;
	}
	
	public void setLocked(BigDecimal locked) {
		this.locked = locked;
	}
	
	public BigDecimal getUnlocked() {
		return unlocked;
	}
	
	public void setUnlocked(BigDecimal unlocked) {
		this.unlocked = unlocked;
	}
}
