package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_member_level_buy_rebate_reconcilia")
public class BQMemberLevelBuyRebateReconcilia implements Serializable {
	
	private static final long serialVersionUID = -2648314886456519177L;
	
	private long id;
	private long f_user_id;
	private String f_user_email;// 用户名
	private BigDecimal total_buy_amount;
	private BigDecimal total_bfx_lock;
	private BigDecimal total_bfx_unlock;
	private BigDecimal self_bfx_lock;
	private long superior = 0;
	private long superior2 = 0;
	
	@Transient
	private String vip_name = "";
	

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

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public String getF_user_email() {
		return f_user_email;
	}

	public void setF_user_email(String f_user_email) {
		this.f_user_email = f_user_email;
	}

	public BigDecimal getTotal_bfx_lock() {
		return total_bfx_lock;
	}

	public void setTotal_bfx_lock(BigDecimal total_bfx_lock) {
		this.total_bfx_lock = total_bfx_lock;
	}

	public BigDecimal getTotal_bfx_unlock() {
		return total_bfx_unlock;
	}

	public void setTotal_bfx_unlock(BigDecimal total_bfx_unlock) {
		this.total_bfx_unlock = total_bfx_unlock;
	}

	public BigDecimal getSelf_bfx_lock() {
		return self_bfx_lock;
	}

	public void setSelf_bfx_lock(BigDecimal self_bfx_lock) {
		this.self_bfx_lock = self_bfx_lock;
	}

	public long getSuperior() {
		return superior;
	}

	public void setSuperior(long superior) {
		this.superior = superior;
	}

	public long getSuperior2() {
		return superior2;
	}

	public void setSuperior2(long superior2) {
		this.superior2 = superior2;
	}

	@Transient
	public String getVip_name() {
		return vip_name;
	}

	public void setVip_name(String vip_name) {
		this.vip_name = vip_name;
	}

	public BigDecimal getTotal_buy_amount() {
		return total_buy_amount;
	}

	public void setTotal_buy_amount(BigDecimal total_buy_amount) {
		this.total_buy_amount = total_buy_amount;
	}

}
