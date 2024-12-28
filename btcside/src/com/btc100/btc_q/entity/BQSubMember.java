package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_sub_member")
public class BQSubMember implements Serializable {

	private static final long serialVersionUID = 3040149865728179062L;

	private long id;
	private long sub_user_id;// 子账号id
	private long mother_user_id;// 母账户用户id
	private long dateline;// 时间

	public BQSubMember() {

	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSub_user_id() {
		return sub_user_id;
	}

	public void setSub_user_id(long sub_user_id) {
		this.sub_user_id = sub_user_id;
	}

	public long getMother_user_id() {
		return mother_user_id;
	}

	public void setMother_user_id(long mother_user_id) {
		this.mother_user_id = mother_user_id;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

}
