package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 记录经纪人奖励月统计汇总
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_invite_award_count_month")
public class BQInviteAwardCountMonth implements Serializable {

	private static final long serialVersionUID = -8773453123219610951L;

	private long log_id;
	private long f_user_id;// 用户ID
	private BigDecimal amount;// 当月奖励汇总金额
	private long month;// 月份(数字类型:201405)

	public BQInviteAwardCountMonth() {
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

	public long getMonth() {
		return month;
	}

	public void setMonth(long month) {
		this.month = month;
	}
}
