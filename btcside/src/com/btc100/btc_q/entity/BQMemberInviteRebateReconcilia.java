package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 返利统计表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_member_invite_rebate_reconcilia")
public class BQMemberInviteRebateReconcilia implements Serializable {

	private static final long serialVersionUID = 1190171056737317343L;

	private long id;
	private long f_user_id;// 用户ID
	private String f_user_email; // 用户邮箱
	private BigDecimal rebate;// 返利额
	private long cut_off_time;// 截止时间,unix时间格式

	public BQMemberInviteRebateReconcilia() {

	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public String getF_user_email() {
		return f_user_email;
	}

	public void setF_user_email(String f_user_email) {
		this.f_user_email = f_user_email;
	}

	public BigDecimal getRebate() {
		return rebate;
	}

	public void setRebate(BigDecimal rebate) {
		this.rebate = rebate;
	}

	public long getCut_off_time() {
		return cut_off_time;
	}

	public void setCut_off_time(long cut_off_time) {
		this.cut_off_time = cut_off_time;
	}

}
