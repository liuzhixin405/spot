package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_invite_amount")
public class BQMemberInviteAmount implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5956845683354707352L;

	public BQMemberInviteAmount() {

	}

	public BQMemberInviteAmount(long fUserId, int fDayClick, int fDaySuccess, String fDate) {
		f_user_id = fUserId;
		f_day_click = fDayClick;
		f_day_success = fDaySuccess;
		f_date = fDate;
	}

	private long f_user_id;

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long value) {
		f_user_id = value;
	}

	private int f_day_click;

	public int getF_day_click() {
		return f_day_click;
	}

	public void setF_day_click(int value) {
		f_day_click = value;
	}

	private int f_day_success;

	public int getF_day_success() {
		return f_day_success;
	}

	public void setF_day_success(int value) {
		f_day_success = value;
	}

	private String f_date;

	@Id
	public String getF_date() {
		return f_date;
	}

	public void setF_date(String value) {
		f_date = value;
	}

}
