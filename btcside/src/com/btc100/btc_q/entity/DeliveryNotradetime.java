package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "delivery_no_trade_time")
public class DeliveryNotradetime implements Serializable {

	private static final long serialVersionUID = 7524621003781093104L;

	private long h_id;
	private long h_time;
	private int h_type;
	private long transaction_id;

	public DeliveryNotradetime() {
	}

	@Id
	@GeneratedValue
	public long getH_id() {
		return h_id;
	}

	public void setH_id(long hId) {
		h_id = hId;
	}

	public long getH_time() {
		return h_time;
	}

	public void setH_time(long hTime) {
		h_time = hTime;
	}

	public void setH_time(int value) {
		h_time = value;
	}

	public int getH_type() {
		return h_type;
	}

	public void setH_type(int value) {
		h_type = value;
	}

	public long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(long transaction_id) {
		this.transaction_id = transaction_id;
	}

}
