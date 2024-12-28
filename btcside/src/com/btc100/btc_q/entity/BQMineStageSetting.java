package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_mine_stage_setting")
public class BQMineStageSetting implements Serializable {
	
	private static final long serialVersionUID = -4265055741172736273L;
	
	private long id;
	private int num;
	private BigDecimal stage_amount;
	private BigDecimal rate;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	public BigDecimal getstage_amount() {
		return this.stage_amount;
	}
	
	public void setStage_amount(BigDecimal amount) {
		this.stage_amount = amount;
	}
	
	public BigDecimal getRate() {
		return this.rate;
	}
	
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
}
