package com.btc100.btc_q.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_express_news")
public class BQExpressNews implements Serializable {

	private static final long serialVersionUID = -8794892870349304441L;

	private long id;
	private String zh_content; // 中文内容
	private String en_content; // 英文内容
	private String ja_content; // 日文内容
	private String ko_content; // 韩文内容
	private String hk_content; // 繁体内容
	private long dateline; // 创建时间

	public BQExpressNews() {

	}

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getZh_content() {
		return zh_content;
	}

	public void setZh_content(String zh_content) {
		this.zh_content = zh_content;
	}

	public String getEn_content() {
		return en_content;
	}

	public void setEn_content(String en_content) {
		this.en_content = en_content;
	}

	public String getJa_content() {
		return ja_content;
	}

	public void setJa_content(String ja_content) {
		this.ja_content = ja_content;
	}

	public String getKo_content() {
		return ko_content;
	}

	public void setKo_content(String ko_content) {
		this.ko_content = ko_content;
	}

	public String getHk_content() {
		return hk_content;
	}

	public void setHk_content(String hk_content) {
		this.hk_content = hk_content;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

}
