package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 现货交易（币币交易）委托挂单表（非全部成交）
 * 
 * @author Administrator
 * 
 */
@Entity
public class SpotPendingDelegation implements Serializable {

	private static final long serialVersionUID = -6477088177725406479L;
	
	private long delegation_id;// 委托编号
	private long delegation_time;// 委托时间,unix时间格式
	private long pending_no;//挂单流水号
	private long coin_pair_id;// 交易对id
	private String coin_pair_name;// 交易对名称
	private int direction;// 操作方向
	private BigDecimal delegation_count;// 委托数量
	private BigDecimal delegation_price;// 委托价格
	private BigDecimal deal_count;// 已成交数量
	private BigDecimal undeal_count;// 未成交数量
	private BigDecimal commission_rate;//手续费率
	private long f_user_id;// 用户ID
	private int flag=0;//0默认，1交易中，2撤单中（其他）
	private BigDecimal f_frozenremain = BigDecimal.ZERO;//冻结备用金
	
	public SpotPendingDelegation() {
	}

	@Id
	public long getDelegation_id() {
		return delegation_id;
	}

	public void setDelegation_id(long delegationId) {
		delegation_id = delegationId;
	}

	public long getDelegation_time() {
		return delegation_time;
	}

	public void setDelegation_time(long delegation_time) {
		this.delegation_time = delegation_time;
	}

	public long getPending_no() {
		return pending_no;
	}

	public void setPending_no(long pending_no) {
		this.pending_no = pending_no;
	}

	public long getCoin_pair_id() {
		return coin_pair_id;
	}

	public void setCoin_pair_id(long coin_pair_id) {
		this.coin_pair_id = coin_pair_id;
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

	public void setDelegation_count(BigDecimal delegation_count) {
		this.delegation_count = delegation_count;
	}

	public BigDecimal getDelegation_price() {
		return delegation_price;
	}

	public void setDelegation_price(BigDecimal delegation_price) {
		this.delegation_price = delegation_price;
	}

	public BigDecimal getDeal_count() {
		return deal_count;
	}

	public void setDeal_count(BigDecimal deal_count) {
		this.deal_count = deal_count;
	}

	public BigDecimal getUndeal_count() {
		return undeal_count;
	}

	public void setUndeal_count(BigDecimal undeal_count) {
		this.undeal_count = undeal_count;
	}

	public BigDecimal getCommission_rate() {
		return commission_rate;
	}

	public void setCommission_rate(BigDecimal commission_rate) {
		this.commission_rate = commission_rate;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public BigDecimal getF_frozenremain() {
		return f_frozenremain;
	}

	public void setF_frozenremain(BigDecimal f_frozenremain) {
		this.f_frozenremain = f_frozenremain;
	}

}
