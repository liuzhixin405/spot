package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户账户金额表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_account")
public class BQAccount implements Serializable {

	private static final long serialVersionUID = -2382053720715581857L;
	
	private long id;
	private long f_user_id;
	private int currency_type_id;
	private String f_user_email;// 用户名
	private BigDecimal btc; // 'btc金额',
	private BigDecimal btc_locked;// '锁定金额',
	private long lastupdate; // 最后金额变更,unix时间格式(用户活跃分析)',

	public BQAccount() {
	}

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
	
	public int getCurrency_type_id() {
		return currency_type_id;
	}
	
	public void setCurrency_type_id(int typeId) {
		currency_type_id = typeId;
	}

	public String getF_user_email() {
		return f_user_email;
	}

	public void setF_user_email(String fUserName) {
		f_user_email = fUserName;
	}

	public BigDecimal getBtc() {
		return btc;
	}

	public void setBtc(BigDecimal btc) {
		this.btc = btc;
	}

	public BigDecimal getBtc_locked() {
		return btc_locked;
	}

	public void setBtc_locked(BigDecimal btcLocked) {
		btc_locked = btcLocked;
	}

	public long getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(long lastupdate) {
		this.lastupdate = lastupdate;
	}

}
