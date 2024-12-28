package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_credit_count")
public class BQMemberCreditCount implements Serializable {

	private static final long serialVersionUID = 2002367927863873159L;
	
	
	 private long id;
	 private long f_user_id;//'用户ID',
	 private int credit_type_id;//'积分类型',
	 private int credit_count;//'积分次数(每积分一次+1),不包含扣除',
	 private int credits;//'总积分数量',
	
	public long getF_user_id() {
		return f_user_id;
	}
	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getCredit_type_id() {
		return credit_type_id;
	}
	public void setCredit_type_id(int creditTypeId) {
		credit_type_id = creditTypeId;
	}
	public int getCredit_count() {
		return credit_count;
	}
	public void setCredit_count(int creditCount) {
		credit_count = creditCount;
	}
	public int getCredits() {
		return credits;
	}
	public void setCredits(int credits) {
		this.credits = credits;
	}
	

}
