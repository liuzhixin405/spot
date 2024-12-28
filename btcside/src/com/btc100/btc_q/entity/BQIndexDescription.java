package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 指数说明类
 *
 */
@Entity
@Table(name = "bq_index_description")
public class BQIndexDescription implements Serializable {

	private static final long serialVersionUID = -7939814827351366301L;
	
	private int id;
	private String type_code; // 对应的合约类型code
	private String zh_description; // 中文指数说明
	private String en_description; // 英文指数说明
	private String ja_description; // 日文指数说明
	private String ko_description; // 韩文指数说明
	private String hk_description; // 繁体指数说明
	private long dateline; // 创建时间

	public BQIndexDescription() {

	}

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType_code() {
		return type_code;
	}

	public void setType_code(String type_code) {
		this.type_code = type_code;
	}

	public String getZh_description() {
		return zh_description;
	}

	public void setZh_description(String zh_description) {
		this.zh_description = zh_description;
	}

	public String getEn_description() {
		return en_description;
	}

	public void setEn_description(String en_description) {
		this.en_description = en_description;
	}

	public String getJa_description() {
		return ja_description;
	}

	public void setJa_description(String ja_description) {
		this.ja_description = ja_description;
	}

	public String getKo_description() {
		return ko_description;
	}

	public void setKo_description(String ko_description) {
		this.ko_description = ko_description;
	}

	public String getHk_description() {
		return hk_description;
	}

	public void setHk_description(String hk_description) {
		this.hk_description = hk_description;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

}
