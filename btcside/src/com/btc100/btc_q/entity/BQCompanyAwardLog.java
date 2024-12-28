package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_company_award_log")
public class BQCompanyAwardLog implements Serializable {

	private static final long serialVersionUID = 2403116122699230729L;
	private long log_id;
	private long f_user_id; // 用户ID
	private String title; // 标题(奖励原因)
	private BigDecimal commission; //佣金
	private BigDecimal commission_rate; // 佣金比例
	private BigDecimal award_amount; // 奖励金额(佣金*佣金比例)
	private long dateline; // 奖励日期,unix时间格式
	private long uid; // 下线用户uid
	private String email; // 下线用户邮箱
	private long trade_id; // 交易ID(trade_log:trade_id), 这里指合约号码1411，1412。。。

	public BQCompanyAwardLog() {

	}
	
	public BQCompanyAwardLog(long fUserId, String title, BigDecimal commission,
			BigDecimal commissionRate, BigDecimal awardAmount, long dateline, long uid,
			String email, int tradeId) {
		f_user_id = fUserId;
		this.title = title;
		this.commission = commission;
		commission_rate = commissionRate;
		award_amount = awardAmount;
		this.dateline = dateline;
		this.uid = uid;
		this.email = email;
		trade_id = tradeId;
	}

	@Id
	@GeneratedValue
	public long getLog_id() {
		return log_id;
	}

	public void setLog_id(long value) {
		log_id = value;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long value) {
		f_user_id = value;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String value) {
		title = value;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal value) {
		commission = value;
	}

	public BigDecimal getCommission_rate() {
		return commission_rate;
	}

	public void setCommission_rate(BigDecimal value) {
		commission_rate = value;
	}

	public BigDecimal getAward_amount() {
		return award_amount;
	}

	public void setAward_amount(BigDecimal value) {
		award_amount = value;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long value) {
		dateline = value;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long value) {
		uid = value;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String value) {
		email = value;
	}

	public long getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(long value) {
		trade_id = value;
	}

}
