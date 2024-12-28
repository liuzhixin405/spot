package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_for_sosobtc")
public class BQForSoSoBtc implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private long id;
	private String transaction_code;//合约号
	private int position_flag;//sosobtc那边对应的坑位号
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTransaction_code() {
		return transaction_code;
	}
	public void setTransaction_code(String transactionCode) {
		transaction_code = transactionCode;
	}
	public int getPosition_flag() {
		return position_flag;
	}
	public void setPosition_flag(int positionFlag) {
		position_flag = positionFlag;
	}
	
	
}
