package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_level_buy_setting")
public class BQMemberLevelBuySetting implements Serializable {

	private static final long serialVersionUID = -2569245876210037109L;
	
	private long id;								// id
	private int pay_coin_id;						// 支付币种（默认为BFX）
	private String pay_coin_name;					// 支付币种名称（默认为BFX）
	private int start_unlock_gap_day;				// 多少天后开始释放
	private int unlock_times;						// 总释放次数（分多少次释放完）
	private BigDecimal exchange_rate;				// 与BFX汇率（1BFX等于多少支付币种）
	private BigDecimal first_rebate;				// 一级上级返佣（直接上级）
	private BigDecimal second_rebate;				// 二级上级返佣（上级的上级）
	private BigDecimal back_price;					// 回购指导价
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public int getStart_unlock_gap_day() {
		return start_unlock_gap_day;
	}

	public void setStart_unlock_gap_day(int start_unlock_gap_day) {
		this.start_unlock_gap_day = start_unlock_gap_day;
	}

	public int getPay_coin_id() {
		return pay_coin_id;
	}

	public void setPay_coin_id(int pay_coin_id) {
		this.pay_coin_id = pay_coin_id;
	}

	public int getUnlock_times() {
		return unlock_times;
	}

	public void setUnlock_times(int unlock_times) {
		this.unlock_times = unlock_times;
	}

	public BigDecimal getExchange_rate() {
		return exchange_rate;
	}

	public void setExchange_rate(BigDecimal exchange_rate) {
		this.exchange_rate = exchange_rate;
	}

	public String getPay_coin_name() {
		return pay_coin_name;
	}

	public void setPay_coin_name(String pay_coin_name) {
		this.pay_coin_name = pay_coin_name;
	}

	public BigDecimal getFirst_rebate() {
		return first_rebate;
	}

	public void setFirst_rebate(BigDecimal first_rebate) {
		this.first_rebate = first_rebate;
	}

	public BigDecimal getSecond_rebate() {
		return second_rebate;
	}

	public void setSecond_rebate(BigDecimal second_rebate) {
		this.second_rebate = second_rebate;
	}

	public BigDecimal getBack_price() {
		return back_price;
	}

	public void setBack_price(BigDecimal back_price) {
		this.back_price = back_price;
	}

}
