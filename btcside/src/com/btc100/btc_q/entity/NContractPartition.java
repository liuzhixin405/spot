package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "n_contract_partition")
public class NContractPartition implements Serializable {

	private static final long serialVersionUID = 3067269140208960945L;
	
	private long id;
	private String part;
	private int currency_type_id;
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long value) {
		id = value;
	}

	public int getCurrency_type_id() {
		return currency_type_id;
	}

	public void setCurrency_type_id(int currency_type_id) {
		this.currency_type_id = currency_type_id;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

}
