package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_area_country")
public class BQAreaCountry implements Serializable {

	private static final long serialVersionUID = -8238401666552274047L;

	public BQAreaCountry() {
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

	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String value) {
		code = value;
	}

	private String zh_name;

	public String getZh_name() {
		return zh_name;
	}

	public void setZh_name(String zh_name) {
		this.zh_name = zh_name;
	}

	private String en_name;

	public String getEn_name() {
		return en_name;
	}

	public void setEn_name(String en_name) {
		this.en_name = en_name;
	}
	
}
