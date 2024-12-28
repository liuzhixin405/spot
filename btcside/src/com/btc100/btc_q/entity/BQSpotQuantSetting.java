package com.btc100.btc_q.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_spot_quant_setting")
public class BQSpotQuantSetting implements Serializable {
	
	private static final long serialVersionUID = -6476044252455317011L;
	
	private Integer id;
	private String uids;
	private boolean is_auto_cancel;
	private int cancel_timeout;
	private Integer keep_nums;
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getUids() {
		return uids;
	}

	public void setUids(String uids) {
		this.uids = uids;
	}

	public boolean getIs_auto_cancel() {
		return is_auto_cancel;
	}

	public void setIs_auto_cancel(boolean is_auto_cancel) {
		this.is_auto_cancel = is_auto_cancel;
	}

	public int getCancel_timeout() {
		return cancel_timeout;
	}

	public void setCancel_timeout(int cancel_timeout) {
		this.cancel_timeout = cancel_timeout;
	}

	public Integer getKeep_nums() {
		return keep_nums;
	}

	public void setKeep_nums(Integer keep_nums) {
		this.keep_nums = keep_nums;
	}

}
