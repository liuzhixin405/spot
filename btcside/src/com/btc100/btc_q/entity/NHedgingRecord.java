package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "n_hedging_record")
public class NHedgingRecord implements Serializable {

	private static final long serialVersionUID = 8609801548465756923L;
	
	private Long id;
	private String transaction_code;
	private long trade_id;
	private BigDecimal trade_amount;
	private BigDecimal trade_price;
	private int trade_direction;
	private long trade_time;
	private long f_user_id;
	private int direction;
	private BigDecimal price;
	private long process_time;
	private int state;
	private String note;
	private int send_times;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTransaction_code(){
		return transaction_code;
	}
	
	public void setTransaction_code(String code) {
		transaction_code = code;
	}
	
	public long getTrade_id(){
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
	
	public BigDecimal getTrade_price() {
		return trade_price;
	}
	
	public void setTrade_price(BigDecimal price) {
		trade_price = price;
	}
	
	public int getTrade_direction() {
		return trade_direction;
	}
	
	public void setTrade_direction(int direction) {
		trade_direction = direction;
	}
	
	public long getTrade_time() {
		return trade_time;
	}
	
	public void setTrade_time(long time) {
		trade_time = time;
	}
	
	public long getF_user_id() {
		return f_user_id;
	}
	
	public void setF_user_id(long userId) {
		f_user_id = userId;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public long getProcess_time() {
		return process_time;
	}
	
	public void setProcess_time(long time) {
		process_time = time;
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
	
	public int getSend_times() {
		return send_times;
	}
	
	public void setSend_times(int times) {
		this.send_times = times;
	}
}
