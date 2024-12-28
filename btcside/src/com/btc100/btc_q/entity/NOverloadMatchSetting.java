package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "n_overload_match_setting")
public class NOverloadMatchSetting implements Serializable {

	private static final long serialVersionUID = 5844284358855989965L;
	
	private Long id;
	private String f_code;
	private long f_user_id;
	private boolean running;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getF_code() {
		return f_code;
	}
	
	public void setF_code(String f_code) {
		this.f_code = f_code;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public boolean getRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
}
