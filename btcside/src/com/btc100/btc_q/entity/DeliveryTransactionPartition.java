package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "delivery_transaction_partition")
public class DeliveryTransactionPartition implements Serializable {

	private static final long serialVersionUID = 7452182909682220935L;

	private Integer id;
	private String name_cn;
	private String name_en;
	private String name_jp;
	private String name_kr;
	private String name_hk;
	private int sort;
	private String codes;
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName_cn() {
		return name_cn;
	}

	public void setName_cn(String name_cn) {
		this.name_cn = name_cn;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public String getName_jp() {
		return name_jp;
	}

	public void setName_jp(String name_jp) {
		this.name_jp = name_jp;
	}

	public String getName_kr() {
		return name_kr;
	}

	public void setName_kr(String name_kr) {
		this.name_kr = name_kr;
	}

	public String getName_hk() {
		return name_hk;
	}

	public void setName_hk(String name_hk) {
		this.name_hk = name_hk;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getCodes() {
		return codes;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}
}
