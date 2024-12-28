package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 区域城市表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_area_city")
public class BQAreaCity implements Serializable {

	private static final long serialVersionUID = -5157966604189868982L;

	private long id;
	private String code;
	private String name;
	private String provincecode; // 城市代码

	public BQAreaCity() {

	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProvincecode() {
		return provincecode;
	}

	public void setProvincecode(String provincecode) {
		this.provincecode = provincecode;
	}

}
