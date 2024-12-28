package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_member_rebate_record")
public class BQMemberRebateRecord implements Serializable {

	private static final long serialVersionUID = 3714564285029274783L;
	
	private long id;
	private long user_id;
	private int currency_type_id;
	private String coin_name;
	private BigDecimal commission_rate;
	private int rebate_type;
	private int user_level;
	private long sub_user_id;
	private BigDecimal rebate_amount;
	private long record_time;
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public int getCurrency_type_id() {
		return currency_type_id;
	}

	public void setCurrency_type_id(int currency_type_id) {
		this.currency_type_id = currency_type_id;
	}

	public String getCoin_name() {
		return coin_name;
	}

	public void setCoin_name(String coin_name) {
		this.coin_name = coin_name;
	}

	public BigDecimal getCommission_rate() {
		return commission_rate;
	}

	public void setCommission_rate(BigDecimal commission_rate) {
		this.commission_rate = commission_rate;
	}

	public int getRebate_type() {
		return rebate_type;
	}

	public void setRebate_type(int rebate_type) {
		this.rebate_type = rebate_type;
	}

	public int getUser_level() {
		return user_level;
	}

	public void setUser_level(int user_level) {
		this.user_level = user_level;
	}

	public long getSub_user_id() {
		return sub_user_id;
	}

	public void setSub_user_id(long sub_user_id) {
		this.sub_user_id = sub_user_id;
	}

	public BigDecimal getRebate_amount() {
		return rebate_amount;
	}

	public void setRebate_amount(BigDecimal rebate_amount) {
		this.rebate_amount = rebate_amount;
	}

	public long getRecord_time() {
		return record_time;
	}

	public void setRecord_time(long record_time) {
		this.record_time = record_time;
	}

}
