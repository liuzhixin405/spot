package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_mine_setting")
public class BQMineSetting implements Serializable {
	
	private static final long serialVersionUID = 75597508619027600L;
	
	private long id;
	private int currency_type_id;
	private BigDecimal total_amount;
	private BigDecimal mined_amount = BigDecimal.ZERO;
	private BigDecimal personal_max_amount = BigDecimal.ZERO;  //个人开采数量上限
	private boolean running;
	private boolean rebate;
	private long begin_time = 1531893600L;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCurrency_type_id() {
		return this.currency_type_id;
	}
	
	public void setCurrency_type_id(int typeId) {
		this.currency_type_id = typeId;
	}
	
	public BigDecimal getTotal_amount() {
		return this.total_amount;
	}
	
	public void setTotal_amount(BigDecimal amount) {
		this.total_amount = amount;
	}
	
	public BigDecimal getMined_amount() {
		return this.mined_amount;
	}
	
	public void setMined_amount(BigDecimal amount) {
		this.mined_amount = amount;
	}
	
	public boolean getRunning() {
		return this.running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean getRebate() {
		return this.rebate;
	}
	
	public void setRebate(boolean rebate) {
		this.rebate = rebate;
	}
	
	public long getBegin_time() {
		return this.begin_time;
	}
	
	public void setBegin_time(long time) {
		this.begin_time = time;
	}

	public BigDecimal getPersonal_max_amount() {
		return personal_max_amount;
	}

	public void setPersonal_max_amount(BigDecimal personal_max_amount) {
		this.personal_max_amount = personal_max_amount;
	}
}
