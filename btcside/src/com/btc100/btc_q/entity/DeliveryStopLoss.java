package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "delivery_stop_loss")
public class DeliveryStopLoss implements Serializable {

	private static final long serialVersionUID = -1179400787319155517L;

	public DeliveryStopLoss() {
	}

	private long id;
	private long transaction_id;
	private String transaction_code;
	private int trade_type_id;
	private int stop_loss_type_id;
	private int order_type = 0;
	private long f_user_id;
	private long transaction_member_id;
	private BigDecimal stop_loss_price;
	private BigDecimal pending_amount = BigDecimal.ZERO;
	private BigDecimal pending_trans_price;
	private long dateline;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long value) {
		id = value;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long value) {
		transaction_id = value;
	}

	public int getTrade_type_id() {
		return trade_type_id;
	}

	public void setTrade_type_id(int tradeTypeId) {
		trade_type_id = tradeTypeId;
	}

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String value) {
		transaction_code = value;
	}

	public int getStop_loss_type_id() {
		return stop_loss_type_id;
	}

	public void setStop_loss_type_id(int value) {
		stop_loss_type_id = value;
	}

	public long getTransaction_member_id() {
		return transaction_member_id;
	}

	public void setTransaction_member_id(long value) {
		transaction_member_id = value;
	}

	public BigDecimal getStop_loss_price() {
		return stop_loss_price;
	}

	public void setStop_loss_price(BigDecimal value) {
		stop_loss_price = value;
	}

	public BigDecimal getPending_trans_price() {
		return pending_trans_price;
	}

	public void setPending_trans_price(BigDecimal value) {
		pending_trans_price = value;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long value) {
		dateline = value;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long value) {
		f_user_id = value;
	}

	public int getOrder_type() {
		return order_type;
	}

	public void setOrder_type(int order_type) {
		this.order_type = order_type;
	}

	public BigDecimal getPending_amount() {
		return pending_amount;
	}

	public void setPending_amount(BigDecimal pending_amount) {
		this.pending_amount = pending_amount;
	}

}
