package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户所获得荣誉证书
 * @author win7
 *
 */
@Entity
@Table(name = "bq_member_certificate")
public class BQMemberCertificate implements Serializable {

	private static final long serialVersionUID = 1869778649364658826L;
	
	private long id;
	private long user_id; // 用户id
	private long certificate_id; // 证书类型id
	private String certificate_name; // 荣誉称号
	private String activity_name; // 活动名称
	private long issue_time; // 颁发时间
	
	public BQMemberCertificate() {
	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getCertificate_id() {
		return certificate_id;
	}

	public void setCertificate_id(long certificate_id) {
		this.certificate_id = certificate_id;
	}

	public String getCertificate_name() {
		return certificate_name;
	}

	public void setCertificate_name(String certificate_name) {
		this.certificate_name = certificate_name;
	}

	public String getActivity_name() {
		return activity_name;
	}

	public void setActivity_name(String activity_name) {
		this.activity_name = activity_name;
	}

	public long getIssue_time() {
		return issue_time;
	}

	public void setIssue_time(long issue_time) {
		this.issue_time = issue_time;
	}

}
