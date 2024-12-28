package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * API接口AppKey
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_user_app_key")
public class BQUserAppKey implements Serializable {
	
	private static final long serialVersionUID = -850335529658159152L;
	
	private long f_user_id;
	private String app_key;
	private String app_secret;
	private String allow_ip;
	private long update_time;
	private long over_time;
	private int state;

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		this.f_user_id = fUserId;
	}

	public String getApp_key() {
		return app_key;
	}

	public void setApp_key(String app_key) {
		this.app_key = app_key;
	}

	public String getApp_secret() {
		return app_secret;
	}

	public void setApp_secret(String app_secret) {
		this.app_secret = app_secret;
	}

	public String getAllow_ip() {
		return allow_ip;
	}

	public void setAllow_ip(String allow_ip) {
		this.allow_ip = allow_ip;
	}

	public long getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(long update_time) {
		this.update_time = update_time;
	}

	public long getOver_time() {
		return over_time;
	}

	public void setOver_time(long over_time) {
		this.over_time = over_time;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
