package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_usdrate")
public class BQUsdrate implements Serializable {

	private static final long serialVersionUID = 5785252212671718971L;
	
	private int id;
	private String ex_type;
	private BigDecimal ex_rate;
	private long ex_time;
	private int ex_use;

	public BQUsdrate() {
	}

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int value) {
		id = value;
	}

	public String getEx_type() {
		return ex_type;
	}

	public void setEx_type(String exType) {
		ex_type = exType;
	}

	public BigDecimal getEx_rate() {
		return ex_rate;
	}

	public void setEx_rate(BigDecimal value) {
		ex_rate = value;
	}

	public long getEx_time() {
		return ex_time;
	}

	public void setEx_time(long value) {
		ex_time = value;
	}

	public int getEx_use() {
		return ex_use;
	}

	public void setEx_use(int value) {
		ex_use = value;
	}

}
