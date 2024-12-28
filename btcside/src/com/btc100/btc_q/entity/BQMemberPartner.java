package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_partner")
public class BQMemberPartner implements Serializable {

	private static final long serialVersionUID = 1L;

	private long f_user_id;// '用户ID'
	private long dateline;// '开通api时间',
	private String partner;// '商户标识',
	private String secret_key;// '密钥',

	public BQMemberPartner() {
	}

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getSecret_key() {
		return secret_key;
	}

	public void setSecret_key(String secretKey) {
		secret_key = secretKey;
	}

}
