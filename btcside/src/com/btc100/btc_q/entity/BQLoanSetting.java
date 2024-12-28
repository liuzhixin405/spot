package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_loan_setting")
public class BQLoanSetting implements Serializable {

	private static final long serialVersionUID = 8609801548465756821L;
	
	private Long id;
	private int pledge_coin_id;					// 抵押币种id
	private String pledge_symbol;				// 抵押币种名称
	private int pledge_digits = 8;				// 质押数量小数位数
	private int borrow_coin_id;					// 借出币种Id
	private String borrow_symbol;				// 借出币种名称
	private String borrow_uids;					// 可借出用户列表（逗号分隔）
	private int borrow_digits = 8;				// 借出数量小数位数
	private BigDecimal max_borrow_amount = BigDecimal.ZERO;		// 最大借出数量
	private BigDecimal min_borrow_amount = BigDecimal.ZERO;		// 最少借出数量
	private BigDecimal init_pledge_rate;		// 初始抵押率
	private BigDecimal curr_pledge_rate;		// 补仓抵押率
	private BigDecimal close_pledge_rate;		// 平仓抵押率
	private BigDecimal fee_rate;				// 手续费率
	private int state = 1; 						// 状态（0-已关闭，1-进行中）
	private int borrow_type = 0;				// 借出用户方式（0-顺序，1-随机）
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
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

	public int getPledge_digits() {
		return pledge_digits;
	}

	public void setPledge_digits(int pledge_digits) {
		this.pledge_digits = pledge_digits;
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

	public String getBorrow_uids() {
		return borrow_uids;
	}

	public void setBorrow_uids(String borrow_uids) {
		this.borrow_uids = borrow_uids;
	}

	public int getBorrow_digits() {
		return borrow_digits;
	}

	public void setBorrow_digits(int borrow_digits) {
		this.borrow_digits = borrow_digits;
	}

	public BigDecimal getInit_pledge_rate() {
		return init_pledge_rate;
	}

	public void setInit_pledge_rate(BigDecimal init_pledge_rate) {
		this.init_pledge_rate = init_pledge_rate;
	}

	public BigDecimal getCurr_pledge_rate() {
		return curr_pledge_rate;
	}

	public void setCurr_pledge_rate(BigDecimal curr_pledge_rate) {
		this.curr_pledge_rate = curr_pledge_rate;
	}

	public BigDecimal getClose_pledge_rate() {
		return close_pledge_rate;
	}

	public void setClose_pledge_rate(BigDecimal close_pledge_rate) {
		this.close_pledge_rate = close_pledge_rate;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getBorrow_type() {
		return borrow_type;
	}

	public void setBorrow_type(int borrow_type) {
		this.borrow_type = borrow_type;
	}

	public BigDecimal getFee_rate() {
		return fee_rate;
	}

	public void setFee_rate(BigDecimal fee_rate) {
		this.fee_rate = fee_rate;
	}

	public BigDecimal getMax_borrow_amount() {
		return max_borrow_amount;
	}

	public void setMax_borrow_amount(BigDecimal max_borrow_amount) {
		this.max_borrow_amount = max_borrow_amount;
	}

	public BigDecimal getMin_borrow_amount() {
		return min_borrow_amount;
	}

	public void setMin_borrow_amount(BigDecimal min_borrow_amount) {
		this.min_borrow_amount = min_borrow_amount;
	}
}
