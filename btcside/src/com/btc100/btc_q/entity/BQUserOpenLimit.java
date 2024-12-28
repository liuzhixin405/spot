package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户开仓限制
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "bq_user_open_limit")
public class BQUserOpenLimit implements Serializable {

	private static final long serialVersionUID = -2402001580049496960L;

	private long id;
	private long f_user_id;
	private String f_code;
	private BigDecimal max_kdc_nums;
	private BigDecimal max_kkc_nums;
	private long add_time;

	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getF_user_id() {
		return f_user_id;
	}

	public void setF_user_id(long f_user_id) {
		this.f_user_id = f_user_id;
	}

	public String getF_code() {
		return f_code;
	}

	public void setF_code(String f_code) {
		this.f_code = f_code;
	}

	public BigDecimal getMax_kdc_nums() {
		return max_kdc_nums;
	}

	public void setMax_kdc_nums(BigDecimal max_kdc_nums) {
		this.max_kdc_nums = max_kdc_nums;
	}

	public BigDecimal getMax_kkc_nums() {
		return max_kkc_nums;
	}

	public void setMax_kkc_nums(BigDecimal max_kkc_nums) {
		this.max_kkc_nums = max_kkc_nums;
	}

	public long getAdd_time() {
		return add_time;
	}

	public void setAdd_time(long add_time) {
		this.add_time = add_time;
	}

}
