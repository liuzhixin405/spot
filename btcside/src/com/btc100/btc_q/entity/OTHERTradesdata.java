package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "other_tradesdata")
public class OTHERTradesdata implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OTHERTradesdata() {
	}

	public OTHERTradesdata(BigDecimal dAmount, BigDecimal dPrice, long dOid, String dType, long dTime, String dFlag) {
		d_amount = dAmount;
		d_price = dPrice;
		d_oid = dOid;
		d_type = dType;
		d_time = dTime;
		d_flag = dFlag;
	}

	private int d_id;

	@Id
	@GeneratedValue
	public int getD_id() {
		return d_id;
	}

	public void setD_id(int value) {
		d_id = value;
	}

	private BigDecimal d_amount;

	public BigDecimal getD_amount() {
		return d_amount;
	}

	public void setD_amount(BigDecimal value) {
		d_amount = value;
	}

	private BigDecimal d_price;

	public BigDecimal getD_price() {
		return d_price;
	}

	public void setD_price(BigDecimal value) {
		d_price = value;
	}

	private long d_oid;

	public long getD_oid() {
		return d_oid;
	}

	public void setD_oid(long value) {
		d_oid = value;
	}

	private String d_type;

	public String getD_type() {
		return d_type;
	}

	public void setD_type(String value) {
		d_type = value;
	}

	private long d_time;

	public long getD_time() {
		return d_time;
	}

	public void setD_time(long value) {
		d_time = value;
	}

	private String d_flag;

	public String getD_flag() {
		return d_flag;
	}

	public void setD_flag(String value) {
		d_flag = value;
	}

}
