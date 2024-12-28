package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_common_data")
public class BQCommonData implements Serializable {

	private static final long serialVersionUID = -169953344908853175L;

	public BQCommonData() {
	}

	
	
	public BQCommonData( BigDecimal data, String content) {
		this.data = data;
		this.content = content;
	}



	private int id;

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int value) {
		id = value;
	}

	private BigDecimal data;

	public BigDecimal getData() {
		return data;
	}

	public void setData(BigDecimal value) {
		data = value;
	}

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String value) {
		content = value;
	}

}
