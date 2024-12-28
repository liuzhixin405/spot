package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户api交易设置
 * 
 */
@Entity
@Table(name = "bq_user_api_setting")
public class BQUserApiSetting implements Serializable {

	private static final long serialVersionUID = 8863258314673895094L;

	private long id;
	private long delay_time;
	private boolean is_open;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDelay_time() {
		return delay_time;
	}

	public void setDelay_time(long delay_time) {
		this.delay_time = delay_time;
	}

	public boolean getIs_open() {
		return is_open;
	}

	public void setIs_open(boolean is_open) {
		this.is_open = is_open;
	}

}
