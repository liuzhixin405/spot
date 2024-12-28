package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_bcex_member")
public class BQBcexMember implements Serializable {

	private static final long serialVersionUID = -5982436982524795234L;
	
	private String f_user_email;// 用户邮箱,
	private String f_user_pwd;// 用户密码
	private String f_user_tel;// 用户手机,
	private String f_user_paypwd;// 用户资金密码

	public BQBcexMember() {
	}

	@Id
	public String getF_user_email() {
		return f_user_email;
	}

	public void setF_user_email(String fUserEmail) {
		f_user_email = fUserEmail;
	}

	public String getF_user_pwd() {
		return f_user_pwd;
	}

	public void setF_user_pwd(String f_user_pwd) {
		this.f_user_pwd = f_user_pwd;
	}

	public String getF_user_tel() {
		return f_user_tel;
	}

	public void setF_user_tel(String f_user_tel) {
		this.f_user_tel = f_user_tel;
	}

	public String getF_user_paypwd() {
		return f_user_paypwd;
	}

	public void setF_user_paypwd(String f_user_paypwd) {
		this.f_user_paypwd = f_user_paypwd;
	}

}
