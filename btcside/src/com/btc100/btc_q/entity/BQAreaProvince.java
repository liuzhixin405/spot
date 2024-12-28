package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 区域省份表
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_area_province")
public class BQAreaProvince implements Serializable {

	private static final long serialVersionUID = -7736026632960585027L;

	private long id;
	private String code;
	private String name;

	public BQAreaProvince() {

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

}
