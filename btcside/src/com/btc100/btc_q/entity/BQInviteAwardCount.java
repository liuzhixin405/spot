package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 记录经纪人奖励总统计汇总
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_invite_award_count")
public class BQInviteAwardCount implements Serializable {

	private static final long serialVersionUID = 4861700605611333646L;

	private long f_user_id; // 用户ID
	private BigDecimal day_count;// 单日奖励总金额
	private BigDecimal week_count;// 当周奖励总金额
	private BigDecimal month_count;// 当月奖励总金额
	private BigDecimal total_count;// 账户奖励总金额
	private long day;// 当天时间，unix时间格式，只保存yyyy-mm-dd这部分的信息

	public BQInviteAwardCount() {
	}

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public BigDecimal getDay_count() {
		return day_count;
	}

	public void setDay_count(BigDecimal dayCount) {
		day_count = dayCount;
	}

	public BigDecimal getWeek_count() {
		return week_count;
	}

	public void setWeek_count(BigDecimal weekCount) {
		week_count = weekCount;
	}

	public BigDecimal getMonth_count() {
		return month_count;
	}

	public void setMonth_count(BigDecimal monthCount) {
		month_count = monthCount;
	}

	public BigDecimal getTotal_count() {
		return total_count;
	}

	public void setTotal_count(BigDecimal totalCount) {
		total_count = totalCount;
	}

	public long getDay() {
		return day;
	}

	public void setDay(long day) {
		this.day = day;
	}
}
