package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_loan_adjust_record")
public class BQLoanAdjustRecord implements Serializable {

	private static final long serialVersionUID = -4719597295292472013L;
	
	private long id;
	private long f_user_id;						// 用户Id
	private long loan_record_id;				// 借款订单Id
	private int pledge_coin_id;					// 抵押币种id
	private String pledge_symbol;				// 抵押币种名称
	private int borrow_coin_id;					// 借出币种Id
	private String borrow_symbol;				// 借出币种名称
	private BigDecimal pledge_amount;			// 抵押数量
	private BigDecimal old_pledge_rate;			// 原抵押率
	private BigDecimal adjust_amount;			// 调整数量
	private BigDecimal new_pledge_rate;			// 新抵押率
	private int direction;						// 调整方向（0、新增，1、追加，-1、减少）
	private long record_time; 					// 调整时间
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

	public BigDecimal getPledge_amount() {
		return pledge_amount;
	}

	public void setPledge_amount(BigDecimal pledge_amount) {
		this.pledge_amount = pledge_amount;
	}

	public BigDecimal getOld_pledge_rate() {
		return old_pledge_rate;
	}

	public void setOld_pledge_rate(BigDecimal old_pledge_rate) {
		this.old_pledge_rate = old_pledge_rate;
	}

	public BigDecimal getAdjust_amount() {
		return adjust_amount;
	}

	public void setAdjust_amount(BigDecimal adjust_amount) {
		this.adjust_amount = adjust_amount;
	}

	public BigDecimal getNew_pledge_rate() {
		return new_pledge_rate;
	}

	public void setNew_pledge_rate(BigDecimal new_pledge_rate) {
		this.new_pledge_rate = new_pledge_rate;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public long getRecord_time() {
		return record_time;
	}

	public void setRecord_time(long record_time) {
		this.record_time = record_time;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
