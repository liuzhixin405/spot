package com.btc100.btc_q.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bq_ieo_unlock_setting")
public class BQIeoUnlockSetting implements Serializable {

	private static final long serialVersionUID = 9102624669077950972L;
	
	private long id;
	private BigDecimal unlock_rate;
	private long unlock_time;
	private long lock_time_begin;
	private long lock_time_end;
	
	@Id
	@GeneratedValue
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getUnlock_rate() {
		return unlock_rate;
	}

	public void setUnlock_rate(BigDecimal unlock_rate) {
		this.unlock_rate = unlock_rate;
	}

	public long getUnlock_time() {
		return unlock_time;
	}

	public void setUnlock_time(long unlock_time) {
		this.unlock_time = unlock_time;
	}

	public long getLock_time_begin() {
		return lock_time_begin;
	}

	public void setLock_time_begin(long lock_time_begin) {
		this.lock_time_begin = lock_time_begin;
	}

	public long getLock_time_end() {
		return lock_time_end;
	}

	public void setLock_time_end(long lock_time_end) {
		this.lock_time_end = lock_time_end;
	}	
}
