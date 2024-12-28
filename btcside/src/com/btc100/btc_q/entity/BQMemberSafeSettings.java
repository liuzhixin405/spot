package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_safe_settings")
public class BQMemberSafeSettings implements Serializable {

	private static final long serialVersionUID = 2002367927863873159L;
	
	
	private long f_user_id;//'用户ID'
	private int f_user_safetype;//用户安全类型(1短信验证，2google验证，3双重验证)'
	private String f_user_zone = "";//用户手机区号
	private String f_user_tel;//'用户电话(手机/固话/海外电话),可能手机号认证',
	private String f_user_safegoogle;//只有f_user_safetype值为2,3时才有效',
	private int f_user_safequertion;// '用户密保(用密保问题的ID(1|3|4)),以json格式存储',
	private String f_user_safeanswer;//'用户密保答案(用密保问题的答案(aa|ce|hh)),以json格式存储'
	public BQMemberSafeSettings(){}
	@Id
	public long getF_user_id() {
		return f_user_id;
	}
	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}
	public int getF_user_safetype() {
		return f_user_safetype;
	}
	public void setF_user_safetype(int fUserSafetype) {
		f_user_safetype = fUserSafetype;
	}

	public String getF_user_zone() {
		return f_user_zone;
	}

	public void setF_user_zone(String f_user_zone) {
		this.f_user_zone = f_user_zone;
	}

	public String getF_user_tel() {
		return f_user_tel;
	}
	public void setF_user_tel(String fUserTel) {
		f_user_tel = fUserTel;
	}
	public String getF_user_safegoogle() {
		return f_user_safegoogle;
	}
	public void setF_user_safegoogle(String fUserSafegoogle) {
		f_user_safegoogle = fUserSafegoogle;
	}
	public int getF_user_safequertion() {
		return f_user_safequertion;
	}
	public void setF_user_safequertion(int fUserSafequertion) {
		f_user_safequertion = fUserSafequertion;
	}
	public String getF_user_safeanswer() {
		return f_user_safeanswer;
	}
	public void setF_user_safeanswer(String fUserSafeanswer) {
		f_user_safeanswer = fUserSafeanswer;
	}
	

}
