package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_payword")
public class BQMemberPayword implements Serializable {

	private static final long serialVersionUID = 2002367927863873159L;	
	private long f_user_id;//'用户ID'
	private String pwd;//支付密码'
	private String salt;//加密salt'
	private int state;//'1:启用 0:不启用',
	private int f_readszms; //是否阅读过上证合约的信息 1是 0 否
	private int failed_times = 0;
	
	public BQMemberPayword(){}
	
	
	public BQMemberPayword(long fUserId, String pwd, String salt, int state, int fReadszms) {
		f_user_id = fUserId;
		this.pwd = pwd;
		this.salt = salt;
		this.state = state;
		f_readszms = fReadszms;
	}


	@Id
	public long getF_user_id() {
		return f_user_id;
	}
	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getF_readszms() {
		return f_readszms;
	}
	public void setF_readszms(int fReadszms) {
		f_readszms = fReadszms;
	}


	public int getFailed_times() {
		return failed_times;
	}


	public void setFailed_times(int failed_times) {
		this.failed_times = failed_times;
	}
	
	
}
