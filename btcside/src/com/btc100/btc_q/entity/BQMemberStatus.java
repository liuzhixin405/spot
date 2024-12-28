package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_status")
public class BQMemberStatus implements Serializable {

	private static final long serialVersionUID = 2002367927863873159L;	
	private long f_user_id;//'用户ID'
	private String reg_ip;//用户注册ip'
	private int last_time;//最后登录时间,unix时间格式'
	private String last_ip;// '基本后登录ip',
	private BigDecimal last_comein;//status
	public BQMemberStatus(){}
	@Id
	public long getF_user_id() {
		return f_user_id;
	}
	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}
	public String getReg_ip() {
		return reg_ip;
	}
	public void setReg_ip(String regIp) {
		reg_ip = regIp;
	}
	public int getLast_time() {
		return last_time;
	}
	public void setLast_time(int lastTime) {
		last_time = lastTime;
	}
	public String getLast_ip() {
		return last_ip;
	}
	public void setLast_ip(String lastIp) {
		last_ip = lastIp;
	}
	public BigDecimal getLast_comein() {
		return last_comein;
	}
	public void setLast_comein(BigDecimal lastComein) {
		last_comein = lastComein;
	}


}
