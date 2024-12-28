package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户交易白名单
 * 
 */
@Entity
@Table(name = "bq_user_trade_white")
public class BQUserTradeWhite implements Serializable {
	
	private static final long serialVersionUID = 7499747607638615048L;
	
	private long f_user_id;
	private long add_time;

	@Id
	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		this.f_user_id = fUserId;
	}

	public long getAdd_time() {
		return add_time;
	}

	public void setAdd_time(long add_time) {
		this.add_time = add_time;
	}

}
