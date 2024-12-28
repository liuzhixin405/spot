package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 城市区域表，此表暂用不到
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_area_area")
public class BQArea implements Serializable {

	private static final long serialVersionUID = -1059463928500559606L;

	private long id;
	private String code;
	private String name;
	private String citycode; // 城市代码

	public BQArea() {

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

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}

}
