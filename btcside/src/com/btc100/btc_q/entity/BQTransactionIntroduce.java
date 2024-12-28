package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 合约详情介绍
 * 
 * @author Administrator
 * 
 */
/**
 * @author win7
 *
 */
@Entity
@Table(name = "bq_transaction_introduce")
public class BQTransactionIntroduce implements Serializable {

	private static final long serialVersionUID = -8349577166982673958L;

	@Id
	@GeneratedValue
	private long id;
	private String transaction_code;// 合约号
	private String zh_introduce = ""; // 中文介绍
	private String en_introduce = ""; // 英文介绍
	private String ja_introduce = ""; // 日文介绍
	private String ko_introduce = ""; // 韩文介绍
	private String hk_introduce = ""; // 繁体介绍
	private int type;// 市场类型：1、正式盘，2、模拟盘，4、交割合约
	private String part = "";

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTransaction_code() {
		return transaction_code;
	}

	public void setTransaction_code(String transaction_code) {
		this.transaction_code = transaction_code;
	}

	public String getZh_introduce() {
		return zh_introduce;
	}

	public void setZh_introduce(String zh_introduce) {
		this.zh_introduce = zh_introduce;
	}

	public String getEn_introduce() {
		return en_introduce;
	}

	public void setEn_introduce(String en_introduce) {
		this.en_introduce = en_introduce;
	}

	public String getJa_introduce() {
		return ja_introduce;
	}

	public void setJa_introduce(String ja_introduce) {
		this.ja_introduce = ja_introduce;
	}

	public String getKo_introduce() {
		return ko_introduce;
	}

	public void setKo_introduce(String ko_introduce) {
		this.ko_introduce = ko_introduce;
	}

	public String getHk_introduce() {
		return hk_introduce;
	}

	public void setHk_introduce(String hk_introduce) {
		this.hk_introduce = hk_introduce;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	
}
