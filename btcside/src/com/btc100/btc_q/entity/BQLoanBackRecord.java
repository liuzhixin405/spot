package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_loan_back_record")
public class BQLoanBackRecord implements Serializable {

	private static final long serialVersionUID = -4719597295292472013L;
	
	private long id;
	private long f_user_id;						// 用户Id
	private long loan_record_id;				// 借款订单Id
	private int pledge_coin_id;					// 抵押币种id
	private String pledge_symbol;				// 抵押币种名称
	private int borrow_coin_id;					// 借出币种Id
	private String borrow_symbol;				// 借出币种名称
	private long borrow_uid;					// 借出方用户Id
	private BigDecimal borrow_amount;			// 借出数量
	private BigDecimal pledge_amount;			// 抵押数量
	private BigDecimal day_rate;				// 利率（日）
	private BigDecimal fee_rate;				// 手续费率
	private BigDecimal back_all_amount;			// 归还数量
	private BigDecimal back_borrow_amount;		// 归还的本金数量
	private BigDecimal back_pledge_amount;		// 释放的抵押数量
	private BigDecimal interest;				// 利息
	private BigDecimal fee;						// 手续费
	private long back_time; 					// 还款时间
	private int type = 0;						// 平仓类型（0-清偿，1-失败）
	private String note = "";					// 备注
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long value) {
		id = value;
	}
	
	public long getF_user_id() {
		return f_user_id;
	}
	
	public void setF_user_id(long uid) {
		f_user_id = uid;
	}

	public long getLoan_record_id() {
		return loan_record_id;
	}

	public void setLoan_record_id(long loan_record_id) {
		this.loan_record_id = loan_record_id;
	}

	public int getPledge_coin_id() {
		return pledge_coin_id;
	}

	public void setPledge_coin_id(int pledge_coin_id) {
		this.pledge_coin_id = pledge_coin_id;
	}

	public String getPledge_symbol() {
		return pledge_symbol;
	}

	public void setPledge_symbol(String pledge_symbol) {
		this.pledge_symbol = pledge_symbol;
	}

	public int getBorrow_coin_id() {
		return borrow_coin_id;
	}

	public void setBorrow_coin_id(int borrow_coin_id) {
		this.borrow_coin_id = borrow_coin_id;
	}

	public String getBorrow_symbol() {
		return borrow_symbol;
	}

	public void setBorrow_symbol(String borrow_symbol) {
		this.borrow_symbol = borrow_symbol;
	}

	public long getBorrow_uid() {
		return borrow_uid;
	}

	public void setBorrow_uid(long borrow_uid) {
		this.borrow_uid = borrow_uid;
	}

	public BigDecimal getBorrow_amount() {
		return borrow_amount;
	}

	public void setBorrow_amount(BigDecimal borrow_amount) {
		this.borrow_amount = borrow_amount;
	}

	public BigDecimal getPledge_amount() {
		return pledge_amount;
	}

	public void setPledge_amount(BigDecimal pledge_amount) {
		this.pledge_amount = pledge_amount;
	}

	public BigDecimal getBack_all_amount() {
		return back_all_amount;
	}

	public void setBack_all_amount(BigDecimal back_all_amount) {
		this.back_all_amount = back_all_amount;
	}

	public BigDecimal getBack_borrow_amount() {
		return back_borrow_amount;
	}

	public void setBack_borrow_amount(BigDecimal back_borrow_amount) {
		this.back_borrow_amount = back_borrow_amount;
	}

	public BigDecimal getBack_pledge_amount() {
		return back_pledge_amount;
	}

	public void setBack_pledge_amount(BigDecimal back_pledge_amount) {
		this.back_pledge_amount = back_pledge_amount;
	}

	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public long getBack_time() {
		return back_time;
	}

	public void setBack_time(long back_time) {
		this.back_time = back_time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getDay_rate() {
		return day_rate;
	}

	public void setDay_rate(BigDecimal day_rate) {
		this.day_rate = day_rate;
	}

	public BigDecimal getFee_rate() {
		return fee_rate;
	}

	public void setFee_rate(BigDecimal fee_rate) {
		this.fee_rate = fee_rate;
	}

}
