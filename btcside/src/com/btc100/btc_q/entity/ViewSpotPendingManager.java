package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ViewSpotPendingManager implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	private long delegation_id;// 委托编号
	private long delegation_time;// 委托时间,unix时间格式
	private String coin_pair_name;// 合约号
	private int direction;// 操作方向
	private BigDecimal delegation_count;// 委托数量
	private BigDecimal delegation_price;// 委托价格
	private BigDecimal deal_count;// 已成交数量
	private BigDecimal undeal_count;// 未成交数量
	private long f_user_id;// 用户ID
	private String f_user_email;

	public long getDelegation_id() {
		return delegation_id;
	}

	public void setDelegation_id(long delegationId) {
		delegation_id = delegationId;
	}

	public long getDelegation_time() {
		return delegation_time;
	}

	public void setDelegation_time(long delegationTime) {
		delegation_time = delegationTime;
	}

	public String getCoin_pair_name() {
		return coin_pair_name;
	}

	public void setCoin_pair_name(String coin_pair_name) {
		this.coin_pair_name = coin_pair_name;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public BigDecimal getDelegation_count() {
		return delegation_count;
	}

	public void setDelegation_count(BigDecimal delegationCount) {
		delegation_count = delegationCount;
	}

	public BigDecimal getDelegation_price() {
		return delegation_price;
	}

	public void setDelegation_price(BigDecimal delegationPrice) {
		delegation_price = delegationPrice;
	}

	public BigDecimal getDeal_count() {
		return deal_count;
	}

	public void setDeal_count(BigDecimal dealCount) {
		deal_count = dealCount;
	}

	public BigDecimal getUndeal_count() {
		return undeal_count;
	}

	public void setUndeal_count(BigDecimal undealCount) {
		undeal_count = undealCount;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long fUserId) {
		f_user_id = fUserId;
	}

	public String getF_user_email() {
		return f_user_email;
	}

	public void setF_user_email(String fUserEmail) {
		f_user_email = fUserEmail;
	}

}
