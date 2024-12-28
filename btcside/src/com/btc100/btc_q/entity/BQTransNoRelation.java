package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "bq_trans_no_relation")
public class BQTransNoRelation implements Serializable {

	private static final long serialVersionUID = -1059463928500559606L;

	private String trans_no;//交易流水号（20位数字）
	private int trans_type;//交易类型
	private long record_id;//交易类型分表的主键 record_id
	public BQTransNoRelation() {

	}
	@Id
	public String getTrans_no() {
		return trans_no;
	}
	public void setTrans_no(String transNo) {
		trans_no = transNo;
	}
	public int getTrans_type() {
		return trans_type;
	}
	public void setTrans_type(int transType) {
		trans_type = transType;
	}
	public long getRecord_id() {
		return record_id;
	}
	public void setRecord_id(long recordId) {
		record_id = recordId;
	}		
}
