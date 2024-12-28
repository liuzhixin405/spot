package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_sub_member_info")
public class BQSubMemberInfo implements Serializable {

	private static final long serialVersionUID = 8175056144764994654L;

	private long f_user_id;// 子账号id
	private String f_user_email;// 子账号登录名
	private String unencrypted_password;// 子账号密码（未加密）
	private String app_key;
	private String app_secret;
	private String allow_ip;
	private long dateline;// 时间

	public BQSubMemberInfo() {

	}

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public String getF_user_email() {
		return f_user_email;
	}

	public void setF_user_email(String f_user_email) {
		this.f_user_email = f_user_email;
	}

	public String getUnencrypted_password() {
		return unencrypted_password;
	}

	public void setUnencrypted_password(String unencrypted_password) {
		this.unencrypted_password = unencrypted_password;
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

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

}
