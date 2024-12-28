package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_loan_record")
public class BQLoanRecord implements Serializable {

	private static final long serialVersionUID = -4719597295292472013L;
	
	private long id;
	private long f_user_id;						// 用户Id
	private int pledge_coin_id;					// 抵押币种id
	private String pledge_symbol;				// 抵押币种名称
	private int borrow_coin_id;					// 借出币种Id
	private String borrow_symbol;				// 借出币种名称
	private long borrow_uid;					// 借出方用户Id
	private BigDecimal borrow_amount;			// 借出数量
	private BigDecimal received_amount;			// 借入到账金额
	private BigDecimal pledge_amount;			// 抵押数量
	private BigDecimal pledge_rate;				// 初始抵押率
	private BigDecimal day_rate;				// 利率（日）
	private BigDecimal fee_rate;				// 手续费率
	private BigDecimal back_borrow_amount = BigDecimal.ZERO;		// 已归还的借出数量
	private BigDecimal back_pledge_amount = BigDecimal.ZERO;		// 已释放的抵押数量
	private BigDecimal interest = BigDecimal.ZERO;					// 已付利息
	private BigDecimal fee = BigDecimal.ZERO;						// 已产生的手续费
	private BigDecimal payment_pledge_amount = BigDecimal.ZERO;		// 抵偿本金的质押数量
	private BigDecimal payment_interest = BigDecimal.ZERO;			// 抵偿利息的质押数量
	private BigDecimal payment_fee = BigDecimal.ZERO;				// 抵偿的手续费
	private int cycle;							// 周期（天数）
	private long record_time;					// 借贷时间
	private int state = 0;						// 状态（0-提交，1-借款中，2-已还款，3-已平仓	
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

	public BigDecimal getPledge_rate() {
		return pledge_rate;
	}

	public void setPledge_rate(BigDecimal pledge_rate) {
		this.pledge_rate = pledge_rate;
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

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public long getRecord_time() {
		return record_time;
	}

	public void setRecord_time(long record_time) {
		this.record_time = record_time;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public BigDecimal getPayment_pledge_amount() {
		return payment_pledge_amount;
	}

	public void setPayment_pledge_amount(BigDecimal payment_pledge_amount) {
		this.payment_pledge_amount = payment_pledge_amount;
	}

	public BigDecimal getPayment_interest() {
		return payment_interest;
	}

	public void setPayment_interest(BigDecimal payment_interest) {
		this.payment_interest = payment_interest;
	}

	public BigDecimal getPayment_fee() {
		return payment_fee;
	}

	public void setPayment_fee(BigDecimal payment_fee) {
		this.payment_fee = payment_fee;
	}

	public BigDecimal getReceived_amount() {
		return received_amount;
	}

	public void setReceived_amount(BigDecimal received_amount) {
		this.received_amount = received_amount;
	}

}
