package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_mine_record")
public class BQMineRecord implements Serializable {

	private static final long serialVersionUID = -2541158773635944893L;
	
	private long id;
	private long f_user_id;
	private String trade_type;
	private long trade_id;
	private BigDecimal trade_amount;
	private BigDecimal trade_fee;
	private BigDecimal mine_rate;
	private BigDecimal mine_amount;
	private long record_time;
	private int currency_type_id;

	private int trade_mode;  //交易模式，1-永续合约，2-现货

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
	
	public void setF_user_id(long id) {
		f_user_id = id;
	}
	
	public String getTrade_type() {
		return trade_type;
	}
	
	public void setTrade_type(String type) {
		trade_type = type;
	}
	
	public long getTrade_id() {
		return trade_id;
	}
	
	public void setTrade_id(long tradeId) {
		trade_id = tradeId;
	}
	
	public BigDecimal getTrade_amount() {
		return trade_amount;
	}
	
	public void setTrade_amount(BigDecimal amount) {
		trade_amount = amount;
	}
	
	public BigDecimal getTrade_fee() {
		return trade_fee;
	}
	
	public void setTrade_fee(BigDecimal fee) {
		trade_fee = fee;
	}
	
	public BigDecimal getMine_rate() {
		return mine_rate;
	}
	
	public void setMine_rate(BigDecimal rate) {
		mine_rate = rate;
	}
	
	public BigDecimal getMine_amount() {
		return mine_amount;
	}
	
	public void setMine_amount(BigDecimal amount) {
		mine_amount = amount;
	}
	
	public long getRecord_time() {
		return record_time;
	}
	
	public void setRecord_time(long time) {
		record_time = time;
	}
	
	public int getCurrency_type_id() {
		return currency_type_id;
	}
	
	public void setCurrency_type_id(int currencyId) {
		this.currency_type_id = currencyId;
	}

	public int getTrade_mode() {
		return trade_mode;
	}

	public void setTrade_mode(int trade_mode) {
		this.trade_mode = trade_mode;
	}
}
