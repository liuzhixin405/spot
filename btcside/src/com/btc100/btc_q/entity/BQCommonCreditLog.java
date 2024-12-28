package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 积分日志表(网站可以手工添加积分/用户自行购买积分/进行一些资料完善奖励/交易奖励/其它奖励)
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_common_credit_log")
public class BQCommonCreditLog implements Serializable {

	private static final long serialVersionUID = 3685000946192490690L;

	private long credit_log_id;
	private long f_user_id; // 用户ID
	private int credit_type_id; // 积分类别(后台分析统计)
	private long credits;//'积分数量',
	private String title; // 积分标题
	private long dateline; // 积分时间(Unix格式，排序快)

	public BQCommonCreditLog() {
	}

	@Id
	@GeneratedValue
	public long getCredit_log_id() {
		return credit_log_id;
	}

	public void setCredit_log_id(long creditLogId) {
		credit_log_id = creditLogId;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public int getCredit_type_id() {
		return credit_type_id;
	}

	public void setCredit_type_id(int creditTypeId) {
		credit_type_id = creditTypeId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public long getCredits() {
		return credits;
	}

	public void setCredits(long credits) {
		this.credits = credits;
	}
	
}
