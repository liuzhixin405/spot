package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_apply")
public class BQMemberApply implements Serializable {

	private static final long serialVersionUID = 6573905673967132576L;
	
	private long f_user_id;
	private String idcard;
	private String mobile;
	private String address;
	private long dateline;
	private int state;
	private long confirm_dateline;
	private int user_type;
	private String reason;

	public BQMemberApply() {

	}

	public BQMemberApply(long fUserId, String idcard, String mobile,
			String address, long dateline, int state, long confirmDateline,
			int userType, String reason) {
		f_user_id = fUserId;
		this.idcard = idcard;
		this.mobile = mobile;
		this.address = address;
		this.dateline = dateline;
		this.state = state;
		confirm_dateline = confirmDateline;
		user_type = userType;
		this.reason = reason;
	}

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getConfirm_dateline() {
		return confirm_dateline;
	}

	public void setConfirm_dateline(long confirmDateline) {
		confirm_dateline = confirmDateline;
	}

	public int getUser_type() {
		return user_type;
	}

	public void setUser_type(int userType) {
		user_type = userType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
