package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "view_broker_invite_details")
public class VIEWBrokerInviteDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private long f_user_id;
	private int f_day_click;
	private int f_day_success;
	private BigDecimal day_count;

	public VIEWBrokerInviteDetails() {

	}

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long value) {
		f_user_id = value;
	}

	public int getF_day_click() {
		return f_day_click;
	}

	public void setF_day_click(int fDayClick) {
		f_day_click = fDayClick;
	}

	public int getF_day_success() {
		return f_day_success;
	}

	public void setF_day_success(int fDaySuccess) {
		f_day_success = fDaySuccess;
	}

	public BigDecimal getDay_count() {
		return day_count;
	}

	public void setDay_count(BigDecimal dayCount) {
		day_count = dayCount;
	}

}
