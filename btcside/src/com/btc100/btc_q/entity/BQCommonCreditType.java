package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 积分类别表,解决积分规则(一种可以多次添加积分，另一种是只允许添加一次积分)
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_common_credit_type")
public class BQCommonCreditType implements Serializable {

	private static final long serialVersionUID = 5728974908545472371L;

	private long credit_type_id;
	private String credit_type_name; // 积分类别名称
	private int credit_limit_count; // 最多奖励次数 (0:表示不限制 >0:同一个积分类型最多奖励次数）

	public BQCommonCreditType() {
	}

	@Id
	@GeneratedValue
	public long getCredit_type_id() {
		return credit_type_id;
	}

	public void setCredit_type_id(long creditTypeId) {
		credit_type_id = creditTypeId;
	}

	public String getCredit_type_name() {
		return credit_type_name;
	}

	public void setCredit_type_name(String creditTypeName) {
		credit_type_name = creditTypeName;
	}

	public int getCredit_limit_count() {
		return credit_limit_count;
	}

	public void setCredit_limit_count(int creditLimitCount) {
		credit_limit_count = creditLimitCount;
	}

}
