package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_bfx_vip_setting")
public class BQBfxVipSetting implements Serializable {
	
	private static final long serialVersionUID = -7779328398940823634L;
	
	private Long id;
	private BigDecimal hold_count;
	private int level_id;
	private String level_name;
	private String note = "";

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getHold_count() {
		return hold_count;
	}

	public void setHold_count(BigDecimal hold_count) {
		this.hold_count = hold_count;
	}

	public int getLevel_id() {
		return level_id;
	}

	public void setLevel_id(int level_id) {
		this.level_id = level_id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getLevel_name() {
		return level_name;
	}

	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}

}
