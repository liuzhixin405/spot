package com.btc100.btc_q.entity;
import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/*
 *  f_auth_id	自增长ID
 *	f_user_id	用户ID
 *	f_user_tel	用户手机号
 *	f_auth_code	验证码
 */
public class BQTelCode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int f_auth_id;
	private long f_user_id;
	private String f_user_tel;
	private String f_auth_code;
	private String f_comment1;
	private int f_error_times = 0;
	
	public BQTelCode() {
	
	}
	@Id
	@GeneratedValue
	public int getF_auth_id() {
		return f_auth_id;
	}

	public void setF_auth_id(int f_auth_id) {
		this.f_auth_id = f_auth_id;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public String getF_user_tel() {
		return f_user_tel;
	}

	public void setF_user_tel(String f_user_tel) {
		this.f_user_tel = f_user_tel;
	}

	public String getF_auth_code() {
		return f_auth_code;
	}

	public void setF_auth_code(String f_auth_code) {
		this.f_auth_code = f_auth_code;
	}

	public String getF_comment1() {
		return f_comment1;
	}

	public void setF_comment1(String f_comment1) {
		this.f_comment1 = f_comment1;
	}
	
	public int getF_error_times() {
		return f_error_times;
	}
	public void setF_error_times(int f_error_times) {
		this.f_error_times = f_error_times;
	}
}
