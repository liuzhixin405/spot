package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_data_broad_cast_setting")
public class BQDataBroadCastSetting implements Serializable {

	private static final long serialVersionUID = -4086870669444966770L;

	private Long id;
	private String f_code; // 合约号
	private boolean running; // 状态
	private BigDecimal magnified_multiple = BigDecimal.ONE; // 放大倍数
	private int elite_count = 10; // 精英持仓人数

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getF_code() {
		return f_code;
	}

	public void setF_code(String code) {
		f_code = code;
	}

	public boolean getRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public BigDecimal getMagnified_multiple() {
		return magnified_multiple;
	}

	public void setMagnified_multiple(BigDecimal magnified_multiple) {
		this.magnified_multiple = magnified_multiple;
	}

	public int getElite_count() {
		return elite_count;
	}

	public void setElite_count(int elite_count) {
		this.elite_count = elite_count;
	}

}
