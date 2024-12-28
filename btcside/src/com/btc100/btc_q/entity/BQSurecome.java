package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_surecome")
public class BQSurecome implements Serializable {

	private static final long serialVersionUID = -4644637516949714152L;

	private int user_id;
	private String user_name; // 
	private String user_pwd; // 登录密码
	private String user_thpwd; // 安全密码
	private String user_role; // 权限
	private String user_coins;//币币权限
	private String user_salt; // 加盐
	private String user_lastip; // 上次登录IP
	private long user_last_time; // 上次成功登录时间
	private String google_no; // 谷歌验证secretKey
	private int pwd_count; // 密码错误次数
	private long pwd_error_time; // 密码错误最后一次时间
	private int google_count; // 谷歌验证错误次数
	private long google_error_time; // 谷歌验证错误最后一次时间

	public BQSurecome() {
	}

	@Id
	@GeneratedValue
	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int value) {
		user_id = value;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String value) {
		user_name = value;
	}

	public String getUser_pwd() {
		return user_pwd;
	}

	public void setUser_pwd(String value) {
		user_pwd = value;
	}

	public String getUser_thpwd() {
		return user_thpwd;
	}

	public void setUser_thpwd(String value) {
		user_thpwd = value;
	}

	public String getUser_role() {
		return user_role;
	}

	public void setUser_role(String value) {
		user_role = value;
	}

	public String getUser_coins() {
		return user_coins;
	}

	public void setUser_coins(String user_coins) {
		this.user_coins = user_coins;
	}

	public String getUser_salt() {
		return user_salt;
	}

	public void setUser_salt(String value) {
		user_salt = value;
	}

	public String getUser_lastip() {
		return user_lastip;
	}

	public void setUser_lastip(String value) {
		user_lastip = value;
	}

	public long getUser_last_time() {
		return user_last_time;
	}

	public void setUser_last_time(long user_last_time) {
		this.user_last_time = user_last_time;
	}

	public String getGoogle_no() {
		return google_no;
	}

	public void setGoogle_no(String google_no) {
		this.google_no = google_no;
	}

	public int getPwd_count() {
		return pwd_count;
	}

	public void setPwd_count(int pwd_count) {
		this.pwd_count = pwd_count;
	}

	public long getPwd_error_time() {
		return pwd_error_time;
	}

	public void setPwd_error_time(long pwd_error_time) {
		this.pwd_error_time = pwd_error_time;
	}

	public int getGoogle_count() {
		return google_count;
	}

	public void setGoogle_count(int google_count) {
		this.google_count = google_count;
	}

	public long getGoogle_error_time() {
		return google_error_time;
	}

	public void setGoogle_error_time(long google_error_time) {
		this.google_error_time = google_error_time;
	}

}
