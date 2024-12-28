package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户自选市场
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_member_favorite_market")
public class BQMemberFavoriteMarket implements Serializable {

	private static final long serialVersionUID = -350776540005914522L;

	@Id
	@GeneratedValue
	private long id;
	private long f_user_id;// 用户ID
	private String codes;// 合约号(逗号隔开)
	private int type;// 市场类型：1、正式盘，2、模拟盘，3、币币交易，4、交割合约
	private String part;

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

	public String getCodes() {
		return codes;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}
}
