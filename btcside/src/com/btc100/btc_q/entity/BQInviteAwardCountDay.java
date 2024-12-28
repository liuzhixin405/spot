package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 记录经纪人奖励日统计汇总
 * 
 * @author Administrator
 * 
 */
public class BQInviteAwardCountDay implements Serializable {

	private static final long serialVersionUID = -7556533058467295390L;

	private long log_id;
	private long f_user_id;// 用户ID
	private BigDecimal amount;// 当日奖励汇总金额
	private Date day;// 当天日期(yyyy-mm-dd)

	public BQInviteAwardCountDay() {
	}

	@Id
	@GeneratedValue
	public long getLog_id() {
		return log_id;
	}

	public void setLog_id(long logId) {
		log_id = logId;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}
}
