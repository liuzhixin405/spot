package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_invite_count")
public class BQMemberInviteCount implements Serializable {

	private static final long serialVersionUID = 1L;
	private long f_user_id;// '用户ID',
	private int day_count;// '单日邀请数量',
	private int week_count;// '周累计',
	private int month_count;// '月累计',
	private int total_count;// '总数量',
	private int day;// 当天时间，unix时间格式，只保存yyyy-mm-dd这部分的信息

	public BQMemberInviteCount() {
	}

	public BQMemberInviteCount(long fUserId, int dayCount, int weekCount, int monthCount, int totalCount, int day) {
		super();
		f_user_id = fUserId;
		day_count = dayCount;
		week_count = weekCount;
		month_count = monthCount;
		total_count = totalCount;
		this.day = day;
	}


	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	@Id
	public int getDay_count() {
		return day_count;
	}

	public void setDay_count(int dayCount) {
		day_count = dayCount;
	}

	@Id
	public int getWeek_count() {
		return week_count;
	}

	public void setWeek_count(int weekCount) {
		week_count = weekCount;
	}

	@Id
	public int getMonth_count() {
		return month_count;
	}

	public void setMonth_count(int monthCount) {
		month_count = monthCount;
	}

	@Id
	public int getTotal_count() {
		return total_count;
	}

	public void setTotal_count(int totalCount) {
		total_count = totalCount;
	}

	@Id
	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	//---------------------------无主键
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	

}
