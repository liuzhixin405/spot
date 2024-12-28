package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * 会员交易记录关系表(多对多), 一个交易，需要将进行这个交易的双方与交易的对应关系都写入此表一份
 * 
 */
@Entity
@Table(name = "member_trade")// TODO:怎么对应多个不同的表
@Immutable
public class MemberTrade implements Serializable {

	private static final long serialVersionUID = 5859207181171225932L;

	private long id;// 会员与交易对应关系ID
	private long f_user_id;// 用户ID
	private long trade_id;// 交易ID
	private long dateline;// 交易时间

	public MemberTrade() {
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

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public long getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(long tradeId) {
		trade_id = tradeId;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

}
