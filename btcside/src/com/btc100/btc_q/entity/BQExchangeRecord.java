package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_exchange_record")
public class BQExchangeRecord implements Serializable {

	private static final long serialVersionUID = -4719597295292472012L;
	
	private long id;
	private long f_user_id;
	private int ori_currency_type_id;
	private int target_currency_type_id;
	private String ori_currency_name;
	private String target_currency_name;
	private BigDecimal frozen_rate;
	private BigDecimal frozen_amount;
	private BigDecimal frozen_amount_back = BigDecimal.ZERO;
	private BigDecimal ori_amount;
	private BigDecimal target_amount;
	private BigDecimal present_price;
	private BigDecimal price_range;
	private int direction;
	private BigDecimal fee_rate;
	private BigDecimal fee = BigDecimal.ZERO;
	private BigDecimal obtain_amount = BigDecimal.ZERO;
	private int hedging_times = 0;
	private String order_id = "";
	private int state;
	private String note = "";
	private long record_time;
	private long oper_time;
	private BigDecimal order_price;
	private BigDecimal avg_price = BigDecimal.ZERO;
	private BigDecimal deal_amount = BigDecimal.ZERO;
	private BigDecimal deal_cash = BigDecimal.ZERO;
	
	
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

	public int getOri_currency_type_id() {
		return ori_currency_type_id;
	}

	public void setOri_currency_type_id(int value) {
		ori_currency_type_id = value;
	}
	
	public int getTarget_currency_type_id() {
		return target_currency_type_id;
	}

	public void setTarget_currency_type_id(int value) {
		target_currency_type_id = value;
	}
	
	public String getOri_currency_name() {
		return this.ori_currency_name;
	}
	
	public void setOri_currency_name(String name) {
		this.ori_currency_name = name;
	}
	
	public String getTarget_currency_name() {
		return this.target_currency_name;
	}
	
	public void setTarget_currency_name(String name) {
		this.target_currency_name = name;
	}

	public BigDecimal getFrozen_rate() {
		return frozen_rate;
	}

	public void setFrozen_rate(BigDecimal value) {
		frozen_rate = value;
	}

	public BigDecimal getFrozen_amount() {
		return frozen_amount;
	}

	public void setFrozen_amount(BigDecimal value) {
		frozen_amount = value;
	}
	
	public BigDecimal getFrozen_amount_back() {
		return frozen_amount_back;
	}

	public void setFrozen_amount_back(BigDecimal value) {
		frozen_amount_back = value;
	}

	public BigDecimal getOri_amount() {
		return ori_amount;
	}

	public void setOri_amount(BigDecimal value) {
		ori_amount = value;
	}

	public BigDecimal getTarget_amount() {
		return target_amount;
	}

	public void setTarget_amount(BigDecimal value) {
		target_amount = value;
	}

	public BigDecimal getPresent_price() {
		return present_price;
	}

	public void setPresent_price(BigDecimal value) {
		present_price = value;
	}

	public BigDecimal getPrice_range() {
		return price_range;
	}

	public void setPrice_range(BigDecimal value) {
		price_range = value;
	}
	
	public long getRecord_time() {
		return record_time;
	}
	
	public void setRecord_time(long time) {
		this.record_time = time;
	}
	
	public long getOper_time() {
		return oper_time;
	}
	
	public void setOper_time(long time) {
		this.oper_time = time;
	}
	
	public BigDecimal getFee_rate() {
		return fee_rate;
	}
	
	public void setFee_rate(BigDecimal rate) {
		this.fee_rate = rate;
	}
	
	public BigDecimal getFee() {
		return fee;
	}
	
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	
	public BigDecimal getObtain_amount() {
		return obtain_amount;
	}
	
	public void setObtain_amount(BigDecimal amount) {
		this.obtain_amount = amount;
	}
	
	public int getHedging_times() {
		return hedging_times;
	}
	
	public void setHedging_times(int hedging_times) {
		this.hedging_times = hedging_times;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public String getOrder_id() {
		return order_id;
	}
	
	public void setOrder_id(String orderId) {
		this.order_id = orderId;
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

	public BigDecimal getOrder_price() {
		return order_price;
	}
	
	public void setOrder_price(BigDecimal price) {
		this.order_price = price;
	}
	
	public BigDecimal getAvg_price() {
		return this.avg_price;
	}
	
	public void setAvg_price(BigDecimal price) {
		this.avg_price = price;
	}
	
	public BigDecimal getDeal_amount() {
		return this.deal_amount;
	}
	
	public void setDeal_amount(BigDecimal amount) {
		this.deal_amount = amount;
	}
	
	public BigDecimal getDeal_cash() {
		return this.deal_cash;
	}
	
	public void setDeal_cash(BigDecimal cash) {
		this.deal_cash = cash;
	}
}
