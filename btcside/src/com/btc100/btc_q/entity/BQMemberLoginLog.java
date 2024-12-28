package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_login_log")
public class BQMemberLoginLog implements Serializable {

	private static final long serialVersionUID = -5740267146985234015L;

	private long id;
	private long f_user_id;// '用户ID'
	private long login_time;// 登录时间
	private String ip;// '登录IP',
	private String note;// 备注

	public BQMemberLoginLog() {
	}
	
	public BQMemberLoginLog(long userId, long time, String ip, String note) {
		this.f_user_id = userId;
		this.login_time = time;
		this.ip = ip;
		this.note = note;
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

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public long getLogin_time() {
		return login_time;
	}

	public void setLogin_time(long login_time) {
		this.login_time = login_time;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
